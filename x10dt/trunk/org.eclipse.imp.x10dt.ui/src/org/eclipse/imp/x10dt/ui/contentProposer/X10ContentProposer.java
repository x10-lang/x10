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
package org.eclipse.imp.x10dt.ui.contentProposer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.imp.editor.SourceProposal;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.IContentProposer;
import org.eclipse.imp.x10dt.ui.parser.PolyglotNodeLocator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateProposal;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Unary;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ObjectType;
import polyglot.types.Qualifier;
import polyglot.types.ReferenceType;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import x10.parser.X10Parsersym;

public class X10ContentProposer implements IContentProposer, X10Parsersym {
    private void filterFields(List<FieldInstance> fields, List<FieldInstance> in_fields, String prefix) {
        for(FieldInstance f: in_fields) {
            String name= f.name().toString(); // PORT1.7 was f.name()
            String tempName = f.toString();  // PORT1.7 or is this preferable?
            if (name.startsWith(prefix))
                fields.add(f);
        }
    }

    private void filterMethods(List<MethodInstance> methods, List<MethodInstance> in_methods, String prefix) {
        for(MethodInstance m: in_methods) {
            String name= m.name().toString();   // PORT 1.7
            if (name.startsWith(prefix))
                methods.add(m);
        }
    }

    private void filterClasses(List<ClassType> classes, List<ClassType> in_classes, String prefix) {//PORT1.7 2nd arg was List<ReferenceType>
        for(ReferenceType r: in_classes) {
            ClassType c= r.toClass();
            String name= c.name().toString();  // PORT1.7 name() replaced with name().toString()
            if (name.startsWith(prefix)) {
                classes.add(c);
            }
        }
    }
    // PORT1.7  this should probably take a Type instead of a ReferenceType arg.  cast to StructType or ObjectType to get at what we need now
    private void getFieldCandidates(Type container_type, List<FieldInstance> fields, String prefix) {// PORT1.7 ReferenceType replaced with Type
		if (container_type == null)
			return;

		if (container_type instanceof ObjectType) {
			ObjectType oType = (ObjectType) container_type;

			filterFields(fields, oType.fields(), prefix); // PORT1.7 ReferenceType.fields()->ObjectType.fields()

			for (int i = 0; i < oType.interfaces().size(); i++) {
				ObjectType interf = (ObjectType) oType.interfaces().get(i);
				filterFields(fields, interf.fields(), prefix);
			}

			getFieldCandidates(oType.superClass(), fields, prefix);
		}
	}

    private void getMethodCandidates(ObjectType container_type, List<MethodInstance> methods, String prefix) {//PORT1.7 ReferenceType->ObjectType
        if (container_type == null)
            return;

        filterMethods(methods, container_type.methods(), prefix);

        getMethodCandidates((ObjectType)container_type.superClass(), methods, prefix);//PORT1.7 superType()->superClass()
    }

    private void getClassCandidates(Type type, List<ClassType> classes, String prefix) {
        if (type == null)
            return;
        ClassType container_type= type.toClass();
        if (container_type == null)
            return;

        filterClasses(classes, container_type.memberClasses(), prefix);

        for(int i= 0; i < container_type.interfaces().size(); i++) {
            ClassType interf= ((ReferenceType) container_type.interfaces().get(i)).toClass();
            filterClasses(classes, interf.memberClasses(), prefix);
        }

        getClassCandidates(container_type.superClass(), classes, prefix);//PORT1.7 superType)_->superClass()
    }

    private void getCandidates(ObjectType container_type, List<ICompletionProposal> list, String prefix, int offset) {//PORT1.7 RefType->ObjectType
        List<FieldInstance> fields= new ArrayList<FieldInstance>();
        List<MethodInstance> methods= new ArrayList<MethodInstance>();
        List<ClassType> classes= new ArrayList<ClassType>();

        getFieldCandidates(container_type, fields, prefix);
        getMethodCandidates(container_type, methods, prefix);
        getClassCandidates((Type) container_type, classes, prefix);

        for(FieldInstance field : fields) {
            list.add(new SourceProposal(field.name().toString(), prefix, offset)); //PORT1.7 name() replaced with name().toString()
        }

        for(MethodInstance method : methods) {
            list.add(new SourceProposal(method.signature(), prefix, offset));
        }

        for(ClassType type : classes) {
            if (!type.isAnonymous())
                list.add(new SourceProposal(type.name().toString(), prefix, offset));//PORT1.7 name() replaced with name().toString()
        }
    }

