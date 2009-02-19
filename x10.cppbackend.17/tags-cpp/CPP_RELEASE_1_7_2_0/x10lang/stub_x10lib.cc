#include <x10/x10lib.h>

int __x10_errno = 0;
namespace x10lib {
    static const bool TRACE = getenv("X10LIB_TRACE") != NULL;

    int __x10_my_place = 0;
    int __x10_num_places = 1;

    x10_err_t AsyncSpawnInlineAgg(x10_place_t target, x10_async_handler_t handler, void* args, size_t size) {
        if (TRACE) cerr << "Aggregating async " << handler << " in place " << target << " with arg size " << size << endl;
        AsyncSwitch(handler, args, 1);
        return X10_OK;
    }

    x10_err_t AsyncSpawnInline(x10_place_t target, x10_async_handler_t handler, void* args, size_t size) {
        if (TRACE) cerr << "Spawning async " << handler << " in place " << target << " with arg size " << size << endl;
        AsyncSwitch(handler, args, 1);
        return X10_OK;
    }

    void SyncGlobal() {
        if (TRACE) cerr << "Global Fence" << endl;
    }

    x10_err_t Broadcast(void* buffer, size_t nbytes) {
        if (TRACE) cerr << "Broadcasting " << nbytes << " bytes at " << buffer << endl;
        return X10_OK;
    }

//    template<typename T> void reduce(T* loc) {
//        if (TRACE) cerr << "Reduction to " << loc << endl;
//    }

    x10_err_t AsyncArrayPut(void* src, x10_closure_t args, size_t len, int target, x10_switch_t clk)
    {
        if (TRACE) cerr << "AsyncArrayPut: Evaluating " << args->handler << " with arg size " << args->len << endl;
        void* dest = ArrayCopySwitch(args->handler, ((char*) args) + sizeof(x10lib::Closure));
        if (TRACE) cerr << "AsyncArrayPut: Copying " << len << " bytes from " << src << " to " << dest << " in place " << target << endl;
        ::memcpy(dest, src, len);
        return X10_OK;
    }

    x10_err_t AsyncArrayIput(void* src, x10_closure_t args, size_t len, int target, x10_switch_t clk)
    {
        x10_err_t res = AsyncArrayPut(src, args, len, target, clk);
        delete args;  // temporary
        return res;
    }

    void Poll(unsigned int cnt, x10_msg_state_t *state) {
        if (TRACE) cerr << "Poll: Polling " << cnt << " times" << endl;
    }

    x10_place_t here() {
        const_cast<int&>(__x10_my_place) = 0;
        return __x10_my_place;
    }

    int numPlaces() {
        const_cast<int&>(__x10_num_places) = 1;
        return __x10_num_places;
    }

    x10_err_t Init(x10_async_handler_t* handlers, int n) {
        if (TRACE) cerr << "Initializing..." << endl;
        return X10_OK;
    }
 
    x10_err_t Finalize() {
        if (TRACE) cerr << "Shutting down..." << endl;
        return X10_OK;
    }
 
    int FinishStart(int CS) {
        if (TRACE) cerr << "Begin finish @" << CS << endl;
        return CS;
    }

    void FinishEnd(Exception* a) {
        int ptr = (int)(((char*)a)-(char*)NULL);
        if (TRACE) cerr << "End finish: " << ptr << endl;
    }

    x10_err_t LAPIStylePut (int destPlace, size_t size, void* dest, void* src,
                            void* target_cntr, void* origin_cntr,
                            void* compl_cntr)
    {
        return X10_OK;
    }
    x10_err_t LAPIStyleWaitcntr (void* cntr, int value, int* ret) { return X10_OK; }
    x10_err_t LAPIStyleSetcntr (void* cntr, int value) { return X10_OK; }

    void ReduceCounterInit() { }
    void ReduceCounterWait(int depth) { }
    void ReduceTransferData(int low, size_t size, int depth, void *values) { }
    void ReduceDataEnqueue(void *var, size_t size) { }
    void *ReduceDataRetrieve(int index) { return NULL; }
    int ReduceDataGetCount() { return 0; }
    void ReduceDataInitCount() { }
    void ReduceTempStorageInit(size_t size) { }
    void *ReduceTempStorageGet(size_t size, int i, int j) { return NULL; }
}

