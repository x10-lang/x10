package ssca1;

import x10.util.StringBuilder;

/**
 * instances manage an array of timings.  Times are recorded in milliseconds, so an Int
 * is sufficient to hold the values, even though the raw times are returned as Longs.
 */
public class Timings {
   static GARBAGE_INITIAL_VALUE = 0xfedbeef;
   static DISPLAY_WIDTH = 10;
   private static DEBUG = false;
   
   /** the number of timings for each phase */
   public val iterations: Int;
   /** the number of time intervals into which the process has been divided */
   public val phases: Int;
   /** for each iteration and phase, the time in milliseconds it required */
   public val timings: Array[Array[Int](1)](1);
   /** short descriptions of what each phase accomplishes */
   public val phaseDescriptions: Array[String](1);
   /** for each phase, the largest of the times it required */
   public val maxs:    Array[Int](1);
   /** for each phase, the average time that it required */
   public val means:   Array[Double](1);
   /** for each phase, the median of the times it required */
   public val medians: Array[Int](1);
   /** for each phase, the smallest of the times it required */
   public val mins:    Array[Int](1);
   /** for each phase, the std deviation of the times it required */
   public val sigmas:  Array[Double](1);
   /** if true, the current values of the stats reflect the current (complete) timings data */
   public var statsAreAvailable: Boolean = false;
   /** the end to end time for the whole set of iterations */
   public var totalTime: Long = 0;
   
   /**
    * builds an instance from the number of iterations and the array of descriptive
    * info about each of the phases.
    */
   public def this(iterations_: Int, phaseDescriptions_: Array[String](1)) {
      phaseDescriptions = phaseDescriptions_;
      val phaseCount = phases = phaseDescriptions_.size;
      iterations = iterations_;
      maxs = new Array[Int](phaseCount);
      mins = new Array[Int](phaseCount);
      medians = new Array[Int](phaseCount);
      means = new Array[Double](phaseCount);
      sigmas = new Array[Double](phaseCount);
      if (DEBUG) Console.ERR.println("Timing "+iterations_+" iterations, each "+phaseCount+" phases");
      timings = new Array[Array[Int](1)](phaseCount, 
          (n:Int)=> new Array[Int](iterations_, GARBAGE_INITIAL_VALUE));
   }
   
   /** records the time required for the given phase during the given iteration
    * @param phaseNumber which phase is at stake, as an index into phaseDescriptions
    * @param iteration as the name suggests
    * @param t the time required to complete the phase.
    */
   public def add(phaseNumber: Int, iteration: Int, t:Long) {
      if (DEBUG) Console.ERR.println("Phase "+phaseNumber+", Iteration "+iteration+", Time "+t);
      val tAsInt = t as Int;
      timings(phaseNumber)(iteration) = tAsInt==GARBAGE_INITIAL_VALUE ? GARBAGE_INITIAL_VALUE+1 : tAsInt;
      statsAreAvailable = false;
   }
   
   /**
    * records the total time required by the whole set of iterations
    */
   public def addTotalTime(totalTime_: Long) {
      totalTime = totalTime_;
   }
   
   /**
    * if the stats have not yet been computed, or if timings have been updated since 
    * the last attempt, we compute the maxs and so on.
    * @throws IllegalArgumentException if the timings data is incomplete
    */
   public def computeStats() {
      if (statsAreAvailable) return true;
      else if (!isComplete()) throw new IllegalArgumentException("timings not yet complete.");
      
      val oddMedian = (iterations%2 == 1);
      val mid = iterations/2;
      try {
         for([pn] in 0..(phases-1)) {
            val sorted = new Array[Int](iterations, (n: Int)=>Timings.GARBAGE_INITIAL_VALUE);
            var lastI: Int = -1;
            var total: Long = 0;
            try { 
               for([i] in 0..(iterations-1)) {
                  total += timings(pn)(i);
                  var j: Int;
                  for(j=0; j<i && timings(pn)(i)<=sorted(j); j++) { }
                  for(var k:Int = i-1; k >= j; k--) sorted(k+1) = sorted(k);
                  sorted(j) = timings(pn)(i);
               }
            } catch(e: Exception) { Console.ERR.println("sorting: "+e); return false;}
            mins(pn) = sorted(0);
            maxs(pn) = sorted(iterations - 1) ;
            medians(pn) = (oddMedian ? sorted(mid) : (sorted(mid-1)+sorted(mid))/2) ;
            val meanPN = (total as Double)/(iterations as Double);
            means(pn) = meanPN;
            var sumOfSquares: Double = 0.0;
            try {
               for([i] in 0..(iterations-1)) {
                  val delta = (timings(pn)(i) as Double) - meanPN;
                  sumOfSquares += delta*delta;
               }
            } catch(e: Exception) {
                Console.ERR.println("sum of squares: "+e);
                return false;
            }
            sigmas(pn) = Math.sqrt(sumOfSquares/(iterations as Double));         
         }
      } catch(e: Exception) {
        Console.ERR.println("phase loop: "+e);
        return false;
      }
      return statsAreAvailable = true;
   }
   
