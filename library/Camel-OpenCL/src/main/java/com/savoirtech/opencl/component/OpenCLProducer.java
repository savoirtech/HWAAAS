package com.savoirtech.opencl.component;

import com.nativelibs4java.opencl.*;
import com.nativelibs4java.util.IOUtils;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.bridj.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Iterator;

/**
 * The OpenCL producer.
 */
public class OpenCLProducer extends DefaultProducer {

    private static final transient Logger LOG = LoggerFactory.getLogger(OpenCLProducer.class);
    private OpenCLEndpoint endpoint;
    private String kernel, kernelTarget;
    private boolean useGPU;

    public OpenCLProducer(OpenCLEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.kernel = endpoint.getKernel();
        this.kernelTarget = endpoint.getKernelTarget();
        this.useGPU = endpoint.useGPU();
    }

    public void process(Exchange exchange) throws Exception {
        LOG.info("DataSet: " + exchange.getIn().getBody());

        String message = exchange.getIn().getBody().toString();
        String result = null;
        try {
            result = gpuMagic(message);
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
        exchange.getIn().setBody(result);
        LOG.info("Result Set: " + exchange.getIn().getBody());
    }

    private String gpuMagic(String message) throws IOException {
        CLPlatform[] devices = JavaCL.listGPUPoweredPlatforms();
        LOG.info("Working on DataSet: " + message);

        CLContext context;
        if (useGPU) {
            context = JavaCL.createBestContext(CLPlatform.DeviceFeature.GPU);
            LOG.info("Using GPU for OpenCL Computation.");
        } else {
            context = JavaCL.createBestContext(CLPlatform.DeviceFeature.CPU);
            LOG.info("Using CPU for OpenCL Computation.");
        }

        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();

        OpenCLDataSet dataSet = OpenCLDataSetParser.Parse(message, context, byteOrder);

        // Read the program sources and compile them :
        String src = IOUtils.readText(new File(kernel));
        CLProgram program = context.createProgram(src);

        // Get and call the kernel :
        CLKernel userKernel = program.createKernel(kernelTarget);

        userKernel.setArgs(dataSet.getArgs());
        int[] globalSizes = new int[] { dataSet.getGlobalSize() };
        CLEvent addEvt = userKernel.enqueueNDRange(queue, globalSizes);

        Pointer<Float> outPtr = dataSet.getOut().read(queue, addEvt); // blocks until kernelTarget finished

        String result = "";

        Iterator it = outPtr.iterator();
        while (it.hasNext()) {
            result += it.next() + " ";
        }

        return result.trim();
    }

}
