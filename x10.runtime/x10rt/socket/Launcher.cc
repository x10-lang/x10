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

//#define TRACEPM(x)
#define TRACEPM(x) fprintf x

/* *********************************************************************** */
/*     start all children. If there are no children, nothing is done       */
/* *********************************************************************** */

void Sock::Launcher::startChildren()
{
	TRACEPM((stderr, "PM %d: starting %d child%s\n", _myproc, _numchildren, _numchildren==1?"":"ren"));

	if (_numchildren <= 0) return;

	/* -------------------------------------------- */
	/*    create and initialize listener FD         */
	/* -------------------------------------------- */

	unsigned listenPort = 0;
	_listenFD = TCP::listen(&listenPort, 10);
	if (_listenFD < 0)
		DIE("%d: cannot create listener port", _myproc);

	/* -------------------------------------------- */
	/* allocate subprocess list, control FD list    */
	/* -------------------------------------------- */
	_pidlst = (int *) malloc(sizeof(int) * (_numchildren+1));
	_ctrlfd = (int *) malloc(sizeof(int) * (_numchildren));
	_coutfd = (int *) malloc(sizeof(int) * (_numchildren+1));
	_cerrfd = (int *) malloc(sizeof(int) * (_numchildren+1));

	if (!_pidlst || !_ctrlfd )//|| !_coutfd || !_cerrfd)
		DIE("%d: failed in alloca()", _myproc);

	for (int id = 0; id < _numchildren; id++)
	{
		int pid, outpipe[2], errpipe[2]; //,inpipe[2];
//		if (pipe(inpipe) < 0) DIE("PM %d: failed in inpipe()", _myproc);
		if (pipe(outpipe) < 0) DIE("PM %d: failed in outpipe()", _myproc);
		if (pipe(errpipe) < 0) DIE("PM %d: failed in errpipe()", _myproc);
		if ((pid = fork()) < 0) DIE("PM %d: failed in fork()", _myproc);
		else if (pid > 0)
		{
			/* process manager registers child */
			_pidlst[id] = pid;
			_ctrlfd[id] = -1;
			//_cinfd[id] = inpipe[1];
//			close(inpipe[0]);
			_coutfd[id] = outpipe[0];
			close(outpipe[1]);
			_cerrfd[id] = errpipe[0];
			close(outpipe[1]);
			fcntl(_coutfd[id], F_SETFL, O_NONBLOCK | O_NDELAY);
			fcntl(_cerrfd[id], F_SETFL, O_NONBLOCK | O_NDELAY);
		}
		else
		{
			/* finish work with stdin/stdout pipes */
//			int z0 = dup2(inpipe[0], 0);
//			close(inpipe[1]);
/*			int z1 = dup2(outpipe[1], 1);
			close(outpipe[0]);
			int z2 = dup2(errpipe[1], 2);
			close(errpipe[0]);
			if (z1 != 1 || z2 != 2)// || z0 != 0)
				DIE("PM %d: DUP failed", _myproc);
*/			/* SSH children: call startSSH client */
			startSSHclient(_firstchildproc + id);

			/* fork child */
			TRACEPM((stderr, "%d: leaving process management\n", _myproc));
			pm_connect();
			return;
		}
	}

	/* process manager invokes main loop */
	pm_loop();
}

/* *********************************************************************** */
/* this is the infinite loop that the process manager is executing.        */
/* *********************************************************************** */

