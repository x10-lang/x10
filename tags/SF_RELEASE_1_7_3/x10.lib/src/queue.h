/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: queue.h,v 1.1 2008-02-15 09:49:27 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Progress operations interface. **/

#ifndef __X10_QUEUE_H
#define __X10_QUEUE_H

#include <x10/async_closure.h>

/* C++ Lang Interface */
#ifdef __cplusplus

struct AsyncQueueEl {
  x10_async_closure_t _closure;
  AsyncQueueEl *_next;
  AsyncQueueEl *_prev;
};

typedef AsyncQueueEl* x10_async_queue_el_t;

struct AsyncQueue{
  x10_async_queue_el_t _head;
  x10_async_queue_el_t _tail;
};

typedef AsyncQueue* x10_async_queue_t;

namespace x10lib {
  
  x10_async_queue_t CreateQueue ();
  
  void DeleteQueue (x10_async_queue_t);
  
  void PushQueue (x10_async_queue_t, x10_async_closure_t);
  
  x10_async_closure_t PopQueue (x10_async_queue_t);

  void RemoveQueue (x10_async_queue_t, x10_async_queue_el_t);

  void FlushQueue (x10_async_queue_t q);

  void CheckQueue (x10_async_queue_t q);

  bool IsEmpty (x10_async_queue_t q);

} /* closing brace for namespace x10lib */


#endif


#endif /* __X10_QUEUE_H */
