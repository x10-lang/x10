/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *    Rick Lesniak (lesniakr@us.ibm.com) - 
 *    				added ImportJavaBackEndArchiveProject and findX10TypeDef.
 *    				added createCPPBackendProject
 *    				moved in createAndRunJavaBackEndLaunchConfig and openX10FileInEditor
 *    				added X10DT_TestException and exception reporting structure
 *******************************************************************************/

package x10dt.ui.tests;

//import java.util.regex.Matcher;
import org.hamcrest.Matcher;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

import x10dt.tests.services.swbot.constants.LaunchConstants;
import x10dt.tests.services.swbot.constants.WizardConstants;

/**
 * @author rfuhrer@watson.ibm.com
 */
public class X10DTTestBase {
  /**
   * The top-level "bot" for the entire workbench.
   */
  public static SWTWorkbenchBot topLevelBot;

  /**
   * This method waits for a build to finish before continuing
   * 
   * @throws Exception
   */
  public static void waitForBuildToFinish() throws Exception {
    Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
  }
  
  //Create a new X10 Java back-end project
  //
  public static void createJavaBackEndProject(String projName, boolean withHello) throws X10DT_Test_Exception {
	  String operationMsg = null;			//string describing the current operation, for use in constructing error messages
	  String dialogContextMsg = null;		//string identifying the current dialog context, for use in constructing error messages
	  try
	  {
		  //Open the New Project dialog
		  operationMsg = "access the New Project menu '" + WizardConstants.FILE_MENU +":"+
					  		WizardConstants.NEW_MENU_ITEM +":"+ WizardConstants.PROJECTS_SUB_MENU_ITEM + "'.";
		  
		  topLevelBot.menu(WizardConstants.FILE_MENU)
		  				.menu(WizardConstants.NEW_MENU_ITEM)
		  					.menu(WizardConstants.PROJECTS_SUB_MENU_ITEM)
		  						.click();

		  //
		  //Use the New Project wizard in the X10 folder
		  //
		  dialogContextMsg = " the '" + WizardConstants.NEW_PROJECT_DIALOG_TITLE + "' wizard";
		  
		  //find the new project wizard dialog
		  operationMsg = "find" + dialogContextMsg;
		  SWTBotShell newProjShell = topLevelBot.shell(WizardConstants.NEW_PROJECT_DIALOG_TITLE);
		  newProjShell.activate();
		  SWTBot newProjBot = newProjShell.bot();
		  
		  //open the X10 wizard folder
		  operationMsg = "find the '" + WizardConstants.X10_FOLDER + "' wizard folder in" + dialogContextMsg;
		  SWTBotTreeItem x10ProjItems = newProjBot.tree().expandNode(WizardConstants.X10_FOLDER);

		  //find the X10 wizard 
		  operationMsg = "find the '" + WizardConstants.X10_PROJECT_JAVA_BACKEND + "' wizard in" + dialogContextMsg;
		  x10ProjItems.select(WizardConstants.X10_PROJECT_JAVA_BACKEND);

		  //click 'Next'
		  operationMsg = "find the '" + WizardConstants.NEXT_BUTTON + "' button in" + dialogContextMsg;
		  newProjBot.button(WizardConstants.NEXT_BUTTON).click();

		  //
		  //Set up the new X10 project - specify project name and select whether or not to create Hello World sample code
		  //
		  dialogContextMsg = " the '" + WizardConstants.X10_PROJECT_SHELL_JAVA_BACKEND + "' dialog";
		  
		  //find the new X10 project wizard dialog 
		  operationMsg = "find" + dialogContextMsg;
		  SWTBotShell newX10ProjShell = topLevelBot.shell(WizardConstants.X10_PROJECT_SHELL_JAVA_BACKEND);
		  newX10ProjShell.activate();
		  SWTBot newX10ProjBot = newX10ProjShell.bot();

		  //set radio button to either create hello sample code or not
		  operationMsg = /*click the '<name>*/"' radio button in the '" 
			  				+ WizardConstants.NEW_X10_PROJECT_SAMPLE_SOURCE_GROUP + 
			  					"' dialog group, inside"  + dialogContextMsg;
		  if (withHello) 
		  {
			  operationMsg = "click the '" + WizardConstants.NEW_X10_PROJECT_HELLO_SOURCE_RADIO + operationMsg;
			  newX10ProjBot.radioInGroup(WizardConstants.NEW_X10_PROJECT_HELLO_SOURCE_RADIO,
						  WizardConstants.NEW_X10_PROJECT_SAMPLE_SOURCE_GROUP).click();
		  } 
		  else
		  {
			  operationMsg = "click the '" + WizardConstants.NEW_X10_PROJECT_HELLO_SOURCE_RADIO + operationMsg;
			  newX10ProjBot.radioInGroup(WizardConstants.NEW_X10_PROJECT_NO_SAMPLE_SOURCE_RADIO,
						  WizardConstants.NEW_X10_PROJECT_SAMPLE_SOURCE_GROUP).click();
		  }

		  // Fill in the archive file name field
		  operationMsg = "fill in the '" + WizardConstants.X10_PROJECT_JAVA_BACKEND_NAME_FIELD + "' field of" + dialogContextMsg;
		  newX10ProjBot.textWithLabel(WizardConstants.X10_PROJECT_JAVA_BACKEND_NAME_FIELD).setFocus();
		  newX10ProjBot.textWithLabel(WizardConstants.X10_PROJECT_JAVA_BACKEND_NAME_FIELD).setText(projName);

		  // click the 'Finish' button
		  operationMsg = "click the '" + WizardConstants.X10_PROJECT_JAVA_BACKEND_NAME_FIELD + "' button in" + dialogContextMsg;
		  newX10ProjBot.button(WizardConstants.FINISH_BUTTON).click();

		  topLevelBot.waitUntil(Conditions.shellCloses(newX10ProjShell));
	  }
	  
	  catch (Exception e)
	  {
		  throw new X10DT_Test_Exception("Could not create new X10 project '" + projName +
				  							"': Failed to " + operationMsg +
				  								"'.\n        Reason: " + e.getMessage());
	  }
  }
 
