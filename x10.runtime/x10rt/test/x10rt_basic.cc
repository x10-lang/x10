#include <cstdlib>
#include <cstring>

#include <iostream>
#include <iomanip>

#include <x10rt_front.h>

// {{{ nano_time
#include <sys/time.h>

long long nano_time() {
    struct ::timeval tv;
    gettimeofday(&tv, NULL);
    return (long long)(tv.tv_sec * 1000000000LL + tv.tv_usec * 1000LL);
} // }}}

static void x10rt_aborting_probe (void)
{
    x10rt_error err = x10rt_probe();
    if (err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            std::cerr << "X10RT fatal error: " << x10rt_error_msg() << std::endl;
        abort();
    }
}


x10rt_msg_type INIT_ID, SEND_MSG_ID, ACK_SEND_MSG_ID, PUT_ID, ACK_PUT_ID, VALIDATE_PUT_ID, GET_ID, QUIT_ID;

char *src_buf, *dst_buf;
char *remote_src_buf, *remote_dst_buf;
size_t len = 1024;
bool validate = false;
unsigned long window = 100;
unsigned long pongs_outstanding = 0;
bool finished = false;


// {{{ msg handlers
static void recv_init (const x10rt_msg_params *p)
{
    remote_src_buf = ((char**)(p->msg))[0];
    remote_dst_buf = ((char**)(p->msg))[1];
}


static void recv_send_msg (const x10rt_msg_params *p)
{
    if (validate && (p->len > 4)) {
        int offset = (int)(*((char*)p->msg));
        if (memcmp(&src_buf[offset], p->msg, p->len)) {
            std::cerr << "\nReceived scrambled ping message (len: "<<p->len<<")." << std::endl;
            abort();
        }
    }
    x10rt_msg_params p2 = {0, ACK_SEND_MSG_ID, p->msg, p->len};
    x10rt_send_msg(&p2);
}

static void recv_ack_send_msg (const x10rt_msg_params *p)
{
    if (validate && (p->len > 4)) {
        int offset = (int)(*((char*)p->msg));
        if (memcmp(&src_buf[offset], p->msg, p->len)) {
            std::cerr << "\nReceived scrambled ping message (len: "<<p->len<<")." << std::endl;
            abort();
        }
    }
    pongs_outstanding--;
} // }}}


// {{{ put handlers
static void recv_put (const x10rt_msg_params *p, x10rt_copy_sz len)
{
    x10rt_msg_params p2 = {0, ACK_PUT_ID, NULL, 0};
    x10rt_send_msg(&p2);
}

static void recv_ack_put (const x10rt_msg_params *p)
{
    pongs_outstanding--;
}

static void recv_validate_put (const x10rt_msg_params *p)
{
    if (validate) {
        for (unsigned long j=0 ; j<window ; j++) {
            if (memcmp(src_buf+j, dst_buf+(j*len), len)) {
                std::cerr << "\nPut validation failed on window "<<j<<" (len "<<len<<")." << std::endl;
            }
        }
        memset(dst_buf, 0, window*len);
    }
    
    x10rt_msg_params p2 = {0, ACK_PUT_ID, NULL, 0};
    x10rt_send_msg(&p2);
} // }}}



// {{{ get handlers
static void recv_get (const x10rt_msg_params *p, x10rt_copy_sz len)
{
    pongs_outstanding--;
}

static void validate_get(unsigned long len) {
    if (!validate) return;

    for (unsigned long j=0 ; j<window ; j++) {
        if (memcmp(src_buf+j, dst_buf+(j*len), len)) {
            std::cerr << "\nGet validation failed on window "<<j<<" (len "<<len<<")." << std::endl;
        }
    }
    memset(dst_buf, 0, window*len);
}


// }}}

void recv_quit(const x10rt_msg_params *) { finished = true; }


