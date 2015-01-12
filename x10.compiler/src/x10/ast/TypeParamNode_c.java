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

import java.util.List;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Term_c;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.SemanticException;
import polyglot.types.Types;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import x10.types.ParameterType;

import polyglot.types.Context;
import x10.types.X10TypeEnv_c;
import polyglot.types.TypeSystem;

public class TypeParamNode_c extends Term_c implements TypeParamNode {
	protected Id name;
	protected ParameterType type;
	ParameterType.Variance variance;

	public TypeParamNode_c(Position pos, Id name, ParameterType.Variance variance) {
		super(pos);
		this.name = name;
		this.variance = variance;
	}

	public Id name() {
		return name;
	}

	public TypeParamNode name(Id name) {
		TypeParamNode_c n = (TypeParamNode_c) copy();
		n.name = name;
		return n;
	}

	public ParameterType.Variance variance() {
	    return variance;
	}

	public TypeParamNode variance(ParameterType.Variance variance) {
	    TypeParamNode_c n = (TypeParamNode_c) copy();
	    n.variance = variance;
	    return n;
	}

	public ParameterType type() {
	    return this.type;
	}

	/** Set the type this node encapsulates. */
	public TypeParamNode type(ParameterType type) {
		TypeParamNode_c n = (TypeParamNode_c) copy();
		n.type = type;
		return n;
	}

	public Node buildTypes(TypeBuilder tb) {
		TypeSystem xts = (TypeSystem) tb.typeSystem();
		
	        Def def = tb.def();
	        
//	        if (! (def instanceof ProcedureDef || def instanceof TypeDef)) {
//	            throw new SemanticException("Type parameter cannot occur outside method, constructor, closure, or type definition.", position());
//	        }
	        
	        ParameterType t = new ParameterType(xts, position(), position(), name.id(), Types.ref(def));
	        return type(t);
	}

	public Term firstChild() {
		return null;
	}

	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		return succs;
	}

	public String toString() {
		return name().id().toString();
	}

	public void addDecls(Context c) {
        if (type!=null)
		    c.addNamed(type);
	}

	public Node visitChildren(NodeVisitor v) {
		Id id = (Id) visitChild(this.name, v);
		if (id != this.name) return name(id);
		return this;
	}
	
	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
	    pp.print(this, name, w);
	}
	public List<Type> upperBounds() {
	    assert false : "X10 compiler doesn't support this. see XTENLANG-3086";
		Type type = type();
		TypeSystem ts = (TypeSystem) type.typeSystem();
		Context xc =  ts.emptyContext();
		List<Type> results = new X10TypeEnv_c(xc).upperBounds(type, false);
		return results;
	}
}
