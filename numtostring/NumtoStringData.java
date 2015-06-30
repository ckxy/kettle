package org.pentaho.di.trans.steps.numtostring;

import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;

public class NumtoStringData extends BaseStepData implements StepDataInterface{
	public RowMetaInterface outputRowMeta;
	public RowMetaInterface newoutputRowMeta;
	public boolean isFirst;
	private int pRow;
	private int rL[];
	private String rN[];
	private String rT[];
	public boolean readsRows;
	
	public NumtoStringData(){
		super();
	}
	
	public void newFields(RowMetaInterface row, String name,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore)
			throws KettleStepException {
		for (int i = 0; i < pRow; i++) {
			ValueMetaInterface v = null;
			if(rT[i].compareTo("Number")==0||rT[i].compareTo("Integer")==0||rT[i].compareTo("Big Number")==0)
				v = new ValueMeta(rN[i], ValueMetaInterface.TYPE_STRING, rL[i]+1, 0);
			else if(rT[i].compareTo("String")==0)
				v = new ValueMeta(rN[i], ValueMetaInterface.TYPE_STRING, rL[i], 0);
			else if(rT[i].compareTo("Data")==0)
				v = new ValueMeta(rN[i], ValueMetaInterface.TYPE_DATE, rL[i], 0);
			else if(rT[i].compareTo("Boolean")==0)
				v = new ValueMeta(rN[i], ValueMetaInterface.TYPE_BOOLEAN, rL[i], 0);
			else if(rT[i].compareTo("Binary")==0)
				v = new ValueMeta(rN[i], ValueMetaInterface.TYPE_BINARY, rL[i], 0);
			v.setOrigin(name);
			row.addValueMeta(v);
		}
	}
	
	public void setPRow(int pr){
		pRow = pr;
		rL = new int[pRow];
	}
	
	public int getPRow(){
		return pRow;
	}
	
	public void setRL(int r[]){
		rL = r;
	}
	
	public void setRN(String r[]){
		rN = r;
	}
	
	public void setRT(String r[]){
		rT = r;
	}


}
