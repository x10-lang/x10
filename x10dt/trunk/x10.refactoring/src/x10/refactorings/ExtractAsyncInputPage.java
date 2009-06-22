package x10.refactorings;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
//import org.eclipse.jface.dialogs.Dialog;

public class ExtractAsyncInputPage extends UserInputWizardPage implements Listener {
	
	private Text fText;
	
    public ExtractAsyncInputPage(String name) {
	super(name);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {

    	initializeDialogUnits(parent);
	Composite result= new Composite(parent, SWT.NONE);
    //	Composite result = (Composite)getControl();
	GridLayout layout= new GridLayout();
	layout.numColumns = 4;
	result.setLayout(layout);
	//System.out.println("Parent size is "+parent.getSize());
	//System.out.println("Result size is "+result.getSize());
	Label testlabel = new Label(result,SWT.HORIZONTAL);
	GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gd.horizontalSpan= 1;
	testlabel.setLayoutData(gd);
	testlabel.setText("Place:");
		//Group placeborder = new Group(result, SWT.NONE);
	//placeborder.setText("Place");
	fText = new Text(result,SWT.BORDER | SWT.SINGLE);
	//test.setSize(result.getSize().x - testlabel.getSize().x,test.getSize().y);
	gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gd.horizontalSpan= 1;
	fText.setLayoutData(gd);
	fText.addListener(SWT.KeyUp,this);
	setControl(result);

	//		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaHelpContextIds.INTRODUCE_FACTORY_WIZARD_PAGE);		
    }

    public void handleEvent(Event e){
    	Widget source = e.widget;
    	String fTextResult = "";
    	if (source == fText) {
    		fTextResult = fText.getText();
    		if (fTextResult == null)
    			fTextResult = "";
    	}
    	getExtractAsyncRefactoring().setPlace(fTextResult);
//    	System.out.println("Key pressed --> "+fTextResult);
    }
    
    private ExtractAsyncRefactoring getExtractAsyncRefactoring() {
	return (ExtractAsyncRefactoring) getRefactoring();
    }
}
