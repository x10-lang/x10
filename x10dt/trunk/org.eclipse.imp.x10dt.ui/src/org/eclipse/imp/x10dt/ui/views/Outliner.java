package org.eclipse.imp.x10dt.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lpg.runtime.IToken;

import org.eclipse.imp.services.IOutliner;
import org.eclipse.imp.services.base.OutlinerBase;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import polyglot.ast.Block;
import polyglot.ast.Call_c;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ext.x10.ast.ArrayConstructor_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.Future_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.X10Loop_c;
import polyglot.types.Flags;
import polyglot.visit.NodeVisitor;
import x10.parser.X10Parser.JPGPosition;

public class Outliner extends OutlinerBase implements IOutliner
{
    public static Image _DESC_ELCL_VIEW_MENU = JavaPluginImages.DESC_ELCL_VIEW_MENU.createImage();

    public static Image _DESC_FIELD_DEFAULT = JavaPluginImages.DESC_FIELD_DEFAULT.createImage();
    public static Image _DESC_FIELD_PRIVATE = JavaPluginImages.DESC_FIELD_PRIVATE.createImage();
    public static Image _DESC_FIELD_PROTECTED = JavaPluginImages.DESC_FIELD_PROTECTED.createImage();
    public static Image _DESC_FIELD_PUBLIC = JavaPluginImages.DESC_FIELD_PUBLIC.createImage();

    public static Image[] FIELD_DESCS= {
	_DESC_FIELD_DEFAULT, _DESC_FIELD_PRIVATE, _DESC_FIELD_PROTECTED, _DESC_FIELD_PUBLIC
    };

    public static Image _DESC_MISC_DEFAULT = JavaPluginImages.DESC_MISC_DEFAULT.createImage();
    public static Image _DESC_MISC_PRIVATE = JavaPluginImages.DESC_MISC_PRIVATE.createImage();
    public static Image _DESC_MISC_PROTECTED = JavaPluginImages.DESC_MISC_PROTECTED.createImage();
    public static Image _DESC_MISC_PUBLIC = JavaPluginImages.DESC_MISC_PUBLIC.createImage();

    public static Image[] MISC_DESCS= {
	_DESC_MISC_DEFAULT, _DESC_MISC_PRIVATE, _DESC_MISC_PROTECTED, _DESC_MISC_PUBLIC
    };

    public static Image _DESC_OBJS_CFILECLASS = JavaPluginImages.DESC_OBJS_CFILECLASS.createImage();
    public static Image _DESC_OBJS_CFILEINT = JavaPluginImages.DESC_OBJS_CFILEINT.createImage();

    public static Image _DESC_OBJS_INNER_CLASS_DEFAULT = JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PRIVATE = JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PROTECTED = JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PUBLIC = JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC.createImage();

    public static Image[] INNER_CLASS_DESCS= {
	_DESC_OBJS_INNER_CLASS_DEFAULT, _DESC_OBJS_INNER_CLASS_PRIVATE, _DESC_OBJS_INNER_CLASS_PROTECTED, _DESC_OBJS_INNER_CLASS_PUBLIC
    };

    public static Image _DESC_OBJS_INNER_INTERFACE_DEFAULT = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PRIVATE = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PROTECTED = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PUBLIC = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC.createImage();

    public static Image[] INNER_INTF_DESCS= {
	_DESC_OBJS_INNER_INTERFACE_DEFAULT, _DESC_OBJS_INNER_INTERFACE_PRIVATE, _DESC_OBJS_INNER_INTERFACE_PROTECTED, _DESC_OBJS_INNER_INTERFACE_PUBLIC
    };

    public static Image _DESC_OBJS_PACKDECL = JavaPluginImages.DESC_OBJS_PACKDECL.createImage();

    private String filter(String name)
    {
        return name.replaceAll("\n", "").replaceAll("\\{amb\\}", "");
    }
    
