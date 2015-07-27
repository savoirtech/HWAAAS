package com.savoirtech.opencl.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by jgoodyear on 2015-07-27.
 */
public class InputProcessor implements Processor {

    private static final transient Logger LOG = LoggerFactory.getLogger(InputProcessor.class);

    public void process(Exchange exchange) throws Exception {
        LOG.info("FileContent: " + exchange.getIn().getBody(String.class));

        String message = exchange.getIn().getBody(String.class);

        exchange.getIn().setBody(message);
    }
}
