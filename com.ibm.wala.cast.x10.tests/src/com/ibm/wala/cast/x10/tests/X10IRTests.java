/*
 * Created on Oct 21, 2005
 */
package com.ibm.domo.ast.x10.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ibm.domo.ast.java.test.IRTests;
import com.ibm.domo.ast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.domo.ast.x10.ipa.callgraph.X10ZeroXCFABuilder;
import com.ibm.domo.ast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.domo.ipa.callgraph.AnalysisOptions;
import com.ibm.domo.ipa.callgraph.CallGraphBuilder;
import com.ibm.domo.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.domo.ipa.cha.ClassHierarchy;
import com.ibm.domo.util.warnings.WarningSet;

public class X10IRTests extends IRTests {
    protected static List/*<String>*/ x10RTJar;

    protected static String x10LibPath= "." + File.separator + "lib";

    static {
	x10RTJar= new ArrayList();
	x10RTJar.addAll(rtJar);
	x10RTJar.add(x10LibPath + File.separator + "x10-runtime.jar");
    }

    public X10IRTests(String name) {
	super(name);
    }

    protected IRTranslatorExtension getPolyglotExtension() {
	X10IRTranslatorExtension translatorTestExtension= new X10IRTranslatorExtension();
	return translatorTestExtension;
    }

    protected CallGraphBuilder createCGBuilder(AnalysisOptions options, ClassHierarchy cha, WarningSet warnings) {
	return new X10ZeroXCFABuilder(cha, warnings, options, null, null, options.getReflectionSpec(), ZeroXInstanceKeys.NONE);
    }

    protected String singleInputForTest() {
	return getName().substring(4) + ".x10";
    }

    protected String singlePkgInputForTest(String pkgName) {
	return pkgName + File.separator + getName().substring(4) + ".x10";
    }

    public void testAsync1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }

    public void testFuture1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }

    public void testFinish1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }

    public void testFor1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }

    public void testForEach1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }

    public void testAtEach1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }

    public void testWhen1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }
}
