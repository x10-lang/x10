/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif

#include <cstdio>
#include <cstdarg>
#include <cstring>
#include <cassert>
#include <cctype>
#include <string>

#include <unistd.h>

#include <x10rt_logical.h>
#include <x10rt_net.h>
#include <x10rt_cuda.h>
#include <x10rt_internal.h>
#include <x10rt_ser.h>
#include <x10rt_front.h>

#define ESCAPE_IF_ERR if (g.error_code != X10RT_ERR_OK) return; else { }
#define CHECK_ERR_AND_RETURN if (g.error_code != X10RT_ERR_OK) return g.error_code; else { }

#define PROP_ERR(x, y) do { \
    g.error_code = x; \
    if (g.error_code != X10RT_ERR_OK) { \
        g.error_msg = strdup(y); \
        return g.error_code; \
    } \
} while (0)

#define X10RT_NET_PROBE_PROP_ERR PROP_ERR(x10rt_net_probe(), x10rt_net_error_msg())

#ifndef NDEBUG
#define tame_assert(x) if (!(x)) { fatal("Assertion failure (%s;%d): %s\n", __FILE__, __LINE__, #x); g.error_code = X10RT_ERR_INTL; return; } else { }
#define tame_assert_r(x,r) if (!(x)) { fatal("Assertion failure (%s;%d): %s\n", __FILE__, __LINE__, #x); g.error_code = X10RT_ERR_INTL; return r; } else { }
#else
// sizeof avoids warnings about unused vars, etc without actually executing x or r
#define tame_assert(x) ((void)sizeof(x)) 
#define tame_assert_r(x,r) ((void)(sizeof(x)+sizeof(r)))
#endif

namespace {

    struct x10rt_lgl_ctx {
      // Local stuff
      x10rt_place nhosts;
      x10rt_place nplaces;
      void **accel_ctxs; // can contain a variety of x10rt_cuda_ctx / other accelerators

      // Local copy of global node database
      x10rt_lgl_cat *type;
      x10rt_place *parent;
      x10rt_place *index; // child[parent[n]][index[n]] == n
      x10rt_place *naccels;
      x10rt_place **child; // maps node/accel index to global place id

      char *error_msg;
      x10rt_error error_code;
    };

    bool has_remote_op;
    x10rt_coll_type has_collectives;

    x10rt_lgl_ctx g; // note that being a global var, this is zero-initialised
}

static x10rt_error fatal (const char *format, ...)
{
    va_list va_args;

    x10rt_error e = X10RT_ERR_INTL;
    g.error_code = e;

    va_start(va_args, format);
    int sz = vsnprintf(NULL, 0, format, va_args);
    va_end(va_args);

    free(g.error_msg);
    g.error_msg = (char*)malloc(sz);

    va_start(va_args, format);
    vsprintf(g.error_msg, format, va_args);
    va_end(va_args);

    return e;
}

x10rt_stats x10rt_lgl_stats;


static void one_setter (void *arg)
{ *((int*)arg) = 1; }

const char *x10rt_lgl_error_msg (void) {
    return g.error_msg;
}

x10rt_place x10rt_lgl_nplaces (void)
{
    return g.nplaces;
}

x10rt_place x10rt_lgl_ndead (void)
{
	return x10rt_net_ndead();
}

bool x10rt_lgl_is_place_dead (x10rt_place p)
{
	return x10rt_net_is_place_dead(p);
}

x10rt_error x10rt_lgl_get_dead (x10rt_place *dead_places, x10rt_place len)
{
	return x10rt_net_get_dead(dead_places, len);
}

x10rt_place x10rt_lgl_here (void)
{
    return x10rt_net_here();
}

x10rt_lgl_cat x10rt_lgl_type (x10rt_place node)
{
    assert(node<x10rt_lgl_nplaces());
    return g.type[node];
}

x10rt_place x10rt_lgl_nhosts (void)
{
    return g.nhosts;
}

x10rt_place x10rt_lgl_nchildren (x10rt_place place)
{
    assert(place<x10rt_lgl_nplaces());
    if (place>=x10rt_lgl_nhosts()) return 0;
    return g.naccels[place];
}

x10rt_place x10rt_lgl_child (x10rt_place host, x10rt_place i)
{
    assert(host<x10rt_lgl_nhosts());
    assert(i<x10rt_lgl_nchildren(host));
    return g.child[host][i];
}

