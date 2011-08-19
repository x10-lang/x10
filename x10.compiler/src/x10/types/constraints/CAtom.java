package x10.types.constraints;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.FieldDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import x10.constraint.XFormula;
import x10.constraint.XTerm;

/**
 * An atomic formula built from a property field (this has an associated nullary
 * method) or from a method def.
 * The CAtom knows about the MemberDef it is built form. X10 type information
 * about the CAtom can be obtained from this MemberDef.
 * @author vj
 *
 */
public class CAtom extends XFormula<MemberDef> {
  private static final long serialVersionUID = -1734428949188126121L;
  
  public CAtom(MethodDef op, MethodDef opAsExpr, List<XTerm> args) {
      super(op, opAsExpr, args, true);
  }
  public CAtom(MethodDef op, MethodDef opAsExpr, XTerm... args) {
      super(op, opAsExpr, true, args);
  }
  public CAtom(FieldDef op, FieldDef opAsExpr, List<XTerm> args) {
      super(op, opAsExpr, args, true);
  }
  public CAtom(FieldDef op, FieldDef opAsExpr, XTerm... args) {
      super(op, opAsExpr, true, args);
  }
  
  /**
   * Return the MemberDef that this CAtom is built on.
   * @return
   */
  public MemberDef def() {
      return op;
  }
  
  public MemberDef exprDef() {
      return asExprOp;
  }
}
