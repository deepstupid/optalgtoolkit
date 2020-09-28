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
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.oat.utils.FileUtils;


/**
 * Description: Generic text log
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
public class GenericLogPanel extends JPanel 
    implements ActionListener
{
    protected JTextArea jta;
    protected JScrollPane jsp;
    
    protected JButton clearButton;
    protected JButton copyButton;
    protected JButton saveButton;
    
    protected JPanel controlPanel;
    
    public GenericLogPanel()
    {
        setName("LogPanel");
        jta = new JTextArea();
        jta.setWrapStyleWord(true);
        jta.setLineWrap(true);
        jta.setFont(new Font("Courier", Font.PLAIN, 12));
        jta.setEditable(false);
        jsp = new JScrollPane(jta);
        
        controlPanel = prepareControlPanel();
        
        this.setLayout(new BorderLayout());
        this.add(jsp);
        this.add(controlPanel, BorderLayout.SOUTH);
    }
    
    protected JPanel prepareControlPanel()
    {
        JPanel p = new JPanel();
        clearButton = new JButton("Clear");
        copyButton = new JButton("Copy");
        saveButton = new JButton("Save");
        clearButton.addActionListener(this);
        copyButton.addActionListener(this);
        saveButton.addActionListener(this);
        
        copyButton.setToolTipText("Copy the contents of the log to system clipboard");
        clearButton.setToolTipText("Clear the contents of the log");
        saveButton.setToolTipText("Save the contents of the log to file");
        
        p.add(clearButton);
        p.add(copyButton);
        p.add(saveButton);
        
        return p;
    }
    
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        if(src == clearButton)
        {
            clear();
        }
        else if(src == copyButton)
        {
            StringSelection ss = new StringSelection(jta.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        }
        else if(src == saveButton)
        {
        	exportLog();
        }        
    }        
    
    public void exportLog()
    {
    	String data = jta.getText();
    	if(data != null && data.length()>0)
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
    protected void externalizeData(String data, File outputFile)
    {
    	try
    	{
    		FileUtils.writeToFile(data, outputFile);
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
    
    public void scrollToTop()
    {
    	jta.setCaretPosition(0);
    }
    
    public void clear()
    {
        jta.setText("");
    }    
    public void report(String data)
    {
    	jta.setText(data);
    }    
    public void reportAppend(String data)
    {
    	jta.append(data);
    }
    public void reportAppendLine(String dataLine)
    {
    	jta.append(dataLine);
    	jta.append("\n");
    }    
    
    public void disableControls()
    {
    	clearButton.setEnabled(false);
    	copyButton.setEnabled(false);
    	saveButton.setEnabled(false);
    }    
    public void enableControls()
    {
    	clearButton.setEnabled(true);
    	copyButton.setEnabled(true);
    	saveButton.setEnabled(true);
    }    
}