  //Import an archive into an existing X10 Java back-end project
  //
  public static void importArchiveToJavaBackEndProject(String archiveName, String folderName, boolean doOverwrite) throws X10DT_Test_Exception {
	  String operationMsg = null;			//string describing the current operation, for use in constructing error messages
	  try
	  {
		  //Open the New Import dialog
		  operationMsg = " access menu '" + WizardConstants.FILE_MENU +":"+ WizardConstants.IMPORT_MENU_ITEM + "'";
		  topLevelBot.menu(WizardConstants.FILE_MENU).menu(WizardConstants.IMPORT_MENU_ITEM).click();

		  //Find the Import dialog
		  operationMsg = "Cannot select the '" + WizardConstants.IMPORT_DIALOG_TITLE + "' wizard.";
		  SWTBotShell selectArchiveShell = topLevelBot.shell(WizardConstants.IMPORT_DIALOG_TITLE);

		  selectArchiveShell.activate();

		  SWTBot selectArchiveBot = selectArchiveShell.bot();
		  SWTBotTree importSourceTree = selectArchiveBot.tree();
		  //	  try {
		  ////	doesn't work		selectArchiveBot.waitUntil(Conditions.treeHasRows(importSourceTree, 1));
		  ////	also doesn't work	topLevelBot.waitUntil(Conditions.treeHasRows(importSourceTree, 1));
		  //	  }
		  //	  catch (TimeoutException e) {
		  //		  throw new X10DT_Test_Exception("Import source tree is empty");
		  //	  }

		  //Select the General item in the import sources list
		  operationMsg = "select the '" +
		  					WizardConstants.GENERAL_FOLDER + ":" + WizardConstants.IMPORT_ARCHIVE + "' item.";
		  SWTBotTreeItem x10ProjItems = importSourceTree.expandNode(WizardConstants.GENERAL_FOLDER);
		  x10ProjItems.select(WizardConstants.IMPORT_ARCHIVE);

		  //click Next to go to the Import Archive dialog
		  operationMsg = "click the '" + WizardConstants.NEXT_BUTTON +"' to the Import Archive dialog";
		  selectArchiveBot.button(WizardConstants.NEXT_BUTTON).click();

		  // Set up the Archive Import dialog
		  operationMsg = "locate the '" + WizardConstants.IMPORT_ARCHIVE_DIALOG_TITLE + "' dialog ";
		  SWTBotShell importArchiveShell = topLevelBot.shell(WizardConstants.IMPORT_ARCHIVE_DIALOG_TITLE);
		  importArchiveShell.activate();

		  SWTBot importArchiveBot = importArchiveShell.bot();

		  // Fill in the archive file name field
		  operationMsg = "fill in the '" + 
		  					WizardConstants.IMPORT_ARCHIVE_DIALOG_TITLE + " " +
		  							WizardConstants.FROM_ARCHIVE_FILE_FIELD + "' field";
		  importArchiveBot.comboBoxWithLabel(WizardConstants.FROM_ARCHIVE_FILE_FIELD).setText(archiveName);
		  importArchiveBot.comboBoxWithLabel(WizardConstants.FROM_ARCHIVE_FILE_FIELD).setSelection(0);

		  // Fill in the archive destination folder field
		  operationMsg = "fill in the '" + WizardConstants.IMPORT_ARCHIVE_DIALOG_TITLE + " " +
		  							WizardConstants.INTO_FOLDER_FIELD + "' field";
		  importArchiveBot.textWithLabel(WizardConstants.INTO_FOLDER_FIELD).setText(folderName);

		  if (doOverwrite) {
			  // check the 'overwrite existing' box and click 'Finish'
			  operationMsg = "check the '" + 
			  		WizardConstants.OVERWRITE_EXISTING_CHECKBOX + "' checkbox and click the " +
			  			WizardConstants.FINISH_BUTTON + "' button";
			  importArchiveBot.checkBox(WizardConstants.OVERWRITE_EXISTING_CHECKBOX).select();
			  importArchiveBot.button(WizardConstants.FINISH_BUTTON).click();
		  } else {
//			  // uncheck the 'overwrite existing' box and click 'Finish'
//			  operationMsg = "uncheck the '" + 
//			  		WizardConstants.OVERWRITE_EXISTING_CHECKBOX + "' checkbox and click the " +
//		  				WizardConstants.FINISH_BUTTON + "' button";
//	  		  importArchiveBot.checkBox(WizardConstants.OVERWRITE_EXISTING_CHECKBOX).deselect();
//	  		  importArchiveBot.button(WizardConstants.FINISH_BUTTON).click();
//	  		  
//	  		  //the wizard may incredulously question our jugdement, nay, even our sanity!
//			  operationMsg += "find the 'overwrite file, are-you-sure?' dialog";
//	  		  Matcher areYouSureAlert = allOf (widgetOfType(Label.class), withRegex("Overwrite .*"));
//	  		  
//	  		  //tell the wizard that we are quite serious, and in full possession of our faculties, 
//	  		  // and that it should go forth and multiply itself
//			  operationMsg += ", and then click the '" + WizardConstants.YES_TO_ALL_BUTTON + "' button ";
//	  		  try {
//	  			  SWTBotShell areYouSureShell = importArchiveBot.shell(areYouSureAlert.toString());
//	  			  SWTBotButton yesToAllBut = areYouSureShell.bot().button(WizardConstants.YES_TO_ALL_BUTTON);
//	  			  yesToAllBut.click();
//	  		  }
//	  		  catch (WidgetNotFoundException e)
//	  		  {
//	  			  //this is ok - there's nothing to overwrite
//	  		  }
//	  		  catch (TimeoutException e)
//	  		  {
//	  			  //this is ok - there's nothing to overwrite
//	  		  }

		  } //else doOverwrite == false

		  topLevelBot.waitUntil(Conditions.shellCloses(importArchiveShell));
	  }
	  catch (Exception e)
	  {
		  throw new X10DT_Test_Exception("Could not import  '" + archiveName +
				  	"' to X10 project '" + folderName +
				  		"' : Failed to " + operationMsg +
				  			"'.\n        Reason: " + e.getMessage());
	  }
  }

