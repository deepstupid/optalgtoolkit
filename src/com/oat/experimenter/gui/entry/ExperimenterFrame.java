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
package com.oat.experimenter.gui.entry;

import java.awt.BorderLayout;

import com.oat.experimenter.gui.experiments.ExperimentManagementPanel;
import com.oat.gui.GenericOATFrame;

/**
 * Type: ExperimenterMainFrame<br/>
 * Date: 30/07/2007<br/>
 * <br/>
 * Description: Entry point into the experimenter interface
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 21/08/2007	JBrownlee	Updated to extend GenericOATFrame
 * </pre>
 *
 */
public class ExperimenterFrame extends GenericOATFrame
{    
	/**
	 * Default constructor, closes the frame on exit
	 */
	public ExperimenterFrame()
	{
		this(true);
	}
	
	/**
	 * Does not close on exit, just dispose, suitable when called from another frame 
	 * @param closeOnExit
	 */
    public ExperimenterFrame(boolean closeOnExit)
    {
        super("Experimenter");
        setSize(800, 600);
        if(!closeOnExit)
        {
        	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }
    }   
    
    @Override
    protected void prepareGUI()
    {
    	ExperimentManagementPanel p = new ExperimentManagementPanel();
        add(p, BorderLayout.CENTER);
    }
    
    public static void main(String[] args)
    {
        new ExperimenterFrame().makeVisible();
    }
}
