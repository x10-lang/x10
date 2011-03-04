package ssca1;
import x10.io.Printer;
import x10.util.StringBuilder;
import x10.util.RailBuilder;
/**
 * an instance is constructed by the winning Scorer: all a user needs
 * to know about the best match.
 */
public class Output {
   val score:       Long;
   val shortBegins: Int;
   val longBegins:  Int;
   val shortEnds:   Int;
   val longEnds:    Int;
   val shortMatch:  String;
   val longMatch:   String;

   public def this() {
      score = 0L;
      shortBegins = longBegins = shortEnds = longEnds = 0;
      shortMatch = longMatch = "-";
   }
   
   public def this(score_: Long,                        // the value calculated for the best aligned match
		   shorter: Region, longer: Region,   // the starting and ending offsets for the matched substrings
		   shortBuilder: RailBuilder[Byte], // the short aligned string, reversed
		   longBuilder: RailBuilder[Byte]   // the long aligned string, reversed
		  ){shorter.rank==1, longer.rank==1} {
      score = score_;
      shortBegins = shorter.min(0);  shortEnds = shorter.max(0);
      longBegins  = longer.min(0);   longEnds  = longer.max(0);
      shortMatch  = cleanup(shortBuilder);
      longMatch   = cleanup(longBuilder);
   }
		   
   /**
    * Reverses the array to undo the fact that we had to produce the winning aligned
    * sequences back to front.
    * 
    * Programming note: If we used a RailBuilder, if our target hardware could exploit
    * shared memory and multiple threads, and if the strings are long enough, we could
    * parallelize this method.  Frankly, though, it is a low runner: the real cost is
    * the construction of the traceback moves matrix.
    * @param builderIn the Builder used to construct the traceback sequences
    * @return the string corresponding to the bytes in the reversed input Rail 
    */
   private static def cleanup(builderIn: RailBuilder[Byte]) {
      val source = builderIn.result();
      val builderOut = new StringBuilder();
      for(var i: Int = source.length-1; i>=0; i--) builderOut.add(source(i) as Char);
      return builderOut.result();
   }
   
   public def print(out: Printer) {
      out.println(this.toString());
   }
		  
   public def toString() {
      val builder = new StringBuilder();
      builder.add("Highest score: " + score +"\r\n");
      builder.add("Length of the aligned sequences: " + shortMatch.length()+"\r\n");
      builder.add("Subsequence from shorter:  ("+shortBegins+" .. "+shortEnds+")\r\n");
      builder.add("Subsequence from longer:   ("+longBegins +" .. "+longEnds +")\r\n");
      builder.add("Aligned match from the shorter:\r\n\t");
      builder.add(shortMatch);
      builder.add("\r\nAligned match from the longer\r\n\t");
      builder.add(longMatch);
      builder.add("\r\n");
      return builder.result();
   }
}