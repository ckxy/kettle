package org.pentaho.di.trans.steps.progression;

import java.util.Random;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class ProgressionData extends BaseStepData implements StepDataInterface{
	public boolean readsRows;
	public RowMetaInterface outputRowMeta;
	
	public ProgressionData() {
		super();
	}

}
