

public class ResilientHeatTransfer_v3 {
    static val arrayColsPerPlace : Long = 256;
    static val epsilon = 1.0e-2;
    static val dimensionSize : Long = arrayColsPerPlace * Place.numPlaces();
    static val iterationsPerBackup = 6;
    

    public static class PartitionedHeatArray {
      var partition : Rail[Double];
      var numColumns : Long;
      var arraySize : Long;

      def this(colsInPartition:Long) {
        arraySize = (dimensionSize + 2) * (colsInPartition +2);
        partition = new Rail[Double](arraySize, 0.0);
	numColumns = colsInPartition;
      } 
      
      def newPrimary(colsInPartition:Long) {
        arraySize = (dimensionSize + 2) * (colsInPartition +2);
        partition = new Rail[Double](arraySize, 0.0);
	numColumns = colsInPartition;
      } 
      
      def restoreFromBackup(backup:BackupPartition) {

	if (backup.numColumns != numColumns)
	  newPrimary(backup.numColumns);

        for (i : Long in 0..(arraySize - 1))
	  partition(i) = backup.partition(i);
      }


      def restoreFromBackups(backup1:BackupPartition, backup2:BackupPartition) {
        newPrimary(backup1.numColumns + backup2.numColumns);
        for (i in 0..(dimensionSize+1)) {
          for (j in 0..backup1.numColumns)
            partition(tupleToIndex(i,j,numColumns+2)) = backup1.partition(tupleToIndex(i,j,backup1.numColumns+2));
          for (j in 1..(backup2.numColumns+1))
            partition(tupleToIndex(i,j+backup1.numColumns,numColumns+2)) = backup2.partition(tupleToIndex(i,j,backup2.numColumns+2));
        }
      }
    }

    public static class BackupPartition {
      var partition : Rail[Double];
      var numColumns : Long;
      var arraySize : Long;
      var isValid : Boolean;
      var iterationNumber : Long;

      def this(colsInPartition:Long) {
        arraySize = (dimensionSize + 2) * (colsInPartition +2);
        partition = new Rail[Double](arraySize, 0.0);
	numColumns = colsInPartition;
        isValid = false;
	iterationNumber = -1;
      }

      def newBackupArray(colsInPartition:Long) {
        arraySize = (dimensionSize + 2) * (colsInPartition +2);
        partition = new Rail[Double](arraySize, 0.0);
	numColumns = colsInPartition;
      }

      def updateBackup(primary:PartitionedHeatArray, iteration:Long) {
        if (primary.numColumns != numColumns)
          // failed place might change # of columns in backup partition
	  newBackupArray(primary.numColumns);	  
        for (i : Long in 0..(arraySize - 1))
	  partition(i) = primary.partition(i);
        iterationNumber = iteration;
        isValid = true;        
      }
      def printInfo() {
        Console.OUT.printf("Backup partition for iteration %i\n", iterationNumber);
	printHeatArray(partition, numColumns);
      }

    }

    public static class BackupPartitions {
      backup0 : BackupPartition;
      backup1 : BackupPartition;
      localBackup : BackupPartition;

      def this(colsInPartition:Long) {
        backup0 = new BackupPartition(colsInPartition);
        backup1 = new BackupPartition(colsInPartition);
        localBackup = new BackupPartition(colsInPartition);
      }

      def updatePartition(primary:PartitionedHeatArray, iteration:Long) {
        if (((iteration/iterationsPerBackup) % 2) == 0)
	  backup0.updateBackup(primary, iteration);
	else
	  backup1.updateBackup(primary, iteration);
      }

      def updateLocalPartition(primary:PartitionedHeatArray, iteration:Long) {
	localBackup.updateBackup(primary, iteration);
      }


      def printBackupInfo() {
        Console.OUT.printf("Remote backup partition 0\n");
	backup0.printInfo();
        Console.OUT.printf("Remote backup partition 1\n");
	backup1.printInfo();
        Console.OUT.printf("Local backup partition\n");
	localBackup.printInfo();
      }
    }


    public static class RecoveryInformation {
      val activePlaces : Rail[Boolean];
      var lastActivePlace : Long;
      var lastCheckPointIter : Long;
      var numCheckPoints : Long;

      def this() {
        activePlaces = new Rail[Boolean](Place.numPlaces(), true);
	lastActivePlace = Place.numPlaces() - 1;
	lastCheckPointIter = 0;
	numCheckPoints = 0;
      }
    }

