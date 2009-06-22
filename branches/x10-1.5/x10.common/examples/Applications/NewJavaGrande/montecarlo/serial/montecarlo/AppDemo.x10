/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package montecarlo;

import java.util.*;
import x10.lang.Double;

/**
 * X10 port of montecarlo benchmark from Section 3 of Java Grande Forum Benchmark Suite (Version 2.0).
 *
 *  SERIAL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 *
 * Porting issues identified:
 * 1) Replace cast to Object by cast to x10.lang.Object
 * 2) Add nullable ToInitAllTasks type constructor for declaration of initAllTasks, and cast to non-nullable ToInitAllTasks
 *    when passing initAllTasks as a parameter.
 * 4) Add cast to x10.lang.Object
 */
public class AppDemo extends Universal {

	//------------------------------------------------------------------------
	// Class variables.
	//------------------------------------------------------------------------

	public static final Double JGFavgExpectedReturnRateMC = new Double(0.0);
	public static double JGFavgExpectedReturnRateMC() { return JGFavgExpectedReturnRateMC.val; }

	/**
	 * A class variable.
	 */
	public static final boolean DEBUG = true;

	/**
	 * The prompt to write before any debug messages.
	 */
	protected static final String prompt = "AppDemo> ";

	public static final int Serial = 1;

	//------------------------------------------------------------------------
	// Instance variables.
	//------------------------------------------------------------------------

	/**
	 * Directory in which to find the historical rates.
	 */
	private String dataDirname;

	/**
	 * Name of the historical rate to model.
	 */
	private String dataFilename;

	/**
	 * The number of time-steps which the Monte Carlo simulation should
	 * run for.
	 */
	private int nTimeStepsMC = 0;

	/**
	 * The number of Monte Carlo simulations to run.
	 */
	private int nRunsMC = 0;

	/**
	 * The default duration between time-steps, in units of a year.
	 */
	private double dTime = 1.0/365.0;

	/**
	 * Flag to determine whether initialisation has already taken place.
	 */
	private boolean initialised = false;

	/**
	 * Variable to determine which deployment scenario to run.
	 */
	private int runMode;
	private dist D;
	private ToTask[.] tasks;
	private final double[.] expectedReturnRate;
	private final double[.] volatility;

	public AppDemo(String dataDirname, String dataFilename, int nTimeStepsMC,
				   int nRunsMC)
	{
		this.dataDirname    = dataDirname;
		this.dataFilename   = dataFilename;
		this.nTimeStepsMC   = nTimeStepsMC;
		this.nRunsMC        = nRunsMC;
		this.initialised    = false;
		D = [0:(nRunsMC-1)]->here;
		expectedReturnRate = new double[D];
		volatility = new double[D];
		set_prompt(prompt);
		set_DEBUG(DEBUG);
	}

	/**
	 * Initialisation and Run methods.
	 */
	PriceStock psMC;
	double pathStartValue = 100.0;
	double avgExpectedReturnRateMC = 0.0;
	double avgVolatilityMC = 0.0;

	nullable<ToInitAllTasks> initAllTasks = null;

	/**
	 * Single point of contact for running this increasingly bloated
	 * class.  Other run modes can later be defined for whether a new rate
	 * should be loaded in, etc.
	 * Note that if the <code>hostname</code> is set to the string "none",
	 * then the demonstrator runs in purely serial mode.
	 */
	public void initSerial() {
		try {
			//
			// Measure the requested path rate.
			RatePath rateP = new RatePath(dataDirname, dataFilename);
			rateP.dbgDumpFields();
			ReturnPath returnP = rateP.getReturnCompounded();
			returnP.estimatePath();
			returnP.dbgDumpFields();
			double expectedReturnRate = returnP.get_expectedReturnRate();
			double volatility         = returnP.get_volatility();
			//
			// Now prepare for MC runs.
			initAllTasks = new ToInitAllTasks(returnP, nTimeStepsMC,
					pathStartValue);
			String slaveClassName = "MonteCarlo.PriceStock";
			//
			// Now create the tasks.
			initTasks(nRunsMC);
			//
		} catch (DemoException demoEx) {
			dbgPrintln(demoEx.toString());
			System.exit(-1);
		}
	}

	public void runSerial() {
		final ToInitAllTasks t = (ToInitAllTasks) initAllTasks;
		for (point [i] : expectedReturnRate) {
			PriceStock ps = new PriceStock();
			ps.setInitAllTasks(t);
			ps.setTask(tasks[i]);
			ps.run();
			ToResult r = (ToResult) ps.getResult();
			expectedReturnRate[i] = r.get_expectedReturnRate();
			volatility[i] = r.get_volatility();
		};
	}

	public void processSerial() {
		//
		// Process the results.
		try {
			processResults();
		} catch (DemoException demoEx) {
			dbgPrintln(demoEx.toString());
			System.exit(-1);
		}
	}

	//------------------------------------------------------------------------

