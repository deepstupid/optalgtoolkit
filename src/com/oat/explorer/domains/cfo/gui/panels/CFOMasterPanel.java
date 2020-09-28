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
package com.oat.explorer.domains.cfo.gui.panels;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oat.Domain;
import com.oat.domains.cfo.CFODomain;
import com.oat.explorer.domains.cfo.gui.plots.CFOPlot;
import com.oat.explorer.gui.panels.AlgorithmPanel;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.panels.ProblemPanel;

/**
 * Type: FuncOptMasterPanel<br/>
 * Date: 24/11/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 10/07/2007   JBrownlee   Modified to use a standardised plot panel
 * </pre>
 */
public class CFOMasterPanel extends MasterPanel
{
    protected CFOPlot plot;
    
    @Override
    public String getPanelName()
    {
        return "Continuous";
    }

    @Override
    protected JPanel[] prepareAdditionalCentralPanels()
    {
        plot = new CFOPlot();
        return new JPanel[]{plot};
    }

    @Override
    protected void prepareAdditionalListeners()
    {        
        controlPanel.registerClearableListener(plot);
        problemPanel.registerProblemChangedListener(plot);
        problemPanel.registerNewSolutionListener(plot);
    }

    @Override
    protected AlgorithmPanel prepareAlgorithmPanel()
    {
        return new CFOAlgorithmPanel((JFrame)getParent(), domain);
    }

    @Override
    protected ProblemPanel prepareProblemPanel()
    {
        return new CFOProblemPanel((Frame)getParent(), domain);
    }

	@Override
	public Domain getDomain()
	{
		return new CFODomain();
	}
}
