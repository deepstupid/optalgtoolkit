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
package com.oat.explorer.domains.psp.gui.panels;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oat.Domain;
import com.oat.domains.psp.PSPDomain;
import com.oat.explorer.domains.psp.gui.plots.HPModelPanel;
import com.oat.explorer.gui.panels.AlgorithmPanel;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.explorer.gui.panels.ProblemPanel;

/**
 * Type: ProtFoldMasterPanel<br/>
 * Date: 24/11/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 07/07/2007   JBrownlee   Modified name of panel            
 * </pre> 
 */
public class PSPMasterPanel extends MasterPanel
{
    protected HPModelPanel plotPanel;
    
    

    @Override
    public String getPanelName()
    {
        return "Protein Folding";
    }

    @Override
    protected JPanel[] prepareAdditionalCentralPanels()
    {
        plotPanel = new HPModelPanel();

        return new JPanel[]{plotPanel};
    }

    @Override
    protected void prepareAdditionalListeners()
    {
        // prepare plot panel
        problemPanel.registerProblemChangedListener(plotPanel);
        algorithmPanel.registerAlgorithmIterationCompleteListener(plotPanel);
        controlPanel.registerClearableListener(plotPanel);
    }

    @Override
    protected AlgorithmPanel prepareAlgorithmPanel()
    {
        return new PSPAlgorithmPanel((JFrame)getParent(), domain);
    }

    @Override
    protected ProblemPanel prepareProblemPanel()
    {
        return new PSPProblemPanel((Frame)getParent(), domain);
    }

	@Override
	public Domain getDomain()
	{
		return new PSPDomain();
	}

}
