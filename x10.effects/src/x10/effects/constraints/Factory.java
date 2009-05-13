package x10.effects.constraints;

/**
 * A Factory class for creating effects.
 * 
 * @author vj 05/13/09
 *
 */
public class Factory {
	public static boolean PAR_FUN = false;
	public static boolean FUN = true;
	/**
	 * The code walker will create effects for sequential leaf statements by calling 
	 * Factory.makeEffect(Factory.FUN),and will then add the XTerms picked up from the statement
	 * to the readSet, writeSet and atomicIncSet of the effect.
	 * 
	 * 
	 * @param isFun
	 * @return
	 */
	public static Effect makeEffect(boolean isFun) {
		return new Effect_c(isFun);
	}

}
