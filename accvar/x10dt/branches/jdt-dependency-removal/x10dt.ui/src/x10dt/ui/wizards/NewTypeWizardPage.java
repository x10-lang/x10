/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     John Kaplan, johnkaplantech@gmail.com - 108071 [code templates] template for body of newly created class
 *******************************************************************************/
package x10dt.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.actions.StatusInfo;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ISourceRoot;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.runtime.PluginImages;
import org.eclipse.imp.ui.SWTUtil;
import org.eclipse.imp.ui.dialogs.TableTextCellEditor;
import org.eclipse.imp.ui.wizards.NewContainerWizardPage;
import org.eclipse.imp.ui.wizards.NewWizardMessages;
import org.eclipse.imp.ui.wizards.fields.DialogField;
import org.eclipse.imp.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.imp.ui.wizards.fields.IListAdapter;
import org.eclipse.imp.ui.wizards.fields.IStringButtonAdapter;
import org.eclipse.imp.ui.wizards.fields.ListDialogField;
import org.eclipse.imp.ui.wizards.fields.SelectionButtonDialogField;
import org.eclipse.imp.ui.wizards.fields.SelectionButtonDialogFieldGroup;
import org.eclipse.imp.ui.wizards.fields.Separator;
import org.eclipse.imp.ui.wizards.fields.StringButtonDialogField;
import org.eclipse.imp.ui.wizards.fields.StringButtonStatusDialogField;
import org.eclipse.imp.ui.wizards.fields.StringDialogField;
import org.eclipse.imp.ui.wizards.utils.LayoutUtil;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.imp.utils.LoggingUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import polyglot.types.ClassType;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;
import x10dt.search.ui.dialogs.FilteredTypesSelectionDialog;
import x10dt.search.ui.typeHierarchy.ModelUtil;
import x10dt.search.ui.typeHierarchy.SearchUtils;
import x10dt.search.ui.typeHierarchy.SearchUtils.Flags;
import x10dt.search.ui.typeHierarchy.TextFieldNavigationHandler;
import x10dt.search.ui.typeHierarchy.X10PluginImages;
import x10dt.ui.editor.X10LabelProvider;
import x10dt.ui.utils.X10Utils;



/**
 * The class <code>NewTypeWizardPage</code> contains controls and validation routines
 * for a 'New Type WizardPage'. Implementors decide which components to add and to enable.
 * Implementors can also customize the validation code. <code>NewTypeWizardPage</code>
 * is intended to serve as base class of all wizards that create types like applets, servlets, classes,
 * interfaces, etc.
 * <p>
 * See {@link NewClassWizardPage} or {@link NewInterfaceWizardPage} for an
 * example usage of the <code>NewTypeWizardPage</code>.
 * </p>
 *
 * @see org.eclipse.jdt.ui.wizards.NewClassWizardPage
 * @see org.eclipse.jdt.ui.wizards.NewInterfaceWizardPage
 * @see org.eclipse.jdt.ui.wizards.NewEnumWizardPage
 * @see org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage
 *
 * @since 2.0
 */
public abstract class NewTypeWizardPage extends NewContainerWizardPage {

	/**
	 * Class used in stub creation routines to add needed imports to a
	 * compilation unit.
	 */
//	public static class ImportsManager {
//
//		private ImportRewrite fImportsRewrite;
//
//		/* package */ ImportsManager(ICompilationUnit astRoot) {
//			fImportsRewrite= StubUtility.createImportRewrite(astRoot, true);
//		}
//
//		/* package */ ICompilationUnit getCompilationUnit() {
//			return fImportsRewrite.getCompilationUnit();
//		}
//
//		/**
//		 * Adds a new import declaration that is sorted in the existing imports.
//		 * If an import already exists or the import would conflict with an import
//		 * of an other type with the same simple name, the import is not added.
//		 *
//		 * @param qualifiedTypeName The fully qualified name of the type to import
//		 * (dot separated).
//		 * @return Returns the simple type name that can be used in the code or the
//		 * fully qualified type name if an import conflict prevented the import.
//		 */
//		public String addImport(String qualifiedTypeName) {
//			return fImportsRewrite.addImport(qualifiedTypeName);
//		}
//
//		/**
//		 * Adds a new import declaration that is sorted in the existing imports.
//		 * If an import already exists or the import would conflict with an import
//		 * of an other type with the same simple name, the import is not added.
//		 *
//		 * @param typeBinding the binding of the type to import
//		 *
//		 * @return Returns the simple type name that can be used in the code or the
//		 * fully qualified type name if an import conflict prevented the import.
//		 */
//		public String addImport(ITypeInfoBinding typeBinding) {
//			return fImportsRewrite.addImport(typeBinding);
//		}
//
//		/**
//		 * Adds a new import declaration for a static type that is sorted in the existing imports.
//		 * If an import already exists or the import would conflict with an import
//		 * of an other static import with the same simple name, the import is not added.
//		 *
//		 * @param declaringTypeName The qualified name of the static's member declaring type
//		 * @param simpleName the simple name of the member; either a field or a method name.
//		 * @param isField <code>true</code> specifies that the member is a field, <code>false</code> if it is a
//		 * method.
//		 * @return returns either the simple member name if the import was successful or else the qualified name if
//		 * an import conflict prevented the import.
//		 *
//		 * @since 3.2
//		 */
//		public String addStaticImport(String declaringTypeName, String simpleName, boolean isField) {
//			return fImportsRewrite.addStaticImport(declaringTypeName, simpleName, isField);
//		}
//
//		/* package */ void create(boolean needsSave, IProgressMonitor monitor) throws CoreException {
//			TextEdit edit= fImportsRewrite.rewriteImports(monitor);
//			JavaModelUtil.applyEdit(fImportsRewrite.getCompilationUnit(), edit, needsSave, null);
//		}
//
//		/* package */ void removeImport(String qualifiedName) {
//			fImportsRewrite.removeImport(qualifiedName);
//		}
//
//		/* package */ void removeStaticImport(String qualifiedName) {
//			fImportsRewrite.removeStaticImport(qualifiedName);
//		}
//	}


	/** Public access flag. See The Java Virtual Machine Specification for more details. */
	public int F_PUBLIC = X10.PUBLIC.getCode();
	/** Private access flag. See The Java Virtual Machine Specification for more details. */
	public int F_PRIVATE = X10.PRIVATE.getCode();
	/**  Protected access flag. See The Java Virtual Machine Specification for more details. */
	public int F_PROTECTED = X10.PROTECTED.getCode();
	/** Static access flag. See The Java Virtual Machine Specification for more details. */
	public int F_STATIC = X10.STATIC.getCode();
	/** Final access flag. See The Java Virtual Machine Specification for more details. */
	public int F_FINAL = X10.FINAL.getCode();
	/** Abstract property flag. See The Java Virtual Machine Specification for more details. */
	public int F_ABSTRACT = X10.ABSTRACT.getCode();

	private final static String PAGE_NAME= "NewTypeWizardPage"; //$NON-NLS-1$

	/** Field ID of the package input field. */
	protected final static String PACKAGE= PAGE_NAME + ".package";	 //$NON-NLS-1$
	/** Field ID of the enclosing type input field. */
	protected final static String ENCLOSING= PAGE_NAME + ".enclosing"; //$NON-NLS-1$
	/** Field ID of the enclosing type checkbox. */
	protected final static String ENCLOSINGSELECTION= ENCLOSING + ".selection"; //$NON-NLS-1$
	/** Field ID of the type name input field. */
	protected final static String TYPENAME= PAGE_NAME + ".typename"; //$NON-NLS-1$
	/** Field ID of the super type input field. */
	protected final static String SUPER= PAGE_NAME + ".superclass"; //$NON-NLS-1$
	/** Field ID of the super interfaces input field. */
	protected final static String INTERFACES= PAGE_NAME + ".interfaces"; //$NON-NLS-1$
	/** Field ID of the modifier check boxes. */
	protected final static String MODIFIERS= PAGE_NAME + ".modifiers"; //$NON-NLS-1$
	/** Field ID of the method stubs check boxes. */
	protected final static String METHODS= PAGE_NAME + ".methods"; //$NON-NLS-1$

	private static class InterfaceWrapper {
		public String interfaceName;

		public InterfaceWrapper(String interfaceName) {
			this.interfaceName= interfaceName;
		}

		public int hashCode() {
			return interfaceName.hashCode();
		}

		public boolean equals(Object obj) {
			return obj != null && getClass().equals(obj.getClass()) && ((InterfaceWrapper) obj).interfaceName.equals(interfaceName);
		}
	}

	private static class InterfacesListLabelProvider extends LabelProvider {
		private Image fInterfaceImage;

		public InterfacesListLabelProvider() {
			fInterfaceImage= PluginImages.get(X10PluginImages.IMG_OBJS_INTERFACE);
		}

		public String getText(Object element) {
			return ((InterfaceWrapper) element).interfaceName;
		}

		public Image getImage(Object element) {
			return fInterfaceImage;
		}
	}

	private StringButtonStatusDialogField fPackageDialogField;

	private SelectionButtonDialogField fEnclosingTypeSelection;
	private StringButtonDialogField fEnclosingTypeDialogField;

	private boolean fCanModifyPackage;
	private boolean fCanModifyEnclosingType;

	private ISourceFolder fCurrPackage;

	private ITypeInfo fCurrEnclosingType;
	/**
	 * a handle to the type to be created (does usually not exist, can be null)
	 */
	private ITypeInfo fCurrType;
	private StringDialogField fTypeNameDialogField;

	private StringButtonDialogField fSuperClassDialogField;
	private ListDialogField fSuperInterfacesDialogField;

	private SelectionButtonDialogFieldGroup fAccMdfButtons;
	private SelectionButtonDialogFieldGroup fOtherMdfButtons;

	private SelectionButtonDialogField fAddCommentButton;
	private boolean fUseAddCommentButtonValue; // used for compatibility: Wizards that don't show the comment button control
	// will use the preferences settings

	private ITypeInfo fCreatedType;

//	private JavaPackageCompletionProcessor fCurrPackageCompletionProcessor;
//	private JavaTypeCompletionProcessor fEnclosingTypeCompletionProcessor;
//	private StubTypeContext fSuperClassStubTypeContext;
//	private StubTypeContext fSuperInterfaceStubTypeContext;

	protected IStatus fEnclosingTypeStatus;
	protected IStatus fPackageStatus;
	protected IStatus fTypeNameStatus;
	protected IStatus fSuperClassStatus;
	protected IStatus fModifierStatus;
	protected IStatus fSuperInterfacesStatus;

