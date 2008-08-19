/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;

/**
 * An allocation wrapper to rewrite array pointwiseOp construction.
 * @author Igor
 */
public class X10New_c extends New_c implements X10New {
	public X10New_c(Position pos, Expr qualifier, TypeNode tn, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
		super(pos, qualifier, tn, arguments, body);
		this.typeArguments = TypedList.copyAndCheck(typeArguments, TypeNode.class, true);
	}

	@Override
	public Node visitChildren(NodeVisitor v) {
		// TODO Auto-generated method stub
		Expr qualifier = (Expr) visitChild(this.qualifier, v);
		TypeNode tn = (TypeNode) visitChild(this.tn, v);
		List<TypeNode> typeArguments = visitList(this.typeArguments, v);
		List<Expr> arguments = visitList(this.arguments, v);
		ClassBody body = (ClassBody) visitChild(this.body, v);
		X10New_c n = (X10New_c) typeArguments(typeArguments);
		return n.reconstruct(qualifier, tn, arguments, body);
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
	    public List<TypeNode> typeArguments() { return typeArguments; }
	    public X10New typeArguments(List<TypeNode> args) {
		    X10New_c n = (X10New_c) copy();
		    n.typeArguments = new ArrayList<TypeNode>(args);
		    return n;
	    }
	    
	    @Override
	    protected New_c typeCheckHeader(TypeChecker childtc) throws SemanticException {
		X10New_c n = (X10New_c) super.typeCheckHeader(childtc);
		typeArguments = visitList(typeArguments, childtc);
		return (X10New_c) n.typeArguments(typeArguments);
	    }
	    
	/**
	 * Rewrite pointwiseOp construction to use Operator.Pointwise, otherwise
	 * leave alone.
	 */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		
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
		
		typeCheckFlags(tc);
		typeCheckNested(tc);
		
		ClassType ct = tn.type().toClass();
		X10ConstructorInstance ci;
		
		if (! ct.flags().isInterface()) {
		    Context c = tc.context();
		    if (anonType != null) {
		        c = c.pushClass(anonType, anonType.asType());
		    }
		    ClassDef currentClassDef = c.currentClassDef();
		    ci = (X10ConstructorInstance) xts.findConstructor(ct, xts.ConstructorMatcher(ct, typeArgs, argTypes), currentClassDef);
		}
		else {
		    ConstructorDef dci = xts.defaultConstructor(this.position(), Types.<ClassType>ref(ct));
		    ci = (X10ConstructorInstance) dci.asInstance();
		}
		
		if (anonType != null) {
		    // The type of the new expression is the anonymous type, not the base type.
		    ct = anonType.asType();
		}
		
        	// Copy the method instance so we can modify it.
        	X10New_c result = (X10New_c) this.constructorInstance(ci);

        	/////////////////////////////////////////////////////////////////////
        	// End inlined super call.
        	/////////////////////////////////////////////////////////////////////

        	result.checkWhereClause(tc);
		result = result.adjustCI(tc);
		return result.type(ci.returnType());
	}

	    private void checkWhereClause(ContextVisitor tc) throws SemanticException {
		    	X10Context c = (X10Context) tc.context();
		    	X10ConstructorInstance ci = (X10ConstructorInstance) constructorInstance();
		    	if (ci !=null) {
		    		XConstraint guard = ci.guard();
		    		if (guard != null && ! guard.consistent()) {
		    			throw new SemanticException("Constructor guard not satisfied by caller.", position());
		    		}
		    	}
		    }

	 /**
     * Compute the new resulting type for the method call by replacing this and 
     * any argument variables that occur in the rettype depclause with new
     * variables whose types are determined by the static type of the receiver
     * and the actual arguments to the call.
     * @param tc
     * @return
     * @throws SemanticException
     */
    private X10New_c adjustCI(ContextVisitor tc) throws SemanticException {
    	if (ci == null) return this;
    	X10ConstructorInstance xci = (X10ConstructorInstance) ci;
    	Type type = xci.returnType();
    	
    	if (body != null) {
    		// If creating an anonymous class, we need to adjust the return type
    		// to be based on anonType rather than on the supertype.
    		ClassDef anonTypeDef = anonType();
    		Type anonType = anonTypeDef.asType();
    		type = X10TypeMixin.xclause(anonType, X10TypeMixin.xclause(type));
    		xci = (X10ConstructorInstance) xci.returnType(type);
    	}
    	
    	return (X10New_c) this.constructorInstance(xci).type(type);
    }
}

