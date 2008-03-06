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
import polyglot.ast.Expr;
import polyglot.ast.Field;
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
import polyglot.ext.x10.types.X10NamedType;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.parser.X10Parser;

public class X10Disamb_c extends Disamb_c implements X10Disamb {

	
	public String toString() {
        return "X10Disamb(" + amb.getClass().getName() + ": " + amb + ")";
    }
	
	@Override
	protected Node disambiguateNoPrefix() throws SemanticException {
	    X10Context c = (X10Context) this.c;
	    if (c.inDepType()) {
	        X10NamedType t = c.currentDepType();
	        if (exprOK()) {
	            if (t instanceof ReferenceType) {
	                try {
	                    FieldInstance fi = ts.findField((ReferenceType) t, this.name.id(), c.currentClassScope());
	                    Field f = nf.Field(pos, makeMissingPropertyTarget(fi), this.name);
	                    f = f.fieldInstance(fi);
	                    f = (Field) f.type(fi.type());
	                    return f;
	                }
	                catch (SemanticException e) {
	                }
	            }
	        }
	        if (typeOK()) {
	            if (t instanceof ClassType) {
	                try {
	                    ClassType ct = ts.findMemberClass((ClassType) t, this.name.id());
	                    return nf.CanonicalTypeNode(pos, ct);
	                }
	                catch (SemanticException e) {
	                }
	            }
                }
	    }
	    return super.disambiguateNoPrefix();
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
	
	protected Receiver makeMissingPropertyTarget(FieldInstance fi) throws SemanticException {
        Receiver r;

        if (fi.flags().isStatic()) {
            r = nf.CanonicalTypeNode(pos.startOf(), fi.container());
        }
        else {
            // The field is non-static, so we must prepend with self.
            X10Context xc = (X10Context) c;
           
            Expr e = ((X10NodeFactory) nf).Self(pos); 
            e = e.type(xc.currentDepType());
            r = e;
        }
        		
        return r;
    }
}
