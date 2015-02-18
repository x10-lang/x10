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
import x10.matrix.ElemType;

import x10.matrix.distblock.DistVector;
import x10.matrix.util.RandTool;
import x10.matrix.util.PlaceGroupBuilder;

public class TestDistVector extends x10Test {
    
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val M:Long;
    
    public def this(m:Long) {
        M = m;
    }
    
    public def run():Boolean {
        Console.OUT.println("Starting distributed vector clone/add/sub/scaling tests on "+
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
	    ret &= (testScatterGather(places));
	    ret &= (testSnapshotRestore(places));
	}
        return ret;
    }
    
    public def testClone(places:PlaceGroup):Boolean{
        Console.OUT.println("Starting vector clone test");
        val dm = DistVector.make(M, places);
        dm.initRandom(); // same as  val dm = Vector.make(N).initRandom(); 
	
        val dm1 = dm.clone();
        var ret:Boolean = dm.equals(dm1);
	
        if (!ret)
            Console.OUT.println("--------DistVector Clone test failed!--------");
	
        dm(1) = dm(2) = ET(10.0);
	
        if ((dm(1)==dm(2)) && (dm(1)==ET(10.0))) {
            ret &= true;
        } else {
            ret &= false;
            Console.OUT.println("---------- DistVector chain assignment test failed!-------");
        }
	
        return ret;
    }
    
    public def testScale(places:PlaceGroup):Boolean{
        Console.OUT.println("Starting distributed vector scaling test");
        val dm = DistVector.make(M, places).initRandom();
        val dm1  = dm * ET(2.5);
        dm1.scale(ET(1.0/2.5));
        val ret = dm.equals(dm1 as DistVector(dm.M));
        if (!ret)
            Console.OUT.println("--------DistVector Scaling test failed!--------");    
        return ret;
    }
    
    public def testAdd(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector addition test");
        val dm = DistVector.make(M, places).initRandom();
        val dm1:DistVector(M) = -1 * dm;
        val dm0 = dm + dm1;
        val ret = dm0.equals(ET(0.0));
        if (!ret)
            Console.OUT.println("--------DistVector Add: dm + dm*-1 test failed--------");
        return ret;
    }
    
    public def testAddSub(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector add-sub test");
        val dm = DistVector.make(M, places).initRandom();
        val dm1= DistVector.make(M, places).initRandom();
        val dm2= dm  + dm1;
        //
        val dm_c  = dm2 - dm1;
        val ret   = dm.equals(dm_c);
        if (!ret)
            Console.OUT.println("--------DistVector Add-sub test failed!--------");
        return ret;
    }
    
    public def testAddAssociative(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector associative test");
	
        val a = DistVector.make(M, places).init(ET(1.0));
        val b = DistVector.make(M, places).initRandom(1n, 10n);
        val c = DistVector.make(M, places).initRandom(10n, 100n);
        val c1 = a + b + c;
        val c2 = a + (b + c);
        val ret = c1.equals(c2);
        if (!ret)
            Console.OUT.println("--------DistVector Add associative test failed!--------");
        return ret;
    }
    
    public def testScaleAdd(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector scaling-add test");
	
        val a:DistVector(M) = DistVector.make(M, places).initRandom();
        val b:DistVector(M) = DistVector.make(M, places).initRandom();
        val a1:DistVector(a.M)= a * ET(0.2);
        val a2:DistVector(a.M)= a * ET(0.8);
        val a3= a1 + a2;
        val ret = a.equals(a3 as DistVector(a.M));
        if (!ret)
            Console.OUT.println("--------DistVector scaling-add test failed!--------");
        return ret;
    }
    
    public def testCellMult(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector cellwise mult test");
	
        val a = DistVector.make(M, places).initRandom(1n, 10n);
        val b = DistVector.make(M, places).initRandom(10n, 100n);
        val c = (a + b) * a;
        val d = a * a + b * a;
        val ret = c.equals(d);
        if (!ret)
            Console.OUT.println("--------DistVector cellwise mult test failed!--------");
        return ret;
    }
    
    public def testCellDiv(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector cellwise mult-div test");
	
        val a = DistVector.make(M, places).init(1);
        val b = DistVector.make(M, places).init(1);
        val c = (a + b) * a;
        val d =  c / (a + b);
        val ret = d.equals(a);
        if (!ret)
            Console.OUT.println("--------DistVector cellwise mult-div test failed!--------");
        return ret;
    }
    
    public def testScatterGather(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector scatter-gather test");
	
        val a = DistVector.make(M, places);
        val b = Vector.make(M).init(1);
        val c = Vector.make(M);
        a.copyFrom(b);
        a.copyTo(c);
        val ret = b.equals(c);
	
        if (!ret)
            Console.OUT.println("--------DistVector scatter-gather test failed!--------");
        return ret;
    }
    
    public def testSnapshotRestore(places:PlaceGroup):Boolean {
        Console.OUT.println("Starting distributed vector snapshot/restore test");
        var dm:DistVector = DistVector.make(M, Place.places()).init(ET(1.0));
	
        var ret:Boolean = dm.equals(ET(1.0));        
        val dm_snapshot = dm.makeSnapshot();
        val dm1 = dm.clone();    
        dm.cellAdd(ET(2.0)); //change the vector after taking a snapshot        
        val newPlaceGroup:PlaceGroup = places;    
        dm.remake(newPlaceGroup);        
        dm.restoreSnapshot(dm_snapshot);    
        ret &= !dm.equals(dm1);//different place groups
        ret &= dm.equals(ET(1.0));
	
        if (!ret)
            Console.OUT.println("--------DistVector snapshot/restore test failed!--------");
	
        return ret;
    }
    
    public static def main(args:Rail[String]) {
        val n = (args.size > 0) ? Long.parse(args(0)):4;
        new TestDistVector(n).execute();
    }
}
