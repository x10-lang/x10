package x10cpp.postcompiler;

import java.util.Collection;
import java.util.Properties;

/**
 * Represents a pre-compiled X10 library that is available 
 * for use in the current compilation.  A pre-compiled X10 library
 * currently is presented to the compiler as an on-disk directory
 * structure with a number of conventional pieces. These pieces
 * are found by reading a properties file that describes the library
 * in terms of relative paths from the directory containing the 
 * main library properties file.  
 */
public class PrecompiledLibrary extends PostCompileProperties {
    public final String absolutePathToRoot;
    public final String sourceJar;
    public final Collection<String> sourceFiles;
    
    public PrecompiledLibrary(String absPath, Properties p) {
        super (p);
        absolutePathToRoot = absPath;
        sourceJar = p.getProperty("X10LIB_SRC_JAR");
        sourceFiles = split(p.getProperty("X10LIB_SRC_FILES"));
    }

}
