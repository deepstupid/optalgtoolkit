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
package com.oat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.oat.utils.FileUtils;
import com.oat.utils.GUIUtils;

/**
 * Description: Display a table and facilitate exploty data to CSV 
 *  
 * Date: 30/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExplortableTablePanel extends JPanel
	implements ActionListener
{
    protected JTable dataTable;
    protected GenericDefaultTableModel dataTableModel;
    protected JButton copyButton;
    protected JButton saveButton;   
    protected JPanel controlPanel;
	
	public ExplortableTablePanel()
	{		
		createGUI();
	}
	
	public void createGUI()
	{
		dataTableModel = createJTableModel();
        dataTable = createJTable(dataTableModel); 
        
        // control panel
        saveButton = new JButton("Export");
        saveButton.addActionListener(this);
        copyButton = new JButton("Copy");
        copyButton.addActionListener(this);
        
        controlPanel = new JPanel();
        controlPanel.add(saveButton);
        controlPanel.add(copyButton);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(dataTable), BorderLayout.CENTER);        
        add(controlPanel, BorderLayout.SOUTH);
	}
	
	protected GenericDefaultTableModel createJTableModel()
	{
		return new GenericDefaultTableModel();        
	}
	
	protected JTable createJTable(GenericDefaultTableModel model)
	{
		return new JTable(model);   
	}
	
    public void clear()
    {
        // remove rows
        while(dataTableModel.getRowCount()!=0)
        {
        	dataTableModel.removeRow(0);
        }
        // lazy - reset the model
        dataTableModel = new GenericDefaultTableModel();
        dataTable.setModel(dataTableModel); 
        dataTableModel.fireTableDataChanged();
    }

    public static Object [][] tableToCSVData(JTable aTable)
    {
    	int rows = aTable.getRowCount();
    	int cols = aTable.getColumnCount();
    	Object [][] data = new Object[rows+1][cols]; // +1 for the header
    	
    	// get the column headers
    	for (int i = 0; i < data[0].length; i++)
		{
    		data[0][i] = aTable.getColumnName(i);
		}    	
    	
    	// get table data
    	for (int row = 1; row < data.length; row++)
		{
			for (int col = 0; col < data[row].length; col++)
			{
				data[row][col] = aTable.getValueAt(row-1, col); // -1 because the data is offset +1 for the header
			}
		}
    	
    	return data;
    }
    
    public void exportTable()
    {
    	Object [][] data = getTableData();
    	if(data != null)
    	{
	    	// find out where to save the data
	    	File outputFile = getOutputFile();
	    	if(outputFile != null)
	    	{
	    		// externalize the data
	    		externalizeData(data, outputFile);
	    	}
    	}
    }    
    
    protected void externalizeData(Object [][] data, File outputFile)
    {
    	try
    	{
    		FileUtils.writeCSV(data, outputFile);
    		JOptionPane.showMessageDialog(this, "Successfully saved to file: " + outputFile.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
    	}
    	catch(Exception e)
    	{
    		JOptionPane.showMessageDialog(this, "Error writing file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    protected File getOutputFile()
    {	
    	JFileChooser fc = new JFileChooser(new File("./"));
    	fc.showSaveDialog(this);
    	File selFile = fc.getSelectedFile();
    	
    	return selFile;
    }
    
    protected Object [][] getTableData()
    {
    	return tableToCSVData(dataTable);
    }    
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if(src == saveButton)
		{
			exportTable();
		}		
		else if(src == copyButton)
		{
			tableToClipboard();
		}
	}
	
	protected void tableToClipboard()
	{
    	Object [][] data = getTableData();
    	if(data != null)
    	{
	    	String stringData = FileUtils.matrixToCVSString(data);
            StringSelection ss = new StringSelection(stringData);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    	}
	}
	
	
	public void populateWithData(Object [][] data)
	{
		String [] headings = new String[data[0].length];
		for (int i = 0; i < headings.length; i++)
		{
			headings[i] = "Var"+i;
		}
		populateWithData(data, headings);
	}
	
	public void addRow(Object [] row)
	{
		Vector<Object> v = new Vector<Object>();
		for (int i = 0; i < row.length; i++)
		{
			v.add(row[i]);
		}
		addRow(v);
	}
	
	public void addRow(Vector v)
	{		
		dataTableModel.addRow(v);
	}
	
	public void setHeaders(Vector v)
	{		
		dataTableModel.setColumnIdentifiers(v);
	}
	
	public void populateWithData(Object [][] data, String [] headings)	
	{
		clear();
		
        dataTableModel.setColumnIdentifiers(headings);
        
        // rows
        for (int row = 0; row < data.length; row++)
		{
        	addRow(data[row]);
		}
	}
	
	
	
	

	public JTable getDataTable()
	{
		return dataTable;
	}

	public GenericDefaultTableModel getDataTableModel()
	{
		return dataTableModel;
	}

	public JPanel getControlPanel()
	{
		return controlPanel;
	}

	/**
	 * Test out this thing
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{			
			// create some dummy data
			Object [][] dummy = new Object[30][5];
			for (int row = 0; row < dummy.length; row++)
			{
				for (int col = 0; col < dummy[row].length; col++)
				{
					dummy[row][col] = Math.random(); 
				}
			}
			
			ExplortableTablePanel panel = new ExplortableTablePanel();
			panel.populateWithData(dummy);
			
			GUIUtils.testJFrame(panel, "Exportable Table", new Dimension(800, 600));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
