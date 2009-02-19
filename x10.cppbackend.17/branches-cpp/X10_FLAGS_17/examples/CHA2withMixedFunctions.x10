public class CHA2withMixedFunctions {

    public static class A extends CHA2withMixedFunctions {
       public static void printA (String s) { System.out.println(s); B.printA(s); }
       public void printB (String s) { System.out.println(s);}
    }
    
    public static class B extends A {
       public static void printA (String s) { System.out.println(s); A.printA(s); }
    }
    
    public static class C extends A {
       public void printB (String s) { }
    }
    
    public static class CC extends C {
       public void printB (String s) { printf(this);}
    }
    
    public void printf (A a) { 
       System.out.println("Hello, world!");
       a.printB("hi"); 
    }
     
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
