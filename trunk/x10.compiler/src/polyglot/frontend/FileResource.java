/**
 * 
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