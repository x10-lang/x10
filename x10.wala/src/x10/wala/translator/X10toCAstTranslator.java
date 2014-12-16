/*
 * Created on Sep 8, 2005
 */
package x10.wala.translator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.ArrayInit;
import polyglot.ast.ArrayTypeNode;
import polyglot.ast.Assert;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Branch;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Case;
import polyglot.ast.Cast;
import polyglot.ast.Catch;
import polyglot.ast.CharLit;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassLit;
import polyglot.ast.ClassMember;
import polyglot.ast.Conditional;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Do;
import polyglot.ast.Empty;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.FloatLit;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.Import;
import polyglot.ast.Initializer;
import polyglot.ast.Instanceof;
import polyglot.ast.IntLit;
import polyglot.ast.Labeled;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalClassDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.NewArray;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.Switch;
import polyglot.ast.SwitchBlock;
import polyglot.ast.SwitchElement;
import polyglot.ast.Throw;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.Try;
import polyglot.ast.Unary;
import polyglot.ast.While;
import polyglot.ast.Binary.Operator;
import polyglot.ast.ConstructorCall.Kind;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.InitializerInstance;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.JavaArrayType;

import polyglot.types.Name;
import polyglot.types.ObjectType;
import polyglot.types.ProcedureInstance;
import polyglot.types.QName;
import polyglot.types.SemanticException;

import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.ast.AssignPropertyCall;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Clocked;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.Finish;
import x10.ast.ForLoop;
import x10.ast.Here;
import x10.ast.LocalTypeDef;
import x10.ast.Next;
import x10.ast.ParExpr;
import x10.ast.SettableAssign;
import x10.ast.StmtSeq;
import x10.ast.Tuple;
import x10.ast.TypeDecl;
import x10.ast.When;
import x10.ast.When_c;
import x10.ast.X10Formal;
import x10.ast.X10Loop;
import x10.types.FunctionType;
import x10.types.MethodInstance;
import x10.types.MethodInstance_c;
import x10.types.ParametrizedType;
import x10.wala.tree.X10CAstEntity;
import x10.wala.tree.X10CastNode;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

import com.ibm.wala.cast.ir.translator.AstTranslator.InternalCAstSymbol;
import com.ibm.wala.cast.java.loader.Util;
import com.ibm.wala.cast.java.translator.JavaProcedureEntity;
import com.ibm.wala.cast.java.translator.TranslatorToCAst;
import com.ibm.wala.cast.java.types.JavaType;
import com.ibm.wala.cast.tree.CAst;
import com.ibm.wala.cast.tree.CAstControlFlowMap;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.cast.tree.CAstNodeTypeMap;
import com.ibm.wala.cast.tree.CAstQualifier;
import com.ibm.wala.cast.tree.CAstSourcePositionMap;
import com.ibm.wala.cast.tree.CAstSymbol;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.tree.CAstTypeDictionary;
import com.ibm.wala.cast.tree.impl.AbstractSourcePosition;
import com.ibm.wala.cast.tree.impl.CAstCloner;
import com.ibm.wala.cast.tree.impl.CAstControlFlowRecorder;
import com.ibm.wala.cast.tree.impl.CAstImpl;
import com.ibm.wala.cast.tree.impl.CAstNodeTypeMapRecorder;
import com.ibm.wala.cast.tree.impl.CAstOperator;
import com.ibm.wala.cast.tree.impl.CAstRewriter;
import com.ibm.wala.cast.tree.impl.CAstSourcePositionRecorder;
import com.ibm.wala.cast.tree.impl.CAstSymbolImpl;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.EmptyIterator;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.IteratorPlusOne;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.strings.Atom;

public class X10toCAstTranslator implements TranslatorToCAst {
    protected final CAst fFactory = new CAstImpl();

    protected final NodeFactory fNodeFactory;

    protected final TypeSystem fTypeSystem;

    protected Type fNPEType;

    protected Type fCCEType;

    protected Type fEType; // Exception

    protected Type fDivByZeroType;

    protected final ClassLoaderReference fClassLoaderRef;

    private CAstTypeDictionary fTypeDict;

    private X10TranslatorVisitor fTranslator;

    protected X10IdentityMapper fIdentityMapper;

    protected boolean replicateForDoLoops = false;
    
    protected final boolean DEBUG = true;

    final protected X10TranslatorVisitor getTranslator() {
      if (fTranslator == null)
        fTranslator = createTranslator();
      return fTranslator;
    }
    
    protected CAstTypeDictionary getTypeDict() {
      if (fTypeDict == null) {
        fTypeDict = createTypeDict();
      }
      return fTypeDict;
    }

    protected X10TypeDictionary createTypeDict() {
      return new X10TypeDictionary(fTypeSystem, this);
    }

    protected static CAstOperator mapUnaryOpcode(Unary.Operator operator) {
      if (operator.equals(Unary.BIT_NOT))
        return CAstOperator.OP_BITNOT;
      if (operator.equals(Unary.NEG))
        return CAstOperator.OP_SUB; // CAst will handle OP_SUB with only 1
      // arg properly !!! Hah!
      if (operator.equals(Unary.NOT))
        return CAstOperator.OP_NOT;
      if (operator.equals(Unary.POS))
        return CAstOperator.OP_ADD; // CAst will throw away OP_ADD with only
      // 1 arg!!!

      if (operator.equals(Unary.POST_DEC))
        return CAstOperator.OP_SUB; // translator will produce different
      // CAstNode types for post dec
      if (operator.equals(Unary.POST_INC))
        return CAstOperator.OP_ADD; // translator will produce different
      // CAstNode types for post inc
      if (operator.equals(Unary.PRE_DEC))
        return CAstOperator.OP_SUB; // translator will produce different
      // CAstNode types for pre dec
      if (operator.equals(Unary.PRE_INC))
        return CAstOperator.OP_ADD; // translator will produce different
      // CAstNode types for pre inc

      Assertions.UNREACHABLE("Java2CAstTranslator.JavaTranslatingVisitorImpl.mapUnaryOpcode(): unrecognized unary operator.");
      return null;
    }

    protected static CAstOperator mapBinaryOpcode(Binary.Operator operator) {
      if (operator.equals(Binary.ADD))
        return CAstOperator.OP_ADD;
      if (operator.equals(Binary.BIT_AND))
        return CAstOperator.OP_BIT_AND;
      if (operator.equals(Binary.BIT_OR))
        return CAstOperator.OP_BIT_OR;
      if (operator.equals(Binary.BIT_XOR))
        return CAstOperator.OP_BIT_XOR;
      if (operator.equals(Binary.COND_AND))
        return CAstOperator.OP_REL_AND;
      if (operator.equals(Binary.COND_OR))
        return CAstOperator.OP_REL_OR;
      if (operator.equals(Binary.DIV))
        return CAstOperator.OP_DIV;
      if (operator.equals(Binary.EQ))
        return CAstOperator.OP_EQ;
      if (operator.equals(Binary.GE))
        return CAstOperator.OP_GE;
      if (operator.equals(Binary.GT))
        return CAstOperator.OP_GT;
      if (operator.equals(Binary.LE))
        return CAstOperator.OP_LE;
      if (operator.equals(Binary.LT))
        return CAstOperator.OP_LT;
      if (operator.equals(Binary.MOD))
        return CAstOperator.OP_MOD;
      if (operator.equals(Binary.MUL))
        return CAstOperator.OP_MUL;
      if (operator.equals(Binary.NE))
        return CAstOperator.OP_NE;
      if (operator.equals(Binary.SHL))
        return CAstOperator.OP_LSH;
      if (operator.equals(Binary.SHR))
        return CAstOperator.OP_RSH;
      if (operator.equals(Binary.SUB))
        return CAstOperator.OP_SUB;
      if (operator.equals(Binary.USHR))
        return CAstOperator.OP_URSH;
      Assertions.UNREACHABLE("Java2CAstTranslator.JavaTranslatingVisitorImpl.mapBinaryOpcode(): unrecognized binary operator.");
      return null;
    }

    protected CAstNode translateConstant(x10.types.constants.ConstantValue constant) {
      return fFactory.makeConstant(constant.toJavaObject());
    }

    protected class JavaTranslatingVisitorImpl {
      public CAstNode visit(MethodDecl m, WalkContext mc) {
        if (m.body() == null || m.body().statements().size() == 0)
          return makeNode(mc, fFactory, m, CAstNode.RETURN);
        else
          return walkNodes(m.body(), mc);
      }

      public CAstNode visit(ConstructorDecl cd, WalkContext mc) {
        // Needs to examine the initializers in the ClassContext
        // and glue that code into the right place relative to the
        // constructor method body ("wherever that may turn out to be").
        List/* <FieldDecl|Initializer> */inits = mc.getInitializers();
        Block body = cd.body();

        if (hasSuperCall(body)) {
          // Split at call to super:
          // super();
          // field initializer code
          // remainder of ctor body
          CAstNode[] bodyNodes = new CAstNode[inits.size() + body.statements().size()];

          int idx = 0;
          for (Iterator iter = body.statements().iterator(); iter.hasNext();) {
            Stmt s = (Stmt) iter.next();

            bodyNodes[idx++] = walkNodes(s, mc);
            if (idx == 1) {
              Assertions.productionAssertion(isSpecialCallStmt(s, ConstructorCall.SUPER));
              idx = insertInitializers(mc, bodyNodes, false, idx);
            }
          }
          return makeNode(mc, fFactory, body, CAstNode.BLOCK_STMT, bodyNodes);
        } else if (hasThisCall(body)) {
          return walkNodes(body, mc);
        } else {
          // add explicit call to default super()
          // RMF 4/17/2009- The following search for a superClass default ctor
          // won't work if we process Object in source. In particular, the
          // superClass might be null. In that case, simply omit the explicit
          // super() call.
          ClassType superClass = (ClassType) ((ObjectType) cd.constructorDef().asInstance().container()).superClass();
          CAstNode[] bodyNodes;
          int idx= 0;
          int bodyInitsSize= inits.size() + body.statements().size();
          if (superClass != null) {
            ProcedureInstance defaultSuperCtor = findDefaultCtor(superClass);
            CallSiteReference callSiteRef = CallSiteReference.make(0, fIdentityMapper.getMethodRef(defaultSuperCtor),
                IInvokeInstruction.Dispatch.SPECIAL);
            CAstNode superCall = makeNode(mc, fFactory, cd, CAstNode.CALL,
                makeNode(mc, fFactory, cd, CAstNode.SUPER),
                fFactory.makeConstant(callSiteRef));
            bodyNodes = new CAstNode[bodyInitsSize + 1];
            bodyNodes[idx++] = superCall;
          } else { // no super class, so no super call
            bodyNodes = new CAstNode[bodyInitsSize];
          }
          idx= insertInitializers(mc, bodyNodes, false, idx);

          for (Iterator iter = body.statements().iterator(); iter.hasNext(); idx++) {
            Stmt s = (Stmt) iter.next();
            bodyNodes[idx] = walkNodes(s, mc);
          }
          return makeNode(mc, fFactory, body, CAstNode.BLOCK_STMT, bodyNodes);
        }
      }

      private ProcedureInstance findDefaultCtor(ClassType superClass) {
        List/* <ProcedureInstance> */ctors = superClass.constructors();
        for (Iterator iter = ctors.iterator(); iter.hasNext();) {
          ConstructorInstance ctor = (ConstructorInstance) iter.next();
          if (ctor.formalTypes().isEmpty())
            return ctor;
        }
        Assertions.UNREACHABLE("Couldn't find default ctor");
        return null;
      }

      public CAstNode visit(FieldDecl f, WalkContext ctorContext) {
        // Generate CAST node for the initializer (init())
        // Type targetType = f.memberInstance().container();
        // Type fieldType = f.type().type();
        FieldReference fieldRef = fIdentityMapper.getFieldRef(f.fieldDef().asInstance());
        // We use null to indicate an OBJECT_REF to a static field, as the
        // FieldReference doesn't
        // hold enough info to determine this. In this case, (unlike field ref)
        // we don't have a
        // target expr to evaluate.
        CAstNode thisNode = f.flags().flags().isStatic() ? makeNode(ctorContext, fFactory, null, CAstNode.VOID) : makeNode(ctorContext,
            fFactory, f, CAstNode.THIS);
        CAstNode lhsNode = makeNode(ctorContext, fFactory, f, CAstNode.OBJECT_REF, thisNode, fFactory.makeConstant(fieldRef));

        Expr init = f.init();
        CAstNode rhsNode;
        if (init instanceof ArrayInit) {
          rhsNode = visit((ArrayInit) init, ctorContext, f.declType());
        } else {
          rhsNode = walkNodes(init, ctorContext);
        }
        CAstNode assNode = makeNode(ctorContext, fFactory, f, CAstNode.ASSIGN, lhsNode, rhsNode);

        return assNode;
      }

      public CAstNode visit(Import i, WalkContext wc) {
        Assertions.UNREACHABLE("walkNodes.ASTTraverser.visit(Import)");
        return null;
      }

      public CAstNode visit(PackageNode p, WalkContext wc) {
        Assertions.UNREACHABLE("walkNodes.ASTTraverser.visit(PackageNode)");
        return null;
      }

      public CAstNode visit(CanonicalTypeNode ctn, WalkContext wc) {
        // We'll take care of this in its surrounding context...
        return makeNode(wc, fFactory, null, CAstNode.EMPTY);
      }

      public CAstNode visit(ArrayTypeNode ctn, WalkContext wc) {
        // We'll take care of this in its surrounding context...
        Assertions.UNREACHABLE("walkNodes.ASTTraverser.visit(CanonicalTypeNode)");
        return null;
      }

      /*
       * If we've handled all the parent cases, this should never be called --
       * visit(ArrayInit,WalkContext,Type) should be instead.
       */
      public CAstNode visit(ArrayInit ai, WalkContext wc) {
        if (((JavaArrayType) ai.type()).base().isNull()) {
          Assertions.productionAssertion(false, "bad type " + ai.type() + " for " + ai + " at " + ai.position());
        }

        TypeReference newTypeRef = fIdentityMapper.getTypeRef(ai.type());
        CAstNode[] eltNodes = new CAstNode[ai.elements().size() + 1];
        int idx = 0;

        eltNodes[idx++] = makeNode(wc, fFactory, ai, CAstNode.NEW, fFactory.makeConstant(newTypeRef), fFactory.makeConstant(ai
            .elements().size()));
        for (Iterator iter = ai.elements().iterator(); iter.hasNext(); idx++) {
          Expr element = (Expr) iter.next();
          if ( element instanceof ArrayInit ) {
            eltNodes[idx] = visit((ArrayInit)element, wc, ((JavaArrayType)ai.type()).base());
          } else {
            eltNodes[idx] = walkNodes(element, wc);
          }
          if (eltNodes[idx] == null) {
            Assertions.productionAssertion(eltNodes[idx] != null, element.toString());
          }
        }

        return makeNode(wc, fFactory, ai, CAstNode.ARRAY_LITERAL, eltNodes);
      }

      /*
       * Workaround for the null array init bug: just in case ai.type().base() is null (e.g.
       * "new Object[] {null}") we get the type from the parent (in this example, a
       * NewArray of type Object[])
       */
      public CAstNode visit(ArrayInit ai, WalkContext wc, Type t) {
        TypeReference newTypeRef = fIdentityMapper.getTypeRef(t);
        CAstNode[] eltNodes = new CAstNode[ai.elements().size() + 1];
        int idx = 0;

        eltNodes[idx++] = makeNode(wc, fFactory, ai, CAstNode.NEW, fFactory.makeConstant(newTypeRef), fFactory.makeConstant(ai
            .elements().size()));
        for (Iterator iter = ai.elements().iterator(); iter.hasNext(); idx++) {
          Expr element = (Expr) iter.next();
          if (element instanceof ArrayInit) {
            eltNodes[idx] = visit((ArrayInit) element, wc, ((JavaArrayType) t).base());
          } else {
            eltNodes[idx] = walkNodes(element, wc);
          }
          if (eltNodes[idx] == null) {
            Assertions.productionAssertion(eltNodes[idx] != null, element.toString());
          }
        }

        return makeNode(wc, fFactory, ai, CAstNode.ARRAY_LITERAL, eltNodes);
      }

      public CAstNode visit(ArrayAccessAssign aaa, WalkContext wc) {
        return processAssign(aaa, wc);
      }

      public CAstNode visit(FieldAssign fa, WalkContext wc) {
        return processAssign(fa, wc);
      }

      public CAstNode visit(LocalAssign la, WalkContext wc) {
        return processAssign(la, wc);
      }

