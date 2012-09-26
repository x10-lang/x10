package x10.cconstraints.test;

import java.util.List;

import polyglot.types.ContainerType;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.util.Position;

import x10.constraint.XTerm;

import x10.constraint.XVar;
import x10.constraint.xnative.XPromise;
import x10.constraint.xnative.XNativeVar;
import x10.types.ThisDef;
import x10.types.X10FieldDef;
import x10.types.X10FieldDef_c;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CNativeConstraint;
import x10.types.constraints.ConstraintManager;

import x10c.ExtensionInfo;
import junit.framework.TestCase;

public class BindingTest extends X10TestCase {
    public BindingTest() {
        super("BindingTest");
    }
    XTerm zero = ConstraintManager.getConstraintSystem().makeLit(new Integer(0));
    XTerm one = ConstraintManager.getConstraintSystem().makeLit(new Integer(1));
    XTerm two = ConstraintManager.getConstraintSystem().makeLit(new Integer(2));
    XTerm NULL = ConstraintManager.getConstraintSystem().makeLit(null);

    XVar e0 = ConstraintManager.getConstraintSystem().makeEQV();

    X10FieldDef home = makeField("home"); 
    X10FieldDef rank = makeField("rank"); 
    X10FieldDef size = makeField("size"); 
    X10FieldDef region= makeField("region"); 
    X10FieldDef N= makeField("N"); 
    X10FieldDef af= makeField("af"); 
    X10FieldDef here= makeField("here"); 


    XVar e0home = ConstraintManager.getConstraintSystem().makeField(e0, home);
    XVar a = ConstraintManager.getConstraintSystem().makeUQV("a");
    XVar u = ConstraintManager.getConstraintSystem().makeUQV("u");
    XVar v = ConstraintManager.getConstraintSystem().makeUQV("v");
    XVar s = ConstraintManager.getConstraintSystem().makeUQV("s");

    /**
     * self==e0.home, e0==a |- self==a.home
     * @throws Throwable
     */
    public void test1() throws Throwable {
        CNativeConstraint c = new CNativeConstraint();
        c.addSelfBinding(e0home);
        c.addBinding(e0, a);
        XPromise xp  = ((XNativeVar)c.self()).nfp(c);
        System.out.print("(test1: Should print self==a.home) "); print(c);
        assertTrue(c.entails(c.self(), ConstraintManager.getConstraintSystem().makeField(a,home)));
    }

