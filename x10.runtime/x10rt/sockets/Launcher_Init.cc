/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *
 *  This file was written by Ben Herta for IBM: bherta@us.ibm.com
 */

#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>
#include <stdio.h>
#include <assert.h>
#include <stdarg.h>
#include <arpa/inet.h>
#include <errno.h>

#include "TCP.h"
#include "Launcher.h"

/* *********************************************************************** */
/* *********************************************************************** */

Launcher * Launcher::_singleton = NULL;
int Launcher::_parentLauncherControlLink = -1;

/* *********************************************************************** */
/*          Initialize the singleton. Fails if called twice.               */
/* *********************************************************************** */

void Launcher::Setup(int argc, char ** argv)
{
	assert (_singleton == NULL);

	// check to see if we need to launch stuff, or if we need to execute the runtime.
	if ((!getenv(X10_LAUNCHER_RUNLAUNCHER) && getenv(X10_LAUNCHER_PLACE))) return;
	// we just skip the launcher and run the program if the user hasn't set X10LAUNCHER_NPROCS
    if (!getenv(X10_NPLACES) || (strcmp(getenv(X10_NPLACES), "1")==0 && !getenv(X10_HOSTFILE) && !getenv(X10_HOSTLIST))) {
        if (checkBoolEnvVar(getenv(X10_SCRATCH_CWD))) {
            Launcher::enterRandomScratchDir(0);
        }
		return;
    }



	_singleton = (Launcher *) malloc(sizeof(Launcher));
	if (!_singleton) DIE("memory allocation failure in Initializer");
	new (_singleton) Launcher();
	_singleton->initialize(argc, argv);
	_singleton->startChildren();
}


/* *********************************************************************** */
/*           private constructor. Initialize everything to zeroes          */
/* *********************************************************************** */

Launcher::Launcher()
{
	_argc = 0;
	_argv = NULL;
	memset(_realpath, 0, sizeof(_realpath));
	memset(_ssh_command, 0, sizeof(_ssh_command));
	strcpy(_ssh_command, "/usr/bin/ssh");
	memset(_hostfname, 0, sizeof(_hostfname));
	_nplaces = 1;
	_hostlist = NULL;
	_runtimePort[0] = '\0';
	_myproc = 0xFFFFFFFF;
	_dieAt = 0;
	_firstchildproc = 0;
	_numchildren = 0;
	_pidlst = NULL;
	_listenSocket = -1;
	_childControlLinks = NULL;
	_exitcode = 0xFEEDC0DE;
}

/* *********************************************************************** */
/*              initialization.                                            */
/* - read argc, argv and environment variables                             */
/* - read and populate the host file                                       */
/* *********************************************************************** */

void Launcher::initialize(int argc, char ** argv)
{
	/* ------------------------------------------------ */
	/*  copy argc and argv, determine myproc and nprocs */
	/* ------------------------------------------------ */

	_argc = argc;

	// make a copy, because some platforms may delete argv after we return (e.g. Windows 7)
	_argv = (char**)malloc((argc+1)*sizeof(char*));
	_argv[argc] = NULL;
	for (int i=0; i<argc; i++) {
		_argv[i] = (char*)malloc((strlen(argv[i])+1)*sizeof(char));
		strcpy(_argv[i], argv[i]);
	}

	if (NULL==realpath(argv[0], _realpath)) {
        perror("Resolving absolute path of executable");
    }
	if (!getenv(X10_NPLACES))
	{
		_nplaces = 1;
		setenv(X10_NPLACES, "1", 0);
	}
	else
		_nplaces = atoi(getenv(X10_NPLACES));
	if (!getenv(X10_LAUNCHER_PLACE))
		_myproc = 0xFFFFFFFF;
	else
	{
		_myproc = atoi(getenv(X10_LAUNCHER_PLACE));
		char* host = getenv(X10_LAUNCHER_HOST);
		if (host) strcpy(_runtimePort, host);
		else strcpy(_runtimePort, "localhost");
	}

	/* -------------------------------------------- */
	/*  decide who my children are                  */
	/* current algorithm implements a binomial tree */
	/* -------------------------------------------- */
	if (_myproc != 0xFFFFFFFF)
	{
		_firstchildproc = (_myproc*2) + 1;

		if (_firstchildproc >= _nplaces)
			_numchildren = 0;
		else if (_firstchildproc+2 <= _nplaces)
			_numchildren = 2;
		else
			_numchildren = 1;
		#ifdef DEBUG
			fprintf(stderr, "Launcher %d has %i child%s\n", _myproc, _numchildren, _numchildren==1?"":"ren");
		#endif
	}
	else
	{
		_firstchildproc = 0;
		_numchildren = 0;
	}

	/* --------------------------------- */
	/* copy SSH command from environment */
	/* --------------------------------- */
	const char * ssh_command = (const char *) getenv(X10_LAUNCHER_SSH);
	if (ssh_command && strlen(ssh_command) > 0)
	{
		if (strlen(ssh_command) > sizeof(_ssh_command) - 10)
			DIE("Launcher %d: SSH command is too long", _myproc);
		strncpy(_ssh_command, ssh_command, sizeof(_ssh_command) - 1);
	}

	/* ------------------------------------------------------------------ */
	/* read host file name from environment; read and interpret host file */
	/* ------------------------------------------------------------------ */
	const char * hostfname = (const char *) getenv(X10_HOSTFILE);
	const char * hostlist = (const char *) getenv(X10_HOSTLIST);
	if (hostfname && strlen(hostfname) > 0)
	{
		if (strlen(hostfname) > sizeof(_hostfname) - 10)
			DIE("Launcher %d: host file name is too long", _myproc);
		realpath(hostfname, _hostfname);
		readHostFile();
	}
	else if (hostlist && strlen(hostlist) > 0)
	{
		int childLaunchers;
		if (_myproc == 0xFFFFFFFF)
			childLaunchers = 1;
		else
			childLaunchers = _numchildren;

		if (childLaunchers > 0)
		{
			_hostlist = (char **) malloc(sizeof(char *) * childLaunchers);
			if (!_hostlist)
				DIE("Launcher %d: hostname memory allocation failure", _myproc);

			uint32_t currentNumber = 0;
			const char* hostNameStart = hostlist;
			bool skipped = false;
			while (currentNumber < _firstchildproc+childLaunchers)
			{
				bool endOfLine = false;
				const char* hostNameEnd = strchr(hostNameStart, ',');
				if (hostNameEnd == NULL)
				{
					if (!skipped && _firstchildproc > currentNumber)
					{
						skipped = true;
						if (currentNumber == 0)
							currentNumber = _firstchildproc;
						else
							currentNumber = ((_firstchildproc / currentNumber) * currentNumber)-1;
						hostNameStart = hostlist;
						continue;
					}
					else
					{
						hostNameEnd = hostNameStart+strlen(hostNameStart);
						endOfLine = true;
					}
				}
				else if (currentNumber < _firstchildproc)
				{
					currentNumber++;
					hostNameStart = hostNameEnd+1;
					continue;
				}

				int hlen = hostNameEnd-hostNameStart;
				char * host = (char *) malloc(hlen+1);
				if (!host)
					DIE("Launcher %d: memory allocation failure", _myproc);
				strncpy(host, hostNameStart, hlen);
				host[hlen] = '\0';
				_hostlist[currentNumber-_firstchildproc] = host;

				#ifdef DEBUG
					fprintf(stderr, "Launcher %d: launcher for place %i is on %s\n", _myproc, currentNumber, host);
				#endif
				if (endOfLine)
					hostNameStart = hostlist;
				else
					hostNameStart = hostNameEnd+1;
				currentNumber++;
			}
		}
	}
//	else if (_myproc == 0xFFFFFFFF)
//		fprintf(stderr, "Warning: Neither %s nor %s has been set. Proceeding as if you had specified \"%s=localhost\".\n", X10_HOSTFILE, X10_HOSTLIST, X10_HOSTLIST);

	connectToParentLauncher();

	/* -------------------------------------------- */
	/*  set up notification from dying processes    */
	/* -------------------------------------------- */
	signal(SIGCHLD, Launcher::cb_sighandler_cld);
	signal(SIGTERM, Launcher::cb_sighandler_term);
}

