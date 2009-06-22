--
-- The X10 Lexer
--
%Options la=2,list
%Options fp=X10Lexer
%options single_productions
%options package=org.eclipse.imp.x10dt.formatter.parser
%options template=LexerTemplate.gi
%options filter=x10KWLexer.gi

%Notice
/.
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//
./
%End

%Import
    GJavaLexer.gi
%End

%Globals 
	/.
	import java.util.Map;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.io.File;
    import java.util.ArrayList;
    ./
%End

%Define
    --
    -- Definition of macro used in the included file LexerBasicMapB.g
    --
    $kw_lexer_class /.$x10KWLexer./
%End

%Export
--     RANGE
    ARROW
%End

%Headers
 
%End

%Rules
--     Token ::= IntLiteralAndRange
-- 
--     Token ::= '.' '.'
--          /.$BeginAction
--                      makeToken($_RANGE);
--            $EndAction
--          ./
-- 

    Token ::= '-' '>'
        /.$BeginAction
                    makeToken($_ARROW);
          $EndAction
        ./

--     IntLiteralAndRange ::= Integer '.' '.'
--         /.$BeginAction
--                     makeToken($getToken(1), $getRightSpan(1), $_IntegerLiteral);
--                     makeToken($getToken(2), $getToken(3), $_RANGE);
--           $EndAction
--         ./
%End
