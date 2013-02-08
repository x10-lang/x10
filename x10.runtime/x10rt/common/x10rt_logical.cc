#include <cstdio>
#include <cstdarg>
#include <cstring>
#include <cassert>
#include <cctype>

#include <unistd.h>

#include <x10rt_logical.h>
#include <x10rt_net.h>
#include <x10rt_cuda.h>
#include <x10rt_internal.h>
#include <x10rt_ser.h>
#include <x10rt_front.h>

static void error(const char* format, ...)
{
    va_list ap;
    va_start(ap, format);
    vfprintf(stderr, format, ap);
    va_end(ap);
    if (!x10rt_run_as_library()) abort();
}

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
    };

    bool has_remote_op;
    bool has_collectives;

    x10rt_lgl_ctx g;
}

x10rt_stats x10rt_lgl_stats;

static void one_setter (void *arg)
{ *((int*)arg) = 1; }

x10rt_place x10rt_lgl_nplaces (void)
{
    return g.nplaces;
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

        case X10RT_LGL_SPE:
        return 0;

        case X10RT_LGL_CUDA:
        return x10rt_cuda_ndevs();

        default:
        error("Invalid parameter.\n");
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

    void blocking_barrier (void)
    {
            int finished = 0;
            x10rt_lgl_barrier(0, x10rt_lgl_here(), one_setter, &finished);
            while (!finished) { x10rt_emu_coll_probe(); x10rt_net_probe(); }
    }

    void x10rt_lgl_internal_init (x10rt_lgl_cfg_accel *cfgv, x10rt_place cfgc, x10rt_msg_type *counter)
    {
        x10rt_emu_init(counter);
        x10rt_emu_coll_init(counter);
        usleep(1000000); // sleep for 1 second
        has_remote_op = getenv("X10RT_EMULATE_REMOTE_OP")==NULL && 0!=x10rt_net_supports(X10RT_OPT_REMOTE_OP);
        has_collectives = getenv("X10RT_EMULATE_COLLECTIVES")==NULL && 0!=x10rt_net_supports(X10RT_OPT_COLLECTIVES);
        g.nhosts = x10rt_net_nhosts();

        x10rt_place num_local_spes = 0;
        x10rt_place num_local_cudas = 0;

        // discover accelerator situation
        unsigned int cuda_max_dev = x10rt_cuda_ndevs();
        unsigned int cell_max_dev = 2;

        // ensure user mapping can be realised
        for (x10rt_place i=0 ; i<cfgc ; ++i) {
            x10rt_lgl_cfg_accel *cfg = &cfgv[i];

            switch (cfg->cat) {

                case X10RT_LGL_CUDA:
                num_local_cudas++;
                if (cfg->index >= cuda_max_dev) {
                    error("CUDA reports %u devices, you cannot use device %u.\n",
                                   cuda_max_dev, cfg->index);
                }
                break;

                case X10RT_LGL_SPE:
                num_local_spes++;
                if (cfg->index >= cell_max_dev) {
                    error("Cell reports %u devices, you cannot use device %u.\n",
                                   cell_max_dev, cfg->index);
                }
                break;

                default:
                error("Invalid node category.\n");
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

                case X10RT_LGL_SPE:
                //g.cuda_ctxs[i] = x10rt_spe_setup(cfg->index);
                break;

                default:
                error("Invalid node category.\n");
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

        blocking_barrier();

        // Spread the knowledge of accelerators around
#ifdef ENABLE_CUDA        
        g.naccels[x10rt_lgl_here()] = cfgc;

        x10rt_place finish_counter = x10rt_lgl_nhosts()-1;
        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            if (i==x10rt_lgl_here()) continue;
            send_naccels(i, cfgc, &finish_counter);
        }
        while (finish_counter!=0) x10rt_net_probe();

        blocking_barrier();

        // Now we can calculate the total number of places
        g.nplaces = x10rt_lgl_nhosts();
        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            g.nplaces += g.naccels[i];
        }
#else
        g.nplaces = x10rt_lgl_nhosts();
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

        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            if (i==x10rt_lgl_here()) continue;
            for (x10rt_place j=0 ; j<cfgc ; ++j) {
                send_cat(i, j, cfgv[j].cat, &finish_counter);
            }
        }

        while (finish_counter!=0) x10rt_net_probe();
#else
        for (x10rt_place j=0; j<g.naccels[x10rt_lgl_here()]; ++j)
            g.type[g.child[x10rt_lgl_here()][j]] = cfgv[j].cat;
#endif
        blocking_barrier();
    }

}

