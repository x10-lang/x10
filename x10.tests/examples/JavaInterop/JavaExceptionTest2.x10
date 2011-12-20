import x10.interop.java.Throws;

public class JavaExceptionTest2 {
    static def javaException2a() @Throws[java.lang.Throwable] {
        throw new java.lang.Throwable("I like Java.");
    }
    public static def main(args: Array[String]) {
        try {
            javaException2a();
        } catch (e:java.lang.Throwable) {
        	Console.OUT.println("Passed");
            e.printStackTrace(); // this statement should be reachable
        }
    }
}
