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

import x10.util.Timer;

import x10.matrix.util.PlaceGroupBuilder;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupVector;
import x10.util.resilient.Snapshottable;
import x10.util.resilient.DistObjectSnapshot;

public class SnapshottableExecutor {
    public val iter:Long;
    val killPlaceId:Long;
    public val nonZeroDensity:Double;
    val places:PlaceGroup;
    val V:Snapshottable;
    var V_snapshot:DistObjectSnapshot = null;


    public def this(it:Long, killPlace:Long, nzd:Double , pg:PlaceGroup, obj:Snapshottable) {
        iter=it;
        killPlaceId = killPlace;
        places = pg;
        V = obj;
        nonZeroDensity = nzd;
    }

    public def run():void {
        runSnapshot();

        try{
            at(Place.places()(killPlaceId)) {
                Console.OUT.println("Going to Kill " + here);
                System.killHere();
            }
        }
        catch(ex:DeadPlaceException) {
            //ex.printStackTrace();
        }
        runRestore();
    }

    public def runSnapshot(): void {
        var stt:Long = Timer.milliTime();
        for (1..iter) {
            V_snapshot = V.makeSnapshot();
        }    
        var avgt:Double = (1.0*Timer.milliTime()-stt) /iter;
        Console.OUT.printf("BenchmarkResult----"+V.typeName()+" Snapshot --- Time:%8.3f MilliSec \n", avgt);
    }

    public def runRestore(): void {
        val stt = Timer.milliTime();
        for (1..iter) {
            val newPlaceGroup = PlaceGroupBuilder.createRestorePlaceGroup(places);
            if (V instanceof DistVector)
                (V as DistVector).remake(newPlaceGroup);
            else if (V instanceof DupVector)
                (V as DupVector).remake(newPlaceGroup);
            else if (V instanceof DistBlockMatrix){
                val v_mat = V as DistBlockMatrix;
                val rowPlaces = newPlaceGroup.size();
                val colPlaces = 1;
                if (v_mat.handleBS().blocklist.get(0).isSparse())
                    v_mat.remakeSparse(rowPlaces, colPlaces, nonZeroDensity, newPlaceGroup);
                else
                    v_mat.remakeDense(rowPlaces, colPlaces, newPlaceGroup);
            }
            try{
                V.restoreSnapshot(V_snapshot);
            }catch(e:Exception){
                e.printStackTrace();
            }
        }
        val avgt = (1.0*Timer.milliTime()-stt) /iter;
        Console.OUT.printf("BenchmarkResult----"+V.typeName()+" Restore --- Time:%8.3f MilliSec \n", avgt);
    }
}
