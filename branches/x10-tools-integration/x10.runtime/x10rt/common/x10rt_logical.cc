#include <cstdio>
#include <cstring>
#include <cassert>
#include <cctype>

#include <arpa/inet.h>
#include <unistd.h>

#include <x10rt_logical.h>
#include <x10rt_net.h>
#include <x10rt_cuda.h>
#include <x10rt_internal.h>

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

    x10rt_lgl_ctx g;
}

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
        fprintf(stderr,"Invalid parameter.\n");
        abort();
        return 0;

    }
}

namespace {

    x10rt_msg_type send_finish_id;

    void send_finish(x10rt_place to, x10rt_remote_ptr counter_addr)
    {
        char *buf = (char*) x10rt_net_msg_realloc(NULL, 0,
                                                  sizeof(counter_addr));
        memcpy(buf, &counter_addr, sizeof(counter_addr));
        x10rt_msg_params p = { to, send_finish_id, buf, sizeof(counter_addr) };
        x10rt_net_send_msg(&p);
    }

    void recv_finish (const x10rt_msg_params *p)
    {
        x10rt_remote_ptr counter_addr;
        char *buf = (char*) p->msg;
        ::memcpy(&counter_addr, buf, sizeof(counter_addr));
        (*(x10rt_place*)(size_t)counter_addr)--;
    }

    x10rt_msg_type send_naccels_id;

    void send_naccels (x10rt_place to, x10rt_place num, x10rt_place *counter)
    {
        x10rt_place from = htonl(x10rt_net_here());
        num = htonl(num);
        x10rt_remote_ptr counter_addr = (x10rt_remote_ptr)(size_t)counter;
        char *buf = (char*) x10rt_net_msg_realloc(NULL, 0,
                                                  sizeof(from)+sizeof(num)+sizeof(counter_addr));
        size_t so_far = 0;
        memcpy(buf+so_far, &from, sizeof(from)); so_far+=sizeof(from);
        memcpy(buf+so_far, &num, sizeof(num)); so_far+=sizeof(num);
        memcpy(buf+so_far, &counter_addr, sizeof(counter_addr)); so_far+=sizeof(counter_addr);
        x10rt_msg_params p = { to, send_naccels_id, buf, so_far };
        x10rt_net_send_msg(&p);
    }

    void recv_naccels (const x10rt_msg_params *p)
    {
        x10rt_place from;
        x10rt_place num;
        x10rt_remote_ptr counter_addr;
        char *buf = (char*) p->msg;
        size_t so_far = 0;
        memcpy(&from, buf+so_far, sizeof(from)); so_far+=sizeof(from);
        memcpy(&num, buf+so_far, sizeof(num)); so_far+=sizeof(num);
        memcpy(&counter_addr, buf+so_far, sizeof(counter_addr)); so_far+=sizeof(counter_addr);
        from = ntohl(from);
        num = ntohl(num);

        g.naccels[from] = num;
        
        send_finish(from, counter_addr);
    }

    x10rt_msg_type send_cat_id;

    void send_cat (x10rt_place to, x10rt_place child, unsigned long cat, x10rt_place *counter)
    {
        x10rt_place from = htonl(x10rt_net_here());
        child = htonl(child);
        cat = htonl(cat);
        x10rt_remote_ptr counter_addr = (x10rt_remote_ptr)(size_t)counter;
        char *buf = (char*) x10rt_net_msg_realloc(NULL, 0, sizeof(from)+sizeof(child)+
                                                           sizeof(cat)+sizeof(counter_addr));
        size_t so_far = 0;
        memcpy(buf+so_far, &from, sizeof(from)); so_far+=sizeof(from);
        memcpy(buf+so_far, &child, sizeof(child)); so_far+=sizeof(child);
        memcpy(buf+so_far, &cat, sizeof(cat)); so_far+=sizeof(cat);
        memcpy(buf+so_far, &counter_addr, sizeof(counter_addr)); so_far+=sizeof(counter_addr);
        x10rt_msg_params p = { to, send_cat_id, buf, so_far };
        x10rt_net_send_msg(&p);
    }

    void recv_cat (const x10rt_msg_params *p)
    {
        x10rt_place from;
        x10rt_place child;
        unsigned long cat;
        x10rt_remote_ptr counter_addr;
        char *buf = (char*) p->msg;
        size_t so_far = 0;
        memcpy(&from, buf+so_far, sizeof(from)); so_far+=sizeof(from);
        memcpy(&child, buf+so_far, sizeof(child)); so_far+=sizeof(child);
        memcpy(&cat, buf+so_far, sizeof(cat)); so_far+=sizeof(cat);
        memcpy(&counter_addr, buf+so_far, sizeof(counter_addr)); so_far+=sizeof(counter_addr);
        from = ntohl(from);
        child = ntohl(child);
        cat = ntohl(cat);

        g.type[g.child[from][child]] = (x10rt_lgl_cat) cat;
        
        send_finish(from, counter_addr);
    }

