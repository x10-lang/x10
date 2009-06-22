package org.eclipse.imp.x10dt.ui.contentProposer;

import java.util.ArrayList;
import java.util.List;

import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.imp.editor.SourceProposal;
import org.eclipse.imp.parser.IParseController;
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
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.Unary;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Qualifier;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import x10.parser.X10Parsersym;

public class X10ContentProposer implements IContentProposer, X10Parsersym
{
    private void filterFields(ArrayList fields, List in_fields, String prefix)
    {
        for (int i = 0; i < in_fields.size(); i++)
        {
            FieldInstance f = (FieldInstance) in_fields.get(i);
            String name = f.name();
            if (name.length() >= prefix.length() && prefix.equals(name.substring(0, prefix.length())))
                fields.add(f);
        }
    }
    
    private void filterMethods(ArrayList methods, List in_methods, String prefix)
    {
        for (int i = 0; i < in_methods.size(); i++)
        {
            MethodInstance m = (MethodInstance) in_methods.get(i);
            String name = m.name();
            if (name.length() >= prefix.length() && prefix.equals(name.substring(0, prefix.length())))
                methods.add(m);
        }
    }

    private void filterClasses(ArrayList classes, List in_classes, String prefix)
    {
        for (int i = 0; i < in_classes.size(); i++)
        {
            ClassType c = ((ReferenceType) in_classes.get(i)).toClass();
            String name = c.name();
            if (name.length() >= prefix.length() && prefix.equals(name.substring(0, prefix.length())))
            {
                //if (name.length() == prefix.length())
                //    ; // TODO: highlight class name in YELLOW in editor
                //else 
                classes.add(c);
            }
        }
    }
    
    private void getFieldCandidates(ReferenceType container_type, ArrayList fields, String prefix)
    {
        if (container_type == null)
            return;
        
        if (prefix.length() == 0)
             fields.addAll(container_type.fields());
        else filterFields(fields, container_type.fields(), prefix);

        for (int i = 0; i < container_type.interfaces().size(); i++)
        {
            ReferenceType interf = (ReferenceType) container_type.interfaces().get(i);
            if (prefix.length() == 0)
                 fields.addAll(interf.fields());
            else filterFields(fields, interf.fields(), prefix);
        }
        
        getFieldCandidates((ReferenceType) container_type.superType(), fields, prefix);
    }

    private void getMethodCandidates(ReferenceType container_type, ArrayList methods, String prefix)
    {
        if (container_type == null)
            return;
        
        if (prefix.length() == 0)
             methods.addAll(container_type.methods());
        else filterMethods(methods, container_type.methods(), prefix);

        getMethodCandidates((ReferenceType) container_type.superType(), methods, prefix);
    }

    private void getClassCandidates(Type type, ArrayList classes, String prefix)
    {
        if (type == null)
            return;
        ClassType container_type = type.toClass();
        if (container_type == null)
            return;
        
        if (prefix.length() == 0)
             classes.addAll(container_type.memberClasses());
        else filterClasses(classes, container_type.memberClasses(), prefix);

        for (int i = 0; i < container_type.interfaces().size(); i++)
        {
            ClassType interf = ((ReferenceType) container_type.interfaces().get(i)).toClass();
            if (prefix.length() == 0)
                 classes.addAll(interf.memberClasses());
            else filterClasses(classes, interf.memberClasses(), prefix);
        }
        
        getClassCandidates(container_type.superType(), classes, prefix);
    }

