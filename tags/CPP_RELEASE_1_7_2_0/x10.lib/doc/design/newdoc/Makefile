TARGETS: header.dvi header.ps header.pdf

.PHONY: header.dvi header.ps header.pdf clean

all: header.pdf

header.pdf: header.tex
	pdflatex header; bibtex header; pdflatex header; bibtex header; pdflatex header


clean:
	rm -f header.blg header.dvi header.log header.bbl header.aux \
	header.ps header.pdf header.lof header.lot header.toc header.out \
	`find . -name '*~'` _region_*
