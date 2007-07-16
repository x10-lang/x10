/*
 * Created on Oct 21, 2005
 */
package com.ibm.domo.ast.x10.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ibm.domo.ast.x10.client.X10SourceAnalysisEngine;
import com.ibm.domo.ast.x10.translator.X10CAstPrinter;
import com.ibm.domo.ast.x10.translator.X10CastNode;
import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.test.IRTests;
import com.ibm.wala.eclipse.util.*;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;

public class X10IRTests extends IRTests {
    protected static List/*<String>*/ x10RTJar;

    protected static String x10LibPath= "." + File.separator + "lib";

    static {
	x10RTJar= new ArrayList();
	x10RTJar.addAll(rtJar);
	x10RTJar.add(x10LibPath + File.separator + "x10-runtime.jar");

	try {
	    // Force X10 version of CAstPrinter to be used...
	    Class.forName("com.ibm.domo.ast.x10.translator.X10CAstPrinter");
	} catch (ClassNotFoundException e) {
	    System.err.println(e.getMessage());
	}
    }

    public X10IRTests(String name) {
	super(name);
    }

    protected JavaSourceAnalysisEngine getAnalysisEngine(final String[] mainClassDescriptors) {
	return new X10SourceAnalysisEngine() {
          protected Iterable<Entrypoint>
            makeDefaultEntrypoints(AnalysisScope scope, ClassHierarchy cha) 
	  {
	    return Util.makeMainEntrypoints(EclipseProjectPath.SOURCE_REF, cha, mainClassDescriptors);
	  }
	};
    }

    protected String singleInputForTest() {
	return getName().substring(4) + ".x10";
    }

    protected String singlePkgInputForTest(String pkgName) {
	return pkgName + File.separator + getName().substring(4) + ".x10";
    }

    public void testAsync1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, false);
    }

    public void testAsyncInvoke() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
     		new GraphAssertions(), null, true);
    }

    public void testFuture1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, true);
    }

    public void testFinish1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, false);
    }

    public void testFor1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, false);
    }

    public void testForEach1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, false);
    }

    public void testWhen1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, true);
    }

//    public void testAtEach1() {
//	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
//		new GraphAssertions(), null, true);
//    }

    public void testArrayAccess1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, true);
    }
    public void testArrayCtor1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions(), null, true);
    }

    /*    
    public void testHashTable() {
    	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
    			new GraphAssertions(), null, true);
    }
    */
}
