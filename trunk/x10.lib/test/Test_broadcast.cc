/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_broadcast.cc,v 1.2 2007-12-07 14:31:11 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;


struct 
{
  int idx;
  double value;
} args;

int 
main (int argc, char* argv[])
{

  x10lib::Init(NULL, 0);

  if (x10lib::here() == 0) {
     args.idx = 1;
     args.value = 3.14;
  }

  x10lib::Broadcast (&args, sizeof(args));

  x10lib::SyncGlobal ();

//  cout << args.value << " " << args.idx << endl;
  assert (args.value == 3.14 && args.idx == 1);

  cout << "Test_broadcast PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