x10rt_place x10rt_lgl_child_index (x10rt_place child)
{
    assert(child<x10rt_lgl_nplaces());
    assert(x10rt_lgl_type(child)!=X10RT_LGL_HOST);
    return g.index[child];
}

x10rt_place x10rt_lgl_parent (x10rt_place node)
{
    assert(node<x10rt_lgl_nplaces());
    return g.parent[node];
}


unsigned int x10rt_lgl_local_accels (x10rt_lgl_cat cat)
{
    switch (cat) {

        case X10RT_LGL_CUDA:
        return x10rt_cuda_ndevs();

        default:
        tame_assert_r(cat==X10RT_LGL_CUDA, 0);
        return 0;
    }
}

namespace {

    x10rt_msg_type send_finish_id;

    void send_finish(x10rt_place to, x10rt_remote_ptr counter_addr)
    {
        x10rt_serbuf b; x10rt_serbuf_init(&b, to, send_finish_id);
        x10rt_serbuf_write(&b, &counter_addr);
        x10rt_net_send_msg(&b.p);
        x10rt_serbuf_free(&b);
    }

    void recv_finish (const x10rt_msg_params *p)
    {
        x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
        x10rt_remote_ptr counter_addr; x10rt_deserbuf_read(&b, &counter_addr);
        (*(x10rt_place*)(size_t)counter_addr)--;
    }

    x10rt_msg_type send_naccels_id;

    void send_naccels (x10rt_place to, x10rt_place num, x10rt_place *counter)
    {
        x10rt_place from = x10rt_net_here();
        x10rt_remote_ptr counter_addr = (x10rt_remote_ptr)(size_t)counter;
        x10rt_serbuf b; x10rt_serbuf_init(&b, to, send_naccels_id);
        x10rt_serbuf_write(&b, &from);
        x10rt_serbuf_write(&b, &num);
        x10rt_serbuf_write(&b, &counter_addr);
        x10rt_msg_params *p = &b.p;
        x10rt_net_send_msg(p);
        x10rt_serbuf_free(&b);
    }

    void recv_naccels (const x10rt_msg_params *p)
    {
        x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
        x10rt_place from; x10rt_deserbuf_read(&b, &from);
        x10rt_place num; x10rt_deserbuf_read(&b, &num);
        x10rt_remote_ptr counter_addr; x10rt_deserbuf_read(&b, &counter_addr);

        g.naccels[from] = num;
        send_finish(from, counter_addr);
    }

    x10rt_msg_type send_cat_id;

    void send_cat (x10rt_place to, x10rt_place child, unsigned long cat, x10rt_place *counter)
    {
        x10rt_place from = x10rt_net_here();
        x10rt_remote_ptr counter_addr = (x10rt_remote_ptr)(size_t)counter;
        x10rt_serbuf b; x10rt_serbuf_init(&b, to, send_cat_id);
        x10rt_serbuf_write(&b, &from);
        x10rt_serbuf_write(&b, &child);
        x10rt_serbuf_write(&b, &cat);
        x10rt_serbuf_write(&b, &counter_addr);
        x10rt_net_send_msg(&b.p);
        x10rt_serbuf_free(&b);
    }

    void recv_cat (const x10rt_msg_params *p)
    {
        x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
        x10rt_place from; x10rt_deserbuf_read(&b, &from);
        x10rt_place child; x10rt_deserbuf_read(&b, &child);
        unsigned long cat; x10rt_deserbuf_read(&b, &cat);
        x10rt_remote_ptr counter_addr; x10rt_deserbuf_read(&b, &counter_addr);

        g.type[g.child[from][child]] = (x10rt_lgl_cat) cat;
        send_finish(from, counter_addr);
    }

    x10rt_error blocking_barrier (void)
    {
        CHECK_ERR_AND_RETURN;
        volatile int finished = 0;
        x10rt_lgl_barrier(0, x10rt_lgl_here(), one_setter, (void *) &finished);
        while (!finished) {
            x10rt_emu_coll_probe();
            X10RT_NET_PROBE_PROP_ERR;
        }
        return X10RT_ERR_OK;
    }

