#include "x10.h"
#include <assert.h>

int a[100];

typedef struct my_closure_t
{
  x10_async_closure_t base;
  x10_remote_ref_t ref;
} my_closure_t;

void __x10_callback_asyncswitch (x10_async_closure_t* closure, x10_finish_record_t* frecord, x10_clock_t* clocks, int num_clocks)
{

}


void __x10_callback_array_copy_switch(x10_async_closure_t* closure)
{

}

int __xlc_upc_main(){}

int main()
{
  x10_init();

  printf ("hello world: %d %d\n", x10_here(), x10_nplaces());
  
  if (x10_here() == 0) {
    
    int i;

    x10_remote_ref_t proxy = {1, (x10_addr_t) a};

    x10_addr_t ref = x10_deserialize_ref(proxy);

    x10_comm_handle_t req = x10_array_get ((x10_addr_t) a, ref, 0, 100 * sizeof(int));

    x10_async_spawn_wait (req);
    for (i= 0; i < 100; i++)
      assert (a[i] == i);
    
  } else {
    int i;
    for (i = 0; i < 100; i++) a[i] = i;
    x10_wait();
  }
  
  x10_finalize();

  return 0;
}
