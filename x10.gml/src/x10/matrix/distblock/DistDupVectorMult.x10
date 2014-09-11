/*
 * 
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.matrix.distblock;

import x10.regionarray.Dist;
import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

public class DistDupVectorMult  { 

    public static def comp(mA:DistBlockMatrix, vB:DistVector(mA.N), vC:DupVector(mA.M), plus:Boolean):DupVector(vC) {
        Debug.assure(mA.isDistHorizontal(),    "First dist block matrix must have horizontal distribution");        
        val stt = Timer.milliTime();
        val places = mA.getPlaces();
        var offb:Long = 0;
        val rootpid = here.id();
        finish for (var p:Long=0; p<places.size(); offb+=vB.segSize(p), p++) {
            val pid = p;
            val offsetB = offb;
            at(places(pid)) async {
                //Debug.flushln("stating offset of B at for place "+here.id()+" is "+offsetB);
                if (here.id() != rootpid || plus == false) vC.local().reset();
                BlockVectorMult.comp(mA.handleBS(), vB.distV(), offsetB, vC.local(), 0, true);
            }
        }
        vC.calcTime += Timer.milliTime() - stt;
        //vC.printAllCopies();
        vC.reduceSum();
        return vC;
    }
    
    public static def comp(mA:DistBlockMatrix, vB:DupVector(mA.N), vC:DistVector(mA.M), plus:Boolean):DistVector(vC) {
        Debug.assure(mA.isDistVertical(), "First dist block matrix must have vertical distribution");
        val stt = Timer.milliTime();        
        val places = mA.getPlaces();        
        var offc:Long = 0;
        finish for (var p:Long=0; p<places.size(); offc+=vC.segSize(p), p++) {
            val pid = p;
            val offsetC = offc;
            //Debug.flushln("stating offset of C at for place "+here.id()+" is "+offsetC);
            at(places(pid)) async {
                BlockVectorMult.comp(mA.handleBS(), vB.local(), 0, vC.distV(), offsetC, plus);
            }
        }
        vC.calcTime += Timer.milliTime() - stt;

        return vC;                
    }

    public static def comp(vB:DistVector, mA:DistBlockMatrix(vB.M), vC:DupVector(mA.N), plus:Boolean):DupVector(vC) {
        Debug.assure(mA.isDistVertical(),
                "Second operand dist block matrix must have vertical distribution");        
        val stt = Timer.milliTime();
        val places = mA.getPlaces();
        var offb:Long=0;
        val rootpid = here.id();
        finish for (var p:Long=0; p<places.size(); offb+=vB.segSize(p), p++) {
            val pid = p;
            val offsetB = offb;
            at(places(pid)) async {
                if (here.id() != rootpid || plus == false) vC.local().reset();

                BlockVectorMult.comp(vB.distV(), offsetB, mA.handleBS(), vC.local(), 0, true);
            }
        }
        vC.calcTime += Timer.milliTime() - stt;        
        vC.reduceSum();
        return vC;
    }
    
    public static def comp(vB:DupVector, mA:DistBlockMatrix(vB.M), vC:DistVector(mA.N), plus:Boolean):DistVector(vC) {
        Debug.assure(mA.isDistHorizontal(),
                "Second operand dist block matrix must have horizontal distribution");
        val stt = Timer.milliTime();
        val places = mA.getPlaces();
        var offc:Long = 0;
        finish for (var p:Long=0; p<places.size(); offc+=vC.segSize(p), p++) {
            val pid = p;
            val offsetC = offc;
            at(places(pid)) async {
                //Debug.flushln("Vector * BlockSet = vector: offset"+ offsetC);
                BlockVectorMult.comp(vB.local(), 0, mA.handleBS(), vC.distV(), offsetC, plus);
            }
        }
        vC.calcTime += Timer.milliTime() - stt;

        return vC;                
    }
    
    public static def comp(mA:DistBlockMatrix, vB:DupVector(mA.N), vC:DupVector(mA.M), plus:Boolean):DupVector(vC) {
        val places = mA.getPlaces();
        //Bcast vector to all places. 
        vB.sync();

        var stt:Long = Timer.milliTime();
        //Make copy of output local copy
        if (plus) {
            val tmpc = vC.local().clone();
            finish ateach(Dist.makeUnique(places)) {
                vC.local().reset();
                BlockVectorMult.comp(mA.handleBS(), vB.local(), vC.local(),  true);
            }
            vC.calcTime += Timer.milliTime() - stt;
            vC.reduceSum();

            stt = Timer.milliTime();
            val vc = vC.local();
            if (plus) vc.cellAdd(tmpc as Vector(vc.M));
            vC.calcTime += Timer.milliTime() - stt;
        } else {
            finish ateach(Dist.makeUnique(places)) {
                vC.local().reset();
                BlockVectorMult.comp(mA.handleBS(), vB.local(), vC.local(),  true);
            }
            vC.calcTime += Timer.milliTime() - stt;
            vC.reduceSum();
        }
        return vC;
    }
    
    public static def comp(vB:DupVector, mA:DistBlockMatrix(vB.M), vC:DupVector(mA.N), plus:Boolean):DupVector(vC) {
        val places = mA.getPlaces();
        vB.sync();

        var stt:Long = Timer.milliTime();
        //Make copy of output local copy
        if (plus) {
            val tmpc = vC.local().clone();
        
            finish ateach(Dist.makeUnique(places)) {
                vC.local().reset();
                BlockVectorMult.comp(vB.local(), mA.handleBS(), vC.local(), plus);
            }
            vC.calcTime += Timer.milliTime() - stt;
            vC.reduceSum();

            stt = Timer.milliTime();
            val vc = vC.local();
            vc.cellAdd(tmpc as Vector(vc.M));
            vC.calcTime += Timer.milliTime() - stt;
        } else {
            finish ateach(Dist.makeUnique(places)) {
                vC.local().reset();
                BlockVectorMult.comp(vB.local(), mA.handleBS(), vC.local(), plus);
            }
            vC.calcTime += Timer.milliTime() - stt;
            vC.reduceSum();    
        }
        return vC;
    }
}
