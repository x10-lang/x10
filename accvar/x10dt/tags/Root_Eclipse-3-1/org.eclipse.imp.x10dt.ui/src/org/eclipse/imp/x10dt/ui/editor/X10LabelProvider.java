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
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.utils.MarkerUtils;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.imp.x10dt.ui.views.Outliner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import polyglot.ast.ClassDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;

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

    public Image getImage(Object node) {
	if (node instanceof ClassDecl) {
	    ClassDecl cd= (ClassDecl) node;

	    return cd.flags().isInterface() ? Outliner._DESC_OBJS_CFILEINT : Outliner._DESC_OBJS_CFILECLASS;
	} else if (node instanceof FieldDecl) {
	    FieldDecl fd= (FieldDecl) node;

	    if (fd.flags().isPublic()) return Outliner._DESC_FIELD_PUBLIC;
	    if (fd.flags().isPrivate()) return Outliner._DESC_FIELD_PRIVATE;
	    if (fd.flags().isProtected()) return Outliner._DESC_FIELD_PROTECTED;
	    return Outliner._DESC_FIELD_DEFAULT;
	} else if (node instanceof ProcedureDecl) {
	    ProcedureDecl pd= (ProcedureDecl) node;

	    if (pd.flags().isPublic()) return Outliner._DESC_MISC_PUBLIC;
	    if (pd.flags().isPrivate()) return Outliner._DESC_MISC_PRIVATE;
	    if (pd.flags().isProtected()) return Outliner._DESC_MISC_PROTECTED;
	    return Outliner._DESC_MISC_DEFAULT;
	}
	if (node instanceof IFile) {
	    IFile file= (IFile) node;

	    return getErrorTicksFromMarkers(file);
	}
	return DEFAULT_AST_IMAGE;
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
	Node node= (Node) element;

	if (node instanceof ClassDecl) {
	    ClassDecl cd= (ClassDecl) node;
	    return cd.name();
	} else if (node instanceof FieldDecl) {
	    FieldDecl fd= (FieldDecl) node;
	    return fd.name();
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
	    return buff.toString().replaceAll("\\{amb\\}", "");
	}
	return "???";
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
