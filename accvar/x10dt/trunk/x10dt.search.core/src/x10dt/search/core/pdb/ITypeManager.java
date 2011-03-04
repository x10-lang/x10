/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import org.eclipse.core.resources.IResource;
import org.eclipse.imp.pdb.analysis.AnalysisException;
import org.eclipse.imp.pdb.facts.IWriter;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.type.Type;

import polyglot.visit.NodeVisitor;

/**
 * Responsible for managing fact writing for a given database type.
 * 
 * @author egeay
 */
public interface ITypeManager {
  
  /**
   * Clears the current writer associated with the current type.
   * 
   * <p>After this call, {@link #getWriter()} will return <b>null</b> unless a call to <i>initWriter</i> has been done in
   * between.
   */
  public void clearWriter();
  
  /**
   * Creates an indexing file for the given fact value associated with the type managed.
   * 
   * @param factBase The fact database instance to consider.
   * @param factContext The context to use in order to identify uniquely the fact key for the current type.
   */
  public void createIndexingFile(final FactBase factBase, final IFactContext factContext);
  
  /**
   * Creates an X10 {@link NodeVisitor} that will be responsible for writing the data of interest for the current type.
   * 
   * @param scopeTypeName The scope type name for the fact writer.
   * @return A non-null visitor instance.
   */
  public NodeVisitor createNodeVisitor(final String scopeTypeName);
  
  /**
   * Returns the current type managed by this manager.
   * 
   * @return A non-null {@link Type} instance.
   */
  public Type getType();
  
  /**
   * Returns the writer for the current type encapsulated.
   * 
   * @return A non-null instance <b>only if </b> a previous call to one of the two "initWriter" methods has been performed,
   * otherwise <b>null</b>.
   */
  public IWriter getWriter();
  
  /**
   * Creates and initializes a writer from the previous state found in the fact database, and makes an appropriate update
   * of the fact database value according to the resource change to process.
   * 
   * @param factBase The fact database instance to consider.
   * @param factContext The context to use in order to identify uniquely the fact for the current type.
   * @param resource The current resource to process.
   * @throws AnalysisException Occurs if we could not find the database key with the context given and the current type
   * managed.
   */
  public void initWriter(final FactBase factBase, final IFactContext factContext, 
                         final IResource resource) throws AnalysisException;
  
  /**
   * Loads the content of an indexing file (if it exists) for the given type managed.
   * 
   * @param factBase The fact database instance to consider.
   * @param factContext The context to use in order to identify uniquely the fact key for the current type.
   */
  public void loadIndexingFile(final FactBase factBase, final IFactContext factContext);
  
  /**
   * Transfers the data in the "writer" within the fact database.
   * 
   * @param factBase The fact database instance to consider.
   * @param factContext The context to use in order to identify uniquely the fact key for the current type.
   */
  public void writeDataInFactBase(final FactBase factBase, final IFactContext factContext);

}
