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

package org.openo.sdno.routeservice.util.check;

import java.util.List;
import java.util.Map;

import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.exception.ParameterServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.framework.container.util.UuidUtils;
import org.openo.sdno.overlayvpn.brs.invdao.NetworkElementInvDao;
import org.openo.sdno.overlayvpn.brs.model.NetworkElementMO;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.overlayvpn.util.check.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * It is used to check parameters that pass by caller. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class CheckStaticRoute {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckStaticRoute.class);

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    private CheckStaticRoute() {
    }

    /**
     * It is used to check parameters. <br>
     * 
     * @param nbiRoutes The object that want to be checked
     * @param neIdToNeMoMap NE ID to NE information mapping
     * @throws ServiceException When check failed
     * @since SDNO 0.5
     */
    public static void check(List<NbiNeStaticRoute> nbiRoutes, Map<String, NetworkElementMO> neIdToNeMoMap)
            throws ServiceException {

        checkDataAndModel(nbiRoutes);

        checkNeAndController(nbiRoutes, neIdToNeMoMap);
    }

    private static void checkDataAndModel(List<NbiNeStaticRoute> nbiRoutes) throws ServiceException {
        for(NbiNeStaticRoute route : nbiRoutes) {

            if(!StringUtils.hasLength(route.getUuid())) {
                route.setUuid(UuidUtils.createUuid());
            }

            route.transferJsonData();

            ValidationUtil.validateModel(route);

            // next hop and out interface cannot be both empty.
            if(!StringUtils.hasLength(route.getNextHop()) && !StringUtils.hasLength(route.getOutInterfaceName())) {
                LOGGER.error("Next hop and out interface cannot be both empty. body is :" + JsonUtil.toJson(route));
                throw new ParameterServiceException("Next hop and out interface cannot be both empty.");
            }
        }
    }

    private static void checkNeAndController(List<NbiNeStaticRoute> nbiRoutes,
            Map<String, NetworkElementMO> neIdToNeMoMap) throws ServiceException {
        for(NbiNeStaticRoute route : nbiRoutes) {
            NetworkElementInvDao neMoDao = new NetworkElementInvDao();
            NetworkElementMO neMO = neMoDao.query(route.getSrcNeId());

            if(null == neMO) {
                LOGGER.error("NE not exist. neid is :" + route.getSrcNeId());
                throw new ServiceException("NE not exist. neid is :" + route.getSrcNeId());
            }

            if(!StringUtils.hasLength(neMO.getControllerID().get(0))) {
                LOGGER.error("Controller Id is empty. neid is :" + route.getSrcNeId());
                throw new ServiceException("Controller Id is empty. neid is :" + route.getSrcNeId());
            }

            neIdToNeMoMap.put(route.getSrcNeId(), neMO);
        }
    }

}
