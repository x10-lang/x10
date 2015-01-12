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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.frontend;

import java.util.Date;

/** A <code>Source</code> represents a source file. */
public class Source
{
    protected String name;
    protected String path;
    protected Date lastModified;
    
    /**
     * Indicates if this source was explicitly specified by the user,
     * or if it a source that has been drawn in to the compilation process
     * due to a dependency.
     */
    protected boolean userSpecified;

    protected Source(String name) {
        this(name, null, null, false);
    }

    protected Source(String name, boolean userSpecified) {
        this(name, null, null, userSpecified);
    }

    public Source(String name, String path, Date lastModified) {
        this(name, path, lastModified, false);
    }
    
    public Source(String name, String path, Date lastModified, boolean userSpecified) {
	this.name = name;
        this.path = path;
	this.lastModified = lastModified;
        this.userSpecified = userSpecified;   
    }

    public boolean equals(Object o) {
	if (o instanceof Source) {
	    Source s = (Source) o;
	    return name.equals(s.name) && 
                 (path == s.path || (path != null && path.equals(s.path)));
	}

	return false;
    }

    public int hashCode() {
	return (path==null?0:path.hashCode()) ^ name.hashCode();
    }

    /** The name of the source file. */
    public String name() {
	return name;
    }

    /** The path of the source file. */
    public String path() {
	return path;
    }

    /** Return the date the source file was last modified. */
    public Date lastModified() {
	return lastModified;
    }

    public String toString() {
        return path;
    }
    
    public void setUserSpecified(boolean userSpecified) {
        this.userSpecified = userSpecified;
    }
    
    public boolean userSpecified() {
        return userSpecified;
    }
}
