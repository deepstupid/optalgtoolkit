/*
 Optimization Algorithm Toolkit (OAT)
 http://sourceforge.net/projects/optalgtoolkit
 Copyright (C) 2006  Jason Brownlee

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
package com.oat.experimenter.stats.normality;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;

import umontreal.iro.lecuyer.gof.FDist;
import umontreal.iro.lecuyer.gof.GofStat;
import umontreal.iro.lecuyer.probdist.NormalDist;
import cern.colt.list.DoubleArrayList;

import com.oat.experimenter.stats.AnalysisException;

/**
 * Description: Perform a Anderson-Darling statistical test for normality.
 * Tests the NULL hypothesis that the provided results are drawn from a normal
 * distribution with the specified mean and standard deviation. 
 *  
 * Date: 23/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AndersonDarlingTest extends NormalityTest
{
	/**
	 * Significance (P-Value)
	 */
	protected double pValue;
	/**
	 * Anderson Darling test statistic (A^2)
	 * AN2 = - N - 1/N  SUMj=0N-1{(2j + 1)ln(U(j)) + (2N - 1 - 2j)ln(1 - U(j))}
	 */
	protected double testStatistic;
	
	/**
	 *  A^2* = A^2 * (1+(0.75/n)+(2.25/(n*n)))
	 *  If A^2* exceeds 0.752 then the hypothesis of normality is rejected for a 5% level test.
	 *  http://en.wikipedia.org/wiki/Anderson-Darling_test
	 */
	protected double aSquaredStar;
	public final static double CRITICAL_A2STAR = 0.752;	

	

	@Override
	public boolean canRejectNullHypothesis()
	{
		// reject the Null hypothesis if the P-value is <= 0.05
		// this means that we believe the distributions are different 
		// with a confidence of 95%
		return pValue <= ALPHA_FIVE_PERCENT;
		
		// from http://en.wikipedia.org/wiki/Anderson-Darling_test
		//return aSquaredStar > 0.752;
	}

	@Override
	public String getName()
	{
		return "Anderson-Darling Test";
	}

	@Override
	protected void internalEvaluate(double[] results, double mean, double stdev)
			throws AnalysisException
	{
        // check for invalid sigma
        if(stdev == 0)
        {
        	testStatistic = aSquaredStar = 0;
        	pValue = 0;
        	return;
        }                
        // calculate the CDF for the expected (normal distribution)
        double [] r = new double[results.length];
        for (int i = 0; i < r.length; i++)
        {
            r[i] = NormalDist.cdf(mean, stdev, results[i]);
        }  
        // sort the results
        Arrays.sort(r);
        DoubleArrayList d = new DoubleArrayList(r); 
        // perform the Anderson-Darling test
        int n = d.size();
        testStatistic = GofStat.andersonDarling(d);
        pValue = 1.0 - FDist.andersonDarling(n, testStatistic);
        aSquaredStar = testStatistic * (1+(0.75/n)+(2.25/(n*n)));
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
            
        report.add(new String[]{"Test Statistic (A^2)", f.format(testStatistic)});
        report.add(new String[]{"Test Statistic (A^2*)", f.format(aSquaredStar)});
        report.add(new String[]{"Normal Distribution", ""+isNormal()});
        
        return report.toArray(new String[report.size()][]);
	}
	
	

	@Override
	public double getPValue()
	{
		return pValue;
	}

	public double getTestStatistic()
	{
		return testStatistic;
	}

	public double getASquaredStar()
	{
		return aSquaredStar;
	}	
}
