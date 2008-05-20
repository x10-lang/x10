#include "x10.h"

typedef struct 
{
  x10_async_closure_t base;
  int magic_number;
} my_closure_t;

int done = 0;

void __x10_callback_asyncswitch (x10_async_closure_t* closure, 
				 x10_finish_record_t* frecord, 
				 x10_clock_t* clocks, 
				 int num_clocks)
{
  my_closure_t* my_closure = (my_closure_t*) closure;
  switch (my_closure->base.handler) {
  case 1:
    printf ("hello world (%d)\n", my_closure->magic_number);    
    x10_finish_child(frecord, NULL, 0);
    break;
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
    closure.magic_number = 1729;
    
    x10_finish_record_t frecord;
    
    x10_finish_begin_global(&frecord, NULL, NULL, 0, 0);
    
    int i, tmp;
    
    for (i = 0; i < 100; ++i)
      {
	x10_comm_handle_t req = x10_async_spawn(1, (x10_async_closure_t*) &closure, sizeof(closure), &frecord, NULL, 0);
	
	x10_async_spawn_wait(req);
      }
    
    x10_finish_end(&frecord, &tmp);
    
    closure.magic_number = 1730;
    
    x10_finish_begin_global(&frecord, NULL, NULL, 0, 0);
    
    for (i = 0; i < 100; ++i)
      {
	x10_comm_handle_t req = x10_async_spawn(1, (x10_async_closure_t*) &closure, sizeof(closure), &frecord,  NULL, 0);
	 
	 x10_async_spawn_wait(req);
      }
        
    x10_finish_end(&frecord, &tmp);
    
    
  } else {
    
    x10_infinite_poll();
  }

  x10_finalize();

  return 0;

}
