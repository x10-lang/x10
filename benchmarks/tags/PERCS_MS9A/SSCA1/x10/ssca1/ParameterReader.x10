package ssca1;
import x10.io.EOFException;
import x10.io.File;
import x10.io.FileReader;
import x10.io.IOException;
import x10.util.RailBuilder;
import x10.util.StringBuilder;
import x10.util.ValRailBuilder;

/**
 * An instance reads an ASCII character file in order to extract key value pairs.
 * This reader is very much tuned to what I see as the needs of SSCA1.  The 
 * basic sample file is:
 *      openGap = 2
 *      extendGap = 1
 *      alphabet = 'ABCD' # four entries, as in DNA: ABCD seemed as good 
 *                        # a four letter sequence as any for this purpose!
 *      scores = [
 *      #      A  B  C  D
 *             5 -3 -2 -3  # A
 *            -3  5 -3 -3  # B
 *            -2 -3  5 -3  # C
 *            -3 -3 -3  5  # D
 *      ]
 * There are three types of values we need: decimal integers, strings, and arrays of
 * decimal integers.  For purposes of allowing convenient documentation, we use the
 * standard comment convention that all text to the right of a hash sign (#) or a
 * semicolon (;) is ignored.  Arrays are square-bracket delimited and either white
 * space or comma-and-possibly-white-space delimited lists.  Strings are delimited
 * by apostrophes (') and only apostrophes, not quotation marks.  White space not
 * within a string value or being used as a delimiter is ignored.
 * 
 * The parse is line oriented: we read lines until we see a non-empty line whose
 * start looks like "key =".  We extract the key and, based on the first non-blank
 * character after the "=", we deduce the value type and extract that.  The key-
 * value pairs are kept in a hashtable in which the values are treated as Strings.
 * 
 * Public accessors are provided for getting the values converted to the types
 * that are actually needed: int, etc.
 */
public struct ParameterReader {
   private static DEBUG  = false;
   /**
    * the default Char for beginning a comment in the sequence input files--comments
    * end at the next CR or LF.
    */
   public static DEFAULT_COMMENT_START = '#';
   
   val keys:   ValRail[String];
   val values: ValRail[String];
   val commentStart: Int;
   val errors: String;
         
   /**
    * Use the default buffer size to read in the parameter file named by the argument.
    * @param path should specify an ASCII file of the format described in the initial comment
    */
   public def this(path: String) {
      this(path, DEFAULT_COMMENT_START);
   }
   
   /**
     * Use the default buffer size to read in the parameter file named by the argument.
     * @param path should specify an ASCII file of the format described in the initial comment
     */
   public def this(path: String, commentStart_: Char) {
      var status: String;
      val keys_ = new ValRailBuilder[String]();
      val values_ = new ValRailBuilder[String]();
      commentStart = commentStart_.ord();
      try {
         val relative = new File(path);
         val absolute = relative.getAbsoluteFile();
         val reader = absolute.openRead();
         for(status = readAParameter(reader, keys_, values_); 
             status.equals(""); 
             status=readAParameter(reader, keys_, values_)) {
          }
          reader.close();
      } catch(e: IOException) { status = e.getMessage(); }
      keys   = keys_.result();
      values = values_.result();
      errors = status.equals("EOF") ? "" : status;
   }
   
   /**
    * returns an array integers that, in our case, will be the scoring matrix, laid
    * out as a ValRail in row-major order, for the match.
    * @param key the left hand side of the assignment from the parameter file, normally
    *      "scores" for this particular call: see Parameters.x10.
    * @return 
    * @throws IllegalArgumentException if the key is not found
    * @throws NumberFormatException if the source string has a non-decimal entry as one
    *      of the Strings split out of the key's value.
    */
   public def getIntArrayProperty(key: String) {
      val raw = getRawValue(key);
      val builder = new ValRailBuilder[Int]();
      var start: Int = -1;
      for((i) in (0..raw.length()-1)) { // implements raw.split(/[^0-9-]/) the hard way.
         var c: Char = raw.charAt(i);
         var b: Int= c.ord();
         if ((b>=0x30 && b<=0x39) || b==0x2D) {
            if (start==-1) start = i;
         }
         else if (start != -1) {
            builder.add(Int.parseInt(raw.substring(start, i)));
            start = -1;
         }
      }
      if (start != -1) builder.add(Int.parseInt(raw.substring(start, raw.length())));
      return builder.result();
   }
   
