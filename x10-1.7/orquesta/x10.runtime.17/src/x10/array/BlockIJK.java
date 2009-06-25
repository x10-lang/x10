/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import x10.runtime.Place;
import x10.runtime.Runtime;


/**
 * Divides a given 3d region [lo1:hi1,lo2:hi2,lo3:hi3] into
 * a grid of p processors. p is a power of 2. 
 * We assume below that hi1-lo1=hi2-lo2=hi3-lo3=2*n.
 * The paritioning proceeds by splitting the k dimension, then the
 * j dimension, then the i dimension, and repeating until p blocks have been obtained.
 * @author VijaySaraswat
 *
 */
public class BlockIJK extends Distribution_c {
	public boolean powerOf2(int p) {
		if (p==0) return false;
		while (p>1) { p=p/2; if (p%2==1) return false;}
		return true;
	}
	public int log2(int p) {
		assert powerOf2(p);
		int i=0;
		while (p>1) { p=p/2; i++;}
		return i;
	}
	// returns 2^(max(0,i))
	public int pow2(int i) {
		int p = 1;
		for (int j=i; j > 0; j--) p*=2;
		return p;
	}
	final Region[] regions;
	
	final Region procGrid;
	final int x,y,z;
	final int sizeX,sizeY,sizeZ;
	public BlockIJK(Region r, int P) {
		super(r, Place.place(P));
		assert r instanceof MultiDimRegion;
		int p=log2(P);
		final int loX=r.rank(0).low(), hiX=r.rank(0).high(), I=hiX-loX+1;
		final int loY=r.rank(1).low(), hiY=r.rank(1).high(), J=hiY-loY+1;
		final int loZ=r.rank(2).low(), hiZ=r.rank(2).high(), K=hiZ-loZ+1;
		assert powerOf2(I)&& powerOf2(J) && powerOf2(K);
		int a=p/3;
		int b=p%3;
		x=a+(b>0?1:0); sizeX=I/pow2(x);
		y=a+(b>1?1:0); sizeY=J/pow2(y);
		z=a; sizeZ=K/pow2(z);
		regions = new Region[p];
		
		procGrid = new MultiDimRegion(new Region[]{ new ContiguousRange(0,x-1), 
			new ContiguousRange(0,y-1),
			new ContiguousRange(0,z-1)}, true);
		
				
		for (int pp=0; pp < p; pp++) {
			int q = pp;
			int k=q%K; q=q/K;
			int j=q%J; q=q/J;
			int i=q%I;
			// (i,j,k) in procGrid
			regions[pp]= new MultiDimRegion(new Region[] { new ContiguousRange(sizeX*i+loX+1,sizeX*(i+1)+loX),
					new ContiguousRange(sizeY*j+loY+1,sizeY*(j+1)+loY),
					new ContiguousRange(sizeZ*k+loZ+1,sizeZ*(k+1)+loZ)}, false);
		}
		
	}
    // return the ordinate of point(i,j,k) in the procGrid region.
	int ord(int i, int j, int k) {
		return k+z*(j+y*i);
	}

	public Place get(Point p) throws MalformedError {
		int[] r = p.val();
		int i=r[0]/sizeX;
		int j=r[1]/sizeY;
		int k=r[2]/sizeZ;
		return Runtime.place(ord(i,j,k));
	}
	
        /*
         *  (non-Javadoc)
         * @see x10.runtime.dist#getPerPlaceRegions()
         */
        public final Region[] getPerPlaceRegions(){
        	return regions;
        }
        
  

        /** Returns the region mapped by this distribution to the place P.
         The value returned is a subset of this.region.
         */
        public Region/*(rank)*/ restrictToRegion( Place P ) {
        	return regions[P.id];
        }
     
}