      protected CAstNode processAssign(Node assign, CAstNode lhsCAstNode, Assign.Operator operator, Expr rhs, WalkContext wc) {
        if (operator == Assign.ASSIGN) {
          return makeNode(wc, fFactory, assign, CAstNode.ASSIGN, lhsCAstNode, walkNodes(rhs, wc));
        } else {
          CAstNode op =
            makeNode(wc, 
                fFactory, 
                assign, 
                CAstNode.ASSIGN_PRE_OP,
                lhsCAstNode,
                walkNodes(rhs, wc),
                mapAssignOperator(operator));

          if (rhs.type().isLongOrLess() &&
              (mapAssignOperator(operator)==CAstOperator.OP_DIV ||
               mapAssignOperator(operator)==CAstOperator.OP_MOD))
          {
            Collection excTargets = wc.getCatchTargets(fDivByZeroType);
            if (!excTargets.isEmpty()) {
              for (Iterator iterator = excTargets.iterator(); iterator.hasNext();) {
                Pair catchPair = (Pair) iterator.next();
                wc.cfg().add(op, catchPair.snd, fDivByZeroType);
              }
            } else {
              wc.cfg().add(op, 
                  CAstControlFlowMap.EXCEPTION_TO_EXIT, 
                  fDivByZeroType);
            }
          }

          return op;
        }
      }

      private CAstNode processAssign(Assign a, WalkContext wc) {
        Expr lhs= a.left().type(a.type()); // PORT1.7 An Assign no longer has a lhs Expr per se; but you can ask it to materialize one...
        WalkContext lvc = new AssignmentContext(wc);

        return processAssign(a, walkNodes(lhs, lvc), a.operator(), a.right(), wc);
      }

      protected CAstOperator mapAssignOperator(Assign.Operator op) {
        if (op == Assign.ADD_ASSIGN)
          return CAstOperator.OP_ADD;
        else if (op == Assign.BIT_AND_ASSIGN)
          return CAstOperator.OP_BIT_AND;
        else if (op == Assign.BIT_OR_ASSIGN)
          return CAstOperator.OP_BIT_OR;
        else if (op == Assign.BIT_XOR_ASSIGN)
          return CAstOperator.OP_BIT_XOR;
        else if (op == Assign.DIV_ASSIGN)
          return CAstOperator.OP_DIV;
        else if (op == Assign.MOD_ASSIGN)
          return CAstOperator.OP_MOD;
        else if (op == Assign.MUL_ASSIGN)
          return CAstOperator.OP_MUL;
        else if (op == Assign.SHL_ASSIGN)
          return CAstOperator.OP_LSH;
        else if (op == Assign.SHR_ASSIGN)
          return CAstOperator.OP_RSH;
        else if (op == Assign.SUB_ASSIGN)
          return CAstOperator.OP_SUB;
        else if (op == Assign.USHR_ASSIGN)
          return CAstOperator.OP_URSH;
        Assertions.UNREACHABLE("Unknown assignment operator");
        return null;
      }

      public CAstNode visit(Binary b, WalkContext wc) {
        Expr left = b.left();
        Expr right = b.right();
        Operator operator = b.operator();

        if (operator.equals(Binary.COND_AND))
          return makeNode(wc, fFactory, b, CAstNode.IF_EXPR, walkNodes(left, wc), walkNodes(right, wc), fFactory.makeConstant(false));
        else if (operator.equals(Binary.COND_OR))
          return makeNode(wc, fFactory, b, CAstNode.IF_EXPR, walkNodes(left, wc), fFactory.makeConstant(true), walkNodes(right, wc));
        else {
      Type leftType = left.type();
      Type rightType = right.type();
      if (leftType.isJavaPrimitive() && rightType.isJavaPrimitive()) {
        CAstNode leftNode = walkNodes(left, wc);
        CAstNode rightNode = walkNodes(right, wc);
        
        try {
          Type result = fTypeSystem.promote(leftType, rightType);

          if (! result.typeEquals(leftType, fTypeSystem.emptyContext())) {
            leftNode = makeNode(wc, fFactory, b, CAstNode.CAST, fFactory.makeConstant(getTypeDict().getCAstTypeFor(result)), leftNode, fFactory.makeConstant(getTypeDict().getCAstTypeFor(leftType)));
          }

          if (! result.typeEquals(rightType, fTypeSystem.emptyContext())) {
            rightNode = makeNode(wc, fFactory, b, CAstNode.CAST, fFactory.makeConstant(getTypeDict().getCAstTypeFor(result)), rightNode, fFactory.makeConstant(getTypeDict().getCAstTypeFor(rightType)));
          }
        } catch (SemanticException e) {

        }

        CAstNode op = makeNode(wc, fFactory, b, CAstNode.BINARY_EXPR, mapBinaryOpcode(operator), leftNode, rightNode);

        if (leftType.isLongOrLess() &&
            rightType.isLongOrLess() &&
            (mapBinaryOpcode(operator)==CAstOperator.OP_DIV ||
             mapBinaryOpcode(operator)==CAstOperator.OP_MOD))
        {
          Collection excTargets = wc.getCatchTargets(fDivByZeroType);
          if (!excTargets.isEmpty()) {
            for (Iterator iterator = excTargets.iterator(); 
             iterator.hasNext();) 
            {
          Pair catchPair = (Pair) iterator.next();
          wc.cfg().add(op, catchPair.snd, fDivByZeroType);
            }
          } else {
            wc.cfg().add(op, 
                 CAstControlFlowMap.EXCEPTION_TO_EXIT, 
                 fDivByZeroType);
          }
        }

        return op;
        
      } else {
        return makeNode(wc, fFactory, b, CAstNode.BINARY_EXPR, mapBinaryOpcode(operator), walkNodes(left, wc), walkNodes(right, wc));
      }
        }
      }

      @SuppressWarnings("unchecked")
      private void handleThrowsFromCall(ProcedureInstance procedureInstance, Node callAstNode, WalkContext wc) {
  /* there are no checked exceptions in X10
          List<Type> throwTypes = procedureInstance.throwTypes();
        for (Iterator<Type> iter = IteratorPlusOne.make(throwTypes.iterator(), fREType); iter.hasNext();) {
          Type thrownType = (Type) iter.next();
          Collection catchTargets = wc.getCatchTargets(thrownType);

          for (Iterator targetIter = catchTargets.iterator(); targetIter.hasNext();) {
            Pair catchTarget = (Pair) targetIter.next();

            wc.cfg().add(callAstNode, catchTarget.snd, catchTarget.fst);
          }
        }
  */
    }

      public CAstNode visit(Call c, WalkContext wc) {
        MethodInstance methodInstance = c.methodInstance();
        boolean isStatic = methodInstance.flags().isStatic();
        ContainerType methodOwner = methodInstance.container();

        if (methodOwner.isArray()) {
          List realOne = methodInstance.overrides(fTypeSystem.emptyContext());
          Assertions.productionAssertion(realOne.size() == 2, "bad array method");
          methodInstance = (MethodInstance) realOne.get(1);
          methodOwner = methodInstance.container();
        }

        if (!methodOwner.isClass()) {
          Assertions.productionAssertion(false, "owner " + methodOwner + " of " + methodInstance + " is not a class");
        }

        boolean isIntf = ((ClassType) methodOwner).flags().isInterface();
        Receiver target = c.target();
        boolean isSpecial = methodInstance.flags().isPrivate()
            || (target instanceof Special && ((Special) target).kind() == Special.SUPER);

        CAstNode[] children = new CAstNode[2 + methodInstance.formalTypes().size()]; // including
        // the
        // MethodReference
        int i = 0;

        if (!isStatic)
          children[i++] = walkNodes(target, wc);
        else
          children[i++] = makeNode(wc, fFactory, null, CAstNode.VOID);

        if (children[0] == null) {
          Assertions.productionAssertion(children[0] != null, "no receiver for " + methodInstance + " in " + wc.getEnclosingMethod());
        }

        MethodReference methodRef = fIdentityMapper.getMethodRef(methodInstance);
        int dummyPC = 0; // Just want to wrap the kind of call; the "rear end"
        // won't care about anything else...
        CallSiteReference callSiteRef;

        if (isStatic)
          callSiteRef = CallSiteReference.make(dummyPC, methodRef, IInvokeInstruction.Dispatch.STATIC);
        else if (isIntf)
          callSiteRef = CallSiteReference.make(dummyPC, methodRef, IInvokeInstruction.Dispatch.INTERFACE);
        else if (isSpecial)
          callSiteRef = CallSiteReference.make(dummyPC, methodRef, IInvokeInstruction.Dispatch.SPECIAL);
        else
          callSiteRef = CallSiteReference.make(dummyPC, methodRef, IInvokeInstruction.Dispatch.VIRTUAL);

        children[i++] = fFactory.makeConstant(callSiteRef);
        for (Iterator iter = c.arguments().iterator(); iter.hasNext();) {
          Expr arg = (Expr) iter.next();
          children[i++] = walkNodes(arg, wc);
        }

        handleThrowsFromCall(methodInstance, c, wc);

        CAstNode result = makeNode(wc, fFactory, c, CAstNode.CALL, children);
        wc.cfg().map(c, result);
        return result;
      }

      public CAstNode visit(ConstructorCall cc, WalkContext wc) {
        ConstructorInstance ctorInstance = cc.constructorInstance();
        ContainerType methodOwner = ctorInstance.container();
        Assertions.productionAssertion(methodOwner.isClass());
        MethodReference methodRef = fIdentityMapper.getMethodRef(ctorInstance);

        int dummyPC = 0; // Just want to wrap the kind of call; the "rear end"
        // won't care about anything else...
        CallSiteReference callSiteRef = CallSiteReference.make(dummyPC, methodRef, IInvokeInstruction.Dispatch.SPECIAL);

        CAstNode[] children = new CAstNode[1 + 1 + ctorInstance.formalTypes().size()]; // including
        // the
        // MethodReference
        int i = 0;

        CAstNode targetNode;

        targetNode = (cc.kind() == ConstructorCall.THIS) ? makeNode(wc, fFactory, cc, CAstNode.THIS) : makeNode(wc, fFactory, cc,
            CAstNode.SUPER);

        children[i++] = targetNode;
        children[i++] = fFactory.makeConstant(callSiteRef);
        for (Iterator iter = cc.arguments().iterator(); iter.hasNext();) {
          Expr arg = (Expr) iter.next();
          children[i++] = walkNodes(arg, wc);
        }

        handleThrowsFromCall(ctorInstance, cc, wc);

        CAstNode result = makeNode(wc, fFactory, cc, CAstNode.CALL, children);
        wc.cfg().map(cc, result);
        return result;
      }

      public CAstNode visit(Cast c, WalkContext wc) {
        Expr arg = c.expr();
        Type castedFrom = arg.type();
        Type castedTo = c.castType().type();

        // null can go into anything (e.g. in "((Foobar) null)" null can be assumed to be of type Foobar already) 
        if (castedFrom.isNull())
          castedFrom = castedTo;
        
        CAstNode ast = makeNode(wc, fFactory, c, CAstNode.CAST, fFactory.makeConstant(getTypeDict().getCAstTypeFor(castedTo)),
            walkNodes(arg, wc),
        fFactory.makeConstant(getTypeDict().getCAstTypeFor(castedFrom)));

        Collection excTargets = wc.getCatchTargets(fCCEType);
        if (!excTargets.isEmpty()) {
          // connect ClassCastException exception edge to relevant catch targets
          // (presumably only one)
          for (Iterator iterator = excTargets.iterator(); iterator.hasNext();) {
            Pair catchPair = (Pair) iterator.next();
            wc.cfg().add(c, catchPair.snd, fCCEType);
          }
        } else {
          // connect exception edge to exit
          wc.cfg().add(c, CAstControlFlowMap.EXCEPTION_TO_EXIT, fCCEType);
        }

        wc.cfg().map(c, ast);
        return ast;
      }

      public CAstNode visit(Conditional c, WalkContext wc) {
        return makeNode(wc, fFactory, c, CAstNode.IF_EXPR, walkNodes(c.cond(), wc), walkNodes(c.consequent(), wc), walkNodes(c
            .alternative(), wc));
      }

      public CAstNode visit(Instanceof io, WalkContext wc) {
        return makeNode(wc, fFactory, io, CAstNode.INSTANCEOF, fFactory.makeConstant(getTypeDict().getCAstTypeFor(
            io.compareType().type())), walkNodes(io.expr(), wc));
      }

      public CAstNode visit(BooleanLit bl, WalkContext wc) {
        return fFactory.makeConstant(bl.value());
      }

      public CAstNode visit(ClassLit cl, WalkContext wc) {
        Type litType = cl.typeNode().type();
        String typeName = fIdentityMapper.typeToTypeID(litType);
        return makeNode(wc, fFactory, cl, CAstNode.TYPE_LITERAL_EXPR, fFactory.makeConstant(typeName));
      }

      public CAstNode visit(FloatLit fl, WalkContext wc) {
        return (fl.kind() == FloatLit.FLOAT) ? fFactory.makeConstant((float) fl.value()) : fFactory.makeConstant(fl.value());
      }

      public CAstNode visit(NullLit nl, WalkContext wc) {
        return fFactory.makeConstant(null);
      }

      public CAstNode visit(CharLit cl, WalkContext wc) {
        return fFactory.makeConstant(cl.value());
      }

      public CAstNode visit(IntLit il, WalkContext wc) {
        return fFactory.makeConstant((int) il.value());
      }

      public CAstNode visit(StringLit sl, WalkContext wc) {
        return fFactory.makeConstant(sl.value());
      }

      public CAstNode visit(New n, WalkContext wc) {
        CAstEntity anonClass = null;
        String newTypeNameStr;
        TypeReference newTypeRef;
        ConstructorInstance ctorInst = n.constructorInstance();
        MethodReference ctorRef = fIdentityMapper.getMethodRef(ctorInst);

        if (n.body() != null) {
          fIdentityMapper.mapLocalAnonTypeToMethod(n.type().toClass(), wc.getEnclosingMethod());

          anonClass = walkEntity(n, wc);

          newTypeNameStr = anonClass.getType().getName();
          TypeName newTypeName = TypeName.string2TypeName(newTypeNameStr);
          Selector ctorSel = ctorRef.getSelector();
          newTypeRef = TypeReference.findOrCreate(fClassLoaderRef, newTypeName);
          ctorRef = MethodReference.findOrCreate(newTypeRef, ctorSel);
        } else {
          newTypeRef = fIdentityMapper.getTypeRef(n.type());
        }

        List/* <Expr> */args = n.arguments();
        String tmpName = "ctor temp"; // this name is an illegal Java
        // identifier

        // new nodes with an explicit enclosing argument, e.g. "outer.new Inner()". They are mostly treated the same, except in JavaCAst2IRTranslator.doNewObject
        CAstNode newNode;
        Expr enclosing = n.qualifier();
        if (enclosing != null) {
          CAstNode encNode = walkNodes(enclosing, wc);
          newNode = makeNode(wc, fFactory, n, CAstNode.NEW_ENCLOSING, fFactory.makeConstant(newTypeRef), encNode);
        }
        else 
          newNode = makeNode(wc, fFactory, n, CAstNode.NEW, fFactory.makeConstant(newTypeRef));
        // end enclosing new stuff

        if (n.body() != null)
          wc.addScopedEntity(newNode, anonClass);

        int dummyPC = 0; // Just want to wrap the kind of call; the "rear end"
        // won't care about anything else...
        CallSiteReference callSiteRef = CallSiteReference.make(dummyPC, ctorRef, IInvokeInstruction.Dispatch.SPECIAL);

        CAstNode[] argNodes = new CAstNode[args.size() + 2]; // args + recvr
        // + ctor ref

        int idx = 0;
        argNodes[idx++] = makeNode(wc, fFactory, n, CAstNode.VAR, fFactory.makeConstant(tmpName));
        argNodes[idx++] = fFactory.makeConstant(callSiteRef);
        for (Iterator iter = args.iterator(); iter.hasNext();) {
          Expr arg = (Expr) iter.next();
          argNodes[idx++] = walkNodes(arg, wc);
        }
        CAstNode callNode = makeNode(wc, fFactory, n, CAstNode.CALL, argNodes);
        wc.cfg().map(n, callNode);

        handleThrowsFromCall(ctorInst, n, wc);

        return makeNode(wc, fFactory, n, CAstNode.LOCAL_SCOPE, makeNode(wc, fFactory, n, CAstNode.BLOCK_EXPR, makeNode(wc, fFactory,
            n, CAstNode.DECL_STMT, fFactory.makeConstant(new InternalCAstSymbol(tmpName, true)), newNode), callNode, makeNode(wc,
            fFactory, n, CAstNode.VAR, fFactory.makeConstant(tmpName))));
      }

      public CAstNode visit(NewArray na, WalkContext wc) {
        Type newType = na.type();
        ArrayInit ai = na.init();
        Assertions.productionAssertion(newType.isArray());

        if (ai != null) {
          return visit(ai, wc, newType);
        } else {
          JavaArrayType arrayType = (JavaArrayType) newType;
          TypeReference arrayTypeRef = fIdentityMapper.getTypeRef(arrayType);

          List/* <Expr> */dims = na.dims();
          CAstNode[] args = new CAstNode[dims.size() + 1];

          int idx = 0;
          args[idx++] = fFactory.makeConstant(arrayTypeRef);
          for (Iterator iter = dims.iterator(); iter.hasNext();) {
            Expr dimExpr = (Expr) iter.next();
            args[idx++] = walkNodes(dimExpr, wc);
          }
          return makeNode(wc, fFactory, na, CAstNode.NEW, args);
        }
      }

