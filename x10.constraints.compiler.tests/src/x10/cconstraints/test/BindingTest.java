package x10.cconstraints.test;

import java.util.List;

import polyglot.types.Type;
import x10.constraint.XDef;
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;

public class BindingTest extends X10TestCase {
    public BindingTest() {
        super("BindingTest");
    }
    XTerm<Type> zero = sys.makeLit(ts.Int(), new Integer(0));
    XTerm<Type> one = sys.makeLit(ts.Int(), new Integer(1));
    XTerm<Type> two = sys.makeLit(ts.Int(), new Integer(2));
    XTerm<Type> NULL = sys.makeLit(ts.Null(), null);

    XEQV<Type> e0 = sys.makeEQV(ts.GlobalRef());

    XDef<Type> home = makeField("home",ts.Place()); 
    XDef<Type> rank = makeField("rank",ts.Int()); 
    XDef<Type> size = makeField("size",ts.Int()); 
    XDef<Type> region= makeField("region",ts.Region()); 
    XDef<Type> N= makeField("N",ts.Int()); 
    XDef<Type> af= makeField("af",ts.Any()); 
    XDef<Type> here= makeField("here",ts.Place()); 


    XField<Type,XDef<Type>> e0home = sys.makeField(e0, home);
    XUQV<Type> a = sys.makeUQV(ts.Any(),"a");
    XUQV<Type> u = sys.makeUQV(ts.Any(),"u");
    XUQV<Type> v = sys.makeUQV(ts.Any(),"v");
    XUQV<Type> s = sys.makeUQV(ts.Any(),"s");