    x10rt_error x10rt_lgl_internal_init (x10rt_lgl_cfg_accel *cfgv, x10rt_place cfgc, x10rt_msg_type *counter) {

        x10rt_emu_init(counter);

        x10rt_emu_coll_init(counter);
        if (checkBoolEnvVar(getenv("X10RT_FORCE_NATIVE_COLLECTIVES")))
            usleep(1000000); // sleep for 1 second
        has_remote_op = !checkBoolEnvVar(getenv("X10RT_EMULATE_REMOTE_OP")) && x10rt_net_remoteop_support();
        if (checkBoolEnvVar(getenv("X10RT_EMULATE_COLLECTIVES")))
        	has_collectives = X10RT_COLL_NOCOLLECTIVES;
        else
        	has_collectives = x10rt_net_coll_support();
        g.nhosts = x10rt_net_nhosts();

        x10rt_place num_local_cudas = 0;

        // discover accelerator situation
        unsigned int cuda_max_dev = x10rt_cuda_ndevs();

        // ensure user mapping can be realised
        for (x10rt_place i=0 ; i<cfgc ; ++i) {
            x10rt_lgl_cfg_accel *cfg = &cfgv[i];

            switch (cfg->cat) {

                case X10RT_LGL_CUDA:
                num_local_cudas++;
                if (cfg->index >= cuda_max_dev) {
                    return fatal("CUDA reports %u devices, you cannot use device %u.\n",
                                   cuda_max_dev, cfg->index);
                }
                break;

                default:
                return fatal("Invalid node category.\n");
            }
        }

        // Initialise accel APIs to use user mapping 
        g.accel_ctxs = safe_malloc<void*>(cfgc);
        for (x10rt_place i=0 ; i<cfgc ; ++i) {
            x10rt_lgl_cfg_accel *cfg = &cfgv[i];
            switch (cfg->cat) {

                case X10RT_LGL_CUDA:
                g.accel_ctxs[i] = x10rt_cuda_init(cfg->index);
                break;

                default:
                return fatal("Invalid node category.\n");
            }
        }

        g.naccels = safe_malloc<x10rt_place>(x10rt_lgl_nhosts());
        assert(g.naccels!=NULL);

        send_naccels_id = (*counter)++;
        send_cat_id = (*counter)++;
        send_finish_id = (*counter)++;

        x10rt_net_register_msg_receiver(send_naccels_id, recv_naccels);
        x10rt_net_register_msg_receiver(send_cat_id, recv_cat);
        x10rt_net_register_msg_receiver(send_finish_id, recv_finish);

        g.nplaces = x10rt_lgl_nhosts();

        // Spread the knowledge of accelerators around
#ifdef ENABLE_CUDA        
        g.naccels[x10rt_lgl_here()] = cfgc;

        blocking_barrier();
        CHECK_ERR_AND_RETURN;

        x10rt_place finish_counter = x10rt_lgl_nhosts()-1;
        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            if (i==x10rt_lgl_here()) continue;
            send_naccels(i, cfgc, &finish_counter);
        }
        while (finish_counter!=0) {
            X10RT_NET_PROBE_PROP_ERR;
        }

        blocking_barrier();
        CHECK_ERR_AND_RETURN;

        // Now we can calculate the total number of places
        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            g.nplaces += g.naccels[i];
        }
#else
        memset(g.naccels, 0, sizeof(x10rt_place)*g.nplaces);
#endif


        // now assign the node ids and populate the datastructure that represents
        // the hierarchy
        g.type = safe_malloc<x10rt_lgl_cat>(g.nplaces);
        assert(g.type!=NULL);

        g.parent = safe_malloc<x10rt_place>(g.nplaces);
        assert(g.parent!=NULL);

        g.index = safe_malloc<x10rt_place>(g.nplaces);
        assert(g.index!=NULL);

        g.child = safe_malloc<x10rt_place*>(g.nplaces);
        assert(g.child!=NULL);

        x10rt_place a = x10rt_lgl_nhosts();
        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            g.type[i] = X10RT_LGL_HOST;
            g.parent[i] = i;
            g.child[i] = safe_malloc<x10rt_place>(g.naccels[i] );
            assert(g.naccels[i]==0 || g.child[i]!=NULL);
            for (x10rt_place j=0 ; j<g.naccels[i] ; ++j) {
                g.parent[a] = i;
                g.index[a] = j;
                g.child[i][j] = a;
                g.child[a] = NULL;
                a++;
            }
        }

        // set up the type information
