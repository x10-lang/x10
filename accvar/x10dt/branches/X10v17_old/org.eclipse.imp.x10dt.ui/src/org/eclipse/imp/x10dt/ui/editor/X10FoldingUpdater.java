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
package org.eclipse.imp.x10dt.ui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lpg.runtime.Adjunct;
import lpg.runtime.ILexStream;
import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.IFoldingUpdater;
import org.eclipse.imp.x10dt.core.X10Util;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import polyglot.ast.Block;
import polyglot.ast.Catch;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.If;
import polyglot.ast.Initializer;
import polyglot.ast.Loop;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.SwitchBlock;
import polyglot.ast.Try;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.Now;
//import polyglot.ext.x10.ast.ValueClassDecl;  //PORT1.7  ValueClassDecl is gone
import polyglot.ext.x10.ast.When;
import polyglot.visit.NodeVisitor;
import x10.parser.X10Parser.JPGPosition;

public class X10FoldingUpdater implements IFoldingUpdater
{
    private Annotation[] fOldAnnotations;
    // Map of annotations to positions; accumulates new annotations
    // and is used in updating the annotation model
    private HashMap newAnnotations;

    // List of annotations; accumulates new annotations (disregarding
    // positions); serves as a list of keys for newAnnotations and
    // is used in comparing and listing annotations
    private List annotations;

    // Used to support checking of whether annotations have
    // changed between invocations of updateFoldingStructure
    // (because, if they haven't, then it's probably best not
    // to update the folding structure)
    private ArrayList oldAnnotationsList = null;
    private boolean astWasInvalid = false;
        
    private IPrsStream prsStream;
    private ILexStream lexStream;

    /**
    * A simplistic test of whether two lists differ significantly. This may
    * work well enough much of the time as the comparisons between lists should
    * be made very frequently, actually more frequently than the rate at which
    * the typical human user will edit the program text so as to affect the AST so
    * as to affect the lists. Thus most changes of lists will entail some change
    * in the number of elements at some point that will be observed here. Will not
    * work for certain very rapid edits of source text (e.g., rapid replacement
    * of elements).
    */
    private boolean differ(ArrayList list1, ArrayList list2)
    {
        return (list1.size() != list2.size());
    }
        
    private void dumpAnnotations(final List annotations, final HashMap newAnnotations)
    {
        for (int i = 0; i < annotations.size(); i++)
        {
            Annotation a = (Annotation) annotations.get(i);
            Position p = (Position) newAnnotations.get(a);

            if (p == null)
            {
                System.out.println("Annotation position is null");
                continue;
            }

            System.out.println("Annotation @ " + p.offset + ":" + p.length);
        }
    }
    
    /**
    * Use this version of makeAnnotation when you have a range of 
    * tokens to fold.
    */
    private void makeAnnotation(IToken first_token, IToken last_token)
    {
        if (last_token.getEndLine() > first_token.getLine())
        {
            IToken next_token = prsStream.getIToken(prsStream.getNext(last_token.getTokenIndex()));
            IToken[] adjuncts = next_token.getPrecedingAdjuncts();
            IToken gate_token = adjuncts.length == 0 ? next_token : adjuncts[0];
            makeAnnotation(first_token.getStartOffset(),
                           gate_token.getLine() > last_token.getEndLine()
                                                ? lexStream.getLineOffset(gate_token.getLine() - 1)
                                                : last_token.getEndOffset());
        }
    }    
    
    /**
    * Use this version of makeAnnotation(..) when you have the start
    * and end offset of the region to fold.
    */
    private void makeAnnotation(int start_offset, int end_offset)
    {
        int length = end_offset - start_offset + 1;
        ProjectionAnnotation annotation = new ProjectionAnnotation();
        newAnnotations.put(annotation, new Position(start_offset, length));
        annotations.add(annotation);
    }

