/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

/**
 * @author bdlucas
 */

#include <stdio.h>
#include <sys/time.h>
#include <unistd.h>
#include <time.h>


// to be defined by benchmark
extern void init();
extern double operations();
extern double expected();
extern double once();
extern char *name;

double now() {
    struct timeval tv;
    struct timezone tz;
    gettimeofday(&tv, &tz);
    return tv.tv_sec + tv.tv_usec*1e-6;
}

double TIMING = 10.0; // how long to run tests in secs

int main(int argc, char *argv[]) {

    // initialize
    init();

    // functional check;
    printf("functional check\n");
    fflush(stdout);
    double result = once();
    if (result!=expected()) {
        printf("exected %f, result %f\n", expected(), result);
        fflush(stdout);
        return -1;
    }
        
    // run it for >TIMING secs
    printf("timing for >%gs\n", TIMING);
    fflush(stdout);
    double avg = 0.0;
    double min = 1e100;
    int count = 0;
    while (avg < TIMING) {
        double start = now();
        once();
        double t = now() - start;
        if (t<min)
            min = t;
        avg += t;
        count++;
    }
    avg /= count;

    // print info
    double ops = operations() / avg;
    printf("time: %.3f; count: %d; min/time: %.2f\n", avg, count, min/avg);
    if      (ops<1e6) printf("%.3g kop/s\n", ops/1e3);
    else if (ops<1e9) printf("%.3g Mop/s\n", ops/1e6);
    else              printf("%.3g Gop/s\n", ops/1e9);

    //time_t tt = time(NULL);
    //struct tm *t = localtime(&tt);
    //char ds[20], ts[20];
    //strftime(ds, sizeof(ds), "%F", t);
    //strftime(ts, sizeof(ts), "%T", t);

    printf("test=%s lg=cpp ops=%g\n", name, ops);
}
