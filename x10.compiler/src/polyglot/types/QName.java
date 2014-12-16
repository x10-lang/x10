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

package polyglot.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import polyglot.util.StringUtil;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

public class QName implements Serializable {
    private static final long serialVersionUID = 4114000967641601437L;
    
    private QName qualifier;
    private Name name;
    private int hash;

    private QName(QName qualifier, Name name, int hash) {
	this.qualifier = qualifier;
	this.name = name;
	this.hash = hash;
    }

    public Name name() {
	return name;
    }

    public QName qualifier() {
	return qualifier;
    }
    
    static Map<String,QName> internCache = CollectionFactory.newHashMap();

    public static QName make(QName qualifier, Name name) {
	String shortName = name.toString();
	String fullName = qualifier == null ? shortName : qualifier.toString() + "." + shortName;
	synchronized (internCache) {
	    QName q = internCache.get(fullName);
	    if (q != null)
		return q;
	    int hash = fullName.hashCode();
	    q = new QName(qualifier, name, hash);
	    internCache.put(fullName, q);
	    return q;
	}
    }
    
    public boolean startsWith(QName name) {
	if (name == null)
	    return true;
	if (equals(name))
	    return true;
	if (qualifier() != null)
	    return qualifier().startsWith(name);
	return false;
    }

    public static QName make(QName q) {
	return q;
    }
    
    public static QName make(String qualifier, String name) {
	return make(make(qualifier), Name.make(name));
    }

    public static QName make(String fullName) {
	if (fullName == null)
	    return null;
	if (fullName.equals(""))
	    return null;
	
	synchronized (internCache) {
	    QName q = internCache.get(fullName);
	    if (q != null)
		return q;
	    int hash = fullName.hashCode();
	    if (StringUtil.isNameShort(fullName)) {
		q = new QName(null, Name.make(fullName), hash);
	    }
	    else {
		String container = StringUtil.getPackageComponent(fullName);
		String name = StringUtil.getShortNameComponent(fullName);
		q = new QName(make(container), Name.make(name), hash);
	    }
	    internCache.put(fullName, q);
	    return q;
	}
    }

    public final void equals(String s) { }
    public final void equals(Name s) { }

    public boolean equals(Object o) {
        return this == o;
    }

    public int hashCode() {
	return hash;
    }

    public String toString() {
	if (qualifier == null)
	    return name.toString();
	return qualifier.toString() + "." + name.toString();
    }

}
