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
#include <alloca.h>
#include <arpa/inet.h>
#include <time.h>

#include "Launcher.h"
#include "TCP.h"

#define TRACEPM(x) fprintf x

/* *********************************************************************** */
/*     utility methods, called outside of the launcher					   */
/* *********************************************************************** */

int Launcher::setPort(int place, char* port)
{
	if (port == NULL)
		return -1;

	if (_singleton == NULL)
	{
		// send this out to our local launcher
		if (_parentLauncherControlLink <= 0)
		{
			if (getenv(X10LAUNCHER_PARENT) != NULL)
			{
				TRACEPM((stderr, "runtime connecting to launcher: %s\n", getenv(X10LAUNCHER_PARENT)));
				_parentLauncherControlLink = TCP::connect((const char *) getenv(X10LAUNCHER_PARENT), 10);
			}
			if (_parentLauncherControlLink <= 0)
				return -1; // connection failed for some reason
		}

		struct ctrl_msg m;
		m.type = HELLO;
		m.from = place;
		m.to = -1;
		m.datalen = strlen(port);
		int r = TCP::write(_parentLauncherControlLink, &m, sizeof(struct ctrl_msg));
		if (r <= 0) return r;
		return TCP::write(_parentLauncherControlLink, port, m.datalen);
	}
	else
	{
		_singleton->_runtimePort = (char*)malloc(strlen(port)+1);
		strcpy(_singleton->_runtimePort, port);
		return 1;
	}
}

int Launcher::lookupPlace(int myPlace, int destPlace, char* response, int responseLen)
{
	if (_singleton == NULL)
	{
		// send this out to our local launcher
		struct ctrl_msg m;
		m.type = PORT_REQUEST;
		m.from = myPlace;
		m.to = destPlace;
		m.datalen = 0;
		// parent link was established earlier
		TCP::write(_parentLauncherControlLink, &m, sizeof(struct ctrl_msg));

		// this should block, until the data is available
		int ret = TCP::read(_parentLauncherControlLink, &m, sizeof(struct ctrl_msg));
		if (ret <= 0 || m.type != PORT_RESPONSE || m.datalen <= 0)
			DIE("Invalid port request reply");

		if (responseLen < m.datalen)
			DIE("The buffer is too small for the place lookup");

		ret = TCP::read(_parentLauncherControlLink, response, m.datalen);
		if (ret <= 0)
			DIE("Unlable to read port response data");

		return 1;
	}

	// check to see if this place is us
	if (destPlace == _singleton->_myproc)
	{
		strncpy(response, _singleton->_runtimePort, responseLen);
		return 1;
	}
	else
	{
		// this request needs to be forwarded either to the parent launcher, or a child launcher
		// figure out where to send it, by determining if we are on the chain leading to it
		int parentId=destPlace, previousParentId, maxValueInLevel = 0;

		// go out to the level of the tree that contains the requested place
		while (maxValueInLevel < destPlace)
			maxValueInLevel = maxValueInLevel*2 + 2;
		// figure out the process that's in the tree at our level
		do
		{
			previousParentId = parentId;
			parentId = (previousParentId-(maxValueInLevel/2))/2 + maxValueInLevel/4;
		}
		while (parentId >= _singleton->_myproc);

		if (parentId == _singleton->_myproc)
		{	//This is one of my children
			if (previousParentId == _singleton->_firstchildproc)
			{	// send request to first child
				// TODO
			}
			else
			{	// send request to second child
				// TODO
			}
		}
		else
		{	// not my child - send the request up to my parent
			// TODO
		}
		return 0;
	}
}

/* *********************************************************************** */
/*     start all children. If there are no children, nothing is done       */
/* *********************************************************************** */

