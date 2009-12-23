/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.ConstructorInstance;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.FieldDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.Ref;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.extension.X10Ext;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

/**
 * Visitor that expands @NativeClass and @NativeDef annotations.
 */
public class NativeClassVisitor extends ContextVisitor {
    final String theLanguage;
    final X10TypeSystem xts;
    final X10NodeFactory xnf;
    final Position pos;
    final Name fieldName;

    public NativeClassVisitor(Job job, TypeSystem ts, NodeFactory nf, String theLanguage) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
        this.theLanguage = theLanguage;
        pos = Position.COMPILER_GENERATED;
        fieldName = Name.make("__NATIVE_FIELD__");
    }

    public boolean isNativeMethod(X10MethodDef def) {
        try {
            Type t = (Type) xts.systemResolver().find(QName.make("x10.compiler.NativeDef"));
            List<Type> as = def.annotationsMatching(t);
            for (Type at : as) {
                String lang = getPropertyInit(at, 0);
                if (theLanguage.equals(lang)) {
                    return true;
                }
            }
        }
        catch (SemanticException e) {}
        return false;
    }

    public String getNativeClassName(X10ClassDef def) {
        try {
            Type t = (Type) xts.systemResolver().find(QName.make("x10.compiler.NativeClass"));
            List<Type> as = def.annotationsMatching(t);
            for (Type at : as) {
                String lang = getPropertyInit(at, 0);
                if (theLanguage.equals(lang)) {
                    return getPropertyInit(at, 2);
                }
            }
        }
        catch (SemanticException e) {}
        return null;
    }


    public String getNativeClassPackage(X10ClassDef def) {
        try {
            Type t = (Type) xts.systemResolver().find(QName.make("x10.compiler.NativeClass"));
            List<Type> as = def.annotationsMatching(t);
            for (Type at : as) {
                String lang = getPropertyInit(at, 0);
                if (theLanguage.equals(lang)) {
                    return getPropertyInit(at, 1);
                }
            }
        }
        catch (SemanticException e) {}
        return null;
    }

    String getPropertyInit(Type at, int index) throws SemanticException {
        at = X10TypeMixin.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (index < act.propertyInitializers().size()) {
                Expr e = act.propertyInitializer(index);
                if (e.isConstant() && e.constantValue() instanceof String) {
                    return (String) e.constantValue();
                }
                else {
                    throw new SemanticException("Property initializer for @" + at + " must be a string literal.");
                }
            }
        }
        return null;
    }

    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        // look for @NativeClass class declarations
        if (!(n instanceof X10ClassDecl)) 
            return n;
        
        X10ClassDecl cd = (X10ClassDecl) n;
        X10ClassDef cf = (X10ClassDef) cd.classDef();

        String cn = getNativeClassName(cf);
        
        if (cn == null)
            return n;

        if (Report.should_report("nativeclass", 1))
            Report.report(1, "Processing @NativeClass " + cd);

        ClassBody cb = cd.body();
        List<ClassMember> cm = new ArrayList<ClassMember>();

        // create fake def for native class
        ClassDef def = xts.createClassDef();
        def.name(Name.make(cn));
        def.kind(ClassDef.TOP_LEVEL);
        def.setFromEncodedClassFile();
        def.setFlags(X10Flags.NONE);
        def.setPackage(Types.ref(ts.packageForName(QName.make(getNativeClassPackage(cf)))));
        ClassType ft = def.asType();

        // add field with native type
        Flags flags = X10Flags.GLOBAL.Private().Final();
        FieldDef ff = xts.fieldDef(pos, Types.ref(cf.asType()), flags, Types.ref(ft), fieldName);
        ConstructorInstance ci = xts.constructorDef(pos, Types.ref(ft), flags,
                Collections.<Ref<? extends Type>>emptyList(),
                Collections.<Ref<? extends Type>>emptyList()).asInstance();
        CanonicalTypeNode tn = xnf.CanonicalTypeNode(pos, ft);
        Expr init = xnf.New(pos, tn, Collections.<Expr>emptyList()).constructorInstance(ci).type(ft);
        cm.add(xnf.FieldDecl(pos, xnf.FlagsNode(pos, flags), tn, xnf.Id(pos, fieldName), init).fieldDef(ff));

        // look for @NativeDef methods
        for (ClassMember m : cb.members()) {
            if (m instanceof X10MethodDecl) {
                X10MethodDecl md = (X10MethodDecl) m;
                X10MethodDef mf = (X10MethodDef) md.methodDef();
                
                if (!isNativeMethod(mf)) {
                    cm.add(m);
                    continue;
                }

                if (Report.should_report("nativeclass", 2))
                    Report.report(1, "Processing @NativeDef " + md);

                // turn formals into arguments of delegate call
                List<Expr> args = new ArrayList<Expr>();
                for (Formal f : md.formals())
                    args.add(xnf.Local(pos, f.name()));
                
                // call delegate
                Receiver special = xnf.This(pos).type(cf.asType());
                Receiver field = xnf.Field(pos, special, xnf.Id(pos, fieldName)).fieldInstance(ff.asInstance()).type(ft);
                // HACK: reuse x10 method instance for delegate method but make it global
                MethodInstance mi = mf.asInstance();
                mi = (MethodInstance) mi.flags(((X10Flags) mi.flags()).Global());
                Expr expr = xnf.Call(pos, field, md.name(), args).methodInstance(mi).type(md.returnType().type());
                
                // void vs. non-void methods
                Stmt body;
                if (md.returnType().type().isVoid()) {
                    body = xnf.Eval(pos, expr);
                } else {
                    body = xnf.Return(pos, expr);
                }
                cm.add((X10MethodDecl) md.body(xnf.Block(pos, body)));
                continue;
            }
            cm.add(m);
        }
        
        return cd.body(cb.members(cm));
    }
}
