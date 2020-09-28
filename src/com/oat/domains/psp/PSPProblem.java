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
package com.oat.domains.psp;



import com.oat.AlgorithmRunException;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;

/**
 * Type: ProtFoldProblem<br/>
 * Date: 21/11/2006<br/>
 * <br/>
 * Description:
 * 
 * A model is a series of moves (left, right, back, foward) on a 2D lattice
 * The total number of moves for a model is model.length-1 because the first 
 * placement is not a move.
 * Natural (in-permutation) H-H (0-0) connections are not counted in the scoring.
 * The goal is to maximise H-H connections - that is to minmise score (energy), as each H-H 
 * connection is allocated a score of -1
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre> 
 */
public class PSPProblem extends Problem
{        
    /**
     * States for a constructed lattice for a solution
     */
    public final static int EMPTY = 0, H = 1, P = 2;
        
    
    // user configurable
      /**
     * H == 0 (false)
     * P == 1 (true)
     */          
    protected String datasetString;
    
    // loaded
    protected boolean isLoaded;
    protected boolean [] dataset;
    protected int totalNaturalConnections;
    
    
    protected int totalPs(boolean [] dataset)
    {
    	int c = 0;
    	
    	for (int i = 0; i < dataset.length; i++)
		{
			if(dataset[i])
			{
				c++;
			}
		}
    	
    	return c;
    }
    protected int totalHs(boolean [] dataset)
    {
    	int c = 0;
    	
    	for (int i = 0; i < dataset.length; i++)
		{
			if(!dataset[i])
			{
				c++;
			}
		}
    	
    	return c;
    }
    
    public PSPProblem(String aDatasetString)
    {
    	setDatasetString(aDatasetString);
    	try
    	{
    		initialiseBeforeRun();
    	}
    	catch(InitialisationException e)
    	{
    		throw new RuntimeException(e);
    	}
    }  
    
	public PSPProblem()
    {}
	
	
    
    @Override
    public void initialiseBeforeRun()
    	throws InitialisationException
    {  
    	if(!isLoaded)
    	{
    		dataset = stringDatasetToBoolean(datasetString);
            totalNaturalConnections = totalNaturalConnections(dataset, dataset.length);
            isLoaded = true;
    	}
    }
    
    public boolean isLoaded()
	{
		return isLoaded;
	}



	public void setDatasetString(String datasetString)
	{		
		this.datasetString = datasetString;
	}

    
    
    /**
     * Calculate the scoring of a provided solution
     * 
     * @param s : a solution to the current problem definition
     * @return double : the allocated scoring of the provided solution
     * if the folding results in a collision on the lattice, the score is the number of steps
     * in the solution before the collision.
     * If there is no collision, the score is the length of the solution, in addition to the 
     * the number of non-natural H-H connections 
     */
    @Override
    protected double problemSpecificCost(Solution s)
    {
        PSPSolution sol = (PSPSolution) s;
        // get a lattice verion of the solution
        byte [][] lattice = sol.retrieveLattice(dataset);
        // count the number of topological H-H
        int length = (sol.isFeasibleConformation()) ? dataset.length : sol.getLengthBeforeInfeasible()+1; 
        sol.totalTopologicalHH = countTotalTopologicalHH(lattice, length);
        // fitness is the total number of amino acids that could not be laid down minus the total number of
        // non natural (topoligical) H-H connections. Fitness is a minimising problem        
        if(sol.isFeasibleConformation())
        {
            return -sol.totalTopologicalHH;
        }        
        // infeasible solution
        return (sol.getPermutation().length-sol.getLengthBeforeInfeasible()) - sol.totalTopologicalHH;
    }    
    
    @Override
    public void checkSolutionForSafety(Solution solution) 
        throws AlgorithmRunException
    {
        PSPSolution s = (PSPSolution) solution;
        if(s.getPermutation().length != (dataset.length-1))
        {
            throw new AlgorithmRunException("bitstring length "+s.getPermutation().length+" does not match expected length " + (dataset.length-1));
        }
    }
    
