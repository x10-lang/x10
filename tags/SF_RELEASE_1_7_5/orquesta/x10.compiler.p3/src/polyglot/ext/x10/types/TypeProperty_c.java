package polyglot.ext.x10.types;


import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeObject_c;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XSelf;
import x10.constraint.XVar;

public class TypeProperty_c extends TypeObject_c implements TypeProperty {
        Name name;
	Variance variance;
	Ref<? extends X10ClassType> container;

	public TypeProperty_c(X10TypeSystem ts, Position pos,
			Ref<? extends X10ClassType> container, Name name, Variance v) {
		super(ts, pos);
		this.container = container;
		this.name = name;
		this.variance = v;
	}
	
	public String toString() {
		return "type " + name;
	}
	
	PathType asType = null;
	
	public PathType asType() {
		if (asType == null) {
			asType = new PathType_c(ts, position, XSelf.Self, Types.get(container()), this);
		}
		return asType;
	}
	
	XVar asVar = null;
	
	public XVar asVar() {
		if (asVar == null) {
			X10TypeSystem xts = (X10TypeSystem) ts;
			return xts.xtypeTranslator().transPathType(asType());
		}
		return asVar;
	}

	public Name name() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public void setVariance(Variance variance) {
		this.variance = variance;
	}

	public Variance variance() {
		return variance;
	}

	public Ref<? extends StructType> container() {
		return container;
	}

	public Flags flags() {
		return Flags.PUBLIC.Final();
	}

	public void setContainer(Ref<? extends StructType> container) {
		this.container = (Ref<? extends X10ClassType>) container;
	}

	public void setFlags(Flags flags) {
		throw new InternalCompilerError("Cannot set flags.");
	}
}
