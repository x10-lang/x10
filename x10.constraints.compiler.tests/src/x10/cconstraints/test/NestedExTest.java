package x10.cconstraints.test;

import java.util.List;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.TypeSystem;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10FieldDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;

public class NestedExTest extends X10TestCase{

    ExtensionInfo ext;
    
    Compiler compiler;
    TypeSystem ts;  
    X10FieldDef f,g; 
    public NestedExTest() {
        super("NestedExTest");
        
        f = makeField("f");
        g = makeField("g");
        
    }
    public void test1() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar a = ConstraintManager.getConstraintSystem().makeUQV("a");
        XVar x = ConstraintManager.getConstraintSystem().makeUQV("x");
        XVar b = ConstraintManager.getConstraintSystem().makeUQV("b");
        XVar af = ConstraintManager.getConstraintSystem().makeField(a,f);
        XVar afg = ConstraintManager.getConstraintSystem().makeField(af,g);
        
        XVar xf = ConstraintManager.getConstraintSystem().makeField(x,f);
        XVar xfg = ConstraintManager.getConstraintSystem().makeField(xf,g);
        
        XVar bg = ConstraintManager.getConstraintSystem().makeField(b,g);
      
        XVar u = ConstraintManager.getConstraintSystem().makeEQV();
        c.addBinding(afg,xfg);
        c.addBinding(xf,b);
        c=c.substitute(u,x);
        List<? extends XTerm> xl = c.extConstraints();
        System.out.print("constraints:");
        for (XTerm z : xl) {
            System.out.print(" " + z);
        }
        System.out.println();
        System.out.print("(test1: Should print afg=bg) "); 
        print(c);
        assertTrue(c.entails(afg,bg));
        assertFalse(c.valid());
    }

}
