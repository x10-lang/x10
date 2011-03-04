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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.Atomic;
import x10.ast.Finish;
import x10.ast.Next;
import x10.ast.TypeDecl_c;
import x10.ast.X10Loop;
import x10.parser.X10SemanticRules.JPGPosition;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.X10ElementImageProvider;

/**
 * Text and icon labels for X10 Outline View (and possibly others).<br>
 * To add an image, e.g. "foo", add a String field ending in "_IMAGE_NAME", a similarly named Image field ending in
 * "_IMAGE", and an icon in the icons directory of this (x10dt.ui) project that matches the FOO_IMAGE_NAME+".gif"
 * <br>
 * Static initializer block finds all thusly named fields and creates the images and does the registry
 * bookkeeping.
 * @author Beth Tibbitts
 */
public class X10LabelProvider implements ILabelProvider, ILanguageService, IStyledLabelProvider {
    private Set<ILabelProviderListener> fListeners= new HashSet<ILabelProviderListener>();

    private static ImageRegistry sImageRegistry= X10DTUIPlugin.getInstance().getImageRegistry();
    
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

    
    public Image getImage(Object o) {
    	X10ElementImageProvider x = new X10ElementImageProvider();
    	return x.getImageLabel(o, X10ElementImageProvider.OVERLAY_ICONS);
    }

    

    public String getText(Object element) {
		if (element instanceof IType) {
			return ((IType) element).getElementName();
		}
    	
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
//      } else if (node instanceof Future) {
//          Future f= (Future) node;
//          return "future " + f.body();
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
//        else if (node instanceof New) {//PORT1.7 ArrayConstructor -> New w/ Closure arg
//        	New newThing = (New)node;//PORT1.7 ArrayConstruct vs New
//        	if(newThing.type().isArray() && newThing.body()!=null ) {
//        		ArrayType atype = (ArrayType)newThing.type();
//        		return "new " + atype.base() + "[" + sourceText(ac.distribution()) + "]";//PORT1.7 arrayconstructor.arrayBaseType()-> ArrayType.base();
//        	}
//            
//            
//        }
        
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

	public StyledString getStyledText(Object element) {
		return new StyledString(getText(element));
	}
}
