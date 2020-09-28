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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import com.oat.AlgorithmRunException;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: TSPUtils<br/>
 * Date: 07/12/2006<br/>
 * <br/>
 * Description: Utility functions relevant to the TSP problem domain
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class TSPUtils
{     
    public final static double SELECTED = -1;
    
    
    
    /**
     * An implementation of the random proprtional rule to construct a valid TSP permutation
     * Contains lots of safety checks that throw AlgorithmRunException
     * 
     * @param distanceMatrix - distance matrix (euclidean distances)
     * @param heuristicContribution - traditional beta parameter
     * @param historyMatrix - pheromone matrix
     * @param historyContribution - traditional alpha parameter
     * @param rand
     * @return
     */
     public static int [] probabilisticStepwiseConstruction(
             double [][] distanceMatrix, 
             double heuristicContribution,
             double [][] historyMatrix, 
             double historyContribution,
             Random rand)
         throws AlgorithmRunException
     {
         return probabilisticStepwiseConstruction(distanceMatrix, heuristicContribution, historyMatrix, 
                 historyContribution, -1, rand); // -1 will never trigger the greedyness factor
     }
    
   /**
    * An implementation of the random proprtional rule to construct a valid TSP permutation
    * Contains lots of safety checks that throw AlgorithmRunException
    * 
    * @param distanceMatrix - distance matrix (euclidean distances)
    * @param heuristicContribution - traditional beta parameter
    * @param historyMatrix - pheromone matrix
    * @param historyContribution - traditional alpha parameter
    * @param greedynessFactor - used in ACS as a probability of not using the random proportional rule, 
    * and instead selecting the best learned, supply a negative value of (-1) for this not to be used
    * @param rand - tandom number generator
    * @return
    */
    public static int [] probabilisticStepwiseConstruction(
            double [][] distanceMatrix,
            double heuristicContribution,// beta
            double [][] historyMatrix,
            double historyContribution, // alpha
            double greedynessFactor,
            Random rand)
        throws AlgorithmRunException
    {
        if(!AlgorithmUtils.inBounds(greedynessFactor, -1, 1))
        {
            throw new AlgorithmRunException("Greedyness factor not nin bounds [0,1] " + greedynessFactor);
        }
        
        int [] permutation = new int[distanceMatrix.length];
        permutation[0] = rand.nextInt(permutation.length);
        double [][] probabilityMatrix = new double[distanceMatrix.length][distanceMatrix[0].length];
        
        // build a probability matrix and populate it        
        for (int i = 0; i < probabilityMatrix.length; i++)
        {            
            for (int j = 0; j < probabilityMatrix[i].length; j++)
            {    
                // cannot select first city
                if(j == permutation[0])
                {
                    probabilityMatrix[i][j] = SELECTED;
                }
                else
                {
                    // use random proportional rule
                    probabilityMatrix[i][j] = calculateUnNormalizedProbability(i,j,distanceMatrix,heuristicContribution,historyMatrix,historyContribution);
                }
            }
        }
        
        // make selections        
        for (int i = 1; i < permutation.length; i++)
        {
            int lastCity = permutation[i-1];
            int selectedCity = -1;
            
            // check for greedy
            boolean isGreedyDecision = (rand.nextDouble() <= greedynessFactor) ? true : false;
            // check for greedy decision
            // this is a feature used in ACS where the largest probability step
            // max(history * (heuristic^beta)) for all i,j in this step
            if(isGreedyDecision)
            {
                double [] nextStep = new double[probabilityMatrix.length];                
                for (int j = 0; j < nextStep.length; j++)
                {
                    nextStep[j] = probabilityMatrix[lastCity][j];
                    if(probabilityMatrix[lastCity][j] != SELECTED)
                    {
                        // (history^1.0) is the same as (history)
                        nextStep[j] = calculateUnNormalizedProbability(i,j,distanceMatrix,heuristicContribution,historyMatrix,1.0);
                        if(selectedCity == -1 || nextStep[j] > nextStep[selectedCity])
                        {
                            selectedCity = j; // select max
                        }
                    }
                }
            }
            // traditional random proprtional rule
            else
            {
                selectedCity = randomProprtionalSelectCity(lastCity, probabilityMatrix, rand);
            }

            // check for the in-ability to make a selection
            if(selectedCity == -1)
            {
                throw new AlgorithmRunException("Failed to make selection "+i+"/"+permutation.length);
            }                        
            // massive super-duper safety - ensure there are no double ups
            if(ArrayUtils.permutationContainsValue(permutation, i, selectedCity))
            {
                throw new AlgorithmRunException("Attempted to make same selection twice, selection["+selectedCity+"].\n " + Arrays.toString(permutation));
            }
            
            // assign
            permutation[i] = selectedCity;
            
            // ensure we can never move from any city to this city again
            for (int j = 0; j < probabilityMatrix.length; j++)
            {
                probabilityMatrix[j][selectedCity] = SELECTED;
            }
        }
        
        return permutation;
    }
    
    private static class RPR
    {
        int cityNumber;
        double probability;
        double normalisedProbability;
        
        public RPR(int city, double prob)
        {
            cityNumber = city;
            probability = prob;
        }
    }
    
    public static int randomProprtionalSelectCity(
            int lastCity,
            double [][] probabilityMatrix,
            Random rand)
    {
        int selectedCity = -1;
        
        // build a list of choices
        LinkedList<RPR> choices = new LinkedList<RPR>();
        double sumProbability = 0.0;
        for (int j = 0; j < probabilityMatrix.length; j++)
        {
            if(probabilityMatrix[lastCity][j] != SELECTED)
            {
                choices.add(new RPR(j, probabilityMatrix[lastCity][j]));
                sumProbability += probabilityMatrix[lastCity][j];
            }
        }
        // ensure there is something to choose from
        if(choices.isEmpty())
        {
            // should never occur
            throw new RuntimeException("Unable to make decision, nothing to choose from!"); 
        }                 
        // all choices are zero - select randomly
        if(sumProbability == 0.0)
        {
            int index = (choices.size() == 1) ? 0 : rand.nextInt(choices.size());
            selectedCity = choices.get(index).cityNumber;                
        }
        // normalise probabilities and make a selection            
        else
        {
            double v = rand.nextDouble();
            for (int j = 0; selectedCity==-1 && j < choices.size(); j++)
            {
                // check for condition where we are selecting the last in the list
                if(j == choices.size()-1)
                {
                    selectedCity = choices.get(j).cityNumber;
                }                    
                v -= (choices.get(j).probability/sumProbability);
                if(v <= 0.0)
                {
                    selectedCity = choices.get(j).cityNumber;
                }
            }
        }
        
        return selectedCity;
    }
    
    /**
     * Calculate an unnormalized probability for the ACO random proprtional rule
     * @param city1
     * @param city2
     * @param distanceMatrix
     * @param heuristicContribution
     * @param historyMatrix
     * @param historyContribution
     * @return
     */
    public static double calculateUnNormalizedProbability(
            int city1,
            int city2,
            double [][] distanceMatrix, // heuristic (euclidean)
            double heuristicContribution,// beta
            double [][] historyMatrix, // pheromone 
            double historyContribution) // alpha
    {
        // never move to self
        if(city1 == city2)
        {
            return SELECTED;
        }
        
        double history = Math.pow(historyMatrix[city1][city2], historyContribution);
        double dist = 0.0;
        // ensure we never divide by zero (two points on the same position)
        if(distanceMatrix[city1][city2] != 0)
        {
            dist = 1.0/distanceMatrix[city1][city2];
        }        
        double heuristic = Math.pow(dist, heuristicContribution);
        double prob = history * heuristic;
        if(AlgorithmUtils.isInvalidNumber(prob))
        {
            throw new AlgorithmRunException("Probability out of bounds: "+prob+", " +
                    "history["+history+"], historyMatrixValue["+historyMatrix[city1][city2]+"], historyContribution["+historyContribution+"], " +
                    "heuristic["+heuristic+"], distance["+distanceMatrix[city1][city2]+"], heuristicContribution["+heuristicContribution+"].");
        }
        
        return prob;
    }
    
    
    
    /**
     * Generate a determanistic nearest neighbour solution to the specified TSP problem.
     * Uses the ranom number generator to select a random starting city
     * @param p
     * @param r
     * @return
     */
    public final static TSPSolution generateNearestNeighbourSolution(TSPProblem p, Random r)
    {
        double [][] distanceMatrix = p.getDistanceMatrix();
        int [] permutation = new int[distanceMatrix.length];
        HashSet<Integer> set = new HashSet<Integer>();
        
        permutation[0] = r.nextInt(permutation.length);
        set.add(new Integer(permutation[0]));
        
        for (int i = 1; i < permutation.length; i++)
        {
            // select the best neighbour
            double min = Double.POSITIVE_INFINITY;
            Integer best = null;
            for (int j = 0; j < distanceMatrix[i].length; j++)
            {
                if(!set.contains(j))
                {
                    if(distanceMatrix[i][j] < min)
                    {
                        min = distanceMatrix[i][j];
                        best = j;
                    }
                }
            }
            // safety check, ensure that a city was selected
            if(best == null)
            {
                throw new AlgorithmRunException("Failed to select city in NN solution generation, city["+i+"] of " + permutation.length);
            }
            set.add(best); // cannot revisit this city
            permutation[i] = best.intValue();
        }        
        
        return new TSPSolution(permutation);
    }    
    
    /**
     * Edge re-combination approach for creating a new tour.
     * Taken from Evolutionary Computation 1.
     * This is a hack, but it seems to work. 
     * 
     * @param p1
     * @param p2
     * @param r
     * @return
     */
    public final static TSPSolution edgeRecombination(
            TSPSolution p1, TSPSolution p2, Random r)
    {
        int [] v1 = p1.getPermutation();
        int [] v2 = p2.getPermutation(); 
        int totalCitites = v1.length;
        
        int [][] matrix = new int[totalCitites][totalCitites];
        // process the parents
        for (int i = 0; i < v1.length; i++)
        {
            int x = v1[i];
            int y = -1;
            if(i==v1.length-1)
            {
                y = v1[0];
            }
            else
            {
                y = v1[i+1];
            }
            matrix[x][y]++;
            matrix[y][x]++;
        }
        for (int i = 0; i < v2.length; i++)
        {
            int x = v2[i];
            int y = -1;
            if(i==v2.length-1)
            {
                y = v2[0];
            }
            else
            {
                y = v2[i+1];
            }
            matrix[x][y]++;
            matrix[y][x]++;
        }
        HashSet<Integer> set = new HashSet<Integer>();
        int [] c = new int[totalCitites];
        // pick a random initial random city
        c[0] = r.nextInt(totalCitites);
        set.add(c[0]);
        // clear this city 
        for (int k = 0; k < matrix.length; k++)
        {
            matrix[k][c[0]] = 0;
        }        
        
        for (int i = 1; i < c.length; i++)
        {
            LinkedList<Integer> list = new LinkedList<Integer>();
            boolean finished = false;
            
            // check the current edge list for a shared edge
            for (int j = 0; !finished && j < matrix[c[i-1]].length; j++)
            {
                // got one
                if(matrix[c[i-1]][j] > 1)
                {
                    c[i] = j; // take it     
                    if(set.contains(c[i])){throw new AlgorithmRunException("Invalid permutation!");}
                    set.add(c[i]);
                    // clear from the entire matrix
                    for (int k = 0; k < matrix.length; k++)
                    {
                        matrix[k][j] = 0;
                    }
                    finished = true;
                }
                else if(matrix[c[i-1]][j] != 0)
                {
                    list.add(new Integer(j));
                }
            }
            if(finished)
            {
                continue; // next city
            }
            // check for an empty terminal
            if(list.isEmpty())
            {
//                LinkedList<Integer> listWithOutbound = new LinkedList<Integer>();
                
                // build a list of cities not visited, and select one as a new starting point
                for (int k = 0; k < matrix.length; k++)
                {
                    if(!set.contains(k))
                    {
                        list.add(k);
                        // process looking for any neighbours
//                        for (int j = 0; j < matrix[k].length; j++)
//                        {
//                            if(matrix[k][j] > 0)
//                            {
//                                listWithOutbound.add(k);
//                                break;
//                            }
//                        }
                    }
                }
                if(list.isEmpty())
                {
                    throw new AlgorithmRunException("No cities remaining i["+i+"], l["+(c.length-1)+"]");
                }
                
                // randomly pick one of the adjacent cities
//                Integer s = (listWithOutbound.isEmpty()) ? list.get(r.nextInt(list.size())) : listWithOutbound.get(r.nextInt(listWithOutbound.size()));
                Integer s = list.get(r.nextInt(list.size()));
                c[i] = s.intValue(); // take it
                if(set.contains(c[i])){throw new AlgorithmRunException("Invalid permutation!");}
                set.add(c[i]);
                // clear 
                for (int k = 0; k < matrix.length; k++)
                {
                    matrix[k][s.intValue()] = 0;
                }                
            }
            // the case where there is a connection            
            else
            {
                // randomly pick one of the adjacent cities
                Integer s = list.get(r.nextInt(list.size()));
                c[i] = s.intValue(); // take it
                if(set.contains(c[i])){throw new AlgorithmRunException("Invalid permutation!");}
                set.add(c[i]);
                // clear 
                for (int k = 0; k < matrix.length; k++)
                {
                    matrix[k][s.intValue()] = 0;
                }
            }            
        }
        
        return new TSPSolution(c);
    }
    
    /**
     * Complets the provided tour
     * @param p
     * @param offset
     * @param r
     */
    public final static void completeTour(int [] p , int offset, Random r)
    {
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < offset; i++)
        {
            set.add(new Integer(p[i]));
        }
        // now build list of all remaining citites
        LinkedList<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < p.length; i++)
        {
            Integer a = new Integer(i);
            if(!set.contains(a))
            {
                list.add(a);
            }
        }
        for (int i = offset; i < p.length; i++)
        {
            Integer s = list.remove(r.nextInt(list.size()));
            p[i] = s.intValue();            
        }
    }
    
    /**
     * Performs genetic algorithms crossover on the permutation, assuring that the 
     * created permutation is valid
     * 
     * @param p1
     * @param p2
     * @param r
     * @param probability
     * @return
     */
    public final static TSPSolution [] simpleCrossover(TSPSolution p1, TSPSolution p2, Random r, double probability)
    {    
        int [] v1 = p1.getPermutation();
        int [] v2 = p2.getPermutation();     
        // select cross point, or not as it were
        int cutPoint = (r.nextDouble()<probability) ? r.nextInt(v1.length) : 0;        
        // perform crossover
        int [] c1 = getCrossedPermutation(v1, v2, cutPoint);
        int [] c2 = getCrossedPermutation(v2, v1, cutPoint); 
        // return progeny
        return new TSPSolution[]{new TSPSolution(c1),new TSPSolution(c2)};
    }
    /**
     * Creates a permutation, and repairs it when invalid
     * @param d1
     * @param d2
     * @param point
     * @return
     */
    public final static int [] getCrossedPermutation(
            int [] d1, 
            int [] d2, 
            int point)
    {
        int [] data = new int[d1.length];
        
        // copy the first block
        for (int i = 0; i < point; i++)
        {
            data[i] = d1[i];
        }
        
        // copy the remaining from the other        
        for (int i = point, offset = point; i < data.length; )
        {
            int next = d2[offset++];
            if(offset >= data.length)
            {
                offset = 0;
            }
            // check if the piece is useful
            if(!ArrayUtils.permutationContainsValue(data, i, next))
            {
                data[i++] = next;
            }
        } 
        return data;
    }    


    /**
     * Perform the two-opt procedure on the provided permutation. This procedure only occurs 
     * with the the specified probability.
     * 
     * The procedure selects two different points in the permutation then reverses the data
     * between those two points
     * 
     * @param permutation
     * @param rand
     * @param probability
     */
    public final static void twoOpt(int [] permutation, Random rand, double probability)
    {
        if(rand.nextDouble() <= probability)
        {
            int c1 = rand.nextInt(permutation.length);
            int c2 = -1;
            // run until we have a valid c2
            do
            {
                c2 = rand.nextInt(permutation.length);
            }
            while (c2 == c1);
            
            // ensure c1 is low, and c2 is high
            if(c1 > c2)
            {
                int a = c2;
                c2 = c1;
                c1 = a;
            }            
            // now reverse the permutation between the points
            int half = (int) Math.floor((c2-c1)/2.0);
            for (int i = 0; i <= half; i++)
            {
                ArrayUtils.swap(c1+i, c2-i, permutation);
            }
        }
    }
    
    
    /**
     * Rotates the specified permutation
     * Perform a rotation to align a permutation by city 0 then by the next smallest city
     * e.g. 1,2,3,4,5,0 => 0,1,2,3,4,5; 5,4,3,2,1,0 => 0,1,2,3,4,5
     * @param perm
     */
    public final static void rotatePermutation(int[] perm)
    {
    	//offset array by the zero entry
    	int zeroindex = 0;
    	for (int i = 0; i < perm.length; i++) {
			if (perm[i] == 0){
				zeroindex = i;
			}
		}
    	int [] tempArray = new int[perm.length];
    	for (int i = 0; i < tempArray.length; i++) {
			if(zeroindex > perm.length-1){
				zeroindex = 0;
			}
    		tempArray[i] = perm[zeroindex];
    		zeroindex++;
		}
    	//if array is of order 0,3,2,1 then flip to 0,1,2,3
    	if (perm.length > 2)
    	{
    		if (tempArray[1] > tempArray[tempArray.length-1])
    		{
//    			 now reverse the permutation between the points
                int half = (int) Math.floor((tempArray.length-2)/2.0);
                for (int i = 0; i <= half; i++)
                {
                    ArrayUtils.swap(1+i, tempArray.length-1-i, tempArray);
                }
    		}
    	}
    	System.arraycopy(tempArray,0,perm,0,perm.length);
    }
    
