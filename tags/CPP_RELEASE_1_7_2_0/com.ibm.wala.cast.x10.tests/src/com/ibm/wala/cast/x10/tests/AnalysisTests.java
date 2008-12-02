/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.tests;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;
import polyglot.frontend.Compiler;
import polyglot.main.Options;
import polyglot.main.Main.TerminationException;
import polyglot.util.ErrorQueue;
import polyglot.util.StdErrorQueue;

import com.ibm.wala.cast.x10.translator.polyglot.X10AnalysisExtension;

public class AnalysisTests extends TestCase {
    protected static String testSrcPath= "." + File.separator + "testSrc";

    public AnalysisTests(String name) {
	super(name);
    }

//    protected abstract String singleInputForTest();

//    protected abstract String singlePkgInputForTest(String pkgName);

    protected X10AnalysisExtension getPolyglotExtension() {
	return new X10AnalysisExtension();
    }

//    protected Collection singleTestSrc() {
//	return Collections.singletonList(testSrcPath + File.separator + singleInputForTest());
//    }
//
//    protected Collection singlePkgTestSrc(String pkgName) {
//	return Collections.singletonList(testSrcPath + File.separator + singlePkgInputForTest(pkgName));
//    }

    public void runTest(Collection/*<String>*/sources) {
	X10AnalysisExtension ext= getPolyglotExtension();

	Options options= ext.getOptions();

	// Allow all objects to get access to the Options object. This hack should
	// be fixed somehow. XXX###@@@
	Options.global= options;

	ErrorQueue eq= new StdErrorQueue(System.err, options.error_count, ext.compilerName());
	Compiler compiler= new Compiler(ext, eq);

	if (!compiler.compile(sources)) {
	    throw new TerminationException(1);
	}
    }

    public void testFoo() {
	runTest(Collections.singletonList("testSrc/Foo.x10"));
    }
}
