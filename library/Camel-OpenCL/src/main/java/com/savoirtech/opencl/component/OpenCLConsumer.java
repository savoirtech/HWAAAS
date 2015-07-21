package com.savoirtech.opencl.component;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
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

/**
 * The OpenCL consumer.
 */
public class OpenCLConsumer extends ScheduledPollConsumer {
    private static final transient Logger LOG = LoggerFactory.getLogger(OpenCLConsumer.class);
    private final OpenCLEndpoint endpoint;
    private String kernel;
    private boolean useGPU;

    public OpenCLConsumer(OpenCLEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.kernel = endpoint.getKernel();
        this.useGPU = endpoint.useGPU();
    }

    @Override
    protected int poll() throws Exception {
        System.out.println("OpenCLConsumer::poll");
        Exchange exchange = endpoint.createExchange();

        // create a message body
        Date now = new Date();
        exchange.getIn().setBody("Hello World! The time is " + now);
        exchange.setExchangeId("12345");

        String exchID = exchange.getExchangeId();
        String result = null;
        try {
            result = gpuMagic(exchID);
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }        
        exchange.getIn().setBody(result);

        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    private String gpuMagic(String id) throws IOException {
        CLPlatform[] devices = JavaCL.listGPUPoweredPlatforms();

        //CLContext context = JavaCL.createBestContext();
        CLContext context;
        if (useGPU) {
            context = JavaCL.createBestContext(CLPlatform.DeviceFeature.GPU);
            LOG.info("Using GPU");
        } else {
            context = JavaCL.createBestContext(CLPlatform.DeviceFeature.CPU);
            LOG.info("Using CPU");
        }

        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();

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

        // Read the program sources and compile them :
        String src = IOUtils.readText(new File(kernel));
        //String src = IOUtils.readText(IbexCL2DImageProducer.class.getResource("Kernel.cl"));
        CLProgram program = context.createProgram(src);

        // Get and call the kernel :
        CLKernel addFloatsKernel = program.createKernel("add_floats");
        addFloatsKernel.setArgs(a, b, out, n);
        int[] globalSizes = new int[] { n };
        CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, globalSizes);

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

        // Print the first 10 output values :
        for (int i = 0; i < 10 && i < n; i++)
            LOG.info("out[" + i + "] = " + outPtr.get(i));

        return "IbexCL has executed";
    }
}
