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

package x10.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.FieldDef;
import polyglot.types.InitializerDef;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.PropertyDecl;
import x10.ast.TypeParamNode;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ConstructorDef;
import x10.types.X10LocalDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.util.CollectionFactory;

/**
 * This visitor alpha-renames type parameters that have the same name as another
 * type parameter in outer scope.
 * WARNING: this modifies the actual type objects in-place, rather than creating copies.
 */
public class TypeParamAlphaRenamer extends NodeTransformingVisitor {

    private static NodeTransformer NONE = new NodeTransformer() { };

    private TypeParamAlphaRenamer parent = null;
    private Map<Name, ParameterType> types;
    private Map<ParameterType, ParameterType> typeMap;

    protected TypeParamAlphaRenamer(Job job, TypeSystem ts, NodeFactory nf, NodeTransformer xform, TypeParamAlphaRenamer parent) {
        super(job, ts, nf, xform);
        this.parent = parent;
        this.types = CollectionFactory.<Name, ParameterType>newHashMap(3);
        this.typeMap = CollectionFactory.<ParameterType, ParameterType>newHashMap(10);
    }

    public TypeParamAlphaRenamer(Job job, TypeSystem ts, NodeFactory nf) {
        this(job, ts, nf, NONE, null);
    }

    protected void addType(ParameterType t) {
        types.put(t.name(), t);
    }

    protected boolean hasType(ParameterType t) {
        if (types.containsKey(t.name())) {
            return true;
        }
        if (parent != null) {
            return parent.hasType(t);
        }
        return false;
    }

    protected void mapType(ParameterType f, ParameterType t) {
        typeMap.put(f, t);
    }

    protected ParameterType getType(ParameterType f) {
        return typeMap.get(f);
    }
    
    protected TypeParamSubst buildSubst() {
        List<ParameterType> from = new ArrayList<ParameterType>();
        List<ParameterType> to = new ArrayList<ParameterType>();
        for (TypeParamAlphaRenamer parent = this; parent != null; parent = parent.parent) {
            parent.gatherTypeMappings(from, to);
        }
        return new TypeParamSubst(ts, to, from, true);
    }

    private void gatherTypeMappings(List<ParameterType> from, List<ParameterType> to) {
        for (Entry<ParameterType, ParameterType> e : typeMap.entrySet()) {
            from.add(e.getKey());
            to.add(e.getValue());
        }
    }

    private static class DelegatingTransformer extends NodeTransformer {
        public NodeTransformer delegate;
        public DelegatingTransformer(NodeTransformer xform) {
            this.delegate = xform;
        }
        @Override
        public Node transform(Node n, Node old, ContextVisitor v) {
            return delegate.transform(n, old, v);
        }
    }

    private class TypeParamRenameTransformer extends TypeParamSubstTransformer {
        public TypeParamRenameTransformer() {
            super(null);
        }
        @Override
        public TypeParamSubst subst() {
            return buildSubst();
        }
    }
    