  //Open the x10 file by pawing through the project in the Package Explorer view
  //
  public static void openX10FileInEditor(String projName, String fileName) throws X10DT_Test_Exception {
	  String operationMsg = null;			//string describing the current operation, for use in constructing error messages
	  try
	  {
		  //Open the Package Explorer
		  operationMsg = "open the '" + WizardConstants.PACKAGE_EXPLORER_VIEW_TITLE+"' View";
		  SWTBotView packageExplorerView = topLevelBot.viewByTitle(WizardConstants.PACKAGE_EXPLORER_VIEW_TITLE);
		  SWTBot packageExplorerBot = packageExplorerView.bot();
		  
		  //Navigate down the package tree to find the project
		  operationMsg = "find the project'" + projName + "' in the Package View";
		  SWTBotTree packageTree = packageExplorerBot.tree();
		  SWTBotTreeItem projectFolder = packageTree.expandNode(projName);
		  
		  //Find the src folder
		  operationMsg = "find the src folder for project '" + projName + "'";
		  SWTBotTreeItem srcFolder = projectFolder.expandNode("src");
		  
		  //find the file
		  operationMsg = "find the file '" + fileName + "' in project '" + projName + "'";
		  SWTBotTreeItem srcFile = srcFolder.expandNode(fileName);
		  srcFile.select();

		  //open the file
		  operationMsg = "open the file '" + fileName + "' in project '" + projName + "'";
		  topLevelBot.menu(WizardConstants.NAVIGATE_MENU).menu(WizardConstants.OPEN_ITEM).click();
	  }
	  catch (Exception e)
	  {
		  throw new X10DT_Test_Exception("Failed to " + operationMsg +
				  " when opening X10 file '" + fileName + "'.\n        Reason: " + e.getMessage());
	  }
  }

