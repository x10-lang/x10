package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.CodeInstance;
import polyglot.types.Def_c;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;

public class TypeDef_c extends Def_c implements TypeDef {
	protected Ref<? extends StructType> container;
	protected Flags flags;
	protected String name;
	protected Ref<? extends Package> package_;
	protected List<Ref<? extends Type>> typeParameters;
	protected List<String> formalNames;
	protected List<Ref<? extends Type>> formalTypes;
	protected Ref<XConstraint> whereClause;
	protected Ref<? extends Type> type;
	protected MacroType asType;
	
	public TypeDef_c(TypeSystem ts, Position pos, Flags flags, String name, Ref<? extends StructType> container, List<Ref<? extends Type>> typeParams,
			List<String> formalNames, List<Ref<? extends Type>> formalTypes, Ref<XConstraint> whereClause, Ref<? extends Type> type) {

		super(ts, pos);
		this.container = container;
		this.name = name;
		this.flags = flags;
		this.typeParameters = TypedList.copyAndCheck(typeParams, Ref.class, true);
		this.formalNames = TypedList.copyAndCheck(formalNames, String.class, true);
		this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
		this.whereClause = whereClause;
		this.type = type;
	}
	
	// BEGIN ANNOTATION MIXIN
	List<Ref<? extends X10ClassType>> annotations;

	public List<Ref<? extends X10ClassType>> defAnnotations() {
		return Collections.unmodifiableList(annotations);
	}

	public void setDefAnnotations(List<Ref<? extends X10ClassType>> annotations) {
		this.annotations = TypedList.<Ref<? extends X10ClassType>> copyAndCheck(annotations, Ref.class, true);
	}

	public List<X10ClassType> annotations() {
		return X10TypeObjectMixin.annotations(this);
	}

	public List<X10ClassType> annotationsMatching(Type t) {
		return X10TypeObjectMixin.annotationsMatching(this, t);
	}

	public List<X10ClassType> annotationsNamed(String fullName) {
		return X10TypeObjectMixin.annotationsNamed(this, fullName);
	}

	// END ANNOTATION MIXIN

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#asType()
	 */
	public MacroType asType() {
		if (asType == null) {
			asType = new MacroType_c((X10TypeSystem) ts, position(), Types.<TypeDef>ref(this));
			asType = (MacroType) asType.container(container.get());
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
	 * @see polyglot.ext.x10.types.TypeDef#container()
	 */
	public Ref<? extends StructType> container() {
		return container;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#typeParameters()
	 */
	public List<Ref<? extends Type>> typeParameters() {
		return Collections.unmodifiableList(typeParameters);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#setTypeParameters(java.util.List)
	 */
	public void setTypeParameters(List<Ref<? extends Type>> typeParameters) {
		this.typeParameters = TypedList.copyAndCheck(typeParameters, Ref.class, true);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#whereClause()
	 */
	public Ref<XConstraint> whereClause() {
		return whereClause;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#setWhereClause(polyglot.types.Ref)
	 */
	public void setWhereClause(Ref<XConstraint> whereClause) {
		this.whereClause = whereClause;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#setContainer(polyglot.types.Ref)
	 */
	public void setContainer(Ref<? extends StructType> container) {
		this.container = container;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#type()
	 */
	public Ref<? extends Type> definedType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#setReturnType(polyglot.types.Ref)
	 */
	public void setType(Ref<? extends Type> type) {
		assert type != null;
		this.type = type;
	}
	
	public List<String> formalNames() {
		return Collections.unmodifiableList(formalNames);
	}
	
	public void setFormalNames(List<String> formalNames) {
		this.formalNames = TypedList.copyAndCheck(formalNames, String.class, true);
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#formalTypes()
	 */
	public List<Ref<? extends Type>> formalTypes() {
		return Collections.unmodifiableList(formalTypes);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.TypeDef#setFormalTypes(java.util.List)
	 */
	public void setFormalTypes(List<Ref<? extends Type>> formalTypes) {
		this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
	}

	public String toString() {
		return "type " + name + typeParameters + "" + formalTypes + " = " + type;
	}

	public String name() {
		return name;
	}
	
	public void setName(String name) {
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
	    return "type " + name + typeParameters + "" + formalTypes;
	}

	public List<Ref<? extends Type>> throwTypes() {
	    return Collections.EMPTY_LIST;
	}

	public CodeInstance<?> asInstance() {
	    return asType();
	}

}
