package com.showjoy.weex.event;

/**
 * Created by lufei on 12/8/16.
 */

public class ShowTitleBarEvent extends WeexBaseEvent {

    public boolean visible;

    public ShowTitleBarEvent(String instanceId, boolean visible) {
        super(instanceId);
        this.visible = visible;
    }
}
