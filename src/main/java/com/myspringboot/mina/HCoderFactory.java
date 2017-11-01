/**
 * 
 */
package com.myspringboot.mina;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author wangjoun
 *
 */
public class HCoderFactory implements ProtocolCodecFactory {
	
	private final HEncoder encoder;  
    private final HDecoder decoder;
    public HCoderFactory() {  
        this(Charset.forName("UTF-8"));  
    }  
  
    public HCoderFactory(Charset charSet) {  
        this.encoder = new HEncoder(charSet);  
        this.decoder = new HDecoder(charSet);  
    }
	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}
}
