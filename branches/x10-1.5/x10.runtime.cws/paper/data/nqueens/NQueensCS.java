/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

// Adapted from a cilk demo

import jsr166y.forkjoin.*;
import java.util.*;
import java.util.concurrent.atomic.*;

class NQueensCS extends ForkJoinTask<Void> {

    static int boardSize;
    static AtomicInteger nSolns = new AtomicInteger();

    static final int[] expectedSolutions = new int[] {
        0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
        73712, 365596, 2279184, 14772512};
    
    public static void main(String[] args) throws Exception {
        int procs;
        try {
            procs = Integer.parseInt(args[0]);
            System.out.println("Number of procs=" + procs);
        }
        catch (Exception e) {
            System.out.println("Usage: java NQueensCS <threads> ");
            return;
        }
        ForkJoinPool g = new ForkJoinPool(procs);
        for (int i = 4; i < 16; i++) {
            long s = System.nanoTime();
			
            boardSize = i;
            nSolns.set(0);
            ForkJoinTask<Void> task = new NQueensCS(new int[0]);
            Void result = g.invoke(task);
            long t = System.nanoTime();
            System.out.println(i+" " + (expectedSolutions[i] == nSolns.get() ? "ok" : "bad")
                               + " " + (t-s)/1000000 );
        }
        g.shutdown();    
    }


    // Boards are represented as arrays where each cell 
    // holds the column number of the queen in that row

    final int[] sofar;
    NQueensCS next;
    NQueensCS(int[] a) { 
        this.sofar = a;  
    }

    public Void compute() {
        int row = sofar.length;
        if (row >= boardSize) {
            nSolns.incrementAndGet();
            return null;
        }

        NQueensCS subtasks = null;
        for (int q = 0; q < boardSize; ++q) {
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
                NQueensCS s = new NQueensCS(next);
                s.next = subtasks;
                subtasks = s;
                s.fork();
            }
        }
        
        while (subtasks != null) {
            NQueensCS s = subtasks;
            subtasks = subtasks.next;
            s.next = null;
            s.join();
        }
        return null;
    }

}
