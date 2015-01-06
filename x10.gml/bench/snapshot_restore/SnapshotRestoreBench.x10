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

public class SnapshotRestoreBench {
    public static def main(args:Rail[String]) {
        val className = args.size > 0 ? args(0):"DupVector";  // class Name
        val iter = args.size > 1 ? Long.parse(args(1)):1;  // test iterations for averaging
        val spare = args.size > 2 ? Long.parse(args(2)):1; // spare places count
        val killPlaceId = args.size > 3 ? Long.parse(args(3)):Place.numPlaces()/2; // id of the place to kill
        val M_KB = args.size > 4 ? Long.parse(args(4)):10;  // rows in KB (DupVector and DistVector size)
        val N_KB = args.size > 5 ? Long.parse(args(5)):10;  // cols in KB
        val rbs = args.size > 6 ? Long.parse(args(6)):4;  // row blocks in KB
        val cbs = args.size > 7 ? Long.parse(args(7)):4;  // col blocks in KB
        val nonZeroDensity = args.size > 8 ? Double.parse(args(8)):0.09;  // non zero density for sparse matrix test

        if (spare < 0 || spare >= Place.numPlaces()) {
            Console.OUT.println("Error in settings");
            System.setExitCode(1n);
            return;
        }
        
        val places = PlaceGroupBuilder.makeTestPlaceGroup(spare);
        val M = M_KB * 1024;
        val N = N_KB * 1024;
        
        val rowPlaces = places.size();
        val colPlaces = 1;
        
        val restoreMode = System.getenv("X10_PLACE_GROUP_RESTORE_MODE");
        val rebalanceMode = System.getenv("X10_GML_REBALANCE");
        
        var restoreModeDesc:String = "Shrink";
        if (restoreMode != null && restoreMode.equals("1"))
            restoreModeDesc = "Redundant";
        else if (className.indexOf("DistBlockMatrix") != -1n && (rebalanceMode != null && rebalanceMode.equals("1")) )
            restoreModeDesc = "Shrink-Rebalance";
        
        var testDesc:String = "Running SnapshotRestore Benchmarking test >> Mode ["+restoreModeDesc+"] >> KillingPlace["+killPlaceId+"] >> ";
        var testObject:Snapshottable = null;
        if (className.equals("DistVector")){
            testDesc += "[DistVector] size ["+M+"] ...";
            testObject = DistVector.make(M,places).init(1.0);
        }
        else if (className.equals("DenseDistBlockMatrix")){
            testDesc += "[DistBlockMatrix] Type[Dense] size ["+M+"*"+N+"] grid ["+rbs+"*"+cbs+"] dist ["+rowPlaces+"*"+colPlaces+"] ...";
            testObject = DistBlockMatrix.makeDense(M, N, rbs, cbs, rowPlaces, colPlaces, places).init(1.0);
        }
        else if (className.equals("SparseDistBlockMatrix")){
            testDesc += "[DistBlockMatrix] Type[Sparse] size ["+M+"*"+N+"] grid ["+rbs+"*"+cbs+"] NonZeroDensity["+nonZeroDensity+"]  dist ["+rowPlaces+"*"+colPlaces+"] ...";
            testObject = DistBlockMatrix.makeSparse(M, N, rbs, cbs, rowPlaces, colPlaces, nonZeroDensity, places).init(1.0);
        }
        else {
            testDesc += "[DupVector] Type[Dense] size ["+M+"] ...";
            testObject = DupVector.make(M,places).init(1.0);
        }
        Console.OUT.println(testDesc);
        val tc = new SnapshottableExecutor(iter, killPlaceId, nonZeroDensity , places, testObject);
        tc.run();
    }
}