void Launcher::startChildren()
{
	TRACEPM((stderr, "PM %d: starting %d child%s\n", _myproc, _numchildren, _numchildren==1?"":"ren"));

	if (_numchildren <= 0) return;

	/* -------------------------------------------- */
	/*    create and initialize listener FD         */
	/* -------------------------------------------- */

	unsigned listenPort = 0;
	_listenSocket = TCP::listen(&listenPort, 10);
	if (_listenSocket < 0)
		DIE("%d: cannot create listener port", _myproc);

	// allocate space to hold all the links to the child launchers and the runtime
	// position 0 is the runtime, the remaining are for child launchers
	_pidlst = (int *) malloc(sizeof(int) * (_numchildren+1));
	_childCoutLinks = (int *) malloc(sizeof(int) * (_numchildren+1));
	_childCerrorLinks = (int *) malloc(sizeof(int) * (_numchildren+1));
	_childControlLinks = (int *) malloc(sizeof(int) * (_numchildren+1));

	if (!_pidlst || !_childControlLinks || !_childCoutLinks || !_childCerrorLinks)
		DIE("%d: failed in alloca()", _myproc);

	for (int id = 0; id <= _numchildren; id++)
	{
		int pid, outpipe[2], errpipe[2];
		if (pipe(outpipe) < 0) DIE("PM %d: failed in outpipe()", _myproc);
		if (pipe(errpipe) < 0) DIE("PM %d: failed in errpipe()", _myproc);
		if ((pid = fork()) < 0) DIE("PM %d: failed in fork()", _myproc);
		else if (pid > 0)
		{
			// parent process
			_pidlst[id] = pid;
			_childControlLinks[id] = -1;
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
			int z1 = dup2(outpipe[1], 1);
			close(outpipe[0]);
			int z2 = dup2(errpipe[1], 2);
			close(errpipe[0]);
			if (z1 != 1 || z2 != 2)// || z0 != 0)
				DIE("PM %d: DUP failed", _myproc);

			char masterPort[1024];
			TCP::getname(_listenSocket, masterPort, sizeof(masterPort));

			TRACEPM((stderr, "%d: leaving process management\n", _myproc));
			if (id == 0)
			{ // start up the local x10 runtime
				unsetenv(X10LAUNCHER_HOSTFILE);
				unsetenv(X10LAUNCHER_SSH);
				setenv(X10LAUNCHER_PARENT, masterPort, 1);
				execvp(_argv[0], _argv);
			}
			else
			{
				/* SSH children: call startSSH client */
				if (_hostlist && strncmp(_hostlist[id-1-_firstchildproc], "localhost", 9) != 0)
					startSSHclient(_firstchildproc + id-1, masterPort, _hostlist[id-1-_firstchildproc]);
				else
				{ // if the child is on the localhost, just exec it
					setenv(X10LAUNCHER_PARENT, masterPort, 1);
					char idString[24];
					sprintf(idString, "%d", id-1);
					setenv(X10LAUNCHER_MYID, idString, 1);
					execvp(_argv[0], _argv);
				}
				connectToParentLauncher();
			}
			return;
		}
	}

	/* process manager invokes main loop */
	handleIncomingRequests();
}


/* *********************************************************************** */
/* this is the infinite loop that the process manager is executing.        */
/* *********************************************************************** */

