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

package x10.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import polyglot.frontend.Resource;

@SuppressWarnings("deprecation")
public class StringResource implements Resource {
    private String input;
    private String path;

    public StringResource(String input, String path) {
        this.input = input;
        this.path = path;
    }

    public File file() {
        return new File(path);
    }

    public InputStream getInputStream() throws IOException {
        return new StringBufferInputStream(input);
    }

    public String name() {
        int idx = path.lastIndexOf(File.separatorChar);
        if (idx == -1)
            idx = path.lastIndexOf('/');
        return (idx > 0) ? path.substring(idx+1) : path;
    }

    public String toString() {
        return path;
    }
}
// vim:tabstop=4:shiftwidth=4:expandtab