	private final int PUBLIC_INDEX= 0, DEFAULT_INDEX= 1, PRIVATE_INDEX= 2, PROTECTED_INDEX= 3;
	private final int ABSTRACT_INDEX= 0, FINAL_INDEX= 1, STATIC_INDEX= 2, ENUM_ANNOT_STATIC_INDEX= 1;

	private int fTypeKind;

	/**
	 * Constant to signal that the created type is a class.
	 * @since 3.1
	 */
	public static final int CLASS_TYPE = 1;

	/**
	 * Constant to signal that the created type is a interface.
	 * @since 3.1
	 */
	public static final int INTERFACE_TYPE = 2;

	/**
	 * Constant to signal that the created type is an enum.
	 * @since 3.1
	 */
	public static final int ENUM_TYPE = 3;

	/**
	 * Constant to signal that the created type is an annotation.
	 * @since 3.1
	 */
	public static final int ANNOTATION_TYPE = 4;

	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 *
	 * @param isClass <code>true</code> if a new class is to be created; otherwise
	 * an interface is to be created
	 * @param pageName the wizard page's name
	 */
	public NewTypeWizardPage(Language language, boolean isClass, String pageName) {
		this(language, isClass ? CLASS_TYPE : INTERFACE_TYPE, pageName);
	}

	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 *
	 * @param typeKind Signals the kind of the type to be created. Valid kinds are
	 * {@link #CLASS_TYPE}, {@link #INTERFACE_TYPE}, {@link #ENUM_TYPE} and {@link #ANNOTATION_TYPE}
	 * @param pageName the wizard page's name
	 * @since 3.1
	 */
	public NewTypeWizardPage(Language language, int typeKind, String pageName) {
	    super(language, pageName);
	    fTypeKind= typeKind;

	    fCreatedType= null;

		TypeFieldsAdapter adapter= new TypeFieldsAdapter();

		fPackageDialogField= new StringButtonStatusDialogField(adapter);
		fPackageDialogField.setDialogFieldListener(adapter);
		fPackageDialogField.setLabelText(getPackageLabel());
		fPackageDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button);
		fPackageDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);

		fEnclosingTypeSelection= new SelectionButtonDialogField(SWT.CHECK);
		fEnclosingTypeSelection.setDialogFieldListener(adapter);
		fEnclosingTypeSelection.setLabelText(getEnclosingTypeLabel());

		fEnclosingTypeDialogField= new StringButtonDialogField(adapter);
		fEnclosingTypeDialogField.setDialogFieldListener(adapter);
		fEnclosingTypeDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_enclosing_button);

		fTypeNameDialogField= new StringDialogField();
		fTypeNameDialogField.setDialogFieldListener(adapter);
		fTypeNameDialogField.setLabelText(getTypeNameLabel());

		fSuperClassDialogField= new StringButtonDialogField(adapter);
		fSuperClassDialogField.setDialogFieldListener(adapter);
		fSuperClassDialogField.setLabelText(getSuperClassLabel());
		fSuperClassDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button);

		String[] addButtons= new String[] {
			NewWizardMessages.NewTypeWizardPage_interfaces_add,
			/* 1 */ null,
			NewWizardMessages.NewTypeWizardPage_interfaces_remove
		};
		fSuperInterfacesDialogField= new ListDialogField(adapter, addButtons, new InterfacesListLabelProvider());
		fSuperInterfacesDialogField.setDialogFieldListener(adapter);
		fSuperInterfacesDialogField.setTableColumns(new ListDialogField.ColumnsDescription(1, false));
		fSuperInterfacesDialogField.setLabelText(getSuperInterfacesLabel());
		fSuperInterfacesDialogField.setRemoveButtonIndex(2);

		String[] buttonNames1= new String[] {
			NewWizardMessages.NewTypeWizardPage_modifiers_public,
			NewWizardMessages.NewTypeWizardPage_modifiers_default,
			NewWizardMessages.NewTypeWizardPage_modifiers_private,
			NewWizardMessages.NewTypeWizardPage_modifiers_protected
		};
		fAccMdfButtons= new SelectionButtonDialogFieldGroup(SWT.RADIO, buttonNames1, 4);
		fAccMdfButtons.setDialogFieldListener(adapter);
		fAccMdfButtons.setLabelText(getModifiersLabel());
		fAccMdfButtons.setSelection(0, true);

		String[] buttonNames2;
		if (fTypeKind == CLASS_TYPE) {
			buttonNames2= new String[] {
				NewWizardMessages.NewTypeWizardPage_modifiers_abstract,
				NewWizardMessages.NewTypeWizardPage_modifiers_final,
				NewWizardMessages.NewTypeWizardPage_modifiers_static
			};
		} else {
		    if (fTypeKind == ENUM_TYPE || fTypeKind == ANNOTATION_TYPE) {
		        buttonNames2= new String[] {
					NewWizardMessages.NewTypeWizardPage_modifiers_abstract,
					NewWizardMessages.NewTypeWizardPage_modifiers_static
		        };
		    }
		    else
		        buttonNames2= new String[] {};
		}

		fOtherMdfButtons= new SelectionButtonDialogFieldGroup(SWT.CHECK, buttonNames2, 4);
		fOtherMdfButtons.setDialogFieldListener(adapter);

		fAccMdfButtons.enableSelectionButton(PRIVATE_INDEX, false);
		fAccMdfButtons.enableSelectionButton(PROTECTED_INDEX, false);
		fOtherMdfButtons.enableSelectionButton(STATIC_INDEX, false);

		if (fTypeKind == ENUM_TYPE || fTypeKind == ANNOTATION_TYPE) {
		    fOtherMdfButtons.enableSelectionButton(ABSTRACT_INDEX, false);
		    fOtherMdfButtons.enableSelectionButton(ENUM_ANNOT_STATIC_INDEX, false);
		}

		fAddCommentButton= new SelectionButtonDialogField(SWT.CHECK);
		fAddCommentButton.setLabelText(NewWizardMessages.NewTypeWizardPage_addcomment_label);

		fUseAddCommentButtonValue= false; // only used when enabled

