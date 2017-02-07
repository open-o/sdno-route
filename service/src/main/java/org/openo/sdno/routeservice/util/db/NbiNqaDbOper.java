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
import org.apache.commons.collections.Transformer;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.inventory.sdk.util.InventoryDaoUtil;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiRouteNetModel;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It is used to operate NbiNqa table. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class NbiNqaDbOper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NbiNqaDbOper.class);

    private static final String UUID = "uuid";

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    private NbiNqaDbOper() {

    }

    /**
     * It is used to insert NbiNqa data. <br>
     * 
     * @param nbiNqas The list of NbiNqa data
     * @throws ServiceException When insert failed.
     * @since SDNO 0.5
     */
    public static void insert(List<NbiNqa> nbiNqas) throws ServiceException {

        new InventoryDaoUtil<NbiNqa>().getInventoryDao().batchInsert(nbiNqas);
    }

    /**
     * It is used to update status. <br>
     * 
     * @param nbiNqas The data that want to be updated
     * @param updateFieldList The field that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void update(List<NbiNqa> nbiNqas, String updateFieldList) throws ServiceException {
        new InventoryDaoUtil<NbiNqa>().getInventoryDao().update(NbiNqa.class, nbiNqas, updateFieldList);
    }

    /**
     * It is used to update NBI model. <br>
     * 
     * @param nbiNqas The data that want to be updated
     * @param sbiNqas The SBI models related to NBI models
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateNbiBySbiModel(List<NbiNqa> nbiNqas, List<SbiNqa> sbiNqas) throws ServiceException {

        List<NbiNqa> updateList = new ArrayList<NbiNqa>();

        for(NbiNqa tempNbiRoute : nbiNqas) {
            for(SbiNqa tempSbiRoute : sbiNqas) {
                if(tempNbiRoute.getUuid().equals(tempSbiRoute.getNbiNeRouteId())) {
                    updateList.add(tempNbiRoute);
                }
            }
        }

        update(updateList, "filterAction");
    }

    /**
     * It is used to query NbiNqa data. <br>
     * 
     * @param routeId The NQA id
     * @return The object list of NbiNqa
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    public static ResultRsp<NbiNqa> query(String routeId) throws ServiceException {
        return new InventoryDaoUtil<NbiNqa>().getInventoryDao().query(NbiNqa.class, routeId, null);
    }

    /**
     * It is used to batch query NbiNqa data. <br>
     * 
     * @param uuidList The route id list
     * @return The object list of NbiNqa
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    public static ResultRsp<List<NbiNqa>> batchQuery(List<String> uuidList) throws ServiceException {

        Map<String, List<String>> filterMap = new HashMap<String, List<String>>();
        filterMap.put(UUID, uuidList);

        return new InventoryDaoUtil<NbiNqa>().getInventoryDao().queryByFilter(NbiNqa.class, JsonUtil.toJson(filterMap),
                null);
    }

    /**
     * It is used to batch query NbiNqa data. <br>
     * 
     * @param nbiNqas The object list of NbiNqa
     * @return The object list of NbiNqa
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    @SuppressWarnings("unchecked")
    public static ResultRsp<List<NbiNqa>> queryNbiByModel(List<NbiNqa> nbiNqas) throws ServiceException {

        List<String> nbiRouteIds = new ArrayList<String>(CollectionUtils.collect(nbiNqas, new Transformer() {

            @Override
            public Object transform(Object arg0) {
                return ((NbiNqa)arg0).getUuid();
            }
        }));

        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("uuid", nbiRouteIds);

        String filter = JsonUtil.toJson(filterMap);
        LOGGER.info("filter json: " + filter);

        return new InventoryDaoUtil<NbiNqa>().getInventoryDao().queryByFilter(NbiNqa.class, filter, null);
    }

    /**
     * It is used to delete NbiNqa data. <br>
     * 
     * @param nqaId The NQA id
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(String nqaId) throws ServiceException {

        new InventoryDaoUtil<NbiNqa>().getInventoryDao().delete(NbiNqa.class, nqaId);
    }

    /**
     * Update NBI models by SBI models. <br>
     * 
     * @param nbiRoutes The object list of NBI models
     * @param sbiRoutes The object list of SBI models
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateNbiNqa(List<NbiNqa> nbiNqas, List<SbiNqa> sbiNqas) throws ServiceException {

        List<SbiRouteNetModel> sbiNetModels = new ArrayList<SbiRouteNetModel>();
        sbiNetModels.addAll(sbiNqas);

        for(NbiNqa tempRoute : nbiNqas) {
            String tempNbiRouteId = tempRoute.getUuid();

            for(SbiRouteNetModel tempModel : sbiNetModels) {
                if(tempNbiRouteId.equals(tempModel.getNbiNeRouteId())) {
                    updateDeployStatus(tempRoute, tempModel);
                }
            }
        }

        update(nbiNqas, "deployStatus,operationStatus,activeStatus");
    }

    private static void updateDeployStatus(NbiNqa tempNqa, SbiRouteNetModel tempModel) {

        tempNqa.setDeployStatus(tempModel.getDeployStatus());
        tempNqa.setOperationStatus(tempModel.getOperationStatus());
        tempNqa.setActiveStatus(tempModel.getActiveStatus());
    }

}
