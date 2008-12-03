/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_agg_c.c,v 1.5 2008-01-06 03:28:51 ganeshvb Exp $ */

#include <stdio.h>
#include <x10/x10lib.h>

int m = 5;

x10_async_arg_t I = 0;
x10_async_arg_t K = 0;

void async0 (x10_async_arg_t arg0)
{
  I += arg0 *  m;
  K++;
}

void AsyncSwitch (x10_async_handler_t h, void* arg, int niter)
{
  x10_async_arg_t* args = (x10_async_arg_t*) arg;
  switch (h) {
  case 0:
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

   for (x10_place_t target = 0; target < x10_num_places(); target++)
     for (int64_t i = 0; i < N; i++)
       x10_async_spawn_inline_agg1 (target, 0,  i);
     
   x10_async_agg_flush (0, sizeof(x10_async_arg_t));

     
  x10_sync_global (); 
  assert (I == x10_num_places() * N * (N-1) / 2 * m);
  assert (K == x10_num_places() * N) ;

  printf("Test_async_agg_c PASSED\n");

  x10_finalize();

  return 0;
}
