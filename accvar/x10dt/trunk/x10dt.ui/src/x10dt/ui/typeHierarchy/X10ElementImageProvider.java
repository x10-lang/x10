/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman, mpchapman@gmail.com - 89977 Make JDT .java agnostic
 *******************************************************************************/
package x10dt.ui.typeHierarchy;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.runtime.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.model.IWorkbenchAdapter;

import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.Atomic;
import x10.ast.Finish;
import x10.ast.Next;
import x10.ast.TypeDecl_c;
import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.elements.IX10Element;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.SearchUtils.Flags;

/**
 * Default strategy of the Java plugin for the construction of Java element icons.
 */
public class X10ElementImageProvider {
	
	/**
	 * Flags for the JavaImageLabelProvider:
	 * Generate images with overlays.
	 */
	public final static int OVERLAY_ICONS= 0x1;

	/**
	 * Generate small sized images.
	 */
	public final static int SMALL_ICONS= 0x2;

	/**
	 * Use the 'light' style for rendering types.
	 */
	public final static int LIGHT_TYPE_ICONS= 0x4;


	public static final Point SMALL_SIZE= new Point(16, 16);
	public static final Point BIG_SIZE= new Point(22, 16);

	public static ImageDescriptor[] FIELD_DESCS= {
		X10PluginImages.DESC_FIELD_DEFAULT, X10PluginImages.DESC_FIELD_PRIVATE, X10PluginImages.DESC_FIELD_PROTECTED, X10PluginImages.DESC_FIELD_PUBLIC
        };  
    public static ImageDescriptor[] MISC_DESCS= {
    	X10PluginImages.DESC_MISC_DEFAULT, X10PluginImages.DESC_MISC_PRIVATE, X10PluginImages.DESC_MISC_PROTECTED, X10PluginImages.DESC_MISC_PUBLIC
        };
    public static ImageDescriptor[] INNER_CLASS_DESCS= {
    	X10PluginImages.DESC_OBJS_INNER_CLASS_DEFAULT, X10PluginImages.DESC_OBJS_INNER_CLASS_PRIVATE, X10PluginImages.DESC_OBJS_INNER_CLASS_PROTECTED, X10PluginImages.DESC_OBJS_INNER_CLASS_PUBLIC
        };
    public static ImageDescriptor[] INNER_INTF_DESCS= {
    	X10PluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT, X10PluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE, X10PluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED, X10PluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC
        };

	private ImageDescriptorRegistry fRegistry;

	public X10ElementImageProvider() {
		fRegistry= null; // lazy initialization
	}

	/**
	 * Returns the icon for a given element. The icon depends on the element type
	 * and element properties. If configured, overlay icons are constructed for
	 * <code>ISourceReference</code>s.
	 * @param element the element
	 * @param flags Flags as defined by the JavaImageLabelProvider
	 * @return return the image or <code>null</code>
	 */
	public Image getImageLabel(Object element, int flags) {
		return getImageLabel(computeDescriptor(element, flags));
	}

