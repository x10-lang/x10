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
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ConstructorInstance;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.FieldDef;
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
        if (!(n instanceof X10ClassDecl)) 
            return n;
        
        X10ClassDecl cd = (X10ClassDecl) n;
        X10ClassDef cf = (X10ClassDef) cd.classDef();

        String cn = getNativeClassName(cf);
        
        if (cn == null)
            return n;

//        System.err.println("processing @NativeClass(\"" + cn + "\") class " + cd.name());

        ClassBody cb = cd.body();
        List<ClassMember> cm = new ArrayList<ClassMember>();

        ClassDef def = xts.createClassDef();
        def.name(Name.make(cn));
        def.kind(ClassDef.TOP_LEVEL);
        def.setFromEncodedClassFile();
        Flags flags = X10Flags.GLOBAL.Private().Final();
        def.setFlags(flags);
        def.setPackage(Types.ref(ts.packageForName(QName.make(getNativeClassPackage(cf)))));
        ClassType ft = def.asType();
        FieldDef ff = xts.fieldDef(pos, Types.ref(cf.asType()), flags, Types.ref(ft), fieldName);
        ConstructorInstance ci = xts.constructorDef(pos, Types.ref(ft), flags,
                Collections.<Ref<? extends Type>>emptyList(),
                Collections.<Ref<? extends Type>>emptyList()).asInstance();
        CanonicalTypeNode tn = xnf.CanonicalTypeNode(pos, ft);
        Expr init = xnf.New(pos, tn, Collections.<Expr>emptyList()).constructorInstance(ci).type(ft);

        for (ClassMember m : cb.members()) {
            if (m instanceof X10MethodDecl) {
                X10MethodDecl md = (X10MethodDecl) m;
                X10MethodDef mf = (X10MethodDef) md.methodDef();
                
                if (!isNativeMethod(mf)) {
                    cm.add(m);
                    continue;
                }

//                System.err.println("processing " + mf);

                
                List<Expr> args = new ArrayList<Expr>();
                for (Formal f : md.formals())
                    args.add(xnf.Local(pos, f.name()));
                
                Receiver r = xnf.This(pos).type(cf.asType());
                Receiver t = xnf.Field(pos, r, xnf.Id(pos, fieldName)).fieldInstance(ff.asInstance()).type(ft);
                Expr expr = xnf.Call(pos, t, md.name(), args).methodInstance(mf.asInstance()).type(md.returnType().type());
                
                if (md.returnType().type().isVoid()) {
                    cm.add((X10MethodDecl) md.body(xnf.Block(pos, xnf.Eval(pos, expr))));
                } else {
                    cm.add((X10MethodDecl) md.body(xnf.Block(pos, xnf.Return(pos, expr))));
                }
                continue;
            }
            cm.add(m);
        }
        
        cm.add(xnf.FieldDecl(pos, xnf.FlagsNode(pos, flags), tn, xnf.Id(pos, fieldName), init).fieldDef(ff));
        
        X10Ext ext = ((X10Ext) cd.ext());
        ext.annotations(Collections.<AnnotationNode>emptyList());
        
        return cd.body(cb.members(cm));//.ext(ext);
    }
}