      public CAstNode visit(Special s, WalkContext wc) {
        if (s.qualifier() != null) {
          Type owningType = s.qualifier().type();
          TypeReference owningTypeRef = fIdentityMapper.getTypeRef(owningType);

          return makeNode(wc, fFactory, s, s.kind() == Special.THIS ? CAstNode.THIS : CAstNode.SUPER, fFactory
              .makeConstant(owningTypeRef));
        } else {
          return makeNode(wc, fFactory, s, s.kind() == Special.THIS ? CAstNode.THIS : CAstNode.SUPER);
        }
      }

      public CAstNode visit(Unary u, WalkContext wc) {
        if (isAssignOp(u.operator())) {
          WalkContext lvc = new AssignmentContext(wc);
          if (u.operator().isPrefix())
            return makeNode(wc, fFactory, u, CAstNode.ASSIGN_PRE_OP, walkNodes(u.expr(), lvc), fFactory.makeConstant(1),
                mapUnaryOpcode(u.operator()));
          else
            return makeNode(wc, fFactory, u, CAstNode.ASSIGN_POST_OP, walkNodes(u.expr(), lvc), fFactory.makeConstant(1),
                mapUnaryOpcode(u.operator()));
        } else if (u.operator() == Unary.POS) // drop useless unary plus
          // operator
          return walkNodes(u.expr(), wc);
        else if (u.operator() == Unary.NEG) {
          CAstNode zero;
          if (u.expr().type().isLongOrLess())
            zero = fFactory.makeConstant(0L);
          else
            zero = fFactory.makeConstant(0.0);
          return makeNode(wc, fFactory, u, CAstNode.BINARY_EXPR, CAstOperator.OP_SUB, zero, walkNodes(u.expr(), wc));
        } else
          return makeNode(wc, fFactory, u, CAstNode.UNARY_EXPR, mapUnaryOpcode(u.operator()), walkNodes(u.expr(), wc));
      }

      protected boolean isAssignOp(Unary.Operator operator) {
        return operator == Unary.POST_DEC || operator == Unary.POST_INC || operator == Unary.PRE_DEC || operator == Unary.PRE_INC;
      }

      public CAstNode visit(ArrayAccess aa, WalkContext wc) {
        TypeReference eltTypeRef = fIdentityMapper.getTypeRef(aa.type());

        hookUpNPETargets(aa, wc);

        CAstNode n = makeNode(wc, fFactory, aa, CAstNode.ARRAY_REF, walkNodes(aa.array(), wc), fFactory.makeConstant(eltTypeRef),
            walkNodes(aa.index(), wc));
        
        wc.cfg().map(aa, n);
        
        return n;
      }

      protected void hookUpNPETargets(Node n, WalkContext wc) {
        Collection excTargets = wc.getCatchTargets(fNPEType);
        if (!excTargets.isEmpty()) {
          // connect NPE exception edge to relevant catch targets
          // (presumably only one)
          for (Iterator iterator = excTargets.iterator(); iterator.hasNext();) {
            Pair catchPair = (Pair) iterator.next();
            wc.cfg().add(n, catchPair.snd, fNPEType);
          }
        } else {
          // connect exception edge to exit
          wc.cfg().add(n, CAstControlFlowMap.EXCEPTION_TO_EXIT, fNPEType);
        }
      }

      public CAstNode visit(Field f, WalkContext wc) {
        Receiver target = f.target();
        Type targetType = target.type();

        if (targetType.isArray()) {
          Assertions.productionAssertion(f.name().toString().equals("length"));

          return makeNode(wc, fFactory, f, CAstNode.ARRAY_LENGTH, walkNodes(target, wc));
        }
        FieldInstance fi = f.fieldInstance();
        FieldReference fieldRef = fIdentityMapper.getFieldRef(fi);
        CAstNode targetNode = walkNodes(target, wc);

        if (fi.flags().isStatic()) {
          // JLS says: evaluate the target of the field ref and throw it away.
          // Hence the following block expr, whose 2 children are the target
          // evaluation
          // followed by the OBJECT_REF with a null target child (which the
          // "back-end"
          // CAst -> IR translator interprets as a static ref).
          if (fi.isConstant()) {
            return makeNode(wc, fFactory, f, CAstNode.BLOCK_EXPR, targetNode, // can have side effects!
                translateConstant(fi.constantValue()));
          } else {
            return makeNode(wc, fFactory, f, CAstNode.BLOCK_EXPR, targetNode, makeNode(wc, fFactory, f, CAstNode.OBJECT_REF,
                makeNode(wc, fFactory, null, CAstNode.VOID), fFactory.makeConstant(fieldRef)));
          }
        } else {
          Collection excTargets = wc.getCatchTargets(fNPEType);
          if (!excTargets.isEmpty()) {
            // connect NPE exception edge to relevant catch targets
            // (presumably only one)
            for (Iterator iterator = excTargets.iterator(); iterator.hasNext();) {
              Pair catchPair = (Pair) iterator.next();
              wc.cfg().add(f, catchPair.snd, fNPEType);
            }
          } else {
            // connect exception edge to exit
            wc.cfg().add(f, CAstControlFlowMap.EXCEPTION_TO_EXIT, fNPEType);
          }
          CAstNode refNode = makeNode(wc, fFactory, f, CAstNode.OBJECT_REF, targetNode, fFactory.makeConstant(fieldRef));

          wc.cfg().map(f, refNode);

          if (fi.isConstant()) {
            return makeNode(wc, fFactory, f, CAstNode.BLOCK_EXPR, refNode, // can have side effects!
                translateConstant(fi.constantValue()));
          } else {
            return refNode;
          }
        }
      }

      public CAstNode visit(Local l, WalkContext wc) {
        return makeNode(wc, fFactory, l, CAstNode.VAR, fFactory.makeConstant(l.name().id().toString()));
      }

      public CAstNode visit(ClassBody cb, WalkContext wc) {
        Assertions.UNREACHABLE("walkNodes.ASTTraverser.visit(ClassBody)");
        return null;
      }

      public CAstNode visit(ClassDecl cd, WalkContext wc) {
        Assertions.UNREACHABLE("walkNodes.ASTTraverser.visit(ClassDecl)");
        return null;
      }

      public CAstNode visit(Initializer i, WalkContext wc) {
        // Perhaps this is invoked from within the ConstructorDecl visit()
        // method...
        return walkNodes(i.body(), wc);
      }

      public CAstNode visit(Assert a, WalkContext wc) {
        return makeNode(wc, fFactory, a, CAstNode.ASSERT, walkNodes(a.cond(), wc));
      }

      public CAstNode visit(Branch b, WalkContext wc) {
        Node target = null;
        Id labelNode = b.labelNode();
        String labelStr = labelNode != null ? labelNode.id().toString() : null;
        if (b.kind() == Branch.BREAK) {
          target = wc.getBreakFor(labelStr);
        } else {
          target = wc.getContinueFor(labelStr);
        }

        Assertions.productionAssertion(target != null);

        CAstNode result = makeNode(wc, fFactory, b, CAstNode.GOTO);

        wc.cfg().map(b, result);
        wc.cfg().add(b, target, null);

        return result;
      }

      public CAstNode visit(Block b, WalkContext wc) {
        CAstNode[] stmtNodes = new CAstNode[b.statements().size()];

        int idx = 0;
        for (Iterator iter = b.statements().iterator(); iter.hasNext(); idx++) {
          Stmt s = (Stmt) iter.next();
          stmtNodes[idx] = walkNodes(s, wc);
        }
        return makeNode(wc, fFactory, b, CAstNode.LOCAL_SCOPE, makeNode(wc, fFactory, b, CAstNode.BLOCK_STMT, stmtNodes));
      }

      public CAstNode visit(StmtSeq seq, WalkContext wc) {
          CAstNode[] stmtNodes = new CAstNode[seq.statements().size()];

          int idx = 0;
          for (Iterator iter = seq.statements().iterator(); iter.hasNext(); idx++) {
            Stmt s = (Stmt) iter.next();
            stmtNodes[idx] = walkNodes(s, wc);
          }
          return makeNode(wc, fFactory, seq, CAstNode.BLOCK_STMT, stmtNodes);
        }

      public CAstNode visit(SwitchBlock sb, WalkContext wc) {
        CAstNode[] stmtNodes = new CAstNode[sb.statements().size()];

        int idx = 0;
        for (Iterator iter = sb.statements().iterator(); iter.hasNext(); idx++) {
          Stmt s = (Stmt) iter.next();
          stmtNodes[idx] = walkNodes(s, wc);
        }
        return makeNode(wc, fFactory, sb, CAstNode.BLOCK_STMT, stmtNodes);
      }

      public CAstNode visit(Catch c, WalkContext wc) {
        Block body = c.body();
        Formal f = c.formal();

        CAstNode excDecl = makeNode(wc, fFactory, c, CAstNode.CATCH, fFactory.makeConstant(f.name().id().toString()), walkNodes(body, wc));
        CAstNode localScope = makeNode(wc, fFactory, c, CAstNode.LOCAL_SCOPE, excDecl);

        wc.cfg().map(c, excDecl);
        wc.getNodeTypeMap().add(excDecl, wc.getTypeDictionary().getCAstTypeFor(c.catchType()));
        return localScope;
      }

      public CAstNode visit(If i, WalkContext wc) {
        return makeNode(wc, fFactory, i, CAstNode.IF_STMT, walkNodes(i.cond(), wc), walkNodes(i.consequent(), wc), walkNodes(i
            .alternative(), wc));
      }

      public CAstNode visit(Labeled l, WalkContext wc) {
        Node breakTarget = makeBreakTarget(l);

        Node stmt = l.statement();
        while (stmt instanceof Block) {
          stmt = (Node) ((Block) stmt).statements().iterator().next();
        }

        wc.getLabelMap().put(stmt, l.labelNode().id().toString());

        CAstNode result;
        if (! (l.statement() instanceof Empty)) {
      WalkContext child = new BreakContext(wc, l.labelNode().id().toString(), breakTarget);

      result = 
        makeNode(wc, fFactory, l, CAstNode.BLOCK_STMT, 
          makeNode(wc, fFactory, l, CAstNode.LABEL_STMT, fFactory.makeConstant(l.labelNode().id().toString()), walkNodes(l.statement(), child)),
          walkNodes(breakTarget, wc));
        } else {
      result = 
        makeNode(wc, fFactory, l, CAstNode.LABEL_STMT, fFactory.makeConstant(l.labelNode().id().toString()), walkNodes(l.statement(), wc));
        }

        wc.cfg().map(l, result);

        wc.getLabelMap().remove(stmt);

        return result;
      }

      public CAstNode visit(LocalClassDecl lcd, WalkContext wc) {
        fIdentityMapper.mapLocalAnonTypeToMethod(lcd.decl().classDef().asType(), wc.getEnclosingMethod());

        CAstEntity classEntity = walkEntity(lcd.decl(), wc);

        final CAstNode lcdNode = makeNode(wc, fFactory, lcd, CAstNode.EMPTY);

        wc.addScopedEntity(lcdNode, classEntity);
        return lcdNode;
      }

      protected Node makeBreakTarget(Node loop) {
        return fNodeFactory.Labeled(Position.COMPILER_GENERATED,
                fNodeFactory.Id(Position.COMPILER_GENERATED, "breakLabel" + loop.position().toString().replace('.', '_')),
                fNodeFactory.Empty(Position.COMPILER_GENERATED));
      }

      protected Node makeContinueTarget(Node loop) {
        return fNodeFactory.Labeled(Position.COMPILER_GENERATED,
                fNodeFactory.Id(Position.COMPILER_GENERATED, "continueLabel" + loop.position().toString().replace('.', '_')),
            fNodeFactory.Empty(Position.COMPILER_GENERATED));
      }

      public CAstNode visit(Do d, WalkContext wc) {
        Node breakTarget = makeBreakTarget(d);
        Node continueTarget = makeContinueTarget(d);

        String loopLabel = (String) wc.getLabelMap().get(d);

        CAstNode continueNode = walkNodes(continueTarget, wc);
        CAstNode breakBody = walkNodes(breakTarget, wc);

        WalkContext lc = new LoopContext(wc, loopLabel, breakTarget, continueTarget);
        CAstNode loopBody =  walkNodes(d.body(), lc);
        
         if (replicateForDoLoops) {
          CAstRewriter.Rewrite x = (new CAstCloner(fFactory, false)).copy(loopBody, wc.cfg(), wc.pos(), wc.getNodeTypeMap(), null);
          CAstNode otherBody = x.newRoot();
          
          wc.cfg().addAll(x.newCfg());
          wc.pos().addAll(x.newPos());
          wc.getNodeTypeMap().addAll(x.newTypes());
   
          return makeNode(wc, fFactory, d, CAstNode.BLOCK_STMT, 
              loopBody,
              makeNode(wc, fFactory, d, CAstNode.LOOP, 
                  walkNodes(d.cond(), wc), 
                  makeNode(wc, fFactory, d, CAstNode.BLOCK_STMT, otherBody, continueNode)),
              breakBody);
          
        } else {
          Node header = fNodeFactory.Empty(Position.COMPILER_GENERATED);
   
          CAstNode loopGoto = makeNode(wc, fFactory, d, CAstNode.IFGOTO, walkNodes(d.cond(), wc));

          wc.cfg().map(loopGoto, loopGoto);
          wc.cfg().add(loopGoto, header, Boolean.TRUE);

          return makeNode(wc, fFactory, d, CAstNode.BLOCK_STMT, 
              walkNodes(header, wc), 
              makeNode(wc, fFactory, d, CAstNode.BLOCK_STMT, loopBody, continueNode), 
              loopGoto, 
              breakBody);
        }
      }

      public CAstNode visit(For f, WalkContext wc) {
        Node breakTarget = makeBreakTarget(f);
        Node continueTarget = makeContinueTarget(f);
        String loopLabel = (String) wc.getLabelMap().get(f);
        WalkContext lc = new LoopContext(wc, loopLabel, breakTarget, continueTarget);

        CAstNode[] inits = new CAstNode[f.inits().size()];
        for (int i = 0; i < inits.length; i++) {
          inits[i] = walkNodes((Node) f.inits().get(i), wc);
        }

        CAstNode[] iters = new CAstNode[f.iters().size()];
        for (int i = 0; i < iters.length; i++) {
          iters[i] = walkNodes((Node) f.iters().get(i), wc);
        }

        CAstNode initsBlock = makeNode(wc, fFactory, f, CAstNode.BLOCK_STMT, inits);
        CAstNode itersBlock = makeNode(wc, fFactory, f, CAstNode.BLOCK_STMT, iters);

        return makeNode(wc, fFactory, f, CAstNode.BLOCK_STMT, initsBlock, makeNode(wc, fFactory, f, CAstNode.LOOP, walkNodes(
            f.cond(), wc), makeNode(wc, fFactory, f, CAstNode.BLOCK_STMT, walkNodes(f.body(), lc), walkNodes(continueTarget, wc),
            itersBlock)), walkNodes(breakTarget, wc));

      }

      public CAstNode visit(While w, WalkContext wc) {
        Expr c = w.cond();
        Stmt b = w.body();

        Node breakTarget = makeBreakTarget(w);
        Node continueTarget = makeContinueTarget(w);
        String loopLabel = (String) wc.getLabelMap().get(w);
        LoopContext lc = new LoopContext(wc, loopLabel, breakTarget, continueTarget);

        /*
         * The following loop is created sligtly differently than in jscore. It doesn't have a specific target for continue.
         */
        return makeNode(wc, fFactory, w, CAstNode.BLOCK_STMT, makeNode(wc, fFactory, w, CAstNode.LOOP, walkNodes(c, wc), makeNode(wc,
            fFactory, w, CAstNode.BLOCK_STMT, walkNodes(b, lc), walkNodes(continueTarget, wc))), walkNodes(breakTarget, wc));
      }

      public CAstNode visit(Switch s, WalkContext wc) {
      Node breakLabel = fNodeFactory.Labeled(s.position(),
              fNodeFactory.Id(s.position(), "switchBreakLabel" + s.position().toString().replace('.', '_')),
              fNodeFactory.Empty(s.position()));
        CAstNode breakAst = walkNodes(breakLabel, wc);
        String loopLabel = (String) wc.getLabelMap().get(s);
        WalkContext child = new BreakContext(wc, loopLabel, breakLabel);
        Expr cond = s.expr();
        List cases = s.elements();

        // First compute the control flow edges for the various case labels
        for (int i = 0; i < cases.size(); i++) {
          SwitchElement se = (SwitchElement) cases.get(i);
          if (se instanceof Case) {
            Case c = (Case) se;

            if (c.isDefault())
              wc.cfg().add(s, c, CAstControlFlowMap.SWITCH_DEFAULT);
            else
              wc.cfg().add(s, c, fFactory.makeConstant(c.value()));
          }
        }
        CAstNode[] caseNodes = new CAstNode[cases.size()];

        // Now produce the CAst representation for each case
        int idx = 0;
        for (Iterator iter = cases.iterator(); iter.hasNext(); idx++) {
          SwitchElement se = (SwitchElement) iter.next();

          caseNodes[idx] = walkNodes(se, child);
        }

        // Now produce the switch stmt itself
        CAstNode switchAst = makeNode(wc, fFactory, s, CAstNode.SWITCH, walkNodes(cond, wc), makeNode(wc, fFactory, s,
            CAstNode.BLOCK_STMT, caseNodes));

        wc.cfg().map(s, switchAst);

        // Finally, wrap the entire switch in a block so that we have a
        // well-defined place to 'break' to.
        return makeNode(wc, fFactory, s, CAstNode.BLOCK_STMT, switchAst, breakAst);
      }

