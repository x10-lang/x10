#include <cstdlib>
#include <cstdio>

#include <x10rt_net.h>

static void stub (void)
{
    fprintf(stderr,"Not implemented yet!\n");
    abort();
}


void x10rt_net_init (int *, char ***, x10rt_msg_type *)
{ }

void x10rt_net_register_msg_receiver (x10rt_msg_type, x10rt_handler *)
{ }

void x10rt_net_register_put_receiver (x10rt_msg_type, x10rt_finder *, x10rt_notifier *)
{ }

void x10rt_net_register_get_receiver (x10rt_msg_type, x10rt_finder *, x10rt_notifier *)
{ }

void x10rt_net_internal_barrier (void)
{ }

x10rt_place x10rt_net_nhosts (void)
{ return 1; }

x10rt_place x10rt_net_here (void)
{ return 0; }

void x10rt_net_send_msg (x10rt_msg_params *)
{ stub(); }

void x10rt_net_send_get (x10rt_msg_params *, void *, x10rt_copy_sz )
{ stub(); }

void x10rt_net_send_put (x10rt_msg_params *, void *, x10rt_copy_sz)
{ stub(); }

void x10rt_net_probe ()
{ }

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim, x10rt_op_type type, unsigned long long value)
{ stub(); }

x10rt_remote_ptr x10rt_net_register_mem (void *ptr, size_t len)
{ return NULL; }

void x10rt_net_finalize (void)
{ }

int x10rt_net_supports (x10rt_opt o)
{
    switch (o) {
        default: return 1;
    }
}
