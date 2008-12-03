/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: queue.h,v 1.1.1.1 2008-05-20 13:46:46 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_QUEUE_H
#define __X10_QUEUE_H

struct AsyncQueueEl 
{
  void* _el;
  struct AsyncQueueEl *_next;
  struct AsyncQueueEl *_prev;
};

typedef struct AsyncQueueEl* x10_async_queue_el_t;

struct AsyncQueue
{
  x10_async_queue_el_t _head;
  x10_async_queue_el_t _tail;
};

typedef struct AsyncQueue* x10_async_queue_t;

x10_async_queue_t CreateQueue();

void DeleteQueue(x10_async_queue_t);

void PushQueue(x10_async_queue_t, void*);

x10_async_queue_el_t PopQueue(x10_async_queue_t);

void RemoveQueue(x10_async_queue_t, x10_async_queue_el_t);


#endif /* __X10_QUEUE_H */