    @Override
    protected void sendVisitorToAST(Object node) {
	SourceFile ast = (SourceFile) node;
	if (ast != null)
	{
	    tree.removeAll();
	    if (ast.package_() != null)
	    {
		TreeItem parent = new TreeItem(tree, SWT.NONE);
		parent.setData(ast.package_().position());
		parent.setImage(_DESC_OBJS_PACKDECL);
		parent.setText(ast.package_().toString());
	    }
	    outlineTypes(ast.decls());
	}
    }

    public JPGPosition pos(IToken token)
    {
        return new JPGPosition("", token.getPrsStream().getFileName(), token, token);
    }

    public JPGPosition pos(IToken left, IToken right)
    {
        return new JPGPosition("", left.getPrsStream().getFileName(), left, right);
    }

    HashMap tree_item_of;
    HashMap fields_of;

    void outlineTypes(List decls)
    {
        OutlineVisitor v = new OutlineVisitor();
        for (Iterator i = decls.iterator(); i.hasNext();)
        {
            tree_item_of = new HashMap();
            fields_of = new HashMap();
            ClassDecl type = (ClassDecl) i.next();
            type.visit(v);
            
            Set type_set = fields_of.keySet();
            for (Iterator it = type_set.iterator(); it.hasNext();)
            {
                TreeItem parent_item = (TreeItem) it.next();
                ArrayList field_list = (ArrayList) fields_of.get(parent_item);
                if (field_list.size() > 0)
                {
                    TreeItem field_item = new TreeItem(parent_item, SWT.NONE);
                    field_item.setImage(_DESC_ELCL_VIEW_MENU);
                    JPGPosition position = (JPGPosition) parent_item.getData();
                    field_item.setData(position);
                    field_item.setText("member field declarations"); // + (type == null ? ":" : (" of " + type.name())));
                    for (int k = 0; k < field_list.size(); k++)
                        outlineField(field_item, (FieldDecl_c) field_list.get(k));
                }
            }
        }
    }

    private void setImageFromAccessQualifiers(Flags flags, Image[] images, TreeItem tree_item) {
        if (flags.isPrivate())
            tree_item.setImage(images[0]);
        else if (flags.isProtected())
            tree_item.setImage(images[1]);
        else if (flags.isPublic())
             tree_item.setImage(images[2]);
        else tree_item.setImage(images[3]);
    }

    void outlineField(TreeItem parent_item, FieldDecl_c field)
    {
        TreeItem tree_item = new TreeItem(parent_item, SWT.NONE);
        tree_item_of.put(field, tree_item);
        tree_item.setData(field.position());
        setImageFromAccessQualifiers(field.flags(), FIELD_DESCS, tree_item);
        String text = field.name() + " : " + filter(field.type().toString());
        tree_item.setText(text);
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
                         tree_item.setImage(_DESC_OBJS_CFILEINT);
                    else tree_item.setImage(_DESC_OBJS_CFILECLASS);
                    tree_item.setText(type.name());
                }
                else // an inner class of some sort
                {
                    tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                    if (type.flags().isInterface())
                    {
                	setImageFromAccessQualifiers(type.flags(), INNER_INTF_DESCS, tree_item);
                    }
                    else
                    {
                	setImageFromAccessQualifiers(type.flags(), INNER_CLASS_DESCS, tree_item);
                    }
                }

