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
/*
 * Created on Jul 20, 2006
 */
package org.eclipse.imp.x10dt.ui.editor;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.imp.utils.MarkerUtils;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.imp.x10dt.ui.views.Outliner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import polyglot.ast.Call;
import polyglot.ast.Call_c;
import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ext.x10.ast.ArrayConstructor;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import x10.parser.X10Parser.JPGPosition;

public class X10LabelProvider implements ILabelProvider, ILanguageService {
    private Set<ILabelProviderListener> fListeners= new HashSet<ILabelProviderListener>();

//  private ProblemsLabelDecorator fLabelDecorator;

    private static ImageRegistry sImageRegistry= X10UIPlugin.getInstance().getImageRegistry();

    public static final String DEFAULT_AST_IMAGE_NAME= "default_ast";
    private static Image DEFAULT_AST_IMAGE;

    // TODO Shouldn't need all these images - use 1 generic image combined w/ 0+ decorations
    public static final String COMPILATION_UNIT_NORMAL_IMAGE_NAME= "compilationUnitNormal";
    public static final String COMPILATION_UNIT_WARNING_IMAGE_NAME= "compilationUnitWarning";
    public static final String COMPILATION_UNIT_ERROR_IMAGE_NAME= "compilationUnitError";

    private static Image COMPILATION_UNIT_NORMAL_IMAGE;
    private static Image COMPILATION_UNIT_WARNING_IMAGE;
    private static Image COMPILATION_UNIT_ERROR_IMAGE;

    public static final String PROJECT_NORMAL_IMAGE_NAME= "projectNormal";
    public static final String PROJECT_WARNING_IMAGE_NAME= "projectWarning";
    public static final String PROJECT_ERROR_IMAGE_NAME= "projectError";

    private static Image PROJECT_NORMAL_IMAGE;
    private static Image PROJECT_WARNING_IMAGE;
    private static Image PROJECT_ERROR_IMAGE;

    static {
        // Retrieve all images and put them in the appropriately-named fields
        Class myClass= X10LabelProvider.class;
        Field[] fields= myClass.getDeclaredFields();

        for(int i= 0; i < fields.length; i++) {
            Field imageNameField= fields[i];
            String imageNameFieldName= imageNameField.getName();

            if (!imageNameFieldName.endsWith("_IMAGE_NAME"))
                continue;

            try {
                String imageName= (String) imageNameField.get(null);
                ImageDescriptor desc= X10UIPlugin.create(imageName + ".gif");

                sImageRegistry.put(imageName, desc);

                try {
                    String imageFieldName= imageNameFieldName.substring(0, imageNameFieldName.lastIndexOf("_NAME"));
                    Field imageField= myClass.getDeclaredField(imageFieldName);

                    imageField.set(null, sImageRegistry.get(imageName));
                } catch(NoSuchFieldException e) {
                    X10UIPlugin.log(e);
                }
            } catch (IllegalArgumentException e) {
                X10UIPlugin.log(e);
            } catch (IllegalAccessException e) {
                X10UIPlugin.log(e);
            }
        }
    }

    private static Image[] CU_IMAGES= { COMPILATION_UNIT_ERROR_IMAGE, COMPILATION_UNIT_WARNING_IMAGE, COMPILATION_UNIT_NORMAL_IMAGE, COMPILATION_UNIT_NORMAL_IMAGE };
    private static Image[] PROJECT_IMAGES= { PROJECT_ERROR_IMAGE, PROJECT_WARNING_IMAGE, PROJECT_NORMAL_IMAGE, PROJECT_NORMAL_IMAGE };

    private Language fX10Language= LanguageRegistry.findLanguage("x10");

