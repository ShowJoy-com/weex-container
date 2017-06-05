package com.showjoy.weex;

import com.showjoy.weex.entities.WeexConfig;

import java.util.List;

/**
 * Created by lufei on 5/23/17.
 */

public interface ISHWeexRequestCallback {

    void response(List<WeexConfig> weexConfigs);
}
