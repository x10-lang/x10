/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *
 *  This file was written by Ben Herta for IBM: bherta@us.ibm.com
 */
#ifndef __tcp_h__
#define __tcp_h__

/* ************************************************************************* */
/*                   basic TCP functions                                     */
/* ************************************************************************* */

namespace TCP
{
	int read(int fd, void * p, unsigned cnt);
	int write(int fd, void * p, unsigned cnt);
	int listen(unsigned * port, unsigned backlog);
	int accept(int fd, bool noDelay);
	int connect(const char *host, unsigned port, unsigned retr, bool noDelay);
	int connect(const char * hostport, unsigned retries, bool noDelay);
	int getname(int fd, char * name, unsigned namelen);
}

#endif

