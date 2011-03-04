

public class TestMethod {
 
 var a : int;

  def foo() = {
  finish {
  	async bar();
   	a = 6;
   }
}

 def bar () = {
 	a = 5;
 }

 public static def main(args:Rail[String]!)= {
		
		(new TestMethod()).bar();
	}



}