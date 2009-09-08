#include <cstdlib>
#include <cstdio>

#include <x10rt_api.h>


void x10rt_register_msg_receiver (unsigned,
                                  void (*)(const x10rt_msg_params &))
{ }

void x10rt_register_put_receiver (unsigned,
                                  void *(*)(const x10rt_msg_params &, unsigned long),
                                  void (*)(const x10rt_msg_params &, unsigned long))
{ }

void x10rt_register_get_receiver (unsigned,
                                  void *(*)(const x10rt_msg_params &),
                                  void (*)(const x10rt_msg_params &, unsigned long))
{ }

void x10rt_registration_complete (void)
{ }

unsigned long x10rt_nplaces (void)
{ return 1; }

unsigned long x10rt_here (void)
{ return 0; }

static void stub (void)
{
    fprintf(stderr,"This is a standalone implementation so there should be no messages.\n");
    abort();
}

void *x10rt_msg_realloc (void *, size_t, size_t)
{ stub(); return NULL; }
void x10rt_send_msg (x10rt_msg_params &)
{ stub(); }

void *x10rt_get_realloc (void *, size_t, size_t)
{ stub(); return NULL; }
void x10rt_send_get (x10rt_msg_params &, void *, unsigned long )
{ stub(); }

void *x10rt_put_realloc (void *, size_t, size_t)
{ stub(); return NULL; }
void x10rt_send_put (x10rt_msg_params &, void *, unsigned long)
{ stub(); }

void x10rt_probe (void)
{ }

void x10rt_finalize (void)
{ }
