package dpll;
public class V {
    final int index;
    final Clause[] pos, neg;
    public V(int index, Clause[] pos, Clause[] neg) {
	this.pos=pos;
	this.neg=neg;
	this.index=index;
    }

    /** Through all clauses that this variable occurs in, propagate
	this==value.
     */
    public void propagate(boolean value) {
    }
}
