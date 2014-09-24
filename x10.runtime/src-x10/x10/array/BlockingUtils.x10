/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2013.
 */

package x10.array;

/**
 * Utility functions for blocking iteration spaces.
 * Can be used both for computing local iteration spaces for 
 * distributed arrays and for tiling iteration spaces for concurrency within
 * a single place. 
 */
public class BlockingUtils {

    /**
     * A Block distribution takes a rank-1 iteration space
     * and distributes all points contained in the bounding box of the 
     * space roughly evenly into the requested number of units.
     * If the input iteration space is dense, then the returned iteration space will
     * only contain points that were also contained in the input iteration space.
     * If the input iteration space is not dense, then the returned iteration space
     * may contain points that were NOT in the input iteration space (and thus depending
     * on the application may require additional filtering before being used).
     * 
     * @param is the iteration space to partition
     * @param n is the total number of partitions desired
     * @return the IterationSpace representing the ith partition
     */
    public static def partitionBlock(is:IterationSpace(1), n:Long):Rail[DenseIterationSpace_1{self!=null}]{self.size==n,self!=null} {
        return new Rail[DenseIterationSpace_1{self!=null}](n, (i:Long)=>partitionBlock(is, n, i));
    }


    /**
     * A Block distribution takes a rank-1 iteration space
     * and distributes all points contained in the bounding box of the 
     * space roughly evenly into the requested number of units.
     * If the input iteration space is dense, then the returned iteration space will
     * only contain points that were also contained in the input iteration space.
     * If the input iteration space is not dense, then the returned iteration space
     * may contain points that were NOT in the input iteration space (and thus depending
     * on the application may require additional filtering before being used).
     * 
     * This utility method computes and returns the ith element in such a distribution.
     *
     * @param is the iteration space to partition
     * @param n is the total number of partitions desired
     * @param i is the index of the partition requested
     * @return the IterationSpace representing the ith partition
     */
    public static def partitionBlock(is:IterationSpace(1), n:Long, i:Long):DenseIterationSpace_1{self!=null} {
        return partitionBlock(is.min(0), is.max(0), n, i);
    }


    /**
     * A Block distribution takes a rank-1 iteration space
     * and distributes all points contained in the bounding box of the 
     * space roughly evenly into the requested number of units.
     * If the input iteration space is dense, then the returned iteration space will
     * only contain points that were also contained in the input iteration space.
     * If the input iteration space is not dense, then the returned iteration space
     * may contain points that were NOT in the input iteration space (and thus depending
     * on the application may require additional filtering before being used).
     * 
     * This utility method computes and returns the ith element in such a distribution.
     *
     * @param min is the minimum element in the iteration space to partition
     * @param max is the maximum element in the iteration space to partition
     * @param n is the total number of partitions desired
     * @param i is the index of the partition requested
     * @return the IterationSpace representing the ith partition
     */
    public static def partitionBlock(min:Long, max:Long, n:Long, i:Long):DenseIterationSpace_1{self!=null} {
        val numElems = max - min + 1;
	if (numElems < 1) return DenseIterationSpace_1.EMPTY;
        val blockSize = numElems/n;
        val leftOver = numElems - n*blockSize;
        val low = min + blockSize*i + (i< leftOver ? i : leftOver);
        val hi = low + blockSize + (i < leftOver ? 0 : -1);
        return new DenseIterationSpace_1(low, hi);
    }


    /**
     * A Block distribution takes a rank-1 iteration space
     * and distributes all points contained in the bounding box of the 
     * space roughly evenly into the requested number of units.
     * If the input iteration space is dense, then the returned iteration space will
     * only contain points that were also contained in the input iteration space.
     * If the input iteration space is not dense, then the returned iteration space
     * may contain points that were NOT in the input iteration space (and thus depending
     * on the application may require additional filtering before being used).
     * 
     * This utility method computes which partition an argument index would be placed.
     *
     * @param is the iteration space to partition
     * @param n is the total number of partitions desired
     * @param i the given index 
     * @return the partition number into which i is mapped by the distribution
     *         (or -1 if not contained in the bounding box of the argument iteration space)
     */
    public static def mapIndexToBlockPartition(is:IterationSpace(1), n:Long, i:Long):Long {
        val min = is.min(0);
        val max = is.max(0);
        return mapIndexToBlockPartition(is.min(0), is.max(0), n, i);
    }


    /**
     * A Block distribution takes a rank-1 iteration space
     * and distributes all points contained in the bounding box of the 
     * space roughly evenly into the requested number of units.
     * If the input iteration space is dense, then the returned iteration space will
     * only contain points that were also contained in the input iteration space.
     * If the input iteration space is not dense, then the returned iteration space
     * may contain points that were NOT in the input iteration space (and thus depending
     * on the application may require additional filtering before being used).
     * 
     * This utility method computes which partition an argument index would be placed.
     *
     * @param min is the minimum element in the iteration space to partition
     * @param max is the maximum element in the iteration space to partition
     * @param n is the total number of partitions desired
     * @param i the given index 
     * @return the partition number into which i is mapped by the distribution
     *         (or -1 if not contained in the bounding box of the argument iteration space)
     */
    public static def mapIndexToBlockPartition(min:Long, max:Long, n:Long, i:Long):Long {
        if (i < min || i > max) return -1;
        val numElems = max - min + 1;
        val blockSize = numElems/n;
        val leftOver = numElems - n*blockSize;
        val normalizedIndex = i-min;
        val nominalIndex = normalizedIndex/(blockSize+1);
        if (nominalIndex < leftOver) {
            return nominalIndex;
        } else {
            val indexFromTop = max-i;
            return n - 1 - (indexFromTop/(blockSize));
        }
    }



