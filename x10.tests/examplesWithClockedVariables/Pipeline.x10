import clocked.Clocked;


public class Pipeline {


   public def pipeline() {
  		val c = Clock.make(); 
   		val op = Int.+;
      	shared var a: int @ Clocked[int] (c, op) = 0;
   		shared var b: int @ Clocked[int] (c, op) = 0;
 	    async clocked(c)  {
                        var i: int;
                        for (i = 0; i < 10; i++)  {
                                a = i;
                                next;  /*write phase over */
                        }
                }
                async clocked (c) {
                        var i: int;
                        for (i = 0; i < 10; i++)  {
                                next; /*write phase over */
                                b = a + 1;
                        }
                }
                var i: int;
                next; /*write phase over */
                for (i = 0; i < 10; i++)  {
                        next; /*write phase over */
                        val o = b + 1;
                        Console.OUT.println(o);
                 }
        }

   

    public static def main(args:Rail[String]!) {
         val h = new Pipeline();  // final variable
         h.pipeline();
    }

}
