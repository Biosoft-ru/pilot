package ru.biosoft.uscience.bpmn;

import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.h2.tools.Server;
import org.yaml.snakeyaml.Yaml;

public class PilotMain 
{
	// Keys for YAML file
	public static final String WORKFLOW_KEY = "workflow";  
	
	static long start;
    protected static Logger log;
    protected static CommandLine cmd;
    protected static File workDir;
    protected static Map<String, Object> yamlMap;
    
	protected static Server server;
	protected static BpmnService bpmnService;
	protected static String processDefinitionKey;
	protected static String processInstanceId;
	
	public static void main(String[] args) 
	{
        log = Logger.getLogger(PilotMain.class.getName());

        parseCommandLine(args);
        prepareWorkingDir();
        
        try
        {
        	start = System.currentTimeMillis();

        	server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-ifExists").start();
        	bpmnService = new CamundaBpmnServiceImpl();
        	log.log(Level.INFO, "BPMN engine started, t=" + (System.currentTimeMillis()-start));
        	
        	String workflow = "default.bpmn";
        	if( yamlMap.containsKey(WORKFLOW_KEY) )
        		workflow = (String) yamlMap.get(WORKFLOW_KEY);
        	
        	long deployStart = System.currentTimeMillis();
        	processDefinitionKey = bpmnService.deployModel(workflow, 
        			PilotMain.class.getClassLoader().getResourceAsStream(workflow));
        	log.log(Level.INFO, "BPMN workflow loaded: " + workflow + ", t=" + (System.currentTimeMillis()-deployStart));

            processInstanceId = bpmnService.startProcess(processDefinitionKey, yamlMap);
        	log.log(Level.INFO, "Process started, id: " + processInstanceId);

        	bpmnService.deleteModel(processDefinitionKey);
        	log.log(Level.INFO, "Process completed, delete model: " + processInstanceId);
        }
        catch(Throwable t)
        {
            log.log(Level.SEVERE, "Can not initialise BPMN engine: " + t, t);

        }
        finally
        {
        	if( server != null )
        		server.stop();

        	log.log(Level.INFO, "BPMN engine completed, t=" + (System.currentTimeMillis()-start));
        }

    	System.exit(0);
	}

	protected static void parseCommandLine(String[] args)
	{
		Options options = new Options();
		options.addRequiredOption("y", "yaml64",  true, "YAML file (encoded in BASE64) with task configuraton for pilot");
		options.addOption("c", "cleanDir", false, "Clean working directory for pilot before the task execution");
		options.addOption("w", "workDir", false, "Working directory. By default it is ./tmp");

		try
		{
			CommandLineParser parser = new DefaultParser();
			cmd = parser.parse( options, args);
		}
		catch(ParseException e)
		{
            log.log(Level.SEVERE, "Parse command line options: " + e.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("pilot ", options, true);
        	System.exit(-1);
		}
	}

	protected static void prepareWorkingDir() 
	{
		String workDirPath = "./tmp";
		
		if( cmd.hasOption("workDir") )
			workDirPath = cmd.getOptionValue("workDir");	
		
		workDir = new File(workDirPath);
		String path = null;
		try
		{
			path = workDir.getCanonicalPath();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "Working dir path error: " + e, e);
		}
		
		if( workDir.exists() )
		{
			if( cmd.hasOption("cleanDir") )
				cleanDir(workDir, false);	
		}
		else
		{
			if( ! workDir.mkdirs() )
			{
	            log.log(Level.SEVERE, "Can not create working directory: " + workDirPath + ", path: " + path);
	            System.exit(-1);
			}
		}		

        // create yaml file from string parameter in base64
        String base64 = cmd.getOptionValue("yaml64");	
        String yamlStr = new String(Base64.getDecoder().decode(base64));
        try
        {
        	FileWriter writer = new FileWriter(new File(workDir, "task.yaml"));
        	writer.append(yamlStr);
        	writer.close();
        }
        catch(Exception e)
        {
            log.log(Level.SEVERE, "Can not write yaml file for the task: " + e.getMessage(), e);
            System.exit(-1);
        }

        yamlMap = (new Yaml()).load(yamlStr);
        System.out.println(yamlMap);        
        
        log.log(Level.INFO, "Working dir is ready, path: " + path + System.lineSeparator() + "YAML: " + yamlMap);
	}

	protected static void cleanDir(File dir, boolean deleteDir) 
	{
		String path = null;
		try
		{
			path = dir.getCanonicalPath();
			log.log(Level.INFO, "Clean working dir: " + path);

			for(File f : dir.listFiles() ) 
			{
				if( f.isDirectory() )
					cleanDir(f, true);
				else
					f.delete();
			}
		
			if( deleteDir )
				dir.delete();
		}
		catch(Exception e)
		{
            log.log(Level.SEVERE, "Can not clean working directory: " + path);
            System.exit(-1);
		}
	} 
	
}
