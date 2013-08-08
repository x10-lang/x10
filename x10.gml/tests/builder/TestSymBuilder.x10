/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.SymDense;
import x10.matrix.sparse.SymSparseCSC;
import x10.matrix.builder.SparseCSCBuilder;
import x10.matrix.builder.SymDenseBuilder;
import x10.matrix.builder.SymSparseBuilder;

public class TestSymBuilder{
    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):4;
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
		Console.OUT.println("Starting symmetric dense-sparse builder  on "+
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
    	Console.OUT.println("Starting initial test of symmetric dense builder");
    	val symbld = SymDenseBuilder.make(M).initRandom(nzd);

    	ret = symbld.checkSymmetric();

    	val sbld = SymDenseBuilder.make(M);
    	
    	val src = DenseMatrix.make(M,M).init((r:Long,c:Long)=>1.0+r+2*c);
    	val tgt = DenseMatrix.make(M,M).init((r:Long,c:Long)=>(r>c)?1.0+r+2*c:1.0+c+2*r);

    	val sden = sbld.init(false, src).toSymDense();
    	ret &= sbld.checkSymmetric();
    	
    	ret &=  tgt.equals(sden as Matrix(tgt.M,tgt.N));
    	if (ret)
    		Console.OUT.println("Symmetric dense matrix mirror test passed!");
    	else
    		Console.OUT.println("--------Symmetric dense matrix mirror test failed!--------"); 
    	return ret;
    }
    
    public def testSparse():Boolean {
    	var ret:Boolean = true;
    	val symbld = SymSparseBuilder.make(M);
    	Console.OUT.println("Starting initial test of symmetric sparse builder");
    	
    	symbld.initRandom(nzd).toSymSparseCSC();
    	ret = symbld.checkSymmetric();
    	
    	val src = SparseCSCBuilder.make(M,M).initRandom(1.0, (r:Long,c:Long)=>(r>c)?(1.0+r+2*c):(1.0+c+2*r)).toSparseCSC();
    	val spa = SymSparseBuilder.make(M).init(false, src).toSymSparseCSC();
    	
    	ret &=  src.equals(spa as Matrix(src.M,src.N));
    	if (ret)
    		Console.OUT.println("Symmetric sparse matrix mirror test passed!");
    	else
    		Console.OUT.println("--------Symmetric sparse matrix mirror test failed!--------"); 
    	return ret;
    }
}
