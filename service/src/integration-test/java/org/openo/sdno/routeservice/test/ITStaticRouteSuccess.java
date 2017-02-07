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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.routeservice.drivermanager.DriverRegisterManager;
import org.openo.sdno.routeservice.mocoserver.SbiStaticRouteSuccessServer;
import org.openo.sdno.testframework.checker.IChecker;
import org.openo.sdno.testframework.http.model.HttpModelUtils;
import org.openo.sdno.testframework.http.model.HttpRequest;
import org.openo.sdno.testframework.http.model.HttpResponse;
import org.openo.sdno.testframework.http.model.HttpRquestResponse;
import org.openo.sdno.testframework.testmanager.TestManager;
import org.openo.sdno.testframework.topology.ResourceType;
import org.openo.sdno.testframework.topology.Topology;

/**
 * ITStaticRouteSuccess test class. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class ITStaticRouteSuccess extends TestManager {

    private static final String CREATE_STATIC_ROUTE_SUCCESS =
            "src/integration-test/resources/staticroute/createstaticroutesuccess.json";
    
    private static final String BATCH_QUERY_STATIC_ROUTE_SUCCESS =
            "src/integration-test/resources/staticroute/batchquerystaticroutesuccess.json";
    
    private static final String QUERY_STATIC_ROUTE_SUCCESS =
            "src/integration-test/resources/staticroute/querystaticroutesuccess.json";
    
    private static final String UNDEPLOY_STATIC_ROUTE_SUCCESS =
            "src/integration-test/resources/staticroute/undeploystaticroutesuccess.json";
    
    private static final String DEPLOY_STATIC_ROUTE_SUCCESS =
            "src/integration-test/resources/staticroute/deploystaticroutesuccess.json";
    
    private static final String UPDATE_STATIC_ROUTE_SUCCESS =
            "src/integration-test/resources/staticroute/updatestaticroutesuccess.json";
    
    private static final String DELETE_STATIC_ROUTE_SUCCESS =
            "src/integration-test/resources/staticroute/deletestaticroutesuccess.json";

    private static final String TOPODATA_PATH = "src/integration-test/resources/topodata";

    private static Topology topo = new Topology(TOPODATA_PATH);

    private static SbiStaticRouteSuccessServer sbiAdapterServer = new SbiStaticRouteSuccessServer();

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
    public void testOperationStaticRouteSuccess() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(CREATE_STATIC_ROUTE_SUCCESS);
            HttpRequest createRequest = httpCreateObject.getRequest();
            List<NbiNeStaticRoute> list = JsonUtil.fromJson(createRequest.getData(), new TypeReference<List<NbiNeStaticRoute>>() {});
            list.get(0).setSrcNeId(topo.getResourceUuid(ResourceType.NETWORKELEMENT, "Ne1"));
            createRequest.setData(JsonUtil.toJson(list));
            execTestCase(createRequest, new SuccessChecker());
            
            // test undeploy
            HttpRquestResponse httpUndeployObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(UNDEPLOY_STATIC_ROUTE_SUCCESS);
            HttpRequest undeployRequest = httpUndeployObject.getRequest();
            execTestCase(undeployRequest, new SuccessChecker());
            
            // test deploy
            HttpRquestResponse httpDeployObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(DEPLOY_STATIC_ROUTE_SUCCESS);
            HttpRequest deployRequest = httpDeployObject.getRequest();
            execTestCase(deployRequest, new SuccessChecker());
            
            // test update
            HttpRquestResponse httpUpdateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(UPDATE_STATIC_ROUTE_SUCCESS);
            HttpRequest updateRequest = httpUpdateObject.getRequest();
            execTestCase(updateRequest, new SuccessChecker());
            
            // test delete
            execTestCase(undeployRequest, new SuccessChecker());
            HttpRquestResponse httpDeleteObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(DELETE_STATIC_ROUTE_SUCCESS);
            HttpRequest deleteRequest = httpDeleteObject.getRequest();
            execTestCase(deleteRequest, new SuccessChecker());
            
        } finally {
            
        }
    }
    
    @Test
    public void testBatchQueryStaticRouteSuccess() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(BATCH_QUERY_STATIC_ROUTE_SUCCESS);
            HttpRequest createRequest = httpCreateObject.getRequest();
            
            execTestCase(createRequest, new SuccessChecker());
        } finally {
            
        }
    }
    
    @Test
    public void testQueryStaticRouteSuccess() throws ServiceException {
        try {
            // test create
            HttpRquestResponse httpCreateObject =
                    HttpModelUtils.praseHttpRquestResponseFromFile(QUERY_STATIC_ROUTE_SUCCESS);
            HttpRequest createRequest = httpCreateObject.getRequest();
            
            execTestCase(createRequest, new SuccessChecker());
        } finally {
            
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
