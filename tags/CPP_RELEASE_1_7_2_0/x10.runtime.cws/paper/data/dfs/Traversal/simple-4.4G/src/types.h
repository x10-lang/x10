#ifndef _TYPES_H
#define _TYPES_H

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/time.h>		/* struct timeval */
#include <sys/resource.h>
#include <math.h>
#include <string.h>
#include "mach_def.h"

#define MAXLEN             80

void assert_malloc(void *ptr);

enum reduce_tag {MAX, MIN, SUM, PROD, LAND, BAND, LOR, BOR, LXOR, BXOR};
typedef int reduce_t;

#define max(a,b)   ((a) > (b) ? (a) : (b))
#define min(a,b)   ((a) < (b) ? (a) : (b))

#ifdef SOLARIS
#ifndef volatile
#define volatile
#endif
#include <sys/errno.h>
#endif

extern FILE         *outfile;

int log_2(int number); /* log base 2 of number */
int ilog2(int); /* the bit position of the right-most 1 */

#if (_MACH_LOG2 == FALSE)
#define log2(d) (log(d)/log(2.0))
#endif

#if (_MACH_CLOCK == _MACH_CLOCK_GETCLOCK)
struct timespec tp;
#define get_seconds()   (getclock(TIMEOFDAY, &tp), \
                        (double)tp.tv_sec + (double)tp.tv_nsec / 1000000000.0)

#define get_nseconds()  (getclock(TIMEOFDAY, &tp), \
                        (double)(1000000000*tp.tv_sec) + (double)tp.tv_nsec)
#elif (_MACH_CLOCK == _MACH_CLOCK_GETTIME)
double get_seconds();
#endif

void
main_get_args(int, char **);
  
#endif
