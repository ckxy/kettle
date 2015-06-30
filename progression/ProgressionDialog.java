package org.pentaho.di.ui.trans.steps.progression;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.provider.local.LocalFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransPreviewFactory;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.steps.csvinput.CsvInput;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.mytest.MyTestMeta;
import org.pentaho.di.trans.steps.progression.ProgressionMeta;
import org.pentaho.di.trans.steps.randomvalue.RandomValueMeta;
import org.pentaho.di.trans.steps.textfileinput.EncodingType;
import org.pentaho.di.trans.steps.textfileinput.TextFileInput;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.EnterNumberDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboValuesSelectionListener;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.di.ui.trans.dialog.TransPreviewProgressDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class ProgressionDialog extends BaseStepDialog implements StepDialogInterface{
	
	private TextVar tFirstTerm,tDifference,tTerms,tName;
	private Button bOK,bCancel; 
	private Label lFirstTerm,lDifference,lTerms,lName;
	private ProgressionMeta inputmeta;

	public ProgressionDialog(Shell parent, Object in,
	           TransMeta transMeta, String stepname) {
	       super(parent, (BaseStepMeta)in, transMeta, stepname);
	       inputmeta = (ProgressionMeta)in;
	       // TODO Auto-generated constructor stub
	    }

	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		//shell
	    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
	    FormLayout formLayout = new FormLayout();
	    formLayout.marginWidth = Const.FORM_MARGIN;
	    formLayout.marginHeight = Const.FORM_MARGIN;
	    shell.setLayout(formLayout);
	    
	    ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				inputmeta.setChanged();
			}
		};
		changed = inputmeta.hasChanged();
	    
	    int middle = props.getMiddlePct();
	    int margin = Const.MARGIN;
	    	    
	    formLayout.marginWidth = Const.FORM_MARGIN;
	    formLayout.marginHeight = Const.FORM_MARGIN;

	    shell.setLayout(formLayout);
	    shell.setText("Progression");
	    props.setLook(shell);
	    
	    wlStepname = new Label(shell, SWT.RIGHT);//步骤名称标签设置
	    wlStepname.setText("stepname"); 
	    props.setLook(wlStepname);
	    fdlStepname = new FormData();
	    fdlStepname.left = new FormAttachment(0, 0);
	    fdlStepname.right = new FormAttachment(middle, -margin);
	    fdlStepname.top = new FormAttachment(0, margin);
	    wlStepname.setLayoutData(fdlStepname);
	    wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	    wStepname.setText("Progression");
	    props.setLook(wStepname);
	    wStepname.addModifyListener(lsMod);
	    fdStepname = new FormData();
	    fdStepname.left = new FormAttachment(middle, 0);
	    fdStepname.top = new FormAttachment(0, margin);
	    fdStepname.right = new FormAttachment(100, 0);
	    wStepname.setLayoutData(fdStepname);
		Control lc = wStepname;
		
		lFirstTerm = new Label(shell, SWT.RIGHT);
		lFirstTerm.setText("firstterm"); 
	    props.setLook(lFirstTerm);
	    FormData flFirstTerm = new FormData();
	    flFirstTerm.top = new FormAttachment(lc, margin);
	    flFirstTerm.left = new FormAttachment(0, 0);
	    flFirstTerm.right = new FormAttachment(middle, -margin);
	    lFirstTerm.setLayoutData(flFirstTerm);
	    tFirstTerm = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	    tFirstTerm.setText("0");
	    props.setLook(tFirstTerm);
	    tFirstTerm.addModifyListener(lsMod);
	    FormData ftFirstTerm = new FormData();
	    ftFirstTerm.top = new FormAttachment(lc, margin);
	    ftFirstTerm.left = new FormAttachment(middle, 0);
	    ftFirstTerm.right = new FormAttachment(100, 0);
	    tFirstTerm.setLayoutData(ftFirstTerm);
	    lc = tFirstTerm;
	    
	    lDifference = new Label(shell, SWT.RIGHT);
	    lDifference.setText("difference"); 
	    props.setLook(lDifference);
	    FormData flDifference = new FormData();
	    flDifference.top = new FormAttachment(lc, margin);
	    flDifference.left = new FormAttachment(0, 0);
	    flDifference.right = new FormAttachment(middle, -margin);
	    lDifference.setLayoutData(flDifference);
	    tDifference = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	    tDifference.setText("1");
	    props.setLook(tDifference);
	    tDifference.addModifyListener(lsMod);
	    FormData ftDifference = new FormData();
	    ftDifference.top = new FormAttachment(lc, margin);
	    ftDifference.left = new FormAttachment(middle, 0);
	    ftDifference.right = new FormAttachment(100, 0);
	    tDifference.setLayoutData(ftDifference);
	    lc = tDifference;
	    
	    lTerms = new Label(shell, SWT.RIGHT);
	    lTerms.setText("terms"); 
	    props.setLook(lTerms);
	    FormData flTerms = new FormData();
	    flTerms.top = new FormAttachment(lc, margin);
	    flTerms.left = new FormAttachment(0, 0);
	    flTerms.right = new FormAttachment(middle, -margin);
	    lTerms.setLayoutData(flTerms);
	    tTerms = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	    tTerms.setText("5");
	    props.setLook(tTerms);
	    tTerms.addModifyListener(lsMod);
	    FormData ftTerms = new FormData();
	    ftTerms.top = new FormAttachment(lc, margin);
	    ftTerms.left = new FormAttachment(middle, 0);
	    ftTerms.right = new FormAttachment(100, 0);
	    tTerms.setLayoutData(ftTerms);
	    lc = tTerms;
	    
	    lName = new Label(shell, SWT.RIGHT);
	    lName.setText("name"); 
	    props.setLook(lName);
	    FormData flName = new FormData();
	    flName.top = new FormAttachment(lc, margin);
	    flName.left = new FormAttachment(0, 0);
	    flName.right = new FormAttachment(middle, -margin);
	    lName.setLayoutData(flName);
	    tName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	    tName.setText("Num");
	    props.setLook(tName);
	    tName.addModifyListener(lsMod);
	    FormData ftName = new FormData();
	    ftName.top = new FormAttachment(lc, margin);
	    ftName.left = new FormAttachment(middle, 0);
	    ftName.right = new FormAttachment(100, 0);
	    tName.setLayoutData(ftName);
	    lc = tName;
		
	    bOK = new Button(shell, SWT.PUSH);
	    bOK.setText("ok");
	    bCancel = new Button(shell, SWT.PUSH);
	    bCancel.setText("cancel");
	    
	    setButtonPositions(new Button[] {bOK,bCancel}, margin, lc);
	    
	    lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		bCancel.addListener(SWT.Selection, lsCancel);
		bOK.addListener    (SWT.Selection, lsOK    );
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener(lsDef);
		tFirstTerm.addSelectionListener(lsDef);
		tDifference.addSelectionListener(lsDef);
		tTerms.addSelectionListener(lsDef);
		tName.addSelectionListener(lsDef);
		
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );
		
		shell.getSize();
		getData();
		inputmeta.setChanged(changed);
		
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {  
            if (!display.readAndDispatch()) {  
                display.sleep();  
            }  
        }  
		
		return stepname;
	}
	
	public void getData() {
	    wStepname.setText(stepname);
	    tFirstTerm.setText(Double.toString(inputmeta.getFirstTerm()));
	    tDifference.setText(Double.toString(inputmeta.getDifference()));
	    tTerms.setText(Integer.toString(inputmeta.getTerms()));
	    tName.setText(inputmeta.getName());
	    wStepname.selectAll();
	    wStepname.setFocus();
	  }
		
	private void cancel()
	{
		stepname=null;
		inputmeta.setChanged(changed);
		dispose();
	}
	
	private void ok()
	{
		if (Const.isEmpty(wStepname.getText())) 
			return;
		stepname = wStepname.getText(); // return value
		inputmeta.setFirstTerm(Double.parseDouble(tFirstTerm.getText()));
		inputmeta.setDifference(Double.parseDouble(tDifference.getText()));
		inputmeta.setTerms(Integer.parseInt(tTerms.getText()));
		inputmeta.setName(tName.getText());
		dispose();
	}
}


	
