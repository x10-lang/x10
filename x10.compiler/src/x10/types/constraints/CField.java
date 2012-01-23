/**
 * 
 */
package x10.types.constraints;
import polyglot.ast.Typed;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.Types;
import polyglot.types.VarDef;
import x10.constraint.XField;
import x10.constraint.XVar;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.X10MethodDef;

/**
 * An XField with type information (either a MethodDef or a FieldDef).
 * 
 * @author vj
 */
public class CField extends XField<Def> implements Typed {
    private static final long serialVersionUID = -1015395555006123008L;
    // lazily initialized
    private String string;
    private String getString() {
        if (string == null) {
            if (field instanceof MethodDef) {
                MethodDef fi = (MethodDef)field;
                string = fi.name().toString() + "()";
                // string = Types.get(fi.container()) + "#" + fi.name().toString()+ "()";
            } else {
                FieldDef fi = (FieldDef)field;
                string = fi.name().toString();
                // string = Types.get(fi.container()) + "#" + fi.name().toString();
            }
        }
        return string;
    }

    public CField(XVar r, MethodDef fi)                 {this(r, fi, false);}
    public CField(XVar r, MethodDef fi, boolean hidden) {super(r, fi, hidden);}
    public CField(XVar r, FieldDef fi)                  {this(r, fi, false);}
    public CField(XVar r, FieldDef fi, boolean hidden)  {super(r, fi, hidden);}

    /**
     * Return a new CField the same as the current one except that it has a new receiver.
     * (In particular the new CField has the same Def information as the old CField.)
     */
    @Override
    public CField copyReceiver(XVar newReceiver) {
        return field instanceof MethodDef ? new CField(newReceiver, (MethodDef) field)
        : new CField(newReceiver, (FieldDef) field);
    }

    /**
     * Return the Def associated with this field.
     * @return
     */
    public Def def() {return field;}
    public XVar thisVar() {
        if (field instanceof X10FieldDef)
            return ((X10ClassDef) Types.get(((X10FieldDef) field).container()).toClass().def()).thisVar();
        return null;
    }
    /**
     * Return the type of this field.
     * @return
     */
    public Type type() {
        if (field instanceof MethodDef) return Types.get(((MethodDef) field).returnType());
        if (field instanceof VarDef)    return Types.get(((VarDef) field).type());
        return null;
    }

    @Override
    public String toString() {return (receiver == null ? "" : receiver.toString() + ".") + getString();}
}
