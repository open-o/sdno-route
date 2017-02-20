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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.inventory.sdk.util.InventoryDaoUtil;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiRouteNetModel;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It is used to operate NbiNeStaticRoute table. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class NbiNeStaticRouteDbOper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NbiNeStaticRouteDbOper.class);

    private static final String UUID = "uuid";

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    private NbiNeStaticRouteDbOper() {

    }

    /**
     * It is used to insert NbiNeStaticRoute data. <br>
     * 
     * @param nbiRoutes The list of NbiNeStaticRoute data
     * @throws ServiceException When insert failed.
     * @since SDNO 0.5
     */
    public static void insert(List<NbiNeStaticRoute> nbiRoutes) throws ServiceException {

        new InventoryDaoUtil<NbiNeStaticRoute>().getInventoryDao().batchInsert(nbiRoutes);
    }

    /**
     * It is used to update status. <br>
     * 
     * @param nbiRoutes The data that want to be updated
     * @param updateFieldList The field that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void update(List<NbiNeStaticRoute> nbiRoutes, String updateFieldList) throws ServiceException {
        new InventoryDaoUtil<NbiNeStaticRoute>().getInventoryDao().update(NbiNeStaticRoute.class, nbiRoutes,
                updateFieldList);
    }

    /**
     * It is used to update NBI model. <br>
     * 
     * @param nbiRoutes The data that want to be updated
     * @param sbiRoutes The SBI models related to NBI models
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateNbiBySbiModel(List<NbiNeStaticRoute> nbiRoutes, List<SbiNeStaticRoute> sbiRoutes)
            throws ServiceException {

        List<NbiNeStaticRoute> updateList = new ArrayList<>();

        for(NbiNeStaticRoute tempNbiRoute : nbiRoutes) {
            for(SbiNeStaticRoute tempSbiRoute : sbiRoutes) {
                if(tempNbiRoute.getUuid().equals(tempSbiRoute.getNbiNeRouteId())) {
                    updateList.add(tempNbiRoute);
                }
            }
        }

        update(updateList, "priority");
    }

    /**
     * It is used to query NbiNeStaticRoute data. <br>
     * 
     * @param routeId The route id
     * @return The object list of NbiNeStaticRoute
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    public static ResultRsp<NbiNeStaticRoute> query(String routeId) throws ServiceException {
        return new InventoryDaoUtil<NbiNeStaticRoute>().getInventoryDao().query(NbiNeStaticRoute.class, routeId, null);
    }

    /**
     * It is used to batch query NbiNeStaticRoute data. <br>
     * 
     * @param uuidList The route id list
     * @return The object list of NbiNeStaticRoute
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    public static ResultRsp<List<NbiNeStaticRoute>> batchQuery(List<String> uuidList) throws ServiceException {

        Map<String, List<String>> filterMap = new HashMap<>();
        filterMap.put(UUID, uuidList);

        return new InventoryDaoUtil<NbiNeStaticRoute>().getInventoryDao().queryByFilter(NbiNeStaticRoute.class,
                JsonUtil.toJson(filterMap), null);
    }

    /**
     * It is used to batch query NbiNeStaticRoute data. <br>
     * 
     * @param nbiRoutes The object list of NbiNeStaticRoute
     * @return The object list of NbiNeStaticRoute
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    @SuppressWarnings("unchecked")
    public static ResultRsp<List<NbiNeStaticRoute>> queryNbiByModel(List<NbiNeStaticRoute> nbiRoutes)
            throws ServiceException {

        List<String> nbiRouteIds =
                new ArrayList<>(CollectionUtils.collect(nbiRoutes, arg0 -> ((NbiNeStaticRoute)arg0).getUuid()));

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("uuid", nbiRouteIds);

        String filter = JsonUtil.toJson(filterMap);
        LOGGER.info("filter json: " + filter);

        return new InventoryDaoUtil<NbiNeStaticRoute>().getInventoryDao().queryByFilter(NbiNeStaticRoute.class, filter,
                null);
    }

    /**
     * It is used to delete NbiNeStaticRoute data. <br>
     * 
     * @param routeId The route id
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(String routeId) throws ServiceException {

        new InventoryDaoUtil<NbiNeStaticRoute>().getInventoryDao().delete(NbiNeStaticRoute.class, routeId);
    }

    /**
     * Update NBI models by SBI models. <br>
     * 
     * @param nbiRoutes The object list of NBI models
     * @param sbiRoutes The object list of SBI models
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateNbiRoutes(List<NbiNeStaticRoute> nbiRoutes, List<SbiNeStaticRoute> sbiRoutes)
            throws ServiceException {

        List<SbiRouteNetModel> sbiNetModels = new ArrayList<>();
        sbiNetModels.addAll(sbiRoutes);

        for(NbiNeStaticRoute tempRoute : nbiRoutes) {
            String tempNbiRouteId = tempRoute.getUuid();

            for(SbiRouteNetModel tempModel : sbiNetModels) {
                if(tempNbiRouteId.equals(tempModel.getNbiNeRouteId())) {
                    updateDeployStatus(tempRoute, tempModel);
                }
            }
        }

        update(nbiRoutes, "deployStatus,operationStatus,activeStatus");
    }

    private static void updateDeployStatus(NbiNeStaticRoute tempRoute, SbiRouteNetModel tempModel) {

        tempRoute.setDeployStatus(tempModel.getDeployStatus());
        tempRoute.setOperationStatus(tempModel.getOperationStatus());
        tempRoute.setActiveStatus(tempModel.getActiveStatus());
    }

}
