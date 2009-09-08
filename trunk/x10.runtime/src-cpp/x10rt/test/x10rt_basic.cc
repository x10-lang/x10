#include <cstdlib>
#include <cstdio>
#include <cstring>

#include <x10rt_api.h>

// {{{ nano_time
#include <sys/time.h>

long long nano_time() {
    struct ::timeval tv;
    gettimeofday(&tv, NULL);
    return (long long)(tv.tv_sec * 1000000000LL + tv.tv_usec * 1000LL);
} // }}}


enum id {
    UNUSED, // pgas currently broken for id==0
    PING_ID, PONG_ID,
    PING_PUT_ID, PONG_PUT_ID,
    PING_GET_ID, PONG_GET_ID,
    QUIT_ID
};

char *buf, *ping_buf, *pong_buf; 
size_t len = 1024;
bool validate = false;
unsigned long pongs_outstanding = 0;
bool finished = false;


// {{{ msg handlers
static void recv_msg_ping (const x10rt_msg_params &p)
{
    if (validate && memcmp(buf, (const char*)p.msg, len)) {
        fprintf(stderr, "\nReceived scrambled ping message (len: %lu).\n", p.len);
        abort();
    }
    void *tmp = x10rt_msg_realloc(NULL,0,p.len);
    memcpy(tmp,p.msg,p.len);
    x10rt_msg_params p2 = {0, PONG_ID, tmp, p.len};
    x10rt_send_msg(p2);
}

static void recv_msg_pong (const x10rt_msg_params &p)
{
    if (validate && memcmp(buf, (const char*)p.msg, len)) {
        fprintf(stderr, "\nReceived scrambled pong message (len: %lu).\n", p.len);
        abort();
    }
    pongs_outstanding--;
} // }}}


// {{{ put handlers
static void *recv_put_ping_hh (const x10rt_msg_params &, unsigned long)
{
    return ping_buf;
}
static void recv_put_ping (const x10rt_msg_params &, unsigned long len)
{
    if (validate && memcmp(buf, ping_buf, len)) {
        fprintf(stderr, "\nReceived scrambled ping message (len: %lu).\n", len);
        abort();
    }
    x10rt_msg_params p2 = {0, PONG_PUT_ID, NULL, 0};
    x10rt_send_put(p2, buf, len);
}

static void *recv_put_pong_hh (const x10rt_msg_params &, unsigned long)
{
    return pong_buf;
}
static void recv_put_pong (const x10rt_msg_params &, unsigned long len)
{
    if (validate && memcmp(buf, pong_buf, len)) {
        fprintf(stderr, "\nReceived scrambled pong message (len: %lu).\n", len);
        abort();
    }
    pongs_outstanding--;
} // }}}


// {{{ get handlers
static void *recv_get_ping_hh (const x10rt_msg_params &)
{
    return buf;
}
static void recv_get_ping (const x10rt_msg_params &p, unsigned long len)
{
    if (validate && memcmp(buf, ping_buf, len)) {
        fprintf(stderr, "\nReceived scrambled ping message (len: %lu).\n", len);
        abort();
    }
    // send to dest place again
    x10rt_msg_params p2 = {p.dest_place, PONG_GET_ID, NULL, 0};
    x10rt_send_get(p2, pong_buf, len);
}

static void *recv_get_pong_hh (const x10rt_msg_params &)
{
    return buf;
}
static void recv_get_pong (const x10rt_msg_params &, unsigned long len)
{
    if (validate && memcmp(buf, pong_buf, len)) {
        fprintf(stderr, "\nReceived scrambled pong message (len: %lu).\n", len);
        abort();
    }
    pongs_outstanding--;
} // }}}


void recv_quit(const x10rt_msg_params &) { finished = true; }


// {{{ show_help
void show_help(FILE *out, char* name)
{
    fprintf(out,"Usage: %s <args>\n", name);
    fprintf(out,"-h (--help)        ");
    fprintf(out,"this message\n");
    fprintf(out,"-l (--length) <n>      ");
    fprintf(out,"size of individual message\n");
    fprintf(out,"-b (--batch) <n>       ");
    fprintf(out,"number of pongs to wait for in parallel (window size)\n");
    fprintf(out,"-i (--iterations) <n>  ");
    fprintf(out,"top-level iterations (round trips)\n");
    fprintf(out,"-v (--validate)    ");
    fprintf(out,"check whether messages are mangled in transit\n");
    fprintf(out,"-p (--put)    ");
    fprintf(out,"use x10rt_send_put instead of x10rt_send_msg\n");
    fprintf(out,"-g (--get)    ");
    fprintf(out,"use x10rt_send_get instead of x10rt_send_msg\n");
    fprintf(out,"-a (--auto)    ");
    fprintf(out,"test a variety of --length and --batch\n");
} // }}}


