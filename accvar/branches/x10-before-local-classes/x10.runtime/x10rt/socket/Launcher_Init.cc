/* *********************************************************************** */
/* *********************************************************************** */

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>
#include <stdio.h>
#include <assert.h>
#include <fcntl.h>
#include <stdarg.h>
#include <arpa/inet.h>
#include <errno.h>

#include "TCP.h"
#include "Launcher.h"

/* *********************************************************************** */
/* *********************************************************************** */

Sock::Launcher * Sock::Launcher::_instance = NULL;

/* *********************************************************************** */
/*           private constructor. Initialize everything to zeroes          */
/* *********************************************************************** */

Sock::Launcher::Launcher()
{
	_argc = 0;
	_argv = NULL;
	memset(_realpath, 0, sizeof(_realpath));
	memset(_ssh_command, 0, sizeof(_ssh_command));
	strcpy(_ssh_command, "/usr/bin/ssh");
	memset(_hostfname, 0, sizeof(_hostfname));
	strcpy(_hostfname, "");
	_nprocs = 1;

	_hostlist = NULL;
//	_execlist = NULL;
	_myproc = -1;
	_firstchildproc = 0;
	_numchildren = 0;

	_pidlst = NULL;

	_listenFD = -1;
	_ctrlfdp = -1;
	_ctrlfd = NULL;
}

/* *********************************************************************** */
/*          Initialize the singleton. Fails if called twice.               */
/* *********************************************************************** */

void Sock::Launcher::Setup(int argc, char ** argv)
{
	assert (_instance == NULL);

	// check to see if we need to launch stuff, or if we need to run stuff.  This combination distinguishes the two.
	if (!getenv(X10LAUNCHER_HOSTFILE) && getenv(X10LAUNCHER_NPROCS) && getenv(X10LAUNCHER_MYID) && !getenv(X10LAUNCHER_SSH) && !getenv(X10LAUNCHER_PARENT))
		return;

	_instance = (Launcher *) malloc(sizeof(Launcher));
	if (!_instance) DIE("memory allocation failure in Initializer");
	new (_instance) Launcher();
	_instance->initialize(argc, argv);
	_instance->startChildren();
}

/* *********************************************************************** */
/*              initialization.                                            */
/* - read argc, argv and environment variables                             */
/* - read and populate the host file                                       */
/* *********************************************************************** */

void Sock::Launcher::initialize(int argc, char ** argv)
{
	/* ------------------------------------------------ */
	/*  copy argc and argv, determine myproc and nprocs */
	/* ------------------------------------------------ */

	_argc = argc;
	_argv = argv;
	realpath(argv[0], _realpath);
	_nprocs = getenv(X10LAUNCHER_NPROCS) ? atoi(getenv(X10LAUNCHER_NPROCS)) : 1;
	_myproc = getenv(X10LAUNCHER_MYID) ? atoi(getenv(X10LAUNCHER_MYID)) : -1;

	/* -------------------------------------------- */
	/*  decide who my children are                  */
	/* current algorithm implements a binomial tree */
	/* -------------------------------------------- */
	if (_myproc >= 0)
	{
		int maxValueInLevel = 0;
		while (maxValueInLevel < _myproc)
			maxValueInLevel = maxValueInLevel*2 + 2;
		_firstchildproc = (_myproc - ((maxValueInLevel-2)/2))*2 + maxValueInLevel - 1;

		if (_firstchildproc >= _nprocs)
			_numchildren = 0;
		else if (_firstchildproc+2 <= _nprocs)
			_numchildren = 2;
		else
			_numchildren = 1;
	}
	else
	{
		_firstchildproc = 0;
		_numchildren = 1;
	}

	fprintf(stderr, "Place %i has %i children\n", _myproc, _numchildren);

	/* --------------------------------- */
	/* copy SSH command from environment */
	/* --------------------------------- */
	const char * ssh_command = (const char *) getenv(X10LAUNCHER_SSH);
	if (ssh_command && strlen(ssh_command) > 0)
	{
		if (strlen(ssh_command) > sizeof(_ssh_command) - 10)
			DIE("%d: SSH command is too long", _myproc);
		strncpy(_ssh_command, ssh_command, sizeof(_ssh_command) - 1);
	}

	/* ------------------------------------------------------------------ */
	/* read host file name from environment; read and interpret host file */
	/* ------------------------------------------------------------------ */
	const char * hostfname = (const char *) getenv(X10LAUNCHER_HOSTFILE);
	if (hostfname && strlen(hostfname) > 0)
	{
		if (strlen(hostfname) > sizeof(_hostfname) - 10)
			DIE("%d: host file name is too long", _myproc);
		strncpy(_hostfname, hostfname, sizeof(_hostfname) - 1);
		init_readHostFile();
	}

	pm_connect();

	/* -------------------------------------------- */
	/*  set up notification from dying processes    */
	/* -------------------------------------------- */
	signal(SIGCHLD, Launcher::cb_sighandler_cld);
}

/* *********************************************************************** */
/*                read and interpret the host file                         */
/* *********************************************************************** */

void Sock::Launcher::init_readHostFile()
{
	FILE * fd = fopen(_hostfname, "r");
	if (!fd)
		DIE("%d: cannot open hostfile '%s': exiting", _myproc, _hostfname);

	_hostlist = (char **) malloc(sizeof(char *) * _numchildren);
	if (!_hostlist)
		DIE("%d: memory allocation failure", _myproc);
/*	_execlist = (char **) malloc(sizeof(char *) * _numchildren);
	if (!_execlist)
		DIE("%d: memory allocation failure", _myproc);
*/
	int lineNumber = -1;
	while (lineNumber < _firstchildproc+_numchildren-1)
	{
		char buffer[5120];
		fgets(buffer, sizeof(buffer), fd);
		if (feof(fd))
			break;

		// skip lines that aren't our children
		lineNumber++;
		if (lineNumber < _firstchildproc)
			continue;

		char * p = strtok(buffer, " \t\n\r");
		int plen = p ? strlen(p) : 0;
		if (plen <= 0)
			break;

		char * host = (char *) malloc(plen + 10);
		if (!host)
			DIE("%d: memory allocation failure", _myproc);
		strcpy(host, p);
		_hostlist[lineNumber-_firstchildproc] = host;

		fprintf(stderr, "child %i (place %i) is on %s\n", lineNumber-_firstchildproc, lineNumber, host);

		/*
		p = strtok(NULL, " \t\n\r");
		plen = p ? strlen(p) : 0;
		char * exec = NULL;
		if (plen > 0)
		{
			exec = (char *) malloc(plen + 10);
			if (!exec)
				DIE("%d: memory allocation failure", _myproc);
			strcpy(exec, p);
		}
		_execlist[lineNumber-_firstchildproc] = exec;
		*/
	}
	fclose(fd);
}


/* *********************************************************************** */
/*                    internal error in process manager                    */
/* *********************************************************************** */

void Sock::Launcher::DIE(const char * msg, ...)
{
	char buffer[1200];
	va_list ap;
	va_start(ap, msg);
	vsnprintf(buffer, 119, msg, ap);
	va_end(ap);
	fprintf(stderr, "PM: %s\n", buffer);
	if (errno != 0)
		fprintf(stderr, strerror(errno));
	exit(1);
}

