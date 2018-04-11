/*
 * Project: java_test
 * 
 * File Created at 2018年3月7日
 * 
 * Copyright 2016 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.ssm.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.FormService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.impl.el.UelExpressionCondition;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.validation.ProcessValidator;
import org.activiti.validation.ProcessValidatorFactory;
import org.activiti.validation.ValidationError;
import org.junit.Before;
import org.junit.Test;

/**
 * @Type ActivtiDemo.java
 * @Desc activiti 常见操作
 * @author songyalong
 * @date 2018年3月7日 下午2:28:32
 * @version 
 */
public class ActivtiDemo {
    ProcessEngine processEngine = null;

    @Before
    public void creataTable() {

        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration();
        // 设置数据库信息
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        processEngineConfiguration.setJdbcUrl(
                "jdbc:mysql://172.23.31.173:3306/activiti?useUnicode=true&characterEndocing=utf8");
        processEngineConfiguration.setJdbcUsername("activitier");
        processEngineConfiguration.setJdbcPassword("acterHY123@#");
        // 设置数据库操作的设置
        processEngineConfiguration
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 获取工作流的核心对象
        processEngine = processEngineConfiguration.buildProcessEngine();
    }

    // 部署流程
    @Test
    public void deployment() {
        Deployment deployment = processEngine.getRepositoryService().createDeployment()
                .name("serrequestProcess").category("serrequestProcess")
                .addClasspathResource("diagrams/serrequestProcess2.bpmn")
                .addClasspathResource("diagrams/serrequestProcess2.png").deploy();
        System.out.println("部署id号 = " + deployment.getId());
        System.out.println("部署名称 = " + deployment.getName());
    }

    //删除流程
    @Test
    public void deleteDeployment() {
        String deploymentId = "ttt";
        processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
    }

    // 启动流程
    @Test
    public void startProcess() {

        String id = "FormProcess2:1:3412504";
        Map<String, Object> variable = new HashMap<>();
        variable.put("startDate", "2010-03-20");
        variable.put("endDate", "2010-03-21");
        variable.put("reason", "病假");
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(id, variable);
        System.out.println("部署实例id = " + processInstance.getProcessDefinitionId());
        System.out.println("部署实例id = " + processInstance.getId());
    }

    // 获取我的任务
    @Test
    public void getMyTask() {
        Task task = processEngine.getTaskService().createTaskQuery().taskAssignee("lisi")
                .singleResult();
        System.out.println("分配人 = " + task.getAssignee());
        System.out.println("任务id = " + task.getId());
    }

    // 完成我的任务
    @Test
    public void completeMyTask() {
        String taskId = "3382506";
        Map<String, Object> para = new HashMap<>();
        para.put("hr", "true");
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(taskId, para);
        throw new RuntimeException("出错");
    }

    @Test
    public void completeFormTask() {
        String taskId = "3415007";
        FormService formService = processEngine.getFormService();

        TaskService taskService = processEngine.getTaskService();

        Map<String, Object> properties = new HashMap<>();
        properties.put("deptLeaderApprove", "true");
        taskService.complete(taskId, properties);

        //        formService.submitTaskFormData(taskId, properties);
        //        Map<String, Object> para = new HashMap<>();
        //        para.put("hr", "true");
        //        TaskService taskService = processEngine.getTaskService();
        //        taskService.complete(taskId, para);
        //        throw new RuntimeException("出错");
    }

    // 获取表单
    @Test
    public void getForm() {
        String taskId = "3415007";
        FormService formService = processEngine.getFormService();
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        System.out.println(taskFormData.getFormKey());
        List<FormProperty> formProperties = taskFormData.getFormProperties();
        for (FormProperty formProperty : formProperties) {
            System.out.println("id = " + formProperty.getId());
            System.out.println("name = " + formProperty.getName());
            System.out.println("value = " + formProperty.getValue());
        }
    }

    @Test
    public void getForm2() {
        FormService formService = processEngine.getFormService();
        TaskFormData taskFormData = formService.getTaskFormData("3425004");
        List<FormProperty> formProperties = taskFormData.getFormProperties();
        System.out.println(formProperties.size());

    }

    // 获取流程各个节点信息
    @Test
    public void getActivitiInfo2() {
        String processDefinitionId = "ActivitiDemo2Process:1:3207504";
        BpmnModel bpmnModel = processEngine.getRepositoryService()
                .getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcesses().get(0);

        Collection<FlowElement> flowElements = process.getFlowElements();

        for (FlowElement flowElement : flowElements) {
            System.out.println(flowElement);
            if (flowElement instanceof UserTask) {
                // 用户任务
                UserTask userTask = (UserTask) flowElement;
            } else if (flowElement instanceof ExclusiveGateway) {
                // 排他网关
                ExclusiveGateway exclusiveGateway = (ExclusiveGateway) flowElement;
                List<SequenceFlow> outgoingFlows = exclusiveGateway.getOutgoingFlows();
                System.out.println(outgoingFlows.get(0).getConditionExpression());
                System.out.println(outgoingFlows.get(1).getConditionExpression());
            }

        }
    }