//		fCurrPackageCompletionProcessor= new JavaPackageCompletionProcessor();
//		fEnclosingTypeCompletionProcessor= new JavaTypeCompletionProcessor(false, false, true);

		fPackageStatus= new StatusInfo();
		fEnclosingTypeStatus= new StatusInfo();

		fCanModifyPackage= true;
		fCanModifyEnclosingType= true;
		updateEnableState();

		fTypeNameStatus= new StatusInfo();
		fSuperClassStatus= new StatusInfo();
		fSuperInterfacesStatus= new StatusInfo();
		fModifierStatus= new StatusInfo();
	}

	/**
	 * Initializes all fields provided by the page with a given selection.
	 *
	 * @param elem the selection used to initialize this page or <code>
	 * null</code> if no selection was available
	 */
	protected void initTypePage(ISourceEntity elem) {
		String initSuperclass= "x10.lang.Object"; //$NON-NLS-1$
		ArrayList initSuperinterfaces= new ArrayList(5);

		ISourceProject project= null;
		ISourceFolder pack= null;
		ITypeInfo enclosingType= null;

		if (elem != null) {
			// evaluate the enclosing type
			project= elem.getProject();
			pack= (ISourceFolder) elem.getAncestor(ISourceFolder.class);
			if(pack != null)
			{
				ISourceRoot root = (ISourceRoot)pack.getAncestor(ISourceRoot.class);
				if(root != null)
				{
					pack = root.getSourceFolder(BuildPathUtils.getPackageName(root, pack.getResource()));
				}
			}
			
			ICompilationUnit cu= (ICompilationUnit) elem.getAncestor(ICompilationUnit.class);
			if (cu != null) {
				final ArrayList<ClassType> x10Types = new ArrayList<ClassType>();
		          try {
		            X10Utils.collectX10MainTypes(x10Types, cu, new NullProgressMonitor());
		            if(x10Types.size() > 0)
		            {
		            	enclosingType = SearchUtils.getType(cu.getProject().getRawProject(), x10Types.get(0).fullName().toString());
		            }
		          } catch (CoreException e) {
		            LoggingUtils.log(e);
		          } catch (InterruptedException e) {
		        	  LoggingUtils.log(e);
		          }
			}
			

			try {
				ITypeInfo type= null;
				if (elem instanceof ITypeInfo) {
					type= (ITypeInfo)elem;
					if (type.exists(null)) {
						String superName= SuperInterfaceSelectionDialog.getNameWithTypeParameters(type);
						if (ModelUtil.isInterface(type)) {
							initSuperinterfaces.add(superName);
						} else {
							initSuperclass= superName;
						}
					}
				}
			} catch (ModelException e) {
				LoggingUtils.log(e);
				// ignore this exception now
			}
		}

		String typeName= ""; //$NON-NLS-1$

		ITextSelection selection= getCurrentTextSelection();
		if (selection != null) {
			String text= selection.getText();
			if (text != null && validateJavaTypeName(text, project).isOK()) {
				typeName= text;
			}
		}

		setPackageFragment(pack, true);
		setEnclosingType(enclosingType, true);
		setEnclosingTypeSelection(false, true);

		setTypeName(typeName, true);
		setSuperClass(initSuperclass, true);
		setSuperInterfaces(initSuperinterfaces, true);

//		setAddComments(StubUtility.doAddComments(project), true); // from project or workspace
		setAddComments(true, true); // from project or workspace
	}




	private static IStatus validateJavaTypeName(String text, ISourceProject project) {
//		if (project == null || !project.exists()) {
//			return JavaConventions.validateJavaTypeName(text, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
//		}
//		return JavaConventionsUtil.validateJavaTypeName(text, project);
		return Status.OK_STATUS;
	}

	private static IStatus validatePackageName(String text, ISourceProject project) {
//		if (project == null || !project.exists()) {
//			return JavaConventions.validatePackageName(text, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
//		}
//		return JavaConventionsUtil.validatePackageName(text, project);
		return Status.OK_STATUS;
	}

	// -------- UI Creation ---------

	/**
	 * Returns the label that is used for the package input field.
	 *
	 * @return the label that is used for the package input field.
	 * @since 3.2
	 */
	protected String getPackageLabel() {
		return NewWizardMessages.NewTypeWizardPage_package_label;
	}

	/**
	 * Returns the label that is used for the enclosing type input field.
	 *
	 * @return the label that is used for the enclosing type input field.
	 * @since 3.2
	 */
	protected String getEnclosingTypeLabel() {
		return NewWizardMessages.NewTypeWizardPage_enclosing_selection_label;
	}

	/**
	 * Returns the label that is used for the type name input field.
	 *
	 * @return the label that is used for the type name input field.
	 * @since 3.2
	 */
	protected String getTypeNameLabel() {
		return NewWizardMessages.NewTypeWizardPage_typename_label;
	}

	/**
	 * Returns the label that is used for the modifiers input field.
	 *
	 * @return the label that is used for the modifiers input field
	 * @since 3.2
	 */
	protected String getModifiersLabel() {
		return NewWizardMessages.NewTypeWizardPage_modifiers_acc_label;
	}

	/**
	 * Returns the label that is used for the super class input field.
	 *
	 * @return the label that is used for the super class input field.
	 * @since 3.2
	 */
	protected String getSuperClassLabel() {
		return NewWizardMessages.NewTypeWizardPage_superclass_label;
	}

	/**
	 * Returns the label that is used for the super interfaces input field.
	 *
	 * @return the label that is used for the super interfaces input field.
	 * @since 3.2
	 */
	protected String getSuperInterfacesLabel() {
	    if (fTypeKind != INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_interfaces_class_label;
	    return NewWizardMessages.NewTypeWizardPage_interfaces_ifc_label;
	}

	/**
	 * Creates a separator line. Expects a <code>GridLayout</code> with at least 1 column.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSeparator(Composite composite, int nColumns) {
		(new Separator(SWT.SEPARATOR | SWT.HORIZONTAL)).doFillIntoGrid(composite, nColumns, convertHeightInCharsToPixels(1));
	}

	/**
	 * Creates the controls for the package name field. Expects a <code>GridLayout</code> with at
	 * least 4 columns.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createPackageControls(Composite composite, int nColumns) {
		fPackageDialogField.doFillIntoGrid(composite, nColumns);
		Text text= fPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(text);
//		ControlContentAssistHelper.createTextContentAssistant(text, fCurrPackageCompletionProcessor);
		TextFieldNavigationHandler.install(text);
	}

	/**
	 * Creates the controls for the enclosing type name field. Expects a <code>GridLayout</code> with at
	 * least 4 columns.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createEnclosingTypeControls(Composite composite, int nColumns) {
		// #6891
		Composite tabGroup= new Composite(composite, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
 		tabGroup.setLayout(layout);

		fEnclosingTypeSelection.doFillIntoGrid(tabGroup, 1);

		Text text= fEnclosingTypeDialogField.getTextControl(composite);
		SWTUtil.setAccessibilityText(text, NewWizardMessages.NewTypeWizardPage_enclosing_field_description);

		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint= getMaxFieldWidth();
		gd.horizontalSpan= 2;
		text.setLayoutData(gd);

		Button button= fEnclosingTypeDialogField.getChangeControl(composite);
		gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = SWTUtil.getButtonWidthHint(button);
		button.setLayoutData(gd);
//		ControlContentAssistHelper.createTextContentAssistant(text, fEnclosingTypeCompletionProcessor);
		TextFieldNavigationHandler.install(text);
	}

	/**
	 * Creates the controls for the type name field. Expects a <code>GridLayout</code> with at
	 * least 2 columns.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createTypeNameControls(Composite composite, int nColumns) {
		fTypeNameDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);

		Text text= fTypeNameDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		TextFieldNavigationHandler.install(text);
	}

	/**
	 * Creates the controls for the modifiers radio/checkbox buttons. Expects a
	 * <code>GridLayout</code> with at least 3 columns.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createModifierControls(Composite composite, int nColumns) {
		LayoutUtil.setHorizontalSpan(fAccMdfButtons.getLabelControl(composite), 1);

		Control control= fAccMdfButtons.getSelectionButtonsGroup(composite);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= nColumns - 2;
		control.setLayoutData(gd);

		DialogField.createEmptySpace(composite);

		if (fTypeKind == CLASS_TYPE) {
			DialogField.createEmptySpace(composite);

			control= fOtherMdfButtons.getSelectionButtonsGroup(composite);
			gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan= nColumns - 2;
			control.setLayoutData(gd);

			DialogField.createEmptySpace(composite);
		}
	}

	/**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code>
	 * with at least 3 columns.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSuperClassControls(Composite composite, int nColumns) {
		fSuperClassDialogField.doFillIntoGrid(composite, nColumns);
		Text text= fSuperClassDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());

//		JavaTypeCompletionProcessor superClassCompletionProcessor= new JavaTypeCompletionProcessor(false, false, true);
//		superClassCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
//			public StubTypeContext getStubTypeContext() {
//				return getSuperClassStubTypeContext();
//			}
//		});

//		ControlContentAssistHelper.createTextContentAssistant(text, superClassCompletionProcessor);
		TextFieldNavigationHandler.install(text);
	}

	/**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code> with
	 * at least 3 columns.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSuperInterfacesControls(Composite composite, int nColumns) {
		final String INTERFACE= "interface"; //$NON-NLS-1$
		fSuperInterfacesDialogField.doFillIntoGrid(composite, nColumns);
		final TableViewer tableViewer= fSuperInterfacesDialogField.getTableViewer();
		tableViewer.setColumnProperties(new String[] {INTERFACE});

		TableTextCellEditor cellEditor= new TableTextCellEditor(tableViewer, 0) {
		    protected void doSetFocus() {
		        if (text != null) {
		            text.setFocus();
		            text.setSelection(text.getText().length());
		            checkSelection();
		            checkDeleteable();
		            checkSelectable();
		        }
		    }
		};
//		JavaTypeCompletionProcessor superInterfaceCompletionProcessor= new JavaTypeCompletionProcessor(false, false, true);
//		superInterfaceCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
//			public StubTypeContext getStubTypeContext() {
//				return getSuperInterfacesStubTypeContext();
//			}
//		});
//		SubjectControlContentAssistant contentAssistant= ControlContentAssistHelper.createJavaContentAssistant(superInterfaceCompletionProcessor);
//		Text cellEditorText= cellEditor.getText();
//		ContentAssistHandler.createHandlerForText(cellEditorText, contentAssistant);
//		TextFieldNavigationHandler.install(cellEditorText);
//		cellEditor.setContentAssistant(contentAssistant);

		tableViewer.setCellEditors(new CellEditor[] { cellEditor });
		tableViewer.setCellModifier(new ICellModifier() {
			public void modify(Object element, String property, Object value) {
				if (element instanceof Item)
					element = ((Item) element).getData();

				((InterfaceWrapper) element).interfaceName= (String) value;
				fSuperInterfacesDialogField.elementChanged(element);
			}
			public Object getValue(Object element, String property) {
				return ((InterfaceWrapper) element).interfaceName;
			}
			public boolean canModify(Object element, String property) {
				return true;
			}
		});
		tableViewer.getTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.F2 && event.stateMask == 0) {
					ISelection selection= tableViewer.getSelection();
					if (! (selection instanceof IStructuredSelection))
						return;
					IStructuredSelection structuredSelection= (IStructuredSelection) selection;
					tableViewer.editElement(structuredSelection.getFirstElement(), 0);
				}
			}
		});
		GridData gd= (GridData) fSuperInterfacesDialogField.getListControl(null).getLayoutData();
		if (fTypeKind == CLASS_TYPE) {
			gd.heightHint= convertHeightInCharsToPixels(3);
		} else {
			gd.heightHint= convertHeightInCharsToPixels(6);
		}
		gd.grabExcessVerticalSpace= false;
		gd.widthHint= getMaxFieldWidth();
	}

	/**
	 * Creates the controls for the preference page links. Expects a <code>GridLayout</code> with
	 * at least 3 columns.
	 *
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 *
	 * @since 3.1
	 */
	protected void createCommentControls(Composite composite, int nColumns) {
    	Link link= new Link(composite, SWT.NONE);
    	link.setText(NewWizardMessages.NewTypeWizardPage_addcomment_description);
    	link.addSelectionListener(new TypeFieldsAdapter());
    	link.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, nColumns, 1));
		DialogField.createEmptySpace(composite);
		fAddCommentButton.doFillIntoGrid(composite, nColumns - 1);
	}



	/**
	 * Sets the focus on the type name input field.
	 */
	protected void setFocus() {
		if (fTypeNameDialogField.isEnabled()) {
			fTypeNameDialogField.setFocus();
		} else {
			setFocusOnContainer();
		}
	}

	// -------- TypeFieldsAdapter --------

	private class TypeFieldsAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter, SelectionListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typePageChangeControlPressed(field);
		}

		// -------- IListAdapter
		public void customButtonPressed(ListDialogField field, int index) {
			typePageCustomButtonPressed(field, index);
		}

		public void selectionChanged(ListDialogField field) {}

		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			typePageDialogFieldChanged(field);
		}

		public void doubleClicked(ListDialogField field) {
		}


		public void widgetSelected(SelectionEvent e) {
			typePageLinkActivated();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			typePageLinkActivated();
		}
	}

	private void typePageLinkActivated() {
		ISourceProject project= getJavaProject();
//		if (project != null) {
//			PreferenceDialog dialog= PreferencesUtil.createPropertyDialogOn(getShell(), project.getProject(), CodeTemplatePreferencePage.PROP_ID, null, null);
//			dialog.open();
//		} else {
			String title= NewWizardMessages.NewTypeWizardPage_configure_templates_title;
			String message= NewWizardMessages.NewTypeWizardPage_configure_templates_message;
			MessageDialog.openInformation(getShell(), title, message);
//		}
	}

	private void typePageChangeControlPressed(DialogField field) {
		if (field == fPackageDialogField) {
			ISourceFolder pack= choosePackage();
			if (pack != null) {
				fPackageDialogField.setText(pack.getName());
			}
		} else if (field == fEnclosingTypeDialogField) {
			ITypeInfo type= chooseEnclosingType();
			if (type != null) {
				fEnclosingTypeDialogField.setText(SearchUtils.getFullyQualifiedName(type, '.'));
			}
		} else if (field == fSuperClassDialogField) {
			ITypeInfo type= chooseSuperClass();
			if (type != null) {
				fSuperClassDialogField.setText(SuperInterfaceSelectionDialog.getNameWithTypeParameters(type));
			}
		}
	}

	private void typePageCustomButtonPressed(DialogField field, int index) {
		if (field == fSuperInterfacesDialogField && index == 0) {
			chooseSuperInterfaces();
			List interfaces= fSuperInterfacesDialogField.getElements();
			if (!interfaces.isEmpty()) {
				Object element= interfaces.get(interfaces.size() - 1);
				fSuperInterfacesDialogField.editElement(element);
			}
		}
	}

	/*
	 * A field on the type has changed. The fields' status and all dependent
	 * status are updated.
	 */
	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName= null;
		if (field == fPackageDialogField) {
			fPackageStatus= packageChanged();
			updatePackageStatusLabel();
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();
			fieldName= PACKAGE;
		} else if (field == fEnclosingTypeDialogField) {
			fEnclosingTypeStatus= enclosingTypeChanged();
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();
			fieldName= ENCLOSING;
		} else if (field == fEnclosingTypeSelection) {
			updateEnableState();
			boolean isEnclosedType= isEnclosingTypeSelected();
			if (!isEnclosedType) {
				if (fAccMdfButtons.isSelected(PRIVATE_INDEX) || fAccMdfButtons.isSelected(PROTECTED_INDEX)) {
					fAccMdfButtons.setSelection(PRIVATE_INDEX, false);
					fAccMdfButtons.setSelection(PROTECTED_INDEX, false);
					fAccMdfButtons.setSelection(PUBLIC_INDEX, true);
				}
				if (fOtherMdfButtons.isSelected(STATIC_INDEX)) {
					fOtherMdfButtons.setSelection(STATIC_INDEX, false);
				}
			}
			fAccMdfButtons.enableSelectionButton(PRIVATE_INDEX, isEnclosedType);
			fAccMdfButtons.enableSelectionButton(PROTECTED_INDEX, isEnclosedType);
			fOtherMdfButtons.enableSelectionButton(STATIC_INDEX, isEnclosedType);
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();
			fieldName= ENCLOSINGSELECTION;
		} else if (field == fTypeNameDialogField) {
			fTypeNameStatus= typeNameChanged();
			fieldName= TYPENAME;
		} else if (field == fSuperClassDialogField) {
			fSuperClassStatus= superClassChanged();
			fieldName= SUPER;
		} else if (field == fSuperInterfacesDialogField) {
			fSuperInterfacesStatus= superInterfacesChanged();
			fieldName= INTERFACES;
		} else if (field == fOtherMdfButtons || field == fAccMdfButtons) {
			fModifierStatus= modifiersChanged();
			fieldName= MODIFIERS;
		} else {
			fieldName= METHODS;
		}
		// tell all others
		handleFieldChanged(fieldName);
	}

	// -------- update message ----------------

	/*
	 * @see org.eclipse.jdt.ui.wizards.NewContainerWizardPage#handleFieldChanged(String)
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (fieldName == CONTAINER) {
			fPackageStatus= packageChanged();
			fEnclosingTypeStatus= enclosingTypeChanged();
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();
			fSuperInterfacesStatus= superInterfacesChanged();
		}
	}

	// ---- set / get ----------------

	/**
	 * Returns the text of the package input field.
	 *
	 * @return the text of the package input field
	 */
	public String getPackageText() {
		return fPackageDialogField.getText();
	}

	/**
	 * Returns the text of the enclosing type input field.
	 *
	 * @return the text of the enclosing type input field
	 */
	public String getEnclosingTypeText() {
		return fEnclosingTypeDialogField.getText();
	}


	/**
	 * Returns the package fragment corresponding to the current input.
	 *
	 * @return a package fragment or <code>null</code> if the input
	 * could not be resolved.
	 */
	public ISourceFolder getPackageFragment() {
		if (!isEnclosingTypeSelected()) {
			return fCurrPackage;
		} else {
			if (fCurrEnclosingType != null) {
				return SearchUtils.getSourceFolder(fCurrEnclosingType);
			}
		}
		return null;
	}

	/**
	 * Sets the package fragment to the given value. The method updates the model
	 * and the text of the control.
	 *
	 * @param pack the package fragment to be set
	 * @param canBeModified if <code>true</code> the package fragment is
	 * editable; otherwise it is read-only.
	 */
	public void setPackageFragment(ISourceFolder pack, boolean canBeModified) {
		fCurrPackage= pack;
		fCanModifyPackage= canBeModified;
		
		String str= (pack == null) ? "" : pack.getName(); //$NON-NLS-1$
		fPackageDialogField.setText(str);
		updateEnableState();
	}

	/**
	 * Returns the enclosing type corresponding to the current input.
	 *
	 * @return the enclosing type or <code>null</code> if the enclosing type is
	 * not selected or the input could not be resolved
	 */
	public ITypeInfo getEnclosingType() {
		if (isEnclosingTypeSelected()) {
			return fCurrEnclosingType;
		}
		return null;
	}

	/**
	 * Sets the enclosing type. The method updates the underlying model
	 * and the text of the control.
	 *
	 * @param type the enclosing type
	 * @param canBeModified if <code>true</code> the enclosing type field is
	 * editable; otherwise it is read-only.
	 */
	public void setEnclosingType(ITypeInfo type, boolean canBeModified) {
		fCurrEnclosingType= type;
		fCanModifyEnclosingType= canBeModified;
		String str= (type == null) ? "" : SearchUtils.getFullyQualifiedName(type, '.'); //$NON-NLS-1$
		fEnclosingTypeDialogField.setText(str);
		updateEnableState();
	}

	/**
	 * Returns the selection state of the enclosing type checkbox.
	 *
	 * @return the selection state of the enclosing type checkbox
	 */
	public boolean isEnclosingTypeSelected() {
		return fEnclosingTypeSelection.isSelected();
	}

	/**
	 * Sets the enclosing type checkbox's selection state.
	 *
	 * @param isSelected the checkbox's selection state
	 * @param canBeModified if <code>true</code> the enclosing type checkbox is
	 * modifiable; otherwise it is read-only.
	 */
	public void setEnclosingTypeSelection(boolean isSelected, boolean canBeModified) {
		fEnclosingTypeSelection.setSelection(isSelected);
		fEnclosingTypeSelection.setEnabled(canBeModified);
		updateEnableState();
	}

	/**
	 * Returns the type name entered into the type input field.
	 *
	 * @return the type name
	 */
	public String getTypeName() {
		return fTypeNameDialogField.getText();
	}

	/**
	 * Sets the type name input field's text to the given value. Method doesn't update
	 * the model.
	 *
	 * @param name the new type name
	 * @param canBeModified if <code>true</code> the type name field is
	 * editable; otherwise it is read-only.
	 */
	public void setTypeName(String name, boolean canBeModified) {
		fTypeNameDialogField.setText(name);
		fTypeNameDialogField.setEnabled(canBeModified);
	}

	/**
	 * Returns the selected modifiers.
	 *
	 * @return the selected modifiers
	 * @see Flags
	 */
	public int getModifiers() {
		int mdf= 0;
		if (fAccMdfButtons.isSelected(PUBLIC_INDEX)) {
			mdf+= F_PUBLIC;
		} else if (fAccMdfButtons.isSelected(PRIVATE_INDEX)) {
			mdf+= F_PRIVATE;
		} else if (fAccMdfButtons.isSelected(PROTECTED_INDEX)) {
			mdf+= F_PROTECTED;
		}
		if (fOtherMdfButtons.isSelected(ABSTRACT_INDEX)) {
			mdf+= F_ABSTRACT;
		}
		if (fOtherMdfButtons.isSelected(FINAL_INDEX)) {
			mdf+= F_FINAL;
		}
		if (fOtherMdfButtons.isSelected(STATIC_INDEX)) {
			mdf+= F_STATIC;
		}
		return mdf;
	}

	/**
	 * Sets the modifiers.
	 *
	 * @param modifiers <code>F_PUBLIC</code>, <code>F_PRIVATE</code>,
	 * <code>F_PROTECTED</code>, <code>F_ABSTRACT</code>, <code>F_FINAL</code>
	 * or <code>F_STATIC</code> or a valid combination.
	 * @param canBeModified if <code>true</code> the modifier fields are
	 * editable; otherwise they are read-only
	 * @see Flags
	 */
	public void setModifiers(int modifiers, boolean canBeModified) {
		if (Flags.isPublic(modifiers)) {
			fAccMdfButtons.setSelection(PUBLIC_INDEX, true);
		} else if (Flags.isPrivate(modifiers)) {
			fAccMdfButtons.setSelection(PRIVATE_INDEX, true);
		} else if (Flags.isProtected(modifiers)) {
			fAccMdfButtons.setSelection(PROTECTED_INDEX, true);
		} else {
			fAccMdfButtons.setSelection(DEFAULT_INDEX, true);
		}
		if (Flags.isAbstract(modifiers)) {
			fOtherMdfButtons.setSelection(ABSTRACT_INDEX, true);
		}
		if (Flags.isFinal(modifiers)) {
			fOtherMdfButtons.setSelection(FINAL_INDEX, true);
		}
		if (Flags.isStatic(modifiers)) {
			fOtherMdfButtons.setSelection(STATIC_INDEX, true);
		}

		fAccMdfButtons.setEnabled(canBeModified);
		fOtherMdfButtons.setEnabled(canBeModified);
	}

	/**
	 * Returns the content of the superclass input field.
	 *
	 * @return the superclass name
	 */
	public String getSuperClass() {
		return fSuperClassDialogField.getText();
	}

	/**
	 * Sets the super class name.
	 *
	 * @param name the new superclass name
	 * @param canBeModified  if <code>true</code> the superclass name field is
	 * editable; otherwise it is read-only.
	 */
	public void setSuperClass(String name, boolean canBeModified) {
		fSuperClassDialogField.setText(name);
		fSuperClassDialogField.setEnabled(canBeModified);
	}

	/**
	 * Returns the chosen super interfaces.
	 *
	 * @return a list of chosen super interfaces. The list's elements
	 * are of type <code>String</code>
	 */
	public List getSuperInterfaces() {
		List interfaces= fSuperInterfacesDialogField.getElements();
		ArrayList result= new ArrayList(interfaces.size());
		for (Iterator iter= interfaces.iterator(); iter.hasNext();) {
			InterfaceWrapper wrapper= (InterfaceWrapper) iter.next();
			result.add(wrapper.interfaceName);
		}
		return result;
	}

	/**
	 * Sets the super interfaces.
	 *
	 * @param interfacesNames a list of super interface. The method requires that
	 * the list's elements are of type <code>String</code>
	 * @param canBeModified if <code>true</code> the super interface field is
	 * editable; otherwise it is read-only.
	 */
	public void setSuperInterfaces(List interfacesNames, boolean canBeModified) {
		ArrayList interfaces= new ArrayList(interfacesNames.size());
		for (Iterator iter= interfacesNames.iterator(); iter.hasNext();) {
			interfaces.add(new InterfaceWrapper((String) iter.next()));
		}
		fSuperInterfacesDialogField.setElements(interfaces);
		fSuperInterfacesDialogField.setEnabled(canBeModified);
	}

	/**
	 * Adds a super interface to the end of the list and selects it if it is not in the list yet.
	 *
	 * @param superInterface the fully qualified type name of the interface.
	 * @return returns <code>true</code>if the interfaces has been added, <code>false</code>
	 * if the interface already is in the list.
	 * @since 3.2
	 */
	public boolean addSuperInterface(String superInterface) {
		return fSuperInterfacesDialogField.addElement(new InterfaceWrapper(superInterface));
	}


	/**
	 * Sets 'Add comment' checkbox. The value set will only be used when creating source when
	 * the comment control is enabled (see {@link #enableCommentControl(boolean)}
	 *
	 * @param doAddComments if <code>true</code>, comments are added.
	 * @param canBeModified if <code>true</code> check box is
	 * editable; otherwise it is read-only.
	 * 	@since 3.1
	 */
	public void setAddComments(boolean doAddComments, boolean canBeModified) {
		fAddCommentButton.setSelection(doAddComments);
		fAddCommentButton.setEnabled(canBeModified);
	}

	/**
	 * Sets to use the 'Add comment' checkbox value. Clients that use the 'Add comment' checkbox
	 * additionally have to enable the control. This has been added for backwards compatibility.
	 *
	 * @param useAddCommentValue if <code>true</code>,
	 * 	@since 3.1
	 */
	public void enableCommentControl(boolean useAddCommentValue) {
		fUseAddCommentButtonValue= useAddCommentValue;
	}


	/**
	 * Returns if comments are added. This method can be overridden by clients.
	 * The selection of the comment control is taken if enabled (see {@link #enableCommentControl(boolean)}, otherwise
	 * the settings as specified in the preferences is used.
	 *
	 * @return Returns <code>true</code> if comments can be added
	 * @since 3.1
	 */
	public boolean isAddComments() {
		if (fUseAddCommentButtonValue) {
			return fAddCommentButton.isSelected();
		}
//		return StubUtility.doAddComments(getJavaProject());
		return true;
	}

	/**
	 * Returns the resource handle that corresponds to the compilation unit to was or
	 * will be created or modified.
	 * @return A resource or null if the page contains illegal values.
	 * @since 3.0
	 */
	public IResource getModifiedResource() {
		ITypeInfo enclosing= getEnclosingType();
		if (enclosing != null) {
			return SearchUtils.getResource(enclosing);
		}
		ISourceFolder pack= getPackageFragment();
		if (pack != null) {
			return pack.getResource();
		}
		return null;
	}

	// ----------- validation ----------

	/*
	 * @see org.eclipse.jdt.ui.wizards.NewContainerWizardPage#containerChanged()
	 */
	protected IStatus containerChanged() {
		IStatus status= super.containerChanged();
	    ISourceRoot root= getPackageFragmentRoot();
		if ((fTypeKind == ANNOTATION_TYPE || fTypeKind == ENUM_TYPE) && !status.matches(IStatus.ERROR)) {
	    	if (fTypeKind == ENUM_TYPE) {
		    	try {
		    	    // if findType(...) == null then Enum is unavailable
		    	    if (findType(root.getProject(), "java.lang.Enum") == null) //$NON-NLS-1$
		    	        return new StatusInfo(IStatus.WARNING, NewWizardMessages.NewTypeWizardPage_warning_EnumClassNotFound);
		    	} catch (ModelException e) {
		    	    LoggingUtils.log(e);
		    	}
	    	}
	    }

//		fCurrPackageCompletionProcessor.setPackageFragmentRoot(root);
//		if (root != null) {
//			fEnclosingTypeCompletionProcessor.setPackageFragment(root.createSourceFolder("", false, null)); //$NON-NLS-1$
//		}
		return status;
	}

	/**
	 * A hook method that gets called when the package field has changed. The method
	 * validates the package name and returns the status of the validation. The validation
	 * also updates the package fragment model.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 *
	 * @return the status of the validation
	 */
	protected IStatus packageChanged() {
		StatusInfo status= new StatusInfo();
		ISourceRoot root= getPackageFragmentRoot();
		fPackageDialogField.enableButton(root != null);

		ISourceProject project= root != null ? root.getProject() : null;

		String packName= getPackageText();
		if (packName.length() > 0) {
			IStatus val= validatePackageName(packName, project);
			if (val.getSeverity() == IStatus.ERROR) {
				status.setError(MessageFormat.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName, val.getMessage()));
				return status;
			} else if (val.getSeverity() == IStatus.WARNING) {
				status.setWarning(MessageFormat.format(NewWizardMessages.NewTypeWizardPage_warning_DiscouragedPackageName, val.getMessage()));
				// continue
			}
		} else {
			status.setWarning(NewWizardMessages.NewTypeWizardPage_warning_DefaultPackageDiscouraged);
		}

		if (project != null) {
			if (project.getRawProject().exists() && packName.length() > 0) {
//				try {
					IPath rootPath= root.getPath();
					IPath outputPath= project.getOutputLocation(fLanguage);
					if (rootPath.isPrefixOf(outputPath) && !rootPath.equals(outputPath)) {
						// if the bin folder is inside of our root, don't allow to name a package
						// like the bin folder
						IPath packagePath= rootPath.append(packName.replace('.', '/'));
						if (outputPath.isPrefixOf(packagePath)) {
							status.setError(NewWizardMessages.NewTypeWizardPage_error_ClashOutputLocation);
							return status;
						}
					}
//				} catch (ModelException e) {
//					LoggingUtils.log(e);
//					// let pass
//				}
			}

			fCurrPackage= root.getSourceFolder(packName);
			if(fCurrPackage != null)
			{
				IResource resource = fCurrPackage.getResource();
				if (resource != null){
					if (resource.isVirtual()){
						status.setError(NewWizardMessages.NewTypeWizardPage_error_PackageIsVirtual);
						return status;
					}			
					if (!ResourcesPlugin.getWorkspace().validateFiltered(resource).isOK()) {
						status.setError(NewWizardMessages.NewTypeWizardPage_error_PackageNameFiltered);
						return status;
					}
				}
			}
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}

	/*
	 * Updates the 'default' label next to the package field.
	 */
	private void updatePackageStatusLabel() {
		String packName= getPackageText();

		if (packName.length() == 0) {
			fPackageDialogField.setStatus(NewWizardMessages.NewTypeWizardPage_default);
		} else {
			fPackageDialogField.setStatus(""); //$NON-NLS-1$
		}
	}

	/*
	 * Updates the enable state of buttons related to the enclosing type selection checkbox.
	 */
	private void updateEnableState() {
		boolean enclosing= isEnclosingTypeSelected();
		fPackageDialogField.setEnabled(fCanModifyPackage && !enclosing);
		fEnclosingTypeDialogField.setEnabled(fCanModifyEnclosingType && enclosing);
		if (fTypeKind == ENUM_TYPE || fTypeKind == ANNOTATION_TYPE) {
		    fOtherMdfButtons.enableSelectionButton(ABSTRACT_INDEX, enclosing);
		    fOtherMdfButtons.enableSelectionButton(ENUM_ANNOT_STATIC_INDEX, enclosing);
		}
	}

	/**
	 * Hook method that gets called when the enclosing type name has changed. The method
	 * validates the enclosing type and returns the status of the validation. It also updates the
	 * enclosing type model.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 *
	 * @return the status of the validation
	 */
	protected IStatus enclosingTypeChanged() {
		StatusInfo status= new StatusInfo();
		fCurrEnclosingType= null;

		ISourceRoot root= getPackageFragmentRoot();

		fEnclosingTypeDialogField.enableButton(root != null);
		if (root == null) {
			status.setError(""); //$NON-NLS-1$
			return status;
		}

		String enclName= getEnclosingTypeText();
		if (enclName.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingTypeEnterName);
			return status;
		}
		try {
			ITypeInfo type= findType(root.getProject(), enclName);
			if (type == null) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingTypeNotExists);
				return status;
			}

			if (type.getCompilationUnit() == null) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingNotInCU);
				return status;
			}
			if (type.getCompilationUnit().getResource().getResourceAttributes().isReadOnly()) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingNotEditable);
				return status;
			}

			fCurrEnclosingType= type;
			ISourceRoot enclosingRoot= (ISourceRoot)type.getSourceEntity().getAncestor(ISourceRoot.class);
			if (!enclosingRoot.equals(root)) {
				status.setWarning(NewWizardMessages.NewTypeWizardPage_warning_EnclosingNotInSourceFolder);
			}
			return status;
		} catch (ModelException e) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingTypeNotExists);
			LoggingUtils.log(e);
			return status;
		}
	}

	private ITypeInfo findType(ISourceProject project, String typeName) throws ModelException {
		if (project.getResource().exists()) {
			return SearchUtils.getType(project.getRawProject(), typeName);
		}
		return null;
	}

	private String getTypeNameWithoutParameters() {
		String typeNameWithParameters= getTypeName();
		int angleBracketOffset= typeNameWithParameters.indexOf('<');
		if (angleBracketOffset == -1) {
			return typeNameWithParameters;
		} else {
			return typeNameWithParameters.substring(0, angleBracketOffset);
		}
	}

	/**
	 * Hook method that is called when evaluating the name of the compilation unit to create. By default, a file extension
	 * <code>java</code> is added to the given type name, but implementors can override this behavior.
	 *
	 * @param typeName the name of the type to create the compilation unit for.
	 * @return the name of the compilation unit to be created for the given name
	 *
	 * @since 3.2
	 */
	protected String getCompilationUnitName(String typeName) {
		return typeName + ".x10";
	}


	/**
	 * Hook method that gets called when the type name has changed. The method validates the
	 * type name and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 *
	 * @return the status of the validation
	 */
	protected IStatus typeNameChanged() {
		StatusInfo status= new StatusInfo();
		fCurrType= null;
		String typeNameWithParameters= getTypeName();
		// must not be empty
		if (typeNameWithParameters.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnterTypeName);
			return status;
		}

		String typeName= getTypeNameWithoutParameters();
		if (typeName.indexOf('.') != -1) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_QualifiedName);
			return status;
		}

		ISourceProject project= getJavaProject();
		IStatus val= validateJavaTypeName(typeName, project);
		if (val.getSeverity() == IStatus.ERROR) {
			status.setError(MessageFormat.format(NewWizardMessages.NewTypeWizardPage_error_InvalidTypeName, val.getMessage()));
			return status;
		} else if (val.getSeverity() == IStatus.WARNING) {
			status.setWarning(MessageFormat.format(NewWizardMessages.NewTypeWizardPage_warning_TypeNameDiscouraged, val.getMessage()));
			// continue checking
		}

		// must not exist
		if (!isEnclosingTypeSelected()) {
			ISourceFolder pack= getPackageFragment();
			if (pack != null) {
				IResource resource = ((IContainer)pack.getResource()).getFile(new Path(typeName));
//				fCurrType= cu.getType(typeName);

				if (resource.exists()) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExists);
					return status;
				}
				if (!ResourcesPlugin.getWorkspace().validateFiltered(resource).isOK()) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameFiltered);
					return status;
				}
