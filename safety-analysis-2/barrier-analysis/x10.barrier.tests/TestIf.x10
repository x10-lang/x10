public class TestIf {


public static def main(var s: Rail[String]): void {
    val c = new Clock();
    shared var a: int = 0;
    for (;;)
    async clocked (c) {
       if (1 > 2) {
          a = 2;
          next;
          a = 22;
       }
       else {
           a = 3;
           next;
           a = 33;
        }   
    }
  
  

}


}