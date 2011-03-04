package x10.refactorings.utils;

/**
 * An abstract function class that converts values of type S into values of type
 * T. For functional programming style in Java.
 * 
 * @author sm053
 * 
 * @param <S>
 *            The type of input parameters
 * @param <T>
 *            The type of return values
 */
public abstract class Function<S, T> {

	public abstract T eval(S x);
}
