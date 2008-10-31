/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.Special_c;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XVar;

public class X10Special_c extends Special_c implements X10Special {

	public X10Special_c(Position pos, Kind kind, TypeNode qualifier) {
		super(pos, kind, qualifier);
	}
	
	
	public static X10Special self(Position pos) {
		X10Special_c	self = new X10Special_c(pos, SELF, null);
		return self;
	}

	public boolean isSelf() { return kind == SELF; }

	/** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        X10Context c = (X10Context) tc.context();

        if (isSelf()) {
            Type tt = c.currentDepType();

            if (tt == null) {
        	throw new SemanticException("self may only be used within a dependent type", position());
            }

            // The type of self should not include a dep clause; otherwise
            // self in C{c} could have type C{c}, causing an infinite regress
            // later in type checking.
            tt = X10TypeMixin.baseType(tt);

            assert tt != null;
            return type(tt);
    	}
        
        ClassType t = null;

        if (qualifier == null) {
            // an unqualified "this" 
            t = c.currentClass();
            
            // If in the class header declaration, make this refer to the current class, not the enclosing class (or null).
            if (c.inSuperTypeDeclaration()) {
        	if (kind == SUPER) {
        	    throw new SemanticException("Cannot refer to \"super\" from within a class or interface declaration header.");
        	}
        	t = c.supertypeDeclarationType().asType();
            }
            
            // Use the constructor return type, not the base type.
            if (c.currentDepType() == null && c.currentCode() instanceof X10ConstructorDef) {
            	X10ConstructorDef cd = (X10ConstructorDef) c.currentCode();
            	Type returnType = cd.returnType().get();
            	if (returnType.isClass()) {
            		t = returnType.toClass();
            	}
            	else {
            		throw new SemanticException("Constructor return type is not a class type.", cd.position());
            	}
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
        X10TypeSystem xts = (X10TypeSystem) ts;

        if (kind == THIS) {
            Type tt = t;
            tt = X10TypeMixin.setSelfVar(tt, (XVar) xts.xtypeTranslator().trans(this));
            result = (X10Special) type(tt);
        }
        else if (kind == SUPER) {
            Type tt = t.superClass();
            tt = X10TypeMixin.setSelfVar(tt, (XVar) xts.xtypeTranslator().trans(this));
            result = (X10Special) type(tt);
        }
        
        assert result.type() != null;

        // Fold in the method's guard, if any.
        CodeDef ci = c.currentCode();
        if (ci instanceof X10ProcedureDef) {
            X10ProcedureDef pi = (X10ProcedureDef) ci;
            XConstraint guard = Types.get(pi.guard());
            if (guard != null) {
                Type newType = result.type();
                XConstraint dep = X10TypeMixin.xclause(newType).copy();
                try {
			dep.addIn(guard);
		}
		catch (XFailure e) {
			throw new SemanticException(e.getMessage(), position());
		}
                newType = X10TypeMixin.xclause(newType, dep);
                return result.type(newType);
            }
        }

        return result;
    }

}
