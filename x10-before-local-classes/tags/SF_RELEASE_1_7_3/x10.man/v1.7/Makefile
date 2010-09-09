# This is a makefile template to be copied & modified. Copy it to
# "Makefile" in your directory and do "chmod u+w Makefile".

DOC = x10
#
# change this line to contain the name of the document without extension

TARGETS = spec.pdf
#
# TARGETS could be $(DOC).dvi instead, if there are no figures

TEXS = $(DOC).tex *.tex *.sty
#
# Add any included TeX files to TEXS

FIGS =
#
# List of postscript figures to be included

#
# Remove the include above and uncomment the following line if you have
# commondefs installed in the current directory.

include commondefs

# Otherwise, set the environment variable COMMONDEFS to
# /usr/local/public/include/make/commondefs unless you have some better idea.

default: $(TARGETS)

spec.pdf: $(DOC).pdf
	cp $(DOC).pdf spec.pdf

BIBFILE = master.bib
#
# Ignore this if you're not using bibtex. Change the $(DOC) part if your bib
# file has a different name from your document

$(DOC).dvi: $(TEXS) $(FIGS) $(DOC).stamp $(DOC).bbl $(BIBFILE) $(DOC).ind
$(DOC).pdf: $(TEXS) $(FIGS) $(DOC).stamp $(DOC).bbl $(BIBFILE) $(DOC).ind

# To use bibtex, add these to dependency list:
# $(DOC).bbl $(BIBFILE)
# 
# To use makeindex, add this to dependency list:
# $(DOC).ind
#
# To use glossaries, add this to dependency list:
# $(DOC).glo

$(DOC).bbl: $(BIBFILE) $(DOC).stamp
$(DOC).glo $(DOC).ind: $(DOC).stamp
$(DOC).ps: $(DOC).dvi $(FIGS)
#
# These are standard dependencies. Shouldn't need to modify these.

VPATH = .

include commonrules