      public CAstNode visit(Try t, WalkContext wc) {
        List catchBlocks = t.catchBlocks();
        Block finallyBlock = t.finallyBlock();
        Block tryBlock = t.tryBlock();

        // try/finally
        if (catchBlocks.isEmpty()) {
          return makeNode(wc, fFactory, t, CAstNode.UNWIND, walkNodes(tryBlock, wc), walkNodes(finallyBlock, wc));

          // try/catch/[finally]
        } else {
          TryCatchContext tc = new TryCatchContext(wc, t, fTypeSystem.emptyContext());

          CAstNode tryNode = walkNodes(tryBlock, tc);
          for (Iterator iter = catchBlocks.iterator(); iter.hasNext();) {
            tryNode = makeNode(wc, fFactory, t, CAstNode.TRY, tryNode, walkNodes((Catch) iter.next(), wc));
          }

          // try/catch
          if (finallyBlock == null) {
            return tryNode;

            // try/catch/finally
          } else {
            return makeNode(wc, fFactory, t, CAstNode.UNWIND, tryNode, walkNodes(finallyBlock, wc));
          }
        }
      }

      public CAstNode visit(Empty e, WalkContext wc) {
        CAstNode result = makeNode(wc, fFactory, e, CAstNode.EMPTY);
        wc.cfg().map(e, result);
        return result;
      }

      public CAstNode visit(Eval e, WalkContext wc) {
        return walkNodes(e.expr(), wc);
      }

      public CAstNode visit(LocalDecl ld, WalkContext wc) {
        Expr init = ld.init();
        Type type = ld.declType();
        CAstNode initNode;
        if (init == null) {
          if (type.isLongOrLess())
            initNode = fFactory.makeConstant(0);
          else if (type.isDouble() || type.isFloat())
            initNode = fFactory.makeConstant(0.0);
          else
            initNode = fFactory.makeConstant(null);
        } else if (init instanceof ArrayInit)
          initNode = visit((ArrayInit) init, wc, type); 
        else
            initNode = walkNodes(init, wc);

        Object defaultValue;
        if (type.isLongOrLess())
          defaultValue = new Integer(0);
        else if (type.isDouble() || type.isFloat())
          defaultValue = new Double(0.0);
        else
          defaultValue = CAstSymbol.NULL_DEFAULT_VALUE;

        boolean isFinal = ld.flags().flags().isFinal();

        return makeNode(wc, fFactory, ld, CAstNode.DECL_STMT, fFactory.makeConstant(new CAstSymbolImpl(ld.name().id().toString(), isFinal,
            defaultValue)), initNode);
      }

      public CAstNode visit(Return r, WalkContext wc) {
        Expr retExpr = r.expr();
        if (retExpr == null)
          return makeNode(wc, fFactory, r, CAstNode.RETURN);
        else
          return makeNode(wc, fFactory, r, CAstNode.RETURN, walkNodes(retExpr, wc));
      }

      public CAstNode visit(Case c, WalkContext wc) {
        CAstNode label = makeNode(wc, fFactory, c, CAstNode.LABEL_STMT, fFactory.makeConstant(c.value()));

        wc.cfg().map(c, label);
        return label;
      }

      public CAstNode visit(Throw t, WalkContext wc) {
        CAstNode result = makeNode(wc, fFactory, t, CAstNode.THROW, walkNodes(t.expr(), wc));
        Type label = t.expr().type();

        wc.cfg().map(t, result);

        Collection/* <Pair<Type,Node>> */catchNodes = wc.getCatchTargets(label);

        for (Iterator iter = catchNodes.iterator(); iter.hasNext();) {
          Pair/* <Type,Node> */catchNode = (Pair/* <Type,Node> */) iter.next();

          wc.cfg().add(t, catchNode.snd, catchNode.fst);
        }

        return result;
      }

      public CAstNode visit(Formal f, WalkContext wc) {
        return makeNode(wc, fFactory, f, CAstNode.VAR, fFactory.makeConstant(f.name().id().toString()));
      }
    }

    protected static final class CompilationUnitEntity implements CAstEntity {
      private final String fName;

      private final Collection<CAstEntity> fTopLevelDecls;

      public CompilationUnitEntity(SourceFile file, List<CAstEntity> topLevelDecls) {
        fName = (file.package_() == null) ? "" : file.package_().package_().get().fullName().toString().replace('.', '/');
        fTopLevelDecls = topLevelDecls;
      }

      public int getKind() {
        return FILE_ENTITY;
      }

      public String getName() {
        return fName;
      }

      public String getSignature() {
        Assertions.UNREACHABLE();
        return null;
      }

      public String[] getArgumentNames() {
        return new String[0];
      }

      public CAstNode[] getArgumentDefaults() {
        return new CAstNode[0];
      }

      public int getArgumentCount() {
        return 0;
      }

      public Map<CAstNode, Collection<CAstEntity>> getAllScopedEntities() {
        return Collections.singletonMap(null, fTopLevelDecls);
      }

      public Iterator getScopedEntities(CAstNode construct) {
        Assertions.UNREACHABLE("CompilationUnitEntity asked for AST-related entities, but it has no AST.");
        return null;
      }

      public CAstNode getAST() {
        return null;
      }

      public CAstControlFlowMap getControlFlow() {
        Assertions.UNREACHABLE("CompilationUnitEntity.getControlFlow()");
        return null;
      }

      public CAstSourcePositionMap getSourceMap() {
        Assertions.UNREACHABLE("CompilationUnitEntity.getSourceMap()");
        return null;
      }

      public CAstSourcePositionMap.Position getPosition() {
        Assertions.UNREACHABLE("CompilationUnitEntity.getPosition()");
        return null;
      }

      public CAstNodeTypeMap getNodeTypeMap() {
        Assertions.UNREACHABLE("CompilationUnitEntity.getNodeTypeMap()");
        return null;
      }

      public Collection getQualifiers() {
        return Collections.EMPTY_LIST;
      }

      public CAstType getType() {
        Assertions.UNREACHABLE("CompilationUnitEntity.getType()");
        return null;
      }
    }

    public class PolyglotJavaType implements JavaType {
      protected final CAstTypeDictionary fDict;

      protected final TypeSystem fSystem;

      protected final ClassType fType;

      private Collection<CAstType> fSuperTypes = null;

      public PolyglotJavaType(ClassType type, CAstTypeDictionary dict, TypeSystem system) {
        super();
        fDict = dict;
        fSystem = system;
        fType = type;
      }

      public String getName() {
        // TODO Will the IdentityMapper do the right thing for anonymous classes?
        // If so, we can delete most of the following logic...
        if (fType.isLocal() || fType.isAnonymous()) {
          return fIdentityMapper.anonLocalTypeToTypeID(fType);
        } else
          return fIdentityMapper.getTypeRef(fType).getName().toString();
      }

      public Collection getSupertypes() {
        if (fSuperTypes == null) {
          buildSuperTypes();
        }
        return fSuperTypes;
      }

      private void buildSuperTypes() {
        // TODO this is a source entity, but it might actually be the root type
        // (Object), so assume # intfs + 1
        Type superType;
        if (fType.superClass() == null) {
          superType = fSystem.Any(); // fSystem.Object() MUST be the root of the class hierarchy
        } else {
          superType = fType.superClass();
        }
        int N = fType.interfaces().size() + 1;

        fSuperTypes = new ArrayList<CAstType>(N);
        // Following assumes that no one can call getSupertypes() before we have
        // created CAstType's for every type in the program being analyzed.
        if (superType != null) {
          fSuperTypes.add(fDict.getCAstTypeFor(superType));
        }
        for (Iterator iter = fType.interfaces().iterator(); iter.hasNext(); ) {
          Type t = (Type) iter.next();
          if (t instanceof ClassType) {
            ClassType classType = (ClassType) t;
            // [DC] seems that this no-longer applies in an Object-less X10
            //if (classType == fSystem.Object()) {
            //  continue; // Skip fSystem.Object() as a super-interface; it really MUST be a class as far as WALA is concerned
            //}
          }
          fSuperTypes.add(fDict.getCAstTypeFor(t));
        }
      }

      public Collection<CAstQualifier> getQualifiers() {
        return mapFlagsToQualifiers(fType.flags());
      }

      public boolean isInterface() {
        // [DC] seems that this no-longer applies in an Object-less X10
        //if (fType == fSystem.Object()) {
        //  return false; // fSystem.Object() MUST be a class, as far as WALA is concerned
        //}
        return fType.flags().isInterface();
      }
    }

    protected abstract static class CodeBodyEntity implements CAstEntity {
      private final Map<CAstNode, Collection<CAstEntity>> fEntities;

      public CodeBodyEntity(Map<CAstNode, CAstEntity> entities) {
        fEntities = new LinkedHashMap<CAstNode, Collection<CAstEntity>>();
        for (Iterator keys = entities.keySet().iterator(); keys.hasNext();) {
          CAstNode key = (CAstNode) keys.next();
          fEntities.put(key, Collections.singleton(entities.get(key)));
        }
      }

      public Map<CAstNode, Collection<CAstEntity>> getAllScopedEntities() {
        return Collections.unmodifiableMap(fEntities);
      }

      public Iterator getScopedEntities(CAstNode construct) {
        if (fEntities.containsKey(construct)) {
          return (fEntities.get(construct)).iterator();
        } else {
          return EmptyIterator.instance();
        }
      }

      public String getSignature() {
        return Util.methodEntityToSelector(this).toString();
      }
    }

    protected final class ClassEntity implements CAstEntity {
      @SuppressWarnings("unused")
      private final ClassContext fContext;

      private final ClassType fCT;

      private final String fName;

      private final Collection<CAstEntity> fEntities;

      private final CAstSourcePositionMap.Position sourcePosition;

      private ClassEntity(ClassContext context, List<CAstEntity> entities, ClassDecl cd, Position p) {
        this(context, entities, cd.classDef().asType(), cd.name().id().toString(), p);
      }

      private ClassEntity(ClassContext context, List<CAstEntity> entities, ClassType ct, String name, Position p) {
        fContext = context;
        this.fEntities = entities;
        fCT = ct;
        fName = name;
        sourcePosition = makePosition(p);
      }

      public int getKind() {
        return TYPE_ENTITY;
      }

      public String getName() {
        return fName; // unqualified?
      }

      public String getSignature() {
        return "L" + fName.replace('.', '/') + ";";
      }

      public String[] getArgumentNames() {
        return new String[0];
      }

      public CAstNode[] getArgumentDefaults() {
        return new CAstNode[0];
      }

      public int getArgumentCount() {
        return 0;
      }

      public CAstNode getAST() {
        // This entity has no AST nodes, really.
        return null;
      }

      public Map<CAstNode, Collection<CAstEntity>> getAllScopedEntities() {
        return Collections.singletonMap(null, fEntities);
      }

      public Iterator getScopedEntities(CAstNode construct) {
        Assertions.UNREACHABLE("Non-AST-bearing entity (ClassEntity) asked for scoped entities related to a given AST node");
        return null;
      }

      public CAstControlFlowMap getControlFlow() {
        // This entity has no AST nodes, really.
        return null;
      }

      public CAstSourcePositionMap getSourceMap() {
        // This entity has no AST nodes, really.
        return null;
      }

      public CAstSourcePositionMap.Position getPosition() {
        return sourcePosition;
      }

      public CAstNodeTypeMap getNodeTypeMap() {
        // This entity has no AST nodes, really.
        return new CAstNodeTypeMap() {
          public CAstType getNodeType(CAstNode node) {
            throw new UnsupportedOperationException();
          }
          
          public Collection<CAstNode> getMappedNodes() {
            throw new UnsupportedOperationException();
          }
        };
      }

      public Collection getQualifiers() {
        // [DC] seems this does not apply in an object-less X10
        //if (fCT == fTypeSystem.Object()) { // pretend the root of the hierarchy is always a class
        //  return mapFlagsToQualifiers(fCT.flags().clear(Flags.INTERFACE));
        //}
        return mapFlagsToQualifiers(fCT.flags());
      }

      public CAstType getType() {
        return new PolyglotJavaType(fCT, getTypeDict(), fTypeSystem);
      }
      
      @Override
      public String toString() {
        return fCT.fullName().toString();
      }
    }

    protected final class ProcedureEntity extends CodeBodyEntity implements JavaProcedureEntity {
      private final CAstNode fPdast;

      private final TypeSystem fSystem;

      private final Type declaringType;

      private final CodeInstance fPd;

      private final MethodContext fMc;

      private final String[] argumentNames;

      public ProcedureEntity(CAstNode pdast, TypeSystem system, CodeInstance pd, Type declaringType, String[] argumentNames,
          Map<CAstNode, CAstEntity> entities, MethodContext mc) {
        super(entities);
        fPdast = pdast;
        fSystem = system;
        fPd = pd;
        this.declaringType = declaringType;
        this.argumentNames = argumentNames;
        fMc = mc;
      }

      public ProcedureEntity(CAstNode pdast, TypeSystem system, CodeInstance pd, String[] argumentNames,
          Map<CAstNode, CAstEntity> entities, MethodContext mc) {
          //PORT1.7 used to be this(pdast, system, pd, ((MemberInstance) pd).container(), argumentNames, entities, mc);
          this(pdast, system, pd, ((MemberDef) pd.def()).container().get(), argumentNames, entities, mc);
      }

      private List formalTypes() {
        if (fPd instanceof ProcedureInstance) {
          return ((ProcedureInstance) fPd).formalTypes();
        } else {
          return Collections.EMPTY_LIST;
        }
      }

      public String toString() {
        return fPd.toString();
      }

      public int getKind() {
        return CAstEntity.FUNCTION_ENTITY;
      }

      public String getName() {
        if (fPd instanceof ConstructorInstance) {
          return MethodReference.initAtom.toString();
        } else {
          if (fPd instanceof InitializerInstance) {
            return MethodReference.clinitName.toString();
          } else {
            Assertions.productionAssertion(fPd instanceof MethodInstance);
            return ((MethodInstance) fPd).name().toString();
          }
        }
      }

      public String[] getArgumentNames() {
        return argumentNames;
      }

      public CAstNode[] getArgumentDefaults() {
        return new CAstNode[0];
      }

      private Flags getFlags() {
          // PORT1.7
          if (fPd instanceof ProcedureInstance) {
              return ((MemberDef) fPd.def()).flags();
          }
          return Flags.NONE;
      }

      private boolean isStatic() {
          // PORT1.7
          if (fPd instanceof ProcedureInstance) {
              return ((MemberDef) fPd.def()).flags().isStatic();
          } else if (fPd instanceof InitializerInstance) {
              return ((InitializerInstance) fPd).def().flags().isStatic();
          }
          return false;
      }

      public int getArgumentCount() {
        return isStatic() ? formalTypes().size() : formalTypes().size() + 1;
      }

      public CAstNode getAST() {
        return fPdast;
      }

      public CAstControlFlowMap getControlFlow() {
        return fMc.cfg();
      }

      public CAstSourcePositionMap getSourceMap() {
        return fMc.pos();
      }

      public CAstSourcePositionMap.Position getPosition() {
        return getSourceMap().getPosition(fPdast);
      }

      public CAstNodeTypeMap getNodeTypeMap() {
        return fMc.getNodeTypeMap();
      }

      public Collection getQualifiers() {
        return mapFlagsToQualifiers(getFlags());
      }

      public CAstType getType() {
        return new CAstType.Method() {
          private Collection<CAstType> fExceptionTypes = null;

          private List<CAstType> fParameterTypes = null;

          public CAstType getReturnType() {
            return fMc.getTypeDictionary().getCAstTypeFor(
                (fPd instanceof MethodInstance) ? ((MethodInstance) fPd).returnType() : fSystem.Void());
          }

          public List getArgumentTypes() {
            if (fParameterTypes == null) {
              final List formalTypes = formalTypes();
              fParameterTypes = new ArrayList<CAstType>(formalTypes.size());

              for (Iterator iter = formalTypes.iterator(); iter.hasNext();) {
                fParameterTypes.add(fMc.getTypeDictionary().getCAstTypeFor((Type) iter.next()));
              }
            }
            return fParameterTypes;
          }

          public String getName() {
            Assertions.UNREACHABLE("CAstType.FunctionImpl#getName() called???");
            return "?";
          }

          public Collection getSupertypes() {
            Assertions.UNREACHABLE("CAstType.FunctionImpl#getSupertypes() called???");
            return null;
          }

          public Collection/* <CAstType> */getExceptionTypes() {
            return Collections.EMPTY_LIST;
  /* there are no checked exceptions in X10
            if (fExceptionTypes == null) {
              fExceptionTypes = new LinkedHashSet<CAstType>();
            }

            return fExceptionTypes;
  */
          }

          public int getArgumentCount() {
            return formalTypes().size();
          }

          public CAstType getDeclaringType() {
            return getTypeDict().getCAstTypeFor(declaringType);
          }
        };
      }
    }

