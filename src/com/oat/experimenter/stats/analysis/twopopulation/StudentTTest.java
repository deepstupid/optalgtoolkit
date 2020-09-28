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

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.inference.TestUtils;

import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;


/**
 * Date: 03/08/2007<br/>
 * <br/>
 * Description: Based on the example from http://commons.apache.org/math/userguide/stat.html
 *  Unpaired, two-sided, two-sample t-test using StatisticalSummary 
 *  instances, without assuming that sub-population variances are equal.
 * <br/>
 * In each case, the test does not assume that the subpopulation variances are equal. 
 * To perform the tests under this assumption, replace "t" at the beginning of 
 * the method name with "homoscedasticT"
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
public class StudentTTest extends StatisticalComparisonTest
{
    /**
     * t = (m1 - m2) / sqrt(var1/n1 + var2/n2)
     */
    protected double tStatistic;
    
    /**
     * observed significance level, or p-value (min significance)
     * The number returned is the smallest significance level at which one can 
     * reject the null hypothesis that the two means are equal in favor of the 
     * two-sided alternative that they are different.
     */
    protected double pValue;
    /**
     * true if the null hypothesis can be rejected with confidence 1 - alpha (95%)
     */
    protected boolean significance05;
   

    @Override
    public boolean supportsNPopulations()
    {
        return false;
    }
    
    @Override
    public String getName()
    {
        return "Independant Student's t-test";
    }

	@Override
	public boolean canRejectNullHypothesis()
	{
		return significance05;
	}

	
	/**
	 * Evaluate the populations according to the Student T-Test
	 * @param s1
	 * @param s2
	 * @throws AnalysisException
	 */
	public void evaluate(DescriptiveStatistics s1, DescriptiveStatistics s2)
		throws AnalysisException
	{
        // calculate the things        
        try
        {
            tStatistic = TestUtils.t(s1, s2);
            pValue = TestUtils.tTest(s1, s2);
            significance05 = TestUtils.tTest(s1, s2, ALPHA_FIVE_PERCENT);
        }
        catch (Exception e)
        {
            throw new AnalysisException("Error calculating statistic: " + e.getMessage(), e);
        }
	}
	
	/**
	 * Evaluate the populations according to the Student T-Test
	 * @param s1
	 * @param s2
	 * @throws AnalysisException
	 */
	public void evaluate(double [] s1, double [] s2)
		throws AnalysisException
	{
		DescriptiveStatistics ds1 = DescriptiveStatistics.newInstance();
        for (int i = 0; i < s1.length; i++)
        {
        	ds1.addValue(s1[i]);
        }
        
		DescriptiveStatistics ds2 = DescriptiveStatistics.newInstance();
        for (int i = 0; i < s2.length; i++)
        {
        	ds2.addValue(s2[i]);
        }
        
        evaluate(ds1, ds2);
	}
	
	@Override
	protected void internalEvaluate(
			RunStatisticSummary s1,
			RunStatisticSummary s2) 
	throws AnalysisException
	{
		evaluate(s1.getDescriptiveStatistics(), s2.getDescriptiveStatistics());
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
        // f
        report.add(new String[]{"T-Statistic", f.format(tStatistic)});        
        
        return report.toArray(new String[report.size()][]);
	}
	
	


	public double getTStatistic()
	{
		return tStatistic;
	}
	@Override
	public double getPValue()
	{
		return pValue;
	}

	public boolean isSignificance05()
	{
		return significance05;
	}
	
	
}
