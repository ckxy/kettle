package org.pentaho.di.trans.steps.stringtonumber;


import java.util.List;


import org.pentaho.di.core.exception.KettleException;

import org.pentaho.di.core.row.ValueMetaInterface;

import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;


public class StringToNumber extends BaseStep implements StepInterface {
	private StringToNumberMeta meta;
	private StringToNumberData data;
	
	Object[] row;
	
	public StringToNumber(StepMeta stepMeta, StepDataInterface stepDataInterface,int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		// TODO Auto-generated constructor stub
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException
	{
		meta=(StringToNumberMeta)smi;
		data=(StringToNumberData)sdi;

		Object[] row = getRow();
		if (first) {
			first=false;
			
			if(row==null){
				setOutputDone();
				return false;
			}else{
				data.outputRowMeta = getInputRowMeta().clone();
				meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
			}
		}
		
		if(row==null){
			setOutputDone();
			return false;
		}else{
			int i = 0;
			List<ValueMetaInterface> valueMetas = data.outputRowMeta.getValueMetaList();
			try{
				for(ValueMetaInterface valueMeta:valueMetas){
					if(meta.getTransIndex(i) && valueMeta.getType() == ValueMetaInterface.TYPE_NUMBER){
							row[i] = Double.parseDouble(row[i].toString());//根据meta配置进行转换
					}
					i++;
				}
				putRow(data.outputRowMeta, row);
			}catch(NumberFormatException e){
				logError("\""+valueMetas.get(i).getName()+"\""+"字段中含有非纯数字字符串"+"\""+row[i].toString()+"\"");
				setErrors(1);
		        stopAll();
			}
		}
		
		return true;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi)
	{
		meta=(StringToNumberMeta)smi;
		data=(StringToNumberData)sdi;
		
		if (super.init(smi, sdi)) {
			return true;
		}
		return false;
	}
	
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
	    super.dispose(smi, sdi);
		}
		
}
