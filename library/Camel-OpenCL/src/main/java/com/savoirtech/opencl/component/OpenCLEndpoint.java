package com.savoirtech.opencl.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represents an OpenCL endpoint.
 */
public class OpenCLEndpoint extends DefaultEndpoint {

    private String kernel = null;
    private String kernelTarget = null;
    private boolean useGPU = false;

    public OpenCLEndpoint() {
    }

    public OpenCLEndpoint(String uri, OpenCLComponent component) {
        super(uri, component);
        kernel = component.getKernel();
        kernelTarget = component.getKernelTarget();
        useGPU = component.useGPU();
    }

    public OpenCLEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new OpenCLProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new OpenCLConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    public String getKernel() {
        return kernel;
    }

    public String getKernelTarget() { return kernelTarget; }

    public boolean useGPU() {
        return useGPU;
    }

}
