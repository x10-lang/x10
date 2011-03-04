#ifndef __tcp_h__
#define __tcp_h__

/* ************************************************************************* */
/*                   basic TCP functions                                     */
/* ************************************************************************* */

namespace TCP
{
	void FATAL(const char * msg);
	int read(int fd, void * p, unsigned cnt);
	int write(int fd, void * p, unsigned cnt);
	int listen(unsigned * port, unsigned backlog);
	int accept(int fd, bool noDelay);
	int connect(const char *host, unsigned port, unsigned retr, bool noDelay);
	int connect(const char * hostport, unsigned retries, bool noDelay);
	int getname(int fd, char * name, unsigned namelen);
}

#endif