    private void getCandidates(ReferenceType container_type, ArrayList list, String prefix, int offset)
    {        
        ArrayList fields = new ArrayList(),
                  methods = new ArrayList(),
                  classes = new ArrayList();

        getFieldCandidates(container_type, fields, prefix);
        getMethodCandidates(container_type, methods, prefix);
        getClassCandidates((Type) container_type, classes, prefix);

        for (int i = 0; i < fields.size(); i++)
        {
            assert(fields.get(i) instanceof FieldInstance);
            FieldInstance field = (FieldInstance) fields.get(i);
            list.add(new SourceProposal(field.name(), prefix, offset));
        }

        for (int i = 0; i < methods.size(); i++)
        {
            assert(methods.get(i) instanceof MethodInstance);
            MethodInstance method = (MethodInstance) methods.get(i);
            list. add(new SourceProposal(method.signature(), prefix, offset));
        }
        
        for (int i = 0; i < classes.size(); i++)
        {
            assert(classes.get(i) instanceof ClassType);
            //ClassType type = ((ReferenceType) classes.get(i)).toClass();
            ClassType type = (ClassType) classes.get(i);
            if (!type.isAnonymous())
        	list. add(new SourceProposal(type.name(), prefix, offset));
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

    public ICompletionProposal[] getContentProposals(IParseController controller, int offset, ITextViewer viewer)
    {
	ArrayList list = new ArrayList();
        //
        // When the offset is in between two tokens (forexample, on a white space or comment)
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
        IPrsStream prs_stream = controller.getParser().getParseStream(); 
        int index = prs_stream.getTokenIndexAtCharacter(offset),
            token_index = (index < 0 ? -(index - 1) : index);
        IToken token = prs_stream.getIToken(token_index),
               candidate = prs_stream.getIToken(prs_stream.getPrevious(token_index));
        //
        // If we are at an offset position immediately following an "identifier"
        // candidate, then consider the candidate to be the token for which we need
        // assistance and chose its predecessor as the candidate for the lookup.
        //
        if ((candidate.getKind() == TK_IDENTIFIER ||
             controller.isKeyword(candidate.getKind()))
            && offset == candidate.getEndOffset() + 1)
        {
            token = candidate;
            candidate = prs_stream.getIToken(prs_stream.getPrevious(candidate.getTokenIndex()));
        }

        String prefix = "";
        if (token.getKind() == TK_IDENTIFIER ||
            token.getKind() == TK_ErrorId ||
            controller.isKeyword(token.getKind()))
        {
            if (offset >= token.getStartOffset() && offset <= token.getEndOffset() + 1)
            {
                prefix = token.toString().substring(0, offset - token.getStartOffset());
            }
        }
/*
list.add(new SourceProposal("Offset: " + offset, "", offset));
list.add(new SourceProposal("Token start offset: " + token.getStartOffset(), "", offset));
list.add(new SourceProposal("Token end offset: " + token.getEndOffset(), "", offset));
list.add(new SourceProposal("Prefix: \"" + (prefix == null ? "" : prefix) + "\"", "", offset));
list.add(new SourceProposal("Candidate start offset: " + candidate.getStartOffset(), "", offset));
list.add(new SourceProposal("Candidate end offset: " + candidate.getEndOffset(), "", offset));
list.add(new SourceProposal("Token: " + token, "", offset));
list.add(new SourceProposal("Candidate: " + candidate, "", offset));
*/
        PolyglotNodeLocator locator = new PolyglotNodeLocator(controller.getProject(), controller.getLexer().getLexStream());
        Node node = (Node) locator.findNode(controller.getCurrentAst(), candidate.getStartOffset(), candidate.getEndOffset()); // offset);
        //
        // We execute this code when we encounter a qualified name x.foo,
        // where the left-hand side x of the qualified name may be either
        // an object reference (declared field or local declaration) or a
        // type. Note that x itself may be a qualified name.
        // 
        if (node instanceof Field)
        {
//            list.add(new SourceProposal("Field: " + node.getClass().toString(), " source proposal ", 0));
            Field field = (Field) node;
            if (candidate.getKind() == TK_DOT && field.target().type().isReference() /* instanceof ReferenceType */)
                 getCandidates((ReferenceType) field.target().type(), list, prefix, offset);
            else list.add(new SourceProposal("no info available", " source proposal ", 0));
        }
        //
        // We execute this code when we encounter a qualified name x.foo,
        // where the left-hand side x is a package name.
        //
        else if (node instanceof CanonicalTypeNode)
        {
            if (candidate.getKind() == TK_DOT)
            {
                candidate = prs_stream.getIToken(prs_stream.getPrevious(candidate.getTokenIndex()));
                node = (Node) locator.findNode(controller.getCurrentAst(), candidate.getStartOffset() + 1, candidate.getEndOffset() - 1);
                Qualifier qualifier = ((CanonicalTypeNode) node).qualifier();
                if (qualifier.isType())
                {
                    list.add(new SourceProposal("CanonicalTypeNode: " + candidate.toString() + " " + qualifier.toType().toString() + " => " + node.getClass().toString(), " source proposal ", 0));
                }
            }
        }
        else if (node instanceof Assign)
        {
            list.add(new SourceProposal("Assign: " + node.getClass().toString(), " source proposal ", 0));
            list.add(new SourceProposal("complete prefix " + prefix, " source proposal ", 0));
            if (prefix != null && prefix.length() > 0)
            {
                // TODO: Any package, type, local variable and accessible class members
            }
            else list.add(new SourceProposal("no info available", " source proposal ", 0));
        }
        else if (node instanceof FieldDecl)
        {
            list.add(new SourceProposal("FieldDecl: " + node.getClass().toString(), " source proposal ", 0));
            list.add(new SourceProposal("complete prefix " + prefix, " source proposal ", 0));
            if (prefix != null && prefix.length() > 0)
            {
                // TODO: Any package, type, local variable and accessible class members
                list.add(new SourceProposal("complete prefix " + prefix, " source proposal ", 0));
            }
            else if (candidate.getKind() == TK_EQUAL)
            {
                // TODO: Any package, type, local variable and accessible class members
            }
            else list.add(new SourceProposal("no info available", " source proposal ", 0));
        }
        else if (node instanceof Call)
        {
            Call call= (Call) node;
            if (candidate.getKind() == TK_DOT && call.target().type().isReference())
        	getCandidates((ReferenceType) call.target().type(), list, prefix, offset);
        }
        else 
        {
            // TODO: Any package, type, local variable and accessible class members
            if (node instanceof Binary)
            {
                 list.add(new SourceProposal("BINARY: " + node.getClass().toString(), " source proposal ", 0));
            }
            else if (node instanceof Unary)
            {
                 list.add(new SourceProposal("UNARY: " + node.getClass().toString(), " source proposal ", 0));
            }
            else
            {
                IDocument docu= viewer.getDocument();
                Region r= new Region(offset, prefix.length());
                TemplateContext tc= new DocumentTemplateContext(fContextType, docu, offset, prefix.length());

                addTemplateProposalIfMatch(list, fRegion1DTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fRegion1DTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fRegion2DTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fRegion3DTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fArrayNewTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fAsyncTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fAtEachTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fAtomicTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fForRegionTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fForEachTemplate, tc, r, prefix);
                addTemplateProposalIfMatch(list, fFutureTemplate, tc, r, prefix);
            }
//          else list.add(new SourceProposal("Other: " + node.getClass().toString(), " source proposal ", 0));
//
//          if (prefix != null && prefix.length() > 0)
//          {
//              // complete this prefix...
//              list.add(new SourceProposal("complete prefix " + prefix, " source proposal ", 0));
//          }
        }
        return (ICompletionProposal[]) list.toArray(new ICompletionProposal[list.size()]);
    }

    private void addTemplateProposalIfMatch(ArrayList list, Template template, TemplateContext tc, Region r, String prefix) {
	if (template.getName().startsWith(prefix))
	    list.add(new TemplateProposal(template, tc, r, null));
    }
}
