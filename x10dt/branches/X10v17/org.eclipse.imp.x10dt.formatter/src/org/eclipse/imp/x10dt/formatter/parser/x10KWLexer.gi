--
-- The X10 KeyWord Lexer
--
%Options list
%Options fp=X10KWLexer
%options package=org.eclipse.imp.x10dt.formatter.parser
%options template=KeywordTemplate.gi

%Notice
/.
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//
./
%End

%Import
    GJavaKWLexer.gi
%End

%Globals
    /.import java.util.*;
    ./
%End

%Export

    activitylocal
    any
    async
    ateach 
    atomic 
    await
    boxed
    clocked
    compilertest
    current 
    extern
    finish
    foreach 
    fun
    future 
    here
    local
    method
    mutable
    next
    nonblocking
    now
    nullable 
    or
    placelocal
    reference
    safe
    self
    sequential
    unsafe
    value
    when
    
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

    KeyWord ::= a s y n c
        /.$BeginAction
                    $setResult($_async);
          $EndAction
        ./
              | a n y
        /.$BeginAction
                    $setResult($_any);
          $EndAction
        ./
              | a c t i v i t y l o c a l
        /.$BeginAction
                    $setResult($_activitylocal);
          $EndAction
        ./
              | a t e a c h
        /.$BeginAction
                    $setResult($_ateach);
          $EndAction
        ./
              | a t o m i c
        /.$BeginAction
                    $setResult($_atomic);
          $EndAction
        ./
              | a w a i t
        /.$BeginAction
                    $setResult($_await);
          $EndAction
        ./
              | b o x e d
        /.$BeginAction
                    $setResult($_boxed);
          $EndAction
        ./
        | c l o c k e d
        /.$BeginAction
                    $setResult($_clocked);
          $EndAction
        ./
         | c o m p i l e r t e s t
        /.$BeginAction
                    $setResult($_compilertest);
          $EndAction
        ./
              | c u r r e n t
        /.$BeginAction
                    $setResult($_current);
          $EndAction
        ./
              | e x t e r n
        /.$BeginAction
                    $setResult($_extern);
          $EndAction
        ./
              | f i n i s h
        /.$BeginAction
                    $setResult($_finish);
          $EndAction
        ./
              | f o r e a c h
        /.$BeginAction
                    $setResult($_foreach);
          $EndAction
        ./
              | f u n
        /.$BeginAction
                    $setResult($_fun);
          $EndAction
        ./
              | f u t u r e
        /.$BeginAction
                    $setResult($_future);
          $EndAction
        ./
              | h e r e
        /.$BeginAction
                    $setResult($_here);
          $EndAction
        ./
              | l o c a l
        /.$BeginAction
                    $setResult($_local);
          $EndAction
        ./
              | m e t h o d 
        /.$BeginAction
                    $setResult($_method);
          $EndAction
        ./
              | m u t a b l e
        /.$BeginAction
                    $setResult($_mutable);
          $EndAction
        ./
              | n e x t
        /.$BeginAction
                    $setResult($_next);
          $EndAction
        ./
              | n o n b l o c k i n g
        /.$BeginAction
                    $setResult($_nonblocking);
          $EndAction
        ./
              | n o w
        /.$BeginAction
                    $setResult($_now);
          $EndAction
        ./
              | n u l l a b l e
        /.$BeginAction
                    $setResult($_nullable);
          $EndAction
        ./
              | o r
        /.$BeginAction
                    $setResult($_or);
          $EndAction
        ./
              | p l a c e l o c a l
        /.$BeginAction
                    $setResult($_placelocal);
          $EndAction
        ./
              | r e f e r e n c e
        /.$BeginAction
                    $setResult($_reference);
          $EndAction
        ./
               | s a f e 
        /.$BeginAction
                    $setResult($_safe);
          $EndAction
        ./
              | s e l f
        /.$BeginAction
                    $setResult($_self);
          $EndAction
        ./
              | s e q u e n t i a l
        /.$BeginAction
                    $setResult($_sequential);
          $EndAction
        ./
              | u n s a f e
        /.$BeginAction
                    $setResult($_unsafe);
          $EndAction
        ./
              | v a l u e
        /.$BeginAction
                    $setResult($_value);
          $EndAction
        ./
              | w h e n
        /.$BeginAction
                    $setResult($_when);
          $EndAction
        ./
%End
