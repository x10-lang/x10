include commondefs

SHELL = /bin/sh

#
# change this line to contain the name of the document without extension

TARGET = modules.pdf

TEXS = paper.tex \
       $(STY)
STY = defs.sty utils.sty x10.sty
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

default: $(TARGET)

$(TARGET): paper.pdf
	-cp paper.pdf $(TARGET)

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

