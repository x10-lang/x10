public class CHA1withVirtualCall {

    public static class A extends CHA1withVirtualCall {
       public void printB (String s) { System.out.println(s);}
       public void printA (CHA1withVirtualCall o) {}
    }
      
    public void printA (CHA1withVirtualCall o) {o.printA(o);}
    
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
