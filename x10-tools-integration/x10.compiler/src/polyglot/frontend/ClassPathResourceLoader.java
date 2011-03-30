/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
