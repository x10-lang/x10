/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;


import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.TypeObject_c;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
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
	        X10TypeSystem ts = (X10TypeSystem) this.ts;
	        StructType container = Types.get(container());
	        try {
	            asType = new PathType_c(ts, position, ts.xtypeTranslator().transThis(container), container, this);
	        }
	        catch (SemanticException e) {
	            throw new InternalCompilerError(e);
	        }
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
