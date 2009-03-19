SRCS = master.bib Paper.tex abstract.tex

default: Paper.ps


Paper.ps: $(SRCS)
	latex Paper
	bibtex Paper
	latex Paper
	latex Paper

clean:
	rm -f *~
	rm -f Paper.log Paper.aux Paper.bbl Paper.blg Paper.dvi Paper.ps


