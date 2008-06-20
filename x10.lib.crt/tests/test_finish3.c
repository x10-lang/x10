#include <assert.h>

#include "x10.h"

typedef struct 
{
  x10_async_closure_t base;
  int magic_number;
} my_closure_t;

int done = 0;

int sum = 0;

void __x10_callback_asyncswitch (x10_async_closure_t* closure_in, 
				 x10_finish_record_t* frecord_in, 
				 x10_clock_t* clocks, 
				 int num_clocks)
{
  my_closure_t* my_closure = (my_closure_t*) closure_in;


  switch (my_closure->base.handler) {

  case 1:
    {      
      my_closure_t closure_out;   
      closure_out.base.handler = 2;
      closure_out.magic_number = 1;
      
      x10_finish_record_t frecord_out;
      
      x10_finish_begin(&frecord_out, NULL, NULL, 0, 0);
      
      int i, tmp;
      
      for (i = 0; i < 100; ++i)
	{
	  x10_comm_handle_t req = x10_async_spawn(x10_nplaces()-1, (x10_async_closure_t*) &closure_out, sizeof(closure_out), &frecord_out, NULL, 0);
	
	  x10_async_spawn_wait(req);
	}
      
      x10_finish_end(&frecord_out, &tmp);

      x10_finish_child(frecord_in, NULL, 0);
      break;
    }
  case 2:
    {
      sum+= my_closure->magic_number;
      assert (x10_here() == x10_nplaces() - 1);
      x10_finish_child(frecord_in, NULL, 0);
    }
  }
}


int __xlc_upc_main(){}

int main()
{
  x10_init();

  printf ("hello world: %d %d \n", x10_here(), x10_nplaces());

  assert (x10_nplaces() > 1);
  
  if (x10_here() == 0) {
    
    my_closure_t closure;
    closure.base.handler = 1;
    closure.magic_number = 1729;
    
    x10_finish_record_t frecord;
    
    x10_finish_begin(&frecord, NULL, NULL, 0, 0);
    
    int i, tmp;
    
    for (i = 1; i < x10_nplaces() - 1; ++i)
      {
	x10_comm_handle_t req = x10_async_spawn(i, (x10_async_closure_t*) &closure, sizeof(closure), &frecord, NULL, 0);
	
	x10_async_spawn_wait(req);
      }
    
    x10_finish_end(&frecord, &tmp);
    
  } else {
    
    x10_wait();
  }
    
  if (x10_here() == x10_nplaces() - 1) { 
    //printf ("sum :%d\n", sum);
    assert (sum == 100 * (x10_nplaces()-2));
  }
  x10_finalize();

  return 0;

}