/* *********************************************************************** */
/*                read and interpret the host file                         */
/* *********************************************************************** */

void Launcher::readHostFile()
{
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: Processing hostfile \"%s\"\n", _myproc, _hostfname);
	#endif
	FILE * fd = fopen(_hostfname, "r");
	if (!fd)
		DIE("Launcher %d: cannot open hostfile '%s': exiting", _myproc, _hostfname);

	int childLaunchers;
	if (_myproc == 0xFFFFFFFF)
		childLaunchers = 1;
	else
		childLaunchers = _numchildren;

	if (childLaunchers > 0)
	{
		_hostlist = (char **) malloc(sizeof(char *) * childLaunchers);
		if (!_hostlist)
			DIE("Launcher %d: hostname memory allocation failure", _myproc);

		uint32_t lineNumber = 0;
		bool skipped = false;
		char buffer[5120];
		while (lineNumber < _firstchildproc+childLaunchers)
		{
			char* s = fgets(buffer, sizeof(buffer), fd);
			if (s == NULL)
			{
				if (lineNumber == 0)
					DIE("file \"%s\" can not be empty", _hostfname);
				// hit the end of the file, so there are more places than lines
				// We wrap around, reusing hostnames when this happens
				if (!skipped && _firstchildproc > lineNumber)
				{ // don't read the same lines again and again.  Skip ahead.
					skipped = true;
					lineNumber = (_firstchildproc / lineNumber) * lineNumber;
				}
				rewind(fd);
				continue;
			}

			// skip lines that aren't our children
			if (lineNumber < _firstchildproc)
			{
				lineNumber++;
				continue;
			}

			char * p = strtok(buffer, " \t\n\r");
			int plen = p ? strlen(p) : 0;
			if (plen <= 0)
				break;

			char * host = (char *) malloc(plen + 10);
			if (!host)
				DIE("Launcher %d: memory allocation failure", _myproc);
			strcpy(host, p);
			_hostlist[lineNumber-_firstchildproc] = host;

			#ifdef DEBUG
				fprintf(stderr, "Launcher %d: launcher for place %i is on %s\n", _myproc, lineNumber, host);
			#endif
			lineNumber++;
		}
	}
	fclose(fd);
}


/* *********************************************************************** */
/*                    internal error in process manager                    */
/* *********************************************************************** */

void Launcher::DIE(const char * msg, ...)
{
	char buffer[1200];
	va_list ap;
	va_start(ap, msg);
	vsnprintf(buffer, sizeof(buffer), msg, ap);
	va_end(ap);
	fprintf(stderr, "%s\n", buffer);
	if (errno != 0)
		fprintf(stderr, "%s\n", strerror(errno));
	fflush(stderr);
	exit(9);
}

