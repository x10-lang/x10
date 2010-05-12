package ssca1;

/**
 * In order to compute the number of processors to use and the parameters for what part
 * of the longer string this process gets to analyze, the key number is the size of the
 * longest string, counting gaps, can come out of the algorithm.  The highest score we
 * can get is when the short sequence occurs as a subsequence of the long sequence, in
 * which case the score is maxScoreOnMatch, which we shall compute here.  If the penalty
 * for inserting a gap is openGapPenalty and that for extending one is extendGapPenalty,
 * then:
 *   if openGapPenalty is less than or equal to extendGapPenalty, the limit on gaps that
 *   one may introduce is 
 *                       maxScoreOnMatch/openGapPenalty
 *   otherwise that limit is 
 *           1 + (maxScoreOnMatch-openGapPenalty)/extendGapPenalty.
 * These bounds hold because, in either case, introducing another gap would drive the
 * score negative--and you can always get a score of 0 by having 0-length matches, so
 * the best match never has a negative score.
 * 
 * We will use maxAlignedLength to denote the implied bound on the length of longest
 * match: (the length of the shorter sequence + one less than the limit on how many
 * gaps can be inserted).
 * 
 * We assume that, when aligning two sequences, the highest score at any given entry
 * is achieved by matching the opposing entry exactly.  That is, we assume that ALL
 * mismatches must incur some penalty.  A more sophisticated version would loop over
 * all values in the scoring matrix that correspond to the entry as row index.
 *
 * One way we get parallelism by covering the long sequence with overlapping segments in
 * such a way that the winning segment must lie entirely in one of these larger segments.
 * Suppose, to begin with, that we cover with segments of length maxAlignedLength.
 * We would have to have one segment for each entry in the longer sequence up to and
 * including the entry at (longerLength - maxAlignedLength).  In general, though, this is
 * a horrifically redundant way to operate.  We can eliminate a lot of the redundancy and
 * still get most of the gain based on the observation that if A is one of the segments
 * and if B is the segment that immediately follows A, and if N is the index in A where
 * the overlap between A and B begins, then:
 *   If the overlap of B with A is at least maxAlignedLength-1, then a winner that
 *       starts in A BEFORE N must end in A.
 *   If B also extends at least maxAlignedLength-1 after N, or if the whole sequence ends in
 *       B, then a winner that starts in A BUT AFTER N, must end in B, .
 * Hence if we allow 2*maxAlignedLength-2 for the length of the segments and overlap
 * the right half of one with the left half of the next, we can ensure that each segment
 * can be processed independently and our best match will lie entirely in one of them.  
 * 
 * One reasonable heuristic, therefore, for the  number of processors one can profitably
 * use is to make the segment length 2*maxAlignedLength-2, and to make half of each segment
 * overlap with the next.  We can bound processes worth spawning by the number of segments
 * we need, or (if that number is too large) by the total number of places available.
 * As noted, there are other approaches: this is a significant tuning issue.
 */

public struct SegmentationInfo {
   /** the length of the largest possible answer, with gaps included */
   val maxAlignedLength: Int;
   /** the number of places == the number of segments we break the long sequence into */
   val segmentCount: Int;
   /** the number of bytes in the overlap between two adjacent segments in the long sequence */
   val overlap: Int;
   /** the size the segment would be if the segment count divided (long sequence length - overlap) */
   val baseSegmentLength: Int;
   /** the remainder when the segment count is divided into (long sequence length - overlap) */
   val shortfall: Int;
   
   public def this(parms: Parameters, shorter: ValRail[Byte], longLength: Int) {
      var maxScoreOnMatch: Int = 0;
      val shortLength = shorter.length();
      for((i) in (0..shortLength-1)) {
         val entry = shorter(i);
         maxScoreOnMatch += parms.getScore(entry, entry);
      }
      maxAlignedLength =  (parms.openGapPenalty <= parms.extendGapPenalty) ?
                      shortLength - 1 + maxScoreOnMatch/parms.openGapPenalty :
                      shortLength  + (maxScoreOnMatch-parms.openGapPenalty)/parms.extendGapPenalty;
      overlap = maxAlignedLength - 1;
      val mostWeCanUse = (2*overlap >= longLength) ? 1 : (longLength-overlap)/overlap;
      segmentCount = (mostWeCanUse>Place.MAX_PLACES) ? Place.MAX_PLACES : mostWeCanUse;
      baseSegmentLength = overlap + (longLength - overlap)/segmentCount;
      shortfall = (longLength-overlap) % segmentCount;
   }
   
   public def firstInLonger(placeId: Int) {
      val baseOffset = placeId*(baseSegmentLength - overlap);  // where to start if shortfall == 0
      return (placeId < shortfall) ? baseOffset + placeId:  baseOffset + shortfall;
   }
   
   public def slice(placeId: Int, whole:ValRail[Byte]) {
      val first = firstInLonger(placeId);
      val last = first + (placeId < shortfall ? baseSegmentLength + 2 :  baseSegmentLength + 1);

      // TODO: Dave G:  This is a hack around what appears to be an off-by-one error in the calculation
      //                of last for the very last segment.  
      //                It was "harmless" when using String's because the C++ backend's implementation
      //                of String's native code wasn't doing bounds checking in substring and the ways strings
      //                were constructed made it ok to have the substring ending in an extra \0 character 
      //                (it disappeared when doing the strlen/strdup operations to construct the String).
      //                When switching to ValRail[Byte], this problem was no longer masked.
      //                I think there is probabbly a more principled adjustment in the calculations that feed into
      //                last, but am leaving that for Jonathan to investigate.  
      val last2 = last < whole.length() ? last : whole.length();

      return ValRail.make[Byte](last2-first, (i:int)=>{ whole(first+i) });
   }
}