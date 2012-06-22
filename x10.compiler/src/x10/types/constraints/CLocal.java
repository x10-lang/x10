/**
 * 
 */
package x10.types.constraints;

import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10LocalDef;


/**
 * A representation of a local variable, with its associated type information.
 * <p>In essence, a CLocal is a serializable representation of an X10LocalInstance.
 * @author vj
 */
public interface CLocal extends XLocal<X10LocalDef>, Typed {
    public X10LocalDef localDef();
}
