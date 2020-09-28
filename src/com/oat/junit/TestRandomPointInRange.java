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
package com.oat.junit;

import java.util.Random;
import static org.junit.Assert.fail;
import org.junit.Test;

import com.oat.utils.AlgorithmUtils;
import com.oat.utils.RandomUtils;

/**
 * 
 * Description: 
 *  
 * Date: 30/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class TestRandomPointInRange
{
	@Test
	public void testNegatives()
	{
		double [][] minmax = new double[][]{{-3.32E-9, -4.48E-10}};
		
		if(minmax[0][0] > minmax[0][1])
		{
			fail("invalid max and min");
		}
		
		Random r = new Random(1);
		int numPoints = 100;
		
		for (int i = 0; i < numPoints; i++)
		{
			double [] v = RandomUtils.randomPointInRange(r, minmax);
			
			for (int j = 0; j < v.length; j++)
			{
				if(!AlgorithmUtils.inBounds(v[j], minmax[j][0], minmax[j][1]))
				{
					fail("out of bounds index=" + j + ", value="+v[j]);
				}
			}			
		}		
		
	}
}