    @Override
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        if (n instanceof ClassDecl) {
            DelegatingTransformer xform = new DelegatingTransformer(this.xform);
            TypeParamAlphaRenamer tpar = new TypeParamAlphaRenamer(job, ts, nf, xform, this);
            TypeParamRenameTransformer tprt = tpar.new TypeParamRenameTransformer();
            xform.delegate = tprt;
            return tpar.context(this.context());
        }
        if (n instanceof X10MethodDecl) {
            DelegatingTransformer xform = new DelegatingTransformer(this.xform);
            TypeParamAlphaRenamer pvisit = this;
//            if  {((X10MethodDecl) n).flags().flags().isStatic()) // TODO
//                pvisit = null;
//            }
            TypeParamAlphaRenamer tpar = new TypeParamAlphaRenamer(job, ts, nf, xform, pvisit);
            TypeParamRenameTransformer tprt = tpar.new TypeParamRenameTransformer();
            xform.delegate = tprt;
            return tpar.context(this.context());
        }
        return super.enterCall(parent, n);
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof TypeParamNode) {
            if (context.currentCode() instanceof MemberDef && ((MemberDef) context.currentCode()).flags().isStatic()) {
                return n;
            }
            TypeParamNode tpn = (TypeParamNode) n;
            ParameterType pt = tpn.type();
            if (hasType(pt)) {
                ParameterType npt = new ParameterType(ts, pt.position(), pt.position().markCompilerGenerated(), Name.makeFresh(pt.name()), pt.def());
                mapType(pt, npt);
                return tpn.type(npt).name(tpn.name().id(npt.name()));
            } else {
                addType(pt);
                return tpn;
            }
        }
        if (n instanceof ClassDecl) {
            X10ClassDef def = ((ClassDecl) n).classDef();
            TypeParamAlphaRenamer tpar = (TypeParamAlphaRenamer) v;
            TypeParamSubst subst = tpar.buildSubst();
            adjustClassDef(def, subst);
            List<ParameterType> tps = def.typeParameters();
            List<TypeParamNode> tpns = ((ClassDecl) n).typeParameters();
            boolean changed = false;
            for (int i = 0; i < tps.size(); i++) {
                ParameterType p = tps.get(i);
                ParameterType.Variance z = def.variances().get(i);
                TypeParamNode tpn = tpns.get(i);
                ParameterType np = tpar.getType(p);
                if (np != null) {
                    assert (tpn.type().typeEquals(np, context));
                    def.replaceTypeParameter(i, np, z);
                    changed = true;
                }
            }
            if (changed) {
                def.setSubst(subst);
            }
            return n;
        }
        if (n instanceof X10MethodDecl && !((X10MethodDecl) n).flags().flags().isStatic()) {
            MethodDef def = ((X10MethodDecl) n).methodDef();
            TypeParamAlphaRenamer tpar = (TypeParamAlphaRenamer) v;
            TypeParamSubst subst = tpar.buildSubst();
            adjustMethodDef(def, subst);
            List<ParameterType> tps = new ArrayList<ParameterType>();
            List<TypeParamNode> tpns = ((X10MethodDecl) n).typeParameters();
            boolean changed = false;
            for (int i = 0; i < def.typeParameters().size(); i++) {
                ParameterType p = def.typeParameters().get(i);
                TypeParamNode tpn = tpns.get(i);
                ParameterType np = tpar.getType(p);
                if (np != null) {
                    assert (tpn.type().typeEquals(np, context));
                    tps.add(np);
                    changed = true;
                } else {
                    tps.add(p);
                }
            }
            if (changed) {
                def.setTypeParameters(tps);
            }
            return n;
        }
        //[DC] based on the MethodDecl part
        if (n instanceof X10ConstructorDecl) {
            X10ConstructorDef def = ((X10ConstructorDecl) n).constructorDef();
            TypeParamAlphaRenamer tpar = (TypeParamAlphaRenamer) v;
            TypeParamSubst subst = tpar.buildSubst();
            adjustConstructorDef(def, subst);
            /* [DC] don't think that constructors can have type params of their own
            List<ParameterType> tps = new ArrayList<ParameterType>();
            List<TypeParamNode> tpns = ((X10ConstructorDecl) n).typeParameters();
            boolean changed = false;
            for (int i = 0; i < def.typeParameters().size(); i++) {
                ParameterType p = def.typeParameters().get(i);
                TypeParamNode tpn = tpns.get(i);
                ParameterType np = tpar.getType(p);
                if (np != null) {
                    assert (tpn.type().typeEquals(np, context));
                    tps.add(np);
                    changed = true;
                } else {
                    tps.add(p);
                }
            }
            if (changed) {
                def.setTypeParameters(tps);
            }
            */
            return n;
        }        
        return super.leaveCall(parent, old, n, v);
    }

    private static void adjustClassDef(X10ClassDef def, TypeParamSubst subst) {
        for (FieldDef fd : def.fields()) {
            adjustFieldDef(fd, subst);
        }
        for (MethodDef md : def.methods()) {
            adjustMethodDef(md, subst);
        }
        for (ConstructorDef cd : def.constructors()) {
            adjustConstructorDef(cd, subst);
        }
        def.superType(subst.reinstantiate(def.superType()));
        def.setInterfaces(subst.reinstantiate(def.interfaces()));
    }

    private static void adjustFieldDef(FieldDef fd, TypeParamSubst subst) {
        Type t = Types.get(fd.type());
        ((Ref<Type>) fd.type()).update(subst.reinstantiate(t));
        InitializerDef id = fd.initializer();
        adjustMemberDef(id, subst);
    }

    private static void adjustConstructorDef(ConstructorDef cd, TypeParamSubst subst) {
        adjustMemberDef(cd, subst);
        adjustProcedureDef(cd, subst);
    }

    private static void adjustMethodDef(MethodDef md, TypeParamSubst subst) {
        adjustMemberDef(md, subst);
        adjustProcedureDef(md, subst);
    }

    private static void adjustProcedureDef(ProcedureDef pd, TypeParamSubst subst) {
        Type r = Types.get(pd.returnType());
        ((Ref<Type>) pd.returnType()).update(subst.reinstantiate(r));
        List<Ref<? extends Type>> ft = pd.formalTypes();
        List<LocalDef> fn = pd.formalNames();
        for (Ref<? extends Type> f : ft) {
            ((Ref<Type>) f).update(subst.reinstantiate(Types.get(f)));
        }
        for (LocalDef f : fn) {
            ((Ref<Type>) f.type()).update(subst.reinstantiate(Types.get(f.type())));
        }
        Type o = Types.get(pd.offerType());
        if (o != null) {
            ((Ref<Type>) pd.offerType()).update(subst.reinstantiate(o));
        }
        CConstraint g = Types.get(pd.guard());
        if (g != null) {
            pd.guard().update(subst.reinstantiate(g));
        }
        TypeConstraint q = Types.get(pd.typeGuard());
        if (q != null) {
            pd.typeGuard().update(subst.reinstantiate(q));
        }
    }

    private static void adjustMemberDef(MemberDef md, TypeParamSubst subst) {
        if (md == null) return;
        ContainerType c = Types.get(md.container());
        ((Ref<ContainerType>) md.container()).update(subst.reinstantiate(c));
    }

}

