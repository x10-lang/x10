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
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import polyglot.main.Reporter;
import polyglot.util.FileUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;

/**
 * We implement our own class loader.  All this pain is so
 * we can define the classpath on the command line.
 */
public class ResourceLoader
{
    /**
     * Keep a cache of the zips and jars so we don't have to keep
     * opening them from the file system.
     */
    protected Map<File, Object> zipCache;

    /**
     * A cache of directories found in zip files.
     */
    protected Set<String> dirCache;

    /**
     * Directory contents cache. Cache the first level of the directory
     * so that we get less FileNotFoundExceptions
     */
    protected Map<File, Set<String>> dirContentsCache;

    protected static final Set<String> DOES_NOT_EXIST = new AbstractSet<String>() {
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                public boolean hasNext() { return false; }
                public String next() { throw new NoSuchElementException(); }
                public void remove() { throw new UnsupportedOperationException(); }
            };
        }
        public int size() { return 0; }
        public boolean contains(String s) { return false; }
    };

    /**
     * Cache File.canRead()
     */
    protected Map<File, Boolean> dirCanRead = CollectionFactory.newHashMap();

    protected final static Object not_found = new Object();
    
    protected Reporter reporter;

    public ResourceLoader(Reporter reporter) {
        this.zipCache = CollectionFactory.newHashMap();
        this.dirContentsCache = CollectionFactory.newHashMap();
        this.dirCache = CollectionFactory.newHashSet();
        this.reporter = reporter;
    }

    private boolean canRead(File dir) {
        Boolean res = dirCanRead.get(dir);
        if (res==null) {
            res = dir.canRead();
            dirCanRead.put(dir,res);
        }
        return res;
    }
    /**
     * Return true if the package name exists under the directory or file
     * <code>dir</code>.
     */
    public boolean dirExists(File dir, String name) {
        if (reporter.should_report(Reporter.loader, 3)) {
           reporter.report(3, "looking in " + dir + " for " +
                             name.replace('.', File.separatorChar));
        }

        if (!canRead(dir))
            return false;

        try {
            if (dir.getName().endsWith(".jar") ||
                dir.getName().endsWith(".zip")) {

                if (dirCache.contains(name)) {
		    return true;
		}

		// load the zip file, forcing the package cache to be initialized
		// with its contents.
                loadZip(dir);
    		
		return dirCache.contains(name);
	    }
            else {
                File f = new File(dir, name);
                return f.isDirectory() && FileUtil.checkNameFromRoot(dir, f);
            }
        }
        catch (FileNotFoundException e) {
            // ignore the exception.
        }
        catch (IOException e) {
            throw new InternalCompilerError(e);
        }

        return false;
    }

    /**
     * Try to find the file <code>name</code> in the directory or jar or zip
     * file <code>dir</code>.
     * If the file does not exist in the specified file/directory, then
     * <code>null</code> is returned.
     */
    public Resource loadResource(File dir, String name)
    {
        if (reporter.should_report(Reporter.loader, 3)) {
            reporter.report(3, "looking in " + dir + " for " + name);
        }
	
        if (!canRead(dir))
            return null;
        
        try {
            if (dir.getName().endsWith(".jar") ||
                dir.getName().endsWith(".zip")) {

                ZipFile zip = loadZip(dir);
                return loadFromZip(dir, zip, name);
            }
            else {
                return loadFromFile(name, dir);
            }
        }
        catch (FileNotFoundException e) {
            // ignore the exception.
        }
        catch (IOException e) {
            throw new InternalCompilerError("Error while processing "+dir, e);
        }

        return null;
    }

    ZipFile loadZip(File dir) throws IOException {
        Object o = zipCache.get(dir);
        if (o != not_found) {
            ZipFile zip = (ZipFile) o;
            if (zip != null) {
                return zip;
            }
            else {
                // the zip is not in the cache.
                // try to get it.
                if (!dir.exists()) {
                    // record that the file does not exist,
                    zipCache.put(dir, not_found);
                }
                else {
                    // get the zip and put it in the cache.
                    if (reporter.should_report(Reporter.loader, 2))
                        reporter.report(2, "Opening zip " + dir);
		    if (dir.getName().endsWith(".jar")) {
			zip = new JarFile(dir);
		    }
		    else {
			zip = new ZipFile(dir);
		    }
		    zipCache.put(dir, zip);
		    
		    // Load the package cache.
		    for (Enumeration<? extends ZipEntry> i = zip.entries(); i.hasMoreElements(); ) {
			ZipEntry ei = i.nextElement();
			String n = ei.getName();
			
			int index = n.indexOf('/');
			while (index >= 0) {
			    dirCache.add(n.substring(0, index));
			    index = n.indexOf('/', index+1);
			}
		    }
		    
                    return zip;
                }
            }
        }
        throw new FileNotFoundException(dir.getAbsolutePath());
    }

    Resource loadFromZip(File source, ZipFile zip, String fileName) throws IOException {
	String entryName = fileName.replace(File.separatorChar, '/');
        if (reporter.should_report(Reporter.loader, 2))
            reporter.report(2, "Looking for " + entryName + " in " + zip.getName());
        if (zip != null) {
            ZipEntry entry = zip.getEntry(entryName);
            if (entry != null) {
                if (reporter.should_report(Reporter.loader, 3))
                    reporter.report(3, "found zip entry " + entry);
                Resource c = new ZipResource(source, zip, entryName);
                return c;
            }
        }
        return null;
    }

    Resource loadFromFile(String name, File dir) throws IOException {
        int sepIndex = name.indexOf('/');
        if (sepIndex < 0) {
            sepIndex = name.indexOf(File.separatorChar);
        }
        if (sepIndex > 0) {
            String firstPart = name.substring(0, sepIndex);
            Set<String> contents = dirContentsCache.get(dir);
            if (contents != null && !contents.contains(firstPart)) {
                return null;
            }
            File newDir = new File(dir, firstPart);
            String newName = name.substring(sepIndex+1);
            return loadFromFile(newName, newDir);
        }
        Set<String> dirContents = dirContentsCache.get(dir);
        if (dirContents == null) {
            if (dir.exists() && dir.isDirectory()) {
                dirContents = CollectionFactory.newHashSet();
                String[] contents = dir.list();
                if (contents != null) {
                    for (int j = 0; j < contents.length; j++) {
                        dirContents.add(contents[j]);
                    }
                }
            } else {
                dirContents = DOES_NOT_EXIST;
            }
            dirContentsCache.put(dir, dirContents);
        }
        if (dirContents == DOES_NOT_EXIST) {
            return null;
        }

        String firstPart = name;

        // check to see if the directory has the first part of the filename,
        // to avoid trying to open the file if it doesn't
        if (!dirContents.contains(firstPart)) {
            return null;
        }

        // otherwise, try and open the thing.
        File file = new File(dir, name);

        if (! file.exists())
            return null;

        if (reporter.should_report(Reporter.loader, 3)) {
            reporter.report(3, "found " + file);
            reporter.report(3, "defining class " + name);
        }

        Resource c = new FileResource(file);
        return c;
    }

}
