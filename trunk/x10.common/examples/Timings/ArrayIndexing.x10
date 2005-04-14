import x10.lang.*;
import java.lang.reflect.*;
/**
 * Synthetic benchmark to time arary accesses
 */

public class ArrayIndexing {
	String _tests[] = {"testDouble"};
	
	static final boolean verbose=false;
	double[.] _doubleArray1D;
	double[.] _doubleArray2D;
	double[.] _doubleArray3D;
	double[.] _doubleArray4D;
	
	int [.] _intArray1D;
	int [.] _intArray2D;
	int [.] _intArray3D;
	int [.] _intArray4D;
	
	public ArrayIndexing(){
		final int kArraySize=2000;
		region range1D,range2D,range3D,range4D;
		
		// Note: cannot do anything fancy with starting index--assume 0 based
		range1D = [0:kArraySize];
		range2D = [0:kArraySize,0:kArraySize];
		range3D = [1:4,3:4,1:20];
		range4D = [0:10,0:4,2:10,1:20];
		System.out.println("Testing double arrays...");
		long start = System.currentTimeMillis();
		_doubleArray1D = new double[distribution.factory.block(range1D)];
		_doubleArray2D = new double[distribution.factory.block(range2D)];
		_doubleArray3D = new double[distribution.factory.block(range3D)];
		_doubleArray4D = new double[distribution.factory.block(range4D)];
		long stop = System.currentTimeMillis();
		System.out.println("Double arrays allocated in "+((double)(stop-start)/1000)+ "seconds");
		
		 start = System.currentTimeMillis();
		_intArray1D = new int[distribution.factory.block(range1D)];
		_intArray2D = new int[distribution.factory.block(range2D)];
		_intArray3D = new int[distribution.factory.block(range3D)];
		_intArray4D = new int[distribution.factory.block(range4D)];
		 stop = System.currentTimeMillis();
		System.out.println("int arrays allocated in "+((double)(stop-start)/1000)+ "seconds");
	
		
		
	}
	
	boolean verify3D(double [.] array){
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high() ;
		int l1 =  array.distribution.region.rank(0).low();
		int l2 =  array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int count = 0;
		for(int i = l1; i <= h1;++i)
			for(int j = l2; j <= h2;++j)
				for(int k = l3; k <= h3;++k){
					array[i,j,k] = array[i,j,k];
					if(verbose) System.out.println("a["+i+","+j+","+k+"]="+count);
					if( array[i,j,k] != count){
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i,j,k]+")!="+count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(double [.] array){
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high();
		int h4 = array.distribution.region.rank(3).high();
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int l4 = array.distribution.region.rank(3).low();
		int count = 0;
		for(int i = l1; i <= h1;++i)
			for(int j = l2; j <= h2;++j)
				for(int k = l3; k <= h3;++k)
					for(int l = l4; l <= h4;++l){
						array[i,j,k,l] = array[i,j,k,l]; // ensure set works as well
						if(verbose) System.out.println("a["+i+","+j+","+k+","+l+"]="+count);
						if( array[i,j,k,l] != count){
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i,j,k,l]+")!="+count);
							return false;
						}
						++count;
					}
		return true;
	}
	boolean verify3D(int [.] array){
		
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high();
		
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
	
		int count = 0;
		for(int i = l1; i <= h1;++i)
			for(int j = l2; j <= h2;++j)
				for(int k = l3; k <= h3;++k){
					//System.out.println("value is:"+array[i,j,k]);
					array[i,j,k] = array[i,j,k];
					if(verbose) System.out.println("a["+i+","+j+","+k+"]="+count);
					if( array[i,j,k] != count){
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i,j,k]+")!="+count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(int [.] array){
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high();
		int h4 = array.distribution.region.rank(3).high();
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int l4 = array.distribution.region.rank(3).low();
		int count = 0;
		for(int i = l1; i <= h1;++i)
			for(int j = l2; j <= h2;++j)
				for(int k = l3; k <= h3;++k)
					for(int l = l4; l <= h4;++l){
						array[i,j,k,l] = array[i,j,k,l]; // ensure set works as well
						if(verbose) System.out.println("a["+i+","+j+","+k+","+l+"]="+count);
						if( array[i,j,k,l] != count){
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i,j,k,l]+")!="+count);
							return false;
						}
						++count;
					}
		return true;
	}
	void initialize(double [.] array){
		distribution arrayDist = array.distribution;
		int count=0;
		for(point p:array.distribution.region) {
			array[p] = count++;	
			if(verbose) System.out.println("init:"+p+"="+count);
		}
	}
	void initialize(int [.] array){
		distribution arrayDist = array.distribution;
		int count=0;
		for(point p:array.distribution.region) {
			array[p] = count++;	
			if(verbose) System.out.println("init:"+p+"="+count);
		}
	}
	
	public static void main(String args[]) {
		boolean b= (new ArrayIndexing()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
	boolean runDoubleTests(){
		System.out.println("Testing Doubles...");
		long start = System.currentTimeMillis();
		initialize(_doubleArray3D);
		if(!verify3D(_doubleArray3D)) return false;
		
		initialize(_doubleArray4D);
		if(!verify4D(_doubleArray4D)) return false;
		long stop = System.currentTimeMillis();
		System.out.println("Testing of double arrays took "+((double)(stop-start)/1000));
		return true;
	}
	
	boolean runIntTests(){
		System.out.println("Testing Ints...");
		long start = System.currentTimeMillis();
		initialize(_intArray3D);
		if(!verify3D(_intArray3D)) return false;
		
		initialize(_intArray4D);
		if(!verify4D(_intArray4D)) return false;
		
		long stop = System.currentTimeMillis();
		System.out.println("Testing of int arrays took "+((double)(stop-start)/1000));
		return true;
	}
	
	boolean run(){
		
		if(!runDoubleTests()) return false;
		if(!runIntTests()) return false;
		return true;
	}
	
	
	


void  testDouble2D(int iterations,boolean verbose){
	
	//if(verbose) System.out.println("testDouble2d called with "+iterations);
	long start,stop;
	start = System.currentTimeMillis();
	for(int i=0;i<iterations;++i)
		for(int j=0;j< iterations;++j){
			_doubleArray2D[i,j] = _doubleArray2D[i,j] + i+j;
		}
	stop = System.currentTimeMillis();
	if(verbose)System.out.println("testDouble2d("+iterations+") elapsed time:"+((double)(stop-start)/1000));
}
void  testDouble1D(int iterations,boolean verbose){
	
	if(verbose) System.out.println("testDouble1d called with "+iterations);
	long start,stop;
	start = System.currentTimeMillis();
	for(int i=0;i<iterations;++i){
			_doubleArray1D[i] = _doubleArray1D[i] + i;
		}
		stop = System.currentTimeMillis();
		if(verbose)System.out.println("testDouble1d("+iterations+") elapsed time:"+((double)(stop-start)/1000));
	}
}