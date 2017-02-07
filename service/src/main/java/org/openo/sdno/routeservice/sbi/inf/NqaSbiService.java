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

package org.openo.sdno.routeservice.sbi.inf;

import java.util.List;

import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.service.IService;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.ResultRsp;

/**
 * NQA south branch interface. <br>
 * 
 * @author
 * @version SDNO 0.5 June 22, 2017
 */
public interface NqaSbiService extends IService {

    /**
     * It is used to create NQA. <br>
     * 
     * @param sbiNqas The collection of NQA data
     * @return The create result
     * @throws ServiceException When create failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNqa> createNqa(List<SbiNqa> sbiNqas) throws ServiceException;

    /**
     * It is used to deploy NQA. <br>
     * 
     * @param sbiNqas The collection of NQA data
     * @return The deploy result
     * @throws ServiceException When deploy failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNqa> deployNqa(List<SbiNqa> sbiNqas) throws ServiceException;

    /**
     * It is used to undeploy NQA. <br>
     * 
     * @param sbiNqas The collection of NQA data
     * @return The undeploy result
     * @throws ServiceException When undeploy failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNqa> undeployNqa(List<SbiNqa> sbiNqas) throws ServiceException;

    /**
     * It is used to update NQA. <br>
     * 
     * @param sbiNqas The collection of NQA data
     * @return The update result
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNqa> updateNqa(List<SbiNqa> sbiNqas) throws ServiceException;

    /**
     * It is used to query NQA. <br>
     * 
     * @param sbiNqa The NQA data
     * @return The query result
     * @throws ServiceException When query failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNqa> queryNqa(SbiNqa sbiNqa) throws ServiceException;

}
