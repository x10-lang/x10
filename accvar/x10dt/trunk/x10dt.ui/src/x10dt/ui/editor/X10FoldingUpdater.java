/*******************************************************************************
* Copyright (c) 2008,2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package x10dt.ui.editor;

import java.util.HashMap;
import java.util.List;

import lpg.runtime.ILexStream;
import lpg.runtime.IToken;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.base.LPGFolderBase;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Import;
import polyglot.ast.Initializer;
import polyglot.ast.Loop;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.Switch;
import polyglot.ast.SwitchBlock;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.Atomic;
import x10.ast.Finish;
import x10dt.ui.parser.ParseController;

public class X10FoldingUpdater extends LPGFolderBase {
    private ILexStream fLexStream;

    /**
     * Returns the offset of the line containing the given offset
     */
    private int findLineStart(int offset) {
        // N.B. LPG reports a "line offset" as the offset of the line terminator
        // preceding the line; hence the +1 in the following. The -1 is needed
        // because of an asymmetry between getLineOffset() and getLineNumberOfCharAt():
        // the former expects 0-based line numbers, while the latter produces 1-based
        // line numbers. This will hopefully be fixed in LPG 2.0.21.
    	int lineStart= fLexStream.getLineOffset(fLexStream.getLineNumberOfCharAt(offset)-1) + 1;
		return lineStart;
	}

    /**
     * Returns the offset of the line after the one containing the given offset, if any,
     * otherwise, returns the last offset in the character stream
     */
    private int findNextLineStart(int offset) {
    	int lineNum= fLexStream.getLineNumberOfCharAt(offset);

    	if (lineNum >= fLexStream.getLineCount()) {
    	    return fLexStream.getStreamLength();
    	}

    	int lineStart= fLexStream.getLineOffset(lineNum) + 1;

		return lineStart;
	}

    /**
     * Folds multi-line comments
     */
    private void foldComments(ILexStream lexStream) {
		List<? extends IToken> adjuncts = prsStream.getAdjuncts();

		for(IToken adjunct: adjuncts) {
            int adjStart= adjunct.getStartOffset();
            int adjEnd= adjunct.getEndOffset();

            if (fLexStream.getLineNumberOfCharAt(adjStart) != fLexStream.getLineNumberOfCharAt(adjEnd)) {
                int foldStart= findLineStart(adjStart);
    			int foldEnd= findNextLineStart(adjEnd);

    			makeAnnotation(foldStart, foldEnd - foldStart + 1);
            }
        }
	}

    /**
     * Folds import declarations that span multiple lines
     * @param ast
     * @param parseController
     */
	private void foldImports(Node ast, IParseController parseController) {
		List<Import> imports = ((SourceFile) ast).imports();
        if (imports.size() > 0) {
            Node first_import = (Node) imports.get(0),
                 last_import  = (Node) imports.get(imports.size() - 1);
            int leftPos= findLineStart(first_import.position().offset());
            int rightPos= findNextLineStart(last_import.position().endOffset());
            int len= rightPos - leftPos + 1;

            if (leftPos >= 0 && len > 0 && fLexStream.getLineNumberOfCharAt(leftPos) != fLexStream.getLineNumberOfCharAt(rightPos)) {
//            	System.out.println("Making annotation @ <" + leftPos + ", len: " + len + "> for imports");
            	makeAnnotation(leftPos, len);
            }
        }
	}

	/**
	 * Use this version of makeAnnotation(..) when you want to fold a text
	 * element that corresponds directly to an AST node
	 */
    private void makeAnnotation(Node node) {
	    int nodeStart= node.position().offset();
		int nodeEnd= node.position().endOffset();

		if (fLexStream.getLineNumberOfCharAt(nodeStart) == fLexStream.getLineNumberOfCharAt(nodeEnd)) {
			return;
		}

		int foldStart= findLineStart(nodeStart);
		int foldEnd= findNextLineStart(nodeEnd);
		int foldLen= foldEnd - foldStart + 1;

        if (foldStart >= 0 && foldLen > 0) {
//        	System.out.println("Making annotation @ <" + foldStart + ", len: " + foldLen + "> for node of type " + node.getClass());
        	makeAnnotation(foldStart, foldLen);
        }
    }   

    @Override
    protected void sendVisitorToAST(HashMap<Annotation, Position> newAnnotations, List<Annotation> annotations, Object ast) {
    	ParseController x10ParseController= (ParseController) parseController;
    	prsStream = x10ParseController.getParseStream();
    	fLexStream = x10ParseController.getLexStream();

    	Node astRoot = (Node) parseController.getCurrentAst();

    	astRoot.visit(new FoldingVisitor());

    	foldImports(astRoot, parseController);
    	foldComments(fLexStream);

//    	dumpAnnotations(annotations, newAnnotations);
    }

    /*
     * A visitor for ASTs.  Its purpose is to create ProjectionAnnotations
     * for regions of text corresponding to various types of AST node or to
     * text ranges computed from AST nodes.  Projection annotations appear
     * in the editor as the widgets that control folding.
     */
    private class FoldingVisitor extends NodeVisitor {
        public NodeVisitor enter(Node n) {
            if (n instanceof ClassDecl ||
                n instanceof Initializer ||
                n instanceof ConstructorDecl ||
                n instanceof MethodDecl ||
                n instanceof Switch ||
                n instanceof SwitchBlock ||
                n instanceof Loop || // includes For, ForLoop, While, do
                n instanceof AtEach ||
                n instanceof Async ||
                n instanceof Atomic ||
                (n instanceof Finish && ((Finish) n).body() instanceof Block))
            {
            	if (!n.position().isCompilerGenerated()) {
            		makeAnnotation(n);
            	}
            }
            return super.enter(n);
        }
    }
}
