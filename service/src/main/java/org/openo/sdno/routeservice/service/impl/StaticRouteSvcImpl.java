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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiRouteNetModel;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.sbi.inf.StaticRouteSbiService;
import org.openo.sdno.routeservice.service.inf.StaticRouteService;
import org.openo.sdno.routeservice.util.db.NbiNeStaticRouteDbOper;
import org.openo.sdno.routeservice.util.db.SbiNeStaticRouteDbOper;
import org.openo.sdno.routeservice.util.operation.StaticRouteMergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static route service implementation. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class StaticRouteSvcImpl implements StaticRouteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticRouteSvcImpl.class);

    @Resource
    private StaticRouteSbiService sbiStaticRouteSvc;

    public void setSbiStaticRouteSvc(StaticRouteSbiService sbiStaticRouteSvc) {
        this.sbiStaticRouteSvc = sbiStaticRouteSvc;
    }

    @Override
    public ResultRsp<List<NbiNeStaticRoute>> batchQuery(HttpServletRequest req, HttpServletResponse resp,
            List<String> uuidList) throws ServiceException {
        return NbiNeStaticRouteDbOper.batchQuery(uuidList);
    }

    @Override
    public ResultRsp<NbiNeStaticRoute> query(HttpServletRequest req, HttpServletResponse resp, String routeId)
            throws ServiceException {

        return NbiNeStaticRouteDbOper.query(routeId);
    }

    @Override
    public ResultRsp<NbiNeStaticRoute> create(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNeStaticRoute> nbiRoutes, List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {

        Map<String, List<SbiNeStaticRoute>> sbiRoutesMap = new HashMap<String, List<SbiNeStaticRoute>>();

        devideSbiDataByCtrl(sbiRoutes, sbiRoutesMap);

        ResultRsp<SbiNeStaticRoute> resultRsp = new ResultRsp<SbiNeStaticRoute>();

        for(Entry<String, List<SbiNeStaticRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNeStaticRoute> rsp = sbiStaticRouteSvc.createRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Create routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
            }
        }

        return StaticRouteMergeUtil.mergeCreateResult(nbiRoutes, sbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<String> deploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException {

        UuidUtils.checkUuidList(nbiRouteIds);

        List<NbiNeStaticRoute> nbiRoutes = NbiNeStaticRouteDbOper.batchQuery(nbiRouteIds).getData();
        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.error("Nbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Nbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNeStaticRoute> sbiRoutes = SbiNeStaticRouteDbOper.querySbiByNbiModel(nbiRoutes).getData();
        if(CollectionUtils.isEmpty(sbiRoutes)) {
            LOGGER.error("Sbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Sbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNeStaticRoute> deployedRoutes = new ArrayList<SbiNeStaticRoute>();
        List<SbiNeStaticRoute> undeployRoutes = new ArrayList<SbiNeStaticRoute>();

        for(SbiNeStaticRoute tempRoute : sbiRoutes) {
            if("undeploy".equals(tempRoute.getDeployStatus())) {
                undeployRoutes.add(tempRoute);
            } else {
                deployedRoutes.add(tempRoute);
            }
        }

        NbiNeStaticRouteDbOper.updateNbiRoutes(nbiRoutes, deployedRoutes);

        Map<String, List<SbiNeStaticRoute>> sbiRoutesMap = new HashMap<String, List<SbiNeStaticRoute>>();

        devideSbiDataByCtrl(undeployRoutes, sbiRoutesMap);

        ResultRsp<SbiNeStaticRoute> resultRsp = new ResultRsp<SbiNeStaticRoute>();
        resultRsp.getSuccessed().addAll(deployedRoutes);

        for(Entry<String, List<SbiNeStaticRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNeStaticRoute> rsp = sbiStaticRouteSvc.deployRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Deploy routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            }
        }

        return StaticRouteMergeUtil.mergeDeployResult(nbiRoutes, sbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<String> undeploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException {

        UuidUtils.checkUuidList(nbiRouteIds);

        List<NbiNeStaticRoute> nbiRoutes = NbiNeStaticRouteDbOper.batchQuery(nbiRouteIds).getData();
        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.error("Nbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Nbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNeStaticRoute> sbiRoutes = SbiNeStaticRouteDbOper.querySbiByNbiModel(nbiRoutes).getData();
        if(CollectionUtils.isEmpty(sbiRoutes)) {
            LOGGER.error("Sbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
            throw new ServiceException("Sbi route not exists. nbiRouteIds: " + JsonUtil.toJson(nbiRouteIds));
        }

        List<SbiNeStaticRoute> deployedRoutes = new ArrayList<SbiNeStaticRoute>();
        List<SbiNeStaticRoute> undeployRoutes = new ArrayList<SbiNeStaticRoute>();

        for(SbiNeStaticRoute tempRoute : sbiRoutes) {
            if("undeploy".equals(tempRoute.getDeployStatus())) {
                undeployRoutes.add(tempRoute);
            } else {
                deployedRoutes.add(tempRoute);
            }
        }

        NbiNeStaticRouteDbOper.updateNbiRoutes(nbiRoutes, undeployRoutes);

        Map<String, List<SbiNeStaticRoute>> sbiRoutesMap = new HashMap<String, List<SbiNeStaticRoute>>();

        devideSbiDataByDevice(deployedRoutes, sbiRoutesMap);

        ResultRsp<SbiNeStaticRoute> resultRsp = new ResultRsp<SbiNeStaticRoute>();
        resultRsp.getSuccessed().addAll(undeployRoutes);

        for(Entry<String, List<SbiNeStaticRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNeStaticRoute> rsp = sbiStaticRouteSvc.undeployRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Deploy routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            }
        }

        return StaticRouteMergeUtil.mergeUndeployResult(nbiRoutes, sbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<NbiNeStaticRoute> update(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNeStaticRoute> nbiRoutes) throws ServiceException {

        List<NbiNeStaticRoute> nbiDbRoutes = NbiNeStaticRouteDbOper.queryNbiByModel(nbiRoutes).getData();
        if(CollectionUtils.isEmpty(nbiDbRoutes)) {
            LOGGER.error("Nbi route not exists. nbiRoutes: " + JsonUtil.toJson(nbiRoutes));
            throw new ServiceException("Nbi route not exists. nbiRoutes: " + JsonUtil.toJson(nbiRoutes));
        }

        List<SbiNeStaticRoute> sbiDbRoutes = SbiNeStaticRouteDbOper.querySbiByNbiModel(nbiDbRoutes).getData();
        if(CollectionUtils.isEmpty(sbiDbRoutes)) {
            LOGGER.error("Sbi route not exists. nbiRoutes: " + JsonUtil.toJson(nbiDbRoutes));
            throw new ServiceException("Sbi route not exists. nbiRoutes: " + JsonUtil.toJson(nbiDbRoutes));
        }

        updateSbiModelData(nbiDbRoutes, sbiDbRoutes);

        List<SbiNeStaticRoute> deployedRoutes = new ArrayList<SbiNeStaticRoute>();
        List<SbiNeStaticRoute> undeployRoutes = new ArrayList<SbiNeStaticRoute>();

        for(SbiNeStaticRoute tempRoute : sbiDbRoutes) {
            if("undeploy".equals(tempRoute.getDeployStatus())) {
                undeployRoutes.add(tempRoute);
            } else {
                deployedRoutes.add(tempRoute);
            }
        }

        SbiNeStaticRouteDbOper.updateSbiRoute(undeployRoutes);
        NbiNeStaticRouteDbOper.updateNbiBySbiModel(nbiRoutes, undeployRoutes);

        ResultRsp<SbiNeStaticRoute> resultRsp = new ResultRsp<SbiNeStaticRoute>();
        resultRsp.getSuccessed().addAll(undeployRoutes);

        Map<String, List<SbiNeStaticRoute>> sbiRoutesMap = new HashMap<String, List<SbiNeStaticRoute>>();

        devideSbiDataByCtrl(deployedRoutes, sbiRoutesMap);

        for(Entry<String, List<SbiNeStaticRoute>> tempEntry : sbiRoutesMap.entrySet()) {

            ResultRsp<SbiNeStaticRoute> rsp = sbiStaticRouteSvc.updateRoutes(tempEntry.getValue());
            if(rsp.isSuccess()) {
                resultRsp.getSuccessed().addAll(rsp.getSuccessed());
            } else {
                LOGGER.error("Create routes failed. the body is: " + JsonUtil.toJson(tempEntry.getValue()));
                resultRsp.getFail().addAll(rsp.getFail());
            }
        }

        return StaticRouteMergeUtil.mergeUpdateResult(nbiRoutes, resultRsp);
    }

    @Override
    public ResultRsp<String> delete(HttpServletRequest req, HttpServletResponse resp, String routeId)
            throws ServiceException {

        ResultRsp<String> resultRsp = new ResultRsp<String>();

        NbiNeStaticRoute nbiRoute = NbiNeStaticRouteDbOper.query(routeId).getData();
        if(null == nbiRoute) {
            resultRsp.getSuccessed().add(routeId);
            return resultRsp;
        }

        if(!"undeploy".equals(nbiRoute.getDeployStatus())) {
            LOGGER.error("Deploy status is not undeploy");
            throw new ServiceException("Deploy status is not undeploy");
        }

        List<SbiNeStaticRoute> sbiRoutes = SbiNeStaticRouteDbOper.query(routeId).getData();
        List<SbiRouteNetModel> netModels = new ArrayList<SbiRouteNetModel>();
        netModels.addAll(sbiRoutes);

        for(SbiRouteNetModel tempModel : netModels) {
            if(!"undeploy".equals(tempModel.getDeployStatus())) {
                LOGGER.error("Deploy status is not undeploy");
                throw new ServiceException("Deploy status is not undeploy");
            }
        }

        SbiNeStaticRouteDbOper.delete(sbiRoutes);
        NbiNeStaticRouteDbOper.delete(routeId);

        resultRsp.getSuccessed().add(routeId);
        return resultRsp;
    }

    private void devideSbiDataByCtrl(List<SbiNeStaticRoute> sbiRoutes,
            Map<String, List<SbiNeStaticRoute>> sbiRoutesMap) {
        // Sort routes by controller Id.
        for(SbiNeStaticRoute tempRoute : sbiRoutes) {

            String controllerId = tempRoute.getControllerId();
            if(null == sbiRoutesMap.get(controllerId)) {
                sbiRoutesMap.put(controllerId, new ArrayList<SbiNeStaticRoute>());
            }

            sbiRoutesMap.get(controllerId).add(tempRoute);
        }
    }

    private void devideSbiDataByDevice(List<SbiNeStaticRoute> sbiRoutes,
            Map<String, List<SbiNeStaticRoute>> sbiRoutesMap) {
        // Sort routes by device Id.
        for(SbiNeStaticRoute tempRoute : sbiRoutes) {

            String deviceId = tempRoute.getDeviceId();
            if(null == sbiRoutesMap.get(deviceId)) {
                sbiRoutesMap.put(deviceId, new ArrayList<SbiNeStaticRoute>());
            }

            sbiRoutesMap.get(deviceId).add(tempRoute);
        }
    }

    private void updateSbiModelData(List<NbiNeStaticRoute> nbiDbRoutes, List<SbiNeStaticRoute> sbiDbRoutes) {
        for(NbiNeStaticRoute tempNbiRoute : nbiDbRoutes) {
            for(SbiNeStaticRoute tempSbiRoute : sbiDbRoutes) {
                if(tempNbiRoute.getUuid().equals(tempSbiRoute.getNbiNeRouteId())) {
                    tempSbiRoute.setPriority(tempNbiRoute.getPriority());
                }
            }
        }
    }

}
