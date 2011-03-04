import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Stack;

public class TreeExpander {
    static type TreeNode = UTS.TreeNode;
    static type SHA1Rand = UTS.SHA1Rand;
    static type Constants = UTS.Constants;
    private static val NORMALIZER = 2147483648.0; 
    
    public static def geometric (shapeFunction:int, /* 0..3*/
            rootBranchingFactor:int, /* self-expln */
            maxTreeDepth:int, /* cut off after this depth */
            node:TreeNode, /* random number generator */
            deque:Stack[TreeNode]) { /* The place to store */
        /* compute branching factor at this node */
        var curNodeBranchingFactor:double;
        
        if (0 == node.getDepth()) { /* root node */
            curNodeBranchingFactor = rootBranchingFactor;
        } else { /* calculate the branching factor for this node */
            if (Constants.EXPDEC == shapeFunction) { /* Exponential decrease */
                val tmpLogOne = -1.0 * Math.log (rootBranchingFactor as double);
                val tmpLogTwo = Math.log (maxTreeDepth as double);
                curNodeBranchingFactor = rootBranchingFactor  * 
                Math.pow (node.getDepth() as double, 
                        tmpLogOne/tmpLogTwo);
            } else if (Constants.CYCLIC == shapeFunction) { /* Cyclic */
                if (node.getDepth() > (5*maxTreeDepth)) {
                    curNodeBranchingFactor = 0.0;
                } else {
                    val TWO = 2.0;
                    val PI = 3.141592653589793;
                    val exponent = Math.sin (TWO*PI*(node.getDepth() as double)/
                            (maxTreeDepth as double));
                    
                    curNodeBranchingFactor = Math.pow (rootBranchingFactor, 
                            exponent);
                }
            } else if (Constants.FIXED == shapeFunction) { /* Fixed */
                curNodeBranchingFactor = (node.getDepth() < maxTreeDepth) ? 
                        rootBranchingFactor : /* true */
                            0; /* false */
            } else if (Constants.LINEAR == shapeFunction) { /* Linear --- default */
                curNodeBranchingFactor = rootBranchingFactor *
                (1.0 - (node.getDepth() as double)/
                        (maxTreeDepth as double));
            } else {
                curNodeBranchingFactor = 0;
                Console.OUT.println ("Unknown shape function for geometric UTS");
            }
        }
        
        /* Now, calculate the number of children */
        val probForCurNodeBranchingFactor = 1.0 / (1.0 + curNodeBranchingFactor);
        val randomNumber = node() as double;
        val normalizedRandomNumber = randomNumber / NORMALIZER;
        val numChildren = Math.floor ((Math.log (1-normalizedRandomNumber)) /
                (Math.log 
                        (1-probForCurNodeBranchingFactor))) as int;
        
        /* Push all the children onto the Deque (stack) */
        for (var i:Int=0; i<numChildren; ++i) 
            deque.push(TreeNode (node, i, node.getDepth()+1));
    }
    
    public static def binomial (q:Long, 
            m:int, 
            node:TreeNode,
            deque:Stack[TreeNode]) {
        val randomNumber:Long = node();
        val numChildren:Int = (randomNumber < q) ? m : 0;
        
        /* Push all the children onto the stack */
        for (var i:Int=0; i<numChildren; ++i) deque.push(TreeNode (node, i));
    }
    
    public static def processBinomialRoot (b0:Int, 
            node:TreeNode, 
            deque:Stack[TreeNode]) {
        for (var i:Int=0; i<b0; ++i) deque.push(TreeNode (node, i));
    }
}
