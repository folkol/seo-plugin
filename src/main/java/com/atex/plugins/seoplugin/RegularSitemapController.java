package com.atex.plugins.seoplugin;

import com.polopoly.model.ModelWrite;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;

public class RegularSitemapController extends SitemapController {
    /**
     * {@inheritDoc}
     */
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        ModelWrite localModel = m.getLocal();
        //Here you could generate an automatic sitemap based on sub pages
        addSitemapConfigToModel(m, localModel);
    }
}
