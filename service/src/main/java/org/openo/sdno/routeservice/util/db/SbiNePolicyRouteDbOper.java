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
import org.apache.commons.collections.Transformer;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.inventory.sdk.util.InventoryDaoUtil;
import org.openo.sdno.overlayvpn.model.common.enums.ActionStatus;
import org.openo.sdno.overlayvpn.model.common.enums.AdminStatus;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNePolicyRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNePolicyRoute;
import org.openo.sdno.overlayvpn.result.FailData;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * It is used to operate SbiNePolicyRoute table. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class SbiNePolicyRouteDbOper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbiNePolicyRouteDbOper.class);

    private static final String UUID = "uuid";

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    private SbiNePolicyRouteDbOper() {

    }

    /**
     * It is used to insert SbiNePolicyRoute data. <br>
     * 
     * @param sbiRoutes The list of SbiNePolicyRoute data
     * @throws ServiceException When insert failed.
     * @since SDNO 0.5
     */
    public static void insert(List<SbiNePolicyRoute> sbiRoutes) throws ServiceException {

        new InventoryDaoUtil<SbiNePolicyRoute>().getInventoryDao().batchInsert(sbiRoutes);
    }

    /**
     * It is used to update status. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @param updateFieldList The field that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void update(List<SbiNePolicyRoute> sbiRoutes, String updateFieldList) throws ServiceException {
        new InventoryDaoUtil<SbiNePolicyRoute>().getInventoryDao().update(SbiNePolicyRoute.class, sbiRoutes,
                updateFieldList);
    }

    /**
     * It is used to query SbiNePolicyRoute data. <br>
     * 
     * @param routeId The route id
     * @return The object list of SbiNePolicyRoute
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    public static ResultRsp<List<SbiNePolicyRoute>> query(String routeId) throws ServiceException {
        return queryByFilter(routeId, null);
    }

    /**
     * It is used to batch query SbiNePolicyRoute data. <br>
     * 
     * @param nbiRoutes The object list of NbiNePolicyRoute
     * @return The object list of SbiNePolicyRoute
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    @SuppressWarnings("unchecked")
    public static ResultRsp<List<SbiNePolicyRoute>> querySbiByNbiModel(List<NbiNePolicyRoute> nbiRoutes)
            throws ServiceException {

        List<String> nbiRouteIds = new ArrayList<String>(CollectionUtils.collect(nbiRoutes, new Transformer() {

            @Override
            public Object transform(Object arg0) {
                return ((NbiNePolicyRoute)arg0).getUuid();
            }
        }));

        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("nbiNeRouteId", nbiRouteIds);

        String filter = JsonUtil.toJson(filterMap);
        LOGGER.info("filter json: " + filter);

        return new InventoryDaoUtil<SbiNePolicyRoute>().getInventoryDao().queryByFilter(SbiNePolicyRoute.class, filter,
                null);
    }

    /**
     * It is used to delete SbiNePolicyRoute data. <br>
     * 
     * @param routeId The route id
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(String routeId) throws ServiceException {
        ResultRsp<List<SbiNePolicyRoute>> queryDbRsp = queryByFilter(routeId, UUID);
        if(CollectionUtils.isEmpty(queryDbRsp.getData())) {
            LOGGER.warn("delete error, connectionId (" + routeId + ") is not found");
            return;
        }

        SbiNePolicyRoute SbiNePolicyRoute = queryDbRsp.getData().get(0);
        new InventoryDaoUtil<SbiNePolicyRoute>().getInventoryDao().delete(SbiNePolicyRoute.class,
                SbiNePolicyRoute.getUuid());
    }

    /**
     * It is used to batch delete SbiNePolicyRoute data. <br>
     * 
     * @param sbiRoutes The objects that want to be deleted
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(List<SbiNePolicyRoute> sbiRoutes) throws ServiceException {
        List<String> uuidList = new ArrayList<String>();

        if(!CollectionUtils.isEmpty(sbiRoutes)) {
            for(SbiNePolicyRoute route : sbiRoutes) {
                uuidList.add(route.getUuid());
            }
        }

        new InventoryDaoUtil<SbiNePolicyRoute>().getInventoryDao().batchDelete(SbiNePolicyRoute.class, uuidList);
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateSbiRoute(List<SbiNePolicyRoute> sbiRoutes) throws ServiceException {
        new InventoryDaoUtil<SbiNePolicyRoute>().getInventoryDao().update(SbiNePolicyRoute.class, sbiRoutes,
                "filterAction");
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiRoutes The data that want to be updated
     * @param succRoutes The data that deploy successfully
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateDeploySuccess(List<SbiNePolicyRoute> sbiRoutes, List<SbiNePolicyRoute> succRoutes)
            throws ServiceException {

        if(CollectionUtils.isEmpty(succRoutes)) {
            return;
        }

        List<SbiNePolicyRoute> updateList = new ArrayList<SbiNePolicyRoute>();
        for(SbiNePolicyRoute tempRoute : sbiRoutes) {
            for(SbiNePolicyRoute tempSuccRoute : succRoutes) {
                if(tempRoute.getUuid().equals(tempSuccRoute.getUuid())) {
                    tempRoute.setDeployStatus("deploy");
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
    public static void updateDeployFailed(List<SbiNePolicyRoute> sbiRoutes, List<FailData<SbiNePolicyRoute>> failRoutes)
            throws ServiceException {

        if(CollectionUtils.isEmpty(failRoutes)) {
            return;
        }

        List<SbiNePolicyRoute> updateList = new ArrayList<SbiNePolicyRoute>();
        for(SbiNePolicyRoute tempRoute : sbiRoutes) {
            for(FailData<SbiNePolicyRoute> tempFailRoute : failRoutes) {
                if(tempRoute.getUuid().equals(tempFailRoute.getData().getUuid())) {
                    tempRoute.setDeployStatus("undeploy");
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
    public static void updateUndeploySuccess(List<SbiNePolicyRoute> sbiRoutes, List<SbiNePolicyRoute> succRoutes)
            throws ServiceException {

        if(CollectionUtils.isEmpty(succRoutes)) {
            return;
        }

        List<SbiNePolicyRoute> updateList = new ArrayList<SbiNePolicyRoute>();
        for(SbiNePolicyRoute tempRoute : sbiRoutes) {
            for(SbiNePolicyRoute tempSuccRoute : succRoutes) {
                if(tempRoute.getUuid().equals(tempSuccRoute.getUuid())) {
                    tempRoute.setDeployStatus("undeploy");
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
    public static void updateUndeployFailed(List<SbiNePolicyRoute> sbiRoutes,
            List<FailData<SbiNePolicyRoute>> failRoutes) throws ServiceException {

        if(CollectionUtils.isEmpty(failRoutes)) {
            return;
        }

        List<SbiNePolicyRoute> updateList = new ArrayList<SbiNePolicyRoute>();
        for(SbiNePolicyRoute tempRoute : sbiRoutes) {
            for(FailData<SbiNePolicyRoute> tempFailRoute : failRoutes) {
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

    private static ResultRsp<List<SbiNePolicyRoute>> queryByFilter(String connectionId, String queryResultFields)
            throws ServiceException {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        if(StringUtils.hasLength(connectionId)) {
            filterMap.put("nbiNeRouteId", Arrays.asList(connectionId));
        }

        String filter = JSONObject.fromObject(filterMap).toString();

        return new InventoryDaoUtil<SbiNePolicyRoute>().getInventoryDao().queryByFilter(SbiNePolicyRoute.class, filter,
                queryResultFields);
    }

}
