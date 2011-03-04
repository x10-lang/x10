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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.Assert;

import org.junit.Test;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.frontend.ZipResource;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Ref;
import polyglot.types.UnknownType;
import polyglot.visit.NodeVisitor;
import x10dt.ui.launch.core.Constants;

/**
 * Test cases for {@link IndexingCompiler} class.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class RuntimeIndexingTests extends NodeVisitor {
  
  // --- Test cases
  
  @Test public void run() throws IOException, URISyntaxException {
    final URL jarURL = getClass().getClassLoader().getResource("x10.jar");
    assertNotNull("Could not find x10.jar with class loader transmitted.", jarURL);
    
    final IndexingCompiler compiler = new IndexingCompiler();
    
    final List<File> sourcePath = new ArrayList<File>();
    final File file = new File(jarURL.toURI());
    sourcePath.add(file);
    
    final Collection<Source> sources = new ArrayList<Source>();
    final JarFile jarFile = new JarFile(file);
    final Enumeration<? extends JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      final JarEntry entry = entries.nextElement();
      if (entry.getName().endsWith(Constants.X10_EXT)) {
        sources.add(new FileSource(new ZipResource(file, jarFile, entry.getName())));
      }
    }
    
    for (final Job job : compiler.compile("", sourcePath, sources)) {
      if (job.ast() != null) {
        job.ast().visit(this);
      }
    }
  }
  
  // --- Overridden methods
  
  public NodeVisitor enter(final Node node) {
    if (node instanceof ClassDecl) {
      final ClassDecl classDecl = (ClassDecl) node;
      final ClassDef classDef = classDecl.classDef();
      
      for (final ConstructorDef ctor : classDef.constructors()) {
        for (final Ref<? extends polyglot.types.Type> formalType : ctor.formalTypes()) {
          Assert.assertFalse(String.format("Formal type for %s.%s is unknown", classDef.fullName().toString(), 
                                           ctor.signature()), 
                             formalType.get() instanceof UnknownType);
        }
      }
    }
    return this;
  }

}
