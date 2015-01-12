/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Call;
import polyglot.ast.Disamb_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;
import polyglot.types.NoClassException;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.types.CodeDef;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.types.ClosureDef;
import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.X10FieldInstance;

import x10.types.MethodInstance;
import x10.types.matcher.X10FieldMatcher;

import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import x10.types.checker.Checker;

public class X10Disamb_c extends Disamb_c {

	
    public String toString() {
	return "X10Disamb(" + amb.getClass().getName() + ": " + amb + ")";
    }
	
	@Override
	protected Node disambiguateNoPrefix() throws SemanticException {
        if (name.id()== TypeSystem_c.voidName)
			return makeTypeNode(ts.Void());

	    Context c = (Context) this.c;
	    TypeSystem ts = (TypeSystem) this.ts;
	    
	    if (c.inDepType()) {
	    	Type t = c.currentDepType();
	    	
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
	    		     fi = ts.findField(t, t, this.name.id(), c);
	    		}
	    		catch (SemanticException ex) {
	    		}

	    		if (fi != null && vi instanceof FieldInstance && !c.inStaticContext() && !fi.flags().isStatic() && !vi.flags().isStatic()) {
	    		    Receiver e = makeMissingFieldTarget((FieldInstance) vi);
	    		    throw new SemanticException("Ambiguous reference to field " + this.name + "; both self." + name + " and " + e + "." + name + " match.", pos);
	    		}

	    		if (fi == null && vi instanceof FieldInstance && !vi.flags().isStatic() && c.inStaticContext()) {
	    		    throw new Errors.CannotAccessNonStaticFromStaticContext((FieldInstance) vi, pos);
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
	    		    MethodInstance mi = ts.findMethod(t, ts.MethodMatcher(t, this.name.id(), Collections.<Type>emptyList(), c));
	    		    if (mi.flags().isProperty()) {
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
	                throw new Errors.CannotAccessNonStaticFromStaticContext(fi, pos);
	        }

	        if (vi != null) {
	            Node n = disambiguateVarInstance(vi);
	            if (n != null) return n;
	        }
    		
    		// Now try 0-ary property methods.
    		try {
    		    MethodInstance mi =  c.findMethod(ts.MethodMatcher(null, name.id(), Collections.<Type>emptyList(), c));
    		    if (mi.flags().isProperty()) {
    			Call call = nf.Call(pos, makeMissingMethodTarget(mi), this.name);
    			call = call.methodInstance(mi);
                        Type ftype = Checker.rightType(mi.rightType(), mi.x10Def(), call.target(), c);
                        call = (Call) call.type(ftype);
    			return call;
    		    }
    		}
    		catch (SemanticException e) {
    		    int q=1;
    		}
	    }

	    // no variable found. try types.
	    if (typeOK()) {
	        try {
	            List<Type> n = c.find(ts.TypeMatcher(name.id()));
	            if (n.size() > 1) {
	                throw new SemanticException("Ambiguous type "+name.id()+"\nPossible matches: "+n, pos);
	            }
	            for (Type type : n) {
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
		    Type tBase = Types.baseType(tType);
		    if (tBase instanceof X10ClassType) {
			X10ClassType tCt = (X10ClassType) tBase;
			    
			if (tCt.def() == xc.supertypeDeclarationType()) {
			    if (exprOK()) {
				// The only fields in scope here are the ones explicitly declared here.
				for (FieldDef fd : tCt.x10Def().properties()) {
				    if (fd.name().equals(name.id())) {
					FieldInstance fi = fd.asInstance();
					fi = X10FieldMatcher.instantiateAccess((X10FieldInstance)fi,name.id(),tType,false,c);
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
			MethodInstance mi = (MethodInstance) ts.findMethod(e.type(), ts.MethodMatcher(e.type(), name.id(), Collections.<Type>emptyList(), c));
			if (mi.flags().isProperty()) {
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
	    NodeFactory nf = v.nodeFactory();
	    TypeSystem ts = v.typeSystem();
        Context c = v.context();
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
