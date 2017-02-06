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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.result.ResultRsp;

/**
 * Static route service interface. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public interface StaticRouteService extends IService {

    /**
     * Batch query static routes. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param uuidList The collection of static routes UUID
     * @return The object of ResultRsp
     * @throws ServiceException When query static routes failed
     * @since SDNO 0.5
     */
    ResultRsp<List<NbiNeStaticRoute>> batchQuery(HttpServletRequest req, HttpServletResponse resp,
            List<String> uuidList) throws ServiceException;

    /**
     * Query static route operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param routeId The UUID of static route
     * @return The object of ResultRsp
     * @throws ServiceException When query static route failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNeStaticRoute> query(HttpServletRequest req, HttpServletResponse resp, String routeId)
            throws ServiceException;

    /**
     * Create static routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes The collection of NBI static routes data
     * @param sbiRoutes The collection of SBI static routes data
     * @return The object of ResultRsp
     * @throws ServiceException When create static routes failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNeStaticRoute> create(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNeStaticRoute> nbiRoutes, List<SbiNeStaticRoute> sbiRoutes) throws ServiceException;

    /**
     * Deploy static routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRouteIds The collection of static routes UUID
     * @return The object of ResultRsp
     * @throws ServiceException When deploy static routes failed
     * @since SDNO 0.5
     */
    ResultRsp<String> deploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException;

    /**
     * Undeploy static routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRouteIds The collection of static routes UUID
     * @return The object of ResultRsp
     * @throws ServiceException When undeploy static routes failed
     * @since SDNO 0.5
     */
    ResultRsp<String> undeploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiRouteIds)
            throws ServiceException;

    /**
     * Update static routes operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes The collection of static routes data
     * @return The object of ResultRsp
     * @throws ServiceException When update static routes failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNeStaticRoute> update(HttpServletRequest req, HttpServletResponse resp,
            List<NbiNeStaticRoute> nbiRoutes) throws ServiceException;

    /**
     * Delete static route operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param routeId The UUID of static route
     * @return The object of ResultRsp
     * @throws ServiceException When delete static route failed
     * @since SDNO 0.5
     */
    ResultRsp<String> delete(HttpServletRequest req, HttpServletResponse resp, String routeId) throws ServiceException;

}
