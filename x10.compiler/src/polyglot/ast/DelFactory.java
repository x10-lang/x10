/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

/**
 * A <code>DelFactory</code> constructs delegates. It is only used by
 * a <code>NodeFactory</code>, during the creation of AST nodes.
 */
public interface DelFactory
{

    //////////////////////////////////////////////////////////////////
    // Factory Methods
    //////////////////////////////////////////////////////////////////
    
    JL delFlagsNode();
    
    JL delId();
    
    JL delAmbAssign();

    JL delAmbExpr();
    
    JL delAmbPrefix();
    
    JL delAmbReceiver();
    
    JL delAmbTypeNode();
    
    JL delArrayAccess();
    
    JL delArrayInit();
    
    JL delArrayTypeNode();
    
    JL delAssert();
    
    JL delAssign();

    JL delLocalAssign();
    JL delFieldAssign();
    JL delArrayAccessAssign();
    
    JL delBinary();
    
    JL delBlock();
    
    JL delBooleanLit();
    
    JL delBranch();
    
    JL delCall();
    
    JL delCanonicalTypeNode();
    
    JL delCase();
    
    JL delCast();
    
    JL delCatch();
    
    JL delCharLit();
    
    JL delClassBody();
    
    JL delClassDecl();

    JL delClassLit();
    
    JL delClassMember();

    JL delCodeDecl();
    
    JL delCompoundStmt();

    JL delConditional();
    
    JL delConstructorCall();
    
    JL delConstructorDecl();
    
    JL delDo();
    
    JL delEmpty();
    
    JL delEval();
    
    JL delExpr();
    
    JL delField();
    
    JL delFieldDecl();
    
    JL delFloatLit();
    
    JL delFor();
    
    JL delFormal();
    
    JL delIf();
    
    JL delImport();
    
    JL delInitializer();
    
    JL delInstanceof();
    
    JL delIntLit();
    
    JL delLabeled();
    
    JL delLit();
    
    JL delLocal();
    
    JL delLocalClassDecl();
    
    JL delLocalDecl();
    
    JL delLoop();
    
    JL delMethodDecl();
    
    JL delNewArray();
    
    JL delNode();
    
    JL delNodeList();
    
    JL delNew();
    
    JL delNullLit();
    
    JL delNumLit();
    
    JL delPackageNode();
    
    JL delProcedureDecl();

    JL delReturn();
    
    JL delSourceCollection();
    
    JL delSourceFile();
    
    JL delSpecial();
    
    JL delStmt();
    
    JL delStringLit();
    
    JL delSwitchBlock();
    
    JL delSwitchElement();
    
    JL delSwitch();
    
    JL delSynchronized();
    
    JL delTerm();
    
    JL delThrow();
    
    JL delTry();
    
    JL delTypeNode();
    
    JL delUnary();
    
    JL delWhile();
}
