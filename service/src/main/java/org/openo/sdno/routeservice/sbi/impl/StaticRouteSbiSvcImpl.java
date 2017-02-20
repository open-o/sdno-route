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

package org.openo.sdno.routeservice.sbi.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.type.TypeReference;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.baseservice.roa.util.restclient.RestfulParametes;
import org.openo.baseservice.roa.util.restclient.RestfulResponse;
import org.openo.sdno.framework.container.resthelper.RestfulProxy;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.consts.UrlAdapterConst;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.rest.ResponseUtils;
import org.openo.sdno.routeservice.sbi.inf.StaticRouteSbiService;
import org.openo.sdno.routeservice.util.operation.RestfulParametesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static route south branch interface implementation. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class StaticRouteSbiSvcImpl implements StaticRouteSbiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticRouteSbiSvcImpl.class);

    @Override
    public ResultRsp<SbiNeStaticRoute> createRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {

        String ctrlUuid = sbiRoutes.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiRoutes), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL + UrlAdapterConst.CREATE_STATIC_ROUTE;

        LOGGER.info("createRoutes begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("createRoutes response: " + response.getStatus() + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNeStaticRoute> restResult =
                JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNeStaticRoute>>() {});

        LOGGER.info("createRoutes end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNeStaticRoute> deployRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {

        String ctrlUuid = sbiRoutes.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiRoutes), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL + UrlAdapterConst.CREATE_STATIC_ROUTE;

        LOGGER.info("deployRoutes begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("deployRoutes response: " + response.getStatus() + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNeStaticRoute> restResult =
                JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNeStaticRoute>>() {});

        LOGGER.info("deployRoutes end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNeStaticRoute> undeployRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {

        String deviceId = sbiRoutes.get(0).getDeviceId();
        String ctrlUuid = sbiRoutes.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiRoutes), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL
                + MessageFormat.format(UrlAdapterConst.DELETE_STATIC_ROUTE, deviceId);

        LOGGER.info("undeployRoutes begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("undeployRoutes response: " + response.getStatus() + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNeStaticRoute> restResult =
                JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNeStaticRoute>>() {});

        LOGGER.info("undeployRoutes end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNeStaticRoute> updateRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {

        String ctrlUuid = sbiRoutes.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiRoutes), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL + UrlAdapterConst.UPDATE_STATIC_ROUTE;

        LOGGER.info("updateRoutes begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.put(url, restfulParametes);

        LOGGER.info("updateRoutes response: " + response.getStatus() + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNeStaticRoute> restResult =
                JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNeStaticRoute>>() {});

        LOGGER.info("updateRoutes end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNeStaticRoute> queryRoutes(SbiNeStaticRoute sbiRoute) throws ServiceException {

        String ctrlUuid = sbiRoute.getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(Arrays.asList(sbiRoute)), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL + UrlAdapterConst.QUERY_STATIC_ROUTE;

        LOGGER.info("queryRoutes begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("queryRoutes response: " + response.getStatus() + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNeStaticRoute> restResult =
                JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNeStaticRoute>>() {});

        LOGGER.info("queryRoutes end, result = " + restResult.toString());

        return restResult;
    }

}
