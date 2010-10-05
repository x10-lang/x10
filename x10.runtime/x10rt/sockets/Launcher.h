/* ************************************************************************ */
/* ************************************************************************ */
#include <stdlib.h>
#include <stdarg.h>
#include <sys/select.h>

/* ************************************************************************ */
/*            some explicit declarations for strict ANSI mode               */
/* ************************************************************************ */
#ifdef __CYGWIN__
extern "C" int vsnprintf(char *, size_t, const char *, va_list); 
extern "C" char *realpath (const char *, char *);
extern "C" int setenv (const char *, const char *, int);
extern "C" int unsetenv (const char *);
extern "C" int fileno (FILE *__stream);
#endif

#ifndef __sock_launcher_h__
#define __sock_launcher_h__

/* ************************************************************************ */
/*               public ProcManager interface                               */
/* ************************************************************************ */

// Environment variable names
#define X10LAUNCHER_MYID "X10LAUNCHER_MYID" // a number for the "place" of this process.  Set by the launcher.
#define X10LAUNCHER_NPROCS "X10LAUNCHER_NPROCS" // the number of places in this process
#define X10LAUNCHER_HOSTFILE "X10LAUNCHER_HOSTFILE" // full path name of a file containing a list of hostnames
#define X10LAUNCHER_SSH "X10LAUNCHER_SSH" // the ssh command.  This doesn't normally need to be set.
#define X10LAUNCHER_PARENT "X10LAUNCHER_PARENT" // the hostname:port of the parent launcher.  This is set by the launcher.
#define X10LAUNCHER_RUNTIME "X10LAUNCHER_RUNTIME" // this is a flag to run as a runtime or a launcher.  Set by the launcher.
#define X10LAUNCHER_CWD "X10LAUNCHER_CWD" // the working directory of the program

// Enable/disable debug information
//#define DEBUG 1

enum CTRL_MSG_TYPE {HELLO, GOODBYE, PORT_REQUEST, PORT_RESPONSE};
struct ctrl_msg
{
	CTRL_MSG_TYPE type;
	uint32_t to;
	uint32_t from;
	int datalen;
	// followed by the data
};

/* ************************************************************************ */
/*                ProcManager class definition                              */
/* ************************************************************************ */

class Launcher
{
	public:
		static void Setup(int argc, char ** argv);
		static void cb_sighandler_cld(int signo);
		static int lookupPlace(uint32_t myPlace, uint32_t destPlace, char* response, int responseLen);
		static int setPort(uint32_t place, char* port);

	protected:
		/* SockProcManager_Init.cc */
		Launcher();
		void initialize(int argc, char ** argv);
		void readHostFile(void);
		void startSSHclient(uint32_t childrank, char* masterPort, char* remotehost);

		/* SockProcManager.cc */
		void startChildren(void);
		void handleRequestsLoop();
		int makeFDSets(fd_set *, fd_set *, fd_set *);
		void connectToParentLauncher(void); /* connect to parent */
		void handleNewChildConnection(void); /* new child */
		bool handleDeadChild(uint32_t childno, int type); /* child disconnected */
		bool handleDeadParent(void); /* parent disconnected */
		bool handleChildCout(int childno); /* console from child */
		bool handleChildCerror(int childno); /* stderr from child */
		int handleControlMessage(int fd); /* incoming ctrl msg */
		int forwardMessage(struct ctrl_msg* message, char* data); /* move data around */
		static void DIE(const char * message, ...);
		void * operator new(size_t, void * addr){return addr;}

	private:
		static Launcher * _singleton;
		static int _parentLauncherControlLink; /* parent control connection */

		/* startup parameters */
		char ** _argv; /* argv copied from init */
		int _argc; /* argc copied from init */
		char _realpath[512]; /* real path of executable */
		char _ssh_command[64]; /* the SSH command. */
		char _hostfname[512]; /* host file name */
		uint32_t _nplaces; /* number of processors in job */
		uint32_t _myproc; /* my processor ID */

		/* parent child structure */
		char ** _hostlist; /* child host list */
		char* _runtimePort; /* the host:port number of the associated x10 runtime's listen port */
		uint32_t _firstchildproc; /* the ID of the first child launcher */
		uint32_t _numchildren; /* number of launcher children in this node */
		int * _pidlst; /* list of all spawned pids */
		int _listenSocket; /* listener - for children */
		int * _childControlLinks; /* children's control connections */
		int * _childCoutLinks; /* children's cout connections */
		int * _childCerrorLinks; /* children's cerror connections */
};
#endif /* #ifdef */

