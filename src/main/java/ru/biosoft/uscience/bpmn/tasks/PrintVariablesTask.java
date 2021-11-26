package ru.biosoft.uscience.bpmn.tasks;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * Utility tasks to print workflow variables.
 */
public class PrintVariablesTask implements JavaDelegate 
{
	public void execute(DelegateExecution execution) throws Exception 
    {
		System.out.println("Task PrintVariablesTask, variables: ");
		System.out.println(execution.getVariables());
	}
}
