class Hello {
  var a : int;
  public def set(v: int): void{
      a = v;
  }

}


public class Test{
	   var z: int = 5;

   public  def run(): boolean {
       val c = Clock.make();
       shared var y :Int = 4;
     for(;;)
       async clocked(c) {
     y = 2;
     		if (1> 2) {
       		  y = 10;
       		  next;
       		  y = 11;
       		  next;
       		  y = 12;
        }
      else {
         y = 4;
         next;
         y = 7;
         next;
      }
     }
       
      
     
          y = 15;
         next;
         y = 16;
    
   
   
   }

  public static def main(var s: Rail[String]): void {
   (new Test()).run();
    
  
  }
}
