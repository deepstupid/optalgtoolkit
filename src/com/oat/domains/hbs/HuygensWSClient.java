/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006  Jason Brownlee
Copyright (C) 2006  Cara MacNish

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
package com.oat.domains.hbs;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

/**
 * A Java client for webservice access to the Huygens WebServer.<br>
 * Usage:<br>
 * <ol>
 * <li>Create a client
 * <li>Login
 * <li>Initiate a training or benchmark process
 * <li>Request evaluations for the appropriate number of probes (in as many
 * calls as you require)
 * </ol>
 * All methods apart from the constructor and convenience methods call
 * corresponding methods on the server.<br>
 * The server address for manual (browser) use is <a
 * href="http://gungurru.csse.uwa.edu.au/cgi-bin/WebObjects/huygensWS">http://gungurru.csse.uwa.edu.au/cgi-bin/WebObjects/huygensWS</a>.
 * 
 * Jason Brownlee - Changes
 * - removed all writing to the console
 * - adjusted access modifiers on instance and class variables
 * - api for service and call is here: http://ws.apache.org/axis/java/apiDocs/index.html
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class HuygensWSClient
{
    public static final String HUYGENS_WS_ADDRESS = "http://gungurru.csse.uwa.edu.au/cgi-bin/WebObjects/huygensWS.woa/1/ws/huygensWS";
    public static final String VERSION = "v2.1; 21 June 2006";

    protected Service service;
    protected Call call;
    protected Object[] response;

    /**
     * Create a new client. (Step 1)
     */
    public HuygensWSClient() throws Exception
    {        
        //System.out.println("\nHuygens WebService Client " + VERSION + "\n");
        service = new Service();        
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(new java.net.URL(HUYGENS_WS_ADDRESS));
        call.setMaintainSession(true);
    }

    /**
     * Login to the server. (Step 2)
     * 
     * @param email
     *            your email address used as your unique identifier when you
     *            (manually) registered and subsequently login to the Huygens
     *            Server
     * @return Object[0]: a Boolean indicating whether the remote method call
     *         was successful<br>
     *         Object[1]: a status report from the server (all status reports
     *         are of type String)
     */
    public Object[] login(String email) throws Exception
    {
        Object[] args =
        { email };
        response = new Object[2];
        call.setOperationName("login");
        response = (Object[]) call.invoke(args);
        return response;
    }

    /**
     * Ping the server to check the network is available and the server is
     * responding
     * 
     * @return Object[0]: a Boolean indicating whether the remote method call
     *         was successful<br>
     *         Object[1]: a status report from the server
     */
    public Object[] ping() throws Exception
    {
        Object[] args = {};
        call.setOperationName("ping");
        response = (Object[]) call.invoke(args);
        return response;
    }

    /**
     * Initialise a training process
     * 
     * @param series
     *            the landscape series to test (eg 20)
     * @param landscape
     *            the number of the landscape within that series (eg 1)
     * @return Object[0]: a Boolean indicating whether the remote method call
     *         was successful<br>
     *         Object[1]: a status report from the server
     */
    public Object[] startTraining(int series, long landscape) throws Exception
    {
        Object[] args =
        { new Integer(series), new Long(landscape) };
        response = new Object[2];
        call.setOperationName("startTraining");
        response = (Object[]) call.invoke(args);
        return response;
    }

    /**
     * Request evaluations (fitnesses) of points in the training landscape
     * 
     * @param probes
     *            the points to evaluate as an Nx2 array, where N is the number
     *            of points, and each point is represented as a 2-d array of x
     *            and y coordinates.
     * @return Object[0]: a Boolean indicating whether the remote method call
     *         was successful<br>
     *         Object[1]: a status report from the server<br>
     *         Object[2]: an N-d array of doubles containing the evaluations
     *         (fitnesses)<br>
     *         Object[3]: an Integer containing the number of evaluations
     *         completed for the current landscape
     */
    public Object[] train(double[][] probes) throws Exception
    {
        Object[] args =
        { probes };
        call.setOperationName("train");
        response = (Object[]) call.invoke(args);
        return response;
    }

    /**
     * Initialise a benchmark process
     * 
     * @param name
     *            the name of the benchmark sequence (eg 201-1000. See the
     *            website for available sequences.)
     * @param algorithm
     *            the name or type of your algorithm (eg Evolutionary Algorithm,
     *            PSO, etc)
     * @param parameters
     *            parameters or extra information about the algorithm instance
     *            (eg population size, mutation rate, damping coefficient, etc)
     * @return Object[0]: a Boolean indicating whether the remote method call
     *         was successful<br>
     *         Object[1]: a status report from the server
     */
    public Object[] startBenchmark(String name, String algorithm, String parameters) throws Exception
    {
        Object[] args =
        { name, algorithm, parameters };
        response = new Object[2];
        call.setOperationName("startBenchmark");
        response = (Object[]) call.invoke(args);
        return response;
    }

    /**
     * Request evaluations (fitnesses) of points in the current benchmark
     * landscape
     * 
     * @param probes
     *            the points to evaluate as an Nx2 array, where N is the number
     *            of points, and each point is represented as a 2-d array of x
     *            and y coordinates.
     * @return Object[0]: a Boolean indicating whether the remote method call
     *         was successful<br>
     *         Object[1]: a status report from the server<br>
     *         Object[2]: an N-d array of doubles containing the evaluations
     *         (fitnesses)<br>
     *         Object[3]: an Integer containing the number of the current
     *         landscape in the sequence<br>
     *         Object[4]: an Integer the number of evaluations completed for the
     *         current landscape<br>
     *         Object[5]: a Double containing the minimum found so far in the
     *         current landscape<br>
     *         Object[6]: a Boolean indicating whether the allowable probes for
     *         the current landscape have been exhausted<br>
     *         Object[7]: a Boolean indicating whether the benchmark process is
     *         complete (all landscapes exhausted)<br>
     */
    public Object[] benchmark(double[][] probes) throws Exception
    {
        Object[] args =
        { probes };
        call.setOperationName("benchmark");
        response = (Object[]) call.invoke(args);
        return response;
    }

    /**
     * Convenience method indicating if the last call was successful. (Same as
     * ((Boolean) Object[1])booleanValue().)
     * 
     * @return the success or failure
     */
    public boolean successful()
    {
        return (((Boolean) response[0]).booleanValue());
    }

    /**
     * Convenience method to get the message from the last server call. (Same as
     * (String) Object[1].)
     * 
     * @return the message
     */
    public String getMessage()
    {
        return (String) response[1];
    }

    /**
     * Convenience method to print the message from the last server call. (Same
     * as printing getMessage().)
     */
    public void printMessage()
    {
        System.out.println((String) response[1]);
    }

    /**
     * A simple routine to test the connection. <br>
     * Run by the command:
     * <code>java -classpath ".:ThirdPartyJars.jar" huygensWSClient</code><br>
     * Reports the time at the WebServer and the time taken for the call (the
     * approximate network latency)
     * 
     * @param args
     *            should be empty
     */
    public static void main(String[] args) throws Exception
    {        
        HuygensWSClient client = new HuygensWSClient();
        System.out.println("Pinging web service...");
        long timer = System.currentTimeMillis();
        client.ping();
        long timeTaken = System.currentTimeMillis() - timer;
        client.printMessage();
        if (client.successful())
            System.out.println("Connection succeeded. Latency time: " + timeTaken + " milliseconds.\n");
        else
            System.out.println("Connection unsuccessful.");        
    }

}
