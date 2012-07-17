package x10.types.constraints.xsmt;

import polyglot.ast.Typed;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarDef;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtTerm;
import x10.constraint.xsmt.SmtType;
import x10.constraint.xsmt.XSmtField;
import x10.constraint.xsmt.XSmtTerm;
import x10.constraint.xsmt.XSmtVar;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.constraints.CField;

public class CSmtField extends XSmtField<Def> implements CField, Typed{
	private static final long serialVersionUID = -6499055048080909344L;
	private String string;
    
    public CSmtField(XVar r, MethodDef fi) {this(r, fi, false);}
    public CSmtField(XVar r, MethodDef fi, boolean hidden) {super(r, fi, hidden);}

    public CSmtField(XVar r, FieldDef fi) {this(r, fi, false);}
    public CSmtField(XVar r, FieldDef fi, boolean hidden)  {super(r, fi, hidden);}
   
    private String getString() {
        if (string == null) {
            if (field() instanceof MethodDef) {
                MethodDef fi = (MethodDef)field();
                string = fi.name().toString() + "()";
            } else {
                FieldDef fi = (FieldDef)field();
                string = fi.name().toString();
             }
        }
        return string;
    }
 
    /**
     * Return a new CSmtField the same as the current one except that it has a new receiver.
     * (In particular the new CSmtField has the same Def information as the old CSmtField.)
     */
    @Override
    public CSmtField copyReceiver(XVar newReceiver) {
        return field instanceof MethodDef ? new CSmtField((XSmtVar)newReceiver, (MethodDef) field)
        : new CSmtField((XSmtVar)newReceiver, (FieldDef) field);
    }
    
	@Override
	public CSmtField subst(XTerm x, XVar v) {
		XSmtTerm newReceiver = receiver.subst(x, v);
		if (receiver != newReceiver) {
			if (field instanceof MethodDef)
				return new CSmtField((XSmtVar)newReceiver, (MethodDef)field);
			if (field instanceof FieldDef)
				return new CSmtField((XSmtVar)newReceiver, (FieldDef)field);
			
			throw new UnsupportedOperationException("Field should be either a MethodDef or a FieldDef");
		}
			
		return this; 
	}
    

    /**
     * Return the Def associated with this field.
     * @return
     */
    @Override
    public Def def() {return field;}
    @Override
    public XVar thisVar() {
        if (field instanceof X10FieldDef)
            return ((X10ClassDef) Types.get(((X10FieldDef) field).container()).toClass().def()).thisVar();
        return null;
    }
    /**
     * Return the type of this field.
     * @return
     */
    @Override
    public Type type() {
        if (field instanceof MethodDef) return Types.get(((MethodDef) field).returnType());
        if (field instanceof VarDef)    return Types.get(((VarDef) field).type());
        return null;
    }

    @Override
    public String toString() {return (receiver == null ? "" : receiver.toString() + ".") + getString();}
    
    @Override
    public SmtType getType() {
    	SmtType type = get(0).getType(); 
    	return type.get(type.arity());
    }
    
    @Override
    public SmtTerm get(int i) {
    	if (i == 0) {
    		if (uf == null) 
    			uf = CSmtUtil.makeFunctionSymbol(field);
    		return uf; 
    	}

    	if (i == 1) {
    		// Will the receiver have the appropriate type information?
    		return receiver; 
    	}
    	throw new IllegalArgumentException("SmtTerm has only two children. ");
    }

}
