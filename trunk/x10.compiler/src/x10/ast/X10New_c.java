/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.Matcher;
import polyglot.types.MemberInstance;
import polyglot.types.Name;
import polyglot.types.NoMemberException;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.extension.X10Del_c;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10Flags;
import x10.types.X10ParsedClassType_c;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;


/**
 * new C[T](e)
 * 
 * @author nystrom
 */
public class X10New_c extends New_c implements X10New {
    public X10New_c(Position pos, Expr qualifier, TypeNode tn, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
        super(pos, qualifier, tn, arguments, body);
        this.typeArguments = TypedList.copyAndCheck(typeArguments, TypeNode.class, true);
    }

    boolean isStructConstructorCall = false;
    public void setStructConstructorCall() {
    	isStructConstructorCall  = true;
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
        List<TypeNode> typeArgs = (List<TypeNode>) n.visitList(n.typeArguments(), tb);
        n = (X10New_c) n.typeArguments(typeArgs);
        n = (X10New_c) X10Del_c.visitAnnotations(n, tb);
        return n;
    }

    List<TypeNode> typeArguments;

    public List<TypeNode> typeArguments() {
        return typeArguments;
    }

    public X10New typeArguments(List<TypeNode> args) {
        X10New_c n = (X10New_c) copy();
        n.typeArguments = new ArrayList<TypeNode>(args);
        return n;
    }

    @Override
    protected New_c typeCheckHeader(TypeChecker childtc) throws SemanticException {
        X10New_c n = (X10New_c) super.typeCheckHeader(childtc);
        List<TypeNode> typeArguments = (List<TypeNode>) visitList(n.typeArguments, childtc);
        n = (X10New_c) n.typeArguments(typeArguments);

        if (n.body != null) {
            Ref<? extends Type> ct = n.tn.typeRef();
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

        typeArguments = visitList(typeArguments, childtc);

        if (qualifier == null) {
            if (typeArguments.size() > 0) {
                if (tn instanceof AmbTypeNode) {
                    AmbTypeNode atn = (AmbTypeNode) tn;
                    tn = nf.AmbDepTypeNode(atn.position(), atn.prefix(), atn.name(), typeArguments, Collections.EMPTY_LIST, null);
                    tn = tn.typeRef(atn.typeRef());
                }
                else {
                    throw new InternalCompilerError("Unexpected type node " + tn + " + with type arguments " + typeArguments, position());
                }
            }

            tn = (TypeNode) n.visitChild(tn, childtc);

            if (tn.type() instanceof UnknownType) {
                throw new SemanticException();
            }

            Type t = tn.type();
            t = ts.expandMacros(t);

            XConstraint xc = X10TypeMixin.xclause(t);
            t = X10TypeMixin.baseType(t);

            if (!(t instanceof X10ClassType)) {
                throw new SemanticException("Cannot instantiate type " + t + ".");
            }

            X10ClassType ct = (X10ClassType) t;

            if (ct.isMember() && !ct.flags().isStatic()) {
                New k = ((X10New_c) n.objectType(tn)).findQualifier(childtc, ct);
                tn = k.objectType();
                qualifier = (Expr) k.visitChild(k.qualifier(), childtc);
            }
            t = X10TypeMixin.xclause(ct, xc);

            ((Ref<Type>) tn.typeRef()).update(t);
        }
        else {
            qualifier = (Expr) n.visitChild(n.qualifier(), childtc);

            if (!(tn instanceof AmbTypeNode) || ((AmbTypeNode) tn).prefix() != null) {
                throw new SemanticException("Only simply-named member classes may be instantiated by a qualified new expression.", n.objectType().position());
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

            Type t = ts.findMemberType(qualifier.type(), name, c);
            t = ts.expandMacros(t);

            XConstraint xc = X10TypeMixin.xclause(t);
            t = X10TypeMixin.baseType(t);

            if (!(t instanceof X10ClassType)) {
                throw new SemanticException("Cannot instantiate type " + t + ".", n.position());
            }

            X10ClassType ct = (X10ClassType) t;

            if (typeArguments.size() > 0) {
                List<Type> typeArgs = new ArrayList<Type>(this.typeArguments.size());

                for (TypeNode tan : this.typeArguments) {
                    typeArgs.add(tan.type());
                }

                if (typeArguments.size() != ct.x10Def().typeParameters().size()) {
                    throw new SemanticException("Cannot instantiate type " + ct + "; incorrect number of type arguments.", n.position());
                }

                ct = ct.typeArguments(typeArgs);
            }

            t = X10TypeMixin.xclause(ct, xc);

            ((Ref<Type>) tn.typeRef()).update(t);
            tn = nf.CanonicalTypeNode(n.objectType().position(), tn.typeRef());
        }

        n = (X10New_c) n.reconstruct(qualifier, tn, arguments, body);
        n = (X10New_c) n.typeArguments(Collections.EMPTY_LIST);

        return n;
    }

    public static Expr attemptCoercion(ContextVisitor tc, Expr e, Type toType) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();

        if (ts.isSubtypeWithValueInterfaces(e.type(), toType, tc.context())) {
            return e;
        }

        Expr e2 = null;
        if (ts.numericConversionValid(toType, e.type(), e.constantValue(), tc.context())) {
            e2 = nf.X10Cast(e.position(), nf.CanonicalTypeNode(e.position(), toType), e, X10Cast.ConversionType.UNKNOWN_CONVERSION);
        }
        else {
            e2 = nf.X10Cast(e.position(), nf.CanonicalTypeNode(e.position(), toType), e, X10Cast.ConversionType.UNKNOWN_IMPLICIT_CONVERSION);
        }

        e2 = X10Cast_c.check(e2, tc);
        return e2;
    }

    public static interface MatcherMaker<PI> {
        public Matcher<PI> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes);
    }

