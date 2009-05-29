#include "simple.h"
#include "simple-f-defs.h"

#define DEBUG 0

extern void f_init();

void *SIMPLE_main(THREADED)
{
  int *fth;

  fth = (int *)malloc(FTHSIZE*sizeof(int));
  assert_malloc(fth);
  
  fth[0] = (int)TH;      /* ti */
  fth[1] = MYTHREAD+1;   /* mythread */
  fth[2] = THREADS;      /* threads */
#ifndef SMPONLY
  fth[3] = ID;           /* id */
  fth[4] = TID;          /* tid */
#else
  fth[3] = MYTHREAD+1;
  fth[4] = THREADS;
#endif
  fth[5] = 0;            /* m1 */
  fth[6] = 0;            /* m2 */
  fth[7] = 0;            /* blk */
  
#if DEBUG
  fprintf(outfile,"T(%3d): SIMPLE_main()\n",MYTHREAD);
  fflush(outfile);
#endif

  node_Barrier();

  f_init(); /* Initialize Fortran I/O Library */

  fsmain_(fth);

  /*******************************/
  /* End of program              */
  /*******************************/
  
  node_Barrier();
  free(fth);
  SIMPLE_done(TH);
}

