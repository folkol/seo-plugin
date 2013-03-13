package com.atex.plugins.seoplugin;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.psselenium.SimpleWebDriverTestBase;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;

@RunWith(TestBaseRunner.class)
@ImportTestContent
public class RobotsTestInt extends SimpleWebDriverTestBase {

    @Inject
    private PolicyCMServer cmServer;

    private ContentId contentId;

    @Before
    public void createTestSite() throws CMException {
        Policy createContent = cmServer.createContent(2, new ExternalContentId("test.seoplugin.Site"));
        cmServer.commitContent(createContent);
        contentId = createContent.getContentId().getContentId();
    }

    @Test
    public void testRobotsTXTPresent() throws Exception {
        String html = "User-agent: *\nDisallow: /";

        guiAgent().agentLogin().loginAsSysadmin();
        guiAgent().agentContentNavigator().editContent(contentId.getContentIdString());
//        guiAgent().agentInput().typeInTextarea("robots", "Hej hej")

        assertTrue("The robots.txt did not specify useragents", html.contains("User-agent: *"));
        assertTrue("The robots.txt did not disallow listing", html.contains("Disallow: /"));
    }
}
