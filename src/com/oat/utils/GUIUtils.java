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
package com.oat.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Type: GUIUtils<br/>
 * Date: 11/12/2006<br/>
 * <br/>
 * Description: Graphical User Interface Utilities
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre>
 */
public class GUIUtils
{
    
    /**
     * Create a nice labels/fields panel
     * @param labelList
     * @param fieldList
     * @param name
     * @return
     */
    public static JPanel getGenericPropertiesPanel(
            JLabel [] labelList, 
            JComponent [] fieldList, 
            String name)
    {
        // validation
        if(labelList.length != fieldList.length)
        {
            throw new RuntimeException("Lable list length "+labelList.length+" does not match field list length "+fieldList.length+".");
        }                
        // Layout the labels in a panel
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(labelList.length, 1));
        for (int i = 0; i < labelList.length; i++)
        {
            labelPane.add(labelList[i]);
        }
        // Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(fieldList.length, 1));
        for (int i = 0; i < fieldList.length; i++)
        {
            fieldPane.add(fieldList[i]);
        }
        // Put the panels in another panel, labels on left, text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);
        contentPane.setName(name);
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), name));
        return contentPane;
    }
    
    
    /**
     * Centre the provided component, useful for dialogs and frames.
     * @param c
     */
    public static void centerComponent(Component c)
    {
        Dimension dim = c.getToolkit().getScreenSize();
        Rectangle abounds = c.getBounds();
        c.setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
    }
    
    
    /**
     * Creates a test JFrame for the provided panel
     * @param panel
     * @param name
     * @param size
     */
    public static void testJFrame(JPanel panel, String name)
    {		
		JFrame frame = new JFrame(name);
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		GUIUtils.centerComponent(frame);    	
    }
    
    /**
     * Creates a test JFrame for the provided panel
     * @param panel
     * @param name
     * @param size
     */
    public static void testJFrame(JPanel panel, String name, Dimension aSize)
    {		
		JFrame frame = new JFrame(name);
		frame.add(panel);
		frame.setSize(aSize);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		GUIUtils.centerComponent(frame);    	
    }
}
