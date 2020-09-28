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

import javax.swing.JTabbedPane;

import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.gui.GUIException;
import com.oat.gui.GenericOATFrame;

/**
 * Type: MainFrame<br/>
 * Date: 17/11/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 21/08/2007	JBrownlee	Updated to extend GenericOATFrame
 * 							Supports variable domain initialization
 * </pre>
 */
public class ExplorerFrame extends GenericOATFrame
{	
    /**
     * Default Entry point, loads all domains and closes on exit
     */
    public ExplorerFrame()
    {
        this((Domain [])null);
    }
    
    /**
     * Creates an frame with a single domain
     * @param aDomain
     */
    public ExplorerFrame(Domain aDomain)
    {
    	this(new Domain[]{aDomain});
    }
    
    /**
     * Entry point that loads the provided domain list, disposes 
     * the frame on exit rather than exiting the application 
     * @param aDomainList
     */
    public ExplorerFrame(Domain [] aDomainList)
    {
        super("Explorer");
        setSize(800, 600);
    	setDefaultCloseOperation(DISPOSE_ON_CLOSE); // do not close the whole lot    	
    	prepareGUI(aDomainList);
    }   
    
    @Override
    protected void prepareGUI()
    {} // do nothing
    
    
    /**
     * Prepares a JTabbedPane with a panel for each of the specified domains
     * @param domainList
     */
    protected void prepareGUI(Domain [] domainList)
    {
    	if(domainList == null)
    	{
    		try
    		{
    			domainList = DomainUtils.loadDomainList(DomainUtils.DOMAIN_LIST);
    		}
    		catch(Exception e)
    		{
    			throw new GUIException("Unable to load domain list: " + e.getMessage(), e);
    		}    		
    	}
        JTabbedPane tp = new JTabbedPane();

        for (int i = 0; i < domainList.length; i++)
		{
        	tp.add(domainList[i].getExplorerPanel()); 
		}
        
        getContentPane().add(tp, BorderLayout.CENTER);
    }
    
    /**
     * Creates an instance of the frame with all domains
     * @param args
     */
    public static void main(String[] args)
    {
        new ExplorerFrame().makeVisible();
    }
}
