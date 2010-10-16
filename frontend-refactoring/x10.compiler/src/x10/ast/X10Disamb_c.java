/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.Collections;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.extension.X10Del;
import x10.types.ClassType;
import x10.types.ClosureDef;
import x10.types.CodeDef;
import x10.types.Context;
import x10.types.FieldDef;
import x10.types.FieldInstance;
import x10.types.LocalInstance;
import x10.types.MemberInstance;
import x10.types.MethodInstance;
import x10.types.Named;
import x10.types.NoClassException;
import x10.types.Package;
import x10.types.QName;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.Types;
import x10.types.VarInstance;
import x10.types.X10ClassType;
import x10.types.Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10NamedType;
import x10.types.X10TypeMixin;
import x10.types.TypeSystem;
import x10.types.checker.Checker;

public class X10Disamb_c extends Disamb_c {

	
    public String toString() {
	return "X10Disamb(" + amb.getClass().getName() + ": " + amb + ")";
    }
	
	@Override
	protected Node disambiguateNoPrefix() throws SemanticException {
	    Context c = (Context) this.c;
	    TypeSystem ts = (TypeSystem) this.ts;
	    
	    if (c.inDepType()) {
	    	X10NamedType t = c.currentDepType();
	    	
	    	if (exprOK()) {
	    		// First try local variables.
	    		VarInstance<?> vi = c.pop().findVariableSilent(name.id());

	    		if (vi != null && vi.def() == c.varWhoseTypeIsBeingElaborated()) {
	    		    Expr e = ((NodeFactory) nf).Self(pos); 
	    		    e = e.type(t);
	    		    return e;
	    		}

	    		if (vi instanceof LocalInstance) {
	    		    Node n = disambiguateVarInstance(vi);
	    		    if (n != null) 
	    		        return n;
	    		}
	    		
	    		// Now try properties.
	    		FieldInstance fi = null;
	    		try {
	    		     fi = ts.findField(t, ts.FieldMatcher(t, this.name.id(), c));
	    		}
	    		catch (SemanticException ex) {
	    		}

	    		if (fi != null && vi instanceof FieldInstance && !c.inStaticContext()) {
	    		    Receiver e = makeMissingFieldTarget((FieldInstance) vi);
	    		    throw new SemanticException("Ambiguous reference to field " + this.name + "; both self." + name + " and " + e + "." + name + " match.", pos);
	    		}

	    		if (fi == null && vi instanceof FieldInstance && c.inStaticContext()) {
	    		    throw new SemanticException("Cannot access a non-static field "+this.name+" from a static context.", pos);
	    		}

	    		if (fi instanceof X10FieldInstance) {
	    		    X10FieldInstance xfi = (X10FieldInstance) fi;
	    		    if (xfi.isProperty()) {
	    		        Field f = nf.Field(pos, makeMissingPropertyTarget(xfi, t), this.name);
	    		        f = f.fieldInstance(xfi);
	    		        Type ftype = Checker.rightType(xfi.rightType(), xfi.x10Def(), f.target(), c);
	    		        f = (Field) f.type(ftype);
	    		        return f;
	    		    }
	    		    else {
	    		        if (vi == null) {
	    		            throw new SemanticException("Field \"" + name + "\" is not a property of " + t + ".  Only properties may appear unqualified or prefixed with self in a dependent clause.");
	    		        }
	    		        else {
	    		            // found it as a field of an enclosing class, not of self.
	    		            // fall thru to lookup
	    		        }
	    		    }
	    		}

	    		if (vi != null) {
	    		    Node n = disambiguateVarInstance(vi);
	    		    if (n != null)
	    		        return n;
	    		}

	    		// Now try 0-ary property methods.
	    		try {
	    		    X10MethodInstance mi = ts.findMethod(t, ts.MethodMatcher(t, this.name.id(), Collections.<Type>emptyList(), c));
	    		    if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
	    		        Call call = nf.Call(pos, makeMissingPropertyTarget(mi, t), this.name);
	    		        call = call.methodInstance(mi);
	    		        Type ftype = Checker.rightType(mi.rightType(), mi.x10Def(), call.target(), c);
	    		        call = (Call) call.type(ftype);
	    		        return call;
	    		    }
	    		}
	    		catch (SemanticException e) {
	    		}
	    	}
	    	
	        if (typeOK()) {
	            try {
	        	Type ct = ts.findMemberType(t, this.name.id(), c);
	        	return makeTypeNode(ct);
	            }
	            catch (SemanticException e) {
	            }
	        }
	    }

