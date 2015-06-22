/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

import x10.matrix.ElemType;
import x10.matrix.util.Debug;
import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress1D;
import x10.matrix.sparse.Compress2D;

public class TestCompress extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val M:Long;
    public val nzp:Float;
    
    public def this(args:Rail[String]) {
	M = args.size > 0 ? Long.parse(args(0)):32;
	nzp = args.size > 1 ? Float.parse(args(1)):0.5f;
    }
    
    public def run():Boolean {
	var ret:Boolean = true;
	ret &= (testCompressArray());
	ret &= (testCompress1D());
	ret &= (testCompress2D());
	ret &= (testCopySection());
	ret &= (testDataExtraction());
	ret &= (testSeqAdjustIndex());
        return ret;
    }
    
    public def testCompressArray():Boolean {
	Console.OUT.println("Test compress array size of "+M);
	
	val ca = CompressArray.make(M);
	ca.initRandom(0, M, nzp);
	val uc = new Rail[ElemType](M);
	ca.extract(uc);
	
	var ret:Boolean;
	ret = ca.equals(uc);
	
	val ca1 = CompressArray.compress(uc);
	ret &= ca1.equals(ca);
	if (!ret)
	    Console.OUT.println("----------Compress array test failed!----------");
	return ret;
    }
    
    public def testCompress1D():Boolean{
	val ca = new CompressArray(M);
	val c1d = Compress1D.makeRand(M, nzp, 0, ca);
	val alist = new Rail[ElemType](M);
	c1d.extract(alist);
	
	var ret:Boolean=true;
	Console.OUT.println("Test compress 1D size of "+M);
	Console.OUT.println("non-zero : "+ c1d.size());
	
	ret &= c1d.testIn(alist);
	if (!ret)
	    Console.OUT.println("----------Compress 1D test failed!----------");
	return ret;
    }
    
    public def testCompress2D():Boolean{
	var ret:Boolean = true;
	val ca = new CompressArray(M*M);
	val c2d = Compress2D.makeRand(M, M, nzp, ca);
	
	Console.OUT.println("Test compress 2D, lines: "+M+", nzcount:"
			    + c2d.countNonZero());
	
	val nzc = c2d.countNonZero();
	val ia = new Rail[Long](M+1);
	val ja = new Rail[Long](nzc);
	val av = new Rail[ElemType](nzc);
	c2d.toCompressSparse(ia, ja, av);
	val ntd = Compress2D.make(ia, ja, av);
	ret=ntd.equals(c2d);
	
	val c2d_ = c2d.clone();
	ret&=c2d.equals(c2d_);
	
	if (!ret)
	    Console.OUT.println("----------Compress 2D test failed!----------");
	return ret;
    }
    
    public def testCopySection():Boolean{
	var ret:Boolean = true;
	Console.OUT.println("Compress 2D line section copy test");
	
	val ca = new CompressArray(M*M);
	val c1 = Compress2D.makeRand(M, M, nzp, ca);
	
	val ca2= new CompressArray(M*M);
	val c2 = Compress2D.make(M, ca2);
	
	Compress2D.copy(c1, c2);
	
	ret &= c1.equals(c2);
	if (!ret)
	    Console.OUT.println("----------Test full copy failed!------------");
	
	// Test partial copy
	val ss= M/5;
	val cnt= M/2+5;
	c2.reset();
	Compress2D.copySection(c1, ss, c2, cnt);
	
	for (var l:Long=0; l<M; l++) {
	    for (var idx:Long=ss; idx<cnt; idx++) {
		ret &= (c1(l, idx)==c2(l, idx-ss));
	    }
	}
	
	if (!ret)
	    Console.OUT.println("----------Compress 2D line section copy test failed!----------");
	return ret;
    }
    
    public def testDataExtraction():Boolean{
	Console.OUT.println("Compress line data extraction test");
	val idxsz = M * 4;
	val ca = new CompressArray(M);
	val c1= Compress1D.makeRand(idxsz, nzp, 0, ca);
	
	val ss  = idxsz/4;
	val num = idxsz/2;
	
	val s1 = new Rail[ElemType](idxsz);
	c1.extract(ss, num, 0, s1); //Get data between [ss, sd]
	
	var ret:Boolean = true;
	var i:Long;
	for (i=ss; (i<ss+num)&&ret; i++) ret &= (c1(i)==s1(i));
	if (! ret) Console.OUT.printf("Extracted data mismatch at index %d : %f <> %f\n",
				      i-1, c1(i-1), s1(i-1));
	
	if (!ret)
	    Console.OUT.println("----------Compress line data extraction test failed!----------");
	return ret;
    }
    
    public def testSeqAdjustIndex():Boolean{
	Console.OUT.println("Sequentialize index test");
	
	val ldm = M ;
	val ca = new CompressArray(M*ldm);
	val cd = Compress2D.makeRand(M, ldm, nzp, ca);
	val c2 = cd.clone();
	val cnt = cd.serializeIndex(ldm, 0, M);
	
	val lft = cd.buildIndex(ldm, 0, M, cnt);
	Debug.assure(lft==0L, "Left elements unclaimed after rebuilding indexes");
	
	var ret:Boolean = c2.equals(cd);
	
	if (!ret)
	    Console.OUT.println("----------Sequentialize index test failed!----------");
	return ret;
    }
    
    public static def main(args:Rail[String]) {
	new TestCompress(args).execute();
    }
}
