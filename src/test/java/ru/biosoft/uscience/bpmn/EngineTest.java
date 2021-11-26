package ru.biosoft.uscience.bpmn;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EngineTest 
{
	static ProcessEngine processEngine;
	static Deployment deployment;
	static ProcessInstance processInstance;
	static String processDefinitionId;

	@Test
	public void _1_initProcessEngine()
	{
		processEngine = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResourceDefault()
				.buildProcessEngine();
	}

	@Test
	public void _2_deployWorkflow()
	{
		deployment = processEngine.getRepositoryService()
				     .createDeployment()
				     .addInputStream("printVariables.bpmn", this.getClass().getClassLoader().getResourceAsStream("printVariables.bpmn"))
				     .deploy();
		
	    List<ProcessDefinition> definitions = processEngine.getRepositoryService()
	    		.createProcessDefinitionQuery()
	    		.deploymentId(deployment.getId())
	    		.list();
	    		
	    System.out.println("!!!Definitions: " + definitions);	    
		
	    processDefinitionId = definitions.get(0).getId();
	    System.out.println("!!!Definitions: " + definitions);	    
	}

	@Test
	public void _3_workflowParameters()
	{
		FormData fd = processEngine.getFormService()
				.getStartFormData(processDefinitionId);
		
		Map<String, Object> vars = processEngine.getFormService()
				.getStartFormVariables(processDefinitionId);
		
	    System.out.println("!!!Form data: " + fd);	    
	}
	
	@Test
	public void _4_startWorkflow()
	{
		// init variables
	    Map<String, Object> variables = new Hashtable<String, Object>();

		// start the process instance
//	    processInstance = processEngine.getRuntimeService()
//	    		.startProcessInstanceByKey("testWorkflowId", variables);
	    
		ProcessInstance pi = processEngine.getRuntimeService()
	    		.startProcessInstanceById(processDefinitionId, variables);

	    System.out.println("!!!Process instance: " + pi);	    
	}
	
	@Test
	public void _9_removeWorkflow()
	{
//		processEngine.getRepositoryService().deleteDeployment("601", true);

		processEngine.getRepositoryService()
			.deleteDeployment(deployment.getId(), true);
	}
		
}
