package x10.array;
import junit.framework.TestCase;
import x10.array.ContiguousRange;

import x10.array.Distribution_c;
import x10.lang.Runtime;
import x10.lang.place;
import x10.lang.dist;
import x10.lang.region;
import x10.runtime.Activity;
import x10.runtime.Configuration;


/**
 * @author Christian Grothoff, Christoph von Praun
 * @author vj
 */
public class TestDistribution_c extends TestCase {
    
    static {
        Runtime.init();
    }
    
    public void testDistribution_cyclic() {
    	Runtime.runAsync(new Activity() {
    		public void runX10Task() {
    			boolean assert1;
    			
    			int NP=x10.lang.place.MAX_PLACES;
    			region R=region.factory.region(0,NP-1);
    			dist D=dist.factory.block(R);
    			dist D3=dist.factory.cyclic(R);
    			assert1 = D.equals(D3);
    			
    			System.out.println(D);
    			System.out.println(D3);
    			
    			assertTrue(assert1);
    		}
    	});
    }
    
    public void testDistribution_subDistribution() {
    	Runtime.runAsync(new Activity() {
    		public void runX10Task() {
    			region r1 =region.factory.region(0,4);
    			
    			region r2 =region.factory.region(5,9);
    			region r3 =region.factory.region(0,9);
    			place p = Runtime.here();
    			dist d1 = dist.factory.constant(r1, p);
    			dist d2 = dist.factory.constant(r2, p);
    			dist d3 = dist.factory.constant(r3, p);
    			dist d4 = new Distribution_c.Combined(r3, new Distribution_c[] {(Distribution_c) d1, (Distribution_c)d2}, p);
    			
    			System.out.println("d1=" + d1);
    			System.out.println("d2=" + d2);
    			System.out.println("d3=" + d3);
    			System.out.println("d4=" + d4);
    			
    			assertTrue(d4.equals(d3));
    			// E_common=Distribution_c.Constant<region=|{[0],[1],[2],[3],[4]}|, place=|place(id=1)|>
    			// E_notCommon=Distribution_c.Constant<region=|{[5],[6],[7],[8],[9]}|, place=|place(id=1)|>
    			// E=Distribution_c.Constant<region=|0:9|, place=|place(id=1)|>
    			// union=CombinedDistribution_c<Distribution_c.Constant<region=|{[0],[1],[2],[3],[4]}|, place=|place(id=1)|>Distribution_c.Constant<region=|{[5],[6],[7],[8],[9]}|, place=|place(id=1)|
    		}
    	});
    }
    
    public void testDistribution_block() {
    	Runtime.runAsync(new Activity() {
    		public void runX10Task() {
    			boolean assert1, assert2, assert3;
    			
    			region cont = new ContiguousRange(37);
    			dist d = dist.factory.block(cont);
    			assert1 = d instanceof Distribution_c.Combined;
    			System.out.println("assert1 = " + assert1);
    			
    			assert2 = d.region.size() == 38;
    			System.out.println("assert2 = " + assert2);
    			
    			System.out.println(d.toString());
    			
    			assert3 = d.toString().equals("CombinedDistribution_c<Distribution_c.Constant<region=|0:18|, place=|place(id=0)|>Distribution_c.Constant<region=|19:37|, place=|place(id=1)|>>");
    			System.out.println("assert3 = " + assert3);
    			
    			assertTrue(assert1 && assert2 && assert3);
    		}
    	});
    }
    
    public void testDistribution_equals() {
    	Runtime.runAsync(new Activity() {
    		public void runX10Task() {
    			int N = 1;
    			
    			place p = Runtime.here();
    			
    			region R = region.factory.region(0, N);
    			dist D = dist.factory.constant(R, p); 
    			region R_local = region.factory.region(0, N);
    			dist D_local = dist.factory.constant(R_local, p);    			
    			dist D_nonlocal = D.difference(D_local.region);
    			
    			System.out.println("D_local =" + D_local);
    			System.out.println("D_nonlocal =" + D_nonlocal);
    			System.out.println("union =" + D_local.union(D_nonlocal));
    			System.out.println("D =" + D);
    			assertTrue(D_local.union(D_nonlocal).equals(D));
    		}
    	});
    }
    
    public void testDistribution_difference() {
        // must be a number that can be blocked on the 
        // given number of processors.
        
    	Runtime.runAsync(new Activity() {
    		public void runX10Task() {
    			int N = 12;
    			
    			boolean assert1, assert2, assert3, assert4, assert5, assert6;
    			
    			region R1 = region.factory.region(0, N + 1);
    			assert1 = R1.size() == N+2;
    			System.out.println("assert1 = " + assert1);
    			
    			region R2 = region.factory.region(1, N);
    			assert2 = R2.size() == N;
    			System.out.println("assert2 = " + assert2);
    			
    			region R = region.factory.region(R1, R1);
    			assert3 = R.size() == (N+2) * (N+2);
    			System.out.println("assert3 = " + assert3);
    			
    			region R_inner = region.factory.region(R2, R2);
    			assert4 = R_inner.size() == N * N;
    			System.out.println("assert4 = " + assert4);
    			
    			// test might fail for incorrect N.
    			dist D = dist.factory.block(R);        
    			dist D_inner = D.restriction(R_inner);
    			assert5 = D_inner.region.size() == N * N;
    			System.out.println("assert5 = " + assert5);
    			
    			dist D_Boundary = D.difference(D_inner.region);
    			assert6 = D_Boundary.region.size() == ((N+2) * (N+2)) - (N*N);
    			System.out.println("assert6 = " + assert6);
    			
    			assertTrue(assert1 && assert2 && assert3 && assert4 && assert5 && assert6);
    		}
    	});
    }
}