void Launcher::handleIncomingRequests()
{
	TRACEPM((stderr, "PM %d: main loop start\n", _myproc));
//	time_t startTime = time (NULL);
//	bool inStartup = true;
	bool running = true;

	while (running)
	{
		struct timeval timeout = { 0, 100000 };
		fd_set infds, outfds, efds;
		int fd_max = makeFDSets(&infds, &outfds, &efds);
		select(fd_max, &infds, &outfds, &efds, &timeout);

		/* listener socket (new connections) */
		if (_listenSocket >= 0)
		{
			if (FD_ISSET(_listenSocket, &efds))
			{
				close(_listenSocket);
				_listenSocket = -1;
			}
			else if (FD_ISSET(_listenSocket, &infds))
				handleNewChildConnection();
		}
		/* parent control socket */
		if (_parentLauncherControlLink >= 0)
		{
			if (FD_ISSET(_parentLauncherControlLink, &efds))
				handleDeadParent();
			else if (FD_ISSET(_parentLauncherControlLink, &infds))
				if (handleControlMessage(_parentLauncherControlLink) <= 0)
					handleDeadParent();
		}
		/* children control, stdout and stderr */
		for (int i = 0; i <= _numchildren; i++)
		{
			if (_childControlLinks[i] >= 0)
			{
				if (FD_ISSET(_childControlLinks[i], &efds))
					handleDeadChild(i);
				else if (FD_ISSET(_childControlLinks[i], &infds))
					if (handleControlMessage(_childControlLinks[i]) <= 0)
						handleDeadChild(i);
			}

			if (_childCoutLinks[i] >= 0)
			{
				if (FD_ISSET(_childCoutLinks[i], &efds))
					handleDeadChild(i);
				else if (FD_ISSET(_childCoutLinks[i], &infds))
					handleChildCout(i);
			}

			if (_childCerrorLinks[i] >= 0)
			{
				if (FD_ISSET(_childCerrorLinks[i], &efds))
					handleDeadChild(i);
				else if (FD_ISSET(_childCerrorLinks[i], &infds))
					handleChildCerror(i);
			}
		}

/*		if (inStartup)
		{
			if (time(NULL) - startTime >= 10)
			{
				// everything had better be connected within 10 seconds
				inStartup = false;
				for (int i=0; i<_numchildren; i++)
					if (_childControlLinks[i] < 0)
						running = false;
			}
			else
			{
				// see if everything connected before the time is up
				int i;
				for (i=0; i<_numchildren; i++)
					if (_childControlLinks[i] < 0)
						break;
				if (i == _numchildren)
					inStartup = false;
				else
				{
					// the children did not all startup in the time allowed.  What to do???
				}
			}
		}
		else
		{
			for (int i=0; i<_numchildren; i++)
				if (_childControlLinks[i] < 0)
					running = false;
		}
*/	}

	/* --------------------------------------------- */
	/* end of main loop. kill & wait every process   */
	/* --------------------------------------------- */
	//cleanup:

	TRACEPM((stderr, "PM %d: killing sub-processes\n", _myproc));

	int status = 0;
	for (int i = 0; i < _numchildren; i++)
	{
		TRACEPM((stderr, "PM %d: killing pid=%d\n", _myproc, _pidlst[i]));
		int status1;
		kill(_pidlst[i], SIGTERM);
		waitpid(_pidlst[i], &status1, WNOHANG);
		if (status1 != 0)
			status = status1;
	}
	TRACEPM((stderr, "PM %d: cleanup complete\n", _myproc));
	exit(status);
}

/* *********************************************************************** */
/*     prepare FD sets to listen to in main loop                           */
/* *********************************************************************** */

#define MAX(a,b) (((a)<(b))?(b):(a))
int Launcher::makeFDSets(fd_set * infds, fd_set * outfds, fd_set * efds)
{
	int fd_max = 0;
	FD_ZERO(infds);
	FD_ZERO(outfds);
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
	for (int i = 0; i <= _numchildren; i++)
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
	/* case 1: we have inherited the listener FD */
	if (_listenSocket >= 0)
	{
		char masterport[1024];
		TCP::getname(_listenSocket, masterport, sizeof(masterport));
		TRACEPM((stderr, "%d: connecting to parent: %s\n", _myproc, masterport));
		_parentLauncherControlLink = TCP::connect(masterport, 10);
	}

	/* case 2: the SOCK_PARENT env. var is set */
	else if (getenv(X10LAUNCHER_PARENT) != NULL)
	{
		TRACEPM((stderr, "Child %d: connecting to parent: %s\n", _myproc, getenv(X10LAUNCHER_PARENT)));
		_parentLauncherControlLink = TCP::connect((const char *) getenv(X10LAUNCHER_PARENT), 10);
	}

	/* case 3: launcher=-1 has no parent. We don't connect */
	else
	{
		TRACEPM((stderr, "%d: no parent\n", _myproc));
		_parentLauncherControlLink = -1;
		return;
	}

	/* we are hopefully connected. So we announce ourselves */
	if (_parentLauncherControlLink < 0)
		DIE("%d: failed to connect to parent", _myproc);

	struct ctrl_msg helloMsg;
	helloMsg.type = HELLO;
	helloMsg.from = htonl(_myproc);
	helloMsg.to = -1;
	helloMsg.datalen = 0;
	TCP::write(_parentLauncherControlLink, &helloMsg, sizeof(struct ctrl_msg));
}

