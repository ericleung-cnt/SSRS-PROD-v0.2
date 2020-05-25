package org.mardep.ssrs.dns.pojo;

public class BaseServiceFaultRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	protected ServiceFault serviceFault;

	public BaseServiceFaultRuntimeException(String message, ServiceFault serviceFault) {
        super(message);
        this.serviceFault = serviceFault;
    }
	public ServiceFault getServiceFault() {
		return serviceFault;
	}
}
