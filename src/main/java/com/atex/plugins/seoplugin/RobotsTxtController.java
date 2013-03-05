package com.atex.plugins.seoplugin;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderException;
import com.polopoly.render.RenderRequest;
import com.polopoly.render.RenderResponse;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.Renderer;


public class RobotsTxtController extends InheritableSettingsController {

    private static final Logger LOG = Logger.getLogger(RobotsTxtController.class.getName());
    /**
     * {@inheritDoc}
     */
    @Override
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);

        String robotsTxt = "";
        PolicyCMServer cmServer = getCmClient(context).getPolicyCMServer();

        robotsTxt = getRobotsTxtFromModel(cmServer, getCurrentPage(m));
        
        // Fallback, disallow all
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

    private String getRobotsTxtFromModel(PolicyCMServer cmServer, ContentId contentId) {
        try {
            return getInheritedValue(cmServer, contentId, "robotsConfig/robotsTXT");
        } catch (Exception e) {
            LOG.log(Level.WARNING,
                    "Encoutered error while trying to find inherited robots value, will use fall back!", e);
        }
        return "";
    }

    /**
     * Internal class to set text/plain as content type of response
     * @author sarasprang
     */
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