/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.extension.X10Ext;
import x10.types.MethodInstance;
import x10.ast.X10CanonicalTypeNode;

/**
 * Utility class which is used to disambiguate ambiguous
 * AST nodes (Expr, Type, Receiver, Qualifier, Prefix).
 */
public abstract class Disamb_c implements Disamb
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
            throw new Errors.CannotDisambiguateNodeWithAmbiguousPrefix(pos);
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

        List<Type> n;

        try {
            n = pc.find(ts.TypeMatcher(name.id()));
        }
        catch (SemanticException e) {
            n = null;
        }

        Qualifier q = null;

        if (n != null) {
            if (n.size() > 1) {
                StringBuffer sb = new StringBuffer(); // FIXME: copied from TypeSystem_c.findTypeDef()
                for (Iterator<Type> i = n.iterator(); i.hasNext();) {
                    Type ma = i.next();
                    sb.append(n.toString());
                    if (i.hasNext()) {
                        if (n.size() == 2) {
                            sb.append(" and ");
                        }
                        else {
                            sb.append(", ");
                        }
                    }
                }
                throw new SemanticException("Reference to " + name.id() + " is ambiguous, multiple type defintions match: " + sb.toString());
            }
            for (Type t : n) {
                q = t;
                break;
            }
        }

        
        if (q == null) {
            Package p = ts.createPackage(pn.package_(), name.id());
            q = p;
        }

        if (q.isPackage() && packageOK()) {
            return nf.PackageNode(pos, Types.ref(q.toPackage()));
        } else if (q.isType() && typeOK()) {
        	Type qt = q.toType();
        	QName askedName = QName.make(pn.package_().get().fullName(), name.id());
            if (!qt.fullName().equals(askedName)) {
                throw new SemanticException("Java interop type "+askedName+" is represented by the X10 type "+q+".");
            }
            return makeTypeNode(qt);
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
                FieldInstance fi = ts.findField(t, t, name.id(), c);
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
            List<Type> tl;
            try {
                tl = tc.find(ts.MemberTypeMatcher(t, name.id(), c));
            }
            catch (NoClassException e) {
                return null;
            }
            for (Type n : tl) {
                if (n.isClass()) {
                    return makeTypeNode(n);
                }
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

    protected abstract Node disambiguateNoPrefix() throws SemanticException;

    protected Node disambiguateVarInstance(VarInstance<?> vi) throws SemanticException {
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

    protected abstract Receiver makeMissingFieldTarget(FieldInstance fi) throws SemanticException;
    
    protected abstract Receiver makeMissingMethodTarget(MethodInstance mi) throws SemanticException;

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
        CanonicalTypeNode res = null;
	    if (amb instanceof TypeNode) {
            TypeNode tn = (TypeNode) amb;
            if (tn.typeRef() instanceof LazyRef<?>) {
                LazyRef<Type> sym = (LazyRef<Type>) tn.typeRef();
                sym.update(t);

                // Reset the resolver goal to one that can run when the ref is deserialized.
                Goal resolver = v.job().extensionInfo().scheduler().LookupGlobalType(sym);
                resolver.update(Goal.Status.SUCCESS);
                sym.setResolver(resolver);

                res = nf.CanonicalTypeNode(pos, sym);
	        }
        }
        if (res==null) res = nf.CanonicalTypeNode(pos, t);
        final Node node = res.ext((X10Ext) amb.ext().copy());
        return node;
    }
    
    public abstract String toString();
}
