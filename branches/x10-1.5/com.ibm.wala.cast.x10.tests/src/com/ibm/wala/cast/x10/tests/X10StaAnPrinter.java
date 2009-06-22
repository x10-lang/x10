package com.ibm.wala.cast.x10.tests;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;


class Print {
	
	static BufferedWriter out;
	
	static void open (String nusmvFile)
	{
		try {
		out = new BufferedWriter(new FileWriter (nusmvFile));
		}
		catch (IOException e) { System.out.println("Unable to write to output file");}
	}
	
	
	static void println (StringBuffer str)
	{
		try {
		  out.write(str+"\n");
		}
		catch (IOException e) { System.out.println("Unable to write to output file");}
	}
	

	static void println (String str)
	{
		try {
		out.write(str+"\n");
		}
		catch (IOException e) { System.out.println("Unable to write to output file");}
	}
	
	static void close ()
	{
		try {
		out.close();
		}
		catch (IOException e) { System.out.println("Unable to write to output file");}
	}
	
}

class CaseStmt{
	ArrayList clauses = new ArrayList<String>();
	ArrayList results = new ArrayList<String> ();

	/*For eg. the input could be ("&", acitivty, "=", "resumed");*/
	public void addClause(String prefixOp, String leftTerm, String Op, String rightTerm)
	{
		clauses.add(prefixOp + " (" + leftTerm + " " + Op + " " + rightTerm + ") ");
	}
	
	public void addResult(String result)
	{
		results.add(result);
	}
	
	 public String buildCaseStmt () 
	 {
		  StringBuffer str = new StringBuffer();
		  Iterator clauseIt = clauses.listIterator();
		  while(clauseIt.hasNext())
		  {
			  str.append(clauseIt.next() + " ");
		  }
		  str.append(": ");
		  
		  /*Results*/
		  Iterator resIt = results.listIterator();
		  	str.append("{");
		  	while(resIt.hasNext())
		  	{
		  		str.append(resIt.next());
		  		if(resIt.hasNext())
		  			str.append(", ");
		  	}
		  	str.append("}");
		  return str.toString();
	 }
}



class Automaton {
  public ArrayList viableValues = new ArrayList<String>();
  private ArrayList caseStmts = new ArrayList<String>();
  private String initValue = new String();
  public String name = new String();
  
  public void setName(String value)
  {
	  name = value;
  }

  public void setInitValue(String value)
  {
	initValue =  value;
  }
  
  public void addViableValue(String value)
  {
	  if(!viableValues.contains(value))
		  viableValues.add(value);
  }
  
  public void addCaseStmt(CaseStmt c)
  {
	  caseStmts.add(c.buildCaseStmt());
  }
 
  
  public void printAutomaton ()
  {
  	StringBuffer str = new StringBuffer();
  	str.append("\n\n VAR "+ name + ": " );
  	/* Declaration */
  	Iterator viaIt = viableValues.listIterator();
  	str.append("{");
  	while(viaIt.hasNext())
  	{
  		str.append(viaIt.next());
  		if(viaIt.hasNext())
  			str.append(", ");
  	}
  	str.append("};");
    str.append("\n ASSIGN");
  	/* Init value */
  	str.append("\n init("+ name + "):= {" + initValue + "};");
  	
  	/* Transitions */
	str.append("\n next(" + name + "):= ");
	str.append("\n\t case");
	Iterator condIt = caseStmts.listIterator();
  	while(condIt.hasNext())
  	{
  		str.append("\n\t\t " + condIt.next() + ";");
  	}	
  	str.append("\n\t\t 1: " + name + ";"); // default case
	str.append("\n\t esac;");
	Print.println(str);
  }
 }


class Spec {
	
		String spec;
		
		Spec (String sp)
		{
			spec = "SPEC " + sp;
		}
		
		Spec (String sp, boolean isLTL)
		{
			spec = "LTLSPEC " + sp;
		}
		
		public void printSpec ()
		{
			Print.println(spec);
			System.out.println(spec);
			
		}
}


class NuSMVFile {
	ArrayList automata = new ArrayList<Automaton>();
	ArrayList specs = new ArrayList <String>();
	String nusmvFile = null, resultFile = null, nusmv = null;
	
