/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: gas.cc,v 1.1 2007-05-09 12:40:30 ganeshvb Exp $ */

#include <iostream> 
#include "gas.h"

using namespace std;
using namespace x10lib;

place_t 
x10lib::here ()
{
  int task; 
  LAPI_Qenv (GetHandle(), TASK_ID, &task); 
  return task;
}

int 
x10lib::numPlaces()
{ 
  int numTasks;
  LAPI_Qenv (GetHandle(), NUM_TASKS, &numTasks); 
  return numTasks;
}
