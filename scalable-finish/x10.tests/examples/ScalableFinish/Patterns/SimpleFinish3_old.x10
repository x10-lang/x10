import x10.compiler.FinishAsync;
public class SimpleFinish3_old {
    public static def main(args: Rail[String]!) throws Exception{

            var i:int = 0;
	    for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.MAX_PLACES);
	    	async(p1){    
             		@FinishAsync(1,1,false,0)
            		finish {
                    		for(var p:int = 0; p<Place.MAX_PLACES; p++){
                            		async(Place.places(p)){}
                     		}
            		}	
	    	}
	    }
 
   }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
}
