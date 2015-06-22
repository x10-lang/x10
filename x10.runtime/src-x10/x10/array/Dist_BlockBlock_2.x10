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
 * over the Places in its PlaceGroup by block-block distributing the
 * dimensions of the IterationSpace.
 */
public class Dist_BlockBlock_2 extends Dist(2) {
    val globalIndices:DenseIterationSpace_2{self!=null};

    @TransientInitExpr(computeLocalIndices())
    transient var localIndices:DenseIterationSpace_2{self!=null};
    @NonEscaping protected final def computeLocalIndices():DenseIterationSpace_2{self!=null} {
        return BlockingUtils.partitionBlockBlock(globalIndices, pg.numPlaces(), pg.indexOf(here));
    }

    def this(pg:PlaceGroup{self!=null}, is:DenseIterationSpace_2{self!=null}) {
        super(pg, is);
        globalIndices = is;
        localIndices = computeLocalIndices();
    }
}