    protected final class FieldEntity implements CAstEntity {
      private final FieldInstance fFI;

      private final WalkContext fContext;

      private FieldEntity(FieldDecl fd, WalkContext context) {
        super();
        fFI = fd.fieldDef().asInstance();
        fContext = context;
      }

      public int getKind() {
        return CAstEntity.FIELD_ENTITY;
      }

      public String getName() {
        return fFI.name().toString();
      }

      public String getSignature() {
        return fFI.name() + fIdentityMapper.typeToTypeID(fFI.type());
      }

      public String[] getArgumentNames() {
        return new String[0];
      }

      public CAstNode[] getArgumentDefaults() {
        return new CAstNode[0];
      }

      public int getArgumentCount() {
        return 0;
      }

      public Iterator getScopedEntities(CAstNode construct) {
        return EmptyIterator.instance();
      }

      public Map<CAstNode, Collection<CAstEntity>> getAllScopedEntities() {
        return Collections.emptyMap();
      }

      public CAstNode getAST() {
        // No AST for a field decl; initializers folded into
        // constructor processing...
        return null;
      }

      public CAstControlFlowMap getControlFlow() {
        // No AST for a field decl; initializers folded into
        // constructor processing...
        return null;
      }

      public CAstSourcePositionMap getSourceMap() {
        // No AST for a field decl; initializers folded into
        // constructor processing...
        return null;
      }

      public CAstSourcePositionMap.Position getPosition() {
        return makePosition(fFI.position());
      }

      public CAstNodeTypeMap getNodeTypeMap() {
        // No AST for a field decl; initializers folded into
        // constructor processing...
        return null;
      }

      public Collection getQualifiers() {
        return mapFlagsToQualifiers(fFI.flags());
      }

      public CAstType getType() {
        return fContext.getTypeDictionary().getCAstTypeFor(fFI.type());
      }
    }

    protected static class DelegatingContext implements WalkContext {
      private final WalkContext parent;

      public WalkContext getParent() {
        return parent;
      }

      protected DelegatingContext(WalkContext parent) {
        this.parent = parent;
      }

      public void addScopedEntity(CAstNode node, CAstEntity e) {
        parent.addScopedEntity(node, e);
      }

      // public Map/*<CAstNode,CAstEntity>*/ getScopedEntities() {
      // return parent.getScopedEntities();
      // }

      public CAstControlFlowRecorder cfg() {
        return parent.cfg();
      }

      public CAstSourcePositionRecorder pos() {
        return parent.pos();
      }

      public CAstNodeTypeMapRecorder getNodeTypeMap() {
        return parent.getNodeTypeMap();
      }

      public Collection<Pair<Type, Object>> getCatchTargets(Type label) {
        return parent.getCatchTargets(label);
      }

      public Node getContinueFor(String label) {
        return parent.getContinueFor(label);
      }

      public Node getBreakFor(String label) {
        return parent.getBreakFor(label);
      }

      public Node getFinally() {
        return parent.getFinally();
      }

      public CodeInstance getEnclosingMethod() {
        return parent.getEnclosingMethod();
      }

      public Type getEnclosingType() {
        return parent.getEnclosingType();
      }

      public CAstTypeDictionary getTypeDictionary() {
        return parent.getTypeDictionary();
      }

      public List<ClassMember> getStaticInitializers() {
        return parent.getStaticInitializers();
      }

      public List<ClassMember> getInitializers() {
        return parent.getInitializers();
      }

      public Map<Node, String> getLabelMap() {
        return parent.getLabelMap();
      }

      public boolean needLVal() {
        return parent.needLVal();
      }
    }

    public class ClassContext extends DelegatingContext {
      // private final ClassDecl cd;
      private final Type type;

      private List<ClassMember>/* <Initializer+FieldDecl> */fInitializers = new ArrayList<ClassMember>();

      private List<ClassMember>/* <Initializer+FieldDecl> */fStaticInitializers = new ArrayList<ClassMember>();

      private List<CAstEntity> fChildren;

      public ClassContext(Type type, List<CAstEntity> entities, WalkContext parent) {
        super(parent);
        this.type = type;
        fChildren = entities;
      }

      public void addScopedEntity(CAstNode node, CAstEntity e) {
        Assertions.productionAssertion(node == null);
        fChildren.add(e);
      }

      // public Map/*<CAstNode,CAstEntity>*/ getScopedEntities() {
      // return null; // fChildren;
      // }

      public Type getEnclosingType() {
        return type;
      }

      public List<ClassMember> getInitializers() {
        return fInitializers;
      }

      public List<ClassMember> getStaticInitializers() {
        return fStaticInitializers;
      }

      public CAstControlFlowRecorder cfg() {
        Assertions.UNREACHABLE("ClassContext.cfg()");
        return null;
      }

      public Iterator/* <Pair<Type,Node>> */getCatchTarget(Type label) {
        Assertions.UNREACHABLE("ClassContext.getCatchTarget()");
        return null;
      }

      public Node getFinally() {
        Assertions.UNREACHABLE("ClassContext.getFinally()");
        return null;
      }

      public CodeInstance getEnclosingMethod() {
        // No one outside a method defining a local class can see it,
        // so it clearly can't escape through to the method's enclosing
        // type...
        Assertions.UNREACHABLE("ClassContext.getEnclosingMethod()");
        return null;
      }

      public CAstSourcePositionRecorder pos() {
        // No AST, so no AST map
        Assertions.UNREACHABLE("ClassContext.pos()");
        return null;
      }

      public Node getContinueFor(String label) {
        Assertions.UNREACHABLE("ClassContext.getContinueFor() with label " + label + " in " + type);
        return null;
      }

      public Node getBreakFor(String label) {
        System.err.println("Cannot find break target for " + label + " in " + type);
        Assertions.UNREACHABLE("ClassContext.getBreakFor()");
        return null;
      }

      public Map<Node, String> getLabelMap() {
        Assertions.UNREACHABLE("ClassContext.getLabelMap()");
        return null;
      }

      public boolean needLVal() {
        Assertions.UNREACHABLE("ClassContext.needLVal()");
        return false;
      }
    }

    public class CodeBodyContext extends DelegatingContext {
      final CAstSourcePositionRecorder fSourceMap = new CAstSourcePositionRecorder();

      final CAstControlFlowRecorder fCFG = new CAstControlFlowRecorder(fSourceMap);

      final CAstNodeTypeMapRecorder fNodeTypeMap = new CAstNodeTypeMapRecorder();

      private final Map<Node, String> labelMap = HashMapFactory.make(2);

      private final Map<CAstNode, CAstEntity> fEntities;

      public CodeBodyContext(WalkContext parent, Map<CAstNode, CAstEntity> entities) {
        super(parent);
        fEntities = entities;
      }

      public CAstNodeTypeMapRecorder getNodeTypeMap() {
        return fNodeTypeMap;
      }

      public CAstSourcePositionRecorder pos() {
        return fSourceMap;
      }

      public CAstControlFlowRecorder cfg() {
        return fCFG;
      }

      public void addScopedEntity(CAstNode node, CAstEntity entity) {
        fEntities.put(node, entity);
      }

      public Map/* <CAstNode,CAstEntity> */getScopedEntities() {
        return fEntities;
      }

      public Map<Node, String> getLabelMap() {
        return labelMap;
      }

      public boolean needLVal() {
        return false;
      }
    }

    public class MethodContext extends CodeBodyContext {
      final CodeInstance fPI;

      public MethodContext(CodeInstance pi, Map<CAstNode, CAstEntity> entities, WalkContext parent) {
        super(parent, entities);
        fPI = pi;
      }

      public Collection<Pair<Type, Object>> getCatchTargets(Type label) {
        Collection<Pair<Type, Object>> result = Collections.singleton(Pair.<Type,Object>make(fEType, CAstControlFlowMap.EXCEPTION_TO_EXIT));
        return result;
      }

      public CodeInstance getEnclosingMethod() {
        return fPI;
      }
    }

    private static class TryCatchContext extends DelegatingContext {
      @SuppressWarnings("unused")
      private final Try tryNode;
      
      private final Context context;

      Collection<Pair<Type,Object>> fCatchNodes = new ArrayList<Pair<Type, Object>>();

      TryCatchContext(WalkContext parent, Try tryNode, final Context context) {
        super(parent);
        this.tryNode = tryNode;
        this.context = context;   

        for (Iterator catchIter = tryNode.catchBlocks().iterator(); catchIter.hasNext();) {
          Catch c = (Catch) catchIter.next();
          Pair<Type,Object> p = Pair.make(c.catchType(), (Object)c);

          fCatchNodes.add(p);
        }
      }

      public Collection<Pair<Type, Object>> getCatchTargets(Type label) {
        // Look for all matching targets for this thrown type:
        // if supertpe match, then return only matches at this catch
        // if subtype match, then matches here and parent matches
        Collection<Pair<Type, Object>> catchNodes = new ArrayList<Pair<Type, Object>>();

        for (Iterator<Pair<Type, Object>> iter = fCatchNodes.iterator(); iter.hasNext();) {
          Pair<Type, Object> p = (Pair<Type, Object>) iter.next();
          Type catchType = (Type) p.fst;

          // _must_ be caught
          if (label.isSubtype(catchType, this.context) || label.typeEquals(catchType, this.context)) {
            catchNodes.add(p);
            return catchNodes;

            // _might_ get caught
          } else if (catchType.isSubtype(label, this.context)) {
            catchNodes.add(p);
            continue;
          }
        }
        catchNodes.addAll(getParent().getCatchTargets(label));
        return catchNodes;
      }

      public List<ClassMember> getStaticInitializers() {
        return null;
      }

      public List<ClassMember> getInitializers() {
        return null;
      }
    }

    protected static class RootContext implements WalkContext {
      final CAstTypeDictionary fTypeDict;

      public RootContext(CAstTypeDictionary typeDict) {
        fTypeDict = typeDict;
      }

      public void addScopedEntity(CAstNode node, CAstEntity e) {
        Assertions.UNREACHABLE("Attempt to call addScopedEntity() on a RootContext.");
      }

      public CAstControlFlowRecorder cfg() {
        Assertions.UNREACHABLE("RootContext.cfg()");
        return null;
      }

      public Collection<Pair<Type, Object>> getCatchTargets(Type label) {
        Assertions.UNREACHABLE("RootContext.getCatchTargets()");
        return null;
      }

      public CAstNodeTypeMapRecorder getNodeTypeMap() {
        Assertions.UNREACHABLE("RootContext.getNodeTypeMap()");
        return null;
      }

      public Node getFinally() {
        Assertions.UNREACHABLE("RootContext.getFinally()");
        return null;
      }

      public CAstSourcePositionRecorder pos() {
        // No AST, so no AST map
        Assertions.UNREACHABLE("RootContext.pos()");
        return null;
      }

      public Node getContinueFor(String label) {
        Assertions.UNREACHABLE("RootContext.getContinueFor()");
        return null;
      }

      public Node getBreakFor(String label) {
        Assertions.UNREACHABLE("RootContext.getBreakFor()");
        return null;
      }

      public CodeInstance getEnclosingMethod() {
        Assertions.UNREACHABLE("RootContext.getEnclosingMethod()");
        return null;
      }

      public Type getEnclosingType() {
        Assertions.UNREACHABLE("RootContext.getEnclosingType()");
        return null;
      }

      public CAstTypeDictionary getTypeDictionary() {
        return fTypeDict;
      }

      public List<ClassMember> getStaticInitializers() {
        Assertions.UNREACHABLE("RootContext.getStaticInitializers()");
        return null;
      }

      public List<ClassMember> getInitializers() {
        Assertions.UNREACHABLE("RootContext.getInitializers()");
        return null;
      }

      public Map<Node, String> getLabelMap() {
        Assertions.UNREACHABLE("RootContext.getLabelMap()");
        return null;
      }

      public boolean needLVal() {
        Assertions.UNREACHABLE("ClassContext.needLVal()");
        return false;
      }
    }

    public class BreakContext extends DelegatingContext {
      protected final String label;

      private final Node breakTo;

      BreakContext(WalkContext parent, String label, Node breakTo) {
        super(parent);
        this.label = label;
        this.breakTo = breakTo;
      }

      public Node getBreakFor(String label) {
        return (label == null || label.equals(this.label)) ? breakTo : super.getBreakFor(label);
      }

      public List<ClassMember> getStaticInitializers() {
        return null;
      }

      public List<ClassMember> getInitializers() {
        return null;
      }
    }

    public class LoopContext extends BreakContext {
      private final Node continueTo;

      public LoopContext(WalkContext parent, String label, Node breakTo, Node continueTo) {
        super(parent, label, breakTo);
        this.continueTo = continueTo;
      }

      public Node getContinueFor(String label) {
        return (label == null || label.equals(this.label)) ? continueTo : super.getContinueFor(label);
      }
    }

    private class AssignmentContext extends DelegatingContext {

      protected AssignmentContext(WalkContext parent) {
        super(parent);
      }

      public boolean needLVal() {
        return true;
      }
    }

    public X10toCAstTranslator(ClassLoaderReference clr, NodeFactory nf, TypeSystem ts, X10IdentityMapper identityMapper) {
      fClassLoaderRef = clr;
      fTypeSystem = ts;
      fNodeFactory = nf;
      fIdentityMapper = identityMapper;
      fNPEType = fTypeSystem.NullPointerException();
      fCCEType = fTypeSystem.ClassCastException();
      fEType = fTypeSystem.Exception();
      fDivByZeroType = fTypeSystem.ArithmeticException();
    }

    public static class PolyglotSourcePosition extends AbstractSourcePosition {
      private final Position p;

      public PolyglotSourcePosition(Position p) {
        this.p = p;
      }

      public int getFirstLine() {
        return p.line();
      }

      public int getLastLine() {
        return p.endLine();
      }

      public int getFirstCol() {
        return p.column();
      }

      public int getLastCol() {
        return p.endColumn();
      }
       
      public int getFirstOffset() {
        return p.offset();
      }

      public int getLastOffset() {
        return p.endOffset();
      }

      public URL getURL() {
        try {
          String path = p.path();
          return new URL("file:" + (path.length() == 0 ? p.file() : path));
        } catch (MalformedURLException e) {
          Assertions.UNREACHABLE(e.toString());
          return null;
        }
      }

      public InputStream getInputStream() throws IOException {
        return getURL().openConnection().getInputStream();
      }
    }

    protected CAstSourcePositionMap.Position makePosition(Position p) {
      return new PolyglotSourcePosition(p);
    }

    private void setPos(WalkContext wc, CAstNode cn, Node pn) {
      if (pn != null) {
        wc.pos().setPosition(cn, makePosition(pn.position()));
      }
    }

    private void setPos(WalkContext wc, CAstNode cn, Position p) {
      if (p != null) {
        wc.pos().setPosition(cn, makePosition(p));
      }
    }

    protected CAstNode makeNode(WalkContext wc, CAst Ast, Node n, int kind) {
      CAstNode cn = Ast.makeNode(kind);
      setPos(wc, cn, n);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, CAst Ast, Node n, int kind, CAstNode c[]) {
      CAstNode cn = Ast.makeNode(kind, c);
      setPos(wc, cn, n);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, CAst Ast, Node n, int kind, CAstNode c) {
      CAstNode cn = Ast.makeNode(kind, c);
      setPos(wc, cn, n);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, CAst Ast, Node n, int kind, CAstNode c1, CAstNode c2) {
      CAstNode cn = Ast.makeNode(kind, c1, c2);
      setPos(wc, cn, n);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, CAst Ast, Node n, int kind, CAstNode c1, CAstNode c2, CAstNode c3) {
      CAstNode cn = Ast.makeNode(kind, c1, c2, c3);
      setPos(wc, cn, n);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, CAst Ast, Node n, int kind, CAstNode c1, CAstNode c2, CAstNode c3, CAstNode c4) {
      CAstNode cn = Ast.makeNode(kind, c1, c2, c3, c4);
      setPos(wc, cn, n);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, Node n, int kind) {
      return makeNode(wc, fFactory, n, kind);
    }

    protected CAstNode makeNode(WalkContext wc, Node n, int kind, CAstNode c[]) {
      return makeNode(wc, fFactory, n, kind, c);
    }

    protected CAstNode makeNode(WalkContext wc, Node n, int kind, CAstNode c) {
      return makeNode(wc, fFactory, n, kind, c);
    }

    protected CAstNode makeNode(WalkContext wc, Node n, int kind, CAstNode c1, CAstNode c2) {
      return makeNode(wc, fFactory, n, kind, c1, c2);
    }

