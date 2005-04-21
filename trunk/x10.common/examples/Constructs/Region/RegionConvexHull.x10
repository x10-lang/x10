
/**
 * @author kemal 4/2005
 *
 *Tests convex hull operation on regions. 
 *
 *Expected implementation of "convex hull" is simply a bounding box
 *
 * convexHull({(x1,y1),...(xN,yN)}) is
 *
 * [min(i=1:N)(xi):max(i=1:N)(xi),
 *  min(i=1:N)(yi):max(i=1:N)(yi)]
 *
 * A region is convex if it is equal to its bounding box.
 *
 */

public class RegionConvexHull {
	
	public boolean run() {
		distribution P=distribution.factory.unique();
		region R1 = [0:1,0:7]; // horizontal strip
		pr("R1",R1);
		chk(R1.isConvex());
		chk(R1.convexHull().equals(R1));
		region R2 = [4:5,0:7]; // horizontal strip
		pr("R2",R2);
		chk(R2.isConvex());
		chk(R2.convexHull().equals(R2));
		region R3 = [0:7,4:5]; // vertical strip
		pr("R3",R3);
		chk(R3.isConvex());
		chk(R3.convexHull().equals(R3));
		region R1orR2= (R1||R2);
		pr("R1orR2",R1orR2);
		chk(!R1orR2.isConvex());
		region R1orR2ConvexHull=R1orR2.convexHull();
		pr("R1orR2ConvexHull",R1orR2ConvexHull);
		chk(R1orR2ConvexHull.isConvex());
		chk(R1orR2ConvexHull.equals([0:5,0:7]));
		region R1orR2andR3= R1orR2 && R3;
		pr("R1orR2andR3",R1orR2andR3);
		chk(R1orR2andR3.equals([0:1,4:5] || [4:5,4:5]));
		chk(R1orR2.contains(R1orR2andR3) && R3.contains(R1orR2andR3));
		chk(!R1orR2andR3.isConvex());
		region R1orR2andR3ConvexHull=R1orR2andR3.convexHull();
		pr("R1orR2andR3ConvexHull",R1orR2andR3ConvexHull);
		chk(R1orR2andR3ConvexHull.isConvex());
		chk(R1orR2andR3ConvexHull.equals([0:5,4:5]));
		region R1orR2orR3= R1 || R2 || R3;
		pr("R1orR2orR3",R1orR2orR3);
		chk(R1orR2orR3.equals([0:1,0:7]||[4:5,0:7]||
			[2:3,4:5] || [6:7,4:5]));
		chk(R1orR2orR3.contains(R1) &&  R1orR2orR3.contains(R2) &&
			R1orR2orR3.contains(R3));
		chk(!R1orR2orR3.isConvex());
		region R1orR2orR3ConvexHull=R1orR2orR3.convexHull();
		pr("R1orR2orR3ConvexHull",R1orR2orR3ConvexHull);
		chk(R1orR2orR3ConvexHull.isConvex());
		//just the bounding box for all points in region
		chk(R1orR2orR3ConvexHull.equals([0:7,0:7]));
		region R1orR2minusR3 = R1orR2-R3;
		pr("R1orR2minusR3",R1orR2minusR3);
		chk(R1orR2minusR3.equals([0:1,0:3]||[0:1,6:7] ||
		   [4:5,0:3]||[4:5,6:7]));
		chk(R1orR2.contains(R1orR2minusR3) && R1orR2minusR3.disjoint(R3));
		chk(!R1orR2minusR3.isConvex());
		region R1orR2minusR3ConvexHull=R1orR2minusR3.convexHull();
		pr("R1orR2minusR3ConvexHull",R1orR2minusR3ConvexHull);
		chk(R1orR2minusR3ConvexHull.isConvex());
		chk(R1orR2minusR3ConvexHull.equals([0:5,0:7]));
		region R4=([0:0,4:4]||[1:1,3:3] ||[5:5,2:2]||[3:3,6:6]);
		pr("R4",R4);
		region R4convexHull=R4.convexHull();
		pr("R4convexHull",R4convexHull);
		// just bounding box
		chk(R4convexHull.equals([0:5,2:6]));
 
		return true;
}

	static boolean iff(boolean x, boolean y) {
		return (x==y);
	}

	static void chk(boolean b) {
		if(!b) throw new Error();
 	}

	static void pr(String s,region r) {
		System.out.println();
		System.out.println("printing region "+s);
		int k=0;
		int N=8;
		for(point [i,j]:[0:N-1,0:N-1]) {
			
			System.out.print(" "+(r.contains([i,j])?"+":"."));		
			if((++k)%N ==0) System.out.println();
		}
	}


	public static void main(String args[]) {
		boolean b= (new RegionConvexHull()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