    /**
     * self.rank==1, self==a | a.rank==1
     * @throws Throwable
     */
    public void test2() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        c.addBinding(ConstraintManager.getConstraintSystem().makeField(c.self(), rank), one);
        c.addBinding(c.self(), a);
        assertTrue(c.entails(ConstraintManager.getConstraintSystem().makeField(a, rank), one));
    }
    /**
     * a=u, v=u | a=v
     * @throws Throwable
     */
    public void test3() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        c.addBinding(a,u);
        c.addBinding(v, u);

        assertTrue(c.entails(a, v));
    }
    /**
     * exists u(a=u, v=u) | a=v
     * @throws Throwable
     */
    public void test4() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        c.addBinding(a,u);
        c.addBinding(v,u);
        c=c.substitute(e0,u);
        System.out.print("(test4: Should print a=v) "); print(c);
        assertTrue(c.entails(a, v));
    }
    /**
     * exists u(u=a, u=v) | a=v
     * @throws Throwable
     */
    public void test5() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        c.addBinding(u,a);
        c.addBinding(u,v);
        c=c.substitute(e0,u);
        System.out.print("(test5: Should print a=v) "); print(c);
        assertTrue(c.entails(a, v));
    }

    /**
     * u^(a.rank=u.rank, v.rank=u.rank) | a.rank=v.rank
     * @throws Throwable
     */
    public void test6() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XTerm ar = ConstraintManager.getConstraintSystem().makeField(a, rank);
        XTerm ur = ConstraintManager.getConstraintSystem().makeField(u, rank);
        XTerm vr = ConstraintManager.getConstraintSystem().makeField(v, rank);

        c.addBinding(ar,ur);
        c.addBinding(vr,ur);
        c=c.substitute(e0,u);
        System.out.print("(test6: Should print a.rank==v.rank)"); print(c);
        assertTrue(c.entails(ar, vr));
    }

    /**
     * exists u(u.rank=a.rank, u.rank=v.rank) | a.rank=v.rank
     * @throws Throwable
     */
    public void test7() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XTerm ar = ConstraintManager.getConstraintSystem().makeField(a, rank);
        XTerm ur = ConstraintManager.getConstraintSystem().makeField(u, rank);
        XTerm vr = ConstraintManager.getConstraintSystem().makeField(v, rank);

        c.addBinding(ur,ar);
        c.addBinding(ur,vr);
        c=c.substitute(e0,u);
        System.out.print("(test7: Should print a.rank==v.rank) "); print(c);
        assertTrue(c.entails(ar, vr));
    }

    /**
     * a^(s.region=a.region, a.region.rank=u.region.rank,v.rank=u.region.rank)|-s.region.rank=u.region.rank,v.rank=u.egion.rank
     * @throws Throwable
     */
    public void test8() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar aRegion = ConstraintManager.getConstraintSystem().makeField(a, region);
        XVar aRegionRank = ConstraintManager.getConstraintSystem().makeField(aRegion, rank);
        XVar uRegion = ConstraintManager.getConstraintSystem().makeField(u, region);
        XVar uRegionRank = ConstraintManager.getConstraintSystem().makeField(uRegion, rank);

        XVar vRank = ConstraintManager.getConstraintSystem().makeField(v, rank);
        XVar sRegion = ConstraintManager.getConstraintSystem().makeField(s, region);
        XVar sRegionRank = ConstraintManager.getConstraintSystem().makeField(sRegion, rank);
        c.addBinding(sRegion, aRegion);
        c.addBinding(aRegionRank,uRegionRank);
        c.addBinding(vRank,uRegionRank);
        c=c.project(a);
        System.out.print("(test8: Should print s.region.rank==u.region.rank, v.rank==u.region.rank) "); 
        print(c);
        assertTrue(c.entails(sRegionRank, uRegionRank)&&c.entails(vRank, uRegionRank));
    }

    /**
     * a.rank=1, b.size=2, a=b | a.rank=1, b.rank=1, a.size=2, b.size=2
     * @throws Throwable
     */
    public void test9() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar aRank = ConstraintManager.getConstraintSystem().makeField(a, rank);
        XVar uRank = ConstraintManager.getConstraintSystem().makeField(u, rank);
        XVar uSize = ConstraintManager.getConstraintSystem().makeField(u, size);
        XVar aSize = ConstraintManager.getConstraintSystem().makeField(a, size);

        c.addBinding(aRank, one);
        c.addBinding(uSize,two);
        c.addBinding(a,u);
        System.out.print("(test9: Should print a=u,u.size==2,u.rank==1) "); print(c);
        assertTrue(c.entails(aRank, one)
                   && c.entails(aSize,two)
                   && c.entails(uSize,two)
                   && c.entails(uRank,one));
    }

    /**
     * a.region.rank=1, u.size=2, a=u | a.region.rank=1, u.region.rank=1, a.size=2, u.size=2
     * @throws Throwable
     */
    public void test10() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar aRegion = ConstraintManager.getConstraintSystem().makeField(a, region);
        XVar aRegionRank = ConstraintManager.getConstraintSystem().makeField(aRegion, rank);
        XVar uRegion = ConstraintManager.getConstraintSystem().makeField(u, region);
        XVar uRegionRank = ConstraintManager.getConstraintSystem().makeField(uRegion, rank);
        XVar uSize = ConstraintManager.getConstraintSystem().makeField(u, size);
        XVar aSize = ConstraintManager.getConstraintSystem().makeField(a, size);

        c.addBinding(aRegionRank, one);
        c.addBinding(uSize,two);
        c.addBinding(a,u);
        System.out.print("(test10: Should print a=u,u.region.rank==1,u.size==2) "); 
        print(c);
        assertTrue(c.entails(aRegionRank, one)
                   && c.entails(aSize,two)
                   && c.entails(uSize,two)
                   && c.entails(uRegionRank,one));
    }
    /**
     * a.region.rank=1, u.size=2, u=a | a.region.rank=1, b.region.rank=1, a.size=2, b.size=2
     * @throws Throwable
     */
    public void test11() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar aRegion = ConstraintManager.getConstraintSystem().makeField(a, region);
        XVar aRegionRank = ConstraintManager.getConstraintSystem().makeField(aRegion, rank);
        XVar uRegion = ConstraintManager.getConstraintSystem().makeField(u, region);
        XVar uRegionRank = ConstraintManager.getConstraintSystem().makeField(uRegion, rank);
        XVar uSize = ConstraintManager.getConstraintSystem().makeField(u, size);
        XVar aSize = ConstraintManager.getConstraintSystem().makeField(a, size);

        c.addBinding(aRegionRank, one);
        c.addBinding(uSize,two);
        c.addBinding(u,a); // flip order.
        System.out.print("(test11: Should print u=a,a.region.rank==1,a.size=2) "); 
        print(c);
        assertTrue(c.entails(aRegionRank, one)
                   && c.entails(aSize,two)
                   && c.entails(uSize,two)
                   && c.entails(uRegionRank,one));
    }


    public void test13() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XTerm[] terms = new XTerm[]{
                //  ConstraintManager.getConstraintSystem().makeEquals(ConstraintManager.getConstraintSystem().makeField(u,N), zero),
                //ConstraintManager.getConstraintSystem().makeEquals(ConstraintManager.getConstraintSystem().makeField(ConstraintManager.getConstraintSystem().makeField(u,af), here), 
                //	    				 ConstraintManager.getConstraintSystem().makeField(u, here)),
                ConstraintManager.getConstraintSystem().makeDisEquals(ConstraintManager.getConstraintSystem().makeField(u,af), NULL),
                ConstraintManager.getConstraintSystem().makeEquals(c.self(), ConstraintManager.getConstraintSystem().makeField(u,af))
                //  ConstraintManager.getConstraintSystem().makeEquals(ConstraintManager.getConstraintSystem().makeField(v, here), ConstraintManager.getConstraintSystem().makeField(u,here)),
                // ConstraintManager.getConstraintSystem().makeDisEquals(c.self(), NULL)
        };
        for (XTerm term : terms) {
            c.addTerm(term);
        }
        CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint();
        List<? extends XTerm> terms1 = c.constraints();
        for (XTerm term : terms1) {
            term = term.subst((XVar) e0, (XVar) u);
            //	term = term.subst(d.self(), c.self(), true);
            d.addTerm(term);
            System.out.println("test13 (term= " + term + "): " + d);
        }
        CConstraint e = d.copy();
        assertTrue(e.consistent());
    }

    public void test14() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar sRegion = ConstraintManager.getConstraintSystem().makeField(c.self(), region);
        XVar sRegionRank = ConstraintManager.getConstraintSystem().makeField(sRegion, rank);

        c.addBinding(sRegionRank, zero); // self.region.rank==0
        c.addBinding(c.self(), u); // now we should have self==u, u.region.rank==0
        CConstraint d=c.project(u); // now we should be back to self.region.rank==0
        d = d.instantiateSelf(c.self());
        System.out.print("(test14: Should print self.region.rank==0) ");
        print(d);
        assertTrue(d.entails(sRegionRank,zero));
    }

    public void test15() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar uRegion = ConstraintManager.getConstraintSystem().makeField(u, region);
        XVar uRegionRank = ConstraintManager.getConstraintSystem().makeField(uRegion, rank);

        XVar sRegion = ConstraintManager.getConstraintSystem().makeField(c.self(), region);
        XVar sRegionRank = ConstraintManager.getConstraintSystem().makeField(sRegion, rank);

        c.addBinding(uRegionRank, zero); // u.region.rank -> 0
        c.addBinding(u,c.self()); // now we should have self -> u, u.region.rank -> 0
        CConstraint d=c.project(u); // we should still get self.region.rank==0
        d = d.instantiateSelf(c.self());
        System.out.print("(test15: Should print self.region.rank==0) ");
        print(d);
        assertTrue(d.entails(sRegionRank,zero));
    }
    public void test16() throws Throwable {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        XVar eRegion = ConstraintManager.getConstraintSystem().makeField(e0, region);
        XVar eRegionRank = ConstraintManager.getConstraintSystem().makeField(eRegion, rank);

        XVar sRegion = ConstraintManager.getConstraintSystem().makeField(c.self(), region);
        XVar sRegionRank = ConstraintManager.getConstraintSystem().makeField(sRegion, rank);

        c.addBinding(eRegionRank, zero); // e.region.rank -> 0
        c.addBinding(e0,c.self()); // now we should have self -> u, u.region.rank -> 0
        //  CConstraint d=c.project(u); // we should still get self.region.rank==0
        //  d = d.instantiateSelf(c.self());
        System.out.print("(test16: Should print self.region.rank==0) "); print(c);
        assertTrue(c.entails(sRegionRank,zero));
    }
   

}
