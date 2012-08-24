package x10.types.constraints;


import java.util.Collections;
import java.util.List;

import polyglot.ast.Typed;
import polyglot.types.Type;
import x10.constraint.XType;
import x10.constraint.XVar;

/**
 * An optimized representation of this variables.
 * Keeps an int index and a type. Equality involves only
 * int equality.
 *
 */
public interface CThis extends XVar<Type> {
}
