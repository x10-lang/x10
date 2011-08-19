package x10.wala.tree.visit;

import x10.wala.tree.X10CAstEntity;
import x10.wala.tree.X10CastNode;

import com.ibm.wala.cast.tree.*;
import com.ibm.wala.cast.tree.visit.*;

/**
 * @author Igor Peshansky
 * Extend DelegatingCAstVisitor to accommodate X10 CAst nodes and entities.
 * Doesn't work for visit/leaveEntity, visit/leaveNode, visit/leaveAssign...
 * TODO: document me.
 */
public abstract class X10DelegatingCAstVisitor extends DelegatingCAstVisitor {

    /**
     * X10CAstVisitor constructor.
     * Needs to have a valid (non-null) delegate visitor.
     * @param delegate the visitor to delegate to for default implementation
     */
    protected X10DelegatingCAstVisitor(CAstVisitor delegate) {
      super(delegate);
    }

    /**
     * X10CAstVisitor default constructor.
     */
    protected X10DelegatingCAstVisitor() {
      this(new JavaCAstVisitor());
    }

    /**
     * A no-op Java CAst visitor.
     */
    private static final class JavaCAstVisitor extends CAstVisitor { }

    /**
     * Entity processing hook; sub-classes are expected to override if they
     * introduce new entity types.
     * Should invoke super.doVisitEntity() for unprocessed entities.
     * @return true if entity was handled
     */
    protected boolean doVisitEntity(CAstEntity n, Context context, CAstVisitor visitor_) {
	X10DelegatingCAstVisitor visitor = (X10DelegatingCAstVisitor)visitor_;
	switch (n.getKind()) {
	    case X10CAstEntity.ASYNC_BODY: {
		Context codeContext = visitor.makeCodeContext(context, n);
		visitor.visitAsyncBodyEntity(n, context, codeContext, visitor);
		// visit the AST if any
		if (n.getAST() != null)
		    visitor.visit(n.getAST(), codeContext, visitor);
		// process any remaining scoped children
		visitor.visitScopedEntities(n, n.getScopedEntities(null), codeContext, visitor);
		visitor.leaveAsyncBodyEntity(n, context, codeContext, visitor);
		break;
	    }
	    case X10CAstEntity.CLOSURE_BODY: {
		Context codeContext = visitor.makeCodeContext(context, n);
		visitor.visitClosureBodyEntity(n, context, codeContext, visitor);
		// visit the AST if any
		if (n.getAST() != null)
		    visitor.visit(n.getAST(), codeContext, visitor);
		// process any remaining scoped children
		visitor.visitScopedEntities(n, n.getScopedEntities(null), codeContext, visitor);
		visitor.leaveClosureBodyEntity(n, context, codeContext, visitor);
		break;
	    }
	    default:
		return super.doVisitEntity(n, context, visitor);
	}
	return true;
    }

