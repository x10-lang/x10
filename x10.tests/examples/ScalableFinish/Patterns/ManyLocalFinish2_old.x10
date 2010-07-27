import x10.compiler.FinishAsync;
public class ManyLocalFinish2_old {
    public static def main(args: Rail[String]!) throws Exception{
            var i:int = 0;
            for(i=0;i<1000;i++){
		val p = Place.place(i % Place.MAX_PLACES);
	    	async(p){
            	  @FinishAsync(1,1,true,0)
            	  finish{
		    for(var j:int=0;j<Place.MAX_PLACES;j++){
		      async{}
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
