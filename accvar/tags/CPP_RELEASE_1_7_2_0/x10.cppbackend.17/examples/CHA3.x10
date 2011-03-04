public class CHA3 {

    public static class A {
       public void m (String s) { System.out.println(s);}
       public void p (String s) { System.out.println(s);}
    }
    
    public static class B extends A {
       public void m (String s) { }
    }
    
    public static class C extends A {
       public void m (String s) { }
    }
    
    public static class D extends B {
       public void printf (String s) { }
    }
    
    public static class E extends C {
       public void m (String s) { 
         System.out.println("E's m");
       }
    }
    
    public static class F extends C {
       public void p (String s) { 
         System.out.println("F's p");
       }
    }
    
    public static class G extends F {
       public void printf (String s) { 
         System.out.println("G's printf");
       }
    }
    
    public static class H extends F {
       public void printf (String s) { 
         System.out.println("H's printf");
       }
    }
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
