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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.FailData;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * It is used to operate SbiNqa table. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class SbiNqaDbOper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbiNqaDbOper.class);

    private static final String UUID = "uuid";

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    private SbiNqaDbOper() {

    }

    /**
     * It is used to insert SbiNqa data. <br>
     * 
     * @param sbiNqas The list of SbiNqa data
     * @throws ServiceException When insert failed.
     * @since SDNO 0.5
     */
    public static void insert(List<SbiNqa> sbiNqas) throws ServiceException {

        new InventoryDaoUtil<SbiNqa>().getInventoryDao().batchInsert(sbiNqas);
    }

    /**
     * It is used to update status. <br>
     * 
     * @param sbiNqas The data that want to be updated
     * @param updateFieldList The field that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void update(List<SbiNqa> sbiNqas, String updateFieldList) throws ServiceException {
        new InventoryDaoUtil<SbiNqa>().getInventoryDao().update(SbiNqa.class, sbiNqas, updateFieldList);
    }

    /**
     * It is used to query SbiNqa data. <br>
     * 
     * @param routeId The NQA id
     * @return The object list of SbiNqa
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    public static ResultRsp<List<SbiNqa>> query(String routeId) throws ServiceException {
        return queryByFilter(routeId, null);
    }

    /**
     * It is used to batch query SbiNqa data. <br>
     * 
     * @param nbiRoutes The object list of NbiNqa
     * @return The object list of SbiNqa
     * @throws ServiceException When query failed
     * @since SDNO 0.5
     */
    @SuppressWarnings("unchecked")
    public static ResultRsp<List<SbiNqa>> querySbiByNbiModel(List<NbiNqa> nbiNqas) throws ServiceException {

        List<String> nbiRouteIds = new ArrayList<>(CollectionUtils.collect(nbiNqas, arg0 -> ((NbiNqa)arg0).getUuid()));

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("nbiNeRouteId", nbiRouteIds);

        String filter = JsonUtil.toJson(filterMap);
        LOGGER.info("filter json: " + filter);

        return new InventoryDaoUtil<SbiNqa>().getInventoryDao().queryByFilter(SbiNqa.class, filter, null);
    }

    /**
     * It is used to delete SbiNqa data. <br>
     * 
     * @param nqaId The NQA id
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(String nqaId) throws ServiceException {
        ResultRsp<List<SbiNqa>> queryDbRsp = queryByFilter(nqaId, UUID);
        if(CollectionUtils.isEmpty(queryDbRsp.getData())) {
            LOGGER.warn("delete error, connectionId (" + nqaId + ") is not found");
            return;
        }

        SbiNqa sbiNqa = queryDbRsp.getData().get(0);
        new InventoryDaoUtil<SbiNqa>().getInventoryDao().delete(SbiNqa.class, sbiNqa.getUuid());
    }

    /**
     * It is used to batch delete SbiNqa data. <br>
     * 
     * @param sbiNqas The objects that want to be deleted
     * @throws ServiceException When delete failed.
     * @since SDNO 0.5
     */
    public static void delete(List<SbiNqa> sbiNqas) throws ServiceException {
        List<String> uuidList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(sbiNqas)) {
            for(SbiNqa route : sbiNqas) {
                uuidList.add(route.getUuid());
            }
        }

        new InventoryDaoUtil<SbiNqa>().getInventoryDao().batchDelete(SbiNqa.class, uuidList);
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiNqas The data that want to be updated
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateSbiNqa(List<SbiNqa> sbiNqas) throws ServiceException {
        new InventoryDaoUtil<SbiNqa>().getInventoryDao().update(SbiNqa.class, sbiNqas, "name,ttl,tos");
    }

    /**
     * It is used to update SBI model. <br>
     * 
     * @param sbiNqas The data that want to be updated
     * @param succNqas The data that deploy successfully
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateDeploySuccess(List<SbiNqa> sbiNqas, List<SbiNqa> succNqas) throws ServiceException {

        if(CollectionUtils.isEmpty(succNqas)) {
            return;
        }

        List<SbiNqa> updateList = new ArrayList<>();
        for(SbiNqa tempRoute : sbiNqas) {
            for(SbiNqa tempSuccRoute : succNqas) {
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
     * @param sbiNqas The data that want to be updated
     * @param failNqas The data that deploy failed
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateDeployFailed(List<SbiNqa> sbiNqas, List<FailData<SbiNqa>> failNqas)
            throws ServiceException {

        if(CollectionUtils.isEmpty(failNqas)) {
            return;
        }

        List<SbiNqa> updateList = new ArrayList<>();
        for(SbiNqa tempRoute : sbiNqas) {
            for(FailData<SbiNqa> tempFailRoute : failNqas) {
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
     * @param sbiNqas The data that want to be updated
     * @param succNqas The data that undeploy successfully
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateUndeploySuccess(List<SbiNqa> sbiNqas, List<SbiNqa> succNqas) throws ServiceException {

        if(CollectionUtils.isEmpty(succNqas)) {
            return;
        }

        List<SbiNqa> updateList = new ArrayList<>();
        for(SbiNqa tempRoute : sbiNqas) {
            for(SbiNqa tempSuccRoute : succNqas) {
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
     * @param sbiNqas The data that want to be updated
     * @param failNqas The data that undeploy failed
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    public static void updateUndeployFailed(List<SbiNqa> sbiNqas, List<FailData<SbiNqa>> failNqas)
            throws ServiceException {

        if(CollectionUtils.isEmpty(failNqas)) {
            return;
        }

        List<SbiNqa> updateList = new ArrayList<>();
        for(SbiNqa tempRoute : sbiNqas) {
            for(FailData<SbiNqa> tempFailRoute : failNqas) {
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

    private static ResultRsp<List<SbiNqa>> queryByFilter(String connectionId, String queryResultFields)
            throws ServiceException {
        Map<String, Object> filterMap = new HashMap<>();
        if(StringUtils.hasLength(connectionId)) {
            filterMap.put("nbiNeRouteId", Arrays.asList(connectionId));
        }

        String filter = JSONObject.fromObject(filterMap).toString();

        return new InventoryDaoUtil<SbiNqa>().getInventoryDao().queryByFilter(SbiNqa.class, filter, queryResultFields);
    }

}
