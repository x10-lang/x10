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
package x10dt.ui.contentProposer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.imp.editor.SourceProposal;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.IContentProposer;
import org.eclipse.imp.utils.HTMLPrinter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.ObjectType;
import polyglot.types.Type;
import polyglot.visit.NodeVisitor;
import x10.parser.X10Parsersym;
import x10.types.MethodInstance;
import x10dt.ui.parser.ParseController;
import x10dt.ui.parser.PolyglotNodeLocator;

/**
 * ContentProposer Specification:
 * The content proposer is activated using control space, and currently supports the following features:
 * 1. When the cursor is after a "." following a reference type, it displays all members of that type, filtering using the prefix.
 * 2. When the cursor is in the middle or right after an identifier, it proposes completion for that identifier using names that are in the scope.
 * 3. When the cursor is in between tokens, it proposes X10 code templates. It distinguishes between a class body, and a method body.
 * 4. When the cursor is right before a "def" method declaration, it proposes method modifiers.
 * 5. When the cursor is right before a "val" for a field declaration, it proposes field modifiers (global).
 * 
 * Note that these features are not necessarily mutually exclusive. E.g., if the cursor is before a def, it will propose both templates for a class body,
 * as well as method modifiers.
 * 
 * Note that currently these features only work in the absence of compilation errors.
 * 
 * @author mvaziri
 *
 */
public class X10ContentProposer implements IContentProposer, X10Parsersym { 
    
	private final static boolean EMPTY_PREFIX_MATCHES = true;
	
	private boolean emptyPrefixTest(boolean emptyPrefixMatches, String prefix){
		if (!emptyPrefixMatches)
			return !prefix.equals("");
		return true;
	}
	
	private void filterFields(List<FieldInstance> fields, List<FieldInstance> in_fields, String prefix, boolean emptyPrefixMatches) {
        for(FieldInstance f: in_fields) {
            String name= f.name().toString(); // PORT1.7 was f.name()
            if (emptyPrefixTest(emptyPrefixMatches, prefix) && name.startsWith(prefix))
                fields.add(f);
            
        }
        
    }

    private void filterMethods(List<MethodInstance> methods, List<MethodInstance> in_methods, String prefix, boolean emptyPrefixMatches) {
        for(MethodInstance m: in_methods) {
            String name= m.name().toString();
            // RMF 18 Oct 2010 - prune methods w/ identical signatures, since signatures are
            // what's presented in the list of proposals.
            if (emptyPrefixTest(emptyPrefixMatches, prefix) && name.startsWith(prefix) && !containsMethodSig(methods, m)) {
                methods.add(m);
            }
        }
    }

    private boolean containsMethodSig(List<MethodInstance> methods, MethodInstance m) {
        for(MethodInstance mi: methods) {
            if (mi.signature().equals(m.signature())) {
                return true;
            }
        }
		return false;
	}

	private void filterClasses(List<ClassType> classes, List<ClassType> in_classes, String prefix, boolean emptyPrefixMatches) {//PORT1.7 2nd arg was List<ReferenceType>
        for(ObjectType r: in_classes) {
            ClassType c= r.toClass();
            String name= c.name().toString();  // PORT1.7 name() replaced with name().toString()
            if (emptyPrefixTest(emptyPrefixMatches, prefix) && name.startsWith(prefix)) {
                classes.add(c);
            }
        }
    }
    // PORT1.7  this should probably take a Type instead of a ReferenceType arg.  cast to StructType or ObjectType to get at what we need now
    private void getFieldCandidates(Type container_type, List<FieldInstance> fields, String prefix, boolean emptyPrefixMatches) {// PORT1.7 ReferenceType replaced with Type
		if (container_type == null)
			return;

		if (container_type instanceof ObjectType) {
			ObjectType oType = (ObjectType) container_type;

			filterFields(fields, oType.fields(), prefix, emptyPrefixMatches); // PORT1.7 ReferenceType.fields()->ObjectType.fields()

			for (int i = 0; i < oType.interfaces().size(); i++) {
				ObjectType interf = (ObjectType) oType.interfaces().get(i);
				filterFields(fields, interf.fields(), prefix, emptyPrefixMatches);
			}

			getFieldCandidates(oType.superClass(), fields, prefix, emptyPrefixMatches);
		}
	}

