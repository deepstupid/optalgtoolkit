/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.oat.experimenter.stats.analysis.npopulation;

import java.text.DecimalFormat;
import java.util.LinkedList;

import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;

import edu.ucla.stat.SOCR.analyses.data.Data;
import edu.ucla.stat.SOCR.analyses.data.DataType;
import edu.ucla.stat.SOCR.analyses.result.AnovaOneWayResult;


/**
 * Date: 03/08/2007<br/>
 * <br/>
 * Description: One-way ANOVA
 * Based on the example provided in: http://wiki.stat.ucla.edu/socr/index.php/One_Way_ANOVA
 * 
 * Assumes distributions are independent and normal
 * 
 * 
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class ANOVATest extends StatisticalComparisonTest
{
	// model
	protected int dfModel;
	protected double rssModel;
	protected double mssModel;
	// error
	protected int dfError;
	protected double rssError;
	protected double mssError;
	// corrected
	protected int dfCTotal;
	protected double rssTotal;
	
	/**
	 * 
	 */
	protected double fValue;
	
	/**
	 * Probability that the NULL hypothesis is true, that the populations are equal (not different)
	 */
	protected double pValue;
    
    
    
   
    @Override
    public String getName()
    {
        return "One-Way Analysis of Variance (ANOVA)";
    }

	@Override
	public boolean canRejectNullHypothesis()
	{
		// reject the Null hypothesis if the P-value is < 0.05
		// this means that we believe the populations are different 
		// with a confidence of 95%
		return pValue <= ALPHA_FIVE_PERCENT;
	}

	@Override
	protected void internalEvaluate(
			RunStatisticSummary s1,
			RunStatisticSummary s2) throws AnalysisException
	{
		internalEvaluate(new RunStatisticSummary[]{s1, s2});		
	}
	
	
	/**
	 * Calculate the ANOVA statistics 
	 * @param resultsList
	 * @param namesList
	 * @throws AnalysisException
	 */
	public void evaluate(double [][] resultsList, String [] namesList)
		throws AnalysisException
	{
		// validation
		if(resultsList.length != namesList.length)
		{
			throw new AnalysisException("Number of groups of results "+resultsList.length+" and number of groups "+namesList.length+" does not match");
		}
		// names list must be distinct
		for (int i = 0; i < namesList.length; i++)
		{
			for (int j = i+1; j < namesList.length; j++)
			{
				if(namesList[i].equals(namesList[j]))
				{
					throw new AnalysisException("Result names must be distinct: " + namesList[i] + ", " + namesList[j]);
				}
			}
		}
		
        // prepare the groups and results
        // this preparation requires all the data be compressed into a group and result collection
		int totalItems = 0;
		for (int i = 0; i < resultsList.length; i++)
		{
			totalItems += resultsList[i].length;
		}
        double [] results = new double[totalItems];
        String [] groups = new String[totalItems];
        int pos = 0;
        for (int i = 0; i < resultsList.length; i++)
        {            
            for (int j = 0; j < resultsList[i].length; j++)
            {
                // add each data item
                results[pos] = resultsList[i][j];
                groups[pos] = namesList[i];
                pos++;
            }
        }  
        
        
        Data data = new Data();
        data.addPredictor(groups, DataType.FACTOR); // independent variable
        data.addResponse(results, DataType.QUANTITATIVE); // dependent variable
        AnovaOneWayResult result = null;
        try
        {
            result = data.modelAnovaOneWay();
        }
        catch(Exception e)
        {
            throw new AnalysisException("Error performing statisitcal test: " + e.getMessage(), e);
        }
        // validate
        if(result == null)
        {
            throw new AnalysisException("Error performing statisitcal test, unable to prepare - no result calculated.");
        }		
        
        // 
        // NOTE: data extracted and presented as demonstrated in:
        // edu.ucla.stat.SOCR.analyses.gui.AnovaOneWay.class
        // and in the applet on: http://www.socr.ucla.edu/htmls/SOCR_Analyses.html
        
		// model
		dfModel = result.getDFModel();
		rssModel = result.getRSSModel();
		mssModel = result.getMSSModel();		
		// error
		dfError = result.getDFError();
		rssError = result.getRSSError();
		mssError = result.getMSSError();		
		// corrected
		dfCTotal = result.getDFTotal();
		rssTotal = result.getRSSTotal();	
        // p and f values
		fValue = result.getFValue();
        try
        {
            pValue = Double.parseDouble(result.getPValue()); 
        }
        catch(NumberFormatException nfe)
        {
            // < 1E-15 (small number?)
        	pValue = 0;
        }
        
        //
        // Residuals extracted and displayed as demonstrated in the example on:
        // http://wiki.stat.ucla.edu/socr/index.php/One_Way_ANOVA
        //
        
        // COMMENTED out, but left as an example for future if desired
        // LEAVE THIS!
        
        /*        
		double[] residuals = result.getResiduals();
		double[] predicted = result.getPredicted();
		// residuals after being sorted ascendantly.
		double[] sortedResiduals = result.getSortedResiduals();
		// sortedResiduals after being standardized.
		double[] sortedStandardizedResiduals = result.getSortedStandardizedResiduals();
		// the original index of sortedResiduals, stored as integer array.
		int[] sortedResidualsIndex = result.getSortedResidualsIndex();
		// the normal quantiles of sortedResiduals.
		double[] sortedNormalQuantiles = result.getSortedNormalQuantiles();
		// sortedNormalQuantiles after being standardized.
		double[] sortedStandardizedNormalQuantiles = result.getSortedStandardizedNormalQuantiles();
		for (int i = 0; i < residuals.length; i++) {
			System.out.println("residuals["+i+"] = " + residuals[i]);
		}
		*/        
	}
	
	
	/**
	 * Creates R# names for the results
	 * @param resultsList
	 */
	public void evaluate(double [][] resultsList)
		throws AnalysisException
	{
		String [] names = new String[resultsList.length];
		for (int i = 0; i < names.length; i++)
		{
			names[i] = "R"+i;
		}
		evaluate(resultsList, names);
	}

	@Override
	protected void internalEvaluate(RunStatisticSummary[] s)
			throws AnalysisException
	{
		// prepare
		double [][] results = new double [s.length][];
		String [] names = new String[s.length];
		for (int i = 0; i < results.length; i++)
		{
			results[i] = s[i].getRawResults();
			names[i] = s[i].getExperimentalRunName();
		}
		// evaluate
		evaluate(results, names);		
	}
	

	@Override
	public String[][] prepareReport()
	{
    	DecimalFormat f = (DecimalFormat) DecimalFormat.getInstance();
        LinkedList<String[]> report = new LinkedList<String[]>();
        
        report.add(new String[]{"Test", getName()});
        report.add(new String[]{"Null  Hypothesis (H0) Description", nullHypothesisDescription()});        
        report.add(new String[]{"Reject H0 (alpha="+ALPHA_FIVE_PERCENT+")", ""+canRejectNullHypothesis()});
        report.add(new String[]{"P-value", f.format(getPValue())});
        report.add(new String[]{"Populations are Different", ""+canRejectNullHypothesis()});
        // model
        report.add(new String[]{"Model Degrees of Freedom", f.format(dfModel)});
        report.add(new String[]{"Model Residual Sum of Squares", f.format(rssModel)});
        report.add(new String[]{"Model Mean Square Error", f.format(mssModel)});        
        // error
        report.add(new String[]{"Error Degrees of Freedom", f.format(dfError)});
        report.add(new String[]{"Error Residual Sum of Squares", f.format(rssError)});
        report.add(new String[]{"Error Mean Square Error", f.format(mssError)});        
        // corrected
        report.add(new String[]{"Corrected Total Degrees of Freedom", f.format(dfCTotal)});
        report.add(new String[]{"Corrected Total Residual Sum of Squares", f.format(rssTotal)});
        // f and p
        report.add(new String[]{"F-Value", f.format(fValue)});        
        
        return report.toArray(new String[report.size()][]);
	}
	
	



	public int getDfModel()
	{
		return dfModel;
	}

	public double getRssModel()
	{
		return rssModel;
	}

	public double getMssModel()
	{
		return mssModel;
	}

	public int getDfError()
	{
		return dfError;
	}

	public double getRssError()
	{
		return rssError;
	}

	public double getMssError()
	{
		return mssError;
	}

	public int getDfCTotal()
	{
		return dfCTotal;
	}

	public double getRssTotal()
	{
		return rssTotal;
	}

	public double getFValue()
	{
		return fValue;
	}

	@Override
	public double getPValue()
	{
		return pValue;
	}	
}
