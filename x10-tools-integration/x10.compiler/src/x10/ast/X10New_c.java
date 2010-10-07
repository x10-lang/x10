/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.Matcher;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10Flags;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.matcher.DumbConstructorMatcher;
import x10.visit.X10TypeChecker;


/**
 * new C[T](e)
 * 
 * @author nystrom
 */
public class X10New_c extends New_c implements X10New {
    public X10New_c(Position pos, boolean newOmitted, Expr qualifier, TypeNode tn, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
        super(pos, qualifier, tn, arguments, body);
        this.typeArguments = TypedList.copyAndCheck(typeArguments, TypeNode.class, true);
        this.newOmitted = newOmitted;
    }

    private boolean newOmitted = false;
    public boolean newOmitted() {
        return newOmitted;
    }
    public X10New newOmitted(boolean val) {
        X10New_c n = (X10New_c) copy();
        n.newOmitted = val;
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr qualifier = (Expr) visitChild(this.qualifier, v);
        TypeNode tn = (TypeNode) visitChild(this.tn, v);
        List<TypeNode> typeArguments = visitList(this.typeArguments, v);
        List<Expr> arguments = visitList(this.arguments, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        X10New_c n = (X10New_c) typeArguments(typeArguments);
        Node result = n.reconstruct(qualifier, tn, arguments, body);
        return result;
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10New_c n = (X10New_c) super.buildTypesOverride(tb);
        if (n.body() != null) {
            // FIXME: should instead override TypeBuilder.pushAnonClass()
            ((X10ClassDef)n.anonType()).setTypeBounds(Types.ref(new TypeConstraint()));
        }
        List<TypeNode> typeArgs = n.visitList(n.typeArguments(), tb);
        n = (X10New_c) n.typeArguments(typeArgs);
        n = (X10New_c) X10Del_c.visitAnnotations(n, tb);
        return n;
    }

    List<TypeNode> typeArguments;

    public List<TypeNode> typeArguments() {
        return typeArguments;
    }

    public X10New typeArguments(List<TypeNode> args) {
        if (args == this.typeArguments) return this;
        X10New_c n = (X10New_c) copy();
        n.typeArguments = new ArrayList<TypeNode>(args);
        return n;
    }

    @Override
    protected New_c typeCheckHeader(TypeChecker childtc) throws SemanticException {
        X10New_c n = (X10New_c) super.typeCheckHeader(childtc);
        List<TypeNode> typeArguments = visitList(n.typeArguments(), childtc);
        n = (X10New_c) n.typeArguments(typeArguments);

        if (n.body() != null) {
            Ref<? extends Type> ct = n.objectType().typeRef();
            ClassDef anonType = n.anonType();

            assert anonType != null;

            X10TypeSystem ts = (X10TypeSystem) childtc.typeSystem();

            X10Flags flags = X10Flags.toX10Flags(ct.get().toClass().flags());
            if (!flags.isInterface()) {
                anonType.superType(ct);
            }
            else /*if (flags.isValue()) {
                anonType.superType(Types.<Type> ref(ts.Value()));
                anonType.setInterfaces(Collections.<Ref<? extends Type>> singletonList(ct));
            }
            else */{
                anonType.superType(Types.<Type> ref(ts.Object()));
                anonType.setInterfaces(Collections.<Ref<? extends Type>> singletonList(ct));
            }

            assert anonType.superType() != null;
            assert anonType.interfaces().size() <= 1;
        }

        return n;
    }

    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
        Node n = super.typeCheckOverride(parent, tc);
        NodeVisitor childtc = tc.enter(parent, n);
        List<AnnotationNode> oldAnnotations = ((X10Ext) ext()).annotations();
        if (oldAnnotations == null || oldAnnotations.isEmpty()) {
            return n;
        }
        List<AnnotationNode> newAnnotations = node().visitList(oldAnnotations, childtc);
        if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
            return ((X10Del) n.del()).annotations(newAnnotations);
        }
        return n;
    }

    /**
     * @param ar
     * @param ct
     * @throws SemanticException
     */
    protected New findQualifier(TypeChecker ar, ClassType ct) throws SemanticException {
        // If we're instantiating a non-static member class, add a "this"
        // qualifier.
        NodeFactory nf = ar.nodeFactory();
        TypeSystem ts = ar.typeSystem();
        Context c = ar.context();

        // Search for the outer class of the member.  The outer class is
        // not just ct.outer(); it may be a subclass of ct.outer().
        Type outer = null;
        
        Name name = ct.name();
        ClassType t = c.currentClass();
        
        // We're in one scope too many.
        if (t == anonType) {
            t = (ClassType) t.container();
        }
        if (anonType!=null) {
            outer = t;
            t = null;
        }
        
        // Search all enclosing classes for the type.
        while (t != null) {
            try {
                Type mt = ts.findMemberType(t, name, c);

                if (mt instanceof ClassType) {
                    ClassType cmt = (ClassType) mt;
                    if (cmt.def() == ct.def()) {
                    outer = t;
                    break;
                    }
                }
            }
            catch (SemanticException e) {
            }
            
            t = t.outer();
        }
        
        if (outer == null) {
            throw new SemanticException("Could not find non-static member class \"" +
                                        name + "\".", position());
        }
        
        // Create the qualifier.
        Expr q;
        Position cg = X10NodeFactory_c.compilerGenerated(position());

        if (outer.typeEquals(c.currentClass(), ar.context())) {
            q = nf.This(cg);
        }
        else {
            q = nf.This(cg,
                        nf.CanonicalTypeNode(cg, outer));
        }
        
        q = q.type(outer);
        return qualifier(q);
    }
    
    public New_c typeCheckObjectType(TypeChecker childtc) throws SemanticException {
        X10NodeFactory nf = (X10NodeFactory) childtc.nodeFactory();
        X10TypeSystem ts = (X10TypeSystem) childtc.typeSystem();
        Context c = childtc.context();

        X10New_c n = this;

        // Override to associate the type args with the type rather than with
        // the constructor.

        Expr qualifier = n.qualifier;
        TypeNode tn = n.tn;
        List<Expr> arguments = n.arguments;
        ClassBody body = n.body;
        List<TypeNode> typeArguments = n.typeArguments;

        typeArguments = n.visitList(typeArguments, childtc);

        if (qualifier == null) {
            if (typeArguments.size() > 0) {
                if (tn instanceof AmbTypeNode) {
                    AmbTypeNode atn = (AmbTypeNode) tn;
                    tn = nf.AmbDepTypeNode(atn.position(), atn.prefix(), atn.name(), typeArguments, Collections.<Expr>emptyList(), null);
                    tn = tn.typeRef(atn.typeRef());
                }
                else {
                    throw new InternalCompilerError("Unexpected type node " + tn + " + with type arguments " + typeArguments, n.position());
                }
            }

            tn = (TypeNode) n.visitChild(tn, childtc);

            Type t = tn.type();
            t = ts.expandMacros(t);

            CConstraint xc = X10TypeMixin.xclause(t);
            t = X10TypeMixin.baseType(t);

            if (!(t instanceof X10ClassType)) {
                QName name = QName.make(((AmbTypeNode) n.tn).name().id().toString());
                t = ((X10TypeSystem_c) ts).createFakeClass(name, null);
                tn = nf.CanonicalTypeNode(tn.position(), t);
//                throw new SemanticException("Cannot instantiate type " + t + ".");
            }

            X10ClassType ct = (X10ClassType) t;

            if ((ct.isMember()&& !ct.flags().isStatic())) {
                final X10New_c newC = (X10New_c) n.objectType(tn);
                New k = newC.findQualifier(childtc, ct);
                tn = k.objectType();
                qualifier = (Expr) k.visitChild(k.qualifier(), childtc);
            }
            t = X10TypeMixin.xclause(ct, xc);

            ((Ref<Type>) tn.typeRef()).update(t);
        }
        else {
            qualifier = (Expr) n.visitChild(qualifier, childtc);

            if (!(tn instanceof AmbTypeNode) || ((AmbTypeNode) tn).prefix() != null) {
                throw new SemanticException("Only simply-named member classes may be instantiated by a qualified new expression.", tn.position());
            }

            // We have to disambiguate the type node as if it were a member of
            // the
            // static type, outer, of the qualifier. For Java this is simple:
            // type
            // nested type is just a name and we
            // use that name to lookup a member of the outer class. For some
            // extensions (e.g., PolyJ), the type node may be more complex than
            // just a name. We'll just punt here and let the extensions handle
            // this complexity.

            Name name = ((AmbTypeNode) tn).name().id();
            assert name != null;

            if (!qualifier.type().isClass()) {
                throw new SemanticException("Cannot instantiate member class of non-class type.", n.position());
            }

            Type t = ts.findMemberType(X10TypeMixin.baseType(qualifier.type()), name, c);
            t = ts.expandMacros(t);

            CConstraint xc = X10TypeMixin.xclause(t);
            t = X10TypeMixin.baseType(t);

            if (!(t instanceof X10ClassType)) {
                throw new SemanticException("Cannot instantiate type " + t + ".", n.position());
            }

            X10ClassType ct = (X10ClassType) t;

            if (typeArguments.size() > 0) {
                List<Type> typeArgs = new ArrayList<Type>(typeArguments.size());

                for (TypeNode tan : typeArguments) {
                    typeArgs.add(tan.type());
                }

                if (typeArgs.size() != ct.x10Def().typeParameters().size()) {
                    throw new SemanticException("Cannot instantiate type " + ct + "; incorrect number of type arguments.", n.position());
                }

                ct = ct.typeArguments(typeArgs);
            }

            t = X10TypeMixin.xclause(ct, xc);

            ((Ref<Type>) tn.typeRef()).update(t);
            tn = nf.CanonicalTypeNode(tn.position(), tn.typeRef());
        }

        n = (X10New_c) n.reconstruct(qualifier, tn, arguments, body);
        // [IP] Should retain the type argument nodes, even if the type is resolved.
        //n = (X10New_c) n.typeArguments(Collections.EMPTY_LIST);

        return n;
    }

   

    public static interface MatcherMaker<PI> {
        public Matcher<PI> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes);
    }

    public static Pair<ConstructorInstance, List<Expr>> tryImplicitConversions(X10ProcedureCall n, ContextVisitor tc,
            Type targetType, List<Type> argTypes) throws SemanticException {
        final X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        final Context context = tc.context();

        List<ConstructorInstance> methods = ts.findAcceptableConstructors(targetType, new DumbConstructorMatcher(targetType, argTypes, context));
        return Converter.tryImplicitConversions(n, tc, targetType, methods, new MatcherMaker<ConstructorInstance>() {
            public Matcher<ConstructorInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
                return ts.ConstructorMatcher(ct, argTypes, context);
            }
        });
    }

    protected void typeCheckFlags(ContextVisitor tc) throws SemanticException {
        super.typeCheckFlags(tc);
        ClassType ct = tn.type().toClass();
        if (X10Flags.toX10Flags(ct.flags()).isStruct() && ! newOmitted) {
            throw new Errors.NewOfStructNotPermitted(this);
        }
    }

    public Node typeCheck(ContextVisitor tc) {
        try {
            return typeCheck1(tc);
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
            X10TypeSystem_c ts = (X10TypeSystem_c) tc.typeSystem();
            List<Type> argTypes = new ArrayList<Type>(this.arguments.size());
            for (Expr a : this.arguments) {
                argTypes.add(a.type());
            }
            X10ClassType ct = (X10ClassType) X10TypeMixin.baseType(tn.type());
            X10ConstructorInstance ci = ts.createFakeConstructor(ct, Flags.PUBLIC, argTypes, e);
            Type rt = ci.returnType();
            return (X10New_c) constructorInstance(ci).type(rt);
        }
    }
    public Node typeCheck1(ContextVisitor tc) throws SemanticException {
        final X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

        // ///////////////////////////////////////////////////////////////////
        // Inline the super call here and handle type arguments.
        // ///////////////////////////////////////////////////////////////////

        // [IP] The type arguments are retained for later use.
        //assert (this.typeArguments().size() == 0) : position().toString();

        List<Type> argTypes = new ArrayList<Type>(this.arguments.size());
        for (Expr e : this.arguments) {
        	//Type argType = PlaceChecker.ReplaceHereByPlaceTerm((Type) e.type(), (X10Context) tc.context());
        	Type argType = e.type();
        	argTypes.add(argType);
        }

        typeCheckFlags(tc);
        typeCheckNested(tc);

        X10New_c result = this;

        Type t = result.objectType().type();
        X10ClassType ct = (X10ClassType) X10TypeMixin.baseType(t);

        X10ConstructorInstance ci;
        List<Expr> args;

        Pair<ConstructorInstance, List<Expr>> p = findConstructor(tc, result, ct, argTypes, result.anonType());
        ci = (X10ConstructorInstance) p.fst();
        args = p.snd();
        if (ci.error() != null) {
            throw ci.error();
        }

        X10ParsedClassType container = (X10ParsedClassType) ci.container();
        if (!ct.typeArguments().isEmpty() && ct.typeArguments().equals(container.x10Def().typeParameters())) {
            t = new TypeParamSubst(xts, container.typeArguments(), container.x10Def().typeParameters()).reinstantiate(t);
            result = (X10New_c) result.objectType(result.objectType().typeRef(Types.ref(t)));
        }

        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        Type tp = ci.returnType();
        tp = PlaceChecker.ReplaceHereByPlaceTerm(tp, (X10Context) tc.context());
        Type tp1 = (Type) tp.copy();
        
        if (!ts.isSubtype(tp1, t, tc.context())) {
            throw new SemanticException("Constructor return type " + tp + " is not a subtype of " + t + ".", result.position());
        }

        // Copy the method instance so we can modify it.
        //tp = ((X10Type) tp).setFlags(X10Flags.ROOTED);
        ci = (X10ConstructorInstance) ci.returnType(tp);
        ci = result.adjustCI(ci, tc);
        result = (X10New_c) result.constructorInstance(ci);
        result = (X10New_c) result.arguments(args);

        result.checkWhereClause();

        Type type = ci.returnType();
        if (result.body() != null) {
            // If creating an anonymous class, we need to adjust the type
            // to be based on anonType rather than on the supertype.
            ClassDef anonTypeDef = result.anonType();
            Type anonType = anonTypeDef.asType();
            type = X10TypeMixin.xclause(X10TypeMixin.baseType(anonType), X10TypeMixin.xclause(type));
          
        }

        result = (X10New_c) result.type(type);
        return result;
    }

    /**
     * Looks up a constructor with given name and argument types.
     */
    public static Pair<ConstructorInstance,List<Expr>> findConstructor(ContextVisitor tc, X10ProcedureCall n,
            Type targetType, List<Type> actualTypes) {
        return findConstructor(tc, n, targetType, actualTypes, null);
    }
    public static Pair<ConstructorInstance,List<Expr>> findConstructor(ContextVisitor tc, X10ProcedureCall n,
            Type targetType, List<Type> actualTypes, ClassDef anonType) {
        X10ConstructorInstance ci;
        X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();
        X10Context context = (X10Context) tc.context();
        boolean haveUnknown = xts.hasUnknown(targetType);
        for (Type t : actualTypes) {
            if (xts.hasUnknown(t)) haveUnknown = true;
        }
        SemanticException error = null;
        try {
            return findConstructor(tc, context, n, targetType, actualTypes, anonType);
        } catch (SemanticException e) {
            error = e;
        }
        // If not returned yet, fake the constructor instance.
        Collection<X10ConstructorInstance> cis = null;
        try {
            cis = findConstructors(tc, targetType, actualTypes);
        } catch (SemanticException e) {
            if (error == null) error = e;
        }
        // See if all matches have the same return type, and save that to avoid losing information.
        Type rt = null;
        if (cis != null) {
            for (X10ConstructorInstance xci : cis) {
                if (rt == null) {
                    rt = xci.returnType();
                } else if (!xts.typeEquals(rt, xci.returnType(), context)) {
                    if (xts.typeBaseEquals(rt, xci.returnType(), context)) {
                        rt = X10TypeMixin.baseType(rt);
                    } else {
                        rt = null;
                        break;
                    }
                }
            }
        }
        if (haveUnknown)
            error = new SemanticException(); // null message
        ci = xts.createFakeConstructor(targetType.toClass(), Flags.PUBLIC, actualTypes, error);
        if (rt != null) ci = ci.returnType(rt);
        return new Pair<ConstructorInstance, List<Expr>>(ci, n.arguments());
    }

    private static Pair<ConstructorInstance,List<Expr>> findConstructor(ContextVisitor tc, X10Context xc,
            X10ProcedureCall n, Type targetType, List<Type> argTypes, ClassDef anonType) throws SemanticException {

        X10ConstructorInstance ci = null;
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

        X10ClassType ct = (X10ClassType) targetType;
        try {
            if (!ct.flags().isInterface()) {
                Context c = tc.context();
                if (anonType != null) {
                    c = c.pushClass(anonType, anonType.asType());
                }
                ci = xts.findConstructor(targetType, xts.ConstructorMatcher(targetType, argTypes, c));
            }
            else {
                ConstructorDef dci = xts.defaultConstructor(n.position(), 
                        Types.<ClassType> ref(ct));
                ci = (X10ConstructorInstance) dci.asInstance();
            }

            return new Pair<ConstructorInstance, List<Expr>>(ci, n.arguments());
        }
        catch (SemanticException e) {
            // Now, try to find the method with implicit conversions, making
            // them explicit.
            try {
                Pair<ConstructorInstance, List<Expr>> p = tryImplicitConversions(n, tc, targetType, argTypes);
                return p;
            }
            catch (SemanticException e2) {
                throw e;
            }
        }
        //TypeSystem_c.ConstructorMatcher matcher = xts.ConstructorMatcher(targetType, argTypes, xc);
        //throw new Errors.MethodOrStaticConstructorNotFound(matcher, n.position());
    }

    private static Collection<X10ConstructorInstance> findConstructors(ContextVisitor tc, Type targetType,
            List<Type> actualTypes) throws SemanticException {
        X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();
        X10Context context = (X10Context) tc.context();
        if (targetType == null) {
            // TODO
            return Collections.emptyList();
        }
        return xts.findConstructors(targetType, xts.ConstructorMatcher(targetType, actualTypes, context));
    }

    private void checkWhereClause() throws SemanticException {
        X10ConstructorInstance ci = (X10ConstructorInstance) constructorInstance();
        if (ci != null) {
            CConstraint guard = ci.guard();
            if (guard != null && !guard.consistent()) {
                throw new SemanticException("Constructor guard not satisfied by caller.", position());
            }
        }
    }

    /*
     * Compute the new resulting type for the method call by replacing this and
     * any argument variables that occur in the rettype depclause with new
     * variables whose types are determined by the static type of the receiver
     * and the actual arguments to the call.
     * 
     * Also add the self.home==here clause.
     */
    private X10ConstructorInstance adjustCI(X10ConstructorInstance xci, ContextVisitor tc) throws SemanticException {
        if (xci == null)
            return (X10ConstructorInstance) this.ci;
        Type type = xci.returnType();
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

        // Add self.home == here to the return type.
        if (! ts.isStructType(type)) {

        	// Add this even in 2.1 -- the place where this object is created
        	// is tracked in the type through a fake field "here".
        	// This field does not exist at runtime in the object -- but that does not
        	// prevent the compiler from imagining that it exists.
        	type = PlaceChecker.AddIsHereClause(type, tc.context());
        	// Add self != null
        	type = X10TypeMixin.addDisBinding(type, X10TypeMixin.selfVar(type), XTerms.NULL);
        }
        
        xci = (X10ConstructorInstance) xci.returnType(type);
        return xci;
       // return (X10New_c) this.constructorInstance(xci).type(type);
    }

    // TODO: Move down into New_c
    public void dump(CodeWriter w) {
        super.dump(w);

        if (ci != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(instance " + ci + ")");
            w.end();
        }

        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(arguments " + arguments + ")");
        w.end();
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        for (Iterator<AnnotationNode> i = (((X10Ext) this.ext()).annotations()).iterator(); i.hasNext(); ) {
            AnnotationNode an = i.next();
            an.prettyPrint(w, tr);
            w.allowBreak(0, " ");
        }
        super.prettyPrint(w, tr);
    }
    
}
