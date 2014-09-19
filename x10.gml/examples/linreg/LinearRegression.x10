/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
package linreg;

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.Vector;

import x10.matrix.block.Grid;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseMultDenseToDense;

import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistVector;

import x10.matrix.util.PlaceGroupBuilder;
import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.ResilientIterativeApp;
import x10.util.resilient.ResilientExecutor;
import x10.util.resilient.ResilientStoreForApp;
import x10.util.resilient.Snapshottable;

/**
 * Parallel linear regression based on GML distributed
 * dense/sparse matrix
 */
public class LinearRegression implements ResilientIterativeApp {

    //Input matrix
    public val V:DistBlockMatrix;
    public val b:Vector(V.N);
    //Parameters
    public val iteration:Long;
    static val lambda:Double = 0.000001;

    public val w:Vector(V.N);

    val d_p:DupVector(V.N);
    val Vp:DistVector(V.M);

    val r:Vector(V.N);
    val d_q:DupVector(V.N);

    private val chkpntIterations:Long;

    var norm_r2:Double;
    var iter:Long;

    //----Profiling-----
    public var parCompT:Long=0;
    public var seqCompT:Long=0;
    public var commT:Long;

    private var isResilient:Boolean = false;
    private var appSnapshotInfo:LinearRegressionSnapshotInfo;
    private val npz:Double;

    //the matrix snapshot should be taken only once
    private var V_snapshot:DistObjectSnapshot[Any,Any];

    public def this(v:DistBlockMatrix, b_:Vector(v.N), it:Long, chkpntIter:Long, sparseDensity:Double, places:PlaceGroup) {
        iteration = it;
        V =v;
        b =b_ as Vector(V.N);

        Vp = DistVector.make(V.M, V.getAggRowBs(), places);

        r  = Vector.make(V.N);
        d_p= DupVector.make(V.N, places);

        d_q= DupVector.make(V.N, places);

        w  = Vector.make(V.N);

        if (chkpntIter > 0 && Runtime.RESILIENT_MODE > 0) {
            isResilient = true;
            appSnapshotInfo = new LinearRegressionSnapshotInfo();
        }
        this.chkpntIterations = chkpntIter;

        npz = sparseDensity;
    }

    public static def make(mV:Long, nV:Long, nRowBs:Long, nColBs:Long, nzd:Double, it:Long, chkpntIter:Long, places:PlaceGroup) {
        //First dist block matrix must have vertical distribution
        val V = DistBlockMatrix.makeSparse(mV, nV, nRowBs, nColBs, places.size(), 1, nzd, places);
        val b = Vector.make(nV);

        Console.OUT.printf("Start init sparse matrix V(%d,%d) blocks(%dx%d) ", mV, nV, nRowBs, nColBs);
        Console.OUT.printf("dist(%dx%d) nzd:%f\n", places.size(), 1, nzd);
        V.initRandom();

        Debug.flushln("Done. Start init other matrices, b, r, p, q, and w");
        b.initRandom();

        return new LinearRegression(V, b, it, chkpntIter, nzd, places);
    }

    public def isFinished() {
        return iter >= iteration;
    }

    public def run() {
        b.copyTo(r);
        b.copyTo(d_p.local());
        r.scale(-1.0);

        norm_r2 = r.norm();

        if (isResilient) {
            Console.OUT.println("Load Balance Before:");
            V.printSparseLoadStatistics();
            V_snapshot = V.makeSnapshot();
        }

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

    static class LinearRegressionSnapshotInfo implements Snapshottable {
        public var r:Vector;
        public var w:Vector;
        public var iteration:Long;
        public var norm:Double;

        public def makeSnapshot():DistObjectSnapshot[Any,Any]{
            val snapshot:DistObjectSnapshot[Any, Any] = DistObjectSnapshot.make[Any,Any]();
            snapshot.save("r",r.clone());
            snapshot.save("w",w.clone());
            snapshot.save("iteration",iteration);
            snapshot.save("norm",norm);
            return snapshot;
        }

        public def restoreSnapshot(snapshot:DistObjectSnapshot[Any,Any]) {
            r = snapshot.load("r") as Vector;
            w = snapshot.load("w") as Vector;
            iteration = snapshot.load("iteration") as Long;
            norm = snapshot.load("norm") as Double;
        }
    }

    public def checkpoint(resilientStore:ResilientStoreForApp) {
        resilientStore.startNewSnapshot();
        resilientStore.save(V, V_snapshot, true);
        resilientStore.save(d_p);
        resilientStore.save(d_q);

        appSnapshotInfo.r = r;
        appSnapshotInfo.w = w;
        appSnapshotInfo.iteration = iter;
        appSnapshotInfo.norm = norm_r2;
        resilientStore.save(appSnapshotInfo);

        resilientStore.commit();
    }

    /**
     * Restore from the snapshot with new PlaceGroup
     */
    public def restore(newPg:PlaceGroup, store:ResilientStoreForApp) {
        val newRowPs = newPg.size();
        val newColPs = 1;
        Console.OUT.println("Going to restore LinearReg app, newRowPs["+newRowPs+"], newColPs["+newColPs+"] ...");
        //remake all the distributed data structures
        V.remakeSparse(newRowPs, newColPs, npz, newPg);
        d_p.remake(newPg);

        Vp.remake(V.getAggRowBs(), newPg);
        d_q.remake(newPg);

        store.restore();

        Console.OUT.println("Load Balance After:");
        V.printSparseLoadStatistics();

        //adjust the iteration number and the norm value
        iter = appSnapshotInfo.iteration;
        norm_r2 = appSnapshotInfo.norm;
        appSnapshotInfo.r.copyTo(r);
        appSnapshotInfo.w.copyTo(w);

        Console.OUT.println("Restore succeeded. Restarting from iteration["+iter+"] norm["+norm_r2+"]");
    }
}
