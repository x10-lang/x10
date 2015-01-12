/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
package linreg;

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Vector;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistVector;

import x10.matrix.util.PlaceGroupBuilder;
import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.ResilientIterativeApp;
import x10.util.resilient.ResilientExecutor;
import x10.util.resilient.ResilientStoreForApp;

/**
 * Parallel linear regression based on GML distributed
 * dense/sparse matrix
 */
public class LinearRegression implements ResilientIterativeApp {
    static val MAX_SPARSE_DENSITY = 0.1;

    //Input matrix
    public val V:DistBlockMatrix;
    public val b:Vector(V.N);
    //Parameters
    public val iterations:Long;
    static val lambda:Double = 0.000001;

    public val w:Vector(V.N);

    val d_p:DupVector(V.N);
    val Vp:DistVector(V.M);

    val r:Vector(V.N);
    val d_q:DupVector(V.N);

    private val chkpntIterations:Long;

    var norm_r2:Double;
    var lastCheckpointNorm:Double;
    var iter:Long;

    //----Profiling-----
    public var parCompT:Long=0;
    public var seqCompT:Long=0;
    public var commT:Long;
    private val nzd:Double;

    //the matrix snapshot should be taken only once
    private var V_snapshot:DistObjectSnapshot;

    public def this(v:DistBlockMatrix, b_:Vector(v.N), it:Long, chkpntIter:Long, sparseDensity:Double, places:PlaceGroup) {
        iterations = it;
        V =v;
        b =b_ as Vector(V.N);

        Vp = DistVector.make(V.M, V.getAggRowBs(), places);

        r  = Vector.make(V.N);
        d_p= DupVector.make(V.N, places);

        d_q= DupVector.make(V.N, places);

        w  = Vector.make(V.N);

        this.chkpntIterations = chkpntIter;

        nzd = sparseDensity;
    }

    public static def make(mV:Long, nV:Long, nRowBs:Long, nColBs:Long, nzd:Double, it:Long, chkpntIter:Long, places:PlaceGroup) {
        //First dist block matrix must have vertical distribution
        val V:DistBlockMatrix(mV, nV);
        if (nzd < MAX_SPARSE_DENSITY) {
            V = DistBlockMatrix.makeSparse(mV, nV, nRowBs, nColBs, places.size(), 1, nzd, places);
        } else {
            Console.OUT.println("using dense matrix as non-zero density = " + nzd);
            V = DistBlockMatrix.makeDense(mV, nV, nRowBs, nColBs, places.size(), 1, places);
        }
        val b = Vector.make(nV);

        Console.OUT.printf("Start init matrix V(%d,%d) blocks(%dx%d) ", mV, nV, nRowBs, nColBs);
        Console.OUT.printf("dist(%dx%d) nzd:%f\n", places.size(), 1, nzd);
        V.initRandom();

        Debug.flushln("Done. Start init other matrices, b, r, p, q, and w");
        b.initRandom();

        return new LinearRegression(V, b, it, chkpntIter, nzd, places);
    }

    public def isFinished() {
        return iter >= iterations;
    }

    public def run() {
        b.copyTo(r);
        b.copyTo(d_p.local());
        r.scale(-1.0);

        norm_r2 = r.norm();

        new ResilientExecutor(chkpntIterations).run(this);

        return w;
    }

    public def step() {
        d_p.sync();

        // Parallel computing

        var ct:Long = Timer.milliTime();
        // 10: q=((t(V) %*% (V %*% p)) )

        d_q.mult(Vp.mult(V, d_p), V);

        parCompT += Timer.milliTime() - ct;

        // Sequential computing

        ct = Timer.milliTime();
        //q = q + lambda*p
        val p = d_p.local();
        val q = d_q.local();
        q.scaleAdd(lambda, p);

        // 11: alpha= norm_r2/(t(p)%*%q);
        val alpha = norm_r2 / p.dotProd(q);

        // 12: w=w+alpha*p;
        w.scaleAdd(alpha, p);

        // 13: old norm r2=norm r2;
        val old_norm_r2 = norm_r2;

        // 14: r=r+alpha*q;
        r.scaleAdd(alpha, q);
        norm_r2 = r.norm();

        // 15: beta=norm r2/old norm r2;
        val beta = norm_r2/old_norm_r2;

        // 16: p=-r+beta*p;
        p.scale(beta).cellSub(r);

        seqCompT += Timer.milliTime() - ct;
        // 17: i=i+1;

        commT = d_q.getCommTime() + d_p.getCommTime();
        //w.print("Parallel result");

        iter++;
    }

    public def checkpoint(resilientStore:ResilientStoreForApp) {       
        resilientStore.startNewSnapshot();        
        finish{
            async {
                if (V_snapshot == null)                    
                    V_snapshot = V.makeSnapshot();                
                resilientStore.save(V, V_snapshot, true);
            }
            async resilientStore.save(d_p);
            async resilientStore.save(d_q);
            async resilientStore.save(r);
            async resilientStore.save(w);
        }
        resilientStore.commit();
        lastCheckpointNorm = norm_r2;
    }

    /**
     * Restore from the snapshot with new PlaceGroup
     */
    public def restore(newPg:PlaceGroup, store:ResilientStoreForApp, lastCheckpointIter:Long) {
        val newRowPs = newPg.size();
        val newColPs = 1;
        //remake all the distributed data structures
        if (nzd < MAX_SPARSE_DENSITY) {
            V.remakeSparse(newRowPs, newColPs, nzd, newPg);
        } else {
            V.remakeDense(newRowPs, newColPs, newPg);
        }
        d_p.remake(newPg);
        Vp.remake(V.getAggRowBs(), newPg);
        d_q.remake(newPg);

        store.restore();

        //adjust the iteration number and the norm value
        iter = lastCheckpointIter;
        norm_r2 = lastCheckpointNorm;
        Console.OUT.println("Restore succeeded. Restarting from iteration["+iter+"] norm["+norm_r2+"] ...");
    }
    
    public def getMaxIterations():Long{
        return iterations;
    }
}
