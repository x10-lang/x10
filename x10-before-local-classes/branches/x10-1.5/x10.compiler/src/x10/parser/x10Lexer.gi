--
-- The X10 Lexer
--
%Options la=2,list
%Options fp=X10Lexer
%options single_productions
%options package=x10.parser
%options template=LexerTemplateF.gi
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
    --
    -- Additional methods for the action class not provided in the template
    --
    /.
        
        static public class DifferX10 extends DifferJava
        {
            protected DifferX10() {}

            public DifferX10(PrsStream newStream, PrsStream oldStream)
            {
                super(newStream, oldStream);
            }
            
            public DifferX10(boolean dump_input, PrsStream stream)
            {
                super.dump_input = dump_input;
                ILine [] lines = getBuffer(stream);
            }
            
            public ILine[] getBuffer(PrsStream stream)
            {
                IntTuple line_start = new IntTuple();

                int left_brace_count = 0,
                    right_brace_count = 0,
                    class_count = 0,
                    interface_count = 0;

                line_start.add(0); // skip 0th element
                int token = 1;
                while (token < stream.getSize())
                {
                    line_start.add(token);
                    if (stream.getKind(token) == TK_LBRACE)
                    {
                        left_brace_count++;
                        token++;
                    }
                    else if (stream.getKind(token) == TK_RBRACE)
                    {
                        right_brace_count++;
                        token++;
                    }
                    else
                    {
                        if ((stream.getKind(token) == TK_else ||
                             stream.getKind(token) == TK_finish) && token + 1 < stream.getSize())
                            token++;
                        
                        if (stream.getKind(token) == TK_while ||
                            stream.getKind(token) == TK_for ||
                            stream.getKind(token) == TK_ateach ||
                            stream.getKind(token) == TK_if ||
                            stream.getKind(token) == TK_when ||
                            stream.getKind(token) == TK_or ||
                            stream.getKind(token) == TK_now ||
                            stream.getKind(token) == TK_atomic)
                        {
                            token = balanceParentheses(stream, token + 1);
                        }
                        else if (stream.getKind(token) == TK_foreach ||
                                 stream.getKind(token) == TK_ateach  ||
                                 stream.getKind(token) == TK_async)
                        {
                            token = balanceParentheses(stream, token + 1);
                            token = balanceParentheses(stream, token);
                        }
                        else if (stream.getKind(token) == TK_case ||
                                 stream.getKind(token) == TK_default)
                        {
                            for (; token < stream.getSize(); token++)
                            {
                                if (stream.getKind(token) == TK_COLON)
                                {
                                    token++;
                                    break;
                                }
                                if (stream.getKind(token) == TK_LBRACE || stream.getKind(token) == TK_RBRACE)
                                    break;
                            }
                        }
                        else
                        {
                            for (; token < stream.getSize(); token++)
                            {
                                try
                                {
                                    if (stream.getKind(token) == TK_class && stream.getKind(token+1) == TK_IDENTIFIER)
                                        class_count++;
                                    else if (stream.getKind(token) == TK_interface  && stream.getKind(token+1) == TK_IDENTIFIER)
                                        interface_count++;
                                }
                                catch(ArrayIndexOutOfBoundsException e)
                                {
                                }

                                if (stream.getKind(token) == TK_SEMICOLON)
                                {
                                    token++;
                                    break;
                                }
                                if (stream.getKind(token) == TK_LBRACE || stream.getKind(token) == TK_RBRACE)
                                    break;
                            }
                        }
                    }
                }

                Line buffer[] = new Line[line_start.size() - (ignore_braces ? (left_brace_count + right_brace_count) : 0)];
                buffer[0] = new Line(stream, 0, 0); // always add the starting gate line consisting only of token 0
                line_start.add(stream.getSize()); // add a fence for the last line
                int index = 1;
                for (int line_no = 1; line_no < line_start.size() - 1; line_no++)
                {
                    if (ignore_braces &&
                        (stream.getKind(line_start.get(line_no)) == TK_LBRACE ||
                         stream.getKind(line_start.get(line_no)) == TK_RBRACE))
                        continue;
                    buffer[index++] = new Line(stream, line_start.get(line_no), line_start.get(line_no + 1));
                }
                assert (buffer.length == index);

                leftBraceCount += left_brace_count;
                rightBraceCount += right_brace_count;
                classCount += class_count;
                interfaceCount += interface_count;
                //
                // recall that buffer[0] is not used and the last statement
                // consists only of the EOF character. It is important
                // to treat the EOF as a statement in case the user
                // specifies a null file.
                //
                statementCount += (buffer.length - 2);

                if (dump_input)
                {
                    System.out.println();
                    System.out.println("Dumping file " + stream.getFileName());
                    for (int i = 1; i < buffer.length; i++)
                        System.out.println("    " + i + " " + buffer[i].toString());
                }
                
                return buffer;
            }
        }
    ./
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
