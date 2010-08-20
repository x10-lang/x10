import x10.compiler.FinishAsync;
import x10.util.Timer;
public class SimpleFinish3_old {
    public static def main(args: Rail[String]!) throws Exception{
    val start = Timer.milliTime();
    finish{
            var i:int = 0;
	    for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.MAX_PLACES);
	    	async(p1){    
            		finish {
                    		for(var p:int = 0; p<Place.MAX_PLACES; p++){
                            		async(Place.places(p)){}
                     		}
            		}	
	    	}
	    }
    }
    val end = Timer.milliTime();
    Console.OUT.println("time = "+(end-start)+" milliseconds");
   }
}
