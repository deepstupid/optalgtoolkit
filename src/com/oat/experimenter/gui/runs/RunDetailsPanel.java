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
import java.io.File;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.gui.BeanConfigurationFrame;
import com.oat.gui.FinishedEventNotifier;
import com.oat.gui.FinishedNotificationEventListener;
import com.oat.gui.GUIException;
import com.oat.utils.BeanUtils;
import com.oat.utils.GUIUtils;

/**
 * Description: Panel for managing the details of a run 
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
public class RunDetailsPanel extends JPanel 
    implements ActionListener, FinishedEventNotifier
{
	protected static enum InternalMode {NEW, EDIT}
    
    protected JButton saveButton;
    protected JButton clearButton;
    protected JButton deleteButton;
    
    protected JTextField runId;
    protected JButton configProblemButton;
    protected JButton configAlgorithmButton;
    protected JComboBox problemInstances;
    protected JComboBox algorithmInstances;    
    protected JTextField totalRepeats;
    protected JTextField completed;
    
    
    public boolean allowDelete = true;
    public boolean allowEditChangeAlgorithm = true;
    public boolean allowEditChangeProblem = true; 
    
    /**
     * The current experimental run being worked on
     */
    protected ExperimentalRun experimentalRun;
    /**
     * The current experiment being worked on
     */
    protected Experiment experiment;
    /**
     * The current operating mode for the GUI
     */
    protected InternalMode internalMode;
    
    
    /**
     * Default constructor, requires the user select the mode manually
     * via prepareForUpdate() or prepareForNew();
     */
    public RunDetailsPanel()
    {
    	createGUI();
    }
    
    /**
     * Constructor for preparing a panel for creating a new experimental run
     * @param aHomeDirectory
     */
    public RunDetailsPanel(Experiment aExperiment)
    {
    	createGUI();
    	prepareForNew(aExperiment);        
    }
    
    /**
     * Constructor for preparing a panel for editing an existing experimental run
     * @param aExperiment
     */
    public RunDetailsPanel(Experiment aExperiment, ExperimentalRun aRun)
    {
    	createGUI();
    	prepareForUpdate(aExperiment, aRun);        
    }
    
    /**
     * Prepare the object for updating an experiment
     * @param aExperiment
     * @param aHomeDirectory
     */
    public void prepareForUpdate(Experiment aExperiment, ExperimentalRun aRun)
    {
    	experiment = aExperiment;
    	experimentalRun = aRun;
    	internalMode = InternalMode.EDIT;
        // populate the GUI components
        populateGUIFromExperiment();
        // prepare GUI
        prepareGUIBasedOnInternalMode();
    }
    
    /**
     * Prepare the GUI for creating a new experimental run
     * @param aHomeDirectory
     */
    public void prepareForNew(Experiment aExperiment)
    {
    	experiment = aExperiment;
    	experimentalRun = new ExperimentalRun();
    	internalMode = InternalMode.NEW;
        // populate the GUI components
        populateGUIFromExperiment();
        // prepare GUI
        prepareGUIBasedOnInternalMode();
        // ensure config buttons enable/disable
        problemInstances.setSelectedIndex(0);
        algorithmInstances.setSelectedIndex(0);
    }
    

    /**
     * Prepares the GUI
     */
    protected void createGUI()
    {
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");
        deleteButton = new JButton("Delete");
        
        saveButton.addActionListener(this);
        clearButton.addActionListener(this);
        deleteButton.addActionListener(this);
        
        problemInstances = new JComboBox();
        problemInstances.addActionListener(this);        
        algorithmInstances = new JComboBox();
        algorithmInstances.addActionListener(this);        
        totalRepeats = new JTextField(4);       
        runId  = new JTextField(10);   
        runId.setEnabled(false); // never edit       
        completed = new JTextField(10);
        completed.setEnabled(false); // never edit 
        
        configProblemButton = new JButton("Config");
        configAlgorithmButton = new JButton("Config");
        
        configProblemButton.addActionListener(this);
        configAlgorithmButton.addActionListener(this);
        
        configProblemButton.setEnabled(false);
        configAlgorithmButton.setEnabled(false);
        
        // gridbag
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
                
        // run id
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(new JLabel("Run Name:"), c);        
        
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.REMAINDER;
        p.add(runId, c);
        
        // problem
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.WEST;
        p.add(new JLabel("Problem:"), c);        
        
        c.gridx = 1;
        c.gridy = 1;        
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(problemInstances, c);
        
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(configProblemButton, c);
        
        // algorithm
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(new JLabel("Algorithm:"), c);        
        
        c.gridx = 1;
        c.gridy = 2;        
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(algorithmInstances, c);
        
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(configAlgorithmButton, c);
        
        // repeats
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(new JLabel("Run Repeats:"), c);        
        
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(totalRepeats, c);
        
        // completed
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(new JLabel("Completed:"), c);        
        
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.RELATIVE;
        p.add(completed, c);
        
        // control panel
        JPanel cp = new JPanel();
        cp.add(saveButton);
        cp.add(clearButton);
        cp.add(deleteButton);
                
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 4;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        p.add(cp, c);        
        
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Run Details"));
    }
    
    /**
     * Populates the GUI components from an experiment
     * 
     * @param aMode
     */
    protected void populateGUIFromExperiment()
    {
        // problems        
        Problem [] problems = null;
        try
		{
        	problems = experiment.getDomain().loadProblemList();
		}
		catch (Exception e)
		{
			throw new GUIException("Unable to load problems for experiment domain: " + e.getMessage(), e);
		}
        for (int i = 0; i < problems.length; i++)
        {
            problemInstances.addItem(problems[i]);
        }
        // algorithms
        
        Algorithm [] algorithms = null;
        try
		{
        	algorithms = experiment.getDomain().loadAlgorithmList();
		}
		catch (Exception e)
		{
			throw new GUIException("Unable to load algorithms for experiment domain: " + e.getMessage(), e);
		}
        for (int i = 0; i < algorithms.length; i++)
        {
            algorithmInstances.addItem(algorithms[i]);
        }
    	
        // prepare the GUI components based on mode
        if(internalMode == InternalMode.EDIT)
        {	
        	// populate
        	setAndConfigureAlgorithmByClass(experimentalRun.getAlgorithm());
        	setAndConfigureProblemByClass(experimentalRun.getProblem());
        	totalRepeats.setText(""+experimentalRun.getRepeats());
        	runId.setText(experimentalRun.getId());
        	completed.setText(""+experimentalRun.isCompleted());
        }
        else if(internalMode == InternalMode.NEW)
        {
        	clearGUI();
        }
    }
    
    
    /**
     * Locates and configures the algorithm instance in the algorithm list
     * 
     * @param algorithm
     */
    protected void setAndConfigureAlgorithmByClass(Algorithm algorithm)
    {
        boolean found = false;
        
        for (int i = 0; !found && i < algorithmInstances.getItemCount(); i++)
		{
        	Algorithm a = (Algorithm) algorithmInstances.getItemAt(i);     
        	
        	// compare at the class level
			if(a.getClass().equals(algorithm.getClass()) && a.getName().equals(algorithm.getName()))
			{
				// select
				algorithmInstances.setSelectedIndex(i);
				// populate
				a.populateFromInstance(algorithm);			
				found = true;
			}
		}
        
        if(!found)
        {
        	throw new GUIException("Unable to set algorithm configured in experiment, unknown class: " + algorithm.getClass());
        }
        
        if(algorithm.isUserConfigurable())
        {
        	configAlgorithmButton.setEnabled(true);
        }
        else
        {
        	configAlgorithmButton.setEnabled(false);
        }
    }
    
    /**
     * Locates and configures the problem instance in the problem list
     * 
     * @param problem
     */
    protected void setAndConfigureProblemByClass(Problem problem)
    {
        boolean found = false;
        
        for (int i = 0; !found && i < problemInstances.getItemCount(); i++)
		{
        	Problem p = (Problem) problemInstances.getItemAt(i);     
        	
        	// compare at the class level
			if(p.getClass().equals(problem.getClass()) && p.getName().equals(problem.getName()))
			{
				// select
				problemInstances.setSelectedIndex(i);
				// populate
				p.populateFromInstance(problem);
				found = true;
			}
		}
        
        if(!found)
        {
        	throw new GUIException("Unable to set problem configured in experiment, unknown class: " + problem.getClass());
        }
        
        if(problem.isUserConfigurable())
        {
        	configProblemButton.setEnabled(true);
        }
        else
        {
        	configProblemButton.setEnabled(false);
        }
    }
    
    
    /**
     * Enables and disables GUI components based on the internal mode and experiment details
     */
    protected void prepareGUIBasedOnInternalMode()
    {
        // prepare the GUI components based on mode
        if(internalMode == InternalMode.EDIT)
        {	   	
        	// disable general GUI items
        	if(allowDelete)
        	{
        		deleteButton.setEnabled(true);
        	}
        	else
        	{
        		deleteButton.setEnabled(false);
        	}        	
        	clearButton.setEnabled(false);        	
        	// check if completed
        	if(experimentalRun.isCompleted())
        	{
        		// cannot change things after executed
        		algorithmInstances.setEnabled(false);
        		configAlgorithmButton.setEnabled(false);
        		problemInstances.setEnabled(false);
        		configProblemButton.setEnabled(false);
        		totalRepeats.setEnabled(false);
        		// cannot save because we cannot change things
        		saveButton.setEnabled(false); 
        	}
        	else
        	{
        		// can change things after executed        		
        		
        		if(allowEditChangeAlgorithm)
        		{
        			algorithmInstances.setEnabled(true);
        		}
        		else
        		{
        			algorithmInstances.setEnabled(false);
        		}
        		
        		if(allowEditChangeProblem)
        		{
        			problemInstances.setEnabled(true);
        		}
        		else
        		{
        			problemInstances.setEnabled(false);
        		}
        		
        		totalRepeats.setEnabled(true);
        	}
        }
        else if(internalMode == InternalMode.NEW)
        {
            deleteButton.setEnabled(false); // cannot delete
            clearButton.setEnabled(true); // can clear
            // can do everything else
    		problemInstances.setEnabled(true);
    		configProblemButton.setEnabled(true);
    		totalRepeats.setEnabled(true);
        }
    }    
    
    /**
     * Clear the GUI
     */
    protected void clearGUI()
    {        
    	algorithmInstances.setSelectedIndex(0);
    	problemInstances.setSelectedIndex(0);
    	totalRepeats.setText(""+ExperimentUtils.DEFAULT_REPEATS);
    	runId.setText("");
    	completed.setText("false");
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if (src == saveButton)
        {
        	// populate
        	ExperimentalRun run = populateExperimentalRunFromGUI();
        	// validate
        	if(isValidExperimentalRun(run))
        	{
        		// store in the real experiment        		
        		BeanUtils.beanPopulate(run, experimentalRun);
        		if(internalMode == InternalMode.NEW)
        		{
        			// save
        			if(saveExperimentalRun())
        			{
        				// we are now in edit mode
        				prepareForUpdate(experiment, experimentalRun);
        			}
        		}
        		else if(internalMode == InternalMode.EDIT)
        		{
        			// update
        			updateExperimentalRun();
        		}
				// we may be done
				triggerFinishedEvent();

        	}
        }
        else if (src == deleteButton)
        {
        	if(deleteExperimentalRun())
        	{
        		// we are now in new mode
        		prepareForNew(experiment);
				// we may be done
				triggerFinishedEvent();
        	}
        }
        else if (src == clearButton)
        {
            clearGUI();
        }
        else if(src == configProblemButton)
        {
        	Object p = problemInstances.getSelectedItem();
        	if(p!=null)
        	{
        		BeanConfigurationFrame f = new BeanConfigurationFrame(null, p, "Problem Configuration", null);
        		f.setVisible(true);
        	}
        }
        else if(src == configAlgorithmButton)
        {
        	Object a = algorithmInstances.getSelectedItem();
        	if(a!=null)
        	{
        		BeanConfigurationFrame f = new BeanConfigurationFrame(null, a, "Algorithm Configuration", new String[]{"AutomaticallyConfigure","Seed"});
        		f.setVisible(true);
        	}
        }
        else if(src == problemInstances)
        {
        	if(((Problem)problemInstances.getSelectedItem()).isUserConfigurable())
        	{
        		configProblemButton.setEnabled(true);
        	}
        	else
        	{
        		configProblemButton.setEnabled(false);
        	}
        }
        else if(src == algorithmInstances)
        {
        	if(((Algorithm)algorithmInstances.getSelectedItem()).isUserConfigurable())
        	{
        		configAlgorithmButton.setEnabled(true);
        	}
        	else
        	{
        		configAlgorithmButton.setEnabled(false);
        	}
        }
    }
    
    /**
     * Delete the experimental run, if error the user is informed and false is returned
     * @return
     */
    protected boolean deleteExperimentalRun()
    {
        try
		{
        	// delete and output the schedual
        	ExperimentalRunUtils.deleteRunAndExternalizeSchedule(experiment, experimentalRun);
		} 
        catch (ExperimentException e1)
		{
        	JOptionPane.showMessageDialog(this, "Unable to delete experimental run: " + e1.getMessage(), "Error Deleting Experimental Run", JOptionPane.ERROR_MESSAGE);
        	return false;
		}    	
        return true;
    }
    
    /**
     * Save the experimental run, if error the user is informed and false is returned
     * @return
     */
    protected boolean saveExperimentalRun()
    {
        try
		{
            // add to the experiment definition
            experiment.addRun(experimentalRun);
            // just output the schedule again
            ExperimentalRunUtils.externaliseEntireRunSchedule(experiment);
		} 
        catch (ExperimentException e1)
		{
        	JOptionPane.showMessageDialog(this, "Unable to save experimental run: " + e1.getMessage(), "Error Saving Experimental Run", JOptionPane.ERROR_MESSAGE);
        	return false;
		}    	
        return true;
    }
    
    /**
     * Update the experimental run, if error the user is informed and false is returned
     * @return
     */
    protected boolean updateExperimentalRun()
    {
        try
		{
        	// just output the schedule again
        	ExperimentalRunUtils.externaliseEntireRunSchedule(experiment);
		} 
        catch (ExperimentException e1)
		{
        	JOptionPane.showMessageDialog(this, "Unable to update experimental run: " + e1.getMessage(), "Error Updating Experimental Run", JOptionPane.ERROR_MESSAGE);
        	return false;
		}   	
        return true;
    }

    
    /**
     * Validates the a populated experimental run instance
     * If invalid, the user is informed and a false value is returned
     * @return
     */
    protected boolean isValidExperimentalRun(ExperimentalRun run)
    {
    	// validate
        try
        {
        	run.validateConfiguration(experiment.getStopCondition());
        }
        catch(Exception e)
        {
        	JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Experimental Run", JOptionPane.ERROR_MESSAGE);
        	return false;
        }    	
        return true;
    }
    
    /**
     * Creates an experimental run instance variable with information from the GUI
     * 
     * @return
     */
    protected ExperimentalRun populateExperimentalRunFromGUI()
    {
    	ExperimentalRun run = new ExperimentalRun();
    	// algorithm
    	Algorithm algorithm = (Algorithm) algorithmInstances.getSelectedItem();
    	if(algorithm.isUserConfigurable())
    	{
    		algorithm = BeanUtils.beanCopy(algorithm);
    	}
    	run.setAlgorithm(algorithm);
    	// problem
    	Problem problem = (Problem) problemInstances.getSelectedItem();
    	if(problem.isUserConfigurable())
    	{
    		problem = BeanUtils.beanCopy(problem);
    	}
    	run.setProblem(problem);
		
    	// id
    	String id = null;
    	if(internalMode == InternalMode.NEW)
    	{
    		id = ExperimentalRunUtils.getNextValidRunId(experiment);
    	}
    	else
    	{
    		id = experimentalRun.getId();
    	}
    	run.setId(id);
    	// repeats
    	int repeats = 0;
    	try
    	{
    		// valid for update and new
    		repeats = Integer.parseInt(totalRepeats.getText());
    	}
    	catch(NumberFormatException e)
    	{}
    	run.setRepeats(repeats);
    	
    	return run;
    }
    
	
	public ExperimentalRun getExperimentalRun()
	{
		return experimentalRun;
	}

	public Experiment getExperiment()
	{
		return experiment;
	}

	public InternalMode getInternalMode()
	{
		return internalMode;
	}

	public boolean isAllowDelete()
	{
		return allowDelete;
	}

	public void setAllowDelete(boolean allowDelete)
	{
		this.allowDelete = allowDelete;
	}
	
	

	public boolean isAllowEditChangeAlgorithm()
	{
		return allowEditChangeAlgorithm;
	}

	public void setAllowEditChangeAlgorithm(boolean allowEditChangeAlgorithm)
	{
		this.allowEditChangeAlgorithm = allowEditChangeAlgorithm;
	}

	public boolean isAllowEditChangeProblem()
	{
		return allowEditChangeProblem;
	}

	public void setAllowEditChangeProblem(boolean allowEditChangeProblem)
	{
		this.allowEditChangeProblem = allowEditChangeProblem;
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

	/**
	 * Test out this thing
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			// home
			File home = ExperimentUtils.getDefaultHomeDirectory();
			// load all experiments
			Experiment [] experiments = ExperimentUtils.loadExperiments(home);
			// add run to first experiment
			RunDetailsPanel panel = new RunDetailsPanel(experiments[0]);
			
			GUIUtils.testJFrame(panel, "Run Details");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
