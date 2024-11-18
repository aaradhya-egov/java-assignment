package digit.web.controllers;


import digit.service.PgrService;
import digit.util.ResponseInfoFactory;
import digit.web.models.PGREntity;
import digit.web.models.ServiceRequest;
import digit.web.models.ServiceResponse;
    import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

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

//                @RequestMapping(value="/request/_count", method = RequestMethod.POST)
//                public ResponseEntity<ServiceResponse> requestCountPost(@NotNull @Parameter(in = ParameterIn.QUERY, description = "Unique id for a tenant." ,required=true,schema=@Schema()) @Valid @RequestParam(value = "tenantId", required = true) String tenantId,@Parameter(in = ParameterIn.QUERY, description = "Allows search for service type - comma separated list" ,schema=@Schema()) @Valid @RequestParam(value = "serviceCode", required = false) List<String> serviceCode,@Parameter(in = ParameterIn.QUERY, description = "Search by list of UUID" ,schema=@Schema()) @Valid @RequestParam(value = "ids", required = false) List<String> ids,@Parameter(in = ParameterIn.QUERY, description = "Search by mobile number of service requester" ,schema=@Schema()) @Valid @RequestParam(value = "mobileNo", required = false) String mobileNo,@Parameter(in = ParameterIn.QUERY, description = "Search by serviceRequestId of the complaint" ,schema=@Schema()) @Valid @RequestParam(value = "serviceRequestId", required = false) String serviceRequestId,@Parameter(in = ParameterIn.QUERY, description = "Search by list of Application Status" ,schema=@Schema()) @Valid @RequestParam(value = "applicationStatus", required = false) List<String> applicationStatus) {
//                        String accept = request.getHeader("Accept");
//                            if (accept != null && accept.contains("application/json")) {
//                            try {
//                            return new ResponseEntity<ServiceResponse>(objectMapper.readValue("{  \"responseInfo\" : \"{}\",  \"PGREntities\" : [ \"{}\", \"{}\" ]}", ServiceResponse.class), HttpStatus.NOT_IMPLEMENTED);
//                            } catch (IOException e) {
//                            return new ResponseEntity<ServiceResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
//                            }
//                            }
//
//                        return new ResponseEntity<ServiceResponse>(HttpStatus.NOT_IMPLEMENTED);
//                }

               @RequestMapping(value="/request/_create", method = RequestMethod.POST)
               public ResponseEntity<ServiceResponse> requestsCreatePost(@jakarta.validation.Valid @RequestBody ServiceRequest request) throws IOException {
                    PGREntity pgrEntity = pgrService.create(request);
                    ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
                    ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).pgREntities(Collections.singletonList(pgrEntity)).build();
                    return new ResponseEntity<>(response, HttpStatus.OK);

               }

//                @RequestMapping(value="/request/_search", method = RequestMethod.POST)
//                public ResponseEntity<ServiceResponse> requestSearchPost(@NotNull @Parameter(in = ParameterIn.QUERY, description = "Unique id for a tenant." ,required=true,schema=@Schema()) @Valid @RequestParam(value = "tenantId", required = true) String tenantId,@Parameter(in = ParameterIn.QUERY, description = "Allows search for service type - comma separated list" ,schema=@Schema()) @Valid @RequestParam(value = "serviceCode", required = false) List<String> serviceCode,@Parameter(in = ParameterIn.QUERY, description = "Search by list of UUID" ,schema=@Schema()) @Valid @RequestParam(value = "ids", required = false) List<String> ids,@Parameter(in = ParameterIn.QUERY, description = "Search by mobile number of service requester" ,schema=@Schema()) @Valid @RequestParam(value = "mobileNo", required = false) String mobileNo,@Parameter(in = ParameterIn.QUERY, description = "Search by serviceRequestId of the complaint" ,schema=@Schema()) @Valid @RequestParam(value = "serviceRequestId", required = false) String serviceRequestId,@Parameter(in = ParameterIn.QUERY, description = "Search by list of Application Status" ,schema=@Schema()) @Valid @RequestParam(value = "applicationStatus", required = false) List<String> applicationStatus) {
//                        String accept = request.getHeader("Accept");
//                            if (accept != null && accept.contains("application/json")) {
//                            try {
//                            return new ResponseEntity<ServiceResponse>(objectMapper.readValue("{  \"responseInfo\" : \"{}\",  \"PGREntities\" : [ \"{}\", \"{}\" ]}", ServiceResponse.class), HttpStatus.NOT_IMPLEMENTED);
//                            } catch (IOException e) {
//                            return new ResponseEntity<ServiceResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
//                            }
//                            }
//
//                        return new ResponseEntity<ServiceResponse>(HttpStatus.NOT_IMPLEMENTED);
//                }

//                @RequestMapping(value="/request/_update", method = RequestMethod.POST)
//                public ResponseEntity<ServiceResponse> requestUpdatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Request schema.", required=true, schema=@Schema()) @Valid @RequestBody ServiceRequest body) {
//                        String accept = request.getHeader("Accept");
//                            if (accept != null && accept.contains("application/json")) {
//                            try {
//                            return new ResponseEntity<ServiceResponse>(objectMapper.readValue("{  \"responseInfo\" : \"{}\",  \"PGREntities\" : [ \"{}\", \"{}\" ]}", ServiceResponse.class), HttpStatus.NOT_IMPLEMENTED);
//                            } catch (IOException e) {
//                            return new ResponseEntity<ServiceResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
//                            }
//                            }
//
//                        return new ResponseEntity<ServiceResponse>(HttpStatus.NOT_IMPLEMENTED);
//                }

        }
