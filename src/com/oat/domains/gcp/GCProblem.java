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
package com.oat.domains.gcp;

import java.util.HashSet;
import java.util.LinkedList;

import com.oat.AlgorithmRunException;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.utils.FileUtils;

/**
 * Type: GCProblem<br/>
 * Date: 11/12/2006<br/>
 * <br/>
 * Description: Generic Graph Coloring Problem Instance
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class GCProblem extends Problem
{
    public final static char KEY_COMMENT = 'c';
    public final static char KEY_PROBLEM = 'p';
    public final static char KEY_EDGE = 'e';
    
    public final static String SUPPORTED_FORMAT = "edge";    
    
    // configuration
    protected String problemFilename;
    
    // loaded
    protected boolean isLoaded;
    protected String name;    
    protected String formattedComment;
    protected int totalNodes;
    protected int totalEdges;    
    protected int [][] edgeList;
    protected int loadEdgeCounter;
    
    /**
     * Create a problem instance, load from file and prepare internal data structures
     * @param aProblemFile
     */
    public GCProblem(String aProblemFile)
    {
    	setProblemFilename(aProblemFile);
    	try
    	{
    		initialiseBeforeRun();
    	}
    	catch(InitialisationException e)
    	{
    		throw new RuntimeException(e);
    	}
    }
    
    public GCProblem()
    {}
    
    @Override
    public void initialiseBeforeRun()
        throws InitialisationException
    {  
    	if(!isLoaded)
    	{
    		loadProblem();
    	}
    }    
    
	public boolean isLoaded()
	{
		return isLoaded;
	}

	public void setProblemFilename(String f)
	{
		
		this.problemFilename = f;
	}

    
    
    protected String prepareNameFromFilename(String aFilename)
    {
    	// trim leading path
        int startIndex = aFilename.lastIndexOf('/');            
        return aFilename.substring(((startIndex==-1)?0:startIndex+1));        
    }
    
    /**
     * Load a problem instance from file
     * @throws AlgorithmRunException
     */
    protected void loadProblem()
        throws InitialisationException
    {
		if(isLoaded)
		{
			throw new InitialisationException("Dataset already loaded");
		}
    	
        // load the data
        String d = null;
        try
        {
            d = FileUtils.loadFile(problemFilename);
        }
        catch (Exception e)
        {
            throw new InitialisationException("Unable to load problem file: " + problemFilename, e);
        }
        
        String [] lines = d.trim().split("\n");
        
        StringBuffer comment = new StringBuffer();
        
        for (int i = 0; i < lines.length; i++)
        {
            // skip blank lines
            if(lines[i]==null || (lines[i]=lines[i].trim()).length()==0)
            {
                continue;
            }      
            
            // process the line
            if(lines[i].charAt(0) == KEY_COMMENT)
            {
                comment.append(lines[i].substring(1).trim()); // skip the 'c'
                comment.append("\n");
            }
            else if(lines[i].charAt(0) == KEY_PROBLEM)
            {
                parseProblemDefinition(lines[i]);
            }
            else if(lines[i].charAt(0) == KEY_EDGE)
            {
                addEdge(lines[i]);
            }
            else
            { 
                throw new InitialisationException("Unexpected line, does not match expected format: " + lines[i]);
            }
        }
        
        formattedComment = comment.toString();
        name = prepareNameFromFilename(problemFilename);
        isLoaded = true;
    }
    
    
    /**
     * Add an edge definition, expects the following format:
     * e W V
     * Where e indicates an edge line, W and V indicate the node end points of the edge
     * @param aLine
     */
    protected void addEdge(String aLine)
    {
        // ceck for too many edges
        if(loadEdgeCounter > totalEdges)
        {
            throw new AlgorithmRunException("Already loaded too many edges, problem defines more edges than specified in problem definition.");
        }
        
        String [] elements = aLine.split(" ");
        if(elements.length != 3)
        {
            throw new AlgorithmRunException("Edge line does match expected format, too many elements: " + aLine);
        }
        // parse elements
        int n1 = Integer.parseInt(elements[1]);
        int n2 = Integer.parseInt(elements[2]);
        // store the edge
        edgeList[loadEdgeCounter++] = new int[]{n1, n2};
    }
    
    /**
     * Parse an instance problem definition, expected format:
     * p FORMAT NODES EDGES
     * Where elements are separated by space, format is edge and nodes and edgs indicate
     * the number of each
     * @param aLine - expects a problem definition line in the correct format
     * @throws AlgorithmRunException
     */
    protected void parseProblemDefinition(String aLine)
        throws AlgorithmRunException
    {
        String [] elements = aLine.split(" ");
        if(elements.length != 4)
        {
            throw new AlgorithmRunException("Problem line does match expected format, too many elements: " + aLine);
        }
        if(!elements[1].equals(SUPPORTED_FORMAT))
        {
            throw new AlgorithmRunException("Unsupported file format: " + elements[1]);
        }
        totalNodes = Integer.parseInt(elements[2]);
        totalEdges = Integer.parseInt(elements[3]);
        edgeList = new int[totalEdges][];
    }

    @Override
    protected double problemSpecificCost(Solution solution)
    {
        GCPSolution s = (GCPSolution) solution;
        // count the number of unique colors in the solution
        s.numColors = countDistinctColors(s);
        // determine the feasability of the solution
        s.penalty = calculatePenalities(s);
        s.isFeasible = (s.penalty==0) ? true : false;
        // calculate the fitness of the solution
        return calculateFitness(s);
    }
    
    /**
     * Calculates the fitness of a proposed solution as the number of colors used in the solution,
     * in addition to the number of invalid connections (penalties)
     * @param s
     * @return
     */
    public double calculateFitness(GCPSolution s)
    {
        return s.numColors + s.penalty;
    }
    
    /**
     * Calculate the number of connections that cause the provided solution
     * to be invalid. edges are only checked once each (given they are by-directional)
     * 
     * @param s
     * @return
     */
    public int calculatePenalities(GCPSolution s)
    {        
        int penalties = 0;
        int [] colors = s.getNodeColors();
        for (int i = 0; i < edgeList.length; i++)
        {
            // convert nodes to zero offset
            int n1 = edgeList[i][0] - 1;
            int n2 = edgeList[i][1] - 1;
            if(colors[n1] == colors[n2])
            {
                penalties++;
            }
        }
        return penalties;
    }

    public int countDistinctColors(GCPSolution s)
    {
        HashSet<Integer> set = new HashSet<Integer>();
        int [] colors = s.getNodeColors();
        for (int i = 0; i < colors.length; i++)
        {
            Integer c = new Integer(colors[i]);
            if(!set.contains(c))
            {
                set.add(c);
            }
        }        
        return set.size();
    }
    
    @Override
    public void checkSolutionForSafety(Solution solution) 
        throws AlgorithmRunException
    {
        int [] colors = ((GCPSolution)solution).getNodeColors();        
        // ensure correct length
        if(colors.length != totalNodes)
        {
            throw new AlgorithmRunException("Solution has colors for an incorrect number of nodes " + colors.length + ", expected " + totalNodes +" total nodes.");
        }        
        // ensure valid colour values >=1 
        for (int i = 0; i < colors.length; i++)
        {
            if(colors[i] < 1)
            {
                throw new AlgorithmRunException("Solution has invalid color assignment " + colors[i] + ", in position " + i);
            }
        }  
    }
    
    
    /**
     * Return a list of node indexes that are neighbours (connected to)
     * the specified node index
     * 
     * NOTE: Remember that node indexs are not zero offset
     * 
     * @param nodeIndex
     * @return
     */
    public LinkedList<Integer> getNeighbours(int nodeIndex)
    {
        LinkedList<Integer> neighbours = new LinkedList<Integer>();
        
        // process the edge list
        for (int i = 0; i < edgeList.length; i++)
        {
            if(edgeList[i][0] == nodeIndex)
            {
                neighbours.add(edgeList[i][1]);
            }
            else if(edgeList[i][1] == nodeIndex)
            {
                neighbours.add(edgeList[i][0]);
            }
        } 
        
        return neighbours;
    }
    
    
    @Override
    public String getName()
    {
    	if(isLoaded)
    	{
    		return name;
    	}
        
    	return "GCP";
    }

    @Override
    public String getDetails()
    {
        StringBuffer b = new StringBuffer();
        b.append(super.getDetails()+", ");
        b.append("ProblemFilename="+problemFilename+", ");
        b.append("TotalNodes="+totalNodes+", ");
        b.append("TotalEdges="+totalEdges+", ");
        b.append("Comment="+formattedComment+".");
        return b.toString();
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }
    
    /**
     * The human readable comment for the problem instance specified in the instance file 
     * @return
     */
    public String getComment()
    {
        return formattedComment;
    }    
    
    public String getProblemFilename()
    {
        return problemFilename;
    }

    public int getTotalNodes()
    {
        return totalNodes;
    }

    public int getTotalEdges()
    {
        return totalEdges;
    }

    public int[][] getEdgeList()
    {
        return edgeList;
    }

    public static void main(String[] args)
    {
        GCProblem p = new GCProblem("gcp/anna.col");		
		System.out.println(p.getDetails());		
	}
}
