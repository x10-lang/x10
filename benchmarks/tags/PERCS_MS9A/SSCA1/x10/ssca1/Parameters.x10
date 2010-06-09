package ssca1;
import x10.util.StringBuilder;

/**
 * Instances hold the results of reading the parameter file.  See x10doc commments for
 * the static class ParameterReader below for the syntax of that file.
 * 
 * The various individual entries are each described in detail in their x10doc comments.
 * 
 * @author Jonathan Brezin (brezin@us.ibm.com) 
 */
 public struct Parameters {
   /** the path used to find the parameter file: for debugging only */
   val source: String;

   /** the cost of inserting a gap after a real character */ 
   val openGapPenalty: Int;
   /**  the cost of inserting a gap after another gap */
   val extendGapPenalty: Int;
     
   /** 
    * the set of characters from which the entries are taken: order counts here, because
    * the scoring matrix uses the positions of characters in this string as row and column
    * indices.
    */ 
   val alphabet: String;         
   /**  the number of characters in the alphabet */ 
   val alphabetSize: Int;      
   /**  maps the ASCII code for a character in the alphabet to its position in the alphabet */
   val alphabetIndex: ValRail[Byte];
   /** 
    * entries are the values assigned to matching one character against another: the
    * rows and columns are indexed by the positions in the alphabet (starting with index 0).
    * Thus, if 'B' is in position 1 and 'D' in position 3, scoring matrix(1,3) is the score
    * (probably negative in this case!) if a 'B' is matched against a 'D' in the two test
    * strings.
    */ 
   val scoringMatrix: ValRail[Int];
   
   /** if the parameters cannot be read in correctly, a message is left here */   
   val error: String;
   
   private static DEBUG=false;
   private static SHOW_SCORES=true;
   
   /**
    * reads the scoring parameters from the file named by its argument.
    * @param path locates a file containing the scoring information in the format described
    *    in the class comment for the static internal class ParameterReader below.
    */
   public def this(path: String) {
      source = path;
      val reader = ParameterReader(path);
      if (DEBUG) Console.ERR.println("Reader done");
      val alphabetIndex_ = Rail.make[Byte](256, (n: Int) => -1 as Byte);
      if (reader.errors.length() != 0) {
         error = reader.errors;
         openGapPenalty = extendGapPenalty = alphabetSize = 0;
         alphabet = "";
         alphabetIndex = alphabetIndex_;
         scoringMatrix = [0]; // bug: should allow []
         return;
      }
      openGapPenalty = reader.getNumericProperty("openGap");
      extendGapPenalty = reader.getNumericProperty("extendGap");
      val rawAlphabet = reader.getStringProperty("alphabet");
      val builder = new StringBuilder();
      for((n) in (0..rawAlphabet.length()-1)) {
         val c = rawAlphabet.charAt(n);
         if (!c.isSpaceChar()) builder.add(c);
      }
      val compressed = alphabet = builder.result();
      val size = alphabetSize = compressed.length();
      for((j) in 0..size-1) alphabetIndex_(compressed.charAt(j).ord()) = j as Byte;
      alphabetIndex = alphabetIndex_ as ValRail[Byte];
      val scoringMatrix_ = reader.getIntArrayProperty("scores");
      val expected = size*size;
      error = (scoringMatrix_.length == expected)  ? "" :
              "Scoring matrix size is "+scoringMatrix_.length+", but expected "+expected+"\r\n";
 
      scoringMatrix = scoringMatrix_ as ValRail[Int];
      if (DEBUG) Console.OUT.println(asString(SHOW_SCORES));
   }

   /**
    * treat the parameters a and b as row and column indices into the scoring matrix,
    * and return the corresponding value
    */
   public def getScore(a: Byte, b: Byte) {
      return scoringMatrix(alphabetIndex(a as Int)*alphabetSize + alphabetIndex(b as Int));
   }
   
   private def asString(withScores: Boolean) {
      val builder = new StringBuilder();
      builder.add("open gap penalty: "+openGapPenalty);
      builder.add(", extend gap penalty: "+ extendGapPenalty+"\r\n");
      builder.add("alphabet: '"+alphabet+"'\r\n");
      builder.add("index"+showIndex()+"\r\n");
      if (withScores) builder.add("Scores:\r\n"+showScores());
      builder.add("\r\n");
      return builder.result();
   }
   
   private def showIndex() {
      val builder = new StringBuilder();
      var ellipsis: Boolean = false;
      for((n) in (0..255)) {
    	 if (alphabetIndex(n) == -1) {
    	    if(!ellipsis) { builder.add("..."); ellipsis = true; }
    	 }
    	 else { builder.add((n as Char)+"("+alphabetIndex(n)+"), "); }
      }
      return builder.result();
   }
   
   private def showScores() {
      val builder = new StringBuilder();
      val size = alphabet.length();
      val last = size - 1;
      for((n) in (0..last)) {
         builder.add("'"+alphabet(n)+"'\t");
         for((m) in (0..last)) {
            builder.add(scoringMatrix(n*size + m)+"\t");
         }
         builder.add("\r\n");
      }
      return builder.result();
   }
   
}
  