package x10.cconstraints.test;

import x10.constraint.XVar;
import x10.types.X10FieldDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;

public class QualifiedVarTests extends X10TestCase {
    X10FieldDef rank; 
    public QualifiedVarTests() {
        super("QualifiedVarTests");
        rank = makeField("rank", ts.Int()); 
    }
    public void test1() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
    
        XVar qv = ConstraintManager.getConstraintSystem().makeQualifiedThis(ts.Int(), ts.Int());
        c.addBinding(c.self(), qv);
        System.out.print("(test1: Should print self==a.home) "); 
        print(c);
        assertTrue(c.entails(ConstraintManager.getConstraintSystem().makeField(c.self(),rank) , ConstraintManager.getConstraintSystem().makeField(qv, rank)));
    }

}
