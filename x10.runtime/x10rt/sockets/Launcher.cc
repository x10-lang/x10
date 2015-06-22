/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *
 *  This file was written by Ben Herta for IBM: bherta@us.ibm.com
 */

#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/param.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>
#include <stdio.h>
#include <assert.h>
#include <fcntl.h>
#include <stdarg.h>
#ifndef BSD
#include <alloca.h>
#endif
#include <arpa/inet.h>
#include <sched.h>
#include <errno.h>

#ifdef __MACH__
#include <crt_externs.h>
#endif

#include "Launcher.h"
#include "TCP.h"

/* *********************************************************************** */
/*     utility methods, called outside of the launcher					   */
/* *********************************************************************** */
const char* CTRL_MSG_TYPE_STRINGS[] = {"HELLO", "GOODBYE", "PORT_REQUEST", "PORT_RESPONSE", "LAUNCH_REQUEST", "LAUNCH_RESPONSE"};

int Launcher::setPort(uint32_t place, int port)
{
	if (_singleton == NULL) // this call is running outside of the launcher
	{
		// send this out to our local launcher
		if (_parentLauncherControlLink <= 0)
		{
			if (getenv(X10_LAUNCHER_PARENT) != NULL)
			{
				#ifdef DEBUG
					fprintf(stderr, "Runtime %u connecting to launcher at \"%s\"\n", place, getenv(X10_LAUNCHER_PARENT));
				#endif

				_parentLauncherControlLink = TCP::connect((const char *) getenv(X10_LAUNCHER_PARENT), 10, true);
			}
			if (_parentLauncherControlLink <= 0)
				return -1; // connection failed for some reason
		}

		struct ctrl_msg m;
		m.type = HELLO;
		m.from = place;
		m.to = place;
		m.datalen = sizeof(port);
		int r = TCP::write(_parentLauncherControlLink, &m, sizeof(struct ctrl_msg));
		if (r <= 0) return -1;
		r = TCP::write(_parentLauncherControlLink, &port, m.datalen);
		if (r < m.datalen) return -1;
		#ifdef DEBUG
			fprintf(stderr, "Runtime %u: sent port data \"%i\" to launcher\n", place, port);
		#endif
	}
	else // this call is running within the launcher
	{
		int lenofName = strlen(_singleton->_runtimePort);
		snprintf(&_singleton->_runtimePort[lenofName], sizeof(_singleton->_runtimePort)-lenofName, ":%u", port);
		#ifdef DEBUG
			fprintf(stderr, "Launcher %d: Runtime is at \"%s\"\n", place, _singleton->_runtimePort);
		#endif
	}
	return 1;
}

int Launcher::lookupPlace(uint32_t myPlace, uint32_t destPlace, char* response, int responseLen)
{
	// TODO - this isn't reentrant.  Need to fix that.
	struct ctrl_msg m;
	m.type = PORT_REQUEST;
	m.from = myPlace;
	m.to = destPlace;
	m.datalen = 0;

	if (_singleton == NULL)
	{
		// inside this if, we're running within the runtime process, and talking to a launcher
		// send this out to our local launcher
		// parent link was established earlier, at HELLO
		if (_parentLauncherControlLink <= 0)
			DIE("Runtime %u: bad link to launcher", myPlace);

		#ifdef DEBUG
			fprintf(stderr, "Runtime %u: sending %s:%u to launcher %u\n", myPlace, CTRL_MSG_TYPE_STRINGS[m.type], destPlace, myPlace);
		#endif
		int ret = TCP::write(_parentLauncherControlLink, &m, sizeof(struct ctrl_msg));
		if (ret <= 0)
			DIE("Runtime %u: Unable to write port request", myPlace);

		// this should block, until the data is available
		#ifdef DEBUG
			fprintf(stderr, "Runtime %u: waiting for %s:%u message from launcher %u\n", myPlace, CTRL_MSG_TYPE_STRINGS[PORT_RESPONSE], destPlace, myPlace);
		#endif
		ret = TCP::read(_parentLauncherControlLink, &m, sizeof(struct ctrl_msg));
		if (ret <= 0 || m.type != PORT_RESPONSE || m.datalen <= 0)
			DIE("Runtime %u: Invalid port request reply (len=%d, type=%s, datalen=%d)", myPlace, ret, CTRL_MSG_TYPE_STRINGS[m.type], m.datalen);

		if (responseLen < m.datalen+1)
			DIE("Runtime %u: The buffer is too small for the place lookup (data=%d bytes, buffer=%d bytes)", myPlace, m.datalen, responseLen);

		#ifdef DEBUG
			fprintf(stderr, "Runtime %d: waiting for %s data from launcher %u\n", myPlace, CTRL_MSG_TYPE_STRINGS[PORT_RESPONSE], myPlace);
		#endif
		ret = TCP::read(_parentLauncherControlLink, response, m.datalen);
		if (ret <= 0)
			DIE("Runtime %u: Unable to read port response data", myPlace);
		response[m.datalen] = '\0';

		#ifdef DEBUG
			fprintf(stderr, "Runtime %u: determined place %u is at \"%s\"\n", myPlace, destPlace, response);
		#endif

		return m.datalen;
	}

	// if we're here, then this is executing in the launcher process, not in a runtime.
	// we can't handle this request.  Forward it to some launcher that can.
	return _singleton->forwardMessage(&m, NULL);
}

/* *********************************************************************** */
/*     Create a new empty directory at this place and change to it         */
/* *********************************************************************** */

void Launcher::enterRandomScratchDir (uint32_t _myproc)
{
    // [DC] Deciding not to use mkdtemp as it is too recent (posix 2008)
    // Although tmpnam may return a fresh path that then becomes occupied
    // before the mkdir, we detect this and retry tmpnam so this should be safe.
    // [DC] Since tmpnam seems to generate an irritating warning from GNU ld,
    // rolling my own here

    // this tries to avoid collisions
    srand((unsigned int)getpid());

    int max_attempts = 1000;
    for (int attempts = 0 ; attempts<max_attempts ; attempts++) {

        char random_path[PATH_MAX];

        int r = snprintf(random_path, sizeof random_path, "%s/x10_scratch%d_%08x", P_tmpdir, _myproc, (unsigned int)rand());
        if ((size_t)r >= sizeof random_path) {
            DIE("Launcher %d: while trying to create a scratch directory string buffer (%Z bytes) overflowed.", _myproc, sizeof random_path);
        }

        // attempt to mkdir the random_path
        r = mkdir(random_path, S_IRWXU);
        if (r==-1) {
            if (errno==EEXIST) continue; // try again
            // some other sort of error...
            DIE("Launcher %d: cannot create scratch directory \"%s\"", _myproc, random_path);
        }

        // successfully made random_path

        r = chdir(random_path);
        if (r==-1) {
            DIE("Launcher %d: cannot chdir to scratch directory \"%s\"", _myproc, random_path);
        }

        return;
    }

    // if we didn't return already, attempts exceeded
    DIE("Launcher %d: after %d attempts, could not make a fresh scratch directory", _myproc, max_attempts);
}