	public NuSMVFile ()
	{
		Properties p = new Properties();
		
		try {
		p.load(new FileInputStream("nusmv.properties"));
		nusmvFile = p.getProperty("nusmvFile");
		resultFile = p.getProperty("resultFile");
		nusmv = p.getProperty("nusmv");
		if (nusmvFile == null || resultFile == null || nusmv == null)
				throw new Exception();
		} catch (Exception e) {
			System.out.println("Unable to read config file");
		}
		Print.open(nusmvFile);
	}
	
	public void addSpec (Spec s)
	{
		specs.add(s);
	}
	
	public String addDefine (Automaton a, String val)
	{
	StringBuffer str = new StringBuffer();
	Set values = new HashSet();
	Iterator it = (a.viableValues).iterator();
	
	while (it.hasNext())
	{
		String variable = (String)it.next();
		if(variable.contains(val))
			values.add(variable);
	}
	if (values.size() > 0)
	{
		str.append("DEFINE "+ a.name + "_" + val + ":= " + a.name + " in ");
		str.append("{");

		it = values.iterator();
		while(it.hasNext())
		{		
  			str.append(it.next());
  			if(it.hasNext())
  				str.append(", ");
		}
		str.append("};");
	}
	else str.append("DEFINE "+ a.name + "_" + val + ":= 0;");
	str.append("\n");
	return str.toString();
	}
  
	private Automaton buildPrototypeAutomaton(String name)
	{
		String pName = "status_" + name;
		Automaton pA = new Automaton();
		pA.setName(pName);
		pA.setInitValue("inactive");
		pA.addViableValue("active");
		pA.addViableValue("inactive");
		pA.addViableValue("resumed");
		pA.addViableValue("exception");
		
		CaseStmt c;
				
		c = new CaseStmt();
		c.addClause("", pName, "=", "active");
		c.addClause("&", name + "_" + "drop", "", "");
		c.addResult("inactive");
		pA.addCaseStmt(c);
		
		c = new CaseStmt();
		c.addClause("", pName, "=", "active");
		c.addClause("&", name + "_" + "resume", "", "");
		c.addResult("resumed");
		pA.addCaseStmt(c);
		
		c = new CaseStmt();
		c.addClause("", pName, "=", "active");
		c.addClause("&", name + "_" + "asyncUnclocked", "", "");
		c.addResult("inactive");
		pA.addCaseStmt(c);
		
		c = new CaseStmt();
		c.addClause("", pName, "=", "resumed");
		c.addClause("&", name + "_" + "asyncUnclocked", "", "");
		c.addResult("inactive");
		pA.addCaseStmt(c);
			
		//The new version allows this
		/*
		c = new CaseStmt();
		c.addClause("", pName, "=", "resumed");
		c.addClause("&", name + "_" + "asyncClocked", "", "");
		c.addResult("exception");
		pA.addCaseStmt(c);*/
								
		c = new CaseStmt();
		c.addClause("", pName, "=", "resumed");
		c.addClause("&", name + "_" + "next", "", "");
		c.addResult("active");
		pA.addCaseStmt(c);	
				
		c = new CaseStmt();
		c.addClause("", pName, "=", "resumed");
		c.addClause("&", name + "_" + "resume", "", "");
		c.addResult("exception");
		pA.addCaseStmt(c);
		
		c = new CaseStmt();
		c.addClause("", pName, "=", "resumed");
		c.addClause("&", name + "_" + "drop", "", "");
		c.addResult("inactive");
		pA.addCaseStmt(c);
		
		c = new CaseStmt();
		c.addClause("", pName, "=", "inactive");
		c.addClause("&", name + "_" + "next", "", "");
		c.addResult("exception");
		pA.addCaseStmt(c);
				
		c = new CaseStmt();
		c.addClause("", pName, "=", "inactive");
		c.addClause("&", name + "_" + "drop", "", "");
		c.addResult("exception");
		pA.addCaseStmt(c);

		c = new CaseStmt();
		c.addClause("", pName, "=", "inactive");
		c.addClause("&", name + "_" + "resume", "", "");
		c.addResult("exception");
		pA.addCaseStmt(c);
		
		c = new CaseStmt();
		c.addClause("", pName, "=", "inactive");
		c.addClause("&", name + "_" + "create", "", "");
		c.addResult("active");
		pA.addCaseStmt(c);
		
		c = new CaseStmt();
		c.addClause("", pName, "=", "inactive");
		c.addClause("&", name + "_" + "asyncClocked", "", "");
		c.addResult("exception");
		pA.addCaseStmt(c);
	
		addChecks(pA.name, name);
		return pA;
		
	}
	
