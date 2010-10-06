import x10.io.Console;
import x10.compiler.*;
class UniqueFinish1 {
  public static def main(args:Rail[String]!) {
        val d = Dist.makeUnique();
        @FinishAsync(0,0,true,3)
        finish{
                @AteachUniDist
                ateach(p in d){
                        Console.OUT.println(p);
                }
        }
        Console.OUT.println("over");
  }
}


