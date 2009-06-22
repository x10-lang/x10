package org.eclipse.imp.x10dt.core;

import java.io.IOException;
import java.net.URL;

import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.jface.text.IRegion;
import org.osgi.framework.Bundle;

import polyglot.util.Position;

/**
 * General Utilities 
 * @author Beth Tibbitts
 *
 */
public class X10Util {

	/**
	 * Get left (first) token within a Position, given the parse controller
	 * @param pos
	 * @param parseController
	 * @return
	 */
	public static IToken getLeftToken(Position pos,	IParseController parseController) {
		return getToken(pos.offset(),parseController);
	}
	/**
	 * Get left (first) token within an IRegion, given the parse controller
	 * @param region
	 * @param parseController
	 * @return
	 */
	public static IToken getLeftToken(IRegion region, IParseController parseController) {
		return getToken(region.getOffset(), parseController);
	}
	
	/**
	 * Get right (last) token within a Position, given the parse controller
	 * @param pos
	 * @param parseController
	 * @return
	 */
	public static IToken getRightToken(Position pos,	IParseController parseController) {
		int endOffset=pos.endOffset();
		return getToken(endOffset,parseController);
	}
	/**
	 * Get right (last) token within an IRegion, given the parse controller
	 * @param region
	 * @param parseController
	 * @return
	 */
	public static IToken getRightToken(IRegion region, IParseController parseController) {
		int endOffset=region.getOffset()+region.getLength()-1;
		return getToken(endOffset, parseController);
	}
	
	/**
	 * Get left (first) token within a Polyglot Position object, given the parse stream
	 * @param pos
	 * @param prs
	 * @return
	 */
	public static IToken getLeftToken(Position pos, IPrsStream prs) {
		return getToken(pos.offset(),prs);
	}
	/**
	 * Get right (last) token within a Polyglot Position object, given the parse stream
	 * @param pos position object
	 * @param prs parse stream
	 * @return
	 */
	public static IToken getRightToken(Position pos, IPrsStream prs) {
		return getToken(pos.endOffset(),prs);
	}
	
	/**
	 * Get the token at a specific offset location, given the parse controller
	 * @param offset
	 * @param parseController
	 * @return
	 */
	public static IToken getToken(int offset, IParseController parseController) {
		IToken token=null;
		if(parseController instanceof SimpleLPGParseController) {
			SimpleLPGParseController pc = (SimpleLPGParseController) parseController;
			IPrsStream prs = pc.getParser().getIPrsStream();
			token = getToken(offset, prs);
		}
		return token;
	}
	/**
	 * Get the token at a specific offset location, given the parse stream
	 * @param offset
	 * @param prs
	 * @return
	 */
	public static IToken getToken(int offset, IPrsStream prs) {
		return prs.getTokenAtCharacter(offset);
	}
	/**
	 * get the jar file location (fully qualified path) for the given bundle ID
	 * <br>
	 * See also X10RuntimeUtils
	 * @param bundleID
	 * @return
	 */
	public static String getJarLocationForBundle(String bundleID) {
		Bundle b1 = Platform.getBundle(bundleID);
		URL b1u = b1.getResource("");
		String jarFileName = null;
		try {
			// FileLocator is normally used for finding files IN bundles.
			URL jarFileURL = FileLocator.resolve(b1u);
			jarFileName = jarFileURL.getFile(); 
			if (jarFileName.endsWith("!/")) {
				jarFileName = jarFileName
						.substring(0, jarFileName.length() - 2);
			} else {
				X10DTCorePlugin.getInstance().writeInfoMsg(
						"X10RETab did not find jar file for " + bundleID
								+ " ending with '!/'");
			}
			if (jarFileName.startsWith("file:")) {
				jarFileName = jarFileName.substring(5);
			} else {
				X10DTCorePlugin.getInstance().writeInfoMsg(
						"X10RETab did not find jar file for " + bundleID
								+ " beginning with 'file:'");
			}
		} catch (IOException e) {
			X10DTCorePlugin.getInstance().logException(
					"Error getting jar file for " + bundleID, e);
		}
		return jarFileName;
	}

}