	public void addChecks(String prototype, String clockName)
	{
		/*Exception check*/
		Spec s;
		s = new Spec("G(" + clockName + "_next -> H (!" + clockName + "_asyncClocked))", true);
		this.addSpec(s);
		s = new Spec("AG (!" + clockName + "_resume)");
		this.addSpec(s);
		s = new Spec("AG (" + prototype + "!= exception)");
		this.addSpec(s);
		
		
	}
	
	public void addPrototype(Automaton a)
	{
		StringBuffer str = new StringBuffer();
		str.append(addDefine(a, "resume")); 
		str.append(addDefine(a, "drop"));
		str.append(addDefine(a, "next"));
		str.append(addDefine(a, "create"));
		str.append(addDefine (a,"asyncClocked"));
		str.append(addDefine(a, "asyncUnclocked"));
		Print.println(str);
		Automaton pa = buildPrototypeAutomaton(a.name);
		pa.printAutomaton();
		
	}
	
	
	public void addAutomaton(Automaton a)
	{
		automata.add(a);		
	}
	
	
	
	public void buildNuSMVFile()
	{
		Print.println("MODULE main");
		Print.println("");
		for(Iterator it = automata.listIterator(); it.hasNext();)
		{
			Automaton a = (Automaton)it.next();
			 a.printAutomaton();
			 addPrototype(a);
		}
		Print.println("");
		for(Iterator it = specs.listIterator(); it.hasNext();)
			((Spec)(it.next())).printSpec();
		Print.close();
	}
	
	

	/* Run NuSMV to check for a particular specification in the NuSMV file */
	private boolean checkSpec(int specNum)
	{
			Boolean spec = null;
			int attempts = 0;
			do {
				try{
					Process p = Runtime.getRuntime().exec(nusmv + " -n " + specNum + " " + nusmvFile);
					//p.waitFor();
					BufferedReader input = new BufferedReader(
									new InputStreamReader (p.getInputStream()));
					String line;
		
					while((line = input.readLine()) != null)
					{
						
						if (line.contains("TL Counterexample"))
						{ 
							spec = false;
							break;
						}
						else if (line.contains("is true"))
						{
							spec = true;
							break;
						}
					}
					input.close();
					if (spec == null) throw new Exception (); // Parse error case 
					System.out.println("--" + spec);
					} catch (Exception e){
						System.out.println("--");
						attempts ++;
						spec = null;
				}
			} while(spec == null && attempts <= 3); // keep retrying by rerunning NUSMV
			if (spec == null) return false;
			return spec;
		}
	
	
	
	/* Run NuSMV on all specifications */
	public void runNuSMVFile(ClockHash cHash)
	{
		Iterator<InstanceKey> cIt = cHash.getKeys().iterator();
		int specNum = 0;
		System.out.flush();
		try{
			
		int clockCount = cHash.getKeys().size();
		int clockNum = 0;
		BufferedWriter out  = new BufferedWriter(new FileWriter(resultFile));
		while (cIt.hasNext()) {
			InstanceKey ik = cIt.next();
			/*Precedence here*/
				out.write(cHash.get(ik));
			if (checkSpec(clockCount*2 + clockNum)) // The LTL specs are evaluated at the end
			{
					out.write(" ON");
			}
			if(checkSpec(specNum++))
			{
					out.write(" NR");
				
			}
			if (checkSpec(specNum++))
			{
					out.write(" EF");
			}
			
					
			out.write("\n");
			clockNum ++;
		
			//checkSpec(specNum++);
		
		}
		out.close();
		} catch (Exception e){
			System.out.println("-- File Error");
		}
		
	}
	
}


public class X10StaAnPrinter {
	
	public static void main1 (String args[])
	{
		Automaton a = new Automaton();
		a.setName("activity");
		a.addViableValue("resumed");
		a.addViableValue("active");
		a.setInitValue("active");
		CaseStmt c = new CaseStmt();
		c.addClause("", "active", "=", "resumed");
		c.addResult ("resumed");
		a.addCaseStmt(c);
		
		NuSMVFile n = new NuSMVFile();
		n.addAutomaton(a);
		n.buildNuSMVFile();
	}
	
}