//				URI location= resource.getLocationURI();
//				if (location != null) {
//					try {
//						IFileStore store= EFS.getStore(location);
//						if (store.fetchInfo().exists()) {
//							status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExistsDifferentCase);
//							return status;
//						}
//					} catch (CoreException e) {
//						status.setError(MessageFormat.format(
//							NewWizardMessages.NewTypeWizardPage_error_uri_location_unkown,
//							BasicElementLabels.getURLPart(Resources.getLocationString(resource))));
//					}
//				}
			}
		} else {
			ITypeInfo type= getEnclosingType();
			if (type != null) {
//				fCurrType= type.getType(typeName);
				if (fCurrType.exists(new NullProgressMonitor())) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExists);
					return status;
				}
			}
		}

//		if (!typeNameWithParameters.equals(typeName) && project != null) {
//			String typeDeclaration= "class " + typeNameWithParameters + " {}"; //$NON-NLS-1$//$NON-NLS-2$
//			ASTParser parser= ASTParser.newParser(AST.JLS3);
//			parser.setSource(typeDeclaration.toCharArray());
//			parser.setProject(project);
//			ICompilationUnit ICompilationUnit= (ICompilationUnit) parser.createAST(null);
//			IProblem[] problems= ICompilationUnit.getProblems();
//			if (problems.length > 0) {
//				status.setError(MessageFormat.format(NewWizardMessages.NewTypeWizardPage_error_InvalidTypeName, problems[0].getMessage()));
//				return status;
//			}
//		}
		return status;
	}

	/**
	 * Hook method that gets called when the superclass name has changed. The method
	 * validates the superclass name and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 *
	 * @return the status of the validation
	 */
	protected IStatus superClassChanged() {
		StatusInfo status= new StatusInfo();
		ISourceRoot root= getPackageFragmentRoot();
		fSuperClassDialogField.enableButton(root != null);

//		fSuperClassStubTypeContext= null;

		String sclassName= getSuperClass();
		if (sclassName.length() == 0) {
			// accept the empty field (stands for java.lang.Object)
			return status;
		}

		if (root != null) {
			ITypeInfo type= SearchUtils.getType(root.getProject().getRawProject(), sclassName);
			if (type == null) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_InvalidSuperClassName);
				return status;
			}
