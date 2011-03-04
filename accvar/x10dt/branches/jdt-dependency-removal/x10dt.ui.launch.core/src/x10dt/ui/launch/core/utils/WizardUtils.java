package x10dt.ui.launch.core.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WizardUtils {
	/**
	   * Initializes the given file's contents with "Hello World" source code in X10.
	   * 
	   * @param packageName The package name to use.
	   * @param typeName The type name that will contain the code.
	   * @return A non-null input stream encapsulating the sample code.
	   */
	  public static InputStream createSampleContentStream(final String packageName, final String typeName) {
	    final StringBuilder sb = new StringBuilder();

	    if ((packageName != null) && (packageName.trim().length() > 0)) {
	      sb.append("package " + packageName + ";\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
	    }
	    sb.append("/**\n");
	    sb.append(" * The canonical \"Hello, World\" demo class expressed in X10\n");
	    sb.append(" */\n");
	    sb.append("public class " + typeName + " {\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
	    sb.append("    /**\n");
	    sb.append("     * The main method for the Hello class\n");
	    sb.append("     */\n");
	    sb.append("    public static def main(Array[String]) {\n"); //$NON-NLS-1$
	    sb.append("        Console.OUT.println(\"Hello, World!\");\n"); //$NON-NLS-1$
	    sb.append("    }\n\n"); //$NON-NLS-1$
	    sb.append("}"); //$NON-NLS-1$

	    return new ByteArrayInputStream(sb.toString().getBytes());
	  }
}
