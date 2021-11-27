package ru.biosoft.uscience.bpmn;

import java.io.File;
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

public class PilotMain 
{
    static long start;
    protected static Logger log;
    protected static CommandLine cmd;
	protected static Server server;
	protected static BpmnService bpmnService;
	
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
		options.addOption("c", "clean", false, "Clean working directory for pilot before the task execution");
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
		
		File workDir = new File(workDirPath);
		String path = null;
		try
		{
			path = workDir.getCanonicalPath();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "Working dir path error: " + e, e);
		}
		
		if (!workDir.exists() )
		{
			if( ! workDir.mkdirs() )
			{
	            log.log(Level.SEVERE, "Can not create working directory: " + workDirPath + ", path: " + path);
	            System.exit(-1);
			}
		}		

        log.log(Level.INFO, "Working dir: " + workDirPath + ", path: " + path);

	}

	
	
}


/*
static Server server;
static BpmnService bpmnService;
static String processDefinitionKey;
static String processInstanceId;

static String WORKFLOW = "printVariables.bpmn";


@Test
public void _1_init()
{
	long start = System.currentTimeMillis(); 
	bpmnService = new CamundaBpmnServiceImpl();
	System.out.println("BPMN service loading: " + (System.currentTimeMillis()-start));

	start = System.currentTimeMillis(); 
	processDefinitionKey = bpmnService.deployModel(WORKFLOW, 
			getClass().getClassLoader().getResourceAsStream(WORKFLOW));
	System.out.println("Deploy model: " + (System.currentTimeMillis()-start));
}

@Test
public void _2_startWorkflow()
{
	// init variables
    Map<String, Object> variables = new Hashtable<String, Object>();
    variables.put("e-mail", "test@test.com");
    
    processInstanceId = bpmnService.startProcess(processDefinitionKey, variables);
}

@Test
public void _8_removeWorkflow()
{

	bpmnService.deleteModel(processDefinitionKey);
	bpmnService.deleteModel("Process_printVariables");		
}

@Test
public void _9_stopH2Server()
{
	server.stop();
}
*/