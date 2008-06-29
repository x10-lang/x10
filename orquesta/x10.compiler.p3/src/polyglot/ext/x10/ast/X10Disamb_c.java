/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Ambiguous;
import polyglot.ast.Disamb_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.types.PathType;
import polyglot.ext.x10.types.PathType_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XTerm;
import x10.constraint.XVar;

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
	    		// First try local variables.
	    		VarInstance<?> vi = c.findVariableSilent(name.id());
	    		
	    		if (vi != null && vi.def() == c.varWhoseTypeIsBeingElaborated()) {
	                Expr e = ((X10NodeFactory) nf).Self(pos); 
	                e = e.type(t);
	                return e;
	    		}
	    		
	    		if (vi instanceof LocalInstance) {
	    			Node n = disambiguateVarInstance(vi);
	    			if (n != null) return n;
	    		}

	    		// Now try properties.
	    		if (t instanceof StructType) {
	    			try {
	    				FieldInstance fi = ts.findField((StructType) t, this.name.id(), c.currentClassDef());
	    				if (fi instanceof X10FieldInstance) {
	    					X10FieldInstance xfi = (X10FieldInstance) fi;
	    					if (xfi.isProperty()) {
	    						Field f = nf.Field(pos, makeMissingPropertyTarget(fi), this.name);
	    						f = f.fieldInstance(fi);
	    						f = (Field) f.type(fi.type());
	    						return f;
	    					}
	    				}
	    			}
	    			catch (SemanticException e) {
	    			}
	    		}
	    		
	    		if (vi != null) {
	    			Node n = disambiguateVarInstance(vi);
	    			if (n != null) return n;
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

	            if (t instanceof ClassType) {
	        	    try {
	        		    X10TypeSystem xts = (X10TypeSystem) ts;
	        		    PathType pt = xts.findTypeProperty((ClassType) t, this.name.id(), c.currentClassDef());
	        		    return nf.CanonicalTypeNode(pos, PathType_c.pathBase(pt, xts.xtypeTranslator().transThis((ClassType) t), t));
	        	    }
	        	    catch (SemanticException e) {
	        	    }
	            }
	        }
	    }
	    
	    // TODO: typedef members
	    // TODO: check outer members
	    Node result = super.disambiguateNoPrefix();
	    if (result != null)
		    return result;

	    if (typeOK()) {
		    ClassType t = c.currentClass();
		    if (t != null) {
			    try {
				    X10TypeSystem xts = (X10TypeSystem) ts;
				    PathType pt = xts.findTypeProperty((ClassType) t, this.name.id(), c.currentClassDef());
				    Type pt2 = PathType_c.pathBase(pt, xts.xtypeTranslator().transThis((ClassType) t), t);
				    return nf.CanonicalTypeNode(pos, pt2);
			    }
			    catch (SemanticException e) {
			    }
		    }
	    }
	    
	    return null;
	}
	
	@Override
	protected Node disambiguateTypeNodePrefix(TypeNode tn) throws SemanticException {
		// TODO: typedef members
		return super.disambiguateTypeNodePrefix(tn);
	}

	@Override
	protected Node disambiguatePackagePrefix(PackageNode pn) throws SemanticException {
		// TODO: typedef members
		return super.disambiguatePackagePrefix(pn);
	}
	
	@Override
	protected Node disambiguateExprPrefix(Expr e) throws SemanticException {
		// TODO: typedef members

		if (typeOK()) {
			Type t = e.type();
			if (t.isClass()) {
				ClassType ct = t.toClass();
				try {
					X10TypeSystem xts = (X10TypeSystem) ts;
					PathType pt = xts.findTypeProperty(ct, this.name.id(), c.currentClassDef());
					XTerm term = null;
					try {
						term = xts.xtypeTranslator().trans(e);
					}
					catch (SemanticException ex) {
					}
					if (term == null) {
						XConstraint c = new XConstraint_c();
						term = xts.xtypeTranslator().genEQV(c, t, false);
					}
					
					if (term instanceof XVar) {
						XVar v = (XVar) term;
						return nf.CanonicalTypeNode(pos, PathType_c.pathBase(pt, v, e.type()));
					}
				}
				catch (SemanticException ex) {
				}
			}
		}

		return super.disambiguateExprPrefix(e);
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
