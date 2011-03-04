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
#endif

#ifndef __sock_launcher_h__
#define __sock_launcher_h__

/* ************************************************************************ */
/*               public ProcManager interface                               */
/* ************************************************************************ */

#ifdef __cplusplus
extern "C"
{
#endif
	int Launcher_Init(int argc, char ** argv);
	int Launcher_myproc();
	int Launcher_nprocs();

#ifdef __cplusplus
}
#endif

// Environment variable names
#define X10LAUNCHER_MYID "X10LAUNCHER_MYID"
#define X10LAUNCHER_NPROCS "X10LAUNCHER_NPROCS"
#define X10LAUNCHER_HOSTFILE "X10LAUNCHER_HOSTFILE"
#define X10LAUNCHER_SSH "X10LAUNCHER_SSH"
#define X10LAUNCHER_PARENT "X10LAUNCHER_PARENT"

/* ************************************************************************ */
/*                ProcManager class definition                              */
/* ************************************************************************ */

#ifdef __cplusplus
namespace Sock
{
	class Launcher
	{
	public:

		static void Setup(int argc, char ** argv);
		static int myproc()
		{
			return _instance->_myproc;
		}
		static int nprocs()
		{
			return _instance->_nprocs;
		}

		static const char * lookup(const char * key, int proc);
		static void setHash(const char * key, int proc, const char * val);

	public:
		void finalize(void);

	protected:
		void * operator new(size_t, void * addr)
		{
			return addr;
		}

	protected:
		/* SockProcManager_Init.cc */

		Launcher();
		void initialize(int argc, char ** argv);
		void init_readHostFile(void);
		void dump() const;

		/* SockProcManager_SSH.cc */

		void startSSHclient(int childrank);

		/* SockProcManager.cc */

		void startChildren(void);
		void pm_loop();
		int pm_makeFDSets(fd_set *, fd_set *, fd_set *);
		void pm_connect(void); /* connect to parent */
		void pm_disconnect(void); /* disconnect children */

		void cb_ctrl_newChild(void); /* new child */
		void cb_ctrl_deadChild(int childno); /* child disconnected */
		void cb_ctrl_deadParent(void); /* parent disconnected */
		void cb_cout_child(int childno); /* console from child */
		void cb_cerr_child(int childno); /* stderr from child */

		/* SockProcManager_ctrl.cc: control message traffic */

		void cb_ctrl_child(int childno); /* ctrl msg from child */
		void cb_ctrl_parent(void); /* ctrl msg from parent */

		static int ctrl_msg_write(int fd, const char *, int, const char *);
		static int ctrl_msg_read(int fd, char *, int, int *, char *, int);

		/* SockProcManager_hash.cc: local hash table */

		void hash_init();
		void hash_set(const char * key, int proc, const char * val);

		static void cb_sighandler_cld(int signo);

	protected:
		static void DIE(const char * message, ...);

	private:

		/* startup parameters */

		char ** _argv; /* argv copied from init */
		int _argc; /* argc copied from init */
		char _realpath[512]; /* real path of executable */
		char _ssh_command[64]; /* the SSH command. */
		char _hostfname[256]; /* host file name */
		int _nprocs; /* number of processors in job */

		/* parent child structure */
		char ** _hostlist; /* child host list */
//		char ** _execlist; /* child executable list */
		int _myproc; /* my processor ID */
		int _firstchildproc; /* the ID of the first child */
		int _numchildren; /* number of children in this node */

		/* process manager main loop */
		int * _pidlst; /* list of all spawned pids */
//		bool * _isLeaf; /* leaves: non-processmanager children */
//		bool _keepalive; /* stop when this turns off */

		/* control channels */
		int _ctrlfdp; /* parent control connection */
		int _listenFD; /* listener - for children */
		int * _ctrlfd; /* children's control connections */
		int * _coutfd;
		int * _cerrfd;

		/* singleton */
		static Launcher * _instance;
	};
} /* end namespace */

#endif /* __cplusplus */
#endif /* #ifdef */

