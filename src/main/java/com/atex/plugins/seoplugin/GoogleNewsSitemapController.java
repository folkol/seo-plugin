package com.atex.plugins.seoplugin;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.polopoly.model.ModelWrite;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.model.context.PageScope;
import com.polopoly.siteengine.model.context.SiteScope;
import com.polopoly.siteengine.model.context.StructureScope;
import com.polopoly.siteengine.structure.PagePolicy;

public class GoogleNewsSitemapController extends SitemapController {
    private static final String DEFAULT_PUBLICATION_NAME = "";
    /**
     * {@inheritDoc}
     */
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        ModelWrite localModel = m.getLocal();
        StructureScope siteOrPage = m.getContext().getPage();
        siteOrPage = siteOrPage != null ? siteOrPage : m.getContext().getSite();
        PagePolicy pagePolicy = null;
        //Fetch model
        if (siteOrPage != null) {
            if (siteOrPage instanceof SiteScope) {
                pagePolicy = (PagePolicy) (((SiteScope)siteOrPage).getBean());
            }
            else if (siteOrPage instanceof PageScope) {
                pagePolicy = (PagePolicy) (((PageScope)siteOrPage).getBean());
            }
        }
        addSitemapConfigToModel(m, localModel);
        if (pagePolicy != null) {
            String siteName = pagePolicy.getName();
            localModel.setAttribute("timezone", TimeZone.getTimeZone("UTC"));
            localModel.setAttribute("publicationName", StringUtils.isEmpty(siteName) ? DEFAULT_PUBLICATION_NAME : siteName);
        }
    }
}
