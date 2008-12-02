%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% c4sim, a CCCC simulator
%
% Author:     Tom Schrijvers
% E-mail:     Tom.Schrijvers@cs.kuleuven.ac.be
% Copyright:  2007, K.U.Leuven
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
:- module(c4sim,
	[
		exec/0
	]).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
:- use_module(library(chr)).

:- use_module(c4_event_compiler).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
exec :-
	( current_prolog_flag(argv,[pl|Rest]) ->
		% from Prolog source
		once(append(_,[--|Args],Rest))
	; current_prolog_flag(argv,[_|Args]) ->
		% stand-alone executable
		true
%	;
%		prompt(_,''),
%		File = user_input
	),
	( select('-dot',Args,[File]) ->
		b_setval(dot_file_output,yes(File))	
	;
		b_setval(dot_file_output,no),	
		Args = [File]
	),
	run_c4_file(File),
	halt.
exec :-
	writeln('Parameter parsing failed: make sure to mention the desired filename.'),
	halt.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

run_c4_file(File) :-
	nb_setval(solutions,0),
	( compile_file(File,Prog) ->
		( Prog = program(InitEvents,Threads,Dependencies), 
		  events(InitEvents),
		  threads(Threads),
		  dependencies(Dependencies),
		  init *->
			read_values,
			nb_getval(solutions,N),
			M is N + 1,
			nb_setval(solutions,M),
			writeln('--------------------------------------------------------------------------------'),
			dot_graph,
			fail
		;
			writeln('Inconsistency: no solutions!')
		)
	;
		writeln('Error compiling file')
	).
run_c4_file(_) :-
	writeln('--------------------------------------------------------------------------------'),
	nb_getval(solutions,N),
	format('~w solutions found.\n',[N]).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
threads([]).
threads([Thread|Threads]) :-
	thread(Thread),
	threads(Threads).

thread(thread(Events)) :-
	maplist(event_label,Events,Labels),
	serialize_list(Labels),
	events(Events).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
:- chr_constraint event/5.	% event(Label,Thread,Type,Target,Source)

events([]).
events([E|Es]) :-
	call(E),
	events(Es).

event_label(event(Label,_,_,_,_),Label).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
:- chr_constraint (<<)/2.	% EventLabel << EventLabel

% logical set semantics
Label1 << Label2 \ Label1 << Label2 <=> true.

% [Causal Propagation]

Label1 << Label2, event(Label1,Thread1,_,_,_),
Label2 << Label3, event(Label2,Thread1,_,_,_)
	==> 
Label1 << Label3.

Label1 << Label2, event(Label2,Thread2,_,_,_),
Label2 << Label3, event(Label3,Thread2,_,_,_)
	==> 
Label1 << Label3.

serialize_list([]).
serialize_list([Label|Labels]) :-
	serialize_list(Labels,Label).

serialize_list([],_).
serialize_list([Label|Labels],Previous) :-
	Previous << Label,
	serialize_list(Labels,Label).

dependencies([]).
dependencies([dependson(Label1,Label2)|Rest]) :-
	Label1 << Label2,
	dependencies(Rest).
	
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

:- chr_constraint
	init/0,
	done/0,
	init/2,
	writes/2,
	reads/2.

init, event(Label,_,init,Var,_) ==> init(Var,Label), writes(Var,[]), reads(Var,[]).
init <=> done.

init(Var,_), event(Label,_,write,Var,_) ==> writes(Var,[Label]).
init(Var,_), event(Label,_,read,_,Var)  ==> reads(Var,[Label]).

writes(Var,L1), writes(Var,L2) <=> append(L1,L2,L3), writes(Var,L3).
reads(Var,L1), reads(Var,L2) <=> append(L1,L2,L3), reads(Var,L3).

done \ init(Var,InitEvent), writes(Var,WriteEvents), reads(Var,ReadEvents) <=> 
	serialize_writes_reads(InitEvent,WriteEvents,ReadEvents).
done <=> true.

% [Write Serialization]
serialize_writes_reads(InitEvent,WriteEvents,ReadEvents) :-
	permutation(WriteEvents,Serialization_),
	Serialization = [InitEvent|Serialization_],
	serialize_list(Serialization),
	maplist(insert_read_event(Serialization),ReadEvents).

% [Freshness]
insert_read_event([Write|Writes],Read) :-
	insert_read_event(Writes,Write,Read).

insert_read_event([],Write,Read) :-
	Write << Read.
