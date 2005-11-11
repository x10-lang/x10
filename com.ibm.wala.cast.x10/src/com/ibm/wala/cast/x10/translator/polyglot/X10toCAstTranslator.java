/*
 * Created on Sep 8, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.*;
import polyglot.ext.x10.types.FutureType;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.TypeSystem;

import com.ibm.capa.ast.CAstControlFlowMap;
import com.ibm.capa.ast.CAstEntity;
import com.ibm.capa.ast.CAstNode;
import com.ibm.capa.ast.CAstNodeTypeMap;
import com.ibm.capa.ast.CAstSourcePositionMap;
import com.ibm.capa.ast.CAstType;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotJava2CAstTranslator;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotTypeDictionary;
import com.ibm.domo.ast.java.translator.polyglot.TranslatingVisitor;
import com.ibm.domo.ast.x10.translator.X10CAstEntity;
import com.ibm.domo.ast.x10.translator.X10CastNode;
import com.ibm.domo.types.ClassLoaderReference;
import com.ibm.domo.types.TypeReference;

public class X10toCAstTranslator extends PolyglotJava2CAstTranslator {
    public X10toCAstTranslator(ClassLoaderReference clr, NodeFactory nf, TypeSystem ts) {
	super(clr, nf, ts);
    }

    protected TranslatingVisitor createTranslator() {
        return new X10TranslatingVisitorImpl();
    }

    protected PolyglotTypeDictionary createTypeDict() {
	return new X10TypeDictionary(fTypeSystem, this);
    }

    protected CAstEntity walkEntity(final Node rootNode, final WalkContext context) {
	if (rootNode instanceof Async || rootNode instanceof Future) {
	    Node body;
	    List/*<CAstEntity>*/ childEntities= new ArrayList();
	    final CodeBodyContext asyncContext= new CodeBodyContext(context, childEntities);

	    if (rootNode instanceof Async)
		body= ((Async) rootNode).body();
	    else
		body= ((Future) rootNode).body();

	    final CAstNode bodyAST= walkNodes(body, asyncContext);

	    return new AsyncEntity(childEntities, rootNode, asyncContext, bodyAST);
	} else
	    return super.walkEntity(rootNode, context);
    }

    private final class AsyncBodyType implements CAstType.Method {
	private final Node fNode;

	private final CodeBodyContext fContext;

	private AsyncBodyType(Node node, CodeBodyContext context) {
	    super();
	    fNode= node;
	    fContext= context;
	}

	public CAstType getReturnType() {
	    return getTypeDict().getCAstTypeFor(
		    (fNode instanceof Async) ?
			    fTypeSystem.Void() :
	    		    ((Future) fNode).type());
	}

	public List getArgumentTypes() {
	    return Collections.EMPTY_LIST;
	}

	public Collection getExceptionTypes() {
	    // TODO should figure out what exceptions can really be thrown by the body
	    return Collections.EMPTY_LIST;
	}

	public int getArgumentCount() {
	    return 0;
	}

	public String getName() {
	    return "<activity>";
	}

	public Collection getSupertypes() {
	    return Collections.singleton(getTypeDict().getCAstTypeFor(fTypeSystem.Object()));
	}

	public CAstType getDeclaringType() {
	    return getTypeDict().getCAstTypeFor(fContext.getEnclosingMethod().container());
	}
    }

    private final class AsyncEntity extends ParentEntity {
	private final Node fNode;

	private final CodeBodyContext fContext;

	private final CAstNode fBodyast;

	private AsyncEntity(List entities, Node node, CodeBodyContext context, CAstNode bodyast) {
	    super(entities);
	    fNode= node;
	    fContext= context;
	    fBodyast= bodyast;
	}

	public int getKind() {
	    return X10CAstEntity.ASYNC_BODY;
	}

	public String getName() {
	    return "<activity " + fNode.position().file() + ":" + fNode.position().line() + ":" + fNode.position().column() + ">";
	}

	public String[] getArgumentNames() {
	    return new String[] { "<place>" };
	}

	public int getArgumentCount() {
	    return 1;
	}

	public CAstNode getAST() {
	    return fBodyast;
	}

	public CAstControlFlowMap getControlFlow() {
	    return fContext.cfg();
	}

	public CAstSourcePositionMap getSourceMap() {
	    return fContext.pos();
	}

	public CAstNodeTypeMap getNodeTypeMap() {
	    return fContext.getNodeTypeMap();
	}

	public Collection getQualifiers() {
	    return Collections.EMPTY_LIST;
	}

	public CAstType getType() {
	    return new AsyncBodyType(fNode, fContext);
	}
	public String toString() {
	    return getName();
	}
    }

    class X10TranslatingVisitorImpl extends JavaTranslatingVisitorImpl implements X10TranslatorVisitor {
	public CAstNode visit(Async a, WalkContext context) {
	    CAstEntity bodyEntity= walkEntity(a, context);

	    context.addScopedEntity(bodyEntity);

	    return fFactory.makeNode(X10CastNode.ASYNC_INVOKE,
		    walkNodes(a.place(), context),
		    fFactory.makeConstant(bodyEntity));
	}

	public CAstNode visit(Finish f, WalkContext context) {
	    return fFactory.makeNode(CAstNode.UNWIND, 
		    fFactory.makeNode(CAstNode.BLOCK_STMT, 
			    fFactory.makeNode(X10CastNode.FINISH_ENTER),
			    walkNodes(f.body(), context)),
		    fFactory.makeNode(X10CastNode.FINISH_EXIT));
	}

	public CAstNode visit(ForEach f, WalkContext context) {
	    return null;
	}

	public CAstNode visit(AtEach a, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Future f, WalkContext context) {
	    CAstEntity bodyEntity= walkEntity(f, context);

	    context.addScopedEntity(bodyEntity);

	    return fFactory.makeNode(X10CastNode.ASYNC_INVOKE,
		    walkNodes(f.place(), context),
		    fFactory.makeConstant(bodyEntity));
	}

	public CAstNode visit(Call c, WalkContext wc) {
	    MethodInstance methodInstance= c.methodInstance();
	    ReferenceType methodOwner= methodInstance.container();

	    if (methodOwner instanceof FutureType) {
		FutureType type= (FutureType) methodOwner;
		TypeReference typeRef= TypeReference.findOrCreate(fClassLoaderRef, typeToTypeID(type.base()));

		return fFactory.makeNode(X10CastNode.FORCE, walkNodes(c.target(), wc), fFactory.makeConstant(typeRef));
	    } else
		return super.visit(c, wc);
	}

	public CAstNode visit(Region r, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Range r, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Point p, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Here h, WalkContext context) {
	    return fFactory.makeNode(X10CastNode.HERE);
	}

	public CAstNode visit(Next n, WalkContext context) {
	    return fFactory.makeNode(X10CastNode.NEXT);
	}

	public CAstNode visit(PlaceCast pc, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(When w, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(X10Formal f, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Clocked c, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Await a, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Atomic a, WalkContext context) {
	    return fFactory.makeNode(CAstNode.UNWIND, 
		    fFactory.makeNode(CAstNode.BLOCK_STMT, 
			    fFactory.makeNode(X10CastNode.ATOMIC_ENTER),
			    walkNodes(a.body(), context)),
		    fFactory.makeNode(X10CastNode.ATOMIC_EXIT));
	}

	public CAstNode visit(ArrayConstructor ac, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(GenParameterExpr gpe, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}
    }

    protected CAstNode walkNodes(Node n, WalkContext context) {
	if (n == null) return fFactory.makeNode(CAstNode.EMPTY);
	return X10ASTTraverser.visit(n, (X10TranslatorVisitor) getTranslator(), context);
    }
}
