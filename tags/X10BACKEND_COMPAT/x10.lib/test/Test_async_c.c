/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_c.c,v 1.2 2007-06-18 10:33:48 ganeshvb Exp $ */

#include <x10/x10lib.h>

func_t handlers[128];

void async0 (async_arg_t arg)
{
  assert (arg == 333);
}

int asyncSwitch (async_handler_t h, void* args, int niter)
{
  switch (h) {
   case 0:
     async0 (*((async_arg_t*) args));
     break;
  }
}

int 
main (int argc, char* argv[])
{

  x10_init(NULL, 0);

  if (x10_here() == 0)
    for (place_t target = 0; target < x10_num_places(); target++)
       x10_async_spawn_inline (target, 0, 1, 333);

  x10_gfence (); 

  printf ("Test_async_c PASSED\n");

  x10_finalize();

  return 0;
}