    protected CAstNode makeNode(WalkContext wc, Node n, int kind, CAstNode c1, CAstNode c2, CAstNode c3) {
      return makeNode(wc, fFactory, n, kind, c1, c2, c3);
    }

    protected CAstNode makeNode(WalkContext wc, Node n, int kind, CAstNode c1, CAstNode c2, CAstNode c3, CAstNode c4) {
      return makeNode(wc, fFactory, n, kind, c1, c2, c3, c4);
    }

    protected CAstNode makeNode(WalkContext wc, int kind, Position p) {
      CAstNode cn = fFactory.makeNode(kind);
      setPos(wc, cn, p);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, Position p, int kind, CAstNode c1) {
      CAstNode cn = fFactory.makeNode(kind, c1);
      setPos(wc, cn, p);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, Position p, int kind, CAstNode c1, CAstNode[] rest) {
      CAstNode cn = fFactory.makeNode(kind, c1, rest);
      setPos(wc, cn, p);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, Position p, int kind, CAstNode c1, CAstNode c2) {
      CAstNode cn = fFactory.makeNode(kind, c1, c2);
      setPos(wc, cn, p);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, Position p, int kind, CAstNode c1, CAstNode c2, CAstNode c3) {
      CAstNode cn = fFactory.makeNode(kind, c1, c2, c3);
      setPos(wc, cn, p);
      return cn;
    }

    protected CAstNode makeNode(WalkContext wc, Position p, int kind, CAstNode c1, CAstNode c2, CAstNode c3, CAstNode c4) {
      CAstNode cn = fFactory.makeNode(kind, c1, c2, c3, c4);
      setPos(wc, cn, p);
      return cn;
    }

    public CAstEntity translate(Object ast, String fileName) {
      return walkEntity((Node) ast, new RootContext(getTypeDict()));
    }

    protected static Collection<CAstQualifier> mapFlagsToQualifiers(Flags flags) {
      Set<CAstQualifier> quals = new LinkedHashSet<CAstQualifier>();

      if (flags.isAbstract())
        quals.add(CAstQualifier.ABSTRACT);
      if (flags.isFinal())
        quals.add(CAstQualifier.FINAL);
      if (flags.isInterface())
        quals.add(CAstQualifier.INTERFACE);
      if (flags.isNative())
        quals.add(CAstQualifier.NATIVE);
      // if (flags.isPackage()) quals.add(CAstQualifier.PACKAGE);
      if (flags.isPrivate())
        quals.add(CAstQualifier.PRIVATE);
      if (flags.isProtected())
        quals.add(CAstQualifier.PROTECTED);
      if (flags.isPublic())
        quals.add(CAstQualifier.PUBLIC);
      if (flags.isStatic())
        quals.add(CAstQualifier.STATIC);
      if (flags.isTransient())
        quals.add(CAstQualifier.TRANSIENT);
      return quals;
    }

    protected void processClassMembers(Node n, ClassType classType, List<ClassMember> members, DelegatingContext classContext,
        List<CAstEntity> memberEntities) {
      // Collect all initializer-related gorp
      for (Iterator memberIter = members.iterator(); memberIter.hasNext();) {
        ClassMember member = (ClassMember) memberIter.next();

        if (member instanceof Initializer) {
          Initializer initializer = (Initializer) member;

          if (initializer.flags().flags().isStatic())
            classContext.getStaticInitializers().add(initializer);
          else
            classContext.getInitializers().add(initializer);
        } else if (member instanceof FieldDecl) {
          FieldDecl fd = (FieldDecl) member;

          if (fd.init() != null) {
            if (fd.flags().flags().isStatic())
              classContext.getStaticInitializers().add(fd);
            else
              classContext.getInitializers().add(fd);
          }
        }
      }

      // Now process
      for (Iterator memberIter = members.iterator(); memberIter.hasNext();) {
        ClassMember member = (ClassMember) memberIter.next();

        if (!(member instanceof Initializer)) {
          CAstEntity memberEntity = walkEntity(member, classContext);

          memberEntities.add(memberEntity);
        }
      }

      // add class initializer, if needed
      if (!classContext.getStaticInitializers().isEmpty()) {
          InitializerDef initDef = fTypeSystem.initializerDef(n.position(), Types.ref(classType), Flags.STATIC);
        InitializerInstance initInstance = fTypeSystem.createInitializerInstance(n.position(), Types.ref(initDef));

        Map<CAstNode, CAstEntity> childEntities = HashMapFactory.make();
        final MethodContext mc = new MethodContext(initInstance, childEntities, classContext);

        List inits = classContext.getStaticInitializers();
        CAstNode[] bodyNodes = new CAstNode[inits.size()];
        insertInitializers(mc, bodyNodes, true, 0);

        CAstNode ast = makeNode(mc, fFactory, n, CAstNode.BLOCK_STMT, bodyNodes);

        memberEntities.add(new ProcedureEntity(ast, fTypeSystem, initInstance, new String[0], childEntities, mc));

      }
    }

    protected void addConstructorsToAnonymousClass(New n, ClassType anonType, ClassContext classContext,
        List<CAstEntity> memberEntities) {
      List superConstructors = ((ClassType) anonType.superClass()).constructors();

      for (Iterator iter = superConstructors.iterator(); iter.hasNext();) {
        ConstructorInstance superCtor = (ConstructorInstance) iter.next();

        Map<CAstNode, CAstEntity> childEntities = HashMapFactory.make();
        final MethodContext mc = new MethodContext(superCtor, childEntities, classContext);

        String[] fakeArguments = new String[superCtor.formalTypes().size() + 1];
        for (int i = 0; i < fakeArguments.length; i++) {
          fakeArguments[i] = (i == 0) ? "this" : ("argument" + i);
        }

        List inits = classContext.getInitializers();

        CAstNode[] bodyNodes = new CAstNode[inits.size() + 1];

        CallSiteReference callSiteRef = CallSiteReference.make(0, fIdentityMapper.getMethodRef(superCtor),
            IInvokeInstruction.Dispatch.SPECIAL);
        CAstNode[] children = new CAstNode[fakeArguments.length + 1];
        children[0] = makeNode(mc, fFactory, n, CAstNode.SUPER);
        children[1] = fFactory.makeConstant(callSiteRef);
        for (int i = 1; i < fakeArguments.length; i++) {
          children[i + 1] = makeNode(mc, fFactory, n, CAstNode.VAR, fFactory.makeConstant(fakeArguments[i]));
        }
        bodyNodes[0] = makeNode(mc, fFactory, n, CAstNode.CALL, children);

        insertInitializers(mc, bodyNodes, false, 1);

        CAstNode ast = makeNode(mc, fFactory, n, CAstNode.BLOCK_STMT, bodyNodes);

        memberEntities.add(new ProcedureEntity(ast, fTypeSystem, superCtor, anonType, fakeArguments, childEntities, mc));
      }
    }

    public static String anonTypeName(ClassType ct) {
      Position pos = ct.position();

      return ct.fullName() + "$" + pos.line() + "$" + pos.column();
    }

    @SuppressWarnings("unchecked")
    protected CAstEntity SUPER_walkEntity(Node rootNode, final WalkContext context) {
      if (rootNode instanceof SourceFile) {
        SourceFile file = (SourceFile) rootNode;
        List<CAstEntity> declEntities = new ArrayList<CAstEntity>();

        for (Iterator iter = file.decls().iterator(); iter.hasNext();) {
          TopLevelDecl decl = (TopLevelDecl) iter.next();

          declEntities.add(walkEntity(decl, context));
        }
        return new CompilationUnitEntity(file, declEntities);
      } else if (rootNode instanceof ClassDecl) {
        final ClassDecl cd = (ClassDecl) rootNode;
        final List<CAstEntity> memberEntities = new ArrayList<CAstEntity>();
        final ClassContext classContext = new ClassContext(cd.classDef().asType(), memberEntities, context);

        processClassMembers(rootNode, cd.classDef().asType(), cd.body().members(), classContext, memberEntities);

        return new ClassEntity(classContext, memberEntities, cd, cd.position());
      } else if (rootNode instanceof New) {
        final New n = (New) rootNode;
        final List<CAstEntity> memberEntities = new ArrayList<CAstEntity>();
        ClassType anonType = (ClassType) n.anonType().asType();
        String anonTypeName = anonTypeName(anonType);
        final ClassContext classContext = new ClassContext(anonType, memberEntities, context);

        processClassMembers(rootNode, anonType, n.body().members(), classContext, memberEntities);
        addConstructorsToAnonymousClass(n, anonType, classContext, memberEntities);

        return new ClassEntity(classContext, memberEntities, anonType, anonTypeName, n.position());
      } else if (rootNode instanceof ProcedureDecl) {
        final ProcedureDecl pd = (ProcedureDecl) rootNode;
        final Map<CAstNode, CAstEntity> memberEntities = new LinkedHashMap<CAstNode, CAstEntity>();
        final MethodContext mc = new MethodContext(pd.procedureInstance().asInstance(), memberEntities, context);

        CAstNode pdAST = null;

        if (!pd.flags().flags().isAbstract()) {
          // Presumably the MethodContext's parent is a ClassContext,
          // and he has the list of initializers. Hopefully the following
          // will glue that stuff in the right place in any constructor body.
          pdAST = walkNodes(pd, mc);
        }

        List/* <Formal> */formals = pd.formals();
        String[] argNames;
        int i = 0;
        if (!pd.flags().flags().isStatic()) {
          argNames = new String[formals.size() + 1];
          argNames[i++] = "this";
        } else {
          argNames = new String[formals.size()];
        }
        for (Iterator iter = formals.iterator(); iter.hasNext(); i++) {
          Formal formal = (Formal) iter.next();
          argNames[i] = formal.name().toString();
        }

        return new ProcedureEntity(pdAST, fTypeSystem, pd.procedureInstance().asInstance(), argNames, memberEntities, mc);
      } else if (rootNode instanceof FieldDecl) {
        final FieldDecl fd = (FieldDecl) rootNode;

        return new FieldEntity(fd, context);
      } else {
        Assertions.UNREACHABLE("Unknown node type for walkEntity():" + rootNode.getClass().getName());
        return null;
      }
    }

    private boolean isSpecialCallStmt(Stmt maybeSuper, Kind kind) {
      if (maybeSuper instanceof ConstructorCall) {
        ConstructorCall cc = (ConstructorCall) maybeSuper;
        return cc.kind() == kind;
      }
      return false;
    }

    private boolean hasSpecialCall(Block body, Kind kind) {
      if (body.statements().size() <= 0)
        return false;

      Stmt maybeSuper = (Stmt) body.statements().get(0);

      return isSpecialCallStmt(maybeSuper, kind);
    }

    private boolean hasSuperCall(Block body) {
      return hasSpecialCall(body, ConstructorCall.SUPER);
    }

    private boolean hasThisCall(Block body) {
      return hasSpecialCall(body, ConstructorCall.THIS);
    }

    private int insertInitializers(WalkContext wc, CAstNode[] initCode, boolean wantStatic, int offset) {
      List inits = wantStatic ? wc.getStaticInitializers() : wc.getInitializers();

      for (Iterator iter = inits.iterator(); iter.hasNext(); offset++) {
        ClassMember init = (ClassMember) iter.next();
        CAstNode initNode = walkNodes(init, wc);

        if (initNode != null) {
          initCode[offset] = initNode;
        } else {
          initCode[offset] = makeNode(wc, fFactory, null, CAstNode.EMPTY);
        }
      }
      return offset;
    }

    public interface ParametricType extends CAstType.Class {
        List<CAstType> getParameters();
    }

    public class PolyglotJavaParametricType extends PolyglotJavaType implements ParametricType {
        private final List<Type> fPolyglotTypeParameters;
        private List<CAstType> fTypeParameters = null;

        public PolyglotJavaParametricType(ClassType baseType, List<Type> typeParams, CAstTypeDictionary dict, TypeSystem system) {
          super(baseType, dict, system);
          fPolyglotTypeParameters= typeParams;
        }

        public List<CAstType> getParameters() {
          if (fTypeParameters == null) {
            buildTypeParameters();
          }
          return fTypeParameters;
        }

        private void buildTypeParameters() {
          for(Type typeParam: fPolyglotTypeParameters) {
            fTypeParameters.add(fDict.getCAstTypeFor(typeParam));
          }
        }

        @Override
        public String toString() {
          StringBuilder sb= new StringBuilder();
          sb.append(fType.fullName());
          sb.append("<");
          int idx= 0;
          for(Type typeParam: fPolyglotTypeParameters) {
            if (idx++ > 0) { sb.append(", "); }
            sb.append(typeParam.toString());
          }
          sb.append(">");
          return sb.toString();
        }
    }

    protected X10TranslatorVisitor createTranslator() {
        return new X10TranslatingVisitorImpl();
    }

    protected CAstEntity walkAsyncEntity(final Node rootNode, final Node bodyNode, final WalkContext context) {
      Map<CAstNode,CAstEntity> childEntities= new HashMap<CAstNode,CAstEntity>();
      final CodeBodyContext asyncContext= new CodeBodyContext(context, childEntities);
      final CAstNode bodyAST= walkNodes(bodyNode, asyncContext);

      return new AsyncEntity(childEntities, rootNode, asyncContext, bodyAST);
    }

    protected CAstEntity walkClosureEntity(final Closure rootNode, final Node bodyNode, final WalkContext context) {
      Map<CAstNode,CAstEntity> childEntities = new HashMap<CAstNode,CAstEntity>();
      final MethodContext closureContext = new MethodContext(rootNode.closureDef().asInstance(), childEntities, context);
      final CAstNode bodyAST = walkNodes(bodyNode, closureContext);

      final List<Formal> formals = rootNode.formals();
      final Collection<String> argNames = new ArrayList<String>();
      final Collection<CAstNode> bodyNodes = new ArrayList<CAstNode>();
      
      // closure object is the receiver
      argNames.add("$this");
      
      for (final Formal formal : formals) {
        argNames.add(formal.name().toString());
        if (formal instanceof X10Formal) {
          final X10Formal x10Formal = (X10Formal) formal;
          if (x10Formal.hasExplodedVars()) {
            int i = 0;
            for (final Formal explodedVarFormal : x10Formal.vars()) {
              bodyNodes.add(createExplodedVarInitialization(context, formal, explodedVarFormal, i++));
            }
          }
        }
      }
      final CAstNode newBodyAst;
      if (bodyNodes.isEmpty()) {
        newBodyAst = bodyAST;
      } else {
        // We want to access directly to the block statement.
        bodyNodes.add(bodyAST.getChild(0).getChild(0));
        newBodyAst = makeNode(context, fFactory, bodyNode, CAstNode.LOCAL_SCOPE, 
                              makeNode(context, fFactory, bodyNode, CAstNode.BLOCK_STMT,
                                       bodyNodes.toArray(new CAstNode[bodyNodes.size()])));
      }

      return new ClosureBodyEntity(childEntities, rootNode, closureContext, newBodyAst, context.getEnclosingType(), 
                                   argNames.toArray(new String[argNames.size()]),
                                   bodyNodes.toArray(new CAstNode[bodyNodes.size()]));
    }
    
    private CAstNode createExplodedVarInitialization(final WalkContext context, final Formal parameter,
                                                     final Formal explodedVar, final int index) {
      final CAstNode recvNode = makeNode(context, parameter.position(), CAstNode.VAR,
                                         fFactory.makeConstant(parameter.name().toString()));
     
      final TypeReference typeRef = fIdentityMapper.getTypeRef(parameter.type().type());
      final MethodReference methodRef = MethodReference.findOrCreate(typeRef, Atom.findOrCreateAsciiAtom("apply"), 
                                                                     Descriptor.findOrCreateUTF8("(I)Lx10/lang/Int;"));
      final CallSiteReference csRef = CallSiteReference.make(0, methodRef, IInvokeInstruction.Dispatch.VIRTUAL);
      return makeNode(context, parameter.position(), CAstNode.DECL_STMT,
              fFactory.makeConstant(new CAstSymbolImpl(explodedVar.name().toString(), false)),
              makeNode(context, fFactory, parameter, CAstNode.CALL, 
                       new CAstNode[] { recvNode, fFactory.makeConstant(csRef), 
                                        fFactory.makeConstant(index)}));
    }

    private final class AsyncBodyType implements CAstType.Method {
	private final Node fNode;

	private final Type declaringType;

	private AsyncBodyType(Node node, Type declaringType) {
	    super();
	    fNode= node;
	    this.declaringType = declaringType;
	}

	public CAstType getReturnType() {
	    return getTypeDict().getCAstTypeFor(
		    /*(fNode instanceof Future) ?
			    ((Future) fNode).type() :*/
	            fTypeSystem.Void());
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
        // [DC] don't know why an async body had type Object, assuming it's a placeholder
        // so Any seems like a reasonable replacement
	    return Collections.singleton(getTypeDict().getCAstTypeFor(fTypeSystem.Any()));
	}

	public CAstType getDeclaringType() {
	    return getTypeDict().getCAstTypeFor(declaringType);
	}
    }

