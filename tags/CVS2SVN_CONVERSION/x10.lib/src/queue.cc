/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: queue.cc,v 1.2 2008-02-19 07:12:49 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for X10Lib's AsyncQueue interface. **/

#include <iostream>

#include <err.h>
#include <queue.h>
#include <xthreads.h>

using namespace std;

namespace x10lib {
  
  x10_async_queue_t 
  CreateQueue()
  {
    x10_async_queue_t ret = new AsyncQueue;
    ret->_head = ret->_tail = NULL;
    return ret;
  }
  
  void 
  DeleteQueue(x10_async_queue_t q)
  {
    for (x10_async_queue_el_t cur = q->_head; cur != NULL;)
      {
	x10_async_queue_el_t prev = cur;
	cur = cur->_next;
	delete prev;
      }
    delete q;
  }
  
  void 
  PushQueue (x10_async_queue_t q, x10_async_closure_t closure)
  {

    x10_async_queue_el_t el = new AsyncQueueEl;
    el->_closure = closure;
    el->_next = NULL;
    el->_prev =  q->_tail;
    
    if (!q->_tail) {
      q->_head = el;
      q->_tail = el; 
      return;
    }
    
    q->_tail->_next = el;
  
    q->_tail = el;
  
  }
  
  x10_async_closure_t 
  PopQueue (x10_async_queue_t q)
  {
    assert (q->head);
    x10_async_queue_el_t tmp = q->_head;
    if (q->_head->_next) q->_head->_next->_prev = NULL;
    if (q->_head == q->_tail) q->_tail = NULL;
    q->_head = q->_head->_next;
    return tmp->_closure;
  }
  
  void 
  RemoveQueue (x10_async_queue_t q, x10_async_queue_el_t el) 
  {
    if (el->_prev) el->_prev->_next = el->_next;
    if (el->_next) el->_next->_prev = el->_prev;
    
    if (el == q->_head) q->_head = el->_next;
    if (el == q->_tail) q->_tail = el->_prev;
  }
  
  void 
  CheckQueue (x10_async_queue_t q)
  {
    for (volatile x10_async_queue_el_t cur = q->_head; cur != NULL; cur=cur->_next)
      {
	if (cur->_closure->cond()) {
	  RemoveQueue(q, cur);
	  x10_async_closure_t cl = cur->_closure;
	  x10_thread_run (cl);
	  if (cl->_done) delete cl;
	}
      }
  }
  
  void 
  FlushQueue (x10_async_queue_t q)
  {    
    for (volatile x10_async_queue_el_t cur = q->_head; cur != NULL; cur=cur->_next)
      {
	x10_async_closure_t cl = PopQueue (q);
	x10_thread_run (cl);
	if (cl->_done) delete cl;
      }
  }
  
  bool IsEmpty (x10_async_queue_t q)
  {
    return q->_head == NULL;
  }
  
}

x10_async_queue_t ReadyQueue;

x10_async_queue_t WaitQueue;

x10_err_t 
QueueInit () 
{
  ReadyQueue = x10lib::CreateQueue();
  WaitQueue = x10lib::CreateQueue();
}
