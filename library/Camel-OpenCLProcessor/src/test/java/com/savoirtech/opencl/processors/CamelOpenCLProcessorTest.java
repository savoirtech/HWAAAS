package com.savoirtech.opencl.processors;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;

import org.junit.Test;

public class CamelOpenCLProcessorTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/openclProcessorDemo.xml";
    }

    @Test
    public void testRoute() throws Exception {
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);

        template.sendBody("direct:start", "10240");
        // assert expectations
        assertMockEndpointsSatisfied();
    }

}