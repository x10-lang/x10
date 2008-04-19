/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_gen.cc,v 1.4 2008-04-19 06:12:17 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

char STACK[8192];

int sum = 0;

struct __async_closure_0 : public AsyncClosure
{
 __async_closure_0 (int arg1, char* stack, size_t stack_sz) :
      AsyncClosure (0, stack, stack_sz),
      _arg1 (arg1) {

     x10_thread_create;

    }
  
  virtual void run ()
   {
     int h = 1;

     int *a;
     a = new int;
     *a = __x10_my_place;

     //cout << "point 0" << endl;

     x10_thread_yield;

     //cout << "point 1" << endl;

     x10_thread_libcall4 (x10lib::AsyncSpawnInline, (__x10_my_place+1)%__x10_num_places, h, a, sizeof(int));

     x10_thread_wait(0);    
      
     //cout << "point 2" << endl;

     sum++;
    
     x10_thread_free;
   }

  virtual bool cond ()
   {
//      switch (this->_cond_number) {
  //      case 0 : return true;
  //    }
//
    return true;
   }
  int _arg1;
};


void AsyncSwitch (x10_async_handler_t h, void* arg, int niter) 
{
  int sp;
  x10_async_arg_t* args = (x10_async_arg_t*) arg;
  switch (h) {
   case 0: {
     __async_closure_0* cl = new __async_closure_0(*args, STACK, 8192);
     x10_thread_run(cl);
    // cl->run();
    // cout << "after run " << endl;
     break;
    }
   case 1:
      sum++;
      break; 
  }
}

int 
main (int argc, char* argv[])
{
  x10lib::Init(NULL, 0);

  x10_async_arg_t a = 333;
  if (__x10_my_place== 0)
    for (x10_place_t target = 0; target < __x10_num_places; target++)
       AsyncSpawnInline (target, 0, &a, sizeof(a));

  /* spawn depth is two for this program */

  for (int i = 0; i < 2; i++) {
  	x10lib::FlushQueue (ReadyQueue);
 	x10lib::CheckQueue (WaitQueue);
 	x10lib::SyncGlobal (); 
  }


  assert (sum == 2);

  cout << "Test_async_gen PASSED"  <<  endl;

  x10lib::Finalize();

  return 0;
}
