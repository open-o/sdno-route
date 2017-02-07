/*
 * Copyright (c) 2017, Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.sdno.routeservice.util.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.errorcode.ErrorCode;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNePolicyRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNePolicyRoute;
import org.openo.sdno.overlayvpn.result.FailData;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.util.db.NbiNePolicyRouteDbOper;
import org.openo.sdno.routeservice.util.db.SbiNePolicyRouteDbOper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merge result response of policy route operation.<br/>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class PolicyRouteMergeUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyRouteMergeUtil.class);

    private PolicyRouteMergeUtil() {

    }

    /**
     * Merge result of creating policy routes.<br/>
     * 
     * @param nbiNqas The collection of NBI models
     * @param sbiNqas The collection of SBI models
     * @param rsp The create result
     * @return The result with NBI models
     * @throws ServiceException When merge result failed
     * @since  SDNO 0.5
     */
    public static ResultRsp<NbiNePolicyRoute> mergeCreateResult(List<NbiNePolicyRoute> nbiRoutes,
            List<SbiNePolicyRoute> sbiRoutes, ResultRsp<SbiNePolicyRoute> rsp) throws ServiceException {

        ResultRsp<NbiNePolicyRoute> resultRsp = new ResultRsp<NbiNePolicyRoute>();

        List<SbiNePolicyRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNePolicyRoute>> failRoutes = rsp.getFail();

        SbiNePolicyRouteDbOper.updateDeploySuccess(sbiRoutes, succRoutes);
        SbiNePolicyRouteDbOper.updateDeployFailed(sbiRoutes, failRoutes);

        NbiNePolicyRouteDbOper.updateNbiRoutes(nbiRoutes, sbiRoutes);

        return mergeCreateRsp(nbiRoutes, resultRsp, failRoutes);
    }

    /**
     * Merge result of deploying policy routes.<br/>
     * 
     * @param nbiRoutes The collection of NBI models
     * @param sbiRoutes The collection of SBI models
     * @param rsp The deploy result
     * @return The result with NBI models UUID
     * @throws ServiceException When merge result failed
     * @since  SDNO 0.5
     */
    public static ResultRsp<String> mergeDeployResult(List<NbiNePolicyRoute> nbiRoutes,
            List<SbiNePolicyRoute> sbiRoutes, ResultRsp<SbiNePolicyRoute> rsp) throws ServiceException {

        ResultRsp<String> returnRsp = new ResultRsp<String>();

        List<SbiNePolicyRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNePolicyRoute>> failRoutes = rsp.getFail();

        SbiNePolicyRouteDbOper.updateDeploySuccess(sbiRoutes, succRoutes);
        SbiNePolicyRouteDbOper.updateDeployFailed(sbiRoutes, failRoutes);

        NbiNePolicyRouteDbOper.updateNbiRoutes(nbiRoutes, sbiRoutes);

        return mergeDeployRsp(succRoutes, returnRsp, failRoutes);
    }

    /**
     * Merge result of undeploying static routes.<br/>
     * 
     * @param nbiRoutes The collection of NBI models
     * @param sbiRoutes The collection of SBI models
     * @param rsp The undeploy result
     * @return The result with NBI models UUID
     * @throws ServiceException When merge result failed
     * @since  SDNO 0.5
     */
    public static ResultRsp<String> mergeUndeployResult(List<NbiNePolicyRoute> nbiRoutes,
            List<SbiNePolicyRoute> sbiRoutes, ResultRsp<SbiNePolicyRoute> rsp) throws ServiceException {

        ResultRsp<String> returnRsp = new ResultRsp<String>();

        List<SbiNePolicyRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNePolicyRoute>> failRoutes = rsp.getFail();

        SbiNePolicyRouteDbOper.updateUndeploySuccess(sbiRoutes, succRoutes);
        SbiNePolicyRouteDbOper.updateUndeployFailed(sbiRoutes, failRoutes);

        NbiNePolicyRouteDbOper.updateNbiRoutes(nbiRoutes, sbiRoutes);

        return mergeDeployRsp(succRoutes, returnRsp, failRoutes);
    }

    /**
     * Merge result of updating policy routes.<br/>
     * 
     * @param nbiRoutes The collection of NBI models
     * @param rsp The update result
     * @return The result with NBI model UUID
     * @throws ServiceException When merge result failed
     * @since  SDNO 0.5
     */
    public static ResultRsp<NbiNePolicyRoute> mergeUpdateResult(List<NbiNePolicyRoute> nbiRoutes,
            ResultRsp<SbiNePolicyRoute> rsp) throws ServiceException {

        ResultRsp<NbiNePolicyRoute> resultRsp = new ResultRsp<NbiNePolicyRoute>();

        List<SbiNePolicyRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNePolicyRoute>> failRoutes = rsp.getFail();

        SbiNePolicyRouteDbOper.updateSbiRoute(succRoutes);
        NbiNePolicyRouteDbOper.updateNbiBySbiModel(nbiRoutes, succRoutes);

        return mergeCreateRsp(nbiRoutes, resultRsp, failRoutes);
    }

    private static ResultRsp<NbiNePolicyRoute> mergeCreateRsp(List<NbiNePolicyRoute> nbiRoutes,
            ResultRsp<NbiNePolicyRoute> resultRsp, List<FailData<SbiNePolicyRoute>> failRoutes) throws ServiceException {

        List<NbiNePolicyRoute> nbiSuccRoutes = new ArrayList<NbiNePolicyRoute>();
        List<FailData<NbiNePolicyRoute>> nbifailRoutes = new ArrayList<FailData<NbiNePolicyRoute>>();

        if(CollectionUtils.isEmpty(failRoutes)) {
            resultRsp.getSuccessed().addAll(nbiRoutes);
            return resultRsp;
        }

        for(NbiNePolicyRoute nbiRoute : nbiRoutes) {

            boolean isFailed = false;
            for(FailData<SbiNePolicyRoute> tempFailRoute : failRoutes) {

                if(nbiRoute.getUuid().equals(tempFailRoute.getData().getNbiNeRouteId())) {
                    FailData<NbiNePolicyRoute> tempFailData = new FailData<NbiNePolicyRoute>(tempFailRoute.getErrcode(),
                            tempFailRoute.getErrmsg(), nbiRoute);
                    nbifailRoutes.add(tempFailData);
                    isFailed = true;
                    break;
                }
            }

            if(!isFailed) {
                nbiSuccRoutes.add(nbiRoute);
            }
        }

        resultRsp.getSuccessed().addAll(nbiSuccRoutes);
        resultRsp.getFail().addAll(nbifailRoutes);

        if(CollectionUtils.isEmpty(nbifailRoutes)) {
            resultRsp.setErrorCode(ErrorCode.OVERLAYVPN_SUCCESS);
        } else {
            LOGGER.error("Result failed. body: " + JsonUtil.toJson(nbifailRoutes));
            throw new ServiceException("Result failed. body: " + JsonUtil.toJson(nbifailRoutes));
        }

        return resultRsp;
    }

    private static ResultRsp<String> mergeDeployRsp(List<SbiNePolicyRoute> sbiRoutes, ResultRsp<String> resultRsp,
            List<FailData<SbiNePolicyRoute>> failRoutes) throws ServiceException {
        for(SbiNePolicyRoute succRoute : sbiRoutes) {
            resultRsp.getSuccessed().add(succRoute.getNbiNeRouteId());
        }

        for(FailData<SbiNePolicyRoute> failRoute : failRoutes) {
            FailData<String> tempFailData = new FailData<String>(failRoute.getErrcode(), failRoute.getErrmsg(),
                    failRoute.getData().getNbiNeRouteId());
            resultRsp.getFail().add(tempFailData);
        }
        
        if(CollectionUtils.isEmpty(resultRsp.getFail())) {
            resultRsp.setErrorCode(ErrorCode.OVERLAYVPN_SUCCESS);
        } else {
            LOGGER.error("Result failed. body: " + JsonUtil.toJson(resultRsp.getFail()));
            throw new ServiceException("Result failed. body: " + JsonUtil.toJson(resultRsp.getFail()));
        }
        
        return resultRsp;
    }

}