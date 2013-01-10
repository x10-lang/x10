package x10.types.constraints;

import polyglot.types.Type;
import x10.constraint.XExpr;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * A qualified variable is a way of referring to an outer this in a constraint.   
 * Such references e.g. "C.this" are represented with field accesses on this, in a manner similar
 * to how outer this accesses are implemented with additional hidden methods on every class.
 * Note that the representation is wider than "C.this" because any expression can be used,
 * not just 'this'.
 * 
 * 
 *  E.g. class A { class B { def m() { A.this } } is represented with var()==this and qualifier()==A
 *  
 * @author lshadare,dcunnin
 *
 */
public interface CQualifiedVar extends XField<Type, QualifierDef> {

}
