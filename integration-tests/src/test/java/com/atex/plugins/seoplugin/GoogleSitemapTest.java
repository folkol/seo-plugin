package com.atex.plugins.seoplugin;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import com.atex.plugins.baseline.collection.PublishingQueuePolicyManual;
import com.google.inject.Inject;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.psselenium.SimpleWebDriverTestBase;
import com.polopoly.siteengine.structure.SitePolicy;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;

import example.content.article.StandardArticlePolicy;

@RunWith(TestBaseRunner.class)
@ImportTestContent(files = {"test-template.xml"}, once = true)
public class GoogleSitemapTest extends SimpleWebDriverTestBase {

    @Inject
    private PolicyCMServer cmServer;

    private SitePolicy testSite;
    private StandardArticlePolicy testArticle, testArticle2;
    private PublishingQueuePolicyManual publishingQueue;

    @Before
    public void createTestSite() throws CMException {
        testSite = (SitePolicy) cmServer.createContent(2, new ExternalContentId("test.seoplugin.Site"));
        testSite.setName("Test Site with Google News support");

        ContentId siteId = testSite.getContentId().getContentId();
        testArticle = (StandardArticlePolicy) cmServer.createContent(1, siteId, new ExternalContentId("example.StandardArticle"));
        testArticle2 = (StandardArticlePolicy) cmServer.createContent(1, siteId, new ExternalContentId("example.StandardArticle"));
        publishingQueue = (PublishingQueuePolicyManual) cmServer.createContent(2, siteId, new ExternalContentId("com.atex.plugins.baseline.ContentListPublishingQueue"));
        ContentList contentList = publishingQueue.getContentList();
        contentList.add(0, new ContentReference(testArticle.getContentId().getContentId(), null));
        contentList.add(1, new ContentReference(testArticle2.getContentId().getContentId(), null));

        cmServer.commitContents(new Policy[] {testSite, publishingQueue, testArticle, testArticle2});
    }

    @Test
    public void testGoogleSitemapPropagatesToSite() throws Exception {
        guiAgent().agentLogin().loginAsSysadmin();
        guiAgent().agentContentNavigator().openContent(publishingQueue.getContentId().getContentId().getContentIdString());
        guiAgent().agentClipboard().copyOpenedContent();
        guiAgent().agentContentNavigator().editContent(testSite.getContentId().getContentId().getContentIdString());

        // Explicit use of webDriver, since the agent can not handle two labels called "Publishing queue" on the same template
        guiAgent().getWebDriver().findElement(By.xpath("//h2[text() = 'Use Google News sitemap']/../input[@type = 'checkbox']")).click();
        guiAgent().getWebDriver().findElement(By.xpath("//h2[text() = 'Google News sitemap']/..//img[contains(@src, 'paste')]/..")).click();
        guiAgent().agentWait().waitForAjaxPageToLoad();
        guiAgent().agentToolbar().clickOnSaveAndView();

        String sitemapUrl = guiAgent().getBaseURL() + "/cmlink/" + testSite.getContentId().getContentId().getContentIdString() + "/news-sitemap.xml";
        guiAgent().getWebDriver().get(sitemapUrl);
        waitForSitemapPropagation(testArticle.getContent().getContentId().getContentId().getContentIdString());

        String xmlSource = guiAgent().getWebDriver().getPageSource();
        assertTrue("The Google News sitemap did not contain the expected articleid",
                xmlSource.contains(testArticle.getContentId().getContentId().getContentIdString()));
        assertTrue("The Google News sitemap did not contain the expected articleid",
                xmlSource.contains(testArticle2.getContentId().getContentId().getContentIdString()));
        assertTrue("The Google News sitemap did display the publication name",
                xmlSource.contains("<news:name>Test Site with Google News support</news:name>"));
        assertTrue("The Google News sitemap did not contain the expected tag news:publication_date",
                xmlSource.contains("news:publication_date"));
    }

    private void waitForSitemapPropagation(String magicString) {
        long old = System.currentTimeMillis();
        long timeout = 30 * 1000L;

        while(old + timeout > System.currentTimeMillis()) {
            if(guiAgent().getWebDriver().getPageSource().contains(magicString)) {
                return;
            }
            guiAgent().getWebDriver().navigate().refresh();
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fail("Timed out waiting for news-sitemap.xml to update");
    }
}
