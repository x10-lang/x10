.SUFFIXES: .eps .pdf
LATEX= latex
BIBTEX= bibtex
FIG2DEV=fig2dev

all: summary

TEX=summary.tex \
    squeeze.tex \
    references.bib 

all: pdf 

summary: $(TEX) $(EPS) summary.bbl
	@echo Compiling summary
	@$(LATEX) summary
	@$(MAKE) fixlabels

pdf: summary
	@dvipdf summary.dvi

fixlabels:
ifneq ($(shell grep -s -c 'Label(s) may have changed. Rerun to get cross-references right.' summary.log),0)
	@echo Compiling summary again to fix labels
	@$(LATEX) summary
else
	@echo " " Labels are correct
endif

summary.bbl: references.bib
	@echo Compiling main for bibliography
	@$(LATEX) summary
	@echo Generating bibliography
	@$(BIBTEX) summary
	@echo Recompiling main after generating bibliography
	@$(LATEX)  summary
	@$(MAKE) fixlabels

clean:
	@echo "Cleaning the main directory"
	@rm -f summary.pdf *.aux *.dvi *.log *.bbl *.blg figs/*.pdf *.toc *.out *.bak
