package com.showjoy.weex.event;

/**
 * Created by lufei on 12/8/16.
 */

public class TitleEvent extends WeexBaseEvent {

    public String title;

    public TitleEvent(String instanceId, String title) {
        super(instanceId);
        this.title = title;
    }
}
