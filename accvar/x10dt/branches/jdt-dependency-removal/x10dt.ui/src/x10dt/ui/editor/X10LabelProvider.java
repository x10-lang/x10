/*******************************************************************************
* Copyright (c) 2008,2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007,2009
 * 
 */
package x10dt.ui.editor;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.ui.DecoratingLabelProvider;
import org.eclipse.imp.utils.MarkerUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.types.Flags;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.Atomic;
import x10.ast.Finish;
import x10.ast.Next;
import x10.ast.TypeDecl_c;
import x10.ast.X10Loop;
import x10.parser.X10SemanticRules.JPGPosition;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.ui.typeHierarchy.X10ElementImageProvider;
import x10dt.search.ui.typeHierarchy.X10ElementLabels;
import x10dt.search.ui.typeHierarchy.X10PluginImages;
import x10dt.ui.X10DTUIPlugin;

/**
 * Text and icon labels for X10 Outline View (and possibly others).<br>
 * To add an image, e.g. "foo", add a String field ending in "_IMAGE_NAME", a similarly named Image field ending in
 * "_IMAGE", and an icon in the icons directory of this (x10dt.ui) project that matches the FOO_IMAGE_NAME+".gif"
 * <br>
 * Static initializer block finds all thusly named fields and creates the images and does the registry
 * bookkeeping.
 * @author Beth Tibbitts
 */
public class X10LabelProvider extends DecoratingLabelProvider {
    
    private static ImageRegistry sImageRegistry= X10DTUIPlugin.getInstance().getImageRegistry();

    public static final String DEFAULT_AST_IMAGE_NAME= "default_ast";
    private static Image DEFAULT_AST_IMAGE;

    // TODO Shouldn't need all these images - use 1 generic image combined w/ 0+ decorations
    public static final String COMPILATION_UNIT_NORMAL_IMAGE_NAME= "compilationUnitNormal";
    public static final String COMPILATION_UNIT_WARNING_IMAGE_NAME= "compilationUnitWarning";
    public static final String COMPILATION_UNIT_ERROR_IMAGE_NAME= "compilationUnitError";
    /** Alias image used for type definitions */
    public static final String ALIAS_IMAGE_NAME="alias";

    private static Image COMPILATION_UNIT_NORMAL_IMAGE;
    private static Image COMPILATION_UNIT_WARNING_IMAGE;
    private static Image COMPILATION_UNIT_ERROR_IMAGE;
    private static Image ALIAS_IMAGE;

//    public static final String PROJECT_NORMAL_IMAGE_NAME= "projectNormal";
//    public static final String PROJECT_WARNING_IMAGE_NAME= "projectWarning";
//    public static final String PROJECT_ERROR_IMAGE_NAME= "projectError";

//    private static Image PROJECT_NORMAL_IMAGE;
//    private static Image PROJECT_WARNING_IMAGE;
//    private static Image PROJECT_ERROR_IMAGE;
    
    //================== PORT1.7 Images moved here from Outliner
    
    public static Image _DESC_ELCL_VIEW_MENU = X10PluginImages.DESC_ELCL_VIEW_MENU.createImage();

    /** Images for the default, private, protected, and public versions of fields */
    public static Image _DESC_FIELD_DEFAULT = X10PluginImages.DESC_FIELD_DEFAULT.createImage();
    public static Image _DESC_FIELD_PRIVATE = X10PluginImages.DESC_FIELD_PRIVATE.createImage();
    public static Image _DESC_FIELD_PROTECTED = X10PluginImages.DESC_FIELD_PROTECTED.createImage();
    public static Image _DESC_FIELD_PUBLIC = X10PluginImages.DESC_FIELD_PUBLIC.createImage();

    /** Images for the default, private, protected, and public versions of miscellaneous objects */
    public static Image _DESC_MISC_DEFAULT = X10PluginImages.DESC_MISC_DEFAULT.createImage();
    public static Image _DESC_MISC_PRIVATE = X10PluginImages.DESC_MISC_PRIVATE.createImage();
    public static Image _DESC_MISC_PROTECTED = X10PluginImages.DESC_MISC_PROTECTED.createImage();
    public static Image _DESC_MISC_PUBLIC = X10PluginImages.DESC_MISC_PUBLIC.createImage();

