import x10.compiler.FinishAsync;
public class SimpleFinish2 {
    public static def main(args: Rail[String]!) throws Exception{

            
            var i:int = 0;
	    for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.MAX_PLACES);
	    	async(p1){    
 			val p = here;
            		@FinishAsync(1,1,false,4)
            		finish{
                    		async(p.next()){}
            		}
		}
	    }
            
     }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
}
