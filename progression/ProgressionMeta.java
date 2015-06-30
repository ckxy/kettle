package org.pentaho.di.trans.steps.progression;

import java.util.List;

import org.codehaus.groovy.tools.shell.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.randomvalue.RandomValue;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public class ProgressionMeta extends BaseStepMeta implements StepMetaInterface{

	private double firstTerm,difference;
	private int terms;
	private String tName;
	
	public ProgressionMeta(){
		super();
		setDefault();
	}
	
	@Override
	public void setDefault() {
		firstTerm = 0;
		difference = 1;
		terms = 5;
		tName = "Num";
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new Progression(stepMeta, stepDataInterface, copyNr, transMeta,
				trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new ProgressionData();
	}
	
	public void setFirstTerm(double ft){
		firstTerm = ft;
	}
	
	public void setDifference(double d){
		difference = d;
	}
	
	public void setTerms(int t){
		if(t>=0)
			terms = t;
		else
			terms = 0;
	}
	
	public void setName(String n){
		tName = n;
	}
	
	public double getFirstTerm(){
		return firstTerm;
	}
	
	public double getDifference(){
		return difference;
	}
	
	public int getTerms(){
		return terms;
	}
	
	public String getName(){
		return tName;
	}
	
	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) { 
	     return null;
	}

	
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore)
			throws KettleXMLException
	{
		readData(stepnode);
	}
	
	private void readData(Node stepnode) throws KettleXMLException
	{
		try{
			firstTerm = Double.parseDouble(XMLHandler.getTagValue(stepnode, "FIRSTTERM"));
			difference = Double.parseDouble(XMLHandler.getTagValue(stepnode, "DIFFERENCE"));
			terms = Integer.parseInt(XMLHandler.getTagValue(stepnode, "TERMS"));
			tName = XMLHandler.getTagValue(stepnode, "TNAME");
		}
		catch (Exception e){
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}
	
	public String getXML(){
		StringBuffer retval = new StringBuffer(500);
		retval.append("    ").append(XMLHandler.addTagValue("FIRSTTERM", Double.toString(firstTerm)));
		retval.append("    ").append(XMLHandler.addTagValue("DIFFERENCE", Double.toString(difference)));
		retval.append("    ").append(XMLHandler.addTagValue("TERMS", Integer.toString(terms)));
		retval.append("    ").append(XMLHandler.addTagValue("TNAME", tName));
		return retval.toString();
	}
	
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException
	{
		firstTerm = Double.parseDouble(rep.getStepAttributeString(id_step, "FIRSTTERM"));
		difference = Double.parseDouble(rep.getStepAttributeString(id_step, "DIFFERENCE"));
		terms = Integer.parseInt(rep.getStepAttributeString(id_step, "TERMS"));
		tName = rep.getStepAttributeString(id_step, "TNAME");
	}

	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException
	{
		try{

			rep.saveStepAttribute(id_transformation, id_step, "FIRSTTERM", Double.toString(firstTerm));
			rep.saveStepAttribute(id_transformation, id_step, "DIFFERENCE", Double.toString(difference));
			rep.saveStepAttribute(id_transformation, id_step, "TERMS", Integer.toString(terms));
			rep.saveStepAttribute(id_transformation, id_step, "TNAME", tName);
		}
		catch (Exception e){
		}
	}
	
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String input[], String output[], RowMetaInterface info, VariableSpace space, Repository repository, IMetaStore metaStore){
		CheckResult cr;
		if (prev==null || prev.size()==0){
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, "Not receiving any fields from previous steps!", stepMeta); 
			remarks.add(cr);
		}
		if(input.length<=0){
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, "No input received from other steps", stepMeta); 
			remarks.add(cr);
		}
	}
	
	public Object clone(){
		ProgressionMeta retval = (ProgressionMeta)super.clone();
		retval.setFirstTerm(this.getFirstTerm());
		retval.setDifference(this.getDifference());
		retval.setTerms(this.getTerms());
		return retval;
	}
	
	public void allocate(int count) {}
	
	public void getFields(RowMetaInterface row, String name,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore)
			throws KettleStepException {
		for (int i = 0; i < terms; i++) {
			ValueMetaInterface v;
			v = new ValueMeta(tName + Integer.toString(i+1), ValueMetaInterface.TYPE_NUMBER, 15, 5);
			v.setOrigin(name);
			row.addValueMeta(v);
		}
	}

}
