package com.savoirtech.opencl.component;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nativelibs4java.opencl.*;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.util.*;
import org.bridj.Pointer;
import java.nio.ByteOrder;
import static org.bridj.Pointer.*;
import static java.lang.Math.*;
import java.io.IOException;
import java.io.File;
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

        String dataSet = exchange.getIn().getBody().toString();
        String result = null;
        try {
            result = gpuMagic(dataSet);
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
        exchange.getIn().setBody(result);
        LOG.info("Result Set: " + exchange.getIn().getBody());
    }

    private String gpuMagic(String dataSet) throws IOException {
        CLPlatform[] devices = JavaCL.listGPUPoweredPlatforms();
        LOG.info("Working on DataSet: " + dataSet);

        //CLContext context = JavaCL.createBestContext();
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

        //************
        int n = 1024;
        Pointer<Float>
            aPtr = allocateFloats(n).order(byteOrder),
            bPtr = allocateFloats(n).order(byteOrder);

        for (int i = 0; i < n; i++) {
            aPtr.set(i, (float)cos(i));
            bPtr.set(i, (float)sin(i));
        }

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Float>
            a = context.createFloatBuffer(Usage.Input, aPtr),
            b = context.createFloatBuffer(Usage.Input, bPtr);

        // Create an OpenCL output buffer :
        CLBuffer<Float> out = context.createFloatBuffer(Usage.Output, n);

        //**************

        // Read the program sources and compile them :
        String src = IOUtils.readText(new File(kernel));
        CLProgram program = context.createProgram(src);

        // Get and call the kernel :
        CLKernel userKernel = program.createKernel(kernelTarget);

        userKernel.setArgs(a, b, out, n);
        int[] globalSizes = new int[] { n };
        CLEvent addEvt = userKernel.enqueueNDRange(queue, globalSizes);

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until kernelTarget finished

        String result = "";

        Iterator it = outPtr.iterator();
        while (it.hasNext()) {
            result += it.next() + " ";
        }

        return result.trim();
    }

}