    void x10rt_lgl_internal_init (x10rt_lgl_cfg_accel *cfgv, x10rt_place cfgc, x10rt_msg_type *counter)
    {
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
                    fprintf(stderr,"CUDA reports %u devices, you cannot use device %u.\n",
                                   cuda_max_dev, cfg->index);
                    abort();
                }
                break;

                case X10RT_LGL_SPE:
                num_local_spes++;
                if (cfg->index >= cell_max_dev) {
                    fprintf(stderr,"Cell reports %u devices, you cannot use device %u.\n",
                                   cell_max_dev, cfg->index);
                    abort();
                }
                break;

                default:
                fprintf(stderr,"Invalid node category.\n");
                abort();
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
                fprintf(stderr,"Invalid node category.\n");
                abort();
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

        x10rt_net_internal_barrier();

        // Spread the knowledge of accelerators around
        
        g.naccels[x10rt_lgl_here()] = cfgc;

        x10rt_place finish_counter = x10rt_lgl_nhosts()-1;
        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            if (i==x10rt_lgl_here()) continue;
            send_naccels(i, cfgc, &finish_counter);
        }
        while (finish_counter!=0) x10rt_net_probe();

        x10rt_net_internal_barrier();

        // Now we can calculate the total number of places
        g.nplaces = x10rt_lgl_nhosts();
        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            g.nplaces += g.naccels[i];
        }


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

        finish_counter = (x10rt_lgl_nhosts()-1) * cfgc;

        for (x10rt_place j=0 ; j<g.naccels[x10rt_lgl_here()] ; ++j) {
            g.type[g.child[x10rt_lgl_here()][j]] = cfgv[j].cat;
        }

        x10rt_net_internal_barrier();

        for (x10rt_place i=0 ; i<x10rt_lgl_nhosts() ; ++i) {
            if (i==x10rt_lgl_here()) continue;
            for (x10rt_place j=0 ; j<cfgc ; ++j) {
                send_cat(i, j, cfgv[j].cat, &finish_counter);
            }
        }

        while (finish_counter!=0) x10rt_net_probe();

        x10rt_net_internal_barrier();

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
    sprintf(env, ENV"%lu",  x10rt_net_here());
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
                    fprintf(stderr,"%s contains invalid element at "
                                   "index %d: \"%.*s\"\n", env, i, chars, str);
                    abort();
                }
                if (!strncmp(str,"CELL",4) || !strncmp(str,"cell",4)) {
                    str += 4; chars -= 4;
                    char *endptr;
                    long index = strtol(str,&endptr,10);
                    while (isspace(*endptr)) endptr++; // chase up white space
                    if (endptr-str != chars) {
                        fprintf(stderr,"%s contains invalid number at "
                                       "index %d: \"%.*s\"\n", env, i, chars, str);
                        abort();
                    }
                    cfg[i].cat = X10RT_LGL_SPE;
                    cfg[i].index = index;
                } else if (!strncmp(str,"CUDA",4) || !strncmp(str,"cuda",4)) {
                    str += 4; chars -= 4;
                    char *endptr;
                    long index = strtol(str,&endptr,10);
                    while (isspace(*endptr)) endptr++; // chase up white space
                    if (endptr-str != chars) {
                        fprintf(stderr,"%s contains invalid number at "
                                       "index %d: \"%.*s\"\n", env, i, chars, str);
                        abort();
                    }
                    cfg[i].cat = X10RT_LGL_CUDA;
                    cfg[i].index = index;
                } else {
                    fprintf(stderr,"%s contains invalid element at "
                                   "index %d: \"%.*s\"\n", env, i, chars, str);
                    abort();
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
            abort();
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
            abort();
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
            abort();
        }
    }
}

void x10rt_lgl_internal_barrier (void)
{
    x10rt_net_internal_barrier();

    // accelerators
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]);
                x10rt_cuda_registration_complete(cctx);
            } break;
            case X10RT_LGL_SPE: break;
            default:
            abort();
        }
    }
}

void *x10rt_lgl_msg_realloc (void *old, size_t old_sz, size_t new_sz)
{ return x10rt_net_msg_realloc(old, old_sz, new_sz); }
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
                fprintf(stderr,"SPE send_msg still unsupported.\n");
                abort();
            } break;
            default: {
                fprintf(stderr,"Place %lu has invalid type %d in send_msg.\n", d, x10rt_lgl_type(d));
                abort();
            }
        }
    } else {
        fprintf(stderr,"Routing of send_msg still unsupported.\n");
        abort();
    }
}

