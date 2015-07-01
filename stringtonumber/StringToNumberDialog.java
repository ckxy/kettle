package org.pentaho.di.ui.trans.steps.stringtonumber;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.stringtonumber.StringToNumber;
import org.pentaho.di.trans.steps.stringtonumber.StringToNumberMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboValuesSelectionListener;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;


public class StringToNumberDialog extends BaseStepDialog implements StepDialogInterface {
	private static Class<?> PKG = StringToNumber.class;
	private StringToNumberMeta stringToNumberMeta;
	
	private TableView wFields;//显示行信息的表格
	private boolean isReceivingInput;
	private boolean initializing;

	public StringToNumberDialog(Shell parent, Object in,TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta)in, transMeta, stepname);
		stringToNumberMeta=(StringToNumberMeta)in;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String open() {
		/*****调用SWT框架UI*****/
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
		/*****调用SWT框架UI*****/

 	   changed = stringToNumberMeta.hasChanged();
 	    
 	   ModifyListener lsMod = new ModifyListener() {
 	      public void modifyText(ModifyEvent e) {
 	        stringToNumberMeta.setChanged();
 	      }
 	    };
 	    
 	   ModifyListener lsContent = new ModifyListener() {
 	      @Override
 	      public void modifyText(ModifyEvent arg0) {
 	        // asyncUpdatePreview();
 	      }
 	    };
 	    
 	    initializing = true;

 	    FormLayout formLayout = new FormLayout();
 	    formLayout.marginWidth = Const.FORM_MARGIN;
 	    formLayout.marginHeight = Const.FORM_MARGIN;
 	    
 	    shell.setLayout(formLayout);
 	    shell.setText(BaseMessages.getString(PKG, "StringToNumberDialog.Shell.Title")); 
 	    
 	    int middle = props.getMiddlePct();
 	    int margin = Const.MARGIN;
 	    
 	    wlStepname = new Label(shell, SWT.RIGHT);
 	    wlStepname.setText(BaseMessages.getString(PKG, "StringToNumberDialog.Stepname.Label")); 
 	    props.setLook(wlStepname);
 	    fdlStepname = new FormData();
 	    fdlStepname.left = new FormAttachment(0, 0);
 	    fdlStepname.right = new FormAttachment(middle, -margin);
 	    fdlStepname.top = new FormAttachment(0, margin);
 	    wlStepname.setLayoutData(fdlStepname);
 	    
 	    wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 	    props.setLook(wStepname);
 	    fdStepname = new FormData();
 	    fdStepname.left = new FormAttachment(middle, 0);
 	    fdStepname.top = new FormAttachment(0, margin);
 	    fdStepname.right = new FormAttachment(100, 0);
 	    wStepname.setLayoutData(fdStepname);
 	    
 	    Control lastControl = wStepname;//记录当前组件以进行界面调整
 	    
 	    //该模块只是尝试能否获得上一个节点的数据
 	    isReceivingInput = transMeta.findNrPrevSteps(stepMeta) > 0;
 	    if (isReceivingInput) {

 	      RowMetaInterface previousFields;
 	      try {
 	        previousFields = transMeta.getPrevStepFields(stepMeta);
 	      } catch (KettleStepException e) {
 	        new ErrorDialog(shell, BaseMessages.getString(PKG, "StringToNumberDialog.ErrorDialog.UnableToGetInputFields.Title"), BaseMessages.getString(PKG,
 	               "StringToNumberDialog.ErrorDialog.UnableToGetInputFields.Message"), e);
 	        previousFields = new RowMeta();
 	      }
 	    } 
 	    
 	    //设置底部按钮
 	    wOK = new Button(shell, SWT.PUSH);
 	    wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); 
 	    wCancel = new Button(shell, SWT.PUSH);
 	    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); 
 	    wGet = new Button(shell, SWT.PUSH);
 	    wGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields")); 
 	    wGet.setEnabled(isReceivingInput);
 	    setButtonPositions(new Button[] { wOK, wGet, wCancel }, margin, null);
 	    
 	    //设定表格字段
 	   ColumnInfo[] colinf = new ColumnInfo[] {
 	 		new ColumnInfo(BaseMessages.getString(PKG, "StringToNumberDialog.IsTransColumn.Column"), ColumnInfo.COLUMN_TYPE_CCOMBO,getIsTransType(), true),
 			new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_NAME")), ColumnInfo.COLUMN_TYPE_TEXT, false),
	        new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_TYPE")), ColumnInfo.COLUMN_TYPE_TEXT, false),
	        new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_LENGTH")), ColumnInfo.COLUMN_TYPE_TEXT, false),
	        new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_PRECISION")), ColumnInfo.COLUMN_TYPE_TEXT, false),
	        new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_CURRENCY")), ColumnInfo.COLUMN_TYPE_TEXT, false),
	        new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_DECIMAL")), ColumnInfo.COLUMN_TYPE_TEXT, false),
	        new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_GROUP")), ColumnInfo.COLUMN_TYPE_TEXT, false),
	        new ColumnInfo(BaseMessages.getString(PKG, stringToNumberMeta.getDescription("FIELD_TRIM_TYPE")), ColumnInfo.COLUMN_TYPE_TEXT, false) };

 	   colinf[2].setComboValuesSelectionListener(new ComboValuesSelectionListener() {
 	   public String[] getComboValues(TableItem tableItem, int rowNr, int colNr) {
 		   String[] comboValues = new String[] {};
 		   int type = ValueMeta.getType(tableItem.getText(colNr - 1));
 		   switch (type) {
 		    case ValueMetaInterface.TYPE_DATE:
 		    comboValues = Const.getDateFormats();
 		     break;
 		    case ValueMetaInterface.TYPE_INTEGER:
 		    case ValueMetaInterface.TYPE_BIGNUMBER:
 		    case ValueMetaInterface.TYPE_NUMBER:
 		      comboValues = Const.getNumberFormats();
 		      break;
 		    default:
 		      break;
 		    }
 		    return comboValues;
 		  }

 		});

 	   //设置行信息表格
 		wFields = new TableView(transMeta, shell, SWT.FULL_SELECTION | SWT.MULTI, colinf, 1, lsMod, props);
 		FormData fdFields = new FormData();
 		fdFields.top = new FormAttachment(lastControl, margin * 2);
 		fdFields.bottom = new FormAttachment(wOK, -margin * 2);
 		fdFields.left = new FormAttachment(0, 0);
 		fdFields.right = new FormAttachment(100, 0);
 		wFields.setLayoutData(fdFields);
 		wFields.setContentListener(lsContent);

 		//为各个按钮添加事件侦听器
 		lsCancel = new Listener() {
 		  public void handleEvent(Event e) {
 		    cancel();
 		  }
 		};
 		lsOK = new Listener() {
 		  public void handleEvent(Event e) {
 		    ok();
 		  }
 		};
 	    lsGet = new Listener() {
 	       public void handleEvent(Event e) {
 	    	   getRowFields();
 	       }
 	     };

 		wCancel.addListener(SWT.Selection, lsCancel);
 		wOK.addListener(SWT.Selection, lsOK);
 	    wGet.addListener(SWT.Selection, lsGet);
 		    
 		// Detect X or ALT-F4 or something that kills this window...
 	    shell.addShellListener(new ShellAdapter() {
 	      public void shellClosed(ShellEvent e) {
 	        cancel();
 	      }
 	    });
 	    
 	    setSize();

 	    getData();//读取相应Meta对象的配置信息
 	    
 	    initializing = false;

 	    //打开窗口
 	   shell.open();
 	    while (!shell.isDisposed()) {
 	      if (!display.readAndDispatch())
 	        display.sleep();
 	    }
 	    return stepname;
	}

	public void getData() {
	    getData(stringToNumberMeta, true);
	  }

	  /**
	   * Copy information from the meta-data input to the dialog fields.
	   */
	  public void getData(StringToNumberMeta stringToNumberMeta, boolean copyStepname) {
		//将stringToNumberMeta中的配置信息读到Dialog中
	    if (copyStepname) {
	      wStepname.setText(stepname);
	    }
	    if (isReceivingInput) {
		    //从stringToNumberMeta中读取表格中的数据
		    for (int i = 0; i < stringToNumberMeta.getInputFields().length; i++) {
		      TextFileInputField field = stringToNumberMeta.getInputFields()[i];//行信息表格配置
		
		      TableItem item = new TableItem(wFields.table, SWT.NONE);
		      int colnr = 1;
		      item.setText(colnr++,getTransTypeString(stringToNumberMeta.getTransIndex(i)));
		      item.setText(colnr++, Const.NVL(field.getName(), ""));
		      item.setText(colnr++, ValueMeta.getTypeDesc(field.getType()));
		      item.setText(colnr++, field.getLength() >= 0 ? Integer.toString(field.getLength()) : "");
		      item.setText(colnr++, field.getPrecision() >= 0 ? Integer.toString(field.getPrecision()) : "");
		      item.setText(colnr++, Const.NVL(field.getCurrencySymbol(), ""));
		      item.setText(colnr++, Const.NVL(field.getDecimalSymbol(), ""));
		      item.setText(colnr++, Const.NVL(field.getGroupSymbol(), ""));
		      item.setText(colnr++, Const.NVL(field.getTrimTypeDesc(), ""));
		    }
		    wFields.removeEmptyRows();
		    wFields.setRowNums();
		    wFields.optWidth(true);
		
		    wStepname.selectAll();//选中节点名称中的内容
		    wStepname.setFocus();//节点名称输入框获取焦点
	    }
	  }

	  //将修改过的配置信息写入到stringToNumberMeta中
	  private void getInfo(StringToNumberMeta stringToNumberMeta) {

	    if (isReceivingInput) {	    

		    /***写入表格信息***/
		    int nrNonEmptyFields = wFields.nrNonEmpty();
		    stringToNumberMeta.allocate(nrNonEmptyFields);
		    stringToNumberMeta.setTransFieldIndex(nrNonEmptyFields);
	
		    for (int i = 0; i < nrNonEmptyFields; i++) {
		      TableItem item = wFields.getNonEmpty(i);
		      stringToNumberMeta.getInputFields()[i] = new TextFileInputField();
	
		      int colnr = 2;
		      stringToNumberMeta.getInputFields()[i].setName(item.getText(colnr++));
		      stringToNumberMeta.getInputFields()[i].setType(ValueMeta.getType(item.getText(colnr++)));
		      stringToNumberMeta.getInputFields()[i].setLength(Const.toInt(item.getText(colnr++), -1));
		      stringToNumberMeta.getInputFields()[i].setPrecision(Const.toInt(item.getText(colnr++), -1));
		      stringToNumberMeta.getInputFields()[i].setCurrencySymbol(item.getText(colnr++));
		      stringToNumberMeta.getInputFields()[i].setDecimalSymbol(item.getText(colnr++));
		      stringToNumberMeta.getInputFields()[i].setGroupSymbol(item.getText(colnr++));
		      stringToNumberMeta.getInputFields()[i].setTrimType(ValueMeta.getTrimTypeByDesc(item.getText(colnr++)));
		      if(stringToNumberMeta.getInputFields()[i].getType() != ValueMeta.TYPE_STRING){
		    	  stringToNumberMeta.setTransIndex(i, false);//非String类型字段不能进行转换
		      }else{
		    	  stringToNumberMeta.setTransIndex(i,getTransType(item.getText(1)));//配置是否转换
		      }
		    }
		    wFields.removeEmptyRows();
		    wFields.setRowNums();
		    wFields.optWidth(true);
	
		    stringToNumberMeta.setChanged();
	    }
	  }

	  private void ok() {
	    if (Const.isEmpty(wStepname.getText()))
	      return;

	    getInfo(stringToNumberMeta);
	    stepname = wStepname.getText();
	    dispose();
	  }
	  
	  private void cancel() {
	    stepname = null;
	    stringToNumberMeta.setChanged(changed);
	    dispose();
	  }
	  
	  //读取上一个节点字段信息
	  private void getRowFields(){
		  RowMetaInterface previousFields;
		  List<ValueMetaInterface> valueMetas;
 	      try {
 	    	//获取上一个节点的字段信息
 	        previousFields = transMeta.getPrevStepFields(stepMeta);
 	        valueMetas = previousFields.getValueMetaList();
 	        
 	       wFields.table.removeAll();

 	       for (ValueMetaInterface valueMeta:valueMetas) {
 	         TableItem item = new TableItem(wFields.table, SWT.NONE);
 	         int colnr = 1;
 	         if(valueMeta.getType() != ValueMetaInterface.TYPE_STRING){
 	        	 item.setText(colnr++,BaseMessages.getString(PKG, "StringToNumberDialog.UnTrans.IsTransType"));//非String字段默认不转换
 	         }else{
 	        	item.setText(colnr++,BaseMessages.getString(PKG, "StringToNumberDialog.Trans.IsTransType"));//String字段默认转换
 	         }
 	         item.setText(colnr++, valueMeta.getName());
 	         item.setText(colnr++, valueMeta.getTypeDesc());
 	         item.setText(colnr++, valueMeta.getLength()+"");
 	         item.setText(colnr++, valueMeta.getPrecision()+"");
 	         item.setText(colnr++, valueMeta.getCurrencySymbol());
 	         item.setText(colnr++, valueMeta.getDecimalSymbol());
 	         item.setText(colnr++, valueMeta.getGroupingSymbol());
 	         item.setText(colnr++, valueMeta.getTrimType()+"");

 	       }
 	       wFields.removeEmptyRows();
 	       wFields.setRowNums();
 	       wFields.optWidth(true);
 	       
 	      } catch (KettleStepException e) {
 	        new ErrorDialog(shell, BaseMessages.getString(PKG, "StringToNumberDialog.ErrorDialog.UnableToGetInputFields.Title"), BaseMessages.getString(PKG,
 	               "StringToNumberDialog.ErrorDialog.UnableToGetInputFields.Message"), e);
 	        previousFields = new RowMeta();
 	      }
	  }
	  
	  private String[] getIsTransType(){
		  return new String[] {BaseMessages.getString(PKG, "StringToNumberDialog.Trans.IsTransType"),BaseMessages.getString(PKG, "StringToNumberDialog.UnTrans.IsTransType")};
	  }
	  private boolean getTransType(String transType){
		  if(transType.equals(BaseMessages.getString(PKG, "StringToNumberDialog.Trans.IsTransType"))){
			  return true;
		  }
		  else if(transType.equals(BaseMessages.getString(PKG, "StringToNumberDialog.UnTrans.IsTransType"))){
			  return false;
		  }
		  return false;
	  }
	  private String getTransTypeString(boolean transType){
		  if(transType){
			  return BaseMessages.getString(PKG, "StringToNumberDialog.Trans.IsTransType");
		  }
		  else{
			  return BaseMessages.getString(PKG, "StringToNumberDialog.UnTrans.IsTransType");
		  }
	  }
}
