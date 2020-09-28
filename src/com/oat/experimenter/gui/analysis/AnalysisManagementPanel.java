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
package com.oat.experimenter.gui.analysis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.oat.RunProbe;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.experimenter.RunResult;
import com.oat.experimenter.gui.plots.BoxPlot;
import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.StatisticUtilities;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.experimenter.stats.normality.NormalityTest;
import com.oat.gui.GUIException;
import com.oat.utils.BeanUtils;
import com.oat.utils.GUIUtils;

/**
 * Description: Main analysis panel for analyzing run results
 *  
 * Date: 29/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AnalysisManagementPanel extends JPanel
{	
	/**
	 * Current experiment being worked on
	 */
	protected Experiment experiment;	
	
	// list
	protected AnalysisRunList runList;	
	// panels
	protected RawResultPanel rawPanel;	
	protected RunSummaryReportPanel runSummaryReportPanel;
	protected ResultSummaryPanel resultSummaryPanel;
	protected NormalitySummaryPanel normalitySummaryPanel;
	protected BoxplotPanel boxplotPanel;
	protected RunComparisonSummaryReportPanel runComparisonReportPanel;
	protected PairwiseSummaryPanel pairwiseSummary;
	
	                                 
	public AnalysisManagementPanel()
	{
		createGUI();
	}
	
	public AnalysisManagementPanel(Experiment aExperiment)
	{
		this();
		populateWithExperiment(aExperiment);
	}
	
	protected void createGUI()
	{
		// list
		runList = new AnalysisRunList();		
		// raw
		rawPanel = new RawResultPanel();
		runSummaryReportPanel = new RunSummaryReportPanel();
		normalitySummaryPanel = new NormalitySummaryPanel();
		boxplotPanel = new BoxplotPanel();
		runComparisonReportPanel = new RunComparisonSummaryReportPanel();		
		resultSummaryPanel = new ResultSummaryPanel();
		pairwiseSummary = new PairwiseSummaryPanel();
		// tabbs
		JTabbedPane jtp = new JTabbedPane();		
		jtp.add("Raw", rawPanel);
		jtp.add("Summary Reports", runSummaryReportPanel);
		jtp.add("Means Summary", resultSummaryPanel);
		jtp.add("Normality Summary", normalitySummaryPanel);
		jtp.add("Boxplots", boxplotPanel);
		jtp.add("Comparison Reports", runComparisonReportPanel);
		jtp.add("Pairwise Test", pairwiseSummary);
		// split pane
		JSplitPane vpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jtp, runList);
		vpane.setContinuousLayout(false);
		vpane.setDividerLocation(400);		
		vpane.setResizeWeight(1); // top gets all the extra space
		// add to master
		setLayout(new BorderLayout());
		add(vpane, BorderLayout.CENTER);
	}
	
	public void populateWithExperiment(Experiment aExperiment)
	{
		// clear
		clear();
		// store
		experiment = aExperiment;
		// populate
		runList.populateWithExperiment(experiment);
		runSummaryReportPanel.populate(experiment);
		boxplotPanel.populate(experiment);
		runComparisonReportPanel.populate(experiment);
		resultSummaryPanel.populate(experiment);
		normalitySummaryPanel.populate(experiment);
		pairwiseSummary.populate(experiment);
	}		
	
	public void clear()
	{
		rawPanel.clear();
		runSummaryReportPanel.clear();		
		boxplotPanel.clear();
		runComparisonReportPanel.clear();
		resultSummaryPanel.clear();
		normalitySummaryPanel.clear();
		pairwiseSummary.clear();
	}
	
		
	public RunResult [] loadRunResult(ExperimentalRun run)
	{
		if(!run.isCompleted())
		{
			JOptionPane.showMessageDialog(this, "Unable to load results, selected run is not executed: " +run.getId(), "Invalid Selection", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		
		try
		{
			return ExperimentalRunUtils.loadRunResult(experiment, run);
		}
		catch(ExperimentException e)
		{
			JOptionPane.showMessageDialog(this, "Unable to load results, an error occured for " +run.getId() + ": " + e.getMessage(), "Error Loading Result", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	public RunResult [][] loadRunResult(ExperimentalRun [] runs)
	{		
		// ensure they are all completed
		for(ExperimentalRun run : runs)
		{
			if(!run.isCompleted())
			{
				JOptionPane.showMessageDialog(this, "Selected set contains a run that is not completed: " +run.getId(), "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		
		// load results
		RunResult [][] runResults = new RunResult[runs.length][];
		for (int i = 0; i < runResults.length; i++)
		{
			try
			{
				runResults[i] = ExperimentalRunUtils.loadRunResult(experiment, runs[i]);
			}
			catch(ExperimentException e)
			{
				JOptionPane.showMessageDialog(this, "Unable to load results, an error occured for " +runs[i].getId() + ": " + e.getMessage(), "Error Loading Result", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		return runResults;
	}
	
	protected class RawResultPanel extends JPanel
		implements ActionListener
	{
		protected AnalysisRawResultPanel rawResultPanel;
		protected JButton displayButton;
		
		public RawResultPanel()
		{
			rawResultPanel = new AnalysisRawResultPanel();
			displayButton = new JButton("Display Raw Result");
			displayButton.addActionListener(this);			
			rawResultPanel.getControlPanel().add(displayButton);
			setLayout(new BorderLayout());
			add(rawResultPanel, BorderLayout.CENTER);			
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == displayButton)
			{
				ExperimentalRun [] runs = runList.getSelectedRuns();
				if(runs==null || runs.length>1)
				{
					JOptionPane.showMessageDialog(this, "Please select a single experimental run to dispaly.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					RunResult [] result = loadRunResult(runs[0]);
					if(result != null)
					{
						rawResultPanel.populateWithData(result);
					}
				}
			}			
		}
		public void clear()
		{
			rawResultPanel.clear();
		}
	}
	
	
	protected class BoxplotPanel extends JPanel
		implements ActionListener
	{
		protected BoxPlot boxplot;
		protected JButton displayButton;
		protected JComboBox statisticsList;
		
		protected JRadioButton byAlgorithmRadio;
		protected JRadioButton byProblemRadio;		
		
		public BoxplotPanel()
		{
			boxplot = new BoxPlot();
			statisticsList = new JComboBox();
			displayButton = new JButton("Display");
			displayButton.addActionListener(this);
			
			byAlgorithmRadio = new JRadioButton("By Algorithm");
			byAlgorithmRadio.setSelected(true);
			byAlgorithmRadio.addActionListener(this);
			byProblemRadio = new JRadioButton("By Problem");
			byProblemRadio.addActionListener(this);
			ButtonGroup group = new ButtonGroup();
		    group.add(byAlgorithmRadio);
		    group.add(byProblemRadio);		    
			
			JPanel p = new JPanel();
			p.add(byAlgorithmRadio);
			p.add(byProblemRadio);
			p.add(statisticsList);
			p.add(displayButton);
			
			setLayout(new BorderLayout());
			add(boxplot, BorderLayout.CENTER);
			add(p, BorderLayout.SOUTH);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == byAlgorithmRadio)
			{
				boxplot.setGroupByAlgorithm(true);
			}
			else if(src == byProblemRadio)
			{
				boxplot.setGroupByAlgorithm(false);
			}
			else if(src == displayButton)
			{
				ExperimentalRun [] runs = runList.getSelectedRuns();				
				
				if(runs==null || runs.length<1)
				{
					JOptionPane.showMessageDialog(this, "Please select a set of experimental runs to dispaly.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					RunResult [][] results = loadRunResult(runs);
					RunProbe stat = (RunProbe) statisticsList.getSelectedItem();
					// populate the boxplot
					if(results != null)
					{
						boxplot.clear();
						for (int i = 0; i < results.length; i++)
						{
							RunStatisticSummary summary = new RunStatisticSummary();							
							try
							{
								summary.calculate(runs[i], results[i], stat.getName());
							}
							catch (AnalysisException e1)
							{
								JOptionPane.showMessageDialog(this, "Error preparing statistics: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							boxplot.addBoxAndWhiskerItem(summary.getRawResults(), runs[i].getAlgorithm().getName(), runs[i].getProblem().getName());
						}
					}
				}
			}
		}
		
		public void clear()
		{
			boxplot.clear();
			statisticsList.removeAllItems();
		}
		
		public void populate(Experiment e)
		{
			RunProbe [] stats = e.getRunStatistics();
			for (int i = 0; i < stats.length; i++)
			{
				statisticsList.addItem(stats[i]);
			}
		}
	}
	
	protected class RunSummaryReportPanel extends JPanel
		implements ActionListener
	{	
		protected ReportLogPanel logPanel;
		protected JComboBox statisticsList;
		protected JButton displayButton;
		
		public RunSummaryReportPanel()
		{
			logPanel = new ReportLogPanel();
			statisticsList = new JComboBox();
			displayButton = new JButton("Display");
			displayButton.addActionListener(this);
			
			JPanel p = new JPanel();
			p.add(statisticsList);
			p.add(displayButton);
			
			setLayout(new BorderLayout());
			add(logPanel, BorderLayout.CENTER);
			add(p, BorderLayout.SOUTH);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == displayButton)
			{
				ExperimentalRun [] runs = runList.getSelectedRuns();				
				
				if(runs==null || runs.length>1)
				{
					JOptionPane.showMessageDialog(this, "Please select a single experimental run to dispaly.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					RunProbe stat = (RunProbe) statisticsList.getSelectedItem();
					logPanel.clear();
					reportRun(runs[0], stat);
				}
			}
		}
		
		public void reportRun(ExperimentalRun run, RunProbe stat)
		{
			// load results
			RunResult [] result = loadRunResult(run);
			if(result == null)
			{
				// sum of the runs were not completed
				return;
			}
			
			// // calculate statistics
			RunStatisticSummary summary = new RunStatisticSummary();
			
			try
			{
				summary.calculate(run, result, stat.getName());
			}
			catch (AnalysisException e1)
			{
				JOptionPane.showMessageDialog(this, "Error preparing statistics: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			logPanel.reportAppendLine("Run Summary Statistics: " + stat.getName());
			logPanel.reportAppendLine("");
			logPanel.reportAppend(summary);
			// normality
			NormalityTest [] normalityTests = StatisticUtilities.loadNormalityTests();
			for (int i = 0; i < normalityTests.length; i++)
			{
				logPanel.reportAppendLine("");
				// calculate
				try
				{
					normalityTests[i].evaluate(summary);
					// report					
					logPanel.reportAppend(normalityTests[i]);
				}
				catch (AnalysisException e)
				{
					logPanel.reportAppendLine("Error calculating normality test: " + e.getMessage());
					return;
				}
			}
			logPanel.scrollToTop();
		}
		
		
		public void populate(Experiment e)
		{
			RunProbe [] stats = e.getRunStatistics();
			for (int i = 0; i < stats.length; i++)
			{
				statisticsList.addItem(stats[i]);
			}
		}
		
		public void clear()
		{
			logPanel.clear();
			statisticsList.removeAllItems();
		}
	}
	
	
	
	
	protected class RunComparisonSummaryReportPanel extends JPanel
		implements ActionListener
	{			
		protected ReportLogPanel logPanel;
		protected JComboBox statisticsList;
		protected JButton displayButton;
		protected JComboBox comparisonTestList;
		
		public RunComparisonSummaryReportPanel()
		{
			logPanel = new ReportLogPanel();
			statisticsList = new JComboBox();
			comparisonTestList = new JComboBox(StatisticUtilities.loadStatisticalComparisonTests());
			displayButton = new JButton("Display");
			displayButton.addActionListener(this);
			
			JPanel p = new JPanel();
			p.add(statisticsList);
			p.add(comparisonTestList);
			p.add(displayButton);			
			
			setLayout(new BorderLayout());
			add(logPanel, BorderLayout.CENTER);
			add(p, BorderLayout.SOUTH);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == displayButton)
			{
				ExperimentalRun [] runs = runList.getSelectedRuns();				
				
				if(runs==null || runs.length<2)
				{
					JOptionPane.showMessageDialog(this, "Please select a two or more runs to compare.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					RunProbe stat = (RunProbe) statisticsList.getSelectedItem();
					StatisticalComparisonTest comparisonTest = (StatisticalComparisonTest) comparisonTestList.getSelectedItem();
					comparisonTest = BeanUtils.beanCopy(comparisonTest);	
					// ensure the test is suitable for the selected runs
					if(runs.length==2 && !comparisonTest.supportsTwoPopulations())
					{
						JOptionPane.showMessageDialog(this, "Please select a statistical test that supports two populations.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
					}
					else if(runs.length>2 && !comparisonTest.supportsNPopulations())
					{
						JOptionPane.showMessageDialog(this, "Please select a statistical test that supports >2 populations.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
					}
					else
					{
						logPanel.clear();
						reportComparisonRun(runs, stat, comparisonTest);
					}
				}
			}
		}
		
		public void reportComparisonRun(
				ExperimentalRun [] runs, 
				RunProbe stat, 
				StatisticalComparisonTest comparisonTest)
		{
			// load results
			RunResult [][] result = loadRunResult(runs);
			if(result != null)
			{
				// // calculate statistics
				RunStatisticSummary [] summaries = new RunStatisticSummary[runs.length];
				for (int i = 0; i < summaries.length; i++)
				{
					summaries[i] = new RunStatisticSummary();
					try
					{
						summaries[i].calculate(runs[i], result[i], stat.getName());
					}
					catch (AnalysisException e1)
					{
						JOptionPane.showMessageDialog(this, "Error preparing statistics: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				logPanel.reportAppendLine("Run Summary Statistics: " + stat.getName());
				logPanel.reportAppendLine("");
				// perform the test			
				try
				{
					comparisonTest.evaluate(summaries);
					// report					
					logPanel.reportAppend(comparisonTest);
				}
				catch (AnalysisException e)
				{
					logPanel.reportAppendLine("Error calculating comparison test: " + e.getMessage());
				}
				
				logPanel.scrollToTop();	
			}
		}
		
		
		public void populate(Experiment e)
		{
			RunProbe [] stats = e.getRunStatistics();
			for (int i = 0; i < stats.length; i++)
			{
				statisticsList.addItem(stats[i]);
			}
		}
		
		public void clear()
		{
			logPanel.clear();
			statisticsList.removeAllItems();
		}
	}
	
	
	protected class ResultSummaryPanel extends JPanel
		implements ActionListener
	{
		protected JComboBox statisticsList;
		protected ResultSummaryTablePanel summaryPanel;
		protected JButton displayButton;
		
		protected JRadioButton byAlgorithmRadio;
		protected JRadioButton byProblemRadio;	
		
		public ResultSummaryPanel()
		{
			summaryPanel = new ResultSummaryTablePanel();
			statisticsList = new JComboBox();			
			displayButton = new JButton("Display");
			displayButton.addActionListener(this);
			
			byAlgorithmRadio = new JRadioButton("By Algorithm");
			byAlgorithmRadio.setSelected(true);
			byAlgorithmRadio.addActionListener(this);
			byProblemRadio = new JRadioButton("By Problem");
			byProblemRadio.addActionListener(this);
			ButtonGroup group = new ButtonGroup();
		    group.add(byAlgorithmRadio);
		    group.add(byProblemRadio);	
			
			JPanel p = new JPanel();
			p.add(byAlgorithmRadio);
			p.add(byProblemRadio);
			p.add(statisticsList);			
			p.add(displayButton);			
			
			setLayout(new BorderLayout());
			add(summaryPanel, BorderLayout.CENTER);
			add(p, BorderLayout.SOUTH);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == byAlgorithmRadio)
			{
				summaryPanel.setByAlgorithm(true);
			}
			else if(src == byProblemRadio)
			{
				summaryPanel.setByAlgorithm(false);
			}
			else if(src == displayButton)
			{
				ExperimentalRun [] runs = runList.getSelectedRuns();				
				
				if(runs==null || runs.length<2)
				{
					JOptionPane.showMessageDialog(this, "Please select a two or more runs to display.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					summaryPanel.clear();
					RunProbe stat = (RunProbe) statisticsList.getSelectedItem();
					RunResult [][] result = loadRunResult(runs);
					if(result != null)
					{
						// calculate statistics
						RunStatisticSummary [] summaries = new RunStatisticSummary[runs.length];
						for (int i = 0; i < summaries.length; i++)
						{
							summaries[i] = new RunStatisticSummary();
							try
							{
								summaries[i].calculate(runs[i], result[i], stat.getName());
							}
							catch (AnalysisException e1)
							{
								JOptionPane.showMessageDialog(this, "Error preparing statistics: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						
						summaryPanel.populate(runs, summaries);
					}
				}
			}
		}
		
		
		public void populate(Experiment e)
		{
			RunProbe [] stats = e.getRunStatistics();
			for (int i = 0; i < stats.length; i++)
			{
				statisticsList.addItem(stats[i]);
			}
		}
		
		public void clear()
		{
			summaryPanel.clear();
		}
	}
	
	
	protected class NormalitySummaryPanel extends JPanel
		implements ActionListener
	{
		protected JComboBox normalityList;
		protected JComboBox statisticsList;
		protected NormalitySummaryTablePanel summaryPanel;
		protected JButton displayButton;
		
		protected JRadioButton byAlgorithmRadio;
		protected JRadioButton byProblemRadio;	
		
		public NormalitySummaryPanel()
		{
			normalityList = new JComboBox(StatisticUtilities.loadNormalityTests());
			summaryPanel = new NormalitySummaryTablePanel();
			statisticsList = new JComboBox();			
			displayButton = new JButton("Display");
			displayButton.addActionListener(this);
			
			byAlgorithmRadio = new JRadioButton("By Algorithm");
			byAlgorithmRadio.setSelected(true);
			byAlgorithmRadio.addActionListener(this);
			byProblemRadio = new JRadioButton("By Problem");
			byProblemRadio.addActionListener(this);
			ButtonGroup group = new ButtonGroup();
		    group.add(byAlgorithmRadio);
		    group.add(byProblemRadio);	
			
			JPanel p = new JPanel();
			p.add(byAlgorithmRadio);
			p.add(byProblemRadio);
			p.add(statisticsList);			
			p.add(normalityList);
			p.add(displayButton);			
			
			setLayout(new BorderLayout());
			add(summaryPanel, BorderLayout.CENTER);
			add(p, BorderLayout.SOUTH);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == byAlgorithmRadio)
			{
				summaryPanel.setByAlgorithm(true);
			}
			else if(src == byProblemRadio)
			{
				summaryPanel.setByAlgorithm(false);
			}
			else if(src == displayButton)
			{
				ExperimentalRun [] runs = runList.getSelectedRuns();				
				
				if(runs==null || runs.length<2)
				{
					JOptionPane.showMessageDialog(this, "Please select a two or more runs to display.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					summaryPanel.clear();
					RunProbe stat = (RunProbe) statisticsList.getSelectedItem();
					NormalityTest test = (NormalityTest) normalityList.getSelectedItem();
					RunResult [][] result = loadRunResult(runs);
					if(result != null)
					{
						//  calculate statistics
						RunStatisticSummary [] summaries = new RunStatisticSummary[runs.length];
						for (int i = 0; i < summaries.length; i++)
						{
							summaries[i] = new RunStatisticSummary();
							try
							{
								summaries[i].calculate(runs[i], result[i], stat.getName());
							}
							catch (AnalysisException e1)
							{
								JOptionPane.showMessageDialog(this, "Error preparing statistics: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							
						}
						
						summaryPanel.populate(runs, summaries, test);
					}
				}
			}
		}
		
		public void populate(Experiment e)
		{
			RunProbe [] stats = e.getRunStatistics();
			for (int i = 0; i < stats.length; i++)
			{
				statisticsList.addItem(stats[i]);
			}
		}
		
		public void clear()
		{
			summaryPanel.clear();
		}
	}
	
	
	
	protected class PairwiseSummaryPanel extends JPanel
		implements ActionListener
	{
		protected JComboBox compareList;
		protected JComboBox statisticsList;
		protected PairwiseComparisonPanel summaryPanel;
		protected JButton displayButton;
		
		protected JRadioButton byAlgorithmRadio;
		protected JRadioButton byProblemRadio;	
		
		public PairwiseSummaryPanel()
		{
			compareList = new JComboBox(StatisticUtilities.loadStatisticalComparisonTests());
			summaryPanel = new PairwiseComparisonPanel();
			statisticsList = new JComboBox();			
			displayButton = new JButton("Display");
			displayButton.addActionListener(this);
			
			byAlgorithmRadio = new JRadioButton("By Algorithm");
			byAlgorithmRadio.setSelected(true);
			byAlgorithmRadio.addActionListener(this);
			byProblemRadio = new JRadioButton("By Problem");
			byProblemRadio.addActionListener(this);
			ButtonGroup group = new ButtonGroup();
		    group.add(byAlgorithmRadio);
		    group.add(byProblemRadio);	
			
			JPanel p = new JPanel();
			p.add(byAlgorithmRadio);
			p.add(byProblemRadio);
			p.add(statisticsList);			
			p.add(compareList);
			p.add(displayButton);			
			
			setLayout(new BorderLayout());
			add(summaryPanel, BorderLayout.CENTER);
			add(p, BorderLayout.SOUTH);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == byAlgorithmRadio)
			{
				summaryPanel.setCompareAlgorithms(true);
			}
			else if(src == byProblemRadio)
			{
				summaryPanel.setCompareAlgorithms(false);
			}
			else if(src == displayButton)
			{
				ExperimentalRun [] runs = runList.getSelectedRuns();				
				
				if(runs==null || runs.length<2)
				{
					JOptionPane.showMessageDialog(this, "Please select a two or more runs to display.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					summaryPanel.clear();
					RunProbe stat = (RunProbe) statisticsList.getSelectedItem();
					StatisticalComparisonTest test = (StatisticalComparisonTest) compareList.getSelectedItem();
					RunResult [][] result = loadRunResult(runs);
					if(result!=null)
					{
						// calculate statistics
						RunStatisticSummary [] summaries = new RunStatisticSummary[runs.length];
						for (int i = 0; i < summaries.length; i++)
						{
							summaries[i] = new RunStatisticSummary();
							try
							{
								summaries[i].calculate(runs[i], result[i], stat.getName());
							}
							catch (AnalysisException e1)
							{
								JOptionPane.showMessageDialog(this, "Error preparing statistics: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						
						try
						{
							summaryPanel.populate(runs, summaries, test);
						}
						catch(GUIException ee)
						{
							JOptionPane.showMessageDialog(this, ee.getMessage(), "Invalid Selection", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
		
		public void populate(Experiment e)
		{
			RunProbe [] stats = e.getRunStatistics();
			for (int i = 0; i < stats.length; i++)
			{
				statisticsList.addItem(stats[i]);
			}
		}
		
		
		public void clear()
		{
			summaryPanel.clear();
		}
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
			
			AnalysisManagementPanel panel = new AnalysisManagementPanel();
			panel.populateWithExperiment(experiments[0]);
			GUIUtils.testJFrame(panel, "Analysis", new Dimension(800, 600));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

}
