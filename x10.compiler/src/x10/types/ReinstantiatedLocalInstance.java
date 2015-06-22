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

package x10.types;

import polyglot.types.Ref;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.constraints.CConstraint;

/**
 * @author vj
 *
 */
public class ReinstantiatedLocalInstance extends X10LocalInstance_c {
	private static final long serialVersionUID = -4455351212229501992L;

	private final TypeParamSubst typeParamSubst;
	private final X10LocalInstance li;

	ReinstantiatedLocalInstance(TypeParamSubst typeParamSubst, TypeSystem ts, Position pos,
			Ref<? extends X10LocalDef> def, X10LocalInstance li) {
		super(ts, pos, def);
		this.typeParamSubst = typeParamSubst;
		this.li = li;
	}

	@Override
	public Type type() {
		if (type == null)
			return this.typeParamSubst.reinstantiate(li.type());
		return type;
	}

}
