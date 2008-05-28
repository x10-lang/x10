#include "x10.h"
#include <assert.h>

typedef struct
{
  int a;
  char b;
}foo;

typedef struct 
{
  x10_async_closure_t base;
  x10_remote_ref_t foo_local;
} my_closure_t;


void __x10_callback_asyncswitch (x10_async_closure_t* closure, 
				 x10_finish_record_t* frecord, 
				 x10_clock_t* clocks, 
				 int num_clocks){

   printf ("here %d\n", x10_here());  
  switch (closure->handler) {
    
  case 1: {
    my_closure_t* cl = (my_closure_t*) closure;
    void* ref = x10_deserialize_ref(cl->foo_local);
    printf ("foo_remote_ref : %d %x\n", cl->foo_local.loc, cl->foo_local.addr);
    printf ("%x \n", ref);
    printf ("%d\n", x10_get_loc(ref));
  }
    break;
  }
}

int __xlc_upc_main() {}

int main()
{
  x10_init();
  
  printf ("hello world: %d %d \n", x10_here(), x10_nplaces());
  
  if (x10_here() ==0) {
    
    foo* foo_local = (foo*) malloc(sizeof(foo));
    
    foo_local->a = 1;
    foo_local->b = 'a';
    
    assert (x10_get_loc((x10_addr_t) foo_local) == x10_here());
    
    x10_remote_ref_t foo_ref = x10_serialize_ref ((x10_addr_t) foo_local);

    printf ("foo_ref : %d %x\n", foo_ref.loc, foo_ref.addr);

    my_closure_t closure;
    closure.base.handler = 1;
    closure.foo_local = foo_ref;

    x10_finish_record_t frecord = {0, 0};

    x10_comm_handle_t req = x10_async_spawn (1, (x10_async_closure_t*) &closure, sizeof(closure), &frecord, NULL, 0);

    x10_async_spawn_wait (req);

  } else {
    
    x10_wait();
  }
  
  x10_finalize();
  return 0;
}
