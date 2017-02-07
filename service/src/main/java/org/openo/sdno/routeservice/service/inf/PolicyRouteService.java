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

package org.openo.sdno.routeservice.service.inf;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.framework.container.service.IService;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNePolicyRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNePolicyRoute;
import org.openo.sdno.overlayvpn.result.ResultRsp;

/**
 * Policy route service interface. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public interface PolicyRouteService extends IService {

    /**
     * Batch query policy routes. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param uuidList The collection of policy routes UUID
     * @return The object of ResultRsp
     * @throws ServiceException When query policy routes failed
     * @since SDNO 0.5
     */
    ResultRsp<List<NbiNePolicyRoute>> batchQuery(HttpServletRequest req, HttpServletResponse resp,
            List<String> uuidList) throws ServiceException;

    /**
     * Query policy route operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param routeId The UUID of policy route
     * @return The object of ResultRsp
     * @throws ServiceException When query policy route failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNePolicyRoute> query(HttpServletRequest req, HttpServletResponse resp, String routeId)
            throws ServiceException;

    /**
     * Create policy routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes The collection of NBI policy routes data
     * @param sbiRoutes The collection of SBI policy routes data
     * @return The object of ResultRsp
     * @throws ServiceException When create policy routes failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNePolicyRoute> create(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNePolicyRoute> nbiRoutes, List<SbiNePolicyRoute> sbiRoutes) throws ServiceException;

    /**
     * Deploy policy routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRouteIds The collection of policy routes UUID
     * @return The object of ResultRsp
     * @throws ServiceException When deploy policy routes failed
     * @since SDNO 0.5
     */
    ResultRsp<String> deploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException;

    /**
     * Undeploy policy routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRouteIds The collection of policy routes UUID
     * @return The object of ResultRsp
     * @throws ServiceException When undeploy policy routes failed
     * @since SDNO 0.5
     */
    ResultRsp<String> undeploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException;

    /**
     * Update policy routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes The collection of policy routes data
     * @return The object of ResultRsp
     * @throws ServiceException When update policy routes failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNePolicyRoute> update(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNePolicyRoute> nbiRoutes) throws ServiceException;

    /**
     * Delete policy route operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param routeId The UUID of policy route
     * @return The object of ResultRsp
     * @throws ServiceException When delete policy route failed
     * @since SDNO 0.5
     */
    ResultRsp<String> delete(HttpServletRequest req, HttpServletResponse resp, String routeId) throws ServiceException;

}
