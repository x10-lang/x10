#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif

#include <cstdlib>
#include <cstdio>

#include <x10rt_front.h>
#include <x10rt_logical.h>
#include <x10rt_net.h>

static x10rt_msg_type counter = 0;

static bool run_as_library = false;

char* x10rt_preinit() {
	run_as_library = true;
	// Because we don't want to break the old PGAS-BG/P implementation of x10rt_net.h, we
	// can't add methods to lower API layers.  So instead, we set environment variables
	// to pass & return values needed inside the regular x10rt_init method call of sockets.
	// Yuck.
	setenv("X10_LIBRARY_MODE", "preinit", 1);
	x10rt_net_init(NULL, NULL, &counter);
	char* connInfo = getenv("X10_LIBRARY_MODE");
	return connInfo;
}

bool x10rt_run_as_library (void)
{ return run_as_library; }

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
{ x10rt_lgl_registration_complete(); }

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


static uint32_t print_headers = getenv("X10RT_PRINT_MSG_HEADERS") != NULL
                              ? (uint32_t)strtoull(getenv("X10RT_PRINT_MSG_HEADERS"),NULL,10)
                              : 0xFFFFFFFF;
void x10rt_send_msg (x10rt_msg_params *p)
{
    if (p->len > print_headers) {
        ::fprintf(stderr,"p%llu --%llu--> p%llu (%llu bytes)\n",
                  (unsigned long long)x10rt_lgl_here(),
                  (unsigned long long)p->type,
                  (unsigned long long)p->dest_place,
                  (unsigned long long)p->len);
    }
    return x10rt_lgl_send_msg(p);
}


void x10rt_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{ return x10rt_lgl_send_get(p, buf, len); }

void x10rt_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{ return x10rt_lgl_send_put(p, buf, len); }

void x10rt_remote_alloc (x10rt_place place, x10rt_remote_ptr sz,
                         x10rt_completion_handler3 *ch, void *arg)
{ x10rt_lgl_remote_alloc(place, sz, ch, arg); }
void x10rt_remote_free (x10rt_place place, x10rt_remote_ptr ptr)
{ x10rt_lgl_remote_free(place, ptr); }


void x10rt_remote_op (x10rt_place place, x10rt_remote_ptr remote_addr,
                      x10rt_op_type type, unsigned long long value)
{ x10rt_lgl_remote_op(place, remote_addr, type, value); }

void x10rt_remote_ops (x10rt_remote_op_params *opv, size_t opc)
{ x10rt_lgl_remote_ops(opv, opc); }

x10rt_remote_ptr x10rt_register_mem (void *ptr, size_t len)
{ return x10rt_lgl_register_mem(ptr, len); }

void x10rt_blocks_threads (x10rt_place d, x10rt_msg_type type, int dyn_shm,
                           int *blocks, int *threads, const int *cfg)
{ x10rt_lgl_blocks_threads (d, type, dyn_shm, blocks, threads, cfg); }


void x10rt_probe (void)
{ x10rt_lgl_probe(); }

void x10rt_blocking_probe (void)
{ x10rt_lgl_blocking_probe(); }


void x10rt_finalize (void)
{ x10rt_lgl_finalize(); }



void x10rt_team_new (x10rt_place placec, x10rt_place *placev,
                     x10rt_completion_handler2 *ch, void *arg)
{
    x10rt_lgl_team_new(placec, placev, ch, arg);
}

void x10rt_team_del (x10rt_team team, x10rt_place role,
                     x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_team_del(team, role, ch, arg);
}

x10rt_place x10rt_team_sz (x10rt_team team)
{
    return x10rt_lgl_team_sz(team);
}

void x10rt_team_split (x10rt_team parent, x10rt_place parent_role,
                       x10rt_place color, x10rt_place new_role,
                       x10rt_completion_handler2 *ch, void *arg)
{
    x10rt_lgl_team_split(parent, parent_role, color, new_role, ch, arg);
}

void x10rt_barrier (x10rt_team team, x10rt_place role,
                    x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_barrier(team, role, ch, arg);
}

void x10rt_bcast (x10rt_team team, x10rt_place role,
                  x10rt_place root, const void *sbuf, void *dbuf,
                  size_t el, size_t count,
                  x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_bcast(team, role, root, sbuf, dbuf, el, count, ch, arg);
}

void x10rt_scatter (x10rt_team team, x10rt_place role,
                    x10rt_place root, const void *sbuf, void *dbuf,
                    size_t el, size_t count,
                    x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_scatter(team, role, root, sbuf, dbuf, el, count, ch, arg);
}

void x10rt_alltoall (x10rt_team team, x10rt_place role,
                     const void *sbuf, void *dbuf,
                     size_t el, size_t count,
                     x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_alltoall(team, role, sbuf, dbuf, el, count, ch, arg);
}

void x10rt_allreduce (x10rt_team team, x10rt_place role,
                      const void *sbuf, void *dbuf,
                      x10rt_red_op_type op, 
                      x10rt_red_type dtype,
                      size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_allreduce(team, role, sbuf, dbuf, op, dtype, count, ch, arg);
}


void x10rt_one_setter (void *arg)
{ *((int*)arg) = 1; }

void x10rt_team_setter (x10rt_team v, void *arg)
{ *((x10rt_team*)arg) = v; }


void x10rt_remote_ptr_setter (x10rt_remote_ptr v, void *arg)
{ *((x10rt_remote_ptr*)arg) = v; }



void x10rt_get_stats (x10rt_stats *s)
{ x10rt_lgl_get_stats(s); }

void x10rt_set_stats (x10rt_stats *s)
{ x10rt_lgl_set_stats(s); }

void x10rt_zero_stats (x10rt_stats *s)
{ x10rt_lgl_zero_stats(s); }

