/**
 * 
 */
package polyglot.frontend;

import java.io.*;
import java.util.zip.*;

public class ZipResource implements Resource {
    private final ZipFile zip;
    private final String entryName;
    private final File source;
    
    public ZipResource(File source, ZipFile zip, String entryName) {
        this.zip = zip;
        this.entryName = entryName;
        this.source = source;
    }
    
    public int hashCode() {
	return canonicalPath().hashCode() + entryName.hashCode();
    }
    
    private String canonicalPath() {
	try {
	return source.getCanonicalPath();
	}
	catch (IOException e) {
	    return source.getAbsolutePath();
	}
    }

    public boolean equals(Object o) {
	if ( o instanceof ZipResource) {
	    ZipResource r = (ZipResource) o;
	    return canonicalPath().equals(r.canonicalPath()) && entryName.equals(r.entryName);
	}
	return false;
    }

    public InputStream getInputStream() throws IOException {
	ZipEntry entry = zip.getEntry(entryName);
	if (entry == null)
	    throw new ZipException("Entry " + entryName + " not found in " + zip);
        InputStream in = zip.getInputStream(entry);
        return in;
    }
    
    public File file() {
        return source;
    }
    
    public String name() {
	return entryName.substring(entryName.lastIndexOf('/')+1);
    }
    
    public String toString() {
        return source.getPath() + ":" + entryName;
    }
}