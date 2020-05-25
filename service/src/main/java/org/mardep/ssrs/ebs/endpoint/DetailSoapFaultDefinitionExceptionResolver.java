package org.mardep.ssrs.ebs.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.Marshaller;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    private Marshaller marshaller;
    
//    @Override
//    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
//        logger.warn("Exception processed ", ex);
//        SoapFaultDetail detail = fault.addFaultDetail();
//        Result result  = detail.getResult();
//        ServiceFault serviceFault = null;
//        if (ex instanceof BaseServiceFaultRuntimeException) {
//            serviceFault = ((BaseServiceFaultRuntimeException) ex).getServiceFault();
//            if(serviceFault.getDescription()==null){
//            	serviceFault.setDescription(ex.getMessage());
//            }
//        }else{
//        	serviceFault = new ServiceFault("", "un-expected exception!");
//        	serviceFault.setCategory(ServiceFaultCategory.FATAL);
//        }
//        try{
//        	marshaller.marshal(serviceFault, result);
//        }catch(Exception e){
//        	logger.error("Fail to marshal", e);
//        }
//    }
    
    public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}
}
