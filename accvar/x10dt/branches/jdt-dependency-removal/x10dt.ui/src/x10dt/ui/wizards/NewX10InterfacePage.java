/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

 *******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package x10dt.ui.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ISourceRoot;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.ui.typeHierarchy.SearchUtils;

/**
 * The "New X10 Class" wizard page allows setting the package for the new class as well as the class name.
 */
public class NewX10InterfacePage extends NewTypeWizardPage {
  
  private static final String PAGE_NAME = "wizardPage";

  public NewX10InterfacePage(ISelection selection) {
    super(LanguageRegistry.findLanguage("X10"), false, PAGE_NAME);
    setTitle("X10 Interface");
    setDescription("This wizard creates a new X10 interface in a user-specified package.");
  }

  /**
   * The wizard owning this page is responsible for calling this method with the current selection. The selection is used to
   * initialize the fields of the wizard page.
   * 
   * @param selection
   *          used to initialize the fields
   */
  public void init(IStructuredSelection selection) {
    ISourceEntity jelem = getInitialJavaElement(selection);
    initContainerPage(jelem);
    initTypePage(jelem);
    doStatusUpdate();

    // IDialogSettings section= getDialogSettings().getSection(PAGE_NAME);
  }

  // ------ validation --------
  private void doStatusUpdate() {
    // status of all used components
    IStatus[] status = new IStatus[] { fContainerStatus, isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
                                      fTypeNameStatus, fModifierStatus, fSuperClassStatus, fSuperInterfacesStatus };

    // the mode severe status will be displayed and the OK button enabled/disabled.
    updateStatus(status);
  }

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());

    int nColumns = 4;

    GridLayout layout = new GridLayout();
    layout.numColumns = nColumns;
    composite.setLayout(layout);

    // pick & choose the wanted UI components

    createContainerControls(composite, nColumns);
    createPackageControls(composite, nColumns);
    createEnclosingTypeControls(composite, nColumns);

    createSeparator(composite, nColumns);

    createTypeNameControls(composite, nColumns);
    createModifierControls(composite, nColumns);

    // createSuperClassControls(composite, nColumns);
    createSuperInterfacesControls(composite, nColumns);

    // createMethodStubSelectionControls(composite, nColumns);

    createCommentControls(composite, nColumns);
    enableCommentControl(true);

    setControl(composite);

    Dialog.applyDialogFont(composite);
    // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
    setFocus(); // For some reason, the type name doesn't get focus by the time the dialog is fully up... force it
  }

  /*
   * @see NewContainerWizardPage#handleFieldChanged
   */
  protected void handleFieldChanged(String fieldName) {
    super.handleFieldChanged(fieldName);

    doStatusUpdate();
  }

  public void createType(IProgressMonitor monitor) throws ModelException, CoreException, InterruptedException {
    ISourceFolder pkgFrag = this.getPackageFragment();
    String superClass = this.getSuperClass();
    List<String> superIntfs = this.getSuperInterfaces();
    String typeName = this.getTypeName();

    doCreateType(typeName, pkgFrag, superClass, superIntfs, monitor);
  }

  /**
   * The worker method. It will find the container, create the file if missing or just replace its contents, and open the
   * editor on the newly created file.
   */
  private void doCreateType(String typeName, ISourceFolder pkgFrag, String superClass, List<String> superIntfs,
                            IProgressMonitor monitor) throws CoreException, ModelException {
    monitor.beginTask("Creating " + typeName, 2);
    if (pkgFrag == null) {
        String pkgName = getPackageText();
        ISourceRoot root = this.getPackageFragmentRoot();

        if (!root.getResource().exists()) {
            ISourceProject javaProject= getJavaProject();
            IPathEntry newEntry= ModelFactory.createSourceEntry(new Path(getPackageFragmentRootText()).makeAbsolute(), null);
            List<IPathEntry> entries= javaProject.getBuildPath(fLanguage);
            entries.add(newEntry);
            javaProject.setBuildPath(fLanguage, entries, new NullProgressMonitor());

            root= (ISourceRoot) ModelFactory.create(javaProject.getRawProject().getWorkspace().getRoot().getFolder(newEntry.getRawPath()));
        }
        pkgFrag = root.createSourceFolder(pkgName, true, monitor);
      }
    IResource resource = pkgFrag.getResource();
    IContainer container = (IContainer) resource;

    final IFile file = container.getFile(new Path(typeName + ".x10"));
    try {
      InputStream stream = createContentStream(file, typeName, pkgFrag, superClass, superIntfs);

      if (file.exists()) {
        file.setContents(stream, true, true, monitor);
      } else {
        file.create(stream, true, monitor);
      }
      stream.close();
    } catch (IOException e) {
    }
    monitor.worked(1);
  }

  /**
   * We will initialize file contents with a sample text.
   * 
   * @param sourceFile
   */
  private InputStream createContentStream(IFile sourceFile, String typeName, ISourceFolder pkgFrag, String superClass,
                                          List/* <String> */superIntfs) {
    StringBuffer buff = new StringBuffer();

    if (!BuildPathUtils.isDefaultPackage(pkgFrag.getName()))
      buff.append("package " + pkgFrag.getName() + ";\n\n");
    buff.append("public interface " + typeName);
    if (superIntfs.size() > 0) {
      buff.append(" extends ");
      for (Iterator iter = superIntfs.iterator(); iter.hasNext();) {
        String superIntf = (String) iter.next();
        buff.append(superIntf);
        if (iter.hasNext())
          buff.append(", ");
      }
    }
    buff.append(" {\n" + "}");

    return new StringBufferInputStream(buff.toString());
  }

  /**
   * Returns the resource handle that corresponds to the compilation unit to was or will be created or modified.
   * 
   * @return A resource or null if the page contains illegal values.
   * @since 3.0
   */
  public IResource getModifiedResource() {
    ITypeInfo enclosing = getEnclosingType();

    if (enclosing != null) {
      return SearchUtils.getResource(enclosing);
    }

    ISourceFolder pack = getPackageFragment();
	IContainer packCont = null;
	if (pack == null) {
		ISourceRoot root = getPackageFragmentRoot();
		packCont = ((IContainer) root.getResource()).getFolder(new Path(
				getPackageText().replace('.', '/')));
	} else {
		packCont = (IContainer) pack.getResource();
	}

	if (packCont != null) {
		return packCont.getFile(new Path(getTypeName() + ".x10"));
	}
	return null;
  }
}
