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
 * Description: Perform a Cramér-von Mises statistical test for normality.
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
public class CramerVonMisesCriterion extends NormalityTest
{
	/**
	 * Significance (P-Value)
	 */
	protected double pValue;
	/**
	 * Cramér-von-Mises test statistic
	 * WN^2 = 1/(12N) + SUMj=0N-1(U(j) - (j + 0.5)/N)2,
	 */
	protected double testStatistic;

	/**
	 * Z=W (1.0 + 0.5/n)
	 * http://pbil.univ-lyon1.fr/library/nortest/html/cvm.test.html
	 */
	protected double zScore;
	
	
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
		return "Cramér-von-Mises Criterion";
	}

	@Override
	protected void internalEvaluate(double[] results, double mean, double stdev)
			throws AnalysisException
	{
        // check for invalid sigma
        if(stdev == 0)
        {
        	testStatistic = zScore = 0;
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
        int n = d.size();
        // Perform the test
        testStatistic  = GofStat.cramerVonMises(d);
        pValue = 1.0 - FDist.cramerVonMises(n, testStatistic);
        // Z=W (1.0 + 0.5/n)
        zScore = testStatistic * (1.0 + (0.5/n)); 
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
                
        report.add(new String[]{"Test Statistic (W^2)", f.format(testStatistic)});
        report.add(new String[]{"Z Score", f.format(zScore)});
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

	public double getZScore()
	{
		return zScore;
	}
	
	
}
