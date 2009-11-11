#include <cstdlib>

#include <x10rt_types.h>

enum x10rt_lgl_cat {
  X10RT_LGL_HOST,
  X10RT_LGL_SPE,
  X10RT_LGL_CUDA
};

struct x10rt_lgl_cfg_accel {
  x10rt_lgl_cat cat;
  unsigned index;
};

void x10rt_lgl_init (int &argc, char **&argv,
                     x10rt_lgl_cfg_accel *cfgv, x10rt_place cfgc, x10rt_msg_type &counter);
void x10rt_lgl_init (int &argc, char **&argv, x10rt_msg_type &counter);

void x10rt_lgl_register_msg_receiver (x10rt_msg_type msg_type,
                                     void (*cb)(const x10rt_msg_params &));
void x10rt_lgl_register_get_receiver (x10rt_msg_type msg_type,
                                     void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                     void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));
void x10rt_lgl_register_put_receiver (x10rt_msg_type msg_type,
                                     void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                     void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));

void x10rt_lgl_register_msg_receiver_cuda (x10rt_msg_type msg_type,
                                          void *(*pre_cb)(const x10rt_msg_params &,
                                                      size_t &blocks, size_t &threads, size_t &shm),
                                          void (*post_cb)(const x10rt_msg_params &),
                                          const char *cubin, const char *kernel_name);

void x10rt_lgl_register_get_receiver_cuda (x10rt_msg_type msg_type,
                                          void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                          void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));

void x10rt_lgl_register_put_receiver_cuda (x10rt_msg_type msg_type,
                                          void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                          void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));

void x10rt_lgl_internal_barrier (void);

x10rt_place x10rt_lgl_nplaces (void);
x10rt_place x10rt_lgl_nhosts (void);
x10rt_place x10rt_lgl_here (void);
x10rt_lgl_cat x10rt_lgl_type (x10rt_place place);
x10rt_place x10rt_lgl_parent (x10rt_place place);
x10rt_place x10rt_lgl_nchildren (x10rt_place place);
x10rt_place x10rt_lgl_child (x10rt_place host, x10rt_place index);
x10rt_place x10rt_lgl_child_index (x10rt_place child);

void *x10rt_lgl_msg_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_lgl_send_msg (x10rt_msg_params &);

void *x10rt_lgl_get_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_lgl_send_get (x10rt_msg_params &, void *buf, x10rt_copy_sz len);

void *x10rt_lgl_put_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_lgl_send_put (x10rt_msg_params &, void *buf, x10rt_copy_sz len);

x10rt_remote_ptr x10rt_lgl_remote_alloc (x10rt_place place, x10rt_remote_ptr sz);
void x10rt_lgl_remote_free (x10rt_place place, x10rt_remote_ptr ptr);

void x10rt_lgl_remote_xor (x10rt_place place, x10rt_remote_ptr addr, long long update);

void x10rt_lgl_remote_op_fence (void);

void x10rt_lgl_probe (void);

void x10rt_lgl_finalize (void); 

