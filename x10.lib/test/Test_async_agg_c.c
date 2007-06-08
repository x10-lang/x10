/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_agg_c.c,v 1.1 2007-06-08 13:51:42 srkodali Exp $ */

#include <stdio.h>
#include <x10/x10lib.h>

int m = 5;

async_arg_t I = 0;
async_arg_t K = 0;

void async0 (async_arg_t arg0)
{
  I += arg0 *  m;
  K++;
}

int asyncSwitch (async_handler_t h, void* arg, size_t size) 
{
  async_arg_t* args = (async_arg_t*) arg;
  int niter;
  switch (h) {
  case 0:
  niter = size / sizeof(async_arg_t);
  for (int i = 0; i < niter ;i ++)
    async0 (*args++);
  }
}



typedef void (*void_func_t) ();

int 
main (int argc, char* argv[])
{
  int N = 2048;

  x10_init(NULL,0);

   for (place_t target = 0; target < x10_num_places(); target++)
     for (int64_t i = 0; i < N; i++)
       x10_async_spawn_inline_agg1 (target, 0,  i);
     
   x10_async_flush (0);

     
  x10_gfence (); 
  printf ("%d I %d %d \n", x10_here(),  I,  K);
  assert (I == x10_num_places() * N * (N-1) / 2 * m);
  assert (K == x10_num_places() * N) ;

  printf("Test_async_agg PASSED\n");

  x10_finalize();

  return 0;
}
