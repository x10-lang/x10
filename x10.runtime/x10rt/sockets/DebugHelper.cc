/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011.
 *
 *	This code was provided by Steve Cooper (coopers@ca.ibm.com) to allow
 *	the X10 debugger to attach to the X10 runtime.
 */

#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <signal.h>
 
typedef void (*sighandler_t)(int);
#ifndef MSG_NOSIGNAL
#define MSG_NOSIGNAL 0
#endif

#include "DebugHelper.h"

//#define DEBUG						1
#define DEBUGGER_PROCS_MAX			128
#define DEBUGGER_START_SIGNAL		SIGFPE

static volatile int _signalReceived = 0;

extern "C" void handleSignals(int sig)
{
	switch (sig)
	{
	case DEBUGGER_START_SIGNAL:
		break;
	case SIGHUP:
		exit(0);
	case SIGPIPE:
		break;
	default:
		break;
	}
#ifdef DEBUG
	fprintf(stderr, "DebugHelper: Received signal %d\n", sig);
#endif
	_signalReceived = sig;
}

int
DebugHelper::getNextPid(pid_t& nextPid, pid_t& pidIndex)
{
	int found = 0;

	DIR* d = opendir("/proc");
	if (!d)
	{
#ifdef DEBUG
		fprintf(stderr, "opendir(/proc) failed errno = %d", errno);
#endif
		return 0;
	}

	// Now walk the directory, and find the process
	for (;;)
	{
		dirent* ent = readdir(d);
		if (!ent)
			break;
		pid_t pid;
		char c;
		// look for just a number
		if (1 != sscanf(ent->d_name, "%d%c", &pid, &c))
			continue;
		if (pidIndex == 0)
			pidIndex = pid;
		if (pid == pidIndex)
		{
			// found it
			nextPid = pid;
			found = 1;
			break;
		}
	}

	// read next pid to return
	if (found == 1)
	{
		pidIndex++;
		pid_t pid;
		char c;
		dirent* ent = readdir(d);
		if (!ent)
		{
			if (1 == sscanf(ent->d_name, "%d%c", &pid, &c))
				pidIndex = pid;
		}
	}
	closedir(d);
	return found;
}

int
DebugHelper::getArg0(pid_t pid, char* argsBuffer, int argsLen)
{
	char cmdline[64];
	sprintf(cmdline, "/proc/%d/cmdline", pid);

	int fd = open(cmdline, O_RDONLY);
	if (fd < 0)
	{
		fprintf(stderr, "DebugHelper::getArg0 - failed to open %s errno = %d", cmdline, errno);
		return -1;
	}

	memset(argsBuffer, 0, argsLen);
	int n = read(fd, argsBuffer, argsLen);
	if (n < 0)
	{
		fprintf(stderr, "DebugHelper:: getarg0 - arguments longer than buffer");
		return -1;
	}
	close(fd);
	return 0;
}
//------------------------------------------------------------------------------
int
DebugHelper::waitForStartSignal(unsigned seconds)
{
	long giveUpTime = seconds * (1000000 / 10000);

	while (!_signalReceived)
	{
		usleep(10000);
		if (--giveUpTime < 0)
			break;
	}
#ifdef DEBUG
	fprintf(stderr, "DebugHelper::debugger - waitForStartSignal: return %d\n", _signalReceived);
#endif
	return _signalReceived;
}

