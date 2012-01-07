package x10.cconstraints.test;

import java.util.List;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.TypeSystem;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10FieldDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CTerms;

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
        CConstraint c = new CConstraint();
        XVar a = XTerms.makeUQV("a");
        XVar x = XTerms.makeUQV("x");
        XVar b = XTerms.makeUQV("b");
        XVar af = CTerms.makeField(a,f);
        XVar afg = CTerms.makeField(af,g);
        
        XVar xf = CTerms.makeField(x,f);
        XVar xfg = CTerms.makeField(xf,g);
        
        XVar bg = CTerms.makeField(b,g);
      
        XVar u = XTerms.makeEQV();
        c.addBinding(afg,xfg);
        c.addBinding(xf,b);
        c=c.substitute(u,x);
        List<XTerm> xl = c.extConstraints();
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