  //Open up the 'Open X10 Type...' dialog and look for a few sample types
  public static boolean findX10TypeDef(String typeName, String searchString)  throws X10DT_Test_Exception
  {
	  String operationMsg = null;			//string describing the current operation, for use in constructing error messages
	  String dialogContextMsg = null;		//string identifying the current dialog context, for use in constructing error messages

	  boolean found = false;	//assume the worst

	  try {
		  // Open the X10 Type dialog
		  operationMsg = "access the '" +
		  					WizardConstants.NAVIGATE_MENU +":"+ WizardConstants.OPEN_X10_TYPE_ITEM +"' menu";
		  topLevelBot.menu(WizardConstants.NAVIGATE_MENU).menu(WizardConstants.OPEN_X10_TYPE_ITEM).click();

		  //
		  //Set up the new X10 project - specify project name and select whether or not to create Hello World sample code
		  //
		  dialogContextMsg = " the '" + WizardConstants.OPEN_X10_TYPE_DIALOG_TITLE + "' dialog";
		  
		  //Set up a shell for the X10 Type Dialog
		  operationMsg = "find" + dialogContextMsg;
		  SWTBotShell x10TypeShell = topLevelBot.shell(WizardConstants.OPEN_X10_TYPE_DIALOG_TITLE);
		  x10TypeShell.activate();
		  SWTBot x10TypeBot = x10TypeShell.bot();

		  // find the text box for the search patterns, and set the search string
		  operationMsg = "set the '" + WizardConstants.X10_TYPE_SEARCH_PATTERN + "'text box in" + dialogContextMsg;
		  x10TypeBot.textWithLabel(WizardConstants.X10_TYPE_SEARCH_PATTERN).setText(searchString);

			  //find the list box for the types we found
		  operationMsg = "find the '" + WizardConstants.X10_TYPE_SEARCH_LISTBOX + "' list box in" + dialogContextMsg;
		  SWTBotTable typeList = x10TypeBot.tableWithLabel(WizardConstants.X10_TYPE_SEARCH_LISTBOX);

		  operationMsg = "waiting for the '" + WizardConstants.X10_TYPE_SEARCH_LISTBOX + "' list box to fill, in" + dialogContextMsg;
//		  try {
//We need to wait for the table to fill, but waitUntil just doesn't seem to work. The condition seems to 
// be blocked from occurring -  that is, the table never gets any rows - 
// so it just waits for the timeout, then throws the exception
//	doesn't work			  topLevelBot.waitUntil(Conditions.tableHasRows(typeList, 1), 5000);
//	doesn't work			  x10TypeBot.waitUntil(Conditions.tableHasRows(typeList, 1), 5000);	//give it a few seconds to find something
//		  }
//		  catch (TimeoutException e) {
//			  //not a big deal - it just means we didn't find a match, and we'll return false
//			  System.out.println("timed out waiting for the type list to populate"); /*debug*/
//		  }

		  //workaround for the waitUntil problem. Do our own wait. We'll wait for at least one line to appear
		  int timer = 0;
		  int rowCount = typeList.rowCount();
		  while ((rowCount <= 0) && (timer++ < 5)) {
			  Thread.sleep(1000);
			  rowCount = typeList.rowCount();
		  }

		  // look for the expected type in the list

		  operationMsg = "find X10 type '" + typeName + "' by searching on '" + searchString + "' in" + dialogContextMsg;
		  int i=0;
		  while ((!found) && (i < rowCount))
		  {
			  found = typeList.getTableItem(i).getText().contains(typeName);
			  i++;
		  }
		  
		  // throw a regular exception if we don't find anything (the X10DT_Test_Exception error message
		  //		gets properly assembled by doing it this way)
		  if (!found) {
			  throw new Exception(((rowCount == 0) ?  "Type list is empty":"No match found in list"));
		  }

		  //that's enough for now.  find the Cancel button
		  operationMsg = "find the '" + WizardConstants.CANCEL_BUTTON + "' button in" + dialogContextMsg;
		  SWTBotButton cancelButton = x10TypeBot.button(WizardConstants.CANCEL_BUTTON);
		  cancelButton.click();
		  
		  topLevelBot.waitUntil(Conditions.shellCloses(x10TypeShell));
	  }
	  catch (Exception e)
	  {
		  throw new X10DT_Test_Exception("Failed to " + operationMsg + "'.\n        Reason: " + e.getMessage());
	  }

	  return found;
  }

