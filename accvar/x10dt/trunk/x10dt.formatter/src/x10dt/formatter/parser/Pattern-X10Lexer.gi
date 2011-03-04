%options la=2
%options fp=X10Lexer
%options single_productions
%options package=x10dt.formatter.parser
%options template=LexerTemplateF.gi
%options filter=X10KWLexer.gi
%options prefix=TK_

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
    x10Lexer.gi
%End



%Define
    $kw_lexer_class /.$X10KWLexer./
    $additional_interfaces /. , ILexer ./
%End

%Export
METAVARIABLE_PackageName
METAVARIABLE_X10ClassModifier
METAVARIABLE_X10ClassModifiers
METAVARIABLE_Property
METAVARIABLE_Properties
METAVARIABLE_identifier
METAVARIABLE_Type
METAVARIABLE_ConstExpression
METAVARIABLE_DepParameterExp
METAVARIABLE_MethodModifier
METAVARIABLE_Statement
METAVARIABLE_ClockList
METAVARIABLE_Expression
METAVARIABLE_FieldModifier
METAVARIABLE_FieldModifiers
METAVARIABLE_ArgumentList
METAVARIABLE_Object
METAVARIABLE_TypeNode
METAVARIABLE_PackageNode
METAVARIABLE_Import
METAVARIABLE_ClassDecl
METAVARIABLE_ClassBodyDeclaration
METAVARIABLE_ClassBodyDeclarations
METAVARIABLE_TypeDeclaration
METAVARIABLE_TypeDeclarations
METAVARIABLE_BlockStatement
METAVARIABLE_BlockStatements
METAVARIABLE_ClassName
METAVARIABLE_TypeName
METAVARIABLE_TypeArgument --BRT
METAVARIABLE_TypeArgumentList  --BRT
METAVARIABLE_Primary
METAVARIABLE_AmbiguousName
METAVARIABLE_WhenStatement
METAVARIABLE_FormalParameterList
METAVARIABLE_LastFormalParameter
METAVARIABLE_FormalParameters
METAVARIABLE_FormalParameter
METAVARIABLE_StatementExpression

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
    DecimalOpt ::= %Empty | Decimal

    Token ::= '<' 'P' 'a' 'c' 'k' 'a' 'g' 'e' 'N' 'a' 'm' 'e' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_PackageName);
      $EndJava
     ./
    Token ::= '<' 'X' '1' '0' 'C' 'l' 'a' 's' 's' 'M' 'o' 'd' 'i' 'f' 'i' 'e' 'r' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_X10ClassModifier);
      $EndJava
     ./
    Token ::= '<' 'X' '1' '0' 'C' 'l' 'a' 's' 's' 'M' 'o' 'd' 'i' 'f' 'i' 'e' 'r' 's' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_X10ClassModifiers);
      $EndJava
     ./
    Token ::= '<' 'P' 'r' 'o' 'p' 'e' 'r' 't' 'y' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_Property);
      $EndJava
     ./
    Token ::= '<' 'P' 'r' 'o' 'p' 'e' 'r' 't' 'i' 'e' 's' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_Properties);
      $EndJava
     ./
    Token ::= '<' 'i' 'd' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_identifier);
      $EndJava
     ./
    Token ::= '<' 'T' 'y' 'p' 'e' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_Type);
      $EndJava
     ./
    Token ::= '<' 'C' 'o' 'n' 's' 't' 'E' 'x' 'p' 'r' 'e' 's' 's' 'i' 'o' 'n' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_ConstExpression);
      $EndJava
     ./
    Token ::= '<' 'D' 'e' 'p' 'P' 'a' 'r' 'a' 'm' 'e' 't' 'e' 'r' 'E' 'x' 'p' 'r' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_DepParameterExp);
      $EndJava
     ./
    Token ::= '<' 'M' 'e' 't' 'h' 'o' 'd' 'M' 'o' 'd' 'i' 'f' 'i' 'e' 'r' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_MethodModifier);
      $EndJava
     ./
    Token ::= '<' 'S' 't' 'a' 't' 'e' 'm' 'e' 'n' 't' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_Statement);
      $EndJava
     ./
    Token ::= '<' 'C' 'l' 'o' 'c' 'k' 'L' 'i' 's' 't' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_ClockList);
      $EndJava
     ./
    Token ::= '<' 'E' 'x' 'p' 'r' 'e' 's' 's' 'i' 'o' 'n' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_Expression);
      $EndJava
     ./
    Token ::= '<' 'F' 'i' 'e' 'l' 'd' 'M' 'o' 'd' 'i' 'f' 'i' 'e' 'r' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_FieldModifier);
      $EndJava
     ./
      Token ::= '<' 'F' 'i' 'e' 'l' 'd' 'M' 'o' 'd' 'i' 'f' 'i' 'e' 'r' 's' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_FieldModifiers);
      $EndJava
     ./
    Token ::= '<' 'A' 'r' 'g' 'u' 'm' 'e' 'n' 't' 'L' 'i' 's' 't' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_ArgumentList);
      $EndJava
     ./
    Token ::= '<' 'T' 'y' 'p' 'e' 'N' 'o' 'd' 'e' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_TypeNode);
      $EndJava
     ./
    Token ::= '<' 'P' 'a' 'c' 'k' 'a' 'g' 'e' 'N' 'o' 'd' 'e' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_PackageNode);
      $EndJava
     ./
    Token ::= '<' 'I' 'm' 'p' 'o' 'r' 't' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_Import);
      $EndJava
     ./
    Token ::= '<' 'C' 'l' 'a' 's' 's' 'D' 'e' 'c' 'l' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_ClassDecl);
      $EndJava
     ./
  Token ::= '<'  'D' 'e' 'c' 'l' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_ClassBodyDeclaration);
      $EndJava
     ./
     Token ::= '<'  'D' 'e' 'c' 'l' 's' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_ClassBodyDeclarations);
      $EndJava
     ./
        Token ::= '<'  'T' 'y' 'p' 'e' 'D' 'e' 'c' 'l' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_TypeDeclaration);
      $EndJava
     ./
        Token ::= '<'  'T' 'y' 'p' 'e' 'D' 'e' 'c' 'l' 's' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_TypeDeclarations);
      $EndJava
     ./
        Token ::= '<'  'B' 'l' 'o' 'c' 'k' 'S' 't' 'a' 't' 'e' 'm' 'e' 'n' 't' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_BlockStatement);
      $EndJava
     ./
          Token ::= '<'  'B' 'l' 'o' 'c' 'k' 'S' 't' 'a' 't' 'e' 'm' 'e' 'n' 't' 's' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_BlockStatements);
      $EndJava
     ./
           Token ::= '<'  'C' 'l' 'a' 's' 's' 'N' 'a' 'm' 'e' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_ClassName);
      $EndJava
     ./
            Token ::= '<' 'T' 'y' 'p' 'e' 'N' 'a' 'm' 'e'   DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_TypeName);
      $EndJava
     ./
     Token ::= '<' 'T' 'y' 'p' 'e' 'A' 'r' 'g' 'u' 'm' 'e' 'n' 't'   DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_TypeArgument); // BRT
      $EndJava
     ./
     Token ::= '<' 'T' 'y' 'p' 'e' 'A' 'r' 'g' 'u' 'm' 'e' 'n' 't' 'L' 'i' 's' 't'  DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_TypeArgumentList);  //BRT
      $EndJava
     ./
     Token ::= '<' 'P' 'r' 'i' 'm' 'a' 'r' 'y'  DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_Primary);
      $EndJava
     ./
     Token ::= '<' 'A' 'm' 'b' 'i' 'g' 'u' 'o' 'u' 's' 'N' 'a' 'm' 'e'  DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_AmbiguousName);
      $EndJava
     ./
      Token ::= '<' 'W' 'h' 'e' 'n' 'S' 't' 'a' 't' 'e' 'm' 'e' 'n' 't' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_WhenStatement);
      $EndJava
     ./
         Token ::= '<' 'F' 'o' 'r' 'm' 'a' 'l' 'P' 'a' 'r' 'a' 'm' 'e' 't' 'e' 'r' 'L' 'i' 's' 't' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_FormalParameterList);
      $EndJava
     ./
           Token ::= '<' 'L' 'a' 's' 't' 'F' 'o' 'r' 'm' 'a' 'l' 'P' 'a' 'r' 'a' 'm' 'e' 't' 'e' 'r' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_LastFormalParameter);
      $EndJava
     ./
            Token ::= '<' 'F' 'o' 'r' 'm' 'a' 'l' 'P' 'a' 'r' 'a' 'm' 'e' 't' 'e' 'r' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_FormalParameter);
      $EndJava
     ./
              Token ::= '<' 'F' 'o' 'r' 'm' 'a' 'l' 'P' 'a' 'r' 'a' 'm' 'e' 't' 'e' 'r' 's' DecimalOpt '>'
     /.
      $BeginJava
         makeToken($_METAVARIABLE_FormalParameters);
      $EndJava
     ./
     
     Token ::= '<' 'S' '' 't' 'a' 't' 'e' 'm' 'e' 'n' 't' 'E' 'x' 'p' 'r' 'e' 's' 's' 'i' 'o' 'n' DecimalOpt '>'
     /.
       $BeginJava
         makeToken($_METAVARIABLE_StatementExpression);
       $EndJava
     ./
%End


