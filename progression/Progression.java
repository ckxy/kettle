package org.pentaho.di.trans.steps.progression;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.UUIDUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.randomvalue.RandomValueMeta;

import java.util.List;

public class Progression extends BaseStep implements StepInterface{
	private ProgressionMeta meta;
	private ProgressionData data;

	public Progression(StepMeta stepMeta, StepDataInterface stepDataInterface,
			int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		// TODO Auto-generated constructor stub
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (ProgressionMeta) smi;
		data = (ProgressionData) sdi;

		if (super.init(smi, sdi)){
			System.out.println("1"+data.readsRows);
			data.readsRows = getStepMeta().getRemoteInputSteps().size()>0;
	        List<StepMeta> previous = getTransMeta().findPreviousSteps(getStepMeta());
			if (previous != null && previous.size() > 0) {
				data.readsRows = true;
			}
			System.out.println("2"+data.readsRows);
			return true;
		}
		else
			return false;
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		Object[] row;	
		if (data.readsRows) {
			System.out.println("71");
			row = getRow();
			if (row == null) {
				System.out.println("70");
				setOutputDone();
				return false;
			}

			if (first) {
				first = false;
				data.outputRowMeta = getInputRowMeta().clone();
				meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
			}
		} else {
			System.out.println("72");
			row = new Object[] {};
			incrementLinesRead();

			if (first) {
				first = false;
				data.outputRowMeta = new RowMeta();
				meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
			}
		}
		
		RowMetaInterface imeta = getInputRowMeta();
		if (imeta == null) {
			imeta = new RowMeta();
			this.setInputRowMeta(imeta);
		}

		row = getProgression(imeta, row);

		putRow(data.outputRowMeta, row);
		
		if (!data.readsRows)
		{
			System.out.println("73");
			setOutputDone();
			return false;
		}

		return true;
	}
	
	private Object[] getProgression(RowMetaInterface inputRowMeta,
			Object[] inputRowData) {
		Object[] row = new Object[data.outputRowMeta.size()];
		for (int i = 0; i < inputRowMeta.size(); i++)
			row[i] = inputRowData[i];
		for (int i = 0, index = inputRowMeta.size(); i < meta.getTerms(); i++, index++) {
				row[index] = i*meta.getDifference()+meta.getFirstTerm();
		}
		return row;
	}

}
