package com.ibm.wala.cast.x10.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.runner.RunWith;

import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
import com.ibm.wala.cast.x10.tests.X10TestRunner.TestRootPath;
import com.ibm.wala.cast.x10.tests.X10UniqueTestClassRunner.X10UniqueTest;
import com.ibm.wala.cast.x10.tests.ir.CallEdgeChecker;
import com.ibm.wala.cast.x10.tests.ir.IRChecker;
import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

/**
 * Test cases for <b>com.ibm.wala.cast.x10</b> project.
 * 
 * @author egeay
 */
@RunWith(value=X10UniqueTestClassRunner.class)
@TestRootPath(path="../x10.tests/examples/Constructs")
public final class IRAssertionsTests {
  
  // --- X10 File Tests
  
  @X10UniqueTest(path="Typedefs/TypedefConstraint3a.x10") public void testIRAssertion(final File testFile) throws Exception {
    runTest(new IRChecker[] {
              new CallEdgeChecker("X10Source#TypedefConstraint3a#main#(Lx10/lang/Rail;)V", 
                                  "X10Source#harness/x10Test#execute#()V"),
              new CallEdgeChecker("X10Source#TypedefConstraint3a#run#()Lx10/lang/Boolean;", 
                                  "X10Source#TypedefConstraint3a$A#<init>#(Lx10/lang/Int;)V"),
              new CallEdgeChecker("X10Source#TypedefConstraint3a#run#()Lx10/lang/Boolean;", 
                                  "X10Source#TypedefTest#check#(Lx10/lang/String;Lx10/lang/Int;Lx10/lang/Int;)V")
            }, testFile);
  }
  
  // --- Private code
  
  private void runTest(final IRChecker[] assertions, final File testFile) throws Exception {
    final String mainClass = testFile.getName().substring(0, testFile.getName().lastIndexOf('.'));
    final X10SourceAnalysisEngine engine = new X10SourceAnalysisEngine() {
      protected Iterable<Entrypoint> makeDefaultEntrypoints(final AnalysisScope scope, final IClassHierarchy cha) {
        final ClassLoaderReference loaderRef = X10SourceLoaderImpl.X10SourceLoader;
        final TypeReference typeRef = TypeReference.findOrCreate(loaderRef, 'L' + mainClass);
        final MethodReference mainRef = MethodReference.findOrCreate(typeRef, Atom.findOrCreateAsciiAtom("main"), 
                                                                     Descriptor.findOrCreateUTF8("(Lx10/lang/Rail;)V"));
        final Collection<Entrypoint> entryPoints = new ArrayList<Entrypoint>(1);
        entryPoints.add(new DefaultEntrypoint(mainRef, cha));
        return entryPoints;
      }
      
      public String getExclusionsFile() {
        return null;
      }
    };
    
    engine.addX10SystemModule(new SourceDirectoryTreeModule(new File("../x10.runtime/src-x10")));
    engine.addX10SourceModule(new SourceDirectoryTreeModule(testFile.getParentFile()));
    engine.addX10SourceModule(new SourceDirectoryTreeModule(new File("../x10.tests/examples/x10lib")));
    
    engine.addX10SourceModule(new SourceFileModule(testFile, testFile.getName()));
    
    final CallGraph callGraph = engine.buildDefaultCallGraph();
    for (final IRChecker assertion : assertions) {
      Assert.assertTrue(String.format("Checking of '%s' failed.", assertion.toString()), assertion.isValid(callGraph));
    }
  }
  
}
