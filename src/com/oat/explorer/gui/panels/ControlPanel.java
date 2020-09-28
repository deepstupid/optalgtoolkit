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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.oat.Algorithm;
import com.oat.AlgorithmExecutor;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.Domain;
import com.oat.Problem;
import com.oat.StopCondition;
import com.oat.explorer.gui.AlgorithmChangedListener;
import com.oat.explorer.gui.AlgorithmRunStateChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.ProblemChangedListener;
import com.oat.explorer.gui.StopConditionChangedListener;
import com.oat.stopcondition.RequestStopCondition;

/**
 * Type: ControlPanel<br/>
 * Date: 24/11/2006<br/>
 * <br/>
 * Description: Control panel class for the GUI
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ControlPanel extends JPanel
    implements ActionListener, AlgorithmRunStateChangedListener, AlgorithmChangedListener, ProblemChangedListener, StopConditionChangedListener
{
    protected final LinkedList<ClearEventListener> clearableListeners;
    protected final LinkedList<AlgorithmRunStateChangedListener> runStateChangeListeners;
    
    protected final Domain domain;
    protected AlgorithmPanel algorithmPanel;
    
    protected JButton startAlgorithm;
    protected JButton stopAlgorithm;
    protected JButton clearAlgorithm;
    
    protected AlgorithmExecutor executor;
    protected RequestStopCondition requestStopCondition;    
    
    
    public ControlPanel(Domain aDomain)
    {    	
    	domain = aDomain;    	
        clearableListeners = new LinkedList<ClearEventListener>();
        runStateChangeListeners = new LinkedList<AlgorithmRunStateChangedListener>();
        prepareExecutor();
        setName("Control Panel");
        prepareGUI();
    }
    
    
    
    
    public AlgorithmPanel getAlgorithmPanel()
	{
		return algorithmPanel;
	}




	public void setAlgorithmPanel(AlgorithmPanel algorithmPanel)
	{
		this.algorithmPanel = algorithmPanel;
	}




	protected void prepareExecutor()
    {
    	executor = new AlgorithmExecutor();
    	// add all the problems
    	executor.addRunProbes(domain.loadDomainRunProbes());
    	// stop conditions
    	requestStopCondition = new RequestStopCondition();
    	executor.addStopCondition(requestStopCondition); // always start with the user one
    }
    
    
    protected void prepareGUI()
    {
        startAlgorithm = new JButton("Start");
        stopAlgorithm = new JButton("Stop");
        stopAlgorithm.setEnabled(false);
        clearAlgorithm = new JButton("Clear");
        
        startAlgorithm.addActionListener(this);
        stopAlgorithm.addActionListener(this);
        clearAlgorithm.addActionListener(this);
        
        startAlgorithm.setToolTipText("Start a run with the current algorithm and problem instance");
        stopAlgorithm.setToolTipText("Stop the algorithm run");
        clearAlgorithm.setToolTipText("Clear all results from the last run");
        
        add(startAlgorithm);
        add(stopAlgorithm);
        add(clearAlgorithm);
        
        registerAlgorithmRunStateChangeListener(this);   
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), getName()));
    }
    
    public void clearEvent()
    {
        for(ClearEventListener c: clearableListeners)
        {
            c.clear();
        }
    }
    
    public void algorithmStartEvent(Problem p, Algorithm a)
    {
        for(AlgorithmRunStateChangedListener r: runStateChangeListeners)
        {
            r.algorithmStartNotify(p, a);
        }
    }
    
    public void algorithmFinishedEvent(Problem p, Algorithm a)
    {
        for(AlgorithmRunStateChangedListener r: runStateChangeListeners)
        {
            r.algorithmFinishNotify(p, a);
        }
    }
    
    public void registerClearableListener(ClearEventListener c)
    {
        clearableListeners.add(c);
    }
    
    public void registerAlgorithmRunStateChangeListener(AlgorithmRunStateChangedListener r)
    {
        runStateChangeListeners.add(r);
    }

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == startAlgorithm)
        {
            if(executor.getProblem() == null)
            {
                JOptionPane.showMessageDialog(this, "Unable to start algorithm as no problem has been selected.", "No Problem Selected", JOptionPane.ERROR_MESSAGE);
            }
            else if(executor.getAlgorithm() == null)
            {
                JOptionPane.showMessageDialog(this, "Unable to start algorithm as no algorithm has been selected.", "No Algorithm Selected", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                clearEvent();                             
                startAlgorithm();
            }
        }
        else if(src == clearAlgorithm)
        {
            clearEvent();
        }
        else if(src == stopAlgorithm)
        {
            // do not allow the user to press it twice
            stopAlgorithm.setEnabled(false);
            requestStopCondition.requestStop();
        }
    }
    
    /**
     * Execute the selected algorithm with the selected problem in a new worker thread
     * Once the algorithm has completed all interested parties are notified via the event dispatch thread
     */
    protected void startAlgorithm()
    {    	
        final JPanel jp = this;
        
        // check for automatically configured algorithm
        if(algorithmPanel.shoudAutomaticallyConfigure())
        {
        	((AutomaticallyConfigurableAlgorithm)executor.getAlgorithm()).automaticallyConfigure(executor.getProblem());
        }
        
        // prepare to execute the algorithm - still from the event dispatch thread.
        algorithmStartEvent(executor.getProblem(), executor.getAlgorithm()); 
        
        Runnable r = new Runnable()
        {
            public void run()
            {                // execute the algorithm
                try
                {
                	executor.executeAndWait();
                }
                // bad problem configuration
                catch(final Exception e)
                {
                	// TODO - remove this
                	e.printStackTrace();
                	
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {                        	
                            JOptionPane.showMessageDialog(jp, "Something bad happened:\n "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);                            
                        }
                    });
                }     
                finally
                {
                    // notify all interested parties from the event dispatch thread
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            algorithmFinishedEvent(executor.getProblem(), executor.getAlgorithm());
                        }
                    });
                }
            }
        };
        new Thread(r).start();
    }
    

    public void algorithmFinishNotify(Problem p, Algorithm a)
    {
        startAlgorithm.setEnabled(true);
        stopAlgorithm.setEnabled(false);
        clearAlgorithm.setEnabled(true); 
    }

    public void algorithmStartNotify(Problem p, Algorithm a)
    {
        startAlgorithm.setEnabled(false);
        stopAlgorithm.setEnabled(true);
        clearAlgorithm.setEnabled(false); 
    }

    public void algorithmChangedEvent(Algorithm a)
    {  
    	executor.setAlgorithm(a);
    }

    public void problemChangedEvent(Problem p)
    {
    	executor.setProblem(p);
    }

	public AlgorithmExecutor getExecutor()
	{
		return executor;
	}


	@Override
	public void stopConditionSelectionChanged(LinkedList<StopCondition> list)
	{
		// clear stop condition
		executor.getStopConditions().clear();
		// add the needed one
		executor.addStopCondition(requestStopCondition);
		// add all new ones
		executor.addStopConditions(list);
	}
}