void x10rt_lgl_init (int *argc, char ***argv,
                     x10rt_lgl_cfg_accel *cfgv, x10rt_place cfgc, x10rt_msg_type *counter)
{
    x10rt_net_init(argc, argv, counter);
    x10rt_lgl_internal_init(cfgv, cfgc, counter);
}

#define ENV "X10RT_ACCELS"

void x10rt_lgl_init (int *argc, char ***argv, x10rt_msg_type *counter)
{
    x10rt_net_init(argc, argv, counter);
    char env[1024] = "";
    sprintf(env, ENV"%lu",  (unsigned long)x10rt_net_here());
    const char *str = getenv(env);
    if (str==NULL) {
        sprintf(env, ENV);
        str = getenv(env);
    }
    if (str==NULL || *str=='\0' || !strcmp(str,"NONE") || !strcmp(str,"none")) {
        x10rt_lgl_internal_init(NULL, 0, counter);
    } else {
        int num_cudas = x10rt_lgl_local_accels(X10RT_LGL_CUDA);
        int num_cells = x10rt_lgl_local_accels(X10RT_LGL_SPE);

        if (!strcmp(str,"ALL") || !strcmp(str,"all")) {
            if (num_cudas + num_cells == 0) {
                x10rt_lgl_internal_init(NULL, 0, counter);
            } else {
                x10rt_lgl_cfg_accel *cfg = safe_malloc<x10rt_lgl_cfg_accel>(num_cudas+8*num_cells);
                int accel = 0;
                for (int i=0 ; i<num_cells ; ++i) {
                    for (int j=0 ; j<8 ; ++j) {
                        cfg[accel].cat = X10RT_LGL_SPE;
                        cfg[accel].index = i;
                        accel++;
                    }
                }
                for (int i=0 ; i<num_cudas ; ++i) {
                    cfg[accel].cat = X10RT_LGL_CUDA;
                    cfg[accel].index = i;
                    accel++;
                }
                x10rt_lgl_internal_init(cfg, num_cudas+8*num_cells, counter);
                free(cfg);
            }
        } else {
            int num_accels = 1;
            for (const char *c=str ; *c!='\0' ; ++c) num_accels += *c==',';
            x10rt_lgl_cfg_accel *cfg = safe_malloc<x10rt_lgl_cfg_accel>(num_accels);
            for (int i=0 ; i<num_accels ; ++i) {
                while (isspace(*str)) str++; // chase up white space
                int chars = strcspn(str,",");
                if (chars<5) {
                    error("%s contains invalid element at "
                                   "index %d: \"%.*s\"\n", env, i, chars, str);
                }
                if (!strncmp(str,"CELL",4) || !strncmp(str,"cell",4)) {
                    str += 4; chars -= 4;
                    char *endptr;
                    long index = strtol(str,&endptr,10);
                    while (isspace(*endptr)) endptr++; // chase up white space
                    if (endptr-str != chars) {
                        error("%s contains invalid number at "
                                       "index %d: \"%.*s\"\n", env, i, chars, str);
                    }
                    cfg[i].cat = X10RT_LGL_SPE;
                    cfg[i].index = index;
                } else if (!strncmp(str,"CUDA",4) || !strncmp(str,"cuda",4)) {
                    str += 4; chars -= 4;
                    char *endptr;
                    long index = strtol(str,&endptr,10);
                    while (isspace(*endptr)) endptr++; // chase up white space
                    if (endptr-str != chars) {
                        error("%s contains invalid number at "
                                       "index %d: \"%.*s\"\n", env, i, chars, str);
                    }
                    cfg[i].cat = X10RT_LGL_CUDA;
                    cfg[i].index = index;
                } else {
                    error("%s contains invalid element at "
                                   "index %d: \"%.*s\"\n", env, i, chars, str);
                }
                str += chars;
                str++; // the comma
            }
            x10rt_lgl_internal_init(cfg, num_accels, counter);
            free(cfg);
        }
    }
}

