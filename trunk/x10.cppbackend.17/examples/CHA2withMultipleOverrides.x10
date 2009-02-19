public class CHA2withMultipleOverrides {

    public static class A extends CHA2withMultipleOverrides {
       public void printA (String s) { System.out.println(s);}
       public void printB (String s) { System.out.println(s);}
    }
    
    public static class B extends A {
       public void printA (String s) { }
    }
    
    public static class BB extends B {
       public void printA (String s) { }
    }
    
    public static class C extends A {
       public void printB (String s) { }
    }
    
    public static class CC extends C {
       public void printB (String s) { }
    }
    
    public void printf (A a) { 
       System.out.println("Hello, world!");
       a.printA("hi");
       a.printB("hi"); 
    }
     
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
