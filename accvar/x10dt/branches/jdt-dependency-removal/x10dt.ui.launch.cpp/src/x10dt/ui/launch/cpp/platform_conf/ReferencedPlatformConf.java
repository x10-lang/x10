/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.XMLMemento;


final class ReferencedPlatformConf implements IX10PlatformConf {
  
  ReferencedPlatformConf(final IX10PlatformConf source) {
    this.fSource = source;
    this.fId = UUID.randomUUID().toString();
  }
  
  // --- Interface methods implementation

  public IFile getConfFile() {
    return this.fSource.getConfFile();
  }

  public IX10PlatformConfWorkCopy createWorkingCopy() {
    return new X10PlatformConfWorkCopy((X10PlatformConf) this.fSource);
  }

  public ICommunicationInterfaceConf getCommunicationInterfaceConf() {
    return this.fSource.getCommunicationInterfaceConf();
  }

  public IConnectionConf getConnectionConf() {
    return this.fSource.getConnectionConf();
  }

  public ICppCompilationConf getCppCompilationConf() {
    return this.fSource.getCppCompilationConf();
  }
  
  public IDebuggingInfoConf getDebuggingInfoConf() {
    return this.fSource.getDebuggingInfoConf();
  }

  public String getDescription() {
    return this.fSource.getDescription();
  }

  public String getId() {
    return this.fId;
  }

  public String getName() {
    return this.fSource.getName();
  }
  
  public boolean isComplete(final boolean onlyCompilation) {
    return this.fSource.isComplete(onlyCompilation);
  }

  public void save(final Writer writer) throws IOException {
    final XMLMemento platformTag = XMLMemento.createWriteRoot(PLATFORM_TAG);
    
    platformTag.createChild(ID_TAG).putTextData(this.fId);
    platformTag.createChild(ID_REF_TAG).putTextData(this.fSource.getId());
    
    platformTag.save(writer);
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    return this.fSource.equals(rhs);
  }
  
  public int hashCode() {
    return this.fSource.hashCode();
  }
  
  public final String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Id: ").append(this.fId).append("Ref Id: ").append(this.fSource.getId()) //$NON-NLS-1$ //$NON-NLS-2$
      .append("\nName: ").append(this.fSource.getName()) //$NON-NLS-1$
      .append("\nDescription: ").append(this.fSource.getDescription()) //$NON-NLS-1$
      .append('\n').append(this.fSource.getConnectionConf()).append('\n').append(this.fSource.getCommunicationInterfaceConf())
      .append('\n').append(this.fSource.getCppCompilationConf());
    return sb.toString();
  }
  
  // --- Fields
  
  private final IX10PlatformConf fSource;
  
  private final String fId;
  

  private static final String PLATFORM_TAG = "platform"; //$NON-NLS-1$
  
  private static final String ID_REF_TAG = "id-ref"; //$NON-NLS-1$
  
  private static final String ID_TAG = "id"; //$NON-NLS-1$

}