void *x10rt_lgl_get_realloc (void *old, size_t old_sz, size_t new_sz)
{ return x10rt_net_get_realloc(old, old_sz, new_sz); }
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
                fprintf(stderr,"SPE send_get still unsupported.\n");
                abort();
            } break;
            default: {
                fprintf(stderr,"Place %lu has invalid type %d in send_get.\n", d, x10rt_lgl_type(d));
                abort();
            }
        }
    } else {
        fprintf(stderr,"Routing of send_get still unsupported.\n");
        abort();
    }
}

void *x10rt_lgl_put_realloc (void *old, size_t old_sz, size_t new_sz)
{ return x10rt_net_put_realloc(old, old_sz, new_sz); }
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
                fprintf(stderr,"SPE send_put still unsupported.\n");
                abort();
            } break;
            default: {
                fprintf(stderr,"Place %lu has invalid type %d in send_put.\n", d, x10rt_lgl_type(d));
                abort();
            }
        }
    } else {
        fprintf(stderr,"Routing of send_put still unsupported.\n");
        abort();
    }
}

x10rt_remote_ptr x10rt_lgl_remote_alloc (x10rt_place d, x10rt_remote_ptr sz)
{
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        fprintf(stderr,"Host remote_alloc still unsupported.\n");
        abort();
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                return (x10rt_remote_ptr)(size_t) x10rt_cuda_device_alloc(cctx, sz);
            }
            case X10RT_LGL_SPE: {
                fprintf(stderr,"SPE remote_alloc still unsupported.\n");
                abort();
            }
            default: {
                fprintf(stderr,"Place %lu has invalid type %d in remote_alloc.\n",
                               d, x10rt_lgl_type(d));
                abort();
            }
        }
    } else {
        fprintf(stderr,"Routing of remote_alloc still unsupported.\n");
        abort();
    }
    return 0;
}
void x10rt_lgl_remote_free (x10rt_place d, x10rt_remote_ptr ptr)
{
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        fprintf(stderr,"Host remote_free still unsupported.\n");
        abort();
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                x10rt_cuda_ctx *cctx = static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[g.index[d]]);
                x10rt_cuda_device_free(cctx, (void*)ptr);
            } break;
            case X10RT_LGL_SPE: {
                fprintf(stderr,"SPE remote_free still unsupported.\n");
                abort();
            } break;
            default: {
                fprintf(stderr,"Place %lu has invalid type %d in remote_free.\n",
                               d, x10rt_lgl_type(d));
                abort();
            }
        }
    } else {
        fprintf(stderr,"Routing of remote_free still unsupported.\n");
        abort();
    }
}

void x10rt_lgl_remote_xor (x10rt_place d, x10rt_remote_ptr addr, long long update)
{
    assert(d < x10rt_lgl_nplaces());

    if (d < x10rt_lgl_nhosts()) {
        x10rt_net_remote_xor(d,addr,update);
    } else if (x10rt_lgl_parent(d) == x10rt_lgl_here()) {
        // local accelerator
        switch (x10rt_lgl_type(d)) {
            case X10RT_LGL_CUDA: {
                fprintf(stderr,"CUDA remote ops still unsupported.\n");
                abort();
            } break;
            case X10RT_LGL_SPE: {
                fprintf(stderr,"SPE remote ops still unsupported.\n");
                abort();
            } break;
            default: {
                fprintf(stderr,"Place %lu has invalid type %d in remote_op_xor.\n",
                               d, x10rt_lgl_type(d));
                abort();
            }
        }
    } else {
        fprintf(stderr,"Routing of remote ops still unsupported.\n");
        abort();
    }
}

void x10rt_lgl_remote_op_fence (void)
{
    x10rt_net_remote_op_fence();
}

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
                fprintf(stderr,"Place %lu has invalid type %d in remote_op_xor.\n",
                               d, x10rt_lgl_type(d));
                abort();
            }
        }
    } else {
        fprintf(stderr,"Routing of remote ops still unsupported.\n");
        abort();
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
            fprintf(stderr,"SPE still unsupported\n");
            break;
            default:
            abort();
        }
    }
}

void x10rt_lgl_finalize (void)
{
    for (x10rt_place i=0 ; i<g.naccels[x10rt_lgl_here()] ; ++i) {
        switch (g.type[g.child[x10rt_lgl_here()][i]]) {
            case X10RT_LGL_CUDA:
            x10rt_cuda_finalize(static_cast<x10rt_cuda_ctx*>(g.accel_ctxs[i]));
            break;
            case X10RT_LGL_SPE:
            fprintf(stderr,"SPE still unsupported\n");
            break;
            default:
            abort();
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
