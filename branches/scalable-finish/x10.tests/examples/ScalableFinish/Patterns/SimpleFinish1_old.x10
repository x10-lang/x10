import x10.compiler.FinishAsync;
import x10.util.Timer;
public class SimpleFinish1_old {
    public static def main(args: Rail[String]!) throws Exception{
    val start = Timer.milliTime();
            var i:int = 0;
	    for(i=0;i<1000;i++){
            		finish{
				for(var j:int = 0;j<Place.MAX_PLACES;j++){
				val p = Place.place(j);
				async(p){
					async{async{async{async{async{}}}}}
				}}
			} 
		}
    val end = Timer.milliTime();
    Console.OUT.println("time = "+(end-start)+" milliseconds");

     }
}
