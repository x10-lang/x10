/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

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
    
    public PostCompileProperties(Properties p) {
        props = p;
        cxxFlags = split(p.getProperty("X10LIB_CXXFLAGS"));
        libs     = split(p.getProperty("X10LIB_LDLIBS"));
        ldFlags  = split(p.getProperty("X10LIB_LDFLAGS"));
    }

    public static Collection<String> split(String s) {
        ArrayList<String> l = new ArrayList<String>();
        if (s==null) return l;
        QuotedStringTokenizer q = new QuotedStringTokenizer(s);
        while (q.hasMoreTokens()) l.add(q.nextToken());
        return l;
    }
}