    static def tupleToIndex(row:Long, col:Long, colSize:Long): Long {
// Console.OUT.printf("row %i, col %i, colSize %i, real index %i\n", row, col, colSize, (row * colSize) + col);
      return (row * colSize) + col;
    }

    static def initializeHeatArray(): PartitionedHeatArray {
      val heatArray = new PartitionedHeatArray(arrayColsPerPlace);
      for (i in 1..arrayColsPerPlace) {
	  heatArray.partition(tupleToIndex(0,i,arrayColsPerPlace+2)) = 1.0;
	  heatArray.partition(tupleToIndex(dimensionSize+1,i,arrayColsPerPlace+2)) = 1.0; 
          // Previous line not in other versions of heat transfer
      }
      return heatArray;
    }

    static def initializeTempArray(): PartitionedHeatArray {
      val tempArray = new PartitionedHeatArray(arrayColsPerPlace);
      return tempArray;
    }


    static def initializeColumnArray(): Rail[Double] {
      val columnArray = new Rail[Double](dimensionSize + 2, 0.0);
      return columnArray;
    }

    static def getColumn(heatArray:PartitionedHeatArray, highCol:Boolean, column:Rail[double]) {
      val j : Long;

      if (highCol)
        j = heatArray.numColumns;
      else
        j = 1;
      for (i : Long in 0..(dimensionSize + 1))
        column(i) = heatArray.partition(tupleToIndex(i,j,heatArray.numColumns+2));
    }

    static def computeHeatNextIteration(heatArray:PartitionedHeatArray, tempArray:PartitionedHeatArray) {
      var maxDifference : double = 0.0;
      var majorDifference : Boolean = false;
      for (i in 1..dimensionSize)
        for (j in 1..heatArray.numColumns) {

// Console.OUT.printf("%i %i\n", i, j);
          tempArray.partition(tupleToIndex(i,j,tempArray.numColumns+2)) = (heatArray.partition(tupleToIndex(i-1,j,heatArray.numColumns+2)) + heatArray.partition(tupleToIndex(i+1,j,heatArray.numColumns+2)) +
                                                                  heatArray.partition(tupleToIndex(i,j-1,heatArray.numColumns+2)) + heatArray.partition(tupleToIndex(i,j+1,heatArray.numColumns+2))) / 4;
// Console.OUT.printf("got to here\n");
          if (!majorDifference) {
            if (Math.abs(tempArray.partition(tupleToIndex(i,j,tempArray.numColumns+2)) - heatArray.partition(tupleToIndex(i,j,heatArray.numColumns+2))) > epsilon)
              majorDifference = true;
          }
          if (i > 1)
            heatArray.partition(tupleToIndex(i-1,j,heatArray.numColumns+2)) = tempArray.partition(tupleToIndex(i-1,j,tempArray.numColumns+2));
        }
      for (j in 1..heatArray.numColumns)
        heatArray.partition(tupleToIndex(dimensionSize,j,heatArray.numColumns+2)) = tempArray.partition(tupleToIndex(dimensionSize,j,tempArray.numColumns+2));
      return majorDifference;
    }

    static def replaceColumn(heatArray:PartitionedHeatArray, highCol:Boolean, column:Rail[Double]) {
      val j : Long;

      if (highCol)
        j = heatArray.numColumns + 1;
      else
        j = 0;
      for (i in 0..(dimensionSize + 1))
        heatArray.partition(tupleToIndex(i,j,heatArray.numColumns+2)) = column(i);
    }

    static def printHeatArray(heatArray:Rail[Double], numColumns:Long) {
      Console.OUT.println("Number of columns: " + numColumns);
      for (i in 0..(dimensionSize+1)) {
        for (j in 0..(numColumns+1))
          Console.OUT.printf("%1.4f ", heatArray(tupleToIndex(i,j,numColumns+2)));
        Console.OUT.println();
      }
      Console.OUT.println();

    }

    static def outputAcrossAllPlaces(heatArrayPlh:PlaceLocalHandle[PartitionedHeatArray], backupPlh:PlaceLocalHandle[BackupPartitions], iterationNumber:Long, activePlaces:Rail[Boolean]) {
      Console.OUT.printf("Total Iterations: %i\n", iterationNumber);
      for (p in Place.places()) {
        if (activePlaces(p.id())) {
          Console.OUT.printf("Heat Array at Place %i\n", p.id());
          at (p) {
            printHeatArray(heatArrayPlh().partition, heatArrayPlh().numColumns);
            backupPlh().printBackupInfo();
          }
	}
	else
          Console.OUT.printf("Place %i is dead\n", p.id());
      }
    }


