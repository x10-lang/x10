/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2013.
 */

#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <cstdlib>
#include <cstring>

#include <iostream>
#include <iomanip>

#include <x10rt_front.h>
#include <x10rt_ser.h>

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

double do_work (unsigned long long iterations)
{
    double r = 0;
    for (unsigned long long i=0 ; i<iterations ; ++i) {
        double x = random()/double(RAND_MAX);
        double y = random()/double(RAND_MAX);
        if (x*x + y*y <= 1) r += 1;
    }
    return r;
}

x10rt_msg_type DO_WORK_ID, DONE_WORK_ID, QUIT_ID;

unsigned long work_outstanding = 0;
bool finished = false;
double num_in_circle = 0.0;
double num_tried = 0.0;


// {{{ msg handlers

static void recv_msg_do_work (const x10rt_msg_params *p)
{
    x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
    uint64_t iterations; x10rt_deserbuf_read(&b, &iterations);
    double r = do_work(iterations);
    x10rt_serbuf b2; x10rt_serbuf_init(&b2, 0, DONE_WORK_ID);
    x10rt_serbuf_write(&b2, &r);
    x10rt_serbuf_write(&b2, &iterations);
    x10rt_send_msg(&b2.p);
    x10rt_serbuf_free(&b2);
}

static void recv_msg_done_work (const x10rt_msg_params *p)
{
    x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
    double r; x10rt_deserbuf_read(&b, &r);
    uint64_t iterations; x10rt_deserbuf_read(&b, &iterations);
    num_in_circle += r;
    work_outstanding--;
    num_tried += iterations;
}

static void recv_msg_quit (const x10rt_msg_params *p)
{
    finished = true;
}

// }}}




// {{{ show_help
void show_help (std::ostream &out, char* name)
{
    if (x10rt_here()!=0) return;
    out << "Usage: "<<name<<" <args>\n"
        << "-h (--help)        "
        << "this message\n"
        << "-i (--iterations) <n>  "
        << "number of random evaluations (points in the quarter-square)\n";
} // }}}


// {{{ main
int main (int argc, char **argv)
{
    x10rt_error init_err = x10rt_init(&argc, &argv);
    if (init_err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            std::cerr << "X10RT fatal initialization error:  " << x10rt_error_msg() << std::endl;
        abort();
    }

    DO_WORK_ID   = x10rt_register_msg_receiver(&recv_msg_do_work, NULL, NULL, NULL, NULL);
    DONE_WORK_ID = x10rt_register_msg_receiver(&recv_msg_done_work, NULL, NULL, NULL, NULL);
    QUIT_ID      = x10rt_register_msg_receiver(&recv_msg_quit, NULL, NULL, NULL, NULL);

    x10rt_registration_complete();

    unsigned long long iterations=1000000*x10rt_nhosts();

    for (int i=1 ; i<argc; ++i) {
        if (!strcmp(argv[i], "--help")) {
            show_help(std::cout,argv[0]);
            exit(EXIT_SUCCESS);
        } else if (!strcmp(argv[i], "-h")) {
            show_help(std::cerr,argv[0]);
            exit(EXIT_SUCCESS);

        } else if (!strcmp(argv[i], "--iterations")) {
            iterations = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-i")) {
            iterations = strtoul(argv[++i],NULL,0);

        } else {
            if (x10rt_here()==0) {
                std::cerr << "Didn't understand: \""<<argv[i]<<"\"" << std::endl;
                show_help(std::cerr,argv[0]);
            }
            exit(EXIT_FAILURE);
        }
    }

    // round down to multiple of nhosts()
    uint64_t per_place_iterations = iterations / x10rt_nhosts();

    if (x10rt_here()==0) {

        long long nanos = -nano_time();
        work_outstanding = x10rt_nhosts();
        for (unsigned long p=1 ; p<x10rt_nhosts() ; ++p) {
            x10rt_serbuf b; x10rt_serbuf_init(&b, p, DO_WORK_ID);
            x10rt_serbuf_write(&b, &per_place_iterations);
            x10rt_send_msg(&b.p);
            x10rt_serbuf_free(&b);
        }
        num_in_circle += do_work(per_place_iterations);
        num_tried += per_place_iterations;
        work_outstanding--;
        x10rt_place num_dead = x10rt_ndead();
        while (work_outstanding) {
            x10rt_aborting_probe();
            x10rt_place now_dead = x10rt_ndead();
            if (now_dead != num_dead) {
                std::cout << now_dead-num_dead << " place(s) died.  Continuing..." << std::endl;
                num_dead = now_dead;
                work_outstanding--;
            }
        }
        nanos += nano_time();

        std::cout << "Time: "<<nanos/1E9<<" seconds  Value of pi: 4*"<<std::setprecision(10)<<num_in_circle<<"/"<<num_tried<<" = "
                  << std::setprecision(10)<<4*num_in_circle/num_tried<<std::endl;

        for (unsigned long i=1 ; i<x10rt_nhosts() ; ++i) {
            //if (x10rt_is_place_dead(i)) continue;
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
