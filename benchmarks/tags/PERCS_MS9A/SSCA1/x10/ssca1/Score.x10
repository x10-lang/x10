package ssca1;
/**
 * Instances represent the information needed to pick the place with the best match:
 *    highest score wins, and in event of ties, earliest in the long sequence, and
 *    (again) in the event of ties, earliest in the short sequence.
 * 
 * PROGRAMMING NOTES: The Scorer code s written so that the third condition holds 
 * automatically: it always picks the earliest winner from the short sequence.
 */
public struct Score {
   /** higher values == better match */
   val score:       Long;
   /** index into the short sequence of the end of the best match */
   val shortEnds:   Int;
   /** index into the part of the long sequence at this place where the best match ends */
   val longEnds:    Int;
   /** where the part of the long sequence at this place begins in the whole sequence */
   val longStarts:  Int;
   /** the id of the place yielding this score */
   val where:       Int;

   public def this(score_: Long, shortEnds_: Int, longEnds_: Int, longStarts_: Int) {
      where     = here.id;
      score     = score_;
      shortEnds = shortEnds_;
      longEnds  = longEnds_;
      longStarts = longStarts_;
      //Console.ERR.println("["+where+"] score "+score +" short "+shortEnds_+" long "+longEnds_);
   }
   
   /** 
    * see the class comment for what "beats" means.  Think of this method as a binary
    * operation, score1 beats score2 => true/false, to be used in a reduction of an
    * Array of scores.
    * 
    * Programming note: this calculation depends on the Scorers finding the earliest
    * winner in their subsequence.  Hence the winning score associated with the
    * earliest start is guaranteed to be the earliest in the whole.
    * 
    * @return a boolean that is true when this score is the one we should use for
    * the best match
    */
   public def beats(other: Score) {
      if( score > other.score ) return true;
      else if (score == other.score) { 
    	 if(longStarts <= other.longStarts) return true;
      }
      return false;
   }
}