    private void getMethodCandidates(ObjectType container_type, List<MethodInstance> methods, String prefix, boolean emptyPrefixMatches) {//PORT1.7 ReferenceType->ObjectType
        if (container_type == null)
            return;

        filterMethods(methods, container_type.methods(), prefix, emptyPrefixMatches);

        getMethodCandidates((ObjectType)container_type.superClass(), methods, prefix, emptyPrefixMatches);//PORT1.7 superType()->superClass()
    }

    private void getClassCandidates(Type type, List<ClassType> classes, String prefix, boolean emptyPrefixMatches) {
        if (type == null)
            return;
        ClassType container_type= type.toClass();
        if (container_type == null)
            return;

        filterClasses(classes, container_type.memberClasses(), prefix, emptyPrefixMatches);
       
        for(int i= 0; i < container_type.interfaces().size(); i++) {
            ClassType interf= ((ObjectType) container_type.interfaces().get(i)).toClass();
            filterClasses(classes, interf.memberClasses(), prefix, emptyPrefixMatches);
        }
        getClassCandidates(container_type.superClass(), classes, prefix, emptyPrefixMatches);//PORT1.7 superType)_->superClass()
    }

    private void getCandidates(ObjectType container_type, List<ICompletionProposal> list, String prefix, int offset, boolean emptyPrefixMatches) {//PORT1.7 RefType->ObjectType
        List<FieldInstance> fields= new ArrayList<FieldInstance>();
        List<MethodInstance> methods= new ArrayList<MethodInstance>();
        List<ClassType> classes= new ArrayList<ClassType>();

        getFieldCandidates(container_type, fields, prefix, emptyPrefixMatches);
        getMethodCandidates(container_type, methods, prefix, emptyPrefixMatches);
        getClassCandidates((Type) container_type, classes, prefix, emptyPrefixMatches);

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

    // Declarations
    private final Template fVariableDeclaration = new Template("var", "mutable variable/field declaration", CONTEXT_ID, "var ${name}:${typename};", false );
    private final Template fValueDeclaration = new Template("val", "immutable variable/field declaration", CONTEXT_ID, "val ${name} = ${expression};", false );
    /* CHANGED */ private final Template fMethodTemplate = new Template("def", "method declaration", CONTEXT_ID, "def ${name}(${x}:${typename1}){${constraint}}:${typename2} {  }", false );
    private final Template fMainMethod = new Template("main method", "main method", CONTEXT_ID, "public static def main(argv:Array[String](1)) {  }", false);   
    private final Template fConstructorTemplate = new Template("this", "constructor declaration", CONTEXT_ID, "def this(${name}:${typename}) {  }", false);
    private final Template fStructTemplate = new Template("struct", "struct declaration", CONTEXT_ID, "struct ${name} {  }", false );
    private final Template fCovariantGeneric = new Template("[+X]", "generic type, covariant parameter", CONTEXT_ID, "[+${X}]", false);
    private final Template fContravariantGeneric = new Template("[-X]", "generic type, contravariant parameter", CONTEXT_ID, "[-${X}]", false);
    private final Template fDependentTypeDeclaration = new Template("dependent type", "class declaration with property", CONTEXT_ID, "class ${name}(${x}:${typename}) { def this(${x}:${typename}){ property(${x}); } }", false);
    private final Template fTypeDefinition = new Template("type", "type definition", CONTEXT_ID, "type ${name1} = ${name2};", false);
    private final Template fFunctionType = new Template("function", "function definition", CONTEXT_ID, "var ${fname}:(${T1},${T2}) => ${T} = (${x1}:${T1}, ${x2}:${T2}) => ${expression};", false);
   
    //say static val instead.
    private final Template fConstDeclaration = new Template("static val", "immutable static field declaration", CONTEXT_ID, "static val ${name} = ${expression};", false );
    /*CHANGED*/ private final Template fPropertyDeclaration = new Template("property", "property declaration", CONTEXT_ID, "property ${name}() = ${expression};", false);
    private final Template fClassTemplate = new Template("class", "class declaration", CONTEXT_ID, "class ${name} {  }", false);
    
    // Constructs
    /*CHANGED*/ private final Template fAsyncTemplate= new Template("async", "async statement", CONTEXT_ID, "async { ${stmt} }", false);
    private final Template fAtStatementTemplate = new Template("at","at statement", CONTEXT_ID, "at (${place}) { ${stmt} }", false );
    private final Template fAtExpressionTemplate = new Template("at", "at expression", CONTEXT_ID, "at (${place}) ${expression}", false);
    private final Template fFinishTemplate = new Template("finish", "finish statement", CONTEXT_ID, "finish { ${stmt} }", false);
    private final Template fAtEachTemplate= new Template("ateach", "ateach statement", CONTEXT_ID, "ateach (${x}:Point in ${distribution}) { ${stmt} }", false);
    private final Template fAtomicTemplate= new Template("atomic", "atomic statement", CONTEXT_ID, "atomic { ${stmt} }", false);
    private final Template fWhenTemplate= new Template("when", "when statement", CONTEXT_ID, "when (${condition}) { ${stmt} }", false);
    private final Template fCoercionTemplate = new Template("as", "coercion", CONTEXT_ID, "${expression} as ${typename}" , false);
    
    // Places, Regions, Distributions, Arrays
    /*CHANGED*/private final Template fRegion1DTemplate= new Template("region 1-D", "1-dimensional region creation", CONTEXT_ID, "${lower}..${upper}", false);
    /*CHNGED*/private final Template fRegion2DTemplate= new Template("region 2-D", "2-dimensional region creation", CONTEXT_ID, "(${lower1}..${upper1})*(${lower2}..${upper2})", false);
    /*CHANGED*/ private final Template fArrayNewTemplate= new Template("array new", "array instantiation", CONTEXT_ID, "var ${name}: Array[${typename}] = new Array[${typename}](${distribution} , (p: Point) => ${initialization_expression} );", false);
    /*CHANGED*/ private final Template fDistArrayNewTemplate= new Template("dist array new", "dist array instantiation", CONTEXT_ID, "var ${name}: DistArray[${typename}] = DistArray.make[${typename}](${distribution} , (p: Point) => ${initialization_expression} );", false);
    private final Template fArrayAccessTemplate = new Template("dist array access", "remote array access", CONTEXT_ID, "at(${array_name}.dist(${point})) ${array_name}(${point}) = ${expression};", false);
    
    // Modifiers
    private final Template fConstrainedType = new Template("constraint", "constrained type definition", CONTEXT_ID, "type ${typename1} = ${typename2}{${constraint}};", false);
    private final Template fPrivateTemplate = new Template("private", "private qualifier", CONTEXT_ID, "private ", false);
    private final Template fPropertyTemplate = new Template("property", "property qualifier", CONTEXT_ID, "property ", false);   
    private final Template fFinalTemplate = new Template("final", "final modifier", CONTEXT_ID, "final ", false);
    private final Template fAbstractTemplate = new Template("abstract", "abstract modifier", CONTEXT_ID, "abstract", false);
    
    // Clocks
    private final Template fClockedStatement = new Template("clocked", "clocked statement", CONTEXT_ID, "async clocked (${clock}) { ${stmt} }", false);
    
    // Misc
    /*CHANGED*/ private final Template fInstanceofTemplate = new Template("instanceof", "instanceof in a conditional", CONTEXT_ID, "if (${name1} instanceof ${typename}) { val ${name2} = ${name1} as ${typename}; }", false);
    private final Template fPrintTemplate = new Template("printing", "printing to console", CONTEXT_ID, "Console.OUT.println(\"${string}\");", false);
    
     
    private final Template[] fTemplates= new Template[] {  
            fArrayNewTemplate, fDistArrayNewTemplate, fArrayAccessTemplate, fAsyncTemplate, fAtStatementTemplate,
            fAtEachTemplate, fAtomicTemplate,  
            fClockedStatement, fConstrainedType, fFinishTemplate,  
            fFunctionType, fInstanceofTemplate, fPrintTemplate,
            fTypeDefinition, fValueDeclaration, fVariableDeclaration,   fWhenTemplate
    };
    
    
    private final String fArrayNewInfo = "\n\nNew Array";
    private final String fArrayAccessInfo = "\n\n Array access";
    private final String fCoercionInfo = "\n\nCoercion";
    private final String fAsyncInfo = "\n\nAsync statement";
    private final String fAtStatementInfo = "\n\nAt statement";
    private final String fAtEachInfo = "\n\nAteach statement";
    private final String fAtomicInfo = "\n\nAtomic statement";
    private final String fClockedInfo = "\n\nClocked statement";
    private final String fConstrainedInfo = "\n\nConstrained type";
    private final String fFinishInfo = "\n\nFinish statement";
    private final String fFunctionTypeInfo = "\n\nFunction Type";
    private final String fRegion1DInfo = "\n\n1-D region";
    private final String fRegion2DInfo = "\n\n2-D Region";
    private final String fTypeDefinitionInfo = "\n\nType definition";
    private final String fValueInfo = "\n\nValue declaration";
    private final String fVariableInfo = "\n\nVariable declaration";
    private final String fWhenInfo = "\n\nWhen statement";
    private final String fPropertyInfo = "\n\n Property declaration";
    private final String fClassInfo = "\n\n Class declaration";
    private final String fPrintInfo = "\n\n Print statement";
    
    private static final Map<Template,String> infos = new HashMap<Template,String>();
   
    {
    	infos.put(fArrayNewTemplate, fArrayNewInfo);
    	infos.put(fArrayAccessTemplate, fArrayAccessInfo);
    	infos.put(fCoercionTemplate, fCoercionInfo);
    	infos.put(fAsyncTemplate, fAsyncInfo);
    	infos.put(fAtStatementTemplate, fAtStatementInfo);
    	infos.put(fAtEachTemplate, fAtEachInfo);
    	infos.put(fAtomicTemplate, fAtomicInfo);
    	infos.put(fClockedStatement, fClockedInfo);
    	infos.put(fConstrainedType, fConstrainedInfo);
    	infos.put(fFinishTemplate, fFinishInfo);
    	infos.put(fFunctionType, fFunctionTypeInfo);
    	infos.put(fRegion1DTemplate, fRegion1DInfo);
    	infos.put(fRegion2DTemplate, fRegion2DInfo);
    	infos.put(fTypeDefinition, fTypeDefinitionInfo);
    	infos.put(fValueDeclaration, fValueInfo);
    	infos.put(fVariableDeclaration, fVariableInfo);
    	infos.put(fWhenTemplate, fWhenInfo);
    	infos.put(fPropertyDeclaration, fPropertyInfo);
    	infos.put(fClassTemplate, fClassInfo);
    	infos.put(fPrintTemplate, fPrintInfo);
    }
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
        IPrsStream prs_stream= ((ParseController) controller).getParseStream();
        int index= prs_stream.getTokenIndexAtCharacter(offset);
        int token_index = (index < 0 ? -index + 1 : index);
        IToken tokenToComplete = null;
        try {
        	tokenToComplete= prs_stream.getIToken(token_index); 
        } catch(IndexOutOfBoundsException e){
        	return (ICompletionProposal[]) list.toArray(new ICompletionProposal[list.size()]);
        }
        SimpleLPGParseController lpgPC= (SimpleLPGParseController) controller;
        String prefix= computePrefixOfToken(tokenToComplete, offset, lpgPC);

        IToken previousToken = (token_index != 0)? prs_stream.getIToken(token_index - 1) : null;

        PolyglotNodeLocator locator= new PolyglotNodeLocator(controller.getProject(), ((ParseController) controller).getLexStream())  ;
        Node currentAst= (Node) controller.getCurrentAst();
        Node node= (Node) locator.findNode(currentAst, tokenToComplete.getStartOffset(), tokenToComplete.getEndOffset());
        Node previousNode = (previousToken != null)? (Node) locator.findNode(currentAst, previousToken.getStartOffset(), previousToken.getEndOffset()): null;
     
        boolean in_between_tokens = index < 0;
        if (offset == tokenToComplete.getStartOffset()) // TODO Handle case where caret is within whitespace between tokens
        	in_between_tokens = true;
        
        if (previousToken != null && previousToken.getKind() == TK_DOT) { //Display members following a dot
        	Node parent= (Node) locator.getParentNodeOf(node, currentAst);
        	Type type = null;
        	if (parent instanceof Call){
        		type = ((Call)parent).target().type();
        	} else if (parent instanceof Field){
        		type = ((Field)parent).target().type();
        	} else if (parent instanceof FieldAssign){
        		type = ((FieldAssign)parent).target().type();
        	}
        	if (type != null && type.isReference()){
        		getCandidates((ObjectType) type, list, prefix, offset, true);//PORT1.7 RefType->ObjectType. can we guarantee it's an ObjectType?
        	}
        
        //The next case completes an Id with names in scope  
        } else if ((node instanceof Id && offset > tokenToComplete.getStartOffset() && offset <= tokenToComplete.getEndOffset()) ||  
        		   (previousNode instanceof Id && offset == previousToken.getEndOffset() + 1)){ //at the very end of an Id
        	Node n = (node instanceof Id)? node : previousNode;
        	String pref = (node instanceof Id)? prefix : computePrefixOfToken(previousToken, offset, lpgPC);
        	addNamesInScope(currentAst, n, pref, list, offset, !EMPTY_PREFIX_MATCHES);
        
        } else if (in_between_tokens){ //Display templates, names in scope -- index < 0 when we are at a white space or comment
            Node location = location(previousNode, node, locator, currentAst);
            if (location instanceof Block && (justAfter(TK_SEMICOLON, previousToken) || justAfter(TK_RBRACE, previousToken) || justAfter(TK_LBRACE, previousToken))){ //Statement context. 
        		addTemplateProposals(offset, viewer, list, prefix, fTemplates);
        		//addNamesInScope(currentAst, node, prefix, list, offset, EMPTY_PREFIX_MATCHES);
        	}
            else if (justAfter(TK_EQUAL, previousToken) && (location instanceof Assign || location instanceof LocalDecl)){
            	Template[] templates = new Template[]{fAtExpressionTemplate, fCoercionTemplate,  fRegion1DTemplate, fRegion2DTemplate};
            	addTemplateProposals(offset, viewer, list, prefix, templates );
            }
        	else if (location instanceof ClassBody){ //Class context
        		Template[] templates = new Template[]{fVariableDeclaration, fValueDeclaration, fConstDeclaration, fPropertyDeclaration, fMainMethod, fMethodTemplate, fConstructorTemplate, 
        			fClassTemplate, fStructTemplate, fDependentTypeDeclaration, };
        		addTemplateProposals(offset, viewer, list, prefix, templates);
            
        	} 
            
            //add method modifiers before a "def"
            if (justBefore(TK_def, tokenToComplete, previousToken, offset)){
            	Template[] templates = new Template[]{fAbstractTemplate, fFinalTemplate, fPrivateTemplate, fPropertyTemplate };
        		addTemplateProposals(offset, viewer, list, prefix, templates);
            }
            
            //add class modifiers before a "class"
            if (justBefore(TK_class, tokenToComplete, previousToken, offset)) {
            	Template[] templates = new Template[]{fAbstractTemplate, fFinalTemplate};
        		addTemplateProposals(offset, viewer, list, prefix, templates);
            }
        	
            //add field modifiers before a "val" field declaration
            if (location instanceof ClassBody && (justBefore(TK_val, tokenToComplete, previousToken, offset))){
        		Template[] templates = new Template[]{fPropertyTemplate};
        		addTemplateProposals(offset, viewer, list, prefix, templates);
        	}
            
            //add type parameter in a class declaration
            if (location instanceof ClassDecl && previousNode instanceof CanonicalTypeNode &&  
            		offset == previousToken.getEndOffset() + 1){
           		Template[] templates = new Template[]{fContravariantGeneric, fCovariantGeneric};
        		addTemplateProposals(offset, viewer, list, prefix, templates);
            }
        }
               
        return (ICompletionProposal[]) list.toArray(new ICompletionProposal[list.size()]);
    }

