#include "x10.h"
#include <assert.h>

int a[100];

typedef struct my_closure_t
{
  x10_async_closure_t base;
} my_closure_t;

void __x10_callback_asyncswitch(x10_async_closure_t* closure, x10_finish_record_t* frecord, x10_clock_t* clocks, int num_clocks)
{
  
}

x10_addr_t __x10_callback_array_copy_switch(x10_async_closure_t* closure)
{
  switch (closure->handler==1) {
  case 1 :
    return (x10_addr_t) a;
  }
}

int __xlc_upc_main(){}

int main()
{
  x10_init();

  printf ("hello world: %d %d \n", __x10_here, __x10_numplaces);
  
  if (__x10_here == 0) {
    
    my_closure_t closure;

    closure.base.handler = 1;
   
    int i;

    for (i = 0; i < 100; i++) a[i] = i;
    
    x10_comm_handle_t req = x10_async_array_put(1, (x10_addr_t) a, 
						 100 * sizeof(int),
						 (x10_async_closure_t*) &closure, 
						 sizeof(my_closure_t));    
    x10_async_spawn_wait (req);
    
  } else {
  
    x10_infinite_poll();
    int i;
    for (i= 0; i < 100; i++)
      assert (a[i] == i);
  }
  
  x10_finalize();

  return 0;
}
