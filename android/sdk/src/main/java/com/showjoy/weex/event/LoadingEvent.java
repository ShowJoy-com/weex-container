package com.showjoy.weex.event;

/**
 * Created by lufei on 12/8/16.
 */

public class LoadingEvent extends WeexBaseEvent {
    public boolean visible;
    public String title;

    public LoadingEvent(String instanceId, boolean visible, String title) {
        super(instanceId);
        this.visible = visible;
        this.title = title;
    }
}
