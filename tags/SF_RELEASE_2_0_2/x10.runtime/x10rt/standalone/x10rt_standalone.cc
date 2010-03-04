#include <cstdlib>
#include <cstdio>

#include <x10rt_net.h>

void x10rt_net_init (int &, char **&, x10rt_msg_type &)
{ }

void x10rt_net_register_msg_receiver (x10rt_msg_type,
                                  void (*)(const x10rt_msg_params &))
{ }

void x10rt_net_register_put_receiver (x10rt_msg_type,
                                  void *(*)(const x10rt_msg_params &, x10rt_copy_sz),
                                  void (*)(const x10rt_msg_params &, x10rt_copy_sz))
{ }

void x10rt_net_register_get_receiver (x10rt_msg_type,
                                  void *(*)(const x10rt_msg_params &, x10rt_copy_sz),
                                  void (*)(const x10rt_msg_params &, x10rt_copy_sz))
{ }

void x10rt_net_internal_barrier (void)
{ }

x10rt_place x10rt_net_nhosts (void)
{ return 1; }

x10rt_place x10rt_net_here (void)
{ return 0; }

static void stub (void)
{
    fprintf(stderr,"This is a standalone implementation so there should be no messages.\n");
    abort();
}

void *x10rt_net_msg_realloc (void *msg, size_t, size_t sz)
{ return realloc(msg, sz); }
void x10rt_net_send_msg (x10rt_msg_params &)
{ stub(); }

void *x10rt_net_get_realloc (void *msg, size_t, size_t sz)
{ return realloc(msg, sz); }
void x10rt_net_send_get (x10rt_msg_params &, void *, x10rt_copy_sz )
{ stub(); }

void *x10rt_net_put_realloc (void *msg, size_t, size_t sz)
{ return realloc(msg, sz); }
void x10rt_net_send_put (x10rt_msg_params &, void *, x10rt_copy_sz)
{ stub(); }

void x10rt_net_remote_xor (unsigned long, unsigned long long, long long)
{ stub(); }

void x10rt_net_remote_op_fence (void)
{ }

void x10rt_register_thread (void)
{ }

void x10rt_net_probe (void)
{ }

void x10rt_net_finalize (void)
{ }
