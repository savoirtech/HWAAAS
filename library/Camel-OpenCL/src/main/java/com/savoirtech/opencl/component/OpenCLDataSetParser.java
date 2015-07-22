package com.savoirtech.opencl.component;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLMem;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;

import static java.lang.Math.*;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by jgoodyear on 2015-07-21.
 */
public class OpenCLDataSetParser {

    private static OpenCLDataSet dataSet = new OpenCLDataSet();
    /**
     * varName : Type : size : initialization
     *
     * Example:
     *
     * a : float* : 1024 : allocate : usageInput
     * b : float* : 1024 : allocate : usageInput
     * out : float* : 1024 : allocate : usageOutput
     * n : int : 1024 : 1024 : globalSize
     *
     * a:float*:1024:allocate:input,b:float*:1024:allocate:input,out:float*:1024:allocate:output,n:int:1024::globalsize
     */

    public static OpenCLDataSet Parse(String message, CLContext context, ByteOrder byteOrder) {

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
                a = context.createFloatBuffer(CLMem.Usage.Input, aPtr),
                b = context.createFloatBuffer(CLMem.Usage.Input, bPtr);

        // Create an OpenCL output buffer :
        CLBuffer<Float> out = context.createFloatBuffer(CLMem.Usage.Output, n);

        ArrayList<Object> list = new ArrayList<Object>();
        list.add(a);
        list.add(b);
        list.add(out);
        list.add(n);

        dataSet.setArgs(list.toArray());
        dataSet.setGlobalSize(n);
        dataSet.setOut(out);

        return dataSet;
    }

}
