/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: gas.cpp,v 1.1 2007-04-28 07:03:45 ganeshvb Exp $ */

#include "gas.h"

using namespace std;
using namespace x10lib;

#define SM_GLOBAL_SIZE 2^34

char SM_GLOBAL[SM_GLOBAL_SIZE];

int SM_GLOBAL_OFFSET = 0;

char* 
x10lib::mallocSMGlobal (size_t nbytes)
{
  char* ret = SM_GLOBAL + SM_GLOBAL_OFFSET;

  SM_GLOBAL_OFFSET += nbytes;
    
  return ret;
}

place_t 
x10lib::here ()
{
  return 0;
}

int 
x10lib::numPlaces()
{
  return 1;
}