    private static final String CONTEXT_ID= "x10Source";

    private TemplateContextType fContextType= new TemplateContextType(CONTEXT_ID, "Coding Templates");

    private final Template fRegion1DTemplate= new Template("1-D region", "X10 1-dimensional region creation", CONTEXT_ID, "[${lower}:${upper}]", false);
    private final Template fRegion2DTemplate= new Template("2-D region", "X10 2-dimensional region creation", CONTEXT_ID, "[${lower1}:${upper1},${lower2}:${upper2}]", false);
    private final Template fRegion3DTemplate= new Template("3-D region", "X10 3-dimensional region creation", CONTEXT_ID, "[${lower1}:${upper1},${lower2}:${upper2},${lower3}:${upper3}]", false);
//  private final Template fDistribTemplate= new Template("distribution", "X10 distribution creation", CONTEXT_ID, "async (${place}) { }", false);
    private final Template fArrayNewTemplate= new Template("array new", "X10 array instantiation", CONTEXT_ID, "new ${type}[] (point ${p}) { return ${expr}; }", false);
    private final Template fAsyncTemplate= new Template("async", "async statement", CONTEXT_ID, "async (${place}) {\n \n}\n", false);
    private final Template fAtEachTemplate= new Template("ateach", "ateach statement", CONTEXT_ID, "ateach (point ${p}: ${region}) {\n \n}\n", false);
    private final Template fAtomicTemplate= new Template("atomic", "atomic statement", CONTEXT_ID, "atomic { ${stmt} }", false);
    private final Template fForRegionTemplate= new Template("for region", "for iterating over a region", CONTEXT_ID, "for (point ${p}: ${region}) {\n \n}\n", false);
    private final Template fForEachTemplate= new Template("foreach", "foreach statement", CONTEXT_ID, "foreach (point ${p}: ${region}) {\n\n}\n", false);
    private final Template fFutureTemplate= new Template("future", "future expression", CONTEXT_ID, "future (${place}) { ${expr} }.force()", false);

    private final Template[] fTemplates= new Template[] {
            fRegion1DTemplate, fRegion2DTemplate, fRegion3DTemplate, fArrayNewTemplate, fAsyncTemplate,
            fAtEachTemplate, fAtomicTemplate, fForRegionTemplate, fForEachTemplate, fFutureTemplate
    };

