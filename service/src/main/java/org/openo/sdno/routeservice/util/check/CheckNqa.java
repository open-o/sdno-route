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
import org.openo.sdno.framework.container.util.UuidUtils;
import org.openo.sdno.overlayvpn.brs.invdao.NetworkElementInvDao;
import org.openo.sdno.overlayvpn.brs.model.NetworkElementMO;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
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
public class CheckNqa {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckNqa.class);

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    private CheckNqa() {
    }

    /**
     * It is used to check parameters. <br>
     * 
     * @param nbiNqas The object that want to be checked
     * @param neIdToNeMoMap NE ID to NE information mapping
     * @throws ServiceException When check failed
     * @since SDNO 0.5
     */
    public static void check(List<NbiNqa> nbiNqas, Map<String, NetworkElementMO> neIdToNeMoMap)
            throws ServiceException {

        checkDataAndModel(nbiNqas);

        checkNeAndController(nbiNqas, neIdToNeMoMap);
    }

    private static void checkDataAndModel(List<NbiNqa> nbiNqas) throws ServiceException {

        for(NbiNqa route : nbiNqas) {

            if(!StringUtils.hasLength(route.getUuid())) {
                route.setUuid(UuidUtils.createUuid());
            }

            ValidationUtil.validateModel(route);

        }
    }

    private static void checkNeAndController(List<NbiNqa> nbiNqas, Map<String, NetworkElementMO> neIdToNeMoMap)
            throws ServiceException {
        for(NbiNqa nqa : nbiNqas) {
            NetworkElementInvDao neMoDao = new NetworkElementInvDao();
            NetworkElementMO neMO = neMoDao.query(nqa.getNeId());

            if(null == neMO) {
                LOGGER.error("NE not exist. neid is :" + nqa.getNeId());
                throw new ServiceException("NE not exist. neid is :" + nqa.getNeId());
            }

            if(!StringUtils.hasLength(neMO.getControllerID().get(0))) {
                LOGGER.error("Controller Id is empty. neid is :" + nqa.getNeId());
                throw new ServiceException("Controller Id is empty. neid is :" + nqa.getNeId());
            }

            neIdToNeMoMap.put(nqa.getNeId(), neMO);
        }
    }

}
