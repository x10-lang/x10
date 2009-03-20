/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.Special_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10ProcedureInstance;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.CodeInstance;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Special_c extends Special_c implements X10Special {

	boolean isSelf=false;
	public X10Special_c(Position pos, Kind kind, TypeNode qualifier) {
		super(pos, kind, qualifier);
		isSelf = kind.equals(SELF);
	}
	
	
	public static X10Special self(Position pos) {
		X10Special_c	self = new X10Special_c(pos, SELF, null);
		self.isSelf=true;
		return self;
	}
	public boolean isSelf() { return isSelf;}
	public boolean equals(Object other) {
		if (! (other instanceof X10Special_c)) return false;
		X10Special_c o = (X10Special_c) other;
		return isSelf == o.isSelf && kind.equals(o.kind) && 
		(qualifier == null ? o.qualifier==null : qualifier.equals(o.qualifier));
	}
	public int hashCode() {
		return kind.hashCode();
	}
	 /** Type check the expression. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        X10Context c = (X10Context) tc.context();

        ClassType t = null;
        
        if (isSelf) {
    		X10NamedType tt = c.currentDepType();
    		if (tt == null) {
    			   throw new SemanticException("self may only be used within a dependent type", 
    					   position());
    		}
        	if (c.inSuperTypeDeclaration()) {
        		tt = c.supertypeDeclarationType();
        	}
        	
        	// The type of self should not include a dep clause; otherwise
        	// self in C(:c) could have type C(:c), causing an infinite regress
        	// later in type checking.
        	tt = (X10NamedType) tt.makeNoClauseVariant();
    		return type(tt);
    	}
        if (qualifier == null) {
            // an unqualified "this" 
            t =  c.currentClass();
        }
        else {
            if (! qualifier.isDisambiguated()) {
                return this;
            }
            
            if (qualifier.type().isClass()) {
                t = qualifier.type().toClass();
                
                if (!c.currentClass().hasEnclosingInstance(t)) {
                    throw new SemanticException("The nested class \"" + 
                                                c.currentClass() + "\" does not have " +
                                                "an enclosing instance of type \"" +
                                                t + "\".", qualifier.position());
                }
            }
            else {
                throw new SemanticException("Invalid qualifier for \"this\" or \"super\".", qualifier.position());
            }
        }
        
        if (t == null || (c.inStaticContext() && ts.equals(t, c.currentClass()))) {
            // trying to access "this" or "super" from a static context.
            throw new SemanticException("Cannot access a non-static " +
                "field or method, or refer to \"this\" or \"super\" " + 
                "from a static context.", this.position());
        }

        X10Special result = this;

        if (kind == THIS) {
        	X10Type tt = (X10Type) t.copy();
        	tt.setSelfVar(C_Special.This);
            result = (X10Special) type(tt);
        }
        else if (kind == SUPER) {
            result = (X10Special) type(t.superType());
        }

        // Fold in the method's where clause.
        CodeInstance ci = c.currentCode();
        if (ci instanceof X10ProcedureInstance) {
            X10ProcedureInstance pi = (X10ProcedureInstance) ci;
            Constraint where = pi.whereClause();
            if (where != null) {
                X10Type newType = (X10Type) result.type().copy();
                Constraint dep = newType.depClause().copy();
                dep.addIn(where);
                newType = newType.makeVariant(dep, newType.typeParameters());
                return result.type(newType);
            }
        }

        return result;
    }

}
