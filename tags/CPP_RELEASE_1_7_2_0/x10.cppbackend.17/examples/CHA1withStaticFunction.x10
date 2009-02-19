public class CHA1withStaticFunction {

    public static class A extends CHA1withStaticFunction {
       public void printB (CHA1withStaticFunction o) { }
       public void printA (CHA1withStaticFunction o) { }
    }
      
    public static void printA (String[] s) {System.out.println(s);}
    
	public final static void main(String[] s) {
	   printA(s);
	}
}
