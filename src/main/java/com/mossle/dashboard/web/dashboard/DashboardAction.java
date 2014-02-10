package com.mossle.dashboard.web.dashboard;

import java.util.*;

import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.cms.domain.*;
import com.mossle.cms.manager.*;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.*;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.*;
import org.activiti.engine.task.*;

public class DashboardAction {
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private CmsArticleManager cmsArticleManager;
    private List<Task> personalTasks;
    private List<HistoricProcessInstance> historicProcessInstances;
    private List<BpmProcess> bpmProcesses;
    private List<CmsArticle> cmsArticles;

    public String execute() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        personalTasks = processEngine.getTaskService().createTaskQuery()
                .taskAssignee(userId).list();
        historicProcessInstances = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery().startedBy(userId)
                .unfinished().list();
        bpmProcesses = bpmProcessManager.getAll();
        cmsArticles = cmsArticleManager.getAll();

        return "success";
    }

    // ~ ==================================================
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    // ~ ==================================================
    public List<Task> getPersonalTasks() {
        return personalTasks;
    }

    public List<HistoricProcessInstance> getHistoricProcessInstances() {
        return historicProcessInstances;
    }

    public List<BpmProcess> getBpmProcesses() {
        return bpmProcesses;
    }

    public List<CmsArticle> getCmsArticles() {
        return cmsArticles;
    }
}
