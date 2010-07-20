/** x10doc comment for Named */
interface Named {
	def name():String;
}

/** x10doc comment for Hello */
public class Hello implements Named {
	static type Sn = String{self!=null};
	var member : Int = 0;

	static struct Polar(r:Double, theta:Double){
		def this(r:Double, theta:Double) {property(r,theta);}
		static val Origin = Polar(0,0);
		static val x0y1 = Polar(1, 3.14159/2);
	}
	
    public static def main(args: Rail[String]!) {
         if (args.length > 0) {
           Console.OUT.println("The first arg is: "+args(0));
         }
         Console.OUT.println("Hello X10 world");
         
         var example : Sn;
         
         type Rail(n:Int) = Rail[Int]{self.length == n};
         var example2 : Rail(78);
         
         val sq: (Int) => Int = (n:Int) => {
	         var s : Int = 0;
	         val abs_n = n < 0 ? -n : n;
	         for ((i) in 1..abs_n) s += abs_n;
	         s
         };
    }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
    
    public def name():String = {
    	var s : String = "";
        s += member;
    	return s;
    }
}