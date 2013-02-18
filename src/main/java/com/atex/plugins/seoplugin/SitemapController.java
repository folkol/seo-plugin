package com.atex.plugins.seoplugin;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.polopoly.model.Model;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.model.ModelWrite;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.model.context.PageScope;
import com.polopoly.siteengine.model.context.SiteScope;
import com.polopoly.siteengine.model.context.StructureScope;
import com.polopoly.siteengine.mvc.controller.RenderControllerSystem;
import com.polopoly.siteengine.structure.PagePolicy;

public class SitemapController extends
        RenderControllerSystem {
//    private static final Logger LOG = Logger
//            .getLogger(SitemapController.class.getName());

    private static final String newsSiteMapMode = "news_sitemap";

    /**
     * {@inheritDoc}
     */
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        ModelWrite localModel = m.getLocal();
        StructureScope siteOrPage = m.getContext().getPage();
        siteOrPage = siteOrPage != null ? siteOrPage : m.getContext().getSite();
        Model sitemapXml = null;
        PagePolicy pagePolicy = null;
        //Fetch model
        if (siteOrPage != null) {
            Model siteModel = siteOrPage.getContent();
            if (siteModel != null) {
                sitemapXml = (Model) ModelPathUtil.get(siteModel, "sitemapConfig");
                ModelPathUtil.set(m.getLocal(), "sitemapcontent", sitemapXml);
            }
            if (siteOrPage instanceof SiteScope) {
                pagePolicy = (PagePolicy) (((SiteScope)siteOrPage).getBean());
            }
            else if (siteOrPage instanceof PageScope) {
                pagePolicy = (PagePolicy) (((PageScope)siteOrPage).getBean());
            }
        }
        String siteMapMode = (String) request.getAttribute("mode");
        boolean isNewsSitemap = (siteMapMode != null && newsSiteMapMode.equals(siteMapMode));
        if (!isNewsSitemap && sitemapXml != null && pagePolicy != null) {
            //Here you can generate for example sub pages of the current site
        }
        
        if (isNewsSitemap) {
//            SiteScope site = m.getContext().getSite();
            String publicationName = "Irish Times";
            String siteName = pagePolicy.getName();
            localModel.setAttribute("sitemapMode", newsSiteMapMode);
            localModel.setAttribute("timezone", TimeZone.getTimeZone("UTC"));
            localModel.setAttribute("publicationName", StringUtils.isEmpty(siteName) ? publicationName : siteName);
        }
    }
}