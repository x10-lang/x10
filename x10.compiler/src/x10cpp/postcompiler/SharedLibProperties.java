package x10cpp.postcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import polyglot.util.QuotedStringTokenizer;

/**
 * A class to encapsulate a properties file
 * that contains configuration information
 * for a C++ shared library build.
 */
public class SharedLibProperties {
    public final Properties props;
    public final Collection<String> cxxFlags;
    public final Collection<String> ldFlags;
    String libPrefix;
    String libSuffix;
    boolean staticLib;
    
    public SharedLibProperties(Properties p) {
        props = p;
        cxxFlags = PostCompileProperties.split(p.getProperty("X10LIB_CXXFLAGS_SHARED"));
        ldFlags  = PostCompileProperties.split(p.getProperty("X10LIB_LDFLAGS_SHARED"));
        libPrefix = p.getProperty("X10LIB_LIBPREFIX");
        libSuffix = p.getProperty("X10LIB_LIBSUFFIX");
        staticLib = p.getProperty("X10LIB_X10_STATIC_LIB").length() > 0;
    }

}



