%.dvi : %.tex figures/draw.cmd figures/laptop.dat figures/triloka11.dat
	gnuplot figures/draw.cmd 
	gnuplot figures/draw-azul.cmd 
	latex $< < /dev/null
	if grep -s Citation $*.log; then \
	    bibtex $*; \
	    latex $< < /dev/null; \
	    latex $< < /dev/null; \
        fi
	while egrep -s 'Rerun' $*.log; do \
		latex $< < /dev/null; \
	done

%.spell : %.tex
	cat $< | aspell --mode=tex list | sort -u

%.ps : %.dvi 
	dvips -K -Ppdf -R0 -t letter -o $@ $<

%.ps : %.dot
	dot -Tps -o $@ $<

%.eps : %.svg
	inkscape $< --export-text-to-path --export-eps=$@

%.pdf : %.ps
	ps2pdf \
	-dCompatibilityLevel=1.3 \
	-dAutoRotatePages=/None \
	-dCompressPages=true \
	-dASCII85EncodePages=false \
	-dUseFlateCompression=true \
	-dEmbedAlllFonts=true \
	-dMaxSubsetPct=100 \
	-dConvertCMYKImagesToRGB=false \
\
	-dAutoFilterColorImages=true \
	-dEncodeColorImages=true \
	-dDownsampleColorImages=true \
	-dColorImageDepth=-1 \
	-dColorImageResolution=300 \
	-dColorImageDownloadThreshold=1 \
	-dColorImageDownsampleType=/Bicubic \
\
	-dAutoFilterGrayImages=true \
	-dGrayImageFilter=/DCTEncode \
	-dEncodeGrayImages=true \
	-dDownsampleGrayImages=true \
	-dGrayImageDownsampleThreshold=1 \
	-dGrayImageDownsampleType=/Bicubic \
	-dGrayImageDepth=-1 \
	-dGrayImageResolution=200 \
\
	-dMonoImageFilter=/CCITTFaxEncode \
	-dEncodeMonoImages=true \
	-dDownsampleMonoImages=true \
	-dMonoImageDownsampleThreshold=1 \
	-dMonoImageDownsampleType=/Bicubic \
	-dMonoImageDepth=-1 \
	-dMonoImageResolution=600 \
	-sPAPERSIZE=letter $<
#	ps2pdf $<

SCALE = 3
%.xdvi : %.dvi
	xdvi -expert -s ${SCALE} -paper us $< &

all:  $(addsuffix .ps,$(basename $(wildcard *.tex)))

.PHONY : clean
clean :
	rm -f *.out *.log *.aux *.dvi *.ps *.pdf *.mpx *.blg *.bbl mpxerr.tex
	rm -rf figures/*.tricks figures/*.eps figures/*.aux figures/*.ps 
