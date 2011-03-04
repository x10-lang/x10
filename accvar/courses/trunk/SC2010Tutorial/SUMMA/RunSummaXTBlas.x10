/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2010.
 */

import x10.io.Console;
import x10.util.Timer;
import x10.util.Random;

//import SummaTeam;
import SummaTeamBlas;

/**
   This is an example of implementing SUMMA algorithm in X10.
   <p>
   The inter-place communication, broadcast row-wise and column-wise
   is handled by Team
   <p>
   Tow input matrices are partitioned in the same way.
   Each matrix is partitioned in a (rowBlock X colBlock) grid among all places.
   We use column-wise indexing to order matrix blocks as follows:

   -----------------------------------------------------------------------------------
   b(0),          b(rowBlock),     b(rowBlock*2),   ..... b(rowBlock*(colBlock-1))
   b(1),          b(rowBlock+1),   b(rowBlock*2+1), ..... b(rowBlock*(colBlock-1)+1)
   .....
   b(rowBlock-1), b(rowBlock*2-1), b(rowBlock*3-1), ..... b(rowBlock*colBlock -1)
   ----------------------------------------------------------------------------------
 */


//public class RunSummaXT { //This is X10-Team SUMMA, matrix multiply is coded in X10

public class RunSummaXTBlas { //This is BLAS-based X10-Team implementation of SUMMA
    
    //==================================================================
    // --------- Matrix dimension size ------------
    val M:Int;
    // --------- Summa Panel size ------------
    val P:Int;
    //------------ Matrix data ----------------
    val A:DistArray[Array[Double](1)](1); //Input matrix
    val B:DistArray[Array[Double](1)](1); //Input matrix
    val C:DistArray[Array[Double](1)](1); //Output matrix
    //
    //------------ Matrix data partition in grid ------------
    val rowBlock:Int; // Number of row blocks
    val colBlock:Int; // Number of column blocks 
    //
    // Block size in the grid
    val rowBs:Array[Int](1); //Number of rows in each row blocks in the grid
    val colBs:Array[Int](1); //Number of column in each column blocks in the grid
    //
    //----------------- Block distribution --------------
    val dist:Dist;
    //
    //==========================================================================
    
    //------------ Constructor ----------------
    public def this(m:Int, p:Int) {
	M =m; P=p;
	//-------------------------------------
	// Partition all places in a grid in square, or close to square
	val numPlace = Place.MAX_PLACES;
	var rb:Int = Math.sqrt(numPlace as Float) as Int;
	if (rb == 0) rb = 1;
	while (numPlace % rb != 0) { rb--; }
	//
	rowBlock = rb;
	colBlock = numPlace / rb;
	//--------------------------------------
	rowBs = new Array[Int](rowBlock);
	colBs = new Array[Int](colBlock);
	//
	computeBlockSize(rowBs, M);
	computeBlockSize(colBs, M);
	//
	//Create unique distribution, each place is assigned 
	// with one data index
	dist = Dist.makeUnique();

	//
	//------------- Distribute matrix data in block among all places --
	// Initialize the unique data distribution for matrix. 
	// Allocate and set data of a matrix block on each place
	A = DistArray.make[Array[Double](1)](dist); 
	setDistMatrix(A, rowBs, colBs); 

	B = DistArray.make[Array[Double](1)](dist);
	setDistMatrix(B, rowBs, colBs);

	C = DistArray.make[Array[Double](1)](dist);
	setDistMatrix(C, rowBs, colBs);
    }
    
    //--------------------------------------------
    //------------ Compute block size  -----------
    public static def computeBlockSize(blist:Array[Int](1), n:Int) {
	val b:Int = blist.size();
	val bs:Int = n / b;
	var k:Int  = n % b;
	for (var i:Int=0; i< b; i++) {
	    if (k>0) {
		blist(i) = bs + 1; 
		k--;
	    } else {
		blist(i) = bs;
	    }
	}
    }
    