    public static Image _DESC_OBJS_CFILECLASS = X10PluginImages.DESC_OBJS_CFILECLASS.createImage();
    public static Image _DESC_OBJS_CFILEINT = X10PluginImages.DESC_OBJS_CFILEINT.createImage();

    /** Images for the default, private, protected, and public versions of objects */
    public static Image _DESC_OBJS_INNER_CLASS_DEFAULT = X10PluginImages.DESC_OBJS_INNER_CLASS_DEFAULT.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PRIVATE = X10PluginImages.DESC_OBJS_INNER_CLASS_PRIVATE.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PROTECTED = X10PluginImages.DESC_OBJS_INNER_CLASS_PROTECTED.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PUBLIC = X10PluginImages.DESC_OBJS_INNER_CLASS_PUBLIC.createImage();


    /** Images for the default, private, protected, and public versions of Interfaces */
    public static Image _DESC_OBJS_INNER_INTERFACE_DEFAULT = X10PluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PRIVATE = X10PluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PROTECTED = X10PluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PUBLIC = X10PluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC.createImage();



    public static Image _DESC_OBJS_PACKDECL = X10PluginImages.DESC_OBJS_PACKDECL.createImage();
    
    
    public static Image[] FIELD_DESCS= {
    	X10LabelProvider._DESC_FIELD_DEFAULT, X10LabelProvider._DESC_FIELD_PRIVATE, X10LabelProvider._DESC_FIELD_PROTECTED, X10LabelProvider._DESC_FIELD_PUBLIC
        };  
    public static Image[] MISC_DESCS= {
    	X10LabelProvider._DESC_MISC_DEFAULT, X10LabelProvider._DESC_MISC_PRIVATE, X10LabelProvider._DESC_MISC_PROTECTED, X10LabelProvider._DESC_MISC_PUBLIC
        };
    public static Image[] INNER_CLASS_DESCS= {
    	X10LabelProvider._DESC_OBJS_INNER_CLASS_DEFAULT, X10LabelProvider._DESC_OBJS_INNER_CLASS_PRIVATE, X10LabelProvider._DESC_OBJS_INNER_CLASS_PROTECTED, X10LabelProvider._DESC_OBJS_INNER_CLASS_PUBLIC
        };
    public static Image[] INNER_INTF_DESCS= {
    	X10LabelProvider._DESC_OBJS_INNER_INTERFACE_DEFAULT, X10LabelProvider._DESC_OBJS_INNER_INTERFACE_PRIVATE, X10LabelProvider._DESC_OBJS_INNER_INTERFACE_PROTECTED, X10LabelProvider._DESC_OBJS_INNER_INTERFACE_PUBLIC
        };
    
    public static final ImageDescriptor DESC_OVR_FOCUS= X10PluginImages.DESC_OVR_FOCUS;
	
    
    //===================

    static {
        // Retrieve all images and put them in the appropriately-named fields
        Class<?> myClass= X10LabelProvider.class;
        Field[] fields= myClass.getDeclaredFields();

        for(int i= 0; i < fields.length; i++) {
            Field imageNameField= fields[i];
            String imageNameFieldName= imageNameField.getName();

            if (!imageNameFieldName.endsWith("_IMAGE_NAME"))
                continue;

            try {
                String imageName= (String) imageNameField.get(null);
                ImageDescriptor desc= X10DTUIPlugin.create(imageName + ".gif");

                sImageRegistry.put(imageName, desc);

                try {
                    String imageFieldName= imageNameFieldName.substring(0, imageNameFieldName.lastIndexOf("_NAME"));
                    Field imageField= myClass.getDeclaredField(imageFieldName);

                    imageField.set(null, sImageRegistry.get(imageName));
                } catch(NoSuchFieldException e) {
                    X10DTUIPlugin.log(e);
                }
            } catch (IllegalArgumentException e) {
                X10DTUIPlugin.log(e);
            } catch (IllegalAccessException e) {
                X10DTUIPlugin.log(e);
            }
        }
    }

    private static Image[] CU_IMAGES= { COMPILATION_UNIT_ERROR_IMAGE, COMPILATION_UNIT_WARNING_IMAGE, COMPILATION_UNIT_NORMAL_IMAGE, COMPILATION_UNIT_NORMAL_IMAGE };
//    private static Image[] PROJECT_IMAGES= { PROJECT_ERROR_IMAGE, PROJECT_WARNING_IMAGE, PROJECT_NORMAL_IMAGE, PROJECT_NORMAL_IMAGE };

