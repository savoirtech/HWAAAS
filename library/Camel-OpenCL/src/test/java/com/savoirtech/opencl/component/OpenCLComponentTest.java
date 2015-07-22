package com.savoirtech.opencl.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class OpenCLComponentTest extends CamelTestSupport {

    @Test
    public void testOpenCL() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       

        template.sendBody("direct:start", "a:float*:1024:allocate:input,b:float*:1024:allocate:input,out:float*:1024:allocate:output,n:int:1024::globalsize");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                  .to("opencl://src/test/opencl/com/savoirtech/ibexcl/components/Kernel.cl?useGPU=true&kernelTarget=add_floats")
                  .to("mock:result");
            }
        };
    }

}
