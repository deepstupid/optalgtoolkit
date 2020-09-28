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
package com.oat.domains.bcr;

import java.util.Arrays;

import com.oat.Algorithm;
import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.Problem;
import com.oat.explorer.domains.bcr.gui.panels.BCRMasterPanel;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.utils.FileUtils;

/**
 * Description: 
 *  
 * Date: 20/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class BCRDomain extends Domain
{
	public final static String ALGORTIHM_LIST_FILENAME = "algorithms.bcr.properties";
	public final static String PROBLEM_LIST_FILE = "problems.bcr.properties";
	
	
	@Override
	public String getHumanReadableName()
	{
		return "Binary Character Recognition";
	}

	@Override
	public String getShortName()
	{
		return "BCR";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		return DomainUtils.defaultLoadAlgorithmList(ALGORTIHM_LIST_FILENAME);
	}

	@Override
	public Problem[] loadProblemList() throws Exception
	{
        String [] classList = FileUtils.loadClassList(PROBLEM_LIST_FILE);
        Problem [] list = new Problem[classList.length];
        
        for (int i = 0; i < classList.length; i++)
        {
            try
            {
                list[i] = new BCRProblem(classList[i]);
            }
            catch (Exception e)
            {
                throw new Exception("Unable to load problem class from properties file: " + classList[i], e);
            }
        }
        Arrays.sort(list);   
        return list;
	}

	@Override
	public MasterPanel getExplorerPanel()
	{
		return new BCRMasterPanel();
	}
}
