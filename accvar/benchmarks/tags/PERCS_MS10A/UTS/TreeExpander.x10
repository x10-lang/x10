import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Stack;

public class TreeExpander {
  static type SHA1Rand = UTS.SHA1Rand;
  static type Constants = UTS.Constants;
	private static val NORMALIZER = 2147483648.0; 

  public static def geometric (shapeFunction:int, /* 0..3*/
                               rootBranchingFactor:int, /* self-expln */
                               maxTreeDepth:int, /* cut off after this depth */
                               node:SHA1Rand, /* random number generator */
                               deque:Stack[SHA1Rand]) { /* The place to store */

  }

  public static def binomial (q:Long, 
                              m:int, 
                              node:SHA1Rand,
                              deque:Stack[SHA1Rand]) {
    val randomNumber:Long = node();
    val numChildren:Int = (randomNumber < q) ? m : 0;

    /* Push all the children onto the stack */
    for (var i:Int=0; i<numChildren; ++i) deque.push(SHA1Rand (node, i));
  }

  public static def processBinomialRoot (b0:Int, 
                                         node:SHA1Rand, 
                                         deque:Stack[SHA1Rand]) {
    for (var i:Int=0; i<b0; ++i) deque.push(SHA1Rand (node, i));
  }
}
