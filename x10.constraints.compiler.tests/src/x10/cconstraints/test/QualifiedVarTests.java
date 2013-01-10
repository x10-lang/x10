package x10.cconstraints.test;

import polyglot.types.Type;
import x10.constraint.XTerm;
import x10.types.X10FieldDef;
import x10.types.constraints.CConstraint;

public class QualifiedVarTests extends X10TestCase {
    X10FieldDef rank; 
    public QualifiedVarTests() {
        super("QualifiedVarTests");
        rank = makeField("rank", ts.Int()); 
    }
    public void test1() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts.Int(), ts);
        
        XTerm<Type> qv = sys.makeQualifiedVar(ts.Int(), sys.makeThis(ts.Int()));
        c.addEquality(c.self(), qv);
        System.out.print("(test1: Should print self==a.home) "); 
        print(c);
        assertTrue(c.entailsEquality(sys.makeField(c.self(),rank) , sys.makeField(qv, rank)));
    }

}
