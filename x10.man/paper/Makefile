include commondefs

SHELL = /bin/sh

#
# change this line to contain the name of the document without extension

DOC = res0000095-nystrom.pdf
TARGETS = $(DOC)

#
# TARGETS could be $(DOC).dvi instead, if there are no figures

TEXS = paper.tex \
	conclusion.tex examples.tex future.tex impl.tex intro.tex lang.tex longex.tex paper.tex semantics.tex abstract.tex related2.tex quad-semantics.tex \
	fx-semantics.tex \
       $(X10TEXS) $(STY)
STY = defs.sty utils.sty
#
# Add any included TeX files to TEXS

VPATH = $(TEXDIR)

FIGS = 

# Override the .eps.pdf rule to do nothing
# %.pdf : %.eps

# This makes lousy PostScript
# .pdf.eps:
	# pdf2ps $< $@

#
# List of postscript figures to be included

X10_FILES = 
X10TEXS = $(X10_FILES:.x10=.tex)

.SUFFIXES: .x10

default: $(TARGETS)

$(DOC): paper.pdf
	cp paper.pdf $(DOC)

BIBFILE = master.bib
#
# Ignore this if you're not using bibtex. Change the $(DOC) part if your bib
# file has a different name from your document

paper.dvi: $(TEXS) paper.stamp $(FIGS) paper.bbl $(BIBFILE)
paper.pdf: $(TEXS) paper.stamp $(FIGS:.eps=.pdf) paper.bbl $(BIBFILE)

# To use makeindex, add this to dependency list:
# $(DOC).ind
#
# To use glossaries, add this to dependency list:
# $(DOC).glo
paper.bbl: paper.stamp $(BIBFILE)
paper.glo paper.ind: paper.stamp
paper.ps: paper.dvi $(FIGS)
#
# These are standard dependencies. Shouldn't need to modify these.

include commonrules

#SEP=\;  # windows miktex -- annoying
SEP=: # unix including linux, cygwin

LATEX = TEXINPUTS=$(TEXDIR)${SEP} latex
PDFLATEX = TEXINPUTS=$(TEXDIR)${SEP} pdflatex

.x10.tex:
	perl process-x10 -l $< > $@
$(X10_FILES:.x10=.tex): process-x10

DIRT += $(JXTEXS)

