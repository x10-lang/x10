/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: gas.cc,v 1.2 2007-05-17 09:48:52 ganeshvb Exp $ */

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
