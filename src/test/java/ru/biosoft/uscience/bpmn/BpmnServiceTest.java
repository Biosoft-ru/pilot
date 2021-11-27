package ru.biosoft.uscience.bpmn;

//import com.developmentontheedge.be5.bpmn.CamundaBpmnServiceImpl;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;

import org.h2.tools.Server;
import org.camunda.bpm.engine.task.Task;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BpmnServiceTest 
{
	static Server server;
	static BpmnService bpmnService;
	static String processDefinitionKey;
	static String processInstanceId;
	
	static String WORKFLOW = "printVariables.bpmn";


	@Test
	public void _0_initH2Server() throws Exception 
	{
		long start = System.currentTimeMillis();
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-ifExists").start();
		System.out.println("Server loading: " + (System.currentTimeMillis()-start));
	}

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
	
}
