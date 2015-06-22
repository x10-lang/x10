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

import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.util.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.ElemType;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistDupVectorMult;

public class TestDistDupVectorMult extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val M:Long;
    public val N:Long;
    public val bM:Long;
    public val bN:Long;
    public val nzd:Float;
    
    public def this(args:Rail[String]) {
        M = args.size > 0 ? Long.parse(args(0)):20;
        N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
        bM = args.size > 2 ? Long.parse(args(2)):4;
        bN = args.size > 3 ? Long.parse(args(3)):5;
        nzd =  args.size > 6 ?Float.parse(args(6)):0.99f;
    }
    
    public def run():Boolean {
        Console.OUT.println("Dist-Dup block matrix vector multiply tests");
        
        var ret:Boolean = true;
        @Ifndef("MPI_COMMU") { // TODO Deadlocks!
            ret &= (testDistMatDistVecMult());
            ret &= (testDistVecDistMatMult());
            ret &= (testDistMatDupVecMult());
            ret &= (testDupVecDistMatMult());
            ret &= (testDistDupDupMult());
            ret &= (testDupDistDupMult());
        }
        return ret;
    }
    
    public def testDistMatDistVecMult():Boolean{
        Console.OUT.println("DistBlockMatrix * DistVector -> DupVector multiply test");
        val pM = 1, pN=Place.numPlaces(); //Horizontal distribution
        val mA = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN) as DistBlockMatrix(M,N);
        // Better: val vB = DistVector.make(N, mA.getAggColBs());
        val vB = DistVector.make(N, pN);
        val vC = DupVector.make(M);
        
        mA.init((r:Long,c:Long)=>ET(1.0+c));
        vB.init((r:Long)=>ET(1.0));
        
        DistDupVectorMult.comp(mA, vB, vC, false);
        Debug.flushln("Here out");
        val dA = mA.toDense() as DenseMatrix(M,N);
        val vb = vB.toVector() as Vector(N);
        val vc = dA % vb;
        if ( vc.equals(vC.local() as Vector(vc.M)))
            Console.OUT.println("DistBlockMatrix * DistVector multiply test passed!");
        else {
            Console.OUT.println("--------DistBlockMatrix * DistVector multiply test failed!--------");
            return false;
        }
        return true;
    }
    
    public def testDistVecDistMatMult() {
        Console.OUT.println("DistVector * DistBlockMatrix -> DupVector multiply test");
        val pM = Place.numPlaces(), pN= 1;//Vertical distribution
        val mB = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN) as DistBlockMatrix(M,N);
        val vA = DistVector.make(M, pM);
        vA.initRandom();
        mB.initRandom();
        val vC = vA % mB;
        val vc = vA.toVector() % mB.toDense();
        
        if (vc.equals(vC.local() as Vector(vc.M)))
            Console.OUT.println("DistVector * DistBlockMatrix  multiply test passed!");
        else {
            Console.OUT.println("--------DistVector * DistBlockMatrix multiply test failed!--------");
            return false;
        }
        return true;
    }
    
    public def testDistMatDupVecMult():Boolean{
        Console.OUT.println("DistBlockMatrix * DupVector -> DistVector multiply test");
        val pM = Place.numPlaces(), pN= 1;//Vertical distribution               
        val mA = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN) as DistBlockMatrix(M,N);
        val vB = DupVector.make(N);
        val vC = DistVector.make(M, pM); //Be careful, keep partition same
        //Better: val vC = DistVector.make(M, DistGrid.getAggRowBs(M, mA.getGrid(), mA.getMap()));
        //Better: val vC = DistVector.make(M, mA.getAggRowBs());
        mA.initRandom();
        vB.initRandom();
        
        DistDupVectorMult.comp(mA, vB, vC, false);
        
        val vc = mA.toDense() % vB.local();
        
        if ( vC.equals(vc as Vector(vC.M)))
            Console.OUT.println("DistBlockMatrix * DupVector multiply test passed!");
        else {
            Console.OUT.println("--------DistBlockMatrix * DupVector multiply test failed!--------");
            return false;
        }
        return true;
    }
    
    public def testDupVecDistMatMult() : Boolean {
        Console.OUT.println("DupVector * DistBlockMatrix -> DistVector multiply test");
        val pM = 1, pN=Place.numPlaces(); //Horizontal distribution
        val vA = DupVector.make(M);
        val mB = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN) as DistBlockMatrix(M,N);
        
        vA.initRandom();//((r:Int)=>1.0+r);
        mB.initRandom();//((r:Long,c:Long)=>1.0+c);
        
        val vC = vA % mB;
        val vc = vA.local() % mB.toDense();
        if ( vC.equals(vc as Vector(vC.M)))                     
            Console.OUT.println("DupVector * DistBlockMatrix multiply test passed!");
        else {
            Console.OUT.println("--------DupVector * DistBlockMatrix multiply test failed!--------");
            return false;
        }
        return true;
    }
    
    public def testDistDupDupMult():Boolean{
        var ret:Boolean = true;
        val pM:Long = MathTool.sqrt(Place.numPlaces());
        val pN:Long = Place.numPlaces() / pM;
        Console.OUT.printf("DistBlockMatrix * DupVector = DupVector multiply test on %d x %d places\n", pM, pN);
        val mA = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN) as DistBlockMatrix(M,N);
        val vB = DupVector.make(N);
        val vC = DupVector.make(M);
        
        mA.initRandom();
        vB.initRandom();
        
        DistDupVectorMult.comp(mA, vB, vC, false);
        
        val dA = mA.toDense() as DenseMatrix(M,N);
        val vb = vB.local() as Vector(N);
        val vc = dA % vb;
        
        ret &= vc.equals(vC.local() as Vector(vc.M));
        
        if (!ret)
            Console.OUT.println("--------DistBlockMatrix * DupVector = DupVector multiply test failed!--------");
        return ret;
    }
    
    public def testDupDistDupMult():Boolean{
        var ret:Boolean = true;
        val pM:Long = MathTool.sqrt(Place.numPlaces());
        val pN:Long = Place.numPlaces() / pM;
        Console.OUT.printf("DupVector * DistBlockMatrix = DupVector multiply test on %d x %d places\n", pM, pN);
        val mB = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN) as DistBlockMatrix(M,N);
        val vA = DupVector.make(M);
        val vC = DupVector.make(N);
        
        vA.initRandom();
        mB.initRandom();
        
        DistDupVectorMult.comp(vA, mB, vC, false);
        
        val dB = mB.toDense() as DenseMatrix(M,N);
        val va = vA.local() as Vector(M);
        val vc = va % dB;
        
        ret &= vc.equals(vC.local());
        
        if (!ret)
            Console.OUT.println("--------DupVector * DistBlockMatrix = DupVector multiply test failed!--------");
        return ret;
    }
    
    public static def main(args:Rail[String]) {
        new TestDistDupVectorMult(args).execute();
    }
}
