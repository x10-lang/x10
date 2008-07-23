/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

/**
 * Immutable representation of a local variable access. 
 * Introduced to add X10 specific type checks. A local variable accessed
 * in a deptype must be final.
 * 
 * @author vj
 */
import polyglot.ast.Node;
import polyglot.ast.Id;
import polyglot.ast.Local_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ProcedureInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.main.Report;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Local_c extends Local_c {

	public X10Local_c(Position pos, Id name) {
		super(pos, name);
		
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		
		try {
			Context context = tc.context();
			LocalInstance li = context.findLocal(name.id());

			// if the local is defined in an outer class, then it must be final
			if (!context.isLocal(li.name())) {
				// this local is defined in an outer class
				if (!li.flags().isFinal()) {
					throw new SemanticException("Local variable \"" + li.name() + 
							"\" is accessed from an inner class, closure, async, foreach, or ateach, and must be declared " +
							"final.",
							this.position());                     
				}
			}

			X10Local_c result= (X10Local_c) super.typeCheck(tc);
			
			// Permit occurrences of local variables in the type of the variable.
			X10Context xtc = (X10Context) tc.context();
			VarInstance dli = xtc.varWhoseTypeIsBeingElaborated();
			if (xtc.inDepType()) {
				li = result.localInstance();
				if (! (li.equals(dli)) && ! li.flags().isFinal()) {
					throw new SemanticError("Local variable " + li.name() 
							+ " must be final in a dependent clause.", 
							position());
				}
			}

			// Fold in the method's where clause.
			CodeInstance ci = xtc.currentCode();
			if (ci instanceof X10ProcedureInstance) {
				X10ProcedureInstance pi = (X10ProcedureInstance) ci;
				Constraint c = pi.whereClause();
				if (c != null) {
					X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

					c = c.copy();

					// Substitute self for x (this local) in the where clause.
//					C_Var var = new TypeTranslator(xts).trans(localInstance());
//        			Promise p = c.intern(var);
//        			System.out.println("before = " + c);
//        			c = c.substitute(p.term(), C_Special.Self);
//        			System.out.println("after = " + c);

      			    // Add the where clause into the constraint for this type. 
        			X10Type t = (X10Type) result.type().copy();

        			Constraint dep = t.depClause().copy();
        			dep.selfVar(this);
        			dep.addIn(c);
        			t = t.makeVariant(dep, t.typeParameters());
        			
					return result.type(t);
				}
			}
			
			return result;
		} catch (SemanticException z) {
			if (tc instanceof TypeElaborator && !(z instanceof SemanticError)) {
				// Ignore semantic exceptions that may arise during TypeElaboration. The
				// field being referenced may not exist because of an MDE -- e.g. its type
				// does not yet have its signature resolved. 
				if (Report.should_report("types", 2)) 
					Report.report(2, "X10Local_c: " + this 
							+ " encountered exception " + z + " during " + tc + "; being ignored.");
				return this;
			} else {
				throw z;
			}
		}
	}
	
}
