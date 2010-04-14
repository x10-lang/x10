// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import clocked.*;

/**
 * From the paper:
 *
 * The unbalanced tree search (UTS) problem is to count the number of
 * nodes in an implicitly constructed tree that is parameterized in
 * shape, depth, size, and imbalance.
 *
 * A node in a binomial tree has m children with probability q and has
 * no children with probability 1 - q, where m and q are parameters of
 * the class of binomial 3 trees. When qm < 1, this process generates
 * a finite tree with expected size 1/(1-qm).  Since all nodes follow
 * the same distribution, the trees generated are self-similar and the
 * distribution of tree sizes and depths follow a power law [3]. The
 * variation of subtree sizes increases dramatically as qm approaches
 * The root-specific branching factor b0 can be set sufficiently high
 * to generate an interesting variety of subtree sizes below the root
 * according to the power law.  Alternatively, b0 can be set to 1, and
 * a specific value of r chosen to generate a tree of a desired size
 * and imbalance.
 * 
 * @author bdlucas
 * 
 * 
 * 
 */
 
 import x10.io.Console;

 class Tile {
	    static def tile(r:Region(1), a:Region(1)): Rail[Region(1)] {
		assert a.min(0) == 0;
		val rBase = r.min(0);
		val rSize = r.max(0) - rBase+1;
		val aSize = a.max(0) - a.min(0)+1;
		val size = rSize / aSize; 
		val leftOver = rSize - size*aSize;

		val init = (i:Int) => {
		    val min = 
		    i < leftOver ? rBase + (size+1)*i
		    : rBase + size*i + leftOver;
		    val max = (min-1) + (i < leftOver ? size+ 1 : size);
		    return min..max as Region(1);
		};
		val result = Rail.make[Region(1)](aSize, init);
		return result;
	    }

	   /* public static def main(a:Rail[String]) {
		val rMin = 4 ; //Int.parseInt(a(0));
		val rMax = 8; // Int.parseInt(a(1));
		val aMax = 10; //Int.parseInt(a(2));
		val result = tile(rMin..rMax as Region(1), 0..aMax-1 as Region(1));
		for ((p) in 0..result.length-1) {
		    Console.OUT.println("result(" + p+ ")=" + result(p));
	      }
	   }*/

	}

 
 class UTSRand {

	    //
	    // For now use util.Random instead of SHA. To substitute SHA
	    // redefine descriptor, next(), and number().
	    //
	    // Instead of actually using util.Random, we replicate its
	    // function here to avoid allocating a Random object.
	    //

	    final static type descriptor = long;

	    final static def next(r:descriptor, i:int) {
	        var seed: long = r+i;
	        seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
	        for (var k:int=0; k<11; k++)
	            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
	        val l0: long = (seed >>> (48 - 32)) ;
	        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
	        val l1: long = (seed >>> (48 - 32)) ;
	        return ((l0 as long) << 32) + l1;
	    }

	    const scale = (long.MAX_VALUE as double) - (long.MIN_VALUE as double);

	    final static def number(r:descriptor) = (r / scale) - (long.MIN_VALUE / scale);

	}
 


class UTS {
 
    const r0 = 0;                      // seed for root
    const b0 = 300000;                  // branching factor of root node
    const q = 0.12;                    // prob of non-zero branching factor
    const m = 8;                       // branching factor is m with prob q
    val P:int;
    def expected() = 7338936.0;        // expected size given above params 

    
    val c = Clock.make();
    val op = Int.+;
     
    var size: int @ Clocked[int](c,op,0) = 0;
    var sumb: int @ Clocked[int](c,op,0) = 0;
  

    def this(P:int) 
    {
	 this.P=P;;
	 tiles = Tile.tile(0..b0-1 as Region(1), 0..P-1 as Region(1)) as Rail[Region(1)]!;
 		
    }
    def visit(r:UTSRand.descriptor,p:int)  @ClockedM(c) {
	val x = UTSRand.number(r);
        val b = x<q? m : 0; // binomial distributio
        sumb = b;
        size = 1;
       
        for (var i:int=0; i<b; i++)
	    visit(UTSRand.next(r,i),p);
    }
   def visitRegion(r:Region(1),p:int)  @ClockedM(c) {
	for (var i:int=r.min(0);i<= r.max(0); i++) {
	    visit(UTSRand.next(r0, i),p);
	}
    }

    global val tiles : Rail[Region(1)]!;

    def run() @ ClockedM(c) {
  
	
     for (var i:int=0;i<tiles.length;i++) {
         val j =i; 	  
	  async clocked(c)  visitRegion(tiles(j),j);
	}
	next;


        // sanity check on size and branching factor
    
            val expSize = b0 / (1.0 - q*m);
            val obsBranch = (sumb as double) / size;
            val expBranch = q * m;
            x10.io.Console.OUT.println("exp size / obs size: " 
				       + (expSize/size));
            x10.io.Console.OUT.println("exp branching / obs branching: " 
				       + (expBranch / obsBranch));
        
    
        // should always get same size tree
        return size as double;
    }
     
    public static def main(args: Rail[String]) {
	/*if (args.length < 1){
	    Console.OUT.println("Usage : <exec><P : int>");
	    return;
	}*/
	val P = 4; //Int.parseInt(args(0));
        new UTS(P).run();
    }
   
 

}
