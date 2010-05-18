package ssca1;
import x10.util.RailBuilder;
import x10.util.StringBuilder;
import x10.util.ValRailBuilder;

/**
 * Instances compute the traceback matrix and location of the ends of the winning
 * sequences
 */
public class Scorer {
    /** constants used to populate the traceback matrix */
    static BAD = 0, STOP = 1, LEFT = 2, DIAGONAL = 3, UP = 4;
    /** for debugging output only: string values for the traceback entries */
    static DIRECTION_NAMES = ["BAD", "STOP", "LEFT", "DIAGONAL", "UP"];
    static GAP = '-'.ord() as Byte;
    
   // input
   /** where this scorer's reponsibility starts in the long sequence */
   val longOffset: Int;
   /** the number of bytes this scorer is responsble for in the long sequence */
   val longSize: Int;
   /** a local copy of the segment of the longer sequence this scorer looks at */
   val longRail: ValRail[Byte];
   /** a local copy of the shorter sequence */
   val shortRail: ValRail[Byte];

   // output
   /** how to work our way back through the two sequences to the aligned subsequence starts */
   val tracebackMoves: Rail[Byte]!;

   /** 
    * all the info, other than the traceback matrix, that we need to compute the matched
    * subsequences, aligned to show where gaps had to be inserted
    */
   val score: Score;
   
   private static DEBUG = false;
 
