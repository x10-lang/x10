package com.ibm.wala.cast.x10.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
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
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.strings.Atom;

/**
 * Base class containing actual test cases run via {@link X10TestRunner}.
 * 
 * @author egeay
 */
public class X10CallGraphBuildingTestCaseBaseClass {
  
  // --- Test cases
  
  @Test public void compile(final File testedFile) throws IllegalArgumentException, CancelException, IOException {
    final String mainClass = testedFile.getName().substring(0, testedFile.getName().lastIndexOf('.'));
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
    engine.addX10SourceModule(new SourceDirectoryTreeModule(testedFile.getParentFile()));
    engine.addX10SourceModule(new SourceDirectoryTreeModule(new File("../x10.tests/examples/x10lib")));
    
    engine.addX10SourceModule(new SourceFileModule(testedFile, testedFile.getName()));
    
    final CallGraph callGraph = engine.buildDefaultCallGraph();
    System.err.println(callGraph.toString());
  }

}