void DebugHelper::attachDebugger()
{
#ifdef DEBUG
	char tmpf[128];
	sprintf(tmpf, "/tmp/.x10dbg%d", getpid());
	freopen(tmpf, "w+", stderr);
#endif
	pid_t my_pid;
	pid_t debugger_pid;
	char* sock_name[DEBUGGER_PROCS_MAX] = { 0 };
	int timeout;

//int debug = 1;
//while (debug)
//	sleep(1);
	const char* debugger_id = ::getenv(X10_DEBUGGER_ID);
	if (!debugger_id)
		return;		// No debugger available.

	const char* debugger_name = ::getenv(X10_DEBUGGER_NAME);
	if (debugger_name == 0)
		debugger_name = "gdia";

	sighandler_t old_FPE_handler  = signal(DEBUGGER_START_SIGNAL, handleSignals);
	sighandler_t old_PIPE_handler = signal(SIGPIPE, handleSignals);

	debugger_pid = strtol(debugger_id, 0, 10);
#ifdef DEBUG
	fprintf(stderr, "DebugHelper::debugger(%d) "X10_DEBUGGER_ID"=%d\n", getpid(), debugger_pid);
#endif
	int retry = 5;
	int s;

  try_again:
	pid_t i;
	pid_t nextPid;
	int n = 0;
	int last_n = -1;
	timeout = 0;
	do
	{
		int rc;
		char argbuf[1024];

		i = 0;
		for (;;) {
			if (getNextPid(nextPid, i) == 0)
				break;

			rc = getArg0(nextPid, argbuf, sizeof(argbuf));
			if (rc == 0) {
				char *filename = strrchr(argbuf, '/');
				if (filename && strncmp(filename+1, debugger_name, strlen(debugger_name)) == 0) {
					if (sock_name[n] == 0)
						sock_name[n] = (char *)malloc(64);
					sprintf(sock_name[n], "/tmp/.ptp.%s.%d.%d", debugger_name, debugger_pid, nextPid);
#ifdef DEBUG
					fprintf(stderr, "DebugHelper: debugger sock_name[%d]='%s'\n", n, sock_name[n]);
#endif
					if (++n >= 64)
					{
						fprintf(stderr, "DebugHelper::debugger - Too many debugger agents!!\n");
						break;
					}
				}
			}
		}
		/* Exit this loop iff n is stable. */
		if (n == last_n)
			break;
		last_n = n;
		n = 0;
		sleep(1);	/* Allow time to launch more debugger agents */
	} while (timeout < 120);
	if (n == 0)
	{
#ifdef DEBUG
		fprintf(stderr, "DebugHelper::debugger: no %s components found.\n", debugger_name);
#endif
		goto out;		// No debugger found.
	}
#ifdef DEBUG
	fprintf(stderr, "DebugHelper::debugger - found %d %s components\n", n, debugger_name);
#endif

	/* Time to find a receptive debugger agent ... */
	timeout = 0;
	s = -1;
	do
	{
		for (i = 0; i < n; i++) {
			if (access(sock_name[i], F_OK) == 0) {
				struct sockaddr_un un;
				int len;

				if ((s = socket(AF_UNIX, SOCK_STREAM, 0)) < 0)
				   continue;

				un.sun_family = AF_UNIX;
				strcpy(un.sun_path, sock_name[i]);
				len = sizeof(un.sun_family) + strlen(un.sun_path) + 1;
				if (connect(s, (struct sockaddr *)&un, len) == 0)
					goto send_pid;
#ifdef DEBUG
				fprintf(stderr, "DebugHelper::debugger - connect(%s) failed - error %s.\n", sock_name[i], strerror(errno));
#endif
				close(s);
				s = -1;
			}
		}
		sleep(1);
	} while (++timeout < 60);
	if (retry--)
	  goto try_again;

	for (i = 0; i < DEBUGGER_PROCS_MAX; i++)
		if (sock_name[i]) free(sock_name[i]);
	fprintf(stderr, "ERROR: DebugHelper::debugger agent connection timeout - error %s", strerror(errno));
	return;

send_pid:
#ifdef DEBUG
	fprintf(stderr, "DebugHelper::debugger - connected to %s via '%s'\n", debugger_name, sock_name[i]);
#endif
	my_pid = getpid() | 0x80000000;
#ifdef DEBUG
	fprintf(stderr, "DebugHelper::debugger - send pid %d\n", my_pid);
#endif
	if (send(s, &my_pid, sizeof(pid_t), MSG_NOSIGNAL) != sizeof(pid_t)) {
#ifdef DEBUG
		fprintf(stderr, "ERROR: DebugHelper::debugger - failed to send pid %d to debugger. error %s", my_pid, strerror(errno));
#endif
		close(s);
		// This shouldn't happen but it does. Apparently more than one pmdhelper can (almost) connect
		// to the same gdia. This is observed under SLES11 for sure.
		if (retry--)
		  goto try_again;
	}
	else
	{
		close(s);

		switch (waitForStartSignal(30))
		{
		case DEBUGGER_START_SIGNAL:
			break;
		case SIGPIPE:
	#ifdef DEBUG
			fprintf(stderr, "ERROR: SSIGPIPE received during waitForStartSignal(), retry %d\n", retry);
	#endif
			if (retry--)
				goto try_again;
			break;
		default:
	#ifdef DEBUG
			fprintf(stderr, "ERROR: Start signal %d not received!", DEBUGGER_START_SIGNAL);
	#endif
			if (retry--)
				goto try_again;
			break;
		}
	}
  out:
	signal(DEBUGGER_START_SIGNAL, old_FPE_handler);
	signal(SIGPIPE, old_PIPE_handler);

	for (int i = 0; i < DEBUGGER_PROCS_MAX; i++)
		if (sock_name[i]) free(sock_name[i]);
#ifdef DEBUG
	fprintf(stderr, "DebugHelper::debugger - return to caller...\n");
#endif
}
