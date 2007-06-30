/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
public class TestSimpleArrayMult extends x10Test {

	public boolean run() {
		final int N=99900;
		long start1 = System.currentTimeMillis();
		region(:rect) e= [1:N];
		//region r = region.factory.region(new region[]{e, e}); 
		dist(:rect) d= e->here;
		long regionStop = System.currentTimeMillis();
		int[:rect] ia =  new int[d];
		int[:rect] ib = new int[d](point [i]){ return i;};
		int[:rect] ic =  new int[d](point p){return 2;};
		long initStop = System.currentTimeMillis();
		for(point p: e){
			ia[p] = ib[p] * ic[p];
		}
		long multStop = System.currentTimeMillis();
		int sum = ia.sum();
		int expectedValue=(N * (N+1));
		//expectedValue = expectedValue * 2;
		System.out.println("expected vaule:"+expectedValue);
		chk(sum == expectedValue);
		
		long regionTime = regionStop = start1;
		long constructTime = initStop - regionStop;
		long multTime = multStop - initStop;
		System.out.println("Region construction time:"+(regionTime/1000.0));
		System.out.println("Array construction time :"+(constructTime/1000.0));
		System.out.println("Multiplication time     :"+(multTime/1000.0));
		return true;
	}

	
    public static void main(String[] args) {
    	new TestSimpleArrayMult().execute();
       
    }
  
}