   /**
    * retrieve an integer property value
    * @param key the name of the integer property.
    * @return the value found for the key
    * @throws IllegalArgumentException if the key is not found
    * @throws NumberFormatException if the value is not an ASCII decimal string
    */
   public def getNumericProperty(key: String) {
      return Int.parse(trim(getRawValue(key)));
   }
   
   /**
    * retrieves the value, trimmed of leading and trailing white space, 
    * that is associated with this key
    * @param key a property name
    * @return the value for this key
    * @throws IllegalArgumentException if the key is not found.
    */
   public def getStringProperty(key: String) {
      return trim(getRawValue(key));
   }
   
   /**
    * retrieves the value, AS IS, associated with the key
    * @param key a property name
    * @return the value for this key
    * @throws IllegalArgumentException if the key is not found.
    */
   private def getRawValue(key: String) {
      for((n) in (0..keys.length-1)) {
         if(keys(n).equals(key)) return values(n);
      }
      throw new IllegalArgumentException("Key, "+key+", not found.");
   }
   
   /**
    * remove both leading and trailing white space
    */
   private def trim(s: String) {
      var start: Int = 0;
      val size = s.length();
      while(start < size && s(start).ord()<=0x20) start += 1;
      var end: Int = size - 1;
      while(end >= start && s(end).ord()<=0x20) end -= 1;
      if (DEBUG) Console.ERR.println("trim: s(" + start + "," + end + ")='" + s.substring(start,end+1)+"'");
      return s.substring(start, end+1);
   }
   
   /**
    * reads the file byte by loving byte to find the line boundaries.  A 
    * line ends the comment start character, '#' by default, or a carriage-
    * return / line-feed, whichever comes first.
    * @param reader the stream from the file
    * @return the next non-empty line
    * @throws EOFException when attempting to read beyond the end of the file.
    * @throws IOException more generally if the reader fails.
    */
   private def readNonEmptyLine(reader: FileReader!): String throws IOException {
      val builder = new StringBuilder();
      // skip leading white space
      //Console.ERR.println("available? "+reader.available()); // !BUG
      var b: Byte = reader.readByte();
      while(true) {
         while(b <= 0x20) { b = reader.readByte(); }  //!BUG--isSpaceChar wrong
         while(b != 0xA && b != 0xD && b != commentStart) { // read until end of line or EOF
            builder.add(b as Char);
            b = reader.readByte();
         }
         val answer = builder.result();
         if (b == '#'.ord() || b == ';'.ord()) {
            try { while(b != 0xA && b != 0xC) b = reader.read(); }
            catch(e: Exception) {  }
         }
         if (DEBUG) Console.ERR.println("answer: '"+answer+"', length="+answer.length());
         if (answer.length() > 0) return answer;
         else if (DEBUG) Console.ERR.println("Try again");
         // else if (reader.available() > 0)  return readNonEmptyLine(reader);
         // else {  return ""; }
      }
   }
     
   /**
    * reads the input line by line, trimming comments and leading/trailing white
    * space until the terminating "'" character is read.  All characters from the
    * opening "'" that got us here to the closing "'" (not inclusive) are saved as
    * the value, except for line ending CR and LF characters.
    * @param reader a byte-oriented reader for the parameter file
    * @param values_ the array of values: this value is pushed onto the array
    * @param initialSegment the rest of the line in which the key was found
    * @return the empty string normally, an error message otherwise
    * @throws IOException if a read attempt fails for any reason other than EOF
    */
   private def readString(reader: FileReader!, values_:ValRailBuilder[String]!, initialSegment: String) {
      var endString: Int = initialSegment.indexOf("'");
      if (endString >= 0) {
         values_.add(initialSegment.substring(0, endString));
         return "";
      }
      val buffer = new StringBuilder();
      buffer.add(initialSegment);
      try {
         var line: String = readNonEmptyLine(reader);
         line = trim(line);
         while(true) {
            endString = line.indexOf("'");
            if (endString >= 0) {
               buffer.add(line.substring(0, endString));
               values_.add(buffer.result());
               return "";
            }
            else buffer.add(line);
         }
      } catch(e: Exception) { return e.getMessage(); }
   }
   
