/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import harness.x10Test;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.builder.distblock.DistMatrixBuilder;
import x10.matrix.util.PlaceGroupBuilder;

/**
 * This class contains test cases for dense matrix addition, scaling, and negation operations.
 */
public class TestDistBuilder extends x10Test {
    public val M:Long;
    public val N:Long;
    public val nzd:Double;

    public def this(m:Long, n:Long, z:Double) {
        M = m;
        N = n;
        nzd = z;
    }

    public def run():Boolean {
        Console.OUT.println("Distributed block matrix builder tests on "+
                            M+"x"+ N + " matrices");
        var ret:Boolean = true;
        ret &= (testInit());
        val places:PlaceGroup = Place.numPlaces() > 1? PlaceGroupBuilder.makeTestPlaceGroup(1) : Place.places();
        
        ret &= (testInit(places));
        
        return ret;
    }

    public def testInit():Boolean{
        var ret:Boolean = true;
        val nblk = Place.numPlaces();
        val dmat = DistBlockMatrix.make(M,M, nblk, nblk); 
        val dbld = new DistMatrixBuilder(dmat);
        dbld.allocAllDenseBlocks().initRandom(nzd, (r:Long,c:Long)=>1.0+r+2*c);
         
        if (!ret)
            Console.OUT.println("--------Dist dense matrix builder using Place.places() test failed!--------");
    
        return ret;
    }

    public def testInit(places:PlaceGroup):Boolean{        
        var ret:Boolean = true;        
        val nblk = places.size();        
        val dmat = DistBlockMatrix.make(M,M, nblk, nblk, places);
        val dbld = new DistMatrixBuilder(dmat);      
        val dbld_2 = dbld.allocAllDenseBlocks();
        dbld_2.initRandom(nzd, (r:Long,c:Long)=>1.0+r+2*c);
        if (!ret)
            Console.OUT.println("--------Dist dense matrix builder using an arbitrary place group test failed!--------");

        return ret;
    }

    public static def main(args:Rail[String]) {
        val m = (args.size > 0) ? Long.parse(args(0)):8;
        val n = (args.size > 1) ? Long.parse(args(1)):9;
        val z = (args.size > 2) ? Double.parse(args(2)):0.5;
        new TestDistBuilder(m, n, z).execute();
    }
}