void Launcher::disconnectFromLauncher(void)
{
	TRACEPM((stderr, "PM %d: closing child connections\n", _myproc));

	for (int i = 0; i <= _numchildren; i++)
	{
		if (_childControlLinks[i] >= 0)
			close(_childControlLinks[i]);
		_childControlLinks[i] = -1;
	}
}

/* *********************************************************************** */
/*   a new child is announcing itself on the listener socket               */
/* *********************************************************************** */

void Launcher::handleNewChildConnection(void)
{
	/* accept the new connection and read off the rank */
	int fd = TCP::accept(_listenSocket);
	if (fd < 0)
	{
		close(_listenSocket);
		_listenSocket = -1;
	}

	struct ctrl_msg m;
	TCP::read(fd, &m, sizeof(struct ctrl_msg));

	/* find rank in child list */
	if (m.type == HELLO)
	{
		if (m.from == _myproc)
		{
			_childControlLinks[0] = fd;
			TRACEPM((stderr, "PM %d: new ctrl conn. from local runtime\n", _myproc));
			return;
		}

		for (int i = 1; i <= _numchildren; i++)
		{
			if (m.from == _firstchildproc + i - 1)
			{
				_childControlLinks[i] = fd;
					TRACEPM((stderr, "PM %d: new ctrl conn. from child=%d\n", _myproc, m.from));
				return;
			}
		}
	}
	DIE("PM %d: internal: %s", _myproc, __FUNCTION__);
}

/* *********************************************************************** */
/*    A child connection has closed                                        */
/* *********************************************************************** */

void Launcher::handleDeadChild(int childNo)
{
	// this is usually called multiple times, for stdin, stderr, etc
	if (_childControlLinks[childNo] >= 0)
	{
		close(_childControlLinks[childNo]);
		_childControlLinks[childNo] = -1;

		TRACEPM((stderr, "PM %d: child=%d disconnected\n", _myproc, _firstchildproc+childNo));
		disconnectFromLauncher();
	}
}

/* *********************************************************************** */
/*    Parent connection has closed                                         */
/* *********************************************************************** */

void Launcher::handleDeadParent()
{
	TRACEPM((stderr, "PM %d: parent disconnected\n", _myproc));
	disconnectFromLauncher();
	if (_parentLauncherControlLink != -1)
		close(_parentLauncherControlLink);
	_parentLauncherControlLink = -1;
}

/* *********************************************************************** */
/* *********************************************************************** */

int Launcher::handleControlMessage(int fd)
{
	struct ctrl_msg m;
	assert (fd >= 0);
	int n = TCP::read(fd, &m, sizeof(struct ctrl_msg));
	if (n <= 0)
		return n;
	char* data = NULL;
	if (m.datalen > 0)
		data = (char*)alloca(m.datalen);
	if (data == NULL)
		DIE("PM cannot allocate memory for a control message");

	if (TCP::read(fd, data, m.datalen) < 0)
		DIE("PM cannot read control message data");

	if (m.to == _myproc)
	{
		switch(m.type)
		{
			case PORT_REQUEST:
			{
				m.to = m.from;
				m.from = _myproc;
				m.type = PORT_RESPONSE;
				// TODO fill in data
			}
			break;
			case PORT_RESPONSE:
			{
				// forward to connected runtime
				TCP::write(_childControlLinks[0], &m, sizeof(struct ctrl_msg));
				TCP::write(_childControlLinks[0], data, m.datalen);
			}
			break;
			case HELLO:
				DIE("Unexpected HELLO message");
			break;
		}
	}
	else
	{
		// this request needs to be forwarded either to the parent launcher, or a child launcher
		// figure out where to send it, by determining if we are on the chain leading to it
		int parentId=m.to, previousParentId, maxValueInLevel = 0;

		// go out to the level of the tree that contains the requested place
		while (maxValueInLevel < m.to)
			maxValueInLevel = maxValueInLevel*2 + 2;
		// figure out the process that's in the tree at our level
		do
		{
			previousParentId = parentId;
			parentId = (previousParentId-(maxValueInLevel/2))/2 + maxValueInLevel/4;
		}
		while (parentId >= _singleton->_myproc);

		if (parentId == _singleton->_myproc)
		{	//This is one of my children
			if (previousParentId == _singleton->_firstchildproc)
			{	// send request to first child
				// TODO
			}
			else
			{	// send request to second child
				// TODO
			}
		}
		else
		{	// not my child - send the request up to my parent
			// TODO
		}
	}
	return n;
}


