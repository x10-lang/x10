/**************************************************************************
 *                                                                         *
 *         Java Grande Forum Benchmark Suite - Thread Version 1.0          *
 *                                                                         *
 *                            produced by                                  *
 *                                                                         *
 *                  Java Grande Benchmarking Project                       *
 *                                                                         *
 *                                at                                       *
 *                                                                         *
 *                Edinburgh Parallel Computing Centre                      *
 *                                                                         * 
 *                email: epcc-javagrande@epcc.ed.ac.uk                     *
 *                                                                         *
 *                                                                         *
 *      This version copyright (c) The University of Edinburgh, 2001.      *
 *                         All rights reserved.                            *
 *                                                                         *
 **************************************************************************/


package sparsematmult;
import jgfutil.*; 
import java.util.Random;

public class JGFSparseMatmultBench extends SparseMatmult implements JGFSection2{ 

    public int nthreads;

    private int size; 
    private static final long RANDOM_SEED = 10101010;

    // reduced data sizes for test suite
    //private static final int[] datasizes_M = {50000,100000,500000};
    private static final int[] datasizes_M = {500,100000,500000};
    //private static final int[] datasizes_N = {50000,100000,500000};
    private static final int[] datasizes_N = {500,100000,500000};
    //private static final int[] datasizes_nz = {250000,500000,2500000};
    private static final int[] datasizes_nz = {2500,500000,2500000};
    private static final int SPARSE_NUM_ITER = 200;

    Random R = new Random(RANDOM_SEED);

    double [.] x; 
    double [.] y; 
    double [.] val; 
    int [.] col;
    int [.] row;
    int [.] lowsum;
    int [.] highsum;

    public JGFSparseMatmultBench() {
	this.nthreads = place.MAX_PLACES;
    }

    public void JGFsetsize(int size){
	this.size = size;

    }

