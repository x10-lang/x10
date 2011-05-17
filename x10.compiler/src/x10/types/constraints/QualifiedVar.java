package x10.types.constraints;

import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarDef;
import x10.constraint.XField;
import x10.constraint.XVar;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;



public class QualifiedVar extends XField<Type> {
    // lazily initialized
    private String string;
    private String getString() {
        if (string == null) {
            string = field + "." + receiver;
        }
        return string;
    }
    
  
    
    public QualifiedVar(Type fi, XVar r) {
        super(r, fi, false);
    }
   
    @Override
    public QualifiedVar copyReceiver(XVar newReceiver) {
        if (newReceiver == receiver)
            return this;
        return new QualifiedVar(field, newReceiver);
    }
    /**
     * Return the qualifier associated with this field.
     * @return
     */
    public Type qualifier() {
        return field;
    }
  /*  public XVar thisVar() {
        if (field instanceof X10FieldDef) {
            return ((X10ClassDef) Types.get(((X10FieldDef) field).container()).toClass().def()).thisVar();
        }
        return null;
    }
  */
  
    @Override
    public String toString() {
        return getString();
    }
    
}
