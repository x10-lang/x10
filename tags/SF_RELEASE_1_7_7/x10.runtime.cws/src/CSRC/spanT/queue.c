void enQ(int r, int * Q, int *p_tail)
{
	Q[(*p_tail)++]=r;
}

int deQ(int * Q, int *p_head, int * p_tail)
{
	return(Q[(*p_head)++]);
}
