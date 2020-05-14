package com.amcdesk.servicecrm;

/**
 * Created by ABC1 on 04-Nov-17.
 */

public class Contact {

    String customer_name = null;
    String date_time = null;
    String call_id;
    String service_type = null;
    String system_call_id = null;
    String call_status = null;
    String call_issue = null;
    String call_assigned_id = null;
    Integer UnreadMessages = 0;
    String callAlive = "0";
    Integer actionTime=0;

    public void setActionTime(Integer actionTime) {
        this.actionTime = actionTime;
    }

    public Integer getActionTime() {
        return actionTime;
    }

    public void setUnreadMessages(Integer unreadMessages) {
        UnreadMessages = unreadMessages;
    }

    public Integer getUnreadMessages() {
        return UnreadMessages;
    }

    public void setCall_assigned_id(String call_assigned_id) {
        this.call_assigned_id = call_assigned_id;
    }

    public String getCall_assigned_id() {
        return call_assigned_id;
    }

    public String getCall_issue() {
        return call_issue;
    }

    public void setCall_issue(String call_issue) {
        this.call_issue = call_issue;
    }

    public void setCall_status(String call_status) {
        this.call_status = call_status;
    }

    public String getCall_status() {
        return call_status;
    }

    public void setCall_id(String call_id) {
        this.call_id = call_id;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public void setSystem_call_id(String system_call_id) {
        this.system_call_id = system_call_id;
    }

    public String getCall_id() {
        return call_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getService_type() {
        return service_type;
    }

    public void setCallAlive(String callAlive) {
        this.callAlive = callAlive;
    }

    public String getCallAlive() {
        return callAlive;
    }

    public String getSystem_call_id() {
        return system_call_id;
    }
}