   /**
    * Smith-Waterman algorithm in the simplest serial implementation: the outer loop reads left-to-right
    * over one of the sequences--by convention, we assume it is the shorter, although we do not make any
    * use of that in this particular method.  The inner loop is over the second, longer sequence.
    * 
    * The indexing in this function may be a little confusing on first reading.  In reading the inner
    * loop below to keep in mind that for a given i and j, we are looking at the effect of the i-1-st
    * element in the short sequence and the j-1-st element in the long.  The reason for this--what might
    * appear to be an "off by one" error--is that it makes tracing back from the ends much cleaner if the
    * traceback matrix has a "landing spot" at 0,j and i,0 where we can place a "STOP" sentinel.
    * 
    * As for the loop bounds: we go from 1 to length (inclusive) in each case.  Again, this might
    * seem wrong, but remember the indexing is shifted "up 1" from what one might expect, because
    * we have to refer to i-1 and j-1 at the (i,j)-th step.  
    *
    * The inner loop's indexing suffers from the same confusion.  At the very top of the INNER loop
    * (the loop on j) we know the best possible scores using subsequences that begin at i-2
    * or earlier in the shorter sequence and j-2 or less in the longer (the best scores 
    * being 0 if i-2<0 or j-2<0), so the best we can hope to do here is:
    *    1) add both the i-1-st from the short sequence to the short's match and the
    *           j-1-st entry from the long to the long's match
    *   2) add the j-1-st entry from the long sequence only, leaving a gap in the short's
    *           matching sequence, or  
    *   3) add the i-1-st entry from the short sequence only, leaving a gap in the long's
    *           matching sequence.
    * The scoring matrix tells us what we gain (or lose) by adding both.  The open and 
    * extend gap penalties tell us what we lose if we insert or enlarge gaps in the matches.
    * 
    * Try not to get too hung up figuring out how the indexing of the various arrays 
    * works: this is a tightly written loop that you may assume is correct and is designed
    * to avoid keep around a matrix containing all the previously computed best scores.
    * 
    * We COULD have written the critical part this loop this way:
    * 
    *    // Evaluate the current match
    *    scoreOfMatchAtLast = score(shortSequence[i - 1], longSequence[j - 1]);
    *    scoreUsingLastMatch = bestScore(i-1,j-1) + scoreOfMatchAtLast;  
    *    // Evaluate the score if we have to insert a(nother) gap in the I sequence
    *    bestIfGapInsertedInI = bestScore(i-1,j) - (getTracebackMove(i-1,j) == UP ? extendGapPenalty : openGapPenalty);               
    *    // Evaluate the score if we have to insert a(nother) gap in the J sequence
    *    bestIfGapInsertedInJ = bestScore(i,j-1) - (getTracebackMove(i,j-1) == LEFT ? extendGapPenalty : openGapPenalty);             
    *    // based on the best score of the three, choose the traceback direction
    *    winner = setBestScore(i, j, maxOrZero(scoreUsingLastMatch, bestIfGapInsertedInI, bestIfGapInsertedInJ));
    * 
    * The cost is maintaining the doubly-indexed array "bestScore".  In the case where the
    * short and long and long sequences are of comparable length, we actually found  a variation
    * on this form of the loop better in writing the MPI code, because it made the communication
    * among processors a lot easier to understand. 
    */
   public def this(parms: Parameters, shorter: ValRail[Byte], longer: ValRail[Byte], segInfo: SegmentationInfo) {
      longOffset = segInfo.firstInLonger(here.id);  // where to start if shortfall == 0
      longRail = longer;
      shortRail = shorter;
      val shortSize = shorter.length();
      longSize  = longer.length();
      val longSize_ = longSize;
      tracebackMoves = Rail.make[Byte]((shortSize+1)*(longSize_ + 1));
      val tracebackMoves_ = tracebackMoves;	
      for ((i) in 1..shortSize-1) tracebackMoves_(i*(longSize_ + 1)) = STOP;
      for ((j) in 0..longSize_-1)  tracebackMoves_(j) = STOP;
      val bestScoreUpTo_I_J = Rail.make[Int](longSize_ + 1);
      var winningScore : Long = 0;
      var shorterLast: Int = -1;
      var longerLast: Int = -1;

      // TODO: Compiler really should be capable of licm for these loads, but it isn't doing it.  Sigh.
      val scoringMatrix = parms.scoringMatrix;
      val alphabetIndex = parms.alphabetIndex;
      val alphabetSize = parms.alphabetSize;
      val extendGapPenalty = parms.extendGapPenalty;
      val openGapPenalty = parms.openGapPenalty;

      for((i) in 1..shortSize) {
         var previousBestScore: Int = 0;
         val short_i_minus_1: Byte = shorter(i-1);
         for((j) in 1..longSize_) { // try {
            //if (j-1 >= longSize_) throw new IllegalArgumentException("long rail access fails: "+j+" versus "+longSize_);
            val long_j:Byte = longer(j-1);

	    // Dave G:  Manually inline call to parms.getScore to eliminate calls from inner loop.
            // Inlining should be done automatically by x10c++, but it isn't smart enough yet.
	    // var scoreOfMatchAtLast: Int = parms.getScore(short_i_minus_1, long_j);
	    val scoreOfMatchAtLast = scoringMatrix(alphabetIndex(short_i_minus_1 as Int)*alphabetSize + alphabetIndex(long_j as Int));

            val scoreUsingLatestIJ = previousBestScore + scoreOfMatchAtLast;
            val bestIfGapInsertedInI = bestScoreUpTo_I_J(j) - (tracebackMoves_((i-1)*(longSize_+1)+j)==UP ? extendGapPenalty : openGapPenalty);
            val bestIfGapInsertedInJ = bestScoreUpTo_I_J(j-1) - (tracebackMoves_(i*(longSize_+1)+j-1) == LEFT ? extendGapPenalty : openGapPenalty);     
            previousBestScore = bestScoreUpTo_I_J(j);  // save for the next time around the loop. 
            var winner: Int = bestScoreUpTo_I_J(j) = maxOrZero(scoreUsingLatestIJ, bestIfGapInsertedInI, bestIfGapInsertedInJ);
            if (winner == 0)                          tracebackMoves_(i*(longSize_ + 1) + j) = STOP;
            else if (winner == scoreUsingLatestIJ)    tracebackMoves_(i*(longSize_ + 1) + j) = DIAGONAL;
            else if (winner == bestIfGapInsertedInI)  tracebackMoves_(i*(longSize_ + 1) + j) = UP;
            else                                      tracebackMoves_(i*(longSize_ + 1) + j) = LEFT;
            // Should we set the traceback to start at the current cell? 
            if (winner > winningScore) { //  yes, a new champ!
               winningScore = winner;
/*             if (DEBUG) {
                  val howWeGotHere = DIRECTION_NAMES(getTracebackMove(i,j));
                  val msg = "["+here.id+"] ("+i+","+j+") "+winner+" "+ howWeGotHere;
                  Console.ERR.println(msg);
               }
*/             shorterLast = i;  
               longerLast = j; 
            }
/*
        } catch(e: Exception) {
             val message = "["+here.id+"] scoring index of range: i="+i+", j="+j+"\r\n"+e;
             throw new ArrayIndexOutOfBoundsException(message);
         }
*/
        }
      }
//      if (DEBUG) Console.ERR.println("long offset="+longOffset+", last="+longerLast+", array end="+longSize_);
      score = Score(winningScore, shorterLast, longerLast, longOffset);
   }

