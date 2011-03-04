package x10dt.builders.data;

/**
 * Class that contains the data to be used by the builders' test suite.
 * @author mvaziri
 *
 */

public class Data {
	
	// --- Data for MiscTests

	public static String MyProject = "MyProject";
	
	public static String pac = "pac";
	
	public static String Hi = "import pac.Howdy;" + "\n" +
								  "public class Hi extends Howdy {" + "\n"
	  								+ "public def meth(){}" + "\n"
	  								+ "}";
	
	// --- Data for DependencyTests
	
	public static String pakpac = "pak.pac";

	public static String DependencyTestsProject = "DependencyTestsProject";
	public static String MiscTestsProject = "MiscTestsProject";
	
	public static String Hello = "public class Hello extends A { \n"
							+"public static def main(args:Rail[String]) {\n"
							+    "Console.OUT.println(\"Hello World!\" );\n"
							+    "val h = new Hello();\n"
							+    "val v1 = h.methA().methC();\n"
							+    "v1.f = 9;\n"
							+    "h.methB();\n"
							+"}"
							+"}";

	public static String Hello1 = "public class Hello extends A { \n"
							+"var field: E \n"
							+"public static def main(args:Rail[String]) {\n"
							+    "Console.OUT.println(\"Hello World!\" );\n"
							+    "val h = new Hello();\n"
							+    "val v1 = h.methA().methC();\n"
							+    "v1.f = 9;\n"
							+    "h.methB();\n"
							+"}"
							+"}";

	public static String Hello3 = "public class Hello extends A { \n"
							+"public static def main(args:Rail[String]) {\n"
							+    "Console.OUT.println(\"Hello World!\" );\n"
							+    "val h = new Hello();\n"
							+    "val v1 = h.methA().methC().d.f;\n"
							+    "v1.f = 9;\n"
							+    "h.methB();\n"
							+"}"
							+"}";
	
	public static String A = "import pac.B;\n"
							+ "public class A extends B{\n"
							+	"public def methA():C{return new C();}\n"
							+ "}";


	public static String B =  "package pac;\n" 
							+ "public class B {\n"
							+	"public def methB(){}\n"
							+ "}";
	
	
	public static String C = "import pak.pac.D;\n"
							+ "public class C {\n"
							+	"public def methC():D{return new D();}\n"
							+ "}";
	
	public static String D =  "package pak.pac;\n" 
							+ "public class D {\n"
							+ 	"public var f: int;\n"
							+	"public def methD(){}\n"
							+ "}";
	
	public static String D1 =  "package pak.pac;\n" 
		+ "public class D {\n"
		+ 	"public var f: int;\n"
		+   "public val d = new F();\n"
		+	"public def methD(){}\n"
		+ "}";
	
	
	public static String E = "public class E {\n"
							+ "}";
	
	public static String F = "package pak.pac;\n"
							+ "public class F {\n"
							+ "public val f = 0;\n"		
							+ "}";
}
