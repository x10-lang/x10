import x10.compiler.FinishAsync;
public class SimpleFinish4 {
    public static def main(args: Rail[String]!) throws Exception{
	    var i:int = 0;
	    for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.MAX_PLACES);
	    	async(p1){    
		    @FinishAsync(1,1,false,4)
                    finish {
                        for(var p:int = 0; p<Place.MAX_PLACES; p++){
                            async(Place.places(p)){
                                for(var pp:int = 0; pp<Place.MAX_PLACES; pp++){
                                     val i = pp;
                                     async{}
                                }
                            }
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
