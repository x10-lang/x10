--
-- The X10 KeyWord Lexer
--

%Options list
%Options fp=X10KWLexer,prefix=Char_
%options package=x10.parser
%options template=uide/KeyWordTemplateB.gi
%options export_terminals=("X10Parsersym.java", "TK_")

$Import
    GJavaKWLexer.gi
$End

$Title
    /.package $package;
    ./
$End

$Globals
    /.import java.util.*;
    ./
$End

$Define
    --
    -- Definition of macros used in the lexer template
    --
    $action_class /.$file_prefix./
    $eof_char /.Char_EOF./

$End

$Export

    activitylocal
    async
    ateach 
    atomic 
    await
    boxed
    clocked
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
    now
    nullable 
    or
    placelocal
    reference
    unsafe
    value
    when
    
$End

$Rules

    KeyWord ::= a s y n c
        /.$BeginAction
                    $setResult($_async);
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
$End

