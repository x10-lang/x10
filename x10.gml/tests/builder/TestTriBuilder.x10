/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.matrix.TriDense;
import x10.matrix.MathTool;
import x10.matrix.builder.SparseCSCBuilder;
import x10.matrix.builder.TriDenseBuilder;
import x10.matrix.builder.TriSparseBuilder;

public class TestTriBuilder{
    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Int.parse(args(0)):4;
		val d = (args.size > 1) ? Double.parse(args(1)):0.5;
		val testcase = new TestBuilder(m, d);
		testcase.run();
	}
}

class TestBuilder {
	public val M:Long;
	public val nzd:Double;

	public def this(m:Long, d:Double) {
		M = m; nzd = d;
	}

    public def run (): void {
		Console.OUT.println("Starting triangular dense/sparse builder  on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= testDense();
		ret &= testSparse();
		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}
    
    public def testDense():Boolean {
    	var ret:Boolean = true;
    	Console.OUT.println("Starting initial test of triangular dense builder");

    	val tribld = TriDenseBuilder.make(true, M);
    	val tri = tribld.toTriDense();
	
    	tribld.initRandom(nzd);
    	ret &= tribld.isLowerZero();
    	
    	val lobld = TriDenseBuilder.make(false, M);
    	val lo = lobld.toTriDense();
    	lobld.initRandom(nzd);
    	ret &= lobld.isUpperZero();
    	
    	if (ret)
    		Console.OUT.println("Triangular dense matrix random initialization test passed!");
    	else
    		Console.OUT.println("--------Triangular dense matrix random initialization test failed!--------");    
    	return ret;
    }
    
    public def testSparse():Boolean {
    	var ret:Boolean = true;
    	Console.OUT.println("Starting initial test of triangular sparse builder");

    	val tribld = TriSparseBuilder.make(true, M).initRandom(nzd);
    	val tri = tribld.toSparseCSC();
    	
    	for (var c:Long=0; c<M&&ret; c++)
    		for (var r:Long=c+1; r<M&&ret; r++)
    			ret &= (MathTool.isZero(tri(r,c)));
    	
    	val spa = SparseCSCBuilder.make(M, M).initRandom(nzd).toSparseCSC();
    	val ntr = TriSparseBuilder.make(false, M).init(spa).toSparseCSC();
    	if (ret) {
    		Console.OUT.print("spa\n" + spa);
    		Console.OUT.print("ntr\n" + ntr);
    	}
    	for (var c:Long=0; c<M&&ret; c++)
    		for (var r:Long=c; r<M&&ret; r++)
    			ret &= (spa(r,c)==ntr(r,c));

    	if (ret)
    		Console.OUT.println("Triangular sparse matrix builder test passed!");
    	else
    		Console.OUT.println("--------Triangular sparse matrix builder test failed!--------");    
    	return ret;
    }
}
