package x10.refactorings.wizards;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import x10.refactorings.ExtractAsyncRefactoring;

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
        Composite result = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();

        layout.numColumns = 4;
        result.setLayout(layout);

        Label testlabel = new Label(result, SWT.HORIZONTAL);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

        gd.horizontalSpan = 1;
        testlabel.setLayoutData(gd);
        testlabel.setText("Place:");

        fText = new Text(result, SWT.BORDER | SWT.SINGLE);

        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = 1000;
        fText.setLayoutData(gd);
        fText.addListener(SWT.KeyUp, this);
        setControl(result);

        // PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
        // IJavaHelpContextIds.INTRODUCE_FACTORY_WIZARD_PAGE);
    }

    public void handleEvent(Event e) {
        Widget source = e.widget;
        String fTextResult = "";
        if (source == fText) {
            fTextResult = fText.getText();
            if (fTextResult == null)
                fTextResult = "";
        }
        getExtractAsyncRefactoring().setPlace(fTextResult);
    }

    private ExtractAsyncRefactoring getExtractAsyncRefactoring() {
        return (ExtractAsyncRefactoring) getRefactoring();
    }
}
