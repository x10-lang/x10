/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 *  (C) Copyright Australian National University 2013.
 */

import harness.x10Test;

import x10.matrix.Matrix;
import x10.matrix.Vector;
import x10.matrix.DenseMatrix;
import x10.matrix.MatrixMultXTen;
import x10.matrix.DenseMultXTen;
import x10.matrix.ElemType;

import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.util.MathTool;
import x10.matrix.util.VerifyTool;

/**
 * This class contains test cases for dense matrix multiplication.
 */
public class TestMultDense extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val M:Long;
    public val N:Long;
    public val K:Long;

    public def this(args:Rail[String]) {
        M = args.size > 0 ?Long.parse(args(0)):50;
        N = args.size > 1 ?Long.parse(args(1)):(M as Int)+1;
        K = args.size > 2 ?Long.parse(args(2)):(M as Int)+2;
    }

    public def run():Boolean {
        var ret:Boolean = true;
        ret &= (testMultAssociative());
        ret &= (testAddMultDist());
        ret &= (testSubMultDist());
        ret &= (testMatrixMultXTen());
        ret &= (testDenseMultXTen());
        ret &= (testDenseMult());
        ret &= (testDenseMultOffset());
        ret &= (testMultDrivers());
        ret &= (testMatMultVector());
        ret &= (testMatMultVectorOffset());
        ret &= (testRankOneUpdate());
        ret &= (testSymRankKUpdate());
        ret &= (testSymRankKUpdateOffset());
        //ret &= (mm.testSmallMult());
        
        return ret;
    }
    
    public def testMultAssociative():Boolean {
        Console.OUT.printf("\nTest Dense matrix multiply associative: %dx%d * %dx%d * %dx%d\n",
                           M, K, K, N, N, M);
        val a = DenseMatrix.make(M, K);
        val b = DenseMatrix.make(K, N);
        val c = DenseMatrix.make(N, M);
        a.initRandom();
        b.initRandom();
        c.initRandom();
        
        val d = (a % b) % c;
        val d1= a % (b % c);
        
        val ret = d1.equals(d);
        if (!ret)
            Console.OUT.println("-----Dense matrix multiply associative test failed!-----");
        return ret;
    }
    
    public def testAddMultDist():Boolean {
        Console.OUT.println("\nTest Dense matrix (a+b)*c = a*c+b*c ");
        val a = DenseMatrix.make(M, K);
        val b = DenseMatrix.make(M, K);
        val c = DenseMatrix.make(K, N);
        a.initRandom(); b.initRandom(); c.initRandom();
        
        val d = (a + b) % c;
        val d1= a % c + b % c;
        val ret = d1.equals(d);
        if (!ret)
            Console.OUT.println("-----Dense matrix (a+b)*c = a*c+b*c test failed!-----");
        return ret;
    }
    
    public def testSubMultDist():Boolean {
        Console.OUT.println("\nTest (a-b)*c = a*c-b*c ");
        val a = DenseMatrix.make(M, K);
        val b = DenseMatrix.make(M, K);
        val c = DenseMatrix.make(K, N);
        a.initRandom(); b.initRandom(); c.initRandom();
        
        val d = (a - b) % c;
        val d1= a % c - b % c;
        
        val ret = d1.equals(d);
        if (!ret)
            Console.OUT.println("-----Dense matrix (a-b)*c = a*c-b*c test failed!-----");
        return ret;
    }
    
    public def testMatrixMultXTen():Boolean {
        Console.OUT.printf("\nTest X10 matrix multiply driver: (%dx%d) * (%dx%d)\n",
                           M, K, K, N);
        val a = DenseMatrix.make(M, K);
        val b = DenseMatrix.make(K, N);
        a.initRandom(); b.initRandom();
        
        val c = MatrixMultXTen.comp(a as Matrix(M,K), b as Matrix(K,N));
        
        val ret = VerifyTool.verifyMatMult(a, b, c);
        if (!ret)
            Console.OUT.println("-----Matrix base X10 multply driver test failed!-----");
        return ret;
    }
    
    public def testDenseMultXTen():Boolean {
        Console.OUT.printf("\nTest X10 dense multiplication driver: (%dx%d) * (%dx%d)\n",
                           M, K, K, N);
        val a:DenseMatrix(M,K) = DenseMatrix.makeRand(M, K);
        val b:DenseMatrix(K,N) = DenseMatrix.makeRand(K, N);
        val c:DenseMatrix(a.M,b.N) = DenseMultXTen.comp(a, b);
        
        val ret = VerifyTool.verifyMatMult(a, b, c);
        
        if (!ret)
            Console.OUT.println("-----Dense matrix X10 multiply driver test failed!-----");
        return ret;
    }
    
    public def testDenseMult():Boolean {
        Console.OUT.printf("\nTest BLAS multiplication driver: (%dx%d) * (%dx%d)\n",
                           M, K, K, N);
        val a = DenseMatrix.make(M, K);
        val b = DenseMatrix.make(K, N);
        a.initRandom(); b.initRandom();
        
        val c = a % b;
        
        val ret = VerifyTool.verifyMatMult(a, b, c);
        if (!ret)
            Console.OUT.println("-----Dense matrix BLAS multiply driver test failed!-----");
        return ret;
    }
    
    public def testDenseMultOffset():Boolean {
        Console.OUT.printf("\nTest X10 dense multiplication with offsets driver: (%dx%d) * (%dx%d)\n",
                           M, K, K, N);
        val a:DenseMatrix(M,K) = DenseMatrix.makeRand(M, K);
        val b:DenseMatrix(K,N) = DenseMatrix.makeRand(K, N);
        val c:DenseMatrix(a.M,b.N) = DenseMultXTen.comp(a, b);
        
        val offsetM = M / 3;
        val offsetN = N / 3;
        
        val partC = new DenseMatrix(M, N);
        DenseMatrixBLAS.comp(ET(1.0), a, b, ET(0.0), partC, [M - offsetM, N - offsetN, K], [offsetM, 0, 0, offsetN, offsetM, offsetN]);
        
        var ret:Boolean = true;
        for (m in offsetM..(M-1)) {
            for (n in offsetN..(N-1)) {
                val same = MathTool.equals(c(m,n), partC(m,n));
                if (!same) {
                    Console.OUT.println("c("+m+","+n+") = " + c(m,n) + " != partC("+m+","+n+") = " + partC(m,n));
                }
                ret &= same;
            }
        }
        
        if (!ret)
            Console.OUT.println("-----Dense matrix X10 multiply with offsets driver test failed!-----");
        return ret;
    }
    
    public def testMultDrivers():Boolean {
        Console.OUT.printf("\nTest X10 Dense, Matrix, BLAS multiply driver: (%dx%d) * (%dx%d)\n",
                           M, K, K, N);
        val a = DenseMatrix.make(M, K);
        val b = DenseMatrix.make(K, N);
        a.initRandom(); b.initRandom();
        
        val c1 = a % b;
        val c2 = DenseMultXTen.comp(a, b);
        val c3 = MatrixMultXTen.comp(a as Matrix(M,K), b as Matrix(K,N));
        
        var ret:Boolean = true;
        if (!c1.equals(c2 as Matrix(c1.M,c1.N))) {
            Console.OUT.println("----- BLAS <> X10 Dense driver -----\n");
            ret = false;
        }
        if (!c1.equals(c3 as Matrix(c1.M,c1.N))) {
            Console.OUT.println("----- BLAS <> X10 Matrix driver -----\n");
            ret = false;
        }
        
        return ret;
    }
    
    public def testMatMultVector():Boolean {
        Console.OUT.printf("\nTest Dense Matrix-Vector multiply: (%dx%d) * (%dx%d)\n",
                           M, K, K, N);
        val a = DenseMatrix.make(M, K).initRandom();
        val v = Vector.make(K).initRandom();
        val c1 = Vector.make(M);
        val c2 = Vector.make(M);
        
        DenseMatrixBLAS.comp(ET(1.0), a, v, ET(0.0), c1);
        DenseMultXTen.comp(a, v, c2, false);
        
        var ret:Boolean = true;
        if (!c1.equals(c2)) {
            Console.OUT.println("----- c = Av : BLAS != X10 Dense driver -----\n");
            ret = false;
        }
        
        val aT = a.T();
        val c3 = Vector.make(M);
        DenseMatrixBLAS.compTransMult(ET(1.0), aT, v, ET(0.0), c3);
        if (!c3.equals(c1)) {
            Console.OUT.println("----- BLAS: (A^T)^Tv != Av -----\n");
            ret = false;
        }
        val c4 = Vector.make(M);
        DenseMultXTen.compTransMult(aT, v, c4, false);
        if (!c4.equals(c1)) {
            Console.OUT.println("----- X10 Dense driver: (A^T)^Tv != Av -----\n");
            ret = false;
        }
        
        DenseMatrixBLAS.comp(ET(1.0), a, v, ET(1.0), c1);
        DenseMultXTen.comp(a, v, c2, true);
        
        if (!c1.equals(c2)) {
            Console.OUT.println("----- c += Av : BLAS != X10 Dense driver -----\n");
            ret = false;
        }
        
        if (!ret)
            Console.OUT.println("-----Dense matrix vector multiply test failed!-----");
        
        return ret;
    }
    
    public def testMatMultVectorOffset():Boolean {
        Console.OUT.printf("\nTest Dense Matrix-Vector multiply with offsets: (%dx%d) * (%dx%d)\n",
                           M, K, K, N);
        val a = DenseMatrix.make(M, K).initRandom();
        val v = Vector.make(K).initRandom();
        val c1 = Vector.make(M);
        val c2 = Vector.make(M);
        
        DenseMatrixBLAS.comp(ET(1.0), a, v, ET(0.0), c1);
        // compare that a single GEMV is equivalent to a series of M 1-row GEMVs
        for (i in 0..(M-1)) {
            DenseMatrixBLAS.comp(ET(1.0), a, v, ET(0.0), c2, [1, K], [i, 0, 0, i]);
        }
        
        var ret:Boolean = true;
        if (!c1.equals(c2)) {
            Console.OUT.println("----- c = Av : M-row GEMV != M x 1-row GEMVs -----\n");
            ret = false;
        }
        
        val aT = a.T();
        val c3 = Vector.make(M);
        for (i in 0..(M-1)) {
            DenseMatrixBLAS.compTransMult(ET(1.0), aT, v, ET(0.0), c3, [K, 1], [0, i, 0, i]);
        }
        if (!c3.equals(c1)) {
            Console.OUT.println("----- BLAS: (A^T)^Tv != Av -----\n");
            ret = false;
        }
        
        DenseMatrixBLAS.comp(ET(1.0), a, v, ET(1.0), c1);
        for (i in 0..(M-1)) {
            DenseMatrixBLAS.comp(ET(1.0), a, v, ET(1.0), c2, [1, K], [i, 0, 0, i]);
        }
        
        if (!c1.equals(c2)) {
            Console.OUT.println("----- c = Av : M-row GEMV != M x 1-row GEMVs -----\n");
            ret = false;
        }
        
        if (!ret)
            Console.OUT.println("-----Dense matrix vector multiply with offset test failed!-----");
        
        return ret;
    }
    
    public def testRankOneUpdate():Boolean {
        Console.OUT.printf("\nTest rank-one update: (%dx%d) += %d * %d\n",
                           M, N, M, N);
        val a = DenseMatrix.make(M, N);
        val x = Vector.make(M).initRandom();
        val y = Vector.make(N).initRandom();
        var ret:Boolean=true;
        DenseMatrixBLAS.rankOneUpdate(x, y, a);
        
        for (i in 0..(M-1)) {
            for (j in 0..(N-1)) {
                chk(a(i,j) == x(i) * y(j));
            }
        }
        
        if (!ret)
            Console.OUT.println("-----Dense matrix rank-one update test failed!-----");
        return ret;
    }
    
    public def testSymRankKUpdate():Boolean {
        Console.OUT.printf("\nTest X10 symmetric rank-K update driver: (%dx%d) * (%dx%d)\n",
                           M, M, M, M);
        val a:DenseMatrix(M,M) = DenseMatrix.makeRand(M, M);
        // force matrix A to be symmetric
        for (m in 0..(M-1)) {
            for (n in 0..(m-1)) {
                a(n,m) = a(m,n);
            }
        }
        val c:DenseMatrix(a.M,a.N) = DenseMultXTen.comp(a, a);
        
        val d = new DenseMatrix(M, M);
        DenseMatrixBLAS.symRankKUpdate(ET(1.0), a, ET(0.0), d, false);
        
        // check lower triangle same for matmul and rank-K update
        var ret:Boolean=true;
        for (m in 0..(M-1)) {
            for (n in 0..(m-1)) {
                val same = MathTool.equals(c(m,n), d(m,n));
                if (!same) {
                    Console.OUT.println("c("+m+","+n+") = " + c(m,n) + " != d("+m+","+n+") = " + d(m,n));
                }
                ret &= same;
            }
        }
        
        if (!ret)
            Console.OUT.println("-----Dense matrix X10 symmetric rank-K update driver test failed!-----");
        return ret;
    }
    
    public def testSymRankKUpdateOffset():Boolean {
        Console.OUT.printf("\nTest X10 symmetric rank-K update with offsets: (%dx%d) * (%dx%d)\n",
                           M, M, M, M);
        val a:DenseMatrix(M,M) = DenseMatrix.makeRand(M, M);
        // force matrix A to be symmetric
        for (m in 0..(M-1)) {
            for (n in 0..(m-1)) {
                a(n,m) = a(m,n);
            }
        }
        val c:DenseMatrix(a.M,a.N) = DenseMultXTen.comp(a, a);
        
        val half = M / 2;
        
        // calculate the top left quadrant of A*A^T, but write it into the bottom left quadrant of C
        val partC = new DenseMatrix(M, M);
        DenseMatrixBLAS.symRankKUpdate(ET(1.0), a, ET(0.0), partC, [M - half, M], [0, 0, half, 0], false);
        
        // check the calculated quadrant is the same for matmul and rank-K update
        var ret:Boolean=true;
        for (m in 0..(half-1)) {
            for (n in 0..(m-1)) {
                val same = MathTool.equals(c(m,n), partC(m+half,n));
                if (!same) {
                    Console.OUT.println("c("+m+","+n+") = " + c(m,n) + " != partC("+(m+half)+","+n+") = " + partC(m+half,n));
                }
                ret &= same;
            }
        }
        
        if (!ret)
            Console.OUT.println("-----Dense matrix X10 symmetric rank-K update with offsets failed!-----");
        return ret;
    }
    
    
    //  public def testSmallMult():Boolean {
    //          Console.OUT.printf("\nTest X10 MM driver on small numbers: (%dx%d) * (%dx%d)\n",
    //                                             M, K, K, N);
    //          val a = DenseMatrix.makeSmall(M, K);
    //          val b = DenseMatrix.makeSmall(K, N);
    //          val c = DenseMatrixBLAS.comp(a, b);
    //          val cd= MatrixMultXTen.comp(a, b);
    
    //          var ret:Boolean = true;
    //          if (! c.testSame(cd)) {
    //                  ret = false;
    //                  Console.OUT.println("-----Test X10 dense multiply driver on small number test failed!-----\n");
    //          }
    
    //          val cm= MatrixMultXTen.comp(a as Matrix, b as Matrix);
    //          if( !c.testSame(cm) ) {
    //                  Console.OUT.println("-----Test X10 matrix multiply driver on small number test failed!-----\n");
    //                  ret = false;
    //          }
    
    //          return ret;
    //  }
    
    public static def main(args:Rail[String]) {
        new TestMultDense(args).execute();
    }
}
