/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launching;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.IPropertyTester;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IFileEditorInput;

import polyglot.types.ClassType;
import x10dt.ui.utils.X10Utils;

/**
 * 
 * 
 * @author egeay
 */
public class X10LaunchablePropertyTester extends PropertyTester implements IPropertyTester {

  // --- Interface methods implementation
  
  public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
    if (receiver instanceof IAdaptable) {
      final IAdaptable adaptableReceiver = (IAdaptable) receiver;
      IFile file = (IFile) adaptableReceiver.getAdapter(IFile.class);
      if (file == null) {
        final IFileEditorInput fileEditorInput = (IFileEditorInput) adaptableReceiver.getAdapter(IFileEditorInput.class);
        if (fileEditorInput != null) {
          file = fileEditorInput.getFile();
        }
      }
      if (file != null) {
        if (PROPERTY_HAS_MAIN.equals(property)) {
          return hasMain(file);
        }
      }
    }
    return false;
  }
  
  // --- Private code
  
  private boolean hasMain(final IFile file) {
    if (X10_EXT.equals(file.getFileExtension())) {
      try {
        final Collection<ClassType> x10Types = new ArrayList<ClassType>();
        X10Utils.collectX10MainTypes(x10Types, new ResourceToJavaElementAdapter(file), new NullProgressMonitor());
        if (x10Types.size() == 0) {
          return false;
        } else {
          return true;
        }
      } catch (Exception except) {
        // Simply forget
      }
    }
    return false;
  }
  
  // --- Fields
  
  private static final String PROPERTY_HAS_MAIN = "hasMain"; //$NON-NLS-1$
  
  private static final String X10_EXT = "x10"; //$NON-NLS-1$

}
