#include <cstdlib>

#include <x10rt_types.h>

void x10rt_init (int &argc, char **&argv);

x10rt_msg_type x10rt_register_msg_receiver (x10rt_handler *cb,
                                            x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                            const char *cubin, const char *kernel_name);

x10rt_msg_type x10rt_register_get_receiver (x10rt_finder *cb1, x10rt_notifier *cb2,
                                            x10rt_finder *cuda_cb1, x10rt_notifier *cuda_cb2);

x10rt_msg_type x10rt_register_put_receiver (x10rt_finder *cb1, x10rt_notifier *cb2,
                                            x10rt_finder *cuda_cb1, x10rt_notifier *cuda_cb2);

void x10rt_registration_complete (void);

x10rt_place x10rt_nplaces (void);
x10rt_place x10rt_nhosts (void);
x10rt_place x10rt_here (void);
bool x10rt_is_host (x10rt_place place);
bool x10rt_is_cuda (x10rt_place place);
bool x10rt_is_spe (x10rt_place place);
x10rt_place x10rt_parent (x10rt_place place);
x10rt_place x10rt_nchildren (x10rt_place place);
x10rt_place x10rt_child (x10rt_place host, x10rt_place index);
x10rt_place x10rt_child_index (x10rt_place child);

void *x10rt_msg_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_send_msg (x10rt_msg_params &);

void *x10rt_get_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_send_get (x10rt_msg_params &, void *buf, x10rt_copy_sz len);

void *x10rt_put_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_send_put (x10rt_msg_params &, void *buf, x10rt_copy_sz len);

x10rt_remote_ptr x10rt_remote_alloc (x10rt_place place, x10rt_remote_ptr sz);
void x10rt_remote_free (x10rt_place place, x10rt_remote_ptr ptr);

void x10rt_remote_xor (x10rt_place place, x10rt_remote_ptr addr, long long update);

void x10rt_remote_op_fence (void);

void x10rt_blocks_threads (x10rt_place d, x10rt_msg_type type, int dyn_shm,
                           int &blocks, int &threads, const int *cfg);

void x10rt_probe (void);

void x10rt_finalize (void); 

