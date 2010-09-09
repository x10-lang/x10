#include "nbc_internal.h"

int NBC_Icomm_split(MPI_Comm comm, 
        int color, int key, 
        NBC_Handle *handle) {

    int res;

    res = NBC_Init_handle(handle, comm);
    if (NBC_OK != res) {
        fprintf(stderr, "Error in NBC_Init_handle\n");
        return res;
    }

    handle->comm_out = MPI_COMM_NULL;
    handle->comm_split_status = NBC_CONTINUE;
    handle->request_type = NBC_COMM_SPLIT_REQ;

    if (pthread_mutex_lock(&GNBC_thread_pool.lock)) {
        perror("pthread_mutex_lock");
        return NULL;
    }

    GNBC_thread_pool.msg_type = NBC_THREAD_COMM_SPLIT;
    GNBC_thread_pool.arg_u.comm_split_args.comm   = comm;
    GNBC_thread_pool.arg_u.comm_split_args.color  = color;
    GNBC_thread_pool.arg_u.comm_split_args.key    = key;
    GNBC_thread_pool.arg_u.comm_split_args.handle = handle;

    if (pthread_cond_signal(&GNBC_thread_pool.cond)) {
        perror("pthread_cond_signal");
    }

    if (pthread_mutex_unlock(&GNBC_thread_pool.lock)) {
        perror("pthread_mutex_unlock");
        return NULL;
    }

    return NBC_OK;
}

static int do_comm_split() {

    int res;

    if (MPI_SUCCESS != (res =
                MPI_Comm_split(GNBC_thread_pool.arg_u.comm_split_args.comm,
                    GNBC_thread_pool.arg_u.comm_split_args.color,
                    GNBC_thread_pool.arg_u.comm_split_args.key,
                    &GNBC_thread_pool.arg_u.comm_split_args.handle->comm_out))) {
        fprintf(stderr, "Error in MPI_Comm_split\n");
        return res;
    }

    * ((volatile int *) &GNBC_thread_pool.arg_u.comm_split_args.handle->comm_split_status) = NBC_OK;

    return NBC_OK;
}

void * NBC_Helper_thread(void * arg) {

    int finished = 0;

    if (pthread_mutex_lock(&GNBC_thread_pool.lock)) {
        perror("pthread_mutex_lock");
        return NULL;
    }

    while (!finished) {
        if (pthread_cond_wait(&GNBC_thread_pool.cond, 
                    &GNBC_thread_pool.lock)) {
            perror("pthread_cond_wait");
        }

        switch(GNBC_thread_pool.msg_type) {
            case NBC_THREAD_COMM_SPLIT:
                if(NBC_OK != do_comm_split()) {
                    fprintf(stderr, "Error in Comm split\n");
                    exit(-1);
                }
                break;
            case NBC_THREAD_EXIT:
                finished = 1;
                break;
            default:
                fprintf(stderr, "Illegal instruction to helper thread - %d \n",
                        GNBC_thread_pool.msg_type);
                exit(-1);
        }
    }

    if (pthread_mutex_unlock(&GNBC_thread_pool.lock)) {
        perror("pthread_mutex_unlock");
        return NULL;
    }

    return NULL;
}