void x10rt_lgl_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *cb)
{ x10rt_net_register_msg_receiver(msg_type, cb); }

void x10rt_lgl_register_get_receiver (x10rt_msg_type msg_type,
                                      x10rt_finder *cb1, x10rt_notifier *cb2)
{ x10rt_net_register_get_receiver(msg_type, cb1, cb2); }

void x10rt_lgl_register_put_receiver (x10rt_msg_type msg_type,
                                      x10rt_finder *cb1, x10rt_notifier *cb2)
{ x10rt_net_register_put_receiver(msg_type, cb1, cb2); }

void x10rt_lgl_register_msg_receiver_cuda (x10rt_msg_type msg_type,
                                           x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                           const char *cubin, const char *kernel_name)
{
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_register_msg_receiver(cctx, msg_type, pre, post, cubin, kernel_name);
            } break;
            case X10RT_LGL_SPE: break;
            default:
            error("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_register_get_receiver_cuda (x10rt_msg_type msg_type,
                                           x10rt_finder *cb1, x10rt_notifier *cb2)
{
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_register_get_receiver(cctx, msg_type, cb1, cb2);
            } break;
            case X10RT_LGL_SPE: break;
            default:
            error("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_register_put_receiver_cuda (x10rt_msg_type msg_type,
                                           x10rt_finder *cb1, x10rt_notifier *cb2)
{
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_register_put_receiver(cctx, msg_type, cb1, cb2);
            } break;
            case X10RT_LGL_SPE: break;
            default:
            error("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_registration_complete (void)
{
    blocking_barrier();

    // accelerators
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_registration_complete(cctx);
            } break;
            case X10RT_LGL_SPE: break;
            default:
            error("Invalid node category.\n");
        }
    }
}

