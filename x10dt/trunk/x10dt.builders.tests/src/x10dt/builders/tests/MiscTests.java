package x10dt.builders.tests;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import x10dt.builders.data.Data;
import x10dt.tests.services.swbot.utils.PerspectiveUtils;
import x10dt.tests.services.swbot.utils.ProblemsViewUtils;
import x10dt.tests.services.swbot.utils.ProjectUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MiscTests {

		private static SWTWorkbenchBot	bot;
	 
		@BeforeClass
		public static void beforeClass() throws Exception {
			bot = new SWTWorkbenchBot();
			bot.viewByTitle("Welcome").close();
		}  
	 
		/**
		 * The next test checks a situation in which we have a file that refers to a missing type.
		 * First it checks that we initially get the right number of compilation errors.
		 * Second it checks that when we add the missing type, all compilation errors go away.
		 * 
		 */
		@Test
		public void createAndCompileHi() throws Exception {
			PerspectiveUtils.switchToX10Perspective(bot);
			ProjectUtils.createX10ProjectWithJavaBackEndFromTopMenu(bot, Data.MyProject);
			SWTBotView pkgView = bot.viewByTitle("Package Explorer");
			SWTBotTree pkgTree = pkgView.bot().tree();
			SWTBotTreeItem projItem = pkgTree.getTreeItem(Data.MyProject);
			projItem.expand();
			SWTBotTreeItem srcItem = projItem.getNode("src");
			srcItem.expand();
			ProjectUtils.createClass(bot, "Hi");
			SWTBotEclipseEditor srcEditor = bot.editorByTitle("Hi.x10").toTextEditor();
			srcEditor.setText(Data.Hi);
			srcEditor.save();
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
			String[] errors = ProblemsViewUtils.getErrorMessages(bot);
			
			if (errors.length != 3)
				Assert.assertTrue("Compiling Hi did not yield the right number of errors: " + errors.length, false);
			
			ProjectUtils.createPackage(bot, Data.MyProject, "src", Data.pac);
			ProjectUtils.createClass(bot, "Howdy");
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
			errors = ProblemsViewUtils.getErrorMessages(bot);
			if (errors.length != 0)
				Assert.assertTrue("All compilation errors should have been cleared: " + errors.length, false);

		}
	 
	
	 
//		@Test
//		public void NoJavaBuilder() throws Exception {
//			SWTBotShell mainShell = bot.activeShell();
//			
//			bot.menu("Window").menu("Open Perspective").menu("X10").click();
//			bot.menu("File").menu("New").menu("X10 Project (Java back-end)").click();
//			 
//			SWTBotShell shell = bot.shell("New X10 Project (Java back-end)");
//			shell.activate();
//
//			bot.textWithLabel("Name:").setText("MyFirstProject");
//			bot.button("Finish").click();
//			
//			mainShell.activate();
//			
//			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
//			IProject project = wsRoot.getProject("MyFirstProject");
//			IProjectDescription description = project.getDescription();
//	    	ICommand[] commands = description.getBuildSpec();
//	    	for(int i = 0; i < commands.length; i++){
//	    		if (commands[i].getBuilderName().equals(JavaCore.BUILDER_ID)){
//	    			Assert.assertTrue(false);
//	    		}
//	    	}
//		}
	 
	 
		@AfterClass
		public static void sleep() {
			bot.sleep(2000);
		}
	 
}
