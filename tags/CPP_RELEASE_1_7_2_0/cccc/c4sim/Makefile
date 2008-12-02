SWIPL=pl

all:	c4sim	

c4sim:	c4sim.pl c4_event_compiler.pl reader.pl tokenizer.pl
	${SWIPL} -G0 -L0 -T0 --goal=exec --stand_alone=true -o c4sim -c c4sim.pl
