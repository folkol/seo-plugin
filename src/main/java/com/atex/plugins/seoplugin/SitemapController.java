package com.atex.plugins.seoplugin;

import com.polopoly.model.Model;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.model.ModelWrite;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.model.context.StructureScope;
import com.polopoly.siteengine.mvc.Renderer;
import com.polopoly.siteengine.mvc.controller.RenderControllerSystem;
import com.polopoly.siteengine.mvc.controller.RendererForXmlContent;

public class SitemapController extends RenderControllerSystem {

    private static final String newsSiteMapMode = "news_sitemap";

    /**
     * {@inheritDoc}
     */
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        ModelWrite localModel = m.getLocal();
        
        String siteMapMode = (String) request.getAttribute("mode");
        boolean isNewsSitemap = (siteMapMode != null && newsSiteMapMode.equals(siteMapMode));
        
        if (isNewsSitemap) {
            localModel.setAttribute("sitemapMode", newsSiteMapMode);
        }
    }

    protected void addSitemapConfigToModel(TopModel m, ModelWrite model) {
        StructureScope siteOrPage = m.getContext().getPage();
        siteOrPage = siteOrPage != null ? siteOrPage : m.getContext().getSite();
        Model sitemapXml = null;
        //Fetch model
        if (siteOrPage != null) {
            Model siteModel = siteOrPage.getContent();
            if (siteModel != null) {
                sitemapXml = (Model) ModelPathUtil.get(siteModel, "sitemapConfig");
                model.setAttribute("sitemapcontent", sitemapXml);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public com.polopoly.siteengine.mvc.Renderer getRenderer(
            RenderRequest request, TopModel m,
            Renderer defaultRenderer,
            ControllerContext context) {
        return new RendererForXmlContent(defaultRenderer);
    }
}