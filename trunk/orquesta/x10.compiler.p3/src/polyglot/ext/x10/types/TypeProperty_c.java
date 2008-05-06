package polyglot.ext.x10.types;


import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.ClassDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeObject_c;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class TypeProperty_c extends TypeObject_c implements TypeProperty {
	String name;
	Variance variance;
	Ref<? extends X10ClassType> container;

	public TypeProperty_c(X10TypeSystem ts, Position pos,
			Ref<? extends X10ClassType> container, String name, Variance v) {
		super(ts, pos);
		this.container = container;
		this.name = name;
		this.variance = v;
	}
	
	public String toString() {
		return "type " + name;
	}
	
	Type asType = null;
	
	public Type asType() {
		if (asType == null) {
			C_Special_c self = new C_Special_c(X10Special.SELF, container.get());
			asType = new PathType_c(ts, position, self, this);
		}
		return asType;
	}
	
	C_Var asVar = null;
	
	public C_Var asVar() {
		if (asVar == null) {
			C_Special_c self = new C_Special_c(X10Special.SELF, container.get());
			FieldInstance fi = ts.fieldDef(Position.COMPILER_GENERATED, container, Flags.PUBLIC.Final(), Types.ref(ts.Void()), name).asInstance();
			((X10FieldDef) fi.def()).setProperty();
			((X10FieldDef) fi.def()).setNotConstant();
			asVar = new C_Field_c(fi, self);
		}
		return asVar;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVariance(Variance variance) {
		this.variance = variance;
	}

	public Variance variance() {
		return variance;
	}

	public Ref<? extends ReferenceType> container() {
		return container;
	}

	public Flags flags() {
		return Flags.PUBLIC.Final();
	}

	public void setContainer(Ref<? extends ReferenceType> container) {
		this.container = (Ref<? extends X10ClassType>) container;
	}

	public void setFlags(Flags flags) {
		throw new InternalCompilerError("Cannot set flags.");
	}
}
