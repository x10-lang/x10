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

package x10.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.ProcedureCall;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
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
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.ThisDef;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10MemberDef;
import polyglot.types.Context;

import x10.types.X10ParsedClassType;
import polyglot.types.TypeSystem;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.TypeSystem_c;
import polyglot.types.NoMemberException;

import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
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
    public X10New anonType(X10ClassDef anonType) {
        return super.anonType(anonType);
    }
    @Override
    public X10New arguments(List<Expr> arguments) {
        return (X10New) super.arguments(arguments);
    }
    @Override
    public X10New body(ClassBody body) {
        return (X10New) super.body(body);
    }
    @Override
    public X10New constructorInstance(ConstructorInstance ci) {
        return (X10New) super.constructorInstance(ci);
    }
    @Override
    public X10New objectType(TypeNode tn) {
        return (X10New) super.objectType(tn);
    }
    @Override
    public X10New qualifier(Expr qualifier) {
        return (X10New) super.qualifier(qualifier);
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) {
        X10New_c n = (X10New_c) super.buildTypesOverride(tb);
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
    protected X10New_c typeCheckHeader(TypeChecker childtc) {
        X10New_c n = (X10New_c) super.typeCheckHeader(childtc);
        List<TypeNode> typeArguments = visitList(n.typeArguments(), childtc);
        n = (X10New_c) n.typeArguments(typeArguments);

        if (n.body() != null) {
            Ref<? extends Type> ct = n.objectType().typeRef();
            ClassDef anonType = n.anonType();

            assert anonType != null;

            TypeSystem ts = (TypeSystem) childtc.typeSystem();

            Flags flags = ct.get().toClass().flags();
            if (!flags.isInterface()) {
                anonType.superType(ct);
            }
            else /*if (flags.isValue()) {
                anonType.superType(Types.<Type> ref(ts.Value()));
                anonType.setInterfaces(Collections.<Ref<? extends Type>> singletonList(ct));
            }
            else */{
            	// [DC] null means it's a new root in the object hierarchy (no more x10.lang.Object)
                anonType.superType(null);
                anonType.setInterfaces(Collections.<Ref<? extends Type>> singletonList(ct));
            }

            assert anonType.interfaces().size() <= 1;
        }

        return n;
    }

    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
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
     */
    protected X10New findQualifier(TypeChecker ar, ClassType ct) {
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
            Errors.issue(ar.job(),
                    new Errors.CouldNotFindNonStaticMemberClass(name, position()));
            outer = c.currentClass();
        }
        
        // Create the qualifier.
        Expr q;
        Position cg = Position.compilerGenerated(position());

        if (outer.typeEquals(c.currentClass(), ar.context())) {
            q = nf.This(cg);
        }
        else {
            q = nf.This(cg, nf.CanonicalTypeNode(cg, outer));
        }
        
        q = q.type(outer);
        return qualifier(q);
    }

    protected Name getName(TypeNode tn) {
        if (tn instanceof CanonicalTypeNode) {
            if (tn.type().isClass()) {
                return tn.type().toClass().name();
            } else {
                return null;
            }
        }
        if (tn instanceof AmbTypeNode) {
            return ((AmbTypeNode) tn).name().id();
        }
        if (tn instanceof AmbDepTypeNode) {
            return getName(((AmbDepTypeNode) tn).base());
        }
        return null;
    }

    public X10New_c typeCheckObjectType(TypeChecker childtc) {
        NodeFactory nf = (NodeFactory) childtc.nodeFactory();
        TypeSystem ts = childtc.typeSystem();
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
        qualifier = (Expr) n.visitChild(qualifier, childtc);

        if (!(tn instanceof CanonicalTypeNode)) {
        Name name = getName(tn);

        Type t;

        if (qualifier == null) {
            if (typeArguments.size() > 0) {
                if (tn instanceof AmbTypeNode) {
                    AmbTypeNode atn = (AmbTypeNode) tn;
                    tn = nf.AmbDepTypeNode(atn.position(), atn.prefix(), atn.name(), typeArguments, Collections.<Expr>emptyList(), null);
                    tn = tn.typeRef(atn.typeRef());
                }
            }

            tn = (TypeNode) n.visitChild(tn, childtc);

            t = tn.type();
        }
        else {

            if (!(tn instanceof AmbTypeNode) || ((AmbTypeNode) tn).prefix() != null) {
                Errors.issue(childtc.job(),
                        new Errors.OnlySimplyNameMemberClassMayBeInstantiated(tn.position()));
            }

            if (!qualifier.type().isClass()) {
                Errors.issue(childtc.job(),
                        new Errors.CannotInstantiateMemberClass(n.position()));
            }

            tn = nf.CanonicalTypeNode(tn.position(), tn.typeRef());

            // We have to disambiguate the type node as if it were a member of
            // the static type, outer, of the qualifier.

            try {
                t = ts.findMemberType(Types.baseType(qualifier.type()), name, c);
            } catch (SemanticException e) {
                t = ts.unknownType(tn.position());
                if (!qualifier.type().isClass()) {
                    qualifier = null; // will fake it
                }
            }
        }
        t = ts.expandMacros(t);

        CConstraint xc = Types.xclause(t);
        t = Types.baseType(t);

        if (!(t instanceof X10ClassType)) {
            if (name == null)
                name = Name.makeFresh();
            QName outer = qualifier == null ? null : qualifier.type().toClass().fullName();
            QName qname = QName.make(outer, name);
            t = ts.createFakeClass(qname, new Errors.CannotInstantiateType(tn.type(), position()));
        }

        X10ClassType ct = (X10ClassType) t;

        if (qualifier == null && ct.isMember() && !ct.flags().isStatic()) {
            final X10New_c newC = (X10New_c) n.objectType(tn);
            X10New k = newC.findQualifier(childtc, ct);
            tn = k.objectType();
            qualifier = (Expr) k.visitChild(k.qualifier(), childtc);
        }

        /*  
        if (typeArguments.size() > 0) {
            List<Type> typeArgs = new ArrayList<Type>(typeArguments.size());

            for (TypeNode tan : typeArguments) {
                typeArgs.add(tan.type());
            }

            if (typeArgs.size() != ct.x10Def().typeParameters().size()) {
                Errors.issue(childtc.job(),
                        new Errors.CannotInstantiateType(ct, n.position()));
                // TODO: fake missing args or delete extra args
            }
            // [DC] this is where the problem is!  XTENLANG-3133
            ct = ct.typeArguments(typeArgs);
        }
        */

        t = Types.xclause(ct, xc);

        ((Ref<Type>) tn.typeRef()).update(t);
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
        final TypeSystem_c ts = (TypeSystem_c) tc.typeSystem();
        final Context context = tc.context();

        List<ConstructorInstance> methods = ts.findAcceptableConstructors(targetType, ts.ConstructorMatcher(targetType, Collections.EMPTY_LIST,argTypes, context, true));
        return Converter.tryImplicitConversions(n, tc, targetType, methods, new MatcherMaker<ConstructorInstance>() {
            public Matcher<ConstructorInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
                return ts.ConstructorMatcher(ct, argTypes, context);
            }
        });
    }

    public Node typeCheck(ContextVisitor tc) {
        final TypeSystem xts = (TypeSystem) tc.typeSystem();

        // ///////////////////////////////////////////////////////////////////
        // Inline the super call here and handle type arguments.
        // ///////////////////////////////////////////////////////////////////

        // [IP] The type arguments are retained for later use.
        //assert (this.typeArguments().size() == 0) : position().toString();

        SemanticException error = null;

        List<Type> argTypes = new ArrayList<Type>(this.arguments.size());
        for (Expr e : this.arguments) {
        	//Type argType = PlaceChecker.ReplaceHereByPlaceTerm((Type) e.type(), (X10Context) tc.context());
        	Type argType = e.type();
        	argTypes.add(argType);
        }

        Position pos = position();

        try {
        	typeCheckFlags(tc);
        } catch (SemanticException e) {
        	Errors.issue(tc.job(), e, this);
        	if (error == null) { error = e; }
        }
        try {
        	typeCheckNested(tc);
        } catch (SemanticException e) {
        	Errors.issue(tc.job(), e, this);
        	if (error == null) { error = e; }
        }

        X10New_c result = this;

        Type t = result.objectType().type();
        X10ClassType ct = (X10ClassType) Types.baseType(t);

        X10ConstructorInstance ci;
        List<Expr> args;

        Pair<ConstructorInstance, List<Expr>> p = findConstructor(tc, result, ct, argTypes, result.anonType());
        ci = (X10ConstructorInstance) p.fst();
        args = p.snd();
        if (ci.error() != null) {
            Errors.issue(tc.job(), ci.error(), this);
        } else if (error != null) {
            ci = ci.error(error);
        }

        X10ParsedClassType container = (X10ParsedClassType) ci.container();
        if (!ct.x10Def().typeParameters().isEmpty() && (ct.typeArguments() == null || ct.typeArguments().isEmpty())) {
            t = new TypeParamSubst(xts, container.typeArguments(), container.x10Def().typeParameters()).reinstantiate(t);
            result = (X10New_c) result.objectType(result.objectType().typeRef(Types.ref(t)));
        }

        try {
            Types.checkMissingParameters(result.objectType());
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, result.objectType());
        }

        TypeSystem ts = (TypeSystem) tc.typeSystem();
        Type tp = ci.returnType();
        final Context context = tc.context();
        tp = PlaceChecker.ReplaceHereByPlaceTerm(tp, context);
        Type tp1 = Types.instantiateTypeParametersExplicitly(tp);
        Type t1 = Types.instantiateTypeParametersExplicitly(t);
        
        if (ts.hasUnknown(tp1)) {
            SemanticException e = new SemanticException("Inconsistent constructor return type", pos);
            Errors.issue(tc.job(), e, this);
            if (ci.error() == null) {
                ci = ci.error(e);
            }
        }
        boolean doCoercion = false;
        if (!ts.hasUnknown(tp) && !ts.isSubtype(tp1, t1, context)) {
            Expr newNewExpr = Converter.attemptCoercion(tc, result.type(tp1), t1);
            if (newNewExpr != null) {
                // I cann't keep newNewExpr, because result is still going to be modified (and I cannot do this check later because ci might be modified and it is stored in result)
                // so, sadly, I have to call attemptCoercion twice.
                doCoercion = true;
            } else {
                SemanticException e = Errors.NewIncompatibleType.make(result.type(tp1),  t1, tc, pos);
                Errors.issue(tc.job(), e, this);
                if (ci.error() == null) {
                    ci = ci.error(e);
                }
            }
        }

        // Copy the method instance so we can modify it.
        //tp = ((X10Type) tp).setFlags(X10Flags.ROOTED);
        ci = ci.returnType(tp);
        ci = result.adjustCI(ci, tc, qualifier());

        try {
            checkWhereClause(ci, pos, context);
        } catch (SemanticException e) {
            if (ci.error() == null) { ci = ci.error(e); }
        }

        result = (X10New_c) result.constructorInstance(ci);
        result = (X10New_c) result.arguments(args);

        Type type = ci.returnType();
        if (result.body() != null) {
            // If creating an anonymous class, we need to adjust the type
            // to be based on anonType rather than on the supertype.
            ClassDef anonTypeDef = result.anonType();
            Type anonType = anonTypeDef.asType();
            type = Types.xclause(Types.baseType(anonType), Types.xclause(type));

            // Capture "this" for anonymous classes in a non-static context
            if (!context.inStaticContext()) {
                CodeDef code = context.currentCode();
                if (code instanceof X10MemberDef) {
                    ThisDef thisDef = ((X10MemberDef) code).thisDef();
                    if (null == thisDef) {
                        throw new InternalCompilerError(position(), "X10New_c.typeCheck: thisDef is null for containing code " +code);
                    }
                    assert (thisDef != null);
                    context.recordCapturedVariable(thisDef.asInstance());
                }
            }
        }

        result = (X10New_c) result.type(type);
        return doCoercion ?
                Converter.attemptCoercion(tc, result, t1) :
                result;
    }

    /**
     * Looks up a constructor with given name and argument types.
     */
    public static Pair<ConstructorInstance,List<Expr>> findConstructor(ContextVisitor tc, X10ProcedureCall n,
            Type targetType, List<Type> actualTypes) {
        return findConstructor(tc, n, targetType, actualTypes, null);
    }
    public static Pair<ConstructorInstance,List<Expr>> findConstructor(ContextVisitor tc, X10ProcedureCall n,
            Type targetType, List<Type> actualTypes, X10ClassDef anonType) {
        X10ConstructorInstance ci;
        TypeSystem xts = tc.typeSystem();
        Context context = (Context) tc.context();
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
                        rt = Types.baseType(rt);
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

    private static Pair<ConstructorInstance,List<Expr>> findConstructor(ContextVisitor tc, Context xc,
            X10ProcedureCall n, Type targetType, List<Type> argTypes, X10ClassDef anonType) throws SemanticException {

        X10ConstructorInstance ci = null;
        TypeSystem xts = (TypeSystem) tc.typeSystem();

        X10ClassType ct = (X10ClassType) targetType;
        try {
            if (!ct.flags().isInterface()) {
                Context c = tc.context();
                if (anonType != null) {
                    c = c.pushClass(anonType, anonType.asType());
                }
                List<Type> typeArgs = ct.typeArguments();
                if (typeArgs == null) {
                    typeArgs = Collections.<Type>emptyList();
                }
                ci = xts.findConstructor(targetType, xts.ConstructorMatcher(targetType, typeArgs, argTypes, c));
            }
            else {
                ConstructorDef dci = xts.defaultConstructor(n.position(), n.position(), Types.<ClassType> ref(ct));
                ci = (X10ConstructorInstance) dci.asInstance();
            }

            // Force type inference when a constructor is invoked with no type arguments from an instance method of the same class
            List<Type> tas = ct.typeArguments();
            List<ParameterType> tps = ct.x10Def().typeParameters();
            if (!tps.isEmpty() && (tas == null || tas.isEmpty())) {
                throw new Errors.TypeIsMissingParameters(ct, tps, n.position());
            }

            return new Pair<ConstructorInstance, List<Expr>>(ci, n.arguments());
        }
        catch (SemanticException e) {
            e.setPosition(n.position());
            // only if we didn't find any methods, try coercions.
            if (!(e instanceof NoMemberException)) {
                throw e;
            }
            
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
        TypeSystem xts = tc.typeSystem();
        Context context = (Context) tc.context();
        if (targetType == null) {
            // TODO
            return Collections.emptyList();
        }
        return xts.findConstructors(targetType, xts.ConstructorMatcher(targetType, actualTypes, context));
    }

    private static void checkWhereClause(X10ConstructorInstance ci, Position pos, Context context) throws SemanticException {
        if (ci != null) {
            CConstraint guard = ci.guard();
            TypeConstraint tguard = ci.typeGuard();
            if ((guard != null && !guard.consistent()) || (tguard != null && !tguard.consistent(context))) {
                 throw new SemanticException("Constructor guard not satisfied by caller.", pos);
            }
        }
    }

    /*
     * Compute the new resulting type for the method call by replacing this and
     * any argument variables that occur in the rettype depclause with new
     * variables whose types are determined by the static type of the receiver
     * and the actual arguments to the call.
     * 
     * Also self.$$here==here.
     * Add self!=null.
     */
    private X10ConstructorInstance adjustCI(X10ConstructorInstance xci, ContextVisitor tc, Expr outer) {
        if (xci == null)
            return (X10ConstructorInstance) this.ci;
        Type type = xci.returnType();
        if (outer != null) {
            type = Types.addInOuterClauses(type, outer.type());
        } else {
            // this could still be a local class, and the outer this has to be captured.
            Type baseType = Types.baseType(type);
            if (baseType instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) baseType;
                if (ct.isLocal()) {
                    Type outerT = ct.outer();
                    CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
                    Type outerTB = Types.baseType(outerT);
                    if (outerTB instanceof X10ClassType) {
                        X10ClassType outerct = (X10ClassType) outerTB;
                        c.addSelfBinding(outerct.def().thisVar());
                        outerT = Types.xclause(outerT, c);
                        type = Types.addInOuterClauses(type, outerT);
                    }
                }
            }
        }
        
        TypeSystem ts = (TypeSystem) tc.typeSystem();

        if (ts.isStructType(type)) 
            return xci;
        // Add self.home == here to the return type.
        // Add this even in 2.1 -- the place where this object is created
        // is tracked in the type through a fake field "$$here".
        // This field does not exist at runtime in the object -- but that does not
        // prevent the compiler from imagining that it exists.
        ConstrainedType type1 = Types.toConstrainedType(type);
        type1 = (ConstrainedType) PlaceChecker.AddIsHereClause(type1, tc.context());
        // Add self != null
        type1 = (ConstrainedType) Types.addDisBinding(type1, Types.selfVar(type1), ConstraintManager.getConstraintSystem().xnull());
        if (! Types.consistent(type1))
            Errors.issue(tc.job(), new Errors.InconsistentType(type1, xci.position()));
        xci = (X10ConstructorInstance) xci.returnType(type1);
        return xci;

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
