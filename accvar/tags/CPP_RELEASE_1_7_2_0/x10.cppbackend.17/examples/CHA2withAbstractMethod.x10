public class CHA2withAbstractMethod {

    public static abstract class A extends CHA2withAbstractMethod {
       public abstract void printA (String s);
       public void printB (String s) { System.out.println(s);}
    }
    
    public static abstract class B extends A {
       public abstract void printA (String s);
    }
      
    public static class C extends B {
       public void printA (String s) { }
    }
    
    public static class D extends C {
       public void printA (String s) { }
    }
    
    public void printf (B b) { 
       System.out.println("Hello, world!");
       b.printA("hi"); 
    }
     
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
