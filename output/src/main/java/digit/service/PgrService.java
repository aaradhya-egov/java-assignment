package digit.service;


import digit.config.Configuration;
import digit.kafka.Producer;
import digit.util.MdmsUtil;
import digit.validator.PgrValidator;
import digit.web.models.PGREntity;
import digit.web.models.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PgrService {

    private Producer producer;

    private MdmsUtil mdmsUtils;

    private PgrValidator pgrValidator;

    private EnrichmentService enrichmentService;

    private Configuration config;

    private WorkflowService workflowService;


    @Autowired
    public PgrService(Producer producer, MdmsUtil mdmsUtils, PgrValidator validator,PgrValidator pgrValidator,EnrichmentService enrichmentService,Configuration config,WorkflowService workflowService) {
        this.producer = producer;
        this.mdmsUtils = mdmsUtils;
        this.pgrValidator=pgrValidator;
        this.enrichmentService=enrichmentService;
        this.config=config;
        this.workflowService=workflowService;
    }

    public PGREntity create(ServiceRequest request){
//        Object mdmsData = mdmsUtils.pgrMDMSCall(request);
//        pgrValidator.validateCreate(request,mdmsData);
        enrichmentService.enrichCreateRequest(request);
//        workflowService.updateWorkflowStatus(request);
        producer.push(config.getCreateTopic(),request);
        return request.getPgrEntity();
    }




}