    /**
     * Process the lattice seeking all H-H connections, including natural ones
     * @param lattice
     * @param aminoAcidLength
     * @return
     */
    public int countTotalTopologicalHH(byte [][] lattice, int aminoAcidLength)
    {
        int total = 0;
        
        // sum total H-H connections
        for (int i = 0; i < lattice.length; i++)
        {
            for (int j = 0; j < lattice[i].length; j++)
            {
                if(lattice[i][j] == H)
                {
                    // for the current position on the lattice, 
                    // check above and below, prevents double counting
                    // note - it is not an either-or, we can have both cases.
                    if(i!=lattice.length-1 && lattice[i+1][j]==H)
                    {
                        total++;
                    }
                    if(j!=lattice[i].length-1 && lattice[i][j+1]==H)
                    {
                        total++;
                    }
                }
            }
        }    
        
        total = (total - totalNaturalConnections(dataset, aminoAcidLength));
        if(total < 0)
        {
            throw new AlgorithmRunException("Invalid total topological H-H connections total["+total+"], aminoAcidLength["+aminoAcidLength+"]");
        }
        return total;
    }

    @Override
    public String getName()
    {
    	if(isLoaded)
    	{
    		return "2DHP [L="+dataset.length+",P="+totalPs(dataset)+",H="+totalHs(dataset)+"]";
    	}
        
    	return "2DHP Model";
    }

    @Override
    public boolean isMinimization()
    {        
        return true;
    }
    
    @Override
    public String getDetails()
    {
        StringBuffer b = new StringBuffer();
        b.append(super.getDetails()+", ");
        b.append("Permutation="+datasetString+", ");
        b.append("PermutationLength="+dataset.length+", ");
        b.append("NaturalHH="+totalNaturalConnections+", ");        
        return b.toString();
    }
    
    /**
     * Converts a boolean H(0) P(1) into a byte value for 'P' and 'H'
     * @param b
     * @return
     */
    public final static byte val(boolean b)
    {
        return (b) ? (byte)P : (byte)H;
    }
    
    /**
     * Count the number of natural (non-topoligcal) H-H connections in the
     * dataset to the specified length
     * @param dataset
     * @param length
     * @return
     */
    public static int totalNaturalConnections(boolean [] dataset, int length)
    {
        int total = 0;
        
        for (int i = 0; i < length-1; i++)
        {
            if(!dataset[i] && !dataset[i+1])
            {
                total++;
            }
        }
        
        return total;
    }

    
    /**
     * Convert a provided problem domain into a human readable string
     * @param b
     * @return
     */
    public static String booleanToString(boolean [] b)
    {
        char [] c = new char[b.length];
        
        for (int i = 0; i < c.length; i++)
        {
            c[i] = b[i] ? 'P' : 'H';
        }
        
        return new String(c);
    }
    
    /**
     * Convert a provided string data set in HP format to a a boolean string
     * H=0, P=1
     * @param s
     * @return
     */
    public static boolean [] stringDatasetToBoolean(String s)
    {
        boolean [] b = new boolean[s.length()]; 
        char [] c = s.toCharArray();
        
        for (int i = 0; i < c.length; i++)
        {
            if(c[i] == 'H' || c[i] == 'h')
            {
                b[i] = false;
            }
            else if(c[i] == 'P' || c[i] == 'p')
            {
                b[i] = true;
            }
            else
            {
                throw new AlgorithmRunException("Invalid character in problem definition " + c[i] + ", ["+s+"]");
            }
        }
        
        return b;
    }

    public boolean[] getDataset()
    {
        return dataset;
    }


    public String getDatasetString()
    {
        return datasetString;
    }

    public int getTotalNaturalConnections()
    {
        return totalNaturalConnections;
    }
    
    
    
    public static void main(String[] args)
	{
        PSPProblem p = new PSPProblem("hphpphhphpphphhpphph");       
        System.out.println(p.getDetails());  
	}
}
