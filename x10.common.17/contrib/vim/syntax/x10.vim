" Vim syntax file
" Language:     X10
" Maintainer:   Dave Cunningham <dc04@doc.ic.ac.uk>
" URL:          
" Last Change:  2008 Oct 10

" Quit when a syntax file was already loaded
if !exists("main_syntax")
  if version < 600
    syntax clear
  elseif exists("b:current_syntax")
    finish
  endif
  " we define it here so that included files can test for it
  let main_syntax='x10'
endif

" don't use standard HiLink, it will not work with included syntax files
if version < 508
  command! -nargs=+ XTenHiLink hi link <args>
else
  command! -nargs=+ XTenHiLink hi def link <args>
endif

" some characters that cannot be in a x10 program (outside a string)
syn match   x10Error "[\\@`]"
syn match   x10Error "<<<\|\.\.\|=>\|<>\|||=\|&&=\|[^-]->\|\*\/"
syn match   x10OK "\.\.\."

" use separate name so that it can be deleted in an x10 equiv. of javacc.vim
syn match   x10Error2 "#\|=<"
XTenHiLink  x10Error2 x10Error



" keyword definitions
syn keyword x10External         package
syn match   x10External         "\<import\>\(\s\+static\>\)\?"
syn keyword x10Error            transient volatile serializable enum strictfp 
syn keyword x10Error            synchronized 
syn keyword x10Error            to 
syn keyword x10CommentError     inlined ref 
syn keyword x10Conditional      if else switch
syn match   x10Conditional      "=>"
syn match   x10Conditional      "<:"
syn match   x10Conditional      ":>"
syn keyword x10Repeat           while for do in at async finish foreach ateach clocked
syn match   x10Repeat           "\.\."
syn keyword x10Boolean          true false
"syn match   x10Number         "\<[A-Z_][A-Z_0-9]*\>"
syn keyword x10Constant         null here
syn keyword x10Typedef          this super
syn keyword x10Operator         new instanceof as
syn keyword x10Type             Object Ref Value String Number Integer Void
"syn keyword x10Type             Object Ref Value string number integer void
syn keyword x10Type             Boolean Char Byte Short Int Long Float Double Void Nat
"syn keyword x10Type             boolean char byte short int long float double void nat
syn keyword x10Type             Int8 Int16 Int32 Int64
"syn keyword x10Type             int8 int16 int32 int64
syn keyword x10Type             Rail ValRail Array ValArray
syn keyword x10Type             Point Region Dist Clock Place
"syn keyword x10Type             point region dist clock place
syn keyword x10Statement        return
syn keyword x10StorageClass     static val var def shared final native type value 
syn keyword x10StorageClass     const incomplete extern
syn keyword x10Exceptions       throw try catch finally
syn keyword x10Assert           assert
syn keyword x10MethodDecl       throws
syn keyword x10ClassDecl        extends implements interface
" to differentiate the keyword class from MyClass.class we use a match here
syn match   x10Typedef          "\.\s*\<class\>"ms=s+1
syn match   x10ClassDecl        "^class\>"
syn match   x10ClassDecl        "[^.]\s*\<class\>"ms=s+1
syn match   x10Annotation      "@[_$a-zA-Z][_$a-zA-Z0-9_]*\>"
syn match   x10ClassDecl       "@interface\>"
syn keyword x10Branch           atomic next break continue nextgroup=x10UserLabelRef skipwhite
syn match   x10UserLabelRef     "\k\+" contained
syn match   x10VarArg           "\.\.\."
syn keyword x10ScopeDecl        public protected private abstract

if filereadable(expand("<sfile>:p:h")."/x10id.vim")
  source <sfile>:p:h/x10id.vim
endif

"these need tweaking to work with x10 because of the use of : in declarations
"syn region  x10LabelRegion     transparent matchgroup=x10Label start="\<case\>" matchgroup=NONE end=":" contains=x10Number,x10Character
"syn match   x10UserLabel       "^\s*[_$a-zA-Z][_$a-zA-Z0-9_]*\s*:"he=e-1 contains=x10Label
syn keyword x10Label            default
syn keyword x10Label            case

