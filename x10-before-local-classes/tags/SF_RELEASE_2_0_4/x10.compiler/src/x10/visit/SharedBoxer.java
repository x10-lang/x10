/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.X10Binary_c;
import x10.ast.X10Call_c;
import x10.ast.X10Conditional_c;
import x10.ast.X10If_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10New_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10Return_c;
import x10.types.X10ClassType;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10TypeSystem;
import x10.util.Synthesizer;

public class SharedBoxer extends ContextVisitor {
    private static final QName SHARED = QName.make("x10.compiler.Shared");
    
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;
    
    public SharedBoxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
    }
    
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        
        // shared var *** = +++ -> val Share[typeof***] *** = new Shared[typeof***](+++)
        if (n instanceof X10LocalDecl_c)
            return visitLocalDecl((X10LocalDecl_c) n);
        
        // *** = +++ -> ***.set(+++)
        if (n instanceof X10LocalAssign_c)
            return visitLocalAssign((X10LocalAssign_c) n);
        
        // *** not above case && (*** = ) -> ***.get()
        if (!(parent instanceof X10LocalAssign_c) && n instanceof X10Local_c) {
            X10Local_c local = (X10Local_c) n;
            if(X10Flags.toX10Flags(local.flags()).isShared()) {
                return createWrappingCall(local);
            }
        }
        
        return n;
    }
    
    private Node visitLocalDecl(X10LocalDecl_c n) {
        if (X10Flags.toX10Flags(n.flags().flags()).isShared()) {
            Flags f = n.flags().flags();
            try {
                Position position = n.position();
                Type type = xts.typeForName(SHARED);
                X10ClassType type2 = ((X10ClassType) type).typeArguments(Collections.singletonList(n.type().type()));
                Expr construct = xnf.New(position,xnf.CanonicalTypeNode(position, type2),Collections.singletonList(n.init()))
                .constructorInstance(xts.findConstructor(type2, xts.ConstructorMatcher(n.type().type(), Collections.singletonList(n.type().type()), context)))
                .type(type2);
                // clear shared flag and set final flag
                LocalDecl localDecl = xnf.LocalDecl(position,xnf.FlagsNode(position,X10Flags.toX10Flags(f).clearShared().Final()), xnf.CanonicalTypeNode(position, type2), n.name(), construct) 
                .localDef(xts.localDef(n.position(), n.flags().flags().Final(), Types.ref(type2), n.name().id()));
                return localDecl;
            } catch (SemanticException e) {
                throw new InternalCompilerError("Something is terribly wrong", e);
            }
        }
        return n;
    }

    private Node visitLocalAssign(X10LocalAssign_c n) {
        if (X10Flags.toX10Flags(n.local().flags()).isShared()) {
            Local local = n.local();
            Position position = n.position();
            Operator bo = n.operator().binaryOperator();
            Expr right = n.right();
            
            // if b is also shared, a += b -> a += b.get()
            if (right instanceof X10Local_c) {
                X10Local_c local2 = (X10Local_c) right;
                if (X10Flags.toX10Flags(local2.flags()).isShared())
                right = createWrappingCall(local2);
            }
            
            X10MethodInstance mi;
            try {
                Type type = xts.typeForName(SHARED);
                List<Type> typeArguments = ((X10ClassType) type).typeArguments();
                mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(right.type(), Name.make("set"), typeArguments, context));
            } catch (SemanticException e) {
                throw new InternalCompilerError("Something is terribly wrong", e);
            }
            if (bo == null) { // i.e. =         a = b -> a.set(b)
                return (Call) xnf.Call(position, local, xnf.Id(position, "set"), right).methodInstance(mi).type(right.type());
            } else {          // i.e. += *= ... a += b -> a.set(a.get() + b)
                return (Call) xnf.Call(position, local, xnf.Id(position, "set"), xnf.Binary(position, createWrappingCall(local), bo, right))
                .methodInstance(mi).type(right.type());
            }
        }
        return n;
    }

    private Call createWrappingCall(Local local) {
        X10MethodInstance mi;
        try {
            Type type = xts.typeForName(SHARED);
            mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(local.type(), Name.make("get"), Collections.EMPTY_LIST, context));
        } catch (SemanticException e) {
            throw new InternalCompilerError("Something is terribly wrong", e);
        }
        Call call = xnf.Call(local.position(), local, xnf.Id(local.position(), "get"));
        call = (Call) call.methodInstance(mi).type(local.type());
        return call;
    }
}