    final class AsyncEntity extends CodeBodyEntity {
	
    private final CodeBodyContext fContext;

	private final CAstNode fBodyast;

	private final CAstSourcePositionMap.Position fPosition;

	private final AsyncBodyType fBodyType;

	private AsyncEntity(Map<CAstNode,CAstEntity> entities, Node node, CodeBodyContext context, CAstNode bodyast) {
	    super(entities);
	    fPosition= makePosition(node.position());
	    fContext= context;
	    fBodyast= bodyast;
	    fBodyType= new AsyncBodyType(node, fContext.getEnclosingType());
	}

	public CAstSourcePositionMap.Position getPosition() {
	  return fPosition;
	}

	public int getKind() {
	    return X10CAstEntity.ASYNC_BODY;
	}

	public String getName() {
	    return "<activity " + fPosition.getURL() + ":" + fPosition.getFirstLine() + ":" + fPosition.getFirstCol() + ">";
	}

	public String[] getArgumentNames() {
	    return new String[0];
	}

	public CAstNode[] getArgumentDefaults() {
	  return new CAstNode[0];
	}

	public int getArgumentCount() {
	    return 0;
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
	    return fBodyType;
	}
	public String toString() {
	    return getName();
	}
    }

    class X10TranslatingVisitorImpl extends JavaTranslatingVisitorImpl implements X10TranslatorVisitor {
        @Override
        public CAstNode visit(ConstructorDecl cd, WalkContext mc) {
            if (cd.body() == null) { // PORT1.7 Body can be null
              return makeNode(mc, fFactory, cd, CAstNode.BLOCK_STMT, new CAstNode[0]);
            }
            return super.visit(cd, mc);
        }

        private MethodInstance findMethod(Type container, Name name, Type arg1, Type arg2) {
            List<Type> argTypes = new ArrayList<Type>();
            argTypes.add(arg1);
            argTypes.add(arg2);
            try {
                return fTypeSystem.findMethod(container, fTypeSystem.MethodMatcher(container, name, argTypes, fTypeSystem.emptyContext()));
            } catch (SemanticException e) {
                return null;
            }
        }

        // FIXME: workaround for XTENLANG-2278.  Remove when the WALA bridge is updated.
        @Override
        public CAstNode visit(Binary b, WalkContext context) {
            Binary.Operator op = b.operator();
            Expr left = b.left();
            Expr right = b.right();
            Position pos = b.position();
            Type Region = fTypeSystem.Region();
            if (op == Binary.Operator.DOT_DOT) {
                Assertions.productionAssertion(left.type().isInt() && right.type().isInt(), "Operator .. on ("+left.type()+","+right.type()+")");
                Name makeRectangular = Name.make("makeRectangular");
                Call mr = fNodeFactory.Call(pos, fNodeFactory.CanonicalTypeNode(pos, Region),
                        fNodeFactory.Id(pos, makeRectangular), left, right);
                Type Int = fTypeSystem.Int();
                MethodInstance mi = findMethod(Region, makeRectangular, Int, Int);
                mr = mr.methodInstance(mi);
                mr = (Call) mr.type(mi.returnType());
                return visit(mr, context);
            } else if (op == Binary.Operator.ARROW) {
                Assertions.productionAssertion(fTypeSystem.isRegion(left.type()) && fTypeSystem.isPlace(right.type()), "Operator -> on ("+left.type()+","+right.type()+")");
                Type Dist = fTypeSystem.Dist();
                Name makeConstant = Name.make("makeConstant");
                Call mc = fNodeFactory.Call(pos, fNodeFactory.CanonicalTypeNode(pos, Dist),
                        fNodeFactory.Id(pos, makeConstant), left, right);
                Type Place = fTypeSystem.Place();
                MethodInstance mi = findMethod(Dist, makeConstant, Region, Place);
                mc = mc.methodInstance(mi);
                mc = (Call) mc.type(mi.returnType());
                return visit(mc, context);
            }
            return super.visit(b, context);
        }

        public CAstNode visit(Async a, WalkContext context) {
	    CAstEntity bodyEntity= walkAsyncEntity(a, a.body(), context);
	    List clocks = a.clocks();

	    CAstNode args[] = new CAstNode[ clocks.size()+1 ];
//	    args[0] = walkNodes(a.place(), context);
	    for(int i = 0; i < clocks.size(); i++) {
	    	args[i] = walkNodes((Node)clocks.get(i), context);
	    }

	    // FUNCTION_EXPR will translate to a type wrapping the single method with the given body
	    args[args.length-1] = makeNode(context, a.body(), CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity));

	    CAstNode asyncNode= makeNode(context, a, X10CastNode.ASYNC, args);
	    context.addScopedEntity(asyncNode, bodyEntity);
	    return asyncNode;
	}

	public CAstNode visit(Finish f, WalkContext context) {
	    return makeNode(context, f, CAstNode.UNWIND,
		    makeNode(context, f, CAstNode.BLOCK_STMT, 
			    makeNode(context, X10CastNode.FINISH_ENTER, f.body().position().startOf()),
			    walkNodes(f.body(), context)),
		    makeNode(context, X10CastNode.FINISH_EXIT, f.body().position().endOf()));
	}

	private CAstNode walkIterator(X10Loop loop, final CAstNode bodyNode, WalkContext context) {
	    return walkIterator(loop.formal(), bodyNode, walkNodes(loop.domain(), context), loop.position(), context);
	}

	private CAstNode walkIterator(Formal formal, final CAstNode bodyNode, CAstNode domainNode, polyglot.util.Position bodyPos, WalkContext wc) {
	    X10Formal x10Formal= (X10Formal) formal;
	    LocalDef[] vars = x10Formal.localInstances();
	    CAstNode[] bodyStmts = new CAstNode[vars.length + 1]; // var decls + bodyNode

	    for (int i = 0; i < vars.length; i++)
		bodyStmts[i]= makeNode(wc, vars[i].position(), CAstNode.DECL_STMT,
		  fFactory.makeConstant(new CAstSymbolImpl(vars[i].name().toString(), vars[i].flags().isFinal())),
		  makeNode(wc, vars[i].position(), CAstNode.ARRAY_REF, 
		          makeNode(wc, vars[i].position(), CAstNode.VAR, fFactory.makeConstant(formal.name().id().toString())),
		          fFactory.makeConstant(TypeReference.Int),
		          fFactory.makeConstant(i)));
	    bodyStmts[vars.length] = bodyNode;
	    
	    return makeNode(wc, bodyPos, CAstNode.LOCAL_SCOPE,
		makeNode(wc, bodyPos, CAstNode.BLOCK_STMT,
		    makeNode(wc, formal, CAstNode.DECL_STMT, fFactory.makeConstant(new CAstSymbolImpl("iter tmp", false)),
			    makeNode(wc, formal, X10CastNode.ITER_INIT, domainNode)),
		    makeNode(wc, bodyPos, CAstNode.LOOP,
			makeNode(wc, formal, X10CastNode.ITER_HASNEXT,
				makeNode(wc, formal, CAstNode.VAR, fFactory.makeConstant("iter tmp"))),
			makeNode(wc, bodyPos, CAstNode.BLOCK_STMT,
			    makeNode(wc, formal, CAstNode.DECL_STMT, walkNodes(formal, wc),
				makeNode(wc, formal, X10CastNode.ITER_NEXT, makeNode(wc, formal, CAstNode.VAR, fFactory.makeConstant("iter tmp")))),
			    bodyStmts))));
	}

	/*
	public CAstNode visit(Future f, WalkContext wc) {
	    CAstEntity bodyEntity= walkAsyncEntity(f, f.body(), wc);
	    CAstNode bodyNode= makeNode(wc, f, X10CastNode.ASYNC,
		    walkNodes(f.place(), wc),
		    // FUNCTION_EXPR will translate to a type wrapping the single method with the given body
		    makeNode(wc, f.body(), CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity)));

	    wc.addScopedEntity(bodyNode, bodyEntity);
	    return bodyNode;
	}
	*/

	public CAstNode visit(Tuple t, WalkContext wc) {
	    CAstNode[] children = new CAstNode[t.arguments().size()+1];
            // N.B.: The type of the result will be Rail[T], where T is the LUB of all the child expr types.
            TypeReference tupleTypeRef = fIdentityMapper.getTypeRef(t.type());
            int idx= 0;

            children[idx++] = fFactory.makeConstant(tupleTypeRef);
	    for(Expr child: t.arguments()) {
	        children[idx++] = walkNodes(child, wc);
	    }
	    return makeNode(wc, fFactory, t, X10CastNode.TUPLE, children);
	}

	public CAstNode visit(SettableAssign n, WalkContext wc) {
	    Expr array = n.array();
	    List<Expr> indices = n.index();
	    Assign.Operator op = n.operator();
	    Expr rhs = n.right();
	    CAstNode lhsCAstNode = visitArrayAccess(n, array, indices, wc);

	    return processAssign(n, lhsCAstNode, op, rhs, wc);
	}

	public CAstNode visitArrayAccess(Expr access, Expr array, List<Expr> indices, WalkContext wc) {
            TypeReference eltTypeRef = fIdentityMapper.getTypeRef(array.type());
            CAstNode[] children= new CAstNode[indices.size()+2];

            hookUpNPETargets(access, wc); // as for base class visit(ArrayAccess), right???

            int idx= 0;
            children[idx++]= walkNodes(array, wc);
            children[idx++]= fFactory.makeConstant(eltTypeRef);
            for(Expr index: indices) {
                children[idx++]= walkNodes(index, wc);
            }
            return makeNode(wc, fFactory, access, isIndexedByPoint(indices) ? X10CastNode.ARRAY_REF_BY_POINT : CAstNode.ARRAY_REF, children);
        }

        public CAstNode visitArrayAccess1D(Expr access, Expr array, Expr index, WalkContext wc) {
            TypeReference eltTypeRef = fIdentityMapper.getTypeRef(array.type());
            CAstNode[] children= new CAstNode[3];

            int idx= 0;
            children[idx++]= walkNodes(array, wc);
            children[idx++]= fFactory.makeConstant(eltTypeRef);
            children[idx++]= walkNodes(index, wc);

            return makeNode(wc, fFactory, array, isIndexedByPoint(index) ? X10CastNode.ARRAY_REF_BY_POINT : CAstNode.ARRAY_REF, children);
        }

        private boolean isIndexedByPoint(Expr index) {
            return index.type().isSubtype(((TypeSystem) fTypeSystem).Point(), fTypeSystem.emptyContext());
        }

        private CAstNode visitArrayAssign(Expr assign, Expr array, List<Expr> indices, WalkContext wc) {
            throw new UnsupportedOperationException();
        }
        public CAstNode visit(AssignPropertyCall a, WalkContext wc) {
            // TODO process the assignments
            return makeNode(wc, fFactory, null, CAstNode.EMPTY);
        }
        
        public CAstNode visit(Call c, WalkContext wc) {
	    x10.types.MethodInstance methodInstance= c.methodInstance();
	    ContainerType methodOwner= methodInstance.container();
/*
	    //PORT1.7 Array accesses are now represented as ordinary method calls
	    if (methodOwner instanceof ClassType) {
	        ClassType classType = (ClassType) methodOwner;
	        String className = classType.fullName().toString();
	        final QName settableName = QName.make("x10.lang.Settable");
            boolean isSettable = false;
            for (final Type type : classType.interfaces()) {
              if (type instanceof Named) {
                if (((Named) type).fullName().equals(settableName)) {
                  isSettable = true;
                  break;
                }
              }
            }

	        if (className.equals("x10.lang.Array") || isSettable) {
	            if (c.name().id().toString().equals("apply")) {
	                Expr array = (Expr) c.target();
	                List<Expr> indices = c.arguments();

	                return visitArrayAccess(c, array, indices, wc);
	            } else if (c.name().id().toString().equals("set")) {
                        Expr array = (Expr) c.target();
                        List<Expr> indices = c.arguments();

                        return visitArrayAssign(c, array, indices, wc);
	            }
	        }
	    } else if (methodOwner instanceof ParametrizedType) {
	        ParametrizedType parType = (ParametrizedType) methodOwner;
	        String baseName = parType.fullName().toString();

	        if (baseName.equals("x10.lang.Future")) {
	            List<Type> typeParms= parType.typeParameters();
	            Type retType= typeParms.get(0);
	            TypeReference typeRef= TypeReference.findOrCreate(fClassLoaderRef, fIdentityMapper.typeToTypeID(retType));

	            return makeNode(wc, c, X10CastNode.FORCE, walkNodes(c.target(), wc), fFactory.makeConstant(typeRef));
	        }
	    }
*/
	    return super.visit(c, wc);
	}

	public CAstNode visit(Here h, WalkContext context) {
	    return makeNode(context, h, X10CastNode.HERE);
	}
	
	public CAstNode visit(LocalTypeDef l, WalkContext context) {
	  return fFactory.makeNode(CAstNode.EMPTY);
	}

	public CAstNode visit(Next n, WalkContext context) {
	    return makeNode(context, n, X10CastNode.NEXT);
	}

	public CAstNode visit(When w, WalkContext wc) {
	    When_c when= (When_c) w;
//          List/*<When.Branch>*/ branches= when.branches();
	    List/*<Expr>*/ exprs= when.exprs();
	    List/*<Stmt>*/ stmts= when.stmts();
	    // In the fullness of time, some analyses may want to have "when" constructs
	    // clearly marked in a more declarative fashion, but for now, this has the
	    // advantage of making the operational semantics clear, with minimal extra
	    // machinery.
            Assertions.productionAssertion(exprs.size() == stmts.size());
	    CAstNode[] whenClauses= new CAstNode[exprs.size()+1];

	    
	    CAstNode whenExit= makeNode(wc, w.position().endOf(), CAstNode.LABEL_STMT,
		    fFactory.makeConstant("when exit"),
		    makeNode(wc, CAstNode.EMPTY, w.position().endOf()));

	    wc.cfg().map(whenExit, whenExit);

	    int idx= 0;
            Iterator stmtIter= IteratorPlusOne.make(stmts.iterator(), when.stmt());
	    for(Iterator exprIter= IteratorPlusOne.make(exprs.iterator(), when.expr()); exprIter.hasNext(); idx++) {
//		Branch b= (Branch) iter.next();
                Expr expr= (Expr) exprIter.next();
                Stmt stmt= (Stmt) stmtIter.next();

		CAstNode whenBreak= makeNode(wc, expr, CAstNode.GOTO);

		whenClauses[idx]= makeNode(wc, expr, CAstNode.IF_STMT,
			walkNodes(expr, wc),
			makeNode(wc, stmt, CAstNode.BLOCK_STMT,
				walkNodes(stmt, wc),
				whenBreak));
		wc.cfg().map(whenBreak, whenBreak);
		wc.cfg().add(whenBreak, whenExit, null);
	    }
	    return makeNode(wc, w, CAstNode.BLOCK_STMT,
		    makeNode(wc, w, CAstNode.LOOP,
			    fFactory.makeConstant(true),
			    wrapBodyInAtomic(makeNode(wc, w, CAstNode.BLOCK_STMT, whenClauses), w, wc)),
	            whenExit);

	    // Alternative, quasi-declarative representation:
//	    CAstNode[] branchNodes= new CAstNode[branches.size()*2];
//
//	    int idx= 0;
//	    for(Iterator iter= branches.iterator(); iter.hasNext(); idx += 2) {
//		Branch b= (Branch) iter.next();
//
//		branchNodes[idx]= walkNodes(b.expr(), context);
//		branchNodes[idx+1]= walkNodes(b.stmt(), context);
//	    }
//	    return fFactory.makeNode(X10CastNode.WHEN, branchNodes);
	}

	public CAstNode visit(X10Formal f, WalkContext context) {
	    return fFactory.makeConstant(new CAstSymbolImpl(f.name().id().toString(), true));
	}

	public CAstNode visit(Clocked c, WalkContext context) {
		Assertions.UNREACHABLE();
	    return null;
	}

	public CAstNode visit(Atomic a, WalkContext context) {
	    final CAstNode bodyNode= walkNodes(a.body(), context);
	    return wrapBodyInAtomic(bodyNode, a, context);
	}

	private CAstNode wrapBodyInAtomic(final CAstNode bodyNode, Node n, WalkContext wc) {
	    return makeNode(wc, n, CAstNode.UNWIND,
		    makeNode(wc, n, CAstNode.BLOCK_STMT, 
			    makeNode(wc, X10CastNode.ATOMIC_ENTER, n.position().startOf()),
			    bodyNode),
		    makeNode(wc, X10CastNode.ATOMIC_EXIT, n.position().startOf()));
	}

