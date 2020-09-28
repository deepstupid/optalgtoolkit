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
package com.oat.experimenter.examples;

import javax.swing.JFrame;

import com.oat.experimenter.gui.plots.BoxPlot;
import com.oat.junit.stats.StatsTestUtils;
import com.oat.utils.GUIUtils;

/**
 * 
 * Description: Example of using the boxplot 
 *  
 * Date: 28/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExampleBoxPlot
{
	/**
	 * Test
	 * 
	 * @param args
	 */
    public static void main(String[] args)
	{		
		try
		{
			BoxPlot plot = new BoxPlot();
			plot.setChartLabel("Demonstration Box Plot");
			plot.setXAxisLabel("Algorithms");
			plot.setYAxisLabel("Solution Quality");
			
			// results
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomGaussian(), "Algorithm1", "Problem1");
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomUniform(), "Algorithm1", "Problem2");
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomUniform(), "Algorithm1", "Problem3");
			
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomGaussian(), "Algorithm2", "Problem1");
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomUniform(), "Algorithm2", "Problem2");
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomGaussian(), "Algorithm2", "Problem3");			
			
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomGaussian(), "Algorithm3", "Problem1");
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomUniform(), "Algorithm3", "Problem2");
			plot.addBoxAndWhiskerItem(StatsTestUtils.generateRandomUniform(), "Algorithm3", "Problem3");
			

			JFrame frame = new JFrame("Box Plot Test");
			frame.setSize(640, 480);
			frame.add(plot);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			GUIUtils.centerComponent(frame);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
}
