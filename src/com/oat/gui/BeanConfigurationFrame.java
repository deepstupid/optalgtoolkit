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
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.oat.utils.GUIUtils;

/**
 * Type: BeanConfigurationFrame<br/>
 * Date: Dec 10, 2006<br/>
 * <br/>
 * Description: Provides a generic frame that will 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * JBrownlee    06-07-2007  Modified such that frame provides a configuration 
 *                          for any java bean
 * </pre>
 */
public class BeanConfigurationFrame extends JDialog
    implements ActionListener
{
    protected JButton okButton;
    protected Frame parent;
    protected Object bean;
    protected String [] disabledList;
    protected BeanConfigurationPanel beanPanel;
    
    public BeanConfigurationFrame(Frame frame, Object aBean, String title, String [] aDisabledList)
    {
        // prepare the frame
        super(frame, title, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new MyWindowAdapter());
        setResizable(false);
        parent = frame;
        bean = aBean;
        disabledList = aDisabledList;
        // prepare the GUI
        prepareGUI();
    }
    
    protected void prepareGUI()
    {
        // prepare the bean panel
        beanPanel = BeanConfigurationPanel.createInstance(bean, disabledList);        
        beanPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Configuration"));
        // prepare the okay control panel
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        JPanel controlPanel = new JPanel();
        controlPanel.add(okButton);   
        // add everything to the GUI
        JPanel m = new JPanel(new BorderLayout());        
        m.add(beanPanel, BorderLayout.CENTER);
        m.add(controlPanel, BorderLayout.SOUTH);
        add(m, BorderLayout.CENTER);
        pack(); // suitable frame size for the bean configuration
    }
    
    @Override
    public void setVisible(boolean b)
    {
        if(b)
        {
          GUIUtils.centerComponent(this);
        }
        super.setVisible(b);
    }
    
    
    
    protected class MyWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent we)
        {
            populateBean();
        }
    }
    
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        if(src == okButton)
        {
            populateBean();
            dispose();
        }
    }
    
    public void populateBean()
    {
        try
        {
            beanPanel.populateBeanFromGUI();
        }
        catch(final Exception e)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    JOptionPane.showMessageDialog(parent, "Problem with algorithm configuraiton.\n "+e.getMessage(), "Invalid Algorithm Configuration", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
}
