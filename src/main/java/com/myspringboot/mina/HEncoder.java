/**
 * 
 */
package com.myspringboot.mina;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangjoun
 *
 */
public class HEncoder implements ProtocolEncoder {
	private static final Logger logger = LoggerFactory.getLogger(HEncoder.class);
	
	private final Charset charset;  
	  
    public HEncoder(Charset charset) {  
        this.charset = charset;  
    }
	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core.session.IoSession, java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		CharsetEncoder ce = charset.newEncoder();  
        String mes = (String) message;  
        IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);  
        buffer.putString(mes,ce);  
        buffer.flip();  
        out.write(buffer);  
          
        /*System.out.println("---------encode-------------"); 
        String mes = (String) message; 
        byte[] data = mes.getBytes("UTF-8"); 
        IoBuffer buffer = IoBuffer.allocate(data.length + 4); 
        buffer.putInt(data.length); 
        buffer.put(data); 
        buffer.flip(); 
        out.write(buffer); 
        out.flush();*/  
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#dispose(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void dispose(IoSession session) throws Exception {
		logger.info("Dispose called,session is " + session);  
	}

}
