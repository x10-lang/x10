/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.lang.reflect.Constructor;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.Special_c;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10ProcedureInstance;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.CodeInstance;
import polyglot.types.Types;
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

        if (isSelf) {
    		X10NamedType tt = c.currentDepType();
    		if (tt == null) {
    			   throw new SemanticException("self may only be used within a dependent type", 
    					   position());
    		}
        	if (c.inSuperTypeDeclaration()) {
        		tt = (X10NamedType) c.supertypeDeclarationType().asType();
        	}
        	
        	// The type of self should not include a dep clause; otherwise
        	// self in C(:c) could have type C(:c), causing an infinite regress
        	// later in type checking.
        	tt = (X10NamedType) X10TypeMixin.makeNoClauseVariant(tt);
    	
        	assert tt != null;
        	return type(tt);
    	}
        
        ClassType t = null;

        if (qualifier == null) {
            // an unqualified "this" 
            t =  c.currentClass();
            
            // Use the constructor return type, not the base type.
            if (c.currentDepType() == null && c.currentCode() instanceof X10ConstructorDef) {
            	X10ConstructorDef cd = (X10ConstructorDef) c.currentCode();
            	ClassType returnType = cd.returnType().get();
            	t = returnType;
            }
        }
        else {
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
        
        if (t == null || (c.inStaticContext() && ts.typeEquals(t, c.currentClass()))) {
            // trying to access "this" or "super" from a static context.
            throw new SemanticException("Cannot access a non-static " +
                "field or method, or refer to \"this\" or \"super\" " + 
                "from a static context.", this.position());
        }

        X10Special result = this;

        if (kind == THIS) {
        	X10Type tt = (X10Type) t.copy();
        	tt = X10TypeMixin.setSelfVar(tt, new C_Special_c(X10Special.THIS, t));
            result = (X10Special) type(tt);
        }
        else if (kind == SUPER) {
            result = (X10Special) type(t.superType());
        }
        
        assert result.type() != null;

        // Fold in the method's where clause.
        CodeDef ci = c.currentCode();
        if (ci instanceof X10ProcedureDef) {
            X10ProcedureDef pi = (X10ProcedureDef) ci;
            Constraint where = Types.get(pi.whereClause());
            if (where != null) {
                X10Type newType = (X10Type) result.type().copy();
                Constraint dep = X10TypeMixin.depClause(newType).copy();
                dep.addIn(where);
                newType = X10TypeMixin.depClauseDeref(newType, dep);
                return result.type(newType);
            }
        }

        return result;
    }

}
