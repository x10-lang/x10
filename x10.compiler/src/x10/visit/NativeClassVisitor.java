/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.visit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.main.Options;
import polyglot.main.Reporter;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl;
import x10.extension.X10Ext;
import x10.types.MethodInstance;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10Def;
import x10.types.X10FieldDef;
import x10.types.X10MethodDef;
import x10.util.FileUtils;
import x10.types.constants.StringValue;

/**
 * Visitor that expands @NativeClass and @NativeDef annotations.
 */
public class NativeClassVisitor extends ContextVisitor {
    public static final Name NATIVE_FIELD_NAME = Name.make("__NATIVE_FIELD__");

    final String theLanguage;
    final TypeSystem xts;
    final NodeFactory xnf;

    public NativeClassVisitor(Job job, TypeSystem ts, NodeFactory nf, String theLanguage) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
        this.theLanguage = theLanguage;
    }

    protected boolean isNativeDef(X10Def def) throws SemanticException {
        Type t = xts.systemResolver().findOne(QName.make("x10.compiler.NativeDef"));
        List<Type> as = def.annotationsMatching(t);
        for (Type at : as) {
            String lang = getPropertyInit(at, 0);
            if (theLanguage.equals(lang)) {
                return true;
            }
        }
        return false;
    }

    protected String getNativeClassName(X10ClassDef def) throws SemanticException {
        Type t = xts.NativeClass();
        List<Type> as = def.annotationsMatching(t);
        for (Type at : as) {
            String lang = getPropertyInit(at, 0);
            if (theLanguage.equals(lang)) {
                return getPropertyInit(at, 2);
            }
        }
        return null;
    }

    protected String getNativeClassPackage(X10ClassDef def) throws SemanticException {
        Type t = xts.NativeClass();
        List<Type> as = def.annotationsMatching(t);
        for (Type at : as) {
            String lang = getPropertyInit(at, 0);
            if (theLanguage.equals(lang)) {
                return getPropertyInit(at, 1);
            }
        }
        return null;
    }

    protected String getPropertyInit(Type at, int index) throws SemanticException {
        at = Types.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (index < act.propertyInitializers().size()) {
                Expr e = act.propertyInitializer(index);
                if (e.isConstant() && e.constantValue() instanceof StringValue) {
                    return ((StringValue) e.constantValue()).value();
                } else {
                    throw new SemanticException("Property initializer for @" + at + " must be a string literal.");
                }
            }
        }
        return null;
    }

    protected static Flags clearNative(Flags flags) {
        return flags.clear(Flags.NATIVE);
    }

    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        // look for @NativeClass class declarations
        if (!(n instanceof X10ClassDecl))
            return n;

        X10ClassDecl cdecl = (X10ClassDecl) n;
        X10ClassDef cdef = (X10ClassDef) cdecl.classDef();
        String cname = getNativeClassName(cdef);

        if (cname == null)
            return n;

        if (reporter.should_report(Reporter.nativeclass, 1))
            reporter.report(1, "Processing @NativeClass " + cdecl);

        ClassBody cbody = cdecl.body();
        List<ClassMember> cmembers = new ArrayList<ClassMember>();

        Position p = Position.compilerGenerated(cbody.position());

        // create fake def for native class
        X10ClassDef fake = (X10ClassDef) xts.createClassDef();
        fake.name(Name.make(cname));
        fake.kind(ClassDef.TOP_LEVEL);
        fake.setFromEncodedClassFile();
        if (cdef.isStruct()) {
            fake.setFlags(Flags.STRUCT);
        } else {
            fake.setFlags(Flags.NONE);
        }
        String cpackage = getNativeClassPackage(cdef);
        fake.setPackage(Types.ref(ts.packageForName(QName.make(cpackage))));

        java.util.Iterator<ParameterType> ps = cdef.typeParameters().iterator();
        java.util.Iterator<ParameterType.Variance> vs = cdef.variances().iterator();
        while (ps.hasNext()) {
            ParameterType pp = ps.next();
            ParameterType.Variance vv = vs.next();
            fake.addTypeParameter(pp, vv);
        }

        X10ClassType embed = (X10ClassType) xts.Embed();
        List<AnnotationNode> anodes;
        if (fake.isStruct()) {
            anodes = Collections.<AnnotationNode>emptyList();
        } else {
            anodes = Collections.<AnnotationNode>singletonList(xnf.AnnotationNode(p, xnf.CanonicalTypeNode(p, embed)));
        }
        // add field with native type
        Name fname = NATIVE_FIELD_NAME;
        Id fid = xnf.Id(p, fname);
        ClassType ftype = fake.asType();
        CanonicalTypeNode ftnode = xnf.CanonicalTypeNode(p, ftype);
        Flags fflags = Flags.PRIVATE.Final();
        X10FieldDef fdef = xts.fieldDef(p, Types.ref(cdef.asType()), fflags, Types.ref(ftype), fname);
        fdef.setDefAnnotations(Collections.<Ref<? extends Type>>singletonList(Types.ref(embed)));
        FieldDecl fdecl = xnf.FieldDecl(p, xnf.FlagsNode(p, fflags), ftnode, fid).fieldDef(fdef);
        fdecl = (FieldDecl) ((X10Ext) fdecl.ext()).annotations(anodes);
        cmembers.add(fdecl);
        cdef.addField(fdef);

        // field selector
        Receiver special = xnf.This(p).type(cdef.asType());
        Receiver field = xnf.Field(p, special, fid).fieldInstance(fdef.asInstance()).type(ftype);

        // add copy constructor
        ConstructorInstance xinst;
        {
            Name id0 = Name.make("id0");
            LocalDef ldef = xts.localDef(p, Flags.FINAL, Types.ref(ftype), id0);
            Expr init = xnf.Local(p, xnf.Id(p, id0)).localInstance(ldef.asInstance()).type(ftype);
            Expr assign = xnf.FieldAssign(p, special, fid, Assign.ASSIGN, init).fieldInstance(fdef.asInstance()).type(ftype);
            Formal f = xnf.Formal(p, xnf.FlagsNode(p, Flags.FINAL), ftnode, xnf.Id(p, id0)).localDef(ldef);

            ArrayList<Stmt> ctorBlock = new ArrayList<Stmt>();
            // super constructor def (noarg)
            final TypeNode superClass = cdecl.superClass();
            if (superClass!=null) {
                ConstructorDef sdef = xts.findConstructor(superClass.type(),
                        xts.ConstructorMatcher(superClass.type(), Collections.<Type>emptyList(), context)).def();
                ctorBlock.add(xnf.SuperCall(p, Collections.<Expr>emptyList()).constructorInstance(sdef.asInstance()));
            }
            ctorBlock.add(xnf.Eval(p, assign));

            X10ConstructorDecl xd = (X10ConstructorDecl) xnf.ConstructorDecl(p,
                    xnf.FlagsNode(p, Flags.PRIVATE),
                    cdecl.name(),
                    Collections.<Formal>singletonList(f),
                    xnf.Block(p,ctorBlock));
            xd = xd.typeParameters(Collections.<TypeParamNode>emptyList());
            xd = xd.returnType(ftnode);

            ConstructorDef xdef = xts.constructorDef(p, p,
                    Types.ref(cdef.asType()),
                    Flags.PRIVATE,
                    Collections.<Ref<? extends Type>>singletonList(Types.ref(ftype)),
                    Collections.<Ref<? extends Type>>emptyList()
                  );

            cmembers.add(xd.constructorDef(xdef));

            xinst = xdef.asInstance(); // to be used later
        }

        Boolean hasNativeConstructor = false;

        // look for native methods and constructors
        for (ClassMember m : cbody.members()) {
            if (m instanceof X10MethodDecl) {
                X10MethodDecl mdecl = (X10MethodDecl) m;
                X10MethodDef mdef = (X10MethodDef) mdecl.methodDef();

                if (!isNativeDef(mdef) && !mdef.flags().isNative()) {
                    cmembers.add(m);
                    continue;
                }

                if (reporter.should_report("nativeclass", 2))
                    reporter.report(1, "Processing @NativeDef " + mdecl);

                // clear native flag
                mdecl = (X10MethodDecl) mdecl.flags(xnf.FlagsNode(p, clearNative(mdecl.flags().flags())));
                mdef.setFlags(clearNative(mdef.flags()));

                // turn formals into arguments of delegate call
                List<Expr> args = new ArrayList<Expr>();
                for (Formal f : mdecl.formals())
                    args.add(xnf.Local(p, f.name()).localInstance(f.localDef().asInstance()).type(f.type().type()));

                // reuse x10 method instance for delegate method
                MethodInstance minst = mdef.asInstance();
                minst = (MethodInstance) minst.container(ftype);

                // call delegate
                Receiver target = mdef.flags().isStatic() ? ftnode : field;
                Call call = xnf.Call(p, target, mdecl.name(), args); // no type yet

                // void vs factory vs non-void methods
                Stmt body;
                if (mdecl.returnType().type().isVoid()) {
                    body = xnf.Eval(p, call.methodInstance(minst).type(ts.Void()));
                } else if (mdecl.returnType().type().isSubtype(cdef.asType(), context)) {
                    // delegate method return native object
                    minst = minst.returnType(ftype);

                    // call copy constructor
                    New copy = xnf.New(p,
                            xnf.CanonicalTypeNode(p, Types.ref(cdef.asType())),
                            Collections.<Expr>singletonList(call.methodInstance(minst).type(ftype)));
                    body = xnf.Return(p, copy.constructorInstance(xinst).type(cdef.asType()));
                } else{
                    body = xnf.Return(p, call.methodInstance(minst).type(mdecl.returnType().type()));
                }

                cmembers.add((X10MethodDecl) mdecl.body(xnf.Block(p, body)));
                continue;
            }

            if (m instanceof X10ConstructorDecl) {
                X10ConstructorDecl xdecl = (X10ConstructorDecl) m;
                X10ConstructorDef xdef = (X10ConstructorDef) xdecl.constructorDef();

                if (!isNativeDef(xdef) && !xdef.flags().isNative()) {
                    // TODO: check that non-native constructors invoke native constructors
                    cmembers.add(m);
                    continue;
                }

                hasNativeConstructor = true; // good!

                if (reporter.should_report("nativeclass", 2))
                    reporter.report(1, "Processing @NativeDef " + xdecl);

                // clear native flag
                xdecl = (X10ConstructorDecl) xdecl.flags(xnf.FlagsNode(p, clearNative(xdecl.flags().flags())));
                xdef.setFlags(clearNative(xdef.flags()));

                // turn formals into arguments of delegate call
                List<Expr> args = new ArrayList<Expr>();
                for (Formal f : xdecl.formals())
                    args.add(xnf.Local(p, f.name()).localInstance(f.localDef().asInstance()).type(f.type().type()));

                // call delegate constructor
                Expr init = xnf.New(p, ftnode, args).constructorInstance(xdef.asInstance()).type(ftype);
                init = (Expr) ((X10Ext) init.ext()).annotations(anodes);
                
                // invoke copy constructor
                Expr assign = xnf.FieldAssign(p, special, fid, Assign.ASSIGN, init).fieldInstance(fdef.asInstance()).type(ftype);
                ArrayList<Stmt> ctorBlock = new ArrayList<Stmt>();
                // super constructor def (noarg)
                final TypeNode superClass = cdecl.superClass();
                if (superClass!=null) {
                    ConstructorDef sdef = xts.findConstructor(superClass.type(),
                            xts.ConstructorMatcher(superClass.type(), Collections.<Type>emptyList(), context)).def();
                    ctorBlock.add(xnf.SuperCall(p, Collections.<Expr>emptyList()).constructorInstance(sdef.asInstance()));
                }
                ctorBlock.add(xnf.Eval(p, assign));
//                Stmt body = xnf.ThisCall(p, Collections.<Expr>singletonList(init)).constructorInstance(xinst);

                cmembers.add((X10ConstructorDecl) xdecl.body(xnf.Block(p, ctorBlock)));
                continue;
            }

            cmembers.add(m);
        }

        if (!hasNativeConstructor) {
            throw new SemanticException("@NativeClass " + cdecl.name() + " must declare a native constructor.");
        }

        return cdecl.body(cbody.members(cmembers));
    }
}
