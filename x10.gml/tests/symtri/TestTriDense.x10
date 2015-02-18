/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import harness.x10Test;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.TriDense;
import x10.matrix.ElemType;

/**
   This class contains test cases for triangular dense matrix addition, scaling, and negation operations.
 */
public class TestTriDense extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val M:Long;
    /** Each test must be run for both lower- and upper-triangular matrices */
    public var upper:Boolean = false;
    
    public def this(m:Long) {
        M = m;
    }
    
    public def run():Boolean {
        Console.OUT.println("Triangular matrix clone/add/sub/scaling tests on "+
                            M+"x"+ M + " matrices");
        
        val runAllTests = () => {
            Console.OUT.println("Testing for upper = " + upper);
            var ret:Boolean = true;
            ret &= (testClone());
            ret &= (testInit());
            ret &= (testScale());
            ret &= (testAdd());
            ret &= (testAddSub());
            ret &= (testAddAssociative());
            ret &= (testScaleAdd());
            ret &= (testCellMult());
            ret &= (testCellDiv());
            ret &= (testMult());
            ret &= (testSolve());
            ret
        };
        upper = false; val passedLower = runAllTests();
        upper = true;  val passedUpper = runAllTests();
        
        return (passedLower && passedUpper);
    }
    
    
    public def testClone():Boolean{
        Console.OUT.println("Triangular Matrix clone test");
        val dm = TriDense.make(upper, M).init(ET(1.0));
        
        val dm1 = dm.clone();
        var ret:Boolean = dm.equals(dm1);
        
        if (!ret)
            Console.OUT.println("--------Triangular Matrix Clone test failed!--------");
        val dmm = dm.toDense();
        ret &= dmm.equals(dm as Matrix(dmm.M, dmm.N));
        if (!ret)
            Console.OUT.println("--------Triangular Matrix toDense test failed!--------");
        
        
        dm(1, 1) = dm(2,2) = ET(10.0);
        
        if ((dm(1,1)==dm(2,2)) && (dm(1,1)==ET(10.0))) {
            ret &= true;
        } else {
            ret &= false;
            Console.OUT.println("---------- Triangular Matrix chain assignment test failed!-------");
        }
        
        return ret;
    }
    
    public def testInit():Boolean {
        Console.OUT.println("Triangular Matrix initialization test");
        var ret:Boolean = true;
        val sym = TriDense.make(upper, M).init((r:Long, c:Long)=>ET(1.0+10*r+c));
        
        if (upper) {
            for (var c:Long=0; c<M; c++)
                for (var r:Long=0; r<=c; r++)
                    ret &= (sym(r,c) == ET(1.0+10*r+c));
        } else {
            for (var c:Long=0; c<M; c++)
                for (var r:Long=c; r<M; r++)
                    ret &= (sym(r,c) == ET(1.0+10*r+c));
        }
        
        if (!ret)
            Console.OUT.println("--------Triangular matrix initialization func failed!--------");       
        return ret;
    }
    
    public def testScale():Boolean{
        Console.OUT.println("Triangular matrix scaling test");
        val dm  = TriDense.make(upper, M).initRandom();
        val dm1 = dm * ET(2.5);
        
        dm1.scale(ET(1.0/2.5));
        val ret = dm.equals(dm1);
        if (!ret)
            Console.OUT.println("--------Triangular matrix Scaling test failed!--------");      
        return ret;
    }
    
    public def testAdd():Boolean {
        Console.OUT.println("Triangular matrix addition test");
        val dm:TriDense(M)  = TriDense.make(upper, M).initRandom();
        val dm1:TriDense(M) = ET(-1.0) * dm;
        val dm0:DenseMatrix = dm + dm1;
        var ret:Boolean = dm0.equals(ET(0.0));
        
        if (!ret)
            Console.OUT.println("--------Triangular addition: dm + dm*-1 test failed--------");
        
        return ret;
    }
    
    public def testAddSub():Boolean {
        Console.OUT.println("Triangular matrix add-sub test");
        val dm = TriDense.make(upper, M).initRandom();
        val dm1= TriDense.make(upper, M).initRandom();
        val dm2 = dm + dm1;
        //
        val dm_c  = dm2 - dm1;
        val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
        if (!ret)
            Console.OUT.println("--------Triangular matrix Add-sub test failed!--------");
        
        return ret;
    }
    
    public def testAddAssociative():Boolean {
        Console.OUT.println("Triangular matrix associative test");
        
        val a = TriDense.make(M).initRandom();
        val b = TriDense.make(M).initRandom();
        val c = TriDense.make(M).initRandom();
        val c1 = a + b + c;
        val c2 = a + (b + c);
        val ret = c1.equals(c2);
        if (!ret)
            Console.OUT.println("--------Triangular add associative test failed!--------");
        return ret;
    }
    
    public def testScaleAdd():Boolean {
        Console.OUT.println("Triangular Matrix scaling-add test");
        
        val a = TriDense.make(upper, M).initRandom();
        val b = TriDense.make(upper, M).initRandom();
        val a1= a * ET(0.2);
        val a2= a * ET(0.8);
        val ret = a.equals(a1+a2);
        if (!ret)
            Console.OUT.println("--------Triangular matrix scaling-add test failed!--------");
        return ret;
    }
    
    public def testCellMult():Boolean {
        Console.OUT.println("Triangular Matrix cellwise mult test");
        
        val a = TriDense.make(M).init(ET(1.0));
        val b = TriDense.make(M).init(ET(2.0));
        val c = (a + b) * a;
        val aa= a*a;
        val ba= b*a;
        
        val d = a * a + b * a;
        var ret:Boolean = c.equals(d);
        
        //              val da = a.toDense();
        //              val db = b.toDense();
        //              val dd = (a+b) * da;
        // 
        //              ret &= dd.equals(d);
        
        if (!ret)
            Console.OUT.println("--------Triangular matrix cellwise mult test failed!--------");
        return ret;
    }
    
    public def testCellDiv():Boolean {
        Console.OUT.println("Triangular Matrix cellwise mult-div test");
        
        val a = TriDense.make(M).initRandom();
        val b = TriDense.make(M).initRandom();
        val c = (a + b) * a;
        val d =  c / (a + b);
        val ret = d.equals(a);
        if (!ret)
            Console.OUT.println("--------Triangular matrix cellwise mult-div test failed!--------");
        return ret;
    }
    
    public def testMult():Boolean {
        var ret:Boolean = true;
        Console.OUT.println("Triangular-matrix multiply test");
        val a:TriDense(M)     = TriDense.make(M).init(1);//Random();
        val b:DenseMatrix(M,M) = DenseMatrix.makeRand(M,M);
        val ad:DenseMatrix(M,M) = a.toDense();
        val c = a % b;
        val d = ad % b;
        ret= d.equals(c as Matrix(M,M));
        
        val e = b % a;
        val ed= b % ad;
        ret &= e.equals(ed);
        
        if (!ret)
            Console.OUT.println("--------Triangular-matrix multiply test failed!--------");
        return ret;             
        
    }
    public def testSolve():Boolean {
        var ret:Boolean = true;
        Console.OUT.println("Matrix-triangular solver test");
        val X:DenseMatrix(M,M) = DenseMatrix.make(M,M).initRandom();
        val A:TriDense(M)    = TriDense.make(M).initRandom();
        val B:DenseMatrix(M,M) = A % X;
        
        A.solveSelfMultMat(B);// A % X = B  
        ret &= X.equals(B);
        
        val C:DenseMatrix(M,M) = X % A;
        A.solveMatMultSelf(C);
        ret &= X.equals(C);
        
        if (!ret)
            Console.OUT.println("--------Matrix-Triangular solver test failed!--------");
        return ret;             
    }
    
    public static def main(args:Rail[String]) {
        val m = (args.size > 0) ? Long.parse(args(0)):5;
        new TestTriDense(m).execute();
    }
}
