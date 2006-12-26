/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.parse.Name;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
/**
 * An X10ConstructorDecl differs from a ConstructorDecl in that it has a returnType.
 *
 * @author vj
 */
public class X10ConstructorDecl_c extends ConstructorDecl_c implements X10ConstructorDecl {
   
    protected Expr argWhereClause; // ignored for now.
    protected TypeNode returnType;
    
    public X10ConstructorDecl_c(Position pos, Flags flags, 
            String name, TypeNode returnType, List formals, List throwTypes, Block body) {
        super(pos, flags, name, formals, throwTypes, body);
        this.returnType = returnType;
    }
    public X10ConstructorDecl_c(Position pos, Flags flags, 
            String name, TypeNode returnType, 
            List formals, Expr argWhereClause, 
            List throwTypes, Block body) {
        super(pos, flags,  name, formals, throwTypes, body);
        this.returnType = returnType;
        this.argWhereClause=argWhereClause;
        
    }
    public TypeNode returnType() {
    	return returnType;
    }
    /** Reconstruct the constructor. */
    public X10ConstructorDecl reconstruct(TypeNode returnType) {
    	if ( returnType  != this.returnType) {
    		X10ConstructorDecl_c n = (X10ConstructorDecl_c) copy();
    		n.returnType = returnType;
    		return n;
    	}
    	return this;
    }
    public Context enterChildScope(Node child, Context c) {
    	X10Context cc = (X10Context) super.enterChildScope(child, c);
		
		if (child == this.returnType) {
			
			cc.setVarWhoseTypeIsBeingElaborated(null);
		}
		
		return cc;
	}
    /** Visit the children of the method. */
    public Node visitChildren(NodeVisitor v) {
    	X10ConstructorDecl_c result = (X10ConstructorDecl_c) super.visitChildren(v);
    	TypeNode returnType = (TypeNode) visitChild(result.returnType, v);
    	return result.reconstruct(returnType);
    }

    // Adapt the return type checking code from X10MethodDecl.
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
    	X10ConstructorDecl nn = this;
        X10ConstructorDecl old = nn;
        
        // Step I. Typecheck the formal arguments. 
    	TypeChecker childtc = (TypeChecker) tc.enter(parent, nn);
        nn = (X10ConstructorDecl) nn.formals(nn.visitList(nn.formals(),childtc));
        // Now build the new formal arg list.
        // TODO: Add a marker to the TypeChecker which records
        // whether in fact it changed the type of any formal.
        if (tc.hasErrors()) throw new SemanticException();
        if (nn != old) {
        	List<Formal> formals = nn.formals();
        	List<Type> formalTypes = new ArrayList<Type>(formals.size());
        	
        	Iterator<Formal> it = formals.iterator();
        	while (it.hasNext()) {
        		Formal n =  it.next();
        		X10Type newType = (X10Type) n.type().type();
        		//LocalInstance li = n.localInstance().type(newType);
        		//newFormals.add(n.localInstance(li));
        		formalTypes.add(newType);
        		 // Check that the no type contains a reference to this.
        		Constraint clause = newType.realClause();
                if (clause != null) {
                	if (clause.hasVar(C_Special_c.This)) 
                		throw new SemanticException("The type of an argument to a constructor may not reference this.",
                				n.position());
                }
        	}
        	//nn = nn.formals(newFormals);
        	nn.constructorInstance().setFormalTypes(formalTypes);
        	 // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
        }

        // Step II. Check the return tpe. 
        // Now visit the returntype to ensure that its depclause, if any is processed.
        // Visit the formals so that they get added to the scope .. the return type
        // may reference them.
    	// childtc will have a "wrong" mi pushed in, but that doesnt matter.
    	// we simply need to push in a non-null mi here.
    	TypeChecker childtc1 = (TypeChecker) tc.enter(parent, nn);
    	// Add the formals to the context.
    	List onn = nn.visitList(nn.formals(),childtc1);
    	TypeNode rett = nn.returnType();
    	TypeNode nnt = (TypeNode) nn.visitChild(rett, childtc1);
        nn = nn.reconstruct(nnt);
        if (childtc1.hasErrors()) throw new SemanticException(); 
        X10Type retType = (X10Type) nn.returnType().type();
        if (! retType.isCanonical()) {
            return nn;
        }
       
        
        TypeSystem ts = tc.typeSystem();
        X10Type retTypeBase = retType.makeNoClauseVariant();
        X10ConstructorInstance nnci = (X10ConstructorInstance) nn.constructorInstance();
        X10Type clazz = ((X10Type) nnci.container()).makeNoClauseVariant();
        if (! ts.typeEquals(retTypeBase, clazz)) {
        	throw new SemanticException("The return type of the constructor (" + retTypeBase 
        			+ " must be derived from"
        			+ " the type of the class (" + clazz + ") on which the constructor is defined.",
        			position());
        }
        // Update the constructorInstance with the depclause-enriched returnType.
    
       	nnci.setReturnType(retType);
       	
       	// Step III. Check the body. 
       	// We must do it with the correct mi -- the return type will be
       	// checked by AssignPropertyCall_c statements in the body.
       	
       	TypeChecker childtc2 = (TypeChecker) tc.enter(parent, nn);
       	// Add the formals to the context.
       	nn.visitList(nn.formals(),childtc2);
       	// Now process the body.
        nn = (X10ConstructorDecl) nn.body((Block) nn.visitChild(nn.body(), childtc2));
        if (childtc2.hasErrors()) throw new SemanticException();
         nn = (X10ConstructorDecl) childtc2.leave(parent, old, nn, childtc2);
       
        return nn;
    }
   
}
