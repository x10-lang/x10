.SUFFIXES: .eps .pdf
LATEX= latex
BIBTEX= bibtex
FIG2DEV=fig2dev

all: bc

FIGSEPS=$(shell ls -1 -q figs/*.eps 2> /dev/null)

#RESULTSEPS=$(shell ls -1 results/*.eps)

EPS=$(FIGSEPS) #$(RESULTSEPS)

TEX=bc.tex \
    abstract.tex \
    introduction.tex \
    related.tex \
    references.bib 

all: pdf 

bc: $(TEX) $(EPS) bc.bbl
	@echo Compiling bc
	@$(LATEX) bc
	@$(MAKE) fixlabels

pdf: bc
	@dvipdf bc.dvi

fixlabels:
ifneq ($(shell grep -s -c 'Label(s) may have changed. Rerun to get cross-references right.' bc.log),0)
	@echo Compiling bc again to fix labels
	@$(LATEX) bc
else
	@echo " " Labels are correct
endif

bc.bbl: references.bib
	@echo Compiling required EPS files
	@make -C figs
	@echo Compiling main for bibliography
	@$(LATEX) bc
	@echo Generating bibliography
	@$(BIBTEX) bc
	@echo Recompiling main after generating bibliography
	@$(LATEX)  bc
	@$(MAKE) fixlabels

.eps.pdf:
	@echo " " Converting $< to $(@F)
	@epstopdf --outfile=Figs/$(@F) $<


clean:
	@make -C figs clean
	@echo "Cleaning the main directory"
	@rm -f bc.pdf *.aux *.dvi *.log *.bbl *.blg figs/*.pdf *.toc *.out *.bak
