/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2006 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

/**
 * Utility class which is used to disambiguate ambiguous
 * AST nodes (Expr, Type, Receiver, Qualifier, Prefix).
 */
public class Disamb_c implements Disamb
{
    protected ContextVisitor v;
    protected Position pos;
    protected Node prefix;
    protected Id name;

    protected NodeFactory nf;
    protected TypeSystem ts;
    protected Context c;
    protected Ambiguous amb;

    /**
     * Disambiguate the prefix and name into a unambiguous node.
     * @return An unambiguous AST node, or null if disambiguation
     *         fails.
     */
    public Node disambiguate(Ambiguous amb, ContextVisitor v, Position pos,
            Prefix prefix, String name) throws SemanticException {
        return disambiguate(amb, v, pos, prefix, v.nodeFactory().Id(pos, name));
    }
    
    /**
     * Disambiguate the prefix and name into a unambiguous node.
     * @return An unambiguous AST node, or null if disambiguation
     *         fails.
     */
    public Node disambiguate(Ambiguous amb, ContextVisitor v, Position pos,
            Node prefix, Id name) throws SemanticException {

        this.v = v;
        this.pos = pos;
        this.prefix = prefix;
        this.name = name;
        this.amb = amb;

        nf = v.nodeFactory();
        ts = v.typeSystem();
        c = v.context();

        if (prefix instanceof Ambiguous) {
            throw new SemanticException(
                "Cannot disambiguate node with ambiguous prefix.");
        }

        Node result = null;
        
        if (prefix instanceof PackageNode) {
            PackageNode pn = (PackageNode) prefix;
            result = disambiguatePackagePrefix(pn);
        }
        else if (prefix instanceof TypeNode) {
            TypeNode tn = (TypeNode) prefix;
            result = disambiguateTypeNodePrefix(tn);
        }
        else if (prefix instanceof Expr) {
            Expr e = (Expr) prefix;
            result = disambiguateExprPrefix(e);
        }
        else if (prefix == null) {
            result = disambiguateNoPrefix();
        }

        assert ! (result instanceof Ambiguous);
        
        return result;
    }

    protected Node disambiguatePackagePrefix(PackageNode pn) throws SemanticException {
        Resolver pc = ts.packageContextResolver(pn.package_().get());

        Named n;
        
        try {
            n = pc.find(ts.TypeMatcher(name.id()));
        }
        catch (SemanticException e) {
            n = null;
        }
        
        Qualifier q = null;

        if (n instanceof Qualifier) {
            q = (Qualifier) n;
        }
        else if (n == null) {
	    Package p = ts.createPackage(pn.package_(), name.id());
	    q = p;
	}
	else {
            return null;
        }
        
        if (q.isPackage() && packageOK()) {
            return nf.PackageNode(pos, Types.ref(q.toPackage()));
        }
        else if (q.isType() && typeOK()) {
            return makeTypeNode(q.toType());
        }

        return null;
    }


    protected Node disambiguateTypeNodePrefix(TypeNode tn) 
        throws SemanticException 
    {
        // Try static fields.
        Type t = tn.type();

        if (exprOK()) {
            try {
                FieldInstance fi = ts.findField(t, ts.FieldMatcher(t, name.id(), c));
                return nf.Field(pos, tn, name).fieldInstance(fi);
            } catch (NoMemberException e) {
                if (e.getKind() != NoMemberException.FIELD) {
                    // something went wrong...
                    throw e;
                }
                
                // ignore so we can check if we're a member class.
            }
        }

        // Try member classes.
        if (t.isClass() && typeOK()) {
            Resolver tc = t.toClass().resolver();
            Named n;
            try {
                n = tc.find(ts.MemberTypeMatcher(t, name.id(), c));
            }
            catch (NoClassException e) {
                return null;
            }
            if (n instanceof Type) {
                Type type = (Type) n;
                return makeTypeNode(type);
            }
        }

        return null;
    }

