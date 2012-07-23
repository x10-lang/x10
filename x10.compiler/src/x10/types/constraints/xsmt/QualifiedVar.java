package x10.types.constraints.xsmt;

import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarDef;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xsmt.XSmtField;
import x10.constraint.xsmt.XSmtTerm;
import x10.constraint.xsmt.XSmtVar;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.ConstraintManager;

public class QualifiedVar extends XSmtField<Type> implements XVar, CQualifiedVar {
    private static final long serialVersionUID = -407228981450822754L;
    // lazily initialized
    private String string;
    private String getString() {
        if (string == null) string = field + "." + receiver;
        return string;
    }
    public QualifiedVar(Type fi, XVar<Type> r) {super(r, fi, false);}
    @Override
    public QualifiedVar copyReceiver(XVar<Type> newReceiver) {
        if (newReceiver == receiver) return this;
        return new QualifiedVar(field, newReceiver);
    }
    /**
     * Return the type of this term. The type of a qualified term
     * is the type carried by the qualifier. i.e. the type of A.this
     * is A.
     * @return
     */
    @Override
    public Type type() {return field;}
    @Override
    public XVar<Type> var() {return receiver;}

    @Override 
    public XSmtTerm subst(XTerm<Type> y, XVar<Type> x) {
        return equals(x) ? (XSmtTerm) y : receiver.equals(x) 
            ? copyReceiver((XVar) y) : super.subst(y, x);
    }

    @Override
    public boolean equals(Object x) {
        if (! (x instanceof QualifiedVar)) return false;
        QualifiedVar o = (QualifiedVar) x;
        return receiver.equals(o.receiver) && field == o.field;
    }
    @Override public String toString() {return getString();}
}