    // Tests if we are just before a token of kind tokenKind, with white space before the current cursor position
    private boolean justBefore(int tokenKind, IToken tokenToComplete, IToken previousToken, int offset){
    	return tokenToComplete.getKind() == tokenKind && offset > previousToken.getEndOffset() + 1;
    }
    
    // Tests if we are just after a token of kind tokenKind
    private boolean justAfter(int tokenKind, IToken previousToken){
    	return previousToken.getKind() == tokenKind;
    }
    
    // Finds the least common parent of prev and next in the currentAst. The reason for using our own visitor is that polyglot parent finder changes the AST as it goes along.
    private Node location(final Node prev, final Node next, PolyglotNodeLocator locator, Node currentAst){    	if (prev == null)
    		return next;
    	
    	if (next == null)
    		return null;
    	
    	final Stack<Node> nextParents = new Stack<Node>();
        final Stack<Node> prevParents = new Stack<Node>();
       
        currentAst.visit(new NodeVisitor(){
        	boolean prevDone, nextDone = false;
        	
        	@Override
        	public NodeVisitor enter(Node parent, Node child){
        		if (!prevDone){
        			prevParents.push(child);
        			if (child == prev) prevDone = true;
        		} 
        		if (!nextDone){
        			nextParents.push(child);
        			if (child == next) nextDone = true;
        		}
        		return super.enter(parent, child);
        	}
        	
        	public Node leave(Node parent, Node old, Node n, NodeVisitor v){
        		if (!prevDone) prevParents.pop();
        		if (!nextDone) nextParents.pop();
        		return super.leave(parent, old, n, v);
        	}
        });
        
        
    	for(int i = prevParents.size()-1; i >= 0; i--){
    		if (nextParents.contains(prevParents.get(i))) return prevParents.get(i);
    	}
    	return null;
    }
  
   
    