// {{{ show_help
void show_help(std::ostream &out, char* name)
{
    if (x10rt_here()!=0) return;
    out << "Usage: "<<name<<" <args>\n"
        << "-h (--help)        "
        << "this message\n"
        << "-l (--length) <n>      "
        << "size of individual message\n"
        << "-w (--window) <n>       "
        << "number of pongs to wait for in parallel (window size)\n"
        << "-i (--iterations) <n>  "
        << "top-level iterations (round trips)\n"
        << "-v (--validate)    "
        << "check whether messages are mangled in transit\n"
        << "-p (--put)    "
        << "use x10rt_send_put instead of x10rt_send_msg\n"
        << "-g (--get)    "
        << "use x10rt_send_get instead of x10rt_send_msg\n"
        << "-a (--auto)    "
        << "test a variety of --length and --window" << std::endl;
} // }}}


// {{{ run_test(iters,len,put,get) -- sends iters*window*(nhosts-1) msgs
long long run_test(unsigned long iters,
                   unsigned long len,
                   bool put = false,
                   bool get = false)
{
    long long nanos = -nano_time();
    for (unsigned long i=0 ; i<iters ; ++i) {
        for (unsigned long j=0 ; j<window ; ++j) {
            for (unsigned long k=1 ; k<x10rt_nhosts() ; ++k) {
                if (put) {
                    x10rt_msg_params p = {k, PUT_ID, NULL, 0};
                    x10rt_send_put(&p, src_buf+j, remote_dst_buf+(j*len), len);
                } else if (get) {
                    x10rt_msg_params p = {k, GET_ID, NULL, 0};
                    x10rt_send_get(&p, remote_src_buf+j, dst_buf+(j*len), len);
                } else {
                    x10rt_msg_params p = {k, SEND_MSG_ID, src_buf+j, len};
                    x10rt_send_msg(&p);
                }
                pongs_outstanding++;
            }
        }
        while (pongs_outstanding) x10rt_aborting_probe();

        if (validate && put) {
            x10rt_msg_params p2 = {1, VALIDATE_PUT_ID, NULL, 0};
            x10rt_send_msg(&p2);
            pongs_outstanding++;
            while (pongs_outstanding) x10rt_aborting_probe();
        } else if (validate && get) {
            validate_get(len);
        }
    }
    nanos += nano_time();
    return nanos;
} // }}}


