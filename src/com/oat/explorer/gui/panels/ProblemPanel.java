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
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.oat.Algorithm;
import com.oat.Domain;
import com.oat.Problem;
import com.oat.SolutionEvaluationListener;
import com.oat.explorer.gui.AlgorithmRunStateChangedListener;
import com.oat.explorer.gui.ProblemChangedListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;
import com.oat.gui.BeanConfigurationFrame;
import com.oat.gui.GenericTextDialog;
import com.oat.gui.GenericTreeChooser;

/**
 * Type: ProblemPanel<br/>
 * Date: 24/11/2006<br/>
 * <br/>
 * Description:
 * 
 * Displays details of a selected problem, provides facility to choose a specific problem instance
 * and notifys all listeners each time a problem changes
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * JBrownlee    06-07-2007  Added an about and configure button for problem instances
 *                          Removed problem details
 * </pre>
 */
public abstract class ProblemPanel extends JPanel
    implements ActionListener, AlgorithmRunStateChangedListener, ProblemChangedListener, TreeSelectionListener
{
    protected final LinkedList<ProblemChangedListener> problemChangedListeners;
    
    protected final Frame parent;
    protected final Domain domain;
    
    protected Problem [] problemInstanceList;
    protected Problem selected;
        
    protected GenericTreeChooser chooser;
    protected JButton chooseButton;
    protected JButton configButton;
    protected JButton aboutButton;
    
    protected GenericProblemPlot plotPanel;
    protected StopConditionControlPanel stopConditionPanel;
    
    protected JTextField pField;
    
    public ProblemPanel(Frame aParent, Domain aDomain)
    {
        parent = aParent;
        domain = aDomain;
        problemChangedListeners = new LinkedList<ProblemChangedListener>();
        setName("Problem Domain");
        prepareGUI();
    }
    
    protected void prepareGUI()
    {
        registerProblemChangedListener(this);
        // load the problem instances
        loadProblemInstances();
        
        // prepare the problem chooser
        JPanel chooserPanel = prepareChooserPanel();
        // prepare the problem plot
        plotPanel = prepareProblemPlot();
        if(plotPanel!=null)
        {
            registerProblemChangedListener(plotPanel);
        }
        
        stopConditionPanel = new StopConditionControlPanel(domain);
        
        // build the gui
        JPanel top = new JPanel(new BorderLayout());
        top.add(chooserPanel, BorderLayout.NORTH);
        top.add(stopConditionPanel, BorderLayout.CENTER);
        
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        if(plotPanel != null)
        {
            add(plotPanel, BorderLayout.CENTER);
        }
        
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), getName()));
    }
    
    protected JPanel prepareChooserPanel()
    {        
        chooseButton = new JButton("Choose");
        chooseButton.addActionListener(this);
        chooseButton.setToolTipText("Select the problem instance for the next run");
        chooser = new GenericTreeChooser(problemInstanceList, getProblemBase());
        chooser.getTree().addTreeSelectionListener(this);        
        pField = new JTextField("", 15);
        pField.setEditable(false);
        pField.setBackground(Color.WHITE);
      
        configButton = new JButton("Config");
        configButton.addActionListener(this);
        configButton.setToolTipText("Configure the problem instance");
        aboutButton = new JButton("About");
        aboutButton.addActionListener(this);
        aboutButton.setToolTipText("Information about the problem instance");        
                
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        JPanel p = new JPanel(gbl); 
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        gbl.setConstraints(chooseButton, c);
        p.add(chooseButton, c); 
        
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0;        
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(pField, c);
        p.add(pField, c);  
        
        JPanel b = new JPanel();
        b.add(configButton);
        b.add(aboutButton);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;        
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(b, c);
        p.add(b, c);  
        
        return p;
    }
    
    protected abstract String getProblemBase();
    
    public void setRandomProblemInstance(Random r)
    {
        if(problemInstanceList!=null)
        {
            chooser.setSelection(problemInstanceList[r.nextInt(problemInstanceList.length)]);
        }
    }
    
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        if(src == chooseButton)
        {            
            chooser.show(chooseButton, chooseButton.getX(), chooseButton.getY());
        }
        else if(src == configButton)
        {
            Problem p = getCurrentlySelectedProblem();
            if(p.isUserConfigurable())
            {
                BeanConfigurationFrame f = new BeanConfigurationFrame(parent, p, "Problem Configuration", null);
                f.setVisible(true);
            }

            // configuration may have changed, trigger a problem change event
            problemChanged(selected);
        }
        else if(src == aboutButton)
        {
            new GenericTextDialog(getCurrentlySelectedProblem().getDetails()).setVisible(true);
        }
    }
    
    public void loadProblemInstances()
    {
        // load the problems
        String errorMsg = "";
        try
        {
            problemInstanceList = domain.loadProblemList();
            System.err.println(getClass().getName() + " > loaded " + problemInstanceList.length + " problems.");
        }
        catch(Exception e)
        {
            errorMsg = e.getMessage();
            throw new RuntimeException("There was an error preparing problem instances.", e);
        }
        finally
        {
            if(problemInstanceList == null || problemInstanceList.length == 0)
            {
                problemInstanceList = null;
                JOptionPane.showMessageDialog(this, "There was an error preparing problem instances.\n"+errorMsg, "Error Preparing Problems", JOptionPane.ERROR_MESSAGE);
            }
        }        
    }    
    
    public void registerProblemChangedListener(ProblemChangedListener a)
    {
        problemChangedListeners.add(a);
    }
    
    public void problemChanged(Problem prob)
    {
        for(ProblemChangedListener p : problemChangedListeners)
        {
            p.problemChangedEvent(prob);
        }
    }  
    
    public Problem getCurrentlySelectedProblem()
    {
        if(problemInstanceList==null)
        {
            return null;
        }
        
        return selected;
    }
    
    
    protected abstract GenericProblemPlot prepareProblemPlot();
    
    
    
    public void algorithmFinishNotify(Problem p, Algorithm a)
    {
        chooseButton.setEnabled(true);
        configButton.setEnabled(p.isUserConfigurable());
    }
    
    public void algorithmStartNotify(Problem p, Algorithm a)
    {
        chooseButton.setEnabled(false);
        configButton.setEnabled(false);

    }

    public void problemChangedEvent(Problem p)
    {
        // enable/disable the config button
        configButton.setEnabled(p.isUserConfigurable());
        
        pField.setText(p.getName());
       // could be used for dealing with problem configuration windows
        
    }   
    
    public void registerNewSolutionListener(SolutionEvaluationListener l)
    {
        for(Problem p : problemInstanceList)
        {
            p.addListener(l);
        }
    }

    
    public void valueChanged(TreeSelectionEvent e)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) chooser.tree.getLastSelectedPathComponent();

        if (node == null)
        {
            return;
        }
        if (node.isLeaf())
        {
            GenericTreeChooser.Entry entry = (GenericTreeChooser.Entry) node.getUserObject();
            selected = (Problem)entry.object;
            problemChanged(selected);
            chooser.setVisible(false);
        }
    }

	public StopConditionControlPanel getStopConditionPanel()
	{
		return stopConditionPanel;
	}
}

