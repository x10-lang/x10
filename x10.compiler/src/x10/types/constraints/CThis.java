package x10.types.constraints;


import java.util.Collections;
import java.util.List;

import polyglot.ast.Typed;
import x10.constraint.XVar;

/**
 * An optimized representation of this variables.
 * Keeps an int index and a type. Equality involves only
 * int equality.
 * 
 * <p>Code is essentially a copy of CSelf. Do not want to
 * combine, for that will mean extra state needs to be kept
 * with each object.
 * @author vj
 *
 */
public interface CThis extends XVar, Typed {
}
