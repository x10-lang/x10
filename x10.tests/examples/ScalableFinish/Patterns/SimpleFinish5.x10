import x10.compiler.FinishAsync;
public class SimpleFinish5 {
    public static def main(args: Rail[String]!) throws Exception{
	    var i:int = 0;
	    for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.MAX_PLACES);
	    	async(p1){    
		    @FinishAsync(1,1,false,4)
                    finish {
                        async{}
                        for(var p:int = 0; p<Place.MAX_PLACES; p++){
                            async(Place.places(p)){
                                for(var pp:int = 0; pp<Place.MAX_PLACES; pp++){
                                     val i = pp;
                                     async{}
                                }
                            }
                        }
                        async(Place.places(Place.MAX_PLACES/2)){}
                        for(var p:int = Place.MAX_PLACES-1; p>=0;p--){
                            async(Place.places(p)){
                                for(var pp:int = 0; pp<Place.MAX_PLACES; pp++){
                                    val i = pp;
                                    async{}
                                }
                             }
                        }
                        async(Place.places(Place.MAX_PLACES-1)){}
                        for(var p3:int = Place.MAX_PLACES-1; p3>=0;p3--){
                            async(Place.places(p3)){}
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
