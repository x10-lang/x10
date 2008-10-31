/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: timers.cc,v 1.1 2007-08-02 12:59:19 srkodali Exp $
 * This file is part of X10 Applications.
 */

#include <sys/time.h>

double nanoTime()
{
  struct timeval time_v;
  gettimeofday ( &time_v, NULL );
  return (time_v.tv_sec * 1e6 +  time_v.tv_usec) * 1e3;
}
