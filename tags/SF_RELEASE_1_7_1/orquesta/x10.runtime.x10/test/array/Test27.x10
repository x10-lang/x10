/**
 * Distribution algebra
 *
 * (Was DistAlgebra, but NOTE: the semantics of cyclic distributions
 * have changed)
 */

class Test27 extends TestArray {

    void check(String name, Dist d) {

        prDist(name, d);

        //Check range restriction to a place
        for (int k=0; k<place.MAX_PLACES; k++) {
            place p = place.factory.place(k);
            Dist dp = d.$bar(p);
            prDist(name + "|" + p, dp);
        }
    }



    public void run() {

        final Region r1 = r(0,1,0,7);
        prArray("r1", r1);

        final Region r2 = r(4,5,0,7);
        prArray("r2", r2);

        final Region r3 = r(0,7,4,5);
        prArray("r3", r3);

        final Region r12 = r1.$or(r2);
        pr("r12" + r12);

        final Region r12a3 = r12.$and(r3);
        pr("r12a3" + r12a3);

        final Region r123 = r1.$or(r2).$or(r3);
        pr("r123" + r123);

        final Region r12m3 = r12.$minus(r3);
        pr("r12m3" + r12m3);

        final Dist d123x0 = Dist.makeCyclic(r123, 0);
        check("d123x0", d123x0);

        final Dist d123x1 = Dist.makeCyclic(r123, 1);
        check("d123x1", d123x1);

        //
        // dist op region
        //

        final Dist d123x0r12a3 = d123x0.$bar(r12a3);
        prDist("d123x0r12a3", d123x0r12a3);

        final Dist d123x1r12a3 = d123x1.$bar(r12a3);
        prDist("d123x1r12a3", d123x1r12a3);

        final Dist d123x0r12m3 = d123x0.$bar(r12m3);
        prDist("d123x0r12m3", d123x0r12m3);

        final Dist d123x1r12m3 = d123x1.$bar(r12m3);
        prDist("d123x1r12m3", d123x1r12m3);


        //
        // dist - dist
        //

        final Dist d1 = d123x0.$minus(d123x0r12m3);
        prDist("d1 = d123x0 - d123x0r12m3", d1);
        final Dist d3 = d123x0.$bar(r3);
        prDist("d3 = d123x0 | r3", d3);
        pr("d1.equals(d3) checks " + d1.equals(d3));
        pr("d1.isSubdistribution(d123x0) checks " + d1.isSubdistribution(d123x0));
        pr("!d123x0.isSubdistribution(d1) checks " + !d123x0.isSubdistribution(d1));

        final Dist d1x = d123x0.$minus(d123x1r12m3);
        prDist("d1x = d123x0 - d123x1r12m3", d1x);
        pr("d1x.isSubdistribution(d123x0) checks " + d1x.isSubdistribution(d123x0));
        pr("!d123x0.isSubdistribution(d1x) checks " + !d123x0.isSubdistribution(d1x));


        //
        // dist && dist
        //

        final Dist d2 = d123x0r12m3.$and(d123x0);
        prDist("d2 = d123x0r12m3 && d123x0", d2);
        pr("d2.equals(d123x0r12m3) checks " + d2.equals(d123x0r12m3));

        final Dist d2x = d123x0r12m3.$and(d123x1);
        prDist("d2x = d123x0r12m3 && d123x1", d2x);


        //
        // dist overlay dist
        //

        final Dist d5 = d123x0.$bar(r12);
        prDist("d5 = d123x0 | r12", d5);
        final Dist d4 = d5.overlay(d3);
        prDist("d4 = d5.overlay(d3)", d4);
        pr("d4.equals(d123x0) checks " + d4.equals(d123x0));


        final Dist d5x = d123x1.$bar(r12);
        prDist("d5x = d123x1 | r12", d5);
        final Dist d4x = d5x.overlay(d3);
        prDist("d4x = d5x.overlay(d3)", d4x);


        //
        // dist union dist
        //

        final Dist d6 = d123x0r12a3.$or(d123x0r12m3);
        prDist("d6 = d123x0r12a3 || d123x0r12m3", d6);
        pr("d6.equals(d5) checks " + d6.equals(d5));


        new E("d6x = d123x0 || d123x1") {
            void run() {
                final Dist d6x = d123x0.$or(d123x1);
                prDist("d6x = d123x0 || d123x1", d6x);
            }
        };
    }

}

