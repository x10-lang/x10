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
	int accept(int fd);
	int connect(const char *host, unsigned port, unsigned retr);
	int connect(const char * hostport, unsigned retries);
	int getname(int fd, char * name, unsigned namelen);
}

#endif