#ifdef ENABLE_CUDA
        finish_counter = (x10rt_lgl_nhosts()-1) * cfgc;

        for (x10rt_place j=0 ; j<g.naccels[x10rt_lgl_here()] ; ++j) {
            g.type[g.child[x10rt_lgl_here()][j]] = cfgv[j].cat;
        }

        blocking_barrier();
        CHECK_ERR_AND_RETURN;

        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            if (i==x10rt_lgl_here()) continue;
            for (x10rt_place j=0 ; j<cfgc ; ++j) {
                send_cat(i, j, cfgv[j].cat, &finish_counter);
            }
        }

        while (finish_counter!=0) {
            X10RT_NET_PROBE_PROP_ERR;
        }
#else
        for (x10rt_place j=0; j<g.naccels[x10rt_lgl_here()]; ++j)
            g.type[g.child[x10rt_lgl_here()][j]] = cfgv[j].cat;
#endif
        blocking_barrier();
        return g.error_code;
    }

}

x10rt_error x10rt_lgl_preinit(char* connInfoBuffer, int connInfoBufferSize)
{
    return x10rt_net_preinit(connInfoBuffer, connInfoBufferSize);
}

x10rt_error x10rt_lgl_init (int *argc, char ***argv,
                     x10rt_lgl_cfg_accel *cfgv, x10rt_place cfgc, x10rt_msg_type *counter)
{
    PROP_ERR(x10rt_net_init(argc, argv, counter), x10rt_net_error_msg());
    return x10rt_lgl_internal_init(cfgv, cfgc, counter);
}

#define ENV "X10RT_ACCELS"

x10rt_error x10rt_lgl_init (int *argc, char ***argv, x10rt_msg_type *counter)
{
    PROP_ERR(x10rt_net_init(argc, argv, counter), x10rt_net_error_msg());
    char env[1024] = "";
    sprintf(env, ENV"%lu",  (unsigned long)x10rt_net_here());
    const char *str = getenv(env);
    if (str==NULL) {
        sprintf(env, ENV);
        str = getenv(env);
    }
    if (str==NULL || *str=='\0' || !strcmp(str,"NONE") || !strcmp(str,"none")) {
        return x10rt_lgl_internal_init(NULL, 0, counter);
    } else {
        int num_cudas = x10rt_lgl_local_accels(X10RT_LGL_CUDA);

        if (!strcmp(str,"ALL") || !strcmp(str,"all")) {
            if (num_cudas == 0) {
                return x10rt_lgl_internal_init(NULL, 0, counter);
            } else {
                x10rt_lgl_cfg_accel *cfg = safe_malloc<x10rt_lgl_cfg_accel>(num_cudas);
                int accel = 0;
                for (int i=0 ; i<num_cudas ; ++i) {
                    cfg[accel].cat = X10RT_LGL_CUDA;
                    cfg[accel].index = i;
                    accel++;
                }
                x10rt_error code = x10rt_lgl_internal_init(cfg, num_cudas, counter);
                free(cfg);
                return code;
            }
        } else {
            int num_accels = 1;
            for (const char *c=str ; *c!='\0' ; ++c) num_accels += *c==',';
            x10rt_lgl_cfg_accel *cfg = safe_malloc<x10rt_lgl_cfg_accel>(num_accels);
            for (int i=0 ; i<num_accels ; ++i) {
                while (isspace(*str)) str++; // chase up white space
                int chars = strcspn(str,",");
                if (chars<5) {
                    return fatal("%s contains invalid element at "
                                   "index %d: \"%.*s\"\n", env, i, chars, str);
                }
                if (!strncmp(str,"CUDA",4) || !strncmp(str,"cuda",4)) {
                    str += 4; chars -= 4;
                    char *endptr;
                    long index = strtol(str,&endptr,10);
                    while (isspace(*endptr)) endptr++; // chase up white space
                    if (endptr-str != chars) {
                        return fatal("%s contains invalid number at "
                                       "index %d: \"%.*s\"\n", env, i, chars, str);
                    }
                    cfg[i].cat = X10RT_LGL_CUDA;
                    cfg[i].index = index;
                } else {
                    return fatal("%s contains invalid element at "
                                   "index %d: \"%.*s\"\n", env, i, chars, str);
                }
                str += chars;
                str++; // the comma
            }
            x10rt_error code = x10rt_lgl_internal_init(cfg, num_accels, counter);
            free(cfg);
            return code;
        }
    }
    return X10RT_ERR_OK; // never reached, but needed for compiling on some systems
}