    /**
    * Make annotations for the ast.
    */
    private void makeAnnotations(IParseController parseController)
    {
        //
        // Cache the prsStream and the lexStream.
        //
        prsStream = ((SimpleLPGParseController) parseController).getParser().getParseStream();
        lexStream = prsStream.getLexStream();

        //
        // The AST representing the foldable source text
        // ASTNode ast= (ASTNode) parseController.getCurrentAst();
        //
        Node ast = (Node) parseController.getCurrentAst();

        //
        // Use a visitor to the AST to create new annotations
        // corresponding to foldable nodes
        //
        ast.visit(new FoldingVisitor(prsStream));

        //            
        // If the program contains more than one import declaration
        // that span multiple lines, fold them.
        //            
        List imports = ((SourceFile) ast).imports();
        if (imports.size() > 0)
        {
            Node first_import = (Node) imports.get(0),
                 last_import = (Node) imports.get(imports.size() - 1);
            
            //PORT1.7 get tokens from parseController, JPGPosition doesn't cache tokens                            
            IToken left=X10Util.getLeftToken(first_import.position(), parseController);
            IToken right=X10Util.getLeftToken(last_import.position(), parseController);
            makeAnnotation(left,right);
        }

        //            
        // Fold comments that span multiple lines.
        //            
        ArrayList adjuncts = prsStream.getAdjuncts();
        for (int i = 0; i < adjuncts.size();)
        {
            Adjunct adjunct = (Adjunct) adjuncts.get(i);

            IToken previous_token = prsStream.getIToken(adjunct.getTokenIndex()),
                   next_token = prsStream.getIToken(prsStream.getNext(previous_token.getTokenIndex())),
                   comments[] = previous_token.getFollowingAdjuncts();

            for (int k = 0; k < comments.length; k++)
            {
                Adjunct comment = (Adjunct) comments[k];
                if (comment.getEndLine() > comment.getLine())
                {
                    IToken gate_token = k + 1 < comments.length ? comments[k + 1] : next_token;
                    makeAnnotation(comment.getStartOffset(),
                                   gate_token.getLine() > comment.getEndLine()
                                       ? lexStream.getLineOffset(gate_token.getLine() - 1)
                                       : comment.getEndOffset());
                }
            }

            i += comments.length;
        }

        return;
    }

    /**
	 * Use this version of makeAnnotation(..) when you want to fold a text
	 * element that corresponds directly to an AST node
	 */
    private void makeAnnotation(IPrsStream ps, Node node)
    {
		//PORT1.7 no speical case for JPGPosition now. tokens are not cached in JPGPosition
	    makeAnnotation(node.position().offset(), node.position().endOffset());
    }   

    /**
     * Update the folding structure for a source text, where the text and its
     * AST are represented by a given parse controller and the folding structure
     * is represented by annotations in a given annotation model.  This is the
     * principal routine of the folding updater.
     * 
     * @param parseController		A parse controller through which the AST for
     *								the source text can be accessed
     * @param annotationModel		A structure of projection annotations that
     *								represent the foldable elements in the source
     *								text
     */
    public void updateFoldingStructure(IParseController parseController, ProjectionAnnotationModel annotationModel)
    {
        try
        {
            if (parseController.getCurrentAst() == null)
            {
                // note that the AST was invalid so that next time we
                // won't worry about previous annotations
                astWasInvalid = true;
                return; // return since we can't create annotations without an AST
            }
            if (astWasInvalid)
                astWasInvalid = false; // note that this time the AST was valid

            //
            // Map of annotations to positions; accumulates new annotations
            // and is used in updating the annotation model
            //
            newAnnotations = new HashMap();

            //
            // List of annotations; accumulates new annotations (disregarding
            // positions); serves as a list of keys for newAnnotations and
            // is used in comparing and listing annotations
            //
            annotations = new ArrayList();

            makeAnnotations(parseController);
            
            //
            // List the annotations in you're interested
            // dumpAnnotations(annotations, newAnnotations);
            //

            //
            // Update the annotation model if there have been changes
            // but not otherwise (since update leads to redrawing of the
            // source in the editor, which is likely to be unwelcome if
            // there haven't been any changes relevant to folding)
            //
            // Need to curtail calls to modifyAnnotations() because these lead
            // to calls to fireModelChanged(), which eventually lead to calls to
            // updateFoldingStructure, which lead back here, which would lead to
            // another call to modifyAnnotations() (unless those were curtailed)
            //
            if (oldAnnotationsList == null ||
                //
                // Check to see whether the current and previous annotations
                // differ in any significant way; if not, then there's no
                // reason to update the annotation model.
                // Note: This test may be implemented in various ways that may
                // be more or less simple, efficient, correct, etc. So it may
                // not work perfectly and may be subject to revision. (The
                // default test provided below is simplistic although quick and
                // usually effective.)
                //
                differ(oldAnnotationsList, (ArrayList) annotations))
            {
                oldAnnotationsList = (ArrayList) annotations; // Save annotations to compare for changes next time through
                annotationModel.modifyAnnotations(fOldAnnotations, newAnnotations, null);
            }

            //
            // Capture the latest set of annotations in a form that can be used the next
            // time that it is necessary to modify the annotations
            //
            fOldAnnotations = (Annotation[]) annotations.toArray(new Annotation[annotations.size()]);
        }
        catch (Exception e)
        {
            ErrorHandler.reportError("X10FoldingUpdater.updateFoldingStructure:  EXCEPTION", e);
        }
    }


