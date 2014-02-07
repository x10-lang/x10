/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.frontend;

import java.io.*;

public class FileResource implements Resource {
    private final File source;

    public FileResource(File source) {
	this.source = source;
    }

    public int hashCode() {
	return canonicalPath().hashCode();
    }
    
    private String cachedCanonicalPath = null;
    private String canonicalPath() {
	if (cachedCanonicalPath == null) {
	try {
	    cachedCanonicalPath = source.getCanonicalPath();
	}
	catch (IOException e) {
	    cachedCanonicalPath = source.getAbsolutePath();
	}
	}
	return cachedCanonicalPath;
    }

    public boolean equals(Object o) {
	if ( o instanceof FileResource) {
	    FileResource r = (FileResource) o;
	    return canonicalPath().equals(r.canonicalPath());
	}
	return false;
    }

    public InputStream getInputStream() throws IOException {
	FileInputStream in = new FileInputStream(source);
	return in;
    }

    public File file() {
	return source;
    }

    public String name() {
	return source.getName();
    }

    public String toString() {
	return source.getPath();
    }
}