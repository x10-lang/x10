#ifndef _SIMPLE_F_H
#define _SIMPLE_F_H
#include "simple-f-defs.h"

#ifndef SMPONLY
#include "umd-f.h"
#endif

#define fthreaded integer fth(FTHSIZE)

#define MYTHREAD fth(2)
#define THREADS  fth(3)
#define ID       fth(4)
#define TID      fth(5)
#define _M1      fth(6)
#define _M2      fth(7)
#define _BLK     fth(8)

#define on_one_thread if (MYTHREAD .eq. 1) then
#define on_thread(k) if (MYTHREAD .eq. k) then
#ifdef SMPONLY
#define on_one on_one_node on_one_thread
#else
#define on_one on_one_thread
#endif

#define task_do(x)  (MYTHREAD  .eq.  ((x) % THREADS))

#endif