//			if (type instanceof ParameterizedType) {
//				status.setError(NewWizardMessages.NewTypeWizardPage_error_SuperClassNotParameterized);
//				return status;
//			}
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}

//	private StubTypeContext getSuperClassStubTypeContext() {
//		if (fSuperClassStubTypeContext == null) {
//			String typeName;
//			if (fCurrType != null) {
//				typeName= getTypeName();
//			} else {
//				typeName= JavaTypeCompletionProcessor.DUMMY_CLASS_NAME;
//			}
//			fSuperClassStubTypeContext= TypeContextChecker.createSuperClassStubTypeContext(typeName, getEnclosingType(), getPackageFragment());
//		}
//		return fSuperClassStubTypeContext;
//	}

	/**
	 * Hook method that gets called when the list of super interface has changed. The method
	 * validates the super interfaces and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 *
	 * @return the status of the validation
	 */
	protected IStatus superInterfacesChanged() {
		StatusInfo status= new StatusInfo();

		ISourceRoot root= getPackageFragmentRoot();
		fSuperInterfacesDialogField.enableButton(0, root != null);

		if (root != null) {
			List elements= fSuperInterfacesDialogField.getElements();
			int nElements= elements.size();
			for (int i= 0; i < nElements; i++) {
				String intfname= ((InterfaceWrapper) elements.get(i)).interfaceName;
				ITypeInfo type = SearchUtils.getType(root.getProject().getRawProject(), intfname);
				if (type == null) {
					status.setError(MessageFormat.format(NewWizardMessages.NewTypeWizardPage_error_InvalidSuperInterfaceName, intfname));
					return status;
				}
//				if (type instanceof ParameterizedType) {
//					status.setError(MessageFormat.format(NewWizardMessages.NewTypeWizardPage_error_SuperInterfaceNotParameterized, BasicElementLabels.getJavaElementName(intfname)));
//					return status;
//				}
			}
		}
		return status;
	}

