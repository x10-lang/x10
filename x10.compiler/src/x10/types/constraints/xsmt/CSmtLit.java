package x10.types.constraints.xsmt;

import polyglot.ast.IntLit_c;
import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.constraint.xsmt.SmtConstant;
import x10.constraint.xsmt.SmtType;
import x10.constraint.xsmt.XSmtLit;
import x10.types.constraints.CLit;
import x10.types.constraints.ConstraintManager;


public class CSmtLit extends XSmtLit<Object> implements CLit, Typed {
	private static final long serialVersionUID = 2667496895839955994L;
	protected final Type type;
    public CSmtLit(Object val, Type type) {
        super(val);
        this.type = Types.addSelfBinding(Types.baseType(type), this);
    }

    /**
     * Return the type of the literal.
     */
    @Override
    public Type type() {return type;}

    @Override
    public boolean equals(Object o) {
        if (this == o)           return true;
        if (!(o instanceof CSmtLit))return false;
        CSmtLit other = (CSmtLit) o;
        if (!super.equals(other))return false;
        if (type == null)        return other.type == null;
        TypeSystem ts = type.typeSystem();
        return ts.typeEquals(Types.baseType(type), Types.baseType(other.type), ts.emptyContext());
    }

    @Override 
    public String toString() {
        if (type != null && type.isUnsignedNumeric()) {
            return new IntLit_c(Position.COMPILER_GENERATED, 
            					ConstraintManager.getConstraintSystem().getIntLitKind(type), 
            					((Number) value).longValue()).toString();
        }
        return super.toString();
    }
    
   	@Override
	public SmtType getType() {
		return CSmtUtil.toSmtType(type);
	}


}
