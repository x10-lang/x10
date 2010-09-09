/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * $Id: async_cc.cc,v 1.3 2007-12-10 16:44:37 ganeshvb Exp $ 
 */


/** Example to illustrate the usage of AsyncSpawnInline and SyncGlobal.
 **
 ** An inlinable async is spawned from place zero on other places.
 ** The async sends the rank of destination ("val") to the destination.
 ** A global synchronization is performed after this using SyncGlobal.
 ** SyncGlobal is a wrapper over LAPI_Gfence. 
 **
 ** Followed by this, another async is spawned from non-zero places
 ** to place zero. This async sends back the "val" to place zero.
 ** Place zero cumulatively adds the "val" to "sum".
 **
 ** After another global synchronization, the "sum" is printed
 ** together with the expected output on place zero.
 **/

#include <x10/x10lib.h>

#include <iostream>

using namespace std;
using namespace x10lib;

long val;
long sum = 0;

/* async switch routine */
extern "C"
void AsyncSwitch (x10_async_handler_t h, void* arg, int niter) 
{
  switch (h) {
   case 0:
     val = *((long*) arg);
     break;
   case 1:
      sum += *((long*) arg);
      break;
  }
}

int 
main (int argc, char* argv[])
{
  /* Initialize x10lib */
  x10lib::Init(NULL, 0);

  int my_place = x10lib::here();
  int num_places = x10lib::numPlaces();

  /* brodcast val from 0 to all other places */
  if (my_place == 0) {
    for (x10_place_t target = 0; target < num_places; target++)
       if (target != my_place) {
         long i = (long) target; 
         AsyncSpawnInline (target, 0, &i, sizeof(i));
       }
  }

  x10lib::SyncGlobal (); 

  /* reduce "val" from all the places to 0 in sum */
  if (my_place != 0) {
    x10lib::AsyncSpawnInline (0, 1, &val, sizeof(val)); 
  }

  x10lib::SyncGlobal (); 

  if (my_place == 0) {
     long ref = num_places * (num_places - 1) / 2;
     cout << "result: " << sum << " expected : " << ref << endl;
  }

  /* Fianlize x10lib */
  x10lib::Finalize();

  return 0;
}