    private void addTemplateProposals(int offset, ITextViewer viewer, ArrayList<ICompletionProposal> list, String prefix, Template[] templates) {
        IDocument doc= viewer.getDocument();
        Region r= new Region(offset, prefix.length());
        TemplateContext tc= new DocumentTemplateContext(fContextType, doc, offset - prefix.length(), prefix.length());

        for(int i= 0; i < templates.length; i++) {
        	addTemplateProposalIfMatch(list, templates[i], tc, r, prefix);
        }
    }


    private void addTemplateProposalIfMatch(ArrayList<ICompletionProposal> proposals, Template template, TemplateContext tc, Region r, String prefix) {
    	if (template.getName().startsWith(prefix)) {
    		String html = createHtml(template);
    		X10BrowserInformationControlInput info = new X10BrowserInformationControlInput(null, html, template.getName());
    		ICompletionProposal proposal = new X10TemplateProposal(template, tc, r, null, info);
    		proposals.add(proposal);
        }
    }
    
    private String createHtml(Template template){
    	StringBuffer buffer= new StringBuffer();
    	HTMLPrinter.insertPageProlog(buffer, 0);
    	buffer.append(X10BrowserInformationControlInput.PATTERN); //Ugly HACK!!! need a placeholder for pattern (it will be evaluated later).
    	//String base = "PLUGINS_ROOT/x10dt.help/html/langref/variables.html";
    	String langrefurl = "http://dist.codehaus.org/x10/documentation/languagespec/x10-latest.pdf";
    	if (infos.get(template) != null) 
    		buffer.append("\n<h4>"+ infos.get(template) + "</h4>\n"); 
    	else buffer.append("\n<h4></h4>\n");
		buffer.append("\n<a href=\"" + langrefurl + "\">X10 Language Reference</a>\n");
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
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
    
   

    private void addNamesInScope(Node currentAst, final Node in_node, String prefix, List<ICompletionProposal> proposals, int offset, boolean emptyPrefixMatches) {
        // Polyglot can't supply the pkg/class names, so we'll have to appeal to the search index
        if (emptyPrefixTest(emptyPrefixMatches, prefix) && "this".startsWith(prefix)) // Should check that we're not in a static method or initializer
            proposals.add(new SourceProposal("this", prefix, offset));
        if (emptyPrefixTest(emptyPrefixMatches, prefix) && "here".startsWith(prefix)) // Should check that we're not in a static method or initializer
            proposals.add(new SourceProposal("here", prefix, offset));
//        if (emptyPrefixTest(emptyPrefixMatches, prefix) && "self".startsWith(prefix)) // Should check that we're not in a static method or initializer
//            proposals.add(new SourceProposal("self", prefix, offset));
        final Stack<Node> path= new Stack<Node>();
        currentAst.visit(new NodeVisitor() {
        	boolean done = false;
        	@Override
        	public NodeVisitor enter(Node parent, Node child){
        		if (!done) {
        			if (parent != null) path.push(parent);
        			if (child == in_node) {
        				path.push(child);
        				done = true;
        			}
        		}
        		return super.enter(parent, child);
        	}
        	
            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            	if (!done) path.pop();
                return super.leave(parent, old, n, v);
            }
        });
        
        for(Node node : path) {
        	if (node instanceof ClassBody){
        		ClassBody cd = (ClassBody) node;
        		List<ClassMember> members = cd.members();
        		for(ClassMember m: members){
        			if (m instanceof FieldDecl){
        				String fname = ((FieldDecl)m).name().id().toString();
        				if (emptyPrefixTest(emptyPrefixMatches, prefix) && fname.startsWith(prefix)) { //PORT1.7 name() changed to name().id().toString()
                            proposals.add(new SourceProposal(fname, fname, prefix, offset)); //PORT1.7 use cached value
                        }
        			}
        		}
        	}
            if (node instanceof MethodDecl) {
                MethodDecl md= (MethodDecl) node;
                List<Formal> formals= md.formals();
                for(Formal formal : formals) {
                	String fname = formal.name().id().toString(); //PORT1.7 name() changed to name().id().toString(); cached here for reuse
                    if (emptyPrefixTest(emptyPrefixMatches, prefix) && fname.startsWith(prefix)) { //PORT1.7 name() changed to name().id().toString()
                        proposals.add(new SourceProposal(fname, fname, prefix, offset)); //PORT1.7 use cached value
                    }
                } 
            }
            
            if (node instanceof Block) {
                Block b= (Block) node;
                List<Stmt> stmts= b.statements();
                for(Stmt s : stmts) {
                	if (s.position().offset() > offset) {
                        // Don't include declarations that follow the current cursor pos
                        continue;
                    }
                    if (s instanceof LocalDecl) {
                        LocalDecl decl= (LocalDecl) s;
                        String ldname = decl.name().id().toString();// PORT1.7 changed name() to name().id().toString(); cached
                        if (emptyPrefixTest(emptyPrefixMatches, prefix) && ldname.startsWith(prefix)) { // PORT1.7 changed name() to name().id().toString(); cached
                            proposals.add(new SourceProposal(ldname, ldname, prefix, offset)); //PORT1.7 use cached value
                        }
                    }
                }
            }
           
            
        }
    }

}