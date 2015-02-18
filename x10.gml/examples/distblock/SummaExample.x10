/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.SummaMult;
import x10.matrix.distblock.summa.SummaMultTrans;


/**
   <p>
   * Examples of distributed block matrix in matrix multiplication using SUMMA algorithm
   <p>
*/
public class SummaExample {
    
    public static def main(args:Rail[String]) {
        val testcase = new RunSummaExample(args);
        testcase.run();
    }
}

class RunSummaExample {
    //Matrix dimentsions
    public val M:Long; 
    public val K:Long;
    public val N:Long;
    //Partition parameters
    public val bM:Long;
    public val bN:Long;
    //Distribution parameters
    public val pM:Long;
    public val pN:Long;
    //Sparse matrix nonzero density
    public val nzd:Float;
    //Verification flag
    val vrf:Boolean;
    
    
    public def this(args:Rail[String]) {
        M = args.size   > 0 ? Long.parse(args(0)):4;
        K = args.size   > 1 ? Long.parse(args(1)):6;
        N = args.size   > 2 ? Long.parse(args(2)):4;
        bM = args.size  > 3 ? Long.parse(args(3)):2;
        bN = args.size  > 4 ? Long.parse(args(4)):2;
        nzd = args.size > 5 ?Float.parse(args(5)):1.0f;
        vrf = args.size > 6 ?true:false;
        
        pM = MathTool.sqrt(Place.numPlaces()); // number of row-wise places
        pN = Place.numPlaces()/pM;             // number of column-wise places
        
    }
    
    public def run (): void {
        exampleMult();
        exampleMultTrans();
        exampleSparseMult();
        exampleCylicDistMult();
        exampleCylicDistMultTrans();
        exampleRandomDistMult();
        exampleRandomDistMultTrans();
    }
    
    public def exampleMult():Boolean {
        Console.OUT.println("Starting SUMMA on multiply dense block Matrix example");
        //Matrix partitioning
        val gA = new Grid(M, K, bM, bN);
        val gB = new Grid(K, N, bM, bN);
        val gC = new Grid(M, N, bM, bN);
        //Block distribution
        val dgA = DistGrid.make(gA); 
        val dA = dgA.dmap;
        val dB = (DistGrid.make(gB)).dmap;
        val dC = (DistGrid.make(gC)).dmap;              
        
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           M, K, K, N, bM, bN);
        Console.OUT.printf("distributed in (%dx%d) places\n", dgA.numRowPlaces, dgA.numColPlaces);
        
        // Partition and distribution info is remote-captured in all places
        val a = DistBlockMatrix.makeDense(gA, dA).init((r:Long,c:Long)=>(1.0*(r+c+1)) as ElemType);
        val b = DistBlockMatrix.makeDense(gB, dB).init((r:Long,c:Long)=>(2.0*(r*c+1)) as ElemType);
        val c = DistBlockMatrix.makeDense(gC, dC);
        SummaMult.mult(a, b, c, false);
        Debug.flushln("Done SUMMA mult");
        
        if (! vrf) return true;
        var ret:Boolean = true;
        val da= a.toDense() as DenseMatrix(a.M, a.N);
        val db= b.toDense() as DenseMatrix(a.N, b.N);
        val dc= da % db;
        ret &= dc.equals(c as Matrix(dc.M,dc.N));
        
        if (ret)
            Console.OUT.println("Distributed dense block Matrix SUMMA mult example done!");
        else
            Console.OUT.println("--------Distributed dense block matrix SUMMA mult example failed!--------");
        
