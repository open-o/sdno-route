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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;

/**
 * Convert NBI model to SBI model.<br/>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class NqaNbiToSbi {

    private NqaNbiToSbi() {

    }

    /**
     * Convert NBI model to SBI model.<br/>
     * 
     * @param nbiRoutes NBI models
     * @param sbiRoutes SBI models
     * @param neIdToNeMoMap NE Id to NE model map
     * @since SDNO 0.5
     */
    public static void convertNbiToSbiModel(List<NbiNqa> nbiNqas, List<SbiNqa> sbiNqas,
            Map<String, NetworkElementMO> neIdToNeMoMap) {
        for(NbiNqa nbiNqa : nbiNqas) {

            SbiNqa sbiNqa = new SbiNqa();
            sbiNqa.setUuid(UuidUtils.createUuid());

            setBasciSbiNqa(nbiNqa, sbiNqa);
            setSbiNqa(nbiNqa, sbiNqa);

            NetworkElementMO neMO = neIdToNeMoMap.get(nbiNqa.getNeId());
            sbiNqa.setDeviceId(neMO.getNativeID());
            sbiNqa.setControllerId(neMO.getControllerID().get(0));

            sbiNqas.add(sbiNqa);
        }
    }

    private static void setSbiNqa(NbiNqa nbiNqa, SbiNqa sbiNqa) {
        sbiNqa.setNeId(nbiNqa.getNeId());
        sbiNqa.setNeRole(nbiNqa.getNeRole());
        sbiNqa.setSrcIp(nbiNqa.getSrcIp());
        sbiNqa.setSrcPortName(nbiNqa.getSrcPortName());
        sbiNqa.setDstIp(nbiNqa.getDstIp());
        sbiNqa.setDstPortName(nbiNqa.getDstPortName());
        sbiNqa.setTestType(nbiNqa.getTestType());
        sbiNqa.setFrequency(nbiNqa.getFrequency());
        sbiNqa.setProbeCount(nbiNqa.getProbeCount());
        sbiNqa.setTimeout(nbiNqa.getTimeout());
        sbiNqa.setTtl(nbiNqa.getTtl());
        sbiNqa.setTos(nbiNqa.getTos());
        sbiNqa.setInterval(nbiNqa.getInterval());
    }

    private static void setBasciSbiNqa(NbiNqa nbiNqa, SbiNqa sbiNqa) {
        sbiNqa.setNbiNeRouteId(nbiNqa.getUuid());
        sbiNqa.setExternalId(nbiNqa.getUuid());
        sbiNqa.setName(nbiNqa.getName());
        sbiNqa.setTenantId(nbiNqa.getTenantId());
        sbiNqa.setDescription(nbiNqa.getDescription());
        sbiNqa.setDeployStatus(nbiNqa.getDeployStatus());
        sbiNqa.setActiveStatus(nbiNqa.getActiveStatus());
        sbiNqa.setOperationStatus(ActionStatus.CREATING.getName());
    }

}