void x10rt_lgl_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *cb)
{
    ESCAPE_IF_ERR;
    x10rt_net_register_msg_receiver(msg_type, cb);
}

void x10rt_lgl_register_get_receiver (x10rt_msg_type msg_type, x10rt_notifier *cb)
{
    ESCAPE_IF_ERR;
    x10rt_net_register_get_receiver(msg_type, cb);
}

void x10rt_lgl_register_put_receiver (x10rt_msg_type msg_type, x10rt_notifier *cb)
{
    ESCAPE_IF_ERR;
    x10rt_net_register_put_receiver(msg_type, cb);
}

void x10rt_lgl_register_msg_receiver_cuda (x10rt_msg_type msg_type,
                                           x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                           const char *cubin, const char *kernel_name)
{
    ESCAPE_IF_ERR;
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_register_msg_receiver(cctx, msg_type, pre, post, cubin, kernel_name);
            } break;
            default:
            fatal("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_register_get_receiver_cuda (x10rt_msg_type msg_type, x10rt_notifier *cb)
{
    ESCAPE_IF_ERR;
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_register_get_receiver(cctx, msg_type, cb);
            } break;
            default:
            fatal("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_register_put_receiver_cuda (x10rt_msg_type msg_type, x10rt_notifier *cb)
{
    ESCAPE_IF_ERR;
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_register_put_receiver(cctx, msg_type, cb);
            } break;
            default:
            fatal("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_registration_complete (void)
{
    ESCAPE_IF_ERR;
    blocking_barrier();

    ESCAPE_IF_ERR;

    // accelerators
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_registration_complete(cctx);
            } break;
            default:
            fatal("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_send_msg (x10rt_msg_params *p)
{
    ESCAPE_IF_ERR;
    x10rt_place d = p->dest_place;

    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        x10rt_net_send_msg(p);
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_send_msg(cctx, p);
                x10rt_net_unblock_probe();
            } break;
            default: {
                fatal("Place %lu has invalid type %d in send_msg.\n",
                        (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        fatal("Routing of send_msg still unsupported.\n");
    }
}

void x10rt_lgl_send_get (x10rt_msg_params *p, void *srcAddr, void *dstAddr, x10rt_copy_sz len)
{
    ESCAPE_IF_ERR;
    x10rt_place d = p->dest_place;

    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        x10rt_net_send_get(p, srcAddr, dstAddr, len);
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_send_get(cctx, p, srcAddr, dstAddr, len);
                x10rt_net_unblock_probe();
            } break;
            default: {
                fatal("Place %lu has invalid type %d in send_get.\n",
                        (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        fatal("Routing of send_get still unsupported.\n");
    }
}

void x10rt_lgl_send_put (x10rt_msg_params *p, void *srcAddr, void *dstAddr, x10rt_copy_sz len)
{
    ESCAPE_IF_ERR;
    x10rt_place d = p->dest_place;

    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        x10rt_net_send_put(p, srcAddr, dstAddr, len);
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_send_put(cctx, p, srcAddr, dstAddr, len);
               	x10rt_net_unblock_probe();
            } break;
            default: {
                fatal("Place %lu has invalid type %d in send_put.\n",
                        (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        fatal("Routing of send_put still unsupported.\n");
    }
}

void x10rt_lgl_remote_alloc (x10rt_place d, x10rt_remote_ptr sz,
                             x10rt_completion_handler3 *ch, void *arg)
{
    ESCAPE_IF_ERR;
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        fatal("Host remote_alloc still unsupported.\n");
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                ch((x10rt_remote_ptr)(size_t) x10rt_cuda_device_alloc(cctx, sz),arg);
                break;
            }
            default: {
                fatal("Place %lu has invalid type %d in remote_alloc.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        fatal("Routing of remote_alloc still unsupported.\n");
    }
}
void x10rt_lgl_remote_free (x10rt_place d, x10rt_remote_ptr ptr)
{
    ESCAPE_IF_ERR;
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        fatal("Host remote_free still unsupported.\n");
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_device_free(cctx, (void*)ptr);
            } break;
            default: {
                fatal("Place %lu has invalid type %d in remote_free.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        fatal("Routing of remote_free still unsupported.\n");
    }
}

void x10rt_lgl_remote_op (x10rt_place d, x10rt_remote_ptr remote_addr,
                          x10rt_op_type type, unsigned long long value)
{
    ESCAPE_IF_ERR;
    tame_assert(d < x10rt_lgl_nplaces());
    tame_assert(type >= X10RT_OP_ADD); tame_assert(type <= X10RT_OP_XOR);

    if (d < x10rt_lgl_nhosts()) {
        if (has_remote_op) {
            x10rt_net_remote_op(d,remote_addr,type,value);
        } else {
            x10rt_emu_remote_op(d,remote_addr,type,value);
        }
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                fatal("CUDA remote ops still unsupported.\n");
            } break;
            default: {
                fatal("Place %lu has invalid type %d in remote_op.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        fatal("Routing of remote_op still unsupported.\n");
    }
}
    
void x10rt_lgl_remote_ops (x10rt_remote_op_params *opv, size_t opc)
{
    ESCAPE_IF_ERR;
    if (has_remote_op) {
        // currently build system does not define NDEBUG in optimised mode
        #if 0
        #ifndef NDEBUG
            for (size_t i=0 ; i<opc ; ++i) {
                x10rt_place d = opv[i].dest;
                assert(d < x10rt_lgl_nplaces());
                if (d < x10rt_lgl_nhosts()) {
                    // no problem
                } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
                    // local accelerator
                    switch (x10rt_lgl_type(d)) {
                        case X10RT_LGL_CUDA: {
                            error("CUDA remote ops still unsupported.\n");
                        } break;
                        default: {
                            error("Place %lu has invalid type %d in remote_ops.\n",
                                           (unsigned long)d, (int)x10rt_lgl_type(d));
                        }
                    }
                } else {
                    error("Routing of remote_ops still unsupported.\n");
                }
            }
        #endif
        #endif
        x10rt_net_remote_ops(opv, opc);
    } else {
        x10rt_emu_remote_ops(opv, opc);
    }
}
    
void x10rt_lgl_register_mem (void *ptr, size_t len)
{
    ESCAPE_IF_ERR;
    x10rt_net_register_mem(ptr, len);
}

void x10rt_lgl_deregister_mem (void *ptr)
{
    ESCAPE_IF_ERR;
    x10rt_net_deregister_mem(ptr);
}

void x10rt_lgl_blocks_threads (x10rt_place d, x10rt_msg_type type, int dyn_shm,
                               int *blocks, int *threads, const int *cfg)
{
    ESCAPE_IF_ERR;
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        *blocks = 8; *threads = 1;
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_blocks_threads(cctx, type, dyn_shm, blocks, threads, cfg);
            } break;
            default: {
                fatal("Place %lu has invalid type %d in blocks_threads.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
                return;
            }
        }
    } else {
        fatal("Routing of blocks_threads still unsupported.\n");
        return;
    }
}

void x10rt_lgl_device_sync (x10rt_place d)
{
    ESCAPE_IF_ERR;
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_device_sync(cctx);
            } break;
            default: {
                fatal("Place %lu has invalid type %d in device_sync.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
                return;
            }
        }
    } else {
        fatal("Routing of device_sync still unsupported.\n");
        return;
    }
}


x10rt_error x10rt_lgl_probe (void)
{
    CHECK_ERR_AND_RETURN;
    X10RT_NET_PROBE_PROP_ERR;
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA:
            x10rt_cuda_probe(static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]));
            break;
            default:
            return fatal("Invalid node category.\n");
            return g.error_code;
        }
    }
    // advance collectives as much as possible
    while (x10rt_emu_coll_probe());

    return X10RT_ERR_OK;
}

