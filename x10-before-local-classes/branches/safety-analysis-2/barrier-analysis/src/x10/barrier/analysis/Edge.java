package x10.barrier.analysis;
class Edge {
    State from;
    State to;
    static final int PAR = 1;
    static final int COND = 2;
    static final int NEXT = 3;
    static final int ASYNC = 4;
    int type; 
    
    public Edge(State fromState, State toState, int edgeType) {
	from = fromState;
	to = toState;
	type = edgeType;
	from.addOutgoingEdge(this);
	to.addIncomingEdge(this);
    }
}