" The following cluster contains all x10 groups except the contained ones
syn cluster x10Top add=x10External,x10Error,x10Error,x10Branch,x10LabelRegion,x10Label,x10Conditional,x10Repeat,x10Boolean,x10Constant,x10Typedef,x10Operator,x10Type,x10Type,x10Statement,x10StorageClass,x10Assert,x10Exceptions,x10MethodDecl,x10ClassDecl,x10ClassDecl,x10ClassDecl,x10ScopeDecl,x10Error,x10Error2,x10UserLabel,x10LangObject,x10Annotation,x10VarArg


" Comments
syn keyword x10Todo              contained TODO FIXME XXX
if exists("x10_comment_strings")
  syn region  x10CommentString    contained start=+"+ end=+"+ end=+$+ end=+\*/+me=s-1,he=s-1 contains=x10Special,x10CommentStar,x10SpecialChar,@Spell
  syn region  x10Comment2String   contained start=+"+  end=+$\|"+  contains=x10Special,x10SpecialChar,@Spell
  syn match   x10CommentCharacter contained "'\\[^']\{1,6\}'" contains=x10SpecialChar
  syn match   x10CommentCharacter contained "'\\''" contains=x10SpecialChar
  syn match   x10CommentCharacter contained "'[^\\]'"
  syn cluster x10CommentSpecial add=x10CommentString,x10CommentCharacter,x10Number
  syn cluster x10CommentSpecial2 add=x10Comment2String,x10CommentCharacter,x10Number
endif
syn region  x10Comment           start="/\*"  end="\*/" contains=@x10CommentSpecial,x10Todo,@Spell,x10CommentError
syn match   x10CommentStar      contained "^\s*\*[^/]"me=e-1
syn match   x10CommentStar      contained "^\s*\*$"
syn match   x10LineComment      "//.*" contains=@x10CommentSpecial2,x10Todo,@Spell
XTenHiLink x10CommentString x10StrinG
XTenHiLink x10Comment2String x10String
XTenHiLink x10CommentCharacter x10Character

syn cluster x10Top add=x10Comment,x10LineComment

" match the special comment /**/
syn match   x10Comment           "/\*\*/"

