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
import java.util.*;

import polyglot.main.Reporter;
import polyglot.types.QName;
import polyglot.util.FileUtil;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

/** A <code>SourceLoader</code> is responsible for loading source files. */
public class SourceLoader
{
    protected ExtensionInfo sourceExt;
    protected Reporter reporter;
    protected List<File> sourcePath;

    /** 0 if unknown, 1 if case insensitive, -1 if not. */
    protected int caseInsensitive;

    /** Set of sources already loaded.  An attempt to load a source
      * already loaded will cause an IOException. */
    protected Map<Object,Source> loadedSources;

    public SourceLoader(ExtensionInfo sourceExt, List<File> sourcePath) {
        this.sourcePath = sourcePath;
        this.sourceExt = sourceExt;
        this.reporter = sourceExt.getOptions().reporter;
        this.caseInsensitive = 0;
        this.loadedSources = CollectionFactory.newHashMap();
    }

    /** Load a source from a specific file. */
    public FileSource fileSource(String fileName) throws IOException {
        return fileSource(fileName, false);
    }
    
    public FileSource fileSource(String fileName, boolean userSpecified) throws IOException {
        File sourceFile = new File(fileName);
        
        if (! sourceFile.exists()) {
            throw new FileNotFoundException(fileName);
        }
        
        String[] exts = sourceExt.fileExtensions();
        boolean ok = false;
        
        for (int i = 0; i < exts.length; i++) {
            String ext = exts[i];
            
            if (fileName.endsWith("." + ext)) {
                ok = true;
                break;
            }
        }
        
        if (! ok) {
            String extString = "";
            
            for (int i = 0; i < exts.length; i++) {
                if (exts.length == 2 && i == exts.length-1) {
                    extString += " or ";
                }
                else if (exts.length != 1 && i == exts.length-1) {
                    extString += ", or ";
                }
                else if (i != 0) {
                    extString += ", ";
                }
                extString = extString + "\"." + exts[i] + "\"";
            }
            
            if (exts.length == 1) {
                throw new IOException("Source \"" + fileName +
                                      "\" does not have the extension "
                                      + extString + ".");
            }
            else {
                throw new IOException("Source \"" + fileName +
                                      "\" does not have any of the extensions "
                                      + extString + ".");
            }
        }
        
        if (reporter.should_report(Reporter.loader, 2))
            reporter.report(2, "Loading class from " + sourceFile);

        Resource r = new FileResource(sourceFile);
        FileSource s = (FileSource) loadedSources.get(fileKey(r));
        
        if (s != null) {
            if (!s.userSpecified && userSpecified) {
                s.setUserSpecified(true);
            }
            return s;
        }
        
        s = sourceExt.createFileSource(r, userSpecified);
        loadedSources.put(fileKey(r), s);
        return s;
    }

    /**
     * The current user directory. We make it static so we don't need to
     * keep on making copies of it. 
     */
    protected static File current_dir = null;

    /**
     * The current user directory.
     */
    protected static File current_dir() {
        if (current_dir == null) {
            current_dir = new File(System.getProperty("user.dir"));
        }
        return current_dir;
    }

    /** Check if a directory for a package exists. */
    public boolean packageExists(QName name) {
        String fileName = name.toString().replace('.', '/');

        /* Search the source path. */
        boolean result = pathloader().dirExists(fileName);
        return result;
    }

    ClassPathResourceLoader pathloader;
    
    private ClassPathResourceLoader pathloader() {
	if (pathloader == null)
	    pathloader = new ClassPathResourceLoader(sourcePath,reporter);
	return pathloader;
    }
        
    /** Load the source file for the given class name using the source path. */
    public FileSource classSource(QName className) {
	ClassPathResourceLoader loader = pathloader();
	/* Search the source path. */
        String[] exts = sourceExt.fileExtensions();

        for (int k = 0; k < exts.length; k++) {
            String fileName = className.toString().replace('.', File.separatorChar) +
            "." + exts[k];

            Resource r = loader.loadResource(fileName);
            if (r != null) {
        	try {
        	    if (reporter.should_report(Reporter.loader, 2))
        	        reporter.report(2, "Loading " + className + " from " + r);
        	    FileSource s = sourceExt.createFileSource(r, false);
        	    loadedSources.put(fileKey(r), s);
        	    return s;
        	}
        	catch (IOException e) {
        	}
            }
        }

        return null;
    }

    public Object fileKey(Resource r) {
	File file = r.file();
	String suffix = r instanceof FileResource ? "" : ":" + r.name();
	try {
	    return file.getCanonicalPath() + suffix;
	}
	catch (IOException e) {
	    return file.getAbsolutePath() + suffix;
	}
    }
}
