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

package org.openo.sdno.routeservice.rest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.baseservice.roa.util.restclient.RestfulParametes;
import org.openo.baseservice.roa.util.restclient.RestfulResponse;
import org.openo.sdno.framework.container.resthelper.RestfulProxy;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.brs.invdao.NetworkElementInvDao;
import org.openo.sdno.overlayvpn.brs.model.NetworkElementMO;
import org.openo.sdno.overlayvpn.dao.common.InventoryDao;
import org.openo.sdno.overlayvpn.errorcode.ErrorCode;
import org.openo.sdno.overlayvpn.model.v2.route.NbiActionModel;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNePolicyRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNePolicyRoute;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring/applicationContext.xml",
                "classpath*:META-INF/spring/service.xml", "classpath*:spring/service.xml"})
public class PolicyRouteRoaResourceTest {

    @Mocked
    HttpServletRequest request;

    @Mocked
    HttpServletResponse response;

    @Autowired
    PolicyRouteRoaResource roaResource;

    private NbiNePolicyRoute nbiRoute = buildNbiNePolicyRoute();

    private SbiNePolicyRoute sbiRoute = buildSbiNePolicyRoute();

    @SuppressWarnings("rawtypes")
    @Before
    public void setUp() throws Exception {
        new MockInventoryDao();
        new MockRestfulProxy();
    }

    @Test
    public void testBatchQuery() throws ServiceException {
        List<String> uuidList = new ArrayList<String>();
        uuidList.add("nbiuuid");

        List<NbiNePolicyRoute> nbiNePolicyRoutes = roaResource.batchQuery(request, response, uuidList);

        assertEquals(nbiRoute, nbiNePolicyRoutes.get(0));
    }

    @Test
    public void testQuery() throws ServiceException {
        NbiNePolicyRoute nbiNePolicyRoute = roaResource.query(request, response, "nbiuuid");

        assertEquals(nbiRoute, nbiNePolicyRoute);
    }

    @Test
    public void testCreate() throws ServiceException {

        new MockNetworkElementDao();

        List<NbiNePolicyRoute> nbiRoutes = new ArrayList<NbiNePolicyRoute>();
        nbiRoutes.add(nbiRoute);

        List<NbiNePolicyRoute> nbiNePolicyRoutes = roaResource.create(request, response, nbiRoutes);

        assertEquals(nbiRoute, nbiNePolicyRoutes.get(0));
    }

    @Test
    public void testDeploy() throws ServiceException {

        NbiActionModel nbiActionModel = new NbiActionModel();
        nbiActionModel.getDeploy().add("nbiuuid");

        nbiRoute.setDeployStatus("undeploy");
        sbiRoute.setDeployStatus("undeploy");

        List<String> succeedUuids = roaResource.action(request, response, nbiActionModel);

        assertEquals(nbiRoute.getUuid(), succeedUuids.get(0));
    }

    @Test
    public void testUndeploy() throws ServiceException {

        NbiActionModel nbiActionModel = new NbiActionModel();
        nbiActionModel.getUndeploy().add("nbiuuid");

        nbiRoute.setDeployStatus("deploy");
        sbiRoute.setDeployStatus("deploy");

        List<String> succeedUuids = roaResource.action(request, response, nbiActionModel);

        assertEquals(nbiRoute.getUuid(), succeedUuids.get(0));
    }

    @Test
    public void testUpdate() throws ServiceException {

        new MockNetworkElementDao();

        nbiRoute.setDeployStatus("deploy");
        sbiRoute.setDeployStatus("deploy");

        List<NbiNePolicyRoute> nbiRoutes = new ArrayList<NbiNePolicyRoute>();
        nbiRoutes.add(nbiRoute);

        List<NbiNePolicyRoute> nbiNePolicyRoutes = roaResource.update(request, response, nbiRoutes);

        assertEquals(nbiRoute, nbiNePolicyRoutes.get(0));
    }

    @Test
    public void testDelete() throws ServiceException {

        nbiRoute.setDeployStatus("undeploy");
        sbiRoute.setDeployStatus("undeploy");

        String succeedUuid = roaResource.delete(request, response, "nbiuuid");
        assertEquals(nbiRoute.getUuid(), succeedUuid);
    }

    @SuppressWarnings("unused")
    private final class MockInventoryDao<T> extends MockUp<InventoryDao<T>> {