    private Language fX10Language= LanguageRegistry.findLanguage("x10");

    public X10LabelProvider()
    {
    	this(X10ElementLabels.ALL_DEFAULT, X10ElementImageProvider.OVERLAY_ICONS);
    }
    
    public X10LabelProvider(long textFlags, int imageFlags)
    {
    	super(null, new X10ElementImageProvider(), textFlags, imageFlags);
    }
    
    public Image getImage(Object o) {
        if (o instanceof IResource || o instanceof ISourceEntity) {
            return super.getImage(o);
        }
        
        else if(o instanceof Node || o instanceof ModelTreeNode)
		{
	        Node node= (o instanceof ModelTreeNode) ?
	                (Node) ((ModelTreeNode) o).getASTNode() :
	                        (Node) o;
	
	        if (node instanceof PackageNode) {
	            return _DESC_OBJS_PACKDECL;
	        
	        } else if (node instanceof ClassDecl) {
	            ClassDecl cd= (ClassDecl) node;
	            return cd.flags().flags().isInterface() ? _DESC_OBJS_CFILEINT : _DESC_OBJS_CFILECLASS;//PORT1.7 flags()->flags().flags()
	        
	        } else if (node instanceof FieldDecl) {
	            FieldDecl fd= (FieldDecl) node;
	            return getImageFromQualifiers(fd.flags().flags(), FIELD_DESCS);//PORT1.7 flags()->flags().flags() (Flags vs FlagsNode)
	        
	        } else if (node instanceof ProcedureDecl) {
	            ProcedureDecl pd= (ProcedureDecl) node;
	            return getImageFromQualifiers(pd.flags().flags(), MISC_DESCS);//PORT1.7 flags()->flags().flags() (Flags vs FlagsNode)
	        
	        } else if (node instanceof Async || node instanceof AtEach ||
	                /*node instanceof Future ||*/ node instanceof Finish || node instanceof Atomic ||
	                node instanceof Next) {
	            return _DESC_MISC_DEFAULT;
	        
	        } else if (node instanceof New) {//PORT1.7 ArrayConstructor->New (Nate: "with a Closure as an argument")
	            return _DESC_MISC_DEFAULT;
	        
	        } else if (node instanceof TypeDecl_c){
	        	return ALIAS_IMAGE;
	        }
		}
        return DEFAULT_AST_IMAGE;
    }

    /**
     * Get image based on the public/protected/private qualifier
     * @param flags
     * @param images
     * @return
     */
    private Image getImageFromQualifiers(Flags flags, Image[] images) {
        if (flags.isPrivate())
            return images[1];
        else if (flags.isProtected())
            return images[2];
        else if (flags.isPublic())
            return images[3];
        else
            return images[0];
    }

    public Image getErrorTicksFromMarkers(IResource res) {
        if (res instanceof IFile) {
            IFile file= (IFile) res;
            boolean found= false;

            for (String ext : fX10Language.getFilenameExtensions()) {
                if (ext.equals(file.getFileExtension())) {
                    found= true;
                }
            }
            if (found) {
                return selectDecoratedImage(res, CU_IMAGES);
            }
        }
//        if (res instanceof IProject) {
//            return selectDecoratedImage(res, PROJECT_IMAGES);
//        }
        return null;
    }

    private Image selectDecoratedImage(IResource res, Image[] images) {
        int severity= MarkerUtils.getMaxProblemMarkerSeverity(res, IResource.DEPTH_ONE);

        switch (severity) {
        case IMarker.SEVERITY_ERROR: return images[0];
        case IMarker.SEVERITY_WARNING: return images[1];
        case IMarker.SEVERITY_INFO: return images[2];
        default: return images[3];
        }
    }

