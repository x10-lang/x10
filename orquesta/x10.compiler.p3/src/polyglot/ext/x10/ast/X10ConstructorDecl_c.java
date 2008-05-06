/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
/**
 * An X10ConstructorDecl differs from a ConstructorDecl in that it has a returnType.
 *
 * @author vj
 */
public class X10ConstructorDecl_c extends ConstructorDecl_c implements X10ConstructorDecl {
   
    protected DepParameterExpr whereClause; // ignored for now.
    protected TypeNode returnType;
    
    public X10ConstructorDecl_c(Position pos, Flags flags, 
            Id name, TypeNode returnType, List formals, List throwTypes, Block body) {
        super(pos, flags, name, formals, throwTypes, body);
        this.returnType = returnType;
    }
    public X10ConstructorDecl_c(Position pos, Flags flags, 
            Id name, TypeNode returnType, 
            List formals, DepParameterExpr whereClause, 
            List throwTypes, Block body) {
        super(pos, flags,  name, formals, throwTypes, body);
        this.returnType = returnType;
        this.whereClause=whereClause;
        
    }
    
    public TypeNode returnType() {
        return returnType;
    }

    public DepParameterExpr whereClause() {
        return whereClause;
    }

    public X10ConstructorDecl whereClause(DepParameterExpr e) {
        X10ConstructorDecl_c n = (X10ConstructorDecl_c) copy();
        n.whereClause = e;
        return n;
    }

    /** Reconstruct the constructor. */
    public X10ConstructorDecl returnType(TypeNode returnType) {
        if (returnType != this.returnType) {
            X10ConstructorDecl_c n = (X10ConstructorDecl_c) copy();
            n.returnType = returnType;
            return n;
        }
        return this;
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10ConstructorDecl_c n = (X10ConstructorDecl_c) super.buildTypesOverride(tb);
        X10ConstructorDef ci = (X10ConstructorDef) n.constructorDef();
        ci.setReturnType((Ref<? extends X10ClassType>) n.returnType().typeRef());
        if (n.whereClause() != null)
            ci.setWhereClause(n.whereClause().constraint());
        return n;
    }

    public Context enterChildScope(Node child, Context c) {
        // We should have entered the constructor scope already.
        assert c.currentCode() == this.constructorDef();

        if (! formals.isEmpty() && child != body) {
            // Push formals so they're in scope in the types of the other formals.
            c = c.pushBlock();
            for (Formal f : formals) {
                f.addDecls(c);
            }
        }

        X10Context cc = (X10Context) super.enterChildScope(child, c);

        if (child == this.returnType) {
            cc.setVarWhoseTypeIsBeingElaborated(null);
        }

        return cc;
    }

    /** Visit the children of the method. */
    public Node visitSignature(NodeVisitor v) {
    	X10ConstructorDecl_c result = (X10ConstructorDecl_c) super.visitSignature(v);
    	TypeNode returnType = (TypeNode) visitChild(result.returnType, v);
    	if (returnType != result.returnType)
    	    result = (X10ConstructorDecl_c) result.returnType(returnType);
    	DepParameterExpr whereClause = (DepParameterExpr) visitChild(result.whereClause, v);
    	if (whereClause != result.whereClause)
    	    result = (X10ConstructorDecl_c) result.whereClause(whereClause);
    	return result;
    }


    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
    	X10ConstructorDecl nn = this;
    	X10ConstructorDecl old = nn;

        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        
        // Step I.a.  Check the formals.
        TypeChecker childtc = (TypeChecker) tc.enter(parent, nn);
        
    	// First, record the final status of each of the formals.
    	List<Formal> processedFormals = nn.visitList(nn.formals(), childtc);
        nn = (X10ConstructorDecl) nn.formals(processedFormals);
        if (tc.hasErrors()) throw new SemanticException();
        
        for (Formal n : processedFormals) {
    		Ref<Type> ref = (Ref<Type>) n.type().typeRef();
    		X10Type newType = (X10Type) ref.get();
    		
    		if (n.localDef().flags().isFinal()) {
    			Constraint c = newType.depClause();
    			if (c != null) {
    				c = c.copy();
    			}
    			c = Constraint_c.addSelfBinding(new C_Local_c(n.localDef().asInstance()), c, xts);
    			newType = X10TypeMixin.makeDepVariant(newType, c);
    		}
    		
    		ref.update(newType);
        }
        
