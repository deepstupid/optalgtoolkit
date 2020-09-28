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
package com.oat.explorer.domains.bcr.gui.panels;

import java.awt.Frame;

import com.oat.Domain;
import com.oat.explorer.domains.bcr.gui.plots.BCRPlot;
import com.oat.explorer.gui.panels.ProblemPanel;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Type: CharRecProblemPanel<br/>
 * Date: 18/12/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 11/07/2007   JBrownlee   Modified to show the problem instance
 * </pre>
 */
public class BCRProblemPanel extends ProblemPanel
{

    public BCRProblemPanel(Frame parent, Domain domain)
	{
		super(parent, domain);
	}

	@Override
    protected String getProblemBase()
    {
        return null;
    }
    


    @Override
    protected GenericProblemPlot prepareProblemPlot()
    {
        return new BCRPlot(BCRPlot.DRAW_MODE.PROBLEM);
    }
}
