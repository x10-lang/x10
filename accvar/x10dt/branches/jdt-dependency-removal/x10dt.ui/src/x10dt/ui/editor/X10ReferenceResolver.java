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

package x10dt.ui.editor;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IReferenceResolver;

import polyglot.ast.Ambiguous;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.visit.NodeVisitor;
import x10.types.MethodInstance;
import x10dt.ui.parser.PolyglotNodeLocator;

public class X10ReferenceResolver implements IReferenceResolver, ILanguageService {
    /**
     * Get the target for a given referencing source node in the AST represented by a given ParseController.
     */
    public Object getLinkTarget(Object node, IParseController parseController) {
        if (node instanceof Id) {
            Id id= (Id) node;
            Node parent= findParent(id, parseController);

            if (parent instanceof Assign) {
            	// Assignment nodes are odd: they don't generally have an Expr as their LHS
            	// (that's why the left() method takes a NodeFactory argument - it produces the
            	// LHS expr on demand, but only if asked). So, the parent of an Id might actually
            	// be a FieldAssign or LocalAssign. Check for that.
				if (parent instanceof FieldAssign) {
					FieldAssign fa = (FieldAssign) parent;

					return fa.fieldInstance().def();
				} else if (parent instanceof LocalAssign) { // Not sure we'd ever see this as a direct parent of an Id...
					LocalAssign la = (LocalAssign) parent;

					node= la.local();
				} else if (parent instanceof ArrayAccessAssign) { // Not sure we'd ever see this as a direct parent of an Id...
					ArrayAccessAssign aa = (ArrayAccessAssign) parent;

					node= aa.array();
				}
            } else {
            	node= parent;
            }
        }
        if (node instanceof Ambiguous) {
            return null;
        }
        
        if (node instanceof TypeNode) {
          Node grandparent = findParent((Node)node, parseController);
          if (grandparent instanceof ConstructorDecl) { //MV
              node=grandparent;
              //return node;
          }
        }
        	
        
//        if (node instanceof TypeNode) {
//            Object grandparent = findParent((Node)node, parseController);
//            if (grandparent instanceof ConstructorDecl) { //MV
//                node=grandparent;
//            }
//            
//            else if (grandparent instanceof ClassDecl)
//            {
//	            if(((TypeNode)node).type().toString().equals("x10.lang.Object"))
//	            {
//	            	node=grandparent;
//	            }
//	            
//	            else
//	            {
//	            	
//	            }
//            }
//        }
        if (node instanceof TypeNode) {
            TypeNode typeNode= (TypeNode) node;
            PolyglotNodeLocator locator= (PolyglotNodeLocator) parseController.getSourcePositionLocator();
            Node parent= (Node) locator.findParentNode(parseController.getCurrentAst(), typeNode.position().offset(), typeNode.position().endOffset());

            if (parent instanceof New) {
                New n= (New) parent;
                return n.constructorInstance().def();
            }
            return typeNode.type();
        } else if (node instanceof Call) {
            Call call= (Call) node;
            MethodInstance mi= call.methodInstance();
            if (mi != null) {
                return mi.def();  //PORT1.7mi.declaration() -> mi.def();
            }
        } else if (node instanceof Field) {
            Field field= (Field) node;
            FieldInstance fi= field.fieldInstance();
            if (fi != null)
                return fi.def();   //PORT1.7 fi.declaration-> fi.def();
        } else if (node instanceof Local) {
            Local local= (Local) node;
            LocalInstance li= local.localInstance();
            if (li != null)
                return li.def();  //PORT1.7 li.declaration() -> li.def();
        } else if (node instanceof Formal) {
			Formal formal = (Formal) node;
			return formal.localDef();
        }
        return null;
    }

    private Node findParent(Node node, IParseController parseController) {
        PolyglotNodeLocator locator= (PolyglotNodeLocator) parseController.getSourcePositionLocator();

        return (Node) locator.findParentNode(parseController.getCurrentAst(), node.position().offset(), node.position().endOffset());
    }

    public static Node findVarDefinition(Local local, Node ast) {
        final LocalInstance li= local.localInstance();
        final LocalDecl ld[]= new LocalDecl[1];
        NodeVisitor finder= new NodeVisitor() {
            public NodeVisitor enter(Node n) {
                if (n instanceof LocalDecl) {
                    LocalDecl thisLD= (LocalDecl) n;
                    LocalInstance tli = thisLD.localDef().asInstance();//PORT1.7 thisLD.localInstance()->thisLD.localDef().asInstance()
                    if (tli == li)//PORT1.7 was thisLD.localInstance()
                        ld[0]= thisLD;
                }
                return super.enter(n);
            }
        };
        ast.visit(finder);
        return ld[0];
    }

    /**
     * Get the text associated with a given node for use in a link from (or to) that node
     */
    public String getLinkText(Object node) {
        // In the original, link text is determined by ISourceHyperlink,
        // and this is all that does
        return node.toString();
    }
}
