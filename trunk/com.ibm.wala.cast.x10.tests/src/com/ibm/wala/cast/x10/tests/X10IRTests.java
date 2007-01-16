/*
 * Created on Oct 21, 2005
 */
package com.ibm.domo.ast.x10.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ibm.domo.ast.java.client.EclipseProjectSourceAnalysisEngine;
import com.ibm.domo.ast.java.test.IRTests;
import com.ibm.domo.ast.x10.client.X10EclipseSourceAnalysisEngine;

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

    protected EclipseProjectSourceAnalysisEngine getAnalysisEngine() {
	return new X10EclipseSourceAnalysisEngine();
    }

    protected String singleInputForTest() {
	return getName().substring(4) + ".x10";
    }

    protected String singlePkgInputForTest(String pkgName) {
	return pkgName + File.separator + getName().substring(4) + ".x10";
    }

    public void testAsync1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null);
    }

    public void testFuture1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null);
    }

    public void testFinish1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null);
    }

    public void testFor1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null);
    }

    public void testForEach1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null);
    }

//    public void testAtEach1() {
//	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
//		new GraphAssertions(), null);
//    }

    public void testWhen1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null);
    }

    public void testArrayCtor1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null);
    }
}
