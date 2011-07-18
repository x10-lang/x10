import x10.compiler.FinishAsync;
public class LocalFinish {
    public static def main(args: Array[String](1)) //throws Exception
    {
            @FinishAsync(1,1,true,1)
            finish{
                    async{}
                    async{}
                    async{}
                    async{}
                    async{}
            }
     }
}
