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

public class HeatTransfer_v8 {
    static val arrayColsPerPlace : Int = 2n;
    static val epsilon = 1.0e-2;
    static val dimensionSize : Int = arrayColsPerPlace * (Place.numPlaces() as Int);

    static def tupleToIndex(row:Long, col:Long, colSize:Long): Long {
// Console.OUT.printf("row %i, col %i, colSize %i, real index %i\n", row, col, colSize, (row * colSize) + col);
      return (row * colSize) + col;
    }

    static def initializeHeatArray(): Rail[Double] {
      val heatArray = new Rail[Double]((dimensionSize + 2) * (arrayColsPerPlace + 2), 0.0); 
      for (i in 1..arrayColsPerPlace) {
	  heatArray(tupleToIndex(0,i,arrayColsPerPlace+2)) = 1.0;
	  heatArray(tupleToIndex(dimensionSize+1,i,arrayColsPerPlace+2)) = 1.0; 
          // Previous line not in other versions of heat transfer
      }
      return heatArray;
    }

    static def initializeTempArray(): Rail[Double] {
      val tempArray = new Rail[Double](dimensionSize * arrayColsPerPlace, 0.0);
      return tempArray;
    }

    static def initializeColumnArray(): Rail[Double] {
      val columnArray = new Rail[Double](dimensionSize + 2, 0.0);
      return columnArray;
    }

    static def getColumn(heatArray:Rail[Double], j:Int, column:Rail[double]) {
      for (i in 0..(dimensionSize + 1))
        column(i) = heatArray(tupleToIndex(i,j,arrayColsPerPlace+2));
    }

    static def computeHeatNextIteration(heatArray:Rail[Double], tempArray:Rail[double]) {
      var maxDifference : double = 0.0;
      var majorDifference : Boolean = false;
      for (i in 1..dimensionSize)
        for (j in 1..arrayColsPerPlace) {

// Console.OUT.printf("%i %i\n", i, j);
          tempArray(tupleToIndex(i-1,j-1,arrayColsPerPlace)) = (heatArray(tupleToIndex(i-1,j,arrayColsPerPlace+2)) + heatArray(tupleToIndex(i+1,j,arrayColsPerPlace+2)) +
                                                                  heatArray(tupleToIndex(i,j-1,arrayColsPerPlace+2)) + heatArray(tupleToIndex(i,j+1,arrayColsPerPlace+2))) / 4;
// Console.OUT.printf("got to here\n");
          if (!majorDifference) {
            if (Math.abs(tempArray(tupleToIndex(i-1,j-1,arrayColsPerPlace)) - heatArray(tupleToIndex(i,j,arrayColsPerPlace+2))) > epsilon)
              majorDifference = true;
          }
          if (i > 1)
            heatArray(tupleToIndex(i-1,j,arrayColsPerPlace+2)) = tempArray(tupleToIndex(i-2,j-1,arrayColsPerPlace));
        }
      for (j in 1..arrayColsPerPlace)
        heatArray(tupleToIndex(dimensionSize,j,arrayColsPerPlace+2)) = tempArray(tupleToIndex(dimensionSize-1,j-1,arrayColsPerPlace));
      return majorDifference;
    }

    static def replaceColumn(heatArray:Rail[Double], j:Int, column:Rail[Double]) {
      for (i in 0..(dimensionSize + 1))
        heatArray(tupleToIndex(i,j,arrayColsPerPlace+2)) = column(i);
    }

    static def printHeatArray(heatArray:Rail[Double]) {
      for (i in 0..(dimensionSize+1)) {
        for (j in 0..(arrayColsPerPlace+1))
          Console.OUT.printf("%1.4f ", heatArray(tupleToIndex(i,j,arrayColsPerPlace+2)));
        Console.OUT.println();
      }
      Console.OUT.println();

    }

    static def outputAcrossAllPlaces(heatArrayPlh:PlaceLocalHandle[Rail[double]]
, iterationNumber:Int) {
      Console.OUT.printf("Total Iterations: %i\n", iterationNumber);
      for (p in Place.places()) {
        Console.OUT.printf("Heat Array at Place %i\n", p.id());
        at (p) printHeatArray(heatArrayPlh());
      }
    }

    public static def main(Rail[String]) {
      val heatArrayPlh = PlaceLocalHandle.make[Rail[double]](Place.places(), ()=>initializeHeatArray());
      val tempArrayPlh = PlaceLocalHandle.make[Rail[double]](Place.places(), ()=>initializeTempArray());
      val columnArrayPlh = PlaceLocalHandle.make[Rail[double]](Place.places(), ()=>initializeColumnArray());
      var keepIterating : Boolean = true;
      val continueVariables = new Rail[Boolean](Place.numPlaces() as Int);
      val outputResults : Boolean = true;
      val printDebugInfo : Boolean = false;
      var iterationNumber : Int = 0n;
      var before : Long;
      var after : Long;

      Console.OUT.printf("Array Dimension: %i, heat difference threshold: %e, number of places: %i\n", 
                           dimensionSize, epsilon, Place.numPlaces());
      Console.OUT.printf("Array columns per place: %i\n", arrayColsPerPlace);

      before = System.nanoTime();
      while (keepIterating) {
        iterationNumber++;

        finish for (p in Place.places()) 
          async continueVariables(p.id()) = at (p) computeHeatNextIteration(heatArrayPlh(), tempArrayPlh());

        keepIterating = false;
        for (i in 0..((Place.numPlaces()-1) as Int)) {
          if (continueVariables(i) == true) {
            keepIterating = true;  // only 1 needs to be true to continue iterating
            break;
          }
        }

        if (keepIterating)
        // Copy border columns across partitions
          finish {
            val secondPlace : Place = Place.places().next(Place.FIRST_PLACE);
            val lastPlace : Place =  (Place(Place.numPlaces()-1));
            at (secondPlace) getColumn(heatArrayPlh(), 1n, columnArrayPlh());
            at(Place.FIRST_PLACE) async replaceColumn(heatArrayPlh(),  arrayColsPerPlace+1n, at (secondPlace) columnArrayPlh());
            at (Place.places().prev(lastPlace)) getColumn(heatArrayPlh(), arrayColsPerPlace, columnArrayPlh());
            at(lastPlace) async replaceColumn(heatArrayPlh(), 0n, at (Place.places().prev(lastPlace)) columnArrayPlh());
            for (placeNum in 1..(Place.numPlaces()-2)) {
 	      val p : Place = Place(placeNum); 
              at (p) {
                at (Place.places().prev(p)) getColumn(heatArrayPlh(), arrayColsPerPlace, columnArrayPlh());
                async replaceColumn(heatArrayPlh(), 0n, at (Place.places().prev(p)) columnArrayPlh());
 		at (Place.places().next(p)) getColumn(heatArrayPlh(), 1n, columnArrayPlh());
                async replaceColumn(heatArrayPlh(), arrayColsPerPlace+1n, at (Place.places().next(p)) columnArrayPlh());
              }
            }
          }
        if (printDebugInfo)
          outputAcrossAllPlaces(heatArrayPlh, iterationNumber);
      }
      after = System.nanoTime();
      Console.OUT.printf("Computation finished.  %i iterations.\n", iterationNumber);
      Console.OUT.println("Time in seconds: " + (after-before)/1E9);
      if (outputResults) {
        Console.OUT.printf("Below is the final heat array.\n");
        outputAcrossAllPlaces(heatArrayPlh, iterationNumber);
      }
    }
}
