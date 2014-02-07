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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.Def;
import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MemberDef;

import polyglot.types.QName;
import polyglot.types.Ref;

import polyglot.types.ReferenceType_c;
import polyglot.types.Resolver;
import polyglot.types.Name;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;

public abstract class ParametrizedType_c extends ReferenceType_c implements ParametrizedType {
	private static final long serialVersionUID = 7637749680707950061L;

	ContainerType container;
	Flags flags;
	Name name;

	public ParametrizedType_c(TypeSystem ts, Position pos, Position errorPosition) {
		super(ts, pos, errorPosition);
	}
	
	public ParametrizedType container(ContainerType container) {
		ParametrizedType_c t = (ParametrizedType_c) copy();
		t.container = container;
		return t;
	}

	public ContainerType container() {
		if (this.container == null) {
			this.container = Types.get(def().container());
		}
		return this.container;
	}
	
	public Flags flags() {
		if (this.flags == null) { 
			this.flags = def().flags();
		}
		return this.flags;
	}

	public ParametrizedType flags(Flags flags) {
		ParametrizedType_c t = (ParametrizedType_c) copy();
		t.flags = flags;
		return t;
	}
	
	public ParametrizedType name(Name name) {
		ParametrizedType_c t = (ParametrizedType_c) copy();
		t.name = name;
		return t;
	}

	@Override
	public abstract String translate(Resolver c);

	public abstract MemberDef def();
	
	public QName fullName() {
		if (container() != null) {
			return QName.make(container().fullName(), name());
		}
		return QName.make(null, name());
	}
	
	@Override
	public boolean equalsImpl(TypeObject t) {
		if (t instanceof ParametrizedType) {
			ParametrizedType pt = (ParametrizedType) t;
			if (pt.def() != def()) return false;
			if (! pt.typeParameters().equals(typeParameters())) return false;
			if (! pt.formals().equals(formals())) return false;
			if (! pt.formalTypes().equals(formalTypes())) return false;
			return true;
		}
		return false;
	}

}
