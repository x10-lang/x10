#include <cstdlib>

#include <x10rt_types.h>

struct x10rt_cuda_ctx;

unsigned x10rt_cuda_ndevs (void);

x10rt_cuda_ctx *x10rt_cuda_init (unsigned device);


void x10rt_cuda_register_msg_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                       void *(*pre_cb) (const x10rt_msg_params &p, 
                                                        size_t &blocks, size_t &threads,
                                                        size_t &shm),
                                       void (*post_cb)(const x10rt_msg_params &),
                                       const char *cubin, const char *kernel_name);

void x10rt_cuda_register_get_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                       void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                       void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));

void x10rt_cuda_register_put_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                       void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz len),
                                       void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz len));


void x10rt_cuda_registration_complete (x10rt_cuda_ctx *ctx);

void x10rt_cuda_send_msg (x10rt_cuda_ctx *ctx, x10rt_msg_params &);

void x10rt_cuda_send_get (x10rt_cuda_ctx *ctx, x10rt_msg_params &, void *buf, x10rt_copy_sz len);

void x10rt_cuda_send_put (x10rt_cuda_ctx *ctx, x10rt_msg_params &, void *buf, x10rt_copy_sz len);

void *x10rt_cuda_device_alloc (x10rt_cuda_ctx *ctx, size_t sz);
void x10rt_cuda_device_free (x10rt_cuda_ctx *ctx, void *ptr);

void x10rt_cuda_probe (x10rt_cuda_ctx *ctx);

void x10rt_cuda_finalize (x10rt_cuda_ctx *ctx); 

