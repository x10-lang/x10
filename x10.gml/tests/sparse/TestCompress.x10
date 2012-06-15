/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.RandTool;
import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress1D;
import x10.matrix.sparse.Compress2D;

/**
   <p>

   <p>
 */

public class TestCompress{
    public static def main(args:Array[String](1)) {
		val testcase = new CompArrayTest(args);
		testcase.run();
	}
}

class CompArrayTest {

	public val M:Int;
	public val nzp:Double;

    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):32;
		nzp = args.size > 1 ?Double.parse(args(1)):0.5;
	}

	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testCompressArray());
 		ret &= (testCompress1D());
 		ret &= (testCompress2D());
 		ret &= (testCopySection());
 		ret &= (testDataExtraction());
 		ret &= (testSeqAdjustIndex());
		//
		if (ret)
			Console.OUT.println("Compress Data Test passed!\n");
		else
			Console.OUT.println("----------Compress Data Test failed!----------\n");
	}
	//
	public def testCompressArray():Boolean {
		Console.OUT.println("Test compress array size of "+M);
		//
		val ca = CompressArray.make(M);
		ca.initRandom(0, M, nzp);
		//ca.print();
		val uc = new Array[Double](M);
		ca.extract(uc);
		//Console.OUT.println(uc.toString());

		var ret:Boolean;
		ret = ca.equals(uc);
		if (ret) Console.OUT.println("Decompress passed");

		val ca1 = CompressArray.compress(uc);
		ret &= ca1.equals(ca);
		//cl2.print("c2 =");
		if (ret)
			Console.OUT.println("Compress array test passed!");
		else
			Console.OUT.println("----------Compress array test failed!----------");
		return ret;	
	}
	//
	public def testCompress1D():Boolean{

		val ca = new CompressArray(M);
		val c1d = Compress1D.makeRand(M, nzp, 0, ca);
		val alist = new Array[Double](M);
		c1d.extract(alist);

		var ret:Boolean=true;
		Console.OUT.println("Test compress 1D size of "+M);
		Console.OUT.println("non-zero : "+ c1d.size());
		
		//c1d.print();
		ret &= c1d.testIn(alist);
		if (ret)
			Console.OUT.println("Compress 1D test passed!");
		else
			Console.OUT.println("----------Compress 1D test failed!----------");
		return ret;
	}
	//
	public def testCompress2D():Boolean{

		var ret:Boolean = true;
		val ca = new CompressArray(M*M);
		val c2d = Compress2D.make(M, ca);
		c2d.initRandom(M, nzp);
		//val c2d = Compress2D.makeRand(M, M, nzp, ca);

		Console.OUT.println("Test compress 2D, lines: "+M+", nzcount:"
							+ c2d.countNonZero());
		
		val nzc:Int = c2d.countNonZero() as Int;
		val ia:Array[Int](1)    = new Array[Int](M+1);
		val ja:Array[Int](1)    = new Array[Int](nzc);
		val av:Array[Double](1) = new Array[Double](nzc);
		c2d.toCompressSparse(ia, ja, av);
		//Console.OUT.println("Convert to Compress Sparse");
		//Global.debug.println(ia);
		//Global.debug.println(ja);
		//Global.debug.println(av);
		//Global.debug.flush();
		val ntd = Compress2D.make(ia , ja, av);
		//ntd.print2D("Convert back\n");
		ret=ntd.equals(c2d);
		//
		val c2d_ = c2d.clone();
		ret&=c2d.equals(c2d_);
		//

		if (ret)
			Console.OUT.println("Compress 2D test passed!");
		else
			Console.OUT.println("----------Compress 2D test failed!----------");
		return ret;
	}
// 	//
	public def testCopySection():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Compress 2D line section copy test");

		val ca = new CompressArray(M*M);
		val c1 = Compress2D.makeRand(M, M, nzp, ca);

		val ca2= new CompressArray(M*M);
		val c2 = Compress2D.make(M, ca2);

		Compress2D.copy(c1, c2);
		//c2.print();

		ret &= c1.equals(c2);
		if (ret) 
			Console.OUT.println("Test full copy passed");
		else
			Console.OUT.println("----------Test full copy failed!------------");

		// Test partial copy
 		val ss= M/5;
 		val cnt= M/2+5;
		c2.reset();
		Console.OUT.printf("Copy [%d %d] indexes \n", ss, ss+cnt-1);
		Compress2D.copySection(c1, ss, c2, cnt);
		//c2.print();

 		for (var l:Int=0; l<M; l++) {
 			for (var idx:Int=ss; idx<cnt; idx++) {
 				ret &= (c1(l, idx)==c2(l, idx-ss));
 			}
 		}
		//c2.print();
		if (ret) Console.OUT.println("Test partial copy passed");

		if (ret)
			Console.OUT.println("Compress 2D line section copy test passed!");
		else
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
		//
		val s1 = new Array[Double](idxsz);
		c1.extract(ss, num, 0, s1); //Get data between [ss, sd]
		//Global.debug.println(s1); Global.debug.flush();

		var ret:Boolean = true;
		var i:Int;
		for (i=ss; (i<ss+num)&&ret; i++) ret &= (c1(i)==s1(i));
		if (! ret) Console.OUT.printf("Extracted data mismatch at index %d : %f <> %f\n", 
									  i-1, c1(i-1), s1(i-1));
		
		if (ret)
			Console.OUT.println("Compress line data extraction test passed!");
		else
			Console.OUT.println("----------Compress line data extraction test failed!----------");
		return ret;		
	}

	public def testSeqAdjustIndex():Boolean{
		Console.OUT.println("Sequentialize index test");

		val ldm = M ;
		val ca = new CompressArray(M*ldm);
		val cd = Compress2D.makeRand(M, ldm, nzp, ca);
		val c2 = cd.clone();
		//cd.print();			 
		//ca.print();
		val cnt = cd.serializeIndex(ldm, 0, M);
		//ca.print();

		val lft = cd.buildIndex(ldm, 0, M, cnt);
		Debug.assure(lft==0, "Left elements unclaimed after rebuilding indexes");
		//cd.print();
		//ca.print();
		//
		var ret:Boolean = c2.equals(cd);

		if (ret)
			Console.OUT.println("Sequentialize index test passed!");
		else
			Console.OUT.println("----------Sequentialize index test failed!----------");
		return ret;		
	}

}