void x10rt_lgl_send_msg (x10rt_msg_params *p)
{
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
            } break;
            case X10RT_LGL_SPE: {
                error("SPE send_msg still unsupported.\n");
            } break;
            default: {
                error("Place %lu has invalid type %d in send_msg.\n",
                        (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        error("Routing of send_msg still unsupported.\n");
    }
}

void x10rt_lgl_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
    x10rt_place d = p->dest_place;

    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        x10rt_net_send_get(p, buf, len);
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_send_get(cctx, p, buf, len);
            } break;
            case X10RT_LGL_SPE: {
                error("SPE send_get still unsupported.\n");
            } break;
            default: {
                error("Place %lu has invalid type %d in send_get.\n",
                        (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        error("Routing of send_get still unsupported.\n");
    }
}

void x10rt_lgl_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
    x10rt_place d = p->dest_place;

    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        x10rt_net_send_put(p, buf, len);
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_send_put(cctx, p, buf, len);
            } break;
            case X10RT_LGL_SPE: {
                error("SPE send_put still unsupported.\n");
            } break;
            default: {
                error("Place %lu has invalid type %d in send_put.\n",
                        (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        error("Routing of send_put still unsupported.\n");
    }
}

void x10rt_lgl_remote_alloc (x10rt_place d, x10rt_remote_ptr sz,
                             x10rt_completion_handler3 *ch, void *arg)
{
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        error("Host remote_alloc still unsupported.\n");
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                ch((x10rt_remote_ptr)(size_t) x10rt_cuda_device_alloc(cctx, sz),arg);
                break;
            }
            case X10RT_LGL_SPE: {
                error("SPE remote_alloc still unsupported.\n");
            }
            default: {
                error("Place %lu has invalid type %d in remote_alloc.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        error("Routing of remote_alloc still unsupported.\n");
    }
}
void x10rt_lgl_remote_free (x10rt_place d, x10rt_remote_ptr ptr)
{
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        error("Host remote_free still unsupported.\n");
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_device_free(cctx, (void*)ptr);
            } break;
            case X10RT_LGL_SPE: {
                error("SPE remote_free still unsupported.\n");
            } break;
            default: {
                error("Place %lu has invalid type %d in remote_free.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        error("Routing of remote_free still unsupported.\n");
    }
}

void x10rt_lgl_remote_op (x10rt_place d, x10rt_remote_ptr remote_addr,
                          x10rt_op_type type, unsigned long long value)
{
    assert(d < x10rt_lgl_nplaces());

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
                error("CUDA remote ops still unsupported.\n");
            } break;
            case X10RT_LGL_SPE: {
                error("SPE remote ops still unsupported.\n");
            } break;
            default: {
                error("Place %lu has invalid type %d in remote_op_xor.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        error("Routing of remote ops still unsupported.\n");
    }
}
    
void x10rt_lgl_remote_ops (x10rt_remote_op_params *opv, size_t opc)
{
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
                        case X10RT_LGL_SPE: {
                            error("SPE remote ops still unsupported.\n");
                        } break;
                        default: {
                            error("Place %lu has invalid type %d in remote_op_xor.\n",
                                           (unsigned long)d, (int)x10rt_lgl_type(d));
                        }
                    }
                } else {
                    error("Routing of remote ops still unsupported.\n");
                }
            }
        #endif
        #endif
        x10rt_net_remote_ops(opv, opc);
    } else {
        for (size_t i=0 ; i<opc ; ++i) {
            x10rt_emu_remote_op(opv[i].dest, opv[i].dest_buf, (x10rt_op_type)opv[i].op, opv[i].value);
        }
    }

}
    
x10rt_remote_ptr x10rt_lgl_register_mem (void *ptr, size_t len)
{ return x10rt_net_register_mem(ptr, len); }

void x10rt_lgl_blocks_threads (x10rt_place d, x10rt_msg_type type, int dyn_shm,
                               int *blocks, int *threads, const int *cfg)
{
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
            case X10RT_LGL_SPE: {
                *blocks = 8; *threads = 1;
            } break;
            default: {
                error("Place %lu has invalid type %d in remote_op_xor.\n",
                               (unsigned long)d, (int)x10rt_lgl_type(d));
            }
        }
    } else {
        error("Routing of remote ops still unsupported.\n");
    }
}


void x10rt_lgl_probe (void)
{
    x10rt_net_probe();
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA:
            x10rt_cuda_probe(static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]));
            break;
            case X10RT_LGL_SPE:
            error("SPE still unsupported\n");
            break;
            default:
            error("Invalid node category.\n");
        }
    }
    // advance collectives as much as possible
    while (x10rt_emu_coll_probe());
}

void x10rt_lgl_blocking_probe (void)
{
    // first attempt to make progress on collectives
    if (x10rt_emu_coll_probe()) {
        // unsafe to block if collectives have made progress
        x10rt_lgl_probe();
        return;
    }
#if !defined(__bgp__)
    // blocking probe
    x10rt_net_blocking_probe();
#else
    // Compatibility hack with pgas_bgp; treat blocking probe as just a probe
    x10rt_lgl_probe();
#endif
    // advance collectives as much as possible
    while (x10rt_emu_coll_probe());
}


void x10rt_lgl_finalize (void)
{
    if (getenv("X10RT_RXTX")) {
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
    blocking_barrier();
    x10rt_emu_coll_finalize();
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA:
            x10rt_cuda_finalize(static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]));
            break;
            case X10RT_LGL_SPE:
            error("SPE still unsupported\n");
            break;
            default:
            error("Invalid node category.\n");
        }
    }
    free(g.accel_ctxs);

    x10rt_net_finalize();

    /* discard global node database */
    for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
        free(g.child[i]);
    }
    free(g.child);
    free(g.type);
    free(g.parent);
    free(g.naccels);

}

