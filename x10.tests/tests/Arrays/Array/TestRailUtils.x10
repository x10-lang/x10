/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2012.
 *  (C) Copyright IBM Corporation 2014.
 */

import harness.x10Test;
import x10.util.ArrayList;
import x10.util.RailUtils;
import x10.util.Random;

/**
 * Functional test of x10.util.RailUtils
 * @author milthorpe 05/2012
 */
public class TestRailUtils extends x10Test {
    static N = 30;

	public def run(): Boolean = {
        testSortAndSearchRail();
        testSortAndSearchArrayList();
        testMap();
        testMap2();
        testReduce();
        testScan();
        return true;
	}

    public def testSortAndSearchRail() {
        val r = new Random();
        val a = new Rail[Double](N);
        for (i in 0..(N-1)) {
            a(i) = r.nextDouble();
        }

        // seed the array for later search
        val magicValue = 0.8;
        a(2) = magicValue; 

        RailUtils.sort(a);

        // check that sort actually sorted the array
        var current:Double = -Double.MAX_VALUE;
        for (i in 0..(N-1)) {
            chk(current <= a(i));
            current = a(i);
        }

        val key = 0.4;
        val index = RailUtils.binarySearch(a, key);

        if (index >= 0) {
            // key found (unlikely)
            chk(a(index) == key);
        } else {
            // key not found - check insertion index
            val insertion = -(index+1);
            chk(a(insertion-1) < key);
            chk(a(insertion) > key);
        }

        // search for the value that was previously seeded
        val indexMagic = RailUtils.binarySearch(a, magicValue);
        chk(a(indexMagic) == magicValue);
    }

    public def testSortAndSearchArrayList() {
        val r = new Random();
        val aList = new ArrayList[Double](N);
        for (i in 0..(N-1)) {
            aList.add(r.nextDouble());
        }

        // seed the list for later search
        val magicValue = 0.8;
        aList(2) = magicValue;

        aList.sort();

        var current:Double = -Double.MAX_VALUE;
        for (i in 0..(N-1)) {
            chk(current <= aList(i));
            current = aList(i);
        }

        val key = 0.4;
        val index2 = aList.binarySearch(key);
        if (index2 >= 0) {
            chk(aList(index2) == key);
        } else {
            val insertion = -(index2+1);
            chk(aList(insertion-1) < key);
            chk(aList(insertion) > key);
        }

        val indexMagic2 = aList.binarySearch(magicValue);
        chk(aList(indexMagic2) == magicValue);

        // search for index before first, after last and in empty list
        val indexOfMin = aList.binarySearch(Double.MIN_VALUE);
        chk(-(indexOfMin+1) == 0L);

        val indexOfMax = aList.binarySearch(Double.MAX_VALUE);
        chk(-(indexOfMax+1) == aList.size());

        val emptyArray = new Rail[Double](0);
        chk(RailUtils.binarySearch[Double](emptyArray, 1.0) == -1L);
    }

    public def testMap() {
        val a = new Rail[Long](N, (i:Long)=>i);
        val b = new Rail[Double](N);
        RailUtils.map(a, b, (x:Long) => x*2.0);
        for (i in 0..(N-1)) {
            chk(b(i) == i*2.0);
        }
    }

    public def testMap2() {
        val a = new Rail[Long](N, (i:Long)=>i);
        val b = new Rail[Double](N, 2.0);
        val c = new Rail[Double](N);
        RailUtils.map(a, b, c, (x:Long,y:Double) => x+y);
        for (i in 0..(N-1)) {
            chk(c(i) == a(i)+b(i));
        }
    }

    public def testReduce() {
        val a = new Rail[Double](N, (i:Long)=>i as Double);
        val sum = RailUtils.reduce(a, (x:Double, y:Double) => x+y, 0.0);
        chk(sum == (N*(N-1))/2.0);
    }

    public def testScan() {
        val a = new Rail[Double](N, 1.0);
        val b = RailUtils.scan(a, (x:Double, y:Double) => x+y, 0.0);
        for (i in 0..(N-1)) {
            chk(b(i) == (i+1) as Double);
        }
    }

	public static def main(args: Rail[String]): void = {
		new TestRailUtils().execute();
	}
}

