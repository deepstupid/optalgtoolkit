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
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import com.oat.utils.GUIUtils;



/**
 * Type: AboutDialog<br/>
 * Date: 02/12/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AboutDialog extends JDialog
{
    public AboutDialog(Frame owner)
    {
        super(owner, "About", true);
        setSize(500, 400);
        setResizable(false);
        prepareGUI();
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
    
    protected void prepareGUI()
    {
        JTabbedPane tp = new JTabbedPane();
        tp.add(new OATPanel());
        tp.add(new JavaControlPanel());
        setLayout(new BorderLayout());
        add(tp, BorderLayout.CENTER);
    }
}
