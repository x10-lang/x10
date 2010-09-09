#include "x10.h"
#include <assert.h>
#include "queue.h"

int __xlc_upc_main() {}

int test_queue()
{
  x10_async_queue_t queue = CreateQueue();
  
  int* el0 = (int*) malloc(sizeof(int));
  
  PushQueue(queue, el0);

  x10_async_queue_el_t val = PopQueue(queue);

  assert (val->_el == el0);

  val = PopQueue(queue);
  assert (val == NULL);

  int* el1 = (int*) malloc(sizeof(int));
  PushQueue(queue,el1);

  int* el2 = (int*) malloc(sizeof(int));
  PushQueue(queue, el2);
  
  RemoveQueue(queue, queue->_tail);
  val = PopQueue (queue);

  assert (val->_el == el1);
  
  val = PopQueue (queue);
  assert (val == NULL);
}
