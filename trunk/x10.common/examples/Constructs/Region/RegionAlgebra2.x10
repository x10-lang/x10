
/**
 * @author kemal 4/2005
 *
 *Tests convex hull, upper triangular, lower triangular regions, 
 *and banded regions
 *
 */

public class RegionAlgebra2 {
	
	public boolean run() {
		distribution P=distribution.factory.unique();
		region R1 = [0:1,0:7]; // horizontal strip
		pr("R1",R1);
		chk(R1.isConvex());
		region R2 = [4:5,0:7]; // horizontal strip
		pr("R2",R2);
		chk(R2.isConvex());
		region R3 = [0:7,4:5]; // vertical strip
		pr("R3",R3);
		chk(R3.isConvex());
		region R1orR2= (R1||R2);
		pr("R1orR2",R1orR2);
		chk(!R1orR2.isConvex());
		region R1orR2ConvexHull=R1orR2.convexHull();
		pr("R1orR2ConvexHull",R1orR2ConvexHull);
		chk(R1orR2ConvexHull.isConvex());
		region R1orR2andR3= R1orR2 && R3;
		pr("R1orR2andR3",R1orR2andR3);
		chk(R1orR2andR3.equals([0:1,4:5] || [4:5,4:5]));
		chk(R1orR2.contains(R1orR2andR3) && R3.contains(R1orR2andR3));
		chk(!R1orR2andR3.isConvex());
		region R1orR2andR3ConvexHull=R1orR2andR3.convexHull();
		pr("R1orR2andR3ConvexHull",R1orR2andR3ConvexHull);
		chk(R1orR2andR3ConvexHull.isConvex());
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
		region R1orR2minusR3 = R1orR2-R3;
		pr("R1orR2minusR3",R1orR2minusR3);
		chk(R1orR2minusR3.equals([0:1,0:3]||[0:1,6:7] ||
		   [4:5,0:3]||[4:5,6:7]));
		chk(R1orR2.contains(R1orR2minusR3) && R1orR2minusR3.disjoint(R3));
		chk(!R1orR2minusR3.isConvex());
		region R1orR2minusR3ConvexHull=R1orR2minusR3.convexHull();
		pr("R1orR2minusR3ConvexHull",R1orR2minusR3ConvexHull);
		chk(R1orR2minusR3ConvexHull.isConvex());
		region Universe=[0:7,0:7];
		region upperT=region.factory.upperTriangular(8);
		pr("upperT",upperT);
		for(point [i,j]:Universe)
		  chk(iff(i<=j,upperT.contains([i,j])));
		region lowerT=region.factory.lowerTriangular(8);
		pr("lowerT",lowerT);
		for(point [i,j]:Universe) 
		  chk(iff(i>=j,lowerT.contains([i,j])));
		region banded1=region.factory.banded(8,1);
		pr("banded1",banded1);
		for(point [i,j]:Universe) 
		  chk(iff(i==j,banded1.contains([i,j])));
		region banded2=region.factory.banded(8,2);
		pr("banded2",banded2);
		// not sure if 2nd band is to north or south of diagonal
		for(point [i,j]:Universe) 
		  chk(iff(j==i||j==i+1,banded2.contains([i,j])));
		region banded3=region.factory.banded(8,3);
		pr("banded3",banded3);
		for(point [i,j]:Universe) 
		  chk(iff(j==i-1||j==i||j==i+1,banded3.contains([i,j])));
		region banded4=region.factory.banded(8,4);
		pr("banded4",banded4);
		for(point [i,j]:Universe) 
		  chk(iff((j==i-1||j==i||j==i+1||j==i+2),
		          banded4.contains([i,j])));
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
		boolean b= (new RegionAlgebra2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
