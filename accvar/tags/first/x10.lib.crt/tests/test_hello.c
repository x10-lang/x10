#include "x10.h"

typedef struct my_closure_t
{
  x10_async_closure_t base;
  int magic_number;
} my_closure_t;

void __x10_callback_asyncswitch (x10_async_closure_t* closure, x10_finish_record_t* frecord, x10_clock_t* clocks, int num_clocks)
{
}


int __xlc_upc_main(){}

int main()
{
  x10_init();

  printf ("hello world: %d %d \n", __x10_here, __x10_numplaces);

  x10_finalize();
  return 0;
}
