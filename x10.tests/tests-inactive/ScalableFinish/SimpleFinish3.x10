import x10.compiler.FinishAsync;
import x10.util.Timer;
public class SimpleFinish3 {
    public static def main(args: Rail[String]) //throws Exception
    {
    val start = Timer.milliTime();
    finish{
            var i:int = 0;
	    for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.numPlaces());
	    	async at (p1){    
             		@FinishAsync(1,1,false,2)
            		finish {
                    		for(var p:int = 0; p<Place.numPlaces(); p++){
                    		    val p_ = p;
                            		async at (Place.place(p_)){}
                     		}
            		}	
	    	}
	    }
    }
    val end = Timer.milliTime();
    Console.OUT.println("time = "+(end-start)+" milliseconds");
   }
}
