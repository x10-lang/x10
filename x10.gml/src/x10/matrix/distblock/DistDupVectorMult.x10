/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.distblock;

import x10.regionarray.Dist;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.util.Team;
import x10.matrix.ElemType;

public class DistDupVectorMult  {

    public static def comp(mA:DistBlockMatrix, vB:DistVector(mA.N), vC:DupVector(mA.M), plus:Boolean):DupVector(vC) {
        assert (mA.isDistHorizontal()) :
            "First dist block matrix must have horizontal distribution";        
        val stt = Timer.milliTime();
        val places = mA.places();
        var offb:Long = 0;
        val rootpid = here.id();
        val vBsegSize =  vB.getSegSize();
        finish for (var p:Long=0; p<places.size(); offb+=vBsegSize(p), p++) {
            val offsetB = offb;
            at(places(p)) async {
                if (here.id() != rootpid || plus == false) vC.local().reset();
                BlockVectorMult.comp(mA.handleBS(), vB.distV().vec, offsetB, vC.local(), 0, true);
                
                val src = vC.dupV().vec.d;
                val dst = vC.dupV().vec.d;                
                vC.team.allreduce(src, 0, dst, 0,vC.M, Team.ADD);
            }
        }
        vC.calcTime += Timer.milliTime() - stt;
        return vC;
    }
    
    public static def comp(mA:DistBlockMatrix, vB:DupVector(mA.N), vC:DistVector(mA.M), plus:Boolean):DistVector(vC) {
        assert (mA.isDistVertical()) :
            "First dist block matrix must have vertical distribution";
        val stt = Timer.milliTime();        
        val places = mA.places();        
        var offc:Long = 0;
        val vCsegSize =  vC.getSegSize();
        finish for (var p:Long=0; p<places.size(); offc+=vCsegSize(p), p++) {
            val offsetC = offc;
            at(places(p)) async {
                BlockVectorMult.comp(mA.handleBS(), vB.local(), 0, vC.distV().vec, offsetC, plus);
            }
        }
        vC.calcTime += Timer.milliTime() - stt;

        return vC;                
    }

    public static def comp_local(mA:DistBlockMatrix, vB:DupVector(mA.N), vC:DistVector(mA.M), plus:Boolean):DistVector(vC) {        
        BlockVectorMult.comp(mA.handleBS(), vB.local(), 0, vC.distV().vec, vC.getOffset(), plus);        
        return vC;
    }
    
    public static def comp(vB:DistVector, mA:DistBlockMatrix(vB.M), vC:DupVector(mA.N), plus:Boolean):DupVector(vC) {
        assert (mA.isDistVertical()) :
            "Second operand dist block matrix must have vertical distribution";        
        val stt = Timer.milliTime();
        val places = mA.places();
        var offb:Long=0;
        val rootpid = here.id();
        val vBsegSize =  vB.getSegSize();
        finish for (var p:Long=0; p<places.size(); offb+=vBsegSize(p), p++) {
            val offsetB = offb;
            at(places(p)) async {
                if (here.id() != rootpid || plus == false) vC.local().reset();

                BlockVectorMult.comp(vB.distV().vec, offsetB, mA.handleBS(), vC.local(), 0, true);
                
                val src = vC.dupV().vec.d;
                val dst = vC.dupV().vec.d;                
                vC.team.allreduce(src, 0, dst, 0, vC.M, Team.ADD);                
            }
        }
        vC.calcTime += Timer.milliTime() - stt;
        return vC;
    }
    
    public static def comp_local(root:Place, vB:DistVector, mA:DistBlockMatrix(vB.M), vC:DupVector(mA.N), plus:Boolean):DupVector(vC) {
        assert (mA.isDistVertical()) :
            "Second operand dist block matrix must have vertical distribution";        
        
        val rootpid = root.id();
        val offsetB = vB.getOffset();        
        if (here.id() != rootpid || plus == false) vC.local().reset();
        
        BlockVectorMult.comp(vB.distV().vec, offsetB, mA.handleBS(), vC.local(), 0, true);
        
        val src = vC.dupV().vec.d;
        val dst = vC.dupV().vec.d;        
        vC.team.allreduce(src, 0, dst, 0, vC.M, Team.ADD);            
        return vC;
    }
    
    public static def comp(vB:DupVector, mA:DistBlockMatrix(vB.M), vC:DistVector(mA.N), plus:Boolean):DistVector(vC) {
        assert (mA.isDistHorizontal()) :
            "Second operand dist block matrix must have horizontal distribution";
        val stt = Timer.milliTime();
        val places = mA.places();
        var offc:Long = 0;
        val vCsegSize =  vC.getSegSize();
        finish for (var p:Long=0; p<places.size(); offc+=vCsegSize(p), p++) {
            val offsetC = offc;
            at(places(p)) async {
                BlockVectorMult.comp(vB.local(), 0, mA.handleBS(), vC.distV().vec, offsetC, plus);
            }
        }
        vC.calcTime += Timer.milliTime() - stt;

        return vC;                
    }