    public ICompletionProposal[] getContentProposals(IParseController controller, int offset, ITextViewer viewer) {
        ArrayList<ICompletionProposal> list= new ArrayList<ICompletionProposal>();
        //
        // When the offset is in between two tokens (for example, on a white space or comment)
        // the getTokenIndexAtCharacter in parse stream returns the negative index
        // of the token preceding the offset. Here, we adjust this index to point instead
        // to the token following the offset.
        //
        // Note that the controller also has a getTokenIndexAtCharacter and
        // getTokenAtCharacter. However, these methods won't work here because
        // controller.getTokenAtCharacter(offset) returns null when the offset
        // is not the offset of a valid token; and controller.getTokenAtCharacter(offset) returns
        // the index of the token preceding the offset when the offset is not
        // the offset of a valid token.
        //
        // PORT1.7 --  token use here, calculation OK for now (does not use getLeftToken() etc)
        IPrsStream prs_stream= ((SimpleLPGParseController) controller).getParser().getParseStream();
        int index= prs_stream.getTokenIndexAtCharacter(offset), token_index= (index < 0 ? -index : index);
        IToken tokenToComplete= prs_stream.getIToken(token_index), contextToken= prs_stream.getIToken(token_index - 1);
        SimpleLPGParseController lpgPC= (SimpleLPGParseController) controller;

        if (offset == tokenToComplete.getStartOffset()) { // If we're at the beginning of the tokenToComplete, back up
                                                            // one token
            tokenToComplete= prs_stream.getIToken(tokenToComplete.getTokenIndex() - 1);
            contextToken= prs_stream.getIToken(tokenToComplete.getTokenIndex() - 1);
        }

        //
        // If we are at an offset position immediately following an "identifier"
        // candidate, then consider the contextToken to be the token to complete
        // and choose its predecessor as the context for the lookup.
        //

        if ((contextToken.getKind() == TK_IDENTIFIER || lpgPC.isKeyword(contextToken.getKind())) && offset == contextToken.getEndOffset() + 1
                && prs_stream.getIToken(contextToken.getTokenIndex() - 1).getKind() == TK_DOT) {
            contextToken= prs_stream.getIToken(prs_stream.getPrevious(contextToken.getTokenIndex()));
        } else if (tokenToComplete.getKind() == TK_IDENTIFIER && contextToken.getKind() == TK_DOT) {
            contextToken= prs_stream.getIToken(contextToken.getTokenIndex() - 1);
        } else if (tokenToComplete.getKind() == TK_DOT) {
            contextToken= prs_stream.getIToken(tokenToComplete.getTokenIndex() - 1);
        }

        String prefix= computePrefixOfToken(tokenToComplete, offset, lpgPC);

        /*
         * list.add(new SourceProposal("Offset: " + offset, "", offset)); list.add(new SourceProposal("Token start
         * offset: " + token.getStartOffset(), "", offset)); list.add(new SourceProposal("Token end offset: " +
         * token.getEndOffset(), "", offset)); list.add(new SourceProposal("Prefix: \"" + (prefix == null ? "" : prefix) +
         * "\"", "", offset)); list.add(new SourceProposal("Candidate start offset: " + candidate.getStartOffset(), "",
         * offset)); list.add(new SourceProposal("Candidate end offset: " + candidate.getEndOffset(), "", offset));
         * list.add(new SourceProposal("Token: " + token, "", offset)); list.add(new SourceProposal("Candidate: " +
         * candidate, "", offset));
         */
        PolyglotNodeLocator locator= new PolyglotNodeLocator(controller.getProject(), ((SimpleLPGParseController) controller).getLexer().getLexStream());
        Node currentAst= (Node) controller.getCurrentAst();
        Node node= (Node) locator.findNode(currentAst, contextToken.getStartOffset(), contextToken.getEndOffset()); // offset);
        //
        // We execute this code when we encounter a qualified name x.foo,
        // where the left-hand side x of the qualified name may be either
        // an object reference (declared field or local declaration) or a
        // type. Note that x itself may be a qualified name.
        // 
        if (node instanceof Field) {
            // list.add(new SourceProposal("Field: " + node.getClass().toString(), " source proposal ", 0));
            Field field= (Field) node;
            if (contextToken.getKind() == TK_DOT && field.target().type().isReference() /* instanceof ReferenceType */)
                getCandidates((ObjectType) field.target().type(), list, prefix, offset);//PORT1.7 RefType->ObjectType. can we guarantee it's an ObjectType?
            else
                list.add(new SourceProposal("no info available", " source proposal ", 0));
        } else if (node instanceof CanonicalTypeNode) {
        	CanonicalTypeNode ctNode=(CanonicalTypeNode)node;
            Qualifier qualifier= ctNode.qualifierRef().get();//PORT1.7 qualifier()->qualifierRef().get();
            Type qtype = ctNode.type();
            if (qualifier.isType()) {
                Type qualType= (Type) qualifier;
                if (qualType.isReference()) {
                    getCandidates((ObjectType) qualType, list, prefix, offset);//PORT1.7 RefType->ObjectType. can we guarantee it's an ObjectType?
                }
            }
        } else if (node instanceof Assign) {
            list.add(new SourceProposal("Assign: " + node.getClass().toString(), " source proposal ", 0));
            list.add(new SourceProposal("complete prefix " + prefix, " source proposal ", 0));
            if (prefix != null && prefix.length() > 0) {
                // TODO: Any package, type, local variable and accessible class members
            } else
                list.add(new SourceProposal("no info available", " source proposal ", 0));
        } else if (node instanceof FieldDecl) {
            list.add(new SourceProposal("FieldDecl: " + node.getClass().toString(), " source proposal ", 0));
            list.add(new SourceProposal("complete prefix " + prefix, " source proposal ", 0));
            if (prefix != null && prefix.length() > 0) {
                // TODO: Any package, type, local variable and accessible class members
                list.add(new SourceProposal("complete prefix " + prefix, " source proposal ", 0));
            } else if (contextToken.getKind() == TK_EQUAL) {
                // TODO: Any package, type, local variable and accessible class members
            } else
                list.add(new SourceProposal("no info available", " source proposal ", 0));
        } else if (node instanceof Call) {
            Call call= (Call) node;
            if (contextToken.getKind() == TK_DOT && call.target().type() != null && call.target().type().isReference())
                getCandidates((ObjectType) call.target().type(), list, prefix, offset);//PORT1.7 RefType->ObjectType. can we guarantee it's an ObjectType?
        } else {
            // TODO: Any package, type, local variable and accessible class members
            if (node instanceof Binary) {
                list.add(new SourceProposal("BINARY: " + node.getClass().toString(), " source proposal ", 0));
            } else if (node instanceof Unary) {
                list.add(new SourceProposal("UNARY: " + node.getClass().toString(), " source proposal ", 0));
            } else if (node instanceof Id) {
                if (tokenToComplete.getKind() == TK_DOT || prs_stream.getIToken(tokenToComplete.getTokenIndex() - 1).getKind() == TK_DOT) {
                    // node is the left-hand side of the dot operator
                    Node parent= (Node) locator.findParentNode(currentAst, tokenToComplete.getStartOffset());
                    if (parent instanceof Call) {
                        Call call= (Call) parent;
                        if (call.target().type() != null && call.target().type().isReference())
                            getCandidates((ObjectType) call.target().type(), list, prefix, offset);//PORT1.7 RefType->ObjectType. can we guarantee it's an ObjectType?
                    }
                } else {
                    // Possibilities: prefix of a package, local variable, parameter, field, or type name
                    addNamesInScope(currentAst, (Id) node, prefix, list, offset);
                }
            } else {
                addTemplateProposals(offset, viewer, list, prefix);
            }
        }
        return (ICompletionProposal[]) list.toArray(new ICompletionProposal[list.size()]);
    }

