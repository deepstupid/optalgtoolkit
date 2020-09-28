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
package com.oat.explorer.gui.panels;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.oat.Domain;
import com.oat.explorer.gui.plot.PopulationQualityLineGraph;
import com.oat.explorer.gui.plot.RunLogPanel;

/**
 * Type: MasterPanel<br/>
 * Date: 24/11/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 18/01/2007   JBrownlee   Added a solution quality line graph (quality of solution for each solution generated)
 * 21/08/2007	JBrownlee	Changed the panel name to that of the domains human readable name
 * </pre>
 */
public abstract class MasterPanel extends JPanel
{
    public final static Random rand = new Random(); // for GUI only
    
    // master elements
    protected ControlPanel controlPanel;
    protected AlgorithmPanel algorithmPanel;
    protected ProblemPanel problemPanel;
    protected JTabbedPane centrePanel;
    
    // specific elements
    protected PopulationQualityLineGraph runGraph;  
    protected RunLogPanel logPanel;
    
    protected final Domain domain;
    
    public MasterPanel()
    {        
        domain = getDomain();
        //setName(getPanelName()); // smaller but less clear
        setName(domain.getHumanReadableName());
        prepareGUI();
    }    
    
    public abstract String getPanelName();
    
    public abstract Domain getDomain();
    
    protected void prepareGUI()
    {
        // GUI element creation
        controlPanel = prepareControlPanel();
        algorithmPanel = prepareAlgorithmPanel();
        problemPanel = prepareProblemPanel();
        centrePanel = prepareCentrePanel();        
        
        controlPanel.setAlgorithmPanel(algorithmPanel);
        
        // listeners
        prepareDefaultListeners();
        prepareAdditionalListeners();
        
        // element placement
        JPanel top = new JPanel(new BorderLayout());
        top.add(algorithmPanel, BorderLayout.CENTER);
        top.add(controlPanel, BorderLayout.EAST);
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(problemPanel, BorderLayout.EAST);
        add(centrePanel, BorderLayout.CENTER);
        
        // select random problem and algorithm
        problemPanel.setRandomProblemInstance(rand);
        algorithmPanel.setRandomAlgorithmInstance(rand);        
    }
 
    protected ControlPanel prepareControlPanel()
    {
        ControlPanel cp = new ControlPanel(domain);
        return cp;
    }
    
    protected abstract AlgorithmPanel prepareAlgorithmPanel();
    protected abstract ProblemPanel prepareProblemPanel();
    protected abstract JPanel [] prepareAdditionalCentralPanels();
    protected abstract void prepareAdditionalListeners();
    
    protected JTabbedPane prepareCentrePanel()
    {
        JTabbedPane tp = new JTabbedPane();
        
        // creation
        runGraph = new PopulationQualityLineGraph();
        logPanel = new RunLogPanel();
        logPanel.setExecutor(controlPanel.getExecutor());
        
        // addition
        tp.add(runGraph);
        tp.add(logPanel);                
        
        // additional
        JPanel [] additionalPanels = prepareAdditionalCentralPanels();
        if(additionalPanels!=null)
        {
            for (int i = 0; i < additionalPanels.length; i++)
            {
                tp.add(additionalPanels[i]);
            }
        }
        
        return tp;
    }
    
    protected void prepareDefaultListeners()
    {
        // prepare control panel
        algorithmPanel.registerAlgorithmChangedListener(controlPanel);
        problemPanel.registerProblemChangedListener(controlPanel);
        problemPanel.getStopConditionPanel().addStopConditionChangedListener(controlPanel);
        // prepare algorithm panel
        controlPanel.registerAlgorithmRunStateChangeListener(algorithmPanel);               
        // prepare problem panel
        controlPanel.registerAlgorithmRunStateChangeListener(problemPanel);
        controlPanel.registerAlgorithmRunStateChangeListener(problemPanel.getStopConditionPanel());
        // prepare line graph
        algorithmPanel.registerAlgorithmIterationCompleteListener(runGraph); // listen to algorithm iterations
        controlPanel.registerClearableListener(runGraph);
        // prepare run log
        controlPanel.registerClearableListener(logPanel);
        controlPanel.registerAlgorithmRunStateChangeListener(logPanel);
    }
}
