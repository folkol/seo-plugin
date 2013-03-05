package com.atex.plugins.seoplugin;

import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.model.ModelWrite;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.structure.PagePolicy;

public class MetaDataController extends InheritableSettingsController {
    private static final Logger LOG = Logger.getLogger(MetaDataController.class.getName());
    /**
     * {@inheritDoc}
     */
    @Override
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m, CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        ModelWrite localModel = m.getLocal();
        PolicyCMServer cmServer = getCmClient(context).getPolicyCMServer();
        
        ContentId currentSiteOrPage = getCurrentPage(m);
        String pageTitle = getInheritedValue(cmServer, currentSiteOrPage, "metadataConfig/seoSiteName");
        String pageDescription = getInheritedValue(cmServer, currentSiteOrPage, "metadataConfig/seoSiteDescription");
        String pageKeywords = getInheritedValue(cmServer, currentSiteOrPage, "metadataConfig/seoSiteKeywords");
        ContentId lastContentId = context.getContentId();
        Policy lastContent = getContent(cmServer, lastContentId);
        
        //Use article/content MetaData if present
        if (lastContent != null && lastContent instanceof MetaDataModelTypeDescription) {
            MetaDataModelTypeDescription article = (MetaDataModelTypeDescription) lastContent;
            SimpleDateFormat fm = new SimpleDateFormat(" - EEE, MMM dd, yyyy");
            String datePart = fm.format(article.getPublishingDateTime());
            
            String articleTitle = article.getTitle() + datePart;
            String articleDescription = article.getDescription();
            String articleKeywords = article.getKeywords();
            
            if (!StringUtils.isEmpty(articleTitle)) {
                pageTitle = articleTitle;
            }
            if (!StringUtils.isEmpty(articleDescription)) {
                pageDescription = articleDescription;
            }
            if (!StringUtils.isEmpty(articleKeywords)) {
                pageKeywords = articleKeywords;
            }
        }
        //Last fall back if title not found anywhere
        if (StringUtils.isEmpty(pageTitle) && currentSiteOrPage != null) {
            PagePolicy pageOrSite = (PagePolicy) getContent(cmServer, currentSiteOrPage);
            pageTitle = pageOrSite.getName();
        }
        localModel.setAttribute("pageTitle", pageTitle);
        localModel.setAttribute("pageDescription", pageDescription);
        localModel.setAttribute("pageKeywords", pageKeywords);
    }
    
    private Policy getContent(PolicyCMServer cmServer,
            ContentId contentId) {

        Policy policy = null;
        try {
            policy = cmServer.getPolicy(contentId);
        } catch (CMException cme) {
            LOG.log(Level.FINE, "Can't find policy for " + contentId, cme);
        }
        return policy;
    }
}
