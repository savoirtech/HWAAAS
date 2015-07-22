package com.savoirtech.opencl.component;

import com.nativelibs4java.opencl.CLBuffer;

/**
 * Created by jgoodyear on 2015-07-22.
 */
public class OpenCLDataSet {

    private Object[] args;
    private int globalSize;
    private CLBuffer<Float> out;

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setGlobalSize(int globalSize) {
        this.globalSize = globalSize;
    }

    public int getGlobalSize() {
        return globalSize;
    }

    public void setOut(CLBuffer<Float> out) {
        this.out = out;
    }

    public CLBuffer<Float> getOut() {
        return out;
    }
}