	private Image getImageLabel(ImageDescriptor descriptor){
		if (descriptor == null)
			return null;
		return getRegistry().get(descriptor);
	}

	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry= X10DTUIPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}


	private ImageDescriptor computeDescriptor(Object element, int flags){
		if (element instanceof IX10Element) {
			return getJavaImageDescriptor((IX10Element) element, flags);
		} 
		else if (element instanceof IFile) {
			IFile file= (IFile) element;
			if (LanguageRegistry.findLanguage("X10").getFilenameExtensions().contains(file.getFileExtension())) {
				return getCUResourceImageDescriptor(file, flags); // image for a CU not on the build path
			}
			return getWorkbenchImageDescriptor(file, flags);
		} else if (element instanceof IAdaptable) {
			return getWorkbenchImageDescriptor((IAdaptable) element, flags);
		} else if(element instanceof ModelTreeNode) {
			return getNodeImageDescriptor((Node) ((ModelTreeNode) element).getASTNode(), flags);
		} else if(element instanceof Node) {
			return getNodeImageDescriptor((Node)element, flags);
		}
		
		return null;
	}

	private static boolean showOverlayIcons(int flags) {
		return (flags & OVERLAY_ICONS) != 0;
	}

	private static boolean useSmallSize(int flags) {
		return (flags & SMALL_ICONS) != 0;
	}

	private static boolean useLightIcons(int flags) {
		return (flags & LIGHT_TYPE_ICONS) != 0;
	}
	
	

	/**
	 * Returns an image descriptor for a compilation unit not on the class path.
	 * The descriptor includes overlays, if specified.
	 * @param file the cu resource file
	 * @param flags the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getCUResourceImageDescriptor(IFile file, int flags) {
		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		ImageDescriptor desc= X10PluginImages.DESC_OBJS_CUNIT_RESOURCE;
		
		try {
			int severity = file.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
			if (severity == IMarker.SEVERITY_ERROR) {
				severity = X10ElementImageDescriptor.ERROR;
			} else if (severity == IMarker.SEVERITY_WARNING) {
				severity = X10ElementImageDescriptor.WARNING;
			}

			if(severity > 0)
			{
				desc = new X10ElementImageDescriptor(desc, severity, size);
			}
		} catch (CoreException e) {
			X10DTUIPlugin.log(e);
		}
		
		return desc;
	}

	/**
	 * Returns an image descriptor for a java element. The descriptor includes overlays, if specified.
	 * @param element the Java element
	 * @param flags the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getJavaImageDescriptor(IX10Element element, int flags) {
		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;

		ImageDescriptor baseDesc= getBaseImageDescriptor(element, flags);
		if (baseDesc != null) {
			int adornmentFlags= computeJavaAdornmentFlags(element, flags);
			return new X10ElementImageDescriptor(baseDesc, adornmentFlags, size);
		}
		return new X10ElementImageDescriptor(X10PluginImages.DESC_OBJS_GHOST, 0, size);
	}
	
	public ImageDescriptor getNodeImageDescriptor(Node node, int flags) {
		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		ImageDescriptor baseDesc= null;
		
        if (node instanceof PackageNode) {
        	baseDesc= X10PluginImages.DESC_OBJS_PACKDECL;
        
        } else if (node instanceof ClassDecl) {
            ClassDecl cd= (ClassDecl) node;
            baseDesc= cd.flags().flags().isInterface() ? X10PluginImages.DESC_OBJS_CFILEINT : X10PluginImages.DESC_OBJS_CFILECLASS;//PORT1.7 flags()->flags().flags()
        
        } else if (node instanceof FieldDecl) {
            FieldDecl fd= (FieldDecl) node;
            baseDesc= getImageFromQualifiers(fd.flags().flags(), FIELD_DESCS);//PORT1.7 flags()->flags().flags() (Flags vs FlagsNode)
        
        } else if (node instanceof ProcedureDecl) {
            ProcedureDecl pd= (ProcedureDecl) node;
            baseDesc= getImageFromQualifiers(pd.flags().flags(), MISC_DESCS);//PORT1.7 flags()->flags().flags() (Flags vs FlagsNode)
        
        } else if (node instanceof Async || node instanceof AtEach ||
                /*node instanceof Future ||*/ node instanceof Finish || node instanceof Atomic ||
                node instanceof Next) {
        	baseDesc= X10PluginImages.DESC_MISC_DEFAULT;
        
        } else if (node instanceof New) {//PORT1.7 ArrayConstructor->New (Nate: "with a Closure as an argument")
        	baseDesc= X10PluginImages.DESC_MISC_DEFAULT;
        
        } else if (node instanceof TypeDecl_c){
        	baseDesc= X10PluginImages.DESC_ALIAS;
        }
        else
        {
        	baseDesc= X10PluginImages.DESC_DEFAULT_AST;
        }
        if (baseDesc != null) {
			int adornmentFlags= computeJavaAdornmentFlags(node, flags);
			return new X10ElementImageDescriptor(baseDesc, adornmentFlags, size);
		}
        return new X10ElementImageDescriptor(X10PluginImages.DESC_OBJS_GHOST, 0, size);
	}
	
	/**
     * Get image based on the public/protected/private qualifier
     * @param flags
     * @param images
     * @return
     */
    private ImageDescriptor getImageFromQualifiers(polyglot.types.Flags flags, ImageDescriptor[] images) {
        if (flags.isPrivate())
            return images[1];
        else if (flags.isProtected())
            return images[2];
        else if (flags.isPublic())
            return images[3];
        else
            return images[0];
    }

	/**
	 * Returns an image descriptor for a IAdaptable. The descriptor includes overlays, if specified (only error ticks apply).
	 * Returns <code>null</code> if no image could be found.
	 * @param adaptable the adaptable
	 * @param flags the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getWorkbenchImageDescriptor(IAdaptable adaptable, int flags) {
		IWorkbenchAdapter wbAdapter= (IWorkbenchAdapter) adaptable.getAdapter(IWorkbenchAdapter.class);
		if (wbAdapter == null) {
			return null;
		}
		ImageDescriptor descriptor= wbAdapter.getImageDescriptor(adaptable);
		if (descriptor == null) {
			return null;
		}

		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new X10ElementImageDescriptor(descriptor, 0, size);
	}

	// ---- Computation of base image key -------------------------------------------------

	/**
	 * Returns an image descriptor for a java element. This is the base image, no overlays.
	 * @param element the element
	 * @param renderFlags the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getBaseImageDescriptor(IX10Element element, int renderFlags) {

//		try {
			if(element instanceof IMethodInfo)
			{
				IMethodInfo method= (IMethodInfo) element;
				ITypeInfo declType= method.getDeclaringType();
				int flags= method.getX10FlagsCode();
//				if (declType.isEnum() && isDefaultFlag(flags) && method.isConstructor())
//					return X10PluginImages.DESC_MISC_PRIVATE;
				return getMethodImageDescriptor(Flags.isInterface(declType.getX10FlagsCode()), flags);
			}
			
			else if(element instanceof IFieldInfo)
			{
				IMemberInfo member= (IMemberInfo) element;
				ITypeInfo declType= member.getDeclaringType();
				return getFieldImageDescriptor(Flags.isInterface(declType.getX10FlagsCode()), member.getX10FlagsCode());
			}
			
			else if(element instanceof ITypeInfo)
			{
				ITypeInfo type= (ITypeInfo) element;

				ITypeInfo declType= type.getDeclaringType();
				boolean isInner= declType != null;
				boolean isInInterfaceOrAnnotation= isInner && Flags.isInterface(declType.getX10FlagsCode());
				return getTypeImageDescriptor(isInner, isInInterfaceOrAnnotation, type.getX10FlagsCode(), useLightIcons(renderFlags));
			}
			
			// ignore. Must be a new, yet unknown Java element
			// give an advanced IWorkbenchAdapter the chance
//			IWorkbenchAdapter wbAdapter= (IWorkbenchAdapter) element.getAdapter(IWorkbenchAdapter.class);
//			if (wbAdapter != null && !(wbAdapter instanceof JavaWorkbenchAdapter)) { // avoid recursion
//				ImageDescriptor imageDescriptor= wbAdapter.getImageDescriptor(element);
//				if (imageDescriptor != null) {
//					return imageDescriptor;
//				}
//			}
			return X10PluginImages.DESC_OBJS_GHOST;
			
//			switch (element.getElementType()) {
//				case IX10Element.INITIALIZER:
//					return X10PluginImages.DESC_MISC_PRIVATE; // 23479
				
//				case IX10Element.LOCAL_VARIABLE:
//					return X10PluginImages.DESC_OBJS_LOCAL_VARIABLE;

//				case IX10Element.PACKAGE_DECLARATION:
//					return X10PluginImages.DESC_OBJS_PACKDECL;

//				case IX10Element.IMPORT_DECLARATION:
//					return X10PluginImages.DESC_OBJS_IMPDECL;

//				case IX10Element.IMPORT_CONTAINER:
//					return X10PluginImages.DESC_OBJS_IMPCONT;

				
				

//				case IX10Element.PACKAGE_FRAGMENT_ROOT: {
//					IPackageFragmentRoot root= (IPackageFragmentRoot) element;
//					IPath attach= root.getSourceAttachmentPath();
//					if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
//						if (root.isArchive()) {
//							if (root.isExternal()) {
//								if (attach == null) {
//									return X10PluginImages.DESC_OBJS_EXTJAR;
//								} else {
//									return X10PluginImages.DESC_OBJS_EXTJAR_WSRC;
//								}
//							} else {
//								if (attach == null) {
//									return X10PluginImages.DESC_OBJS_JAR;
//								} else {
//									return X10PluginImages.DESC_OBJS_JAR_WSRC;
//								}
//							}
//						} else {
//							if (attach == null) {
//								return X10PluginImages.DESC_OBJS_CLASSFOLDER;
//							} else {
//								return X10PluginImages.DESC_OBJS_CLASSFOLDER_WSRC;
//							}
//						}
//					} else {
//						return X10PluginImages.DESC_OBJS_PACKFRAG_ROOT;
//					}
//				}

//				case IX10Element.PACKAGE_FRAGMENT:
//					return getPackageFragmentIcon(element);


//				case IX10Element.COMPILATION_UNIT:
//					return X10PluginImages.DESC_OBJS_CUNIT;

//				case IX10Element.CLASS_FILE:
//					/* this is too expensive for large packages
//					try {
//						IClassFile cfile= (IClassFile)element;
//						if (cfile.isClass())
//							return X10PluginImages.IMG_OBJS_CFILECLASS;
//						return X10PluginImages.IMG_OBJS_CFILEINT;
//					} catch(ModelException e) {
//						// fall through;
//					}*/
//					return X10PluginImages.DESC_OBJS_CFILE;
//
//				case IX10Element.JAVA_PROJECT:
//					IJavaProject jp= (IJavaProject)element;
//					if (jp.getProject().isOpen()) {
//						IProject project= jp.getProject();
//						IWorkbenchAdapter adapter= (IWorkbenchAdapter)project.getAdapter(IWorkbenchAdapter.class);
//						if (adapter != null) {
//							ImageDescriptor result= adapter.getImageDescriptor(project);
//							if (result != null)
//								return result;
//						}
//						return DESC_OBJ_PROJECT;
//					}
//					return DESC_OBJ_PROJECT_CLOSED;
//
//				case IX10Element.JAVA_MODEL:
//					return X10PluginImages.DESC_OBJS_JAVA_MODEL;
//
//				case IX10Element.TYPE_PARAMETER:
//					return X10PluginImages.DESC_OBJS_TYPEVARIABLE;
//
//				case IX10Element.ANNOTATION:
//					return X10PluginImages.DESC_OBJS_ANNOTATION;

