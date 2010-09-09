:- module(c4_event_compiler,
		[
			compile_file/2
		]).

:- [reader].

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%yy%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% event(Label,Thread,Type,Variable,Value)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%yy%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
compile_file(File,Goal) :-
	accept_file(File,Prog),
	compile_prog(Prog,Goal).	

compile_prog(prog(Inits,Threads,Dependencies),Goal) :-
	Goal = program(InitEvents,ThreadEvents,Dependencies),
	compile_inits(Inits,GlobalVariables,InitEvents),
	compile_threads(Threads,GlobalVariables,ThreadEvents).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
compile_inits([],[],[]).
compile_inits([I|Is],[GlobalVariable|GlobalVariables],[Event|Tail]) :-
	compile_init(I,GlobalVariable,Event),
	compile_inits(Is,GlobalVariables,Tail).

compile_init(init(GlobalVariable,Value),GlobalVariable,Event) :-	
	gensym(init,Label),	
	Event = event(Label,init,init,GlobalVariable,Value).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
compile_threads([],_,[]).
compile_threads([Thread|Threads],GlobalVariables,[thread(ThreadEvents)|Rest]) :-
	compile_thread(Thread,GlobalVariables,ThreadEvents),
	compile_threads(Threads,GlobalVariables,Rest).

compile_thread(thread(Statements),GlobalVariables,Events) :-
	gensym(thread,ThreadName),
	compile_statements(Statements,ThreadName,GlobalVariables,Events).

compile_statements([],_,_,[]).
compile_statements([Statement|Statements],ThreadName,GlobalVariables,[Event|Events]) :-
	compile_statement(Statement,ThreadName,GlobalVariables,Event),
	compile_statements(Statements,ThreadName,GlobalVariables,Events).
	
compile_statement(Statement,ThreadName,GlobalVariables,Event) :-
	( Statement = labeled(Label,ActualStatement) ->
		true
	;
		gensym(label,Label),
		ActualStatement = Statement
	),
	compile_statement(ActualStatement,Label,ThreadName,GlobalVariables,Event).


compile_statement(assign(Var,Value),Label,ThreadName,GlobalVariables,Event) :-
	( memberchk(Var,GlobalVariables) ->
		Event = event(Label,ThreadName,write,Var,Value)
	; memberchk(Value,GlobalVariables) ->
		Event = event(Label,ThreadName,read,Var,Value)
	).
