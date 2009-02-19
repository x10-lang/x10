public class CHA2withPrivateMethod {

    public static class A extends CHA2withPrivateMethod {
       private void printA (String s) { System.out.println(s);}
       public void printB (String s) { System.out.println(s);}
    }
    
    public static class B extends A {
       public void printA (String s) { }
    }
    
    public static class C extends A {
       public void printB (String s) { }
    }
    
    public void printf (B b) { 
       System.out.println("Hello, world!");
       b.printA("hi");
    }
     
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
