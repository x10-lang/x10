public class CHA2 {

    public static class A extends CHA2 {
       public void printA (String s) { System.out.println(s);}
       public void printB (String s) { System.out.println(s);}
    }
    
    public static class B extends A {
       public void printA (String s) { }
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
