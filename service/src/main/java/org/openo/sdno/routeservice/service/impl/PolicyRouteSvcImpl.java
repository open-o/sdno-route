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
import org.openo.sdno.overlayvpn.model.common.enums.DeployStatus;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNePolicyRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNePolicyRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiRouteNetModel;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.sbi.inf.PolicyRouteSbiService;
import org.openo.sdno.routeservice.service.inf.PolicyRouteService;
import org.openo.sdno.routeservice.util.db.NbiNePolicyRouteDbOper;
import org.openo.sdno.routeservice.util.db.SbiNePolicyRouteDbOper;
import org.openo.sdno.routeservice.util.operation.PolicyRouteMergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Policy route service implementation. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class PolicyRouteSvcImpl implements PolicyRouteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyRouteSvcImpl.class);

    @Resource
    private PolicyRouteSbiService sbiPolicyRouteSvc;

    public void setSbiPolicyRouteSvc(PolicyRouteSbiService sbiPolicyRouteSvc) {
        this.sbiPolicyRouteSvc = sbiPolicyRouteSvc;
    }

    @Override
    public ResultRsp<List<NbiNePolicyRoute>> batchQuery(HttpServletRequest req, HttpServletResponse resp,
            List<String> uuidList) throws ServiceException {
        return NbiNePolicyRouteDbOper.batchQuery(uuidList);
    }

    @Override
    public ResultRsp<NbiNePolicyRoute> query(HttpServletRequest req, HttpServletResponse resp, String routeId)
            throws ServiceException {

        return NbiNePolicyRouteDbOper.query(routeId);
    }

    @Override
    public ResultRsp<NbiNePolicyRoute> create(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNePolicyRoute> nbiRoutes, List<SbiNePolicyRoute> sbiRoutes) throws ServiceException {

        Map<String, List<SbiNePolicyRoute>> sbiRoutesMap = new HashMap<>();

        devideSbiDataByCtrl(sbiRoutes, sbiRoutesMap);

        ResultRsp<SbiNePolicyRoute> resultRsp = new ResultRsp<>();

        for(Entry<String, List<SbiNePolicyRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNePolicyRoute> rsp = sbiPolicyRouteSvc.createRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Create routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
            }
        }

        return PolicyRouteMergeUtil.mergeCreateResult(nbiRoutes, sbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<String> deploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException {

        UuidUtils.checkUuidList(nbiRouteIds);

        List<NbiNePolicyRoute> nbiRoutes = NbiNePolicyRouteDbOper.batchQuery(nbiRouteIds).getData();
        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.error("Deploy NbiRoute not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Deploy Nbi Route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNePolicyRoute> sbiRoutes = SbiNePolicyRouteDbOper.querySbiByNbiModel(nbiRoutes).getData();
        if(CollectionUtils.isEmpty(sbiRoutes)) {
            LOGGER.error("Deploy SbiRoute not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Deploy Sbi Route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNePolicyRoute> deployedRoutes = new ArrayList<>();
        List<SbiNePolicyRoute> undeployRoutes = new ArrayList<>();

        for(SbiNePolicyRoute tempRoute : sbiRoutes) {
            if(DeployStatus.UNDEPLOY.getName().equals(tempRoute.getDeployStatus())) {
                undeployRoutes.add(tempRoute);
            } else {
                deployedRoutes.add(tempRoute);
            }
        }

        NbiNePolicyRouteDbOper.updateNbiRoutes(nbiRoutes, deployedRoutes);

        Map<String, List<SbiNePolicyRoute>> sbiRoutesMap = new HashMap<>();

        devideSbiDataByCtrl(undeployRoutes, sbiRoutesMap);

        ResultRsp<SbiNePolicyRoute> resultRsp = new ResultRsp<>();
        resultRsp.getSuccessed().addAll(deployedRoutes);

        for(Entry<String, List<SbiNePolicyRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNePolicyRoute> rsp = sbiPolicyRouteSvc.deployRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Deploy routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            }
        }

        return PolicyRouteMergeUtil.mergeDeployResult(nbiRoutes, sbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<String> undeploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException {

        UuidUtils.checkUuidList(nbiRouteIds);

        List<NbiNePolicyRoute> nbiRoutes = NbiNePolicyRouteDbOper.batchQuery(nbiRouteIds).getData();
        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.error("Undeploy NbiRoute not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Undeploy Nbi Route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNePolicyRoute> sbiRoutes = SbiNePolicyRouteDbOper.querySbiByNbiModel(nbiRoutes).getData();
        if(CollectionUtils.isEmpty(sbiRoutes)) {
            LOGGER.error("Undeploy SbiRoute not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Undeploy Sbi Route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNePolicyRoute> deployedRoutes = new ArrayList<>();
        List<SbiNePolicyRoute> undeployRoutes = new ArrayList<>();

        for(SbiNePolicyRoute tempRoute : sbiRoutes) {
            if(DeployStatus.UNDEPLOY.getName().equals(tempRoute.getDeployStatus())) {
                undeployRoutes.add(tempRoute);
            } else {
                deployedRoutes.add(tempRoute);
            }
        }

        NbiNePolicyRouteDbOper.updateNbiRoutes(nbiRoutes, undeployRoutes);

        Map<String, List<SbiNePolicyRoute>> sbiRoutesMap = new HashMap<>();

        devideSbiDataByDevice(deployedRoutes, sbiRoutesMap);

        ResultRsp<SbiNePolicyRoute> resultRsp = new ResultRsp<>();
        resultRsp.getSuccessed().addAll(undeployRoutes);

        for(Entry<String, List<SbiNePolicyRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNePolicyRoute> rsp = sbiPolicyRouteSvc.undeployRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Undeploy routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            }
        }

        return PolicyRouteMergeUtil.mergeUndeployResult(nbiRoutes, sbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<NbiNePolicyRoute> update(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNePolicyRoute> nbiRoutes) throws ServiceException {

        List<NbiNePolicyRoute> nbiDbRoutes = NbiNePolicyRouteDbOper.queryNbiByModel(nbiRoutes).getData();
        if(CollectionUtils.isEmpty(nbiDbRoutes)) {
            LOGGER.error("Update NbiRoute not exists. nbiRoutes: " + JsonUtil.toJson(nbiRoutes));
            throw new ServiceException("Update Nbi Route not exists. nbiRoutes: " + JsonUtil.toJson(nbiRoutes));
        }

        List<SbiNePolicyRoute> sbiDbRoutes = SbiNePolicyRouteDbOper.querySbiByNbiModel(nbiDbRoutes).getData();
        if(CollectionUtils.isEmpty(sbiDbRoutes)) {
            LOGGER.error("Update SbiRoute not exists. nbiRoutes: " + JsonUtil.toJson(nbiDbRoutes));
            throw new ServiceException("Update Sbi Route not exists. nbiRoutes: " + JsonUtil.toJson(nbiDbRoutes));
        }

        updateSbiModelData(nbiRoutes, sbiDbRoutes);

        List<SbiNePolicyRoute> deployedRoutes = new ArrayList<>();
        List<SbiNePolicyRoute> undeployRoutes = new ArrayList<>();

        for(SbiNePolicyRoute tempRoute : sbiDbRoutes) {
            if(DeployStatus.UNDEPLOY.getName().equals(tempRoute.getDeployStatus())) {
                undeployRoutes.add(tempRoute);
            } else {
                deployedRoutes.add(tempRoute);
            }
        }

        SbiNePolicyRouteDbOper.updateSbiRoute(undeployRoutes);
        NbiNePolicyRouteDbOper.updateNbiBySbiModel(nbiRoutes, undeployRoutes);

        ResultRsp<SbiNePolicyRoute> resultRsp = new ResultRsp<>();
        resultRsp.getSuccessed().addAll(undeployRoutes);

        Map<String, List<SbiNePolicyRoute>> sbiRoutesMap = new HashMap<>();

        devideSbiDataByCtrl(deployedRoutes, sbiRoutesMap);

        for(Entry<String, List<SbiNePolicyRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNePolicyRoute> rsp = sbiPolicyRouteSvc.updateRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Update routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
            }
        }

        return PolicyRouteMergeUtil.mergeUpdateResult(nbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<String> delete(HttpServletRequest req, HttpServletResponse resp, String routeId)
            throws ServiceException {

        ResultRsp<String> resultRsp = new ResultRsp<>();

        NbiNePolicyRoute nbiRoute = NbiNePolicyRouteDbOper.query(routeId).getData();
        if(null == nbiRoute) {
            resultRsp.getSuccessed().add(routeId);
            return resultRsp;
        }

        if(!DeployStatus.UNDEPLOY.getName().equals(nbiRoute.getDeployStatus())) {
            LOGGER.error("NbiRoute deploystatus is not undeploy");
            throw new ServiceException("Nbi Route deploystatus is not undeploy");
        }

        List<SbiNePolicyRoute> sbiRoutes = SbiNePolicyRouteDbOper.query(routeId).getData();
        List<SbiRouteNetModel> netModels = new ArrayList<>();
        netModels.addAll(sbiRoutes);

        for(SbiRouteNetModel tempModel : netModels) {
            if(!DeployStatus.UNDEPLOY.getName().equals(tempModel.getDeployStatus())) {
                LOGGER.error("SbiRoute deploystatus is not undeploy");
                throw new ServiceException("Sbi Route deploystatus is not undeploy");
            }
        }

        SbiNePolicyRouteDbOper.delete(sbiRoutes);
        NbiNePolicyRouteDbOper.delete(routeId);

        resultRsp.getSuccessed().add(routeId);
        return resultRsp;
    }

    private void devideSbiDataByCtrl(List<SbiNePolicyRoute> sbiRoutes,
            Map<String, List<SbiNePolicyRoute>> sbiRoutesMap) {
        // Sort routes by controller Id.
        for(SbiNePolicyRoute tempRoute : sbiRoutes) {

            String controllerId = tempRoute.getControllerId();
            if(null == sbiRoutesMap.get(controllerId)) {
                sbiRoutesMap.put(controllerId, new ArrayList<>());
            }

            sbiRoutesMap.get(controllerId).add(tempRoute);
        }
    }

    private void devideSbiDataByDevice(List<SbiNePolicyRoute> sbiRoutes,
            Map<String, List<SbiNePolicyRoute>> sbiRoutesMap) {
        // Sort routes by device Id.
        for(SbiNePolicyRoute tempRoute : sbiRoutes) {

            String deviceId = tempRoute.getDeviceId();
            if(null == sbiRoutesMap.get(deviceId)) {
                sbiRoutesMap.put(deviceId, new ArrayList<>());
            }

            sbiRoutesMap.get(deviceId).add(tempRoute);
        }
    }

    private void updateSbiModelData(List<NbiNePolicyRoute> nbiDbRoutes, List<SbiNePolicyRoute> sbiDbRoutes) {
        for(NbiNePolicyRoute tempNbiRoute : nbiDbRoutes) {
            for(SbiNePolicyRoute tempSbiRoute : sbiDbRoutes) {
                if(tempNbiRoute.getUuid().equals(tempSbiRoute.getNbiNeRouteId())) {
                    tempSbiRoute.setFilterAction(tempNbiRoute.getFilterAction());
                }
            }
        }
    }

}