    //-------------------------------------------------------
    //--------Creating distributed matrix blocks ---------
    public static def setDistMatrix(mat:DistArray[Array[Double](1)](1), 
				    row_blist:Array[Int](1), 
				    col_blist:Array[Int](1)) {
	
	val rb = row_blist.size();
	finish ateach (val [p] :Point in mat.dist()) {
	    val rb_idx = p % rb;            // Get the block's row block index
	    val cb_idx = p / rb;            // Get the block's column block index
	    val row_M  = row_blist(rb_idx); // Number of rows in this block
	    val col_N  = col_blist(cb_idx); // Number of columns in this block
	    val randval = new Random(p);
	    mat(p) = new Array[Double](row_M * col_N, 
				       //(i:Int)=>1.0*p+1);
				       (i:Int)=>randval.nextDouble()*100);
	}
    }
    //----------------------------------------------------
    // Print block matrix data 
    public def printMatrix(M:Int, N:Int, data:Array[Double](1)) {
	
	Console.OUT.printf("------------------------------------\n");
	Console.OUT.printf("Matrix in %ix%i\n", M, N);
	Console.OUT.flush();
	for (var r:Int=0; r<M; r++){
	    for (var c:Int=0; c<N; c++)
		Console.OUT.printf("%4.3f ", data(c*M+r));
	    Console.OUT.println();
	    Console.OUT.flush();
	}
    }
    // Print distributed matrix data in blocks
    public def printMatrix(dmat:DistArray[Array[Double](1)](1)) {
	
	for (val [p]:Point in dmat.dist()) {
	    val mdata = at(dmat.dist([p])) dmat(p); // Bring the remote data to here
	    val bM = rowBs(p%rowBlock);
	    val bN = colBs(p/rowBlock);
	    printMatrix(bM, bN, mdata);
	}
    }
    //-------------------------------------------------------
    public def run() {
	
	Console.OUT.printf("Starting X10 SUMMA-Team-BLAS: Matrix:%ix%i, %ix%i blocks(places), panel:%i\n", 
			   M, M, rowBlock, colBlock, P);
	Console.OUT.flush();
	
	//You may want to see what is the matrix
	//printMatrix(A);
	
	//-----------------------Profiling instrumentation-------------------
	val start_t = Timer.milliTime();
	//-------------------------------------------------------------------
	
	// val xt = new SummaTeam(P, rowBs, colBs, colBs);
	// Using BLAS for matrix multiply
	val xt= new SummaTeamBlas(P, rowBs, colBs, colBs); 
	xt.mult(1.0, A, B, 0.0, C);
	
	// You may want to see the result
	// printMatrix(C);
	
	//-----------------------Profiling instrumentation-------------------
	val total_t = 1.0*(Timer.milliTime() - start_t) / 1000; // In seconds
	//-------------------------------------------------------------------
	
	// Print out the used time and MFLOPS info
	val mfps = 2.0*M*M*M/total_t*1.0e-6;
	Console.OUT.printf("X10 SUMMA-Team-BLAS (%ix%x): Run time: %3.3f (s)  MFLOPS: %6.3f (%6.3f for each proc)\n", 
			   M, M, total_t, mfps, mfps/Place.MAX_PLACES); 
	Console.OUT.flush();
    }
    

    public static def main(args:Array[String](1)): Void {
	
	//The first option set the size of matrix, default is 8
	val sM = args.size > 0 ? Int.parse(args(0)):8; 

	//The second set the panel size, default is 1
	val sP = args.size > 1 ? Int.parse(args(1)):1;
	
	Console.OUT.println("Set Matrix size:"+sM+", Panel size:"+sP);
	if ((sM<0) || (sP<1) )
	    Console.OUT.println("Error in settings");
	else
	    {
		//val summa =  new RunSummaXT(sM, sP);
		//Using BLAS-based routing
		val summa =  new RunSummaXTBlas(sM, sP); 
		summa.run();
	    }
    }

} 