	/**
	 * Generates the parameters for the given Monte Carlo simulation.
	 *
	 * @param nRunsMC the number of tasks, and hence Monte Carlo paths to
	 *        produce.
	 */
	private void initTasks(int nRunsMC) {
		tasks = new ToTask[D] (point [i]) { return new ToTask("MC run " + i, (long)i*11); };
	}

	/**
	 * Method for doing something with the Monte Carlo simulations.
	 * It's probably not mathematically correct, but shall take an average over
	 * all the simulated rate paths.
	 *
	 * @exception DemoException thrown if there is a problem with reading in
	 *            any values.
	 */
	private void processResults() throws DemoException {
		double avgExpectedReturnRateMC = expectedReturnRate.sum()/nRunsMC;
		double avgVolatilityMC = volatility.sum()/nRunsMC;
		/* The other computations are not actually used. But if needed here
		 * is how they would be represented:
		 */
		/*
		final double[.] runERR = expectedReturnRate.scan(doubleArray.add, 0);
		final double[.] runVolatility = volatility.scan(doubleArray.add, 0);
		finish ateach (point [i] : expectedReturnRate) {
			runERR[i] /= ((double)(i+1));
			runVolatility[i] /= ((double) (i+1));
		}
		// Paths can be averaged by sum-reducing the distributed array of paths
		// with a custom sum() method that adds path components pointwise, and then
		// scaling with 1/nRunsMC.
		*/
		JGFavgExpectedReturnRateMC.val = avgExpectedReturnRateMC;

		//dbgPrintln("Average over "+nRunsMC+": expectedReturnRate = "+
		//	avgExpectedReturnRateMC+" volatility = "+avgVolatilityMC + JGFavgExpectedReturnRateMC);
	}

	//
	//------------------------------------------------------------------------
	// Accessor methods for class AppDemo.
	// Generated by 'makeJavaAccessor.pl' script.  HWY.  20th January 1999.
	//------------------------------------------------------------------------

	/**
	 * Accessor method for private instance variable <code>dataDirname</code>.
	 * @return Value of instance variable <code>dataDirname</code>.
	 */
	public String get_dataDirname() {
		return (this.dataDirname);
	}

	/**
	 * Set method for private instance variable <code>dataDirname</code>.
	 * @param dataDirname the value to set for the instance variable <code>dataDirname</code>.
	 */
	public void set_dataDirname(String dataDirname) {
		this.dataDirname = dataDirname;
	}

	/**
	 * Accessor method for private instance variable <code>dataFilename</code>.
	 * @return Value of instance variable <code>dataFilename</code>.
	 */
	public String get_dataFilename() {
		return (this.dataFilename);
	}

	/**
	 * Set method for private instance variable <code>dataFilename</code>.
	 * @param dataFilename the value to set for the instance variable <code>dataFilename</code>.
	 */
	public void set_dataFilename(String dataFilename) {
		this.dataFilename = dataFilename;
	}

	/**
	 * Accessor method for private instance variable <code>nTimeStepsMC</code>.
	 * @return Value of instance variable <code>nTimeStepsMC</code>.
	 */
	public int get_nTimeStepsMC() {
		return (this.nTimeStepsMC);
	}

	/**
	 * Set method for private instance variable <code>nTimeStepsMC</code>.
	 * @param nTimeStepsMC the value to set for the instance variable <code>nTimeStepsMC</code>.
	 */
	public void set_nTimeStepsMC(int nTimeStepsMC) {
		this.nTimeStepsMC = nTimeStepsMC;
	}

	/**
	 * Accessor method for private instance variable <code>nRunsMC</code>.
	 * @return Value of instance variable <code>nRunsMC</code>.
	 */
	public int get_nRunsMC() {
		return (this.nRunsMC);
	}

	/**
	 * Set method for private instance variable <code>nRunsMC</code>.
	 * @param nRunsMC the value to set for the instance variable <code>nRunsMC</code>.
	 */
	public void set_nRunsMC(int nRunsMC) {
		this.nRunsMC = nRunsMC;
	}

	/**
	 * Accessor method for private instance variable <code>tasks</code>.
	 * @return Value of instance variable <code>tasks</code>.
	 */
	public ToTask[.] get_tasks() {
		return (this.tasks);
	}

	/**
	 * Set method for private instance variable <code>tasks</code>.
	 * @param tasks the value to set for the instance variable <code>tasks</code>.
	 */
	public void set_tasks(ToTask[.] tasks) {
		this.tasks = tasks;
	}

	/**
	 * Accessor method for private instance variable <code>results</code>.
	 * @return Value of instance variable <code>results</code>.
	 */
	/*
	public Vector get_results() {
		return (this.results);
	}
	*/

	/**
	 * Set method for private instance variable <code>results</code>.
	 * @param results the value to set for the instance variable <code>results</code>.
	 */
	/*
	public void set_results(Vector results) {
		this.results = results;
	}
	*/

	//------------------------------------------------------------------------
}

