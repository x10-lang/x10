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
import polyglot.ast.Local_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Local_c extends Local_c {

	public X10Local_c(Position pos, String name) {
		super(pos, name);
		
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		
		try {
			X10Local_c result= (X10Local_c) super.typeCheck(tc);
			
			// Permit occurrences of local variables in the type of the variable.
			X10Context xtc = (X10Context) tc.context();
			VarInstance dli = xtc.varWhoseTypeIsBeingElaborated();
			if (xtc.inDepType()) {
				li = result.localInstance();
				if (! (li.equals(dli)) && ! li.flags().isFinal()) {
					throw new SemanticError("Local variable " + li.name() 
							+ " must be final in a depclause.", 
							position());
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
