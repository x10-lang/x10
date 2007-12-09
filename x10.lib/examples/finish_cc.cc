/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * $Id: finish_cc.cc,v 1.1 2007-12-09 11:00:54 ganeshvb Exp $ 
 */


/** Example to illustrate the usage of finishStart and finishEnd
 **
 ** An inlinable async is spawned from place zero on other places.
 ** The async sends the rank of destination ("val") to the destination.
 **
 ** Followed by this, another async is spawned from non-zero places
 ** to place zero. This async sends back the "val" to place zero.
 ** Place zero cumulatively adds the "val" to "sum".
 **
 **/

#include <x10/x10lib.h>

#include <iostream>

using namespace std;
using namespace x10lib;

long val;
long sum = 0;


/* async switch routine */
void asyncSwitch (x10_async_handler_t h, void* arg, int niter) 
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

  int CS = 0;

  if (__x10_my_place == 0) CS = 1;
  CS = finishStart (CS);
  if (CS != 1) goto SKIP_1;

  if (__x10_my_place != 0) goto SKIP_s1;
   /* brodcast val from 0 to all other places */
    for (x10_place_t target = 0; target < __x10_num_places; target++)
       if (target != __x10_my_place) {
         long i = (long) target; 
         asyncSpawnInline (target, 0, &i, sizeof(i));
       }
SKIP_s1:
SKIP_1:
    finishEnd (NULL);
    CS = 0;

  if (__x10_my_place == 0) CS = 2;
  CS = finishStart(CS);
  if (CS != 2) goto SKIP_2;

  /* reduce "val" from all the places to 0 in sum */
    asyncSpawnInline (0, 1, &val, sizeof(val)); 
SKIP_2:
     finishEnd (NULL);
     CS = 0;
  //cout << "Hello " << endl;
  if (__x10_my_place == 0) {
     long ref = __x10_num_places * (__x10_num_places - 1) / 2;
     cout << "result: " << sum << " expected : " << ref << endl;
  }

  x10lib::SyncGlobal();
  /* Fianlize x10lib */
  x10lib::Finalize();

  return 0;
}
