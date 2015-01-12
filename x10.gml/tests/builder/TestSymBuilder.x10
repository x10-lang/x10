/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import harness.x10Test;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.SymDense;
import x10.matrix.sparse.SymSparseCSC;
import x10.matrix.builder.SparseCSCBuilder;
import x10.matrix.builder.SymDenseBuilder;
import x10.matrix.builder.SymSparseBuilder;

public class TestSymBuilder extends x10Test {
	public val M:Long;
	public val nzd:Double;

	public def this(m:Long, d:Double) {
		M = m; nzd = d;
	}

    public def run():Boolean {
		Console.OUT.println("Symmetric dense-sparse builder  on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
		ret &= testDense();
		ret &= testSparse();

		return ret;
	}
    
    public def testDense():Boolean {
    	var ret:Boolean = true;
    	Console.OUT.println("Initial test of symmetric dense builder");
    	val symbld = SymDenseBuilder.make(M).initRandom(nzd);

    	ret = symbld.checkSymmetric();

    	val sbld = SymDenseBuilder.make(M);
    	
    	val src = DenseMatrix.make(M,M).init((r:Long,c:Long)=>1.0+r+2*c);
    	val tgt = DenseMatrix.make(M,M).init((r:Long,c:Long)=>(r>c)?1.0+r+2*c:1.0+c+2*r);

    	val sden = sbld.init(false, src).toSymDense();
    	ret &= sbld.checkSymmetric();
    	
    	ret &=  tgt.equals(sden as Matrix(tgt.M,tgt.N));
    	if (!ret)
    		Console.OUT.println("--------Symmetric dense matrix mirror test failed!--------"); 
    	return ret;
    }
    
    public def testSparse():Boolean {
    	var ret:Boolean = true;
    	val symbld = SymSparseBuilder.make(M);
    	Console.OUT.println("Initial test of symmetric sparse builder");
    	
    	symbld.initRandom(nzd).toSymSparseCSC();
    	ret = symbld.checkSymmetric();
    	
    	val src = SparseCSCBuilder.make(M,M).init((r:Long,c:Long)=>(r>c)?(1.0+r+2*c):(1.0+c+2*r)).toSparseCSC();
    	val spb = SymSparseBuilder.make(M).init(false, src);
        val spa = spb.toSymSparseCSC();

    	ret &=  src.equals(spa as Matrix(src.M,src.N));
    	if (!ret)
    		Console.OUT.println("--------Symmetric sparse matrix mirror test failed!--------"); 
    	return ret;
    }

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):4;
		val d = (args.size > 1) ? Double.parse(args(1)):0.5;
		new TestSymBuilder(m, d).execute();
	}
}
