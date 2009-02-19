public class CHA2withStaticFunctions {

    public static class A extends CHA2withStaticFunctions {
       public static void printA (String s) { System.out.println(s); B.printA(s); }
       public void printB (String s) { System.out.println(s);}
    }
    
    public static class B extends A {
       public static void printA (String s) { System.out.println(s); A.printA(s); }
    }
    
    public static class C extends A {
       public void printB (String s) { }
    }
    
    public void printf (String s) { 
       System.out.println("Hello, world!");
       System.out.println(s); 
    }
     
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
