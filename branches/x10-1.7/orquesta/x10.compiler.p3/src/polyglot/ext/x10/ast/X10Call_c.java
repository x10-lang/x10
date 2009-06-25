/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Call_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.ast.Variable;
import polyglot.ext.x10.types.ClosureDef;
import polyglot.ext.x10.types.ClosureInstance;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.FieldInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.NoMemberException;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XLocal;

/**
 * A method call wrapper to rewrite getLocation() calls on primitives
 * and array operator calls. And perform other dep type processing on some selected method calls.
 * @author Igor
 */
public class X10Call_c extends Call_c implements X10Call {
    public X10Call_c(Position pos, Receiver target, Id name,
                     List<TypeNode> typeArguments, List<Expr> arguments) {
        super(pos, target, name, arguments);
        this.typeArguments = new ArrayList<TypeNode>(typeArguments);
    }

    List<TypeNode> typeArguments;
    public List<TypeNode> typeArguments() { return typeArguments; }
    public X10Call typeArguments(List<TypeNode> args) {
	    X10Call_c n = (X10Call_c) copy();
	    n.typeArguments = new ArrayList<TypeNode>(args);
	    return n;
    }
    
   @Override
   public Node visitChildren(NodeVisitor v) {
	   Receiver target = (Receiver) visitChild(this.target, v);
	   Id name = (Id) visitChild(this.name, v);
	   List<TypeNode> typeArguments = visitList(this.typeArguments, v);
	   List<Expr> arguments = visitList(this.arguments, v);
	   X10Call_c n = (X10Call_c) typeArguments(typeArguments);
	   return n.reconstruct(target, name, arguments);
   }
   
   @Override
   public Node disambiguate(ContextVisitor tc) throws SemanticException {
       return this;
   }
   
	protected Node typeCheckNullTarget(ContextVisitor tc, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
	        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	        NodeFactory nf = tc.nodeFactory();
	        Context c = tc.context();

	        // the target is null, and thus implicit
	        // let's find the target, using the context, and
	        // set the target appropriately, and then type check
	        // the result
	        MethodInstance mi = c.findMethod(ts.MethodMatcher(null, name.id(), typeArgs, argTypes));
	        
	        XLocal this_ = ts.xtypeTranslator().transThisWithoutTypeConstraint();
	        
	        Receiver r;
	        if (mi.flags().isStatic()) {
	            Type container = findContainer(ts, mi);            
	            container = X10TypeMixin.setSelfVar(container, this_);
	            r = nf.CanonicalTypeNode(position().startOf(), container).typeRef(Types.ref(container));
	        } else {
	            // The method is non-static, so we must prepend with "this", but we
	            // need to determine if the "this" should be qualified.  Get the
	            // enclosing class which brought the method into scope.  This is
	            // different from mi.container().  mi.container() returns a super type
	            // of the class we want.
	            Type scope = c.findMethodScope(name.id());
	            scope = X10TypeMixin.setSelfVar(scope, this_);

	            if (! ts.typeEquals(scope, c.currentClass())) {
	                r = (Special) nf.This(position().startOf(),
	                            nf.CanonicalTypeNode(position().startOf(), scope)).del().typeCheck(tc);
	            }
	            else {
	                r = (Special) nf.This(position().startOf()).del().typeCheck(tc);
	            }
	        }

	        Call_c call = (Call_c) this.targetImplicit(true).target(r);       
	        call = (Call_c)call.methodInstance(mi).type(mi.returnType());
	        return call;
	    }

    /**
     * Rewrite getLocation() to Here for value types and operator calls for
     * array types, otherwise leave alone.
     */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        X10Context c = (X10Context) tc.context();
        
