
public class PlaceCast {
	   int nplaces=0;

	    public boolean run() {         
	        final dist d=dist.factory.unique(place.places);
	        System.out.println("num places=" + place.places);
	        final BoxedBoolean[d] disagree 
			= new BoxedBoolean[d] (point [p]) { 
	        	System.out.println("The currentplace is:" + here);
	        	return new BoxedBoolean(); 
	        	};
	        finish ateach(final point [p]:d) {
	            // remember if here and d[p] disagree
	            // at any activity at any place
	        	try {
	        		BoxedBoolean@here x = (@d[p].next()) disagree[p];
	        		async(this){atomic {nplaces++;}}
	        	} catch (BadPlaceException x)  {
	        		System.out.println("Caught bad place exception for " + p);
	        	}
	        }
	        System.out.println("nplaces==" + nplaces);
	        return nplaces==0;
	    }
	        
	    public static void main(String[] args) {
	    	final BoxedBoolean b=new BoxedBoolean();
	    	try {
	    		finish async b.val=(new PlaceCast()).run();
	    	} catch (Throwable e) {
	    		e.printStackTrace();
	    		b.val=false;
	    	}
	        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
	        x10.lang.Runtime.setExitCode(b.val?0:1);
	    }
	    static class BoxedBoolean {
	        boolean val=false;
	    }

}