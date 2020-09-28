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
 * Description: Perform a Kolmogorov-Smirnov statistical test for normality.
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
public class KolmogorovSmirnovTest extends NormalityTest
{
	/**
	 * DN 	= 	max (DN+, DN-).
	 */
	public final static int INDEX_KS_STATISTIC = 2;
	
	/**
	 * Kolmogorov-Smirnov (KS) test statistics DN+, DN-, and DN. 
	 * It is defined by double[3] as follows
	 * DN+ 	= 	max0 <= j <= N-1((j + 1)/N - U(j)), 	 
	 * DN- 	= 	max0 <= j <= N-1(U(j) - j/N), 	 
	 * DN 	= 	max (DN+, DN-).
	 */
	protected double [] testStatistics;
	/**
	 * Significance (P-Value)
	 */
	protected double pValue;
	

	@Override
	public boolean canRejectNullHypothesis()
	{
		// reject the Null hypothesis if the P-value is <= 0.05
		// this means that we believe the distributions are different 
		// with a confidence of 95%
		return pValue <= ALPHA_FIVE_PERCENT;
	}

	@Override
	public String getName()
	{
		return "Kolmogorov-Smirnov Test";
	}

	@Override
	protected void internalEvaluate(double [] results, double mean, double stdev)
			throws AnalysisException
	{
        // check for invalid sigma
        if(stdev == 0)
        {
        	testStatistics = null;
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
        // Perform the test
        testStatistics  = GofStat.kolmogorovSmirnov(d);
        // calculate the P-Value
        pValue = 1.0 - FDist.kolmogorovSmirnov(d.size(), testStatistics[INDEX_KS_STATISTIC]);
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
        
        if(testStatistics!=null)
        {
	        report.add(new String[]{"DN+ = max0<=j<=N-1((j+1)/N-U(j))", f.format(testStatistics[0])});
	        report.add(new String[]{"DN- = max0<=j<=N-1(U(j)-j/N)", f.format(testStatistics[1])});
	        report.add(new String[]{"DN  = max(DN+,DN-) (Two-sided Kolmogorov-Smirnov statistic)", f.format(testStatistics[2])});
        }
        report.add(new String[]{"Normal Distribution", ""+isNormal()});
        
        return report.toArray(new String[report.size()][]);
	}
	
	public double getKSStatistic()
	{
		return testStatistics[INDEX_KS_STATISTIC];
	}
	
	

	

	public double[] getTestStatistics()
	{
		return testStatistics;
	}
	
	@Override
	public double getPValue()
	{
		return pValue;
	}
}