//    /**
//     * Testing of the rotation
//     * @param args
//     */
//    public static void main(String[] args)
//    {
//    	int[] perm = {0,1,2,3,4,5,6,7,8,9,10};
//    	TSPSolution s = new TSPSolution(perm);
//    	Random r = new Random();
//    	String temp = "";
//    	for (int i = 0; i < perm.length-1; i++) {
//			temp += Integer.toString(perm[i]);
//			temp += ", ";
//		}
//    	temp += Integer.toString(perm[perm.length-1]);
//    	System.out.println(temp);
//    	temp = "";
//    	twoOpt(s,r,1);
//    	for (int i = 0; i < perm.length-1; i++) {
//			temp += Integer.toString(perm[i]);
//			temp += ", ";
//		}
//    	temp += Integer.toString(perm[perm.length-1]);
//    	System.out.println(temp);
//    }

    /**
     * 
     * @param p
     * @param r
     * @return
     */
    public final static int[] generateRandomVector(TSPProblem p, Random r)
    {
        return RandomUtils.generateRandomVector(p.getCities().length, r);
    }

    /**
     * Generate a random coordinate within the bounds of the problem domain
     * 
     * @param p
     * @param r
     * @return
     */
    public final static TSPSolution generateRandomSolution(TSPProblem p, Random r)
    {
        return new TSPSolution(generateRandomVector(p,r));
    }
}
