" Vim syntax file
" Language:    X10
" Maintainer:  Nate Nystrom
" Last Change: 2009 Mar 14
"
" Based on:
" Language:    Scala
" Maintainer:  Stefan Matthias Aust
" Last Change: 2006 Apr 13

if version < 600
  syntax clear
elseif exists("b:current_syntax")
  finish
endif

syn case match
syn sync minlines=50

" most X10 keywords
syn keyword x10Keyword abstract as assert async at ateach atomic break case catch class clocked continue def default do else extends false final finally finish for goto haszero here if implements import in instanceof interface native new next null offer offers operator package private property protected public resume return self static struct super switch this throw transient true try type val var void when while
syn match x10Keyword "=>"
syn match x10Keyword "<-"
syn match x10Keyword "_"

syn match x10Operator ":\{2,\}" "this is not a type

" package and import statements
syn keyword x10Package package nextgroup=x10Fqn skipwhite
syn keyword x10Import import nextgroup=x10Fqn skipwhite
syn match x10Fqn "\<[._$a-zA-Z0-9,]*" contained nextgroup=x10FqnSet
syn region x10FqnSet start="{" end="}" contained

" boolean literals
syn keyword x10Boolean true false

" definitions
syn keyword x10Def def nextgroup=x10DefName skipwhite
syn keyword x10Val val nextgroup=x10ValName skipwhite
syn keyword x10Var var nextgroup=x10VarName skipwhite
syn keyword x10Class class nextgroup=x10ClassName skipwhite
syn keyword x10Object object nextgroup=x10ClassName skipwhite
syn keyword x10Trait trait nextgroup=x10ClassName skipwhite
syn match x10DefName "[^ =:;([]\+" contained nextgroup=x10DefSpecializer skipwhite
syn match x10ValName "[^ =:;([]\+" contained
syn match x10VarName "[^ =:;([]\+" contained 
syn match x10ClassName "[^ =:;(\[]\+" contained nextgroup=x10ClassSpecializer skipwhite
syn region x10DefSpecializer start="\[" end="\]" contained contains=x10DefSpecializer
syn region x10ClassSpecializer start="\[" end="\]" contained contains=x10ClassSpecializer

" type constructor (actually anything with an uppercase letter)
syn match x10Constructor "\<[A-Z][_$a-zA-Z0-9]*\>" nextgroup=x10ConstructorSpecializer
syn region x10ConstructorSpecializer start="\[" end="\]" contained contains=x10ConstructorSpecializer

" method call
syn match x10Root "\<[a-zA-Z][_$a-zA-Z0-9]*\."me=e-1
syn match x10MethodCall "\.[a-z][_$a-zA-Z0-9]*"ms=s+1

" type declarations in val/var/def
syn match x10Type ":\s*\(=>\s*\)\?[._$a-zA-Z0-9]\+\(\[[^]]*\]\+\)\?\(\s*\(<:\|>:\|#\|=>\)\s*[._$a-zA-Z0-9]\+\(\[[^]]*\]\+\)*\)*"ms=s+1

" comments
syn match x10Todo "[tT][oO][dD][oO]" contained
syn match x10LineComment "//.*" contains=x10Todo
syn region x10Comment start="/\*" end="\*/" contains=x10Todo
syn case ignore
syn include @x10Html syntax/html.vim
unlet b:current_syntax
syn case match
syn region x10DocComment start="/\*\*" end="\*/" contains=x10DocTags,x10Todo,@x10Html
syn region x10DocTags start="{@\(link\|linkplain\|inherit[Dd]oc\|doc[rR]oot\|value\)" end="}" contained
syn match x10DocTags "@[a-z]\+" contained

" string literals with escapes
syn region x10String start="\"" skip="\\\"" end="\"" contains=x10StringEscape
syn match x10StringEscape "\\u[0-9a-f][0-9a-f][0-9a-f][0-9a-f]" contained
syn match x10StringEscape "\\[nrfvb\\\"]" contained

" number literals
syn match x10Number "\<\(0[0-7]*\|0[xX]\x\+\|\d\+\)[lL]\=\>"
syn match x10Number "\(\<\d\+\.\d*\|\.\d\+\)\([eE][-+]\=\d\+\)\=[fFdD]\="
syn match x10Number "\<\d\+[eE][-+]\=\d\+[fFdD]\=\>"
syn match x10Number "\<\d\+\([eE][-+]\=\d\+\)\=[fFdD]\>"

" xml literals
syn match x10Xml "<[a-zA-Z][^>]*/>" contains=x10XmlQuote,x10XmlEscape
syn region x10Xml start="<[a-zA-Z][^>]*[^/]>" end="</[^>]\+>;"he=e-1 contains=x10XmlEscape,x10XmlQuote
syn region x10XmlEscape matchgroup=x10XmlEscapeSpecial start="{" matchgroup=x10XmlEscapeSpecial end="}" contained contains=x10Keyword,x10Type,x10String
syn match x10XmlQuote "&[^;]\+;" contained

"syn include @x10Xml syntax/xml.vim
"unlet b:current_syntax
"syn region x10Xml start="<[a-zA-Z][^>]*>" skip="<!--[^>]*-->" end="</[^>]>;" contains=@x10Xml,x10XmlEscape


" map X10 groups to standard groups
hi link x10Keyword Keyword
hi link x10Package Include
hi link x10Import Include
hi link x10Boolean Boolean
hi link x10Operator Normal
hi link x10Number Number
hi link x10String String
hi link x10StringEscape Special
hi link x10Comment Comment
hi link x10LineComment Comment
hi link x10DocComment Comment
hi link x10DocTags Special
hi link x10Todo Todo
hi link x10Type Type
hi link x10TypeSpecializer x10Type
hi link x10Xml String
hi link x10XmlEnd String
hi link x10XmlEscape Normal
hi link x10XmlEscapeSpecial Special
hi link x10XmlQuote Special
hi link x10Def Keyword
hi link x10Var Keyword
hi link x10Val Keyword
hi link x10Class Keyword
hi link x10Object Keyword
hi link x10Trait Keyword
hi link x10DefName Function
hi link x10DefSpecializer Function
hi link x10ClassName Special
hi link x10ClassSpecializer Special
hi link x10Constructor Special
hi link x10ConstructorSpecializer x10Constructor

let b:current_syntax = "x10"

" customize colors a little bit (should be a different file)
hi x10New gui=underline
hi x10MethodCall gui=italic
hi x10ValName gui=underline
hi x10VarName gui=underline

