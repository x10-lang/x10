
import x10.util.Box;
/**
  Adapted to X10 from Java.
  Original code by Robert Sedgewick, taken from 
  http://www.cs.princeton.edu/~rs/talks/LLRB/Java/RedBlackBST.java
  on Wed 06 Jun 2012 06:10:57 AM EDT

  @author vj
 */

public class LLRBTreeSetInt(species:Int, comparator:(Int,Int)=>Int, zero:Box[Int]) {
    public static class EmptyTreeException extends Exception {}
    private static val BST=0,TD234=1,BU23=2,RED=true,BLACK=false;

    private var root:Node;            // root of the BST
    private var k:Int;                // ordinal for drawing
    private var heightBLACK:Int;      // black height of tree 

    public def this() {
        this(BU23);
    }

    private def this(species:Int) {
        property(species,(a:Int,b:Int)=>a.compareTo(b),new Box[Int](0));
    }
    private static class Node {
        var key:Int;                  // key
        var value:Int;              // associated data
        var left:Node, right:Node;         // left and right subtrees
        var color:Boolean;            // color of parent link
        var N:Long;            // number of nodes in tree rooted here
        var height:Int;       // height of tree rooted here

        def this(key:Int, value:Int) {
            this.key=key;
            this.value=value;
            this.color=RED;
            this.N=1;
            this.height=1;
        }
        def copy():Node {
            val result=new Node(key,value);
            result.color=color;
            result.N=N;
            result.height=height;
            if (left!=null) result.left=left.copy();
            if (right!=null) result.right=right.copy();
            return result;
        }
        public def toString()
            =(left==null?"":left + ",")+key+(right==null?"":","+right);
        //=(isRed(this)?"*":" ")+"n(" + (left==null?"":left + ",")+key+"->"+value+(right==null?"":","+right)+")";

    }//Node

    public def copy():LLRBTreeSetInt {
        val result = new LLRBTreeSetInt(species);
        if (root!=null) result.root=root.copy();
        result.k=k;
        result.heightBLACK=heightBLACK;
        return result;
    }

    /** Visit the elements in this map, applying visitor to each one.
        The code in visitor should not mutate this map, else the results
        are unpredictable.
     */
    public def visit(visitor:(Int,Int)=>void):void {
        if (root != null)
            visit(visitor, root);
    }
    private def visit(visitor:(Int,Int)=>void, x:Node) {
        if (x.left!=null) visit(visitor,x.left);
        visitor(x.key,x.value);
        if (x.right!=null) visit(visitor,x.right);
    }
    public def visit(visitor:(Int)=>void):void {
        if (root != null)
            visit(visitor, root);
    }
    private def visit(visitor:(Int)=>void, x:Node) {
        if (x.left!=null) visit(visitor,x.left);
        visitor(x.key);
        if (x.right!=null) visit(visitor,x.right);
    }
    /** visitor is applied in order. visit is aborted
        at the first node which returns true. Returns
        true if the visitor aborted.
     */
    public def visit(visitor:(Int,Int)=>Boolean):Boolean {
        if (root != null)
            return visit(visitor, root);
        return false;
    }
    private def visit(visitor:(Int,Int)=>Boolean, x:Node):Boolean {
        if (x.left!=null)
            if (visit(visitor,x.left)) return true;
        if (visitor(x.key,x.value)) return true;
        if (x.right!=null) return visit(visitor,x.right);
        return false;
    }
    public def visit(visitor:(Int)=>Boolean):Boolean {
        if (root != null)
            return visit(visitor, root);
        return false;
    }
    private def visit(visitor:(Int)=>Boolean, x:Node):Boolean {
        if (x.left!=null)
            if (visit(visitor,x.left)) return true;
        if (visitor(x.key)) return true;
        if (x.right!=null) return visit(visitor,x.right);
        return false;
    }    

    public def size():Long=size(root);
    private def size(x:Node):Long=x==null?0L:x.N;
    public  def rootRank():Long=root==null?0L:size(root.left);
    public  def height():Int=height(root);
    private def height(x:Node):Int=x==null?0:x.height;
    public  def heightB():Int=heightBLACK;
    public  def contains(key:Int):Boolean=get(root,key)!=null;  

    public  operator this(key:Int):Int= get0(root,key);
    private def get0(x:Node,key:Int):Int = {
        val g=get(root,key);
        g==null?zero():g.value
    };
    private def get(x:Node, key:Int):Node
        = x==null?null
        :eq(key,x.key)?x
        :less(key,x.key)?get(x.left,key)
        :get(x.right,key);
    
