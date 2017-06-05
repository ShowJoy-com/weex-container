package com.showjoy.weex.extend.component;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.showjoy.image.SHImageView;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.dom.ImmutableDomObject;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXImage;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.border.BorderDrawable;
import com.taobao.weex.utils.ImageDrawable;
import com.taobao.weex.utils.WXDomUtils;
import com.taobao.weex.utils.WXViewUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by lufei on 12/20/16.
 */

public class FrescoImageComponent extends WXImage {

    public FrescoImageComponent(WXSDKInstance instance, WXDomObject node, WXVContainer parent) {
        super(instance, node, parent);
    }

    @Override
    protected ImageView initComponentHostView(@NonNull Context context) {
        SHImageView view = new SHImageView(context);
        view.setScaleType(ImageView.ScaleType.FIT_XY);

        return view;
    }

    @Override
    public void updateProperties(Map<String, Object> props) {
        super.updateProperties(props);
        SHImageView imageView;
        ImmutableDomObject imageDom;
        if ((imageDom = getDomObject()) != null &&
                getHostView() instanceof SHImageView) {
            imageView = (SHImageView) getHostView();
            BorderDrawable borderDrawable = WXViewUtils.getBorderDrawable(getHostView());
            float[] borderRadius;
            if (borderDrawable != null) {
                RectF borderBox = new RectF(0, 0, WXDomUtils.getContentWidth(imageDom), WXDomUtils.getContentHeight(imageDom));
                borderRadius = borderDrawable.getBorderRadius(borderBox);
            } else {
                borderRadius = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
            }
            imageView.setBorderRadius(borderRadius);

            if (imageView.getDrawable() instanceof ImageDrawable) {
                ImageDrawable imageDrawable = (ImageDrawable) imageView.getDrawable();
                float[] previousRadius = imageDrawable.getCornerRadii();
                if (!Arrays.equals(previousRadius, borderRadius)) {
                    imageDrawable.setCornerRadii(borderRadius);
                }
            }
            readyToRender();
        }
    }
}