    public void JGFinitialise(){

	final dist d_places = dist.factory.unique(place.places);
	final region r_N = [0 : datasizes_N[size]-1]; 
	final region r_M = [0 : datasizes_M[size]-1]; 
	final region r_nz = [0 : datasizes_nz[size]-1]; 
	final region r_nthreads = [0 : nthreads-1]; 
	final dist d_N = dist.factory.block(r_N, place.places);
	final dist d_M = dist.factory.block(r_M, place.places);
	final dist d_nz = dist.factory.block(r_nz, place.places);
	final dist d_nthreads = dist.factory.unique(place.places);

	x = RandomVector(d_N, R); // distributed -- cvp
	y = new double[d_M];      // distributed -- cvp
	row = new int[d_nz];      // distributed -- cvp
	col = new int[d_nz];      // distributed -- cvp
	val = new double[d_nz];   // distributed -- cvp
	lowsum = new int[d_nthreads];       // distributed -- cvp
	highsum = new int[d_nthreads];      // distributed -- cvp

	int [] ilow = new int[nthreads];        // local temporary -- cvp
	int [] iup = new int[nthreads];         // local temporary -- cvp
	int [] sum = new int[nthreads+1];       // local temporary -- cvp
	int [] rowt = new int[datasizes_nz[size]];       // local temporary -- cvp
	int [] colt = new int[datasizes_nz[size]];       // local temporary -- cvp
	double [] valt = new double[datasizes_nz[size]]; // local temporary -- cvp
	int sect = (datasizes_M[size] + nthreads-1)/nthreads;

	// the initialization of the arrays low, col val cannot be don in array initializers, because 
	// of the random number generation and the final validation check. Consecutive random numbers 
	// are cyclically assigned to the arrays low, col and val.

	for (int i=0; i < datasizes_nz[size]; i++) {
	    final int ds_M = datasizes_M[size];
	    final int random_1 = R.nextInt();	// generate random row index (0, M-1)
	    final int random_2 = R.nextInt(); // generate random column index (0, N-1)
	    final double random_3 = R.nextDouble();
	    final int i_final = i;

	    finish async (d_nz.distribution[i]) {
		row[i_final] = Math.abs(random_1) % ds_M;
		col[i_final] = Math.abs(random_2) % ds_M;
		val[i_final] = random_3;
	    }
	}

	// reorder arrays for parallel decomposition

	for (int i=0; i < nthreads; i++) {
	    ilow[i] = i*sect;
	    iup[i] = ((i+1)*sect)-1;
	    if(iup[i] > datasizes_M[size]) iup[i] = datasizes_M[size];
	}

	for (int i=0; i < datasizes_nz[size]; i++) {
	    final int i_final = i;
	    int row_i = (future (row.distribution[i_final]) { row[i_final] }).force();
	    for (int j=0; j<nthreads; j++) {
		if((row_i >= ilow[j]) && (row_i <= iup[j])) { 
		    sum[j+1]++; 
		}
	    }         
	}

	// cvp - the sum array is place local and completely initialized 
	// at this point and I would like to make it available on all 
	// places ... but I cannot find a construct to do that in X10.
	// in that loop, hnec, the async is in the innermost iteration.

	for (int j=0; j<nthreads; j++) {
	    for (int i=0; i<=j; i++) {
		final int sum_ij = sum[j-i];
		final int j_final = j;
		finish async (d_nthreads[j_final]) {
		    lowsum[j_final] = lowsum[j_final] + sum_ij;
		    highsum[j_final] = highsum[j_final] + sum_ij;
		}
	    }
	}
	
	for (int i=0; i<datasizes_nz[size]; i++) {
	    final int i_final = i;
	    for (int j=0; j<nthreads; j++) {
		final int j_final = j;
		final int highsum_j = (future (highsum.distribution[j]) { highsum[j_final] }).force();
		int row_i = (future (d_nz.distribution[i]) { row[i_final] }).force();
		
		if((row_i >= ilow[j]) && (row_i <= iup[j])) {
		    // cvp would like to aggregate these 3 communications - but there seems to be no 
		    // easy way to do that.
		    rowt[highsum_j] = row_i;
		    colt[highsum_j] = (future (d_nz.distribution[i]) { col[i_final] }).force();
		    valt[highsum_j] = (future (d_nz.distribution[i]) { val[i_final] }).force();
		    finish async (highsum.distribution[j]) { highsum[j_final] += 1; };  // ++ postfix operator did not work -- cvp
		}
	    }
	}

	for (int i=0; i < datasizes_nz[size]; i++) {
	    final int rowt_i = rowt[i];
	    final int colt_i = colt[i];
	    final double valt_i = valt[i];
	    final int i_final = i;
	    finish async (d_nz.distribution[i]) {
		row[i_final] = rowt_i;
		col[i_final] = colt_i;
		val[i_final] = valt_i;
	    }
	}

    }
 
    public void JGFkernel(){

	SparseMatmult.test(y, val, row, col, x, SPARSE_NUM_ITER, lowsum, highsum);

    }

    public void JGFvalidate(){

	//double refval[] = {75.02484945753453,150.0130719633895,749.5245870753752};
	double refval[] = {0.7379886692958086,150.0130719633895,749.5245870753752};
	double dev = Math.abs(ytotal.val - refval[size]);
	if (dev > 1.0e-10 ){
	    System.out.println("Validation failed");
	    System.out.println("ytotal = " + ytotal.val + "  " + dev + "  " + size);
	    throw new Error("Validation failed");
	}

    }

    public void JGFtidyup(){
	System.gc();
    }  



    public void JGFrun(int size){


	JGFInstrumentor.addTimer("Section2:SparseMatmult:Kernel", "Iterations",size);

	JGFsetsize(size); 
	JGFinitialise(); 
	JGFkernel(); 
	JGFvalidate(); 
	JGFtidyup(); 

     
	JGFInstrumentor.addOpsToTimer("Section2:SparseMatmult:Kernel", (double) (SPARSE_NUM_ITER));
 
	JGFInstrumentor.printTimer("Section2:SparseMatmult:Kernel"); 
    }

    private static double[.] RandomVector(dist d, final java.util.Random R)
    {
	final double[.] A = new double[d] (point[i]) { return R.nextDouble() * 1e-6; };
	return A;
    }


}
