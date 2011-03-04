setlocal
set PROLOG=plwin
set SOURCEPATH=
%PROLOG% -q -s %SOURCEPATH%c4sim.pl -g "exec" -- %*
endlocal
