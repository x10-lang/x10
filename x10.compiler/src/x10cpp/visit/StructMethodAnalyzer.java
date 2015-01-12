/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10cpp.visit;


import polyglot.ast.Allocation_c;
import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit;
import polyglot.ast.Branch;
import polyglot.ast.Conditional;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.FlagsNode_c;
import polyglot.ast.FloatLit;
import polyglot.ast.Id_c;
import polyglot.ast.Labeled_c;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.NumLit;
import polyglot.ast.Return;
import polyglot.frontend.Job;

import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode_c;
import x10.ast.AssignPropertyCall;
import x10.ast.AssignPropertyCall_c;
import x10.ast.ParExpr_c;
import x10.ast.StmtExpr_c;
import x10.ast.X10Binary_c;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10Field_c;
import x10.ast.X10Formal_c;
import x10.ast.X10If_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.ast.X10Special_c;
import x10.types.X10ClassType;
import x10.types.MethodInstance;
import polyglot.types.TypeSystem;
import x10.util.Synthesizer;

public class StructMethodAnalyzer extends ContextVisitor {
    private final TypeSystem xts;
    private final X10ClassType myContainer;
    
    // GACK:  This is ridiculous.  canGoInHeaderStream should be a simple boolean field,
    //        but polyglot insists on doing copying/cloning under the covers
    //        and the update to a simple instance field within leaveCall get lost.
    boolean[] canGoInHeaderStream = new boolean[] { true };
    
    public StructMethodAnalyzer(Job job, TypeSystem ts, NodeFactory nf, X10ClassType container) {
        super(job, ts, nf);
        myContainer = container;
        xts = (TypeSystem) ts;
    }
    
    public boolean canGoInHeaderStream() { return canGoInHeaderStream[0]; }
    
    private boolean isBuiltInNumeric(Type t) {
        return xts.typeBaseEquals(xts.Boolean(), t, context) || 
        xts.typeBaseEquals(xts.Byte(), t, context) || 
        xts.typeBaseEquals(xts.UByte(), t, context) || 
        xts.typeBaseEquals(xts.Char(), t, context) || 
        xts.typeBaseEquals(xts.Short(), t, context) || 
        xts.typeBaseEquals(xts.UShort(), t, context) || 
        xts.typeBaseEquals(xts.Int(), t, context) || 
        xts.typeBaseEquals(xts.UInt(), t, context) || 
        xts.typeBaseEquals(xts.Float(), t, context) || 
        xts.typeBaseEquals(xts.Long(), t, context) || 
        xts.typeBaseEquals(xts.ULong(), t, context) || 
        xts.typeBaseEquals(xts.Double(), t, context);
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        // Boilerplate to get to the body of the constructor/method
        if (n instanceof X10ConstructorDecl_c || n instanceof X10MethodDecl_c ||
                n instanceof Block_c || n instanceof Eval_c) {
            return n;
        }
        
        // Wrapper expressions/statements that by themselves are not a problem
        if (n instanceof Conditional || n instanceof Branch || n instanceof Return || 
                n instanceof X10If_c || n instanceof X10Binary_c || n instanceof ParExpr_c ||
                n instanceof StmtExpr_c || n instanceof Labeled_c ) {
            return n;
        }
        
        // Trivial nodes that will never by themselves prevent us from putting the body in the struct method class.
        if (n instanceof FlagsNode_c || n instanceof Id_c || 
                n instanceof X10Local_c || n instanceof LocalAssign_c || n instanceof AnnotationNode_c || 
                n instanceof X10CanonicalTypeNode_c || n instanceof X10Special_c || n instanceof Empty_c) {
            return n;
        }
        
        if (n instanceof X10Formal_c) {
            Type decType =  ((X10Formal_c)n).type().type();
            if (!(xts.typeBaseEquals(decType, myContainer, context) || isBuiltInNumeric(decType))) {
                canGoInHeaderStream[0] = false;
            }
            return n;
        }

        // Literals of built-in struct types and of null are fine.
        if (n instanceof BooleanLit || n instanceof FloatLit || n instanceof NullLit || n instanceof NumLit) {
            return n;
        }

        // Constructor call.
        // We can allow calls to the same class as we are analyzing.  
        // Any other constructor call will require us to set canBeInlined to false.
        if (n instanceof X10ConstructorCall_c) {
            ContainerType container = ((X10ConstructorCall_c)n).constructorInstance().container();
            if (!(xts.typeBaseEquals(container, myContainer, context))) {
                canGoInHeaderStream[0] = false;
            }
            return n;
        }
        
        // Can allow X10New_c of our container type, but nothing else
        if (n instanceof X10New_c) {
            X10New_c ne = (X10New_c)n;
            if (!xts.typeBaseEquals(ne.objectType().type(), myContainer, context)) {
                canGoInHeaderStream[0] = false;                
            }
            return n;
        }

        // Can allow Allocation_c of our container type, but nothing else
        if (n instanceof Allocation_c) {
            Allocation_c ne = (Allocation_c)n;
            if (!xts.typeBaseEquals(ne.type(), myContainer, context)) {
                canGoInHeaderStream[0] = false;                
            }
            return n;
        }

        // Only allow field assignments to fields of the current container; since those decls must be available.
        if (n instanceof X10FieldAssign_c) {
            X10FieldAssign_c fa = (X10FieldAssign_c)n;
            if (!xts.typeBaseEquals(fa.fieldInstance().container(), myContainer, context)) {
                canGoInHeaderStream[0] = false;
            }
            return n;
        }
        
        // Another way to get a field assign to a field of the current container.
        if (n instanceof AssignPropertyCall) {
            return n;
        }
        
        // Only allow LocalDecls of the type of the current container or a built-in primitive
        if (n instanceof X10LocalDecl_c) {
            Type decType =  ((X10LocalDecl_c)n).type().type();
            if (!(xts.typeBaseEquals(decType, myContainer, context) || isBuiltInNumeric(decType))) {
                canGoInHeaderStream[0] = false;
            }
            return n;
        }
               
        // Only allow field reads of fields of the current container; those decls must be available.
        if (n instanceof X10Field_c) {
            X10Field_c fr = (X10Field_c)n;
            if (!xts.typeBaseEquals(fr.fieldInstance().container(), myContainer, context)) {
                canGoInHeaderStream[0] = false;
            }
            return n;
        }
        
        // Only allow calls to methods of the current container.
        if (n instanceof X10Call_c) {
            ContainerType methodType = ((X10Call_c)n).methodInstance().container();
            if (!(xts.typeBaseEquals(methodType, myContainer, context))) {
                canGoInHeaderStream[0] = false;
            }
            return n;    
        }
        
        // Discovering any other AST in the body means that we don't want to put the method in the header file.
        canGoInHeaderStream[0] = false;
        
        return n;
    }
}