/* *********************************************************************** */
/*     start all children. If there are no children, nothing is done       */
/* *********************************************************************** */

void Launcher::startChildren()
{
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: starting %d child%s\n", _myproc, _myproc==0xFFFFFFFF?1:_numchildren, (_numchildren==1 || _myproc==0xFFFFFFFF)?"":"ren");
	#endif

	/* -------------------------------------------- */
	/*    create and initialize listener FD         */
	/* -------------------------------------------- */

	unsigned listenPort = 0;
	_listenSocket = TCP::listen(&listenPort, 10);
	if (_listenSocket < 0)
		DIE("Launcher %d: cannot create listener port", _myproc);
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: opened listen socket at port %d\n", _myproc, listenPort);
	#endif

	// allocate space to hold all the links to the child launchers and the runtime
	// Child launchers (if any) are first in the list, followed by the local runtime in the last slot.
	_pidlst = (int *) malloc(sizeof(int) * (_numchildren+1));
	_childCoutLinks = (int *) malloc(sizeof(int) * (_numchildren+1));
	_childCerrorLinks = (int *) malloc(sizeof(int) * (_numchildren+1));
	_childControlLinks = (int *) malloc(sizeof(int) * (_numchildren+1));

	if (!getenv(X10_LAUNCHER_CWD))
	{
		char dir[1024];
		char *r = getcwd(dir, sizeof(dir));
        if (r==NULL) DIE("Launcher %d: getcwd failed", _myproc);
		setenv(X10_LAUNCHER_CWD, dir, 1);
	}

	if (!_pidlst || !_childControlLinks || !_childCoutLinks || !_childCerrorLinks)
		DIE("%u: failed in alloca()", _myproc);

	char masterPort[PORT_MAX];
	if (_myproc == 0xFFFFFFFF)	// the initial launcher is not in the hostlist.  Get the hostname from the socket
	{
		TCP::getname(_listenSocket, masterPort, sizeof(masterPort));
		setenv(X10_LAUNCHER_HOST, "localhost", 0);
	}
	else
	{ // get hostname from the X10_LAUNCHER_HOST environment variable
		strncpy(masterPort, getenv(X10_LAUNCHER_HOST), PORT_MAX);
		sockaddr_in addr;
		socklen_t len = sizeof(addr);
		if (getsockname(_listenSocket, (sockaddr *) &addr, &len) < 0)
			DIE("failed to get the local socket information");
		int mplen=strlen(masterPort);
		snprintf(&masterPort[mplen], sizeof(masterPort)-mplen, ":%u", ntohs(addr.sin_port));
	}

	for (uint32_t id=0; id <= _numchildren; id++)
	{
		int pid, outpipe[2], errpipe[2];
		if (pipe(outpipe) < 0) DIE("Launcher %d: failed in outpipe()", _myproc);
		if (pipe(errpipe) < 0) DIE("Launcher %d: failed in errpipe()", _myproc);
		if ((pid = fork()) < 0) DIE("Launcher %d: failed in fork()", _myproc);
		else if (pid > 0)
		{
			// parent process
			_pidlst[id] = pid;
			_childControlLinks[id] = -1; // these get set later, when the child connects
			_childCoutLinks[id] = outpipe[0];
			close(outpipe[1]);
			_childCerrorLinks[id] = errpipe[0];
			close(errpipe[1]);
			fcntl(_childCoutLinks[id], F_SETFL, O_NONBLOCK | O_NDELAY);
			fcntl(_childCerrorLinks[id], F_SETFL, O_NONBLOCK | O_NDELAY);
		}
		else
		{
			// child process
			// redirect stderr and stdout over to our pipes
			int z1 = dup2(outpipe[1], fileno(stdout));
			close(outpipe[0]);
			int z2 = dup2(errpipe[1], fileno(stderr));
			close(errpipe[0]);
			if (z1 != 1 || z2 != 2)
				DIE("Launcher %d: DUP failed", _myproc);

			if (id == _numchildren && _myproc != 0xFFFFFFFF)
			{ // start up the local x10 runtime
				unsetenv(X10_HOSTFILE);
				unsetenv(X10_HOSTLIST);
				unsetenv(X10_LAUNCHER_SSH);
				unsetenv(X10_LAUNCHER_RUNLAUNCHER);
				setenv(X10_LAUNCHER_PARENT, masterPort, 1);
                if (checkBoolEnvVar(getenv(X10_SCRATCH_CWD))) {
                    enterRandomScratchDir(_myproc);
                } else {
                    int r = chdir(getenv(X10_LAUNCHER_CWD));
                    if (r==-1) {
                        DIE("Launcher %d: could not chdir to indicated working directory \"%s\"", _myproc, getenv(X10_LAUNCHER_CWD));
                    }
                }
				unsetenv(X10_LAUNCHER_CWD);

				// check to see if we want to launch this in a debugger
                if (checkBoolEnvVar(getenv(X10_JDB)))
                {
                    const char *jdb_suspend = getenv(X10_JDB_SUSPEND) == NULL ? "none" : getenv(X10_JDB_SUSPEND);
                    bool suspend_me = false;
                    if (!strcmp(jdb_suspend,"none") || !strcmp(jdb_suspend,"NONE")) {
                        suspend_me = false;
                    } else if (!strcmp(jdb_suspend,"all") || !strcmp(jdb_suspend,"ALL")) {
                        suspend_me = true;
                    } else if (!strcmp(jdb_suspend,"first") || !strcmp(jdb_suspend,"FIRST")) {
                        suspend_me = 0==_myproc;
                    } else {
                        DIE("The X10_JDB_SUSPEND value %s is invalid (must be none / all / first)\n", jdb_suspend);
                    }
                    long base_port = getenv(X10_JDB_BASE_PORT)==NULL ? 8000 : strtol(getenv(X10_JDB_BASE_PORT), NULL, 10);
                    if (base_port < 0 || base_port > 65535) {
                        DIE("The X10_JDB_BASE_PORT value %ld is invalid (must be 0 -> 65535)\n", base_port);
                    }
                    
                    // launch this runtime in a debugger
                    char** newargv;
                    #ifdef DEBUG
                        fprintf(stderr, "Runtime %u forked with jdb at port %l.  Running exec.\n", _myproc, base_port);
                    #endif
                    int numArgs = 0;
                    while (_argv[numArgs] != NULL)
                        numArgs++;
                    newargv = (char**)alloca((numArgs+3)*sizeof(char*));
                    if (newargv == NULL)
                        DIE("Launcher %d: Allocating new argv for jdb params failed\n", _myproc);
                    newargv[0] = _argv[0];
                    newargv[1] = _argv[1];
                    newargv[2] = (char*)"-Xdebug";
                    char jdwp[1000] = "";
                    snprintf(jdwp, sizeof jdwp, "-Xrunjdwp:transport=dt_socket,address=%ld,server=y,suspend=%s", base_port+_myproc, suspend_me ? "y" : "n");
                    newargv[3] = jdwp;
                    for (int i=2; i<numArgs; i++)
                        newargv[i+2] = _argv[i];
                    newargv[numArgs+2] = (char*)NULL;
                    if (newargv[0] == NULL)
                    	DIE("Launcher %d: Unable to exec runtime with jdb because newargv[0] is null", _myproc);
                    if (execvp(newargv[0], newargv))
                        // can't get here, if the exec succeeded
                        DIE("Launcher %d: runtime exec with jdb failed", _myproc);
                }

                // check to see if we want to launch this in a debugger
				char* which = getenv(X10_GDB);
				if (which != NULL)
				{
					char placenum[64];
					strcpy(placenum, which);
					char * colon = strchr(placenum, ':');
					if (colon != NULL)
						colon[0] = '\0';

					// see if the launcher argument applies to this place
					char* p;
					errno = 0;
					if (strcasecmp("all", placenum) == 0 || ((_myproc == (uint32_t)strtoul(placenum, &p, 10)) && !(errno != 0 || *p != 0 || p == placenum)))
					{
						// launch this runtime in a debugger
						char** newargv;
						if (colon == NULL)
						{
							#ifdef DEBUG
								fprintf(stderr, "Runtime %u forked with gdb in an xterm.  Running exec.\n", _myproc);
							#endif
							int numArgs = 0;
							while (_argv[numArgs] != NULL)
								numArgs++;
							newargv = (char**)alloca((numArgs+8)*sizeof(char*));
							if (newargv == NULL)
								DIE("Allocating space for exec-ing gdb runtime %d\n", _myproc);
							char* title = (char*)alloca(32);
							sprintf(title, "Place %u debug", _myproc);
							newargv[0] = (char*)"xterm";
							newargv[1] = (char*)"-T";
							newargv[2] = title;
							newargv[3] = (char*)"-e";
							newargv[4] = (char*)"gdb";
							newargv[5] = _argv[0];
							newargv[6] = (char*)"--args";
							for (int i=0; i<numArgs; i++)
								newargv[i+7] = _argv[i];
							newargv[numArgs+7] = (char*)NULL;
						}
						else
						{
							colon[0] = ':';
							#ifdef DEBUG
								fprintf(stderr, "Runtime %u forked with a remote gdbserver at port %s.  Running exec.\n", _myproc, &colon[1]);
							#endif
							int numArgs = 0;
							while (_argv[numArgs] != NULL)
								numArgs++;
							newargv = (char**)alloca((numArgs+3)*sizeof(char*));
							if (newargv == NULL)
								DIE("Allocating space for exec-ing gdb runtime %d\n", _myproc);
							newargv[0] = (char*)"gdbserver";
							newargv[1] = colon;
							for (int i=0; i<numArgs; i++)
								newargv[i+2] = _argv[i];
							newargv[numArgs+2] = (char*)NULL;
						}
						if (newargv[0] == NULL)
							DIE("Launcher %d: Unable to exec with gdb because newargv[0] is null", _myproc);
						if (execvp(newargv[0], newargv))
							// can't get here, if the exec succeeded
							DIE("Launcher %d: runtime exec with gdb failed", _myproc);
					}
				}

				#ifdef DEBUG
					fprintf(stderr, "Runtime %u forked.  Running exec.\n", _myproc);
				#endif
				if (_argv[0] == NULL)
					DIE("Launcher %d: Unable to exec runtime because argv[0] is null", _myproc);
				if (execvp(_argv[0], _argv))
					// can't get here, if the exec succeeded
					DIE("Launcher %d: runtime exec failed", _myproc);
			}
			else
			{
				setenv(X10_LAUNCHER_RUNLAUNCHER, "1", 1);
				/* SSH children: call startSSH client */
				if (_hostlist && strncmp(_hostlist[id], "localhost", 9) != 0)
					startSSHclient(_firstchildproc+id, masterPort, _hostlist[id]);
				else
				{ // if the child is on the localhost, just exec it.  No need for ssh.
					setenv(X10_LAUNCHER_PARENT, masterPort, 1);
					char idString[24];
					sprintf(idString, "%d", _firstchildproc+id);
					setenv(X10_LAUNCHER_PLACE, idString, 1);
					#ifdef DEBUG
						fprintf(stderr, "Launcher %d forked launcher %s on localhost.  Running exec.\n", _myproc, idString);
					#endif
					if (_argv[0] == NULL)
						DIE("Launcher %d: Unable to exec child launcher because argv[0] is null", _myproc);
					if (execvp(_argv[0], _argv))
						DIE("Launcher %d: local child launcher exec failed", _myproc);
				}
			}
		}
	}

	/* process manager invokes main loop */
	handleRequestsLoop(false);
}


