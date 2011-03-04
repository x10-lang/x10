%options fp=X10Parser,states
%options la=6
--%options parent_saved,automatic_ast=toplevel,visitor=preorder,ast_directory=./ast,ast_type=ASTNode
%options variables=nt
%options conflicts --BRT
%options softkeywords
%options package=x10dt.formatter.parser
%options template=btParserTemplateF.gi
%options import_terminals="Pattern-X10Lexer.gi"
%options prefix=TK_


%Notice
/.
// This is the grammar for parsing formatting patterns for  the X10 language.
./
%End

%Globals
    /.
    // this is a test
     import org.eclipse.imp.parser.*;
     import x10.parser.X10ParsedName;
     import lpg.runtime.IMessageHandler;
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
    $additional_interfaces /. , IParser, ParseErrorCodes ./
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
-- Add 1 rule to this pattern non-terminal for each language construct 
--    for which you wish to write a formatting rule. 
  Pattern ::=  %Empty
            | AmbiguousName
            | Statement
            | Expression
            | TypeDeclaration    -- BRT went to here
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
-- new: closures? 
 
-- Add a rule below for each  metavariable that can be used as a placeholder in a 
-- formatting pattern
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
ActualTypeArgumentList ::= METAVARIABLE_TypeArgumentList --BRT
ActualTypeArgument ::= METAVARIABLE_TypeArgument --BRT
AmbiguousName ::= METAVARIABLE_AmbiguousName
WhenStatement ::= METAVARIABLE_WhenStatement
FormalParameterList ::= METAVARIABLE_FormalParameterList
LastFormalParameter ::= METAVARIABLE_LastFormalParameter
FormalParameters ::= METAVARIABLE_FormalParameters
FormalParameter ::= METAVARIABLE_FormalParameter
StatementExpression ::= METAVARIABLE_StatementExpression


             
%End

