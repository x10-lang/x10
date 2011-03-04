class Hello {
  var a : int;
  public def set(v: int): void{
      a = v;
  }

}


public class Test{
	   var z: int = 5;

   public  def run(): boolean {
       val c = new Clock();

       shared var y :Int = 4;

   
       for(;;)
       async clocked(c) {
          y = 2;
          next;
          y = 3;
          next;
          y = 4;
       }
       for(;;) {
          y = 2;
          next;
          y = 3;
          next;
          y = 4;
       }
    
    }
       
     		  



  public static def main(var s: Rail[String]): void {
   (new Test()).run();
    
  
  }
}
