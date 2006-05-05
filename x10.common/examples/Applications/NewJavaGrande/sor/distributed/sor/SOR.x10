/**
 * X10 port of sor benchmark from Section 2 of Java Grande Forum Benchmark Suite
 *
 *  SERIAL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 *
 * Porting issues identified:
 * 1) Replace Java multidimensional array by X10 Multidimensional array
 * 
 * @author xinb
 * 	o	remember G[i,*] lives at the same place
 * 	o	reduce the number of cross place async-launchings; try aggregate them;
 * 		In a lot of cases, this means watching the order of nested loop;
 */

package sor; 
import jgfutil.*; 
import x10.lang.Double;
public class SOR
{
	static final dist uniqueD = dist.factory.unique();	
	const Double gtotal = new Double(0.0);
	
	public static double read(final double[.] G, final int i, final int j) {
		return future (G.distribution[i,j]) {G[i,j]}.force();
	}
	public static final void SORrun(final double omega, final double[.] G, final int num_iterations)
	{
		final int M = G.distribution.region.rank(0).size();
		final int N = G.distribution.region.rank(1).size();
		
		final double omega_over_four = omega * 0.25;
		final double one_minus_omega = 1.0 - omega;
		
		
		// update interior points
		//
		final int Mm1 = M-1;
		final int Nm1 = N-1;
		
		JGFInstrumentor.startTimer("Section2:SOR:Kernel"); 
		
		for (point [p] : [0 : num_iterations-1]) {
			for (point [o] : [0:1]) {
				finish ateach(point [_]: uniqueD) {
					for (point [i,j] : G|here) {
						if(i > 0 && i < Mm1 && j > 0 && j < Nm1) { //skip bounds elements
							if(i%2 != o) { //pick one of two subsets of rows 
								G[i,j] = omega_over_four * (read(G, i-1,j) + read(G, i+1,j) + G[i,j-1] 
								          + G[i,j+1]) + one_minus_omega * G[i,j];
							}
						}
					}
				}
			}
		}
		
		JGFInstrumentor.stopTimer("Section2:SOR:Kernel");
		gtotal.val = G.sum();
		
	}
}


