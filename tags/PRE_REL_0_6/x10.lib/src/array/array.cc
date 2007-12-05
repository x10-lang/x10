/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array.cc,v 1.12 2007-11-13 05:28:50 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** helper functions (internal) for array classe */

#include <iostream>
#include <x10/alloc.h>
using namespace std;

namespace x10lib
{
  Allocator* GlobalSMAlloc = NULL;
}

void 
arrayInit ()
{
  x10lib::GlobalSMAlloc = new x10lib::Allocator (1UL<<5);
}
