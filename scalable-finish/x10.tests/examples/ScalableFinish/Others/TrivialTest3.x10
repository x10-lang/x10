
public class TrivialTest3 {
        public static def f1(var i:int){f2(i);}
        public static def f2(var i:int){}

	public static def main(args: Rail[String]) {
              finish{
                  async{f2(3);f2(4);}
                  at(here){f2(3);}
                  f1(3);
              }
	}
}


         
