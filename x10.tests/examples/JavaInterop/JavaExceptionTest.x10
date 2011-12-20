public class JavaExceptionTest {
    static def x10Exception() {
        try {
            throw new x10.lang.Exception("I like X10");
        } catch (e: x10.lang.Throwable) {
            e.printStackTrace();
        }               
    }
    static def javaException() {
        try {
            throw new java.lang.Exception("I like Java");
        } catch (e: java.lang.Throwable) {
            e.printStackTrace();
        }       
    }
    public static def main(args: Array[String]) {
        x10Exception();
        javaException();
    }
}
