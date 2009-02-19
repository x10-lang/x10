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

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.105432
//@@X101X@@TCASE@@ArrayInitializer
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@ArrayInitializer.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@dims[4]=4 true
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