    /**
     * self==e0.home, e0==a |- self==a.home
     * @throws Throwable
     */
    public void test1() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts.Place(),ts);
        c.addSelfEquality(e0home);
        c.addEquality(e0, a);
        //XPromise xp  = (c.self()).nfp(c);
        System.out.print("(test1: Should print self==a.home) "); print(c);
        assertTrue(c.entailsEquality(c.self(), sys.makeField(a,home)));
    }

    /**
     * self.rank==1, self==a | a.rank==1
     * @throws Throwable
     */
    public void test2() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts.Any(), ts);
        c.addEquality(sys.makeField(c.self(), rank), one);
        c.addEquality(c.self(), a);
        assertTrue(c.entailsEquality(sys.makeField(a, rank), one));
    }
    /**
     * a=u, v=u | a=v
     * @throws Throwable
     */
    public void test3() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        c.addEquality(a,u);
        System.out.print("(test3: Should print a=u) "); print(c);
        c.addEquality(v, u);
        System.out.print("(test3: Should print a==u, v==u) "); print(c);
        assertTrue(c.entailsEquality(a, v));
    }
    /**
     * exists u(a=u, v=u) | a=v
     * @throws Throwable
     */
    public void test4() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        c.addEquality(a,u);
        c.addEquality(v,u);
        c=c.substitute(e0,u);
        System.out.print("(test4: Should print a=v) "); print(c);
        assertTrue(c.entailsEquality(a, v));
    }
    /**
     * exists u(u=a, u=v) | a=v
     * @throws Throwable
     */
    public void test5() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        c.addEquality(u,a);
        c.addEquality(u,v);
        c=c.substitute(e0,u);
        System.out.print("(test5: Should print a=v) "); print(c);
        assertTrue(c.entailsEquality(a, v));
    }

    /**
     * u^(a.rank=u.rank, v.rank=u.rank) | a.rank=v.rank
     * @throws Throwable
     */
    public void test6() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        XField<Type,XDef<Type>> ar = sys.makeField(a, rank);
        XField<Type,XDef<Type>>  ur = sys.makeField(u, rank);
        XField<Type,XDef<Type>>  vr = sys.makeField(v, rank);

        c.addEquality(ar,ur);
        c.addEquality(vr,ur);
        c=c.substitute(e0,u);
        System.out.print("(test6: Should print a.rank==v.rank)"); print(c);
        assertTrue(c.entailsEquality(ar, vr));
    }

    /**
     * exists u(u.rank=a.rank, u.rank=v.rank) | a.rank=v.rank
     * @throws Throwable
     */
    public void test7() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        XField<Type,XDef<Type>>  ar = sys.makeField(a, rank);
        XField<Type,XDef<Type>>  ur = sys.makeField(u, rank);
        XField<Type,XDef<Type>>  vr = sys.makeField(v, rank);

        c.addEquality(ur,ar);
        c.addEquality(ur,vr);
        c=c.substitute(e0,u);
        System.out.print("(test7: Should print a.rank==v.rank) "); print(c);
        assertTrue(c.entailsEquality(ar, vr));
    }

    /**
     * a^(s.region=a.region, a.region.rank=u.region.rank,v.rank=u.region.rank)|-s.region.rank=u.region.rank,v.rank=u.egion.rank
     * @throws Throwable
     */
    public void test8() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        XField<Type,XDef<Type>> aRegion = sys.makeField(a, region);
        XField<Type,XDef<Type>> aRegionRank = sys.makeField(aRegion, rank);
        XField<Type,XDef<Type>> uRegion = sys.makeField(u, region);
        XField<Type,XDef<Type>> uRegionRank = sys.makeField(uRegion, rank);

        XField<Type,XDef<Type>> vRank = sys.makeField(v, rank);
        XField<Type,XDef<Type>> sRegion = sys.makeField(s, region);
        XField<Type,XDef<Type>> sRegionRank = sys.makeField(sRegion, rank);
        c.addEquality(sRegion, aRegion);
        c.addEquality(aRegionRank,uRegionRank);
        c.addEquality(vRank,uRegionRank);
        System.out.print("(test8: Should print s.region == a.region, s.region.rank == a.region.rank, v.rank == u.region.rank) "); 
        print(c);
        // Existentially quantify 'a'.  This means the print should not mention a anymore, so the first
        // conjunct vanishes, and the second on has u.region substituted for a.region
        c=c.project(a);
        System.out.print("(test8: Should print s.region.rank==u.region.rank, v.rank==u.region.rank) "); 
        print(c);
        assertTrue(c.entailsEquality(sRegionRank, uRegionRank));
        assertTrue(c.entailsEquality(vRank, uRegionRank));
    }

    /**
     * a.rank=1, b.size=2, a=b | a.rank=1, b.rank=1, a.size=2, b.size=2
     * @throws Throwable
     */
    public void test9() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        XField<Type,XDef<Type>> aRank = sys.makeField(a, rank);
        XField<Type,XDef<Type>> uRank = sys.makeField(u, rank);
        XField<Type,XDef<Type>> uSize = sys.makeField(u, size);
        XField<Type,XDef<Type>> aSize = sys.makeField(a, size);

        c.addEquality(aRank, one);
        c.addEquality(uSize,two);
        c.addEquality(a,u);
        System.out.print("(test9: Should print a=u,u.size==2,u.rank==1) "); print(c);
        assertTrue(c.entailsEquality(aRank, one)
                   && c.entailsEquality(aSize,two)
                   && c.entailsEquality(uSize,two)
                   && c.entailsEquality(uRank,one));
    }

    /**
     * a.region.rank=1, u.size=2, a=u | a.region.rank=1, u.region.rank=1, a.size=2, u.size=2
     * @throws Throwable
     */
    public void test10() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        XField<Type,XDef<Type>> aRegion = sys.makeField(a, region);
        XField<Type,XDef<Type>> aRegionRank = sys.makeField(aRegion, rank);
        XField<Type,XDef<Type>> uRegion = sys.makeField(u, region);
        XField<Type,XDef<Type>> uRegionRank = sys.makeField(uRegion, rank);
        XField<Type,XDef<Type>> uSize = sys.makeField(u, size);
        XField<Type,XDef<Type>> aSize = sys.makeField(a, size);

        c.addEquality(aRegionRank, one);
        c.addEquality(uSize,two);
        c.addEquality(a,u);
        System.out.print("(test10: Should print a=u,u.region.rank==1,u.size==2) "); 
        print(c);
        assertTrue(c.entailsEquality(aRegionRank, one)
                   && c.entailsEquality(aSize,two)
                   && c.entailsEquality(uSize,two)
                   && c.entailsEquality(uRegionRank,one));
    }
    /**
     * a.region.rank=1, u.size=2, u=a | a.region.rank=1, b.region.rank=1, a.size=2, b.size=2
     * @throws Throwable
     */
    public void test11() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts);
        XField<Type,XDef<Type>> aRegion = sys.makeField(a, region);
        XField<Type,XDef<Type>> aRegionRank = sys.makeField(aRegion, rank);
        XField<Type,XDef<Type>> uRegion = sys.makeField(u, region);
        XField<Type,XDef<Type>> uRegionRank = sys.makeField(uRegion, rank);
        XField<Type,XDef<Type>> uSize = sys.makeField(u, size);
        XField<Type,XDef<Type>> aSize = sys.makeField(a, size);

        c.addEquality(aRegionRank, one);
        c.addEquality(uSize,two);
        c.addEquality(u,a); // flip order.
        System.out.print("(test11: Should print u=a,a.region.rank==1,a.size=2) "); 
        print(c);
        assertTrue(c.entailsEquality(aRegionRank, one)
                   && c.entailsEquality(aSize,two)
                   && c.entailsEquality(uSize,two)
                   && c.entailsEquality(uRegionRank,one));
    }


    public void test13() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts.Any(), ts);
        @SuppressWarnings("unchecked")
		XTerm<Type>[] terms = new XTerm[]{
                //  sys.makeEquals(sys.makeField(u,N), zero),
                //sys.makeEquals(sys.makeField(sys.makeField(u,af), here), 
                //	    				 sys.makeField(u, here)),
                sys.makeExpr(XOp.EQ(ts.Boolean()), sys.makeField(u,af), NULL),
                sys.makeExpr(XOp.NEQ(ts.Boolean()), c.self(), sys.makeField(u,af))
                //  sys.makeEquals(sys.makeField(v, here), sys.makeField(u,here)),
                // sys.makeDisEquals(c.self(), NULL)
        };
        for (XTerm<Type> term : terms) {
            c.addTerm(term);
        }
        System.out.println(c);
        CConstraint d = sys.makeCConstraint(ts.Any(), ts);
        List<? extends XTerm<Type>> terms1 = c.extTerms();
        for (XTerm<Type> term : terms1) {
        	XTerm<Type> term2 = term.subst(sys, (XVar<Type>) e0, (XVar<Type>) u);
            //	term = term.subst(d.self(), c.self(), true);
            d.addTerm(term2);
            System.out.println("test13 (term= " + term2 + "): " + d);
        }
        System.out.println(d);
        CConstraint e = d.copy();
        assertTrue(e.consistent());
    }

    public void test14() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts.Any(),ts);
        XField<Type,XDef<Type>> sRegion = sys.makeField(c.self(), region);
        XField<Type,XDef<Type>> sRegionRank = sys.makeField(sRegion, rank);

        c.addEquality(sRegionRank, zero); // self.region.rank==0
        c.addEquality(c.self(), u); // now we should have self==u, u.region.rank==0
        System.out.print("(test14: Should print self==u, u.region.rank==0) ");
        print(c);
        CConstraint d=c.project(u); // now we should be back to self.region.rank==0
        System.out.print("(test14: Should print self.region.rank==0) ");
        print(d);
        d = d.instantiateSelf(c.self());
        System.out.print("(test14: Should print self.region.rank==0) ");
        print(d);
        assertTrue(d.entailsEquality(sRegionRank,zero));
    }

    public void test15() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts.Any(), ts);
        XField<Type,XDef<Type>> uRegion = sys.makeField(u, region);
        XField<Type,XDef<Type>> uRegionRank = sys.makeField(uRegion, rank);

        XField<Type,XDef<Type>> sRegion = sys.makeField(c.self(), region);
        XField<Type,XDef<Type>> sRegionRank = sys.makeField(sRegion, rank);

        c.addEquality(uRegionRank, zero); // u.region.rank -> 0
        c.addEquality(u,c.self()); // now we should have self -> u, u.region.rank -> 0
        CConstraint d=c.project(u); // we should still get self.region.rank==0
        d = d.instantiateSelf(c.self());
        System.out.print("(test15: Should print self.region.rank==0) ");
        print(d);
        assertTrue(d.entailsEquality(sRegionRank,zero));
    }
    public void test16() throws Throwable {
        CConstraint c = sys.makeCConstraint(ts.Any(), ts);
        XField<Type,XDef<Type>> eRegion = sys.makeField(e0, region);
        XField<Type,XDef<Type>> eRegionRank = sys.makeField(eRegion, rank);

        XField<Type,XDef<Type>> sRegion = sys.makeField(c.self(), region);
        XField<Type,XDef<Type>> sRegionRank = sys.makeField(sRegion, rank);

        c.addEquality(eRegionRank, zero); // e.region.rank -> 0
        c.addEquality(e0,c.self()); // now we should have self -> u, u.region.rank -> 0
        //  CConstraint d=c.project(u); // we should still get self.region.rank==0
        //  d = d.instantiateSelf(c.self());
        System.out.print("(test16: Should print self.region.rank==0) "); print(c);
        assertTrue(c.entailsEquality(sRegionRank,zero));
    }
   

}
