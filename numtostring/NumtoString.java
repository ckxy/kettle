package org.pentaho.di.trans.steps.numtostring;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.progression.ProgressionData;
import org.pentaho.di.trans.steps.progression.ProgressionMeta;

public class NumtoString extends BaseStep implements StepInterface{
	
	private NumtoStringMeta meta;
	private NumtoStringData data;

	public NumtoString(StepMeta stepMeta, StepDataInterface stepDataInterface,
			int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException
	{
		int i;
		Object[] row=getRow();
		if (row==null)
		{
			setOutputDone();
			return false;
		}
		
		RowMetaInterface imeta = getInputRowMeta();
		if (imeta == null) {
			imeta = new RowMeta();
			this.setInputRowMeta(imeta);
		}
		
		int rL[] = new int[imeta.size()];
		for(i=0;i<imeta.size();i++){
			rL[i] = row[i].toString().length();
		}
		
		String rT[]=imeta.getFieldNamesAndTypes(imeta.size());
		String regex = "\\((.*?)\\)";
		Pattern p = Pattern.compile(regex);
		Matcher m;
		for(i=0;i<imeta.size();i++){
			m = p.matcher(rT[i]);
			while (m.find()) {
				rT[i] = m.group();
				rT[i] = rT[i].replace("(", "");
				rT[i] = rT[i].replace(")", "");
			}
		}
		
		data.setPRow(imeta.size());
		data.setRL(rL);
		data.setRT(rT);

		if (first) {
			first = false;
			data.outputRowMeta = getInputRowMeta().clone();
			String rN[] = new String[data.outputRowMeta.size()];
			rN = data.outputRowMeta.getFieldNames();
			data.setRN(rN);
			data.newoutputRowMeta = new RowMeta();
			data.newFields(data.newoutputRowMeta, getStepname(), null, null, this, repository, metaStore);
		}

		row = changetoStr(imeta, row, rT);

		putRow(data.newoutputRowMeta, row);
			
		return true;
	}
	
	private Object[] changetoStr(RowMetaInterface inputRowMeta,
			Object[] inputRowData,String type[]) {
		int i;
		Object[] row = new Object[data.outputRowMeta.size()];
		for (i = 0; i < inputRowMeta.size(); i++)
			if(type[i].compareTo("Number")==0||type[i].compareTo("Integer")==0||type[i].compareTo("Big Number")==0)
				row[i] = inputRowData[i].toString()+"*";
			else
				row[i] = inputRowData[i];
		return row;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (NumtoStringMeta) smi;
		data = (NumtoStringData) sdi;

		if (super.init(smi, sdi)){
			data.readsRows = getStepMeta().getRemoteInputSteps().size()>0;
	        List<StepMeta> previous = getTransMeta().findPreviousSteps(getStepMeta());
			if (previous != null && previous.size() > 0) {
				data.readsRows = true;
			}
			return true;
		}
		else
			return false;
	}

}
