package com.atex.plugins.seoplugin;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import com.polopoly.siteengine.structure.PagePolicy;
import com.polopoly.siteengine.structure.SitePolicy;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;

@RunWith(TestBaseRunner.class)
@ImportTestContent(once = true)
public class SitemapTestInt extends SimpleWebDriverTestBase {

    @Inject
    private PolicyCMServer cmServer;

    private SitePolicy testSite;
    private PagePolicy testPage, testPage2;

    private PublishingQueuePolicyManual publishingQueue;

    @Before
    public void createTestSite() throws CMException {
        testSite = (SitePolicy) cmServer.createContent(2, new ExternalContentId("test.seoplugin.Site"));
        ContentId siteId = testSite.getContentId().getContentId();
        testPage = (PagePolicy) cmServer.createContent(2, siteId, new ExternalContentId("test.seoplugin.Page"));
        testPage2 = (PagePolicy) cmServer.createContent(2, siteId, new ExternalContentId("test.seoplugin.Page"));
        publishingQueue = (PublishingQueuePolicyManual) cmServer.createContent(2, siteId, new ExternalContentId("com.atex.plugins.baseline.ContentListPublishingQueue"));

        ContentList contentList = publishingQueue.getContentList();
        contentList.add(0, new ContentReference(testPage.getContentId().getContentId(), null));
        contentList.add(1, new ContentReference(testPage2.getContentId().getContentId(), null));

        testSite.getContentList("sources").add(0, new ContentReference(publishingQueue.getContentId().getContentId(), null));

        cmServer.commitContents(new Policy[] {testSite, testPage, testPage2, publishingQueue});
    }

    @Test
    public void testPublishingQueueBasedSitemapPropagatesToSite() throws Exception {
        guiAgent().agentLogin().loginAsSysadmin();
        guiAgent().agentContentNavigator().openContent(publishingQueue.getContentId().getContentId().getContentIdString());
        guiAgent().agentClipboard().copyOpenedContent();
        guiAgent().agentContentNavigator().editContent(testSite.getContentId().getContentId().getContentIdString());
        guiAgent().agentClipboard().pasteContent("Publishing queues");
        guiAgent().agentInput().checkCheckbox("Use publishing queues");
        guiAgent().agentToolbar().clickOnSaveAndView();

        String sitemapUrl = guiAgent().getBaseURL() + "/cmlink/" + testSite.getContentId().getContentId().getContentIdString() + "/sitemap.xml";
        guiAgent().getWebDriver().get(sitemapUrl);
        waitForSitemapPropagation("<loc>");
        String xmlSource = guiAgent().getWebDriver().getPageSource();
        assertTrue("The publishing queue based sitemap did not contain the expected page id",
                xmlSource.contains(testPage.getContent().getContentId().getContentId().getContentIdString()));
        assertTrue("The publishing queue based sitemap did not contain the expected page id",
                xmlSource.contains(testPage2.getContent().getContentId().getContentId().getContentIdString()));
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
        fail("Timed out waiting for sitemap.xml to update");
    }
}
