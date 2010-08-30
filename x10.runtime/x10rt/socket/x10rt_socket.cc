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
        default: return 0;
    }
}

void x10rt_net_team_new (x10rt_place placec, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
    stub();
}

void x10rt_net_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
    stub();
}

x10rt_place x10rt_net_team_sz (x10rt_team team)
{
    stub();
}

void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role,
                           x10rt_place color, x10rt_place new_role,
                           x10rt_completion_handler2 *ch, void *arg)
{
    stub();
}

void x10rt_net_barrier (x10rt_team team, x10rt_place role,
                        x10rt_completion_handler *ch, void *arg)
{
    stub();
}

void x10rt_net_bcast (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
    stub();
}

void x10rt_net_alltoall (x10rt_team team, x10rt_place role,
                         const void *sbuf, void *dbuf,
                         size_t el, size_t count,
                         x10rt_completion_handler *ch, void *arg)
{
    stub();
}

void x10rt_net_allreduce (x10rt_team team, x10rt_place role,
                          const void *sbuf, void *dbuf,
                          x10rt_red_op_type op,
                          x10rt_red_type dtype,
                          size_t count,
                          x10rt_completion_handler *ch, void *arg)
{
    stub();
}
