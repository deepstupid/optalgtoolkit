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
package com.oat.domains.tsp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import com.oat.AlgorithmRunException;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.utils.FileUtils;


/**
 * Type: Problem<br/>
 * Date: 27/03/2006<br/>
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
public class TSPProblem extends Problem
{
    public final static String KEY_TOUR_START = "TOUR_SECTION";
    public final static String KEY_START_CITIES = "NODE_COORD_SECTION";
    public final static String KEY_DIMENSIONS = "DIMENSION";
    public final static String KEY_NAME = "NAME";
    public final static String KEY_DISTANCE = "EDGE_WEIGHT_TYPE";
    
    public final static String KEY_DISTANCE_EUCLIDEAN = "EUC_2D";
    public final static String KEY_DISTANCE_GEOGRAPHICAL = "GEO";
    
    public final static double RRR = 6378.388;

    public static enum DISTANCE_TYPE
    {
        EUCLIDEAN, GEOGRAPHICAL
    }
    
    
    // configurable
    protected String problemFilename;
    protected String solutionFilename;
    
    // loaded
    protected boolean isLoaded = false;
    protected DISTANCE_TYPE distanceType;
    protected String name;    
    protected int[] solutionCityList;
    protected double solutionTourLength;
    protected double[][] cities;
    protected double[][] distanceMatrix;
    
    

    
    public TSPProblem(String aProblemFile, String aSolutionFile)
    {
    	setProblemFilename(aProblemFile);
    	setSolutionFilename(aSolutionFile);
    	
    	try
    	{
    		initialiseBeforeRun();
    		cleanupAfterRun();
    	}
    	catch(InitialisationException e)
    	{
    		throw new RuntimeException(e);
    	}
    	
    }
    
    public TSPProblem()
    {}
    
    
	@Override
    public void initialiseBeforeRun()
    	throws InitialisationException
	{
    	if(!isLoaded)
    	{
	    	// load the problem
	    	loadProblem();
	        // calculate distances once
	        prepareDistanceMatrix();
	    	// load the solution
	    	loadSolution();
    	}
    	else
    	{
    		// calculate distances once
    		prepareDistanceMatrix();
    	}
	}
    
	@Override
    public void cleanupAfterRun()
    	throws InitialisationException
	{
		distanceMatrix = null;
	}
	
   
    public boolean isLoaded()
	{
		return isLoaded;
	}

	public void setProblemFilename(String f)
	{		
		this.problemFilename = f;
	}

	public void setSolutionFilename(String f)
	{		
		this.solutionFilename = f;
	}

	/**
     * Load the problem's solution
     * @throws Exception
     */
    protected void loadSolution()
        throws InitialisationException
    {
        // load the data
        String d = null;
        try
        {
        	d = FileUtils.loadFile(solutionFilename);     
        }
        catch(IOException e)
        {
        	throw new InitialisationException("Error loading dataset "+solutionFilename+": " + e.getMessage(), e);
        }
        
        String [] lines = d.trim().split("\n");
        
        solutionCityList = new int[cities.length];
        boolean isProcessingCities = false;
        int cityOffset = 0;
        
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i].trim();
            
            if(isProcessingCities)
            {
                solutionCityList[cityOffset] = Integer.parseInt(line.trim());
                solutionCityList[cityOffset]--; // definition is 1 offset, make 0 offset
                if(++cityOffset >= solutionCityList.length)
                {
                    break; // finished
                } 
            }
            else if(line.equalsIgnoreCase(KEY_TOUR_START))
            {
                isProcessingCities = true;
            }
        }
        solutionTourLength = problemSpecificCost(solutionCityList);
        isLoaded = true;
    }
    
    
    
    protected void loadProblem()
        throws InitialisationException
    {
        // load the data
        String d = null;
        try
        {
        	d = FileUtils.loadFile(problemFilename);
        }
        catch(IOException e)
        {
        	throw new InitialisationException("Error loading dataset "+problemFilename+": " + e.getMessage(), e);
        }
        String [] lines = d.trim().split("\n");
       
        int dimensions = 0;
        boolean isProcessingCities = false;
        int cityOffset = 0;
        
        // process all lines
        for (int i = 0; i < lines.length; i++)
        {        
            String line = lines[i].trim();
            
            if(isProcessingCities)
            {                
                String [] tmp = line.split(" ");
                String [] parts = new String[3];
                int o = 0;
                for (int j = 0; j < tmp.length; j++)
                {
                    if(tmp[j] != null && (tmp[j]=tmp[j].trim()).length()>0)
                    {
                        parts[o++] = tmp[j];
                    }
                }                
                if(o != 3)
                {
                    throw new InitialisationException("Unexpected line while processing cities line["+i+"]: " + line);
                }
                // load in city data
                cities[cityOffset][0] = Double.parseDouble(parts[1].trim());
                cities[cityOffset][1] = Double.parseDouble(parts[2].trim());                
                if(++cityOffset >= dimensions)
                {
                    break; // finished
                }                
            }
            else if(line.trim().equalsIgnoreCase(KEY_START_CITIES))
            {
                if(dimensions <= 0)
                {
                    throw new InitialisationException("Reached city nodes before dimensionality was defined!");
                }
                
                isProcessingCities = true;
                cities = new double[dimensions][2];
            }
            else
            {
                String [] parts = line.split(":");                

                if(parts[0].trim().equalsIgnoreCase(KEY_NAME))
                {                    
                    name = parts[1].trim();
                }
                else if(parts[0].trim().equalsIgnoreCase(KEY_DIMENSIONS))
                {
                    dimensions = Integer.parseInt(parts[1].trim());
                }
                else if(parts[0].trim().equalsIgnoreCase(KEY_DISTANCE))
                {
                    String dist = parts[1].trim();
                    if(dist.equalsIgnoreCase(KEY_DISTANCE_EUCLIDEAN))
                    {
                        distanceType = DISTANCE_TYPE.EUCLIDEAN;
                    }
                    else if(dist.equalsIgnoreCase(KEY_DISTANCE_GEOGRAPHICAL))
                    {
                        distanceType = DISTANCE_TYPE.GEOGRAPHICAL;
                    }
                    else
                    {
                        throw new InitialisationException("Unknown distance type: " + dist);
                    }
                }
            }
        } 
    }
    

    @Override
    public String getDetails()
    {
        StringBuffer b = new StringBuffer();
        b.append(super.getDetails()+", ");
        b.append("ProblemFilename="+problemFilename+", ");
        b.append("SolutionFilename="+solutionFilename+", ");
        b.append("TotalCities="+solutionCityList.length+", ");
        b.append("DistanceType="+distanceType.name()+", ");
        b.append("OptimalTourLength="+solutionTourLength+".");
        return b.toString();
    }
   
    protected void prepareDistanceMatrix()
    {
        distanceMatrix = new double[cities.length][cities.length];
        
        for (int x = 0; x < cities.length; x++)
        {
            for (int y = 0; y < cities.length; y++)
            {
                distanceMatrix[x][y] = distance(x, y);
            }
        }
    }
    
    @Override
    protected double problemSpecificCost(Solution s)
    {
        return problemSpecificCost(((TSPSolution)s).getPermutation());
    }

    public double problemSpecificCost(int [] p)
    {              
        double sum = 0.0; 
        // do all cities
        for (int i = 1; i < p.length; i++)
        {
            sum += distanceMatrix[p[i-1]][p[i]];
        }
        // do the end to the start
        sum += distanceMatrix[p[p.length-1]][p[0]];
        return sum;
    }
    
    @Override
    public void checkSolutionForSafety(Solution solution) 
        throws AlgorithmRunException
    {
        checkSolutionForSafety(((TSPSolution)solution).getPermutation());
    }
    
    public void checkSolutionForSafety(int [] v)
    {
        if(v.length != cities.length)
        {
            throw new AlgorithmRunException("Length of tour permutation is unexpected " + v.length +", expected " + cities.length);
        }  
        
        HashSet<Integer> set = new HashSet<Integer>(v.length);
        
        for (int i = 0; i < v.length; i++)
        {
            if(set.contains(v[i]))
            {
                throw new AlgorithmRunException("Invalid TSP permutation, found ["+v[i]+"] twice:\n"+Arrays.toString(v));
            }
            else if(v[i]<0||v[i]>=v.length)
            {
                throw new AlgorithmRunException("Invalid TSP permutation, contains invalid city number.\n"+Arrays.toString(v));
            }
            
            set.add(v[i]);
        }
    }
    
    protected double distance(int c1, int c2)
    {
        double d = 0.0;
        
        switch(distanceType)        
        {
            case EUCLIDEAN:
            {
                d = euclideanDistance(c1,c2);
                break;
            }        
            case GEOGRAPHICAL:
            {
                d = geographicalDistance(c1,c2);
                break;
            }   
            default:
            {
                throw new AlgorithmRunException("Unknown distance type: " + distanceType);
            }
        }
        
        return d;
    }
    
    
    
    /**
     * As defined in TSPLIB'95 (GEO)
     * Changed round's to floor's because we want the minutes component, 
     *  not the closest integer
     * 
     * @param c1
     * @param c2
     * @return
     */
    protected double geographicalDistance(int c1, int c2)
    {
        double latitude1 = Math.PI * (Math.floor(cities[c1][0]) + 5.0 * (cities[c1][0]-Math.floor(cities[c1][0])) / 3.0) / 180.0; // [c1]x
        double longitude1 = Math.PI * (Math.floor(cities[c1][1]) + 5.0 * (cities[c1][1]-Math.floor(cities[c1][1])) / 3.0) / 180.0; // [c1]y
        double latitude2 = Math.PI * (Math.floor(cities[c2][0]) + 5.0 * (cities[c2][0]-Math.floor(cities[c2][0])) / 3.0) / 180.0; // [c2]x
        double longitude2 = Math.PI * (Math.floor(cities[c2][1]) + 5.0 * (cities[c2][1]-Math.floor(cities[c2][1])) / 3.0) / 180.0; // [c2]y
        
        double q1 = Math.cos(longitude1 - longitude2);
        double q2 = Math.cos(latitude1 - latitude2);
        double q3 = Math.cos(latitude1 + latitude2);
        double dij = (int) (RRR * Math.acos(0.5*((1.0+q1)*q2 - (1.0-q1)*q3)) + 1.0);        
        return dij;
    }
    
    /**
     * As defined in TSPLIB'95 (EUC_2D)
     * @param c1
     * @param c2
     * @return
     */
    protected double euclideanDistance(int c1, int c2)
    {
        double xd = cities[c1][0] - cities[c2][0];
        double yd = cities[c1][1] - cities[c2][1];
        double dij = Math.sqrt((xd*xd + yd*yd));
        dij = Math.round(dij); // whatever...
        return dij;
    }  
    

    
    
    public double[][] getCities()
    {
        return cities;
    }

    public double[][] getDistanceMatrix()
    {
        return distanceMatrix;
    }

    public DISTANCE_TYPE getDistanceType()
    {
        return distanceType;
    }

    public String getProblemFilename()
    {
        return problemFilename;
    }

    public int[] getSolutionCityList()
    {
        return solutionCityList;
    }

    public String getSolutionFilename()
    {
        return solutionFilename;
    }

    public double getSolutionTourLength()
    {
        return solutionTourLength;
    }
    
    public int getTotalCities()
    {
        return cities.length;
    }

    @Override
    public String getName()
    {
    	if(isLoaded)
    	{
    		return name;
    	}
        
    	return "TSP";
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    
    
    public static void main(String[] args)
    {
        TSPProblem p = new TSPProblem("tsp/tsp225.tsp", "tsp/tsp225.opt.tour");
		System.out.println(p.getDetails());
		
    }
}
