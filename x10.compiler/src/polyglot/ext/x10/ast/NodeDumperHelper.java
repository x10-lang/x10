/*
 * Created on Oct 5, 2004
 */
package polyglot.ext.x10.ast;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import x10.runtime.Configuration;

/**
 * Helper class for X10 nodes that perform a purely syntactic transformation when dumped.
 * This syntactic transformation can be specified in an .xcd definition file which
 * must have the same name as the name of the class (i.e. Future_c.xcd).  The format
 * of the specification file is Java source with placeholders of the form
 * <tt>#X</tt> where X is a number from 0 to 9.  When the dumper encounters a token
 * of the form <tt>#X</tt> it in-lines the node that the getArgument(X) function
 * returns for the given value of X.  This allows us to specify simple syntactic
 * transformations without dealing with polyglot.
 * 
 * 
 * @author Christian Grothoff
 */
class NodeDumperHelper {

    static HashMap translationCache_ = new HashMap();
    
    static String translate(Class me) {
        String cached = (String) translationCache_.get(me);
        if (cached != null)
            return cached;        
        String name = me.getName();
        String shortName = name.substring(name.lastIndexOf('.')+1);
        try {
            String fname = Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY + shortName + ".xcd"; // xcd = x10 compiler data/definition
            FileInputStream fis = new FileInputStream(fname);
            DataInputStream dis = new DataInputStream(fis);
            byte[] b = new byte[dis.available()];
            dis.read(b);
            String trans = new String(b, "UTF-8");
            translationCache_.put(me, trans);
            return trans;
        } catch (IOException io) {
            throw new Error("No translation for " + shortName + " found!");
        }
    }
    
    public static void dump(Dumpable c, 
                            CodeWriter w) {
        String regex = translate(c.getClass());
        int len = regex.length();
        int pos = 0;
        int start = 0;
        while (pos < len) {
            if (regex.charAt(pos) == '#') {
                w.write(regex.substring(start, pos));
                Integer idx = new Integer(regex.substring(pos,pos+1));
                pos += 2;
                start = pos+1;
                c.getArgument(idx.intValue()).dump(w);
            }
            pos++;
        }
        w.write(regex.substring(start));
    }

    interface Dumpable {
        
        public Node getArgument(int id);
        
    }
    
} // end of TranslateWhenDumpedNode
