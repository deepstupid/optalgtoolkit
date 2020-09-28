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
package com.oat.explorer.domains.bfo.gui.panels;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oat.Domain;
import com.oat.domains.bfo.BFODomain;
import com.oat.explorer.domains.bfo.gui.plots.StringPlot;
import com.oat.explorer.gui.panels.AlgorithmPanel;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.panels.ProblemPanel;

/**
 * Type: BinaryMasterPanel<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 11/07/2007   JBrownlee   Modified the plot to show the best evaluated solution, rather
 *                          than the best epoch solution
 * </pre>
 */
public class BFOMasterPanel extends MasterPanel
{    
    protected StringPlot runBest;
    

    @Override
	public Domain getDomain()
	{
    	return new BFODomain();
	}

	@Override
    public String getPanelName()
    {
        return "Binary";
    }

    @Override
    protected JPanel[] prepareAdditionalCentralPanels()
    {
        runBest = new StringPlot();        
        return new JPanel[]{runBest};
    }

    @Override
    protected void prepareAdditionalListeners()
    {
        // listeners for run best
        controlPanel.registerClearableListener(runBest);
        problemPanel.registerProblemChangedListener(runBest);
        //algorithmPanel.registerAlgorithmIterationCompleteListener(runBest);
        problemPanel.registerNewSolutionListener(runBest);
    }

    @Override
    protected AlgorithmPanel prepareAlgorithmPanel()
    {
        return new BFOAlgorithmPanel((JFrame)getParent(), domain);
    }

    @Override
    protected ProblemPanel prepareProblemPanel()
    {
        return new BFOProblemPanel((JFrame)getParent(), domain);
    }

}