  //Set up Java backend run configuration and launch application
  //
  public static void createAndRunJavaBackEndLaunchConfig(String launchName, String projectName, String mainTypeName) throws X10DT_Test_Exception
  {
	  String operationMsg = null;			//string describing the current operation, for use in constructing error messages
	  String dialogContextMsg = null;		//string identifying the current dialog context, for use in constructing error messages

	  try {
		  // Open the X10 Run Configuration dialog
		  dialogContextMsg = " the '" + LaunchConstants.RUN_CONF_DIALOG_TITLE + "' dialog";

		  operationMsg = "access the '" + LaunchConstants.RUN_MENU +":"+ LaunchConstants.RUN_CONFS_MENU_ITEM +"' menu";
		  topLevelBot.menu(LaunchConstants.RUN_MENU).menu(LaunchConstants.RUN_CONFS_MENU_ITEM).click();

		  // Wait for the Run Configuration dialog to open
		  operationMsg = "find" + dialogContextMsg;
		  
		  topLevelBot.waitUntil(Conditions.shellIsActive(LaunchConstants.RUN_CONF_DIALOG_TITLE));
		  SWTBotShell configsShell = topLevelBot.shell(LaunchConstants.RUN_CONF_DIALOG_TITLE);
		  configsShell.activate();
		  SWTBot configsBot = configsShell.bot();

		  //Select Java Back-End application configuration
		  operationMsg = "select the '" + LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_TYPE + "' item in" + dialogContextMsg;
		  SWTBotTreeItem x10AppItem = configsBot.tree().getTreeItem(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_TYPE);
		  x10AppItem.doubleClick();

		  //enter the project configuration name
		  operationMsg = "enter the configuration name '" + launchName + "' in field '" +
		  					LaunchConstants.JAVA_BACK_END_PROJECT_DIALOG_NAME_FIELD + "' of" + dialogContextMsg;
		  SWTBotText launchNameText = configsBot.textWithLabel(LaunchConstants.JAVA_BACK_END_PROJECT_DIALOG_NAME_FIELD);
		  launchNameText.setText(launchName);

		  //pick the the 'Main' dialog tab
		  operationMsg = "select the '" + LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_MAIN_TAB + "' tab in"+ dialogContextMsg;
		  SWTBotCTabItem mainTab = configsBot.cTabItem(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_MAIN_TAB);
		  mainTab.activate();
		  
		  //set the project name
		  operationMsg = "enter  '" + projectName + "' in field " + LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_PROJECT + "' of" + dialogContextMsg;
		  configsBot.textInGroup(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_PROJECT, 0).setText(projectName);

		  //set the main class name
		  operationMsg = "enter  '" + mainTypeName + "' in field " + LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_MAIN_CLASS + "' of" + dialogContextMsg;
		  configsBot.textInGroup(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_MAIN_CLASS, 0).setText(mainTypeName);

		  //click the RUN button
		  operationMsg = "click the '" + LaunchConstants.RUN_BUTTON + "' button in" + dialogContextMsg;
		  configsBot.button(LaunchConstants.RUN_BUTTON).click();
	  }
	  catch (Exception e)
	  {
		  throw new X10DT_Test_Exception("Failed to " + operationMsg + "'.\n        Reason: " + e.getMessage());
	  }

  }

  
  public static void createCPPBackEndProject(String projName, boolean withHello) throws Exception {
	    topLevelBot.menu(WizardConstants.FILE_MENU).menu(WizardConstants.NEW_MENU_ITEM).menu(WizardConstants.PROJECTS_SUB_MENU_ITEM).click();

	    SWTBotShell newProjShell = topLevelBot.shell(WizardConstants.NEW_PROJECT_DIALOG_TITLE);

	    newProjShell.activate();

	    SWTBot newProjBot = newProjShell.bot();
	    SWTBotTreeItem x10ProjItems = newProjBot.tree().expandNode(WizardConstants.X10_FOLDER);

	    x10ProjItems.select(WizardConstants.X10_PROJECT_CPP_BACKEND);

	    SWTBotButton nextBut = newProjBot.button(WizardConstants.NEXT_BUTTON);

	    nextBut.click();

	    SWTBotShell newX10ProjShell = topLevelBot.shell(WizardConstants.X10_PROJECT_SHELL_CPP_BACKEND);
	    newX10ProjShell.activate();
	    SWTBot newX10ProjBot = newX10ProjShell.bot();

	    newX10ProjBot.textWithLabel(WizardConstants.NEW_CPP_PROJECT_NAME_FIELD).setText(projName);
	    if (withHello) {
	      newX10ProjBot.checkBox(WizardConstants.NEW_X10_PROJECT_HELLO_SOURCE_CHECKBOX).select();
	    } else {
		      newX10ProjBot.checkBox(WizardConstants.NEW_X10_PROJECT_HELLO_SOURCE_CHECKBOX).deselect();
	    }
	    newX10ProjBot.button(WizardConstants.FINISH_BUTTON).click();

	    topLevelBot.waitUntil(Conditions.shellCloses(newX10ProjShell));
	  }

}