void Sock::Launcher::pm_loop()
{
	TRACEPM((stderr, "PM %d: main loop start\n", _myproc));
	time_t startTime = time (NULL);
	bool inStartup = true;
	bool running = true;

	while (running)
	{
		struct timeval timeout = { 0, 100000 };
		fd_set infds, outfds, efds;
		int fd_max = pm_makeFDSets(&infds, &outfds, &efds);
		select(fd_max, &infds, &outfds, &efds, &timeout);

		/* listener socket (new connections) */
		if (_listenFD >= 0)
		{
			if (FD_ISSET(_listenFD, &efds))
			{
				close(_listenFD);
				_listenFD = -1;
			}
			else if (FD_ISSET(_listenFD, &infds))
				cb_ctrl_newChild();
		}
		/* parent control socket */
		if (_ctrlfdp >= 0)
		{
			if (FD_ISSET(_ctrlfdp, &efds))
				cb_ctrl_deadParent();
			else if (FD_ISSET(_ctrlfdp, &infds))
				cb_ctrl_parent();
		}
		/* children control, stdout and stderr */
		for (int i = 0; i < _numchildren; i++)
		{
			if (_ctrlfd[i] >= 0)
			{
				if (FD_ISSET(_ctrlfd[i], &efds))
					cb_ctrl_deadChild(i);
				else if (FD_ISSET(_ctrlfd[i], &infds))
					cb_ctrl_child(i);
			}

			if (_coutfd[i] >= 0)
			{
				if (FD_ISSET(_coutfd[i], &efds))
					cb_ctrl_deadChild(i);
				else if (FD_ISSET(_coutfd[i], &infds))
					cb_cout_child(i);
			}

			if (_cerrfd[i] >= 0)
			{
				if (FD_ISSET(_cerrfd[i], &efds))
					cb_ctrl_deadChild(i);
				else if (FD_ISSET(_cerrfd[i], &infds))
					cb_cerr_child(i);
			}
		}

		if (inStartup)
		{
			if (time(NULL) - startTime >= 10)
			{
				// everything had better be connected within 10 seconds
				inStartup = false;
				for (int i=0; i<_numchildren; i++)
					if (_ctrlfd[i] < 0)
						running = false;
			}
			else
			{
				// see if everything connected before the time is up
				int i;
				for (i=0; i<_numchildren; i++)
					if (_ctrlfd[i] < 0)
						break;
				if (i == _numchildren)
				{
					inStartup = false;
					// Now that our child launchers are running, time to start the local runtime instance.
					// TODO - fork off the program to run
					// TODO - shutdown listener
				}
			}
		}
		else
		{
			for (int i=0; i<_numchildren; i++)
				if (_ctrlfd[i] < 0)
					running = false;
		}
	}

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
int Sock::Launcher::pm_makeFDSets(fd_set * infds, fd_set * outfds, fd_set * efds)
{
	int fd_max = 0;
	FD_ZERO(infds);
	FD_ZERO(outfds);
	FD_ZERO(efds);

	/* listener socket */
	if (_listenFD >= 0)
	{
		FD_SET (_listenFD, infds);
		FD_SET (_listenFD, efds);
		fd_max = MAX(fd_max, _listenFD);
	}

	/* control socket to parent */
	if (_ctrlfdp >= 0)
	{
		FD_SET (_ctrlfdp, infds);
		FD_SET (_ctrlfdp, efds);
		fd_max = MAX(fd_max, _ctrlfdp);
	}

	/* control sockets to children */
	for (int i = 0; i < _numchildren; i++)
	{
		if (_ctrlfd[i] >= 0)
		{
			FD_SET(_ctrlfd[i], infds);
			FD_SET(_ctrlfd[i], efds);
			fd_max = MAX(fd_max, _ctrlfd[i]);
		}
		if (_coutfd[i] >= 0)
		{
			FD_SET(_coutfd[i], infds);
			FD_SET(_coutfd[i], efds);
			fd_max = MAX(fd_max, _coutfd[i]);
		}
		if (_cerrfd[i] >= 0)
		{
			FD_SET(_cerrfd[i], infds);
			FD_SET(_cerrfd[i], efds);
			fd_max = MAX(fd_max, _cerrfd[i]);
		}
	}

	return fd_max;
}

/* *********************************************************************** */
/* open control connection to parent (if any) and announce ourselves       */
/* *********************************************************************** */

void Sock::Launcher::pm_connect(void)
{
	/* case 1: we have inherited the listener FD */
	if (_listenFD >= 0)
	{
		char masterport[1024];
		TCP::getname(_listenFD, masterport, sizeof(masterport));
		TRACEPM((stderr, "%d: connecting to parent: %s\n", _myproc, masterport));
		_ctrlfdp = TCP::connect(masterport, 10);
	}

	/* case 2: the SOCK_PARENT env. var is set */
	else if (getenv(X10LAUNCHER_PARENT) != NULL)
	{
		TRACEPM((stderr, "Child %d: connecting to parent: %s\n", _myproc, getenv(X10LAUNCHER_PARENT)));
		_ctrlfdp = TCP::connect((const char *) getenv(X10LAUNCHER_PARENT), 10);
	}

	/* case 3: rank=0 has no parent. We don't connect */
	else
	{
		TRACEPM((stderr, "%d: no parent\n", _myproc));
		_ctrlfdp = -1;
		return;
	}

	/* we are hopefully connected. So we announce ourselves */
	if (_ctrlfdp < 0)
		DIE("%d: failed to connect to parent", _myproc);
	int myrank = htonl(_myproc);
	write(_ctrlfdp, &myrank, sizeof(myrank));
}

void Sock::Launcher::pm_disconnect(void)
{
	TRACEPM((stderr, "PM %d: closing child connections\n", _myproc));

	for (int i = 0; i < _numchildren; i++)
	{
		if (_ctrlfd[i] >= 0)
			close(_ctrlfd[i]);
		_ctrlfd[i] = -1;
	}
}

/* *********************************************************************** */
/*   a new child is announcing itself on the listener socket               */
/* *********************************************************************** */

void Sock::Launcher::cb_ctrl_newChild(void)
{
	/* accept the new connection and read off the rank */
	int fd = TCP::accept(_listenFD);
	if (fd < 0)
	{
		close(_listenFD);
		_listenFD = -1;
	}
	int tmp, rank;
	if (read(fd, &tmp, sizeof(tmp)) != sizeof(tmp))
		DIE("PM %d: cannot read newly opened child control connection", _myproc);
	rank = ntohl(tmp);

	/* find rank in child list */
	for (int i = 0; i < _numchildren; i++)
	{
		if (rank == _firstchildproc + i)
		{
			_ctrlfd[i] = fd;
			TRACEPM((stderr, "PM %d: new ctrl conn. from child=%d\n", _myproc, rank));
			return;
		}
	}
	DIE("PM %d: internal: %s", _myproc, __FUNCTION__);
}

/* *********************************************************************** */
/*    A child connection has closed                                        */
/* *********************************************************************** */

void Sock::Launcher::cb_ctrl_deadChild(int childNo)
{
	// this is usually called multiple times, for stdin, stderr, etc
	if (_ctrlfd[childNo] >= 0)
	{
		close(_ctrlfd[childNo]);
		_ctrlfd[childNo] = -1;

		TRACEPM((stderr, "PM %d: child=%d disconnected\n", _myproc, _firstchildproc+childNo));
		pm_disconnect();
	}
}

/* *********************************************************************** */
/*    Parent connection has closed                                         */
/* *********************************************************************** */

void Sock::Launcher::cb_ctrl_deadParent()
{
	TRACEPM((stderr, "PM %d: parent disconnected\n", _myproc));
	pm_disconnect();
	if (_ctrlfdp != -1)
		close(_ctrlfdp);
	_ctrlfdp = -1;
}

/* *********************************************************************** */
/* *********************************************************************** */

void Sock::Launcher::cb_ctrl_child(int childNo)
{
	char buf[1024];
	// interpret message from child
	assert (_ctrlfd[childNo] >= 0);

	int n = read(_ctrlfd[childNo], buf, sizeof(buf));
	if (n <= 0)
		cb_ctrl_deadChild(childNo);
	TRACEPM((stderr, "PM %d: ctrl message, %d bytes from child %d\n", _myproc, n, childNo));
}

/* *********************************************************************** */
/* *********************************************************************** */

void Sock::Launcher::cb_ctrl_parent()
{
	char buf[1024];
	assert (_ctrlfdp >= 0);
	int n = read(_ctrlfdp, buf, sizeof(buf));
	if (n <= 0)
		cb_ctrl_deadParent();
	TRACEPM((stderr, "PM %d: ctrl message from parent, %d bytes\n", _myproc, n));
}


void Sock::Launcher::cb_cout_child(int childNo)
{
	char buf[1024];
	int n = read(_coutfd[childNo], buf, sizeof(buf));
	if (n <= 0)
		cb_ctrl_deadChild(childNo);
	else
		write(1, buf, n);
}

void Sock::Launcher::cb_cerr_child(int childNo)
{
	char buf[1024];
	int n = read(_cerrfd[childNo], buf, sizeof(buf));
	if (n <= 0)
		cb_ctrl_deadChild(childNo);
	else
		write(2, buf, n);
}

/* *********************************************************************** */
/*            signal handler for PM to handle dying processes              */
/* *********************************************************************** */

void Sock::Launcher::cb_sighandler_cld(int signo)
{
	int status;
	wait(&status);

/*
	int status, pid=wait(&status);

	for (int i=0; i<_instance->_numchildren+1; i++)
	if (_instance->_pidlst[i] == pid)
	{
		_instance->_actlst[i] = false;
		TRACEPM((stderr, "PM %d: SIGCHLD, killing proc=%d\n", _instance->_myproc, _instance->_childranks[i]));

		if (i == _instance->_numchildren)
			for (int j=0; j<_instance->_numchildren+1; j++)
				_instance->_actlst[j] = false;
	}

	for (int j=0; j<_instance->_numchildren+1; j++)
		_instance->_actlst[j] = false;
*/
}
