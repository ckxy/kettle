package org.pentaho.di.trans.steps.stringtonumber;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public class StringToNumberMeta extends BaseStepMeta implements StepMetaInterface {
	
	private static Class<?> PKG = StringToNumber.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

	private TextFileInputField[] inputFields;//字段信息
	private boolean[] transFieldIndex;//转换配置信息
	
	public StringToNumberMeta()
	{
		super();
		allocate(0);
		setTransFieldIndex(0);
	}
	@Override
	public void setDefault() {
		// TODO Auto-generated method stub

	}
	
	public void allocate(int nrFields) {
		inputFields = new TextFileInputField[nrFields];//申请输入域
	}
	
	public void setTransFieldIndex(int num){
		transFieldIndex = new boolean[num];
	}
	
	public boolean[] getTransFieldIndex(){
		return transFieldIndex;
	}
	
	public boolean setTransIndex(int index,boolean isTrans){
		if(transFieldIndex!=null && index<transFieldIndex.length && index>=0){
			transFieldIndex[index] = isTrans;
			return true;
		}
		return false;
	}

	public boolean getTransIndex(int index){
		if(transFieldIndex!=null && index<transFieldIndex.length && index>=0){
			return transFieldIndex[index];
		}
		return false;
	}
	public void loadXML(Node stepnode, List<DatabaseMeta> databases,Map<String, Counter> counters) throws KettleXMLException {
		// TODO Auto-generated method stub

		try
		{
			
			Node fields = XMLHandler.getSubNode(stepnode, getXmlCode("FIELDS"));
			int nrfields = XMLHandler.countNodes(fields, getXmlCode("FIELD"));
			
			allocate(nrfields);
			setTransFieldIndex(nrfields);

			for (int i = 0; i < nrfields; i++)
			{
				inputFields[i] = new TextFileInputField();
				
				Node fnode = XMLHandler.getSubNodeByNr(fields, getXmlCode("FIELD"), i);
				this.setTransIndex(i, XMLHandler.getTagValue(fnode, "istrans").compareTo("0") == 0);
				inputFields[i].setName( XMLHandler.getTagValue(fnode, getXmlCode("FIELD_NAME")) );
				inputFields[i].setType(  ValueMeta.getType(XMLHandler.getTagValue(fnode, getXmlCode("FIELD_TYPE"))) );
				inputFields[i].setCurrencySymbol( XMLHandler.getTagValue(fnode, getXmlCode("FIELD_CURRENCY")) );
				inputFields[i].setDecimalSymbol( XMLHandler.getTagValue(fnode, getXmlCode("FIELD_DECIMAL")) );
				inputFields[i].setGroupSymbol( XMLHandler.getTagValue(fnode, getXmlCode("FIELD_GROUP")) );
				inputFields[i].setLength( Const.toInt(XMLHandler.getTagValue(fnode, getXmlCode("FIELD_LENGTH")), -1) );
				inputFields[i].setPrecision( Const.toInt(XMLHandler.getTagValue(fnode, getXmlCode("FIELD_PRECISION")), -1) );
				inputFields[i].setTrimType( ValueMeta.getTrimTypeByCode( XMLHandler.getTagValue(fnode, getXmlCode("FIELD_TRIM_TYPE")) ) );
			}
		}
		catch (Exception e)
		{
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}

	public String getXML()
	{
		StringBuffer retval = new StringBuffer(500);

		retval.append("    ").append(XMLHandler.openTag(getXmlCode("FIELDS"))).append(Const.CR);
		for (int i = 0; i < inputFields.length; i++)
		{
			TextFileInputField field = inputFields[i];
			
	        retval.append("      ").append(XMLHandler.openTag(getXmlCode("FIELD"))).append(Const.CR);
			retval.append("        ").append(XMLHandler.addTagValue("istrans", getTransIndex(i)?"0":"1"));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_NAME"), field.getName()));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_TYPE"), ValueMeta.getTypeDesc(field.getType())));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_CURRENCY"), field.getCurrencySymbol()));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_DECIMAL"), field.getDecimalSymbol()));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_GROUP"), field.getGroupSymbol()));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_LENGTH"), field.getLength()));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_PRECISION"), field.getPrecision()));
			retval.append("        ").append(XMLHandler.addTagValue(getXmlCode("FIELD_TRIM_TYPE"), ValueMeta.getTrimTypeCode(field.getTrimType())));
            retval.append("      ").append(XMLHandler.closeTag(getXmlCode("FIELD"))).append(Const.CR);
		}
        retval.append("    ").append(XMLHandler.closeTag(getXmlCode("FIELDS"))).append(Const.CR);
        
		return retval.toString();
	}
	
	public Object clone()
	{
		Object retval = super.clone();
		return retval;
	}
	
	@Override
	public void saveRep(Repository rep, ObjectId id_transformation,
			ObjectId id_step) throws KettleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readRep(Repository rep, ObjectId id_step,
			List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
		// TODO Auto-generated method stub

	}

	public void getFields(RowMetaInterface rowMeta, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException
	{
		try {
			List<ValueMetaInterface> valueMetas = rowMeta.getValueMetaList();
			int i = 0;
			for(ValueMetaInterface valueMeta : valueMetas){
				if(this.getTransIndex(i)&&valueMeta.getType() == ValueMetaInterface.TYPE_STRING){
					//valueMeta.setType(ValueMetaInterface.TYPE_NUMBER);
					valueMeta = ValueMetaFactory.cloneValueMeta(valueMeta, ValueMetaInterface.TYPE_NUMBER);
					valueMeta.setCurrencySymbol(valueMetas.get(i).getCurrencySymbol());
					ValueMetaInterface storageMetadata = ValueMetaFactory.cloneValueMeta(valueMeta.getStorageMetadata(), ValueMetaInterface.TYPE_NUMBER);
		  			valueMeta.setStorageMetadata(storageMetadata);
		  			valueMeta.setOrigin(origin);
		  			rowMeta.setValueMeta(i, valueMeta);
				}
				i++;
			}
		}catch(Exception e) {
		    throw new KettleStepException(e);
		 }
		
	}
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta,
			StepMeta stepMeta, RowMetaInterface prev, String[] input,
			String[] output, RowMetaInterface info) {
		// TODO Auto-generated method stub

		CheckResult cr;
		if (prev==null || prev.size()==0)
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "CsvInputMeta.CheckResult.NotReceivingFields"), stepMeta); 
			remarks.add(cr);
		}
		else
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "CsvInputMeta.CheckResult.StepRecevingData",prev.size()+""), stepMeta);  
			remarks.add(cr);
		}
		
		// See if we have input streams leading to this step!
		if (input.length>0)
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "CsvInputMeta.CheckResult.StepRecevingData2"), stepMeta); 
			remarks.add(cr);
		}
		else
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "CsvInputMeta.CheckResult.NoInputReceivedFromOtherSteps"), stepMeta); 
			remarks.add(cr);
		}
	}
	
	public TextFileInputField[] getInputFields() {
		return inputFields;
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta,StepDataInterface stepDataInterface, int copyNr,TransMeta transMeta, Trans trans) {
		// TODO Auto-generated method stub
		return new StringToNumber(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		// TODO Auto-generated method stub
		return new StringToNumberData();	}

}
