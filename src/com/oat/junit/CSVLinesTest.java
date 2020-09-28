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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.oat.utils.FileUtils;

/**
 * 
 * Description: 
 *  
 * Date: 07/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class CSVLinesTest
{
	
	@Test
	public void testCSVLineNormal()
	{
		String test1 = "a,b,c,d,e,f,g";
		String [] parts1 = FileUtils.parseCSVLine(test1);
		assertEquals(7, parts1.length);
	}
	
	@Test
	public void testCSVLineSomeQuoats()
	{
		String test1 = "a,\"ba\",c,d,e,f,g";
		String [] parts1 = FileUtils.parseCSVLine(test1);
		assertEquals(7, parts1.length);
	}
	
	@Test
	public void testCSVLineSomeQuoatsAndCommas()
	{
		String test1 = "a,\"b,ipoipo,opipoipoia\",c,d,e,f,g";
		String [] parts1 = FileUtils.parseCSVLine(test1);
		assertEquals(7, parts1.length);
	}
	
	@Test
	public void testCSVLineSomeQuoatsAndCommasAndSpaces()
	{
		String test1 = "a,\"b,ipoipo,opipoi, poia\",c,d,e,f,g";
		String [] parts1 = FileUtils.parseCSVLine(test1);
		assertEquals(7, parts1.length);
	}
	
	@Test
	public void testCSVLineAllQuoatsAndCommasAndSpaces()
	{
		String test1 = "\"1,2,3,4,5,6\",\"1,2,3,4,5,6\",\"1,2,3,4,5,6\",\"1,2,3,4,5,6\",\"1,2,3,4,5,6\",\"1,2,3,4,5,6\",\"1,2,3,4,5,6\"";
		String [] parts1 = FileUtils.parseCSVLine(test1);
		assertEquals(7, parts1.length);
	}
}