void Launcher::handleChildCout(int childNo)
{
	char buf[1024];
	int n = read(_childCoutLinks[childNo], buf, sizeof(buf));
	if (n <= 0)
		handleDeadChild(childNo);
	else
		write(1, buf, n);
}

void Launcher::handleChildCerror(int childNo)
{
	char buf[1024];
	int n = read(_childCerrorLinks[childNo], buf, sizeof(buf));
	if (n <= 0)
		handleDeadChild(childNo);
	else
		write(2, buf, n);
}

/* *********************************************************************** */
/*            signal handler for PM to handle dying processes              */
/* *********************************************************************** */

void Launcher::cb_sighandler_cld(int signo)
{
	int status;
	wait(&status);

/*
	int status, pid=wait(&status);

	for (int i=0; i<_singleton->_numchildren+1; i++)
	if (_singleton->_pidlst[i] == pid)
	{
		_singleton->_actlst[i] = false;
		TRACEPM((stderr, "PM %d: SIGCHLD, killing proc=%d\n", _singleton->_myproc, _singleton->_childranks[i]));

		if (i == _singleton->_numchildren)
			for (int j=0; j<_singleton->_numchildren+1; j++)
				_singleton->_actlst[j] = false;
	}

	for (int j=0; j<_singleton->_numchildren+1; j++)
		_singleton->_actlst[j] = false;
*/
}


/* *********************************************************************** */
/*          start a new child                                              */
/* *********************************************************************** */

void Launcher::startSSHclient(int id, char* masterPort, char* remotehost)
{
	char * cmd = (char *) _realpath;
	char ** argv = (char **) alloca (sizeof(char *) * (_argc+8));
	int z = 0;
	argv[z] = _ssh_command;
	argv[++z] = remotehost;
//	argv[++z] = (char *) "/usr/bin/env";
	argv[++z] = (char*) alloca(256);
	sprintf(argv[z], X10LAUNCHER_HOSTFILE"=%s", _hostfname);
	argv[++z] = (char*) alloca(256);
	sprintf(argv[z], X10LAUNCHER_SSH"=%s", _ssh_command);
	argv[++z] = (char*) alloca(1024);
	sprintf(argv[z], X10LAUNCHER_PARENT"=%s", masterPort);
	argv[++z] = (char*) alloca(100);
	sprintf(argv[z], X10LAUNCHER_MYID"=%d", id);
	argv[++z] = (char*) alloca(100);
	sprintf(argv[z], X10LAUNCHER_NPROCS"=%d", _nplaces);
	argv[++z] = cmd;
	for (int i = 1; i < _argc; i++)
		argv[z + i] = _argv[i];
	argv[z + _argc] = NULL;

	fprintf (stderr, "PM %d: executing %s ", _myproc, argv[0]);
	for (int i=1; i<z+_argc; i++)
		fprintf (stderr, " %s ", argv[i]);
	fprintf (stderr, "\n");

	z = execvp(argv[0], argv);

	if (z)
		DIE("%d: Exec failed", _myproc);
	abort();
}
