/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_array_table.cc,v 1.2 2007-12-10 16:44:40 ganeshvb Exp $ 
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
  
  GenericArray* a = new GenericArray;
  a->_data = (char*) new double[N];
  a->_nelements = N;
  a->_elsize = sizeof (double);
  a->_rank = 1;
  a->_origin[0] = 0;
  a->_diagonal[0] = N-1;
  a->_stride[0] = 1;
  
  registerLocalSection (a, 1);

  assert (getLocalSection (1) == a);

  registerLocalSection (a, 2);

  assert (getLocalSection (2) == a);

  registerLocalSection (a, 128); //should throw assertion

  registerLocalSection (a, 2); //should throw assertion

  assert (getLocalSection(0) == NULL); 

  freeLocalSection (2);

  assert (getLocalSection (2) == NULL);

  cout << "Test_array_table PASSED" << endl;
  
  x10lib::Finalize();
  
  return 0;
}