bool x10rt_lgl_blocking_probe_support(void)
{
	return x10rt_net_blocking_probe_support();
}

x10rt_error x10rt_lgl_blocking_probe (void)
{
    CHECK_ERR_AND_RETURN;
    // first attempt to make progress on collectives
    if (x10rt_emu_coll_probe()) {
        // unsafe to block if collectives have made progress
        return x10rt_lgl_probe();
    }

#ifdef ENABLE_CUDA
    // check to see if there is anything in the GPUs
    bool activeGPU = false;
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA:
            if (x10rt_cuda_probe(static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i])))
            	activeGPU = true;
            break;
            default:
            return fatal("Invalid node category.\n");
            return g.error_code;
        }
    }
    if (activeGPU) { // unsafe to block if the GPU is active
        X10RT_NET_PROBE_PROP_ERR;
    }
    else
    	x10rt_net_blocking_probe();
#else
    // blocking probe
    x10rt_net_blocking_probe();
#endif

    // advance collectives as much as possible
    while (x10rt_emu_coll_probe());

    return X10RT_ERR_OK;
}

x10rt_error x10rt_lgl_unblock_probe (void)
{
	return x10rt_net_unblock_probe();
}

void x10rt_lgl_finalize (void)
{
    if (g.error_code==X10RT_ERR_OK && getenv("X10RT_RXTX")) {
        for (x10rt_place i=0 ; i<x10rt_net_nhosts() ; ++i) {
            blocking_barrier();
            if (x10rt_net_here() != i) continue;
            fprintf(stderr, "Place: %lu   msg_rx: %llu/%llu   msg_tx: %llu/%llu\n",
                    (unsigned long)x10rt_lgl_here(), 
                    (unsigned long long)x10rt_lgl_stats.msg.bytes_received,
                    (unsigned long long)x10rt_lgl_stats.msg.messages_received,
                    (unsigned long long)x10rt_lgl_stats.msg.bytes_sent,
                    (unsigned long long)x10rt_lgl_stats.msg.messages_sent);

            fprintf(stderr, "Place: %lu   put_rx: %llu(&%llu)/%llu   put_tx: %llu(&%llu)/%llu\n",
                    (unsigned long)x10rt_lgl_here(), 
                    (unsigned long long)x10rt_lgl_stats.put.bytes_received,
                    (unsigned long long)x10rt_lgl_stats.put_copied_bytes_received,
                    (unsigned long long)x10rt_lgl_stats.put.messages_received,
                    (unsigned long long)x10rt_lgl_stats.put.bytes_sent,
                    (unsigned long long)x10rt_lgl_stats.put_copied_bytes_sent,
                    (unsigned long long)x10rt_lgl_stats.put.messages_sent);
            fprintf(stderr, "Place: %lu   get_rx: %llu(&%llu)/%llu   get_tx: %llu(&%llu)/%llu\n",
                    (unsigned long)x10rt_lgl_here(), 
                    (unsigned long long)x10rt_lgl_stats.get.bytes_received,
                    (unsigned long long)x10rt_lgl_stats.get_copied_bytes_received,
                    (unsigned long long)x10rt_lgl_stats.get.messages_received,
                    (unsigned long long)x10rt_lgl_stats.get.bytes_sent,
                    (unsigned long long)x10rt_lgl_stats.get_copied_bytes_sent,
                    (unsigned long long)x10rt_lgl_stats.get.messages_sent);
        }
    }
    if (g.error_code == X10RT_ERR_OK) {
        //blocking_barrier();
    }
    x10rt_emu_coll_finalize();
    // a failure during init can mean these arrays are NULL
    if (g.naccels != NULL) {
        for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
            if (g.type != NULL && g.child != NULL) {
                switch (g.type[g.child[x10rt_lgl_here()][i]]) {
                    case X10RT_LGL_CUDA:
                    if (g.accel_ctxs != NULL) {
                        x10rt_cuda_finalize(static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]));
                    }
                    break;
                    default:
                    // we're shutting down, no point complaining now
                    break;
                }
            }
        }
    }

    x10rt_net_finalize();

    /* discard global node database */
    for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
        free(g.child[i]);
    }
    free(g.accel_ctxs);
    free(g.child);
    free(g.type);
    free(g.parent);
    free(g.naccels);
    free(g.error_msg);
}

