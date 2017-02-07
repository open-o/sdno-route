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

package org.openo.sdno.routeservice.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.framework.container.util.UuidUtils;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.sbi.inf.NqaSbiService;
import org.openo.sdno.routeservice.service.inf.NqaService;
import org.openo.sdno.routeservice.util.db.NbiNqaDbOper;
import org.openo.sdno.routeservice.util.db.SbiNqaDbOper;
import org.openo.sdno.routeservice.util.operation.NqaMergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NQA service implementation. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class NqaSvcImpl implements NqaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NqaSvcImpl.class);

    @Resource
    private NqaSbiService sbiNqaSvc;

    public void setSbiNqaSvc(NqaSbiService sbiNqasSvc) {
        this.sbiNqaSvc = sbiNqasSvc;
    }

    @Override
    public ResultRsp<List<NbiNqa>> batchQuery(HttpServletRequest req, HttpServletResponse resp, List<String> uuidList)
            throws ServiceException {
        return NbiNqaDbOper.batchQuery(uuidList);
    }

    @Override
    public ResultRsp<NbiNqa> query(HttpServletRequest req, HttpServletResponse resp, String nqaId)
            throws ServiceException {

        return NbiNqaDbOper.query(nqaId);
    }

    @Override
    public ResultRsp<NbiNqa> create(HttpServletRequest req, HttpServletResponse resp, List<NbiNqa> nbiNqas,
            List<SbiNqa> sbiNqas) throws ServiceException {

        Map<String, List<SbiNqa>> sbiNqasMap = new HashMap<String, List<SbiNqa>>();

        devideSbiDataByCtrl(sbiNqas, sbiNqasMap);

        ResultRsp<SbiNqa> resultRsp = new ResultRsp<SbiNqa>();

        for(Entry<String, List<SbiNqa>> tempEntry : sbiNqasMap.entrySet()) {

            ResultRsp<SbiNqa> rsp = sbiNqaSvc.createNqa(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Create NQA failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
            }
        }

        return NqaMergeUtil.mergeCreateResult(nbiNqas, sbiNqas, resultRsp);
    }

    @Override
    public ResultRsp<String> deploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiNqasIds)
            throws ServiceException {

        UuidUtils.checkUuidList(nbiNqasIds);

        List<NbiNqa> nbiNqas = NbiNqaDbOper.batchQuery(nbiNqasIds).getData();
        if(CollectionUtils.isEmpty(nbiNqas)) {
            LOGGER.error("Nbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
            throw new ServiceException("Nbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
        }

        List<SbiNqa> sbiNqas = SbiNqaDbOper.querySbiByNbiModel(nbiNqas).getData();
        if(CollectionUtils.isEmpty(sbiNqas)) {
            LOGGER.error("Sbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
            throw new ServiceException("Sbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
        }

        List<SbiNqa> deployedNqa = new ArrayList<SbiNqa>();
        List<SbiNqa> undeployNqa = new ArrayList<SbiNqa>();

        for(SbiNqa tempNqa : sbiNqas) {
            if("undeploy".equals(tempNqa.getDeployStatus())) {
                undeployNqa.add(tempNqa);
            } else {
                deployedNqa.add(tempNqa);
            }
        }

        NbiNqaDbOper.updateNbiNqa(nbiNqas, deployedNqa);

        Map<String, List<SbiNqa>> sbiNqasMap = new HashMap<String, List<SbiNqa>>();

        devideSbiDataByCtrl(undeployNqa, sbiNqasMap);

        ResultRsp<SbiNqa> resultRsp = new ResultRsp<SbiNqa>();
        resultRsp.getSuccessed().addAll(deployedNqa);

        for(Entry<String, List<SbiNqa>> tempEntry : sbiNqasMap.entrySet()) {

            ResultRsp<SbiNqa> rsp = sbiNqaSvc.deployNqa(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Deploy NQA failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            }
        }

        return NqaMergeUtil.mergeDeployResult(nbiNqas, sbiNqas, resultRsp);
    }

    @Override
    public ResultRsp<String> undeploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiNqasIds)
            throws ServiceException {

        UuidUtils.checkUuidList(nbiNqasIds);

        List<NbiNqa> nbiNqas = NbiNqaDbOper.batchQuery(nbiNqasIds).getData();
        if(CollectionUtils.isEmpty(nbiNqas)) {
            LOGGER.error("Nbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
            throw new ServiceException("Nbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
        }

        List<SbiNqa> sbiNqas = SbiNqaDbOper.querySbiByNbiModel(nbiNqas).getData();
        if(CollectionUtils.isEmpty(sbiNqas)) {
            LOGGER.error("Sbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
            throw new ServiceException("Sbi NQA not exists. nbiNqasIds: " + JsonUtil.toJson(nbiNqasIds));
        }

        List<SbiNqa> deployedNqa = new ArrayList<SbiNqa>();
        List<SbiNqa> undeployNqa = new ArrayList<SbiNqa>();

        for(SbiNqa tempNqa : sbiNqas) {
            if("undeploy".equals(tempNqa.getDeployStatus())) {
                undeployNqa.add(tempNqa);
            } else {
                deployedNqa.add(tempNqa);
            }
        }

        NbiNqaDbOper.updateNbiNqa(nbiNqas, undeployNqa);

        Map<String, List<SbiNqa>> sbiNqasMap = new HashMap<String, List<SbiNqa>>();

        devideSbiDataByDevice(deployedNqa, sbiNqasMap);

        ResultRsp<SbiNqa> resultRsp = new ResultRsp<SbiNqa>();
        resultRsp.getSuccessed().addAll(undeployNqa);

        for(Entry<String, List<SbiNqa>> tempEntry : sbiNqasMap.entrySet()) {

            ResultRsp<SbiNqa> rsp = sbiNqaSvc.undeployNqa(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Deploy NQA failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            }
        }

        return NqaMergeUtil.mergeUndeployResult(nbiNqas, sbiNqas, resultRsp);
    }

    @Override
    public ResultRsp<NbiNqa> update(HttpServletRequest req, HttpServletResponse resp, List<NbiNqa> nbiNqas)
            throws ServiceException {

        List<NbiNqa> nbiDbNqa = NbiNqaDbOper.queryNbiByModel(nbiNqas).getData();
        if(CollectionUtils.isEmpty(nbiDbNqa)) {
            LOGGER.error("Nbi NQA not exists. nbiNqas: " + JsonUtil.toJson(nbiNqas));
            throw new ServiceException("Nbi NQA not exists. nbiNqas: " + JsonUtil.toJson(nbiNqas));
        }

        List<SbiNqa> sbiDbNqa = SbiNqaDbOper.querySbiByNbiModel(nbiDbNqa).getData();
        if(CollectionUtils.isEmpty(sbiDbNqa)) {
            LOGGER.error("Sbi NQA not exists. nbiNqas: " + JsonUtil.toJson(nbiDbNqa));
            throw new ServiceException("Sbi NQA not exists. nbiNqas: " + JsonUtil.toJson(nbiDbNqa));
        }

        updateSbiModelData(nbiDbNqa, sbiDbNqa);

        List<SbiNqa> deployedNqa = new ArrayList<SbiNqa>();
        List<SbiNqa> undeployNqa = new ArrayList<SbiNqa>();

        for(SbiNqa tempNqa : sbiDbNqa) {
            if("undeploy".equals(tempNqa.getDeployStatus())) {
                undeployNqa.add(tempNqa);
            } else {
                deployedNqa.add(tempNqa);
            }
        }

        SbiNqaDbOper.updateSbiNqa(undeployNqa);
        NbiNqaDbOper.updateNbiBySbiModel(nbiNqas, undeployNqa);

        ResultRsp<SbiNqa> resultRsp = new ResultRsp<SbiNqa>();
        resultRsp.getSuccessed().addAll(undeployNqa);

        Map<String, List<SbiNqa>> sbiNqasMap = new HashMap<String, List<SbiNqa>>();

        devideSbiDataByCtrl(deployedNqa, sbiNqasMap);

        for(Entry<String, List<SbiNqa>> tempEntry : sbiNqasMap.entrySet()) {

            ResultRsp<SbiNqa> rsp = sbiNqaSvc.updateNqa(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Create NQA failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
            }
        }

        return NqaMergeUtil.mergeUpdateResult(nbiNqas, resultRsp);
    }

    @Override
    public ResultRsp<String> delete(HttpServletRequest req, HttpServletResponse resp, String nqaId)
            throws ServiceException {

        ResultRsp<String> resultRsp = new ResultRsp<String>();

        NbiNqa nbiNqas = NbiNqaDbOper.query(nqaId).getData();
        if(null == nbiNqas) {
            resultRsp.getSuccessed().add(nqaId);
            return resultRsp;
        }

        if(!"undeploy".equals(nbiNqas.getDeployStatus())) {
            LOGGER.error("Deploy status is not undeploy");
            throw new ServiceException("Deploy status is not undeploy");
        }

        List<SbiNqa> sbiNqas = SbiNqaDbOper.query(nqaId).getData();
        List<SbiNqa> nqaList = new ArrayList<SbiNqa>();
        nqaList.addAll(sbiNqas);

        for(SbiNqa tempModel : nqaList) {
            if(!"undeploy".equals(tempModel.getDeployStatus())) {
                LOGGER.error("Deploy status is not undeploy");
                throw new ServiceException("Deploy status is not undeploy");
            }
        }

        SbiNqaDbOper.delete(sbiNqas);
        NbiNqaDbOper.delete(nqaId);

        resultRsp.getSuccessed().add(nqaId);
        return resultRsp;
    }

    private void devideSbiDataByCtrl(List<SbiNqa> sbiNqas, Map<String, List<SbiNqa>> sbiNqasMap) {
        // Sort NQA by controller Id.
        for(SbiNqa tempNqa : sbiNqas) {

            String controllerId = tempNqa.getControllerId();
            if(null == sbiNqasMap.get(controllerId)) {
                sbiNqasMap.put(controllerId, new ArrayList<SbiNqa>());
            }

            sbiNqasMap.get(controllerId).add(tempNqa);
        }
    }

    private void devideSbiDataByDevice(List<SbiNqa> sbiNqas, Map<String, List<SbiNqa>> sbiNqasMap) {
        // Sort NQA by device Id.
        for(SbiNqa tempNqa : sbiNqas) {

            String deviceId = tempNqa.getDeviceId();
            if(null == sbiNqasMap.get(deviceId)) {
                sbiNqasMap.put(deviceId, new ArrayList<SbiNqa>());
            }

            sbiNqasMap.get(deviceId).add(tempNqa);
        }
    }

    private void updateSbiModelData(List<NbiNqa> nbiDbNqas, List<SbiNqa> sbiDbNqas) {
        for(NbiNqa tempNbiNqa : nbiDbNqas) {
            for(SbiNqa tempSbiNqa : sbiDbNqas) {
                if(tempNbiNqa.getUuid().equals(tempSbiNqa.getNbiNeRouteId())) {
                    tempSbiNqa.setTtl(tempNbiNqa.getTtl());
                    tempSbiNqa.setName(tempNbiNqa.getName());
                    tempSbiNqa.setTos(tempNbiNqa.getTos());
                }
            }
        }
    }

}
