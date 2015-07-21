package com.savoirtech.ibexcl.component;

/**
 * Created by jgoodyear on 2015-07-21.
 */
public class OpenCLDataSetParser {

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

    private Object[] args;

    public OpenCLDataSetParser(String message) {

    }

    public Object[] getArgs() {
        return args;
    }

}
