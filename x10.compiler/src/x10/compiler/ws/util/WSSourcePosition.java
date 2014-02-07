/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10.compiler.ws.util;

import polyglot.util.Position;

/**
 * @author Haichuan
 * 
 * A simple source code location representation. 
 * It is only used to map WALA object to X10 object.
 * 
 * It's different from polyglot's position object.
 * It has simple equal mechanism
 * 
 *
 */
public class WSSourcePosition{

	protected String url; //file url; //the path should use '/' not '\' in windows
	protected int startLine;
	protected int startColumn;
	protected int endLine;
	protected int endColumn;
	protected int hash;

	public WSSourcePosition(String url, int startLine, int startColumn,
			int endLine, int endColumn) {
		this.url = url;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		hash = url.hashCode() * 37 + startLine;
		hash = hash * 37 + startColumn;
		hash = hash * 37 + endLine;
		hash = hash * 37 + endColumn;
	}
	
	/**
	 * Construct the simple position from Polyglot position
	 * @param pos Polyglot position
	 */
	public WSSourcePosition(Position pos){
		this(processPolyglotURL(pos), pos.line(), pos.column(), pos.endLine(), pos.endColumn());
	}
	
	protected static String processPolyglotURL(Position pos){
		String file = pos.file();
		file = file == null ? "" : file;
		String path = pos.path();
		path = path == null ? "" : path;
		return (file + path).replace('\\', '/');
	}

	public String getUrl() {
		return url;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public int getEndLine() {
		return endLine;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public boolean equals(Object o) {
		if(o instanceof WSSourcePosition){
			WSSourcePosition other = (WSSourcePosition)o;
			
			if(getUrl().equals(other.getUrl())
					&& getStartLine() == other.getStartLine()
					&& getStartColumn() == other.getStartColumn()
					&& getEndLine() == other.getEndLine()
					&& getEndColumn() == other.getEndColumn())
			return true;
		}
		return false;
	}

	public int hashCode() {
		return hash;
	}

	public String toString() {
		//format: url,L12:10 ~ L12:84
		StringBuffer sb = new StringBuffer();
		sb.append(url).append(", L");
		sb.append(getStartLine()).append(':').append(getStartColumn());
		sb.append(" ~ L");
		sb.append(getEndLine()).append(':').append(getEndColumn());
		return sb.toString();
	}
	
	/**
	 * @return only line number/column number, no url
	 */
	public String toShortString() {
		//format: url,L12:10 ~ L12:84
		StringBuffer sb = new StringBuffer();
		sb.append("L");
		sb.append(getStartLine()).append(':').append(getStartColumn());
		sb.append(" ~ L");
		sb.append(getEndLine()).append(':').append(getEndColumn());
		return sb.toString();
	}

}