// {{{ run_test
long long run_test(unsigned long iters,
                   unsigned long batch,
                   unsigned long len,
                   bool put = false,
                   bool get = false)
{
    long long nanos = -nano_time();
    for (unsigned long i=0 ; i<iters ; ++i) {
        for (unsigned long j=0 ; j<batch ; ++j) {
            for (unsigned long k=1 ; k<x10rt_nplaces() ; ++k) {
                if (put) {
                    x10rt_msg_params p = {k, PING_PUT_ID, NULL, 0};
                    x10rt_send_put(p, buf, len);
                } else if (get) {
                    x10rt_msg_params p = {k, PING_GET_ID, NULL, 0};
                    x10rt_send_get(p, ping_buf, len);
                } else {
                    void *tmp = x10rt_msg_realloc(NULL,0,len);
                    memcpy(tmp,buf,len);
                    x10rt_msg_params p = {k, PING_ID, tmp, len};
                    x10rt_send_msg(p);
                }
                pongs_outstanding++;
            }
        }
        while (pongs_outstanding) x10rt_probe();
    }
    nanos += nano_time();
    return nanos;
} // }}}


// {{{ main
int main(int argc, char **argv)
{
    x10rt_register_msg_receiver(PING_ID, &recv_msg_ping);
    x10rt_register_msg_receiver(PONG_ID, &recv_msg_pong);
    x10rt_register_put_receiver(PING_PUT_ID, &recv_put_ping_hh, &recv_put_ping);
    x10rt_register_put_receiver(PONG_PUT_ID, &recv_put_pong_hh, &recv_put_pong);
    x10rt_register_get_receiver(PING_GET_ID, &recv_get_ping_hh, &recv_get_ping);
    x10rt_register_get_receiver(PONG_GET_ID, &recv_get_pong_hh, &recv_get_pong);
    x10rt_register_msg_receiver(QUIT_ID, &recv_quit);

    x10rt_registration_complete();

    size_t num_messages=320;
    unsigned long batch = 100;
    bool put = false;
    bool get = false;
    bool automatic = false;

    for (int i=1 ; i<argc && x10rt_here()==0 ; ++i) {
        if (!strcmp(argv[i], "--help")) {
            show_help(stdout,argv[0]);
            exit(EXIT_SUCCESS);
        } else if (!strcmp(argv[i], "-h")) {
            show_help(stdout,argv[0]);
            exit(EXIT_SUCCESS);

        } else if (!strcmp(argv[i], "--length")) {
            len = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-l")) {
            len = strtoul(argv[++i],NULL,0);

        } else if (!strcmp(argv[i], "--batch")) {
            batch = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-b")) {
            batch = strtoul(argv[++i],NULL,0);

        } else if (!strcmp(argv[i], "--iterations")) {
            num_messages = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-i")) {
            num_messages = strtoul(argv[++i],NULL,0);

        } else if (!strcmp(argv[i], "--validate")) {
            validate = true;
        } else if (!strcmp(argv[i], "-v")) {
            validate = true;

        } else if (!strcmp(argv[i], "--auto")) {
            automatic = true;
        } else if (!strcmp(argv[i], "-a")) {
            automatic = true;

        } else if (!strcmp(argv[i], "--put")) {
            put = true;
        } else if (!strcmp(argv[i], "-p")) {
            put = true;

        } else if (!strcmp(argv[i], "--get")) {
            get = true;
        } else if (!strcmp(argv[i], "-g")) {
            get = true;

        } else {
            fprintf(stderr,"Didn't understand: \"%s\"\n", argv[i]);
            show_help(stderr,argv[0]);
            exit(EXIT_FAILURE);
        }
    }

    if (put && get) {
        fprintf(stderr, "You can't specify both put and get.\n");
        show_help(stderr,argv[0]);
        exit(EXIT_FAILURE);
    }

    {
        size_t sz = len < 1024*1024 ? 1024*1024 : len;
        buf = (char*)malloc(sz);
        ping_buf = (char*)malloc(sz);
        pong_buf = (char*)malloc(sz);
        for (size_t i=0 ; i<sz ; ++i) {
            buf[i] = i;
        }
    }

    if (x10rt_here()==0) {

        if (automatic) {
            printf("         ");
            for (int j=1 ; j<=16 ; ++j) {
                    printf("%3d  ", j);
            }
            printf(" b/w (MB)\n");
            fflush(stdout);
            for (int l=0 ; l<512*1024 ; l=(l*2)>0?(l*2):1) {
                printf("%8d ", l);
                float micros = 0;
                for (int j=1 ; j<=16 ; ++j) {
                    micros = run_test(num_messages/j, j, l, put, get)
                             / 1E3 / num_messages / 2;
                    printf("%4.0f ",micros);
                }
                printf("%0.3f\n", l/micros);
                fflush(stdout);
            }
        } else {
            float micros = run_test(num_messages/batch, batch, len, put, get)
                           / 1E3 / num_messages / 2;
            printf("Time taken: %f us  Bandwidth: %f MB/s\n",micros,len/micros);
        }

        for (unsigned long i=1 ; i<x10rt_nplaces() ; ++i) {
            x10rt_msg_params p = {i, QUIT_ID, NULL, 0};
            x10rt_send_msg(p);
        }
        finished = true;
    }

    while (!finished) x10rt_probe();

    x10rt_finalize();

    return EXIT_SUCCESS;
} // }}}

// vim: shiftwidth=4:tabstop=4:expandtab

