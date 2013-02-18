package com.atex.plugins.seoplugin;
import org.apache.commons.lang.StringUtils;

import com.polopoly.model.Model;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderException;
import com.polopoly.render.RenderRequest;
import com.polopoly.render.RenderResponse;
import com.polopoly.siteengine.mvc.Renderer;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.model.context.PageScope;
import com.polopoly.siteengine.model.context.SiteScope;
import com.polopoly.siteengine.model.context.StructureScope;
import com.polopoly.siteengine.mvc.controller.RenderControllerSystem;


public class RobotsTxtController extends RenderControllerSystem {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);

        String robotsTxt = "";
        PageScope page = m.getContext().getPage();

        // Use page setting
        if (page != null) {
            robotsTxt = getRobotsTxtFromModel(page);
        }
        // Fallback to site
        SiteScope site = m.getContext().getSite();
        if (StringUtils.isEmpty(robotsTxt) && site != null) {
            robotsTxt = getRobotsTxtFromModel(site);
        }
        // Last fallback, disallow all
        if (StringUtils.isEmpty(robotsTxt)) {
            robotsTxt = "User-agent: *\r\nDisallow: /\r\n";
        }
        m.getLocal().setAttribute("robotsTxt", robotsTxt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.polopoly.siteengine.mvc.Renderer getRenderer(
            RenderRequest request, TopModel m,
            Renderer defaultRenderer,
            ControllerContext context) {
        return new RendererForStaticTxtContent(defaultRenderer);
    }

    private String getRobotsTxtFromModel(StructureScope siteOrPage) {
        Model siteModel = siteOrPage.getContent();
        if (siteModel != null) {
            return (String) ModelPathUtil.get(siteModel, "robotsConfig/robotsTXT/value");
        }
        return "";
    }

    protected class RendererForStaticTxtContent implements Renderer {
        private final Renderer defaultRenderer;

        public RendererForStaticTxtContent(Renderer defaultRenderer) {
            this.defaultRenderer = defaultRenderer;
        }

        public void render(TopModel m, RenderRequest req,
                RenderResponse response, CacheInfo cacheInfo,
                ControllerContext context) throws RenderException {
            response.setHeader("Content-Type", "text/plain; charset=UTF-8");
            defaultRenderer.render(m, req, response, cacheInfo, context);
        }
    }
}