    static def checkpoint(backupPlh:PlaceLocalHandle[BackupPartitions], heatArrayPlh:PlaceLocalHandle[PartitionedHeatArray], iterationNumber:Long, recoveryInfo:RecoveryInformation): boolean {
      var placeNum : Long = 0;
      var p : Place = Place.FIRST_PLACE;
      var checkpointSucceeded : Boolean = true;

      try {
        finish {
          while (placeNum < recoveryInfo.lastActivePlace) {
            val nextPlace = findActivePlace(Place.places().next(p), recoveryInfo.activePlaces, true);
	    val currPlace = p;
            at (nextPlace) async backupPlh().updatePartition(at (currPlace) heatArrayPlh(), iterationNumber);
	    p = nextPlace;
	    placeNum = p.id();
          }
          at (Place.FIRST_PLACE) async backupPlh().updatePartition(at (Place(recoveryInfo.lastActivePlace)) heatArrayPlh(), iterationNumber);
        }
      } catch (e:MultipleExceptions) {
        handleDeadPlaces(heatArrayPlh, backupPlh, recoveryInfo, e, 3);
	checkpointSucceeded = false;
      }

      try {
        finish {
          for (p1 in Place.places()) {
	    if (recoveryInfo.activePlaces(p1.id()))
	      at (p1) async backupPlh().updateLocalPartition(heatArrayPlh(), iterationNumber);
          }
        }
      } catch (e:MultipleExceptions) {
        handleDeadPlaces(heatArrayPlh, backupPlh, recoveryInfo, e, 4);
      }
      return checkpointSucceeded;
    }

    static def handleDeadPlaces(heatArrayPlh:PlaceLocalHandle[PartitionedHeatArray], backupPlh:PlaceLocalHandle[BackupPartitions], recoveryInfo:RecoveryInformation, e:MultipleExceptions, failureLocation:Long) {
    // failureLocation can be one of the following: 
    // 1: While the new heat array values are being calculated
    // 2. While the border columns are being copied
    // 3. While the array values are being checkpointed at remote places.
    // 4. While the array values are being checkpointed locally.  At this stage, all of the information from the current
    //    iteration is recoverable.  Thus, there is no reason to undo the results of recent loop iterations

        //val deadPlaceExceptions = e.getExceptionsOfType[DeadPlaceException]();\
        // TODO do something with dead place exception?
        val filtered = e.filterExceptionsOfType[DeadPlaceException]();
        if (filtered != null) throw filtered;
    }

    static def findActivePlace(p:Place, activePlaces:Rail[Boolean], ascending:Boolean): Place {
      var p1: Place = p;

      while (!activePlaces(p1.id())) {
        if (ascending)
          p1 = Place.places().next(p1);
        else
          p1 = Place.places().prev(p1);
      }
      return p1;
    }

