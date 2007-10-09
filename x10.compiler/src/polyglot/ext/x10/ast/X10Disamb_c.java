/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.Collections;

import polyglot.ast.Ambiguous;
import polyglot.ast.Call;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Prefix;
import polyglot.ast.QualifierNode;
import polyglot.ast.Receiver;
import polyglot.ast.Disamb_c;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.parser.X10Parser;

public class X10Disamb_c extends Disamb_c implements X10Disamb {

	
	public String toString() {
        return "X10Disamb(" + amb.getClass().getName() + ": " + amb + ")";
    }
	
	@Override
	public Node disambiguate(Ambiguous amb, ContextVisitor v, Position pos, Prefix prefix, Id name) throws SemanticException {
		Node n = super.disambiguate(amb, v, pos, prefix, name);
		if (n != null) {
			n = ((X10Del) n.del()).annotations(((X10Del) amb.del()).annotations());
			n = ((X10Del) n.del()).setComment(((X10Del) amb.del()).comment());
		}
		return n;
	}
	
	protected Receiver makeMissingFieldTarget(FieldInstance fi) throws SemanticException {
        Receiver r;

        if (fi.flags().isStatic()) {
            r = nf.CanonicalTypeNode(pos.startOf(), fi.container());
        } else {
            // The field is non-static, so we must prepend with
        	// self or 
            // "this", but we need to determine if the "this"
            // should be qualified.  Get the enclosing class which
            // brought the field into scope.  This is different
            // from fi.container().  fi.container() returns a super
            // type of the class we want.
            ClassType scope = c.findFieldScope(name.id());
            X10Context xc = (X10Context) c;
           
            if (xc.isDepType()) {
            	boolean result = ts.equals(scope, xc.currentDepType());
            	
            	//Report.report(1, "X10Disamb_c: Making missing field target for " + fi + " field scope =|"
            	//		+ scope +"| deptype=|"+ xc.currentDepType() + " " + result);
            	r = result ? ((X10NodeFactory) nf).Self(pos) : 
            		nf.This(pos.startOf(), nf.CanonicalTypeNode(pos.startOf(), scope));
            	
            } else {
            	boolean result = ts.equals(scope, c.currentClass());
            	r = result ? nf.This(pos.startOf())  : nf.This(pos.startOf(), nf.CanonicalTypeNode(pos.startOf(), scope));
            }
        }
      
        		
        return r;
    }
}
