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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.FailData;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.util.db.NbiNqaDbOper;
import org.openo.sdno.routeservice.util.db.SbiNqaDbOper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merge result response of NQA operation.<br/>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class NqaMergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NqaMergeUtil.class);

    private NqaMergeUtil() {

    }

    /**
     * Merge result of creating NQA.<br/>
     * 
     * @param nbiNqas The collection of NBI models
     * @param sbiNqas The collection of SBI models
     * @param rsp The create result
     * @return The result with NBI models
     * @throws ServiceException When merge result failed
     * @since SDNO 0.5
     */
    public static ResultRsp<NbiNqa> mergeCreateResult(List<NbiNqa> nbiNqas, List<SbiNqa> sbiNqas, ResultRsp<SbiNqa> rsp)
            throws ServiceException {

        ResultRsp<NbiNqa> resultRsp = new ResultRsp<>();

        List<SbiNqa> succNqa = rsp.getSuccessed();
        List<FailData<SbiNqa>> failNqa = rsp.getFail();

        SbiNqaDbOper.updateDeploySuccess(sbiNqas, succNqa);
        SbiNqaDbOper.updateDeployFailed(sbiNqas, failNqa);

        NbiNqaDbOper.updateNbiNqa(nbiNqas, sbiNqas);

        return mergeCreateRsp(nbiNqas, resultRsp, failNqa);
    }

    /**
     * Merge result of deploying NQA.<br/>
     * 
     * @param nbiNqas The collection of NBI models
     * @param sbiNqas The collection of SBI models
     * @param rsp The deploy result
     * @return The result with NBI models UUID
     * @throws ServiceException When merge result failed
     * @since SDNO 0.5
     */
    public static ResultRsp<String> mergeDeployResult(List<NbiNqa> nbiNqas, List<SbiNqa> sbiNqas, ResultRsp<SbiNqa> rsp)
            throws ServiceException {

        ResultRsp<String> returnRsp = new ResultRsp<>();

        List<SbiNqa> succNqa = rsp.getSuccessed();
        List<FailData<SbiNqa>> failNqa = rsp.getFail();

        SbiNqaDbOper.updateDeploySuccess(sbiNqas, succNqa);
        SbiNqaDbOper.updateDeployFailed(sbiNqas, failNqa);

        NbiNqaDbOper.updateNbiNqa(nbiNqas, sbiNqas);

        return mergeDeployRsp(succNqa, returnRsp, failNqa);
    }

    /**
     * Merge result of undeploying NQA.<br/>
     * 
     * @param nbiNqas The collection of NBI models
     * @param sbiNqas The collection of SBI models
     * @param rsp The undeploy result
     * @return The result with NBI models UUID
     * @throws ServiceException When merge result failed
     * @since SDNO 0.5
     */
    public static ResultRsp<String> mergeUndeployResult(List<NbiNqa> nbiNqas, List<SbiNqa> sbiNqas,
            ResultRsp<SbiNqa> rsp) throws ServiceException {

        ResultRsp<String> returnRsp = new ResultRsp<>();

        List<SbiNqa> succNqa = rsp.getSuccessed();
        List<FailData<SbiNqa>> failNqa = rsp.getFail();

        SbiNqaDbOper.updateUndeploySuccess(sbiNqas, succNqa);
        SbiNqaDbOper.updateUndeployFailed(sbiNqas, failNqa);

        NbiNqaDbOper.updateNbiNqa(nbiNqas, sbiNqas);

        return mergeDeployRsp(succNqa, returnRsp, failNqa);
    }

    /**
     * Merge result of updating NQA.<br/>
     * 
     * @param nbiNqas The collection of NBI models
     * @param rsp The update result
     * @return The result with NBI models
     * @throws ServiceException When merge result failed
     * @since SDNO 0.5
     */
    public static ResultRsp<NbiNqa> mergeUpdateResult(List<NbiNqa> nbiNqas, ResultRsp<SbiNqa> rsp)
            throws ServiceException {

        ResultRsp<NbiNqa> resultRsp = new ResultRsp<>();

        List<SbiNqa> succNqa = rsp.getSuccessed();
        List<FailData<SbiNqa>> failNqa = rsp.getFail();

        SbiNqaDbOper.updateSbiNqa(succNqa);
        NbiNqaDbOper.updateNbiBySbiModel(nbiNqas, succNqa);

        return mergeCreateRsp(nbiNqas, resultRsp, failNqa);
    }

    /**
     * Convert result with ID to result with body data.<br/>
     * 
     * @param reRsp Result with ID
     * @param sbiNqas Collection of body data
     * @return Result with body data
     * @since SDNO 0.5
     */
    public static ResultRsp<SbiNqa> convertResultType(ResultRsp<String> reRsp, List<SbiNqa> sbiNqas) {

        ResultRsp<SbiNqa> resultRsp = new ResultRsp<>();

        for(String successId : reRsp.getSuccessed()) {
            for(SbiNqa sbiNqa : sbiNqas) {
                if(sbiNqa.getUuid().equals(successId)) {
                    resultRsp.getSuccessed().add(sbiNqa);
                }
            }
        }

        for(FailData<String> tempFail : reRsp.getFail()) {
            for(SbiNqa sbiNqa : sbiNqas) {
                if(sbiNqa.getUuid().equals(tempFail.getData())) {
                    FailData<SbiNqa> failData = new FailData<>();
                    failData.setData(sbiNqa);
                    failData.setErrcode(tempFail.getErrcode());
                    failData.setErrmsg(tempFail.getErrmsg());
                    resultRsp.getFail().add(failData);
                }
            }
        }

        resultRsp.setErrorCode(reRsp.getErrorCode());
        return resultRsp;
    }

    private static ResultRsp<NbiNqa> mergeCreateRsp(List<NbiNqa> nbiNqas, ResultRsp<NbiNqa> resultRsp,
            List<FailData<SbiNqa>> failNqa) throws ServiceException {

        List<NbiNqa> nbiSuccNqa = new ArrayList<>();
        List<FailData<NbiNqa>> nbifailNqa = new ArrayList<>();

        if(CollectionUtils.isEmpty(failNqa)) {
            resultRsp.getSuccessed().addAll(nbiNqas);
            return resultRsp;
        }

        for(NbiNqa nbiNqa : nbiNqas) {

            boolean isFailed = false;
            for(FailData<SbiNqa> tempFailRoute : failNqa) {

                if(nbiNqa.getUuid().equals(tempFailRoute.getData().getNbiNeRouteId())) {
                    FailData<NbiNqa> tempFailData =
                            new FailData<>(tempFailRoute.getErrcode(), tempFailRoute.getErrmsg(), nbiNqa);
                    nbifailNqa.add(tempFailData);
                    isFailed = true;
                    break;
                }
            }

            if(!isFailed) {
                nbiSuccNqa.add(nbiNqa);
            }
        }

        resultRsp.getSuccessed().addAll(nbiSuccNqa);
        resultRsp.getFail().addAll(nbifailNqa);

        if(CollectionUtils.isEmpty(nbifailNqa)) {
            resultRsp.setErrorCode(ErrorCode.OVERLAYVPN_SUCCESS);
        } else {
            LOGGER.error("Result failed. FailData: " + JsonUtil.toJson(nbifailNqa));
            throw new ServiceException("Result failed. Fail data: " + JsonUtil.toJson(nbifailNqa));
        }

        return resultRsp;
    }

    private static ResultRsp<String> mergeDeployRsp(List<SbiNqa> sbiNqas, ResultRsp<String> resultRsp,
            List<FailData<SbiNqa>> failNqas) throws ServiceException {
        for(SbiNqa succRoute : sbiNqas) {
            resultRsp.getSuccessed().add(succRoute.getNbiNeRouteId());
        }

        for(FailData<SbiNqa> tempNqa : failNqas) {
            FailData<String> tempFailData =
                    new FailData<>(tempNqa.getErrcode(), tempNqa.getErrmsg(), tempNqa.getData().getNbiNeRouteId());
            resultRsp.getFail().add(tempFailData);
        }

        if(CollectionUtils.isEmpty(resultRsp.getFail())) {
            resultRsp.setErrorCode(ErrorCode.OVERLAYVPN_SUCCESS);
        } else {
            LOGGER.error("Total result failed. FailData: " + JsonUtil.toJson(resultRsp.getFail()));
            throw new ServiceException("Total result failed. Fail data: " + JsonUtil.toJson(resultRsp.getFail()));
        }

        return resultRsp;
    }

}
