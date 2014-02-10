package com.mossle.bpm.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfUserTaskListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(ConfUserTaskListener.class);
    private BpmConfUserManager bpmConfUserManager;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        List<BpmConfUser> bpmConfUsers = bpmConfUserManager
                .find("from BpmConfUser where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        delegateTask.getProcessDefinitionId(), delegateTask
                                .getExecution().getCurrentActivityId());
        logger.debug("{}", bpmConfUsers);

        try {
            for (BpmConfUser bpmConfUser : bpmConfUsers) {
                logger.debug("status : {}, type: {}", bpmConfUser.getStatus(),
                        bpmConfUser.getType());
                logger.debug("value : {}", bpmConfUser.getValue());

                if (bpmConfUser.getStatus() == 1) {
                    if (bpmConfUser.getType() == 0) {
                        delegateTask.setAssignee(bpmConfUser.getValue());
                    } else if (bpmConfUser.getType() == 1) {
                        delegateTask.addCandidateUser(bpmConfUser.getValue());
                    } else if (bpmConfUser.getType() == 2) {
                        delegateTask.addCandidateGroup(bpmConfUser.getValue());
                    }
                } else if (bpmConfUser.getStatus() == 2) {
                    if (bpmConfUser.getType() == 0) {
                        if (delegateTask.getAssignee().equals(
                                bpmConfUser.getValue())) {
                            delegateTask.setAssignee(null);
                        }
                    } else if (bpmConfUser.getType() == 1) {
                        delegateTask
                                .deleteCandidateUser(bpmConfUser.getValue());
                    } else if (bpmConfUser.getType() == 2) {
                        delegateTask.deleteCandidateGroup(bpmConfUser
                                .getValue());
                    }
                }
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
    }
}
