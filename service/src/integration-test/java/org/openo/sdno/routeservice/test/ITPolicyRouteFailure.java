/*
 * Copyright 2017 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.sdno.routeservice.test;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.exception.HttpCode;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNePolicyRoute;
import org.openo.sdno.routeservice.drivermanager.DriverRegisterManager;
import org.openo.sdno.routeservice.mocoserver.SbiPolicyRouteFailureServer;
import org.openo.sdno.testframework.checker.IChecker;
import org.openo.sdno.testframework.http.model.HttpModelUtils;
import org.openo.sdno.testframework.http.model.HttpRequest;
import org.openo.sdno.testframework.http.model.HttpResponse;
import org.openo.sdno.testframework.http.model.HttpRquestResponse;
import org.openo.sdno.testframework.testmanager.TestManager;
import org.openo.sdno.testframework.topology.ResourceType;
import org.openo.sdno.testframework.topology.Topology;

/**
 * ITPolicyRouteFailure test class. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class ITPolicyRouteFailure extends TestManager {

    private static SbiPolicyRouteFailureServer sbiAdapterServer = new SbiPolicyRouteFailureServer();

    private static final String CREATE_POLICY_ROUTE_FAIL_1 =
            "src/integration-test/resources/policyroute/createpolicyroutefail1.json";

    private static final String CREATE_POLICY_ROUTE_FAIL_2 =
            "src/integration-test/resources/policyroute/createpolicyroutefail2.json";
    
    private static final String BATCH_QUERY_POLICY_ROUTE_FAIL =
            "src/integration-test/resources/policyroute/batchquerypolicyroutefail.json";
    
    private static final String QUERY_POLICY_ROUTE_FAIL =
            "src/integration-test/resources/policyroute/querypolicyroutefail.json";
    
    private static final String DEPLOY_POLICY_ROUTE_FAIL =
            "src/integration-test/resources/policyroute/deploypolicyroutefail.json";
    
    private static final String UNDEPLOY_POLICY_ROUTE_FAIL =
            "src/integration-test/resources/policyroute/undeploypolicyroutefail.json";
    
    private static final String UPDATE_POLICY_ROUTE_FAIL_1 =
            "src/integration-test/resources/policyroute/updatepolicyroutefail1.json";
    
    private static final String UPDATE_POLICY_ROUTE_FAIL_2 =
            "src/integration-test/resources/policyroute/updatepolicyroutefail2.json";
    
    private static final String DELETE_POLICY_ROUTE_FAIL =
            "src/integration-test/resources/policyroute/deletepolicyroutefail.json";
    
    private static final String DELETE_POLICY_ROUTE_SUCCESS_2 =
            "src/integration-test/resources/policyroute/deletepolicyroutesuccess2.json";

    private static final String TOPODATA_PATH = "src/integration-test/resources/topodata";

    private static Topology topo = new Topology(TOPODATA_PATH);

    @BeforeClass
    public static void setup() throws ServiceException {
        topo.createInvTopology();
        DriverRegisterManager.registerDriver();
        sbiAdapterServer.start();
    }

    @AfterClass
    public static void tearDown() throws ServiceException {
        sbiAdapterServer.stop();
        DriverRegisterManager.unRegisterDriver();
        topo.clearInvTopology();
    }

    @Test
    public void testCreatePolicyRouteFailAsParamInvalid() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(CREATE_POLICY_ROUTE_FAIL_1);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerParameterInvalid());

        } finally {
            
        }
    }
    
    @Test
    public void testCreatePolicyRouteFail() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(CREATE_POLICY_ROUTE_FAIL_2);
            HttpRequest createRequest = httpCreateObject.getRequest();
            List<NbiNePolicyRoute> list = JsonUtil.fromJson(createRequest.getData(), new TypeReference<List<NbiNePolicyRoute>>() {});
            list.get(0).setSrcNeId(topo.getResourceUuid(ResourceType.NETWORKELEMENT, "Ne1"));
            
            createRequest.setData(JsonUtil.toJson(list));
            
            execTestCase(createRequest, new CheckerInternalFailure());
        } finally {
            HttpRquestResponse httpDeleteObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(DELETE_POLICY_ROUTE_SUCCESS_2);
            HttpRequest deleteRequest = httpDeleteObject.getRequest();
            execTestCase(deleteRequest, new SuccessChecker());
        }
    }
    
    @Test
    public void testBatchQueryPolicyRouteFail() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(BATCH_QUERY_POLICY_ROUTE_FAIL);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerParameterInvalid());

        } finally {
            
        }
    }
    
    @Test
    public void testQueryPolicyRouteFail() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(QUERY_POLICY_ROUTE_FAIL);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerParameterInvalid());

        } finally {
            
        }
    }
    
    @Test
    public void testUndeployPolicyRouteFail() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(UNDEPLOY_POLICY_ROUTE_FAIL);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerInternalFailure());

        } finally {
            
        }
    }
    
    @Test
    public void testDeployPolicyRouteFail() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(DEPLOY_POLICY_ROUTE_FAIL);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerInternalFailure());

        } finally {
            
        }
    }
    
    @Test
    public void testUpdatePolicyRouteFailAsParamInvalid() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(UPDATE_POLICY_ROUTE_FAIL_1);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerParameterInvalid());

        } finally {
            
        }
    }
    
    @Test
    public void testUpdatePolicyRouteFail() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(UPDATE_POLICY_ROUTE_FAIL_2);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerInternalFailure());

        } finally {
            
        }
    }
    
    @Test
    public void testDeletePolicyRouteFail() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(DELETE_POLICY_ROUTE_FAIL);
            HttpRequest createRequest = httpCreateObject.getRequest();
            execTestCase(createRequest, new CheckerParameterInvalid());

        } finally {
            
        }
    }

    private class CheckerParameterInvalid implements IChecker {

        @Override
        public boolean check(HttpResponse response) {
            if(HttpCode.BAD_REQUEST == response.getStatus()) {
                return true;
            }

            return false;
        }
    }
    
    private class CheckerInternalFailure implements IChecker {

        @Override
        public boolean check(HttpResponse response) {
            if(HttpCode.ERR_FAILED == response.getStatus()) {
                return true;
            }

            return false;
        }
    }
    
    private class SuccessChecker implements IChecker {

        @Override
        public boolean check(HttpResponse response) {
            if(HttpCode.isSucess(response.getStatus())) {
                return true;
            }

            return false;
        }

    }

}