    /**
     * 
     * @param <PD>
     * @param <PI>
     * @param n
     * @param tc
     * @param targetType
     * @param methods
     *            Unsubstituted, uninstantiated methods. Need to go through
     *            MethodMatcher.instantiate to use.
     * @param maker
     * @return
     * @throws SemanticException
     */
    public static <PD extends ProcedureDef, PI extends ProcedureInstance<PD>> Pair<PI, List<Expr>> tryImplicitConversions(X10ProcedureCall n,
            ContextVisitor tc, Type targetType, List<PI> methods, MatcherMaker<PI> maker) throws SemanticException {
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10Context xc = (X10Context) tc.context();
        ClassDef currentClassDef = xc.currentClassDef();

        List<PI> acceptable = new ArrayList<PI>();
        Map<Def, List<Expr>> newArgs = new HashMap<Def, List<Expr>>();

        List<Type> typeArgs = new ArrayList<Type>(n.typeArguments().size());

        for (TypeNode tn : n.typeArguments()) {
            typeArgs.add(tn.type());
        }

        METHOD: for (Iterator<PI> i = methods.iterator(); i.hasNext();) {
            PI smi = (PI) i.next();

            if (Report.should_report(Report.types, 3))
                Report.report(3, "Trying " + smi);

            List<Expr> transformedArgs = new ArrayList<Expr>();
            List<Type> transformedArgTypes = new ArrayList<Type>();

            List<Type> formals = smi.formalTypes();

            for (int j = 0; j < n.arguments().size(); j++) {
                Expr e = n.arguments().get(j);
                Type toType = formals.get(j);

                try {
                    Expr e2 = attemptCoercion(tc, e, toType);
                    transformedArgs.add(e2);
                    transformedArgTypes.add(e2.type());
                }
                catch (SemanticException ex) {
                    // Implicit cast not allowed here.
                    continue METHOD;
                }
            }

            try {
                Matcher<PI> matcher = maker.matcher(targetType, typeArgs, transformedArgTypes);
                // ((X10ProcedureInstance) smi).returnType();
                // X10MethodInstance_c.checkCall(xc, (X10ProcedureInstance) smi,
                // targetType, typeArgs, transformedArgTypes);
                // // smi = (PI) matcher.instantiate(smi);

                // Reinstantiate using the new argument types.
                // Be careful to re-subst in the type arguments of the container type.
                // This should be cleaner!
                PI raw = (PI) smi.def().asInstance();
                if (smi instanceof MemberInstance) {
                    Type container = ((MemberInstance) smi).container();
                    Type base = X10TypeMixin.baseType(container);
                    if (base instanceof X10ClassType) {
                        X10ParsedClassType_c ct = (X10ParsedClassType_c) base;
                        raw = ct.subst().reinstantiate(raw);
                    }
                }
                PI smi2 = (PI) matcher.instantiate(raw);
                // ((X10ProcedureInstance) smi2).returnType();
                acceptable.add(smi2);
                newArgs.put(smi2.def(), transformedArgs);
            }
            catch (SemanticException e) {
                System.out.print("");
            }
        }

        if (acceptable.size() == 0) {
            if (n instanceof New || n instanceof ConstructorCall)
                throw new NoMemberException(NoMemberException.CONSTRUCTOR, "Could not find matching constructor in " + targetType + ".", n.position());
            else
                throw new NoMemberException(NoMemberException.METHOD, "Could not find matching method in " + targetType + ".", n.position());
        }

        Collection<PI> maximal = ts.<PD, PI> findMostSpecificProcedures(acceptable, (Matcher<PI>) null, xc);

        if (maximal.size() > 1) {
            StringBuffer sb = new StringBuffer();
            for (Iterator<PI> i = maximal.iterator(); i.hasNext();) {
                PI ma = (PI) i.next();
                if (ma instanceof MemberInstance) {
                    sb.append(((MemberInstance) ma).container());
                    sb.append(".");
                }
                sb.append(ma.signature());
                if (i.hasNext()) {
                    if (maximal.size() == 2) {
                        sb.append(" and ");
                    }
                    else {
                        sb.append(", ");
                    }
                }
            }

            if (n instanceof New || n instanceof ConstructorCall)
                throw new NoMemberException(NoMemberException.CONSTRUCTOR, "Reference to " + targetType + " is ambiguous, multiple " + "constructors match: "
                        + sb.toString(), n.position());
            else
                throw new NoMemberException(NoMemberException.METHOD, "Reference to " + targetType + " is ambiguous, multiple " + "methods match: "
                        + sb.toString(), n.position());
        }

        PI mi;
        mi = (PI) maximal.iterator().next();

        List<Expr> args = newArgs.get(mi.def());
        assert args != null;

        return new Pair<PI, List<Expr>>(mi, args);
    }