//	private StubTypeContext getSuperInterfacesStubTypeContext() {
//		if (fSuperInterfaceStubTypeContext == null) {
//			String typeName;
//			if (fCurrType != null) {
//				typeName= getTypeName();
//			} else {
//				typeName= JavaTypeCompletionProcessor.DUMMY_CLASS_NAME;
//			}
//			fSuperInterfaceStubTypeContext= TypeContextChecker.createSuperInterfaceStubTypeContext(typeName, getEnclosingType(), getPackageFragment());
//		}
//		return fSuperInterfaceStubTypeContext;
//	}

	/**
	 * Hook method that gets called when the modifiers have changed. The method validates
	 * the modifiers and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 *
	 * @return the status of the validation
	 */
	protected IStatus modifiersChanged() {
		StatusInfo status= new StatusInfo();
		int modifiers= getModifiers();
		if (Flags.isFinal(modifiers) && Flags.isAbstract(modifiers)) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_ModifiersFinalAndAbstract);
		}
		return status;
	}

	// selection dialogs

	/**
	 * Opens a selection dialog that allows to select a package.
	 *
	 * @return returns the selected package or <code>null</code> if the dialog has been canceled.
	 * The caller typically sets the result to the package input field.
	 * <p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 *
	 * @since 3.2
	 */
	protected ISourceFolder choosePackage() {
		ISourceRoot froot= getPackageFragmentRoot();
		ISourceEntity[] packages= null;
//		try {
			if (froot != null) {
				packages= froot.getChildren();
			}
//		} catch (ModelException e) {
//			LoggingUtils.log(e);
//		}
		if (packages == null) {
			packages= new ISourceEntity[0];
		}

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new X10LabelProvider());
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_description);
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_empty);
		dialog.setElements(packages);
		dialog.setHelpAvailable(false);

		ISourceFolder pack= getPackageFragment();
		if (pack != null) {
			dialog.setInitialSelections(new Object[] { pack });
		}

		if (dialog.open() == Window.OK) {
			return (ISourceFolder) dialog.getFirstResult();
		}
		return null;
	}

	/**
	 * Opens a selection dialog that allows to select an enclosing type.
	 *
	 * @return returns the selected type or <code>null</code> if the dialog has been canceled.
	 * The caller typically sets the result to the enclosing type input field.
	 * <p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 *
	 * @since 3.2
	 */
	protected ITypeInfo chooseEnclosingType() {
		ISourceRoot root= getPackageFragmentRoot();
		if (root == null) {
			return null;
		}
		
		if(root.getResource() == null)
		{
			return null;
		}

		IX10SearchScope scope= SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, root.getResource());

		FilteredTypesSelectionDialog dialog= new FilteredTypesSelectionDialog(getShell(),
			false, getWizard().getContainer(), scope, 0);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChooseEnclosingTypeDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChooseEnclosingTypeDialog_description);
		dialog.setInitialPattern(getEnclosingTypeText());

		if (dialog.open() == Window.OK) {
			return (ITypeInfo) dialog.getFirstResult();
		}
		return null;
	}

	/**
	 * Opens a selection dialog that allows to select a super class.
	 *
	 * @return returns the selected type or <code>null</code> if the dialog has been canceled.
	 * The caller typically sets the result to the super class input field.
	 * 	<p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 *
	 * @since 3.2
	 */
	protected ITypeInfo chooseSuperClass() {
		ISourceProject project= getJavaProject();
		if (project == null) {
			return null;
		}

		IX10SearchScope scope= SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project.getRawProject());

		FilteredTypesSelectionDialog dialog= new FilteredTypesSelectionDialog(getShell(), false,
			getWizard().getContainer(), scope, 2);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_message);
		dialog.setInitialPattern(getSuperClass());

		if (dialog.open() == Window.OK) {
			return (ITypeInfo) dialog.getFirstResult();
		}
		return null;
	}

	/**
	 * Opens a selection dialog that allows to select the super interfaces. The selected interfaces are
	 * directly added to the wizard page using {@link #addSuperInterface(String)}.
	 *
	 * 	<p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 *
	 * @since 3.2
	 */
	protected void chooseSuperInterfaces() {
		ISourceProject project= getJavaProject();
		if (project == null) {
			return;
		}

		SuperInterfaceSelectionDialog dialog= new SuperInterfaceSelectionDialog(getShell(), getWizard().getContainer(), this, project);
		dialog.setTitle(getInterfaceDialogTitle());
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_InterfacesDialog_message);
		dialog.open();
	}

	private String getInterfaceDialogTitle() {
	    if (fTypeKind == INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_interface_title;
	    return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_class_title;
	}


	// ---- creation ----------------

	/**
	 * Creates the new type using the entered field values.
	 *
	 * @param monitor a progress monitor to report progress.
	 * @throws CoreException Thrown when the creation failed.
	 * @throws InterruptedException Thrown when the operation was canceled.
	 */
	public void createType(IProgressMonitor monitor) throws ModelException, CoreException, InterruptedException {
//		if (monitor == null) {
//			monitor= new NullProgressMonitor();
//		}
//
//		monitor.beginTask(NewWizardMessages.NewTypeWizardPage_operationdesc, 8);
//
//		ISourceRoot root= getPackageFragmentRoot();
//		ISourceFolder pack= getPackageFragment();
//		if (pack == null) {
//			pack= root.createSourceFolder("", true, monitor); //$NON-NLS-1$
//			monitor.worked(1);
//		}
//
//		boolean needsSave;
//		ICompilationUnit connectedCU= null;
//
//		try {
//			String typeName= getTypeNameWithoutParameters();
//
//			boolean isInnerClass= isEnclosingTypeSelected();
//
//			ITypeInfo createdType;
//			ImportsManager imports;
//			int indent= 0;
//
//			Set /* String (import names) */ existingImports;
//
//			String lineDelimiter= null;
//			if (!isInnerClass) {
//				lineDelimiter= StubUtility.getLineDelimiterUsed(pack.getProject());
//
//				String cuName= getCompilationUnitName(typeName);
//				ICompilationUnit parentCU= pack.createICompilationUnit(cuName, "", false, new SubProgressMonitor(monitor, 2)); //$NON-NLS-1$
//				// create a working copy with a new owner
//
//				needsSave= true;
//				parentCU.becomeWorkingCopy(new SubProgressMonitor(monitor, 1)); // cu is now a (primary) working copy
//				connectedCU= parentCU;
//
//				IBuffer buffer= parentCU.getBuffer();
//
//				String simpleTypeStub= constructSimpleTypeStub();
//				String cuContent= constructCUContent(parentCU, simpleTypeStub, lineDelimiter);
//				buffer.setContents(cuContent);
//
//				ICompilationUnit astRoot= createASTForImports(parentCU);
//				existingImports= getExistingImports(astRoot);
//
//				imports= new ImportsManager(astRoot);
//				// add an import that will be removed again. Having this import solves 14661
//				imports.addImport(JavaModelUtil.concatenateName(pack.getElementName(), typeName));
//
//				String typeContent= constructTypeStub(parentCU, imports, lineDelimiter);
//
//				int index= cuContent.lastIndexOf(simpleTypeStub);
//				if (index == -1) {
//					AbstractTypeDeclaration typeNode= (AbstractTypeDeclaration) astRoot.types().get(0);
//					int start= ((ASTNode) typeNode.modifiers().get(0)).getStartPosition();
//					int end= typeNode.getStartPosition() + typeNode.getLength();
//					buffer.replace(start, end - start, typeContent);
//				} else {
//					buffer.replace(index, simpleTypeStub.length(), typeContent);
//				}
//
//				createdType= parentCU.getType(typeName);
//			} else {
//				ITypeInfo enclosingType= getEnclosingType();
//
//				ICompilationUnit parentCU= enclosingType.getCompilationUnit();
//
//				needsSave= !parentCU.isWorkingCopy();
//				parentCU.becomeWorkingCopy(new SubProgressMonitor(monitor, 1)); // cu is now for sure (primary) a working copy
//				connectedCU= parentCU;
//
//				ICompilationUnit astRoot= createASTForImports(parentCU);
//				imports= new ImportsManager(astRoot);
//				existingImports= getExistingImports(astRoot);
//
//
//				// add imports that will be removed again. Having the imports solves 14661
//				ITypeInfo[] topLevelTypes= parentCU.getTypes();
//				for (int i= 0; i < topLevelTypes.length; i++) {
//					imports.addImport(topLevelTypes[i].getFullyQualifiedName('.'));
//				}
//
//				lineDelimiter= StubUtility.getLineDelimiterUsed(enclosingType);
//				StringBuffer content= new StringBuffer();
//
//				String comment= getTypeComment(parentCU, lineDelimiter);
//				if (comment != null) {
//					content.append(comment);
//					content.append(lineDelimiter);
//				}
//
//				content.append(constructTypeStub(parentCU, imports, lineDelimiter));
//				ISourceEntity sibling= null;
//				if (enclosingType.isEnum()) {
//					IField[] fields = enclosingType.getFields();
//					if (fields.length > 0) {
//						for (int i = 0, max = fields.length; i < max; i++) {
//							if (!fields[i].isEnumConstant()) {
//								sibling = fields[i];
//								break;
//							}
//						}
//					}
//				} else {
//					ISourceEntity[] elems= enclosingType.getChildren();
//					sibling = elems.length > 0 ? elems[0] : null;
//				}
//
//				createdType= enclosingType.createType(content.toString(), sibling, false, new SubProgressMonitor(monitor, 2));
//
//				indent= StubUtility.getIndentUsed(enclosingType) + 1;
//			}
//			if (monitor.isCanceled()) {
//				throw new InterruptedException();
//			}
//
//			// add imports for superclass/interfaces, so types can be resolved correctly
//
//			ICompilationUnit cu= createdType.getCompilationUnit();
//
//			imports.create(false, new SubProgressMonitor(monitor, 1));
//
//			JavaModelUtil.reconcile(cu);
//
//			if (monitor.isCanceled()) {
//				throw new InterruptedException();
//			}
//
//			// set up again
//			ICompilationUnit astRoot= createASTForImports(imports.getCompilationUnit());
//			imports= new ImportsManager(astRoot);
//
//			createTypeMembers(createdType, imports, new SubProgressMonitor(monitor, 1));
//
//			// add imports
//			imports.create(false, new SubProgressMonitor(monitor, 1));
//
//			removeUnusedImports(cu, existingImports, false);
//
//			JavaModelUtil.reconcile(cu);
//
//			ISourceRange range= createdType.getSourceRange();
//
//			IBuffer buf= cu.getBuffer();
//			String originalContent= buf.getText(range.getOffset(), range.getLength());
//
//			String formattedContent= CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, originalContent, indent, lineDelimiter, pack.getProject());
//			formattedContent= Strings.trimLeadingTabsAndSpaces(formattedContent);
//			buf.replace(range.getOffset(), range.getLength(), formattedContent);
//			if (!isInnerClass) {
//				String fileComment= getFileComment(cu);
//				if (fileComment != null && fileComment.length() > 0) {
//					buf.replace(0, 0, fileComment + lineDelimiter);
//				}
//			}
//			fCreatedType= createdType;
//
//			if (needsSave) {
//				cu.commitWorkingCopy(true, new SubProgressMonitor(monitor, 1));
//			} else {
//				monitor.worked(1);
//			}
//
//		} finally {
//			if (connectedCU != null) {
//				connectedCU.discardWorkingCopy();
//			}
//			monitor.done();
//		}
	}

//	private ICompilationUnit createASTForImports(ICompilationUnit cu) {
//		ASTParser parser= ASTParser.newParser(AST.JLS3);
//		parser.setSource(cu);
//		parser.setResolveBindings(false);
//		parser.setFocalPosition(0);
//		return (ICompilationUnit) parser.createAST(null);
//	}
//
//
//	private Set /* String */ getExistingImports(ICompilationUnit root) {
//		List imports= root.imports();
//		Set res= new HashSet(imports.size());
//		for (int i= 0; i < imports.size(); i++) {
//			res.add(ASTNodes.asString((ImportDeclaration) imports.get(i)));
//		}
//		return res;
//	}
//
//	private void removeUnusedImports(ICompilationUnit cu, Set existingImports, boolean needsSave) throws CoreException {
//		ASTParser parser= ASTParser.newParser(AST.JLS3);
//		parser.setSource(cu);
//		parser.setResolveBindings(true);
//
//		ICompilationUnit root= (ICompilationUnit) parser.createAST(null);
//		if (root.getProblems().length == 0) {
//			return;
//		}
//
//		List importsDecls= root.imports();
//		if (importsDecls.isEmpty()) {
//			return;
//		}
//		ImportsManager imports= new ImportsManager(root);
//
//		int importsEnd= ASTNodes.getExclusiveEnd((ASTNode) importsDecls.get(importsDecls.size() - 1));
//		IProblem[] problems= root.getProblems();
//		for (int i= 0; i < problems.length; i++) {
//			IProblem curr= problems[i];
//			if (curr.getSourceEnd() < importsEnd) {
//				int id= curr.getID();
//				if (id == IProblem.UnusedImport || id == IProblem.NotVisibleType) { // not visible problems hide unused -> remove both
//					int pos= curr.getSourceStart();
//					for (int k= 0; k < importsDecls.size(); k++) {
//						ImportDeclaration decl= (ImportDeclaration) importsDecls.get(k);
//						if (decl.getStartPosition() <= pos && pos < decl.getStartPosition() + decl.getLength()) {
//							if (existingImports.isEmpty() || !existingImports.contains(ASTNodes.asString(decl))) {
//								String name= decl.getName().getFullyQualifiedName();
//								if (decl.isOnDemand()) {
//									name += ".*"; //$NON-NLS-1$
//								}
//								if (decl.isStatic()) {
//									imports.removeStaticImport(name);
//								} else {
//									imports.removeImport(name);
//								}
//							}
//							break;
//						}
//					}
//				}
//			}
//		}
//		imports.create(needsSave, null);
//	}

	/**
	 * Uses the New Java file template from the code template page to generate a
	 * compilation unit with the given type content.
	 * 
	 * @param cu The new created compilation unit
	 * @param typeContent The content of the type, including signature and type
	 * body.
	 * @param lineDelimiter The line delimiter to be used.
	 * @return String Returns the result of evaluating the new file template
	 * with the given type content.
	 * @throws CoreException when fetching the file comment fails or fetching the content for the
	 *             new compilation unit fails
	 * @since 2.1
	 */
//	protected String constructCUContent(ICompilationUnit cu, String typeContent, String lineDelimiter) throws CoreException {
//		String fileComment= getFileComment(cu, lineDelimiter);
//		String typeComment= getTypeComment(cu, lineDelimiter);
//		ISourceFolder pack= (ISourceFolder) cu.getParent();
//		String content= CodeGeneration.getCompilationUnitContent(cu, fileComment, typeComment, typeContent, lineDelimiter);
//		if (content != null) {
//			ASTParser parser= ASTParser.newParser(AST.JLS3);
//			parser.setProject(cu.getProject());
//			parser.setSource(content.toCharArray());
//			ICompilationUnit unit= (ICompilationUnit) parser.createAST(null);
//			if ((pack.isDefaultPackage() || unit.getPackage() != null) && !unit.types().isEmpty()) {
//				return content;
//			}
//		}
//		StringBuffer buf= new StringBuffer();
//		if (!pack.isDefaultPackage()) {
//			buf.append("package ").append(pack.getElementName()).append(';'); //$NON-NLS-1$
//		}
//		buf.append(lineDelimiter).append(lineDelimiter);
//		if (typeComment != null) {
//			buf.append(typeComment).append(lineDelimiter);
//		}
//		buf.append(typeContent);
//		return buf.toString();
//	}


	/**
	 * Returns the created type or <code>null</code> is the type has not been created yet. The method
	 * only returns a valid type after <code>createType</code> has been called.
	 *
	 * @return the created type
	 * @see #createType(IProgressMonitor)
	 */
	public ITypeInfo getCreatedType() {
		return fCreatedType;
	}

	// ---- construct CU body----------------

//	private void writeSuperClass(StringBuffer buf, ImportsManager imports) {
//		String superclass= getSuperClass();
//		if (fTypeKind == CLASS_TYPE && superclass.length() > 0 && !"java.lang.Object".equals(superclass)) { //$NON-NLS-1$
//			buf.append(" extends "); //$NON-NLS-1$
//
//			ITypeInfoBinding binding= null;
//			if (fCurrType != null) {
//				binding= TypeContextChecker.resolveSuperClass(superclass, fCurrType, getSuperClassStubTypeContext());
//			}
//			if (binding != null) {
//				buf.append(imports.addImport(binding));
//			} else {
//				buf.append(imports.addImport(superclass));
//			}
//		}
//	}
//
//	private void writeSuperInterfaces(StringBuffer buf, ImportsManager imports) {
//		List interfaces= getSuperInterfaces();
//		int last= interfaces.size() - 1;
//		if (last >= 0) {
//		    if (fTypeKind != INTERFACE_TYPE) {
//				buf.append(" implements "); //$NON-NLS-1$
//			} else {
//				buf.append(" extends "); //$NON-NLS-1$
//			}
//			String[] intfs= (String[]) interfaces.toArray(new String[interfaces.size()]);
//			ITypeInfoBinding[] bindings;
//			if (fCurrType != null) {
//				bindings= TypeContextChecker.resolveSuperInterfaces(intfs, fCurrType, getSuperInterfacesStubTypeContext());
//			} else {
//				bindings= new ITypeInfoBinding[intfs.length];
//			}
//			for (int i= 0; i <= last; i++) {
//				ITypeInfoBinding binding= bindings[i];
//				if (binding != null) {
//					buf.append(imports.addImport(binding));
//				} else {
//					buf.append(imports.addImport(intfs[i]));
//				}
//				if (i < last) {
//					buf.append(',');
//				}
//			}
//		}
//	}


	private String constructSimpleTypeStub() {
		StringBuffer buf= new StringBuffer("public class "); //$NON-NLS-1$
		buf.append(getTypeName());
		buf.append("{ }"); //$NON-NLS-1$
		return buf.toString();
	}

	/*
	 * Called from createType to construct the source for this type
	 */
//	private String constructTypeStub(ICompilationUnit parentCU, ImportsManager imports, String lineDelimiter) throws CoreException {
//		StringBuffer buf= new StringBuffer();
//
//		int modifiers= getModifiers();
//		buf.append(Flags.toString(modifiers));
//		if (modifiers != 0) {
//			buf.append(' ');
//		}
//		String type= ""; //$NON-NLS-1$
//		String templateID= ""; //$NON-NLS-1$
//		switch (fTypeKind) {
//			case CLASS_TYPE:
//				type= "class ";  //$NON-NLS-1$
//				templateID= CodeGeneration.CLASS_BODY_TEMPLATE_ID;
//				break;
//			case INTERFACE_TYPE:
//				type= "interface "; //$NON-NLS-1$
//				templateID= CodeGeneration.INTERFACE_BODY_TEMPLATE_ID;
//				break;
//			case ENUM_TYPE:
//				type= "enum "; //$NON-NLS-1$
//				templateID= CodeGeneration.ENUM_BODY_TEMPLATE_ID;
//				break;
//			case ANNOTATION_TYPE:
//				type= "@interface "; //$NON-NLS-1$
//				templateID= CodeGeneration.ANNOTATION_BODY_TEMPLATE_ID;
//				break;
//		}
//		buf.append(type);
//		buf.append(getTypeName());
//		writeSuperClass(buf, imports);
//		writeSuperInterfaces(buf, imports);
//
//		buf.append(" {").append(lineDelimiter); //$NON-NLS-1$
//		String typeBody= CodeGeneration.getTypeBody(templateID, parentCU, getTypeName(), lineDelimiter);
//		if (typeBody != null) {
//			buf.append(typeBody);
//		} else {
//			buf.append(lineDelimiter);
//		}
//		buf.append('}').append(lineDelimiter);
//		return buf.toString();
//	}

	/**
	 * Hook method that gets called from <code>createType</code> to support adding of
	 * unanticipated methods, fields, and inner types to the created type.
	 * <p>
	 * Implementers can use any methods defined on <code>ITypeInfo</code> to manipulate the
	 * new type.
	 * </p>
	 * <p>
	 * The source code of the new type will be formatted using the platform's formatter. Needed
	 * imports are added by the wizard at the end of the type creation process using the given
	 * import manager.
	 * </p>
	 *
	 * @param newType the new type created via <code>createType</code>
	 * @param imports an import manager which can be used to add new imports
	 * @param monitor a progress monitor to report progress. Must not be <code>null</code>
	 * @throws CoreException thrown when creation of the type members failed
	 *
	 * @see #createType(IProgressMonitor)
	 */
//	protected void createTypeMembers(ITypeInfo newType, final ImportsManager imports, IProgressMonitor monitor) throws CoreException {
//		// default implementation does nothing
//		// example would be
//		// String mainMathod= "public void foo(Vector vec) {}"
//		// createdType.createMethod(main, null, false, null);
//		// imports.addImport("java.lang.Vector");
//	}

	/**
	 * Hook method that gets called from <code>createType</code> to retrieve
	 * a file comment. This default implementation returns the content of the
	 * 'file comment' template or <code>null</code> if no comment should be created.
	 *
	 * @param parentCU the parent compilation unit
	 * @param lineDelimiter the line delimiter to use
	 * @return the file comment or <code>null</code> if a file comment
	 * is not desired
	 * @throws CoreException when fetching the file comment fails
     *
     * @since 3.1
	 */
//	protected String getFileComment(ICompilationUnit parentCU, String lineDelimiter) throws CoreException {
//		if (isAddComments()) {
//			return CodeGeneration.getFileComment(parentCU, lineDelimiter);
//		}
//		return null;
//
//	}

//	private boolean isValidComment(String template) {
//		IScanner scanner= ToolFactory.createScanner(true, false, false, false);
//		scanner.setSource(template.toCharArray());
//		try {
//			int next= scanner.getNextToken();
//			while (TokenScanner.isComment(next)) {
//				next= scanner.getNextToken();
//			}
//			return next == ITerminalSymbols.TokenNameEOF;
//		} catch (InvalidInputException e) {
//		}
//		return false;
//	}

	/**
	 * Hook method that gets called from <code>createType</code> to retrieve
	 * a type comment. This default implementation returns the content of the
	 * 'type comment' template.
	 *
	 * @param parentCU the parent compilation unit
	 * @param lineDelimiter the line delimiter to use
	 * @return the type comment or <code>null</code> if a type comment
	 * is not desired
     *
     * @since 3.0
	 */
//	protected String getTypeComment(ICompilationUnit parentCU, String lineDelimiter) {
//		if (isAddComments()) {
//			try {
//				StringBuffer typeName= new StringBuffer();
//				if (isEnclosingTypeSelected()) {
//					typeName.append(SearchUtils.getTypeQualifiedName(getEnclosingType())).append('.');
//				}
//				typeName.append(getTypeNameWithoutParameters());
//				String[] typeParamNames= new String[0];
//				String comment= CodeGeneration.getTypeComment(parentCU, typeName.toString(), typeParamNames, lineDelimiter);
//				if (comment != null && isValidComment(comment)) {
//					return comment;
//				}
//			} catch (CoreException e) {
//				LoggingUtils.log(e);
//			}
//		}
//		return null;
//	}

	

	/**
	 * Returns the string resulting from evaluation the given template in
	 * the context of the given compilation unit. This accesses the normal
	 * template page, not the code templates. To use code templates use
	 * <code>constructCUContent</code> to construct a compilation unit stub or
	 * getTypeComment for the comment of the type.
	 *
	 * @param name the template to be evaluated
	 * @param parentCU the templates evaluation context
	 * @param pos a source offset into the parent compilation unit. The
	 * template is evaluated at the given source offset
	 * @return return the template with the given name or <code>null</code> if the template could not be found.
	 */
//	protected String getTemplate(String name, ICompilationUnit parentCU, int pos) {
//		try {
//			Template template= X10DTUIPlugin.getInstance().getTemplateStore().findTemplate(name);
//			if (template != null) {
//				return JavaContext.evaluateTemplate(template, parentCU, pos);
//			}
//		} catch (CoreException e) {
//			LoggingUtils.log(e);
//		} catch (BadLocationException e) {
//			LoggingUtils.log(e);
//		} catch (TemplateException e) {
//			LoggingUtils.log(e);
//		}
//		return null;
//	}


	/**
	 * Creates the bodies of all unimplemented methods and constructors and adds them to the type.
	 * Method is typically called by implementers of <code>NewTypeWizardPage</code> to add
	 * needed method and constructors.
	 *
	 * @param type the type for which the new methods and constructor are to be created
	 * @param doConstructors if <code>true</code> unimplemented constructors are created
	 * @param doUnimplementedMethods if <code>true</code> unimplemented methods are created
	 * @param imports an import manager to add all needed import statements
	 * @param monitor a progress monitor to report progress
	 * @return the created methods.
	 * @throws CoreException thrown when the creation fails.
	 */
//	protected IMethodInfo[] createInheritedMethods(ITypeInfo type, boolean doConstructors, boolean doUnimplementedMethods, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
//		final ICompilationUnit cu= type.getCompilationUnit();
//		JavaModelUtil.reconcile(cu);
//		IMethodInfo[] typeMethods= type.getMethods();
//		Set handleIds= new HashSet(typeMethods.length);
//		for (int index= 0; index < typeMethods.length; index++)
//			handleIds.add(typeMethods[index].getHandleIdentifier());
//		ArrayList newMethods= new ArrayList();
//		CodeGenerationSettings settings= JavaPreferencesSettings.getCodeGenerationSettings(type.getProject());
//		settings.createComments= isAddComments();
//		ASTParser parser= ASTParser.newParser(AST.JLS3);
//		parser.setResolveBindings(true);
//		parser.setSource(cu);
//		ICompilationUnit unit= (ICompilationUnit) parser.createAST(new SubProgressMonitor(monitor, 1));
//		final ITypeInfoBinding binding= ASTNodes.getTypeBinding(unit, type);
//		if (binding != null) {
//			if (doUnimplementedMethods) {
//				AddUnimplementedMethodsOperation operation= new AddUnimplementedMethodsOperation(unit, binding, null, -1, false, true, false);
//				operation.setCreateComments(isAddComments());
//				operation.run(monitor);
//				createImports(imports, operation.getCreatedImports());
//			}
//			if (doConstructors) {
//				AddUnimplementedConstructorsOperation operation= new AddUnimplementedConstructorsOperation(unit, binding, null, -1, false, true, false);
//				operation.setOmitSuper(true);
//				operation.setCreateComments(isAddComments());
//				operation.run(monitor);
//				createImports(imports, operation.getCreatedImports());
//			}
//		}
//		JavaModelUtil.reconcile(cu);
//		typeMethods= type.getMethods();
//		for (int index= 0; index < typeMethods.length; index++)
//			if (!handleIds.contains(typeMethods[index].getHandleIdentifier()))
//				newMethods.add(typeMethods[index]);
//		IMethodInfo[] methods= new IMethodInfo[newMethods.size()];
//		newMethods.toArray(methods);
//		return methods;
//	}
//
//	private void createImports(ImportsManager imports, String[] createdImports) {
//		for (int index= 0; index < createdImports.length; index++)
//			imports.addImport(createdImports[index]);
//	}


	// ---- creation ----------------

	/**
	 * Returns the runnable that creates the type using the current settings.
	 * The returned runnable must be executed in the UI thread.
	 *
	 * @return the runnable to create the new type
	 */
	public IRunnableWithProgress getRunnable() {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					if (monitor == null) {
						monitor= new NullProgressMonitor();
					}
					createType(monitor);
				} catch (ModelException e) {
					throw new InvocationTargetException(e);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
	}
}