" Strings and constants
syn match   x10SpecialError     contained "\\."
syn match   x10SpecialCharError contained "[^']"
syn match   x10SpecialChar      contained "\\\([4-9]\d\|[0-3]\d\d\|[\"\\'ntbrf]\|u\x\{4\}\)"
syn region  x10String           start=+"+ end=+"+ end=+$+ contains=x10SpecialChar,x10SpecialError,@Spell
" next line disabled, it can cause a crash for a long line
"syn match   x10StringError       +"\([^"\\]\|\\.\)*$+
syn match   x10Character         "'[^']*'" contains=x10SpecialChar,x10SpecialCharError
syn match   x10Character         "'\\''" contains=x10SpecialChar
syn match   x10Character         "'[^\\]'"
syn match   x10Number            "\<\(0[0-7]*\|0[xX]\x\+\|\d\+\)[lL]\=\>"
syn match   x10Number            "\(\<\d\+\.\d+\|\.\d\+\)\([eE][-+]\=\d\+\)\=[fFdD]\="
syn match   x10Number            "\<\d\+\.[eE][-+]\=\d\+[fFdD]\="
syn match   x10Number            "\<\d\+\.\([eE][-+]\=\d\+\)\=[fFdD]"
syn match   x10Number            "\<\d\+\.[^.]"me=e-1
syn match   x10Number            "\<\d\+[eE][-+]\=\d\+[fFdD]\=\>"
syn match   x10Number            "\<\d\+\([eE][-+]\=\d\+\)\=[fFdD]\>"

" unicode characters
syn match   x10Special "\\u\d\{4\}"

syn cluster x10Top add=x10String,x10Character,x10Number,x10Special,x10StringError

if exists("x10_highlight_functions")
  if x10_highlight_functions == "indent"
    syn match  x10FuncDef "^\(\t\| \{8\}\)[_$a-zA-Z][_$a-zA-Z0-9_. \[\]]*([^-+*/()]*)" contains=x10ScopeDecl,x10Type,x10StorageClass,@x10Classes
    syn region x10FuncDef start=+^\(\t\| \{8\}\)[$_a-zA-Z][$_a-zA-Z0-9_. \[\]]*([^-+*/()]*,\s*+ end=+)+ contains=x10ScopeDecl,x10Type,x10StorageClass,@x10Classes
    syn match  x10FuncDef "^  [$_a-zA-Z][$_a-zA-Z0-9_. \[\]]*([^-+*/()]*)" contains=x10ScopeDecl,x10Type,x10StorageClass,@x10Classes
    syn region x10FuncDef start=+^  [$_a-zA-Z][$_a-zA-Z0-9_. \[\]]*([^-+*/()]*,\s*+ end=+)+ contains=x10ScopeDecl,x10Type,x10StorageClass,@x10Classes
  else
    " This line catches method declarations at any indentation>0, but it assumes
    " two things:
    "   1. class names are always capitalized (ie: Button)
    "   2. method names are never capitalized (except constructors, of course)
    syn region x10FuncDef start=+^\s\+\(\(public\|protected\|private\|static\|abstract\|final\|native\|synchronized\)\s\+\)*\(\(void\|boolean\|char\|byte\|short\|int\|long\|float\|double\|\([A-Za-z_][A-Za-z0-9_$]*\.\)*[A-Z][A-Za-z0-9_$]*\)\(<[^>]*>\)\=\(\[\]\)*\s\+[a-z][A-Za-z0-9_$]*\|[A-Z][A-Za-z0-9_$]*\)\s*([^0-9]+ end=+)+ contains=x10ScopeDecl,x10Type,x10StorageClass,x10Comment,x10LineComment,@x10Classes
  endif
  syn match  x10Braces  "[{}]"
  syn cluster x10Top add=x10FuncDef,x10Braces
endif

if exists("x10_highlight_debug")

  " Strings and constants
  syn match   x10DebugSpecial           contained "\\\d\d\d\|\\."
  syn region  x10DebugString            contained start=+"+  end=+"+  contains=x10DebugSpecial
  syn match   x10DebugStringError      +"\([^"\\]\|\\.\)*$+
  syn match   x10DebugCharacter contained "'[^\\]'"
  syn match   x10DebugSpecialCharacter contained "'\\.'"
  syn match   x10DebugSpecialCharacter contained "'\\''"
  syn match   x10DebugNumber            contained "\<\(0[0-7]*\|0[xX]\x\+\|\d\+\)[lL]\=\>"
  syn match   x10DebugNumber            contained "\(\<\d\+\.\d*\|\.\d\+\)\([eE][-+]\=\d\+\)\=[fFdD]\="
  syn match   x10DebugNumber            contained "\<\d\+[eE][-+]\=\d\+[fFdD]\=\>"
  syn match   x10DebugNumber            contained "\<\d\+\([eE][-+]\=\d\+\)\=[fFdD]\>"
  syn keyword x10DebugBoolean           contained true false
  syn keyword x10DebugType              contained null this super
  syn region x10DebugParen  start=+(+ end=+)+ contained contains=x10Debug.*,x10DebugParen

  " to make this work you must define the highlighting for these groups
  syn match x10Debug "\<System\.\(out\|err\)\.print\(ln\)*\s*("me=e-1 contains=x10Debug.* nextgroup=x10DebugParen
  syn match x10Debug "\<p\s*("me=e-1 contains=x10Debug.* nextgroup=x10DebugParen
  syn match x10Debug "[A-Za-z][a-zA-Z0-9_]*\.printStackTrace\s*("me=e-1 contains=x10Debug.* nextgroup=x10DebugParen
  syn match x10Debug "\<trace[SL]\=\s*("me=e-1 contains=x10Debug.* nextgroup=x10DebugParen

  syn cluster x10Top add=x10Debug

  if version >= 508 || !exists("did_c_syn_inits")
    XTenHiLink x10Debug          Debug
    XTenHiLink x10DebugString            DebugString
    XTenHiLink x10DebugStringError       x10Error
    XTenHiLink x10DebugType              DebugType
    XTenHiLink x10DebugBoolean           DebugBoolean
    XTenHiLink x10DebugNumber            Debug
    XTenHiLink x10DebugSpecial           DebugSpecial
    XTenHiLink x10DebugSpecialCharacter DebugSpecial
    XTenHiLink x10DebugCharacter         DebugString
    XTenHiLink x10DebugParen             Debug

    XTenHiLink DebugString               String
    XTenHiLink DebugSpecial              Special
    XTenHiLink DebugBoolean              Boolean
    XTenHiLink DebugType                 Type
  endif
