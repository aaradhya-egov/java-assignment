package digit.web.controllers;


import digit.service.PgrService;
import digit.util.PgrConstants;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import javax.validation.Valid;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-18T11:04:31.362244598+05:30[Asia/Kolkata]")
@Controller
    @RequestMapping("")
    public class RequestApiController{

        private final ObjectMapper objectMapper;

        private final HttpServletRequest request;

        private final PgrService pgrService;

        private final ResponseInfoFactory responseInfoFactory;

        @Autowired
        public RequestApiController(ObjectMapper objectMapper, HttpServletRequest request,PgrService pgrService,ResponseInfoFactory responseInfoFactory) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.pgrService=pgrService;
        this.responseInfoFactory=responseInfoFactory;
        }

    @RequestMapping(value="/request/_count", method = RequestMethod.POST)
    public ResponseEntity<CountResponse> requestsCountPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                           @Valid @ModelAttribute RequestSearchCriteria criteria) {
        Integer count = pgrService.count(requestInfoWrapper.getRequestInfo(), criteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        CountResponse response = CountResponse.builder().responseInfo(responseInfo).count(count).build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

               @RequestMapping(value="/request/_create", method = RequestMethod.POST)
               public ResponseEntity<ServiceResponse> requestsCreatePost(@jakarta.validation.Valid @RequestBody ServiceRequest request) throws IOException {
                    PGREntity pgrEntity = pgrService.create(request);
                    ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
                    ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).pgREntities(Collections.singletonList(pgrEntity)).build();
                    return new ResponseEntity<>(response, HttpStatus.OK);

               }

    @RequestMapping(value="/request/_search", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> requestsSearchPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                              @Valid @ModelAttribute RequestSearchCriteria criteria) {

        String tenantId = criteria.getTenantId();
        List<PGREntity> pgrEntities = pgrService.search(requestInfoWrapper.getRequestInfo(), criteria);
//        Map<String,Integer> dynamicData = pgrService.getDynamicData(tenantId);

//        int complaintsResolved = dynamicData.get(PgrConstants.COMPLAINTS_RESOLVED);
//        int averageResolutionTime = dynamicData.get(PgrConstants.AVERAGE_RESOLUTION_TIME);
//        int complaintTypes = pgrService.getComplaintTypes();

        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).pgREntities(pgrEntities).build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @RequestMapping(value="/request/_update", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> requestsUpdatePost(@Valid @RequestBody ServiceRequest request) throws IOException {
        PGREntity pgrEntity = pgrService.update(request);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).pgREntities(Collections.singletonList(pgrEntity)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

        }
