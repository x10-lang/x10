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

import java.io.File;
import java.util.*;

import polyglot.main.Reporter;
import polyglot.util.CollectionUtil; 
import x10.util.CollectionFactory;

/**
 * We implement our own class loader. All this pain is so we can define the
 * classpath on the command line.
 */
public class ClassPathResourceLoader {
	protected List<File> classpath;
	protected ResourceLoader loader;
	protected Reporter reporter;

	public ClassPathResourceLoader(List<File> classpath, Reporter reporter) {
		this.classpath = new ArrayList<File>(classpath);
		this.loader = new ResourceLoader(reporter);
		this.reporter = reporter;
	}

	public ClassPathResourceLoader(String classpath, Reporter reporter) {
        this.classpath = new ArrayList<File>();
        this.reporter = reporter;

		StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);

		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			this.classpath.add(new File(s));
		}

		this.loader = new ResourceLoader(reporter);
	}

	public String classpath() {
		return classpath.toString();
	}

	public boolean dirExists(String name) {
		for (Iterator<File> i = classpath.iterator(); i.hasNext();) {
			File dir = i.next();
			if (loader.dirExists(dir, name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Load a resource from the classpath. If the class is not found, then
	 * <code>null</code> is returned.
	 */
	public Resource loadResource(String name) {
		if (reporter.should_report(Reporter.loader, 2)) {
			reporter.report(2, "attempting to load class " + name);
			reporter.report(2, "classpath = " + classpath);
		}

		for (Iterator<File> i = classpath.iterator(); i.hasNext();) {
			File dir = i.next();
			Resource cf = loader.loadResource(dir, name);
			if (cf != null) {
				return cf;
			}
		}

		return null;
	}
	
}
