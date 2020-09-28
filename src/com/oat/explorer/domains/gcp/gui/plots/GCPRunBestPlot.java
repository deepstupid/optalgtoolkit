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
package com.oat.explorer.domains.gcp.gui.plots;

import java.awt.event.ActionListener;

import com.oat.AlgorithmEpochCompleteListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;


/**
 * Type: GCPRunBestPlot<br/>
 * Date: 11/12/2006<br/>
 * <br/>
 * Description:

 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   This is crap, but I don't want to throw it out yet 
 *                          - so just commented it all out 
 * </pre>
 */
public abstract class GCPRunBestPlot 
    extends GenericProblemPlot 
    implements ActionListener, ClearEventListener, AlgorithmEpochCompleteListener
{     
    /*
    public final static String DATUM_KEY = "GCPDATUM";
    
    protected DirectedGraph graph;
    protected VisualizationViewer viewer;
 
    protected GCProblem problem;
    protected int [] currentBestSolution;
    
    protected JRadioButton circleLayout;
    protected JRadioButton frLayout;    
    protected JRadioButton isomLayout;
    protected JRadioButton kkLayout;
    protected JRadioButton springLayout;
    

    public GCPRunBestPlot()
    {
        setName("Run Best");
        prepareGUI();        
    }
    
    public void clear()
    {
        synchronized(this)
        {
            currentBestSolution = null;
        }        
        repaint();
    }
   
    public void problemChangedEvent(Problem p)
    {
        synchronized(this)
        {
            problem = (GCProblem) p;
            currentBestSolution = null;
            
            
            setEnabled(false);
            setVisible(false);
            viewer.stop();
            prepareGraph();
            viewer.setGraphLayout(new KKLayout(graph));
            // redraw things
            try
            {
                viewer.restart();
            }
            catch (Exception ee)
            {}// ignore
            setEnabled(true);
            setVisible(true);            
        }       
        repaint();
    }

    public void iterationComplete(Problem p, LinkedList<? extends Solution> currentPop, Solution bestEver)
    {
        GCPSolution s = (GCPSolution) bestEver;
        // prepare a graph representation
        synchronized(this)
        {
            currentBestSolution = ArrayUtils.copyArray(s.getNodeColors());
        }
        repaint();
    }

    protected JPanel getControlPanel()
    {   
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Display Control"));
        
        circleLayout = new JRadioButton("Circle Layout", false);
        frLayout = new JRadioButton("FR Layout", false);
        isomLayout = new JRadioButton("ISOM Layout", false);
        kkLayout = new JRadioButton("KK Layout", true);
        springLayout = new JRadioButton("Spring Layout", false);
        
        circleLayout.addActionListener(this);
        frLayout.addActionListener(this);
        isomLayout.addActionListener(this);
        kkLayout.addActionListener(this);
        springLayout.addActionListener(this);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(circleLayout);
        bg.add(frLayout);
        bg.add(isomLayout);
        bg.add(kkLayout);
        bg.add(springLayout);
        
        p.add(circleLayout);
        p.add(frLayout);
        p.add(isomLayout);
        p.add(kkLayout);
        p.add(springLayout);
        
        return p;
    }
    
    protected void prepareGUI()
    {
        // prepare the graph
        graph = new DirectedSparseGraph();
        
        // prepare the graph renderer
        PluggableRenderer graphRenderer = new PluggableRenderer();
        Layout layout = new KKLayout(graph);
        viewer = new VisualizationViewer(layout, graphRenderer);   
        viewer.setBackground(Color.white);
//        viewer.setPickSupport(new ShapePickSupport(viewer));
        
        // special painting renderer
        graphRenderer.setVertexPaintFunction(new GCPVertexPainter());
        
        // permit scrolling
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(viewer);
        // permit zooming with the mouse
        //ZoomPanGraphMouse gm = new ZoomPanGraphMouse(viewer);
//        gm.setZoomAtMouse(true);
        //viewer.setGraphMouse(gm);
        
        JPanel p = getControlPanel();
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
    }
    
    
    
    protected class GCPVertexPainter implements VertexPaintFunction
    {          
        public Paint getFillPaint(Vertex v)
        {
            Integer index = (Integer) v.getUserDatum(DATUM_KEY);
            return determineColor(index);
        }
        
        protected Color determineColor(int index)
        {
            return Color.RED;
        }

        public Paint getDrawPaint(Vertex arg0)
        {
            return Color.BLACK;
        }
    }
    
    protected void prepareGraph()
    {
        graph.removeAllEdges();
        graph.removeAllVertices();
        
        // prepare all nodes
        int totalNodes = problem.getTotalNodes();
        SimpleDirectedSparseVertex [] nodes = new SimpleDirectedSparseVertex[totalNodes];
        for (int i = 0; i < totalNodes; i++)
        {
            // create the node
            nodes[i] = new SimpleDirectedSparseVertex();
            // store the nodes index (for painting)
            nodes[i].addUserDatum(DATUM_KEY, new Integer(i), new UserDataContainer.CopyAction.Shared());
            // add to the graph
            graph.addVertex(nodes[i]);
        }
        
        // prepare all edges
        int [][] edges = problem.getEdgeList();
        for (int i = 0; i < edges.length; i++)
        {
            int n1 = edges[i][0] - 1;
            int n2 = edges[i][1] - 1;
            // prepare the edge
            DirectedSparseEdge edge = new DirectedSparseEdge(nodes[n1], nodes[n2]);
            // add the edge to the graph
            graph.addEdge(edge);
        }
    }
    

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        
        if(src == circleLayout)
        {
            viewer.setGraphLayout(new CircleLayout(graph));
        }
        else if(src == frLayout)
        {
            viewer.setGraphLayout(new FRLayout(graph));
        }
        else if(src == isomLayout)
        {
            viewer.setGraphLayout(new ISOMLayout(graph));
        }
        else if(src == kkLayout)
        {
            viewer.setGraphLayout(new KKLayout(graph));
        }
        else if(src == springLayout)
        {
            viewer.setGraphLayout(new SpringLayout(graph));
        }
        
        // redraw things
        try
        {
            viewer.restart();
        }
        catch(Exception ee)
        {}// ignore
    }        
    */
    
}
