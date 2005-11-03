/*
 * Created on Oct 21, 2005
 */
package com.ibm.domo.ast.x10.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.domo.ast.java.test.IRTests;
import com.ibm.domo.ast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.domo.ast.x10.translator.polyglot.X10IRTranslatorExtension;

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

    protected String singleInputForTest() {
	return getName().substring(4) + ".x10";
    }

    public void testAsync1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }

    public void testFuture1() {
	runTest(singleTestSrc(), x10RTJar, simpleTestEntryPoint(),
		new GraphAssertions());
    }
}
