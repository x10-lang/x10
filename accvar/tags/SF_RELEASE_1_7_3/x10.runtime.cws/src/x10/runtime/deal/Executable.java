package x10.runtime.deal;

/**
 * Executables specify parallel tasks and 
 * are always  in a singly linked chain.
 * @author vj
 *
 */
public interface Executable {
	
	void compute(Worker w);
	
	Executable next();
	void setNext(Executable e);
}
