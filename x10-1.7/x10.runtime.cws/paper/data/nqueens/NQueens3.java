package jsr166y.forkjoin;


/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 * 
 * vj: This returns the number of solutions as an int. However, this kills
 * performance because of all the boxing of ints.
 */

//Adapted from a cilk demo

import java.util.*;


public class NQueens3 extends ForkJoinTask<Integer> {
	
	static int boardSize;
	
	public static final int[] expectedSolutions = new int[] {
		0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
		73712, 365596, 2279184, 14772512};
	
	static int[] solns;
	
	public static void main(String[] args) throws Exception {
		int procs;
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
			solns= new int[procs];
		}
		catch (Exception e) {
			System.out.println("Usage: java NQueens <threads> ");
			return;
		}
		ForkJoinPool g = new ForkJoinPool(procs, null); // new Summer());
		for (int i = 4; i < 16; i++) {
			long s = System.nanoTime();
			
			boardSize = i;
			
			ForkJoinTask<Integer> task = new NQueens3(new int[0]);
			Integer result = g.invoke(task);
			long t = System.nanoTime();
			System.out.println(i+" " + (expectedSolutions[i] +":" +  result.intValue()) // ? "ok" : "bad")
					+ " " + (t-s)/1000000 );
		}
		g.shutdown();    
	}
	
	// Boards are represented as arrays where each cell 
	// holds the column number of the queen in that row
	
	final int[] sofar;
	NQueens3(int[] a) { 
		this.sofar = a;  
	}
	
	public Integer compute() {
		int row = sofar.length;
		if (row >= boardSize) {
			return new Integer(1);
		}
		
		ArrayList<NQueens3> subtasks = new ArrayList<NQueens3>();
		for (int q = 0; q < boardSize && !isDone(); ++q) {
			// Check if can place queen in column q of next row
			boolean attacked = false;
			for (int i = 0; i < row && ! attacked; i++) {
				int p = sofar[i];
				attacked = (q == p || q == p - (row - i) || q == p + (row - i));
			}
			// Fork to explore moves from new configuration
			if (!attacked) { 
				int[] next = new int[row+1];
				for (int k = 0; k < row; ++k) 
					next[k] = sofar[k];
				next[row] = q;
				NQueens3 s = new NQueens3(next);
				s.fork();
				subtasks.add(s);
			}
		}
		Iterator<NQueens3> it = subtasks.iterator();
		int ans=0;
		while (it.hasNext()) {
			NQueens3 s = it.next();
			Integer i = s.join();
			ans += (i==null ? 0 : i.intValue());
		}
		return ans == 0? null : new Integer(ans);
	}
	
}

