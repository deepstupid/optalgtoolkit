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

import java.util.Random;

/**
 * Description: Basic test utils 
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
public class StatsTestUtils
{
	public final static int SAMPLE_SIZE = 500;
	
	public static double [] generateRandomGaussian()
	{
		Random rand = new Random(1);
		double [] d = new double[SAMPLE_SIZE];
		for (int i = 0; i < d.length; i++)
		{
			d[i] = rand.nextGaussian();
		}		
		return d;
	}
	
	public static double [] generateRandomGaussianOffset()
	{
		Random rand = new Random(1);
		double [] d = new double[SAMPLE_SIZE];
		for (int i = 0; i < d.length; i++)
		{
			d[i] = rand.nextGaussian() + 100;
		}		
		return d;
	}
	
	public static double [] generateRandomUniform()
	{
		Random rand = new Random(1);
		double [] d = new double[SAMPLE_SIZE];
		for (int i = 0; i < d.length; i++)
		{
			d[i] = rand.nextDouble();
		}		
		return d;
	}
	
	public static double [] generateRandomUniformOffset()
	{
		Random rand = new Random(1);
		double [] d = new double[SAMPLE_SIZE];
		for (int i = 0; i < d.length; i++)
		{
			d[i] = rand.nextDouble() + 100;
		}		
		return d;
	}
}
