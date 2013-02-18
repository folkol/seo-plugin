package com.atex.plugins.seoplugin;

import java.util.Date;

public interface GoogleNewsSitemapModelTypeDescription {

    /**
     * The title of the news article. Note: The title may be truncated for space
     * reasons when shown on Google News. Article title tags should only include
     * the title of the article as it appears on your site. Please make sure not
     * to include the author name, the publication name, or publication date as
     * part of the title tag.
     * 
     * @return title of the news article or empty/null to omit.
     */
    public String getTitle();

    /**
     * Possible values include "Subscription" or "Registration", describing the
     * accessibility of the article. If the article is accessible to Google News
     * readers without a registration or subscription, this tag should be
     * omitted.
     * 
     * @return accessibility description ("Subscription" or "Registration") or empty/null to omit.
     */
    public String getAccess();

    /**
     * Article publication date. Please ensure that you give
     * the original date and time at which the article was published on your
     * site; do not give the time at which the article was added to your Sitemap.
     * 
     * @return publication date.
     */
    public Date getPublishedDate();

    /**
     * A comma-separated list of keywords describing the topic of the article.
     * Keywords may be drawn from, but are not limited to, the list of existing
     * Google News keywords.
     * 
     * @return comma-separated list of keywords or empty/null to omit.
     */
    public String getKeywords();

    /**
     * A comma-separated list of properties characterizing the content of the
     * article, such as "PressRelease" or "UserGenerated." See Google News
     * content properties for a list of possible values. Your content must be
     * labeled accurately, in order to provide a consistent experience for our
     * users.
     * 
     * @return comma-separated list of properties or empty/null to omit.
     */
    public String getGenres();
}
