#include <cstdlib>

#include <x10rt_types.h>

void x10rt_init (int &argc, char **&argv);

x10rt_msg_type x10rt_register_msg_receiver (void (*cb)(const x10rt_msg_params &),
                                            void *(*pre_cb)(const x10rt_msg_params &,
                                                            size_t &blocks, size_t &threads,
                                                            size_t &shm),
                                            void (*post_cb)(const x10rt_msg_params &),
                                            const char *cubin, const char *kernel_name);

x10rt_msg_type x10rt_register_get_receiver (void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz),
                                            void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz),
                                       void *(*cuda_cb1)(const x10rt_msg_params &, x10rt_copy_sz),
                                       void (*cuda_cb2)(const x10rt_msg_params &, x10rt_copy_sz));

x10rt_msg_type x10rt_register_put_receiver (void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz),
                                            void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz),
                                       void *(*cuda_cb1)(const x10rt_msg_params &, x10rt_copy_sz),
                                       void (*cuda_cb2)(const x10rt_msg_params &, x10rt_copy_sz));

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

void x10rt_probe (void);

void x10rt_finalize (void); 