    // 获取流程定义
    @Test
    public void getActivitiInfo() {
        String processDefinitionId = "ipRequestProcess:2:2887504";
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) processEngine
                .getRepositoryService().getProcessDefinition(processDefinitionId);
        ActivityImpl activity = processDefinitionEntity.findActivity("usertask2");

        // 任务节点流出的线 的信息
        List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();
        for (PvmTransition pt : outgoingTransitions) {
            System.out.println();
            TransitionImpl t = (TransitionImpl) pt;
            System.out.println(t.getSource().getProperties());
            System.out.println(t.getDestination().getProperties());
            System.out.println(t.getExecutionListeners());
            PvmActivity destination = pt.getDestination();
            System.out.println(destination.isExclusive());
            ActivityImpl execu = (ActivityImpl) destination;
            System.out.println(execu.getProperties());

            List<PvmTransition> outgoingTransitions2 = execu.getOutgoingTransitions();
            for (PvmTransition pvt : outgoingTransitions2) {
                TransitionImpl pi = (TransitionImpl) pvt;
                Object object = pi.getProperties().get("conditionText");
                System.out.println(object);
                UelExpressionCondition uel = (UelExpressionCondition) pi.getProperties()
                        .get("condition");
                Map<String, Object> vars = new HashMap<>();
                vars.put("outcome", 1010);
                System.out.println(isCondition(object.toString(), vars));
            }

        }
    }

    public static boolean isCondition(String el, Map<String, Object> vars) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (String key : vars.keySet()) {
            context.setVariable(key, factory.createValueExpression(vars.get(key), Object.class));
        }
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (boolean) e.getValue(context);
    }

    // activiti 使用的uuid
    @Test
    public void uuid() {

    }

    // 代码生成流程图
    public BpmnModel createMyBpmnModel() {
        BpmnModel bpmnModel = new BpmnModel();
        // 开始节点
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        startEvent.setName("startEvent");

        // 普通的UserTask节点
        UserTask userTask = new UserTask();
        userTask.setId("userTask");
        userTask.setName("userTask");

        // 结束节点
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        endEvent.setName("endEvent");

        // 连线
        List<SequenceFlow> sequenceFlows = new ArrayList<>();
        List<SequenceFlow> toEnd = new ArrayList<>();

        // 第一条线
        SequenceFlow sf = new SequenceFlow();
        sf.setId("starttouserTask");
        sf.setName("starttouserTask");
        sf.setSourceRef("startEvent");
        sf.setTargetRef("userTask");

        sequenceFlows.add(sf);

        // 第二条线
        SequenceFlow s2 = new SequenceFlow();
        s2.setId("userTasktoend");
        s2.setName("userTasktoend");
        s2.setSourceRef("userTask");
        s2.setTargetRef("endEvent");

        toEnd.add(s2);

        startEvent.setOutgoingFlows(sequenceFlows);
        userTask.setIncomingFlows(sequenceFlows);
        userTask.setOutgoingFlows(toEnd);

        endEvent.setIncomingFlows(toEnd);

        // Process 对象
        Process process = new Process();
        process.setId("MyProcess");

        process.addFlowElement(startEvent);
        process.addFlowElement(sf);
        process.addFlowElement(userTask);
        process.addFlowElement(s2);
        process.addFlowElement(endEvent);

        bpmnModel.addProcess(process);

        return bpmnModel;
    }

    @Test
    public void bpmnModelToStr() {
        BpmnModel bpmnModel = createMyBpmnModel();

        BpmnXMLConverter converter = new BpmnXMLConverter();
        byte[] convertToXML = converter.convertToXML(bpmnModel);

        String str = new String(convertToXML);
        System.out.println(str);

    }

    // 验证 bpmnmodel
    @Test
    public void validatorBpmnModel() {
        BpmnModel bpmnModel = createMyBpmnModel();

        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator defaultProcessValidator = processValidatorFactory
                .createDefaultProcessValidator();
        List<ValidationError> validate = defaultProcessValidator.validate(bpmnModel);
        System.out.println(validate.size());
    }

}

/**
 * Revision history
 * -------------------------------------------------------------------------
 * 
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2018年3月7日 songyalong creat
 */