x10rt_coll_type x10rt_lgl_coll_support () {
	x10rt_coll_type v = x10rt_net_coll_support();
	if (v == X10RT_COLL_NOCOLLECTIVES && checkBoolEnvVar(getenv("X10RT_FORCE_NATIVE_COLLECTIVES")))
        return X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES; // use local implementation
	else
		return v;
}


void x10rt_lgl_team_new (x10rt_place placec, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
    ESCAPE_IF_ERR;
    for (x10rt_place i=0 ; i<placec ; ++i) {
        if (placev[i] >= x10rt_lgl_nhosts()) {
            fatal("teams can only be across non-accelerator places.\n");
            return;
        }
    }
    if (has_collectives > X10RT_COLL_NOCOLLECTIVES) {
        x10rt_net_team_new(placec, placev, ch, arg);
    } else {
        x10rt_emu_team_new(placec, placev, ch, arg);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives > X10RT_COLL_NOCOLLECTIVES) {
        x10rt_net_team_del(team, role, ch, arg);
    } else {
        x10rt_emu_team_del(team, role, ch, arg);
        while (x10rt_emu_coll_probe());
    }
}

x10rt_place x10rt_lgl_team_sz (x10rt_team team)
{
    if (g.error_code != X10RT_ERR_OK) return 0;
    if (has_collectives > X10RT_COLL_NOCOLLECTIVES) {
        return x10rt_net_team_sz(team);
    } else {
        return x10rt_emu_team_sz(team);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_team_split (x10rt_team parent, x10rt_place parent_role,
                           x10rt_place color, x10rt_place new_role,
                           x10rt_completion_handler2 *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives > X10RT_COLL_NOCOLLECTIVES) {
        x10rt_net_team_split(parent, parent_role, color, new_role, ch, arg);
    } else {
        x10rt_emu_team_split(parent, parent_role, color, new_role, ch, arg);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_barrier (x10rt_team team, x10rt_place role,
                        x10rt_completion_handler *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives >= X10RT_COLL_BARRIERONLY) {
        x10rt_net_barrier(team, role, ch, arg);
    } else {
        x10rt_emu_barrier(team, role, ch, arg);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_bcast (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives >= X10RT_COLL_ALLBLOCKINGCOLLECTIVES) {
        x10rt_net_bcast(team, role, root, sbuf, dbuf, el, count, ch, arg);
    } else {
        x10rt_emu_bcast(team, role, root, sbuf, dbuf, el, count, ch, arg);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_scatter (x10rt_team team, x10rt_place role,
                        x10rt_place root, const void *sbuf, void *dbuf,
                        size_t el, size_t count,
                        x10rt_completion_handler *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives >= X10RT_COLL_ALLBLOCKINGCOLLECTIVES) {
        x10rt_net_scatter(team, role, root, sbuf, dbuf, el, count, ch, arg);
    } else {
        x10rt_emu_scatter(team, role, root, sbuf, dbuf, el, count, ch, arg);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_alltoall (x10rt_team team, x10rt_place role,
                         const void *sbuf, void *dbuf,
                         size_t el, size_t count,
                         x10rt_completion_handler *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives >= X10RT_COLL_ALLBLOCKINGCOLLECTIVES) {
        x10rt_net_alltoall(team, role, sbuf, dbuf, el, count, ch, arg);
    } else {
        x10rt_emu_alltoall(team, role, sbuf, dbuf, el, count, ch, arg);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_reduce (x10rt_team team, x10rt_place role,
                       x10rt_place root, const void *sbuf, void *dbuf,
                       x10rt_red_op_type op, 
                       x10rt_red_type dtype,
                       size_t count,
                       x10rt_completion_handler *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives >= X10RT_COLL_ALLBLOCKINGCOLLECTIVES) {
        x10rt_net_reduce(team, role, root, sbuf, dbuf, op, dtype, count, ch, arg);
    } else {
        x10rt_emu_reduce(team, role, root, sbuf, dbuf, op, dtype, count, ch, arg, false);
        while (x10rt_emu_coll_probe());
    }
}

void x10rt_lgl_allreduce (x10rt_team team, x10rt_place role,
                          const void *sbuf, void *dbuf,
                          x10rt_red_op_type op, 
                          x10rt_red_type dtype,
                          size_t count,
                          x10rt_completion_handler *ch, void *arg)
{
    ESCAPE_IF_ERR;
    if (has_collectives >= X10RT_COLL_ALLBLOCKINGCOLLECTIVES) {
        x10rt_net_allreduce(team, role, sbuf, dbuf, op, dtype, count, ch, arg);
    } else {
        x10rt_emu_reduce(team, role, 0, sbuf, dbuf, op, dtype, count, ch, arg, true);
        while (x10rt_emu_coll_probe());
    }
}
