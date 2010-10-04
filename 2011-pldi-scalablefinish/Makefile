FIGURES =  
#fft_mem.eps \

%.eps : %.gnuplot
	gnuplot $<

%.dvi : %.tex 
	latex $< < /dev/null
	if grep -s Citation $*.log; then \
	    bibtex $*; \
	    latex $< < /dev/null; \
	    latex $< < /dev/null; \
        fi
	while egrep -s 'Rerun' $*.log; do \
		latex $< < /dev/null; \
	done

%.ps : %.dvi 
	dvips -K -Ppdf -R0 -t letter -o $@ $<

%.ps : %.dot
	dot -Tps -o $@ $<

%.eps : %.svg
	inkscape $< --export-text-to-path --export-eps=$@

%.pdf : %.dvi acm.joboptions
	cp acm.joboptions $*.ps
	dvips -K -Ppdf -R0 -t letter $< >> $*.ps
	ps2pdf $*.ps

all: 2011-pldi-scalablefinish.ps 2011-pldi-scalablefinish.pdf

2011-pldi-scalablefinish.dvi : 2011-pldi-scalablefinish.tex $(FIGURES)

.PHONY : clean
clean :
	rm -f *~ *.pdf *.out *.log *.aux *.dvi *.ps *.eps \
                *.gz *.mpx *.blg *.bbl mpxerr.tex
