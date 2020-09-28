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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Type: JavaControlPanel<br/>
 * Date: 21/12/2006<br/>
 * <br/>
 * Description: Provides information about the java runtime and basic java controls
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class JavaControlPanel extends JPanel
    implements ActionListener
{
    protected JButton gcButton;
    protected JButton finalizeButton;
    protected JButton exitButton;
    
    protected JTextArea jta;
    
    public JavaControlPanel()
    {
        prepareGUI();
    }
    
    protected void prepareGUI()
    {
        setName("Java");
        JPanel controlPanel = getControlPanel();
        prepareTextArea();
        
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    
    protected JPanel getControlPanel()
    {
        gcButton = new JButton("Run GC");
        gcButton.addActionListener(this);
        gcButton.setToolTipText("Run garbage collection");
        
        finalizeButton = new JButton("Run Finalization");
        finalizeButton.addActionListener(this);
        finalizeButton.setToolTipText("Run Finalization");
        
        exitButton = new JButton("Close VM");
        exitButton.addActionListener(this);
        exitButton.setToolTipText("Exit the VM and close the application");
        
        JPanel controlPanel = new JPanel();
        controlPanel.add(gcButton);
        controlPanel.add(finalizeButton);
        controlPanel.add(exitButton);
        return controlPanel;
    }
    
    protected void prepareTextArea()
    {
        jta = new JTextArea();
        jta.setEditable(false);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        
        // name value pairs of all properties
        Properties p = System.getProperties();
        Enumeration<Object> keyEnum = p.keys();
        while(keyEnum.hasMoreElements())
        {
            String key = (String) keyEnum.nextElement();
            jta.append(key + "=" + p.getProperty(key) + "\n");
        }
        jta.setCaretPosition(0); // top
    }

    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        
        if(src == gcButton)
        {
            Runtime.getRuntime().gc();
        }
        else if(src == finalizeButton)
        {
            Runtime.getRuntime().runFinalization();
        }
        else if(src == exitButton)
        {
            System.exit(0);
        }
    }
}
