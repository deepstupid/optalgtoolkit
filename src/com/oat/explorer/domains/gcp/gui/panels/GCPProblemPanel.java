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
package com.oat.explorer.domains.gcp.gui.panels;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.oat.Domain;
import com.oat.Problem;
import com.oat.domains.gcp.GCProblem;
import com.oat.explorer.gui.panels.ProblemPanel;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Type: GCPProblemPanel<br/>
 * Date: 11/12/2006<br/>
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
public class GCPProblemPanel extends ProblemPanel
{
    
    protected CommentPlot commentPlot;    
    

    public GCPProblemPanel(Frame parent, Domain domain)
	{
		super(parent, domain);
		// TODO Auto-generated constructor stub
	}

	@Override
    protected String getProblemBase()
    {
        return null;
    }



    @Override
    protected GenericProblemPlot prepareProblemPlot()
    {
        commentPlot = new CommentPlot();
        return commentPlot;
    }   
    
    protected class CommentPlot extends GenericProblemPlot
    {
        protected JTextArea jta;
        protected JScrollPane jsp;
        
        public CommentPlot()
        {
            jta = new JTextArea("");
            jta.setEditable(false);
            jta.setWrapStyleWord(true);
            jta.setLineWrap(true);
            jsp = new JScrollPane(jta); 
            setLayout(new BorderLayout());
            add(jsp);
        }
        
        public void problemChangedEvent(Problem problem)
        {
            GCProblem p = (GCProblem) problem;
            jta.setText(p.getComment());
            jta.setCaretPosition(0);
        }
    }
}