// {{{ main
int main(int argc, char **argv)
{
    x10rt_error init_err = x10rt_init(&argc, &argv);
    if (init_err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            std::cerr << "X10RT fatal initialization error:  " << x10rt_error_msg() << std::endl;
        abort();
    }

    INIT_ID = x10rt_register_msg_receiver(&recv_init, NULL, NULL, NULL, NULL);
    SEND_MSG_ID = x10rt_register_msg_receiver(&recv_send_msg, NULL, NULL, NULL, NULL);
    ACK_SEND_MSG_ID = x10rt_register_msg_receiver(&recv_ack_send_msg, NULL, NULL, NULL, NULL);
    GET_ID = x10rt_register_get_receiver(&recv_get, NULL);
    PUT_ID = x10rt_register_put_receiver(&recv_put, NULL);
    ACK_PUT_ID = x10rt_register_msg_receiver(&recv_ack_put, NULL, NULL, NULL, NULL);
    VALIDATE_PUT_ID = x10rt_register_msg_receiver(&recv_validate_put, NULL, NULL, NULL, NULL);
    QUIT_ID = x10rt_register_msg_receiver(&recv_quit, NULL, NULL, NULL, NULL);

    x10rt_registration_complete();

    if (x10rt_nhosts()==1) {
        std::cerr << "This is a communications test so needs at least 2 hosts." << std::endl;
        exit(EXIT_FAILURE);
    }

    size_t iterations=320;
    bool put = false;
    bool get = false;
    bool automatic = false;

    for (int i=1 ; i<argc; ++i) {
        if (!strcmp(argv[i], "--help")) {
            show_help(std::cout,argv[0]);
            exit(EXIT_SUCCESS);
        } else if (!strcmp(argv[i], "-h")) {
            show_help(std::cerr,argv[0]);
            exit(EXIT_SUCCESS);

        } else if (!strcmp(argv[i], "--length")) {
            len = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-l")) {
            len = strtoul(argv[++i],NULL,0);

        } else if (!strcmp(argv[i], "--window")) {
            window = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-w")) {
            window = strtoul(argv[++i],NULL,0);

        } else if (!strcmp(argv[i], "--iterations")) {
            iterations = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-i")) {
            iterations = strtoul(argv[++i],NULL,0);

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
            if (x10rt_here()==0) {
                std::cerr << "Didn't understand: \""<<argv[i]<<"\"" << std::endl;
                show_help(std::cerr,argv[0]);
            }
            exit(EXIT_FAILURE);
        }
    }

    if (put && get) {
        if (x10rt_here()==0) {
            std::cerr << "You can't specify both put and get." << std::endl;
            show_help(std::cerr,argv[0]);
        }
        exit(EXIT_FAILURE);
    }

    if ((put || get) && x10rt_nhosts()!=2) {
        if (x10rt_here()==0) {
            std::cerr << "Must have exactly two places for put/get test." << std::endl;
            show_help(std::cerr,argv[0]);
        }
        exit(EXIT_FAILURE);
    }
        

    {
        size_t src_sz = (automatic ? 1024 * 1024 : len) + window;
        size_t dst_sz = (src_sz - window) * window;
        src_buf = (char*)malloc(src_sz);
        dst_buf = (char*)malloc(dst_sz);
        for (size_t i=0; i<src_sz ; i++) {
            src_buf[i] = i;
        }
    }


    void** tmp = (void**)malloc(2*sizeof(void*));
    tmp[0] = src_buf;
    tmp[1] = dst_buf;
    x10rt_msg_params p = {x10rt_here()==0 ? 1 : 0, INIT_ID, tmp, 2*sizeof(void*)};
    x10rt_send_msg(&p);

    while (!remote_dst_buf) x10rt_aborting_probe();

    if (x10rt_here()==0) {
        // warm up
        for (int i=0 ; i<16 ; ++i) {
            run_test(1, 1024, false, false);
            run_test(1, 1024, true, false);
            run_test(1, 1024, false, true);
        }
    }

    if (x10rt_here()==0) {

        if (automatic) {
            if (validate && put) {
                std::cout << "Validation not implemented for put in auto mode.  Disabling validation" << std::endl;
                validate = false;
            }
            std::cout << "        ";
            for (int j=1 ; j<=16 ; ++j) {
				std::cout << std::setw(7) << j << " ";
            }
            std::cout << " b/w (MB)" << std::endl;
            for (int l=0 ; l<512*1024 ; l=(l*2)>0?(l*2):1) {
                std::cout << std::setw(8) << l;
                float micros = 0;
                for (int j=1 ; j<=16 ; j++) {
                    window = j;
                    micros = run_test(iterations/j, l, put, get)
                             / 1E3 / (iterations/j*j) / 2 / (x10rt_nhosts() - 1);
                    std::cout << std::setw(7) << std::setprecision(3) << micros << " ";
                }
                std::cout << std::setprecision(3) << l/micros << std::endl;
            }
        } else {
            float micros = run_test(iterations, len, put, get)
                           / 1E3 / (iterations*window) / 2 / (x10rt_nhosts() - 1);
            std::cout << "Half roundtrip time: "<<micros<<" us  Bandwidth: "<<len/micros<<" MB/s" << std::endl;
        }

        for (unsigned long i=1 ; i<x10rt_nhosts() ; ++i) {
            x10rt_msg_params p = {i, QUIT_ID, NULL, 0};
            x10rt_send_msg(&p);
        }
        finished = true;
    }

    while (!finished) x10rt_aborting_probe();

    x10rt_finalize();

    return EXIT_SUCCESS;
} // }}}

// vim: shiftwidth=4:tabstop=4:expandtab

