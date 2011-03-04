/*
 * Created on Nov 3, 2004
 */
package x10.array;

import java.util.Iterator;

import junit.framework.TestCase;
import x10.lang.Runtime;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.Activity;
import x10.runtime.Configuration;


/**
 * @author Christoph von Praun
 */
public class TestRegion extends TestCase {
	
	static {
		Runtime.init();
	}    
	
	public void testContiguous1() {
		Runtime.runAsync( new Activity() { public void runX10Task() {
			try {
				
				region r1 = new ContiguousRange(30);
				region r2 = new ContiguousRange(50);
				region r3 = r1.union(r2);
				assertTrue(r3.equals(r2));
			} catch (Exception e) {
				System.err.println("Exception" + e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}});
	}
	
	public void testContiguous2() {
		Runtime.runAsync( new Activity() { public void runX10Task() {
			try {
				
				region r1 = new ContiguousRange(10, 30);
				region r2 = new ContiguousRange(5, 25);
				region r3 = new ContiguousRange(5, 30);
				region r4 = r1.union(r2);
				assertTrue(r3.equals(r4));
			} catch (Exception e) {
				System.err.println("Exception" + e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}});
	}
	
	public void testContiguous3() {
		Runtime.runAsync( new Activity() { public void runX10Task() {
			try {
				
				region r1 = new ContiguousRange(10, 30);
				region r2 = new ContiguousRange(50, 100);
				region r3 = new EmptyRegion(1);
				region r4 = r1.intersection(r2);
				assertTrue(r3.equals(r4));
			} catch (Exception e) {
				System.err.println("Exception" + e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}});
	}
	
	public void testContiguous4() {
		Runtime.runAsync( new Activity() { public void runX10Task() {
			try {
				
				region r1 = new ContiguousRange(10, 30);
				region r2 = new ContiguousRange(50, 100);
				region r3 = r1.union(r2);
				
				region r4 = new ContiguousRange(10, 100);
				region r5 = new ContiguousRange(31, 49);
				region r6 = r4.difference(r5);
				
				assertTrue(r3.equals(r6));
			} catch (Exception e) {
				System.err.println("Exception" + e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}});
	}
	
	public void testRegion_iterator0() {
		region[] ranges = new region[] { new ContiguousRange(2, 4)};        
		ArbitraryRegion reg = new ArbitraryRegion(ranges);
		
		int cnt = 0;
		for (Iterator it = reg.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			System.out.println(p);
			cnt++;
		}
		assertTrue(cnt == 3);
	}
	
	public void testRegion_iterator1() {
		Runtime.runAsync( new Activity() { public void runX10Task() {
			region[] ranges = new region[] { new ContiguousRange(1,3), new ContiguousRange(2, 4)};         
			
			ArbitraryRegion reg = new ArbitraryRegion(ranges);
			
			int cnt = 0;
			for (Iterator it = reg.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				System.out.println(p);
				cnt++;
			}
			assertTrue(cnt == 9);
		}});
	}
	
	public void testRegion_iterator2() {
		Runtime.runAsync( new Activity() { public void runX10Task() {
			region[] ranges = new region[] { new ContiguousRange(1,3), new ContiguousRange(2, 4), new ContiguousRange(1,3)};         
			
			ArbitraryRegion reg = new ArbitraryRegion(ranges);
			
			int cnt = 0;
			for (Iterator it = reg.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				System.out.println(p);
				cnt++;
			}
			assertTrue(cnt == 27);
		}});
	}
}
