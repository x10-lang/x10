/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_array_table.cc,v 1.1 2007-11-28 19:11:18 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

int 
main (int argc, char* argv[])
{
  int N = 1024;
  
  x10lib::Init(NULL,0);
  
  array_info_t    a = new array_info;

  a->data = new double[N];
  a->nelements = N;
  a->elementSize = sizeof (double);
  a->rank = 1;
  a->sizes[0] = N;
  a->stride[0] = 1;
  
  registerArray (a, 1);

  assert (getLocalAddress (1) == a);

  registerArray (a, 2);

  assert (getLocalAddress (2) == a);


  registerArray (a, 128); //should throw assertion

  registerArray (a, 2); //should throw assertion

  assert (getLocalAddress(0) == NULL); 

  cout << "Test_array_table PASSED" << endl;
  
  x10lib::Finalize();
  
  return 0;
}
