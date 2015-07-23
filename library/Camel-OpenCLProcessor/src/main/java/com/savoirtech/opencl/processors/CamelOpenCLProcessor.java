package com.savoirtech.opencl.processors;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.nativelibs4java.opencl.*;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.util.*;
import com.nativelibs4java.util.*;
import org.bridj.Pointer;
import java.nio.ByteOrder;
import static org.bridj.Pointer.*;
import static java.lang.Math.*;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.Processor;
import org.apache.camel.Exchange;


public class CamelOpenCLProcessor implements Processor {

    private static final transient Logger LOG = LoggerFactory.getLogger(CamelOpenCLProcessor.class);

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
        CLContext context = JavaCL.createBestContext(CLPlatform.DeviceFeature.GPU);

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
        String src = IOUtils.readText(CamelOpenCLProcessor.class.getResource("Kernel.cl"));
        CLProgram program = context.createProgram(src);

        // Get and call the kernel :
        CLKernel addFloatsKernel = program.createKernel("add_floats");
        addFloatsKernel.setArgs(a, b, out, n);
        int[] globalSizes = new int[] { n };
        CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, globalSizes);

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

        String result = "";

        // Print the first 10 output values :
        for (int i = 0; i < 10 && i < n; i++) {
            System.out.println("out[" + i + "] = " + outPtr.get(i));
            result += " " + outPtr.get(i);
        }

        return result;
    }

}