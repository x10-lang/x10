public class CHA2withCandidateInheritances {

    public static class A extends CHA2withCandidateInheritances {
       public void printA (String s) { System.out.println(s);}
       public void printB (String s) { System.out.println(s);}
       public void printf (String s) { System.out.println(s); }
    }
    
    public static class B extends A {
       public void printA (B o) { printf(o.toString());}
    }
    
    public static class C extends A {
       public void printB (C o) { o.printf(o.toString());}
    }
    
    public void printf (String s) { 
       System.out.println("Hello, world!");
       System.out.println(s); 
    }
     
     
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
