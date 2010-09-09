public class ArrayInitializer {
	public static void main(String[] a) {
		final int [:self.rect && self.rank==1]  dims = 
		    (int  [:self.rect && self.rank==1]) 
		    new int [[0:8]] (point [i]){ return (i==0) ? 0 
		      : (i==1) ? 1
		      : (i==2) ? 2
		      : (i==3) ? 3
		      : (i==4) ? 4
		      : (i==5) ? 5
		      : (i==6) ? 6
		      : (1==7) ? 7
                      : 8;
		    };
		    finish ateach(point [p] : dist.UNIQUE) {
		    	if (p==0)
		    	System.out.println("dims[4]=" + dims[4] + " " + (dims[4]==4));
		    }
	}
}