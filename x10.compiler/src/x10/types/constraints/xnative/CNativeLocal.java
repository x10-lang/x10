/**
 * 
 */
package x10.types.constraints.xnative;

import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeLocal;
import x10.types.X10LocalDef;
import x10.types.constraints.CLocal;
import x10.types.constraints.ConstraintManager;


/**
 * A representation of a local variable, with its associated type information.
 * <p>In essence, a CLocal is a serializable representation of an X10LocalInstance.
 * @author vj
 */

public class CNativeLocal extends XNativeLocal<X10LocalDef> implements CLocal,Typed {
    private static final long serialVersionUID = 127892741748021961L;
    String s; // just for documentation

    public CNativeLocal(X10LocalDef ld) {
        super(ld);
        s=ld.name().toString();
    }
    public CNativeLocal(X10LocalDef ld, String s) {
        super(ld);
        this.s=s;
    }
    @Override
    public X10LocalDef localDef() {return name;}
    /** Return the type of this variable.
     * 
     */
    @Override
    public Type type() {return Types.get(name.type());}
    @Override 
    public String toString() {return s;}
}