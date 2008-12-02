% -*- LaTeX -*-
%%% ====================================================================
%%%  @LaTeX-style-file{
%%%     author          = "Glenn Paulley",
%%%     version         = "1.0",
%%%     date            = "2002 April 16",
%%%     time            = "17:18:41",
%%%     filename        = "ieee.cls",
%%%     address         = "iAnywhere Solutions
%%%                        415 Phillip Street
%%%                        Waterloo, Ontario, Canada
%%%                        N2L 3X2",
%%%     telephone       = "(519) 883-6244",
%%%     FAX             = "(519) 747-4971",
%%%     checksum        = "",
%%%     email           = "paulley@ianywhere.com",
%%%     codetable       = "ISO/ASCII",
%%%     keywords        = "",
%%%     supported       = "yes",
%%%     abstract        = "Contains the LaTeX style command definitions
%%%                        for typesetting IEEE conference proceedings.",
%%%     docstring       = "The checksum field above contains a CRC-16
%%%                        checksum as the first value, followed by the
%%%                        equivalent of the standard UNIX wc (word
%%%                        count) utility output of lines, words, and
%%%                        characters.  This is produced by Robert
%%%                        Solovay's checksum utility.",
%%%  }
%%% ====================================================================
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{ieee}[2002/04/16
           LaTeX2e class file `ieee' v1.0 for IEEE Conference Proceedings]
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%
%% \CharacterTable
%%  {Upper-case    \A\B\C\D\E\F\G\H\I\J\K\L\M\N\O\P\Q\R\S\T\U\V\W\X\Y\Z
%%   Lower-case    \a\b\c\d\e\f\g\h\i\j\k\l\m\n\o\p\q\r\s\t\u\v\w\x\y\z
%%   Digits        \0\1\2\3\4\5\6\7\8\9
%%   Exclamation   \!     Double quote  \"     Hash (number) \#
%%   Dollar        \$     Percent       \%     Ampersand     \&
%%   Acute accent  \'     Left paren    \(     Right paren   \)
%%   Asterisk      \*     Plus          \+     Comma         \,
%%   Minus         \-     Point         \.     Solidus       \/
%%   Colon         \:     Semicolon     \;     Less than     \<
%%   Equals        \=     Greater than  \>     Question mark \?
%%   Commercial at \@     Left bracket  \[     Backslash     \\
%%   Right bracket \]     Circumflex    \^     Underscore    \_
%%   Grave accent  \`     Left brace    \{     Vertical bar  \|
%%   Right brace   \}     Tilde         \~}
%%
%
% Process options and load class article:
%
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{article}}
%
% Make the generation of page numbers an option.
%
\newif\if@pagenumbers
\@pagenumbersfalse% default is no page numbers
\DeclareOption{pagenumbers}{\@pagenumberstrue}
\DeclareOption{nopagenumbers}{\@pagenumbersfalse}
%
\ProcessOptions
%
\LoadClass[letterpaper,twocolumn,10pt]{article}
%
\setlength\textwidth{6.875in} 
\setlength\textheight{8.875in}
% set both margins to 2.5 pc
\setlength{\oddsidemargin}{-0.1875in}% 1 - (8.5 - 6.875)/2
\setlength{\evensidemargin}{-0.1875in}
\setlength{\marginparwidth}{0pc}
\setlength{\marginparsep}{0pc}% 
\setlength{\topmargin}{0in} \setlength{\headheight}{0pt}
\setlength{\headsep}{0pt} 
\setlength{\footskip}{37pt}% 
\setlength{\columnsep}{0.3125in}
\setlength{\columnwidth}{3.28125in}% (6.875 - 0.3125)/2 = 3.28125in 
\setlength{\parindent}{1pc}
%
\input{ieee.clo}% assumes the use of 10 pt
%
% change default date format 
%
\def\today{\number\day\space%
\ifcase\month\or%
  January\or February\or March\or April\or May\or June\or%
  July\or August\or September\or October\or November\or December\fi%
  \space\number\year}
%%
%% Default footnote handling
%%
\setlength\footnotesep{1pt}
\setlength{\skip\footins}{16\p@ \@plus 4\p@ \@minus 2\p@}% less 7 pt for rule
%
% footnoterule: let \footins specify the distance between the text
% and the rule (\footins should be at least 7pts), and space a bit 
% before the first footnote so that \footnotesep can be smaller
%
\renewcommand\footnoterule{%
  \kern-7\p@
  \hrule width .4\columnwidth% \hrule is by default .4pt high
  \kern 6.6\p@}
%
% Redefine footnote numerals so that they are no longer raised in the footnote,
% and redefine the footnote shape so that the text is in an unindented block
% also; don't use \@makefnmark, which raises the footnote numeral.
%
\long\def\@makefntext#1{%
     \setlength{\parindent}{0pt}%
     \begin{list}{}{%
         \setlength{\labelwidth}{4mm}%
         \setlength{\labelsep}{1mm}%
         \setlength{\listparindent}{1pc}%
         \setlength{\leftmargin}{\labelwidth}%
         \advance\leftmargin\labelsep% indent the entire text
         \setlength{\topsep}{0pt}% let footnotesep space the footnotes
         \setlength{\parsep}{0pt}%
         \setlength{\itemindent}{0pt}% footnote numbers at left margin
         \setlength{\itemsep}{0pt}%
         \footnotesize}%
       \item[\@thefnmark\hfill]#1%
     \end{list}}
%
% Titles as per IEEE.
%
\def\affiliation#1{\gdef\@affiliation{#1}} \gdef\@affiliation{}
%
\def\email#1{\gdef\@email{#1}}
\gdef\@email{}
%
\def\@maketitle%
   {%
   \newpage%
   \null%
   \vskip .375in%
   \begin{center}%
      {\Large \bf \@title \par} 
      % additional two empty lines at the end of the title 
      \vspace*{24pt} 
      {
      \large 
      \lineskip .5em
      \begin{tabular}[t]{c}
         \@author 
      \end{tabular}
      \par
      } 
      % additional small space at the end of the author name 
      \vskip .5em 
      {
       \large 
      \begin{tabular}[t]{c}
         \@affiliation 
      \end{tabular}
      \par 
      \ifx \@empty \@email
      \else
         \begin{tabular}{r@{~}l}
            E-mail: & {\tt \@email}
         \end{tabular}
         \par
      \fi
      }
      % additional empty line at the end of the title block 
      \vspace*{12pt} 
   \end{center}
   } 
%
% Redefine maketitle to set \thanks as above footnotes
%
\renewcommand{\maketitle}{%\par
  \begingroup%
    \renewcommand{\thefootnote}{\fnsymbol{footnote}}%
    \if@twocolumn%
      \ifnum \col@number=\@ne%
        \@maketitle%
      \else%
        \twocolumn[\@maketitle]%
      \fi%
    \else%
      \newpage%
      \global\@topnum\z@% Prevents figures from going at top of page.
      \@maketitle%
    \fi%
    \if@pagenumbers\thispagestyle{plain}\else\thispagestyle{empty}\fi\@thanks%
  \endgroup%
  \setcounter{footnote}{0}%
  \let\thanks\relax%
  \let\maketitle\relax\let\@maketitle\relax%
  \gdef\@thanks{}\gdef\@author{}\gdef\@title{}}%
%
% Generate page numbers according to the pagenumbers option.
%
\if@pagenumbers\pagestyle{plain}\else\pagestyle{empty}\fi
%
% set more compact interword spacing
%
\frenchspacing
%
% Section Headings - redefine to meet IEEE standards
%
% Add font size for intermediate headings
%
\newcommand\intermediate{\@setfontsize\intermediate\@xipt{13}}
%
% Alter default section/subsection styles
%
% Paragraphs after section and subsection headings must still be indented.
% To achieve this, ensure the beforeskip value is positive.
%
\renewcommand\section{\@startsection{section}{1}{\z@}%
                                   {2.75ex \@plus 1ex \@minus .2ex}%
                                   {2.3ex \@plus.2ex}%
                                   {\rmfamily\large\bfseries}}
\renewcommand\subsection{\@startsection{subsection}{2}{\z@}%
                                     {2.75ex\@plus1ex\@minus.2ex}%
                                     {2.3ex\@plus.2ex}%
                                     {\rmfamily\intermediate\bfseries}}%
%
% Section headings smaller than 'subsection' are to be merely inlined.
% Note: minus in afterskip means no linebreak after section title; it 
% determines the horizontal space following it.
%
\renewcommand\subsubsection{\@startsection{subsubsection}{3}{\z@}%
                                     {-1.25ex\@plus-1ex\@minus-.2ex}%
                                     {-0.5em}%
                                     {\rmfamily\normalsize\bfseries\selectfont}}%
\renewcommand\paragraph{\@startsection{paragraph}{4}{\z@}%
                                    {0.8ex\@plus.3ex\@minus.2ex}%
                                    {-0.5em}%
                                    {\rmfamily\normalsize\itshape\selectfont}}%
%
% Add trailing period to section numbers as per IEEE format
%
\renewcommand{\@seccntformat}[1]{\csname the#1\endcsname.\hspace{0.70em}}
%
% redo abstract in italics
%
\newenvironment{italictext}
               {\list{}{\listparindent 1pc%
                        \itemindent    1pc%
			\leftmargin    0pt
                        \rightmargin   \leftmargin
			\it
                        \parsep        \z@ \@plus\p@}%
                \item\relax}
               {\endlist}
\if@titlepage
  \renewenvironment{abstract}{%
      \titlepage
      \null\vfil
      \@beginparpenalty\@lowpenalty
      \begin{center}%
        \bfseries \abstractname
        \@endparpenalty\@M
      \end{center}}%
     {\par\vfil\null\endtitlepage}
\else
  \renewenvironment{abstract}{%
      \if@twocolumn
        \large
        \begin{center}%
          {\bfseries \abstractname\vspace*{6pt}}%
        \end{center}%
	\normalsize
        \italictext
      \else
        \large
        \begin{center}%
          {\bfseries \abstractname\vspace*{6pt}}%
        \end{center}%
	\normalsize
        \italictext
      \fi}
      {\enditalictext\vspace*{12pt}}
\fi
%
\renewenvironment{quotation}
               {\list{}{\listparindent 1pc%
                        \itemindent    \listparindent
                        \rightmargin   \leftmargin
                        \parsep        \z@ \@plus\p@}%
                \item\relax}
               {\endlist}
%%%
%
% Change Bibliography environment so that references are listed in 9-pt
%
%%%
\renewenvironment{thebibliography}[1]
     {\section*{\refname}%
      \@mkboth{\MakeUppercase\refname}{\MakeUppercase\refname}%
      \begin{small}%
      \list{\@biblabel{\@arabic\c@enumiv}}%
           {\settowidth\labelwidth{\@biblabel{#1}}%
	    \itemindent\z@%
            \leftmargin\labelwidth%
            \advance\leftmargin\labelsep%
            \@openbib@code%
            \usecounter{enumiv}%
            \let\p@enumiv\@empty%
            \renewcommand\theenumiv{\@arabic\c@enumiv}}%
      \sloppy%
      \clubpenalty4000%
      \@clubpenalty \clubpenalty%
      \widowpenalty4000%
      \sfcode`\.\@m}%
     {\def\@noitemerr%
       {\@latex@warning{Empty `thebibliography' environment}}%
      \endlist%
      \end{small}}%
%
% FLOAT package parameters
%
\renewcommand\topfraction{.8}
\setcounter{bottomnumber}{1}
\renewcommand\bottomfraction{.5}
\setcounter{totalnumber}{3}
\renewcommand\textfraction{.15}
\renewcommand\floatpagefraction{.8}
\setcounter{dbltopnumber}{2}
\renewcommand\dbltopfraction{.85}
\renewcommand\dblfloatpagefraction{.85}
%
\RequirePackage{float}
\RequirePackage{floatsty}
\floatstyle{IEEE}
\setlength{\floatsep}{7pt plus 3pt minus 1.5pt}
\setlength{\dblfloatsep}{10pt plus 3pt minus 1.5pt}
\setlength{\textfloatsep}{4pt plus 3pt minus 1.5pt}
\setlength{\dbltextfloatsep}{2pt plus 3pt minus 1.5pt}
\restylefloat{figure}
\restylefloat{table}
%%
