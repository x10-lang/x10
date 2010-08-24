/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.frontend;

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import polyglot.main.Report;
import polyglot.util.FileUtil;
import polyglot.util.InternalCompilerError;

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
    protected Map zipCache;

    /**
     * A cache of directories found in zip files.
     */
    protected Set dirCache;

    /**
     * Directory contents cache. Cache the first level of the directory
     * so that we get less FileNotFoundExceptions
     */
    protected Map dirContentsCache;

    protected final static Object not_found = new Object();

    public ResourceLoader() {
        this.zipCache = new HashMap();
        this.dirContentsCache = new HashMap();
	this.dirCache = new HashSet();
    }

    /**
     * Return true if the package name exists under the directory or file
     * <code>dir</code>.
     */
    public boolean dirExists(File dir, String name) {
        if (Report.should_report(verbose, 3)) {
	    Report.report(3, "looking in " + dir + " for " +
                             name.replace('.', File.separatorChar));
        }

        if (!dir.canRead())
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
        if (Report.should_report(verbose, 3)) {
	    Report.report(3, "looking in " + dir + " for " + name);
        }
	
        if (!dir.canRead())
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
            throw new InternalCompilerError(e);
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
                    if (Report.should_report(verbose, 2))
                        Report.report(2, "Opening zip " + dir);
		    if (dir.getName().endsWith(".jar")) {
			zip = new JarFile(dir);
		    }
		    else {
			zip = new ZipFile(dir);
		    }
		    zipCache.put(dir, zip);
		    
		    // Load the package cache.
		    for (Enumeration i = zip.entries(); i.hasMoreElements(); ) {
			ZipEntry ei = (ZipEntry) i.nextElement();
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
        if (Report.should_report(verbose, 2))
            Report.report(2, "Looking for " + entryName + " in " + zip.getName());
        if (zip != null) {
            ZipEntry entry = zip.getEntry(entryName);
            if (entry != null) {
                if (Report.should_report(verbose, 3))
                    Report.report(3, "found zip entry " + entry);
                Resource c = new ZipResource(source, zip, entryName);
                return c;
            }
        }
        return null;
    }

    Resource loadFromFile(String name, File dir) throws IOException {
	int sepIndex = name.indexOf(File.separatorChar);
	if (sepIndex > 0) {
	    File newDir = new File(dir, name.substring(0, sepIndex));
	    String newName = name.substring(sepIndex+1);
	    return loadFromFile(newName, newDir);
	}
        Set dirContents = (Set) dirContentsCache.get(dir);
        if (dirContents == null) {
            dirContents = new HashSet();
            dirContentsCache.put(dir, dirContents);
            if (dir.exists() && dir.isDirectory()) {
                String[] contents = dir.list();
                if (contents != null) {
                    for (int j = 0; j < contents.length; j++) {
                        dirContents.add(contents[j]);
                    }
                }
            }
        }
        
        // otherwise, try and open the thing.
        File file = new File(dir, name);
        
        if (! file.exists())
            return null;
        
        String firstPart = name;

        // check to see if the directory has the first part of the filename,
        // to avoid trying to open the file if it doesn't
        if (!dirContents.contains(firstPart)) {
            return null;
        }


        if (Report.should_report(verbose, 3))
            Report.report(3, "found " + file);
	if (Report.should_report(verbose, 3))
	Report.report(3, "defining class " + name);
	
	Resource c = new FileResource(file);
        return c;
    }
    
    protected static Collection verbose;

    static {
        verbose = new HashSet();
        verbose.add("loader");
    }
}