	    if (exprOK()) {
	        // First try local variables and fields.
	        VarInstance<?> vi = c.findVariableSilent(name.id());

	        if (vi instanceof FieldInstance) {
	            FieldInstance fi = (FieldInstance) vi;
	            TypeSystem xts = (TypeSystem) v.typeSystem();
	            Context p = c;
	            // FIXME: [IP] should we pop back to the right context before proceeding?
	            //while (p.pop() != null && ((p.currentClass() != null && !xts.typeEquals(p.currentClass(), fi.container(), p)) || p.currentCode() instanceof ClosureDef))
	            //    p = p.pop();
	            if (p.inStaticContext() && !fi.flags().isStatic())
	                throw new SemanticException("Cannot access a non-static field "+this.name+" from a static context.", pos);
	        }

	        if (vi != null) {
	            Node n = disambiguateVarInstance(vi);
	            if (n != null) return n;
	        }
    		
    		// Now try 0-ary property methods.
    		try {
    		    X10MethodInstance mi = (X10MethodInstance) c.findMethod(ts.MethodMatcher(null, name.id(), Collections.<Type>emptyList(), c));
    		    if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
    			Call call = nf.Call(pos, makeMissingMethodTarget(mi), this.name);
    			call = call.methodInstance(mi);
                        Type ftype = Checker.rightType(mi.rightType(), mi.x10Def(), call.target(), c);
                        call = (Call) call.type(ftype);
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
	    }

	    // Must be a package then...
	    if (packageOK()) {
	        try {
	            Package p = ts.packageForName(QName.make(null, name.id()));
	            return nf.PackageNode(pos, Types.ref(p));
	        }
	        catch (SemanticException e) {
	        }
	        Package p = ts.createPackage(QName.make(null, name.id()));
	        return nf.PackageNode(pos, Types.ref(p));
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

	        Context xc = (Context) this.c;

		// If in a class header, don't search the supertypes of this class.
		if (xc.inSuperTypeDeclaration()) {
		    Type tType = t;
		    Type tBase = X10TypeMixin.baseType(tType);
		    if (tBase instanceof X10ClassType) {
			X10ClassType tCt = (X10ClassType) tBase;
			    
			if (tCt.def() == xc.supertypeDeclarationType()) {
			    if (exprOK()) {
				// The only fields in scope here are the ones explicitly declared here.
				for (FieldDef fd : tCt.x10Def().properties()) {
				    if (fd.name().equals(name.id())) {
					FieldInstance fi = fd.asInstance();
					fi = ts.FieldMatcher(tType, name.id(), c).instantiate(fi);
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
		
		if (exprOK()) {
		    try {
			return super.disambiguateExprPrefix(e);
		    }
		    catch (SemanticException ex) {
		    }
		    // Now try 0-ary property methods.
		    try {
			X10MethodInstance mi = (X10MethodInstance) ts.findMethod(e.type(), ts.MethodMatcher(e.type(), name.id(), Collections.<Type>emptyList(), c));
			if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
			    Call call = nf.Call(pos, e, this.name);
			    call = call.methodInstance(mi);
			    Type ftype = Checker.rightType(mi.rightType(), mi.x10Def(), call.target(), c);
			    call = (Call) call.type(ftype);
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

	protected Receiver makeMissingFieldTarget(FieldInstance fi) {
	    return makeMissingFieldTarget(fi, pos, v);
	}

	public static Receiver makeMissingFieldTarget(FieldInstance fi, Position pos, ContextVisitor v) {
	    Receiver r = null;
	    NodeFactory nf = (NodeFactory) v.nodeFactory();
	    TypeSystem ts = (TypeSystem) v.typeSystem();
        Context c = (Context) v.context();
        ClassType cur = c.currentClass();

	    try {
	    Position prefixPos = pos.startOf().markCompilerGenerated();
        if (fi.flags().isStatic()) {
	        r = nf.CanonicalTypeNode(prefixPos, fi.container());
	    } else {

	        // The field is non-static, so we must prepend with
	        // "this", but we need to determine if the "this"
	        // should be qualified.  Get the enclosing class which
	        // brought the field into scope.  This is different
	        // from fi.container().  fi.container() returns a super
	        // type of the class we want.

	        ClassType scope = c.findFieldScope(fi.name());
            if (cur!=null && cur.flags()!=null && cur.flags().isStatic()) { // The class is an inner static class
                scope = cur;
            } else if (c.inSuperTypeDeclaration()) {
	            cur = c.supertypeDeclarationType().asType();
	            scope = cur;
	        }
	        assert scope != null;

	        if (! ts.typeEquals(scope, cur, c)) {
	            r = (Special) nf.This(prefixPos, nf.CanonicalTypeNode(prefixPos, scope));
	        }
	        else {
	            r = (Special) nf.This(prefixPos);
	        }
	        r = (Special) r.del().typeCheck(v);
	    }
	    } catch (SemanticException cause) {
	        Position p = r == null ? pos : r.position();
	        throw new InternalCompilerError("Unexpected exception when typechecking "+r, p, cause);
	    }

	    return r;
	}

	protected Receiver makeMissingMethodTarget(MethodInstance mi) throws SemanticException {
	    Receiver r;

	    Context c = (Context) this.c;
	    ClassType cur  =c.currentClass();
	    if (c.inSuperTypeDeclaration())
	        cur = c.supertypeDeclarationType().asType();

	    if (mi.flags().isStatic()) {
	        r = nf.CanonicalTypeNode(pos.startOf(), mi.container());
	    } else {
	        // The field is non-static, so we must prepend with
	        // "this", but we need to determine if the "this"
	        // should be qualified.  Get the enclosing class which
	        // brought the field into scope.  This is different
	        // from fi.container().  fi.container() returns a super
	        // type of the class we want.
	        ClassType scope = c.findMethodScope(name.id());
	        assert scope != null;

	        if (! ts.typeEquals(scope, cur, c)) {
	            r = (Special) nf.This(pos.startOf(), nf.CanonicalTypeNode(pos.startOf(), scope)).del().typeCheck(v);
	        }
	        else {
	            r = (Special) nf.This(pos.startOf()).del().typeCheck(v);
	        }
	    }

	    return r;
	}

	protected Receiver makeMissingPropertyTarget(MemberInstance<?> fi, Type currentDepType) throws SemanticException {
	    Receiver r;
	    
	    if (fi.flags().isStatic()) {
		r = nf.CanonicalTypeNode(pos.startOf(), fi.container());
	    }
	    else {
		// The field is non-static, so we must prepend with self.
		
		Expr e = ((NodeFactory) nf).Self(pos); 
		e = e.type(currentDepType);
		r = e;
	    }
	    
	    return r;
	}
}