   /**
    * returns true if the garbage default value is not found in any timings entry
    */
   public def isComplete() { 
      for([n] in 0..(phases-1)) if (!isComplete(n)) return false;
      return true;
   }
   
   /**
    * returns true if the garbage default value is not found in any timing for 
    * phase n
    * @param n the phase whose completeness is at issue
    */
   public def isComplete(n: Int) {
      val timing = timings(n);
      for([m] in 0..(iterations-1)) if (timing(m) == GARBAGE_INITIAL_VALUE) return false;
      return true;
   }
   
   /**
    * computes a (reasonably) pretty printed portion of that part of this object
    * that deals with the computed statistics
    * @returns a string suitable for direct display as plain text
    */
   public def statsToString() {
      if(!computeStats()) return "-- no stats --";
      val builder = new StringBuilder();
      if (iterations == 1) return "";
      else try {
         var leaderSize: Int = 0;
         for([n] in 0..(phases-1)) {
            val length = phaseDescriptions(n).length();
            if (length>leaderSize) leaderSize = length; 
         }
         val delta = leaderSize%4 != 0 ? 4-leaderSize%4 : 0;
         for([n] in 0..(leaderSize+delta)) builder.add(" ");
         builder.add("   Min     Mean     Median     Max   Sigma\r\n");
         for([n] in 0..(phases-1)) {
            val leader = new StringBuilder();
            leader.add(phaseDescriptions(n));
            var size:Int = leader.length();
            while(size++<leaderSize) leader.add(" ");
            builder.add(leader.result());
            builder.add(pad(maxs(n).toString()));
            builder.add(pad((Math.round(means(n)) as Long).toString()));
            builder.add(pad(medians(n).toString()));
            builder.add(pad(mins(n).toString()));
            builder.add(pad((Math.round(sigmas(n)) as Long).toString()));
            builder.add("\r\n");
         }
      } catch(e: Exception) { Console.ERR.println("creating stat string: "+e); }
      return builder.result();
   }

   public def toString(): String = toStringInternal();
   
   private def toStringInternal(): String {
      val builder = new StringBuilder();
      try { 
      if (iterations > 1) {
    	  builder.add("There were "+iterations+" iterations, which took "+totalTime+" milliseconds in all.\r\n\r\n");
    	  builder.add(statsToString());
    	  builder.add("\r\n");
    	  builder.add("Individual timings:\r\n\r\n");
      }
      var leaderSize: Int = 0;
      for([n] in 0..(phases-1)) {
          val length = phaseDescriptions(n).length();
          if (length>leaderSize) leaderSize = length; 
       }
       leaderSize += leaderSize%4 != 0 ? 4-leaderSize%4 : 0;
       for(var i:Int=0; i<iterations; i+=4) {
          for(var n: Int = 0; n<phases; n++) {
             val leader = new StringBuilder();
             leader.add(phaseDescriptions(n));
             var size: Int = leader.length();
             while(size++<leaderSize) leader.add(" ");
             builder.add(leader.result());
             builder.add(pad(timings(n)(i).toString()));
             if(i+1<iterations) builder.add(pad(timings(n)(i+1).toString()));
             if(i+2<iterations) builder.add(pad(timings(n)(i+2).toString()));
             if(i+3<iterations) builder.add(pad(timings(n)(i+3).toString()));
             builder.add("\r\n");
         }
         builder.add("\r\n");
      }
      return builder.result(); }
      catch(e: Exception) { 
         Console.ERR.println(e.getMessage());
         return builder.result();
      }
   }
   
   /** as simple way to right justify some of the ints */
   private static def pad(aString: String) {
       switch(aString.length()) {
       case 1: return "        "+aString;
       case 2: return "       "+aString;
       case 3: return "      "+aString;
       case 4: return "     "+aString;
       case 5: return "    "+aString;
       case 6: return "   "+aString;
       case 7: return "  "+aString;
       case 8: return " "+aString;
       default: return(aString.substring(0,6)+"...");
       }
    }
}