package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.Term;
import polyglot.ast.Term_c;
import polyglot.ext.x10.types.ParameterType_c;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.TypeProperty_c;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.types.Named;
import polyglot.types.Qualifier;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;

public class TypePropertyNode_c extends Node_c implements TypePropertyNode {
	protected Id name;
	protected TypeProperty.Variance variance;
	protected TypeProperty prop;

	public TypePropertyNode_c(Position pos, Id name, TypeProperty.Variance variance) {
		super(pos);
		this.name = name;
		this.variance = variance;
	}
	
	public Id name() {
	    return name;
	}
	
	public TypePropertyNode name(Id name) {
	    TypePropertyNode_c n = (TypePropertyNode_c) copy();
	    n.name = name;
	    return n;
	}

	public TypeProperty.Variance variance() {
	    return variance;
	}

	public TypePropertyNode variance(TypeProperty.Variance variance) {
	    TypePropertyNode_c n = (TypePropertyNode_c) copy();
	    n.variance = variance;
	    return n;
	}

	public TypeProperty typeProperty() {
		return prop;
	}

	/** Set the type this node encapsulates. */
	protected TypePropertyNode typeProperty(TypeProperty prop) {
		TypePropertyNode_c n = (TypePropertyNode_c) copy();
		n.prop = prop;
		return n;
	}

	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem xts = (X10TypeSystem) tb.typeSystem();
		X10ClassDef cd = (X10ClassDef) tb.currentClass();
		TypeProperty prop = new TypeProperty_c(xts, position(), Types.ref((X10ClassType) cd.asType()), name.id(), variance);
		cd.addTypeProperty(prop);
		return typeProperty(prop);
	}

	public String toString() {
		if (variance == TypeProperty.Variance.CONTRAVARIANT)
			return "-" + name().id();
		if (variance == TypeProperty.Variance.COVARIANT)
			return "+" + name().id();
		return name().id().toString();
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
}
