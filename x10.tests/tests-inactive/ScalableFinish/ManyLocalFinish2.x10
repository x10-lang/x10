import x10.compiler.FinishAsync;
import x10.util.Timer;
public class ManyLocalFinish2 {
    public static def main(args: Rail[String]) //throws Exception
    {
	    val start = Timer.milliTime();
            finish{
            var i:int = 0;
            for(i=0;i<1000;i++){
		val p = Place.place(i % Place.numPlaces());
	    	async at (p){
            	  @FinishAsync(1,1,true,1)
            	  finish{
		    for(var j:int=0;j<50;j++){
		      async{
		         for(var k:int = 0; k<50; k++){
			     async{}
			 }
		      }
		    }
            	  }
 	        }
	      }
	     }
	     val end = Timer.milliTime();
    	     Console.OUT.println("time = "+(end-start)+" milliseconds");
    }
}
