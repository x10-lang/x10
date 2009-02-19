public class CHA1withMatchingStaticFunctions {

    public static class A extends CHA1withMatchingStaticFunctions {
       public void printB (CHA1withMatchingStaticFunctions o) { }
       public static void printA (A o) { printA(o); printA(o); }
    }
      
    public static void printA (A o) {printA(o);}
    
	public final static void main(String[] s) {
	   System.out.println("Hello World!");
	}
}
