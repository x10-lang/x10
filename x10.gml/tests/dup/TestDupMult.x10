/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2013.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DupMultToDup;

public class TestDupMult extends x10Test {
    public val M:Long;
    public val N:Long;
    public val K:Long;

    public def this(args:Rail[String]) {
        M = args.size > 0 ? Long.parse(args(0)):50;
        N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
        K = args.size > 2 ? Long.parse(args(2)):(M as Int)+2;
    }

    public def run():Boolean {
        Console.OUT.println("Dup dense mult matrix tests in "+Place.numPlaces()+" places");

        var ret:Boolean = true;
    @Ifndef("MPI_COMMU") { // TODO Deadlocks!
        ret &= testDupDup();
        ret &= testDupTransDup();
        ret &= testDupDupTrans();
        ret &= testDupDense();
    }
        if (!ret)
            Console.OUT.println("--------Dup dense mult matrix test failed!--------");
        return ret;
    }

    public def testDupDup():Boolean {
        Console.OUT.println("Dup Dense Matrix dup-dup mult test");
        val a = DupDenseMatrix.makeRand(M,K);
        val b = DupDenseMatrix.makeRand(K,N);
        val c = a % b;

        val da = a.getMatrix();
        val db = b.getMatrix();
        val dc = DenseMatrix.make(M,N);
        DenseMatrixBLAS.comp(1.0, da, db, 0.0, dc);

        var ret:Boolean = c.equals(dc);
        ret &= c.syncCheck();

        if (!ret)
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
        DenseMatrixBLAS.compTransMult(1.0, da, db, 0.0, dc);

        var ret:Boolean = c.equals(dc);
        ret &= c.syncCheck();

        if (!ret)
            Console.OUT.println("--------Dup Dense Matrix dup-dup trans mult test failed!--------");
        return ret;
    }

    public def testDupDupTrans():Boolean {
        Console.OUT.println("Dup Dense Matrix dup-dup mult trans test");
        val a = DupDenseMatrix.makeRand(M,K);
        val b = DupDenseMatrix.makeRand(N,K);
        val c = DupMultToDup.compMultTrans(a, b);

        val da = a.getMatrix();
        val db = b.getMatrix();
        val dc = DenseMatrix.make(M,N);
        DenseMatrixBLAS.compMultTrans(1.0, da, db, 0.0, dc);

        var ret:Boolean = c.equals(dc);
        ret &= c.syncCheck();

        if (!ret)
            Console.OUT.println("--------Dup Dense Matrix dup-dup mult trans test failed!--------");
        return ret;
    }

    public def testDupDense():Boolean{
        Console.OUT.println("Dup dense mult add test");
        val a = DupDenseMatrix.makeRand(M,K);
        val b = DenseMatrix.makeRand(K,N);
        val c = a % b;

        val da = a.getMatrix();
        val dc = DenseMatrix.make(M,N);
        DenseMatrixBLAS.comp(1.0, da, b, 0.0, dc);

        var ret:Boolean = c.equals(dc);
        ret &= c.syncCheck();

        if (!ret)
            Console.OUT.println("--------Dup Dense Matrix mult add test failed!--------");
        return ret;
    }

    public static def main(args:Rail[String]) {
        new TestDupMult(args).execute();
    }
}
