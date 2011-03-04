package x10dt.builders.tests;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import x10dt.builders.data.Data;
import x10dt.core.utils.Timeout;
import x10dt.builders.tests.utils.EditorMatcher;
import x10dt.tests.services.swbot.constants.WizardConstants;
import x10dt.tests.services.swbot.utils.PerspectiveUtils;
import x10dt.tests.services.swbot.utils.ProblemsViewUtils;
import x10dt.tests.services.swbot.utils.ProjectUtils;
import x10dt.tests.services.swbot.utils.SWTBotUtils;

import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;


@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public class DependencyTests {

	private static SWTWorkbenchBot	bot;

	/**
	 * This method creates the new SWTWorkbenchBot at the start of the class and closes the "Welcome" tab
	 * @throws Exception
	 */
		@BeforeClass
		public static void beforeClass() throws Exception {
			bot = new SWTWorkbenchBot();
			SWTBotUtils.closeWelcomeViewIfNeeded(bot);
		}  
		
		/**
		 * This method runs before each test and calls the method that sets up the 
		 * DependencyTests project
		 * @throws Exception
		 */
		@Before
		public void before() throws Exception {
			SWTBotPreferences.TIMEOUT = Timeout.SIXTY_SECONDS;
			setUp(Data.DependencyTestsProject);		
		}
		
		/**
		 * This method creates the test project.
		 * @throws Exception
		 */
		private void setUp(String project) throws Exception {
			PerspectiveUtils.switchToX10Perspective(bot);
			ProjectUtils.createX10ProjectWithJavaBackEndFromTopMenu(bot, project);
			SWTBotView pkgView = bot.viewByTitle("Package Explorer");
			SWTBotTree pkgTree = pkgView.bot().tree();
			SWTBotTreeItem projItem = pkgTree.getTreeItem(project);
			projItem.expand();
			SWTBotTreeItem srcItem = projItem.getNode("src");
			srcItem.expand();
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("Hello.x10").toTextEditor();
			srcEditor.setText(Data.Hello);
			srcEditor.save();
			createClass("A", Data.A);
			createClass("C", Data.C);
			ProjectUtils.createPackage(bot, project, "src", Data.pac);
			createClass("B", Data.B);
			ProjectUtils.createPackage(bot, project, "src", Data.pakpac);
			createClass("D", Data.D);
			waitForBuildToFinish();
			waitForProblemsView();
		}
		
		/**
		 * This method cleans up the workspace: it deletes the project created.
		 * @throws Exception
		 */
		private void cleanUp(String project) throws Exception {
			SWTBotView pkgView = bot.viewByTitle("Package Explorer");
			SWTBotTree pkgTree = pkgView.bot().tree();
			SWTBotTreeItem projItem = pkgTree.getTreeItem(project);
			projItem.select();
			bot.menu(WizardConstants.EDIT_MENU).menu(WizardConstants.DELETE_MENU_ITEM).click();
			SWTBotShell deleteShell= bot.shell(WizardConstants.DELETE_RESOURCES_SHELL);
		    deleteShell.activate();
		    bot.checkBox(WizardConstants.DELETE_PROJECT_CONTENTS).select();
		    bot.button(WizardConstants.OK_BUTTON).click();
		}
		
	 /**
	  * This method creates a class with the name specified and the text specified in the 
	  * class body
	  * @param name
	  * @param text
	  */
		private void createClass(String name, String text){
			ProjectUtils.createClass(bot, name);
			SWTBotEclipseEditor srcEditor = bot.editorByTitle(name + ".x10").toTextEditor();
			srcEditor.setText(text);
			srcEditor.save();
			bot.shells()[0].activate();
			bot.waitUntil(Conditions.waitForEditor(new EditorMatcher(name + ".x10")));
		}
		
		/**
		 * This method waits for a build to finish before continuing
		 * @throws Exception
		 */
		private void waitForBuildToFinish() throws Exception {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		}
		
		/** 
		 * This method waits for the "Problems" view to finish so that the information read
		 * from it is accurate when checking for errors during the tests.
		 * @throws Exception
		 */
		private void waitForProblemsView() throws Exception {
			final ExtendedMarkersView view = (ExtendedMarkersView) bot.viewByTitle("Problems").getViewReference().getView(true); //$NON-NLS-1$			
			if (Platform.getBundle("org.eclipse.core.runtime").getHeaders().get("Bundle-Version").toString().startsWith("3.6"))
				{
				// For Eclipse v.3.6.x
				Job.getJobManager().join(view.MARKERSVIEW_UPDATE_JOB_FAMILY, null);				
				}
			else
				{
				// For other versions of Eclipse
				bot.sleep(Timeout.FIVE_SECONDS);
				}
		}
		
		/**
		 * Test number 1 just verifies that the initial setup has no errors
		 * @throws Exception
		 */
		@Test
		public void depTest1() throws Exception {
			waitForProblemsView();
			String[] errors = ProblemsViewUtils.getErrorMessages(bot);
			Assert.assertTrue("Number of compilation errors should be 0:  " + errors.length, errors.length == 0);
		}
		
		/**
		 * Hello extends A and calls method methA of A. If we rename methA to methAA, then Hello should have a compilation error.
		 * @throws Exception
		 */
		@Test
		public void depTest2() throws Exception {
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("A.x10").toTextEditor();
			srcEditor.insertText(2, 23, "A");
			srcEditor.save();
			waitForBuildToFinish();
			waitForProblemsView();
			String[] errors = ProblemsViewUtils.getErrorResources(bot);
			Assert.assertTrue("Number of compilation errors should be 3:  " + errors.length, errors.length == 3);
		}
		
		/**
		 * Rename method methB in to methBB, this should cause a compilation error in Hello.
		 * @throws Exception
		 */
		@Test
		public void depTest3() throws Exception {
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("B.x10").toTextEditor();
			srcEditor.insertText(2, 23, "B");
			srcEditor.save();
			waitForBuildToFinish();
			waitForProblemsView();
			String[] errors = ProblemsViewUtils.getErrorResources(bot);
			Assert.assertTrue("Number of compilation errors should be 4:  " + errors.length, errors.length == 4);
		}
		
		/**
		 * This test deletes file C.x10. This should cause a compilation error in A.x10 and Hello.x10.
		 * Type C is reference indirectly in Hello.x10 (as the return type of a method call).
		 * @throws Exception
		 */
		@Test
		public void depTest4() throws Exception {
			SWTBotView pkgView = bot.viewByTitle("Package Explorer");
			SWTBotTree pkgTree = pkgView.bot().tree();
			SWTBotTreeItem projItem = pkgTree.getTreeItem(Data.DependencyTestsProject);
			projItem.expand();
			SWTBotTreeItem srcItem = projItem.getNode("src");
			srcItem.expand();
			SWTBotTreeItem CItem = srcItem.getNode("C.x10");
			CItem.select();
			deleteSelection();
			waitForBuildToFinish();
			waitForProblemsView();
		    String[] errors = ProblemsViewUtils.getErrorResources(bot);
		    assertError(errors, "Hello");
		}
		
		
		/**
		 * This test changes the name of a field in D. This field is referenced in Hello.x10, so this
		 * should cause a compilation error in that file.
		 * @throws Exception
		 */
		@Test
		public void depTest5() throws Exception {
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("D.x10").toTextEditor();
			srcEditor.insertText(2, 12, "1");
			srcEditor.save();
			waitForBuildToFinish();
			waitForProblemsView();
			String[] errors = ProblemsViewUtils.getErrorResources(bot);
			assertError(errors, "Hello");
		}
		
		/**
		 * In this test Hello.x10 has a field of type E. Deleting E should cause compilation errors in Hello.x10
		 * @throws Exception
		 */
		@Test
		public void depTest6() throws Exception {
			SWTBotView pkgView = bot.viewByTitle("Package Explorer");
			SWTBotTree pkgTree = pkgView.bot().tree();
			SWTBotTreeItem projItem = pkgTree.getTreeItem(Data.DependencyTestsProject);
			projItem.expand();
			SWTBotTreeItem srcItem = projItem.getNode("src").select();
			srcItem.expand();
			createClass("E", Data.E);
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("Hello.x10").toTextEditor();
			srcEditor.setText(Data.Hello1);
			srcEditor.save();
			srcItem.getNode("E.x10").select();
			deleteSelection();
			waitForProblemsView();
		    String[] errors = ProblemsViewUtils.getErrorResources(bot);
		    assertError(errors, "Hello");
		}
		
		/**
		 * In this test, Hello.x10 refers to the field f of type F. If we remove F, Hello.x10 should have a compilation error.
		 * @throws Exception
		 */
		@Test
		public void depTest7() throws Exception {
			SWTBotView pkgView = bot.viewByTitle("Package Explorer");
			SWTBotTree pkgTree = pkgView.bot().tree();
			SWTBotTreeItem projItem = pkgTree.getTreeItem(Data.DependencyTestsProject);
			projItem.expand();
			SWTBotTreeItem srcItem = projItem.getNode("src");
			srcItem.expand();
			SWTBotTreeItem pakpacItem = srcItem.getNode("pak.pac").select();
			pakpacItem.expand();
			createClass("F", Data.F);
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("Hello.x10").toTextEditor();
			srcEditor.setText(Data.Hello3);
			srcEditor.save();
			waitForBuildToFinish();
			SWTBotEclipseEditor DEditor = bot.editorByTitle("D.x10").toTextEditor();
			DEditor.setText(Data.D1);
			DEditor.save();
			waitForBuildToFinish();
			pakpacItem.getNode("F.x10").select();
			deleteSelection();
			waitForProblemsView();
			String[] errors = ProblemsViewUtils.getErrorResources(bot);
		    assertError(errors, "Hello");
		}
		
		/**
		 * In this test, Hello.x10 refers to the field f of type F. If we rename field f, Hello.x10 should have a compilation error.
		 * @throws Exception
		 */
		@Test
		public void depTest8() throws Exception {
			SWTBotView pkgView = bot.viewByTitle("Package Explorer");
			SWTBotTree pkgTree = pkgView.bot().tree();
			SWTBotTreeItem projItem = pkgTree.getTreeItem(Data.DependencyTestsProject);
			projItem.expand();
			SWTBotTreeItem srcItem = projItem.getNode("src");
			srcItem.expand();
			SWTBotTreeItem pakpacItem = srcItem.getNode("pak.pac").select();
			pakpacItem.expand();
			createClass("F", Data.F);
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("Hello.x10").toTextEditor();
			srcEditor.setText(Data.Hello3);
			srcEditor.save();
			waitForBuildToFinish();
			SWTBotEclipseEditor DEditor = bot.editorByTitle("D.x10").toTextEditor();
			DEditor.setText(Data.D1);
			DEditor.save();
			waitForBuildToFinish();
			SWTBotEclipseEditor FEditor = bot.editorByTitle("F.x10").toTextEditor();
			FEditor.insertText(2,20,"1");
			FEditor.save();
			waitForBuildToFinish();
			waitForProblemsView();
			String[] errors = ProblemsViewUtils.getErrorResources(bot);
		    assertError(errors, "Hello");
		}
		
		
		
		/**
		 * This method asserts that there is an error in errors that belongs to file.
		 * 
		 * @param errors
		 * @param file A filename specified without extension.
		 */
		private void assertError(String[] errors, String file){
			boolean fine = false;
		    for(String error: errors){
		    	if (error.contains(file)) 
		    		fine = true;
		    }
		    Assert.assertTrue("There should be compilation errors on the file Hello.x10:  " + errors.length, fine);
		}


		/**
		 * Deletes selected file.
		 */
		private void deleteSelection() throws Exception {
			bot.menu(WizardConstants.EDIT_MENU).menu(WizardConstants.DELETE_MENU_ITEM).click();
			SWTBotShell deleteShell= bot.shell(WizardConstants.DELETE_CONFIRMATION_SHELL);
		    deleteShell.activate();
		    bot.button(WizardConstants.OK_BUTTON).click();
		    Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		}
		
		/**
		 * This method cleans up the project after each test is run and verifies that everything is closed
		 * @throws Exception
		 */
		@After
		public void after() throws Exception {
			cleanUp(Data.DependencyTestsProject);
			bot.closeAllEditors();
			bot.closeAllShells();
			Assert.assertTrue("X10 Eclipse editors should all be closed.", bot.editors().isEmpty());
			Assert.assertTrue("X10 Eclipse shells should all be closed except main shell.", bot.shells().length == 1);
		}
	 
		/**
		 * This method tries to close anything that's still open after the last test is run.
		 */
		@AfterClass
		public static void sleep() {
			try
			{
				bot.closeAllEditors();
				bot.closeAllShells();
			} catch (Exception e) {}
		}
		
	 
}

