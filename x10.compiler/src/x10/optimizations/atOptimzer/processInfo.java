package x10.optimizations.atOptimzer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import x10.optimizations.atOptimzer.processInfo;

public class processInfo
{
	public Stack<String> currClass = new Stack<String>();
	public HashMap<String, LinkedList<ClassInfo>> classDetails  = new HashMap<String, LinkedList<ClassInfo>>();

}