                tree_item_of.put(type, tree_item);
                IToken left_token = ((JPGPosition) type.position()).getLeftIToken();
                IToken right_token;
                // RMF 10/26/2006 - Avoid NPE when encountering a class with no body
                // (can happen when part way through typing in a new class declaration).
                ClassBody body= type.body();
                if (body != null)
                    right_token= ((JPGPosition) body.position()).getLeftIToken().getPrsStream().getTokenAt(((JPGPosition) body.position()).getLeftIToken().getTokenIndex() - 1);
                else
                    right_token= ((JPGPosition) type.position()).getRightIToken();
//              int right_token_index = ((JPGPosition) type.body().position()).getLeftIToken().getTokenIndex() - 1;
                tree_item.setData(pos(left_token, right_token));
                tree_item.setText(type.name());
                fields_of.put(tree_item, new ArrayList());
            }
            else if (n instanceof ConstructorDecl_c)
            {
                ConstructorDecl_c cons = (ConstructorDecl_c) n;
                
                if (!(cons.position() instanceof JPGPosition))
                    return this; // presumably a synthetic ctor; don't put this in the outline

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
                Block body= cons.body();
                IToken left_token = ((JPGPosition) cons.position()).getLeftIToken();
                IToken right_token;
                if (body != null)
                    right_token = left_token.getPrsStream().getIToken(((JPGPosition) body.position()).getLeftIToken().getTokenIndex() - 1);
                else
                    right_token = ((JPGPosition) cons.position()).getRightIToken();
                tree_item.setData(pos(left_token, right_token));
                setImageFromAccessQualifiers(cons.flags(), MISC_DESCS, tree_item);
                tree_item.setText(text);
            }
            else if (n instanceof ArrayConstructor_c)
            {
                ArrayConstructor_c cons = (ArrayConstructor_c) n;
                if (cons.initializer() != null)
                {
                    TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                    tree_item.setImage(_DESC_OBJS_INNER_CLASS_DEFAULT);
                    tree_item_of.put(cons, tree_item);
                    IToken left_token = ((JPGPosition) cons.position()).getLeftIToken();
                    int right_token_index = ((JPGPosition) cons.initializer().position()).getLeftIToken().getTokenIndex() - 1;
                    JPGPosition position = pos(left_token,
                                               left_token.getPrsStream().getIToken(right_token_index));
                    tree_item.setData(position);
                    tree_item.setText(position.toText() + " ...");
                    fields_of.put(tree_item, new ArrayList());
                }
                else tree_item_of.put(n, tree_item_of.get(parent));
//                override(n);
            }
            else if (n instanceof New_c)
            {
                New_c anon = (New_c) n;
                if ((! (parent instanceof ArrayConstructor_c)) && anon.body() != null)
                {
                    TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                    tree_item.setImage(_DESC_OBJS_INNER_CLASS_DEFAULT);
                    tree_item_of.put(anon, tree_item);
                    IToken left_token = ((JPGPosition) anon.position()).getLeftIToken();
                    int right_token_index = ((JPGPosition) anon.body().position()).getLeftIToken().getTokenIndex() - 1;
                    JPGPosition position = pos(left_token, left_token.getPrsStream().getIToken(right_token_index));
                    tree_item.setData(position);
                    tree_item.setText("new " +
                                      filter(anon.objectType().toString()) +
                                      "() {...}");
                    fields_of.put(tree_item, new ArrayList());
                }
                else tree_item_of.put(n, tree_item_of.get(parent));
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
                IToken left_token = ((JPGPosition) method.position()).getLeftIToken();
                int right_token_index =
                    (method.body() == null
                         ? ((JPGPosition) method.position()).getRightIToken().getTokenIndex()
                         : ((JPGPosition) method.body().position()).getLeftIToken().getTokenIndex() - 1);
                tree_item.setData(pos(left_token, left_token.getPrsStream().getIToken(right_token_index)));
                setImageFromAccessQualifiers(method.flags(), MISC_DESCS, tree_item);
                tree_item.setText(text);
            }
            else if (n instanceof FieldDecl_c)
            {
                TreeItem parent_item = (TreeItem) tree_item_of.get(parent);
                ArrayList field_list = (ArrayList) fields_of.get(parent_item);
                assert(field_list != null);
                field_list.add(n);
                tree_item_of.put(n, tree_item_of.get(parent));
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
                IToken left_token = ((JPGPosition) loop.position()).getLeftIToken();
                int right_token_index = ((JPGPosition) loop.body().position()).getLeftIToken().getTokenIndex() - 1;
                tree_item.setData(pos(left_token, left_token.getPrsStream().getIToken(right_token_index)));
                tree_item.setImage(_DESC_MISC_PUBLIC);
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
                IToken left_token = ((JPGPosition) loop.position()).getLeftIToken();
                int right_token_index = ((JPGPosition) loop.body().position()).getLeftIToken().getTokenIndex() - 1;
                tree_item.setData(pos(left_token, left_token.getPrsStream().getIToken(right_token_index)));
                tree_item.setImage(_DESC_MISC_PUBLIC);
                tree_item.setText(text);
            }
            else if (n instanceof For_c)
            {
                For_c loop = (For_c) n;
                
                String text = "for (";
                for (int i = 0; i < loop.inits().size(); i++)
                {
                    text += filter(((JPGPosition) ((Node) loop.inits().get(i)).position()).toText());
                    text += (i < loop.inits().size() - 1 ? ", " : ";");
                }
                text += " ... )";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(loop, tree_item);
                IToken left_token = ((JPGPosition) loop.position()).getLeftIToken();
                int right_token_index = ((JPGPosition) loop.body().position()).getLeftIToken().getTokenIndex() - 1;
                tree_item.setData(pos(left_token, left_token.getPrsStream().getIToken(right_token_index)));
                tree_item.setImage(_DESC_MISC_PUBLIC);
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
                IToken left_token = ((JPGPosition) loop.position()).getLeftIToken();
                int right_token_index = ((JPGPosition) loop.body().position()).getLeftIToken().getTokenIndex() - 1;
                tree_item.setData(pos(left_token, left_token.getPrsStream().getIToken(right_token_index)));
                tree_item.setImage(_DESC_MISC_PUBLIC);
                tree_item.setText(text);
            }
            else if (n instanceof Finish_c)
            {
                Finish_c finish = (Finish_c) n;
                
                String text = "finish";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(finish, tree_item);
                tree_item.setData(pos(((JPGPosition) finish.position()).getLeftIToken()));
                tree_item.setImage(_DESC_MISC_PRIVATE);
                tree_item.setText(text);
            }
            else if (n instanceof Atomic_c)
            {
                Atomic_c atomic = (Atomic_c) n;
                
                String text = "atomic";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(atomic, tree_item);
                tree_item.setData(pos(((JPGPosition) atomic.position()).getLeftIToken()));
                tree_item.setImage(_DESC_MISC_PRIVATE);
                tree_item.setText(text);
            }
            else if (n instanceof Next_c)
            {
                Next_c next = (Next_c) n;
                
                String text = "next";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(next, tree_item);
                tree_item.setData(pos(((JPGPosition) next.position()).getLeftIToken()));
                tree_item.setImage(_DESC_MISC_PROTECTED);
                tree_item.setText(text);
            }
            else if (n instanceof Async_c)
            {
                Async_c async = (Async_c) n;
                
                String text = "async";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(async, tree_item);
                tree_item.setData(pos(((JPGPosition) async.position()).getLeftIToken()));
                tree_item.setImage(_DESC_MISC_DEFAULT);
                tree_item.setText(text);
            }
            
            else if (n instanceof Future_c)
            {
                Future_c future = (Future_c) n;
                
                String text = "future";
                TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                tree_item_of.put(future, tree_item);
                tree_item.setData(future.position());
                tree_item.setImage(_DESC_MISC_DEFAULT);
                tree_item.setText(text);
            }
            
            else if (n instanceof Call_c)
            {
                Call_c call = (Call_c) n;
                if (call.name().equals("force") && call.arguments().size() == 0)
                {
                    String text = "force()";
                    TreeItem tree_item = new TreeItem((TreeItem) tree_item_of.get(parent), SWT.NONE);
                    IToken right_most_token = ((JPGPosition) call.position()).getRightIToken();
                    int right_token_index = right_most_token.getTokenIndex() - 2;
                    tree_item_of.put(call, tree_item);
                    tree_item.setData(pos(right_most_token.getPrsStream().getIToken(right_token_index)));
                    tree_item.setImage(_DESC_MISC_PROTECTED);
                    tree_item.setText(text);
                }
                else tree_item_of.put(n, tree_item_of.get(parent));
            }
            else // keep pushing the info down 
            {
                tree_item_of.put(n, tree_item_of.get(parent));
            }
            return this;
        }
    }
}