    public Image getImage(Object o) {
        if (o instanceof IResource || o instanceof ISourceEntity) {
            IResource res= (o instanceof ISourceEntity) ? ((ISourceEntity) o).getResource() : (IResource) o;
            return getErrorTicksFromMarkers(res);
        }

        Node node= (o instanceof ModelTreeNode) ?
                (Node) ((ModelTreeNode) o).getASTNode() :
                        (Node) o;

        if (node instanceof PackageNode) {
            return Outliner._DESC_OBJS_PACKDECL;
        } else if (node instanceof ClassDecl) {
            ClassDecl cd= (ClassDecl) node;

            return cd.flags().isInterface() ? Outliner._DESC_OBJS_CFILEINT : Outliner._DESC_OBJS_CFILECLASS;
        } else if (node instanceof FieldDecl) {
            FieldDecl fd= (FieldDecl) node;

            return getImageFromQualifiers(fd.flags(), Outliner.FIELD_DESCS);
        } else if (node instanceof ProcedureDecl) {
            ProcedureDecl pd= (ProcedureDecl) node;

            return getImageFromQualifiers(pd.flags(), Outliner.MISC_DESCS);
        } else if (node instanceof Async || node instanceof AtEach || node instanceof ForEach ||
                node instanceof Future || node instanceof Finish || node instanceof Atomic ||
                node instanceof Next) {
            return Outliner._DESC_MISC_DEFAULT;
        } else if (node instanceof ArrayConstructor) {
            return Outliner._DESC_MISC_DEFAULT;
        }
        return DEFAULT_AST_IMAGE;
    }

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
        if (res instanceof IProject) {
            return selectDecoratedImage(res, PROJECT_IMAGES);
        }
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
        Node node= (element instanceof ModelTreeNode) ?
	        (Node) ((ModelTreeNode) element).getASTNode():
                (Node) element;

        if (node instanceof PackageNode) {
            return ((PackageNode) node).package_().fullName();
        } else if (node instanceof ClassDecl) {
            ClassDecl cd= (ClassDecl) node;
            return filter(cd.name());
        } else if (node instanceof FieldDecl) {
            FieldDecl fd= (FieldDecl) node;
            return filter(fd.name() + " : " + fd.type());
        } else if (node instanceof ProcedureDecl) {
    	    ProcedureDecl pd= (ProcedureDecl) node;
    	    List/*<Formal>*/ formals= pd.formals();
    	    StringBuffer buff= new StringBuffer();
    	    buff.append(pd.name());
    	    buff.append("(");
    	    for(Iterator iter= formals.iterator(); iter.hasNext(); ) {
    		Formal formal= (Formal) iter.next();
    		buff.append(formal.type().toString());
    		if (iter.hasNext())
    		    buff.append(", ");
    	    }
    	    buff.append(")");
    	    return filter(buff.toString());
        } else if (node instanceof Async) {
            Async a= (Async) node;
            return "async (" + filter(a.place().toString()) + ")";
        } else if (node instanceof AtEach) {
            AtEach ae= (AtEach) node;
            return "ateach (" + sourceText(ae.domain()) + ")";
        } else if (node instanceof Atomic) {
            Atomic at= (Atomic) node;
            return "atomic {" + sourceText(at.body()) + "}";
        } else if (node instanceof Finish) {
            Finish f= (Finish) node;
            return "finish";
        } else if (node instanceof ForEach) {
            ForEach fe= (ForEach) node;
            return "foreach(" + sourceText(fe.domain()) + ")";
        } else if (node instanceof Future) {
            Future f= (Future) node;
            return "future " + f.body();
        } else if (node instanceof Next) {
            return "next";
        } else if (node instanceof X10Loop) {
            X10Loop loop= (X10Loop) node;
            String text = "for(" + sourceText(loop.formal()) +
                          " : " + sourceText(loop.domain()) + ")";
            return filter(text);
        } else if (node instanceof Call) {
            Call call = (Call) node;
            if (call.name().equals("force") && call.arguments().size() == 0) {
                return "force()";
            }
        } else if (node instanceof ArrayConstructor) {
            ArrayConstructor ac= (ArrayConstructor) node;
            return "new " + ac.arrayBaseType() + "[" + sourceText(ac.distribution()) + "]";
        }
        return "???";
    }

    private String sourceText(Node n) {
        return ((JPGPosition) n.position()).toText();
    }

    private String filter(String name) {
        return name.replaceAll("\n", "").replaceAll("\\{amb\\}", "");
    }

    public void addListener(ILabelProviderListener listener) {
        fListeners.add(listener);
    }

    public void dispose() { }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        fListeners.remove(listener);
    }
}
