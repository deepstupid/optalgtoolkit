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
package com.oat.experimenter.gui.experiments;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.gui.BeanConfigurationFrame;
import com.oat.gui.DomainListCellRenderer;
import com.oat.gui.FinishedEventNotifier;
import com.oat.gui.FinishedNotificationEventListener;
import com.oat.gui.GUIException;
import com.oat.utils.BeanUtils;
import com.oat.utils.GUIUtils;

/**
 * Description: Panel for managing the details of an experiment 
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
public class ExperimentDetailsPanel extends JPanel 
    implements ActionListener, FinishedEventNotifier
{
	protected static enum InternalMode {NEW, EDIT}
    
    protected JButton saveButton;
    protected JButton clearButton;
    protected JButton deleteButton;
    
    protected JTextField experimentName;
    protected JTextField experimentSummary;
    protected JComboBox domainCombo;
    protected JComboBox stopConditionCombo;
    protected JButton configStopConditionButton;
    protected JList runStatisticsList;
    protected DefaultListModel runStatisticsListModel;
    
    
    /**
     * Base or home directory where experiments are created
     */
    protected File homeDirectory;
    /**
     * The current experiment being worked on
     */
    protected Experiment experiment;
    /**
     * The current operating mode for the GUI
     */
    protected InternalMode internalMode;    
    
    
    /**
     * Constructor for preparing a panel for creating a new experiment
     * @param aHomeDirectory
     */
    public ExperimentDetailsPanel(File aHomeDirectory)
    {
    	createGUI();
    	prepareForNew(aHomeDirectory);        
    }
    
    /**
     * Constructor for preparing a panel for editing an existing experiment
     * @param aExperiment
     */
    public ExperimentDetailsPanel(Experiment aExperiment, File aHomeDirectory)
    {
    	createGUI();
    	prepareForUpdate(aExperiment, aHomeDirectory);        
    }
    
    /**
     * Prepare the object for updating an experiment
     * @param aExperiment
     * @param aHomeDirectory
     */
    public void prepareForUpdate(Experiment aExperiment, File aHomeDirectory)
    {
    	homeDirectory = aHomeDirectory;
    	experiment = aExperiment;
    	internalMode = InternalMode.EDIT;
        // populate the GUI components
        populateGUIFromExperiment();
        // prepare GUI
        prepareGUIBasedOnInternalMode();
    }
    
    /**
     * Prepare the GUI for creating a new experiment
     * @param aHomeDirectory
     */
    public void prepareForNew(File aHomeDirectory)
    {
    	homeDirectory = aHomeDirectory;
    	experiment = new Experiment();
    	internalMode = InternalMode.NEW;
        // populate the GUI components
        populateGUIFromExperiment();
        // prepare GUI
        prepareGUIBasedOnInternalMode();
    }
    

    /**
     * Prepares the GUI
     */
    protected void createGUI()
    {
    	// prepare the domain list
        Domain[] domainList = null;
		try
		{
			domainList = DomainUtils.loadDomainList(DomainUtils.DOMAIN_LIST);
		} 
		catch (Exception e)
		{
			throw new GUIException("Unable to load domain list: "+e.getMessage(), e);
		}
        
        experimentName = new JTextField(10);
        experimentSummary = new JTextField(20);
        domainCombo = new JComboBox(domainList);
        domainCombo.setRenderer(new DomainListCellRenderer());
        domainCombo.addActionListener(this);
        stopConditionCombo = new JComboBox();
        stopConditionCombo.addActionListener(this);
        runStatisticsListModel = new DefaultListModel();
        runStatisticsList = new JList(runStatisticsListModel);
        runStatisticsList.setVisibleRowCount(3);
        runStatisticsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");
        deleteButton = new JButton("Delete");        
        saveButton.addActionListener(this);
        clearButton.addActionListener(this);
        deleteButton.addActionListener(this);
        
        configStopConditionButton = new JButton("Config");
        configStopConditionButton.addActionListener(this);
                
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        JPanel p = new JPanel(gbl);
        
        // name
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        JLabel nameLabel = new JLabel("Name:"); 
        gbl.setConstraints(nameLabel, c);
        p.add(nameLabel, c);
        
        c.gridx = 1; 
        c.gridy = 0;        
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(experimentName, c);
        p.add(experimentName, c);
        
        // description
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        JLabel desLabel = new JLabel("Description:");
        gbl.setConstraints(desLabel, c);
        p.add(desLabel, c);
        
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(experimentSummary, c);
        p.add(experimentSummary, c);
        
        // domain 
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        JLabel domainLabel = new JLabel("Domain:");
        gbl.setConstraints(domainLabel, c);
        p.add(domainLabel, c);
        
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(domainCombo, c);
        p.add(domainCombo, c);
        
        // stop condition
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        JLabel stopConditionLabel = new JLabel("Stop Condition:"); 
        gbl.setConstraints(stopConditionLabel, c);
        p.add(stopConditionLabel, c);        
        
        c.gridx = 1;
        c.gridy = 3;        
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(stopConditionCombo, c);
        p.add(stopConditionCombo, c);
        
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(configStopConditionButton, c);
        p.add(configStopConditionButton, c);
        
        // list
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        JLabel rsLabel = new JLabel("Run Statistics:");
        gbl.setConstraints(rsLabel, c);
        p.add(rsLabel, c);
        
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane jsp = new JScrollPane(runStatisticsList);
        gbl.setConstraints(jsp, c);
        p.add(jsp, c);
        
        // buttons
        JPanel b = new JPanel();
        b.add(saveButton);
        b.add(clearButton);
        b.add(deleteButton);
        
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(b, c);
        p.add(b, c);     
        
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Experiment Details"));
    }
    
    /**
     * Populates the GUI components from an experiment
     * 
     * @param aMode
     */
    protected void populateGUIFromExperiment()
    {
        // prepare the GUI components based on mode
        if(internalMode == InternalMode.EDIT)
        {	
        	// populate
        	experimentName.setText(experiment.getName());
        	experimentSummary.setText(experiment.getDescription());
        	setDomainByClass(experiment.getDomain());
        	setAndConfigureStopConditionByClass(experiment.getStopCondition());
        	setRunStatisticsByClass(experiment.getRunStatistics());        	        	
        }
        else if(internalMode == InternalMode.NEW)
        {
        	clearGUI();
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
        	deleteButton.setEnabled(true);
        	clearButton.setEnabled(false);
        	experimentName.setEnabled(false);        
        	domainCombo.setEnabled(false);
        	// disable GUI items that cannot be changed it runs are defined
        	if(experiment.getRunsDefined() > 0)
        	{
        		// once runs are defined, cannot change stop condition
    			stopConditionCombo.setEnabled(false);
    			configStopConditionButton.setEnabled(false);
    			
        		// disable GUI items that cannot be changed if runs are completed
        		if(experiment.getRunsCompleted() > 0)
        		{
        			// once runs are executed, cannot change statistics
        			runStatisticsList.setEnabled(false);
        			// cannot delete
        			deleteButton.setEnabled(false);
        		}
        	}  	
        }
        else if(internalMode == InternalMode.NEW)
        {
            deleteButton.setEnabled(false); // cannot delete
            clearButton.setEnabled(true); // can clear
            // can do everything else
            experimentName.setEnabled(true);
            experimentSummary.setEnabled(true);
            domainCombo.setEnabled(true);
            stopConditionCombo.setEnabled(true);
            
            if(((StopCondition)stopConditionCombo.getSelectedItem())!=null &&
            		((StopCondition)stopConditionCombo.getSelectedItem()).isUserConfigurable())
            {
            	configStopConditionButton.setEnabled(true);
            }
            else
            {
            	configStopConditionButton.setEnabled(false);
            }
            
            runStatisticsList.setEnabled(true);
        }
    }
    
    
    /**
     * Locates and selects the run statistics provided
     * @param runStatistics
     */
    protected void setRunStatisticsByClass(RunProbe [] runStatistics)
    {      
    	int marked = 0;
    	
        // process the list and mark all selected stats
        int size = runStatisticsList.getModel().getSize();
        // clear existing selection
        runStatisticsList.clearSelection();
        // process the list
        for (int i=0; i<size; i++) 
        {
            RunProbe item = (RunProbe) runStatisticsList.getModel().getElementAt(i);
            // locate
            for (int j = 0; j < runStatistics.length; j++)
            {
                if(item.getClass().equals(runStatistics[j].getClass()))
                {
                    runStatisticsList.addSelectionInterval(i, i);
                    marked++;
                    break;
                }
            }            
        }
        
        if(marked != runStatistics.length)
        {
        	throw new GUIException("Unable to locate and mark all configured run statistics. Marked "+marked+", expected "+runStatistics.length);
        }
    }

    /**
     * Locates and configures the stop condition instance in the stop condition list
     * according to the provided stop condition
     * 
     * @param aStopCondition
     */
    protected void setAndConfigureStopConditionByClass(StopCondition aStopCondition)
    {
        boolean found = false;
        
        for (int i = 0; !found && i < stopConditionCombo.getItemCount(); i++)
		{
        	StopCondition s = (StopCondition) stopConditionCombo.getItemAt(i);     
        	
        	// compare at the class level
			if(s.getClass().equals(aStopCondition.getClass()))
			{
				// select
				stopConditionCombo.setSelectedIndex(i);
				// populate
				s.populateFromInstance(aStopCondition);
				found = true;
			}
		}
        
        if(!found)
        {
        	throw new GUIException("Unable to set stop condition configured in experiment, unknown domain class: " + aStopCondition.getClass());
        }
    }
    
    /**
     * Locates and sets the provided domain in the domain combo
     * @param aDomain
     */
    protected void setDomainByClass(Domain aDomain)
    {
        boolean found = false;
        
        for (int i = 0; !found && i < domainCombo.getItemCount(); i++)
		{
        	Domain d = (Domain) domainCombo.getItemAt(i);       
        	// compare at the class level
			if(d.getClass().equals(aDomain.getClass()))
			{
				domainCombo.setSelectedIndex(i);				
				found = true;
			}
		}
        if(!found)
        {
        	throw new GUIException("Unable to set domain configured in experiment, unknown domain class: " + aDomain.getClass());
        } 
    }
    
    /**
     * 
     */
    protected void clearGUI()
    {        
        experimentName.setText("");
        experimentSummary.setText("");
        domainCombo.setSelectedIndex(0);
        stopConditionCombo.setSelectedIndex(0);
        runStatisticsList.clearSelection();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if (src == saveButton)
        {
        	// populate
        	Experiment exp = populateExperimentFromGUI();
        	// validate
        	if(isValidExperiment(exp))
        	{
        		// store in the real experiment        		
        		BeanUtils.beanPopulate(exp, experiment);        		
        		if(internalMode == InternalMode.NEW)
        		{
        			// save
        			if(saveExperiment())
        			{
        				// we are now in edit mode
        				prepareForUpdate(experiment, homeDirectory);
        			}
        		}
        		else if(internalMode == InternalMode.EDIT)
        		{
        			// update
        			updateExperiment();
        		}
        		
        		// we are done
        		triggerFinishedEvent();
        	}
        }
        else if (src == deleteButton)
        {
        	if(deleteExperiment())
        	{
        		// we are now in new mode
        		prepareForNew(homeDirectory);
        		// we are done
        		triggerFinishedEvent();
        	}
        }
        else if (src == clearButton)
        {
            clearGUI();
        }
        else if(src == configStopConditionButton)
        {
        	Object s = stopConditionCombo.getSelectedItem();
        	if(s!=null)
        	{
        		BeanConfigurationFrame f = new BeanConfigurationFrame(null, stopConditionCombo.getSelectedItem(), "Stop Condition Configuration", null);
        		f.setVisible(true);
        	}
        }
        else if(src == domainCombo)
        {
        	populateDependantLists((Domain) domainCombo.getSelectedItem());
        }
        else if(src == stopConditionCombo)
        {
        	StopCondition s = (StopCondition) stopConditionCombo.getSelectedItem();
        	
        	if(s!=null && s.isUserConfigurable())
        	{
        		configStopConditionButton.setEnabled(true);
        	}
        	else
        	{
        		configStopConditionButton.setEnabled(false);
        	}
        }
        
    }
    
    protected void populateDependantLists(Domain d)
    {
    	LinkedList<StopCondition> sc = d.loadDomainStopConditions();
    	stopConditionCombo.removeAllItems();
    	for(StopCondition s : sc)
    	{
    		stopConditionCombo.addItem(s);
    	}
    	stopConditionCombo.setSelectedIndex(0);
    	//configStopConditionButton.setEnabled(true);
    	
    	
    	runStatisticsList.clearSelection();
    	runStatisticsListModel.removeAllElements();
    	LinkedList<RunProbe> rp = d.loadDomainRunProbes();
    	for(RunProbe p : rp)
    	{
    		runStatisticsListModel.addElement(p);
    	}
    }
    
    /**
     * Delete the experiment, if error the user is informed and false is returned
     * @return
     */
    protected boolean deleteExperiment()
    {
        try
		{
        	experiment.delete();
		} 
        catch (ExperimentException e1)
		{
        	JOptionPane.showMessageDialog(this, "Unable to delete experiment: " + e1.getMessage(), "Error Deleting Experiment", JOptionPane.ERROR_MESSAGE);
        	return false;
		}    	
        return true;
    }
    
    /**
     * Save the experiment, if error the user is informed and false is returned
     * @return
     */
    protected boolean saveExperiment()
    {
        try
		{
			experiment.save(homeDirectory);
		} 
        catch (ExperimentException e1)
		{
        	JOptionPane.showMessageDialog(this, "Unable to save experiment: " + e1.getMessage(), "Error Saving Experiment", JOptionPane.ERROR_MESSAGE);
        	return false;
		}    	
        return true;
    }
    
    /**
     * Update the experiment, if error the user is informed and false is returned
     * @return
     */
    protected boolean updateExperiment()
    {
        try
		{
        	experiment.update();
		} 
        catch (ExperimentException e1)
		{
        	JOptionPane.showMessageDialog(this, "Unable to update experiment: " + e1.getMessage(), "Error Updating Experiment", JOptionPane.ERROR_MESSAGE);
        	return false;
		}   	
        return true;
    }

    
    /**
     * Validates the populated experiment instance
     * If invalid, the user is informed and a false value is returned
     * @return
     */
    protected boolean isValidExperiment(Experiment exp)
    {
    	// validate
        try
        {
        	if(internalMode == InternalMode.NEW)
        	{
        		exp.validate(true, homeDirectory);
        	}
        	else if(internalMode == InternalMode.EDIT)
        	{
        		exp.validate();
        	}
        }
        catch(ExperimentException e)
        {
        	JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Experiment", JOptionPane.ERROR_MESSAGE);
        	return false;
        }    	
        return true;
    }
    
    /**
     * Populates the experiment instance variable with information from the GUI
     * The populated experiment is then validated according to the internal mode (new or edit)
     * 
     * @return
     */
    protected Experiment populateExperimentFromGUI()
    {
    	Experiment exp = new Experiment();
    	
    	exp.setName(experimentName.getText().trim());
    	exp.setDescription(experimentSummary.getText().trim());
    	Domain selectedDomain = (Domain) domainCombo.getSelectedItem();
    	exp.setDomain(BeanUtils.beanCopy(selectedDomain));
    	// stop condition
    	StopCondition selectedStopCondition = (StopCondition) stopConditionCombo.getSelectedItem();
    	if(selectedStopCondition.isUserConfigurable())
    	{
    		// duplicate when required
    		selectedStopCondition = BeanUtils.beanCopy(selectedStopCondition);
    	}
    	exp.setStopCondition(selectedStopCondition);
    	
    	// run statistics
    	Object [] selectedRunStatistics = runStatisticsList.getSelectedValues();
        RunProbe [] runStatisticsList = new RunProbe[selectedRunStatistics.length];
        for (int i = 0; i < runStatisticsList.length; i++)
        {        	
        	// not configurable
        	runStatisticsList[i] = (RunProbe) selectedRunStatistics[i];
        }
        exp.setRunStatistics(runStatisticsList);
        
        return exp;
    }

	public File getHomeDirectory()
	{
		return homeDirectory;
	}

	public Experiment getExperiment()
	{
		return experiment;
	}

	public InternalMode getInternalMode()
	{
		return internalMode;
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
			File home = ExperimentUtils.getDefaultHomeDirectory();
			ExperimentDetailsPanel panel = new ExperimentDetailsPanel(home);			
			GUIUtils.testJFrame(panel, "Experiment Details");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
