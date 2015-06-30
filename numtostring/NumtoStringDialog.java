package org.pentaho.di.ui.trans.steps.numtostring;

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
import org.pentaho.di.trans.steps.numtostring.*;
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

public class NumtoStringDialog extends BaseStepDialog implements StepDialogInterface{
	
	private Button bOK,bCancel; 
	private NumtoStringMeta inputmeta;

	public NumtoStringDialog(Shell parent, Object in,
	           TransMeta transMeta, String stepname) {
	       super(parent, (BaseStepMeta)in, transMeta, stepname);
	       inputmeta = (NumtoStringMeta)in;
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
	    shell.setText("NumtoString");
	    props.setLook(shell);
	    
	    wlStepname = new Label(shell, SWT.RIGHT);//�������Ʊ�ǩ����
	    wlStepname.setText("stepname"); 
	    props.setLook(wlStepname);
	    fdlStepname = new FormData();
	    fdlStepname.left = new FormAttachment(0, 0);
	    fdlStepname.right = new FormAttachment(middle, -margin);
	    fdlStepname.top = new FormAttachment(0, margin);
	    wlStepname.setLayoutData(fdlStepname);
	    wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	    wStepname.setText("NumtoString");
	    props.setLook(wStepname);
	    wStepname.addModifyListener(lsMod);
	    fdStepname = new FormData();
	    fdStepname.left = new FormAttachment(middle, 0);
	    fdStepname.top = new FormAttachment(0, margin);
	    fdStepname.right = new FormAttachment(100, 0);
	    wStepname.setLayoutData(fdStepname);
		Control lc = wStepname;
		
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
		dispose();
	}
}


	

