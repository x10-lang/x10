%options fp=PatternX10Parser
%options package=org.eclipse.imp.x10dt.formatter.parser
%options la=6
%options template=btParserTemplateF.gi
%options parent_saved,automatic_ast=toplevel,visitor=preorder,ast_directory=./ast,ast_type=ASTNode
%options prefix=TK_
%options import_terminals="Pattern-X10Lexer.gi"
%options noconflicts


%Notice
/.
// This is an experimental product.
./
%End

%Globals
    /.
    // this is a test
     import org.eclipse.imp.parser.*;
    ./
   
%End 

%Import
    x10.g
%End

%Define
    --
    -- Definition of macros used in the parser template
    --
    $ast_class /.Object./
    $additional_interfaces /. , IParser ./
%End

%Start
   Pattern
%End



%Notice
/.
////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2007 IBM Corporation.
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
//Contributors:
//    Philippe Charles (pcharles@us.ibm.com) - initial API and implementation

////////////////////////////////////////////////////////////////////////////////
./
%End

%Rules 
  Pattern ::=  %Empty
            | AmbiguousName
            | Statement
            | Expression
            | TypeDeclaration
            | ClassBodyDeclaration
            | ClassBodyDeclarations
            | PackageDeclaration
            | ArgumentList
            | BlockStatements
            | ActualTypeArgumentList
            | FormalParameterList
            | FormalParameters
            | ClockList
            | CompilationUnit
 
PackageName ::= METAVARIABLE_PackageName
X10ClassModifier ::= METAVARIABLE_X10ClassModifier
X10ClassModifiers ::= METAVARIABLE_X10ClassModifiers
Property ::= METAVARIABLE_Property
Properties ::= METAVARIABLE_Properties
identifier ::= METAVARIABLE_identifier | METAVARIABLE_Expression
Type ::= METAVARIABLE_Type
ConstExpression ::= METAVARIABLE_ConstExpression
DepParameterExp ::= METAVARIABLE_DepParameterExp
MethodModifier ::= METAVARIABLE_MethodModifier
Statement ::= METAVARIABLE_Statement
ClockList ::= METAVARIABLE_ClockList
ClassBodyDeclaration ::= METAVARIABLE_ClassBodyDeclaration
ClassBodyDeclarations ::= METAVARIABLE_ClassBodyDeclarations
TypeDeclaration ::= METAVARIABLE_TypeDeclaration
TypeDeclarations ::= METAVARIABLE_TypeDeclarations
FieldModifiers ::= METAVARIABLE_FieldModifiers
FieldModifier ::= METAVARIABLE_FieldModifier
ArgumentList ::= METAVARIABLE_ArgumentList
TypeNode ::= METAVARIABLE_TypeNode
PackageNode ::= METAVARIABLE_PackageNode
Import ::= METAVARIABLE_Import
ClassDecl ::= METAVARIABLE_ClassDecl
BlockStatements ::= METAVARIABLE_BlockStatements
BlockStatement ::= METAVARIABLE_BlockStatement
Primary ::= METAVARIABLE_Primary
ClassName ::= METAVARIABLE_ClassName
TypeName ::= METAVARIABLE_TypeName
ActualTypeArgumentList ::= METAVARIABLE_ActualTypeArgumentList
ActualTypeArgument ::= METAVARIABLE_TypeArgument
AmbiguousName ::= METAVARIABLE_AmbiguousName
WhenStatement ::= METAVARIABLE_WhenStatement
FormalParameterList ::= METAVARIABLE_FormalParameterList
LastFormalParameter ::= METAVARIABLE_LastFormalParameter
FormalParameters ::= METAVARIABLE_FormalParameters
FormalParameter ::= METAVARIABLE_FormalParameter
StatementExpression ::= METAVARIABLE_StatementExpression


             
%End

