This piece of code demonstrates the usage of "Yield point".

We set up the task granulairty to be 16384 (i.e., 2^14), which is the number of all the vertices in the graph. Without using yield point, no work-stealing would have occured ( because each place must finish all the tasks at hand before it can probe network). With yield point, user can choose to mark the place to yield in their code, thus they can 

The additional parameter is "-yf ", which specifies the condition of where to yield in BC. 

Final note, normally user needs yield point only when even g=1 is too much a task granularity. That occurs (in BC's case) when the graph gets large (i.e. n>19), even processing one vertex takes a significant amount of time.
