package com.ibm.ia.analytics.utils;

import x10.util.Box;
/**
  Adapted to X10 from Java.
  Original code by Robert Sedgewick, taken from 
  http://www.cs.princeton.edu/~rs/talks/LLRB/Java/RedBlackBST.java
  on Wed 06 Jun 2012 06:10:57 AM EDT

  @author vj
 */

public class LLRBTreeSet[K,V](species:Int, comparator:(K,K)=>Int, zero:Box[V]) {
    public static class EmptyTreeException extends Exception {}
    private static val BST=0,TD234=1,BU23=2,RED=true,BLACK=false;

    private var root:Node[K,V];       // root of the BST
    private var k:Int;                // ordinal for drawing
    private var heightBLACK:Int;      // black height of tree 

    public def this(){K <: Comparable[K], V haszero} {
        this(Zero.get[V]());
    }
    public def this(var c:(K,K)=>Int, var zero:V) {
        this(BST,c,zero);
    }    
    public def this(var zero:V){K <: Comparable[K]} {
        this(BST,(a:K,b:K)=>a.compareTo(b),zero);
    }
    public def this(species:Int,c:(K,K)=>Int){V haszero}{
        this(species,c,Zero.get[V]());
    }
    def this(species:Int,c:(K,K)=>Int,var zero:V) {
        property(species,c,new Box(zero));
    }
    private static class Node[K,V] {
        var key:K;                  // key
        var value:V;              // associated data
        var left:Node[K,V], right:Node[K,V];         // left and right subtrees
        var color:Boolean;            // color of parent link
        var N:Long;            // number of nodes in tree rooted here
        var height:Int;       // height of tree rooted here

        def this(key:K, value:V) {
            this.key=key;
            this.value=value;
            this.color=RED;
            this.N=1;
            this.height=1;
        }
        def copy():Node[K,V] {
            val result=new Node[K,V](key,value);
            result.color=color;
            result.N=N;
            result.height=height;
            if (left!=null) result.left=left.copy();
            if (right!=null) result.right=right.copy();
            return result;
        }
    }//Node

    public def copy():LLRBTreeSet[K,V] {
        val result = new LLRBTreeSet[K,V](species,comparator,zero());
        if (root!=null) result.root=root.copy();
        result.k=k;
        result.heightBLACK=heightBLACK;
        return result;
    }
    public def visit(visitor:(K,V)=>void):void {
        if (root != null)
            visit(visitor, root);
    }
    private def visit(visitor:(K,V)=>void, x:Node[K,V]) {
        if(x==null) return;
        visit(visitor,x.left);
        visitor(x.key,x.value);
        visit(visitor,x.right);
    }
    /** visitor is applied in order. visit is aborted
        at the first node which returns true. Returns
        true if the visitor aborted.
     */
    public def visit(visitor:(K,V)=>Boolean):Boolean=visit(visitor,root);
    private def visit(visitor:(K,V)=>Boolean, x:Node[K,V]):Boolean
        = (x!=null)
        &&(visit(visitor,x.left) 
           ||visitor(x.key,x.value)
           ||visit(visitor,x.right));

    public def size():Long=size(root);
    private def size(x:Node[K,V]):Long=x==null?0L:x.N;

    public  def rootRank():Long=root==null?0L:size(root.left);

    public  def height():Int=height(root);
    private def height(x:Node[K,V]):Int=x==null?0:x.height;

    public  def heightB():Int=heightBLACK;

    public  def contains(key:K):Boolean=get(root,key)!=null;  

    public  operator this(key:K):V= get0(root,key);
    private def get0(x:Node[K,V],key:K):V = {
        val g=get(root,key);
        g==null?zero():g.value
    };
    private def get(x:Node[K,V], key:K):Node[K,V]
        = x==null?null
        :eq(key,x.key)?x
        :less(key,x.key)?get(x.left,key)
        :get(x.right,key);
    
    public  def min():K= {
        if (root==null) throw new EmptyTreeException();
        min(root)
    }
    private def min(x:Node[K,V]):K=x.left==null?x.key:min(x.left);
    
    public  def max():K= {
        if (root==null) throw new EmptyTreeException();        
        max(root)
    }
    private def max(x:Node[K,V]):K=x.right==null?x.key:max(x.right);
    public  operator this(key:K)=(value:V):void{
        root = insert(root, key, value);
        if (isRed(root)) heightBLACK++;
        root.color = BLACK;
    }
    
    private def insert(var h:Node[K,V], key:K, value:V):Node[K,V] {
        if (h == null) return new Node[K,V](key, value);
        
        if (species == TD234) 
            if (isRed(h.left) && isRed(h.right))
                colorFlip(h);
        
        if (eq(key, h.key)) h.value=value;
        else if (less(key, h.key)) h.left=insert(h.left,key,value); 
        else h.right=insert(h.right,key,value); 
        
        if (species==BST) return setN(h);
        
        if (isRed(h.right)) h=rotateLeft(h);
        
        if (isRed(h.left) && isRed(h.left.left))h=rotateRight(h);
        
        if (species==BU23)
            if (isRed(h.left) && isRed(h.right))
                colorFlip(h);
        
        return setN(h);
    }

    
    public def deleteMin():void {
        root=deleteMin(root);
        if (root!=null) root.color=BLACK;
    }
    
