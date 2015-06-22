/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.array;

import x10.compiler.NonEscaping;
import x10.compiler.TransientInitExpr;

/**
 * Represents the distribution of the Points of an IterationSpace(2) 
 * over the Places in its PlaceGroup by block distributing the
 * the first dimension of the IterationSpace.
 */
public class Dist_Block_2 extends Dist(2) {
    val globalIndices:DenseIterationSpace_2{self!=null};

    @TransientInitExpr(computeLocalIndices())
    transient var localIndices:DenseIterationSpace_2{self!=null};
    @NonEscaping protected final def computeLocalIndices():DenseIterationSpace_2{self!=null} {
	val local_m = BlockingUtils.partitionBlock(globalIndices.min(0), globalIndices.max(0), pg.numPlaces(), pg.indexOf(here));
        return new DenseIterationSpace_2(local_m.min(0), globalIndices.min(1), local_m.max(0), globalIndices.max(1));
    }

    def this(pg:PlaceGroup{self!=null}, is:DenseIterationSpace_2{self!=null}) {
        super(pg, is);
        globalIndices = is;
        localIndices = computeLocalIndices();
    }
}
