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

import java.util.List;
import java.util.Map;

import org.openo.sdno.framework.container.util.UuidUtils;
import org.openo.sdno.overlayvpn.brs.model.NetworkElementMO;
import org.openo.sdno.overlayvpn.model.common.enums.ActionStatus;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNePolicyRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNePolicyRoute;

/**
 * Convert NBI model to SBI model.<br/>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class PolicyRouteNbiToSbi {

    private PolicyRouteNbiToSbi() {

    }

    /**
     * Convert NBI model to SBI model.<br/>
     * 
     * @param nbiRoutes NBI models
     * @param sbiRoutes SBI models
     * @param neIdToNeMoMap NE Id to NE model map
     * @since SDNO 0.5
     */
    public static void convertNbiToSbiModel(List<NbiNePolicyRoute> nbiRoutes, List<SbiNePolicyRoute> sbiRoutes,
            Map<String, NetworkElementMO> neIdToNeMoMap) {
        for(NbiNePolicyRoute nbiRoute : nbiRoutes) {

            SbiNePolicyRoute sbiRoute = new SbiNePolicyRoute();
            sbiRoute.setUuid(UuidUtils.createUuid());

            setBasciSbiNePolicyRoute(nbiRoute, sbiRoute);
            setSbiNePolicyRoute(nbiRoute, sbiRoute);

            NetworkElementMO neMO = neIdToNeMoMap.get(nbiRoute.getSrcNeId());
            sbiRoute.setDeviceId(neMO.getNativeID());
            sbiRoute.setControllerId(neMO.getControllerID().get(0));

            sbiRoutes.add(sbiRoute);
        }
    }

    private static void setSbiNePolicyRoute(NbiNePolicyRoute nbiRoute, SbiNePolicyRoute sbiRoute) {
        sbiRoute.setTrafficPolicyName(nbiRoute.getTrafficPolicyName());
        sbiRoute.setFilterAction(nbiRoute.getTrafficPolicyName());
        sbiRoute.setInterfaceName(nbiRoute.getInterfaceName());
        sbiRoute.setDirection(nbiRoute.getDirection());
    }

    private static void setBasciSbiNePolicyRoute(NbiNePolicyRoute nbiRoute, SbiNePolicyRoute sbiRoute) {
        sbiRoute.setNbiNeRouteId(nbiRoute.getUuid());
        sbiRoute.setName(nbiRoute.getName());
        sbiRoute.setTenantId(nbiRoute.getTenantId());
        sbiRoute.setDescription(nbiRoute.getDescription());
        sbiRoute.setDeployStatus(nbiRoute.getDeployStatus());
        sbiRoute.setActiveStatus(nbiRoute.getActiveStatus());
        sbiRoute.setOperationStatus(ActionStatus.CREATING.getName());
    }

}