    /**
     * A BlockBlock distribution takes a rank-2 iteration space
     * and distributes all points contained in the bounding box of the 
     * space roughly evenly into the requested number of units in 2-d grid.
     * If the input iteration space is dense, then the returned iteration space will
     * only contain points that were also contained in the input iteration space.
     * If the input iteration space is not dense, then the returned iteration space
     * may contain points that were NOT in the input iteration space (and thus depending
     * on the application may require additional filtering before being used).
     * 
     * This utility method computes and returns the ith element in such a distribution.
     *
     * @param is the iteration space to partition
     * @param n is the total number of partitions desired
     * @param i is the index of the partition requested
     * @return the IterationSpace representing the ith partition
     */
    public static def partitionBlockBlock(is:IterationSpace(2), n:Long, i:Long):DenseIterationSpace_2{self!=null} {
        val min0 = is.min(0);
        val max0 = is.max(0);
        val min1 = is.min(1);
        val max1 = is.max(1);
        val size0 = (max0 - min0 + 1);
        val size1 = (max1 - min1 + 1);

	if (size0 < 1 || size1 < 1) return DenseIterationSpace_2.EMPTY;
	if (size0 == 1) {
	    val is1 = partitionBlock(min1, max1, n, i);
            return new DenseIterationSpace_2(min0, is1.min(0), max0, is1.max(0));
	} 
        if (size1 == 1) {
	    val is0 = partitionBlock(min0, max0, n, i);
            return new DenseIterationSpace_2(is0.min(0), min1, is0.max(0), max1);
        }

        val size0Even = size0 % 2 == 0 ? size0 : size0-1;
        val P = Math.min(n, size0Even * size1);
        if (i >= P) return DenseIterationSpace_2.EMPTY;

        val divisions0 = Math.min(size0Even, Math.pow2(Math.ceil((Math.log(P as Double) / Math.log(2.0)) / 2.0) as Long));
        val divisions1 = Math.min(size1, Math.ceil((P as Double) / divisions0) as Long);
        val leftOver = divisions0*divisions1 - P;
        val leftOverOddOffset = (divisions0 % 2 == 0) ? 0 : i*2/(divisions0+1);

        val blockIndex0 = i < leftOver ? (i*2-leftOverOddOffset) % divisions0 : (i+leftOver) % divisions0;
        val blockIndex1 = i < leftOver ? (i*2) / divisions0 : (i+leftOver) / divisions0;

        val low0 = min0 + Math.ceil(blockIndex0 * size0 / divisions0 as Double) as Long;
        val blockHi0 = blockIndex0 + (i < leftOver ? 2 : 1);
        val hi0 = min0 + Math.ceil(blockHi0 * size0 / divisions0 as Double) as Long - 1;

        val low1 = min1 + Math.ceil(blockIndex1 * size1 / divisions1 as Double) as Long;
        val hi1 = min1 + Math.ceil((blockIndex1+1) * size1 / divisions1 as Double) as Long - 1;

        return new DenseIterationSpace_2(low0,low1,hi0,hi1);
    }


    /**
     * A BlockBlock distribution takes a rank-2 iteration space
     * and distributes all points contained in the bounding box of the 
     * space roughly evenly into the requested number of units in 2-d grid.
     * If the input iteration space is dense, then the returned iteration space will
     * only contain points that were also contained in the input iteration space.
     * If the input iteration space is not dense, then the returned iteration space
     * may contain points that were NOT in the input iteration space (and thus depending
     * on the application may require additional filtering before being used).
     * 
     * This utility method computes which partition an argument index would be placed.
     *
     * @param is the iteration space to partition
     * @param n is the total number of partitions desired
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the partition number into which (i,j) is mapped by the distribution
     *         (or -1 if not contained in the bounding box of the argument iteration space)
     */
    public static def mapIndexToBlockBlockPartition(is:IterationSpace(2), n:Long, i:Long, j:Long):Long {
        val min0 = is.min(0);
        val max0 = is.max(0);
        val min1 = is.min(1);
        val max1 = is.max(1);
        if (i<min0 || i>max0 || j<min1 || j>max1) return -1;

        val size0 = (max0 - min0 + 1);
        val size1 = (max1 - min1 + 1);

	if (size0 == 1) {
            return mapIndexToBlockPartition(min1, max1, n, j);
	} 
        if (size1 == 1) {
            return mapIndexToBlockPartition(min0, max0, n, i);
        }

        val size0Even = size0 % 2 == 0 ? size0 : size0-1;
        val   P = Math.min(n, size0Even * size1);
        val divisions0 = Math.min(size0Even, Math.pow2(Math.ceil((Math.log(P as Double) / Math.log(2.0)) / 2.0) as Long));
        val divisions1 = Math.min(size1, Math.ceil((P as Double) / divisions0) as Long);
        val numBlocks = divisions0 * divisions1;
        val leftOver = numBlocks - P;

        val blockIndex0 = divisions0 == 1 ? 0 : ((i - min0) * divisions0) / size0;
        val blockIndex1 = divisions1 == 1 ? 0 : ((j - min1) * divisions1) / size1;
        val blockIndex = (blockIndex1 * divisions0) + blockIndex0;

        if (blockIndex <= leftOver * 2) {
            return blockIndex / 2;
        } else {
            return blockIndex - leftOver;
        }
    }
}
