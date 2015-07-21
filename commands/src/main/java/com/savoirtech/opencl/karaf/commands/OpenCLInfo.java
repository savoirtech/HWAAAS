package com.savoirtech.opencl.karaf.commands;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Argument;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.JavaCL;

import java.io.IOException;

@Command(scope = "opencl", name = "info", description="Display OpenCL capable devices.")
public class OpenCLInfo extends OsgiCommandSupport {

    @Argument(index = 0, name = "arg", description = "An option.", required = false, multiValued = false)
    String arg = null;

    @Override
    protected Object doExecute() throws Exception {
        CLPlatform[] platforms = JavaCL.listGPUPoweredPlatforms();
        System.out.println("#### OpenCL Powered Platforms ####");
        for (int p = 0; p < platforms.length; p++) {
            System.out.println("Platform Name:     " + platforms[p].getName());
            System.out.println("Platform Profile:  " + platforms[p].getProfile());
            System.out.println("Platform Version:  " + platforms[p].getVersion());
            System.out.println("Platform Vendor:   " + platforms[p].getVendor());
            CLDevice[] devices = platforms[p].listAllDevices(true);
            for (int d = 0; d < devices.length; d++) {
                System.out.println("");
                System.out.println("Device Name:            " + devices[d].getName());
                System.out.println("Device Version:         " + devices[d].getOpenCLVersion());
                System.out.println("Device Driver:          " + devices[d].getDriverVersion());
                System.out.println("Device MemCache Line:   " + devices[d].getGlobalMemCachelineSize());
                System.out.println("Device MemCache Size:   " + devices[d].getGlobalMemCacheSize());
                System.out.println("Device LocalMem Size:   " + devices[d].getLocalMemSize());
                System.out.println("Device MaxConBuf Size:  " + devices[d].getMaxConstantBufferSize()); 
                System.out.println("Device Global Mem:      " + devices[d].getGlobalMemSize());
                System.out.println("Device Max Mem Alloc:   " + devices[d].getMaxMemAllocSize());
                System.out.println("Device Clock Speed:     " + devices[d].getMaxClockFrequency());
                System.out.println("Device Compute Units:   " + devices[d].getMaxComputeUnits());
                System.out.println("Device Max Work Items:  " + devices[d].getMaxWorkItemDimensions());
                System.out.println("Device Max Work Groups: " + devices[d].getMaxWorkGroupSize());
                System.out.println("Device Branding:       " + devices[d].toString());
            }
        }
        return null;
    }
}
