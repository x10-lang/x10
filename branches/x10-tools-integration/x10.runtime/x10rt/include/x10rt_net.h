#include <cstdlib>

#include <x10rt_types.h>

void x10rt_net_init (int &argc, char **&argv, x10rt_msg_type &counter);

void x10rt_net_register_msg_receiver (x10rt_msg_type, void (*cb)(const x10rt_msg_params &));
void x10rt_net_register_get_receiver (x10rt_msg_type,
                                      void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                      void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));
void x10rt_net_register_put_receiver (x10rt_msg_type,
                                      void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                      void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));

void x10rt_net_internal_barrier (void);

x10rt_place x10rt_net_nhosts (void);
x10rt_place x10rt_net_here (void);

void *x10rt_net_msg_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_net_send_msg (x10rt_msg_params &);

void *x10rt_net_get_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_net_send_get (x10rt_msg_params &, void *buf, x10rt_copy_sz len);

void *x10rt_net_put_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_net_send_put (x10rt_msg_params &, void *buf, x10rt_copy_sz len);

void x10rt_net_probe (void);

void x10rt_net_remote_xor (x10rt_place place, x10rt_remote_ptr addr, long long update);

void x10rt_net_remote_op_fence (void);

void x10rt_net_finalize (void); 

