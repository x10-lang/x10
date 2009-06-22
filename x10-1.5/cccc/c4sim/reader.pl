%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Reader for the CJ mini-language, part of JmmSolve
%
% Author:     Tom Schrijvers
% E-mail:     Tom.Schrijvers@cs.kuleuven.ac.be
% Copyright:  2004, K.U.Leuven
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

:- module(reader,
	[
		accept_file/2
	]).

:- ['tokenizer'].

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
accept_file(File,Prog) :-
	read_file_to_codes(File,Codes,[tail([])]), 
	accept_program(Codes,Prog).


accept_program(String,Prog) :-
	tokenize(String,Tokens),
	program(Prog,Tokens,[]).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
program(prog(Is,Ts,Ds)) --> init_block(Is), threads(Ts), dependency_block(Ds).

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
dependency_block([]) --> [].
dependency_block(Ds) --> [dependency], braceopen, dependencies(Ds), braceclose.

dependencies([]) --> [].
dependencies([D|Ds]) -->
	dependency(D),
	dependencies(Ds).

dependency(dependson(L1,L2)) -->
	label(L1),
	['$arrow'],
	label(L2),
	semicolon. 

label(Label) -->
	label_(Is),
	{ concat_atom(Is,'.',Label)}.
label_([I|Is]) --> [I], {integer(I)}, morelabel(Is).
morelabel(Is) --> dot, label_(Is).
morelabel([]) --> [].
	

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
init_block([I|Is]) --> init_statement(I), semicolon, init_block(Is).
init_block([]) --> [].

init_statement(init(X,Y)) --> init, assignment(assign(X,Y)).

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
threads([T|Ts]) --> thread(T), morethreads(Ts).

morethreads(Ts) --> pipe, threads(Ts).
morethreads([]) --> [].

thread(thread(S)) --> [thread], braceopen, statements(S), braceclose.

statements([S|Ss]) --> labeled_statement(S), morestatements(Ss).

morestatements(Ss) --> statements(Ss).
morestatements([]) --> [].

labeled_statement(labeled(Label,S)) -->
	label(Label),
	colon,
	statement(S).

statement(S) --> assignment(S), semicolon.
statement(S) --> if_statement(S).

if_statement(if(Cond,Then,Else)) --> 
	if, 
		parensopen, 
			condition(Cond), 
		parensclose,
	 	braceopen, 
			statements(Then), 
		braceclose, 
	else_statement(Else).

else_statement(Else) --> 
	else, 
		braceopen, 
			statements(Else), 
		braceclose.
else_statement([]) --> [].

assignment(assign(Var,Value)) --> [Var], equal, assign_rhs(Value).

assign_rhs(Value) --> arith_exp1(Value).

arith_exp1(Value1 + Value2) --> arith_exp2(Value1), op_plus  , { ! }, arith_exp1(Value2).
arith_exp1(Value1 - Value2) --> arith_exp2(Value1), op_minus , { ! }, arith_exp1(Value2).
arith_exp1(Value)	    --> arith_exp2(Value).

arith_exp2(Value1 * Value2) --> arith_exp3(Value1), op_times , { ! }, arith_exp2(Value2).
arith_exp2(Value)	    --> arith_exp3(Value).

arith_exp3(Value) --> [Value].

condition(cond(Test,Left,Right)) --> [Left], test(Test), [Right].

test(eq) --> eqeq.
test(neq) --> neq.
test(leq) --> leq.
test(geq) --> geq.
test(lt) --> lt.
test(gt) --> gt.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% tokens
equal --> ['$equal'].
parensopen --> ['$parensopen'].
parensclose --> ['$parensclose'].
semicolon --> ['$semicolon'].
braceopen --> ['$braceopen'].
braceclose --> ['$braceclose'].
pipe --> ['$pipe'].
init --> [init].
if --> [if].
else --> [else].
bang --> ['$bang'].
lt --> ['$lt'].
gt --> ['$gt'].
geq --> ['$geq'].
leq --> ['$leq'].
eqeq --> ['$equalequal'].
neq --> ['$notequal'].
op_plus --> ['$plus'].
op_minus --> ['$minus'].
op_times --> ['$asterisk'].
dot --> ['$dot'].
colon --> ['$colon'].
