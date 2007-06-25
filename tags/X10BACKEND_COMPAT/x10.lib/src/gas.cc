/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: gas.cc,v 1.4 2007-06-15 22:19:38 ipeshansky Exp $ */

#include <iostream> 
#include "gas.h"

using namespace std;
using namespace x10lib;

place_t 
x10lib::here ()
{
  //int task; 
  //LAPI_Qenv (GetHandle(), TASK_ID, &task); 
  return ID;
}

int 
x10lib::numPlaces()
{ 
  //int numTasks;
  //LAPI_Qenv (GetHandle(), NUM_TASKS, &numTasks); 
  //cout << x10lib::MAX_PLACES << " " << numTasks << endl;
  return  MAX_PLACES;
}

extern "C"
place_t
x10_here()
{
  return  x10lib::here();
}

extern "C"
int
x10_num_places()
{
  return x10lib::numPlaces();
}

place_t x10lib::ID;

int x10lib::MAX_PLACES;

Allocator* x10lib::GlobalSMAlloc;

func_t* x10lib::handlerTable;