endif

if exists("x10_mark_braces_in_parens_as_errors")
  syn match x10InParen           contained "[{}]"
  XTenHiLink x10InParen x10Error
  syn cluster x10Top add=x10InParen
endif

" catch errors caused by wrong parenthesis
syn region  x10ParenT  transparent matchgroup=x10Paren  start="("  end=")" contains=@x10Top,x10ParenT1
syn region  x10ParenT1 transparent matchgroup=x10Paren1 start="(" end=")" contains=@x10Top,x10ParenT2 contained
syn region  x10ParenT2 transparent matchgroup=x10Paren2 start="(" end=")" contains=@x10Top,x10ParenT  contained
syn match   x10ParenError       ")"
" catch errors caused by wrong square parenthesis
syn region  x10ParenT  transparent matchgroup=x10Paren  start="\["  end="\]" contains=@x10Top,x10ParenT1
syn region  x10ParenT1 transparent matchgroup=x10Paren1 start="\[" end="\]" contains=@x10Top,x10ParenT2 contained
syn region  x10ParenT2 transparent matchgroup=x10Paren2 start="\[" end="\]" contains=@x10Top,x10ParenT  contained
syn match   x10ParenError       "\]"

XTenHiLink x10ParenError       x10Error

if !exists("x10_minlines")
  let x10_minlines = 10
endif
exec "syn sync ccomment x10Comment minlines=" . x10_minlines

" The default highlighting.
if version >= 508 || !exists("did_x10_syn_inits")
  if version < 508
    let did_x10_syn_inits = 1
  endif
  XTenHiLink x10FuncDef         Function
  XTenHiLink x10VarArg                 Function
  XTenHiLink x10Braces                  Function
  XTenHiLink x10Branch                  Conditional
  XTenHiLink x10UserLabelRef            x10UserLabel
  XTenHiLink x10Label                   Label
  XTenHiLink x10UserLabel               Label
  XTenHiLink x10Conditional             Conditional
  XTenHiLink x10Repeat                  Repeat
  XTenHiLink x10Exceptions              Exception
  XTenHiLink x10Assert                  Statement
  XTenHiLink x10StorageClass            StorageClass
  XTenHiLink x10MethodDecl              x10StorageClass
  XTenHiLink x10ClassDecl               x10StorageClass
  XTenHiLink x10ScopeDecl               x10StorageClass
  XTenHiLink x10Boolean         Boolean
  XTenHiLink x10Special         Special
  XTenHiLink x10SpecialError            Error
  XTenHiLink x10SpecialCharError        Error
  XTenHiLink x10String                  String
  XTenHiLink x10Character               Character
  XTenHiLink x10SpecialChar             SpecialChar
  XTenHiLink x10Number                  Number
  XTenHiLink x10Error                   Error
  XTenHiLink x10CommentError            Error
  XTenHiLink x10StringError             Error
  XTenHiLink x10Statement               Statement
  XTenHiLink x10Operator                Operator
  XTenHiLink x10Comment         Comment
  XTenHiLink x10DocComment              Comment
  XTenHiLink x10LineComment             Comment
  XTenHiLink x10Constant                Constant
  XTenHiLink x10Typedef         Typedef
  XTenHiLink x10Todo                    Todo
  XTenHiLink x10Annotation             PreProc

  XTenHiLink x10CommentTitle            SpecialComment
  XTenHiLink x10DocTags         Special
  XTenHiLink x10DocParam                Function
  XTenHiLink x10DocSeeTagParam          Function
  XTenHiLink x10CommentStar             x10Comment

  XTenHiLink x10Type                    Type
  XTenHiLink x10External                Include

  XTenHiLink htmlComment                Special
  XTenHiLink htmlCommentPart            Special
  XTenHiLink x10SpaceError              Error
endif

delcommand XTenHiLink

let b:current_syntax = "x10"

if main_syntax == 'x10'
  unlet main_syntax
endif

let b:spell_options="contained"

" vim: ts=8