/* *********************************************************************** */
/* this is the infinite loop that the process manager is executing.        */
/* *********************************************************************** */

void Launcher::handleRequestsLoop(bool onlyCheckForNewConnections)
{
	#ifdef DEBUG
	if (!onlyCheckForNewConnections)
		fprintf(stderr, "Launcher %d: main loop start\n", _myproc);
	#endif
	bool running = true;

	while (running)
	{
		struct timeval timeout = { 0, 100000 };
		fd_set infds, efds;
		int fd_max = makeFDSets(&infds, NULL, &efds);
		if (select(fd_max+1, &infds, NULL, &efds, &timeout) < 0)
			break; // select error.  This can happen when we're in the middle of shutdown

		if (_singleton->_dieAt > 0)
		{
			time_t now = time(NULL);
			#ifdef DEBUG
				fprintf(stderr, "Launcher %d: Time to die=%lu, current time=%lu\n", _myproc, _dieAt, now);
			#endif
			if (now >= _singleton->_dieAt) {
				if (_singleton->_dieAt == 1)
					fprintf(stderr, "Launcher %d: Caught a SIGTERM: tearing down all runtimes\n", _myproc);
				else
					fprintf(stderr, "Launcher %d: tearing down remaining runtimes after waiting %u seconds\n", _myproc, SHUTDOWN_GRACE_PERIOD);
				break;
			}
		}

		/* listener socket (new connections) */
		if (_listenSocket >= 0)
		{
			if (FD_ISSET(_listenSocket, &efds))
			{
				#ifdef DEBUG
					fprintf(stderr, "Launcher %d: listensocket ERROR detected\n", _myproc);
				#endif
				close(_listenSocket);
				_listenSocket = -1;
			}
			else if (FD_ISSET(_listenSocket, &infds))
				handleNewChildConnection();
		}
		if (onlyCheckForNewConnections)
			return;
		/* parent control socket */
		if (_parentLauncherControlLink >= 0)
		{
			if (FD_ISSET(_parentLauncherControlLink, &efds))
				running = handleDeadParent();
			else if (FD_ISSET(_parentLauncherControlLink, &infds))
				if (handleControlMessage(_parentLauncherControlLink) < 0)
					running = handleDeadParent();
		}
		/* runtime and children control, stdout and stderr */
		for (uint32_t i = 0; i <= _numchildren; i++)
		{
            bool this_child_alive = true;
			if (_childControlLinks[i] >= 0)
			{
				if (FD_ISSET(_childControlLinks[i], &efds))
					running = handleDeadChild(i, 0);
				else if (FD_ISSET(_childControlLinks[i], &infds))
					if (handleControlMessage(_childControlLinks[i]) < 0)
						running = handleDeadChild(i, 0);
			}

			if (_childCoutLinks[i] >= 0)
			{
				if (FD_ISSET(_childCoutLinks[i], &efds))
					running = handleDeadChild(i, 1);
				else if (FD_ISSET(_childCoutLinks[i], &infds))
					running = handleChildCout(i);
			}

			if (_childCerrorLinks[i] >= 0)
			{
				if (FD_ISSET(_childCerrorLinks[i], &efds))
					running = handleDeadChild(i, 2);
				else if (FD_ISSET(_childCerrorLinks[i], &infds))
					running = handleChildCerror(i);
			}
		}
	}
    #ifdef DEBUG
        fprintf(stderr, "Launcher %d: coming out of main loop\n", _myproc);
    #endif

	/* --------------------------------------------- */
	/* end of main loop. kill & wait every process   */
	/* --------------------------------------------- */

	signal(SIGCHLD, SIG_DFL); // disable our SIGCHLD handler, restore default handler

	// send SIGTERM to any remaining children
	Launcher::cb_sighandler_term(SIGTERM);

	// shut down any connections if they still exist
	handleDeadParent();

	// clean up any leftover children (prevent zombies) and process exit code
	// launchers -1 and 0 may hang forever if runtime at place 0 hangs
	// other launchers and runtimes should always terminate
	int status;

	// first wait for sublaunchers to exit
	// sublaunchers should die on their own because we previously cut connections with them
	for (uint32_t i = 0; i < _numchildren; i++)
	{
		if (_pidlst[i] != -1)
			waitpid(_pidlst[i], &status, 0);
	}

	// second kill local runtimes at place > 0, or if we're exiting due to a signal at some other place
	if (_pidlst[_numchildren] != -1)
	{
		if ((_myproc!=0 && _myproc!=0xFFFFFFFF) || _exitcode != (int)0xFEEDC0DE)
			kill(_pidlst[_numchildren], SIGKILL);

		// finally wait for local runtime/launcher 0
		waitpid(_pidlst[_numchildren], &status, 0);
	}

	if (_exitcode == (int)0xFEEDC0DE) // capture the runtime exit code if we haven't already saved one previously
	{
		// propagate exit code of local runtime/launcher 0
		if (WIFEXITED(status))
		{
			_exitcode = WEXITSTATUS(status);
//			#ifdef DEBUG
				if (_exitcode != 0) fprintf(stdout, "Launcher %d: local runtime exited with code %i\n", _myproc, WEXITSTATUS(status));
//			#endif
		}
		else if (WIFSIGNALED(status))
		{
			// ignore these, as they are normal on shutdown, or likely were sent a few lines above
			if (WTERMSIG(status) == SIGPIPE || WTERMSIG(status) == SIGKILL || WTERMSIG(status) == SIGTERM)
				_exitcode = 0;
			else
			{
				_exitcode = 128 + WTERMSIG(status);
//				#ifdef DEBUG
					fprintf(stdout, "Launcher %d: local runtime exited with signal %i\n", _myproc, WTERMSIG(status));
//				#endif
			}
		}
		else if (WIFSTOPPED(status))
		{
			_exitcode = 128 + WSTOPSIG(status);
//			#ifdef DEBUG
				fprintf(stdout, "Launcher %d: local runtime stopped with code %i\n", _myproc, WSTOPSIG(status));
//			#endif
		}
		#ifdef DEBUG
		else if (WIFCONTINUED(status))
			fprintf(stdout, "Launcher %d: local runtime continue signal detected\n", _myproc);
		else
			fprintf(stdout, "Launcher %d: local runtime exit status unknown\n", _myproc);
		#endif
	}

	// free up allocated memory (not really needed, since we're about to exit)
	free(_hostlist);
//	#ifdef DEBUG
        if (_exitcode != 0)
		    fprintf(stderr, "Launcher %d: cleanup complete, exit code=%u.  Goodbye!\n", _myproc, _exitcode);
//	#endif
	exit(_exitcode);
}