    /**
     * Visit an AsyncBody entity.
     * @param n the entity to process
     * @param context a visitor-specific context
     * @param codeContext a visitor-specific context for this script
     * @return true if no further processing is needed
     */
    protected boolean visitAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) { return visitor.visitEntity(n, context, visitor); }
    /**
     * Leave an AsyncBody entity.
     * @param n the entity to process
     * @param context a visitor-specific context
     * @param codeContext a visitor-specific context for this script
     */
    protected void leaveAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) { visitor.leaveEntity(n, context, visitor); }

    /**
     * Visit an AsyncBody entity.
     * @param n the entity to process
     * @param context a visitor-specific context
     * @param codeContext a visitor-specific context for this script
     * @return true if no further processing is needed
     */
    protected boolean visitClosureBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) { return visitor.visitEntity(n, context, visitor); }
    /**
     * Leave an AsyncBody entity.
     * @param n the entity to process
     * @param context a visitor-specific context
     * @param codeContext a visitor-specific context for this script
     */
    protected void leaveClosureBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) { visitor.leaveEntity(n, context, visitor); }

    /**
     * Node processing hook; sub-classes are expected to override if they
     * introduce new node types.
     * Should invoke super.doVisit() for unprocessed nodes.
     * @return true if node was handled
     */
    protected boolean doVisit(CAstNode n, Context context, CAstVisitor visitor_) {
	X10DelegatingCAstVisitor visitor = (X10DelegatingCAstVisitor)visitor_;
	switch (n.getKind()) {
	    case X10CastNode.ASYNC: {
		if (visitor.visitAsyncInvoke(n, context, visitor))
		    break;
		for(int i = 0; i < n.getChildCount(); i++) {
			visitor.visit(n.getChild(i), context, visitor);
		}
		visitor.leaveAsyncInvoke(n, context, visitor);
		break;
	    }
	    case X10CastNode.ATOMIC_ENTER: {
		if (visitor.visitAtomicEnter(n, context, visitor))
		    break;
		visitor.leaveAtomicEnter(n, context, visitor);
		break;
	    }
	    case X10CastNode.ATOMIC_EXIT: {
		if (visitor.visitAtomicExit(n, context, visitor))
		    break;
		visitor.leaveAtomicExit(n, context, visitor);
		break;
	    }
	    case X10CastNode.FINISH_ENTER: {
		if (visitor.visitFinishEnter(n, context, visitor))
		    break;
		visitor.leaveFinishEnter(n, context, visitor);
		break;
	    }
	    case X10CastNode.FINISH_EXIT: {
		if (visitor.visitFinishExit(n, context, visitor))
		    break;
		visitor.leaveFinishExit(n, context, visitor);
		break;
	    }
	    case X10CastNode.ITER_INIT: {
		if (visitor.visitIterInit(n, context, visitor))
		    break;
		visitor.visit(n.getChild(0), context, visitor);
		visitor.leaveIterInit(n, context, visitor);
		break;
	    }
	    case X10CastNode.ITER_HASNEXT: {
		if (visitor.visitIterHasNext(n, context, visitor))
		    break;
		visitor.visit(n.getChild(0), context, visitor);
		visitor.leaveIterHasNext(n, context, visitor);
		break;
	    }
	    case X10CastNode.ITER_NEXT: {
		if (visitor.visitIterNext(n, context, visitor))
		    break;
		visitor.visit(n.getChild(0), context, visitor);
		visitor.leaveIterNext(n, context, visitor);
		break;
	    }
	    case X10CastNode.HERE: {
		if (visitor.visitHere(n, context, visitor))
		    break;
		visitor.leaveHere(n, context, visitor);
		break;
	    }
	    case X10CastNode.NEXT: {
		visitor.visitNext(n, context, visitor);
		break;
	    }
	    case X10CastNode.ARRAY_REF_BY_POINT: {
		if (visitor.visitArrayRef(n, context, visitor))
		    break;
		visitor.visit(n.getChild(0), context, visitor); // the array expr
		// n.getChild(1) is a TypeReference for the array element type...
		visitor.visit(n.getChild(2), context, visitor); // the index expr
		visitor.leaveArrayRef(n, context, visitor);
		break;
	    }
	    case X10CastNode.TUPLE: {
	        if (visitor.visitTupleExpr(n, context, visitor))
	            break;
                // n.getChild(0) is a TypeReference for the tuple element type...
	        for(int i=1; i < n.getChildCount(); i++) {
	            visitor.visit(n.getChild(i), context, visitor);
	        }
	        visitor.leaveTupleExpr(n, context, visitor);
	        break;
	    }
	    case X10CastNode.AT_STMT_ENTER:
	        if (visitor.visitAtStmtEnter(n, context, visitor))
	            break;
	        visitor.leaveAtStmtEnter(n, context, visitor);
	    case X10CastNode.AT_STMT_EXIT:
	        if (visitor.visitAtStmtExit(n, context, visitor))
	            break;
	        visitor.leaveAtStmtExit(n, context, visitor);
	    default:
		return super.doVisit(n, context, visitor);
	}
	return true;
    }

    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitAtomicEnter(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveAtomicEnter(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitAtomicExit(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveAtomicExit(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitFinishEnter(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveFinishEnter(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitFinishExit(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveFinishExit(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitForce(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveForce(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitIterInit(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveIterInit(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitIterHasNext(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveIterHasNext(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitIterNext(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveIterNext(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitHere(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave a here node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveHere(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit an Async node.
     * @param n the node to process
     * @param c a visitor-specific context
     * @return true if no further processing is needed
     */
    protected boolean visitNext(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave a PLACE_OF_POINT node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leavePlaceOfPoint(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    /**
     * Visit a PLACE_OF_POINT node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected boolean visitPlaceOfPoint(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Visit a TUPLE_EXPR node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected boolean visitTupleExpr(CAstNode n, Context c, CAstVisitor visitor) { return visitor.visitNode(n, c, visitor); }
    /**
     * Leave a TUPLE_EXPR node.
     * @param n the node to process
     * @param c a visitor-specific context
     */
    protected void leaveTupleExpr(CAstNode n, Context c, CAstVisitor visitor) { visitor.leaveNode(n, c, visitor); }
    
    /**
     * Visits the AtStmt node at the beginning of the block code.
     * 
     * @param node The node to process.
     * @param context A visitor-specific context.
     * @param visitor The CAst visitor for redirection.
     * @return True if no further processing is needed, false otherwise.
     */
    protected boolean visitAtStmtEnter(final CAstNode node, final Context context, final CAstVisitor visitor) {
      return visitor.visitNode(node, context, visitor);
    }
    
    /**
     * Leaves the AtStmt node at the beginning of the block code.
     * 
     * @param node The node to process.
     * @param context A visitor-specific context.
     * @param visitor The CAst visitor for redirection.
     */
    protected void leaveAtStmtEnter(final CAstNode node, final Context context, final CAstVisitor visitor) {
      visitor.leaveNode(node, context, visitor);
    }
    
    /**
     * Visits the AtStmt node at the end of the block code.
     * 
     * @param node The node to process.
     * @param context A visitor-specific context.
     * @param visitor The CAst visitor for redirection.
     * @return True if no further processing is needed, false otherwise.
     */
    protected boolean visitAtStmtExit(final CAstNode node, final Context context, final CAstVisitor visitor) {
      return visitor.visitNode(node, context, visitor);
    }
    
    /**
     * Leaves the AtStmt node at the end of the block code.
     * 
     * @param node The node to process.
     * @param context A visitor-specific context.
     * @param visitor The CAst visitor for redirection.
     */
    protected void leaveAtStmtExit(final CAstNode node, final Context context, final CAstVisitor visitor) {
      visitor.leaveNode(node, context, visitor);
    }
}
