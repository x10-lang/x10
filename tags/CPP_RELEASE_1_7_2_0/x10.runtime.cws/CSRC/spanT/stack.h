#ifndef _STACK_H_
#define _STACK_H_
 
void push(int a, volatile int * stack, volatile int *top);
int pop(volatile int * stack, volatile int *top, volatile int bottom);
int is_empty(volatile int * stack, volatile int * top, volatile int * bottom);

#endif

