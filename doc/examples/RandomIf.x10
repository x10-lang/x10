import x10.util.*;

class RandomIf {
    val random = new Random();
    public operator if(then: ()=>void, else_: ()=>void) {
        if (random.nextBoolean()) {
            then();
        } else {
            else_();
        }

    }


    public static def main(Rail[String]) {
	val random = new RandomIf();
	random.if () {
	    Console.OUT.println("true");
	} else {
	    Console.OUT.println("false");
	}

	random.operator if (() => { Console.OUT.println("true"); },
			    () => { Console.OUT.println("false"); });

    }

}
