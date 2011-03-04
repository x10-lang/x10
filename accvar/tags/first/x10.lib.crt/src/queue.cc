/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: queue.cc,v 1.1.1.1 2008-05-20 13:46:46 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for X10Lib's AsyncQueue interface. **/

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include "queue.h"

x10_async_queue_t 
CreateQueue()
{
  x10_async_queue_t ret = (struct AsyncQueue*) malloc(sizeof(struct AsyncQueue));
  ret->_head = ret->_tail = NULL;
  return ret;
}

void 
DeleteQueue(x10_async_queue_t q)
{
  x10_async_queue_el_t cur;
  
  for(cur = q->_head; cur != NULL;)
    {
      x10_async_queue_el_t prev = cur;
      cur = cur->_next;
      free(prev);
    }
  
 free(q);
}

void 
PushQueue (x10_async_queue_t q, void* element)
{
  x10_async_queue_el_t  queue_el= (struct AsyncQueueEl*) malloc(sizeof(struct AsyncQueueEl));
  queue_el->_el = element;
  queue_el->_next = NULL;
  queue_el->_prev =  q->_tail;
  
  if (!q->_tail) {
    q->_head = queue_el;
    q->_tail = queue_el;
    return;
  }
  
  q->_tail->_next = queue_el;
  
  q->_tail = queue_el;
  
}

x10_async_queue_el_t
PopQueue (x10_async_queue_t q)
{
  if (q->_head == NULL) return NULL;
  x10_async_queue_el_t tmp = q->_head;
  if (q->_head->_next) q->_head->_next->_prev = NULL;
  if (q->_head == q->_tail) q->_tail = NULL;
  q->_head = q->_head->_next;
  return tmp;
}

void 
RemoveQueue (x10_async_queue_t q, x10_async_queue_el_t el) 
{
  if (el->_prev) el->_prev->_next = el->_next;
  if (el->_next) el->_next->_prev = el->_prev;
  
  if (el == q->_head) q->_head = el->_next;
  if (el == q->_tail) q->_tail = el->_prev;
}




