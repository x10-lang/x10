/*
 * Created on Oct 21, 2005
 */
package com.ibm.wala.cast.x10.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

import org.junit.Test;

import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.test.IRTests;
import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.classLoader.JarFileModule;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.collections.Pair;

public class X10IRTests extends IRTests {
    protected static List<String> x10SystemModules;

    protected static String x10LibPath= "." + File.separator + "lib";

    static {
	x10SystemModules= new ArrayList<String>();
	x10SystemModules.add(x10LibPath + File.separator + "x10-runtime.jar");

	try {
	    // Force X10 version of CAstPrinter to be used...
	    Class.forName("com.ibm.wala.cast.x10.translator.X10CAstPrinter");
	} catch (ClassNotFoundException e) {
	    System.err.println(e.getMessage());
	}
    }

    public X10IRTests() {
    	super(null);
    	setTestSrcPath("." + File.separator + "testSrc");
    }

    protected ClassLoaderReference getSourceLoader() {
        return X10SourceLoaderImpl.X10SourceLoader;
    }

    protected JavaSourceAnalysisEngine getAnalysisEngine(final String[] mainClassDescriptors) {
	JavaSourceAnalysisEngine engine = new X10SourceAnalysisEngine() {
          protected Iterable<Entrypoint>
            makeDefaultEntrypoints(AnalysisScope scope, IClassHierarchy cha) 
	  {
	    return Util.makeMainEntrypoints(X10SourceLoaderImpl.X10SourceLoader, cha, mainClassDescriptors);
	  }
	};
	engine.setExclusionsFile(CallGraphTestUtil.REGRESSION_EXCLUSIONS);
	return engine;
    }

    protected String singleJavaInputForTest() {
	return getTestName().substring(4) + ".x10";
    }

    protected String singleJavaPkgInputForTest(String pkgName) {
	return pkgName + File.separator + getTestName().substring(4) + ".x10";
    }

    @Override
    protected void populateScope(JavaSourceAnalysisEngine engine, Collection<String> sources, List<String> libs) throws IOException {
	super.populateScope(engine, Collections.EMPTY_SET, libs);

        for(String modPath: x10SystemModules) {
            ((X10SourceAnalysisEngine) engine).addX10SystemModule(new JarFileModule(new JarFile(modPath)));
        }
        for(String modPath: sources) {
            ((X10SourceAnalysisEngine) engine).addX10SourceModule(new SourceFileModule(new File(modPath), modPath));
        }
    }

    @Test public void testAsync1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    @Test public void testAsyncInvoke() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }

    @Test public void testFuture1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }

    @Test public void testFinish1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    @Test public void testFor1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    @Test public void testForEach1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    @Test public void testWhen1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }

//    public void testAtEach1() {
//	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(), emptyList, true);
//    }

    @Test public void testArrayAccess1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }

    @Test public void testArrayAccess2D() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }

    @Test public void testArrayAccess3D() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }

    @Test public void testArrayCtor1() {
	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }
    
    @Test public void testPlaces() {
    	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    @Test public void testx10ForLoopBreakTest() {
    	runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), emptyList, true);
    }

    @Test public void testHashTable() {
    	runTest(singlePkgTestSrc("p"), rtJar, simplePkgTestEntryPoint("p"), emptyList, false);
    }
}
