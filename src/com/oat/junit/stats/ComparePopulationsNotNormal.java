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
import com.oat.experimenter.stats.analysis.npopulation.KruskalWallisTest;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;

/**
 * Description: Basic tests for comparing non-normal populations
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
public class ComparePopulationsNotNormal
{
	@Test
	public void testMannWhitneyUSamePopulations()
	{
		MannWhitneyUTest testSame = new MannWhitneyUTest();
		try
		{
			testSame.evaluate(StatsTestUtils.generateRandomUniform(),  StatsTestUtils.generateRandomUniform());		
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
	public void testMannWhitneyUDifferentPopulations1()
	{
		MannWhitneyUTest testSame = new MannWhitneyUTest();
		try
		{
			testSame.evaluate(StatsTestUtils.generateRandomUniform(),  StatsTestUtils.generateRandomGaussian());		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testSame.prepareReport()));
		assertEquals(true, testSame.canRejectNullHypothesis());
		assertEquals(true, testSame.isPopulationsDifferent());		
	}
	
	@Test
	public void testMannWhitneyUDifferentPopulations2()
	{
		MannWhitneyUTest testSame = new MannWhitneyUTest();
		try
		{
			testSame.evaluate(StatsTestUtils.generateRandomUniform(),  StatsTestUtils.generateRandomUniformOffset());		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error during test: " + e.getMessage());			
		}
		System.out.println(StatisticsReporting.reportToString(testSame.prepareReport()));
		assertEquals(true, testSame.canRejectNullHypothesis());
		assertEquals(true, testSame.isPopulationsDifferent());		
	}
	
	@Test
	public void testKruskalWallisSamePopulations()
	{
		KruskalWallisTest testSame = new KruskalWallisTest();		
		double [][] pops = new double[][]{StatsTestUtils.generateRandomUniform(),StatsTestUtils.generateRandomUniform(),StatsTestUtils.generateRandomUniform()};		
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
	}
	
	
	@Test
	public void testKruskalWallisDifferentPopulations1()
	{
		KruskalWallisTest testSame = new KruskalWallisTest();		
		double [][] pops = new double[][]{StatsTestUtils.generateRandomUniform(),StatsTestUtils.generateRandomUniformOffset(),StatsTestUtils.generateRandomUniform()};		
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
		assertEquals(true, testSame.canRejectNullHypothesis());
		assertEquals(true, testSame.isPopulationsDifferent());		
	}
	
	@Test
	public void testKruskalWallisDifferentPopulations2()
	{
		KruskalWallisTest testSame = new KruskalWallisTest();		
		double [][] pops = new double[][]{StatsTestUtils.generateRandomUniform(),StatsTestUtils.generateRandomGaussian(),StatsTestUtils.generateRandomUniform()};		
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
		assertEquals(true, testSame.canRejectNullHypothesis());
		assertEquals(true, testSame.isPopulationsDifferent());		
	}
}
