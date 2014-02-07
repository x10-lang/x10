/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.Id;
import polyglot.ast.Id_c;
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
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Pair;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure_c;
import x10.ast.PropertyDecl;
import x10.ast.TypeParamNode;
import x10.ast.TypeParamNode_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10MethodDecl_c;
import x10.types.ClosureDef;
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
 * WARNING this class modifies type objects (X10ClassDef etc) in place without copying them
 * 
 * Needs to be a ContextVistitor so we can use TypeSubst to change the types (even though full power of TypeSubst is not needed)
 * 
 * @author Dave Cunningham (originally: unknown, probably Igor)
 */
public class TypeParamAlphaRenamer extends ContextVisitor {

	TypeParamAlphaRenamer parent;
	// each name maps to the original and the transformed type (aliases if not being transformed)
	private Map<Name, Pair<ParameterType,ParameterType>> map;
	
	// This guy keeps internal state that is generated at AST leaves and must be visible at other leaves
	// Construct it in the root visitor (parent == null) and alias it in all children, updating the 
	// substitution before each transform.
	TypeParamSubstTransformer tpst;
	
	protected TypeParamAlphaRenamer(Map<Name, Pair<ParameterType,ParameterType>> map, TypeParamAlphaRenamer parent) {
		super(parent.job, parent.ts, parent.nf);
		//this.job = parent.job;
		//this.ts = parent.ts;
		//this.nf = parent.nf;
        this.map = map;
        this.parent = parent;
        this.context = parent.context;
        this.tpst = parent.tpst;
    }

	public TypeParamAlphaRenamer(Job job, TypeSystem ts, NodeFactory nf) {
		super(job,ts,nf);
		//this.job = job;
		//this.ts = ts;
		//this.nf = nf;
        this.map = new HashMap<Name, Pair<ParameterType,ParameterType>>();
        this.parent = null;
        this.context = ts.emptyContext();
        tpst = new TypeParamSubstTransformer(null);
    }

	// checks inner scopes first
	// returns null if the type does not exist, otherwise returns the pair
    private Pair<ParameterType,ParameterType> lookupMap(Name name) {
    	for (TypeParamAlphaRenamer t = this; t!= null ; t = t.parent) {
			if (t.map.containsKey(name)) return t.map.get(name);
    	}
		return null;
    }

    
    protected TypeParamSubst buildSubst() {
    	// squash maps into a single map
    	Map<Name, Pair<ParameterType,ParameterType>> squashed = new HashMap<Name, Pair<ParameterType,ParameterType>>();
    	for (TypeParamAlphaRenamer t = this; t!= null ; t = t.parent) {
    	    for (Entry<Name, Pair<ParameterType,ParameterType>> e : t.map.entrySet()) {
            	// let inner scopes overwrite outer scopes
    	    	if (!squashed.containsKey(e.getKey()))
	    			squashed.put(e.getKey(), e.getValue());
            }
    	}
        	
    	// build a TypeParamSubst out of it
        List<ParameterType> from = new ArrayList<ParameterType>();
        List<ParameterType> to = new ArrayList<ParameterType>();
        for (Entry<Name, Pair<ParameterType,ParameterType>> e : squashed.entrySet()) {
        	ParameterType from_ = e.getValue().fst();
        	ParameterType to_ = e.getValue().snd();
        	if (from_ != to_) {
	        	from.add(from_);
	        	to.add(to_);
        	}
        }
        assert from.size() == to.size() : from.size()+" != "+to.size();
        if (from.size() == 0) return null;
        return new TypeParamSubst(ts, to, from, true);
    }

    
    @Override
    public NodeVisitor enterCall(Node parent, Node n) {
    	List<ParameterType> bound_params = null;
    	boolean static_scope = false;
        if (n instanceof X10ClassDecl_c) {
        	X10ClassDecl_c n2 = (X10ClassDecl_c) n;
        	bound_params = n2.classDef().typeParameters();
        	static_scope = n2.flags().flags().isStatic();
        } else if (n instanceof X10MethodDecl_c) {
        	X10MethodDecl_c n2 = (X10MethodDecl_c) n;
        	bound_params = n2.methodDef().typeParameters();
        	static_scope = n2.flags().flags().isStatic();
        }
        // constructors do not introduce additional type parameters
        
        if (bound_params == null) return this;
        

    	Map<Name, Pair<ParameterType,ParameterType>> new_scope = new HashMap<Name,Pair<ParameterType,ParameterType>>();
    	for (ParameterType p : bound_params) {
    		ParameterType map_to = p;
    		Pair<ParameterType,ParameterType> outer = lookupMap(p.name());
        	if (outer!=null && !static_scope) {
        		// this name clashes, rewrite it
                map_to = new ParameterType(ts, p.position(), p.position().markCompilerGenerated(), Name.makeFresh(p.name()), p.def());
        	}
        	new_scope.put(p.name(), new Pair<ParameterType,ParameterType>(p, map_to));
    	}

    	return new TypeParamAlphaRenamer(new_scope, this);
    }

    
    
    @Override
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
    	TypeParamAlphaRenamer v2 = (TypeParamAlphaRenamer)v;
        TypeParamSubst subst = v2.buildSubst();
        
        if (subst == null) return n;

        // First, apply the subst
        tpst.subst(subst);
        n = tpst.transform(n, old, v2);
        
        // Now, fix the defs which apparently are not changed by the type substitution
        if (n instanceof X10ClassDecl_c) {
        	X10ClassDecl_c n2 = (X10ClassDecl_c) n;
            X10ClassDef def = (X10ClassDef)n2.classDef();
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

			// [DC] these guys were updated already
			List<TypeParamNode> new_type_param_nodes = n2.typeParameters();

			// [DC] whack the class def with new type parameter types
			for (int i = 0; i < new_type_param_nodes.size(); i++) {
				ParameterType new_param = new_type_param_nodes.get(i).type();
                def.replaceTypeParameter(i, new_param, def.variances().get(i));
            }
			
            def.setSubst(subst); // [DC] unsure how well this works
        }
        
        if (n instanceof X10MethodDecl_c) {
        	X10MethodDecl_c n2 = (X10MethodDecl_c) n;
            MethodDef def = n2.methodDef();
            adjustMethodDef(def, subst);

			// [DC] these guys were updated already
			List<TypeParamNode> new_type_param_nodes = n2.typeParameters();

			// [DC] whack the method def with new type parameter types
            List<ParameterType> tps = new ArrayList<ParameterType>();
			for (int i = 0; i < def.typeParameters().size(); i++) {
				ParameterType new_param = new_type_param_nodes.get(i).type();
				tps.add(new_param);
            }
            def.setTypeParameters(tps);
        }

        if (n instanceof X10ConstructorDecl_c) {
        	X10ConstructorDecl_c n2 = (X10ConstructorDecl_c)n;
            X10ConstructorDef def = n2.constructorDef();
            adjustConstructorDef(def, subst);
        }
        
        /*if (n instanceof Closure_c) {
        	Closure_c n2 = (Closure_c)n;
            ClosureDef def = n2.closureDef();
            List<VarInstance<? extends VarDef>> new_env = new ArrayList<VarInstance<? extends VarDef>>();
            for (VarInstance<? extends VarDef> env_vi : def.capturedEnvironment()) {
            	new_env.add(subst.reinstantiate(env_vi));
            }
            def.setCapturedEnvironment(new_env);
        }*/
        
        return n;
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

