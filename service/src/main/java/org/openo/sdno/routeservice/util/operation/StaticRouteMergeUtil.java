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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.result.FailData;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.util.db.NbiNeStaticRouteDbOper;
import org.openo.sdno.routeservice.util.db.SbiNeStaticRouteDbOper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merge result response of static route operation.<br/>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class StaticRouteMergeUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticRouteMergeUtil.class);

    private StaticRouteMergeUtil() {

    }

    /**
     * Merge result of creating static routes.<br/>
     * 
     * @param nbiRoutes The collection of NBI models
     * @param sbiRoutes The collection of SBI models
     * @param rsp The create result
     * @return The result with NBI models
     * @throws ServiceException When merge result failed
     * @since  SDNO 0.5
     */
    public static ResultRsp<NbiNeStaticRoute> mergeCreateResult(List<NbiNeStaticRoute> nbiRoutes,
            List<SbiNeStaticRoute> sbiRoutes, ResultRsp<SbiNeStaticRoute> rsp) throws ServiceException {

        ResultRsp<NbiNeStaticRoute> resultRsp = new ResultRsp<NbiNeStaticRoute>();

        List<SbiNeStaticRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNeStaticRoute>> failRoutes = rsp.getFail();

        SbiNeStaticRouteDbOper.updateDeploySuccess(sbiRoutes, succRoutes);
        SbiNeStaticRouteDbOper.updateDeployFailed(sbiRoutes, failRoutes);

        NbiNeStaticRouteDbOper.updateNbiRoutes(nbiRoutes, sbiRoutes);

        return mergeCreateRsp(nbiRoutes, resultRsp, failRoutes);
    }

    /**
     * Merge result of deploying static routes.<br/>
     * 
     * @param nbiRoutes The collection of NBI models
     * @param sbiRoutes The collection of SBI models
     * @param rsp The deploy result
     * @return The result with NBI models UUID
     * @throws ServiceException When merge result failed
     * @since  SDNO 0.5
     */
    public static ResultRsp<String> mergeDeployResult(List<NbiNeStaticRoute> nbiRoutes,
            List<SbiNeStaticRoute> sbiRoutes, ResultRsp<SbiNeStaticRoute> rsp) throws ServiceException {

        ResultRsp<String> returnRsp = new ResultRsp<String>();

        List<SbiNeStaticRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNeStaticRoute>> failRoutes = rsp.getFail();

        SbiNeStaticRouteDbOper.updateDeploySuccess(sbiRoutes, succRoutes);
        SbiNeStaticRouteDbOper.updateDeployFailed(sbiRoutes, failRoutes);

        NbiNeStaticRouteDbOper.updateNbiRoutes(nbiRoutes, sbiRoutes);

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
    public static ResultRsp<String> mergeUndeployResult(List<NbiNeStaticRoute> nbiRoutes,
            List<SbiNeStaticRoute> sbiRoutes, ResultRsp<SbiNeStaticRoute> rsp) throws ServiceException {

        ResultRsp<String> returnRsp = new ResultRsp<String>();

        List<SbiNeStaticRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNeStaticRoute>> failRoutes = rsp.getFail();

        SbiNeStaticRouteDbOper.updateUndeploySuccess(sbiRoutes, succRoutes);
        SbiNeStaticRouteDbOper.updateUndeployFailed(sbiRoutes, failRoutes);

        NbiNeStaticRouteDbOper.updateNbiRoutes(nbiRoutes, sbiRoutes);

        return mergeDeployRsp(succRoutes, returnRsp, failRoutes);
    }

    /**
     * Merge result of updating static routes.<br/>
     * 
     * @param nbiRoutes The collection of NBI models
     * @param rsp The update result
     * @return The result with NBI model UUID
     * @throws ServiceException When merge result failed
     * @since  SDNO 0.5
     */
    public static ResultRsp<NbiNeStaticRoute> mergeUpdateResult(List<NbiNeStaticRoute> nbiRoutes,
            ResultRsp<SbiNeStaticRoute> rsp) throws ServiceException {

        ResultRsp<NbiNeStaticRoute> resultRsp = new ResultRsp<NbiNeStaticRoute>();

        List<SbiNeStaticRoute> succRoutes = rsp.getSuccessed();
        List<FailData<SbiNeStaticRoute>> failRoutes = rsp.getFail();

        SbiNeStaticRouteDbOper.updateSbiRoute(succRoutes);
        NbiNeStaticRouteDbOper.updateNbiBySbiModel(nbiRoutes, succRoutes);

        return mergeCreateRsp(nbiRoutes, resultRsp, failRoutes);
    }

    private static ResultRsp<NbiNeStaticRoute> mergeCreateRsp(List<NbiNeStaticRoute> nbiRoutes,
            ResultRsp<NbiNeStaticRoute> resultRsp, List<FailData<SbiNeStaticRoute>> failRoutes) throws ServiceException {

        List<NbiNeStaticRoute> nbiSuccRoutes = new ArrayList<NbiNeStaticRoute>();
        List<FailData<NbiNeStaticRoute>> nbifailRoutes = new ArrayList<FailData<NbiNeStaticRoute>>();

        if(CollectionUtils.isEmpty(failRoutes)) {
            resultRsp.getSuccessed().addAll(nbiRoutes);
            return resultRsp;
        }

        for(NbiNeStaticRoute nbiRoute : nbiRoutes) {

            boolean isFailed = false;
            for(FailData<SbiNeStaticRoute> tempFailRoute : failRoutes) {

                if(nbiRoute.getUuid().equals(tempFailRoute.getData().getNbiNeRouteId())) {
                    FailData<NbiNeStaticRoute> tempFailData = new FailData<NbiNeStaticRoute>(tempFailRoute.getErrcode(),
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

    private static ResultRsp<String> mergeDeployRsp(List<SbiNeStaticRoute> sbiRoutes, ResultRsp<String> resultRsp,
            List<FailData<SbiNeStaticRoute>> failRoutes) throws ServiceException {
        for(SbiNeStaticRoute succRoute : sbiRoutes) {
            resultRsp.getSuccessed().add(succRoute.getNbiNeRouteId());
        }

        for(FailData<SbiNeStaticRoute> failRoute : failRoutes) {
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
