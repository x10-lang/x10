/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 *  (C) Copyright Australian National University 2013.
 */

import x10.compiler.Ifndef;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DupMultToDup;
import x10.matrix.dist.DistSparseMatrix;

public class TestDupMult {
    public static def main(args:Rail[String]) {
        val testcase = new RunTest(args);
        testcase.run();
    }

    static class RunTest {
        public val M:Long;
        public val N:Long;
        public val K:Long;

        public def this(args:Rail[String]) {
            M = args.size > 0 ?Int.parse(args(0)):50;
            N = args.size > 1 ?Int.parse(args(1)):(M as Int)+1;
            K = args.size > 2 ?Int.parse(args(2)):(M as Int)+2;
        }

        public def run():Boolean {
            Console.OUT.println("Starting dup dense mult matrix tests in "+Place.numPlaces()+" places");
            Console.OUT.println("Info of matrix sizes: M:"+M+" K:"+K+" N:"+N);

            var ret:Boolean = true;
	    @Ifndef("MPI_COMMU") { // TODO Deadlocks!
            ret &= testDupDup();
            ret &= testDupTransDup();
            ret &= testDupDupTrans();
            ret &= testDupDense();
        }
            if (ret)
                Console.OUT.println("Test passed!");
            else
                Console.OUT.println("----------------Test failed!----------------");
            return ret;
        }

        public def testDupDup():Boolean {
            Console.OUT.println("Starting Dup Dense Matrix dup-dup mult test");
            val a = DupDenseMatrix.makeRand(M,K);
            val b = DupDenseMatrix.makeRand(K,N);
            val c = a % b;

            val da = a.getMatrix();
            val db = b.getMatrix();
            val dc = DenseMatrix.make(M,N);
            DenseMatrixBLAS.comp(da, db, dc);

            var ret:Boolean = c.equals(dc);
            ret &= c.syncCheck();

            if (ret)
                Console.OUT.println("Dup Dense Matrix dup-dup mult test passed!");
            else
                Console.OUT.println("--------Dup Dense Matrix dup-dup mult test failed!--------");
            return ret;
        }

        public def testDupTransDup():Boolean {
            Console.OUT.println("Starting Dup Dense Matrix dup-dup trans mult test");
            val a = DupDenseMatrix.makeRand(K,M);
            val b = DupDenseMatrix.makeRand(K,N);
            val c = DupMultToDup.compTransMult(a, b);

            val da = a.getMatrix();
            val db = b.getMatrix();
            val dc = DenseMatrix.make(M,N);
            DenseMatrixBLAS.compTransMult(da, db, dc);

            var ret:Boolean = c.equals(dc);
            ret &= c.syncCheck();

            if (ret)
                Console.OUT.println("Dup Dense Matrix dup-dup trans mult test passed!");
            else
                Console.OUT.println("--------Dup Dense Matrix dup-dup trans mult test failed!--------");
            return ret;
        }

        public def testDupDupTrans():Boolean {
            Console.OUT.println("Starting Dup Dense Matrix dup-dup mult trans test");
            val a = DupDenseMatrix.makeRand(M,K);
            val b = DupDenseMatrix.makeRand(N,K);
            val c = DupMultToDup.compMultTrans(a, b);

            val da = a.getMatrix();
            val db = b.getMatrix();
            val dc = DenseMatrix.make(M,N);
            DenseMatrixBLAS.compMultTrans(da, db, dc);

            var ret:Boolean = c.equals(dc);
            ret &= c.syncCheck();

            if (ret)
                Console.OUT.println("Dup Dense Matrix dup-dup mult trans test passed!");
            else
                Console.OUT.println("--------Dup Dense Matrix dup-dup mult trans test failed!--------");
            return ret;
        }

        public def testDupDense():Boolean{
            Console.OUT.println("Starting dup dense mult add test");
            val a = DupDenseMatrix.makeRand(M,K);
            val b = DenseMatrix.makeRand(K,N);
            val c = a % b;

            val da = a.getMatrix();
            val dc = DenseMatrix.make(M,N);
            DenseMatrixBLAS.comp(da, b, dc, false);

            var ret:Boolean = c.equals(dc);
            ret &= c.syncCheck();

            if (ret)
                Console.OUT.println("Dup dense Matrix addsub test passed!");
            else
                Console.OUT.println("--------Dup Dense Matrix addsub test failed!--------");
            return ret;
        }
    }
}
