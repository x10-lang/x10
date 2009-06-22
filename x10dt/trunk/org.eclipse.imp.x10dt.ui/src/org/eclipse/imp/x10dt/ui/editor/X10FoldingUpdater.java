package x10.uide.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.uide.core.ErrorHandler;
import org.eclipse.uide.editor.IFoldingUpdater;
import org.eclipse.uide.parser.IParseController;

//import x10.uide.parser.Ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.ast.Node;			// Added
import polyglot.visit.NodeVisitor;	// Added
import x10.parser.X10Parser.JPGPosition;

public class X10FoldingUpdater implements IFoldingUpdater {
    private Annotation[] fOldAnnotations;

	/*
	 * A visitor for ASTs.  Its purpose is to create ProjectionAnnotations
	 * for regions of text corresponding to various types of AST node or to
	 * text ranges computed from AST nodes.  Projection annotations appear
	 * in the editor as the widgets that control folding.
	 */
	private class FoldingVisitor extends NodeVisitor	//AbstractVisitor
	{
		// Maps new annotations to positions
	    HashMap newAnnotations = null;
	    // Lists new annotations, which are the keys for newAnnotations
	    List annotations = null;
	    
		public FoldingVisitor(HashMap newAnnotations, List annotations)
		{
			super();
			this.newAnnotations = newAnnotations;
			this.annotations = annotations;
		}
			
	    public void unimplementedVisitor(String s) { }
	    
	    // Use this version of makeAnnotation(..) when you want to fold
	    // a text element that corresponds directly to an AST node
	    private void makeAnnotation(Node n) {				//ASTNode n) {
			ProjectionAnnotation annotation= new ProjectionAnnotation();
			//int start= n.getLeftIToken().getStartOffset();
			//int len= n.getRightIToken().getEndOffset() - start;
			int start= ((JPGPosition)n.position()).getLeftIToken().getStartOffset();
			int len= ((JPGPosition)n.position()).getRightIToken().getEndOffset() - start;

			newAnnotations.put(annotation, new Position(start, len));
			annotations.add(annotation);
	    }    

	    // Use this version of makeAnnotation(..) when you want to fold
	    // a text element where the range does not correspond to that of
	    // an AST node
	    private void makeAnnotation(int start, int len) {
			ProjectionAnnotation annotation= new ProjectionAnnotation();
			newAnnotations.put(annotation, new Position(start, len));
			annotations.add(annotation);
	    }

	    
		// START_HERE
	    
   	    public NodeVisitor enter(Node n)
   	    {
            if (n instanceof ClassDecl_c)
            {
            	makeAnnotation(n);
            }

            else if (n instanceof Block_c)
            {
            	makeAnnotation(n);
            }
            
    		return super.enter(n);
   	    }

	
	}; // class FoldingVisitor
    

	// Used to support checking of whether annotations have
	// changed between invocations of updateFoldingStructure
	// (because, if they haven't, then it's probably best not
	// to update the folding structure)
	private ArrayList oldAnnotationsList = null;
	private boolean astWasInvalid = false;
	
	/**
	 * Update the folding structure for a source text, where the text and its
	 * AST are represented by a gven parse controller and the folding structure
	 * is represented by annotations in a given annotation model.  This is the
	 * principal routine of the folding updater.
	 * 
	 * @param parseController		A parse controller through which the AST for
	 *								the source text can be accessed
	 * @param annotationModel		A structure of projection annotations that
	 *								represent the foldable elements in the source
	 *								text
	 */
	public void updateFoldingStructure(
		IParseController parseController, ProjectionAnnotationModel annotationModel)
	{
		try {
			// Map of annotations to positions; accumulates new annotations
			// and is used in updating the annotation model
			final HashMap newAnnotations= new HashMap();

			// List of annotations; accumulates new annotations (disregarding
			// positions); serves as a list of keys for newAnnotations and
			// is used in comparing and listing annotations	
			final List annotations= new ArrayList();

			// The AST representing the foldable source text
			//ASTNode ast= (ASTNode) parseController.getCurrentAst();
			Node ast= (Node) parseController.getCurrentAst();
			
			if (ast == null) {
				// note that the AST was invalid so that next time we
				// won't worry about previous annotations
				astWasInvalid = true;
				// return since we can't create annotations without an AST
				return;
			}
			if (astWasInvalid) {
				// note that this time the AST was valid
				astWasInvalid = false;
			}
			
			// Use a visitor to the AST to create new annotations	
			// corresponding to foldable nodes
			//AbstractVisitor abstractVisitor = new FoldingVisitor(newAnnotations, annotations);
			//ast.accept(abstractVisitor);
			NodeVisitor nodeVisitor = new FoldingVisitor(newAnnotations, annotations);
			ast.visit(nodeVisitor);
			
			// List the annotations in you're interested
			//dumpAnnotations(annotations, newAnnotations);
	
			// Update the annotation model if there have been changes
			// but not otherwise (since update leads to redrawing of the
			// source in the editor, which is likely to be unwelcome if
			// there haven't been any changes relevant to folding)
			boolean updateNeeded = false;
			if (oldAnnotationsList == null) {
				// Should just be the first time through
				updateNeeded = true;
			} else {
				// Check to see whether the current and previous annotations
				// differ in any significant way; if not, then there's no
				// reason to update the annotation model.
				// Note:  This test may be implemented in various ways that may
				// be more or less simple, efficient, correct, etc.  So it may
				// not work perfectly and may be subject to revision.  (The
				// default test provided below is simplistic although quick and
				// usually effective.)
				updateNeeded = differ(oldAnnotationsList, (ArrayList) annotations);
			}
			if (updateNeeded) {
				// Save the current annotations to compare for changes
				// the next time through
				oldAnnotationsList = (ArrayList) annotations;
			}
		
			// Need to curtail calls to modifyAnnotations() because these lead to calls
			// to fireModelChanged(), which eventually lead to calls to updateFoldingStructure,
			// which lead back here, which would lead to another call to modifyAnnotations()
			// (unless those were curtailed)
			if (updateNeeded) {
				annotationModel.modifyAnnotations(fOldAnnotations, newAnnotations, null);
			} else {
			}
			
			// Capture the latest set of annotations in a form that can be used tne next
			// time that it is necessary to modify the annotations
			fOldAnnotations= (Annotation[]) annotations.toArray(new Annotation[annotations.size()]);
		} catch (Exception e) {
			ErrorHandler.reportError("JsdifFoldingUpdater.updateFoldingStructure:  EXCEPTION", e);
		}
    }


	// A simplistic test of whether two lists differ significantly.  This may
	// work well enough much of the time as the comparisons between lists should
	// be made very frequently, actually more frequently than the rate at which the
	// typical human user will edit the program text so as to affect the AST so as
	// to affect the lists.  Thus most changes of lists will entail some change in
	// the number of elements at some point that will be observed here.  Will not
	// work for certain very rapid edits of source text (e.g., rapid replacement
	// of elements).
	private boolean differ(ArrayList list1, ArrayList list2) {
		if (list1.size() != list2.size()) return true;
		return false;
	}
	
	
    private void dumpAnnotations(final List annotations, final HashMap newAnnotations)
    	{
		for(int i= 0; i < annotations.size(); i++) {
		    Annotation a= (Annotation) annotations.get(i);
		    Position p= (Position) newAnnotations.get(a);

		    if (p == null) {
		    	System.out.println("Annotation position is null");
		    	continue;
		    }
		    
		    System.out.println("Annotation @ " + p.offset + ":" + p.length);
		}
    }
	
}
