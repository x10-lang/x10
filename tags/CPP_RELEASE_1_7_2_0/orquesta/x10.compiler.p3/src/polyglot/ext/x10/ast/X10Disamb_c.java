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
import polyglot.ast.Disamb_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.types.PathType;
import polyglot.ext.x10.types.PathType_c;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XTerm;
import x10.constraint.XVar;

public class X10Disamb_c extends Disamb_c {

	
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
	    		try {
	    		    FieldInstance fi = ts.findField(t, ts.FieldMatcher(t, this.name.id()), c.currentClassDef());
	    		    if (fi instanceof X10FieldInstance) {
	    			X10FieldInstance xfi = (X10FieldInstance) fi;
	    			if (xfi.isProperty()) {
	    			    Field f = nf.Field(pos, makeMissingPropertyTarget(fi, t), this.name);
	    			    f = f.fieldInstance(fi);
	    			    f = (Field) f.type(fi.type());
	    			    return f;
	    			}
	    		    }
	    		}
	    		catch (SemanticException e) {
	    		}

	    		if (vi != null) {
	    		    Node n = disambiguateVarInstance(vi);
	    		    if (n != null) return n;
	    		}
	    		
	    		// Now try 0-ary property methods.
	    		try {
	    		    MethodInstance mi = ts.findMethod(t, ts.MethodMatcher(t, this.name.id(), Collections.EMPTY_LIST), c.currentClassDef());
	    		    if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
	    			Call call = nf.Call(pos, makeMissingPropertyTarget(mi, t), this.name);
	    			call = call.methodInstance(mi);
	    			call = (Call) call.type(mi.returnType());
	    			return call;
	    		    }
	    		}
	    		catch (SemanticException e) {
	    		}
	    	}
	    	
	        if (typeOK()) {
	            try {
	        	Type ct = ts.findMemberType(t, this.name.id());
	        	return makeTypeNode(ct);
	            }
	            catch (SemanticException e) {
	            }
	            
//	            try {
//	        	X10TypeSystem xts = (X10TypeSystem) ts;
//	        	Type pt = xts.findTypeProperty(t, this.name.id(), c.currentClassDef());
//	        	return nf.CanonicalTypeNode(pos, pt);
//	            }
//	            catch (SemanticException e) {
//	            }
//
//	            try {
//	        	X10TypeSystem xts = (X10TypeSystem) ts;
//	        	Type mt = xts.findTypeDef(t, this.name.id(), Collections.EMPTY_LIST, Collections.EMPTY_LIST, c.currentClassDef());
//	        	return nf.CanonicalTypeNode(pos, mt);
//	            }
//	            catch (SemanticException e) {
//	            }

	        }
	    }

	    if (exprOK()) {
		// First try local variables and fields.
		VarInstance vi = c.findVariableSilent(name.id());

		if (vi != null) {
		    Node n = disambiguateVarInstance(vi);
		    if (n != null) return n;
		}
		
    		
    		// Now try 0-ary property methods.
    		try {
    		    MethodInstance mi = c.findMethod(ts.MethodMatcher(null, name.id(), Collections.EMPTY_LIST));
    		    if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
    			Call call = nf.Call(pos, makeMissingMethodTarget(mi), this.name);
    			call = call.methodInstance(mi);
    			call = (Call) call.type(mi.returnType());
    			return call;
    		    }
    		}
    		catch (SemanticException e) {
    		}
	    }

	    // no variable found. try types.
	    if (typeOK()) {
		try {
		    Named n = c.find(ts.TypeMatcher(name.id()));
		    if (n instanceof Type) {
			Type type = (Type) n;
			return makeTypeNode(type);
		    }
		} catch (NoClassException e1) {
		    if (!name.id().toString().equals(e1.getClassName())) {
			// hmm, something else must have gone wrong
			// rethrow the exception
			throw e1;
		    }

		    // couldn't find a type named name. 
		    // It must be a package--ignore the exception.
		}

		ClassType t = c.currentClass();
		if (t != null) {
		    try {
			X10TypeSystem xts = (X10TypeSystem) ts;
			PathType pt = xts.findTypeProperty((ClassType) t, this.name.id(), c.currentClassDef());
			Type pt2;
			if (c.inSuperTypeDeclaration())
			    pt2 = PathType_c.pathBase(pt, xts.xtypeTranslator().transThisWithoutTypeConstraint(), t);
			else
			    pt2 = PathType_c.pathBase(pt, xts.xtypeTranslator().transThis((ClassType) t), t);
			

	        	return makeTypeNode(pt2);
		    }
		    catch (SemanticException e) {
		    }
		}
	    }

	    // Must be a package then...
	    if (packageOK()) {
	        return nf.PackageNode(pos, Types.ref(ts.packageForName(QName.make(null, name.id()))));
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
	    
		Type t = e.type();

	        X10Context xc = (X10Context) this.c;
		X10TypeSystem xts = (X10TypeSystem) ts;

		// If in a class header, don't search the supertypes of this class.
		if (xc.inSuperTypeDeclaration()) {
		    Type tType = t;
		    Type tBase = X10TypeMixin.baseType(tType);
		    if (tBase instanceof X10ClassType) {
			X10ClassType tCt = (X10ClassType) tBase;
			    
			if (tCt.def() == xc.supertypeDeclarationType()) {
			    if (typeOK()) {
				for (TypeProperty p : tCt.x10Def().typeProperties()) {
				    if (p.name().equals(name.id())) {
					PathType pt = p.asType();
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
						Type pt2 = PathType_c.pathBase(pt, v, e.type());
				        	return makeTypeNode(pt2);
					}
				    }
				}
				
				// Nothing to see here.
				return null;
			    }
			    
			    if (exprOK()) {
				// The only fields in scope here are the ones explicitly declared here.
				for (FieldDef fd : tCt.x10Def().properties()) {
				    if (fd.name().equals(name.id())) {
					FieldInstance fi = fd.asInstance();
					fi = ts.FieldMatcher(tType, name.id()).instantiate(fi);
					if (fi != null) {
					    // Found!
					    X10Field_c result = (X10Field_c) nf.Field(pos, e, name);
					    result = (X10Field_c) result.fieldInstance(fi).type(fi.type());
					    return result;
					}
				    }
				}
				
				return null;
			    }
			}
		    }
		}


		if (typeOK()) {
			if (t.isClass()) {
				ClassType ct = t.toClass();
				try {
					PathType pt = xts.findTypeProperty(ct, this.name.id(), xc.currentClassDef());
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
						Type pt2 = PathType_c.pathBase(pt, v, e.type());
				        	return makeTypeNode(pt2);
					}
				}
				catch (SemanticException ex) {
				}
			}
		}
		
		if (exprOK()) {
		    try {
			return super.disambiguateExprPrefix(e);
		    }
		    catch (SemanticException ex) {
		    }
		    // Now try 0-ary property methods.
		    try {
			MethodInstance mi = ts.findMethod(e.type(), ts.MethodMatcher(e.type(), name.id(), Collections.EMPTY_LIST), xc.currentClassDef());
			if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
			    Call call = nf.Call(pos, e, this.name);
			    call = call.methodInstance(mi);
			    call = (Call) call.type(mi.returnType());
			    return call;
			}
		    }
		    catch (SemanticException ex) {
		    }
		}
		
		return null;
	}

	@Override
	public Node disambiguate(Ambiguous amb, ContextVisitor v, Position pos, Node prefix, Id name) throws SemanticException {
		Node n = super.disambiguate(amb, v, pos, prefix, name);
		if (n != null) {
			n = ((X10Del) n.del()).annotations(((X10Del) amb.del()).annotations());
			n = ((X10Del) n.del()).setComment(((X10Del) amb.del()).comment());
		}
		return n;
	}
	
	protected Receiver makeMissingPropertyTarget(MemberInstance<?> fi, Type currentDepType) throws SemanticException {
	    Receiver r;
	    
	    if (fi.flags().isStatic()) {
		r = nf.CanonicalTypeNode(pos.startOf(), fi.container());
	    }
	    else {
		// The field is non-static, so we must prepend with self.
		X10Context xc = (X10Context) c;
		
		Expr e = ((X10NodeFactory) nf).Self(pos); 
		e = e.type(currentDepType);
		r = e;
	    }
	    
	    return r;
	}
}
