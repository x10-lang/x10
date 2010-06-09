package ssca1;
import x10.io.EOFException;
import x10.io.IOException;
import x10.io.FileReader;
import x10.io.File;
import x10.util.HashMap;
import x10.util.RailBuilder;
import x10.util.StringBuilder;
import x10.util.Timer;
import x10.util.ValRailBuilder;

/**
 * This is the X10 implementation of the Smith-Waterman algorithm for string matching.
 * The version here is optimized for the case in which one of the two strings to be
 * matched is much shorter than the other.  For more details on the algorithm, see the
 * comments in Scorer.x10.
 *
 * @author Jonathan Brezin (brezin@us.ibm.com)
 */
public class SW {
   
   /**
    * the default Char for beginning a comment in the sequence input files--comments
    * end at the next CR or LF.
    */
   public static DEFAULT_COMMENT_START = '#';
   
   private static phaseLeaders = ["Input phase:", "Scoring phase:", "Traceback phase:"];
   private static INPUT_PHASE = 0, SCORING_PHASE = 1, TRACEBACK_PHASE = 2;
   private static timer: Timer! = new Timer(); // used to get the time at each phase end
   private static DEBUG = true; // used to filter debugging output
   private static USAGE = 
	      "Usage for SSCA1Main is \r\n\tx10 ... Main -s shortPath -l longPath -p paramPath [-r reps]";
   
   /**
    * Parse the command line arguments and check to see whether all three paths we need
    * have been supplied.  If not, display an appropriate error message, and set the
    * process's exit code to a value that identifies the problem.
    * 
    * We are actually a little more generous than the USAGE implies:  all we check is that
    * the strings for the keys begin with the right letter: -short ... will set the -s
    * option without complaint.
    * 
    * @parm args the command line arguments as delivered by the runtime environment
    * @parm parsedArgs a hash whose keys are the one-letter keys from the USAGE string:
    *    l, p, r and s.  The values are the corresponding arguments following those keys.
    * @return a boolean: true means we successfully parsed the arguments.
    */
   private static def parseTheArgs(args: Rail[String]!, parsedArgs: HashMap[String, String]!) {
      if (args.length%2 != 0) {
         Console.ERR.println(USAGE);
         at(Place.FIRST_PLACE) System.setExitCode(24);
         return false;
      }
      for(var n: Int = 0; n < args.length-1; n+=2) {
         val value = args(n+1);
         val key = args(n).substring(1,2); // only care about the key's prefix
         if("lprs".indexOf(key) >= 0) parsedArgs.put(key, value); 
         else {
            Console.ERR.println("Unrecognized command line flag, '" +key + "'\r\n"+USAGE);
            at(Place.FIRST_PLACE) System.setExitCode(25);
            return false;
         }
      }
      var missing: String = "";
      for((n) in (0..2)) {
         val key = ["l","p","s"](n);
         if (!parsedArgs.containsKey(key)) missing += ","+key;
      }
      if (missing.length() == 0) return true;
      else {
         Console.ERR.println("Missing command line argument(s): "+missing.substring(1,missing.length())+".\r\n"+USAGE);
         at(Place.FIRST_PLACE) System.setExitCode(26);
         return false;
      }
   }
   
   /**
    * reads the input file, discarding line enders and comments.  Comments are begun with your
    * choice of comment character.  The default, as noted above, is '#'.  Comments end at the
    * first CR or LF. The sequence is left as the contents of the StringBuilder that is the
    * final argument.  Only characters certified legal by the alphabetIndex are added to the
    * sequence.
    * 
    * Why so complicated?  The point is that REAL input, such as chromosomes or proteins may well
    * be input for other software, and it may be useful to add markup to delineate subseqences
    * of interest--for example, gene boundaries in chromosomes.  Also, comments may be there
    * for human beings to be able to scan the sequences efficiently.  Think real code, not
    * toys.
    * 
    * @param path locates the input file
    * @param alphabetIndex maps ASCII values to the location of the corresponding character
    *    in the alphabet string, -1 meaning "not in the alphabet".  Used to filter valid
    *    input--allowing, for example, ' ' or '|' to appear in the input and be ignored.
    * @param builder used to accumulate the sequence of characters we really care about.
    * @param commentStart starts text to be ignored through the next LF or CR.
    */
   private static def readInASequence(path:String, alphabetIndex: ValRail[Byte], builder: ValRailBuilder[Byte]!, commentStart: Char) {
      try { 
         val reader = (new File(path)).openRead();
         try {
            var inComment: Boolean = false;
            while(true) {
               val b = reader.read();
	       val bAsChar = b as Char;
               if (bAsChar == '\n' || bAsChar == '\r') inComment = false;
               else if (bAsChar==commentStart) inComment = true;
               else if (!inComment) {
                  if (alphabetIndex(b)!=(-1 as Byte)) builder.add(b);
                  else if (DEBUG) Console.ERR.println("Unexpected character, ASCII "+b);
               }
            }
         } catch (eof: EOFException) { reader.close(); }
      } catch(ie: IOException) { Console.ERR.println(ie.getMessage()); }
   }

