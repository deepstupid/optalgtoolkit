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
import com.oat.experimenter.stats.normality.AndersonDarlingTest;
import com.oat.experimenter.stats.normality.CramerVonMisesCriterion;
import com.oat.experimenter.stats.normality.KolmogorovSmirnovTest;


/**
 * 
 * Description: Basic tests for normality functions
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
public class NormalityTests
{
	
	@Test
	public void testAndersonDarlingTestNormal()
	{
		AndersonDarlingTest testNormal = new AndersonDarlingTest();
		// test normal
		try
		{
			testNormal.evaluate(StatsTestUtils.generateRandomGaussian());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testNormal.prepareReport()));			
		// normal
		assertEquals(false, testNormal.canRejectNullHypothesis());
		assertEquals(true, testNormal.isNormal());		
	}	
	
	@Test
	public void testAndersonDarlingTestNotNormal()
	{		
		// test not normal
		AndersonDarlingTest testNotNormal = new AndersonDarlingTest();
		// test normal
		try
		{
			testNotNormal.evaluate(StatsTestUtils.generateRandomUniform());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testNotNormal.prepareReport()));
		// not normal		
		assertEquals(true, testNotNormal.canRejectNullHypothesis());
		assertEquals(false, testNotNormal.isNormal());
	}
	
	
	@Test
	public void testCramerVonMisesCriterionNormal()
	{
		CramerVonMisesCriterion testNormal = new CramerVonMisesCriterion();
		// test normal
		try
		{
			testNormal.evaluate(StatsTestUtils.generateRandomGaussian());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testNormal.prepareReport()));			
		// normal
		assertEquals(false, testNormal.canRejectNullHypothesis());
		assertEquals(true, testNormal.isNormal());		
	}
	
	
	@Test
	public void testCramerVonMisesCriterionNotNormal()
	{		
		// test not normal
		CramerVonMisesCriterion testNotNormal = new CramerVonMisesCriterion();
		// test normal
		try
		{
			testNotNormal.evaluate(StatsTestUtils.generateRandomUniform());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testNotNormal.prepareReport()));
		// not normal		
		assertEquals(true, testNotNormal.canRejectNullHypothesis());
		assertEquals(false, testNotNormal.isNormal());
	}
	
	
	
	@Test
	public void testKolmogorovSmirnovTestNormal()
	{
		KolmogorovSmirnovTest testNormal = new KolmogorovSmirnovTest();
		// test normal
		try
		{
			testNormal.evaluate(StatsTestUtils.generateRandomGaussian());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testNormal.prepareReport()));			
		// normal
		assertEquals(false, testNormal.canRejectNullHypothesis());
		assertEquals(true, testNormal.isNormal());		
	}
	
	
	@Test
	public void testKolmogorovSmirnovTestNotNormal()
	{		
		// test not normal
		KolmogorovSmirnovTest testNotNormal = new KolmogorovSmirnovTest();
		// test normal
		try
		{
			testNotNormal.evaluate(StatsTestUtils.generateRandomUniform());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testNotNormal.prepareReport()));
		// not normal		
		assertEquals(true, testNotNormal.canRejectNullHypothesis());
		assertEquals(false, testNotNormal.isNormal());
	}

}
