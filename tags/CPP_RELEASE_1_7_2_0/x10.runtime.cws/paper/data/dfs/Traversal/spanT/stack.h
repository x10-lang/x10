#ifndef _STACK_H_
#define _STACK_H_
 
void push(int a, int * stack, volatile int *top);
int pop(int * stack, volatile int *top, volatile int bottom);
int is_empty(int * stack, volatile int * top, volatile int * bottom);

#endif