void x10rt_lgl_team_new (x10rt_place placec, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
    for (x10rt_place i=0 ; i<placec ; ++i) {
        if (placev[i] >= x10rt_lgl_nhosts()) {
            error("teams can only be across non-accelerator places.\n");
        }
    }
    if (has_collectives) {
        x10rt_net_team_new(placec, placev, ch, arg);
    } else {
        x10rt_emu_team_new(placec, placev, ch, arg);
    }
}

void x10rt_lgl_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
    if (has_collectives) {
        x10rt_net_team_del(team, role, ch, arg);
    } else {
        x10rt_emu_team_del(team, role, ch, arg);
    }
}

x10rt_place x10rt_lgl_team_sz (x10rt_team team)
{
    if (has_collectives) {
        return x10rt_net_team_sz(team);
    } else {
        return x10rt_emu_team_sz(team);
    }
}

void x10rt_lgl_team_split (x10rt_team parent, x10rt_place parent_role,
                           x10rt_place color, x10rt_place new_role,
                           x10rt_completion_handler2 *ch, void *arg)
{
    if (has_collectives) {
        x10rt_net_team_split(parent, parent_role, color, new_role, ch, arg);
    } else {
        x10rt_emu_team_split(parent, parent_role, color, new_role, ch, arg);
    }
}

void x10rt_lgl_barrier (x10rt_team team, x10rt_place role,
                        x10rt_completion_handler *ch, void *arg)
{
    if (has_collectives) {
        x10rt_net_barrier(team, role, ch, arg);
    } else {
        x10rt_emu_barrier(team, role, ch, arg);
    }
}

void x10rt_lgl_bcast (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
    if (has_collectives) {
        x10rt_net_bcast(team, role, root, sbuf, dbuf, el, count, ch, arg);
    } else {
        x10rt_emu_bcast(team, role, root, sbuf, dbuf, el, count, ch, arg);
    }
}

void x10rt_lgl_scatter (x10rt_team team, x10rt_place role,
                        x10rt_place root, const void *sbuf, void *dbuf,
                        size_t el, size_t count,
                        x10rt_completion_handler *ch, void *arg)
{
    if (has_collectives) {
        x10rt_net_scatter(team, role, root, sbuf, dbuf, el, count, ch, arg);
    } else {
        x10rt_emu_scatter(team, role, root, sbuf, dbuf, el, count, ch, arg);
    }
}

void x10rt_lgl_alltoall (x10rt_team team, x10rt_place role,
                         const void *sbuf, void *dbuf,
                         size_t el, size_t count,
                         x10rt_completion_handler *ch, void *arg)
{
    if (has_collectives) {
        x10rt_net_alltoall(team, role, sbuf, dbuf, el, count, ch, arg);
    } else {
        x10rt_emu_alltoall(team, role, sbuf, dbuf, el, count, ch, arg);
    }
}

void x10rt_lgl_allreduce (x10rt_team team, x10rt_place role,
                          const void *sbuf, void *dbuf,
                          x10rt_red_op_type op, 
                          x10rt_red_type dtype,
                          size_t count,
                          x10rt_completion_handler *ch, void *arg)
{
    if (has_collectives) {
        x10rt_net_allreduce(team, role, sbuf, dbuf, op, dtype, count, ch, arg);
    } else {
        x10rt_emu_allreduce(team, role, sbuf, dbuf, op, dtype, count, ch, arg);
    }
}


void x10rt_lgl_get_stats (x10rt_stats *s)
{
    *s = x10rt_lgl_stats;
}

void x10rt_lgl_set_stats (x10rt_stats *s)
{
    x10rt_lgl_stats = *s;
}


void x10rt_lgl_zero_stats (x10rt_stats *s)
{
    memset(s, 0, sizeof(*s));
}