        @Mock
        ResultRsp queryByFilter(Class clazz, String filter, String queryResultFields) throws ServiceException {

            if(NbiNePolicyRoute.class.equals(clazz)) {

                ResultRsp<List<NbiNePolicyRoute>> resp =
                        new ResultRsp<List<NbiNePolicyRoute>>(ErrorCode.OVERLAYVPN_SUCCESS, Arrays.asList(nbiRoute));

                return resp;
            } else if(SbiNePolicyRoute.class.equals(clazz)) {

                ResultRsp<List<SbiNePolicyRoute>> resp =
                        new ResultRsp<List<SbiNePolicyRoute>>(ErrorCode.OVERLAYVPN_SUCCESS, Arrays.asList(sbiRoute));

                return resp;
            }

            return new ResultRsp<>();
        }

        @Mock
        public ResultRsp<NbiNePolicyRoute> query(Class clazz, String uuid, String tenantId) throws ServiceException {
            if(NbiNePolicyRoute.class.equals(clazz)) {

                ResultRsp<NbiNePolicyRoute> resp =
                        new ResultRsp<NbiNePolicyRoute>(ErrorCode.OVERLAYVPN_SUCCESS, nbiRoute);

                return resp;
            }

            return new ResultRsp<NbiNePolicyRoute>();
        }

        @Mock
        public ResultRsp<List<T>> update(Class clazz, List oriUpdateList, String updateFieldListStr) {
            return new ResultRsp<List<T>>(ErrorCode.OVERLAYVPN_SUCCESS);
        }

        @Mock
        public ResultRsp<List<T>> batchInsert(List<T> dataList) {
            return new ResultRsp<List<T>>(ErrorCode.OVERLAYVPN_SUCCESS);
        }

        @Mock
        public ResultRsp<String> batchDelete(Class clazz, List<String> uuids) throws ServiceException {
            return new ResultRsp<String>(ErrorCode.OVERLAYVPN_SUCCESS);
        }
    }

    private class MockNetworkElementDao extends MockUp<NetworkElementInvDao> {

        @Mock
        public NetworkElementMO query(String neId) throws ServiceException {
            NetworkElementMO ne = new NetworkElementMO();

            ne.setNativeID(neId + "n");
            ne.setControllerID(Arrays.asList(neId + "c"));
            ne.setId(neId);

            return ne;
        }
    }

    private final class MockRestfulProxy extends MockUp<RestfulProxy> {

        @Mock
        RestfulResponse put(String uri, RestfulParametes restParametes) throws ServiceException {

            ResultRsp<SbiNePolicyRoute> resultRsp = new ResultRsp<SbiNePolicyRoute>();
            resultRsp.getSuccessed().add(sbiRoute);

            RestfulResponse response = new RestfulResponse();
            response.setStatus(200);
            response.setResponseJson(JsonUtil.toJson(resultRsp));

            return response;
        }

        @Mock
        RestfulResponse post(String uri, RestfulParametes restParametes) throws ServiceException {

            ResultRsp<SbiNePolicyRoute> resultRsp = new ResultRsp<SbiNePolicyRoute>();
            resultRsp.getSuccessed().add(sbiRoute);

            RestfulResponse response = new RestfulResponse();
            response.setStatus(200);
            response.setResponseJson(JsonUtil.toJson(resultRsp));

            return response;
        }

    }

    private NbiNePolicyRoute buildNbiNePolicyRoute() {
        NbiNePolicyRoute nbiRoute = new NbiNePolicyRoute();
        nbiRoute.setType("policy");
        nbiRoute.setTrafficPolicyName("mqc2");
        nbiRoute.setInterfaceName("vlanif1");
        nbiRoute.setDirection("inbound");
        nbiRoute.setFilterAction("filterAction");
        nbiRoute.setSrcNeId("neid");
        nbiRoute.setSrcNeRole("localcpe");
        nbiRoute.setConnectionId("connectionuuid");
        nbiRoute.setName("route");
        nbiRoute.setUuid("nbiuuid");

        return nbiRoute;
    }

    private SbiNePolicyRoute buildSbiNePolicyRoute() {
        SbiNePolicyRoute sbiRoute = new SbiNePolicyRoute();
        sbiRoute.setTrafficPolicyName("mqc2");
        sbiRoute.setInterfaceName("vlanif1");
        sbiRoute.setDirection("inbound");
        sbiRoute.setFilterAction("filterAction");
        sbiRoute.setDeviceId("deviceuuid");
        sbiRoute.setControllerId("controlleruuid");
        sbiRoute.setNbiNeRouteId("nbiuuid");
        sbiRoute.setUuid("sbiuuid");

        return sbiRoute;
    }

}
