package com.atex.plugins.seoplugin;

import static junit.framework.TestCase.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;

@RunWith(TestBaseRunner.class)
@ImportTestContent
public class RobotsTestInt {

    @Inject
    private PolicyCMServer cmServer;

    @Test
    public void dummy() throws CMException {
        Policy createContent = cmServer.createContent(2, new ExternalContentId("test.seoplugin.Site"));
        cmServer.commitContent(createContent);



        assertTrue(true);
    }
}
