FILE = doc
$(FILE).dvi: 
	  latex $(FILE)

$(FILE).pdf: $(FILE).ps
	  ps2pdf $(FILE).ps $(FILE).pdf

$(FILE).ps: $(FILE).dvi
	dvips -Ppdf $(FILE).dvi -o $(FILE).ps

newcite: $(FILE).bib $(FILE).dvi 
	bibtex $(FILE)
	latex $(FILE)
	latex $(FILE)
	
force:
	latex $(FILE)
	bibtex $(FILE)
	latex $(FILE)
	latex $(FILE)
	
clean:
	rm -f $(FILE).aux $(FILE).bbl $(FILE).blg $(FILE).dvi $(FILE).log $(FILE).pdf $(FILE).ps
