#include <sys/time.h>

double nanoTime()
{
  struct timeval time_v;
  gettimeofday ( &time_v, NULL );
  return (time_v.tv_sec * 1e6 +  time_v.tv_usec) * 1e3;
}
