import clocked.Clocked;


public class Pipeline2 {


   public def pipeline() {
   		finish {
   		val op = Int.+;
      	shared clocked a: int = 0;
   		shared clocked(a) b: int  = 0;
 	    async  {
              var i: int;
              for (i = 0; i < 10; i++)  {
                  a = i;
                  next;  /*write phase over */
                  }
        }
        async  {
       			next;
                 var i: int;
                 for (i = 0; i < 10; i++)  {
                           
                           b = a + 1;
                           next; /*write phase over */
                 }
       }
       var i: int;
       next; /*write phase over */
       next;
       for (i = 0; i < 10; i++)  {
            
                val o = b + 1; 
                Console.OUT.println(o);
                next; /*write phase over */
       }
     }
   }

   

    public static def main(args:Rail[String]!) {
         val h = new Pipeline();  // final variable
         h.pipeline();
    }

}
