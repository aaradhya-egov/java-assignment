package digit.service;


import digit.config.Configuration;
import digit.kafka.Producer;
import digit.repository.PgrRepository;
import digit.util.MdmsUtil;
import digit.validator.PgrValidator;
import digit.web.models.PGREntity;
import digit.web.models.RequestSearchCriteria;
import digit.web.models.ServiceRequest;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class PgrService {

    private Producer producer;

    private MdmsUtil mdmsUtils;

    private PgrValidator pgrValidator;

    private EnrichmentService enrichmentService;

    private Configuration config;

    private WorkflowService workflowService;

    private UserService userService;

    private PgrRepository repository;




    @Autowired
    public PgrService(Producer producer, MdmsUtil mdmsUtils, PgrValidator validator,PgrValidator pgrValidator,EnrichmentService enrichmentService,Configuration config,WorkflowService workflowService,
                      UserService userService,PgrRepository repository) {
        this.producer = producer;
        this.mdmsUtils = mdmsUtils;
        this.pgrValidator=pgrValidator;
        this.enrichmentService=enrichmentService;
        this.config=config;
        this.workflowService=workflowService;
        this.userService=userService;
        this.repository= repository;
    }

    public PGREntity create(ServiceRequest request){
        Object mdmsData = mdmsUtils.pgrMDMSCall(request);
        pgrValidator.validateCreate(request,mdmsData);
        enrichmentService.enrichCreateRequest(request);
        workflowService.updateWorkflowStatus(request);
        producer.push(config.getCreateTopic(),request);
        return request.getPgrEntity();
    }

    public List<PGREntity> search(RequestInfo requestInfo, RequestSearchCriteria criteria){
        pgrValidator.validateSearch(requestInfo, criteria);

        enrichmentService.enrichSearchRequest(requestInfo, criteria);

        if(criteria.isEmpty())
            return new ArrayList<>();

        if(criteria.getMobileNumber()!=null && CollectionUtils.isEmpty(criteria.getUserIds()))
            return new ArrayList<>();

        criteria.setIsPlainSearch(false);

        List<PGREntity> serviceWrappers = repository.getServiceWrappers(criteria);

        if(CollectionUtils.isEmpty(serviceWrappers))
            return new ArrayList<>();;

        userService.enrichUsers(serviceWrappers);
        List<PGREntity> enrichedServiceWrappers = workflowService.enrichWorkflow(requestInfo,serviceWrappers);
        Map<Long, List<PGREntity>> sortedWrappers = new TreeMap<>(Collections.reverseOrder());
        for(PGREntity svc : enrichedServiceWrappers){
            if(sortedWrappers.containsKey(svc.getService().getAuditDetails().getCreatedTime())){
                sortedWrappers.get(svc.getService().getAuditDetails().getCreatedTime()).add(svc);
            }else{
                List<PGREntity> serviceWrapperList = new ArrayList<>();
                serviceWrapperList.add(svc);
                sortedWrappers.put(svc.getService().getAuditDetails().getCreatedTime(), serviceWrapperList);
            }
        }
        List<PGREntity> sortedServiceWrappers = new ArrayList<>();
        for(Long createdTimeDesc : sortedWrappers.keySet()){
            sortedServiceWrappers.addAll(sortedWrappers.get(createdTimeDesc));
        }
        return sortedServiceWrappers;
    }


    public Integer count(RequestInfo requestInfo, RequestSearchCriteria criteria){
        criteria.setIsPlainSearch(false);
        Integer count = repository.getCount(criteria);
        return count;
    }

    public PGREntity update(ServiceRequest request){
        Object mdmsData = mdmsUtils.pgrMDMSCall(request);
        pgrValidator.validateUpdate(request, mdmsData);
        enrichmentService.enrichUpdateRequest(request);
        workflowService.updateWorkflowStatus(request);
        producer.push(config.getUpdateTopic(),request);
        return request.getPgrEntity();
    }


}
