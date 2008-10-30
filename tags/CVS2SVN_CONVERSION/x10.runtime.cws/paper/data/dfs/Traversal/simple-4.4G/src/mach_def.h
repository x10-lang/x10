#ifndef _MACH_DEF_H
#define _MACH_DEF_H

#define FALSE 0
#define TRUE  1

#define _MACH_CLOCK_GETCLOCK 0
#define _MACH_CLOCK_GETTIME  1
#define _MACH_SELECT_CHAR    0
#define _MACH_SELECT_FD      1
#define _MACH_THREAD_DEF     0
#define _MACH_THREAD_SYS     1
#define _MACH_SCHED_1003_4a  0
#define _MACH_SCHED_1003_1b  1
#define _MACH_SCHED_1003_1c  2
#define _MACH_BAR_SYNC       0
#define _MACH_BAR_TREE       1

#define _MACH_BAR     _MACH_BAR_SYNC


#if defined(SOLARIS)
/* #include <sys/types.h> */
#define _MACH_SYSINFO TRUE
#endif

#if defined(AXP)||defined(TCS)
#define _MACH_CLOCK   _MACH_CLOCK_GETCLOCK
#define _MACH_LOG2    TRUE
#define _MACH_SELECT  _MACH_SELECT_CHAR
#define _MACH_THREAD  _MACH_THREAD_SYS
#define _MACH_LOAD    TRUE
#define _MACH_SYSINFO TRUE
#define _MACH_SCHED   _MACH_SCHED_1003_1c
#endif

#if defined(AIX)
#define _MACH_SYSINFO TRUE
#define _MACH_SCHED   _MACH_SCHED_1003_1c
#define _MACH_SCHED_I <sys/sched.h>
#define PRI_FIFO_MIN   PRIORITY_MIN
#define PRI_FIFO_MAX   PRIORITY_MAX
#endif

#if defined(SGI)
#define _MACH_SYSINFO TRUE
#endif

#if defined(FREEBSD)
#define sched_yield() pthread_yield(NULL)
#endif

#if defined(LINUX)
#endif

/************* DEFAULT MACHINE PARAMETER ******/
#ifndef _MACH_CLOCK
#define _MACH_CLOCK   _MACH_CLOCK_GETTIME
#endif

#ifndef _MACH_LOG2
#define _MACH_LOG2    FALSE
#endif

#ifndef _MACH_SELECT
#define _MACH_SELECT  _MACH_SELECT_FD
#endif

#ifndef _MACH_THREAD
#define _MACH_THREAD  _MACH_THREAD_DEF
#endif

#ifndef _MACH_LOAD
#define _MACH_LOAD    FALSE
#endif

#ifndef _MACH_SYSINFO
#define _MACH_SYSINFO FALSE
#endif

#ifndef _MACH_SCHED
#define _MACH_SCHED   _MACH_SCHED_1003_1b
#endif

#ifndef _MACH_SCHED_I
#define _MACH_SCHED_I <sched.h>
#endif
/************* DEFAULT MACHINE PARAMETER ******/

#endif