    public static def main(Rail[String]) {
      val heatArrayPlh = PlaceLocalHandle.make[PartitionedHeatArray](Place.places(), ()=>initializeHeatArray());
      val tempArrayPlh = PlaceLocalHandle.make[PartitionedHeatArray](Place.places(), ()=>initializeTempArray());
      val columnArrayPlh = PlaceLocalHandle.make[Rail[Double]](Place.places(), ()=>initializeColumnArray());
      val columnArrayPlhHigh = PlaceLocalHandle.make[Rail[Double]](Place.places(), ()=>initializeColumnArray());
      val backupPlh = PlaceLocalHandle.make[BackupPartitions](Place.places(), ()=>new BackupPartitions(arrayColsPerPlace));
      var keepIterating : Boolean = true;
      val continueVariables = new Rail[Boolean](Place.numPlaces());
      val outputResults : Boolean = false;
      val printDebugInfo : Boolean = false;
      var iterationNumber : Long = 0;
      var before : Long;
      var after : Long;
      val maxIterations = 100;
      val recoveryInfo : RecoveryInformation = new RecoveryInformation();
      var placeNum : Long;
      var prevPlaceVar : Place;
      var nextPlaceVar : Place;
      var placeVar : Place;
      var checkPointSucceeded : Boolean;

      Console.OUT.printf("Array Dimension: %i, heat difference threshold: %e, number of places: %i\n", 
                           dimensionSize, epsilon, Place.numPlaces());
      Console.OUT.printf("Array columns per place: %i\n", arrayColsPerPlace);
      Console.OUT.printf("Loop iterations between checkpoints: %i\n", iterationsPerBackup);
      before = System.nanoTime();
      while (keepIterating) {
        iterationNumber++;
	try {
          finish for (p in Place.places())
            if (recoveryInfo.activePlaces(p.id())) 
              async continueVariables(p.id()) = at (p) computeHeatNextIteration(heatArrayPlh(), tempArrayPlh());
	} catch (e:MultipleExceptions) {
	  handleDeadPlaces(heatArrayPlh, backupPlh, recoveryInfo, e, 1);
          iterationNumber = recoveryInfo.lastCheckPointIter;
	  continue;
        }
        keepIterating = false;
        for (i in 0..(Place.numPlaces()-1)) {
	  if (recoveryInfo.activePlaces(i))
            if (continueVariables(i) == true) {
              keepIterating = true;  // only 1 needs to be true to continue iterating
              break;
            }
        }

	if (iterationNumber > maxIterations)
	  keepIterating = false;
        if (keepIterating)
        // Copy border columns across partitions
	  try {
            finish {
              val secondPlace : Place = findActivePlace(Place.places().next(Place.FIRST_PLACE), recoveryInfo.activePlaces, true);
	      val secondToLastPlace = findActivePlace(Place.places().prev(Place(recoveryInfo.lastActivePlace)), recoveryInfo.activePlaces, false);
	      async {
                at (secondPlace) getColumn(heatArrayPlh(), false, columnArrayPlh());
                at(Place.FIRST_PLACE) replaceColumn(heatArrayPlh(), true, at (secondPlace) columnArrayPlh());
              }
	      async {
                at (secondToLastPlace) getColumn(heatArrayPlh(), true, columnArrayPlhHigh());
                at(Place(recoveryInfo.lastActivePlace)) replaceColumn(heatArrayPlh(), false, at (secondToLastPlace) columnArrayPlhHigh());
              }
	      placeVar = secondPlace;
	      placeNum = secondPlace.id();
	      prevPlaceVar = Place.FIRST_PLACE;
	      while (placeNum <= secondToLastPlace.id()) {
	        val p1 = placeVar;
		val prevPlace = prevPlaceVar;
	        async {
                  at (prevPlace) getColumn(heatArrayPlh(), true, columnArrayPlhHigh());
                  at (p1) replaceColumn(heatArrayPlh(), false, at (prevPlace) columnArrayPlhHigh());
                }
		val nextPlace = findActivePlace(Place.places().next(p1), recoveryInfo.activePlaces, true);
		async {
 		  at (nextPlace) getColumn(heatArrayPlh(), false, columnArrayPlh());
                  at (p1) replaceColumn(heatArrayPlh(), true, at (nextPlace) columnArrayPlh());
                }
	        prevPlaceVar = p1;
	        placeVar = nextPlace;
	        placeNum = nextPlace.id();
              }
            } // finish
          } catch (e:MultipleExceptions) {
	    handleDeadPlaces(heatArrayPlh, backupPlh, recoveryInfo, e, 2);
            iterationNumber = recoveryInfo.lastCheckPointIter;
	    continue;
          }
          if ((iterationNumber % iterationsPerBackup) == 0) {
	    checkPointSucceeded = checkpoint(backupPlh, heatArrayPlh, iterationNumber, recoveryInfo);
	    if (checkPointSucceeded) {
	      recoveryInfo.numCheckPoints++;
	      recoveryInfo.lastCheckPointIter = iterationNumber;
            }
	    else {
              iterationNumber = recoveryInfo.lastCheckPointIter;
	      continue;
	    }
          }
          if (printDebugInfo)
            outputAcrossAllPlaces(heatArrayPlh, backupPlh, iterationNumber, recoveryInfo.activePlaces);
      } // while
      after = System.nanoTime();
      Console.OUT.printf("Computation finished.  %i iterations.\n", iterationNumber);
      Console.OUT.println("Time in seconds: " + (after-before)/1E9);
      Console.OUT.println("Number of checkpoints: " + recoveryInfo.numCheckPoints);

      if (outputResults) {
        Console.OUT.printf("Below is the final heat array.\n");
        outputAcrossAllPlaces(heatArrayPlh, backupPlh, iterationNumber, recoveryInfo.activePlaces);
      }
    }
}
