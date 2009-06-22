package x10.uide.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.uide.core.ErrorHandler;
import org.eclipse.uide.defaults.DefaultOutliner;
import org.eclipse.uide.editor.IOutliner;
import org.eclipse.uide.editor.UniversalEditor;
import org.eclipse.uide.parser.Ast;
import org.eclipse.uide.parser.IParseController;
import org.eclipse.uide.parser.ParseError;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;

import com.ibm.lpg.IToken;

public class Outliner extends DefaultOutliner implements IOutliner
{
    public void createOutlinePresentation(IParseController controller, int offset)
    {
        try
        {
            if (controller != null && tree != null)
            {
    		    tree.setRedraw(false);
    		    tree.removeAll();
    		    SourceFile ast = (SourceFile) controller.getCurrentAst();
                if (ast.package_() != null)
                {
                    TreeItem parent = new TreeItem(tree, SWT.NONE);
            	    parent.setImage(JavaPluginImages.DESC_OBJS_PACKDECL.createImage());
                    parent.setText(ast.package_().toString());
                }
                if (ast != null)
                    createOutlinePresentation(ast.decls());
        		tree.setSelection(new TreeItem[] { tree.getItem(new Point(0, 0)) });
    		}
//    	    selectTreeItemAtTextOffset(offset);
    	}
        catch (Throwable e)
        {
    	    ErrorHandler.reportError("Could not generate outline", e);
    	}
        finally
        {
    	    if (tree != null)
    	        tree.setRedraw(true);
    	}
    }

    public void createOutlinePresentation(List decls)
    {
        for (Iterator i = decls.iterator(); i.hasNext();)
        {
            ClassDecl type = (ClassDecl) i.next();
            createOutlinePresentation(type);
        } 
    }

    public void createOutlinePresentation(ClassDecl type)
    {
        TreeItem parent = new TreeItem(tree, SWT.NONE);
        if (type.flags().isInterface())
             parent.setImage(JavaPluginImages.DESC_OBJS_CFILEINT.createImage());
        else parent.setImage(JavaPluginImages.DESC_OBJS_CFILECLASS.createImage());
        parent.setText(type.name());
        
        ClassBody body = type.body();
    	List members = body.members();
        for (Iterator i = members.iterator(); i.hasNext();)
        {
            Node member = (Node) i.next();
            if (member instanceof ClassDecl)
                createOutlinePresentation(parent, (ClassDecl) member);
            else if (member instanceof ConstructorDecl)
            	createOutlinePresentation(parent, (ConstructorDecl) member);
            else if (member instanceof MethodDecl)
            	createOutlinePresentation(parent, (MethodDecl) member);
            else if (member instanceof FieldDecl)
                createOutlinePresentation(parent, (FieldDecl) member);
        } 
    }

    public void createOutlinePresentation(TreeItem parent, ClassDecl type)
    {
        parent = new TreeItem(parent, SWT.NONE);
        if (type.flags().isInterface())
        {
            if (type.flags().isPrivate())
                parent.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE.createImage());
           else if (type.flags().isProtected())
                parent.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED.createImage());
           else if (type.flags().isPublic())
                parent.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC.createImage());
           else parent.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT.createImage());
        }
        else
        {
            if (type.flags().isPrivate())
                parent.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE.createImage());
           else if (type.flags().isProtected())
                parent.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED.createImage());
           else if (type.flags().isPublic())
                parent.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC.createImage());
           else parent.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT.createImage());
        }
        parent.setText(type.name());
        
        ClassBody body = type.body();
    	List members = body.members();
        for (Iterator i = members.iterator(); i.hasNext();)
        {
            Node member = (Node) i.next();
            if (member instanceof ClassDecl)
                createOutlinePresentation(new TreeItem(parent, SWT.NONE), (ClassDecl) member);
            else if (member instanceof ConstructorDecl)
            	createOutlinePresentation(parent, (ConstructorDecl) member);
            else if (member instanceof MethodDecl)
            	createOutlinePresentation(parent, (MethodDecl) member);
            else if (member instanceof FieldDecl)
                createOutlinePresentation(parent, (FieldDecl) member);
        } 
    }

    public void createOutlinePresentation(TreeItem parent, ConstructorDecl cons)
    {
    	String text = cons.name() + "(";
    	List formals = cons.formals();
        for (Iterator i = formals.iterator(); i.hasNext();)
        {
            Formal formal = (Formal) i.next();
            text += formal.type().toString();
            if (i.hasNext()) text += ", ";
        }
        text += ")";
        TreeItem tree_item = new TreeItem(parent, SWT.NONE);
        if (cons.flags().isPrivate())
             tree_item.setImage(JavaPluginImages.DESC_MISC_PRIVATE.createImage());
        else if (cons.flags().isProtected())
             tree_item.setImage(JavaPluginImages.DESC_MISC_PROTECTED.createImage());
        else if (cons.flags().isPublic())
             tree_item.setImage(JavaPluginImages.DESC_MISC_PUBLIC.createImage());
        else tree_item.setImage(JavaPluginImages.DESC_MISC_DEFAULT.createImage());
        tree_item.setText(text);
	}

    public void createOutlinePresentation(TreeItem parent, MethodDecl method)
    {
    	String text = method.returnType().toString() + " " + method.name() + "(";
    	List formals = method.formals();
        for (Iterator i = formals.iterator(); i.hasNext();)
        {
            Formal formal = (Formal) i.next();
            text += formal.type().toString();
            if (i.hasNext()) text += ", ";
        }
        text += ")";
        
        TreeItem tree_item = new TreeItem(parent, SWT.NONE);
        if (method.flags().isPrivate())
            tree_item.setImage(JavaPluginImages.DESC_MISC_PRIVATE.createImage());
        else if (method.flags().isProtected())
            tree_item.setImage(JavaPluginImages.DESC_MISC_PROTECTED.createImage());
        else if (method.flags().isPublic())
             tree_item.setImage(JavaPluginImages.DESC_MISC_PUBLIC.createImage());
        else tree_item.setImage(JavaPluginImages.DESC_MISC_DEFAULT.createImage());
        tree_item.setText(text);
    }

    public void createOutlinePresentation(TreeItem parent, FieldDecl field)
    {
       	String text = field.name() + " : " + field.type().toString();
        TreeItem tree_item = new TreeItem(parent, SWT.NONE);
        if (field.flags().isPrivate())
             tree_item.setImage(JavaPluginImages.DESC_FIELD_PRIVATE.createImage());
        else if (field.flags().isProtected())
             tree_item.setImage(JavaPluginImages.DESC_FIELD_PROTECTED.createImage());
        else if (field.flags().isPublic())
             tree_item.setImage(JavaPluginImages.DESC_FIELD_PUBLIC.createImage());
        else tree_item.setImage(JavaPluginImages.DESC_FIELD_DEFAULT.createImage());
        tree_item.setText(text);
    }
}