        return ret;
    }
    
    public def exampleSparseMult():Boolean {
        Console.OUT.println("Starting SUMMA on multiply sparse block Matrix example");
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           M, K, K, N, bM, bN);
        Console.OUT.printf("distributed in (%dx%d) places\n", pM, pN);
        
        //More efficient in creating distributed block matrix
        val a = DistBlockMatrix.makeSparse(M, K, bM, bN, pM, pN, nzd).init((r:Long,c:Long)=>(1.0*(r+c)) as ElemType);
        val b = DistBlockMatrix.makeSparse(K, N, bM, bN, pM, pN, nzd).init((r:Long,c:Long)=>(1.0*(r+c)) as ElemType);
        val c = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN);
        
        SummaMult.mult(a, b, c, false);
        Debug.flushln("Done SUMMA sparse block mult");
        
        if (! vrf) return true;
        
        var ret:Boolean = true;
        //Console.OUT.println("Summa result:\n" + c);
        val da= a.toDense() as DenseMatrix(a.M, a.N);
        val db= b.toDense() as DenseMatrix(a.N, b.N);
        val dc= da % db;
        //Console.OUT.println("Verified result:\n" + dc);
        ret &= dc.equals(c as Matrix(dc.M,dc.N));
        
        if (ret)
            Console.OUT.println("Distributed sparse block Matrix SUMMA mult passed!");
        else
            Console.OUT.println("--------Distributed sparse block matrix SUMMA mult example failed!--------");
        return ret;
    }
    
    
    public def exampleMultTrans():Boolean {
        //Matrix partitioning
        val gA = new Grid(M, K, bM, bN);
        val gBt = new Grid(N, K, bM, bN);
        val gC = new Grid(M, N, bM, bN);
        //Block distribution
        val gdA = DistGrid.make(gA);
        val dA  = gdA.dmap;
        val dB = (DistGrid.make(gBt)).dmap;
        val dC = (DistGrid.make(gC)).dmap;
        
        Console.OUT.println("Starting SUMMA on multiply-Transpose of dense block Matrix example");
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           gA.M, gA.N, gBt.M, gBt.N, bM, bN);
        Console.OUT.printf("distributed in (%dx%d) places\n", gdA.numRowPlaces, gdA.numColPlaces);
        
        val a = DistBlockMatrix.makeDense(gA, dA).initRandom();
        val b = DistBlockMatrix.makeDense(gBt, dB).initRandom() as DistBlockMatrix{self.N==a.N};
        val c = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(a.M,b.M);
        SummaMultTrans.multTrans(a, b, c, false);
        Debug.flushln("Done SUMMA multTrans");
        
        if (! vrf) return true;
        
        var ret:Boolean = true;
        val da= a.toDense() as DenseMatrix(a.M, a.N);
        val db= b.toDense() as DenseMatrix(b.M, a.N);
        val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
        dc.multTrans(da, db, false);
        ret &= dc.equals(c as Matrix(dc.M,dc.N));
        
        if (ret)
            Console.OUT.println("Distributed dense block Matrix SUMMA multTrabs passed!");
        else
            Console.OUT.println("--------Distributed dense block matrix SUMMA multTrans example failed!--------");
        return ret;
    }
    
    
    public def exampleCylicDistMult():Boolean {
        //Matrix partitioning
        val gA = new Grid(M, K, bM, bN);
        val gB = new Grid(K, N, bM, bN);
        val gC = new Grid(M, N, bM, bN);
        //Block distribution
        val dmap = DistMap.makeCylic(bM*bN, Place.numPlaces());
        
        Console.OUT.println("Starting SUMMA on multiply dense block Matrix example using cylic distribution");
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           M, K, K, N, bM, bN);
        Console.OUT.printf("cylic distribution in %d places\n", Place.numPlaces());
        
        val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
        val b = DistBlockMatrix.makeDense(gB, dmap).initRandom();
        val c = DistBlockMatrix.makeDense(gC, dmap);
        SummaMult.mult(a, b, c, false);
        Debug.flushln("Done SUMMA mult using cylic block distribution");
        
        if (!vrf) return true;
        
        var ret:Boolean = true;
        val da= a.toDense() as DenseMatrix(a.M, a.N);
        val db= b.toDense() as DenseMatrix(a.N, b.N);
        val dc= da % db;
        ret &= dc.equals(c as Matrix(dc.M,dc.N));
        
        if (ret)
            Console.OUT.println("Cylic distribution of dense block Matrix SUMMA mult example passed!");
        else
            Console.OUT.println("--------Cylic distribution of dense block matrix SUMMA mult example failed!--------");
        return ret;
    }
    
    public def exampleCylicDistMultTrans():Boolean {
        //Matrix partitioning
        val gA = new Grid(M, K, bM, bN);
        val gBt = new Grid(N, K, bM, bN);
        val gC = new Grid(M, N, bM, bN);
        //Block distribution
        val dmap = DistMap.makeCylic(bM*bN, Place.numPlaces());
        
        Console.OUT.println("Starting SUMMA on mult-trans dense block Matrix example using cylic distribution");
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           M, K, K, N, bM, bN);
        Console.OUT.printf("cylic distribution in %d places\n", Place.numPlaces());
        
        val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
        val b = DistBlockMatrix.makeDense(gBt, dmap).initRandom() as DistBlockMatrix{self.N==a.N};
        val c = DistBlockMatrix.makeDense(gC, dmap) as DistBlockMatrix(a.M,b.M);
        
        SummaMultTrans.multTrans(a, b, c, false);
        Debug.flushln("Done SUMMA multTrans using cylic block distribution");
        
        if (!vrf) return true;
        
        var ret:Boolean = true;
        val da= a.toDense() as DenseMatrix(a.M, a.N);
        val db= b.toDense() as DenseMatrix(b.M, a.N);
        val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
        dc.multTrans(da, db, false);
        
        if (ret)
            Console.OUT.println("Cylic distribution of dense block Matrix SUMMA mult-trans example passed!");
        else
            Console.OUT.println("--------Cylic distribution of dense block matrix SUMMA mult-trans example failed!--------");
        return ret;
    }
    
    
    public def exampleRandomDistMult():Boolean {
        //Matrix partitioning
        val gA = new Grid(M, K, bM, bN);
        val gB = new Grid(K, N, bM, bN);
        val gC = new Grid(M, N, bM, bN);
        //Block distribution
        val dmap = DistMap.makeRandom(bM*bN, Place.numPlaces());
        
        Console.OUT.println("Starting SUMMA on mult dense block Matrix example using random distribution");
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           M, K, K, N, bM, bN);
        Console.OUT.printf("randomly distributed in %d places\n", Place.numPlaces());
        
        val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
        val b = DistBlockMatrix.makeDense(gB, dmap).initRandom() as DistBlockMatrix(a.N);
        val c = DistBlockMatrix.makeDense(gC, dmap) as DistBlockMatrix(a.M,b.N);
        SummaMult.mult(a, b, c, false);
        Debug.flushln("Done SUMMA mult using random block distribution");
        
        if (!vrf) return true;
        
        var ret:Boolean = true;
        val da= a.toDense() as DenseMatrix(a.M, a.N);
        val db= b.toDense() as DenseMatrix(b.M, b.N);
        val dc= DenseMatrix.make(da.M, db.N) as DenseMatrix(a.M,b.N);
        dc.mult(da, db, false);
        
        if (ret)
            Console.OUT.println("Random distribution of dense block Matrix SUMMA mult example passed!");
        else
            Console.OUT.println("--------Random distribution of dense block matrix SUMMA mult example failed!--------");
        return ret;
    }
    
    public def exampleRandomDistMultTrans():Boolean {
        //Matrix partitioning
        val gA = new Grid(M, K, bM, bN);
        val gBt = new Grid(N, K, bM, bN);
        val gC = new Grid(M, N, bM, bN);
        //Block distribution
        val dmap = DistMap.makeRandom(bM*bN, Place.numPlaces());
        
        Console.OUT.println("Starting SUMMA on mult-trans dense block Matrix example using random distribution");
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           M, K, K, N, bM, bN);
        Console.OUT.printf("randomly distributed in %d places\n", Place.numPlaces());
        
        val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
        val b = DistBlockMatrix.makeDense(gBt, dmap).initRandom() as DistBlockMatrix{self.N==a.N};
        val c = DistBlockMatrix.makeDense(gC, dmap) as DistBlockMatrix(a.M,b.M);
        SummaMultTrans.multTrans(a, b, c, false);
        Debug.flushln("Done SUMMA multTrans using random block distribution");
        
        if (!vrf) return true;
        var ret:Boolean = true;
        val da= a.toDense() as DenseMatrix(a.M, a.N);
        val db= b.toDense() as DenseMatrix(b.M, a.N);
        val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
        dc.multTrans(da, db, false);
        
        if (ret)
            Console.OUT.println("Random distribution of dense block Matrix SUMMA mult-trans example passed!");
        else
            Console.OUT.println("--------Random distribution of dense block matrix SUMMA mult-trans example failed!--------");
        return ret;
        }
} 
