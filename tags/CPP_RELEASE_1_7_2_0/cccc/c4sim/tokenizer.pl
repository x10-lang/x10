%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Tokenizer for the CJ mini-language, part of JmmSolve
%
% Author:     Tom Schrijvers
% E-mail:     Tom.Schrijvers@cs.kuleuven.ac.be
% Copyright:  2004, K.U.Leuven
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

:- module(tokenizer,[tokenize/2]).

tokenize(String,Atoms) :-
	tokenize(String,Current-Current,v(Atoms-Atoms,[])).

tokenize([],Current,Acc) :-
	close_current(Current,_,Acc,NAcc),
	seal(NAcc).
tokenize([First|Rest],Current,Acc) :-
	( space(First) ->
		close_current(Current,NCurrent,Acc,NAcc)
	; delimiter(First, Delimiter) ->
		close_current(Current,NCurrent,Acc,Acc1),
		push_delimiter(Acc1,Delimiter,NAcc)
	;
		push(Current,First,NCurrent),
		NAcc = Acc
	),
	tokenize(Rest,NCurrent,NAcc).

close_current(Q,NQ,Acc,NAcc) :-
	Q = H - T,
	( H == T ->
		NQ = Q,
		NAcc = Acc
	;
		NQ = X-X,
		T = [],
		H = [First|_],
		( is_digit(First) ->
			number_codes(Token,H)
		;
			atom_codes(Token,H)
		),	
		push_identifier(Acc,Token,NAcc)
	).

push(L-[E|T],E,L-T).

push_identifier(v(H-T,Del),Ident,v(H-NT,[])) :-
	( Del == [] ->
		T = [Ident|NT]
	;
		T = [Del,Ident|NT]
	).

push_delimiter(v(H-T,Del1),Del2,v(H-NT,NDel)) :-
	( Del1 == [] ->
		T = NT,
		NDel = Del2
	;
		( combine_delimiters(Del1,Del2,Token) ->
			NDel = [],
			T = [Token|NT]
		;
			NDel = Del2,
			T = [Del1|NT]
		)
	).
			


seal(v(_-T,Del)) :-
	( Del == [] ->
		T = []
	;
		T = [Del]
	).

space(Space)   :- char_code(' ',Space).
space(Tab)     :- char_code('\t',Tab).
space(Newline) :- char_code('\n',Newline).
space(Return)  :- char_code('\r',Return).

delimiter(Del,'$parensopen')  :- char_code('(',Del).
delimiter(Del,'$parensclose') :- char_code(')',Del).
delimiter(Del,'$braceopen')   :- char_code('{',Del).
delimiter(Del,'$braceclose')  :- char_code('}',Del).
delimiter(Del,'$semicolon')   :- char_code(';',Del).
delimiter(Del,'$equal')       :- char_code('=',Del).
delimiter(Del,'$gt')          :- char_code('>',Del).
delimiter(Del,'$lt')          :- char_code('<',Del).
delimiter(Del,'$bang')        :- char_code('!',Del).
delimiter(Del,'$pipe')        :- char_code('|',Del).
delimiter(Del,'$ampersand')   :- char_code('&',Del).
delimiter(Del,'$plus')        :- char_code('+',Del).
delimiter(Del,'$minus')       :- char_code('-',Del).
delimiter(Del,'$asterisk')    :- char_code('*',Del).
delimiter(Del,'$dot')         :- char_code('.',Del).
delimiter(Del,'$colon')       :- char_code(':',Del).
delimiter(Del,'$squareopen')  :- char_code('[',Del).
delimiter(Del,'$squareclose') :- char_code(']',Del).
	
combine_delimiters('$minus','$gt','$arrow').	
combine_delimiters('$equal','$equal','$equalequal').
combine_delimiters('$bang','$equal','$notequal').
combine_delimiters('$ampersand','$ampersand','$and').
combine_delimiters('$pipe','$pipe','$or').
combine_delimiters('$gt','$equal','$geq').
combine_delimiters('$equal','$lt','$leq').
