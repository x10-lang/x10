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

import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.test.IRTests;
import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.classLoader.JarFileModule;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;

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

    public X10IRTests(String name) {
    	super(name,  null);
    	setTestSrcPath("." + File.separator + "testSrc");
    }

    protected ClassLoaderReference getSourceLoader() {
        return X10SourceLoaderImpl.X10SourceLoader;
    }

    protected JavaSourceAnalysisEngine getAnalysisEngine(final String[] mainClassDescriptors) {
        JavaSourceAnalysisEngine engine = new X10SourceAnalysisEngine() {
          protected Iterable<Entrypoint> makeDefaultEntrypoints(AnalysisScope scope, IClassHierarchy cha) {
              return Util.makeMainEntrypoints(X10SourceLoaderImpl.X10SourceLoader, cha, mainClassDescriptors);
          }
        };
        engine.setExclusionsFile(CallGraphTestUtil.REGRESSION_EXCLUSIONS);
        return engine;
    }

    private String testBaseName() {
        return getName().substring(4);
    }

    protected String singleJavaInputForTest() {
	return testBaseName() + ".x10";
    }

    protected String singleJavaPkgInputForTest(String pkgName) {
	return pkgName + File.separator + testBaseName() + ".x10";
    }

    @Override
    protected void populateScope(JavaSourceAnalysisEngine engine, Collection<String> sources, List<String> libs) throws IOException {
        super.populateScope(engine, Collections.<String>emptySet(), libs);

        for(String modPath: x10SystemModules) {
            ((X10SourceAnalysisEngine) engine).addX10SystemModule(new JarFileModule(new JarFile(modPath)));
        }
        for(String modPath: sources) {
            ((X10SourceAnalysisEngine) engine).addX10SourceModule(new SourceFileModule(new File(modPath), modPath));
        }
    }
    
    // --- Array
    
    public void testArray1() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testArray2v() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testArray3Double() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testArrayCopy3() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    public void testArrayAccess1() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    public void testBoxArrayAssign() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testIntArrayInitializerShorthand() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testUserArrayBounds3D() {
        runTest(singleTestSrc("Array"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Async

    public void testAsyncFieldAccess() {
        runTest(singleTestSrc("Async"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    public void testAsyncNext() {
        runTest(singleTestSrc("Async"), rtJar, simpleTestEntryPoint(), emptyList, true);
    }
    
    public void testAsyncReturn() {
        runTest(singleTestSrc("Async"), rtJar, simpleTestEntryPoint(), emptyList, true);
    }
    
    public void testAsyncTest2() {
        runTest(singleTestSrc("Async"), rtJar, simpleTestEntryPoint(), emptyList, true);
    }
    
    // --- AtEach

    public void testAtEach() {
        runTest(singleTestSrc("AtEach"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testAtEach2() {
        runTest(singleTestSrc("AtEach"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testAtEachLoopOnArray() {
        runTest(singleTestSrc("AtEach"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Atomic
    
    public void testAtomic1() {
        runTest(singleTestSrc("Atomic"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testAtomic2() {
        runTest(singleTestSrc("Atomic"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testAtomicReturn() {
        runTest(singleTestSrc("Atomic"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testAwaitTest() {
        runTest(singleTestSrc("Atomic"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testConditionalAtomicTest() {
        runTest(singleTestSrc("Atomic"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Closure
    
    public void testClosureBody2() {
        runTest(singleTestSrc("Closure"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testClosureConstraint1() {
        runTest(singleTestSrc("Closure"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testClosureExample1() {
        runTest(singleTestSrc("Closure"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testClosureExample2() {
        runTest(singleTestSrc("Closure"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testClosureExample3() {
        runTest(singleTestSrc("Closure"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testClosureExample4() {
        runTest(singleTestSrc("Closure"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testClosureObject1() {
        runTest(singleTestSrc("Closure"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Constructor
    
    public void testPropertyAssign() {
        runTest(singleTestSrc("Constructor"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Finish
    
    public void testFinishTest1() {
        runTest(singleTestSrc("Finish"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testFinishTest2() {
        runTest(singleTestSrc("Finish"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- For
    
    public void testForLoop() {
        runTest(singleTestSrc("For"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testForLoop2() {
        runTest(singleTestSrc("For"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testForLoop3() {
        runTest(singleTestSrc("For"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testForLoop4() {
        runTest(singleTestSrc("For"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testForLoopOnArray() {
        runTest(singleTestSrc("For"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- ForEach
    
    public void testForEach1() {
        runTest(singleTestSrc("ForEach"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testForEach2() {
        runTest(singleTestSrc("ForEach"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Future
    
    public void testFuture0() {
        runTest(singleTestSrc("Future"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testFuture1() {
        runTest(singleTestSrc("Future"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testFutureForce() {
        runTest(singleTestSrc("Future"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testFutureTest2() {
        runTest(singleTestSrc("Future"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

    public void testFutureTest5() {
        runTest(singleTestSrc("Future"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Place
    
    public void testPlaceCheckArray() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckGenericClass() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckInnerClass() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckInRail() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckRail() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckReverse() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckStaticClass() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckStringBuilder() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testPlaceCheckValueClass() {
        runTest(singleTestSrc("Place"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    // --- Region
    
    public void testArrayOfRegions() {
        runTest(singleTestSrc("Region"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }
    
    public void testRegionWithHoles() {
        runTest(singleTestSrc("Region"), rtJar, simpleTestEntryPoint(), emptyList, false);
    }

}