/* *********************************************************************** */
/*     prepare FD sets to listen to in main loop                           */
/* *********************************************************************** */

#ifndef MAX
#define MAX(a,b) (((a)<(b))?(b):(a))
#endif
int Launcher::makeFDSets(fd_set * infds, fd_set * outfds, fd_set * efds)
{
	int fd_max = 0;
	FD_ZERO(infds);
	FD_ZERO(efds);

	/* listener socket */
	if (_listenSocket >= 0)
	{
		FD_SET (_listenSocket, infds);
		FD_SET (_listenSocket, efds);
		fd_max = MAX(fd_max, _listenSocket);
	}

	/* control socket to parent */
	if (_parentLauncherControlLink >= 0)
	{
		FD_SET (_parentLauncherControlLink, infds);
		FD_SET (_parentLauncherControlLink, efds);
		fd_max = MAX(fd_max, _parentLauncherControlLink);
	}

	/* control sockets to children */
	for (uint32_t i = 0; i <= _numchildren; i++)
	{
		if (_childControlLinks[i] >= 0)
		{
			FD_SET(_childControlLinks[i], infds);
			FD_SET(_childControlLinks[i], efds);
			fd_max = MAX(fd_max, _childControlLinks[i]);
		}
		if (_childCoutLinks[i] >= 0)
		{
			FD_SET(_childCoutLinks[i], infds);
			FD_SET(_childCoutLinks[i], efds);
			fd_max = MAX(fd_max, _childCoutLinks[i]);
		}
		if (_childCerrorLinks[i] >= 0)
		{
			FD_SET(_childCerrorLinks[i], infds);
			FD_SET(_childCerrorLinks[i], efds);
			fd_max = MAX(fd_max, _childCerrorLinks[i]);
		}
	}

	return fd_max;
}

