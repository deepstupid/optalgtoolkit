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
package com.oat.experimenter.gui.analysis;

import com.oat.experimenter.Reportable;
import com.oat.experimenter.stats.StatisticsReporting;
import com.oat.gui.GenericLogPanel;

/**
 * Description: Report various statistics 
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
public class ReportLogPanel extends GenericLogPanel
{	
	
	public void report(Reportable [] stats)
	{
		for (int i = 0; i < stats.length; i++)
		{
			reportAppend(stats[i]);
			reportAppendLine("");
		}
		scrollToTop();
	}
	
	public void reportAppend(Reportable stats)
	{
		String [][] reportData = stats.prepareReport();
		String data = StatisticsReporting.reportToString(reportData);
		reportAppend(data);		
	}
	
	public void report(Reportable stats)
	{
		String [][] reportData = stats.prepareReport();
		String data = StatisticsReporting.reportToString(reportData);
		report(data);
		scrollToTop();
	}
}
