package com.showjoy.weex.event;

import android.content.Intent;

/**
 * Created by lufei on 12/9/16.
 */

public class NewPageEvent extends WeexBaseEvent {
    public Intent intent;

    public NewPageEvent(String instanceId, Intent intent) {
        super(instanceId);
        this.intent = intent;
    }
}
