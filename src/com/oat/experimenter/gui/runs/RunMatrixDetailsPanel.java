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
package com.oat.experimenter.gui.runs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.oat.Algorithm;
import com.oat.Configurable;
import com.oat.Problem;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.gui.BeanConfigurationFrame;
import com.oat.gui.FinishedEventNotifier;
import com.oat.gui.FinishedNotificationEventListener;
import com.oat.gui.GUIException;
import com.oat.utils.GUIUtils;

/**
 * Description: Manage a run matrix, including creation, saving, editing and deleting 
 * 
 * Create a new matrix for an experiment
 * View a matrix for an experiment
 * Save a matrix for an experiment
 * 
 * Add/Remove/Config problems and algorithms
 * Set the number of repeats
 *  
 * Date: 28/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class RunMatrixDetailsPanel extends JPanel
	implements ActionListener, FinishedEventNotifier, ListSelectionListener
{		
	protected JTextField repeatsField;
	
	protected JList algorithmList;
	protected DefaultListModel algorithmListModel;
	protected JComboBox algorithmsCombobox;
	protected JButton addAlgorithmButton;
	protected JButton removeAlgorithmButton;
	protected JButton configAlgorithmButton;
	
	protected JList problemList;
	protected DefaultListModel problemListModel;
	protected JComboBox problemsCombobox;
	protected JButton addProblemButton;
	protected JButton removeProblemButton;
	protected JButton configProblemButton;
	
	protected JButton saveScheduleButton;
	protected JButton clearScheduleButton;
	
	
	protected Experiment experiment;
	protected ExperimentalRunMatrix runMatrix;
	
	public RunMatrixDetailsPanel()
	{
		createGUI();
	}
	
	
	protected void createGUI()
	{
		// repeats
		repeatsField = new JTextField(5);
		// algorithms
		algorithmListModel = new DefaultListModel();
		algorithmList = new JList(algorithmListModel);		
		algorithmList.setVisibleRowCount(5);
		algorithmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		algorithmsCombobox = new JComboBox();
		addAlgorithmButton = new JButton("Add");
		removeAlgorithmButton = new JButton("Remove");
		configAlgorithmButton = new JButton("Config");
		addAlgorithmButton.addActionListener(this);
		removeAlgorithmButton.addActionListener(this);
		configAlgorithmButton.addActionListener(this);
		configAlgorithmButton.setEnabled(false);
		algorithmList.addListSelectionListener(this);
		// problems
		problemListModel = new DefaultListModel();
		problemList = new JList(problemListModel);
		problemList.setVisibleRowCount(5);
		problemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		problemsCombobox = new JComboBox();
		addProblemButton = new JButton("Add");
		removeProblemButton = new JButton("Remove");
		configProblemButton = new JButton("Config");
		addProblemButton.addActionListener(this);
		removeProblemButton.addActionListener(this);
		configProblemButton.addActionListener(this);
		configProblemButton.setEnabled(false);
		problemList.addListSelectionListener(this);
		// controls
		saveScheduleButton = new JButton("Save");
		clearScheduleButton = new JButton("Clear");
		saveScheduleButton.addActionListener(this);
		clearScheduleButton.addActionListener(this);
		
        // gridbag
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        JPanel p = new JPanel(gbl);
        
        // repeats
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        JLabel jl = new JLabel("Repeats:");
        gbl.setConstraints(jl, c);
        p.add(jl, c);  
        
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 0.5;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(repeatsField, c);
        p.add(repeatsField, c);  
        
        // algorithms
        JPanel algorithmsPanel = createAlgorithmsPanel();  
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.gridheight = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(algorithmsPanel, c);
        p.add(algorithmsPanel, c);  
        
        // problem
        JPanel problemPanel = createProblemsPanel();  
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 4;
        c.gridheight = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(problemPanel, c);
        p.add(problemPanel, c);  
        
        // controls
        JPanel controls = new JPanel();        
        controls.add(saveScheduleButton);
        controls.add(clearScheduleButton);
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 4;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(controls, c);
        p.add(controls, c);         
        
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Run Matrix Details"));
	}
	
	
	public JPanel createAlgorithmsPanel()
	{
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        JPanel p = new JPanel(gbl);
		
        // add algorithm
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        JLabel aal = new JLabel("Add Algorithms:");
        gbl.setConstraints(aal, c);
        p.add(aal, c); 
        // combo
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(algorithmsCombobox, c);
        p.add(algorithmsCombobox, c); 
        // add button
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(addAlgorithmButton, c);
        p.add(addAlgorithmButton, c); 
        // run algorithms
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        JLabel ral = new JLabel("Run Algorithms:");
        gbl.setConstraints(ral, c);
        p.add(ral, c); 
        // main list
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane jsp = new JScrollPane(algorithmList);
        gbl.setConstraints(jsp, c);
        p.add(jsp, c); 
        // config
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(configAlgorithmButton, c);
        p.add(configAlgorithmButton, c); 
        // remove
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(removeAlgorithmButton, c);
        p.add(removeAlgorithmButton, c);              

        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Algorithms"));		
		return p;
	}
	
	
	public JPanel createProblemsPanel()
	{        
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        JPanel p = new JPanel(gbl);
		
        // add problem
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;        
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        JLabel ap = new JLabel("Add Problems:");
        gbl.setConstraints(ap, c);
        p.add(ap, c); 
        // combo
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(problemsCombobox, c);
        p.add(problemsCombobox, c); 
        // add button
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(addProblemButton, c);
        p.add(addProblemButton, c); 
        // run problems
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        JLabel rpl = new JLabel("Run Problems:");
        gbl.setConstraints(rpl, c);
        p.add(rpl, c); 
        // main list
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane pljsp = new JScrollPane(problemList);
        gbl.setConstraints(pljsp, c);
        p.add(pljsp, c); 
        // config
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(configProblemButton, c);
        p.add(configProblemButton, c); 
        // remove
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(removeProblemButton, c);
        p.add(removeProblemButton, c);              

        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Problem"));		
		return p;
	}

	
	public void populateGUIFromRunSchedule(Experiment aExperiment, ExperimentalRunMatrix aMatrix)
	{
		// clear the GUI
		clear();
		// store things
		experiment = aExperiment;
		runMatrix = aMatrix;
		// repeats
		repeatsField.setText(""+aMatrix.getRepeats());
		
		
		// algorithms
		LinkedList<Algorithm> configuredAlgorithms = aMatrix.getAlgorithms();
		Collections.sort(configuredAlgorithms);
		for(Algorithm a : configuredAlgorithms)
		{
			algorithmListModel.addElement(a);
		}
        Algorithm [] algorithms = null;
        try
		{
        	algorithms = experiment.getDomain().loadAlgorithmList();
        	Arrays.sort(algorithms);
		}
		catch (Exception e)
		{
			throw new GUIException("Unable to load algorithms for experiment domain: " + e.getMessage(), e);
		}
        for (int i = 0; i < algorithms.length; i++)
        {
        	// only add algorithms that are not selected
        	boolean canAdd = true;
        	for (int j = 0; canAdd && j < configuredAlgorithms.size(); j++)
			{
				if(configuredAlgorithms.get(j).getClass().equals(algorithms[i].getClass())
						&& configuredAlgorithms.get(j).getName().equals(algorithms[i].getName()))
				{
					canAdd = false;
				}
			}
        	
        	if(canAdd)
        	{
        		algorithmsCombobox.addItem(algorithms[i]);
        	}
        }
        
        
        // problems
		LinkedList<Problem> configuredProblems = aMatrix.getProblems();
		Collections.sort(configuredProblems);
		for(Problem p : configuredProblems)
		{
			problemListModel.addElement(p);
		}
        Problem [] problems = null;
        try
		{
        	problems = experiment.getDomain().loadProblemList();
        	Arrays.sort(problems);
		}
		catch (Exception e)
		{
			throw new GUIException("Unable to load problems for experiment domain: " + e.getMessage(), e);
		}
        for (int i = 0; i < problems.length; i++)
        {
        	// only add algorithms that are not selected
        	boolean canAdd = true;
        	for (int j = 0; canAdd && j < configuredProblems.size(); j++)
			{
				if(configuredProblems.get(j).getClass().equals(problems[i].getClass()) 
						&& configuredProblems.get(j).getName().equals(problems[i].getName()))
				{
					canAdd = false;
				}
			}
        	
        	if(canAdd)
        	{
        		problemsCombobox.addItem(problems[i]);
        	}   
        }
	}
	
	public void clear()
	{
		// repeats
		repeatsField.setText("");
		// algorithms
		if(algorithmListModel.capacity()>0)
		{
			algorithmList.clearSelection();
			algorithmListModel.clear();		
		}
		if(algorithmsCombobox.getModel().getSize()>0)
		{
			algorithmsCombobox.removeAllItems();
		}
		// problems
		if(problemListModel.capacity()>0)
		{
			problemList.clearSelection();
			problemListModel.clear();	
		}
		if(problemsCombobox.getModel().getSize()>0)
		{
			problemsCombobox.removeAllItems();
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		// algorithms
		if(src == addAlgorithmButton)
		{
			addAlgorithm();
		}
		else if(src == removeAlgorithmButton)
		{
			removeAlgorithm();
		}
		else if(src == configAlgorithmButton)
		{
			configAlgorithm();
		}		
		// problems
		if(src == addProblemButton)
		{
			addProblem();
		}
		else if(src == removeProblemButton)
		{
			removeProblem();
		}
		else if(src == configProblemButton)
		{
			configProblem();
		}
		// controls
		else if(src == saveScheduleButton)
		{
			saveMatrix();
		}
		else if(src == clearScheduleButton)
		{
			clearMatrix();
		}		
	}
	
	protected boolean validateGUIMatrix()
	{
		int repeats = 0;
		try
		{
			repeats = Integer.parseInt(repeatsField.getText());
		}
		catch(NumberFormatException e)
		{
        	JOptionPane.showMessageDialog(this, "Invalid repeats: "+e.getMessage(), "Invalid Run Matrix", JOptionPane.ERROR_MESSAGE);
        	return false;
		}
		if(algorithmListModel.size()==0)
		{
        	JOptionPane.showMessageDialog(this, "Run matrix must contain at least one algorithm", "Invalid Run Matrix", JOptionPane.ERROR_MESSAGE);
        	return false;
		}
		else if(problemListModel.size()==0)
		{
        	JOptionPane.showMessageDialog(this, "Run matrix must contain at least one problem", "Invalid Run Matrix", JOptionPane.ERROR_MESSAGE);
        	return false;
		}
		
		return true;
	}
	
	protected void saveMatrix()
	{
		// must be valid
		if(!validateGUIMatrix())
		{
			return;
		}		
		// clear out the current matrix
		runMatrix.clear();		
		// add repeats
		runMatrix.setRepeats(Integer.parseInt(repeatsField.getText()));
		// add algorithms
		for (int i = 0; i < algorithmListModel.size(); i++)
		{
			runMatrix.addAlgorithm((Algorithm)algorithmListModel.get(i));
		}
		// add problems
		for (int i = 0; i < problemListModel.size(); i++)
		{
			runMatrix.addProblem((Problem)problemListModel.get(i));
		}
		// delete the current run schedule
		try
		{
			ExperimentalRunUtils.deleteAllRuns(experiment);
		}
		catch (ExperimentException e)
		{
			throw new GUIException("Error deleting all runs: " + e.getMessage(), e);
		}
		// store the new run schedule		
		try
		{
			runMatrix.toRunListAndAddToExperiment(experiment);
		}
		catch (Exception e)
		{
			throw new GUIException("Error creating runs: " + e.getMessage(), e);
		}
		// store the new schedule
		try
		{
			ExperimentalRunUtils.externaliseEntireRunSchedule(experiment);
		}
		catch (ExperimentException e)
		{
			throw new GUIException("Error externalizing run schedule: " + e.getMessage(), e);
		}		
		
		JOptionPane.showMessageDialog(this, "Successfully saved the run schedule.", "Run Schedule Saved", JOptionPane.INFORMATION_MESSAGE);
		// we are done
		triggerFinishedEvent();
	}
	
	
	protected void clearMatrix()
	{
		// repeats
		repeatsField.setText(""+ExperimentUtils.DEFAULT_REPEATS);
		// algorithms
		while(algorithmListModel.size()>0)
		{
			// take from list
			Object o = algorithmListModel.remove(0);
			// add to combo
			algorithmsCombobox.addItem(o);
		}
		// problems
		while(problemListModel.size()>0)
		{
			// take from list
			Object o = problemListModel.remove(0);
			// add to combo
			problemsCombobox.addItem(o);			
		}
	}
	
	
	protected void addAlgorithm()
	{
		// get selected algorithm		
		Algorithm selectedAlgorithm = (Algorithm) algorithmsCombobox.getSelectedItem();
		if(selectedAlgorithm != null)
		{		
			// remove from combo
			algorithmsCombobox.removeItem(selectedAlgorithm);
			// add to selected list
			algorithmListModel.addElement(selectedAlgorithm);
		}
	}
	protected void removeAlgorithm()
	{
		// get selected algorithm		
		Algorithm selectedAlgorithm = (Algorithm) algorithmList.getSelectedValue();
		if(selectedAlgorithm != null)
		{		
			// remove from list
			algorithmListModel.removeElement(selectedAlgorithm);
			// add to the combo box
			algorithmsCombobox.addItem(selectedAlgorithm);
		}
	}
	protected void configAlgorithm()
	{
		// get selected algorithm		
		Algorithm selectedAlgorithm = (Algorithm) algorithmList.getSelectedValue();
		if(selectedAlgorithm != null)
		{		
    		BeanConfigurationFrame f = new BeanConfigurationFrame(null, selectedAlgorithm, "Algorithm Configuration", new String[]{"AutomaticallyConfigure","Seed"});
    		f.setVisible(true);
		}
	}

	
	protected void addProblem()
	{
		// get selected problem		
		Problem selectedProblem = (Problem) problemsCombobox.getSelectedItem();
		if(selectedProblem != null)
		{		
			// remove from combo
			problemsCombobox.removeItem(selectedProblem);
			// add to selected list
			problemListModel.addElement(selectedProblem);
		}
	}
	protected void removeProblem()
	{
		// get selected problem		
		Problem selectedProblem = (Problem) problemList.getSelectedValue();
		if(selectedProblem != null)
		{		
			// remove from list
			problemListModel.removeElement(selectedProblem);
			// add to the combo box
			problemsCombobox.addItem(selectedProblem);
		}
	}
	protected void configProblem()
	{
		// get selected algorithm		
		Problem selectedProblem = (Problem) problemList.getSelectedValue();
		if(selectedProblem != null)
		{		
			BeanConfigurationFrame f = new BeanConfigurationFrame(null, selectedProblem, "Problem Configuration", null);
    		f.setVisible(true);
		}
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
	
	

	
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Object src = e.getSource();
		
		if(src == algorithmList)
		{
			Configurable f = (Configurable) algorithmList.getSelectedValue();
			if(f != null && f.isUserConfigurable())
			{
				configAlgorithmButton.setEnabled(true);
			}
			else
			{
				configAlgorithmButton.setEnabled(false);
			}
		}		
		else if(src == problemList)
		{
			Configurable f = (Configurable) problemList.getSelectedValue();
			if(f != null && f.isUserConfigurable())
			{
				configProblemButton.setEnabled(true);
			}
			else
			{
				configProblemButton.setEnabled(false);
			}
		}
	}


	public static void main(String[] args)
	{
		try
		{
			// home
			File home = ExperimentUtils.getDefaultHomeDirectory();
			// load all experiments
			Experiment [] experiments = ExperimentUtils.loadExperiments(home);	
			Experiment experiment = experiments[0];
			ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
			matrix.populateFromRunList(experiment.getRuns());
			
			// list runs for the first experiment
			RunMatrixDetailsPanel panel = new RunMatrixDetailsPanel();			
			// populate
			panel.populateGUIFromRunSchedule(experiment,  matrix);	

			GUIUtils.testJFrame(panel, "Matrix Details");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
}
