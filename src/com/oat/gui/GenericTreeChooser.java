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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Type: GenericTreeChooser<br/>
 * Date: 29/11/2006<br/>
 * <br/>
 * Description: A generic tree chooser GUI element for any object type
 * organises objects by known package structure, otherwise flat
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 20/08/2007	JBrownlee	Added support for default base if classes do not conform 
 * 							to an expected base
 * </pre>
 */
public class GenericTreeChooser extends JPopupMenu
{
    public final static String ROOT = "ROOT";
    public final static String DEFAULT_BASE = "com.oat.domains";
    
    public final JTree tree;
    protected final LinkedList<DefaultMutableTreeNode> flatModel;
    
    protected JScrollPane scroller;
        

    /**
     * 
     * @param objects
     * @param base
     */
    public GenericTreeChooser(Object[] objects, String base)
    {
        super("Tree Chooser");

        flatModel = new LinkedList<DefaultMutableTreeNode>();
        tree = prepareTree(objects, base);

        JPanel treeView = new JPanel();
        treeView.setLayout(new BorderLayout());
        treeView.add(tree, BorderLayout.NORTH);

        // make backgrounds look the same
        treeView.setBackground(tree.getBackground());

        scroller = new JScrollPane(treeView);
        scroller.setPreferredSize(new Dimension(300, 300));
        scroller.getVerticalScrollBar().setUnitIncrement(20);

        add(scroller);
    }
    
    /**
     * For externally selecting a known (same instance) object in the tree
     * @param o
     */
    public void setSelection(Object o)
    {
        // locate and set
        for(DefaultMutableTreeNode n: flatModel)
        {
            Entry entry = (Entry) n.getUserObject();
            if(entry.object == o)
            {                
                tree.setSelectionPath(new TreePath(n.getPath()));
                break;
            }
        }
    }

    /**
     * Type: Entry<br/>
     * Date: 30/11/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    public static class Entry implements Comparable<Entry>
    {
        public String key;
        public String name;
        public Object object;

        public Entry(Object o, String k)
        {
            this(o, o.getClass().getName(), k);
            name = name.substring(name.lastIndexOf('.')+1);
        }
        public Entry(Object o, String n, String k)
        {
            name = n;
            object = o;
            key = k;
        }

        public int compareTo(Entry o)
        {
            return name.compareTo(o.name);
        }
        
        @Override
        public String toString()
        {
            return name;
        }
        
        @Override
        public boolean equals(Object o)
        {
            return object.equals(o);
        }
    }

    /**
     * 
     * @param objects
     * @return
     */
    protected JTree prepareFlatTree(Object[] objects)
    {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("root");
        
        for (int i = 0; i < objects.length; i++)
        {
            Entry e = new Entry(objects[i], objects[i].toString(), ROOT);
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(e);
            top.add(n);
            flatModel.add(n);
        }
        
        return new JTree(top);
    }
    
    protected Map<String, LinkedList<Entry>> buildObjectMap(Object[] objects, String base)
    {
        HashMap<String, LinkedList<Entry>> map = new HashMap<String, LinkedList<Entry>>();
        
        for (int i = 0; i < objects.length; i++)
        {
            String name = objects[i].getClass().getName();
            String key = null;
            // safety
            if (!name.startsWith(base))
            {
            	// does not conform to expected (for example: recursive properties files)
            	// attempt to trim a default base
            	if (!name.startsWith(DEFAULT_BASE))
            	{
            		throw new RuntimeException("Object does not have expected or default base (" + base + ", or "+DEFAULT_BASE+"): " + name);
            	}
            	// trim default
            	key = name.substring(DEFAULT_BASE.length(), name.lastIndexOf('.'));
            }
            else
            {
            	key = name.substring(base.length(), name.lastIndexOf('.'));
            }
            if(key.length()==0)
            {
                key = ROOT;
            }
            else
            {
                // remove the initial dot
                key = key.substring(1); 
            }
            LinkedList<Entry> entry = map.get(key);
            if (entry == null)
            {
                entry = new LinkedList<Entry>();
                map.put(key, entry);
            }
            entry.add(new Entry(objects[i], key));
        }
        
        return map;
    }
    
    
    /**
     * 
     * @param objects
     * @param base
     * @return
     */
    protected JTree prepareTree(Object[] objects, String base)
    { 
        // check for the case of a flat tree structure
        if(base == null)
        {
            return prepareFlatTree(objects);
        }        
        
        // prepare the object map
        Map<String, LinkedList<Entry>> map = buildObjectMap(objects, base);

        // create the tree structure        
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(base);
        LinkedList<Entry> addLast = null;
        
        Collection<LinkedList<Entry>> unsortedList = map.values();
        LinkedList<LinkedList<Entry>> sortedList = prepareSortedList(unsortedList);
        for (LinkedList<Entry> l : sortedList)
        {
            Collections.sort(l);
            if(l.getFirst().key.equals(ROOT))
            {
                addLast = l;
                continue;
            }
           
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(l.getFirst().key);
            top.add(n);            
            
            for (Entry e : l)
            {
                DefaultMutableTreeNode a = new DefaultMutableTreeNode(e);
                n.add(a);
                flatModel.add(a);
            }
        }

        // check for a list of nodes that need to be added to the root node
        if(addLast!=null)
        {
            for (Entry e : addLast)
            {
                DefaultMutableTreeNode a = new DefaultMutableTreeNode(e);
                top.add(a);
                flatModel.add(a);
            }
        }
        
        return new JTree(top);
    }
    
    protected LinkedList<LinkedList<Entry>> prepareSortedList(Collection<LinkedList<Entry>> u)
    {
        LinkedList<LinkedList<Entry>> sorted = new LinkedList<LinkedList<Entry>>();
        sorted.addAll(u);
        Comparator<LinkedList<Entry>> c = new Comparator<LinkedList<Entry>>()
        {
            public int compare(LinkedList<Entry> a, LinkedList<Entry> b)
            {
                return a.getFirst().key.compareTo(b.getFirst().key);
            }
        };
        // sort
        Collections.sort(sorted, c);
        
        return sorted;
    }

    public JTree getTree()
    {
        return tree;
    }
}