    public String getText(Object element) {
		String result= X10ElementLabels.getTextLabel(element, getTextFlags());
		if (result.length() == 0)
		{
			if (element instanceof ITypeInfo) {
				return ((ITypeInfo) element).getName();
			}
			
			else if(element instanceof ISourceEntity)
			{
				return ((ISourceEntity) element).getName();
			}
	    	
			else if(element instanceof Node || element instanceof ModelTreeNode)
			{
		        Node node= (element instanceof ModelTreeNode) ?
			        (Node) ((ModelTreeNode) element).getASTNode():
		                (Node) element;
	
		        if (node instanceof PackageNode) {
		            PackageNode pNode = (PackageNode) node;
		            
					String pnName = pNode.package_().get().fullName().toString();
					return pnName; //PORT1.7  package_()->get()
		        } else if (node instanceof ClassDecl) {
		            ClassDecl cd= (ClassDecl) node;
		            return filter(cd.name().id().toString());//PORT1.7 name() no longer returns a string: name()->name().id().toString()
		        } else if (node instanceof FieldDecl) {
		            FieldDecl fd= (FieldDecl) node;
		            return filter(fd.name() + " : " + fd.type());
		        } else if (node instanceof ProcedureDecl) {
		    	    ProcedureDecl pd= (ProcedureDecl) node;
		            StringBuffer buff= new StringBuffer();
	
		    	    if (node instanceof ConstructorDecl) {
		    	        // Front-end replaces "this" with the containing class name, so handle this case specially
						buff.append("this");
		    	    } else {
		                buff.append(pd.name().id().toString());//PORT1.7 note that ProcedureDecl.name() re-added 10/1/08 by Nate
		    	    }
	
		    	    List<Formal> formals= pd.formals();
		    	    buff.append("(");
		    	    for (Iterator<Formal> iter = formals.iterator(); iter.hasNext();) {
						Formal formal =  iter.next();
						buff.append(formal.type().toString());
						if (iter.hasNext())
							buff.append(", ");
					}
		    	    buff.append(")");
		    	    return filter(buff.toString());
		        } else if (node instanceof Async) {
		            Async a= (Async) node;
		            return "async";
		        } else if (node instanceof AtEach) {
		            AtEach ae= (AtEach) node;
		            return "ateach (" + sourceText(ae.domain()) + ")";
		        } else if (node instanceof Atomic) {
		            Atomic at= (Atomic) node;
		            return "atomic {" + sourceText(at.body()) + "}";
		        } else if (node instanceof Finish) {
		            return "finish";
	//	      } else if (node instanceof Future) {
	//	          Future f= (Future) node;
	//	          return "future " + f.body();
		        } else if (node instanceof Next) {
		            return "next";
		        } else if (node instanceof X10Loop) {
		            X10Loop loop= (X10Loop) node;
		            String text = "for(" + sourceText(loop.formal()) +
		                          " : " + sourceText(loop.domain()) + ")";
		            return filter(text);
		        } else if (node instanceof Call) {
		            Call call = (Call) node;
		            if (call.name().toString().equals("force") && call.arguments().size() == 0) {
		                return "force()";
		            }
		            String temp = node.getClass().getName()+": "+node.toString();//PORT1.7 what is this thing? find out later
		            return temp;
		        } else if (node instanceof TypeDecl_c){  // type definition a.k.a. alias
		        	TypeDecl_c td=(TypeDecl_c)node;
		        	String tdName= td.name().toString();
		        	return tdName;
		        }
	//	        else if (node instanceof New) {//PORT1.7 ArrayConstructor -> New w/ Closure arg
	//	        	New newThing = (New)node;//PORT1.7 ArrayConstruct vs New
	//	        	if(newThing.type().isArray() && newThing.body()!=null ) {
	//	        		ArrayType atype = (ArrayType)newThing.type();
	//	        		return "new " + atype.base() + "[" + sourceText(ac.distribution()) + "]";//PORT1.7 arrayconstructor.arrayBaseType()-> ArrayType.base();
	//	        	}
	//	            
	//	            
	//	        }
			}
	        else if(element instanceof IStorage)
			{
				result= fStorageLabelProvider.getText(element);
			}
	        
	        result = "???";	
		}
		
		return decorateText(result, element);
	}

	public StyledString getStyledText(Object element) {
		StyledString string= X10ElementLabels.getStyledTextLabel(element, (getTextFlags() | X10ElementLabels.COLORIZE));
		if (string.length() == 0 && (element instanceof IStorage)) {
			string= new StyledString(fStorageLabelProvider.getText(element));
		}
		String decorated= decorateText(string.getString(), element);
		if (decorated != null) {
			return StyledCellLabelProvider.styleDecoratedString(decorated, StyledString.DECORATIONS_STYLER, string);
		}
		return string;
	}
    

    private String sourceText(Node n) {
        return ((JPGPosition) n.position()).toText();
    }

    private String filter(String name) {
        return name.replaceAll("\n", "").replaceAll("\\{amb\\}", "");
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
