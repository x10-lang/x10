/***********************************s******************************************
 * Copyright (c) 2010 IBM Corporation.eLocal
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rick Lesniak (lesniakr@us.ibm.com) - initial API and implementation,
 *    									   adapted from JavaBackEndSmokeTest.java 
*******************************************************************************/

package x10dt.ui.tests;

import java.util.List;
import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

import x10dt.core.utils.Timeout;
import x10dt.tests.services.swbot.utils.SWTBotUtils;

/**
 * @author lesniakr@us.ibm.com
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ImportX10ArchiveTest extends ImportX10Archive {

	private static final String CLASS_NAME = "QSort";  //$NON-NLS-1$					//imported class name

	private static final String CLASS_SRCFILE_NAME = CLASS_NAME + ".x10"; //$NON-NLS-1$	//imported source file name

	private static final String ARCHIVE_NAME = "ArchiveTestFile.zip"; //$NON-NLS-1$		// archive file name
	
	private static final String PROJECT_NAME_JAVABACK = "ArchiveTest_JBack"; //$NON-NLS-1$	//new empty Java backend project to accept the import
	private static final String PROJECT_NAME_CPPBACK = "ArchiveTest_CPPBack"; //$NON-NLS-1$	//new empty C++ backend project to accept the import
	
	private static final int DEFAULT_CPP_NUM_PLACES = 4;	//default
	private static final int JAVA_NUM_PLACES = 4;	//default

	//we expect to see this console output when we run the imported archive
	public static final List<String> EXPECTED_OUTPUT = Arrays.asList(	"size of array: 1000",
																		"array is now sorted", 
																		"++++++ Test succeeded spectacularly!"
																	);
	
	//static initialization of TypeSearchInfo 
	//		/*search*/					-	The search string
	//		/*type*/					- 	The type we're looking for
	//		/*# to find*/				-	The minimum number of search results we're looking for
	//		/*file*/					-	The source file where we're expecting to find the type
	//		/*expected declaration*/	-	The expected type declaration text in the source file
	public static final List<TypeSearchInfo> declarationCheckList = Arrays.asList
	(
							/*search*/	/*type*/	 /*# to find*/	/*file*/			/*expected declaration*/
		new TypeSearchInfo("QS*",		"QSortable",	2,			"QSort.x10",		"public class QSortable(theArray: SortableArray)"								),
		new TypeSearchInfo("Pos*",		"Position",		1,			"TriangleTest.x10",	"class Position(x: Int, y: Int)"												),
		new TypeSearchInfo("Ar*",		"Array",		5,			"Array.x10",		"public final class Array[T] ("													),
		new TypeSearchInfo("Ar*",		"ArrayList",	5,			"ArrayList.x10",	"public class ArrayList[T] extends AbstractCollection[T] implements List[T] {"	),
		new TypeSearchInfo("QSort*",	"QSort",		2,			"QSort.x10",		"public class QSort"															)
	);


  @BeforeClass
  public static void beforeClass() throws Exception {
    topLevelBot = new SWTWorkbenchBot();
    SWTBotUtils.closeWelcomeViewIfNeeded(topLevelBot);
    topLevelBot.perspectiveByLabel("X10").activate();
    SWTBotPreferences.TIMEOUT = Timeout.SIXTY_SECONDS;
  }
  
  /* @  After */  //not using this as an After routine.  We'll call it explicitly when we're ready
  public void clean() throws Exception {
    SWTBotUtils.closeAllEditors(topLevelBot);
    SWTBotUtils.closeAllShells(topLevelBot);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    SWTBotUtils.saveAllDirtyEditors(topLevelBot);
  }

  
  
/*
 * Import archive into CPP back end project and test it
 */
  
  //
  @Test
  public void importCPPArchiveTest() throws Exception {
	  importArchive(BackEndType.cppBackEnd, PROJECT_NAME_CPPBACK, ARCHIVE_NAME, CLASS_SRCFILE_NAME);
  }

  //
//  @Test
//  public void test_CPPOpenType() throws Exception {
//	  validateOpenType(declarationCheckList);
//  }
//  
//  @Test
//  public void test_CPPSearchType() throws Exception {
//	  validateTypeSearch(declarationCheckList);
//  }
  
  //
  @Test
  public void test_RunCPPBackend() throws Exception {	//run with CPP back end
	  //use local platform config w/ sockets runtime
	  ConfigureAndRunCppProject(PROJECT_NAME_CPPBACK, CLASS_NAME, DEFAULT_CPP_NUM_PLACES,
			  "CppPlatformConfigs.xml", "localSocketsConfig",
			  EXPECTED_OUTPUT);
	  //ConfigureAndRunCppProject already calls verifyConsoleOutput, so we don't have to do it here
  }

  
  /*
   * Import archive into Java back end project and test it
   */
  
  @Test
  public void importJavaArchiveTest() throws Exception {

	  clean();			//get rid of whatever mess the CPP tests left behind
	  importArchive(BackEndType.javaBackEnd, PROJECT_NAME_JAVABACK, ARCHIVE_NAME, CLASS_SRCFILE_NAME);	  
  }

  //
//  @Test
//  public void test_JavaOpenType() throws Exception {
//	  validateOpenType(declarationCheckList);
//  }
//
//  @Test
//  public void test_JavaSearchType() throws Exception {
//	  validateTypeSearch(declarationCheckList);
//  }

  //
  @Test
  public void test_RunJavaBackend() throws Exception {	//run with Java back end
	  createAndRunJavaBackEndLaunchConfig(PROJECT_NAME_JAVABACK, PROJECT_NAME_JAVABACK, CLASS_NAME, JAVA_NUM_PLACES);

	  //Well, let's see if it worked
	  // verify that the actual output matches the expected output
	  boolean match = verifyConsoleOutput(EXPECTED_OUTPUT); //$NON-NLS-1$
	  Assert.assertTrue("test_RunJavaBackend: Console output does not match", match); //$NON-NLS-1$
  }
}