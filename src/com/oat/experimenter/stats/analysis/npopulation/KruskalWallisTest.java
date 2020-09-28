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
import edu.ucla.stat.SOCR.analyses.model.AnalysisType;
import edu.ucla.stat.SOCR.analyses.result.TwoIndependentKruskalWallisResult;

/**
 * Type: KruskalWallisTest<br/>
 * Date: 06/08/2007<br/>
 * <br/>
 * Description: Kruskal-Wallis one-way analysis of variance
 * Based on example provided in http://wiki.stat.ucla.edu/socr/index.php/Multiple_Independent_Sample_Kruskal_Wallis_Test
 * Information provided in: http://en.wikipedia.org/wiki/Kruskal-Wallis_test
 * <br/>
 * Compare 2 or more sets of data when your data are not normally-distributed, or are ranks or scores
 * A non-parametric method for testing equality of population medians among groups.
 * Intuitively, it is identical to a one-way analysis of variance with the data replaced by their ranks. 
 * It is an extension of the Mann-Whitney U test to 2 or more groups
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
public class KruskalWallisTest extends StatisticalComparisonTest
{	
	/**
	 * fixed alpha (p-value cutoff) from: edu.ucla.stat.SOCR.analyses.model.TwoIndependentKruskalWallis.alpha 
	 * (private instance variable)
	 */
	public final static double FIXED_ALPHA = 0.050000000000000003D;
	
    /**
     * Degrees of Freedom
     */
    protected double degreesOfFreedom;
    /**
     * Test statistic (T-Statistic)
     * The result of the statistical test that may be compared to the critical value
     */
    protected double tStatistic;
    /**
     * S^2 (S Squared)
     * Used in the calculation of the test statistic
     */
    protected double sSquared;
    /**
     * Critical Value
     * 
     * In statistics, a critical value is the value corresponding to a given significance level. 
     * This cutoff value determines the boundary between those samples resulting in a test statistic 
     * that leads to rejecting the null hypothesis and those lead to a decision not to reject 
     * the null hypothesis. If the absolute value of the calculated value from the statistical 
     * test is greater than the critical value, then the null hypothesis is rejected and the 
     * alternative hypothesis is accepted, and vice versa.
     * From: http://en.wikipedia.org/wiki/Critical_value
     */
    protected double criticalValue;    
   
    public final static String COMPARISON_KEY = "Notation: Ri -- Rank of group i; ni -- size of group i.";
	protected String groupComparisonHeader;
	protected String [] groupComparisonInfo;
       

    @Override
    public String getName()
    {
        return "Kruskal-Wallis Test";
    }

  
	@Override
	protected void internalEvaluate(
			RunStatisticSummary s1,
			RunStatisticSummary s2)
		throws AnalysisException
	{
		internalEvaluate(new RunStatisticSummary[]{s1, s2});
	}

	/**
	 * Calculate the statistics
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
		
		Data data = new Data();        
        // append all data
        for (int i = 0; i < resultsList.length; i++)
        {
            data.appendX(namesList[i], resultsList[i], DataType.QUANTITATIVE);
        }
        // do the analysis
        TwoIndependentKruskalWallisResult result = null;
        try
        {
            result = (TwoIndependentKruskalWallisResult) data.getAnalysis(AnalysisType.TWO_INDEPENDENT_KRUSKAL_WALLIS);        
        }
        catch(Exception e)
        {
            throw new AnalysisException("Error performing statisitcal test: " + e.getMessage(), e);
        }        
        // ensure we have a result
        if(result == null)
        {
            throw new AnalysisException("Error performing statisitcal test, Unable to prepare - no result calculated.");
        }                
        // extract information
        degreesOfFreedom = Double.parseDouble(result.getDegreesOfFreedom());
        tStatistic = Double.parseDouble(result.getTStat());
        sSquared = Double.parseDouble(result.getSSqaured());
        criticalValue = Double.parseDouble(result.getCriticalValue());             

        // only do pair-wise sample comparisons if the NULL hypothesis can be rejected
        // The columns demonstrate the differences between the populations
        // if the first number is bigger than the second, then the populations are different in the comparison
        if(canRejectNullHypothesis())
        {
        	// collect group information
        	groupComparisonInfo = result.getMultipleComparisonInformation();
        	groupComparisonHeader = result.getMultipleComparisonHeader();					
        }
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
		double [][] results = new double[s.length][];
		String [] names = new String[s.length];
		
		for (int i = 0; i < names.length; i++)
		{
			results[i] = s[i].getRawResults();
			names[i] = s[i].getExperimentalRunName();
		}
		
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
        
        report.add(new String[]{"Degrees of Freedom", f.format(degreesOfFreedom)});
        report.add(new String[]{"Critical Value", f.format(criticalValue)});
        report.add(new String[]{"T-Statistic", f.format(tStatistic)});
        report.add(new String[]{"S * S", f.format(sSquared)});
        
        
        // other things
        if(canRejectNullHypothesis())
        {
        	// can only do group comparisons if the NULL hypothesis is rejected
        	
        	report.add(new String[]{"Pairwise Comparison", "If the first number is bigger than the second, then the populations are different in the comparison"});
        	report.add(new String[]{"Key", COMPARISON_KEY});
        	report.add(new String[]{"", groupComparisonHeader});
        	for (int i = 0; i < groupComparisonInfo.length; i++)
			{
        		report.add(new String[]{"", groupComparisonInfo[i]});
			}
        }
        
        return report.toArray(new String[report.size()][]);
	}
	

	
	@Override
	public double getPValue()
	{
		// As of 24th August 2007, Email Response from SOCR group:
		// ----
		// We don't have it currently. It's a good idea to include it 
		// so perhaps we'll include them in the near future.
		
		// TODO implement this myself
		return Double.NaN;
	}

	
	@Override
	public boolean canRejectNullHypothesis()
	{		
		// we reject the NULL hypothesis if the statistic value is greater than the critical value
		// according to 'practical nonparametric statistics' page 232
		// this example also provides the basis for the library implementation of the test
		return tStatistic > criticalValue;
	}


	public double getDegreesOfFreedom()
	{
		return degreesOfFreedom;
	}


	public double getTStatistic()
	{
		return tStatistic;
	}


	public double getSSquared()
	{
		return sSquared;
	}


	public double getCriticalValue()
	{
		return criticalValue;
	}
}