    static Pair<ConstructorInstance, List<Expr>> tryImplicitConversions(X10New_c n, ContextVisitor tc, Type targetType, List<Type> typeArgs, List<Type> argTypes)
            throws SemanticException {
        final X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        final Context context = tc.context();
        ClassDef currentClassDef = context.currentClassDef();

        List<ConstructorInstance> methods = ts.findAcceptableConstructors(targetType, new X10TypeSystem_c.DumbConstructorMatcher(targetType, typeArgs, argTypes, context));
        return tryImplicitConversions(n, tc, targetType, methods, new MatcherMaker<ConstructorInstance>() {
            public Matcher<ConstructorInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
                return ts.ConstructorMatcher(ct, typeArgs, argTypes, context);
            }
        });
    }

    
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        final X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

        // ///////////////////////////////////////////////////////////////////
        // Inline the super call here and handle type arguments.
        // ///////////////////////////////////////////////////////////////////

        List<Type> typeArgs = new ArrayList<Type>(this.typeArguments.size());

        for (TypeNode tn : this.typeArguments) {
            typeArgs.add(tn.type());
        }

        List<Type> argTypes = new ArrayList<Type>(this.arguments.size());

        for (Expr e : this.arguments) {
            argTypes.add(e.type());
        }

        if (typeArgs.size() > 0)
            throw new SemanticException("Constructor calls may not take type arguments.", position());

        typeCheckFlags(tc);
        typeCheckNested(tc);

        Type t = tn.type();
        X10ClassType ct = (X10ClassType) X10TypeMixin.baseType(t);

        X10ConstructorInstance ci=null;
        List<Expr> args;

        try {
            if (!ct.flags().isInterface()) {
                Context c = tc.context();
                if (anonType != null) {
                    c = c.pushClass(anonType, anonType.asType());
                }
                ClassDef currentClassDef = c.currentClassDef();
                ci = (X10ConstructorInstance) xts.findConstructor(ct, xts.ConstructorMatcher(ct, 
                		Collections.EMPTY_LIST, argTypes, c));
                if (xts.isStructType(ci.returnType()) && ! isStructConstructorCall) {
                	// Ths is an invocation of the class automatically constructed from a struct decl.
                	ci = ci.toRefCI();
                }
            }
            else {
                ConstructorDef dci = xts.defaultConstructor(this.position(), 
                		Types.<ClassType> ref(ct));
                ci = (X10ConstructorInstance) dci.asInstance();
            }

            args = this.arguments;
        }
        catch (SemanticException e) {
            // Now, try to find the method with implicit conversions, making
            // them explicit.
            try {
                Pair<ConstructorInstance, List<Expr>> p = tryImplicitConversions(this, tc, ct, typeArgs, argTypes);
                ci = (X10ConstructorInstance) p.fst();
                args = p.snd();
            }
            catch (SemanticException e2) {
                throw e;
            }
        }

        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        Type tp = ci.returnType();
      
        if (!ts.isSubtype(tp, t, tc.context())) {
            throw new SemanticException("Constructor return type " + tp + " is not a subtype of " + t + ".", position());
        }

        if (anonType != null) {
            // The type of the new expression is the anonymous type, not the
            // base type.
            ct = (X10ClassType) anonType.asType();
        }

        // Copy the method instance so we can modify it.
      //  tp = ((X10Type) tp).setFlags(X10Flags.ROOTED);
        ci = (X10ConstructorInstance) ci.returnType(tp);
        ci = adjustCI(ci, tc);
        X10New_c result = (X10New_c) this.constructorInstance(ci);
        result = (X10New_c) result.arguments(args);

        result.checkWhereClause(tc);
        result = (X10New_c) result.type(ci.returnType());
        return result;
    }

    private void checkWhereClause(ContextVisitor tc) throws SemanticException {
        X10Context c = (X10Context) tc.context();
        X10ConstructorInstance ci = (X10ConstructorInstance) constructorInstance();
        if (ci != null) {
            XConstraint guard = ci.guard();
            if (guard != null && !guard.consistent()) {
                throw new SemanticException("Constructor guard not satisfied by caller.", position());
            }
        }
    }

    /**
     * Compute the new resulting type for the method call by replacing this and
     * any argument variables that occur in the rettype depclause with new
     * variables whose types are determined by the static type of the receiver
     * and the actual arguments to the call.
     * 
     * Also add the self.home==here clause.
     * 
     * @param tc
     * @return
     * @throws SemanticException
     */
    private X10ConstructorInstance adjustCI(X10ConstructorInstance xci, ContextVisitor tc) throws SemanticException {
        if (xci == null)
            return (X10ConstructorInstance) this.ci;
        Type type = xci.returnType();
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        
        // Add self.home == here to the return type.
        if (! ts.isStructType(type)) {
        	XTerm selfVar = X10TypeMixin.selfVar(type);
        	X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        	X10Context xc = (X10Context) tc.context();
        	XTerm locVar = xts.homeVar(selfVar, xc);
        	type = X10TypeMixin.addBinding(type, locVar, xc.currentPlaceTerm());
        	
        	// Add self != null
        	type = X10TypeMixin.addDisBinding(type, selfVar, XTerms.NULL);
        }
       
        
        if (body != null) {
            // If creating an anonymous class, we need to adjust the return type
            // to be based on anonType rather than on the supertype.
            ClassDef anonTypeDef = anonType();
            Type anonType = anonTypeDef.asType();
            type = X10TypeMixin.xclause(X10TypeMixin.baseType(anonType), X10TypeMixin.xclause(type));
          
        }
        xci = (X10ConstructorInstance) xci.returnType(type);
        return xci;
       // return (X10New_c) this.constructorInstance(xci).type(type);
    }
    
   
   
}