    private def deleteMin(var h:Node[K,V]):Node[K,V] {
        if (h.left == null) return null;
        
        if (!isRed(h.left) && !isRed(h.left.left))h=moveRedLeft(h);
        
        h.left=deleteMin(h.left);
        
        return fixUp(h);
    }
    
    public def deleteMax():void {
        root=deleteMax(root);
        root.color=BLACK;
    }
    
    private def deleteMax(var h:Node[K,V]):Node[K,V] {
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
    
    public def delete(key:K):void {
        root = delete(root, key);
        if (root!=null) root.color = BLACK;
    }
    
    private def delete(var h:Node[K,V], key:K):Node[K,V] {
    	if (less(key, h.key)) {
    		if (!isRed(h.left) && !isRed(h.left.left)) h=moveRedLeft(h);
    		h.left=delete(h.left,key);
    	}
        else {
            if (isRed(h.left)) h=rotateRight(h);
            if (eq(key, h.key) && (h.right==null)) return null;
            if (h.right!=null) {
                if (!isRed(h.right) && !isRed(h.right.left)) h = moveRedRight(h);
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
    
    private def less(a:K, b:K):Boolean =comparator(a,b)<0; 
    private def eq  (a:K, b:K):Boolean =comparator(a,b)==0; 
    
    private def isRed(x:Node[K,V])=x==null?false:x.color==RED;
    private def colorFlip(h:Node[K,V]) {
        h.color        = !h.color;
        h.left.color   = !h.left.color;
        h.right.color  = !h.right.color;
    }

    // Make a right-leaning 3-node lean to the left.
    private def rotateLeft(h:Node[K,V]):Node[K,V] {
        val x = h.right;
        h.right = x.left;
        x.left = setN(h);
        x.color      = x.left.color;
        x.left.color = RED;
        return setN(x);
    }

    // Make a left-leaning 3-node lean to the right.
    private def rotateRight(h:Node[K,V]):Node[K,V] {
        val x = h.left;
        h.left = x.right;
        x.right = setN(h);
        x.color       = x.right.color;                   
        x.right.color = RED;                     
        return setN(x);
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private def moveRedLeft(var h:Node[K,V]):Node[K,V] {
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
    private def moveRedRight(var h:Node[K,V]):Node[K,V] {
        colorFlip(h);
        if (isRed(h.left.left)) { 
        	h = rotateRight(h);
        	colorFlip(h);
        }
        return h;
    }
    
    private def fixUp(var h:Node[K,V]):Node[K,V] {
        if (isRed(h.right))                      h=rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h=rotateRight(h);
        if (isRed(h.left) && isRed(h.right))     colorFlip(h);
        if (h.left!=null && isRed(h.left.right)&&isRed(h.left.left)) {
            h.left=rotateLeft(h.left);
            if (isRed(h.left)) h=rotateRight(h);
        }
        return setN(h);
    }
    
    private def setN(h:Node[K,V]):Node[K,V] {
        h.N = size(h.left) + size(h.right) + 1;
        if (height(h.left) > height(h.right)) h.height = height(h.left) + 1;
        else                                  h.height = height(h.right) + 1;
        return h;
    }

    public def toString():String=root==null?"":heightB() + " " + toString(root);  
    public def toString(x:Node[K,V]):String {
        var s:String = "n(";
        if (x.left == null) s += "(null"; else s += toString(x.left);
        s +=",";
        if (isRed(x)) s += "*";
        s +=x.key+"->"+x.value+",";
        if (x.right == null) s += "null"; else s += toString(x.right);
        return s + ")";
    }
    
    
    // Integrity checks
    
    /** Is this tree a red-black tree?*/
    public def  check():Boolean = isBST() && is234() && isBalanced();
    private def isBST():Boolean=size()==0L||isBST(root, min(), max());    

    
    private def isBST(x:Node[K,V], min:K, max:K):Boolean {
        // Are all the values in the BST rooted at x between min and max,
        // and does the same property hold for both subtrees?
        var result:Boolean=true;
        try {
        if (x == null) return result=true;
        if (less(x.key, min) || less(max, x.key)) return result=false;
        return result=isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
        } finally {
            val r=result;
            Logger.debug(()=>"isBST(" + x + ","+min+","+max+")=>" + r);
        }
    } 
    
    private def is234():Boolean=is234(root); 
    private def is234(x:Node[K,V]):Boolean{
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
            val r=result;
            Logger.debug(()=>"is234(" + x +")=>" + r);            
        }
    } 
    
    private def isBalanced():Boolean {
    	// Do all paths from root to leaf have same number of black edges?
        var black:Int = 0;     // number of black links on path from root to min
        var x:Node[K,V] = root;
        while (x != null) {
            if (!isRed(x)) black++;
            x = x.left;
        }
        return isBalanced(root, black);
    }
    
    private def isBalanced(x:Node[K,V], var black:Int):Boolean {
        // Does every path from the root to a leaf have the given number 
        // of black links?
        var result:Boolean=true;
        try {
            if      (x == null && black == 0) return result=true;
            else if (x == null && black != 0) return result=false;
            if (!isRed(x)) black--;
            return result=isBalanced(x.left, black) && isBalanced(x.right, black);
        } finally {
            val r=result;
            val b=black;
            Logger.debug(()=>"isBalanced(" + x +"black="+b+")=>" + r);                        
        }
    } 
    
}
