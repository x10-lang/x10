import x10.compiler.FinishAsync;
public class LocalFinish {
    public static def main(args: Rail[String]) //throws Exception
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
