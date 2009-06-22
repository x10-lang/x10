package org.eclipse.imp.x10dt.core.wizards;

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
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * The "New X10 Class" wizard page allows setting the package for the new class
 * as well as the class name.
 */
public class NewX10ClassPage extends NewTypeWizardPage {
    private final static String SETTINGS_CREATEMAIN= "create_main"; //$NON-NLS-1$

    private final static String SETTINGS_CREATECONSTR= "create_constructor"; //$NON-NLS-1$

    private final static String SETTINGS_CREATEUNIMPLEMENTED= "create_unimplemented"; //$NON-NLS-1$

    private static final String PAGE_NAME= "wizardPage";

    public NewX10ClassPage(ISelection selection) {
	super(true, PAGE_NAME);
	setTitle("X10 Class");
	setDescription("This wizard creates a new X10 class in a user-specified package.");
    }

    /**
     * The wizard owning this page is responsible for calling this method with the
     * current selection. The selection is used to initialize the fields of the wizard 
     * page.
     * 
     * @param selection used to initialize the fields
     */
    public void init(IStructuredSelection selection) {
	IJavaElement jelem= getInitialJavaElement(selection);
	initContainerPage(jelem);
	initTypePage(jelem);
	setSuperClass("x10.lang.Object", true);
	doStatusUpdate();

	// TODO RMF 2/7/2005 - re-enable the following to remember dialog settings across invocations
	//	boolean createMain= false;
	//	boolean createConstructors= false;
	//	boolean createUnimplemented= true;
	//	IDialogSettings section= getDialogSettings().getSection(PAGE_NAME);
	//
	//	if (section != null) {
	//	    createMain= section.getBoolean(SETTINGS_CREATEMAIN);
	//	    createConstructors= section.getBoolean(SETTINGS_CREATECONSTR);
	//	    createUnimplemented= section.getBoolean(SETTINGS_CREATEUNIMPLEMENTED);
	//	}

	//	setMethodStubSelection(createMain, createConstructors, createUnimplemented, true);
    }

    // ------ validation --------
    private void doStatusUpdate() {
	// status of all used components
	IStatus[] status= new IStatus[] { fContainerStatus, isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
		fTypeNameStatus, fModifierStatus, fSuperClassStatus, fSuperInterfacesStatus };

	// the mode severe status will be displayed and the OK button enabled/disabled.
	updateStatus(status);
    }

    public void createControl(Composite parent) {
	initializeDialogUnits(parent);

	Composite composite= new Composite(parent, SWT.NONE);
	composite.setFont(parent.getFont());

	int nColumns= 4;

	GridLayout layout= new GridLayout();
	layout.numColumns= nColumns;
	composite.setLayout(layout);

	// pick & choose the wanted UI components

	createContainerControls(composite, nColumns);
	createPackageControls(composite, nColumns);
	createEnclosingTypeControls(composite, nColumns);

	createSeparator(composite, nColumns);

	createTypeNameControls(composite, nColumns);
	createModifierControls(composite, nColumns);

	createSuperClassControls(composite, nColumns);
	createSuperInterfacesControls(composite, nColumns);

	// createMethodStubSelectionControls(composite, nColumns);

	createCommentControls(composite, nColumns);
	enableCommentControl(true);

	setControl(composite);

	Dialog.applyDialogFont(composite);
	PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	setFocus(); // For some reason, the type name doesn't get focus by the time the dialog is fully up... force it
    }

    /*
     * @see NewContainerWizardPage#handleFieldChanged
     */
    protected void handleFieldChanged(String fieldName) {
	super.handleFieldChanged(fieldName);

	doStatusUpdate();
    }

    public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {
	IPackageFragment pkgFrag= this.getPackageFragment();
	String superClass= this.getSuperClass();
	List/*<String>*/superIntfs= this.getSuperInterfaces();
	String typeName= this.getTypeName();

	doCreateType(typeName, pkgFrag, superClass, superIntfs, monitor);
    }

    /**
     * The worker method. It will find the container, create the file if missing or just replace its contents,
     * and open the editor on the newly created file.
     */
    private void doCreateType(String typeName, IPackageFragment pkgFrag, String superClass, List/*<String>*/superIntfs,
	    IProgressMonitor monitor) throws CoreException {
	monitor.beginTask("Creating " + typeName, 2);
	if (!pkgFrag.exists()) {
	    String pkgName= pkgFrag.getElementName();
	    IPackageFragmentRoot root= this.getPackageFragmentRoot();

	    pkgFrag= root.createPackageFragment(pkgName, true, null);
	}
	IResource resource= pkgFrag.getCorrespondingResource();
	IContainer container= (IContainer) resource;

	final IFile file= container.getFile(new Path(typeName + ".x10"));
	try {
	    InputStream stream= createContentStream(file, typeName, pkgFrag, superClass, superIntfs);

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
     * @param sourceFile 
     */
    private InputStream createContentStream(IFile sourceFile, String typeName, IPackageFragment pkgFrag, String superClass,
	    List/*<String>*/superIntfs) {
	StringBuffer buff= new StringBuffer();

	if (!pkgFrag.isDefaultPackage())
	    buff.append("package " + pkgFrag.getElementName() + ";\n\n");
	buff.append("public class " + typeName);
	if (superClass != null && superClass.length() > 0 && !superClass.equals("x10.lang.Object"))
	    buff.append(" extends " + superClass);
	if (superIntfs.size() > 0) {
	    buff.append(" implements ");
	    for(Iterator iter= superIntfs.iterator(); iter.hasNext();) {
		String superIntf= (String) iter.next();
		buff.append(superIntf);
		if (iter.hasNext())
		    buff.append(", ");
	    }
	}
	buff.append(" {\n" + "}");

	return new StringBufferInputStream(buff.toString());
    }

    /**
     * Returns the resource handle that corresponds to the compilation unit to was or
     * will be created or modified.
     * @return A resource or null if the page contains illegal values.
     * @since 3.0
     */
    public IResource getModifiedResource() {
	IType enclosing= getEnclosingType();

	if (enclosing != null) {
	    return enclosing.getResource();
	}

	IPackageFragment pack= getPackageFragment();

	if (pack != null) {
	    IContainer packCont;
	    try {
		packCont= (IContainer) pack.getCorrespondingResource();
	    } catch (JavaModelException e) {
		X10Plugin.getInstance().writeErrorMsg(e.getMessage());
		return null;
	    }
	    return packCont.getFile(new Path(getTypeName() + ".x10"));
	}
	return null;
    }
}