    protected Node disambiguateExprPrefix(Expr e) throws SemanticException {
        // Must be a non-static field.
        if (exprOK()) {
            return nf.Field(pos, e, name);
        }
        return null;
    }

    protected Node disambiguateNoPrefix() throws SemanticException {
        if (exprOK()) {
            // First try local variables and fields.
            VarInstance vi = c.findVariableSilent(name.id());
            
            if (vi != null) {
                Node n = disambiguateVarInstance(vi);
                if (n != null) return n;
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
            } catch (NoClassException e) {
                if (!name.id().toString().equals(e.getClassName())) {
                    // hmm, something else must have gone wrong
                    // rethrow the exception
                    throw e;
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

    protected Node disambiguateVarInstance(VarInstance vi) throws SemanticException {
        if (vi instanceof FieldInstance) {
            FieldInstance fi = (FieldInstance) vi;
            Receiver r = makeMissingFieldTarget(fi);
            return nf.Field(pos, r, name).fieldInstance(fi).targetImplicit(true);
        } else if (vi instanceof LocalInstance) {
            LocalInstance li = (LocalInstance) vi;
            return nf.Local(pos, name).localInstance(li);
        }
        return null;
    }

    protected Receiver makeMissingFieldTarget(FieldInstance fi) throws SemanticException {
	Receiver r;
	
	if (fi.flags().isStatic()) {
	    r = nf.CanonicalTypeNode(pos.startOf(), fi.container());
	} else {
	    // The field is non-static, so we must prepend with
	    // "this", but we need to determine if the "this"
	    // should be qualified.  Get the enclosing class which
	    // brought the field into scope.  This is different
	    // from fi.container().  fi.container() returns a super
	    // type of the class we want.
	    ClassType scope = c.findFieldScope(name.id());
	    assert scope != null;
	    
	    if (! ts.typeEquals(scope, c.currentClass(), c)) {
		r = (Special) nf.This(pos.startOf(), nf.CanonicalTypeNode(pos.startOf(), scope)).del().typeCheck(v);
	    }
	    else {
		r = (Special) nf.This(pos.startOf()).del().typeCheck(v);
	    }
	    
	}
	
	return r;
    }
    
    protected Receiver makeMissingMethodTarget(MethodInstance mi) throws SemanticException {
        Receiver r;

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

	    if (! ts.typeEquals(scope, c.currentClass(), c)) {
		r = (Special) nf.This(pos.startOf(), nf.CanonicalTypeNode(pos.startOf(), scope)).del().typeCheck(v);
	    }
	    else {
		r = (Special) nf.This(pos.startOf()).del().typeCheck(v);
	    }
            
        }

        return r;
    }

    protected boolean typeOK() {
        return ! (amb instanceof Expr) &&
              (amb instanceof TypeNode || amb instanceof QualifierNode ||
               amb instanceof Receiver || amb instanceof Prefix);

    }

    protected boolean packageOK() {
        return ! (amb instanceof Receiver) &&
              (amb instanceof QualifierNode || amb instanceof Prefix);
    }

    protected boolean exprOK() {
        return ! (amb instanceof QualifierNode) &&
               ! (amb instanceof TypeNode) &&
              (amb instanceof Expr || amb instanceof Receiver ||
               amb instanceof Prefix);
    }

    protected Node makeTypeNode(Type t) {
	if (amb instanceof TypeNode) {
	    TypeNode tn = (TypeNode) amb;
	    if (tn.typeRef() instanceof LazyRef) {
		LazyRef<Type> sym = (LazyRef<Type>) tn.typeRef();
		sym.update(t);

		// Reset the resolver goal to one that can run when the ref is deserialized.
		Goal resolver = Globals.Scheduler().LookupGlobalType(sym);
		resolver.update(Goal.Status.SUCCESS);
		sym.setResolver(resolver);

		return nf.CanonicalTypeNode(pos, sym);
	    }
	}

	return nf.CanonicalTypeNode(pos, t);
    }
    
    public String toString() {
        return "Disamb(" + amb.getClass().getName() + ": " + amb + ")";
    }
}
