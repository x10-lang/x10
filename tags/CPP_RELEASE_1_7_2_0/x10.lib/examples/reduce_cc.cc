/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * $Id: reduce_cc.cc,v 1.2 2007-12-10 16:44:38 ganeshvb Exp $ 
 */


/** Example to illustrate the usage of Reduce.
 ** Reduction is performed over an (logical) uniquely 
 ** distributed array whose values are 0:rank-1.
 ** The result is collected at place 0.
 **
 **/

#include <x10/x10lib.h>

#include <iostream>

using namespace std;
using namespace x10lib;

void add (int& result, const int& val)
{
   result += val;
}

int 
main (int argc, char* argv[])
{
  /* Initialize x10lib */
  x10lib::Init(NULL, 0);

  int val = __x10_my_place;
  Reduce <int> (&val);
  FinishReduceAll <int, add> ();

  if (__x10_my_place == 0) {
     long ref = __x10_num_places * (__x10_num_places - 1) / 2;
     cout << "result: " << val << " expected : " << ref << endl;
  }

  /* Fianlize x10lib */
  x10lib::Finalize();

  return 0;
}
