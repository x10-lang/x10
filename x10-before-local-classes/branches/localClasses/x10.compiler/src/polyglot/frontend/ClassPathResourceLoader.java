/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.frontend;

import java.io.File;
import java.util.*;

import polyglot.main.Report;

/**
 * We implement our own class loader. All this pain is so we can define the
 * classpath on the command line.
 */
public class ClassPathResourceLoader {
	protected List<File> classpath;
	protected ResourceLoader loader;

	public ClassPathResourceLoader(List<File> classpath) {
		this.classpath = new ArrayList<File>(classpath);
		this.loader = new ResourceLoader();
	}

	public ClassPathResourceLoader(String classpath) {
		this.classpath = new ArrayList();

		StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);

		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			this.classpath.add(new File(s));
		}

		this.loader = new ResourceLoader();
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
		if (Report.should_report(verbose, 2)) {
			Report.report(2, "attempting to load class " + name);
			Report.report(2, "classpath = " + classpath);
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

	protected static Collection verbose;

	static {
		verbose = new HashSet();
		verbose.add("loader");
	}
}