	private boolean isIndexedByPoint(List<Expr> indices) {
	    if (indices.size() > 1) return false;
	    return isIndexedByPoint(indices.get(0));
	}

//        private int tempCtr= 0;
//
//	public CAstNode visit(ArrayConstructor ac, WalkContext wc) {
//	    Expr dist= ac.distribution();
//	    Expr init= ac.initializer();
//	    Type arrayType= ac.type();
//	    // TODO Filter arrayType so that e.g. x10.lang.IntReferenceArray becomes int[] so
//	    // that WALA doesn't complain that an array type doesn't seem to be an array type.
//	   /* if (arrayType instanceof X10ParsedClassType_c) {
//	    	X10ParsedClassType_c t = ((X10ParsedClassType_c)arrayType);
//	    	List<Type> ps = t.typeParameters();
//	    	if (ps.size() == 1) {
//	    		arrayType = ps.get(0).arrayOf();
//	    	}
//	    }
//	    // ugly hackish attempt to find array type.  unlikely to be right all the time
//	    */
//	    TypeReference arrayTypeRef= fIdentityMapper.getTypeRef(arrayType);
//	    Type baseType= ac.arrayBaseType().type();
//	    TypeReference baseTypeRef= fIdentityMapper.getTypeRef(baseType);
//
//	    if (init instanceof Closure) {
//		Closure closure= (Closure) init;
//		Formal formal1= (Formal) closure.formals().get(0); // The closure for an array ctor init always has a single argument
//		// Turn this construct into an array allocation followed by a region
//		// iteration whose body calls the initializer and assigns the result
//		// to the corresponding array slot.
//		//
//		// BLOCK_EXPR [
//		//     ASSIGN [ arrayTmp, NEW [ type, dist.region ] ],
//		//     for(point p: dist.region) {
//		//         ASSIGN [ ARRAY_REF [ arrayTmp, p ], CALL [ closure, p ] ]
//		//     }
//		//     tmp
//		// ]
//		//
//		CAstNode closureNode= walkNodes(closure, wc);
//                String arrayTempName= "array temp" + tempCtr;
//                String distTempName= "dist temp" + tempCtr++;
//		CAstSymbol arrayTemp= new AstTranslator.InternalCAstSymbol(arrayTempName, true);
//		CAstSymbol distTemp= new AstTranslator.InternalCAstSymbol(distTempName, true);
//		CAstNode distDeclNode=
//			makeNode(wc, dist, CAstNode.DECL_STMT,
//					fFactory.makeConstant(distTemp),
//					walkNodes(dist, wc));
//		CAstNode arrayNewNode= 
//			makeNode(wc, ac, CAstNode.DECL_STMT,
//				fFactory.makeConstant(arrayTemp),
//				makeNode(wc, ac, CAstNode.NEW,
//					fFactory.makeConstant(arrayTypeRef),
//					makeNode(wc, fFactory, dist, CAstNode.VAR, fFactory.makeConstant(distTempName))));
//		int dummyPC = 0; // Just wrap the kind of call; the "rear end" won't care about anything else...
//		MethodReference closureRef= createMethodRefForClosure(closure);
//		CallSiteReference closureCallSiteRef= CallSiteReference.make(dummyPC, closureRef, IInvokeInstruction.Dispatch.VIRTUAL);
//		CAstNode arrayElemInit= makeNode(wc, closure, CAstNode.BLOCK_EXPR,
//			makeNode(wc, formal1, CAstNode.ASSIGN,
//				makeNode(wc, closure, X10CastNode.ARRAY_REF_BY_POINT,
//					makeNode(wc, closure, CAstNode.VAR, fFactory.makeConstant(arrayTempName)),
//					fFactory.makeConstant(baseTypeRef),
//					makeNode(wc, fFactory, formal1, CAstNode.VAR, fFactory.makeConstant(formal1.name()))),
//				makeNode(wc, closure, CAstNode.CALL,
//					closureNode,
//					fFactory.makeConstant(closureCallSiteRef),
//					makeNode(wc, fFactory, formal1, CAstNode.VAR, fFactory.makeConstant(formal1.name())))));
//
//		CAstNode loopBody=
//			walkIterator(formal1, arrayElemInit,
//					makeNode(wc, dist, CAstNode.VAR, fFactory.makeConstant(distTempName)), closure.position(), wc);
//
//		return makeNode(wc, closure, CAstNode.BLOCK_EXPR, // NEED CAstNode.LOCAL_SCOPE or make "array temp" names unique
//			distDeclNode,
//			arrayNewNode,
//			loopBody,
//			makeNode(wc, fFactory, formal1, CAstNode.VAR, fFactory.makeConstant(arrayTempName)));
//	    } else if (init instanceof ArrayInit) {
//		ArrayInit arrayInit= (ArrayInit) init;
//		// ARRAY_NEW [ type, dist.region, walkNodes(init, wc) ]
//		CAstNode[] eltNodes= new CAstNode[arrayInit.elements().size()+1];
//		int idx= 0;
//		eltNodes[idx++]= makeNode(wc, ac, CAstNode.NEW,
//			fFactory.makeConstant(arrayTypeRef),
//			walkNodes(dist, wc));
//		for(Iterator iter= arrayInit.elements().iterator(); iter.hasNext(); ) {
//		    Expr elem= (Expr) iter.next();
//		    eltNodes[idx++]= walkNodes(elem, wc);
//		}
//		return makeNode(wc, init, CAstNode.ARRAY_LITERAL, eltNodes);
//	    } else if (init == null) {
//		return makeNode(wc, ac, CAstNode.NEW,
//			fFactory.makeConstant(arrayTypeRef),
//			walkNodes(dist, wc));
//	    } else {
//		Assertions.UNREACHABLE("ArrayConstructor has non-closure, non-ArrayInit initializer of type " + init.getClass());
//		return null;
//	    }
//	}
//
	private String castNameForType(Type type) {
	    return getTypeDict().getCAstTypeFor(type).getName();
	}
/*
	private MethodReference createMethodRefForClosure(Closure closure) {
	    List formals= closure.formals();
	    TypeName[] argTypes= new TypeName[formals.size()];
	    for(int i= 0; i < argTypes.length; i++) {
		Formal f= (Formal) formals.get(i);
		argTypes[i]= TypeName.findOrCreate(castNameForType(f.type().type()));
	    }
	    TypeName retType= TypeName.findOrCreate(castNameForType(closure.returnType().type()));
	    MethodReference closureRef= MethodReference.findOrCreate(
		    TypeReference.findOrCreate(fClassLoaderRef, "Lclosure" + new PolyglotSourcePosition(closure.position())),
		    new Selector(Atom.findOrCreateAsciiAtom("invoke"), Descriptor.findOrCreate(argTypes, retType)));
	    return closureRef;
	}
	*/

/*	
	public CAstNode visit(GenParameterExpr gpe, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}
*/
	
	public CAstNode visit(ForLoop f, WalkContext context) {
		Node breakTarget = makeBreakTarget(f);
		Node continueTarget = makeContinueTarget(f);
		String loopLabel = (String) context.getLabelMap().get(f);
		WalkContext lc = new LoopContext(context, loopLabel, breakTarget, continueTarget);

	    return makeNode(context, f, CAstNode.BLOCK_STMT,
	    		walkNodes(breakTarget, context),
	    		walkIterator(f, 
	    				makeNode(context, f, CAstNode.BLOCK_STMT,
	    						walkNodes(f.body(), lc),
	    						walkNodes(continueTarget, lc)),
	    				lc));
	}

	public CAstNode visit(Closure closure, WalkContext wc) {
	    fIdentityMapper.mapLocalAnonTypeToMethod(closure.closureDef().asType(), wc.getEnclosingMethod());

	    CAstEntity bodyEntity= walkClosureEntity(closure, closure.body(), wc);
	    CAstNode closureNode= makeNode(wc, closure.body(), CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity));

	    wc.addScopedEntity(closureNode, bodyEntity);
	    return closureNode;
	}

	public CAstNode visit(ClosureCall closureCall, WalkContext wc) {
        MethodInstance instance = closureCall.closureInstance();

        CAstNode[] children = new CAstNode[2 + instance.formalTypes().size()];
        int i = 0;
        children[i++] = walkNodes(closureCall.target(), wc);

        MethodReference methodRef = fIdentityMapper.getMethodRef(instance);

        int dummyPC = 0;
        CallSiteReference callSiteRef = CallSiteReference.make(dummyPC, methodRef, IInvokeInstruction.Dispatch.VIRTUAL);

        children[i++] = fFactory.makeConstant(callSiteRef);

        for (final Expr arg : closureCall.arguments()) {
            children[i++] = walkNodes(arg, wc);
        }

        CAstNode result = makeNode(wc, fFactory, closureCall, CAstNode.CALL, children);
        wc.cfg().map(closureCall, result);
        return result;
	}

	public CAstNode visit(ParExpr pe, WalkContext wc) {
	    return walkNodes(pe.expr(), wc);
	}
    
    public CAstNode visit(final AtStmt atStmt, final WalkContext context) {
      return makeNode(context, atStmt, CAstNode.UNWIND,
                      makeNode(context, atStmt, CAstNode.BLOCK_STMT, 
                               makeNode(context, X10CastNode.AT_STMT_ENTER, atStmt.position().startOf()),
                               walkNodes(atStmt.body(), context)),
                               makeNode(context, X10CastNode.AT_STMT_EXIT, atStmt.position().endOf()));
    }
    
    }
    
    final class ClosureBodyEntity extends CodeBodyEntity {
	private final CodeBodyContext fContext;

	private final CAstNode fBodyAst;

	private final CAstSourcePositionMap.Position fPosition;

	private final ClosureBodyType fBodyType;

	private final CAstType fEnclosingType;

    private final String[] fArgumentNames;
    
    private final CAstNode[] fArgDefaults;

	public ClosureBodyEntity(final Map<CAstNode,CAstEntity> entities, final Closure node, final CodeBodyContext context,
	                         final CAstNode bodyAst, final Type enclosingType, final String[] argumentNames,
	                         final CAstNode[] argDefaults) {
	    super(entities);
	    fContext= context;
	    fBodyAst= bodyAst;
	    fPosition= makePosition(node.position());
	    fEnclosingType= getTypeDict().getCAstTypeFor(enclosingType);
	    fBodyType= new ClosureBodyType(node.closureDef().asType(), fEnclosingType);
	    this.fArgumentNames = argumentNames;
	    this.fArgDefaults = argDefaults;
	}
	
	public CAstNode getAST() {
	    return fBodyAst;
	}

	public int getArgumentCount() {
	    return fArgumentNames.length;
	}

	public CAstNode[] getArgumentDefaults() {
	    return fArgDefaults;
	}

	public String[] getArgumentNames() {
	     return fArgumentNames;
	}

	public CAstControlFlowMap getControlFlow() {
	    return fContext.cfg();
	}

	public int getKind() {
	    return X10CAstEntity.CLOSURE_BODY;
	}

	public String getName() {
	    return "apply";
	}

	public CAstNodeTypeMap getNodeTypeMap() {
	    return fContext.getNodeTypeMap();
	}

	public CAstSourcePositionMap.Position getPosition() {
	    return fPosition;
	}

	public Collection getQualifiers() {
	    return Collections.EMPTY_LIST;
	}

	public CAstSourcePositionMap getSourceMap() {
	    return fContext.pos();
	}

	public CAstType getType() {
	    return fBodyType;
	}
    }

    private final class ClosureBodyType implements CAstType.Method {
	private final FunctionType closureType;

	private List<CAstType> argTypes;

	private List<CAstType> excTypes;

	private CAstType returnType;

	private CAstType enclosingType;

	public ClosureBodyType(FunctionType cType, CAstType enclosingType) {
	    closureType= cType;
	    this.enclosingType= enclosingType;
	}

	public int getArgumentCount() {
	    return getArgumentTypes().size();
	}

	private List<CAstType> mapTypes(List<Type> types) {
	    List<CAstType> castTypes= new ArrayList<CAstType>();
	    for(Iterator iter= types.iterator(); iter.hasNext(); ) {
		Type type= (Type) iter.next();
		CAstType castType= getTypeDict().getCAstTypeFor(type);
		castTypes.add(castType);
	    }
	    return castTypes;
	}

	public List getArgumentTypes() {
	    if (argTypes == null) {
	    	argTypes= mapTypes(closureType.argumentTypes());
	    }
	    return argTypes;
	}

	public Collection getExceptionTypes() {
	    return Collections.EMPTY_LIST;
/* there are not checked exceptions in X10
	    if (excTypes == null) {
		excTypes= mapTypes(closureType.throwTypes());
	    }
	    return excTypes;
*/
	}

	public CAstType getReturnType() {
	    if (returnType == null) {
		returnType= getTypeDict().getCAstTypeFor(closureType.returnType());
	    }
	    return returnType;
	}

	public String getName() {
	    return ""; // Closures have no names
	}

	public Collection getSupertypes() {
	    return Collections.EMPTY_LIST;
	}

	public CAstType getDeclaringType() {
	    return enclosingType;
	}
    }

    protected CAstNode walkNodes(Node n, WalkContext context) {
      if (n == null) return fFactory.makeNode(CAstNode.EMPTY);
      return X10ASTTraverser.visit(n, (X10TranslatorVisitor) getTranslator(), context);
    }
      
    protected CAstEntity walkEntity(final Node rootNode, final WalkContext context) {
      if (rootNode instanceof TypeDecl) {
        return new TypeDeclarationCAstEntity(makePosition(rootNode.position()), (TypeDecl) rootNode);
      } else  if (rootNode instanceof ProcedureDecl) {
        // We need to have a specialization for the case of exploded vars definition and initialization.
        final ProcedureDecl pd = (ProcedureDecl) rootNode;
        final Map<CAstNode, CAstEntity> memberEntities = new LinkedHashMap<CAstNode, CAstEntity>();
        final MethodContext mc = new MethodContext(pd.procedureInstance().asInstance(), memberEntities, context);

        CAstNode procedureAST = null;

        if (! pd.flags().flags().isAbstract()) {
          procedureAST = walkNodes(pd, mc);
        }

        final List<Formal> formals = pd.formals();
        final String[] argNames;
        int i = 0;
        if (! pd.flags().flags().isStatic()) {
          argNames = new String[formals.size() + 1];
          argNames[i++] = "this";
        } else {
          argNames = new String[formals.size()];
        }
        final Collection<CAstNode> bodyNodes = new ArrayList<CAstNode>();
        for (final Formal formal : formals) {
          argNames[i++] = formal.name().toString();
          if (formal instanceof X10Formal) {
            final X10Formal x10Formal = (X10Formal) formal;
            if (x10Formal.hasExplodedVars()) {
              int j = 0;
              for (final Formal explodedVarFormal : x10Formal.vars()) {
                bodyNodes.add(createExplodedVarInitialization(mc, formal, explodedVarFormal, j++));
              }
            }
          }
        }
        
        final CAstNode newBodyAst;
        if (bodyNodes.isEmpty()) {
          newBodyAst = procedureAST;
        } else {
          // We want to access directly to the block statement.
          bodyNodes.add(procedureAST.getChild(0).getChild(0));
          newBodyAst = makeNode(mc, fFactory, pd, CAstNode.LOCAL_SCOPE, 
                                makeNode(mc, fFactory, pd, CAstNode.BLOCK_STMT,
                                         bodyNodes.toArray(new CAstNode[bodyNodes.size()])));
        }

        return new ProcedureEntity(newBodyAst, fTypeSystem, pd.procedureInstance().asInstance(), argNames, memberEntities, mc);
      } else {
        return SUPER_walkEntity(rootNode, context);
      }
    }
    
    final class TypeDeclarationCAstEntity implements CAstEntity {

      TypeDeclarationCAstEntity(final CAstSourcePositionMap.Position position, final TypeDecl typeDecl) {
        this.fPosition = position;
        this.fTypeDecl = typeDecl;
      }

      public CAstNode getAST() {
        // No AST node
        return null;
      }

      public Map<CAstNode, Collection<CAstEntity>> getAllScopedEntities() {
        return Collections.emptyMap();
      }

      public int getArgumentCount() {
        return 0;
      }

      public CAstNode[] getArgumentDefaults() {
        return new CAstNode[0];
      }

      public String[] getArgumentNames() {
        return new String[0];
      }

      public CAstControlFlowMap getControlFlow() {
        // No Control Flow Map
        return null;
      }

      public int getKind() {
        return CAstEntity.TYPE_ENTITY;
      }

      public String getName() {
        return this.fTypeDecl.name().toString();
      }

      public CAstNodeTypeMap getNodeTypeMap() {
        return null;
      }

      public CAstSourcePositionMap.Position getPosition() {
        return this.fPosition;
      }

      public Collection getQualifiers() {
        return mapFlagsToQualifiers(this.fTypeDecl.typeDef().flags());
      }

      public Iterator getScopedEntities(CAstNode construct) {
        return null;
      }

      public String getSignature() {
        final StringBuilder sb = new StringBuilder();
        sb.append('L').append(getName().replace('.', '/')).append(';');
        return sb.toString();
      }

      public CAstSourcePositionMap getSourceMap() {
        // No source map
        return null;
      }

      public CAstType getType() {
        return new CAstType() {

          public String getName() {
            return fTypeDecl.name().toString();
          }

          public Collection getSupertypes() {
            return Collections.EMPTY_LIST;
          }
          
        };
      }
      
      private final CAstSourcePositionMap.Position fPosition;
      
      private final TypeDecl fTypeDecl;
      
    }
}
