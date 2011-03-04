/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launching;

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import polyglot.types.ClassType;
import x10dt.ui.Messages;
import x10dt.ui.X10DTUIPlugin;

/**
 * Selection dialog to select a list of X10 types among a list of types provided.
 * 
 * @author egeay
 */
public final class X10TypeSelectionDialog extends FilteredItemsSelectionDialog {
  
  /**
   * Creates the selection dialog with the required shell and the help of the X10 types transmitted.
   * 
   * @param shell The shell to consider for the selection dialog.
   * @param x10Types The list of X10 types that represents the scope of selection.
   */
  public X10TypeSelectionDialog(final Shell shell, final Collection<ClassType> x10Types) {
    super(shell, false);
    
    this.fX10Types = x10Types;
    
    setTitle(Messages.XTSD_SelectX10TypeDlgTitle);
    setMessage(Messages.XTSD_SelectX10TypeMsg);
    setInitialPattern("**"); //$NON-NLS-1$
    setListLabelProvider(new X10TypeLabelProvider());
  }
  
  // --- Abstract methods implementation

  protected Control createExtendedContentArea(final Composite parent) {
    // Nothing to do.
    return null;
  }

  protected ItemsFilter createFilter() {
    return new X10TypeItemsFilter();
  }

  protected void fillContentProvider(final AbstractContentProvider contentProvider, final ItemsFilter itemsFilter,
                                     final IProgressMonitor progressMonitor) throws CoreException {
    for (final ClassType classType : this.fX10Types) {
      if (itemsFilter.isConsistentItem(classType)) {
        contentProvider.add(classType, itemsFilter);
      }
    }
  }

  protected IDialogSettings getDialogSettings() {
    final IDialogSettings dialogSettings = X10DTUIPlugin.getInstance().getDialogSettings();
    IDialogSettings section = dialogSettings.getSection(SETTINGS_ID);
    if (section == null) {
      section = dialogSettings.addNewSection(SETTINGS_ID);
    } 
    return section;
  }

  public String getElementName(final Object item) {
    return ((ClassType) item).name().toString();
  }

  protected Comparator<?> getItemsComparator() {
    return new X10TypesComparator();
  }

  protected IStatus validateItem(final Object item) {
    return Status.OK_STATUS;
  }
  
  // --- Private code
  
  private static final class X10TypesComparator implements Comparator<Object> {

    public int compare(final Object lhs, final Object rhs) {
      return ((ClassType) lhs).name().toString().compareTo(((ClassType) rhs).name().toString());
    }
    
  }
  
  private final class X10TypeItemsFilter extends ItemsFilter {

    // --- Abstract methods implementation
    
    public boolean isConsistentItem(final Object item) {
      return item instanceof ClassType;
    }

    public boolean matchItem(final Object item) {
      if (X10TypeSelectionDialog.this.fX10Types.contains(item)) {
        return matches(((ClassType) item).name().toString());
      } else {
        return false;
      }
    }
    
  }
  
  private static final class X10TypeLabelProvider implements ILabelProvider {

    // --- Interface methods implementation
    
    public Image getImage(final Object element) {
      return null;
    }

    public String getText(final Object element) {
      if (element == null) {
        return null;
      }
      final ClassType classType = (ClassType) element;
      final StringBuilder sb = new StringBuilder();
      sb.append(classType.name().toString());
      if ((classType.package_()) != null && classType.package_().fullName().toString().length() > 0) {
        sb.append(" - ").append(classType.package_().fullName().toString()); //$NON-NLS-1$
      }
      return sb.toString();
    }

    public void addListener(final ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(final Object element, final String property) {
      return false;
    }

    public void removeListener(final ILabelProviderListener listener) {
    }
    
  }
  
  // --- Fields
  
  private final Collection<ClassType> fX10Types;
  
  private static final String SETTINGS_ID = X10DTUIPlugin.PLUGIN_ID + ".MAIN_METHOD_SELECTION_DIALOG"; //$NON-NLS-1$

}
