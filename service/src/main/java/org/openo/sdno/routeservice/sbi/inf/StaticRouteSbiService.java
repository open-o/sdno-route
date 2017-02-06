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
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.result.ResultRsp;

/**
 * Static route south branch interface. <br>
 * 
 * @author
 * @version SDNO 0.5 June 22, 2017
 */
public interface StaticRouteSbiService extends IService {

    /**
     * It is used to create static route. <br>
     * 
     * @param sbiRoutes The collection of static route data
     * @return The create result
     * @throws ServiceException When create failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNeStaticRoute> createRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException;

    /**
     * It is used to deploy static route. <br>
     * 
     * @param sbiRoutes The collection of static route data
     * @return The deploy result
     * @throws ServiceException When deploy failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNeStaticRoute> deployRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException;

    /**
     * It is used to undeploy static route. <br>
     * 
     * @param sbiRoutes The collection of static route data
     * @return The undeploy result
     * @throws ServiceException When undeploy failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNeStaticRoute> undeployRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException;

    /**
     * It is used to update static route. <br>
     * 
     * @param sbiRoutes The collection of static route data
     * @return The update result
     * @throws ServiceException When update failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNeStaticRoute> updateRoutes(List<SbiNeStaticRoute> sbiRoutes) throws ServiceException;

    /**
     * It is used to query static route. <br>
     * 
     * @param sbiRoutes The static route data
     * @return The query result
     * @throws ServiceException When query failed.
     * @since SDNO 0.5
     */
    ResultRsp<SbiNeStaticRoute> queryRoutes(SbiNeStaticRoute sbiRoute) throws ServiceException;

}
