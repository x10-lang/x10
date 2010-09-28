import x10.io.Console;
import x10.compiler.TailAsync;

class DelegatingFinish {
  public static def main(args:Rail[String]!) {
        finish{
            val p = here;
            for(var i:int=1;i<Place.MAX_PLACES;i++){
                val p1 = Place.place(i);
                    @TailAsync(true)
                    async(p1){
                        Console.OUT.println(here);
                        @TailAsync(false)
                        async(p){
                            Console.OUT.println(here);
                        }
                    }
            }
        }
        Console.OUT.println("Done");
  }
}


