package x10.types.constraints;

import java.util.Collections;
import java.util.List;

import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;

import x10.constraint.XLit;


/**
 * An optimized representation of literals.
 * Keeps a value and a type.
 * @author vj
 */
public interface CLit extends XLit,Typed {
}
