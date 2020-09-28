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
package com.oat.domains.bcr;

import com.oat.AlgorithmRunException;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.utils.BitStringUtils;
import com.oat.utils.FileUtils;
import com.oat.utils.MeasureUtils;

/**
 * Type: CharRecProblem<br/>
 * Date: 18/12/2006<br/>
 * <br/>
 * Description: Binary character recognition problem domain
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 05/07/2007   JBrownlee   Cost function uses average error, problem is minimise
 * 11/07/2007   JBrownlee   Modified such that binary patterns are never exposed to the system
 * </pre>
 */
public class BCRProblem extends Problem
{
	// configurable
	protected String filename;
	
	// loaded
	protected boolean isLoaded;
    protected boolean [][] patterns;
    protected int singlePatternWidth;
    protected int singlePatternHeight;    
    protected String name;
    
    public BCRProblem()
    {}
    
    public BCRProblem(String aFilename)
    {
    	setFilename(aFilename);
    	try
    	{
    		initialiseBeforeRun();
    	}
    	catch(InitialisationException e)
    	{
    		throw new RuntimeException(e);
    	}
    }
    
    @Override
    public void initialiseBeforeRun()
        throws InitialisationException
    {  
    	if(!isLoaded)
    	{
    		loadDataset();
    	}
    }
    
    
    public boolean isLoaded()
	{
		return isLoaded;
	}

	public void setFilename(String f)
	{
		this.filename = f;
	}


	


	protected void loadDataset()
		throws InitialisationException
    {
		if(isLoaded)
		{
			throw new InitialisationException("Dataset already loaded");
		}
		
        // load the data file
        String data = null;
        try
        {
            data = FileUtils.loadFile(filename);
        }
        catch (Exception e)
        {
            throw new InitialisationException("Unable to load problem file: " + filename, e);
        }
        // split the loaded data file into lines
        String [] lines = data.split("\n");
        // process the header data
        name = lines[0].trim();
        singlePatternWidth = Integer.parseInt(lines[1].trim());
        singlePatternHeight = Integer.parseInt(lines[2].trim());
        int numPatterns = Integer.parseInt(lines[3].trim());
        int patternNumber = 0;
        // prepare expected pattern matrix
        int patternLength = singlePatternWidth * singlePatternHeight;
        patterns = new boolean[numPatterns][patternLength];
        
        // process character lines
        for (int i = 4; i < lines.length; i++)
        {
            String line = lines[i];
            if(line == null || (line=line.trim()).length()==0)
            {
                continue;
            }
            String [] patternElements = line.split(",");
            if(patternElements.length != patternLength)
            {
                throw new InitialisationException("Pattern length "+patternElements.length+" does match expected " + patternLength +"\n"+line);
            }
            // parse
            for (int j = 0; j < patternElements.length; j++)
            {
                boolean b = false;
                if(patternElements[j].equals("0"))
                {
                    b = false;
                }
                else if(patternElements[j].equals("1"))
                {
                    b = true;
                }
                else
                {
                    throw new AlgorithmRunException("Pattern element is not binary (0,1) " + patternElements[j]);
                }
                patterns[patternNumber][j] = b;
            }
            patternNumber++;
        }
        
        // ensure we have loaded enough
        if(patternNumber != patterns.length)
        {
            throw new InitialisationException("Failed to load the expected number of patterns "+patterns.length+", loaded " + patternNumber); 
        }
        isLoaded = true;
    }
    
    
    /**
     * Generic affinity function (binary distance)
     * @param pattern
     * @param response
     * @return
     */
    public double matchFunction(boolean [] pattern, boolean [] response)
    {
        if(pattern==response)
        {
            throw new AlgorithmRunException("System is calculating affinity for identical objects.");
        }
        else if(pattern.length != response.length)
        {
            throw new AlgorithmRunException("Unable to calculate match, string is unexpected length " + response.length);
        }
        
        return BitStringUtils.hammingDistance(pattern, response);
    }
    
    protected class HammingMatchFunction implements MatchFunction
    {
        protected int patternIndex;
        
        public double match(boolean[] pattern)
        {
            return matchFunction(pattern, patterns[patternIndex]);
        }        
    }
    
    @Override
    protected double problemSpecificCost(Solution solution)
    {
        BCRSolution system = (BCRSolution) solution;
        double [] errors = new double[patterns.length];
        HammingMatchFunction affinity = new HammingMatchFunction();
        
        // run a single epoch
        for (int i = 0; i < errors.length; i++, affinity.patternIndex++)
        {
            // expose the system
            boolean [] systemResponse = system.response(affinity, this); 
            // record the affinity of the response
            errors[i] = matchFunction(systemResponse, patterns[i]);
        }
        // return a summation of the error (average)
        return MeasureUtils.calculateAE(errors);
    }
    
    @Override
    public void checkSolutionForSafety(Solution solution) throws AlgorithmRunException
    {}
    
    
    public boolean [][] linearPatternToMatrix(boolean [] p)
    {
        boolean [][] matrix = new boolean[singlePatternWidth][singlePatternHeight];
        
        int offset = 0;
        for (int x = 0; x < matrix.length; x++)
        {
            for (int y = 0; y < matrix[x].length; y++)
            {
                matrix[x][y] = p[offset++];
            }
        }
        
        return matrix;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDetails()
    {
        StringBuffer b = new StringBuffer();
        b.append(super.getDetails()+", ");        
        b.append("PatternWidth="+singlePatternWidth+", ");
        b.append("PatternHeight="+singlePatternHeight+", ");
        b.append("NumPatterns="+getTotalPatterns()+", ");
        b.append("PatternLength="+(singlePatternWidth*singlePatternHeight)+".");        
        return b.toString();
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }
    
    public int getTotalPatterns()
    {
        return patterns.length;
    }
    
    public int getPatternLength()
    {
        return (singlePatternWidth*singlePatternHeight);
    }
    
    public boolean [][] getTrainingPatterns()
    {
        return patterns;
    }

    public int getSinglePatternWidth()
    {
        return singlePatternWidth;
    }

    public int getSinglePatternHeight()
    {
        return singlePatternHeight;
    }

    public String getFilename()
    {
        return filename;
    }
    
    public static void main(String[] args)
    {
    	BCRProblem p = new BCRProblem("bcr/lippman.dat");		
		System.out.println(p.getDetails());
	}
}