        {
            // Check if target.name is a field or local of function type; if so, convert to a closure call.
            X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
            X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

            Expr f;
            if (target() != null)
        	f = nf.Field(position(), target(), name());
            else
        	f = nf.AmbExpr(position(), name());

            Expr e = null;

            try {
        	Node n;
        	n = f.del().disambiguate(tc);
        	n = n.del().typeCheck(tc);
        	n = n.del().checkConstants(tc);
        	if (n instanceof Expr && n instanceof Variable) {
        	    e = (Expr) n;
        	    if (! ts.isFunction(e.type())) {
        		e = null;
        	    }
        	}
            }
            catch (SemanticException ex) {
            }

            if (e != null) {
        	ClosureCall cc = nf.ClosureCall(position(), e, typeArguments(), arguments());
        	X10MethodInstance ci = (X10MethodInstance) ts.createMethodInstance(position(), new ErrorRef_c<MethodDef>(ts, position(), "Cannot get MethodDef before type-checking closure call."));
        	cc = cc.closureInstance(ci);
        	Node n = cc;
        	n = n.del().disambiguate(tc);
        	n = n.del().typeCheck(tc);
        	return n;
            }
        }
     
        try {
        	/////////////////////////////////////////////////////////////////////
        	// Inline the super call here and handle type arguments.
        	/////////////////////////////////////////////////////////////////////
        	
        	List<Type> typeArgs = new ArrayList<Type>(this.typeArguments.size());

        	for (TypeNode tn : this.typeArguments) {
        		typeArgs.add(tn.type());
        	}

        	List<Type> argTypes = new ArrayList<Type>(this.arguments.size());

        	for (Expr e : this.arguments) {
        		argTypes.add(e.type());
        	}

        	if (this.target == null) {
        		return this.typeCheckNullTarget(tc, typeArgs, argTypes);
        	}
        	
        	Type targetType = target.type();
        	Name name = this.name.id();
		ClassDef currentClassDef = c.currentClassDef();

		X10Call_c n = this;
		
		{
		    MethodInstance mi;

		    try {
			mi = xts.findMethod(targetType, 
			                    xts.MethodMatcher(targetType, name, typeArgs, argTypes),
			                    currentClassDef);
		    }
		    catch (SemanticException e) {
			// Look for a method that will take a rail of a bunch of args.
			MethodInstance vmi = null;
			X10Call_c newCall = null;

			for (int numVarArgs = 1; numVarArgs < argTypes.size(); numVarArgs++) {
			    List<Type> newArgTypes = new ArrayList<Type>();
			    List<Expr> newArgs = new ArrayList<Expr>();
			    Expr rail = null;
			    Type railBase = null;
			    List<Expr> railArgs = new ArrayList<Expr>();
			    for (int i = 0; i < argTypes.size() - numVarArgs; i++) {
				newArgTypes.add(argTypes.get(i));
				newArgs.add(arguments.get(i));
			    }
			    for (int i = argTypes.size() - numVarArgs; i < argTypes.size(); i++) {
				if (railBase == null)
				    railBase = argTypes.get(i);
				else
				    railBase = xts.leastCommonAncestor(railBase, argTypes.get(i));
				railArgs.add(arguments.get(i));
				
			    }
			    if (railBase != null) {
				Type railType = xts.ValRail();
				railType = X10TypeMixin.instantiate(railType, railBase);
				newArgTypes.add(railType);
				rail = ((X10NodeFactory) tc.nodeFactory()).Tuple(position(), railArgs);
				rail = rail.type(railType);
				newArgs.add(rail);
			    }

			    try {
				vmi = xts.findMethod(targetType, 
				                     xts.MethodMatcher(targetType, name, typeArgs, newArgTypes),
				                     currentClassDef);
				newCall = (X10Call_c) this.arguments(newArgs);
				break;
			    }
			    catch (SemanticException ex) {
				continue;
			    }
			}

			if (vmi != null) {
			    mi = vmi;
			    n = newCall;
			}
			else
			    throw e;
		    }

		    /* This call is in a static context if and only if
		     * the target (possibly implicit) is a type node.
		     */
		    boolean staticContext = (n.target instanceof TypeNode);

		    if (staticContext && !mi.flags().isStatic()) {
			throw new SemanticException("Cannot call non-static method " + name
			                            + " of " + n.target.type() + " in static "
			                            + "context.", this.position());
		    }

		    // If the target is super, but the method is abstract, then complain.
		    if (n.target instanceof Special && 
			    ((Special) n.target).kind() == Special.SUPER &&
			    mi.flags().isAbstract()) {
			throw new SemanticException("Cannot call an abstract method " +
			                            "of the super class", this.position());            
		    }

		    // Copy the method instance so we can modify it.
		    X10Call_c result = (X10Call_c) n.methodInstance(mi).type(mi.returnType());

		    /////////////////////////////////////////////////////////////////////
		    // End inlined super call.
		    /////////////////////////////////////////////////////////////////////

		    // If we found a method, the call must type check, so no need to check
		    // the arguments here.
		    result.checkConsistency(c);
		    //	        	result = result.adjustMI(tc);
		    //	        	result.checkWhereClause(tc);
		    result.checkAnnotations(tc);

		    return result;
		}
        }
        catch (NoMemberException e) {
//            if (e.getKind() != NoMemberException.METHOD || this.target == null)
//                throw e;
//            Type type = target.type();
//            String name = nameString();
//            List arguments = arguments();
//            StructType java_io_PrintStream = (StructType) xts.forName("java.io.PrintStream");
//            // FIXME: [IP] HACK: do not typecheck printf beyond the first argument
//            if (xts.typeEquals(type, java_io_PrintStream)) {
//                if (name.equals("printf") && arguments.size() >= 1 &&
//                        xts.isSubtype(((Expr) arguments.get(0)).type(), xts.String()))
//                {
//                    try {
//                        MethodInstance new_mi = xts.findMethod(java_io_PrintStream,
//                                "printf",
//                                Arrays.asList(new Type[] { xts.String(), xts.arrayOf(xts.Object()) }), tc.context().currentClassDef());
//                        return (X10Call_c)this.methodInstance(new_mi).type(new_mi.returnType());
//                    } catch (NoMemberException f) {
//                        // For Java 1.4, we need to emulate this method
//                        // TODO: generate a call to x10.lang.Runtime.printf(PrintStream, String, Object[]) instead
//                    }
//                }
//            }
            throw e;
        }
    }
    
    private void checkAnnotations(ContextVisitor tc) throws SemanticException {
    	X10Context c = (X10Context) tc.context();
    	X10MethodInstance mi = (X10MethodInstance) methodInstance();
    	if (mi !=null) {
    		X10Flags flags = X10Flags.toX10Flags(mi.flags());
    		if (c.inNonBlockingCode() 
    				&& ! (mi.isJavaMethod() || mi.isSafe() || flags.isNonBlocking()))
    			throw new SemanticException(mi + ": Only nonblocking methods can be called from nonblocking code.", 
    					position());
    		if (c.inSequentialCode() 
    				&& ! (mi.isJavaMethod()|| mi.isSafe() || flags.isSequential()))
    			throw new SemanticException(mi + ": Only sequential methods can be called from sequential code.", 
    					position());
    		if (c.inLocalCode() 
    				&& ! (mi.isJavaMethod() || mi.isSafe() || flags.isLocal()))
    			throw new SemanticException(mi + ": Only local methods can be called from local code.", 
    					position());
    	}
    }
	private Node superTypeCheck(ContextVisitor tc) throws SemanticException {
        return super.typeCheck(tc);
    }
	
	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(targetImplicit ? "" : target.toString() + ".");
	    sb.append(name);
	    if (typeArguments != null && typeArguments.size() > 0) {
		sb.append("[");
		sb.append(CollectionUtil.listToString(typeArguments));
		sb.append("]");
	    }
	    sb.append("(");
	    sb.append(CollectionUtil.listToString(arguments));
	    sb.append(")");
	    return sb.toString();
	}
}

