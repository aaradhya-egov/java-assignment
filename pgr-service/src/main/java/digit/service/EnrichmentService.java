package digit.service;

import digit.config.Configuration;
import digit.repository.IdGenRepository;
import digit.web.models.RequestSearchCriteria;
import digit.web.models.ServiceRequest;
import digit.web.models.Workflow;
import org.egov.common.contract.idgen.IdResponse;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static digit.util.PgrConstants.USERTYPE_CITIZEN;

@Service
public class EnrichmentService {

    private IdGenRepository idGenRepository;

    private Configuration config;

    private UserService userService;

    @Autowired
    public EnrichmentService(IdGenRepository idGenRepository, Configuration config, UserService userService) {
        this.idGenRepository = idGenRepository;
        this.config = config;
        this.userService = userService;
    }


    public void enrichCreateRequest(ServiceRequest serviceRequest){

        RequestInfo requestInfo = serviceRequest.getRequestInfo();
        digit.web.models.Service service = serviceRequest.getPgrEntity().getService();
        Workflow workflow = serviceRequest.getPgrEntity().getWorkflow();
        String tenantId = service.getTenantId();

        // Enrich accountId of the logged in citizen
        if(requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN))
            serviceRequest.getPgrEntity().getService().setAccountId(requestInfo.getUserInfo().getUuid());

        userService.callUserService(serviceRequest);

        AuditDetails auditDetails = AuditDetails.builder().createdBy(serviceRequest.getRequestInfo().getUserInfo().getUuid())
                .createdTime(System.currentTimeMillis())
                .lastModifiedBy(serviceRequest.getRequestInfo().getUserInfo().getUuid())
                .lastModifiedTime(System.currentTimeMillis())
                .build();
        service.setAuditDetails(auditDetails);
        service.setId(UUID.randomUUID().toString());
        service.getAddress().setId(UUID.randomUUID().toString());
        service.getAddress().setTenantId(tenantId);
        service.setActive(true);

        if(workflow.getVerificationDocuments()!=null){
            workflow.getVerificationDocuments().forEach(document -> {
                document.setId(UUID.randomUUID().toString());
            });
        }

        if(StringUtils.isEmpty(service.getAccountId()))
            service.setAccountId(service.getCitizen().getUuid());

        List<String> customIds = getIdList(requestInfo,tenantId,config.getServiceRequestIdGenName(),config.getServiceRequestIdGenFormat(),1);

        service.setServiceRequestId(customIds.get(0));


    }

//    /**
//     * Enriches the update request (updates the lastModifiedTime in auditDetails0
//     * @param serviceRequest The update request
//     */
    public void enrichUpdateRequest(ServiceRequest serviceRequest){
        digit.web.models.Service service = serviceRequest.getPgrEntity().getService();
        AuditDetails auditDetails = AuditDetails.builder().createdBy(service.getAuditDetails().getCreatedBy()).lastModifiedBy(serviceRequest.getRequestInfo().getUserInfo().getUuid())
                .createdTime(service.getAuditDetails().getCreatedTime()).lastModifiedTime(System.currentTimeMillis()).build();

        service.setAuditDetails(auditDetails);

        userService.callUserService(serviceRequest);
    }

//    /**
//     * Enriches the search criteria in case of default search and enriches the userIds from mobileNumber in case of seach based on mobileNumber.
//     * Also sets the default limit and offset if none is provided
//     * @param requestInfo
//     * @param criteria
//     */
    public void enrichSearchRequest(RequestInfo requestInfo, RequestSearchCriteria criteria){

        if(criteria.isEmpty() && requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN)){
            String citizenMobileNumber = requestInfo.getUserInfo().getUserName();
            criteria.setMobileNumber(citizenMobileNumber);
        }

        criteria.setAccountId(requestInfo.getUserInfo().getUuid());

        String tenantId = (criteria.getTenantId()!=null) ? criteria.getTenantId() : requestInfo.getUserInfo().getTenantId();

        if(criteria.getMobileNumber()!=null){
            userService.enrichUserIds(tenantId, criteria);
        }

        if(criteria.getLimit()==null)
            criteria.setLimit(config.getDefaultLimit());

        if(criteria.getOffset()==null)
            criteria.setOffset(config.getDefaultOffset());

        if(criteria.getLimit()!=null && criteria.getLimit() > config.getMaxLimit())
            criteria.setLimit(config.getMaxLimit());

    }


    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey,
                                   String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count).getIdResponses();

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

        return idResponses.stream()
                .map(IdResponse::getId).collect(Collectors.toList());
    }

}
