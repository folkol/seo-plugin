package com.atex.plugins.seoplugin;

import com.polopoly.cm.policy.PolicyImplBase;
import com.polopoly.model.ModelTypeDescription;

/**
 * Contain convenience methods for settings used by vm.
 * @author sarasprang
 */
public class SitemapConfigFieldPolicy extends PolicyImplBase implements ModelTypeDescription {

    public boolean isManual() {
        return PolicyUtils.isChecked(false, this, "useManual");
    }
    
    public String getManualXML() {
        return PolicyUtils.getSingleValue("", this, "manualxml");
    }

    //Will loop through PQs in the vm
    public boolean isPublishingQueues() {
        return PolicyUtils.isChecked(false, this, "usePublishingQueues");
    }

    //Will loop through PQs in the vm
    public boolean isGoogleSiteMap() {
        return PolicyUtils.isChecked(false, this, "useGoogleNews");
    }
}
