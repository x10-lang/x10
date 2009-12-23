/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.CodeInstance;
import polyglot.types.Def_c;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

public class TypeDef_c extends Def_c implements TypeDef {
	protected Ref<? extends StructType> container;
	protected Flags flags;
	protected Name name;
	protected Ref<? extends Package> package_;
	protected List<Ref<? extends Type>> typeParameters;
	protected List<LocalDef> formalNames;
	protected List<Ref<? extends Type>> formalTypes;
	protected Ref<XConstraint> guard;
	protected Ref<TypeConstraint> typeGuard;
	protected Ref<? extends Type> type;
	protected MacroType asType;
	// FIXME: [IP] HACK
	protected polyglot.ast.TypeNode astNode;
	public void setAstNode(polyglot.ast.TypeNode tn) { astNode = tn; }
	public polyglot.ast.TypeNode astNode() { return astNode; }
	
	public TypeDef_c(TypeSystem ts, Position pos, Flags flags, Name name, Ref<? extends StructType> container, List<Ref<? extends Type>> typeParams,
	        XRoot thisVar, List<LocalDef> formalNames, List<Ref<? extends Type>> formalTypes, Ref<XConstraint> guard, Ref<TypeConstraint> typeGuard, Ref<? extends Type> type) {

		super(ts, pos);
		this.container = container;
		this.name = name;
		this.flags = flags;
		this.typeParameters = TypedList.copyAndCheck(typeParams, Ref.class, true);
		this.thisVar = thisVar;
		this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
		this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
		this.guard = guard;
		this.typeGuard = typeGuard;
		this.type = type;
	}
	
	// BEGIN ANNOTATION MIXIN
	List<Ref<? extends Type>> annotations;

	public List<Ref<? extends Type>> defAnnotations() {
		if (annotations == null) return Collections.EMPTY_LIST;
		return Collections.unmodifiableList(annotations);
	}

	public void setDefAnnotations(List<Ref<? extends Type>> annotations) {
		this.annotations = TypedList.<Ref<? extends Type>> copyAndCheck(annotations, Ref.class, true);
	}

	public List<Type> annotations() {
		return X10TypeObjectMixin.annotations(this);
	}

	public List<Type> annotationsMatching(Type t) {
		return X10TypeObjectMixin.annotationsMatching(this, t);
	}

	public List<Type> annotationsNamed(QName fullName) {
		return X10TypeObjectMixin.annotationsNamed(this, fullName);
	}

	// END ANNOTATION MIXIN

	/* (non-Javadoc)
	 * @see x10.types.TypeDef#asType()
	 */
	public MacroType asType() {
		if (asType == null) {
			asType = new MacroType_c((X10TypeSystem) ts, position(), Types.<TypeDef>ref(this));
			asType = (MacroType) asType.container(Types.get(container));
		}
		return asType;
	}

	public Ref<? extends Package> package_() {
		return this.package_;
	}

	public void setPackage(Ref<? extends Package> p) {
		this.package_ = p;
	}

	/* (non-Javadoc)
	 * @see x10.types.TypeDef#container()
	 */
	public Ref<? extends StructType> container() {
		return container;
	}

	/* (non-Javadoc)
	 * @see x10.types.TypeDef#typeParameters()
	 */
	public List<Ref<? extends Type>> typeParameters() {
		return Collections.unmodifiableList(typeParameters);
	}

	/* (non-Javadoc)
	 * @see x10.types.TypeDef#setTypeParameters(java.util.List)
	 */
	public void setTypeParameters(List<Ref<? extends Type>> typeParameters) {
		this.typeParameters = TypedList.copyAndCheck(typeParameters, Ref.class, true);
	}

	public Ref<XConstraint> guard() {
		return guard;
	}

	public void setGuard(Ref<XConstraint> guard) {
		this.guard = guard;
	}

	public Ref<TypeConstraint> typeGuard() {
	    return typeGuard;
	}
	
	public void setTypeGuard(Ref<TypeConstraint> typeGuard) {
	    this.typeGuard = typeGuard;
	}
	
	/* (non-Javadoc)
	 * @see x10.types.TypeDef#setContainer(polyglot.types.Ref)
	 */
	public void setContainer(Ref<? extends StructType> container) {
		this.container = container;
	}

	
	/* (non-Javadoc)
	 * @see x10.types.TypeDef#type()
	 */
	public Ref<? extends Type> definedType() {
		return type;
	}
	public Ref<? extends Type> returnType() {
	    return definedType();
	}

	/* (non-Javadoc)
	 * @see x10.types.TypeDef#setReturnType(polyglot.types.Ref)
	 */
	public void setType(Ref<? extends Type> type) {
		assert type != null;
		this.type = type;
	}

	XRoot thisVar;
	public XRoot thisVar() {
	    return this.thisVar;
	}

	public void setThisVar(XRoot thisVar) {
	    this.thisVar = thisVar;
	}

	public List<LocalDef> formalNames() {
		return Collections.unmodifiableList(formalNames);
	}
	
	public void setFormalNames(List<LocalDef> formalNames) {
		this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
	}
	
	/* (non-Javadoc)
	 * @see x10.types.TypeDef#formalTypes()
	 */
	public List<Ref<? extends Type>> formalTypes() {
		return Collections.unmodifiableList(formalTypes);
	}

	/* (non-Javadoc)
	 * @see x10.types.TypeDef#setFormalTypes(java.util.List)
	 */
	public void setFormalTypes(List<Ref<? extends Type>> formalTypes) {
		this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
	}

	public String toString() {
            String s = "type " + X10Flags.toX10Flags(flags()).prettyPrint() + (container() == null ? "" : container() + ".") + signature() + (guard() != null ? guard() : "") + " = " + definedType();
            return s;
	}

	public Name name() {
		return name;
	}
	
	public void setName(Name name) {
		this.name = name;
	}
	
	public Flags flags() {
		return flags;
	}

	public void setFlags(Flags flags) {
		this.flags = flags;
	}

	public String designator() {
	    return "type";
	}

	public void setThrowTypes(List<Ref<? extends Type>> l) {
	    // TODO Auto-generated method stub
	    
	}

	public String signature() {
	        return name + (typeParameters.isEmpty() ? "" : typeParameters.toString()) + (
	                formalTypes.isEmpty() ? "" : "(" + CollectionUtil.listToString(formalTypes) + ")");
	}

	public List<Ref<? extends Type>> throwTypes() {
	    return Collections.EMPTY_LIST;
	}

	public CodeInstance<?> asInstance() {
	    return asType();
	}

}
