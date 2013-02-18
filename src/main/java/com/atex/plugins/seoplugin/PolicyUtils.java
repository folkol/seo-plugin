package com.atex.plugins.seoplugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.polopoly.cm.app.policy.CheckboxPolicy;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.policy.Policy;

/**
 * Utility class for accessing child policies without having to write too much code.
 * @author sarasprang
 */
public class PolicyUtils {

    private static Logger LOG = Logger.getLogger(PolicyUtils.class.getName());

    /**
     * Utility method to get a single value
     * 
     * @param policy the policy to get the list from
     * @param names names of the components
     * @return String or null if none found or Exception caught
     */
    public static String getSingleValue(Policy policy, String... names) {
        String ret = null;
        try {
            for (String n : names) {
                policy = policy.getChildPolicy(n);
            }
            if (policy != null) {
                ret = ((SingleValued) policy).getValue();
            }
        } catch (Exception e) {
            LOG.log(Level.FINEST, "Error getting single value.", e);
        }
        return ret;
    }

    /**
     * Utility method to get a single value or default value if missing
     * 
     * @param policy the policy to get the list from
     * @param names names of the components
     * @return String or defaultValue if none found or Exception caught
     */
    public static String getSingleValue(String defaultValue, Policy policy, String... names) {
        String value = getSingleValue(policy, names);
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Utility method to check if checkbox is checked or default value if missing
     * 
     * @param defaultValue value of the boolean if no other found.
     * @param policy the policy to get the list from
     * @param names names of the components
     * @return true if checked or false if not checked, none found or Exception caught
     */
    public static boolean isChecked(boolean defaultValue, Policy policy,
            String... names) {
        boolean ret = defaultValue;
        try {
            for (String n : names) {
                policy = policy.getChildPolicy(n);
            }
            ret = ((CheckboxPolicy) policy).getChecked();
        } catch (Exception e) {
            LOG.log(Level.FINEST, "Error getting isChecked.", e);
        }
        return ret;
    }
}