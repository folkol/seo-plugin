package com.atex.plugins.seoplugin.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for servlet filters that are to provide file behavior.
 * @author sarasprang
 */
public class FileFilterUtils {
    private static final Logger LOG = Logger.getLogger(FileFilterUtils.class.getName());

    public static String decodeUrl(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.log(Level.WARNING, "Could not decode url to UTF-8:"+url, e);
        }
        return url;
    }

    public static String getUriToFileTemplate(Pattern filePattern, String uri, String fileOutputTemplate) {
        Matcher matcher = filePattern.matcher(uri);
        if (!StringUtils.isEmpty(uri)) {
            if (matcher.matches()) {
                uri = matcher.group(1);
            }
            if (StringUtils.isEmpty(uri)) {
                uri = "/";
            }
            uri += fileOutputTemplate;
        }
        return uri;
    }
}