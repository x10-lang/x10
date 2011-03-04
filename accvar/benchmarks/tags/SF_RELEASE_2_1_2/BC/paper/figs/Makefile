FIGS=

PS=$(FIGS:.fig=.ps)
EPS=$(FIGS:.fig=.eps)

all: $(EPS)

.SUFFIXES: .fig .eps

.fig.eps:
	@fig2dev -Leps $< $@
	@ps2eps -f -q $@
	@mv $@.eps $@

clean:
	@rm -rf ${PS} ${EPS} *.*.bak
