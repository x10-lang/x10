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

#include "TCP.h"
#include "Launcher.h"

/* *********************************************************************** */
/*          start a new child                                              */
/* *********************************************************************** */

void Sock::Launcher::startSSHclient(int id)
{
	char masterPort[1024];
	TCP::getname(_listenFD, masterPort, sizeof(masterPort));

	char * remotehost;
	if (_hostlist)
		remotehost = _hostlist[id-_firstchildproc];
	else
		remotehost = (char *) "localhost";

	// fprintf (stderr, "%d: starting client %d, host=%s\n", _myproc, id, remotehost);
	char * cmd = (char *) _realpath;
//	if (_execlist)
//		cmd = _execlist[id] && strlen(_execlist[id]) ? _execlist[id] : _realpath;

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
	sprintf(argv[z], X10LAUNCHER_NPROCS"=%d", _nprocs);
	argv[++z] = cmd;
	for (int i = 1; i < _argc; i++)
		argv[z + i] = _argv[i];
	argv[z + _argc] = NULL;

	// TODO - don't use SSH, just exec the program
	//if (strcmp("localhost", remotehost) == 0 || strcmp("127.0.0.1", remotehost) == 0)


	fprintf (stderr, "PM %d: executing %s ", _myproc, argv[0]);
	for (int i=1; i<z+_argc; i++)
		fprintf (stderr, " %s ", argv[i]);
	fprintf (stderr, "\n");

	z = execvp(argv[0], argv);

	if (z)
		DIE("%d: Exec failed", _myproc);
	abort();
}

