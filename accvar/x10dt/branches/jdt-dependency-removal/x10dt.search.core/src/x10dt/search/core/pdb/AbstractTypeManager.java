/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.IWriter;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.io.PBFReader;
import org.eclipse.imp.pdb.facts.io.PBFWriter;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import x10dt.search.core.Messages;
import x10dt.search.core.SearchCoreActivator;


abstract class AbstractTypeManager implements ITypeManager {
  
  AbstractTypeManager(final Type type) {
    this.fType = type;
  }
  
  // --- Abstract methods implementation

  public final Type getType() {
    return this.fType;
  }
  
  public final IWriter getWriter() {
    return this.fWriter;
  }
  
  // --- Code for descendants
  
  protected final void createIndexingFile(final IValue value) {
    final Bundle bundle = SearchCoreActivator.getInstance().getBundle();
    final File pluginStateLocation = Platform.getStateLocation(bundle).toFile();
    final File indexingFile = new File(pluginStateLocation, 
                                       String.format(SEPARATOR_FORMAT, getType().toString(), getVersion(bundle)));
    try {
      PBFWriter.writeValueToFile(value, indexingFile, SearchDBTypes.getInstance().getTypeStore());
    } catch (IOException except) {
      SearchCoreActivator.log(IStatus.ERROR, NLS.bind(Messages.ATM_IndexingSavingError, indexingFile.getAbsolutePath(), 
                                                      getType().getName()), except);
    }
  }
  
  protected final void loadIndexingFileForManagedType(final FactBase factBase, final IFactContext context) {
    final Bundle bundle = SearchCoreActivator.getInstance().getBundle();
    final File pluginStateDirBase = Platform.getStateLocation(bundle).toFile();
    final IValueFactory valueFactory = ValueFactory.getInstance();
    final File indexingFile = new File(pluginStateDirBase, 
                                       String.format(SEPARATOR_FORMAT, getType().toString(), getVersion(bundle)));
    if (indexingFile.exists()) {
      try {
        final IValue value = PBFReader.readValueFromFile(valueFactory, new TypeStore(), indexingFile);
        factBase.defineFact(new FactKey(this.fType, context), value);
      } catch (IOException except) {
        SearchCoreActivator.log(IStatus.ERROR, NLS.bind(Messages.ATM_IndexingLoadingError, indexingFile.getAbsolutePath(), 
                                                        getType().getName()), except);
      }
    }
  }
  
  // --- Private code
  
  private String getVersion(final Bundle bundle) {
    final Version version = bundle.getVersion();
    return String.format("%d.%d.%d.%s", version.getMajor(), version.getMinor(), version.getMicro(), //$NON-NLS-1$
                         version.getQualifier());
  }
  
  // --- Fields
  
  private final Type fType;
  
  protected IWriter fWriter;
  
  
  private static final String SEPARATOR_FORMAT = "%s_%s"; //$NON-NLS-1$

}
