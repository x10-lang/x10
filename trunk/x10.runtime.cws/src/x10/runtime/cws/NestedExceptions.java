package x10.runtime.cws;

import java.util.List;
public class NestedExceptions extends Exception {
	public List<Exception> exceptions;
	public NestedExceptions(List<Exception> e) {
		super();
		exceptions = e;
	}
	public String toString() {
		return "NestedException<" + (exceptions.size() > 0 ? exceptions.get(1) : "null") + ">"; 
	}

}