    private void addTemplateProposals(int offset, ITextViewer viewer, ArrayList<ICompletionProposal> list, String prefix) {
        IDocument doc= viewer.getDocument();
        Region r= new Region(offset, prefix.length());
        TemplateContext tc= new DocumentTemplateContext(fContextType, doc, offset - prefix.length(), prefix.length());

        for(int i= 0; i < fTemplates.length; i++) {
            addTemplateProposalIfMatch(list, fTemplates[i], tc, r, prefix);
        }
    }

    private String computePrefixOfToken(IToken tokenToComplete, int offset, SimpleLPGParseController lpgPC) {
        String prefix= "";
        if (tokenToComplete.getKind() == TK_IDENTIFIER || tokenToComplete.getKind() == TK_ErrorId || lpgPC.isKeyword(tokenToComplete.getKind())) {
            if (offset >= tokenToComplete.getStartOffset() && offset <= tokenToComplete.getEndOffset() + 1) {
                prefix= tokenToComplete.toString().substring(0, offset - tokenToComplete.getStartOffset());
            }
        }
        return prefix;
    }

    private void addNamesInScope(Node currentAst, Id id, String prefix, List<ICompletionProposal> proposals, int offset) {
        // Polyglot can't supply the pkg/class names, so we'll have to appeal to the search index
        if ("this".startsWith(prefix)) // Should check that we're not in a static method or initializer
            proposals.add(new SourceProposal("this", prefix, offset));
        final Stack<Node> path= new Stack<Node>();
        path.push(id);
        currentAst.visit(new NodeVisitor() {
            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (old == path.peek() && parent != null) {
                    path.push(parent);
                }
                return super.leave(parent, old, n, v);
            }
        });
        int idOffset= id.position().offset();
        for(Node node : path) {
            if (node instanceof MethodDecl) {
                MethodDecl md= (MethodDecl) node;
                List<Formal> formals= md.formals();
                for(Formal formal : formals) {
                	String fname = formal.name().id().toString(); //PORT1.7 name() changed to name().id().toString(); cached here for reuse
                    if (fname.startsWith(prefix)) { //PORT1.7 name() changed to name().id().toString()
                        proposals.add(new SourceProposal(fname, fname, prefix, offset)); //PORT1.7 use cached value
                    }
                }
                break; // Hack: don't bother including variables from surrounding types
            }
            if (node instanceof Block) {
                Block b= (Block) node;
                List<Stmt> stmts= b.statements();
                for(Stmt s : stmts) {
                    if (s.position().offset() > idOffset) {
                        // Don't include declarations that follow the current cursor pos
                        break;
                    }
                    if (s instanceof LocalDecl) {
                        LocalDecl decl= (LocalDecl) s;
                        String ldname = decl.name().id().toString();// PORT1.7 changed name() to name().id().toString(); cached
                        if (ldname.startsWith(prefix)) { // PORT1.7 changed name() to name().id().toString(); cached
                            proposals.add(new SourceProposal(ldname, ldname, prefix, offset)); //PORT1.7 use cached value
                        }
                    }
                }
            }
        }
    }

    private void addTemplateProposalIfMatch(ArrayList proposals, Template template, TemplateContext tc, Region r, String prefix) {
        if (template.getName().startsWith(prefix)) {
            proposals.add(new TemplateProposal(template, tc, r, null));
        }
    }
}
