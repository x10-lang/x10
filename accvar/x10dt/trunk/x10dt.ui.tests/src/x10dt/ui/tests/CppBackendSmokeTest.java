/***********************************s******************************************
 * Copyright (c) 2011 IBM Corporation.eLocal
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import x10dt.core.utils.Timeout;
import x10dt.tests.services.swbot.utils.SWTBotUtils;

/**
 * @author lesniakr@us.ibm.com
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CppBackendSmokeTest extends ImportX10Archive {

	private static final String CLASS_NAME = "QSort";  //$NON-NLS-1$					//imported class name

	private static final String CLASS_SRCFILE_NAME = CLASS_NAME + ".x10"; //$NON-NLS-1$	//imported source file name

	private static final String ARCHIVE_NAME = "ArchiveTestFile.zip"; //$NON-NLS-1$		// archive file name
	
	private static final String PROJECT_NAME_CPPBACK = "ArchiveTest_CPPBack"; //$NON-NLS-1$	//new empty C++ backend project to accept the import

	private static final int DEFAULT_NUM_PLACES = 4;	//default

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

//	static Document xmlConfigurations;		//loaded from XML - this document contains specifics of one or more x10 platform configurations

	@BeforeClass
	public static void beforeClass() throws Exception {
		topLevelBot = new SWTWorkbenchBot();
		SWTBotUtils.closeWelcomeViewIfNeeded(topLevelBot);
		topLevelBot.perspectiveByLabel("X10").activate();
		SWTBotPreferences.TIMEOUT = Timeout.SIXTY_SECONDS;
		
//		xmlConfigurations = loadXML("CppPlatformConfigs.xml");
	}
	
	@After
	public void after() throws Exception {
		SWTBotUtils.closeAllEditors(topLevelBot);
		SWTBotUtils.closeAllShells(topLevelBot);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		SWTBotUtils.saveAllDirtyEditors(topLevelBot);
	}

	//
	@Test
	public void test_ImportCPPArchive() throws Exception {
		importArchive(BackEndType.cppBackEnd, PROJECT_NAME_CPPBACK, ARCHIVE_NAME, CLASS_SRCFILE_NAME);
	}

	//
	@Test
	public void test_OpenType() throws Exception {
		validateOpenType(declarationCheckList);
	}

	//
	@Test
	public void test_SearchType() throws Exception {
		validateTypeSearch(declarationCheckList);
	}

	//
	@Test
	//use local platform config w/ sockets runtime
	public void test_LocalRun() throws Exception {
		 ConfigureAndRunCppProject(PROJECT_NAME_CPPBACK, CLASS_NAME, 
				 DEFAULT_NUM_PLACES, "CppPlatformConfigs.xml", 
				 "localSocketsConfig", EXPECTED_OUTPUT); 
	}

//	//
//	@Test
//	//use remote Linux platform config w/ sockets runtime
//	public void test_RemoteLinux() throws Exception {
//		 ConfigureAndRunCppProject(PROJECT_NAME_CPPBACK, CLASS_NAME, 
//				 DEFAULT_NUM_PLACES, "CppPlatformConfigs.xml", 
//				 "remoteLinuxSocketsConfig", EXPECTED_OUTPUT); 
//	}
}