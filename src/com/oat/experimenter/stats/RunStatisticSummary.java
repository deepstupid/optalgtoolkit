/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

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
package com.oat.experimenter.stats;

import java.text.DecimalFormat;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.Reportable;
import com.oat.experimenter.RunResult;
import com.oat.utils.FileUtils;

/**
 * Date: 03/08/2007<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class RunStatisticSummary
	implements Reportable
{
	// details
	protected double [] rawResults;
	protected String experimentalRunName;
	protected String statisticName;
	
	// summary
	protected DescriptiveStatistics descriptiveStatistics;
	protected int numRecords;  
	
    // simple stats      
    protected double min;
    protected double max;    
    protected double mean;
    protected double stdev;
    
    // other stats
    protected double skewness;
    protected double variance;
    protected double kurtosis;


    
    public void calculate(String filename, int fieldIndex)    
    	throws AnalysisException
    {
    	double [][] results = null;
    	
    	try
		{
    		results = FileUtils.loadCSV(filename);
		}
		catch (Exception e)
		{
			throw new AnalysisException("Failed loading CSV: " + filename, e);
		}
		
		// convert to sample
		double [] sample = csvDataToSample(results, fieldIndex);
		// calculate
		calculate(sample, filename, "Field"+fieldIndex);
    }
    
    
    public void calculate(double [][] csvData, String name, int fieldIndex)    
		throws AnalysisException
	{		
		// convert to sample
		double [] sample = csvDataToSample(csvData, fieldIndex);
		// calculate
		calculate(sample, name, "Field"+fieldIndex);
	}    
    
    
    public static double [] csvDataToSample(double [][] csvData, int selectedVariable)
    {
    	// convert into samples we can work with
    	double [] sample = new double[csvData.length];
    	for (int line = 0; line < sample.length; line++)
		{
    		sample[line] = csvData[line][selectedVariable];
		}    	
    	return sample;
    }
    
    
    public void calculate(double [] results)
    {
    	calculate(results, "Run", "Statistic");
    }
    
    public void calculate(double [] aResults, String aRunName, String aStatisticName)
    {
    	// store things
    	rawResults = aResults;
    	experimentalRunName = aRunName;
    	statisticName = aStatisticName;
        
    	// summarize
    	numRecords = rawResults.length;
    	descriptiveStatistics = DescriptiveStatistics.newInstance();
        for (int i = 0; i < rawResults.length; i++)
        {
        	descriptiveStatistics.addValue(rawResults[i]);
        }
        
        // simple stats        
        min = descriptiveStatistics.getMin();
        max = descriptiveStatistics.getMax();
        mean = descriptiveStatistics.getMean();
        stdev = descriptiveStatistics.getStandardDeviation();
        
        // other stats
        skewness = descriptiveStatistics.getSkewness();
        variance = descriptiveStatistics.getVariance();
        kurtosis = descriptiveStatistics.getKurtosis(); 
    }
    
    
    public void calculate(
    		ExperimentalRun aRun, 
    		RunResult [] stats, 
    		String selectedStatistic)
    	throws AnalysisException
    {
    	
    	double [] aResults = toDoubles(stats, selectedStatistic);  
    	String runName = aRun.getId();
    	calculate(aResults, runName, selectedStatistic);
    }
    
    
	@Override
	public String[][] prepareReport()
	{
    	DecimalFormat f = (DecimalFormat) DecimalFormat.getInstance();
        LinkedList<String[]> report = new LinkedList<String[]>();
        
        report.add(new String[]{"Test", "Summary Statistics"});
        
        // summary
        report.add(new String[]{"Experimental Run Name", experimentalRunName});
        report.add(new String[]{"Statistic", statisticName});
        report.add(new String[]{"Total Records", f.format(numRecords)});
        // simple stats
        report.add(new String[]{"Min", f.format(min)});
        report.add(new String[]{"Max", f.format(max)});
        report.add(new String[]{"Mean", f.format(mean)});
        report.add(new String[]{"Standard Deviation", f.format(stdev)});
        // other
        report.add(new String[]{"Skewness", f.format(skewness)});
        report.add(new String[]{"Variance", f.format(variance)});
        report.add(new String[]{"Kurtosis", f.format(kurtosis)});
        
        return report.toArray(new String[report.size()][]);
	}
    
    public static double [] toDoubles(RunResult [] stats, String selectedStatistic)
    	throws AnalysisException
    {
    	// check for number
    	try
    	{
    		Double.parseDouble((String)stats[0].getResult(selectedStatistic));
    	}
    	catch(NumberFormatException e)
    	{
    		throw new AnalysisException("Selected statistic "+selectedStatistic+" is not numeric: " + stats[0].getResult(selectedStatistic));
    	}    	
    	
        double [] results = new double[stats.length];
        for (int i = 0; i < results.length; i++)
        {
            results[i] = Double.parseDouble((String)stats[i].getResult(selectedStatistic));
        }
        return results;
    	
    }

	public double[] getRawResults()
	{
		return rawResults;
	}

	public String getExperimentalRunName()
	{
		return experimentalRunName;
	}

	public String getStatisticName()
	{
		return statisticName;
	}

	public DescriptiveStatistics getDescriptiveStatistics()
	{
		return descriptiveStatistics;
	}

	public int getNumRecords()
	{
		return numRecords;
	}

	public double getMin()
	{
		return min;
	}

	public double getMax()
	{
		return max;
	}

	public double getMean()
	{
		return mean;
	}

	public double getStdev()
	{
		return stdev;
	}

	public double getSkewness()
	{
		return skewness;
	}

	public double getVariance()
	{
		return variance;
	}

	public double getKurtosis()
	{
		return kurtosis;
	}
}
