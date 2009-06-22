/*
 * Created on Jul 20, 2006
 */
package x10.uide.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uide.core.ILanguageService;

import polyglot.ast.ClassDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import x10.uide.X10UIPlugin;
import x10.uide.views.Outliner;

public class X10LabelProvider implements ILabelProvider, ILanguageService {
    private Set<ILabelProviderListener> fListeners= new HashSet<ILabelProviderListener>();

    public static final String DEFAULT_AST= "default_ast";

    private static ImageRegistry sImageRegistry= X10UIPlugin.getInstance().getImageRegistry();

    private static Image DEFAULT_IMAGE= sImageRegistry.get(DEFAULT_AST);

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
	return DEFAULT_IMAGE;
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
	    return pd.name() + "()";
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
