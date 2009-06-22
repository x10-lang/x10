#include "x10.h"

typedef struct my_closure_t
{
  x10_async_closure_t base;
} my_closure_t;

void __x10_callback_asyncswitch (x10_async_closure_t* closure, x10_finish_record_t* frecord, 
				 x10_clock_t* clocks, int num_clocks)
{
  my_closure_t* my_closure = (my_closure_t*) closure;
  switch (my_closure->base.handler) {
  case 1:
    printf ("hello world (%d)\n", x10_here());
    break;
  }
}

int __xlc_upc_main(){}

int main()
{
  x10_init();

  printf ("hello world: %d %d \n", x10_here(), x10_nplaces());

  if (x10_here() == 0) {
     my_closure_t closure;
     closure.base.handler = 1;
     x10_finish_record_t frecord = {0,0};
     x10_comm_handle_t req = x10_async_spawn (1, (x10_async_closure_t*) &closure, 
					      sizeof(closure), &frecord, NULL, 0);
     x10_async_spawn_wait(req);
  } else {
    x10_comm_handle_t handle = {NULL, NULL};
    x10_async_spawn_wait(handle); 
    x10_probe();
  }

  x10_finalize();
  return 0;
}
