package com.atex.plugins.seoplugin.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to be mapped into a project's web.xml front and preview in order to provide robots.txt files.
 * @author sarasprang
 */
public class RobotsTxtFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(RobotsTxtFilter.class.getName());
    private final static String robotsTxtOutputTemplate = "?ot=com.atex.plugins.seoplugin.RobotsTxt.ot";
    private final static Pattern robotsTxtPattern = Pattern.compile("(.*)/[^/]*robots\\.txt", Pattern.CASE_INSENSITIVE);

    public ServletContext servletContent;

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String url = FileFilterUtils.decodeUrl(httpRequest.getRequestURI());
        LOG.log(Level.FINE, "RobotsTxtFilter will check for robots.txt present in URL: " + url);
        Matcher matcher = robotsTxtPattern.matcher(url);
        if (matcher.matches()) {
            String newURI = FileFilterUtils.getUriToFileTemplate(robotsTxtPattern, url, robotsTxtOutputTemplate);
            LOG.log(Level.FINE, "RobotsTxtFilter is redirecting to: " + newURI);
            servletContent.getRequestDispatcher(newURI).forward(httpRequest, httpResponse);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        servletContent = config.getServletContext();
    }
}