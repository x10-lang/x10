/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 *  (C) Copyright Australian National University 2011.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.Vector;
import x10.matrix.distblock.DupVector;
import x10.matrix.util.PlaceGroupBuilder;

public class TestDupVector extends x10Test {
    public val M:Long;

    public def this(m:Long) {
        M = m;
    }

    public def run():Boolean {
        Console.OUT.println("DupVector clone/add/sub/scaling tests on "+
                            M + "-vectors");
        var ret:Boolean = true;
        val places:PlaceGroup = Place.numPlaces() > 1? PlaceGroupBuilder.makeTestPlaceGroup(1) : Place.places();
        
    @Ifndef("MPI_COMMU") { // TODO Deadlocks!
        ret &= (testClone(places));
        ret &= (testScale(places));
        ret &= (testAdd(places));
        ret &= (testAddSub(places));
        ret &= (testAddAssociative(places));
        ret &= (testScaleAdd(places));
        ret &= (testCellMult(places));
        ret &= (testCellDiv(places));
        ret &= (testReduce(places));
        ret &= (testSnapshotRestore(places));
    }
        return ret;
    }

    public def testClone(places:PlaceGroup):Boolean{
        Console.OUT.println("DupVector clone test");
        val dm = Vector.make(M).initRandom(); 
        val dm1 = DupVector.make(dm, places);
        
        val dm2 = dm1.clone();
        var ret:Boolean = dm.equals(dm1.local());
        ret &= dm1.local().equals(dm2.local());
        ret &= dm1.checkSync();
        if (!ret)
            Console.OUT.println("--------DupVector Clone test failed!--------");
        
        dm(1) = dm(2) = 10.0;
        
        if ((dm(1)==dm(2)) && (dm(1)==10.0)) {
            ret &= true;
        } else {
            ret &= false;
            Console.OUT.println("---------- DupVector chain assignment test failed!-------");
        }
        
        return ret;
    }

    public def testScale(places:PlaceGroup):Boolean{
        Console.OUT.println("DupVector scaling test");
        val dm = DupVector.make(M, places).initRandom();
        val dm1  = dm * 2.5;
        dm1.scale(1.0/2.5);
        var ret:Boolean = dm1.checkSync();
        ret &= dm.equals(dm1 as DupVector(dm.M));
        
        if (!ret)
            Console.OUT.println("--------DupVector Scaling test failed!--------");    
        return ret;
    }

    public def testAdd(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector addition test");
        val dm = DupVector.make(M, places).initRandom();
        val dm1:DupVector(M) = -1 * dm;
        val dm0 = dm + dm1;
        val ret = dm0.equals(0.0);
        if (!ret)
            Console.OUT.println("--------DupVector Add: dm + dm*-1 test failed--------");
        return ret;
    }

    public def testAddSub(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector add-sub test");
        val dm = DupVector.make(M, places).initRandom();
        val dm1= DupVector.make(M, places).initRandom();
        val dm2= dm  + dm1;
        val dm_c  = dm2 - dm1;
        val ret   = dm.equals(dm_c);
        if (!ret)
            Console.OUT.println("--------DupVector Add-sub test failed!--------");
        return ret;
    }

    public def testAddAssociative(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector associative test");
        val a = DupVector.make(M, places).init(1.0);
        val b = DupVector.make(M, places).initRandom(1n, 10n);
        val c = DupVector.make(M, places).initRandom(10n, 100n);
        val c1 = a + b + c;
        val c2 = a + (b + c);
        val ret = c1.equals(c2);
        if (!ret)
            Console.OUT.println("--------DupVector Add associative test failed!--------");
        return ret;
    }

    public def testScaleAdd(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector scaling-add test");
        val a:DupVector(M) = DupVector.make(M, places).initRandom();
        val b:DupVector(M) = DupVector.make(M, places).initRandom();
        val a1:DupVector(a.M)= a * 0.2;
        val a2:DupVector(a.M)= a * 0.8;
        val a3= a1 + a2;
        val ret = a.equals(a3 as DupVector(a.M));
        if (!ret)
            Console.OUT.println("--------DupVector scaling-add test failed!--------");
        return ret;
    }

    public def testCellMult(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector cellwise mult test");
        val a = DupVector.make(M, places).initRandom(1n, 10n);
        val b = DupVector.make(M, places).initRandom(10n, 100n);
        val c = (a + b) * a;
        val d = a * a + b * a;
        val ret = c.equals(d);
        if (!ret)
            Console.OUT.println("--------DupVector cellwise mult test failed!--------");
        return ret;
    }

    public def testCellDiv(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector cellwise mult-div test");
        val a = DupVector.make(M, places).init(1);
        val b = DupVector.make(M, places).init(1);
        val c = (a + b) * a;
        val d =  c / (a + b);
        val ret = d.equals(a);
        if (!ret)
            Console.OUT.println("--------DupVector cellwise mult-div test failed!--------");
        return ret;
    }
    
    public def testReduce(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector reduce test");
        val np = places.size() as Double;        
        val a = DupVector.make(M, places).init(1);
        a.reduceSum();
        var ret:Boolean = a.local().equals(np);
        
        if (!ret)
            Console.OUT.println("--------DupVector reduce sum test failed!--------");
        
        a.init(1).allReduceSum();
        ret = a.equals(np);

        if (!ret)
            Console.OUT.println("--------DupVector all reduce sum test failed!--------");

        return ret;
    }
 
    public def testSnapshotRestore(places:PlaceGroup):Boolean {
        Console.OUT.println("DupVector snapshot/restore test");
        var dm:DupVector = DupVector.make(M, Place.places()).init(1.0);
        var ret:Boolean = dm.equals(1.0);        
        val dm_snapshot = dm.makeSnapshot();        
        dm.cellAdd(2.0); //change the vector after taking a snapshot       
        
        val dm1 = dm.clone();      
        
        val newPlaceGroup:PlaceGroup = places;                
        dm.remake(newPlaceGroup);        
        dm.restoreSnapshot(dm_snapshot);       
               
        ret &= !dm.equals(dm1);//different place groups  
        ret &= dm.equals(1.0);//restore the old value before the snapshot        
        
        if (!ret)
            Console.OUT.println("--------DupVector snapshot/restore test failed!--------");
        
        return ret;
    }

    public static def main(args:Rail[String]) {
        val n = (args.size > 0) ? Long.parse(args(0)):4;
        new TestDupVector(n).execute();
    }
}
