/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

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
package com.oat.experimenter.gui.runs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.experimenter.RunResult;
import com.oat.gui.FinishedEventNotifier;
import com.oat.gui.FinishedNotificationEventListener;

/**
 * Description: GUI Component for executing (start/stop/progress) a set of experimental runs
 *  
 * Date: 24/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class RunExecutionPanel extends JPanel
    implements ActionListener, FinishedEventNotifier
{    
    // progress bars
    protected JProgressBar runSetProgress;
    protected JProgressBar runRepeatsProgress;
    // controls
    protected JButton startButton;
    protected JButton stopButton;    
    // component status
    /**
     * Whether or not a set of runs is currently being executed
     */
    protected volatile boolean isRunning;
    /**
     * Whether or not the user has requested a stop on the execution
     */
    protected volatile boolean stopRequested;
    /**
     * The current experiment being worked on
     */
    protected Experiment experiment;
    /**
     * A set of runs from the current experiment to be/are being executed
     */
    protected ExperimentalRun [] currentRuns;    
    
    
    /**
     * Default constructor
     */
    public RunExecutionPanel()
    {
        createGUI();
    }
    
    /**
     * Construct the GUI
     */
    protected void createGUI()
    {
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        isRunning = false;
        
        runSetProgress = new JProgressBar();
        runSetProgress.setStringPainted(true);
        runRepeatsProgress = new JProgressBar();
        runRepeatsProgress.setStringPainted(true);
        
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); 
        
        // run set
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.WEST;
        p.add(new JLabel("Run Set:"), c);        
        
        c.gridx = 1;
        c.gridy = 0;        
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(runSetProgress, c);
        
        // run repeats
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(new JLabel("Run Repeats:"), c);        
        
        c.gridx = 1;
        c.gridy = 1;        
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(runRepeatsProgress, c);
        
        JPanel b = new JPanel();
        b.add(startButton);
        b.add(stopButton);
        
        c.gridx = 0;
        c.gridy = 2;        
        c.gridwidth = 4;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        p.add(b, c);
        
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Run Execution"));        
    }    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if(src == startButton)        
        {
            executeRunSet();
        }
        else if(src == stopButton)
        {
            stopRun();
        }
    }

    /**
     * Sets the working experiment to the provided, and the run set to
     * all runs in the experiment
     * 
     * @param aExperiment
     */
    public void setExecutionTask(Experiment aExperiment)
    {
    	LinkedList<ExperimentalRun> runs = aExperiment.getRuns();
    	ExperimentalRun [] runsArray = runs.toArray(new ExperimentalRun[runs.size()]);
    	setExecutionTask(aExperiment, runsArray);
    }
    
    public void setExecutionTask(Experiment aExperiment, ExperimentalRun [] runs)
    {
    	// store things 
    	experiment = aExperiment;
    	currentRuns = runs;
    }
    
    
    public void executeRunSet()
    {
        if(currentRuns==null || currentRuns.length<1)
        {
            return; // do nothing
        }
        
        // prepare GUI
        isRunning = true;
        stopRequested = false;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);        
        runSetProgress.setMaximum(currentRuns.length);
        runSetProgress.setValue(0);
        // prepare the runner
        RunSetRunner runner = new RunSetRunner();
        // execute in another thread
        new Thread(runner).start();
    }    
    
    protected void resetRunRepeatsProgress(final int max)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                runRepeatsProgress.setMaximum(max);
                runRepeatsProgress.setValue(0);
            }
        });
    }
    

    
    protected void incrementRunRepeatsProgress()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                runRepeatsProgress.setValue(runRepeatsProgress.getValue()+1);
            }
        });
    }
    
    protected void incrementRunSetProgress()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                runSetProgress.setValue(runSetProgress.getValue()+1);
            }
        });
    }
    
    
    /**
     * Type: RunSetRunner<br/>
     * Date: 02/08/2007<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     *
     * 
     * <pre>
     * Change History
     * ----------------------------------------------------------------------------
     * 
     * </pre>
     *
     */
    protected class RunSetRunner implements Runnable
    {  
        /**
         * Execute the run set
         */
        public void run()
        {
            for (int i = 0; !stopRequested && i < currentRuns.length; i++)
            {
                // reset the progress for this run
                resetRunRepeatsProgress(currentRuns[i].getRepeats());
                // perform the repeats for this run
                LinkedList<RunResult> runRepeats = runRepeats(currentRuns[i]);
                // check for stop or no results (error)
                if(stopRequested || runRepeats == null)
                {
                    break;
                }
                // store results
                //if(!wasError(runRepeats))
                {
                	// only store results if no error (lazy hack)
                	// TODO handle this condition better in the GUI
	                try
					{
						ExperimentalRunUtils.outputResults(experiment, currentRuns[i], runRepeats);
					} 
	                catch (ExperimentException e)
					{
	                	// TODO hack
	                	throw new RuntimeException("Error saving results: " + e.getMessage(), e);
					}
                }
                // increment run set progress
                incrementRunSetProgress();                
            }
            // finished the run good or bad
            runFinished();
        }
        
        
        public boolean wasError(LinkedList<RunResult> runRepeats)
        {
        	for (RunResult r : runRepeats)
			{
				if(r.isWasError())
				{
					return true;
				}
			}
        	
        	return false;
        }
        
        
        /**
         * Execute a single run with a number of repeats
         * 
         * @param run
         * @return
         */
        protected LinkedList<RunResult> runRepeats(ExperimentalRun run)
        {
            LinkedList<RunResult> runRepeats = new LinkedList<RunResult>(); 
            
            for (int i = 1; !stopRequested && i <= run.getRepeats(); i++)
            {       
                // execute the single run
                RunResult r = ExperimentalRunUtils.executeSingleRepeat(experiment, run, i);
                // store the result
                runRepeats.add(r);
                // increment repeat progress
                incrementRunRepeatsProgress();
            }
            
            return runRepeats;
        }
    }
    
    
    
    public void runFinished()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                // re-enable the GUI
                isRunning = false;
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                // reset all progress
                runSetProgress.setMaximum(1);
                runSetProgress.setValue(1);
                runRepeatsProgress.setMaximum(1);
                runRepeatsProgress.setValue(1);  
                triggerFinishedEvent(); // we are done
            }
        });
    }
	
    
    public void stopRun()
    {
        if(!isRunning)
        {
            throw new RuntimeException("Unable to stop execution, not running");
        }
        
        stopRequested = true;
    }
    
	protected LinkedList<FinishedNotificationEventListener> listeners = new LinkedList<FinishedNotificationEventListener>();
	
	protected void triggerFinishedEvent()
	{
		for(FinishedNotificationEventListener l : listeners)
		{
			l.finishedEvent();
		}
	}	
	
	@Override
	public void addNotificationListener(FinishedNotificationEventListener l)
	{
		listeners.add(l);
	}

	@Override
	public boolean removeNotificationListener(
			FinishedNotificationEventListener l)
	{
		return listeners.remove(l);
	}
}