    public static def comp(mA:DistBlockMatrix, vB:DupVector(mA.N), vC:DupVector(mA.M), plus:Boolean):DupVector(vC) {
        val places = mA.places();

        var stt:Long = Timer.milliTime();
        val root = here;
        //Make copy of output local copy
        if (plus) {
            val tmpc = vC.local().clone();
            finish ateach(Dist.makeUnique(places)) {
                ////Bcast vector to all places. 
                var vBsrc:Rail[ElemType] = null;
                val vBdst = vB.dupV().vec.d;
                if (here.id == root.id){
                    vBsrc = vB.dupV().vec.d;
                }
                vB.team.bcast(root, vBsrc, 0, vBdst, 0, vB.M);

                vC.local().reset();
                BlockVectorMult.comp(mA.handleBS(), vB.local(), vC.local(),  true);

                val src = vC.dupV().vec.d;
                val dst = vC.dupV().vec.d;
                
                vC.team.reduce(root, src, 0, dst, 0, vC.M, Team.ADD);

                var vCsrc:Rail[ElemType] = null;
                val vCdst = vC.dupV().vec.d;
                if (here.id == root.id){
                    vC.local().cellAdd(tmpc as Vector(vC.M));
                    vCsrc = vC.dupV().vec.d;
                }
                vC.team.bcast(root, vCsrc, 0, vCdst, 0, vC.M);
            }
            vC.calcTime += Timer.milliTime() - stt;
        } else {
            finish ateach(Dist.makeUnique(places)) {
                ////Bcast vector to all places. 
                var vBsrc:Rail[ElemType] = null;
                val vBdst = vB.dupV().vec.d;
                if (here.id == root.id){
                    vBsrc = vB.dupV().vec.d;
                }
                
                vB.team.bcast(root, vBsrc, 0, vBdst, 0, vB.M);
                
                vC.local().reset();
                BlockVectorMult.comp(mA.handleBS(), vB.local(), vC.local(),  true);
                
                val src = vC.dupV().vec.d;
                val dst = vC.dupV().vec.d;
                vC.team.allreduce(src, 0, dst, 0, vC.M, Team.ADD);
            }
            vC.calcTime += Timer.milliTime() - stt;
        }
        return vC;
    }
    
    public static def comp(vB:DupVector, mA:DistBlockMatrix(vB.M), vC:DupVector(mA.N), plus:Boolean):DupVector(vC) {
        val places = mA.places();

        var stt:Long = Timer.milliTime();
        val root = here;
        //Make copy of output local copy
        if (plus) {
            val tmpc = vC.local().clone();
        
            finish ateach(Dist.makeUnique(places)) {
                var vBsrc:Rail[ElemType] = null;
                val vBdst = vB.dupV().vec.d;
                if (here.id == root.id){
                    vBsrc = vB.dupV().vec.d;
                }
                
                vB.team.bcast(root, vBsrc, 0, vBdst, 0, vB.M);
                
                vC.local().reset();
                BlockVectorMult.comp(vB.local(), mA.handleBS(), vC.local(), plus);
                
                val src = vC.dupV().vec.d;
                val dst = vC.dupV().vec.d;
                
                vC.team.reduce(root, src, 0, dst, 0, vC.M, Team.ADD);                
                
                var vCsrc:Rail[ElemType] = null;
                val vCdst = vC.dupV().vec.d;
                if (here.id == root.id){
                    vC.local().cellAdd(tmpc as Vector(vC.M));
                    vCsrc = vC.dupV().vec.d;
                }                
                
                vC.team.bcast(root, vCsrc, 0, vCdst, 0, vC.M);
            }
            vC.calcTime += Timer.milliTime() - stt;
            
        } else {
            finish ateach(Dist.makeUnique(places)) {
                var vBsrc:Rail[ElemType] = null;
                val vBdst = vB.dupV().vec.d;
                if (here.id == root.id){
                    vBsrc = vB.dupV().vec.d;
                }
                
                vB.team.bcast(root, vBsrc, 0, vBdst, 0, vB.M);
                
                vC.local().reset();
                BlockVectorMult.comp(vB.local(), mA.handleBS(), vC.local(), plus);
                
                val src = vC.dupV().vec.d;
                val dst = vC.dupV().vec.d;
                vC.team.allreduce(src, 0, dst, 0, vC.M, Team.ADD);
            }
            vC.calcTime += Timer.milliTime() - stt;
        }
        return vC;
    }
}
