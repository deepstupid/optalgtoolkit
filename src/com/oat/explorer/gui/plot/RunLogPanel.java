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
package com.oat.explorer.gui.plot;

import java.util.Date;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.AlgorithmExecutor;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.explorer.gui.AlgorithmRunStateChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.gui.GenericLogPanel;


/**
 * Type: RunLogPanel<br/>
 * Date: Nov 9, 2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 24/07/2007   JBrownlee   Reordered a few output fields.
 * 30/08/2007	JBrownee	Moved generic functionality into a GenericLogPanel object
 * </pre>
 */
public class RunLogPanel extends GenericLogPanel 
    implements ClearEventListener, AlgorithmRunStateChangedListener
{   
    
	protected AlgorithmExecutor executor;
	
	
	
    public RunLogPanel()
    {
        setName("Run Log");
    }
    
    public void setExecutor(AlgorithmExecutor aExecutor)
    {
    	executor = aExecutor;
    }
    
    
    public void report(Problem p, Algorithm a)
    {
        reportProblem(p);
        reportAlgorithm(a);
        reportStopConditions(p);
        reportRunDetails();        
    }
    
    public void reportProblem(Problem p)
    {
    	reportAppendLine(" ### Problem Details ###");
        reportAppendLine("Name:............" + p.getName());
        if(p.isUserConfigurable())
        {
        	reportAppendLine("Configuration:..." + p.getConfigurationDetails());
        }
        reportAppendLine("Details:........." + p.getDetails()); 
        reportAppendLine("");
    }
    
    public void reportAlgorithm(Algorithm a)
    {
    	reportAppendLine(" ### Algorithm Details ###");
        reportAppendLine("Name:............" + a.getName());
        if(a.isUserConfigurable())
        {
        	reportAppendLine("Configuration:..." + a.getConfigurationDetails());
        }
        reportAppendLine("Details:........." + a.getDetails()); 
        reportAppendLine("");
    }
    
    
    public void reportRunDetails()
    {
    	reportAppendLine(" ### Run Probes ###");
    	
    	LinkedList<RunProbe> probes = executor.getRunProbes();
    	
    	for(RunProbe p : probes)
    	{
    		reportAppendLine(p.getName() + ": " + p.getProbeObservation());
    	}    	
    }    
    
    
    public void reportStopConditions(Problem p)
	{
    	LinkedList<StopCondition> list = p.getStopConditions();
    	
    	reportAppendLine(" ### Stop Conditions ###");
    	for(StopCondition s : list)
    	{
            reportAppendLine("Name:............" + s.getName());
            if(s.isUserConfigurable())
            {
            	reportAppendLine("Configuration:..." + s.getConfigurationDetails());
            }
            reportAppendLine("Details:........." + s.getDetails()); 
            reportAppendLine("");
    	}
	}
   

    public void algorithmFinishNotify(Problem p, Algorithm a)
    {
    	enableControls();    	
    	reportAppendLine("Finished: " + new Date());
        reportAppendLine("");
        report(p, a);
        scrollToTop();
    }

    public void algorithmStartNotify(Problem p, Algorithm a)
    {
    	disableControls();        
    	reportAppendLine("Started: " + new Date());
    }
}
