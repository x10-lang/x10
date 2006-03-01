class RegionWithHoles {
	boolean _success=true;
	
	public static void main(String args[]) {
		RegionWithHoles t = new RegionWithHoles();
		t.test1D();
		t.test2D();
		t.test3D();
		t.test4D();
		t.testPoint();
		
		System.out.println("++++++ "+(t._success?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(t._success?0:1);
	}
	
	void chk(boolean b) {
		if(!b){
			_success=false;
			throw new Error();
		}
	}
	void test2D(){
		System.out.println("testing 2d");	
//		 all of those are contiguous
		region r = [0:10,0:3];
		region r1 = [1:2,0:3];
		region r2 = [5:6,0:3];
		
		//	 create wholes in r
		r = r - r1;
		r = r - r2;
		
		short[.] a = new short[r];
		
		//	 check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");
		chk(!r.isConvex());		
				
		try{
			for (point[i,j] : r) {
				if(a[i,j] != 0)
					System.out.println("val[" + i + "]=" + a[i,j]);
			}
		}
		catch(Throwable t){
			System.out.println(t);
			_success=false;
		}		
	}
	void test3D(){
//		 all of those are contiguous
		region r = [0:10,0:3,0:0];
		region r1 = [1:2,0:3,0:0];
		region r2 = [5:6,0:3,0:0];
		
		//	 create wholes in r
		r = r - r1;
		r = r - r2;
		
		short[.] a = new short[r];
		chk(!r.isConvex());		
		//	 check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");
		
	
		
		try{
			for (point[i,j,k] : r) {
				if(a[i,j,k] != 0)
					System.out.println("val[" + i + "]=" + a[i,j,k]);
			}
		}
		catch(Throwable t){
			System.out.println(t);
			_success=false;
		}
		}
	
	void test4D(){
//		 all of those are contiguous
		region r = [0:0,0:10,0:3,0:0];
		region r1 = [0:0,1:2,0:3,0:0];
		region r2 = [0:0,5:6,0:3,0:0];
		
		//	 create wholes in r
		r = r - r1;
		r = r - r2;
		
		short[.] a = new short[r];
		chk(!r.isConvex());		
		//	 check if r is convex - it should not!
		System.out.println("4d:convex: " + r.isConvex() + " (should be false)");
		
		if(false){
			System.out.print("indexes: ");
			for (point[i] : r) {
				System.out.print(i + " ");
			}
		}
		
		try{
			for (point[i,j,k,l] : r) {
				if(a[i,j,k,l] != 0)
					System.out.println("val[" + i + "]=" + a[i,j,k,l]);
			}
		}
		catch(x10.lang.Exception e){
			System.out.println(e);
			_success=false;
		}
		}
	void testPoint(){
		System.out.println("testing point");	
		//	 all of those are contiguous
		region r = [0:10];
		region r1 = [1:2];
		region r2 = [5:6];
		
		//	 create wholes in r
		r = r - r1;
		r = r - r2;
		
		short[.] a = new short[r];
		chk(!r.isConvex());		
		//	 check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");
		
		
		try{
			for (point p : r) {
				if(a[p] != 0)
					System.out.println("val[" + p + "]=" + a[p]);
			}
		}
		catch(Throwable t){
			System.out.println(t);
			_success=false;
		}		
	}
	
	
	void test1D(){
		//	 all of those are contiguous
		region r = [0:10];
		region r1 = [1:2];
		region r2 = [5:6];
		
		//	 create wholes in r
		r = r - r1;
		r = r - r2;
		
		short[.] a = new short[r];
		chk(!r.isConvex());		
		//	 check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");
		
		System.out.print("indexes: ");
		for (point[i] : r) {
			System.out.print(i + " ");
		}
		
		try{
			for (point[i] : r) {
				if(a[i] != 0)
					System.out.println("val[" + i + "]=" + a[i]);
			}
		}
		catch(Throwable t){
			System.out.println(t);
			_success=false;
		}		
	}
}
