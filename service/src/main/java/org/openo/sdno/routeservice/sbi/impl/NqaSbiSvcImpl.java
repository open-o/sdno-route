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
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.rest.ResponseUtils;
import org.openo.sdno.routeservice.sbi.inf.NqaSbiService;
import org.openo.sdno.routeservice.util.operation.RestfulParametesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NQA south branch interface implementation. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class NqaSbiSvcImpl implements NqaSbiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NqaSbiSvcImpl.class);

    @Override
    public ResultRsp<SbiNqa> createNqa(List<SbiNqa> sbiNqa) throws ServiceException {

        String deviceId = sbiNqa.get(0).getDeviceId();
        String ctrlUuid = sbiNqa.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiNqa), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL
                + MessageFormat.format(UrlAdapterConst.CREATE_POLICY_NQA, deviceId);

        LOGGER.info("createNqa begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("status: " + response.getStatus() + " content: " + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNqa> restResult = JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNqa>>() {});

        LOGGER.info("createNqa end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNqa> deployNqa(List<SbiNqa> sbiNqa) throws ServiceException {

        String deviceId = sbiNqa.get(0).getDeviceId();
        String ctrlUuid = sbiNqa.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiNqa), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL
                + MessageFormat.format(UrlAdapterConst.DELETE_POLICY_NQA, deviceId);

        LOGGER.info("deployNqa begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("status: " + response.getStatus() + " content: " + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNqa> restResult = JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNqa>>() {});

        LOGGER.info("deployNqa end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNqa> undeployNqa(List<SbiNqa> sbiNqa) throws ServiceException {

        String deviceId = sbiNqa.get(0).getDeviceId();
        String ctrlUuid = sbiNqa.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiNqa), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL
                + MessageFormat.format(UrlAdapterConst.DELETE_POLICY_NQA, deviceId);

        LOGGER.info("undeployNqa begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("status: " + response.getStatus() + " content: " + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNqa> restResult = JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNqa>>() {});

        LOGGER.info("undeployNqa end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNqa> updateNqa(List<SbiNqa> sbiNqa) throws ServiceException {

        String deviceId = sbiNqa.get(0).getDeviceId();
        String ctrlUuid = sbiNqa.get(0).getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(sbiNqa), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL
                + MessageFormat.format(UrlAdapterConst.DELETE_POLICY_NQA, deviceId);

        LOGGER.info("updateNqa begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("status: " + response.getStatus() + " content: " + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNqa> restResult = JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNqa>>() {});

        LOGGER.info("updateNqa end, result = " + restResult.toString());

        return restResult;
    }

    @Override
    public ResultRsp<SbiNqa> queryNqa(SbiNqa sbiNqa) throws ServiceException {

        String deviceId = sbiNqa.getDeviceId();
        String ctrlUuid = sbiNqa.getControllerId();
        RestfulParametes restfulParametes =
                RestfulParametesUtil.getRestfulParametesWithBody(JsonUtil.toJson(Arrays.asList(sbiNqa)), ctrlUuid);

        String url = UrlAdapterConst.ROUTE_ADAPTER_BASE_URL
                + MessageFormat.format(UrlAdapterConst.DELETE_POLICY_NQA, deviceId);

        LOGGER.info("queryNqa begin: " + url + "\n" + restfulParametes.getRawData());

        RestfulResponse response = RestfulProxy.post(url, restfulParametes);

        LOGGER.info("status: " + response.getStatus() + " content: " + response.getResponseContent());

        String rspContent = ResponseUtils.transferResponse(response);
        ResultRsp<SbiNqa> restResult = JsonUtil.fromJson(rspContent, new TypeReference<ResultRsp<SbiNqa>>() {});

        LOGGER.info("queryNqa end, result = " + restResult.toString());

        return restResult;
    }

}