/* *********************************************************************** */
/* open control connection to parent (if any) and announce ourselves       */
/* *********************************************************************** */

void Launcher::connectToParentLauncher(void)
{
	if (_myproc == 0)
		return; // don't connect - nothing useful for us at the primordial launcher.

	/* case 1: we have inherited the listener FD */
	if (_listenSocket >= 0)
	{
		char masterport[1024];
		TCP::getname(_listenSocket, masterport, sizeof(masterport));
		#ifdef DEBUG
			fprintf(stderr, "Launcher %d: connecting to parent via inherited port: %s\n", _myproc, masterport);
		#endif
		_parentLauncherControlLink = TCP::connect(masterport, 10, true);
	}

	/* case 2: the SOCK_PARENT env. var is set */
	else if (getenv(X10_LAUNCHER_PARENT) != NULL)
	{
		#ifdef DEBUG
			fprintf(stderr, "Launcher %d: connecting to parent via: %s\n", _myproc, getenv(X10_LAUNCHER_PARENT));
		#endif
		_parentLauncherControlLink = TCP::connect((const char *) getenv(X10_LAUNCHER_PARENT), 10, true);
	}

	/* case 3: launcher=-1 has no parent. We don't connect */
	else
	{
		#ifdef DEBUG
			fprintf(stderr, "Launcher %d: has no parent.\n", _myproc);
		#endif
		_parentLauncherControlLink = -1;
		return;
	}

	/* we are hopefully connected. So we announce ourselves */
	if (_parentLauncherControlLink < 0)
		DIE("Launcher %d: failed to connect to parent", _myproc);

	struct ctrl_msg helloMsg;
	helloMsg.type = HELLO;
	helloMsg.from = _myproc;
	helloMsg.to = -1;
	helloMsg.datalen = 0;
	TCP::write(_parentLauncherControlLink, &helloMsg, sizeof(struct ctrl_msg));
}

/* *********************************************************************** */
/*   a new child is announcing itself on the listener socket               */
/* *********************************************************************** */

void Launcher::handleNewChildConnection(void)
{
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: new connection detected\n", _myproc);
	#endif
	/* accept the new connection and read off the rank */
	int fd = TCP::accept(_listenSocket, true);
	if (fd < 0)
	{
		close(_listenSocket);
		_listenSocket = -1;
	}

	struct ctrl_msg m;
	int size = TCP::read(fd, &m, sizeof(struct ctrl_msg));

	/* find rank in child list */
	if (size == sizeof(struct ctrl_msg) && m.type == HELLO)
	{
		if (m.from == _myproc)
		{
			_childControlLinks[_numchildren] = fd;
			if (m.datalen > 0)
			{
				int portNum;
				size = TCP::read(_childControlLinks[_numchildren], &portNum, m.datalen);
				if (size < m.datalen)
					DIE("Launcher %d: could not read local runtime port number", _myproc);
				int len=strlen(_runtimePort);
				snprintf(&_runtimePort[len], sizeof(_runtimePort)-len, ":%u", ntohs(portNum));
			}
			#ifdef DEBUG
				fprintf(stderr, "Launcher %d: new ctrl conn %d from local runtime, with runtime port=\"%s\"\n", _myproc, fd, _runtimePort);
			#endif
			return;
		}

		for (uint32_t i = 0; i < _numchildren; i++) // the runtime is not in this loop
		{
			if (m.from == _firstchildproc + i)
			{
				_childControlLinks[i] = fd;
				#ifdef DEBUG
					fprintf(stderr, "Launcher %d: new ctrl conn from child=%d\n", _myproc, m.from);
				#endif
				if (m.datalen > 0)
				{
					char* data = (char*)alloca(m.datalen+1);
					data[m.datalen] = '\0';
					TCP::read(fd, data, m.datalen);
					DIE("Launcher %d: Control message from child launcher came in with datalen of \"%s\"\n", _myproc, data);
				}
				return;
			}
		}
	}

	// something bad came in.  Maybe some other program trying to connect to our port.  Disconnect it.
	close(fd);
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: Invalid %d byte message came into listen socket.  Closing it down.\n", _myproc, size);
	#endif
}

