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

import static org.junit.Assert.*;

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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
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
public class NqaRoaResourceTest {

    @Mocked
    HttpServletRequest request;

    @Mocked
    HttpServletResponse response;

    @Autowired
    NqaRoaResource roaResource;

    private NbiNqa nbiNqa = buildNbiNqa();

    private SbiNqa sbiNqa = buildSbiNqa();

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

        List<NbiNqa> nbiNqas = roaResource.batchQuery(request, response, uuidList);

        assertEquals(nbiNqa, nbiNqas.get(0));
    }

    @Test
    public void testQuery() throws ServiceException {
        NbiNqa nbiNqa = roaResource.query(request, response, "nbiuuid");

        assertEquals(nbiNqa, nbiNqa);
    }

    @Test
    public void testCreate() throws ServiceException {

        new MockNetworkElementDao();

        List<NbiNqa> nbiRoutes = new ArrayList<NbiNqa>();
        nbiRoutes.add(nbiNqa);

        List<NbiNqa> nbiNqas = roaResource.create(request, response, nbiRoutes);

        assertEquals(nbiNqa, nbiNqas.get(0));
    }

    @Test
    public void testDeploy() throws ServiceException {

        NbiActionModel nbiActionModel = new NbiActionModel();
        nbiActionModel.getDeploy().add("nbiuuid");

        nbiNqa.setDeployStatus("undeploy");
        sbiNqa.setDeployStatus("undeploy");

        List<String> succeedUuids = roaResource.action(request, response, nbiActionModel);

        assertEquals(nbiNqa.getUuid(), succeedUuids.get(0));
    }

    @Test
    public void testUndeploy() throws ServiceException {

        NbiActionModel nbiActionModel = new NbiActionModel();
        nbiActionModel.getUndeploy().add("nbiuuid");

        nbiNqa.setDeployStatus("deploy");
        sbiNqa.setDeployStatus("deploy");

        List<String> succeedUuids = roaResource.action(request, response, nbiActionModel);

        assertEquals(nbiNqa.getUuid(), succeedUuids.get(0));
    }

    @Test
    public void testUpdate() throws ServiceException {

        new MockNetworkElementDao();

        nbiNqa.setDeployStatus("deploy");
        sbiNqa.setDeployStatus("deploy");

        List<NbiNqa> nbiRoutes = new ArrayList<NbiNqa>();
        nbiRoutes.add(nbiNqa);

        List<NbiNqa> nbiNqas = roaResource.update(request, response, nbiRoutes);

        assertEquals(nbiNqa, nbiNqas.get(0));
    }

    @Test
    public void testDelete() throws ServiceException {

        nbiNqa.setDeployStatus("undeploy");
        sbiNqa.setDeployStatus("undeploy");

        String succeedUuid = roaResource.delete(request, response, "nbiuuid");
        assertEquals(nbiNqa.getUuid(), succeedUuid);
    }

    @SuppressWarnings("unused")
    private final class MockInventoryDao<T> extends MockUp<InventoryDao<T>> {

        @Mock
        ResultRsp queryByFilter(Class clazz, String filter, String queryResultFields) throws ServiceException {

            if(NbiNqa.class.equals(clazz)) {

                ResultRsp<List<NbiNqa>> resp =
                        new ResultRsp<List<NbiNqa>>(ErrorCode.OVERLAYVPN_SUCCESS, Arrays.asList(nbiNqa));

                return resp;
            } else if(SbiNqa.class.equals(clazz)) {

                ResultRsp<List<SbiNqa>> resp =
                        new ResultRsp<List<SbiNqa>>(ErrorCode.OVERLAYVPN_SUCCESS, Arrays.asList(sbiNqa));

                return resp;
            }

            return new ResultRsp<>();
        }

        @Mock
        public ResultRsp<NbiNqa> query(Class clazz, String uuid, String tenantId) throws ServiceException {
            if(NbiNqa.class.equals(clazz)) {

                ResultRsp<NbiNqa> resp =
                        new ResultRsp<NbiNqa>(ErrorCode.OVERLAYVPN_SUCCESS, nbiNqa);

                return resp;
            }

            return new ResultRsp<NbiNqa>();
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

            ResultRsp<SbiNqa> resultRsp = new ResultRsp<SbiNqa>();
            resultRsp.getSuccessed().add(sbiNqa);

            RestfulResponse response = new RestfulResponse();
            response.setStatus(200);
            response.setResponseJson(JsonUtil.toJson(resultRsp));

            return response;
        }

        @Mock
        RestfulResponse post(String uri, RestfulParametes restParametes) throws ServiceException {

            ResultRsp<SbiNqa> resultRsp = new ResultRsp<SbiNqa>();
            resultRsp.getSuccessed().add(sbiNqa);

            RestfulResponse response = new RestfulResponse();
            response.setStatus(200);
            response.setResponseJson(JsonUtil.toJson(resultRsp));

            return response;
        }

    }

    private NbiNqa buildNbiNqa() {
        NbiNqa nbiNqa = new NbiNqa();
        nbiNqa.setNeId("neId");
        nbiNqa.setNeRole("localcpe");
        nbiNqa.setDstIp("10.23.36.99");
        nbiNqa.setTestType("ping");
        nbiNqa.setTtl("50");
        nbiNqa.setInterval("20");
        nbiNqa.setName("nqa");
        nbiNqa.setUuid("nbiuuid");

        return nbiNqa;
    }

    private SbiNqa buildSbiNqa() {
        SbiNqa sbiNqa = new SbiNqa();
        sbiNqa.setNeId("neId");
        sbiNqa.setNeRole("localcpe");
        sbiNqa.setDstIp("10.23.36.99");
        sbiNqa.setTestType("ping");
        sbiNqa.setTtl("50");
        sbiNqa.setInterval("20");
        sbiNqa.setName("nqa");
        sbiNqa.setDeviceId("deviceuuid");
        sbiNqa.setControllerId("controlleruuid");
        sbiNqa.setNbiNeRouteId("nbiuuid");
        sbiNqa.setUuid("sbiuuid");

        return sbiNqa;
    }

}
