package x10.cconstraints.test;

import java.util.List;

import polyglot.types.Type;
import x10.constraint.XDef;
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.types.constraints.CConstraint;

public class NestedExTest extends X10TestCase{

    XDef<Type> f,g; 
    public NestedExTest() {
        super("NestedExTest");
        assert ts != null;
        f = makeField("f", ts.Any());
        g = makeField("g", ts.Any());
        
    }
    public void test1() throws Throwable {
        CConstraint c = sys.makeCConstraintNoSelf(ts);
        XUQV<Type> a = sys.makeUQV(ts.Any(), "a");
        XUQV<Type> x = sys.makeUQV(ts.Any(), "x");
        XUQV<Type> b = sys.makeUQV(ts.Any(), "b");
        XField<Type,XDef<Type>> af = sys.makeField(a,f);
        XField<Type,XDef<Type>> afg = sys.makeField(af,g);
        
        XField<Type,XDef<Type>> xf = sys.makeField(x,f);
        XField<Type,XDef<Type>> xfg = sys.makeField(xf,g);
        
        XField<Type,XDef<Type>> bg = sys.makeField(b,g);
      
        XEQV<Type> u = sys.makeEQV(ts.Any());
        c.addEquality(afg,xfg);
        c.addEquality(xf,b);
        c=c.substitute(u,x);
        List<? extends XTerm<Type>> xl = c.terms();
        System.out.print("constraints:");
        for (XTerm<Type> z : xl) {
            System.out.print(" " + z);
        }
        System.out.println();
        System.out.print("(test1: Should print afg=bg) "); 
        print(c);
        assertTrue(c.entailsEquality(afg,bg));
        assertFalse(c.valid());
    }

}
