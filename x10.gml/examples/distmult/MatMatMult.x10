/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistDupMult;
import x10.matrix.distblock.DistDistMult;
import x10.matrix.distblock.summa.SummaMult;

/**
 * Examples of distributed block matrix
 */
public class MatMatMult {
    
    public static def main(args:Rail[String]) {
        val testcase = new MatMatMult(args);
        testcase.run();
    }
    public val nzp:Float;
    public val M:Long;
    public val N:Long;
    public val K:Long;
    public val bM:Long;
    public val bK:Long;
    public val bN:Long;
    public val vrf:Boolean;
    
    public def this(args:Rail[String]) {
        M = args.size > 0 ? Long.parse(args(0)):30;
        N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
        K = args.size > 2 ? Long.parse(args(2)):(M as Int)+2;
        bM= args.size > 3 ? Long.parse(args(3)):(Place.numPlaces() as Int)+1;
        bK= args.size > 4 ? Long.parse(args(4)):(bM as Int)+1;
        bN= args.size > 5 ? Long.parse(args(5)):(Place.numPlaces() as Int)+15;
        nzp = args.size > 6 ?Float.parse(args(6)):0.9f;
        vrf = args.size > 7 ?false:true;
        
        Console.OUT.printf("Matrix dimensions M:%d K:%d N:%d, blocking:(%d, %d) \n", M, N, K, bM, bN);
    }
    
    public def run ():void {
        var ret:Boolean = true;
        ret &= (demoDistDupMult());
        ret &= (demoDistDistMultToDup());
        ret &= (demoDistDistMultToDup2());
        ret &= (demoDistDistSUMMA());
    }
    
    public def demoDistDupMult():Boolean{
        Console.OUT.println("Starting Dist-Dup block matrix multiply. Dist matrix must have vertical distribution");
        val pM = Place.numPlaces()      ;
        val pN = 1;
        val A = DistBlockMatrix.makeDense(M, K, bM, bK, pM, pN).initRandom();
        val B = DupBlockMatrix.makeDense(K, N, bK, bN).initRandom();
        val C = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN);
        
        DistDupMult.comp(A, B, C, false);
        
        if (vrf) {
            val c = A.toDense() % B.toDense();
            if (c.equals(C as Matrix(C.M, C.N))) 
                Console.OUT.println("Dist-Dup Block matrix multiply verified");
            else {
                Console.OUT.println("--------ERROR: Dist-Dup Block matrix multiply!--------");
                return false;
            }
        }
        return true;
    }
    
    public def demoDistDistMultToDup():Boolean{
        Console.OUT.println("Starting DistMatrix-DistMatrix multiply.");
        Console.OUT.println("First DistMatrix has horizontal and second has vertical distribution");
        
        val A = DistBlockMatrix.makeDense(M, K, bM, bK, 1, Place.numPlaces()).initRandom();
        val B = DistBlockMatrix.makeDense(K, N, bK, bN, Place.numPlaces(),1).initRandom();
        val C = DupBlockMatrix.makeDense(M, N, bM, bN);
        
        DistDistMult.mult(A, B, C, false);
        
        if (vrf) {
            val c = A.toDense() % B.toDense();
            if (c.equals(C))
                Console.OUT.println("Dist-Dist Block matrix multiply verified");
            else {
                Console.OUT.println("--------ERROR Dist Block matrix multiply!--------");
                return false;
            }
        }
        return true;
    }
    
    public def demoDistDistMultToDup2():Boolean{
        Console.OUT.println("Starting DistMatrix-DistMatrix multiply demo 2.");
        Console.OUT.println("First DistMatrix has horizontal and second has vertical distribution");
        
        //val gPartA = new Grid(M, K, bM, bK); may not be balanced in row-wise partitioning
        val gPartA = DistGrid.makeGrid(M, K, bM, bK, 1, Place.numPlaces());
        val gDistA = DistGrid.makeHorizontal(gPartA);
        val A = DistBlockMatrix.makeDense(gPartA, gDistA.dmap).initRandom() as DistBlockMatrix(M,K);
        
        //val gPartB = new Grid(K, N, bK, bN); may not be balanced in column-wise partitioning
        val gPartB = DistGrid.makeGrid(K, N, bK, bN, Place.numPlaces(), 1);
        val gDistB = DistGrid.makeVertical(gPartB);
        val B = DistBlockMatrix.makeDense(gPartB, gDistB).initRandom() as DistBlockMatrix(K,N);
        
        val C = DupBlockMatrix.makeDense(M, N, bM, bN);
        
        DistDistMult.mult(A, B, C, false);
        
        val A1 = DistBlockMatrix.makeDense(gPartA, gDistA);
        
        if (vrf) {
            val c = A.toDense() % B.toDense();
            if (c.equals(C))
                Console.OUT.println("Dist-Dist Block matrix multiply demo 2 verified");
            else {
                Console.OUT.println("--------ERROR Dist Block matrix multiply demo 2!--------");
                return false;
            }
        }
        return true;
    }
    
    public def demoDistDistSUMMA():Boolean {
        Console.OUT.println("Demo of using SUMMA for DistMatrix-DistMatrix multiplication");
        val pM = MathTool.sqrt(Place.numPlaces());
        val pN = Place.numPlaces()/pM;
        
        Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
                           M, K, K, N, bM, bN);
        
        var ret:Boolean = true;
        val A = DistBlockMatrix.makeDense(M, K, bM, bK, pM, pN).initRandom();
        val B = DistBlockMatrix.makeSparse(K, N, bK, bN, pM, pN, nzp).initRandom();
        val C = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN);
        
        Console.OUT.printf("Starting SUMMA DistBlockMatrix * DistBlockMatrix\n");
        SummaMult.mult(A, B, C, false);
        
        if (vrf) {
            Console.OUT.printf("Start verification\n");
            
            val c= A.toDense() % B.toDense();
            if (c.equals(C))
                Console.OUT.println("Distributed block matrix SUMMA multiplication verified");
            else {
                Console.OUT.println("--------ERROR: Distributed sparse block matrix SUMMA mult test failed!--------");
                return false;
            }
        }
        
        return true;
    }
    
} 
