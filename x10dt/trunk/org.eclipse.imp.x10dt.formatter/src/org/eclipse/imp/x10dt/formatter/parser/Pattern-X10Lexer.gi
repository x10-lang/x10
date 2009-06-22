%options fp=PatternX10Lexer
%options package=org.eclipse.imp.x10dt.formatter.parser
%Options la=2
%options single_productions
%options template=LexerTemplate.gi
%options filter=x10KWLexer.gi

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
    $kw_lexer_class /.$x10KWLexer./
    $additional_interfaces /. , ILexer ./
%End


