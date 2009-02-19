public class CHA2withProtectedMethod {

    public static class A  {
       protected void printA (String s) { System.out.println(s);}
       public void printB (String s) { System.out.println(s);}
    }
    
    public static class B extends A {
       protected void printA (String s) { }
    }
    
    public static class C extends A {
       public void printB (String s) { }
    }
    
    public void printf (A a) { 
       System.out.println("Hello, world!");
       a.printA("hi");
    }
     
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