/* *********************************************************************** */
/*    A child connection has closed                                        */
/* *********************************************************************** */
// returns false if everything should die
bool Launcher::handleDeadChild(uint32_t childNo, int type)
{
	// this is usually called multiple times, for stdin, stderr, etc
	if (type == 0 && _childControlLinks[childNo] >= 0)
	{
		close(_childControlLinks[childNo]);
		_childControlLinks[childNo] = -1;

		#ifdef DEBUG
		if (childNo == _numchildren && _myproc != 0xFFFFFFFF)
			fprintf(stderr, "Launcher %d: local runtime control disconnected\n", _myproc);
		else
			fprintf(stderr, "Launcher %d: child=%d control disconnected\n", _myproc, _firstchildproc+childNo);
		#endif
	}
	else if (type == 1 && _childCoutLinks[childNo] >= 0)
	{
		close(_childCoutLinks[childNo]);
		_childCoutLinks[childNo] = -1;

		#ifdef DEBUG
		if (childNo == _numchildren && _myproc != 0xFFFFFFFF)
			fprintf(stderr, "Launcher %d: local runtime cout disconnected\n", _myproc);
		else
			fprintf(stderr, "Launcher %d: child=%d cout disconnected\n", _myproc, _firstchildproc+childNo);
		#endif
	}
	else if (type == 2 && _childCerrorLinks[childNo] >= 0)
	{
		close(_childCerrorLinks[childNo]);
		_childCerrorLinks[childNo] = -1;

		#ifdef DEBUG
		if (childNo == _numchildren && _myproc != 0xFFFFFFFF)
			fprintf(stderr, "Launcher %d: local runtime cerror disconnected\n", _myproc);
		else
			fprintf(stderr, "Launcher %d: child=%d cerror disconnected\n", _myproc, _firstchildproc+childNo);
		#endif
	}

	// check to see if all of this child's links are down
	if (_childControlLinks[childNo]>=0 || _childCoutLinks[childNo]>=0 || _childCerrorLinks[childNo]>=0)
		return true; // some parts of this child are still alive

	// if we reach here, then this child is dead.  Check out why.
	int status;
	waitpid(_pidlst[childNo], &status, 0);
	_pidlst[childNo] = -1;

	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: captured exit code %s, %i of child %u\n", _myproc, (WIFEXITED(status)?"true":"false"), WEXITSTATUS(status), childNo);
	#endif

	// check for failure to launch, via special ssh exit code 255
	if (childNo < _numchildren && WIFEXITED(status) && WEXITSTATUS(status)==255) {
		_exitcode = 255;
		DIE("Launcher %d: Error launching place %u on host \"%s\".  Please verify hostnames are specified correctly.", _myproc, _firstchildproc+childNo, _hostlist[childNo]);
	}
	// launch was ok, this is the program exiting.
	// save this return code if it's worth saving.  The local runtime overwrites other runtimes if there are multiple errors (runtime 0 is top dog).
	else if (_exitcode == (int)0xFEEDC0DE || (childNo == _numchildren && !(WIFSIGNALED(status) && WTERMSIG(status)==SIGTERM)))
	{
		if (childNo == _numchildren)
		{
			if (WIFEXITED(status))
			{
				_exitcode = WEXITSTATUS(status);
				if (_exitcode > 0 && _myproc != 0xFFFFFFFF)
					fprintf(stderr, "Place %u exited unexpectedly with exit code: %i\n", _myproc, _exitcode);
			}
			else if (WIFSIGNALED(status))
			{
				if (WTERMSIG(status) != SIGPIPE) // normal at shutdown
				{
					_exitcode = 128 + WTERMSIG(status);
					if (_myproc == (uint32_t)-1)
						fprintf(stderr, "Launcher for place 0 exited unexpectedly with signal: %s\n", strsignal(WTERMSIG(status)));
					else
					    fprintf(stderr, "Place %u exited unexpectedly with signal: %s\n", _myproc, strsignal(WTERMSIG(status)));
				}
			}
			else if (WIFSTOPPED(status))
			{
				_exitcode = 128 + WSTOPSIG(status);
				#ifdef DEBUG
					fprintf(stderr, "Launcher %d: Local runtime stopped with code %i\n", _myproc, WSTOPSIG(status));
				#endif
			}
			#ifdef DEBUG
			else if (WIFCONTINUED(status))
				fprintf(stderr, "Launcher %d: Local runtime continue signal detected\n", _myproc);
			else
				fprintf(stderr, "Launcher %d: Local runtime exit status unknown\n", _myproc);
			#endif
		}
		else if (WIFEXITED(status) && WEXITSTATUS(status) > 0)
		{
			// this indicates that one of our child launchers caught exited abnormally
			// forward the signal and exit immediately
			_exitcode = WEXITSTATUS(status);
			#ifdef DEBUG
				fprintf(stderr, "Launcher %d: child launcher gave return code %i.  Forwarding.\n", _myproc, _exitcode);
			#endif
			//Launcher::cb_sighandler_term(SIGTERM);
			//return false;
		}
	}
	#ifdef DEBUG
	else
		fprintf(stderr, "Launcher %d: exit code is already set\n", _myproc);
	#endif

	// start the deadman timer
	Launcher::cb_sighandler_cld(SIGTERM);

	// check to see if *all* child links are down
	for (uint32_t i=0; i<=_numchildren; i++)
	{
		if (_pidlst[i] != -1) {
			#ifdef DEBUG
				fprintf(stderr, "Launcher %d: at least one child is still alive\n", _myproc);
			#endif
			return true;
		}
	}
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: all children appear to be dead\n", _myproc);
	#endif
	return false;
}

/* *********************************************************************** */
/*    Parent connection has closed                                         */
/* *********************************************************************** */

bool Launcher::handleDeadParent()
{
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: parent disconnected\n", _myproc);
	#endif
	if (_parentLauncherControlLink != -1)
		close(_parentLauncherControlLink);
	_parentLauncherControlLink = -1;

	// take down all the children
	for (uint32_t i=0; i<=_numchildren; i++)
	{
		if (_pidlst[i] != -1)
		{
			for (int j=0; j<=2; j++)
				handleDeadChild(i, j);
		}
	}
	return false;
}

/* *********************************************************************** */
/* *********************************************************************** */

int Launcher::handleControlMessage(int fd)
{
	struct ctrl_msg m;
	assert (fd >= 0);
	int ret = TCP::read(fd, &m, sizeof(struct ctrl_msg));
	if (ret < (int)sizeof(struct ctrl_msg))
		return -1;

	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: Incoming %d byte %s message from %u for %u, with datalen=%d\n",
				_myproc, ret, CTRL_MSG_TYPE_STRINGS[m.type], m.from, m.to, m.datalen);
	#endif

	char* data = NULL;
	if (m.datalen > 0)
	{
		data = (char*)alloca(m.datalen);
		if (data == NULL)
			DIE("Launcher %d: cannot allocate %d bytes for a control message", _myproc, m.datalen);
	}

	if (TCP::read(fd, data, m.datalen) < 0)
		DIE("Launcher %d: cannot read %d bytes of control message data", _myproc, m.datalen);

	if (m.to == _myproc)
	{
		switch(m.type)
		{
			case PORT_REQUEST:
			{
				while (strchr(_runtimePort, ':') == NULL)
				{
					sched_yield();
					handleRequestsLoop(true);
				}
				m.to = m.from;
				m.from = _myproc;
				m.type = PORT_RESPONSE;
				m.datalen = strlen(_runtimePort);
				#ifdef DEBUG
					fprintf(stderr, "Launcher %d preparing %s message for %u.\n", _myproc, CTRL_MSG_TYPE_STRINGS[PORT_RESPONSE], m.to);
				#endif
				TCP::write(fd, &m, sizeof(struct ctrl_msg));
				TCP::write(fd, _runtimePort, m.datalen);
			}
			break;
			case PORT_RESPONSE:
			{
				// forward to connected runtime
				#ifdef DEBUG
					fprintf(stderr, "Launcher %d forwarding %s message to local runtime.\n", _myproc, CTRL_MSG_TYPE_STRINGS[PORT_RESPONSE]);
				#endif
				TCP::write(_childControlLinks[_numchildren], &m, sizeof(struct ctrl_msg));
				TCP::write(_childControlLinks[_numchildren], data, m.datalen);
			}
			break;
			case LAUNCH_REQUEST:
			{
				m.to = m.from;
				m.from = _myproc;
				m.type = LAUNCH_RESPONSE;
				int zero = 0;
				m.datalen = sizeof(zero);
				#ifdef DEBUG
					fprintf(stderr, "Launcher %d responding to an unsupported launch request \n", _myproc);
				#endif
				TCP::write(fd, &m, sizeof(struct ctrl_msg));
				TCP::write(fd, &zero, m.datalen);
			}
			break;
			default:
				DIE("Launcher %d got unexpected message type %i", _myproc, m.type);
			break;
		}
	}
	else
	{
		ret = forwardMessage(&m, data);
		if (ret < 0 && m.type == PORT_REQUEST)
		{
			// if we're here, then we were unable to forward the PORT_REQUEST message.  Send an error message back as the response.
			#ifdef DEBUG
				fprintf(stderr, "Launcher %d failed to forward a %s message to place %u.  Sending an error response.\n", _myproc, CTRL_MSG_TYPE_STRINGS[PORT_RESPONSE], m.to);
			#endif

			char* dead = (char*)alloca(64);
			sprintf(dead, "LAUNCHER_%u_IS_NOT_RUNNING", m.to);
			m.to = m.from;
			m.from = _myproc;
			m.type = PORT_RESPONSE;
			m.datalen = strlen(dead);

			TCP::write(fd, &m, sizeof(struct ctrl_msg));
			TCP::write(fd, dead, m.datalen);
		}
	}
	return ret;
}


