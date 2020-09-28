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
package com.oat.explorer.gui.entry;

import java.awt.BorderLayout;

import javax.swing.JApplet;
import javax.swing.JTabbedPane;

import com.oat.explorer.domains.bcr.gui.panels.BCRMasterPanel;
import com.oat.explorer.domains.bfo.gui.panels.BFOMasterPanel;
import com.oat.explorer.domains.cfo.gui.panels.CFOMasterPanel;
import com.oat.explorer.domains.gcp.gui.panels.GCPMasterPanel;
import com.oat.explorer.domains.psp.gui.panels.PSPMasterPanel;
import com.oat.explorer.domains.tsp.gui.panels.TSPMasterPanel;

/**
 * Type: MainApplet<br/>
 * Date: 21/11/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExplorerApplet extends JApplet
{    	
    @Override
    public void init()
    {    	
        prepareGui();
    }
    
    protected void prepareGui()
    {
        JTabbedPane tp = new JTabbedPane();
        
        tp.add(new CFOMasterPanel());
        tp.add(new BFOMasterPanel());
        tp.add(new TSPMasterPanel());             
        tp.add(new PSPMasterPanel());
        tp.add(new GCPMasterPanel());
        tp.add(new BCRMasterPanel());
        
        getContentPane().add(tp, BorderLayout.CENTER);
    }
    
  
}
