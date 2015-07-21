package com.savoirtech.opencl.component;

import java.util.Map;
import java.io.File;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link OpenCLEndpoint}.
 */
public class OpenCLComponent extends DefaultComponent {

    private static final Logger LOG = LoggerFactory.getLogger(OpenCLComponent.class);
    private String kernel = null;
    private String kernelTarget = "default";
    private Boolean useGPU = false;

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        if (remaining == null || remaining.trim().length() == 0) {
            throw new IllegalArgumentException("Kernel file must be specified.");
        }

        String kernelUri = remaining;
        File kernelFile = null;
        if (kernelUri != null) {
            kernelFile = new File(kernelUri);
        }
        LOG.info("Detecting Kernel File: " + kernelFile.getAbsolutePath());
        if (!kernelFile.exists()) {
            LOG.error("File not found!"); 
            throw new IllegalArgumentException("Kernel file not found.");
        } 
        kernel = kernelUri;

        kernelTarget = getAndRemoveParameter(parameters, "kernelTarget", String.class, "default");
        useGPU = getAndRemoveParameter(parameters, "useGPU", Boolean.class, Boolean.FALSE);

        Endpoint endpoint = new OpenCLEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }

    public String getKernel() {
        return kernel;
    }

    public String getKernelTarget() { return kernelTarget; }

    public boolean useGPU() {
        return useGPU.booleanValue();
    }
}
