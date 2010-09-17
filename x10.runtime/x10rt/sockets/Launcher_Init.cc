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
	// we just skip the launcher and run the program if the user hasn't set X10LAUNCHER_NPROCS
	if (!getenv(X10LAUNCHER_NPROCS) || getenv(X10LAUNCHER_RUNTIME))
		return;

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
	strcpy(_hostfname, "hostfile"); // default host file name
	_nplaces = 1;
	_hostlist = NULL;
	_runtimePort = NULL;
	_myproc = 0xFFFFFFFF;
	_firstchildproc = 0;
	_numchildren = 0;
	_pidlst = NULL;
	_listenSocket = -1;
	_childControlLinks = NULL;
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
	_argv = argv;
	realpath(argv[0], _realpath);
	if (!getenv(X10LAUNCHER_NPROCS))
	{
		_nplaces = 1;
		setenv(X10LAUNCHER_NPROCS, "1", 0);
	}
	else
		_nplaces = atoi(getenv(X10LAUNCHER_NPROCS));
	if (!getenv(X10LAUNCHER_MYID)) // TODO - need to set?
		_myproc = 0xFFFFFFFF;
	else
		_myproc = atoi(getenv(X10LAUNCHER_MYID));

	/* -------------------------------------------- */
	/*  decide who my children are                  */
	/* current algorithm implements a binomial tree */
	/* -------------------------------------------- */
	if (_myproc != 0xFFFFFFFF)
	{
		uint32_t maxValueInLevel = 0;
		while (maxValueInLevel < _myproc)
			maxValueInLevel = maxValueInLevel*2 + 2;
		_firstchildproc = (_myproc - ((maxValueInLevel-2)/2))*2 + maxValueInLevel - 1;

		if (_firstchildproc >= _nplaces)
			_numchildren = 0;
		else if (_firstchildproc+2 <= _nplaces)
			_numchildren = 2;
		else
			_numchildren = 1;
		#ifdef DEBUG
			fprintf(stderr, "Launcher %u has %i child%s\n", _myproc, _numchildren, _numchildren==1?"":"ren");
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
	const char * ssh_command = (const char *) getenv(X10LAUNCHER_SSH);
	if (ssh_command && strlen(ssh_command) > 0)
	{
		if (strlen(ssh_command) > sizeof(_ssh_command) - 10)
			DIE("Launcher %u: SSH command is too long", _myproc);
		strncpy(_ssh_command, ssh_command, sizeof(_ssh_command) - 1);
	}

	/* ------------------------------------------------------------------ */
	/* read host file name from environment; read and interpret host file */
	/* ------------------------------------------------------------------ */
	const char * hostfname = (const char *) getenv(X10LAUNCHER_HOSTFILE);
	if (hostfname && strlen(hostfname) > 0)
	{
		if (strlen(hostfname) > sizeof(_hostfname) - 10)
			DIE("Launcher %u: host file name is too long", _myproc);
		strncpy(_hostfname, hostfname, sizeof(_hostfname) - 1);
		readHostFile();
	}
	else
		fprintf(stderr, "Warning: %s not defined.  Running %d places on localhost\n", X10LAUNCHER_HOSTFILE, _nplaces);

	connectToParentLauncher();

	/* -------------------------------------------- */
	/*  set up notification from dying processes    */
	/* -------------------------------------------- */
	signal(SIGCHLD, Launcher::cb_sighandler_cld);
}

/* *********************************************************************** */
/*                read and interpret the host file                         */
/* *********************************************************************** */

void Launcher::readHostFile()
{
	#ifdef DEBUG
		fprintf(stderr, "Launcher %u: Processing hostfile \"%s\"\n", _myproc, _hostfname);
	#endif
	FILE * fd = fopen(_hostfname, "r");
	if (!fd)
		DIE("Launcher %u: cannot open hostfile '%s': exiting", _myproc, _hostfname);

	_hostlist = (char **) malloc(sizeof(char *) * _numchildren);
	if (!_hostlist)
		DIE("Launcher %u: hostname memory allocation failure", _myproc);

/*	_execlist = (char **) malloc(sizeof(char *) * _numchildren);
	if (!_execlist)
		DIE("%d: memory allocation failure", _myproc);
*/
	uint32_t lineNumber = 0;
	while (lineNumber < _firstchildproc+_numchildren)
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
			DIE("Launcher %u: memory allocation failure", _myproc);
		strcpy(host, p);
		_hostlist[lineNumber-_firstchildproc] = host;

		#ifdef DEBUG
			fprintf(stderr, "Child launcher %i (place %i) is on %s\n", lineNumber-_firstchildproc, lineNumber, host);
		#endif

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

void Launcher::DIE(const char * msg, ...)
{
	char buffer[1200];
	va_list ap;
	va_start(ap, msg);
	vsnprintf(buffer, 119, msg, ap);
	va_end(ap);
	fprintf(stderr, "%s\n", buffer);
	if (errno != 0)
		fprintf(stderr, strerror(errno));
	exit(1);
}

