/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Jul 20, 2006
 */
package org.eclipse.imp.x10dt.ui.editor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.utils.MarkerUtils;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.imp.x10dt.ui.views.Outliner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
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

    public static final String DEFAULT_AST= "default_ast.gif";

    // TODO Shouldn't need these decorated images - use 1 CU image combined w/ 0+ decoration images
    public static final String COMPILATION_UNIT_NORMAL= "compilationUnitNormal.gif";
    public static final String COMPILATION_UNIT_WARNING= "compilationUnitWarning.gif";
    public static final String COMPILATION_UNIT_ERROR= "compilationUnitError.gif";

    private static ImageRegistry sImageRegistry= X10UIPlugin.getInstance().getImageRegistry();

    private static ImageDescriptor DEFAULT_AST_IMAGE_DESC;

    private static ImageDescriptor COMPILATION_UNIT_NORMAL_IMAGE_DESC;
    private static ImageDescriptor COMPILATION_UNIT_WARNING_IMAGE_DESC;
    private static ImageDescriptor COMPILATION_UNIT_ERROR_IMAGE_DESC;

    private static Image DEFAULT_AST_IMAGE= sImageRegistry.get(DEFAULT_AST);

    private static Image COMPILATION_UNIT_NORMAL_IMAGE= sImageRegistry.get(COMPILATION_UNIT_NORMAL);
    private static Image COMPILATION_UNIT_WARNING_IMAGE= sImageRegistry.get(COMPILATION_UNIT_WARNING);
    private static Image COMPILATION_UNIT_ERROR_IMAGE= sImageRegistry.get(COMPILATION_UNIT_ERROR);

    static {
	final ImageRegistry ir= X10UIPlugin.getInstance().getImageRegistry();

	DEFAULT_AST_IMAGE_DESC= X10UIPlugin.create(DEFAULT_AST);
	ir.put(DEFAULT_AST, DEFAULT_AST_IMAGE_DESC);
	DEFAULT_AST_IMAGE= ir.get(DEFAULT_AST);

	COMPILATION_UNIT_NORMAL_IMAGE_DESC= X10UIPlugin.create(COMPILATION_UNIT_NORMAL);
	ir.put(COMPILATION_UNIT_NORMAL, COMPILATION_UNIT_NORMAL_IMAGE_DESC);
	COMPILATION_UNIT_NORMAL_IMAGE= ir.get(COMPILATION_UNIT_NORMAL);

	COMPILATION_UNIT_WARNING_IMAGE_DESC= X10UIPlugin.create(COMPILATION_UNIT_WARNING);
	ir.put(COMPILATION_UNIT_WARNING, COMPILATION_UNIT_WARNING_IMAGE_DESC);
	COMPILATION_UNIT_WARNING_IMAGE= ir.get(COMPILATION_UNIT_WARNING);

	COMPILATION_UNIT_ERROR_IMAGE_DESC= X10UIPlugin.create(COMPILATION_UNIT_ERROR);
	ir.put(COMPILATION_UNIT_ERROR, COMPILATION_UNIT_ERROR_IMAGE_DESC);
	COMPILATION_UNIT_ERROR_IMAGE= ir.get(COMPILATION_UNIT_ERROR);
    }

    public Image getImage(Object o) {
        if (o instanceof IResource) {
            return getErrorTicksFromMarkers((IResource) o);
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

    public static Image getErrorTicksFromMarkers(IResource res) {
	int severity= MarkerUtils.getMaxProblemMarkerSeverity(res, IResource.DEPTH_ONE);

	switch (severity) {
	case IMarker.SEVERITY_ERROR: return COMPILATION_UNIT_ERROR_IMAGE;
	case IMarker.SEVERITY_WARNING: return COMPILATION_UNIT_WARNING_IMAGE;
	case IMarker.SEVERITY_INFO: return COMPILATION_UNIT_NORMAL_IMAGE; // COMPILATION_UNIT_INFO_IMAGE;
	default: return COMPILATION_UNIT_NORMAL_IMAGE;
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
	    return filter(fd.type() + " " + fd.name());
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
        } else if (node instanceof Finish) {
            Finish f= (Finish) node;
            return "finish";
        } else if (node instanceof ForEach) {
            ForEach fe= (ForEach) node;
            return "foreach(" + sourceText(fe.domain()) + ")";
        } else if (node instanceof AtEach) {
            AtEach ae= (AtEach) node;
            return "ateach (" + sourceText(ae.domain()) + ")";
        } else if (node instanceof Future) {
            Future f= (Future) node;
            return "future " + f.body();
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
