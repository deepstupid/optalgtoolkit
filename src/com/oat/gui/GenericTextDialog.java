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

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.oat.utils.GUIUtils;

/**
 * Type: GenericTextDialog<br/>
 * Date: 01/12/2006<br/>
 * <br/>
 * Description: Generic about popup
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class GenericTextDialog extends JDialog
{
    protected JTextArea jta;
    protected JScrollPane jsp;
    
    /**
     * 
     * @param objects
     * @param base
     */
    public GenericTextDialog(String text)
    {
        super((JFrame)null, "About", true);
        jta = new JTextArea();
        jta.setWrapStyleWord(true);
        jta.setLineWrap(true);
        jta.setFont(new Font("Courier", Font.PLAIN, 12));
        jta.setEditable(false);
        jsp = new JScrollPane(jta);
        jsp.setPreferredSize(new Dimension(300, 300));
        jsp.getVerticalScrollBar().setUnitIncrement(20);
        add(jsp);
        setText(text);
        setSize(320, 240);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    public void setText(String s)
    {
        jta.setText(s);
        jta.setCaretPosition(0);
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
}
