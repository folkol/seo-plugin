package com.atex.plugins.seoplugin;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.psselenium.SimpleWebDriverTestBase;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;

@RunWith(TestBaseRunner.class)
@ImportTestContent(files = {"test-template.xml"}, once = true)
public class RobotsTest extends SimpleWebDriverTestBase {

    @Inject
    private PolicyCMServer cmServer;

    private Policy testSite;
    private Policy testPage;

    @Before
    public void createTestSite() throws CMException {
        testSite = cmServer.createContent(2, new ExternalContentId("test.seoplugin.Site"));
        testPage = cmServer.createContent(2, testSite.getContentId().getContentId(), new ExternalContentId("test.seoplugin.Page"));
        cmServer.commitContents(new Policy[] {testSite, testPage});
    }

    @Test
    public void testRobotChangePropagatesToSite() throws Exception {
        WebDriver webDriver = guiAgent().getWebDriver();
        String magicString = "Cute Crawler";
        String newRobotText = "# robots.txt for my test site \\n\\nUser-agent: " + magicString + "\\nDisallow: ";
        String siteId = testSite.getContentId().getContentId().getContentIdString();
        String robotUrl = guiAgent().getBaseURL() + "/cmlink/" + siteId + "/robots.txt";

        webDriver.get(robotUrl);
        String html = webDriver.getPageSource();
        assertTrue("The default robots.txt should apply to all user agents", html.contains("User-agent: *"));
        assertTrue("The default robots.txt should disallow everything", html.contains("Disallow: /"));
        assertFalse("The default robots.txt should not know anything about the Cute Crawlerª", html.contains(magicString));

        guiAgent().agentLogin().loginAsSysadmin();
        guiAgent().agentContentNavigator().editContent(siteId);
        guiAgent().agentCodeMirror().setText("Manual robots.txt section", newRobotText);
        guiAgent().agentToolbar().clickOnSaveAndView();

        webDriver.get(robotUrl);
        waitForRobotPropagation(magicString);

        html = webDriver.getPageSource();
        assertFalse("The updated robots.txt still specified all (*) useragents", html.contains("User-agent: *"));
        assertFalse("The updated robots.txt still disallowed everything", html.contains("Disallow: /"));
        assertTrue("The updated robots.txt did not contain the new text", html.contains("Cute Crawler"));
    }

    @Test
    public void testPageInheritsAndOverridesParentSiteRobot() throws Exception {
        WebDriver webDriver = guiAgent().getWebDriver();
        String magicString = "Cute Crawler";
        String newRobotText = "# robots.txt for my test site \\n\\nUser-agent: " + magicString + "\\nDisallow: ";
        String pageRobotOverrideText = "# robots.txt override for my test sub page";
        String siteId = testSite.getContentId().getContentId().getContentIdString();
        String pageId = testPage.getContentId().getContentId().getContentIdString();
        String pageRobotUrl = guiAgent().getBaseURL() + "/cmlink/" + siteId + "/" + pageId + "/robots.txt";

        webDriver.get(pageRobotUrl);
        String html = webDriver.getPageSource();
        assertFalse("The default robots.txt should not know anything about the Cute Crawlerª", html.contains(magicString));

        guiAgent().agentLogin().loginAsSysadmin();
        guiAgent().agentContentNavigator().editContent(siteId);
        guiAgent().agentCodeMirror().setText("Manual robots.txt section", newRobotText);
        guiAgent().agentToolbar().clickOnSaveAndView();

        webDriver.get(pageRobotUrl);
        waitForRobotPropagation(magicString);
        html = webDriver.getPageSource();
        assertTrue("The updated robots.txt did not contain the new text", html.contains("Cute Crawler"));

        guiAgent().agentLogin().loginAsSysadmin();
        guiAgent().agentContentNavigator().editContent(pageId);
        guiAgent().agentCodeMirror().setText("Manual robots.txt section", pageRobotOverrideText);
        guiAgent().agentToolbar().clickOnSaveAndView();

        webDriver.get(pageRobotUrl);
        waitForRobotPropagation(pageRobotOverrideText);
        html = webDriver.getPageSource();
        assertFalse("The updated robots.txt still contained the parent text", html.contains("Cute Crawler"));
        assertTrue("The updated robots.txt did not contain the new text", html.contains(pageRobotOverrideText));
    }

    private void waitForRobotPropagation(String magicString) {
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
        fail("Timed out waiting for robots.txt to update");
    }
}
