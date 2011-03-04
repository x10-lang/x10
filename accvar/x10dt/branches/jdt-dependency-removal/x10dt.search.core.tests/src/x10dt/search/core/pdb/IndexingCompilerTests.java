/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.junit.Assert;
import org.junit.Test;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.frontend.FileResource;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

/**
 * Test cases for {@link IndexingCompiler} class.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class IndexingCompilerTests extends NodeVisitor {
  
  // --- Test cases
  
  @Test public void compileEQSolver() throws IOException, URISyntaxException {
    final String testCode = "indexing_compiler/EQSolver.x10";
    
    final URL codeURL = getClass().getClassLoader().getResource(testCode);
    assertNotNull(NLS.bind("Could not find ''{0}'' with class loader transmitted.", testCode), codeURL);
    
    final URL jarURL = getClass().getClassLoader().getResource("x10.jar");
    assertNotNull("Could not find x10.jar with class loader transmitted.", codeURL);
    
    final IndexingCompiler compiler = new IndexingCompiler();
    
    final File sourceFile = new File(codeURL.toURI());
    
    final List<File> sourcePath = new ArrayList<File>();
    sourcePath.add(sourceFile);
    sourcePath.add(new File(jarURL.toURI()));
    
    final Source source = new FileSource(new FileResource(sourceFile));
    for (final Job job : compiler.compile("", sourcePath, Arrays.asList(source))) {
      if (job.ast() != null) {
        job.ast().visit(this);
      }
    }
  }
  
  @Test public void emptyClassTest() throws IOException, URISyntaxException {
    final String testCode = "indexing_compiler/Splat.x10";
    
    final URL codeURL = getClass().getClassLoader().getResource(testCode);
    assertNotNull(NLS.bind("Could not find ''{0}'' with class loader transmitted.", testCode), codeURL);
    
    final URL jarURL = getClass().getClassLoader().getResource("x10.jar");
    assertNotNull("Could not find x10.jar with class loader transmitted.", codeURL);
    
    final IndexingCompiler compiler = new IndexingCompiler();
    
    final File sourceFile = new File(codeURL.toURI());
    
    final List<File> sourcePath = new ArrayList<File>();
    sourcePath.add(sourceFile);
    sourcePath.add(new File(jarURL.toURI()));
    
    final Source source = new FileSource(new FileResource(sourceFile));
    for (final Job job : compiler.compile("", sourcePath, Arrays.asList(source))) {
      if (job.ast() != null) {
        job.ast().visit(this);
      }
    }
  }
  
  // --- Overridden methods
  
  @SuppressWarnings("unused")
  public NodeVisitor enter(final Node node) {
    if (node instanceof ClassDecl) {
      final ClassDecl classDecl = (ClassDecl) node;
      final ClassDef classDef = classDecl.classDef();
      final ClassType classType = classDef.asType();
      
      final Position position = classType.position();
      String path = position.file();
      
      final StringBuilder scheme = new StringBuilder();
      if (position.file().contains(".jar:")) { //$NON-NLS-1$
        scheme.append("jar:"); //$NON-NLS-1$
      }
      scheme.append("file"); //$NON-NLS-1$
      
      final String osName = System.getProperty("os.name"); //$NON-NLS-1$
      if (osName.startsWith("Windows")) { //$NON-NLS-1$
        path = '/' + path;
      }
      try {
        new URI(scheme.toString(), null /* host */, path, null /* fragment */);
      } catch (URISyntaxException except) {
        Assert.assertFalse(except.getMessage(), true);
      }
    }
    return this;
  }

}