        // Step I.b.  Check the where clause.
        if (nn.whereClause() != null) {
        	DepParameterExpr processedWhere = (DepParameterExpr) nn.visitChild(nn.whereClause(), childtc);
        	nn = (X10ConstructorDecl) nn.whereClause(processedWhere);
        	if (tc.hasErrors()) throw new SemanticException();
        
        	// Now build the new formal arg list.
        	// TODO: Add a marker to the TypeChecker which records
        	// whether in fact it changed the type of any formal.
        	List<Formal> formals = processedFormals;
        	
        	//List newFormals = new ArrayList(formals.size());
        	X10ProcedureDef pi = (X10ProcedureDef) nn.memberDef();
        	Constraint c = pi.whereClause().get();
        	if (c != null) {
        		c = c.copy();

        		for (Formal n : formals) {
        			Ref<Type> ref = (Ref<Type>) n.type().typeRef();
        			X10Type newType = (X10Type) ref.get();

        			// Fold the formal's constraint into the where clause.
        			C_Var var = new TypeTranslator(xts).trans(n.localDef().asInstance());
        			Constraint dep = newType.depClause().copy();
        			Promise p = dep.intern(var);
        			dep = dep.substitute(p.term(), C_Special.Self);
        			c.addIn(dep);

        			ref.update(newType);
        		}
        	}

        	 // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());

        	// Fold this's constraint (the class invariant) into the where clause.
        	{
        		X10Type t = (X10Type) tc.context().currentClass();
        		if (c != null && t.depClause() != null) {
        			Constraint dep = t.depClause().copy();
        			Promise p = dep.intern(C_Special.This);
        			dep = dep.substitute(p.term(), C_Special.Self);
        			c.addIn(dep);
        		}
        	}

        	// Check if the where clause is consistent.
        	if (c != null && ! c.consistent()) {
        		throw new SemanticException("The constructor's dependent clause is inconsistent.",
        				 whereClause != null ? whereClause.position() : position());
        	}
        }

        X10ConstructorDef nnci = (X10ConstructorDef) nn.constructorDef();

        // Step II. Check the return type. 
        // Now visit the returntype to ensure that its depclause, if any is processed.
        // Visit the formals so that they get added to the scope .. the return type
        // may reference them.
    	//TypeChecker tc1 = (TypeChecker) tc.copy();
    	// childtc will have a "wrong" mi pushed in, but that doesnt matter.
    	// we simply need to push in a non-null mi here.
    	TypeChecker childtc1 = (TypeChecker) tc.enter(parent, nn);
    	// Add the formals to the context.
    	nn.visitList(nn.formals(),childtc1);
    	(( X10Context ) childtc1.context()).setVarWhoseTypeIsBeingElaborated(null);
    	final TypeNode r = (TypeNode) nn.visitChild(nn.returnType(), childtc1);
    	if (childtc1.hasErrors()) throw new SemanticException();
        nn = (X10ConstructorDecl) nn.returnType(r);
        ((Ref<Type>) nnci.returnType()).update(r.type());
       // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
        
        X10Type retTypeBase = ((X10Type) r.type()).makeNoClauseVariant();
        X10Type clazz = ((X10Type) nnci.container().get()).makeNoClauseVariant();
        if (! xts.typeEquals(retTypeBase, clazz)) {
        	throw new SemanticException("The return type of the constructor (" + retTypeBase 
        			+ " must be derived from"
        			+ " the type of the class (" + clazz + ") on which the constructor is defined.",
        			position());
        }
    
       	// Step III. Check the body. 
       	// We must do it with the correct mi -- the return type will be
       	// checked by return e; statements in the body.
       	
       	TypeChecker childtc2 = (TypeChecker) tc.enter(parent, nn);
       	// Add the formals to the context.
       	nn.visitList(nn.formals(),childtc2);
       	//Report.report(1, "X10MethodDecl_c: after visiting formals " + childtc2.context());
       	// Now process the body.
        nn = (X10ConstructorDecl) nn.body((Block) nn.visitChild(nn.body(), childtc2));
        if (childtc2.hasErrors()) throw new SemanticException();
        nn = (X10ConstructorDecl) childtc2.leave(parent, old, nn, childtc2);
        
        return nn;
    }
   

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10ConstructorDecl_c n = (X10ConstructorDecl_c) super.typeCheck(tc);

        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10Type retTypeBase = (X10Type) n.returnType().type();
        X10ConstructorDef nnci = (X10ConstructorDef) n.constructorDef();
        X10Type clazz = X10TypeMixin.makeNoClauseVariant((X10Type) nnci.asInstance().container());
        if (! ts.typeEquals(retTypeBase, clazz)) {
            throw new SemanticException("The return type of the constructor (" + retTypeBase 
                                        + " must be derived from"
                                        + " the type of the class (" + clazz + ") on which the constructor is defined.",
                                        n.position());
        }
        return n;
    }
}