   /**
    * reads the input file, discarding line enders and comments, which are take to begin with '#'
    * @param path locates the input file
    * @param alphabetIndex maps ASCII values to the location of the corresponding character in the
    *    alphabet string, -1 meaning "not in the alphabet".  Used only to check that the input
    *    is valid.
    * @param builder used to accumulate the sequence of characters we really care about.
    */
   private static def readInASequence(path:String, alphabetIndex: ValRail[Byte], builder: ValRailBuilder[Byte]!) {
      readInASequence(path, alphabetIndex, builder, DEFAULT_COMMENT_START);
   }
   
   /**
    * The arguments are the paths for the three data files--the long and short sequences
    * and the scoring parameters--and the number of repetitions desired.  Once the
    * arguments have been parsed, the parameters are read into an instance of ssca1.Parameters.
    * The alphabet used by the sequences is then available, so that when the two sequences are
    * read from their files, some minimal validation may be done.
    * 
    * We use the scoring parameters to partition the longer sequence into overlapping
    * subsequences, which may be dealt with in parallel to compute the best score each permits.
    * We are actually using the term "partition" a bit loosely here: the segments we are
    * going to use overlap one another considerably.
    * 
    * Once every subsequence has been scored, we know which of them (there could be several)
    * had the winning score.  Our winner is chosen from among the winning subsequences in
    * our partition of the longer sequence to begin as early as possible in the longer
    * sequence.  These resulting matched sequences are returned with the gaps explicitly
    * inserted as needed so that one can directly see how the two align. 
    */
   
   public static def main(args: Rail[String]!) {
      val parsedArgs = new HashMap[String, String]();
      if (!parseTheArgs(args, parsedArgs)) return;
      val repetitions = Int.parseInt(parsedArgs.get("r").value);
      val timings = new Timings(repetitions, phaseLeaders);
      val begin = timer.milliTime();
      var finalResult: Output = new Output();
      val scorers = DistArray.make[Scorer](Dist.makeUnique());
      val scores  = DistArray.make[Score](Dist.makeUnique());

      for((rep) in (0..repetitions-1)) {
         // Step 1: read in the parameters and data
         val startInput = timer.milliTime();
         val parms = Parameters(parsedArgs.get("p").value);
         if (parms.error.length() > 0) {
            Console.ERR.println(parms.error);
            at(Place.FIRST_PLACE) System.setExitCode(33);
            return;
         }
         val shorterBuilder = new ValRailBuilder[Byte]();
         val longerBuilder = new ValRailBuilder[Byte]();
         finish {
            async readInASequence(parsedArgs.get("s").value, parms.alphabetIndex, shorterBuilder);
            async readInASequence(parsedArgs.get("l").value, parms.alphabetIndex, longerBuilder);
         }
         if (shorterBuilder.length() == 0 || longerBuilder.length()==0) {
            at(Place.FIRST_PLACE) System.setExitCode(30);
         }
         val shorter = shorterBuilder.result();
         val longer  = longerBuilder.result();
         val dataIsRead = timer.milliTime();
         timings.add(INPUT_PHASE, rep, dataIsRead - startInput);

         // Step 2: find the best scores and initialize the traceback matrices
         val segInfo   = SegmentationInfo(parms, shorter, longer.length());
         if (rep == 0) Console.OUT.println("Places available: "+Place.MAX_PLACES+"\r\nPlaces used: "+segInfo.segmentCount);
         val segmentedInput = ValRail.make[ValRail[Byte]](segInfo.segmentCount, (i:int) => segInfo.slice(i, longer));
	 finish for ((p) in 0..segInfo.segmentCount-1) {
             val mySegment = segmentedInput(p);  // extract the subsegment here to minimize captured state in async body.
	     val capturedP = p;
             async (Place.places(p)) {
	         val s = new Scorer(parms, shorter, mySegment, segInfo);
                 scorers(capturedP) = s;
                 scores(capturedP) = s.score;
             }
         }
         val scoringDone = timer.milliTime();
         timings.add(SCORING_PHASE, rep, scoringDone-dataIsRead);
         
         //Step 3: place 0 gets to pick the winning place: that place alone does a traceback
         val bestScore = scores.reduce((a:Score, b:Score) => (a.beats(b)?a:b), scores(0));
         val p = Place.places(bestScore.where);
         finalResult = at(p) (scorers(bestScore.where) as Scorer!).result();
         val tracebackDone = timer.milliTime();
         timings.add(TRACEBACK_PHASE, rep, tracebackDone- scoringDone);
      }
      timings.addTotalTime(timer.milliTime() - begin);
      val fr = finalResult;
      at(fr.home) fr.print(Console.OUT); 
      Console.OUT.println(timings);
      at(Place.FIRST_PLACE) System.setExitCode(0);
   }
}
