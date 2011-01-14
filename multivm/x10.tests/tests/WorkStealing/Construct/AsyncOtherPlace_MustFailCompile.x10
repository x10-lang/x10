
package WorkStealing.Construct;
/*
 * Async in another place. Cannot pass WS compile
 */
public class AsyncOtherPlace_MustFailCompile {
    
    static class T {
        private val root = GlobalRef[T](this);
        transient var val_:Object;
    }
    
    public def run(): boolean = {
        val Other: Place = here.next();
        val t = new T();
        val troot = t.root;
        finish async at(Other) {
            val t1: T = new T();
            async at (troot) troot().val_ = t1;
        }
        val result = (t.val_ as T).root.home == Other;
        Console.OUT.println("AsyncOtherPlace: result = " + result);
        return result;
    }

    public static def main(Array[String](1)) {
        val r = new AsyncOtherPlace_MustFailCompile().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
    
    
}