   /**
    * On entry, the ends of the winning subsequences are known: shorterLast and longerLast.
    * Follow the directions in the tracebackMove array to work back from that starting
    * point to reconstruct the winning aligned subsequences of the shorter and longer
    * sequences.  Because we are working backward, the code builds the subsequences from
    * back to front, inserting a "gap" character in each as needed to keep all the matched
    * entries aligned.  Once the subsequence starting points are reached, we reverse and
    * then return the buffered sequences.
    * @return an instance of Output that summarizes all one needs to know about the result
    *    and where in the original sequences it came from.
    */ 
   public def result() {
      var nextMove: Int = BAD;
      val shorterLast = score.shortEnds;
      val longerLast  = score.longEnds;
      if(DEBUG) Console.ERR.println("long winner ends at "+longerLast+",  array ends at "+longRail.length);
      var i: Int = shorterLast; // i now marks the end of the winning subsequence of the shorter sequence 
      var j: Int = longerLast;  // j marks the end of the winning subsequence of the longer sequence
      val shortBuilder = new ValRailBuilder[Byte]();
      val longBuilder  = new ValRailBuilder[Byte]();
      var done: Boolean = false;
      while(!done) { try { //  until we are at the tracebackMove matrix boundary marked by the STOP sentinels
    	  nextMove = getTracebackMove(i, j);
          if (DEBUG) Console.ERR.println("Move "+DIRECTION_NAMES(nextMove)+" from "+i+","+j);
          switch (nextMove) {
          case UP:        // UP means we got here by HOLDING J THE SAME and adding one to I 
             shortBuilder.add(shortRail(--i));
             longBuilder.add(GAP);
             break;
          case DIAGONAL: // DIAGONAL means we got here by matching two elements 
             shortBuilder.add(shortRail(--i));
             longBuilder.add(longRail(--j));
             break;
          case LEFT:    // LEFT means we got here by holding I the same and adding one to J 
             shortBuilder.add(GAP);
             longBuilder.add(longRail(--j));
             break;
          case STOP:    // If we move back further the score goes non-positive 
             done = true;
            break;
          case BAD:
            Console.ERR.println("BAD traceback move found at "+(i+1)+","+(j+1));
            done = true;
            break;
          }
      } catch(e: Exception) {throw new ArrayIndexOutOfBoundsException("i="+i+", j="+j+" vs "+longRail.length);}}
      try {return new Output(score.score,  
    		  (i+1..shorterLast) as Region!{self.rank == 1}, 
    		  (j+1+longOffset..longOffset+longerLast) as Region!{self.rank == 1}, 
    		  shortBuilder, 
    		  longBuilder);
      } catch(e: Exception) {
    	  return new Output(0L, (0..0) as Region!{self.rank == 1}, (0..0) as Region!{self.rank == 1}, shortBuilder, longBuilder);
      }
   }
   
   
   /**
    * treat the parameters i and j as row and column indices into the scoring matrix,
    * and return the corresponding value
    * @param i index into the shorter string
    * @param j index into the longer string
    * @return one of the symbolic constants BAD, STOP, LEFT, DIAGONAL, UP
    */
   private def getTracebackMove(i:Int, j:Int) { return tracebackMoves(i*(longSize + 1) + j); }
   
   /**
    * Assign the value 'direction' to the traceback move that should be executed from
    * position i in the short sequence and j in the long sequence.
    * @param i index into the shorter string
    * @param j index into the longer string
    * @param direction one of the symbolic constants BAD, STOP, LEFT, DIAGONAL, UP
    */
   private def setTracebackMove(i: Int, j: Int, direction: Byte) {
      tracebackMoves(i*(longSize + 1) + j) = direction;
   }

   /**
    * as the name suggests: return the max or 0, whichever is greater--separated out just
    * to avoid clutter in the main loop.
    * @param a one of three scores to be compared
    * @param b the second score
    * @param c the third score
    */
   private def maxOrZero(a: Int, b: Int, c: Int) {
      if (a > b) {
         if (a > c)   return a > 0 ? a : 0;
         else         return c > 0 ? c : 0;
      }
      else if (b > c) return b > 0 ? b : 0;
      else            return c > 0 ? c : 0;
   }
}