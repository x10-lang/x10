import x10.compiler.FinishAsync;
import x10.util.Timer;
public class SimpleFinish5 {
    public static def main(args: Rail[String]) //throws Exception
    {
	    var i:int = 0;
	   val start = Timer.milliTime();
	   finish{ 
	   for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.numPlaces());
	    	async at (p1){    
		    @FinishAsync(1,1,false,2)
                    finish {
			for(var j:int = 0; j< 500; j++){
                        	async{}
			}
                        for(var p:int = 0; p<Place.numPlaces(); p++){
                            val p_ = p;
                            async at (Place.place(p_)){
                                for(var pp:int = 0; pp<50; pp++){
                                     async{}
                                }
                            }
                        }
                        for(var p3:int = Place.numPlaces()-1; p3>=0;p3--){
                            val p3_ = p3;
                            async at (Place.place(p3_)){}
                        }
                    }
		}
	    }}
	    val end = Timer.milliTime();
	    Console.OUT.println("time = "+ (end - start) + " milliseconds");
     }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
}