    /**
     * reads a line of input if need be to find the value for the given key;
     * the value is check to see that it is a valid ASCII decimal integer.
     * @param reader a byte-oriented reader for the parameter file
     * @param values_ the array of values: this value is pushed onto the array
     * @param initialSegment the rest of the line in which the key was found
     * @return the empty string normally, an error message otherwise
     * @throws IOException if a read attempt fails for any reason other than EOF
     */
   private def readDecimal(reader: FileReader!, values_:ValRailBuilder[String]!, initialSegment: String) {
      var line: String = initialSegment;
      try { 
         if(line.length() == 0) line = readNonEmptyLine(reader);
         Int.parseInt(line);
         values_.add(line);
         return "";
      }
      catch(nfe: NumberFormatException) {
          return "Expected a decimal, but found '"+line+"'\r\n";
       }
      catch(ioe: IOException) {
          return ioe.getMessage();
       }
   }
     
   /**
    * reads the input line by line, trimming comments and leading/trailing white
    * space until the termintating ] character is read.  All characters from the
    * opening [ that got us here to the closing ] (not inclusive) are saved as
    * the value.
    * @param reader a byte-oriented reader for the parameter file
    * @param values_ the array of values: this value is pushed onto the array
    * @param initialSegment the rest of the line in which the opening '[' was found
    * @return the empty string normally, an error message otherwise
    * @throws IOException if a read attempt fails for any reason other than EOF
    */
   private def readArray(reader: FileReader!, values_:ValRailBuilder[String]!, initialSegment: String) {
      val buffer = new StringBuilder();
      var line: String = initialSegment;
      try {
         while(true)  {
            if (DEBUG) Console.ERR.println("readArray: '"+line+"'");
            val endArray = line.indexOf("]");
            if (endArray >=0) {
               buffer.add(trim(line.substring(0, endArray)));
               values_.add(trim(buffer.result()));
               return "";
            }
            else buffer.add(line);
            buffer.add(" ");
            line = this.readNonEmptyLine(reader);
        }
      } catch(ioe: Exception) { return ioe.getMessage()+"\r\n"; }
   }
   
   /**
    * reads the next input line, the start of which is expected to have the format "key =".
    * To be precise, the line is expected to match
    *              \s*([^=]+)=.*
    * The key is extracted from the substring that matches the parenthesized expression
    * by trimming any trailing white space.  The next non-whitespace character after the
    * "=" sign determines the type of value to be read.  "[" implies an array of decimal
    * integers, "'" implies a string, and the characters [-0-9] imply a decimal integer.
    * @param reader a byte-oriented reader for the parameter file
    * @param keys_ builds a Rail of the key strings
    * @param values_ builds a Rail of the corresponding value strings.
    * @return a completion code: empty string means "everything ok", 'EOF' means "end of
    *   file".
    */
   private def readAParameter(reader: FileReader!, keys_: ValRailBuilder[String]!, values_: ValRailBuilder[String]!) {
      try {
         var line: String = readNonEmptyLine(reader);
         val firstEqual = line.indexOf("=");
         if (firstEqual <= 0) {
           return "Missing '=' in parameter start: '"+line+"'\r\n";
         }
         else {
            val key = trim(line.substring(0, firstEqual));
            keys_.add(key);
            if (DEBUG) Console.ERR.println("Parameter key = '"+key+"'");
            val initialSegment = trim(line.substring(firstEqual+1, line.length()));
            line = initialSegment.length() == 0 ? readNonEmptyLine(reader) : initialSegment;
            val size = line.length();
            val firstChar = line.charAt(0);
            if (DEBUG) Console.ERR.println("First char: '"+firstChar+"'");
            return (firstChar == '[') ?
               readArray(reader, values_, line.substring(1, size)) :
               (firstChar == '\'' ? readString(reader, values_, line.substring(1, size)) :
               readDecimal(reader, values_, line));
         }
      } catch (e1: EOFException) {
    	 if (DEBUG) Console.ERR.println("EOF thrown");
    	 return "EOF";
      } catch (e2: IOException) {
         return e2.getMessage();
      }
   }
}
