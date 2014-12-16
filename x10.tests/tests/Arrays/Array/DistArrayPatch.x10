/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

import harness.x10Test;
import x10.array.*;

/**
 * Test of x10.array.DistArray.getPatch().
 */
public class DistArrayPatch extends x10Test {

    public def run():Boolean {
        val block1DistArray = new DistArray_Block_1[Double](123, (i:Long)=>i as Double);
        finish for (place in block1DistArray.placeGroup()) at(place) async {
            val localIndices = block1DistArray.localIndices();
            if (!localIndices.isEmpty()) {
                val mid = localIndices.min + (localIndices.max-localIndices.min+1)/2;
                val secondHalf = mid..localIndices.max;
                val patch = block1DistArray.getPatch(secondHalf);
                var patchIndex:Long = 0;
                for (i in mid..localIndices.max) {
                    chk(block1DistArray(i) == patch(patchIndex++));
                }
            }
        }

        val block2DistArray = new DistArray_Block_2[Double](13, 11, (i:Long, j:Long)=>(10*i+j) as Double);
        finish for (place in block2DistArray.placeGroup()) at(place) async {
            val localIndices = block2DistArray.localIndices();
            if (!localIndices.isEmpty()) {
                val rightEdge = new DenseIterationSpace_2(localIndices.max0, localIndices.min1, localIndices.max0, localIndices.max1);
                val patch = block2DistArray.getPatch(rightEdge);
                var patchIndex:Long = 0;
                for ([i,j] in rightEdge) {
                    chk(block2DistArray(i,j) == patch(patchIndex++));
                }
            }
        }

        val blockBlock2DistArray = new DistArray_BlockBlock_2[Double](10, 13, (i:Long, j:Long)=>(10*i+j) as Double);
        finish for (place in blockBlock2DistArray.placeGroup()) at(place) async {
            val localIndices = blockBlock2DistArray.localIndices();
            if (!localIndices.isEmpty()) {
                val rightEdge = new DenseIterationSpace_2(localIndices.max0, localIndices.min1, localIndices.max0, localIndices.max1);
                val patch = blockBlock2DistArray.getPatch(rightEdge);
                var patchIndex:Long = 0;
                for ([i,j] in rightEdge) {
                    chk(blockBlock2DistArray(i,j) == patch(patchIndex++));
                }
            }
        }

        val block3DistArray = new DistArray_Block_3[Double](7, 5, 6, (i:Long, j:Long, k:Long)=>(100*i+10*j+k) as Double);
        finish for (place in block3DistArray.placeGroup()) at(place) async {
            val localIndices = block3DistArray.localIndices();
            if (!localIndices.isEmpty()) {
                val rightFace = new DenseIterationSpace_3(localIndices.max0, localIndices.min1, localIndices.min2, localIndices.max0, localIndices.max1, localIndices.max2);
                val patch = block3DistArray.getPatch(rightFace);
                var patchIndex:Long = 0;
                for ([i,j,k] in rightFace) {
                    chk(block3DistArray(i,j,k) == patch(patchIndex++));
                }
            }
        }

        val blockBlock3DistArray = new DistArray_BlockBlock_3[Double](6, 5, 8, (i:Long, j:Long, k:Long)=>(100*i+10*j+k) as Double);
        finish for (place in blockBlock3DistArray.placeGroup()) at(place) async {
            val localIndices = blockBlock3DistArray.localIndices();
            if (!localIndices.isEmpty()) {
                val rightFace = new DenseIterationSpace_3(localIndices.max0, localIndices.min1, localIndices.min2, localIndices.max0, localIndices.max1, localIndices.max2);
                val patch = blockBlock3DistArray.getPatch(rightFace);
                var patchIndex:Long = 0;
                for ([i,j,k] in rightFace) {
                    chk(blockBlock3DistArray(i,j,k) == patch(patchIndex++));
                }
            }
        }

        return true;
    }

    public static def main(args:Rail[String]):void {
        new DistArrayPatch().execute();
    }
}
