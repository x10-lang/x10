package x10.uide.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.uide.core.ErrorHandler;
import org.eclipse.uide.defaults.DefaultOutliner;
import org.eclipse.uide.editor.IOutliner;
import org.eclipse.uide.parser.IParseController;

import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ext.jl.ast.ClassDecl_c;
import polyglot.ext.jl.ast.ConstructorDecl_c;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.jl.ast.FieldDecl_c;
import polyglot.ext.jl.ast.MethodDecl_c;
import polyglot.ext.jl.ast.New_c;
import polyglot.ext.x10.ast.*;
import polyglot.visit.NodeVisitor;
import x10.parser.X10Parser.JPGPosition;
import com.ibm.lpg.IToken;

public class Outliner extends DefaultOutliner implements IOutliner
{
    private IParseController controller;

    private String filter(String name)
    {
        return name.replaceAll("\n", "").replaceFirst("\\{amb\\}", "");
    }
    
    public void setTree(Tree tree)
    {
        super.setTree(tree);
        tree.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e)
            {
                TreeItem ti = (TreeItem) e.item;
                Object data = ti.getData();

                if (data instanceof Node) {
                    JPGPosition position = (JPGPosition) ((Node) ti.getData()).position();
                    IToken left_token = position.getLeftIToken();

                    IEditorPart activeEditor = PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage()
                            .getActiveEditor();
                    AbstractTextEditor textEditor = (AbstractTextEditor) activeEditor;

                    textEditor.selectAndReveal(left_token.getStartOffset(), left_token.getEndOffset() - left_token.getStartOffset() + 1);
                    // textEditor.setFocus();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });
    }

    public void createOutlinePresentation(IParseController controller, int offset)
    {
        this.controller = controller;
        try
        {
            if (controller != null && tree != null)
            {
    		    tree.setRedraw(false);
    		    SourceFile ast = (SourceFile) controller.getCurrentAst();
                if (ast != null)
                {
                    tree.removeAll();
                    if (ast.package_() != null)
                    {
                        TreeItem parent = new TreeItem(tree, SWT.NONE);
                        parent.setData(ast.package_());
                	    parent.setImage(JavaPluginImages.DESC_OBJS_PACKDECL.createImage());
                        parent.setText(ast.package_().toString());
                    }
                    outlineTypes(ast.decls());
                }
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

    HashMap tree_item_of;
    void outlineTypes(List decls)
    {
        OutlineVisitor v = new OutlineVisitor();
        for (Iterator i = decls.iterator(); i.hasNext();)
        {
            tree_item_of = new HashMap();
            ClassDecl type = (ClassDecl) i.next();
            type.visit(v);
        }
    }
    
    class OutlineVisitor extends NodeVisitor
    {
        public NodeVisitor enter(Node parent, Node n)
        {
            if (n instanceof ClassDecl_c)
            {
                ClassDecl_c type = (ClassDecl_c) n;

                TreeItem tree_item;
                if (parent == null) // top-level type declaration
                {
                    tree_item = new TreeItem(tree, SWT.NONE);
                    if (type.flags().isInterface())
                         tree_item.setImage(JavaPluginImages.DESC_OBJS_CFILEINT.createImage());
                    else tree_item.setImage(JavaPluginImages.DESC_OBJS_CFILECLASS.createImage());
                    tree_item.setText(type.name());
                }
                else // an inner class of some sort
                {
                    tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                    if (type.flags().isInterface())
                    {
                        if (type.flags().isPrivate())
                             tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE.createImage());
                        else if (type.flags().isProtected())
                             tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED.createImage());
                        else if (type.flags().isPublic())
                             tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC.createImage());
                        else tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT.createImage());
                    }
                    else
                    {
                        if (type.flags().isPrivate())
                             tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE.createImage());
                        else if (type.flags().isProtected())
                             tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED.createImage());
                        else if (type.flags().isPublic())
                             tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC.createImage());
                        else tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT.createImage());
                    }
                }

                tree_item_of.put(type, tree_item);
                tree_item.setData(type);
                tree_item.setText(type.name());
            }
            else if (n instanceof ConstructorDecl_c)
            {
                ConstructorDecl_c cons = (ConstructorDecl_c) n;
                
                String text = cons.name() + "(";
                List formals = cons.formals();
                for (Iterator i = formals.iterator(); i.hasNext();)
                {
                    Formal formal = (Formal) i.next();
                    text += filter(formal.type().toString());
                    if (i.hasNext()) text += ", ";
                }
                text += ")";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(cons, tree_item);
                tree_item.setData(cons);
                if (cons.flags().isPrivate())
                     tree_item.setImage(JavaPluginImages.DESC_MISC_PRIVATE.createImage());
                else if (cons.flags().isProtected())
                     tree_item.setImage(JavaPluginImages.DESC_MISC_PROTECTED.createImage());
                else if (cons.flags().isPublic())
                     tree_item.setImage(JavaPluginImages.DESC_MISC_PUBLIC.createImage());
                else tree_item.setImage(JavaPluginImages.DESC_MISC_DEFAULT.createImage());
                tree_item.setText(text);
            }
            else if (n instanceof New_c)
            {
                New_c anon = (New_c) n;
                if (anon.body() != null)
                {
                    TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                    tree_item.setImage(JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT.createImage());
                    tree_item_of.put(anon, tree_item);
                    tree_item.setData(anon);
                    tree_item.setText("new " +
                                      filter(anon.objectType().toString()) +
                                      "() {...}");
                }
            }
           else if (n instanceof MethodDecl_c)
            {
                MethodDecl_c method = (MethodDecl_c) n;

                String text = filter(method.returnType().toString()) + " " + method.name() + "(";
                List formals = method.formals();
                for (Iterator i = formals.iterator(); i.hasNext();)
                {
                    Formal formal = (Formal) i.next();
                    text += filter(formal.type().toString());
                    if (i.hasNext()) text += ", ";
                }
                text += ")";
                
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(method, tree_item);
                tree_item.setData(method);
                if (method.flags().isPrivate())
                     tree_item.setImage(JavaPluginImages.DESC_MISC_PRIVATE.createImage());
                else if (method.flags().isProtected())
                     tree_item.setImage(JavaPluginImages.DESC_MISC_PROTECTED.createImage());
                else if (method.flags().isPublic())
                     tree_item.setImage(JavaPluginImages.DESC_MISC_PUBLIC.createImage());
                else tree_item.setImage(JavaPluginImages.DESC_MISC_DEFAULT.createImage());
                     tree_item.setText(text);
            }
            else if (n instanceof FieldDecl_c)
            {
                FieldDecl_c field = (FieldDecl_c) n;
                
                String text = field.name() + " : " + filter(field.type().toString());
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(field, tree_item);
                tree_item.setData(field);
                if (field.flags().isPrivate())
                    tree_item.setImage(JavaPluginImages.DESC_FIELD_PRIVATE.createImage());
                else if (field.flags().isProtected())
                    tree_item.setImage(JavaPluginImages.DESC_FIELD_PROTECTED.createImage());
                else if (field.flags().isPublic())
                     tree_item.setImage(JavaPluginImages.DESC_FIELD_PUBLIC.createImage());
                else tree_item.setImage(JavaPluginImages.DESC_FIELD_DEFAULT.createImage());
                tree_item.setText(text);
            }
            //
            //
            //
            else if (n instanceof AtEach_c)
            {
                AtEach_c loop = (AtEach_c) n;
                
                String text = "ateach (" + 
                              filter(((JPGPosition) loop.formal().position()).toText()) +
                              " : " +
                              filter(((JPGPosition) loop.domain().position()).toText()) +
                              ")";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(loop, tree_item);
                tree_item.setData(loop);
                tree_item.setImage(JavaPluginImages.DESC_MISC_PUBLIC.createImage());
                tree_item.setText(text);
            }
            else if (n instanceof ForEach_c)
            {
                ForEach_c loop = (ForEach_c) n;
                
                String text = "foreach (" +
                              filter(((JPGPosition) loop.formal().position()).toText()) +
                              " : " +
                              filter(((JPGPosition) loop.domain().position()).toText()) +
                              ")";

                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(loop, tree_item);
                tree_item.setData(loop);
                tree_item.setImage(JavaPluginImages.DESC_MISC_PUBLIC.createImage());
                tree_item.setText(text);
            }
            else if (n instanceof X10Loop_c)
            {
                X10Loop_c loop = (X10Loop_c) n;
                
                String text = "for (" +
                              filter(((JPGPosition) loop.formal().position()).toText()) +
                              " : " +
                              filter(((JPGPosition) loop.domain().position()).toText()) +
                              ")";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(loop, tree_item);
                tree_item.setData(loop);
                tree_item.setImage(JavaPluginImages.DESC_MISC_PUBLIC.createImage());
                tree_item.setText(text);
            }
            else if (n instanceof Finish_c)
            {
                Finish_c finish = (Finish_c) n;
                
                String text = "finish";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(finish, tree_item);
                tree_item.setData(finish);
                tree_item.setImage(JavaPluginImages.DESC_MISC_PRIVATE.createImage());
                tree_item.setText(text);
            }
            else if (n instanceof Next_c)
            {
                Next_c next = (Next_c) n;
                
                String text = "next";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(next, tree_item);
                tree_item.setData(next);
                tree_item.setImage(JavaPluginImages.DESC_MISC_PROTECTED.createImage());
                tree_item.setText(text);
            }
            else
            {
                tree_item_of.put(n, tree_item_of.get(parent));
            }
            return this;
        }
    }
}