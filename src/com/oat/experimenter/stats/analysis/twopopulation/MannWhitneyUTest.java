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
package com.oat.experimenter.stats.analysis.twopopulation;

import java.text.DecimalFormat;
import java.util.LinkedList;

import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;

import edu.ucla.stat.SOCR.analyses.data.Data;
import edu.ucla.stat.SOCR.analyses.data.DataType;
import edu.ucla.stat.SOCR.analyses.model.AnalysisType;
import edu.ucla.stat.SOCR.analyses.result.TwoIndependentWilcoxonResult;

/**
 * Date: 03/08/2007<br/>
 * <br/>
 * Description: Called the Mann-Whitney U Test or the Wilcoxon Rank Sum Test
 * Based on the example: http://wiki.stat.ucla.edu/socr/index.php/Two_Independent_Sample_Wilcoxon_Test
 * Also see: http://en.wikipedia.org/wiki/Mann-Whitney-Wilcoxon_test 
 * 
 * When your data are not normally-distributed, or are ranks or scores
 * Compare 2 sets of data from the same subjects under different circumstances
 * or 
 * Compare one set of data to a hypothetical value
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
public class MannWhitneyUTest extends StatisticalComparisonTest
{
	/**
	 * Probability that the populations are equal, that
	 * the NULL hypothesis is correct
	 */
	protected double pValue;
	/**
	 * Z-Score
	 */
	protected double zScore;
	/**
	 * Variance of Test Statistics (VAR_U)
	 */
	protected double statisticVariance;
	/**
	 * Expectation of Test Statistics (MEAN_U)
	 */
	protected double statisticExpectation;
	/**
	 * Formula Used for the Expectation of the Test Statistics (U_STAT_EXPECTATION_FORMULA)
	 */
	protected String expectationFormula;
	
	/**
	 * Formula Used for the Variance of the Test Statistics (U_STAT_VARIANCE_FORMULA)
	 */
	protected String varianceFormula;
	
	
	
    @Override
	public boolean canRejectNullHypothesis()
	{
		// reject the Null hypothesis if the P-value is < 0.05
		// this means that we believe the populations are different 
		// with a confidence of 95%
		return pValue <= ALPHA_FIVE_PERCENT;
	}

    
    /**
     * Calculate the statistics
     * @param s1
     * @param n1
     * @param s2
     * @param n2
     * @throws AnalysisException
     */
    public void evaluate(double [] s1, String n1, double [] s2, String n2)
    	throws AnalysisException
    {
    	// must have different names
    	if(n1.equals(n2))
    	{
    		throw new AnalysisException("Runs must have different names");
    	}    	
    	
		// prepare data
        Data data = new Data(); 
        data.appendX(n1, s1, DataType.QUANTITATIVE);
        data.appendY(n2, s2, DataType.QUANTITATIVE);
        // execute test
        TwoIndependentWilcoxonResult result = null;
        try
        {
        	result = (TwoIndependentWilcoxonResult) data.getAnalysis(AnalysisType.TWO_INDEPENDENT_WILCOXON);
        }
        catch(Exception e)
        {
            throw new AnalysisException("Error performing statisitcal test: " + e.getMessage(), e);
        }
        // validate
        if(result == null)
        {
            throw new AnalysisException("Error performing statisitcal test: Unable to prepare and no result calculated.");
        }        
        // the sample is larger than 10
        // has to do with what we can do with this thing
        boolean isLargeSample = result.isLargeSample();        
        // must have a large sample, otherwise we get nothing, no point in doing anything
        if(!isLargeSample)
        {
        	throw new AnalysisException("Unable to execute statistical test, small sample size: < 10");
        }
        
        //
        // Group Information
        //
        
        //
        // Don't really want this information, leave this commented code as a future example
        //
        
        /*
        // first statistic mean
        double meanX = result.getMeanX();
        // second statistic mean
        double meanY = result.getMeanY();              
       
        double rankSumSmall = result.getRankSumSmallerGroup();
        double rankSumLarge = result.getRankSumLargerGroup();
        
        double uStatSmall = result.getUStatSmallerGroup();
        double uStatLarge = result.getUStatLargerGroup();
        
        String summary1 = result.getDataSummaryOfGroup1();
        String summary2 = result.getDataSummaryOfGroup2();

        String nameSmall = result.getGroupNameSmall();
        String nameLarge = result.getGroupNameLarge();
        
        String pValueOneSided = result.getPValueOneSided();
        */
        
        // 
        // Test Information
        //
        
        // Expectation of Test Statistics (MEAN_U)
        statisticExpectation = result.getMeanU();
        // Variance of Test Statistics (VAR_U)
        statisticVariance = result.getVarianceU();
        // Z-Score
        zScore = result.getZScore();
        // P-value that populations are equal
        String pValueTwoSided = result.getPValueTwoSided();
        try
        {
        	pValue = Double.parseDouble(pValueTwoSided);
        }
        catch(NumberFormatException e)
        {
        	// I believe it means the pValue is too small to calculate, assume zero
        	pValue = 0;
        }
        
        // U_STAT_EXPECTATION_FORMULA
        expectationFormula = result.getUMeanFormula();
        // U_STAT_VARIANCE_FORMULA
        varianceFormula = result.getUVarianceFormula();
    }
    
    
    /**
     * Creates fake names R#
     * @param s1
     * @param s2
     */
    public void evaluate(double [] s1, double [] s2)
		throws AnalysisException
	{
    	evaluate(s1, "R1", s2, "R2");
	}
    
	@Override
	protected void internalEvaluate(
			RunStatisticSummary s1,
			RunStatisticSummary s2) 
	throws AnalysisException
	{
		evaluate(s1.getRawResults(), s1.getExperimentalRunName(), s2.getRawResults(), s2.getExperimentalRunName());
	}
		

	@Override
	protected void internalEvaluate(RunStatisticSummary[] s)
			throws AnalysisException
	{        
        throw new UnsupportedOperationException("Does not support n-populations");
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
        // general
        report.add(new String[]{"Normal approximation is used for either of the sample sizes > 10.", ""});
        report.add(new String[]{"Formula Used for the Expectation of the Test Statistics", expectationFormula});        
        report.add(new String[]{"Formula Used for the Variance of the Test Statistics", varianceFormula});
        // test results
        report.add(new String[]{"Expectation of Test Statistics (MEAN_U)", ""+f.format(statisticExpectation)});
        report.add(new String[]{"Variance of Test Statistics (VAR_U)", ""+f.format(statisticVariance)});
        report.add(new String[]{"Z-Score", ""+f.format(zScore)});
        
        return report.toArray(new String[report.size()][]);
	}

	@Override
    public String getName()
    {
        return "Mann-Whitney U Test";
    }
	

    
    @Override
    public boolean supportsNPopulations()
    {
        return false;
    }

    @Override
	public double getPValue()
	{
		return pValue;
	}

	public double getZScore()
	{
		return zScore;
	}

	public double getStatisticVariance()
	{
		return statisticVariance;
	}

	public double getStatisticExpectation()
	{
		return statisticExpectation;
	}

	public String getExpectationFormula()
	{
		return expectationFormula;
	}

	public String getVarianceFormula()
	{
		return varianceFormula;
	}
    
    
}
