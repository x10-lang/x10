package bug081117;
public class Hello {
	public Hello() {
    }
    public static void main(String[] args) {
         supertest tt;
  	     final Test t=new T();
         async{
   		    System.out.println("Entered new activity");
   		    finish async (here.next()){
   			        x10.lang.Runtime.sleep(1000);
   		    }
   		 x10.lang.Runtime.sleep(1000);
   		 System.out.println("Exited new activity");
   	     }
     }
}

class supertest {
}

class Test extends supertest{
	private int target;
	Test() {
		System.out.println("Entered Test Cons");
        System.out.println("Exit Test Cons");
	}	
}

class T extends Test{
	private int target;
	T() {
		super();	
		System.out.println("After super ");
	}
}