//		} catch (ModelException e) {
//			if (e.isDoesNotExist())
//				return X10PluginImages.DESC_OBJS_UNKNOWN;
//			X10DTUIPlugin.log(e);
//			return X10PluginImages.DESC_OBJS_GHOST;
//		}
	}

	private static boolean isDefaultFlag(int flags) {
		return !SearchUtils.hasFlag(X10.PUBLIC, flags) && !SearchUtils.hasFlag(X10.PROTECTED, flags) && !SearchUtils.hasFlag(X10.PRIVATE, flags);
	}

//	private ImageDescriptor getPackageFragmentIcon(IX10Element element) throws ModelException {
//		IPackageFragment fragment= (IPackageFragment)element;
//		boolean containsJavaElements= false;
//		try {
//			containsJavaElements= fragment.hasChildren();
//		} catch(ModelException e) {
//			// assuming no children;
//		}
//		if(!containsJavaElements && (fragment.getNonJavaResources().length > 0))
//			return X10PluginImages.DESC_OBJS_EMPTY_PACKAGE_RESOURCES;
//		else if (!containsJavaElements)
//			return X10PluginImages.DESC_OBJS_EMPTY_PACKAGE;
//		return X10PluginImages.DESC_OBJS_PACKAGE;
//	}

	public void dispose() {
	}

	// ---- Methods to compute the adornments flags ---------------------------------

	private int computeJavaAdornmentFlags(IX10Element element, int renderFlags) {
		int flags= 0;
		if (showOverlayIcons(renderFlags) && element instanceof IMemberInfo) {
			try {
				IMemberInfo member= (IMemberInfo) element;

				if (element instanceof IMethodInfo && ((IMethodInfo)element).isConstructor())
					flags |= X10ElementImageDescriptor.CONSTRUCTOR;

				int modifiers= member.getX10FlagsCode();
				if (Flags.isAbstract(modifiers) && confirmAbstract(member))
					flags |= X10ElementImageDescriptor.ABSTRACT;
				if (Flags.isFinal(modifiers) || isInterfaceOrAnnotationField(member) || isEnumConstant(member, modifiers))
					flags |= X10ElementImageDescriptor.FINAL;
//				if (Flags.isSynchronized(modifiers) && confirmSynchronized(member))
//					flags |= X10ElementImageDescriptor.SYNCHRONIZED;
				if (Flags.isStatic(modifiers) || isInterfaceOrAnnotationFieldOrType(member) || isEnumConstant(member, modifiers))
					flags |= X10ElementImageDescriptor.STATIC;

//				if (Flags.isDeprecated(modifiers))
//					flags |= X10ElementImageDescriptor.DEPRECATED;
//				if (member instanceof ITypeInfo) {
//					if (ModelUtil.hasMainMethod((ITypeInfo) member)) {
//						flags |= X10ElementImageDescriptor.RUNNABLE;
//					}
//				}
//				if (member.getElementType() == IX10Element.FIELD) {
//					if (Flags.isVolatile(modifiers))
//						flags |= X10ElementImageDescriptor.VOLATILE;
//					if (Flags.isTransient(modifiers))
//						flags |= X10ElementImageDescriptor.TRANSIENT;
//				}


			} catch (ModelException e) {
				// do nothing. Can't compute runnable adornment or get flags
			}
		}
		return flags;
	}
	
	int flagsFromNode(polyglot.types.Flags node)
	{
		int flags= 0;

		if (node.isAbstract())
			flags |= X10ElementImageDescriptor.ABSTRACT;
		if (node.isFinal() || node.isInterface())
			flags |= X10ElementImageDescriptor.FINAL;
		if (node.isStatic() || node.isInterface())
			flags |= X10ElementImageDescriptor.STATIC;
		
		return flags;
	}
	
	private int computeJavaAdornmentFlags(Node node, int renderFlags) {
		int flags= 0;
		FlagsNode flagsNode= null; 
		if (showOverlayIcons(renderFlags)) {
			if (node instanceof ClassDecl) {
	            flagsNode = ((ClassDecl)node).flags();
	        } else if (node instanceof FieldDecl) {
	            flagsNode = ((FieldDecl)node).flags();
	        } else if (node instanceof ProcedureDecl) {
	            flagsNode = ((ProcedureDecl)node).flags();
	        } else if (node instanceof TypeDecl_c){
	        	flagsNode = ((TypeDecl_c)node).flags();
	        }

			if(flagsNode != null)
			{
				flags = flagsFromNode(flagsNode.flags());
			}
			if (node instanceof ConstructorDecl)
				flags |= X10ElementImageDescriptor.CONSTRUCTOR;
		}
		return flags;
	}

	private static boolean confirmAbstract(IMemberInfo element) throws ModelException {
		// never show the abstract symbol on interfaces or members in interfaces
		if (element instanceof ITypeInfo) {
			return ! Flags.isInterface(element.getX10FlagsCode());
		}
		return ! Flags.isInterface(element.getX10FlagsCode());
	}

	private static boolean isInterfaceOrAnnotationField(IMemberInfo element) throws ModelException {
		// always show the final symbol on interface fields
		if (element instanceof IFieldInfo) {
			return Flags.isInterface(element.getX10FlagsCode());
		}
		return false;
	}

	private static boolean isInterfaceOrAnnotationFieldOrType(IMemberInfo element) throws ModelException {
		// always show the static symbol on interface fields and types
		if (element instanceof IFieldInfo) {
			return Flags.isInterface(element.getX10FlagsCode());
		} else if (element instanceof ITypeInfo && element.getDeclaringType() != null) {
			return Flags.isInterface(element.getX10FlagsCode());
		}
		return false;
	}

	private static boolean isEnumConstant(IMemberInfo element, int modifiers) {
//		if (element instanceof IFieldInfo) {
//			return Flags.isEnum(modifiers);
//		}
		return false;
	}

	private static boolean confirmSynchronized(IX10Element member) {
		// Synchronized types are allowed but meaningless.
		return member instanceof ITypeInfo;
	}


	public static ImageDescriptor getMethodImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return X10PluginImages.DESC_MISC_PUBLIC;
		if (Flags.isProtected(flags))
			return X10PluginImages.DESC_MISC_PROTECTED;
		if (Flags.isPrivate(flags))
			return X10PluginImages.DESC_MISC_PRIVATE;

		return X10PluginImages.DESC_MISC_DEFAULT;
	}

	public static ImageDescriptor getFieldImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation /* || Flags.isEnum(flags) */)
			return X10PluginImages.DESC_FIELD_PUBLIC;
		if (Flags.isProtected(flags))
			return X10PluginImages.DESC_FIELD_PROTECTED;
		if (Flags.isPrivate(flags))
			return X10PluginImages.DESC_FIELD_PRIVATE;

		return X10PluginImages.DESC_FIELD_DEFAULT;
	}

	public static ImageDescriptor getTypeImageDescriptor(boolean isInner, boolean isInInterfaceOrAnnotation, int flags, boolean useLightIcons) {
//		if (Flags.isEnum(flags)) {
//			if (useLightIcons) {
//				return X10PluginImages.DESC_OBJS_ENUM_ALT;
//			}
//			if (isInner) {
//				return getInnerEnumImageDescriptor(isInInterfaceOrAnnotation, flags);
//			}
//			return getEnumImageDescriptor(flags);
//		} else if (Flags.isAnnotation(flags)) {
//			if (useLightIcons) {
//				return X10PluginImages.DESC_OBJS_ANNOTATION_ALT;
//			}
//			if (isInner) {
//				return getInnerAnnotationImageDescriptor(isInInterfaceOrAnnotation, flags);
//			}
//			return getAnnotationImageDescriptor(flags);
//		}  else 
			
		if (Flags.isInterface(flags)) {
			if (useLightIcons) {
				return X10PluginImages.DESC_OBJS_INTERFACEALT;
			}
			if (isInner) {
				return getInnerInterfaceImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getInterfaceImageDescriptor(flags);
		} else {
			if (useLightIcons) {
				return X10PluginImages.DESC_OBJS_CLASSALT;
			}
			if (isInner) {
				return getInnerClassImageDescriptor(isInInterfaceOrAnnotation, flags);
			}
			return getClassImageDescriptor(flags);
		}
	}


	public static Image getDecoratedImage(ImageDescriptor baseImage, int adornments, Point size) {
		return X10DTUIPlugin.getImageDescriptorRegistry().get(new X10ElementImageDescriptor(baseImage, adornments, size));
	}


	private static ImageDescriptor getClassImageDescriptor(int flags) {
		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_CLASS;
		else
			return X10PluginImages.DESC_OBJS_CLASS_DEFAULT;
	}

	private static ImageDescriptor getInnerClassImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return X10PluginImages.DESC_OBJS_INNER_CLASS_PUBLIC;
		else if (Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_INNER_CLASS_PRIVATE;
		else if (Flags.isProtected(flags))
			return X10PluginImages.DESC_OBJS_INNER_CLASS_PROTECTED;
		else
			return X10PluginImages.DESC_OBJS_INNER_CLASS_DEFAULT;
	}

	private static ImageDescriptor getEnumImageDescriptor(int flags) {
		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_ENUM;
		else
			return X10PluginImages.DESC_OBJS_ENUM_DEFAULT;
	}

	private static ImageDescriptor getInnerEnumImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return X10PluginImages.DESC_OBJS_ENUM;
		else if (Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_ENUM_PRIVATE;
		else if (Flags.isProtected(flags))
			return X10PluginImages.DESC_OBJS_ENUM_PROTECTED;
		else
			return X10PluginImages.DESC_OBJS_ENUM_DEFAULT;
	}

	private static ImageDescriptor getAnnotationImageDescriptor(int flags) {
		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_ANNOTATION;
		else
			return X10PluginImages.DESC_OBJS_ANNOTATION_DEFAULT;
	}

	private static ImageDescriptor getInnerAnnotationImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return X10PluginImages.DESC_OBJS_ANNOTATION;
		else if (Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_ANNOTATION_PRIVATE;
		else if (Flags.isProtected(flags))
			return X10PluginImages.DESC_OBJS_ANNOTATION_PROTECTED;
		else
			return X10PluginImages.DESC_OBJS_ANNOTATION_DEFAULT;
	}

	private static ImageDescriptor getInterfaceImageDescriptor(int flags) {
		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_INTERFACE;
		else
			return X10PluginImages.DESC_OBJS_INTERFACE_DEFAULT;
	}

	private static ImageDescriptor getInnerInterfaceImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
			return X10PluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC;
		else if (Flags.isPrivate(flags))
			return X10PluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
		else if (Flags.isProtected(flags))
			return X10PluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
		else
			return X10PluginImages.DESC_OBJS_INTERFACE_DEFAULT;
	}
}
