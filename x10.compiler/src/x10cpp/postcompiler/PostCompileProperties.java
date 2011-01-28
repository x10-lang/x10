package x10cpp.postcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import polyglot.util.QuotedStringTokenizer;

/**
 * A class to encapsulate a properties file
 * that contains configuration information
 * for a the C++ postcompilation command.
 */
public class PostCompileProperties {
    public final Properties props;
    public final Collection<String> cxxFlags;
    public final Collection<String> libs;
    public final Collection<String> ldFlags;
    
    PostCompileProperties(Properties p) {
        props = p;
        cxxFlags = split(p.getProperty("CXXFLAGS"));
        libs     = split(p.getProperty("LDLIBS"));
        ldFlags  = split(p.getProperty("LDFLAGS"));
    }

    protected Collection<String> split(String s) {
        ArrayList<String> l = new ArrayList<String>();
        if (s==null) return l;
        QuotedStringTokenizer q = new QuotedStringTokenizer(s);
        while (q.hasMoreTokens()) l.add(q.nextToken());
        return l;
    }
}