    /*
     * A visitor for ASTs.  Its purpose is to create ProjectionAnnotations
     * for regions of text corresponding to various types of AST node or to
     * text ranges computed from AST nodes.  Projection annotations appear
     * in the editor as the widgets that control folding.
     */
    private class FoldingVisitor extends NodeVisitor    //AbstractVisitor
    {
    	private IPrsStream prsStream; // PORT1.7 will need prsStream to get tokens from nodes
        public FoldingVisitor(IPrsStream ps)
        {
            super();
            prsStream=ps;//PORT1.7 -- This should be valid over the life of this object
        }
                        
        // START_HERE
        public NodeVisitor enter(Node n)
        {
            if (n instanceof ClassDecl ||
                //n instanceof ValueClassDecl ||  //PORT1.7 ValueClassDecl no longer exists. do we need a replacement?
                n instanceof Initializer ||
                n instanceof ConstructorDecl ||
                n instanceof MethodDecl ||
                n instanceof Switch ||
                n instanceof SwitchBlock ||
                n instanceof Loop || // includes For, ForLoop, While, do
                n instanceof AtEach ||
                n instanceof ForEach ||
                n instanceof Now ||
                n instanceof Async ||
                n instanceof Atomic)
            {
                makeAnnotation(prsStream,n);
            }
            else if (n instanceof If)
            {
                Stmt true_part = ((If) n).consequent();
                Stmt else_part = ((If) n).alternative();
                //PORT1.7 -- token -- get token without using token cache in JPGPosition
                IToken last_true_part_token = X10Util.getRightToken(true_part.position(), prsStream);
                
                //PORT1.7 -- token -- get token without using token cache in JPGPosition
                makeAnnotation(X10Util.getLeftToken(n.position(), prsStream), last_true_part_token);
                
                if (else_part != null && (! (else_part instanceof If)))
                {
                	//PORT1.7 -- token -- get token without using token cache in JPGPosition
                    makeAnnotation(prsStream.getIToken(prsStream.getNext(last_true_part_token.getTokenIndex())),
                    		X10Util.getRightToken(else_part.position(), prsStream));
                }
            }
            else if (n instanceof Try)
            {
                Block try_block = ((Try) n).tryBlock();
                List catch_blocks = ((Try) n).catchBlocks();
                Block finally_block = ((Try) n).finallyBlock();

                //PORT1.7 -- token -- get token without using token cache in JPGPosition
                IToken last_token = X10Util.getRightToken(try_block.position(), prsStream);

                //PORT1.7 -- token -- get token without using token cache in JPGPosition
                makeAnnotation(X10Util.getLeftToken(n.position(),prsStream), last_token);
                for (int i = 0; i < catch_blocks.size(); i++)
                {
                    Catch catch_block = (Catch) catch_blocks.get(i);
                    IToken first_token = prsStream.getIToken(prsStream.getNext(last_token.getTokenIndex()));
                    //PORT1.7 -- token -- get token without using token cache in JPGPosition
                    last_token = X10Util.getRightToken(catch_block.position(), prsStream);
                    makeAnnotation(first_token, last_token);
                }

                if (finally_block != null)
                {
                    IToken first_token = prsStream.getIToken(prsStream.getNext(last_token.getTokenIndex()));
                    //PORT1.7 -- token -- get token without using token cache in JPGPosition
                    last_token=X10Util.getRightToken(finally_block.position(), prsStream);
                    makeAnnotation(first_token, last_token);
                }
            }
            else if (n instanceof When)
            {
                Stmt stmt = ((When) n).stmt();
                List stmts = ((When) n).stmts();
                
                //PORT1.7 -- token -- get token without using token cache in JPGPosition
                IToken last_token=X10Util.getRightToken(stmt.position(), prsStream);
                //PORT1.7 -- token -- get token without using token cache in JPGPosition
                makeAnnotation(X10Util.getLeftToken(n.position(), prsStream), last_token);
                for (int i = 0; i < stmts.size(); i++)
                {
                    stmt = (Stmt) stmts.get(i);
                    IToken first_token = prsStream.getIToken(prsStream.getNext(last_token.getTokenIndex()));
                    //PORT1.7 calculate token
                    last_token=X10Util.getRightToken(stmt.position(), prsStream);
                    makeAnnotation(first_token, last_token);
                }
            }
            return super.enter(n);
        }
    } // class FoldingVisitor
}