bool Launcher::handleChildCout(int childNo)
{
	#ifdef DEBUG
		//fprintf(stderr, "Launcher %d: cout from %s detected\n", _myproc, childNo==_numchildren?"runtime":"child launcher");
	#endif
	char buf[1024];
	int n = read(_childCoutLinks[childNo], buf, sizeof(buf));
	if (n <= 0)
		return handleDeadChild(childNo, 1);
	else
	{
		int r = write(fileno(stdout), buf, n);
        if (r==-1) DIE("Launcher %d could not write child process stdout buffer to launcher stdout", _myproc);
		fflush(stdout);
	}
	return true;
}

bool Launcher::handleChildCerror(int childNo)
{
	#ifdef DEBUG
		//fprintf(stderr, "Launcher %d: cerror from %s detected\n", _myproc, childNo==_numchildren?"runtime":"child launcher");
	#endif
	char buf[1024];
	int n = read(_childCerrorLinks[childNo], buf, sizeof(buf));
	if (n <= 0)
		return handleDeadChild(childNo, 2);
	else
	{
		int r = write(fileno(stderr), buf, n);
        if (r==-1) DIE("Launcher %d could not write child process stderr buffer to launcher stderr", _myproc);
		fflush(stderr);
	}
	return true;
}

int Launcher::forwardMessage(struct ctrl_msg* message, char* data)
{
	// this request needs to be forwarded either to the parent launcher, or a child launcher
	// figure out where to send it, by determining if we are on the chain between place 0 and the dest
	uint32_t child=message->to, parent;

	int destID = -1;
	if (child > _singleton->_myproc)
	{
		do
		{
			parent = (child-1)/2;
			if (parent == _singleton->_myproc)
			{
				if (child == _singleton->_firstchildproc)
					destID = 0; //_childControlLinks[0];
				else
					destID = 1; //_childControlLinks[1];
				#ifdef DEBUG
					fprintf(stderr, "Launcher %d: forwarding %s message to child launcher %u.\n", _myproc, CTRL_MSG_TYPE_STRINGS[message->type], child);
				#endif
				break;
			}
			else
				child = parent;
		}
		while (parent > _singleton->_myproc);
	}

	#ifdef DEBUG
	if (destID == -1)
		fprintf(stderr, "Launcher %d: forwarding %s message to parent launcher.\n", _myproc, CTRL_MSG_TYPE_STRINGS[message->type]);
	#endif

	int destFD = -1;
	do
	{
		if (destID == -1) destFD = _parentLauncherControlLink;
		else if (destID == 0) destFD = _childControlLinks[0];
		else if (destID == 1) destFD = _childControlLinks[1];

		// verify that the link is valid.  It may not be, if we're just starting up
		if (destFD == -1)
		{
			if (destID >= 0 && _pidlst[destID] == -1)
				return -1; // this request is for a launcher that has DIED

			sched_yield();
			handleRequestsLoop(true);
		}
	}
	while (destFD == -1);


	int ret = TCP::write(destFD, message, sizeof(struct ctrl_msg));
	if (ret < (int)sizeof(struct ctrl_msg))
		DIE("Launcher %d: Failed to forward message to %s", _myproc,
			destFD==_parentLauncherControlLink?"parent launcher":
			(destFD==_childControlLinks[0]?"child launcher 0":"child launcher 1"));
	if (message->datalen > 0)
		ret = TCP::write(destFD, data, message->datalen);

	return ret;
}

/* *********************************************************************** */
/*            signal handler for PM to handle dying processes              */
/* *********************************************************************** */

void Launcher::cb_sighandler_cld(int signo)
{
	// one of our children died
	// limit our lifetime to a few seconds, to allow any children to shut down on their own. Then kill em' all.
	if (_singleton->_dieAt == 0)
	{
        // Note that "X10_RESILIENT_MODE" is also checked in Configuration.x10
        char* resilient_mode = getenv(X10_RESILIENT_MODE);
        bool resilient_x10 = (resilient_mode!=NULL && strtol(resilient_mode, NULL, 10) != 0);

        if (!resilient_x10) {
            _singleton->_dieAt = SHUTDOWN_GRACE_PERIOD+time(NULL); // SHUTDOWN_GRACE_PERIOD seconds into the future
            #ifdef DEBUG
                fprintf(stderr, "Launcher %d: started the doomsday device\n", _singleton->_myproc);
            #endif
        } else {
            #ifdef DEBUG
                fprintf(stderr, "Launcher %d: not starting the doomsday device\n", _singleton->_myproc);
            #endif
        }
	}
}

void Launcher::cb_sighandler_term(int signo)
{
	#ifdef DEBUG
		fprintf(stderr, "Launcher %d: got a SIGTERM\n", _singleton->_myproc);
	#endif
	for (uint32_t i = 0; i <= _singleton->_numchildren; i++)
	{
		if (_singleton->_pidlst[i] != -1)
		{
			#ifdef DEBUG
				fprintf(stderr, "Launcher %d: killing pid=%d\n", _singleton->_myproc, _singleton->_pidlst[i]);
			#endif
			kill(_singleton->_pidlst[i], SIGTERM);
		}
	}
	_singleton->_dieAt = 1; // die now.
}


/* *********************************************************************** */
/*          start a new child                                              */
/* *********************************************************************** */

static char *alloc_printf(const char *fmt, ...)
{
    va_list args;
    char try_buf[1];
    va_start(args, fmt);
    size_t sz = vsnprintf(try_buf, 0, fmt, args);
    va_end(args);
    char *r = (char*)malloc(sz+1);
    va_start(args, fmt);
    size_t s1 = vsnprintf(r, sz+1, fmt, args);
    (void) s1;
    assert (s1 == sz);
    va_end(args);
    return r;
}