insert_read_event([Write2|_],Write1,Read) :-
	Write1 << Read,
	Read << Write2.
insert_read_event([Write|Writes],_,Read) :-
	insert_read_event(Writes,Write,Read).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% [Local Consistency]

:- chr_constraint
	local_label/2,		% local_label(Thread,Label)
	local_po/3.		% local_po(Thread,Label1,Label2)

% local labels

local_label(Thread,Label) \ local_label(Thread,Label) <=> true.

event(Label,Thread,_,_,_) 							==> local_label(Thread,Label).	% events of thread
Label1 << Label2, event(Label1,Thread,_,_,_), event(Label2,_,write,_,_) 	==> local_label(Thread,Label2). % events with incoming edge
Label1 << Label2, event(Label2,Thread,_,_,_), event(Label1,_,write,_,_) 	==> local_label(Thread,Label1). % events with outgoing edge

% lifted edges		

Label1 << Label2, local_label(Thread,Label1), local_label(Thread,Label2)	==> local_po(Thread,Label1,Label2).

local_po(Thread,Label1,Label2) \ local_po(Thread,Label1,Label2) <=> true.

% irreflexivity

local_po(_,Label,Label) <=> fail.

% transitive closure

local_po(Thread,Label1,Label2), local_po(Thread,Label2,Label3) ==> 
	local_po(Thread,Label1,Label3).	

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% values

:- chr_constraint 
	read_values/0,
	read_value/2,
	last_write/3,
	last_write_candidate/1.

read_values, event(Label,_,read,_,GlobalVar) ==>
	last_write(Label,GlobalVar,Value),
	read_value(Label,Value).
read_values <=> true.

last_write(Label,GlobalVar,_), WLabel << Label, event(WLabel,_,init,GlobalVar,_) ==>
	last_write_candidate(WLabel).
last_write(Label,GlobalVar,_), WLabel << Label, event(WLabel,_,write,GlobalVar,_) ==>
	last_write_candidate(WLabel).

last_write_candidate(WLabel1), WLabel2 << WLabel1 \ last_write_candidate(WLabel2) <=> true.

event(WLabel,_,_,_,WValue) \ last_write(_,_,RValue), last_write_candidate(WLabel) # passive <=>
	RValue = WValue.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
:- chr_constraint dot_graph/0,
		  dot_arrow/2,
		  dot_subgraph/1.

dot_graph <=> b_getval(dot_file_output,no) | chr_show_store(c4sim).

dot_graph, L1 << L2 ==> dot_arrow(L1,L2).

% hide indirect arrows 
% L1 << L3, L3 << L2 \ dot_arrow(L1,L2) <=> true.

dot_graph ==> dot_graph_file_name(File), format('writing ~w\n',[File]), tell(File).
dot_graph ==> format('digraph G {\n',[]).
dot_graph ==> format('\tranksep=1;\n',[]).
dot_graph ==> format('\tnodesep=1;\n',[]).
dot_graph, event(_,Thread,_,_,_) ==> dot_subgraph(Thread).
dot_graph, event(Label,_,init,Target,Source) ==> format('\t~w [label="~w : init ~w = ~w ; "];\n',[Label,Label,Target,Source]).
dot_graph, event(Label,_,write,Target,Source) ==> format('\t~w [label="~w : ~w = ~w ; "];\n',[Label,Label,Target,Source]).
dot_graph, event(Label,_,read,Target,Source), read_value(Label,Value) ==> format('\t~w [label="~w : ~w = ~w ; (~w)"];\n',[Label,Label,Target,Source,Value]).
dot_graph, dot_arrow(Label1,Label2) # passive	  ==> format('\t~w -> ~w;\n',[Label1,Label2]).
dot_graph ==> format('}\n',[]).
dot_graph <=> told.

dot_subgraph(Thread) \ dot_subgraph(Thread) <=> true.
dot_subgraph(Thread) ==> format('\tsubgraph cluster~w {\n',[Thread]).
dot_subgraph(Thread) ==> format('\t\tlabel = "~w";\n',[Thread]).
dot_subgraph(Thread), event(Label,Thread,_,_,_) ==> format('\t\t~w;\n',[Label]).
dot_subgraph(_) ==> format('\t}\n',[]).

dot_graph_file_name(File) :-
	b_getval(dot_file_output,yes(Source)),
	file_name_extension(Base,_,Source),
	nb_getval(solutions,Index),
	concat_atom([Base,Index,'.dot'],File).
