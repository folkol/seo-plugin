package com.atex.plugins.seoplugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.model.context.PageScope;
import com.polopoly.siteengine.model.context.SiteScope;
import com.polopoly.siteengine.mvc.RenderControllerBase;
import com.polopoly.siteengine.structure.PageModelTypeDescription;
import com.polopoly.siteengine.structure.PagePolicy;
import com.polopoly.siteengine.structure.SitePolicy;

public abstract class InheritableSettingsController extends RenderControllerBase {
    private static final Logger LOG = Logger.getLogger(InheritableSettingsController.class.getName());
    
    public static ContentId getCurrentPage(TopModel m) {
        PageScope page = m.getContext().getPage();

        // Pick up the page and then site is stupid but they don't have a common interface to pick up the contentId from the model.

        // Use page setting
        if (page != null) {
            return ((PageModelTypeDescription) page.getBean()).getContentId();
        }

        // Fallback to site
        SiteScope site = m.getContext().getSite();
        if (site != null) {
            return ((PageModelTypeDescription) site.getBean()).getContentId();
        }

        return null;
    }
    
    /**
     * Getting values and traversing it's parents up to Site if it can't find
     * any. Will return first instance with a value or empty string if none
     * found.
     * 
     * @param cmServer PolicyCMServer to use for look ups.
     * @param contentId ContentId to start from.
     * @param componentName String path of a model to find 'value' on.
     * @return String Found value or empty string if none.
     */
    public static String getInheritedValue(PolicyCMServer cmServer, ContentId contentId, String componentName) {
        String componentValue = "";
        if (contentId == null) {
            throw new IllegalArgumentException(
                    "Cannot get inherited value, no contentId to start from when trying to get component: "
                            + componentName);
        }
        try {
            Policy policy = cmServer.getPolicy(contentId); 

            if(policy instanceof SitePolicy) {
                SitePolicy sitePolicy = (SitePolicy) policy; 
                componentValue = sitePolicy.getComponent(componentName, "value"); 
            } else if(policy instanceof PagePolicy) {
                PagePolicy pagePolicy = (PagePolicy) policy;
                componentValue = pagePolicy.getComponent(componentName, "value");
                
                if(StringUtils.isEmpty(componentValue)) {
                    componentValue = getParentValue(cmServer, contentId, componentName, componentValue, 
                            policy, pagePolicy.getParentIds());
                }
            } 
        } catch (CMException ex) {
            LOG.log(Level.WARNING, "Error when trying to get SEO inherited value: " + contentId.getContentIdString()); 
        }
         
        return componentValue == null ? "" : componentValue.trim(); 
    }

    private static String getParentValue(PolicyCMServer cmServer, ContentId contentId, String componentName,
            String componentValue, Policy policy, ContentId parentIds[]) {
        
        int counter = 50; 
        
        for(int i=(parentIds.length - 1); i >= 0; i--) {
            ContentId parentContentId = parentIds[i];
                    
            if(counter-- < 0)
                break; 
            
            try {
                policy = cmServer.getPolicy(parentContentId); 
                if(policy instanceof PagePolicy) {
                    PagePolicy pagePolicy = (PagePolicy) policy;
                    componentValue = pagePolicy.getComponent(componentName, "value");
                } else {
                    break; 
                }

                if(!StringUtils.isEmpty(componentValue)) { 
                    break;
                }
            } catch (CMException ex) {
                LOG.log(Level.WARNING,
                        "Error when trying to get value using parentId details for : "
                                + contentId.getContentIdString()
                                + ", parentId "
                                + parentContentId.getContentIdString()); 
            }
        }
        
        return componentValue;
    }
}
