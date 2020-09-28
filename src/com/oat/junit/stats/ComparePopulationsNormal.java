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
package com.oat.junit.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.oat.experimenter.stats.StatisticsReporting;
import com.oat.experimenter.stats.analysis.npopulation.ANOVATest;
import com.oat.experimenter.stats.analysis.twopopulation.StudentTTest;

/**
 * Description: Basic tests for comparing normal populations
 *  
 * Date: 27/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ComparePopulationsNormal
{
	@Test
	public void testStudentTTestSamePopulations()
	{
		StudentTTest testSame = new StudentTTest();
		try
		{
			testSame.evaluate(StatsTestUtils.generateRandomGaussian(),  StatsTestUtils.generateRandomGaussian());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testSame.prepareReport()));
		assertEquals(false, testSame.canRejectNullHypothesis());
		assertEquals(false, testSame.isPopulationsDifferent());
		assertEquals(1.0, testSame.getPValue());
	}
	
	@Test
	public void testStudentTTestDifferentPopulations()
	{
		StudentTTest testDifferent = new StudentTTest();
		try
		{
			testDifferent.evaluate(StatsTestUtils.generateRandomGaussian(),  StatsTestUtils.generateRandomGaussianOffset());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testDifferent.prepareReport()));
		assertEquals(true, testDifferent.canRejectNullHypothesis());
		assertEquals(true, testDifferent.isPopulationsDifferent());
		assertEquals(0.0, testDifferent.getPValue());
	}
	
	
	@Test
	public void testANOVASamePopulations()
	{
		ANOVATest testSame = new ANOVATest();
		
		double [][] pops = new double[][]{StatsTestUtils.generateRandomGaussian(), StatsTestUtils.generateRandomGaussian(), StatsTestUtils.generateRandomGaussian()}; 
		try
		{
			testSame.evaluate(pops);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testSame.prepareReport()));
		assertEquals(false, testSame.canRejectNullHypothesis());
		assertEquals(false, testSame.isPopulationsDifferent());
		assertEquals(1.0, testSame.getPValue());
	}
	
	
	@Test
	public void testANOVADifferentPopulations()
	{
		ANOVATest testDifferent = new ANOVATest();
		double [][] pops = new double[][]{StatsTestUtils.generateRandomGaussian(), StatsTestUtils.generateRandomGaussianOffset(), StatsTestUtils.generateRandomGaussian()};
		try
		{
			testDifferent.evaluate(pops);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testDifferent.prepareReport()));
		assertEquals(true, testDifferent.canRejectNullHypothesis());
		assertEquals(true, testDifferent.isPopulationsDifferent());
		assertEquals(0.0, testDifferent.getPValue());
	}	

}