// this escaping is for BLAH in the case of the following bash script
// echo "${VAR-BLAH}"
// (the double quotes are important)
static char *escape_various_things (const char *in)
{
    size_t sz = strlen(in);
    size_t out_sz = sz+3;
    char *out = static_cast<char*>(malloc(out_sz+1));
    size_t out_cnt = 0;
    for (size_t i=0 ; i<sz ; ++i) {
        if (out_cnt+3 >= out_sz) {
            out_sz = out_cnt+3;
            out = static_cast<char*>(realloc(out, out_sz+1));
        }
        switch (in[i]) {
            case '\'': // ' will break bash, turn it into "'" (note that \' does not work for some reason)
            out[out_cnt++] = '"';
            out[out_cnt++] = in[i];
            out[out_cnt++] = '"';
            break;

            case '$': // escape these with an additional backslash
            case '`':
            case '\"':
            case '\\':
            out[out_cnt++] = '\\';

            default: // everything else (probably) OK
            out[out_cnt++] = in[i];
            break;
        }
    }
    out[out_cnt] = '\0';
    return out;
}

// this escaping is for BLAH in the case of the following bash script
// echo BLAH
// the strategy is to put single quotes around the whole thing, meaning the only things
// we have to deal with inside are additional occurrences of the single quote character.
static char *escape_various_things2 (const char *in)
{
    size_t sz = strlen(in);
    size_t out_sz = sz+5;
    char *out = (char*) malloc(sz+5);
    size_t out_cnt = 0;
    out[out_cnt++] = '\''; // beginning quote
    for (size_t i=0 ; i<sz ; ++i) {
        if (out_cnt+5 >= out_sz) {
            out_sz = out_cnt+10;
            out = static_cast<char*>(realloc(out, out_sz+1));
        }
        switch (in[i]) {
             case '\'': // ' will break bash, turn it into "'" (but come out of the existing single quote first)
            out[out_cnt++] = '\'';
            out[out_cnt++] = '"';
            out[out_cnt++] = '\'';
            out[out_cnt++] = '"';
            out[out_cnt++] = '\'';
            break;

            default: // everything else (probably) OK
            out[out_cnt++] = in[i];
            break;
        }
    }
    out[out_cnt++] = '\'';
    out[out_cnt] = '\0';
    return out;
}

static char *alloc_env_assign(const char *var, const char *val)
{
    return alloc_printf("%s\"=${%s-%s}\"", var, var, escape_various_things(val));
}

static char *alloc_env_always_assign(const char *var, const char *val)
{
    return alloc_printf("%s=%s", var, escape_various_things2(val));
}

// check with the environment variable name contains characters that bash does not allow (e.g. '.')
bool is_env_var_valid (const char *var)
{
    // [a-zA-Z_]([a-zA-Z0-9_]*)

    // number as first character not allowed
    if (*var >= '0' && *var <= '9') return false;
    for (const char *v=var ; *v != '\0' ; ++v) {
        if (*v >= '0' && *v <= '9') continue;
        if (*v >= 'A' && *v <= 'Z') continue;
        if (*v >= 'a' && *v <= 'z') continue;
        if (*v == '_') continue;
        return false;
    }
    return true;
}

void Launcher::startSSHclient(uint32_t id, char* masterPort, char* remotehost)
{
	char * cmd = (char *) _realpath;

	bool usePseudoTTY = true;
	char * pseudoTTYEnv = getenv(X10_LAUNCHER_TTY);
	if (pseudoTTYEnv != NULL)
		usePseudoTTY = checkBoolEnvVar(pseudoTTYEnv);

    // leak a bunch of memory, we're about to exec anyway and that will clean it up
    // on all OS that we care about

    // get an array of environment variables in the form "VAR=val"
#ifdef __MACH__
    char** environ = *_NSGetEnviron();
#else
    extern char **environ;
#endif
	// find out how many environment variables there are
    unsigned environ_sz = 0;
    while (environ[environ_sz]!=NULL) environ_sz++;

	char ** argv = (char **) alloca (sizeof(char *) * (_argc+environ_sz+32));
	int z = 0;
	argv[z] = _ssh_command;
	if (usePseudoTTY) {
		static char ttyarg[] = "-t";
		argv[++z] = ttyarg;
		argv[++z] = ttyarg; // put in -t twice
	}
	static char quietarg[] = "-q";
	argv[++z] = quietarg;
	argv[++z] = remotehost;
    static char env_string[] = "env";
	argv[++z] = env_string;

    // deal with the environment variables
    for (unsigned i=0 ; i<environ_sz ; ++i)
    {
        char *var = strdup(environ[i]);
        *strchr(var,'=') = '\0';
        if (!is_env_var_valid(var)) continue;
        if (strcmp(var,X10_HOSTFILE)==0) continue;
        if (strcmp(var,X10_LAUNCHER_SSH)==0) continue;
        if (strcmp(var,X10_LAUNCHER_PARENT)==0) continue;
        if (strcmp(var,X10_LAUNCHER_PLACE)==0) continue;
		char* val = getenv(var);
        assert(val!=NULL);
        #ifdef DEBUG
            fprintf(stderr, "Launcher %d: copying environment variable %s=%s for child %u.\n", _myproc, var, val, id);
        #endif
        bool x10_var = false;
        if (strncmp(var, "X10_", 4)==0) x10_var = true;
        if (strncmp(var, "X10RT_", 6)==0) x10_var = true;
        argv[++z] = x10_var ? alloc_env_always_assign(var,val) : alloc_env_assign(var, val);
	}

	if (_hostfname != '\0')
	{
        argv[++z] = alloc_env_assign(X10_HOSTFILE, _hostfname);
	}
    argv[++z] = alloc_env_always_assign(X10_LAUNCHER_SSH, _ssh_command);
    argv[++z] = alloc_env_always_assign(X10_LAUNCHER_PARENT, masterPort);
    argv[++z] = alloc_env_always_assign(X10_LAUNCHER_PLACE, alloc_printf("%d",id));
    argv[++z] = alloc_env_always_assign(X10_LAUNCHER_HOST, remotehost);
	argv[++z] = cmd;
	for (int i = 1; i < _argc; i++) {
        argv[++z] = escape_various_things2(_argv[i]);
    }
	argv[++z] = NULL;

	#ifdef DEBUG
		fprintf(stderr, "Launcher %d exec-ing SSH process to start up launcher %u on %s.\n", _myproc, id, remotehost);
		for (int i=0; i<z; i++)
			fprintf (stderr, " %s ", argv[i]);
		fprintf (stderr, "\n");
	#endif

	if (argv[0] == NULL)
		DIE("Launcher %d: Unable to exec ssh because argv[0] is null", _myproc);
	z = execvp(argv[0], argv);

	if (z != 0)
		DIE("Launcher %d: ssh exec failed.  errno=%i", _myproc, errno);
	abort();
}
