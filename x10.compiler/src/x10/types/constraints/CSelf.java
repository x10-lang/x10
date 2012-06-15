package x10.types.constraints;


import java.util.Collections;
import java.util.List;

import x10.constraint.XVar;


/**
 * An optimized representation of self variables.
 * Keeps only an int index. Equality involves only
 * int equality.
 * @author vj
 *
 */
public interface CSelf extends XVar {
}
