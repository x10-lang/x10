#include <x10rt_front.h>
#include <x10rt_logical.h>

static x10rt_msg_type counter = 0;

void x10rt_init (int *argc, char ***argv)
{ x10rt_lgl_init(argc, argv, &counter); }

x10rt_msg_type x10rt_register_msg_receiver (x10rt_handler *cb,
                                            x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                            const char *cubin, const char *kernel_name)
{
    x10rt_lgl_register_msg_receiver(counter, cb); 
    if (pre!=NULL)
        x10rt_lgl_register_msg_receiver_cuda(counter, pre, post, cubin, kernel_name);
    return counter++;
}

x10rt_msg_type x10rt_register_get_receiver (x10rt_finder *cb1, x10rt_notifier *cb2,
                                            x10rt_finder *cuda_cb1, x10rt_notifier *cuda_cb2)
{
    x10rt_lgl_register_get_receiver(counter, cb1, cb2);
    if (cuda_cb1!=NULL)
        x10rt_lgl_register_get_receiver_cuda(counter, cuda_cb1, cuda_cb2);
    return counter++;
}

x10rt_msg_type x10rt_register_put_receiver (x10rt_finder *cb1, x10rt_notifier *cb2,
                                            x10rt_finder *cuda_cb1, x10rt_notifier *cuda_cb2)
{
    x10rt_lgl_register_put_receiver(counter, cb1, cb2);
    if (cuda_cb1!=NULL)
        x10rt_lgl_register_put_receiver_cuda(counter, cuda_cb1, cuda_cb2);
    return counter++;
}

void x10rt_registration_complete (void)
{ x10rt_lgl_internal_barrier(); }

x10rt_place x10rt_nplaces (void)
{ return x10rt_lgl_nplaces(); }

x10rt_place x10rt_nhosts (void)
{ return x10rt_lgl_nhosts(); }

x10rt_place x10rt_here (void)
{ return x10rt_lgl_here(); }

bool x10rt_is_host (x10rt_place place)
{ return x10rt_lgl_type(place) == X10RT_LGL_HOST; }

bool x10rt_is_cuda (x10rt_place place)
{ return x10rt_lgl_type(place) == X10RT_LGL_CUDA; }

bool x10rt_is_spe (x10rt_place place)
{ return x10rt_lgl_type(place) == X10RT_LGL_SPE; }

x10rt_place x10rt_parent (x10rt_place place)
{ return x10rt_lgl_parent(place); }

x10rt_place x10rt_nchildren (x10rt_place place)
{ return x10rt_lgl_nchildren(place); }

x10rt_place x10rt_child (x10rt_place host, x10rt_place index)
{ return x10rt_lgl_child(host, index); }

x10rt_place x10rt_child_index (x10rt_place child)
{ return x10rt_lgl_child_index(child); }


void *x10rt_msg_realloc (void *old, size_t old_sz, size_t new_sz)
{ return x10rt_lgl_msg_realloc(old, old_sz, new_sz); }
void x10rt_send_msg (x10rt_msg_params *p)
{ return x10rt_lgl_send_msg(p); }

void *x10rt_get_realloc (void *old, size_t old_sz, size_t new_sz)
{ return x10rt_lgl_get_realloc(old, old_sz, new_sz); }
void x10rt_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{ return x10rt_lgl_send_get(p, buf, len); }

void *x10rt_put_realloc (void *old, size_t old_sz, size_t new_sz)
{ return x10rt_lgl_put_realloc(old, old_sz, new_sz); }
void x10rt_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{ return x10rt_lgl_send_put(p, buf, len); }

x10rt_remote_ptr x10rt_remote_alloc (x10rt_place place, x10rt_remote_ptr sz)
{ return x10rt_lgl_remote_alloc(place, sz); }
void x10rt_remote_free (x10rt_place place, x10rt_remote_ptr ptr)
{ return x10rt_lgl_remote_free(place, ptr); }


void x10rt_remote_xor (x10rt_place place, x10rt_remote_ptr addr, long long update)
{ x10rt_lgl_remote_xor(place, addr, update); }

void x10rt_remote_op_fence (void)
{ x10rt_lgl_remote_op_fence(); }


void x10rt_blocks_threads (x10rt_place d, x10rt_msg_type type, int dyn_shm,
                           int *blocks, int *threads, const int *cfg)
{ x10rt_lgl_blocks_threads (d, type, dyn_shm, blocks, threads, cfg); }


void x10rt_probe (void)
{ x10rt_lgl_probe(); }

void x10rt_finalize (void)
{ x10rt_lgl_finalize(); }
