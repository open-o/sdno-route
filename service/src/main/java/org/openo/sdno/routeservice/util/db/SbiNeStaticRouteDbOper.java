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

package org.openo.sdno.routeservice.util.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.inventory.sdk.util.InventoryDaoUtil;
import org.openo.sdno.overlayvpn.model.common.enums.ActionStatus;
import org.openo.sdno.overlayvpn.model.common.enums.AdminStatus;
import org.openo.sdno.overlayvpn.model.common.enums.DeployStatus;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.result.FailData;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * It is used to operate SbiNeStaticRoute table. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class SbiNeStaticRouteDbOper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbiNeStaticRouteDbOper.class);

    private static final String UUID = "uuid";

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    private SbiNeStaticRouteDbOper() {

    }

    /**
     * It is used to insert SbiNeStaticRoute data. <br>
     * 
     * @param sbiRoutes The list of SbiNeStaticRoute data
     * @throws ServiceException When insert failed.
     * @since SDNO 0.5
     */
    public static void insert(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {

        new InventoryDaoUtil<SbiNeStaticRoute>().getInventoryDao().batchInsert(sbiRoutes);
    }

    /**
     * It is used to update status. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @param updateFieldList The field that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void update(List<SbiNeStaticRoute> sbiRoutes, String updateFieldList) throws ServiceException {
        new InventoryDaoUtil<SbiNeStaticRoute>().getInventoryDao().update(SbiNeStaticRoute.class, sbiRoutes,
                updateFieldList);
    }

    /**
     * It is used to query SbiNeStaticRoute data. <br>
     * 
     * @param routeId The route id
     * @return The object list of SbiNeStaticRoute
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    public static ResultRsp<List<SbiNeStaticRoute>> query(String routeId) throws ServiceException {
        return queryByFilter(routeId, null);
    }

    /**
     * It is used to batch query SbiNeStaticRoute data. <br>
     * 
     * @param nbiRoutes The object list of NbiNeStaticRoute
     * @return The object list of SbiNeStaticRoute
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    @SuppressWarnings("unchecked")
    public static ResultRsp<List<SbiNeStaticRoute>> querySbiByNbiModel(List<NbiNeStaticRoute> nbiRoutes)
            throws ServiceException {

        List<String> nbiRouteIds =
                new ArrayList<>(CollectionUtils.collect(nbiRoutes, arg0 -> ((NbiNeStaticRoute)arg0).getUuid()));

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("nbiNeRouteId", nbiRouteIds);

        String filter = JsonUtil.toJson(filterMap);
        LOGGER.info("filter json: " + filter);

        return new InventoryDaoUtil<SbiNeStaticRoute>().getInventoryDao().queryByFilter(SbiNeStaticRoute.class, filter,
                null);
    }

    /**
     * It is used to delete SbiNeStaticRoute data. <br>
     * 
     * @param routeId The route id
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(String routeId) throws ServiceException {
        ResultRsp<List<SbiNeStaticRoute>> queryDbRsp = queryByFilter(routeId, UUID);
        if(CollectionUtils.isEmpty(queryDbRsp.getData())) {
            LOGGER.warn("delete error, connectionId (" + routeId + ") is not found");
            return;
        }

        SbiNeStaticRoute sbiRoute = queryDbRsp.getData().get(0);
        new InventoryDaoUtil<SbiNeStaticRoute>().getInventoryDao().delete(SbiNeStaticRoute.class, sbiRoute.getUuid());
    }

    /**
     * It is used to batch delete SbiNeStaticRoute data. <br>
     * 
     * @param sbiRoutes The objects that want to be deleted
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {
        List<String> uuidList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(sbiRoutes)) {
            for(SbiNeStaticRoute route : sbiRoutes) {
                uuidList.add(route.getUuid());
            }
        }

        new InventoryDaoUtil<SbiNeStaticRoute>().getInventoryDao().batchDelete(SbiNeStaticRoute.class, uuidList);
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateSbiRoute(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException {
        new InventoryDaoUtil<SbiNeStaticRoute>().getInventoryDao().update(SbiNeStaticRoute.class, sbiRoutes,
                "priority");
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @param succRoutes The data that deploy successfully
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateDeploySuccess(List<SbiNeStaticRoute> sbiRoutes, List<SbiNeStaticRoute> succRoutes)
            throws ServiceException {

        if(CollectionUtils.isEmpty(succRoutes)) {
            return;
        }

        List<SbiNeStaticRoute> updateList = new ArrayList<>();
        for(SbiNeStaticRoute tempRoute : sbiRoutes) {
            for(SbiNeStaticRoute tempSuccRoute : succRoutes) {
                if(tempRoute.getUuid().equals(tempSuccRoute.getUuid())) {
                    tempRoute.setDeployStatus(DeployStatus.DEPLOY.getName());
                    tempRoute.setOperationStatus(ActionStatus.NORMAL.getName());
                    tempRoute.setActiveStatus(AdminStatus.ACTIVE.getName());
                    tempRoute.setExternalId(tempSuccRoute.getExternalId());
                    updateList.add(tempRoute);
                }
            }
        }

        if(!CollectionUtils.isEmpty(updateList)) {
            update(updateList, "deployStatus,operationStatus,activeStatus,externalId");
        }
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @param failRoutes The data that deploy failed
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateDeployFailed(List<SbiNeStaticRoute> sbiRoutes, List<FailData<SbiNeStaticRoute>> failRoutes)
            throws ServiceException {

        if(CollectionUtils.isEmpty(failRoutes)) {
            return;
        }

        List<SbiNeStaticRoute> updateList = new ArrayList<>();
        for(SbiNeStaticRoute tempRoute : sbiRoutes) {
            for(FailData<SbiNeStaticRoute> tempFailRoute : failRoutes) {
                if(tempRoute.getUuid().equals(tempFailRoute.getData().getUuid())) {
                    tempRoute.setDeployStatus(DeployStatus.UNDEPLOY.getName());
                    tempRoute.setOperationStatus(ActionStatus.CREATE_EXCEPTION.getName());
                    updateList.add(tempRoute);
                    break;
                }
            }
        }

        if(!CollectionUtils.isEmpty(updateList)) {
            update(updateList, "deployStatus,operationStatus");
        }
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @param succRoutes The data that undeploy successfully
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateUndeploySuccess(List<SbiNeStaticRoute> sbiRoutes, List<SbiNeStaticRoute> succRoutes)
            throws ServiceException {

        if(CollectionUtils.isEmpty(succRoutes)) {
            return;
        }

        List<SbiNeStaticRoute> updateList = new ArrayList<>();
        for(SbiNeStaticRoute tempRoute : sbiRoutes) {
            for(SbiNeStaticRoute tempSuccRoute : succRoutes) {
                if(tempRoute.getUuid().equals(tempSuccRoute.getUuid())) {
                    tempRoute.setDeployStatus(DeployStatus.UNDEPLOY.getName());
                    tempRoute.setOperationStatus(ActionStatus.NORMAL.getName());
                    updateList.add(tempRoute);
                    break;
                }
            }
        }

        if(!CollectionUtils.isEmpty(updateList)) {
            update(updateList, "deployStatus,operationStatus");
        }
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @param failRoutes The data that undeploy failed
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateUndeployFailed(List<SbiNeStaticRoute> sbiRoutes,
            List<FailData<SbiNeStaticRoute>> failRoutes) throws ServiceException {

        if(CollectionUtils.isEmpty(failRoutes)) {
            return;
        }

        List<SbiNeStaticRoute> updateList = new ArrayList<>();
        for(SbiNeStaticRoute tempRoute : sbiRoutes) {
            for(FailData<SbiNeStaticRoute> tempFailRoute : failRoutes) {
                if(tempRoute.getUuid().equals(tempFailRoute.getData().getUuid())) {
                    tempRoute.setOperationStatus(ActionStatus.DELETE_EXCEPTION.getName());
                    updateList.add(tempRoute);
                }
            }
        }

        if(!CollectionUtils.isEmpty(updateList)) {
            update(updateList, "operationStatus");
        }
    }

    private static ResultRsp<List<SbiNeStaticRoute>> queryByFilter(String connectionId, String queryResultFields)
            throws ServiceException {
        Map<String, Object> filterMap = new HashMap<>();
        if(StringUtils.hasLength(connectionId)) {
            filterMap.put("nbiNeRouteId", Arrays.asList(connectionId));
        }

        String filter = JSONObject.fromObject(filterMap).toString();

        return new InventoryDaoUtil<SbiNeStaticRoute>().getInventoryDao().queryByFilter(SbiNeStaticRoute.class, filter,
                queryResultFields);
    }

}