    public  def min():Int= {
        if (root==null) throw new EmptyTreeException();
        min(root)
    }
    private def min(x:Node):Int=x.left==null?x.key:min(x.left);
    
    public  def max():Int= {
        if (root==null) throw new EmptyTreeException();        
        max(root)
    }
    private def max(x:Node):Int=x.right==null?x.key:max(x.right);
    public  operator this(key:Int)=(value:Int):void{
        root = insert(root, key, value);
        if (isRed(root)) heightBLACK++;
        root.color = BLACK;
    }

    public  operator this+(key:Int):void{
        root = insert(root, key);
        if (isRed(root)) heightBLACK++;
        root.color = BLACK;
    }        
    
    private def insert(var h:Node, key:Int, value:Int):Node {
        //val oldH=h==null?h:h.copy();
        //        Logger.debug(()=>"In insert("+oldH+","+key+")");        
        if (h == null) return new Node(key, value);
        
        if (species == TD234) 
            if (isRed(h.left) && isRed(h.right))
                colorFlip(h);
        
        if (eq(key, h.key)) h.value = value;
        else if (less(key, h.key)) h.left = insert(h.left, key, value); 
        else h.right = insert(h.right, key, value);
        //val hh=h;
        //Logger.debug(()=>"In insert("+oldH+","+key+")==>"+hh);
        if (species == BST) return setN(h);
        if (isRed(h.right)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left))h = rotateRight(h);
        if (species == BU23)
            if (isRed(h.left) && isRed(h.right))
                colorFlip(h);
        //val hh2=h;
        //Logger.debug(()=>"After rotations, in insert("+oldH+","+key+")==>"+hh2);        
        return setN(h);
    }

    private def insert(var h:Node, key:Int):Node {
        //val oldH=h==null?h:h.copy();
        //        Logger.debug(()=>"In insert("+oldH+","+key+")");        
        if (h == null) return new Node(key, key);
        
        if (species == TD234) 
            if (isRed(h.left) && isRed(h.right))
                colorFlip(h);
        
        if (eq(key, h.key)) {}
        else if (less(key, h.key)) h.left = insert(h.left, key); 
        else h.right = insert(h.right, key);
        //val hh=h;
        //Logger.debug(()=>"In insert("+oldH+","+key+")==>"+hh);
        if (species == BST) return setN(h);
        if (isRed(h.right)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left))h = rotateRight(h);
        if (species == BU23)
            if (isRed(h.left) && isRed(h.right))
                colorFlip(h);
        //val hh2=h;
        //Logger.debug(()=>"After rotations, in insert("+oldH+","+key+")==>"+hh2);        
        return setN(h);
    }

    
    public def deleteMin():void {
        if (root==null) return;
        root = deleteMin(root);
        if (root!=null) root.color = BLACK;
    }
    
    private def deleteMin(var h:Node):Node{
        if (h.left == null) return null;
        if (!isRed(h.left) && !isRed(h.left.left)) h = moveRedLeft(h);
        h.left = deleteMin(h.left);
        return fixUp(h);
    }
    
    public def deleteMax():void {
        if (root==null) return;        
        root = deleteMax(root);
        if (root!=null) root.color = BLACK;
    }
    
    private def deleteMax(var h:Node):Node{
        //      if (h.right == null)
        //      {  
        //         if (h.left != null)
        //            h.left.color = BLACK;
        //         return h.left;
        //      }
        if (isRed(h.left)) h=rotateRight(h);
        if (h.right==null) return null;
        if (!isRed(h.right) && !isRed(h.right.left)) h = moveRedRight(h);
        h.right=deleteMax(h.right);
        return fixUp(h);
    }
    
    public def delete(key:Int):void {
        if (root==null) return;        
        root = delete(root, key);
        if (root!=null) root.color = BLACK;
    }
    
    private def delete(var h:Node, key:Int):Node {
        if (less(key, h.key)) {
            if (h.left!=null){
                if (!isRed(h.left) && !isRed(h.left.left)) h = moveRedLeft(h);
                h.left =  delete(h.left, key);
            } // if h.left==null then there is nothing to do, this key is not in this set
        } else {
            if (isRed(h.left)) h = rotateRight(h);
            if (eq(key, h.key) && (h.right == null))      return null;
            if (h.right!=null) {
                if (!isRed(h.right) && !isRed(h.right.left))  h = moveRedRight(h);
                if (eq(key, h.key)) {
                    h.value = get0(h.right, min(h.right));
                    h.key = min(h.right);
                    h.right = deleteMin(h.right);
                } else h.right = delete(h.right, key);
            }
        }
        
        return fixUp(h);
    }
    
    // Helper methods
    
    private def less(a:Int, b:Int):Boolean =comparator(a,b)<0; 
    private def eq  (a:Int, b:Int):Boolean =comparator(a,b)==0; 
    
    private def isRed(x:Node)=x==null?false:x.color==RED;
    private def colorFlip(h:Node) {
        h.color        = !h.color;
        h.left.color   = !h.left.color;
        h.right.color  = !h.right.color;
    }

    // Make a right-leaning 3-node lean to the left.
    private def rotateLeft(h:Node):Node {
        val x = h.right;
        h.right = x.left;
        x.left = setN(h);
        x.color      = x.left.color;                   
        x.left.color = RED;                     
        return setN(x);
    }

    // Make a left-leaning 3-node lean to the right.
    private def rotateRight(h:Node):Node {
        val x = h.left;
        h.left = x.right;
        x.right = setN(h);
        x.color       = x.right.color;                   
        x.right.color = RED;                     
        return setN(x);
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private def moveRedLeft(var h:Node):Node {
        colorFlip(h);
        if (isRed(h.right.left)) { 
        	h.right = rotateRight(h.right);
        	h = rotateLeft(h);
        	colorFlip(h);
        }
        return h;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private def moveRedRight(var h:Node):Node {
        colorFlip(h);
        if (isRed(h.left.left)) { 
        	h = rotateRight(h);
        	colorFlip(h);
        }
        return h;
    }
    
    private def fixUp(var h:Node):Node {
        if (isRed(h.right))  h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) colorFlip(h);
        if (h.left!=null && isRed(h.left.right) && isRed(h.left.left)) {
            h.left=rotateLeft(h.left);
            if (isRed(h.left)) h=rotateRight(h);
        }        
        return setN(h);
    }
    
    private def setN(h:Node):Node {
        h.N = size(h.left) + size(h.right) + 1;
        if (height(h.left) > height(h.right)) h.height = height(h.left) + 1;
        else                                  h.height = height(h.right) + 1;
        return h;
    }
    private def species()=species==BST?"BST":(species==TD234?"TD234":"BU23");
    public def toString():String="{"+(root==null?"":root.toString())+"}";  
                          //=root==null?"null":(species()+ " " + heightB() + " " + root.toString());  
    
    
    // Integrity checks
 
    
    /** Is this tree a red-black tree?insert*/
    public def  check():Boolean = isBST() && is234() && isBalanced();
    private def isBST():Boolean=size()==0L||isBST(root, min(), max());    

    
    private def isBST(x:Node, min:Int, max:Int):Boolean {
        // Are all the values in the BST rooted at x between min and max,
        // and does the same property hold for both subtrees?
        var result:Boolean=true;
        try {
        if (x == null) return result=true;
        if (less(x.key, min) || less(max, x.key)) return result=false;
        return result=isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
        } finally {
            //            val r=result;
            //            Logger.debug(()=>"isBST(" + x + ","+min+","+max+")=>" + r);
        }
    } 
    
    private def is234():Boolean=is234(root); 
    private def is234(x:Node):Boolean{
        // Does the tree have no red right links, and at most two (left)
        // red links in a row on any path?
        var result:Boolean=true;
        try {
        if (x == null) return result=true;
        if (isRed(x.right)) return result=false;
        if (isRed(x))
            if (isRed(x.left))
                if (isRed(x.left.left)) return result=false;
        return result=is234(x.left) && is234(x.right);
        } finally {
            //            val r=result;
            //            Logger.debug(()=>"is234(" + x +")=>" + r);            
        }
    } 
    
    private def isBalanced():Boolean {
    	// Do all paths from root to leaf have same number of black edges?
        var black:Int = 0;     // number of black links on path from root to min
        var x:Node = root;
        while (x != null) {
            if (!isRed(x)) black++;
            x = x.left;
        }
        return isBalanced(root, black);
    }
    
    private def isBalanced(x:Node, var black:Int):Boolean {
        // Does every path from the root to a leaf have the given number 
        // of black links?
        var result:Boolean=true;
        try {
            if      (x == null && black == 0) return result=true;
            else if (x == null && black != 0) return result=false;
            if (!isRed(x)) black--;
            return result=isBalanced(x.left, black) && isBalanced(x.right, black);
        } finally {
            //            val r=result;
            //            val b=black;
            //            Logger.debug(()=>"isBalanced(" + x +",black="+b+")=>" + r);                        
        }
    }
    public def equals(x:Any):Boolean = {
        if (! (x instanceof LLRBTreeSetInt)) return false;
        val y = x as LLRBTreeSetInt;
        size()==y.size() && ! visit((k:Int,Int)=> ! y.contains(k))
    }
}
