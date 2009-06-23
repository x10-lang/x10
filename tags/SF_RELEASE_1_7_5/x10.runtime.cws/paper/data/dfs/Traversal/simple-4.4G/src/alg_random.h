#ifndef _ALG_RANDOM_H
#define _ALG_RANDOM_H

#include "simple.h"

#ifdef RRANDOM
void rrandom_init_th(THREADED);
void rrandom_destroy_th(THREADED);
void srrandom_th(unsigned int x, THREADED);
long rrandom_th(THREADED);

void srandomBit_th(unsigned int x, THREADED);
int  randomBit_th(THREADED);
#endif

#ifdef SPRNG
void rrand_init_th(int seed, THREADED);
void rrand_destroy_th(THREADED);
#define  rrand_th(t) isprng(THSPRNG)
#endif

long rrandom();
void srrandom(unsigned int x);
char *iinitstate(unsigned int seed, char *arg_state, int n);
char *ssetstate(char *arg_state);


#endif

