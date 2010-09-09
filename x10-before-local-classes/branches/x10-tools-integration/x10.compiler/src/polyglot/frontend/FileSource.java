/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.frontend;

import java.io.*;
import java.util.Date;

/** A <code>Source</code> represents a source file. */
public class FileSource extends Source
{
    protected final Resource resource;
    protected Reader reader;

    public FileSource(Resource r) throws IOException {
        this(r, false);
    }
    
    public  Resource resource() {
	return resource;
    }
        
    public FileSource(Resource r, boolean userSpecified) throws IOException {
        super(r.name(), userSpecified);
        this.resource = r;
    
        if (! r.file().exists()) {
            throw new FileNotFoundException(r.name());
        }

        path = r.file().getCanonicalPath();
        lastModified = new Date(r.file().lastModified());
    }

    public boolean equals(Object o) {
	if (o instanceof FileSource) {
	    FileSource s = (FileSource) o;
	    return resource.equals(s.resource);
	}

	return false;
    }

    public int hashCode() {
	return resource.hashCode();
    }

    /** Open the source file. */
    public Reader open() throws IOException {
	if (reader == null) {
	    reader = createReader(resource.getInputStream());
	}

	return reader;
    }

    /** This method defines the character encoding used by
        a file source. By default, it is ASCII with Unicode escapes,
	but it may be overridden. */
    protected Reader createReader(InputStream str) {
      try {
	  return new InputStreamReader(str, "US-ASCII");
      } catch (UnsupportedEncodingException e) { return null; }
    }

    /** Close the source file. */
    public void close() throws IOException {
	if (reader != null) {
	    reader.close();
	    reader = null;
	}
    }

    public String toString() {
	return resource.toString();
    }
}
