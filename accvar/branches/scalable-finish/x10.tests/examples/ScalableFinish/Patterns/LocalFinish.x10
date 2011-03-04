import x10.compiler.FinishAsync;
public class LocalFinish {
    public static def main(args: Rail[String]!) throws Exception{
            @FinishAsync(1,1,true,0)
            finish{
                    async{Console.OUT.println("1");}
                    async{Console.OUT.println("2");}
                    async{Console.OUT.println("3");}
                    async{Console.OUT.println("4");}
                    async{Console.OUT.println("5");}
            }
            Console.OUT.println("00000000000");
     }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
}
