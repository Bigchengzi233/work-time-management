package com.worktime.vo;

import com.worktime.entity.WorkTimeLog;

// 工时日志数据库查询结果对象：在 WorkTimeLog 基础上带出操作人姓名。
public class WorkTimeLogRowVO extends WorkTimeLog {

    // 操作人姓名。
    private String operatorName;

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
