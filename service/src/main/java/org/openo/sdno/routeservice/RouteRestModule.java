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

package org.openo.sdno.routeservice;

import org.openo.sdno.framework.container.service.IRoaModule;
import org.openo.sdno.overlayvpn.inventory.sdk.DbOwerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RouteService Rest Module Class. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class RouteRestModule extends IRoaModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteRestModule.class);

    @Override
    protected void destroy() {
        LOGGER.info("=====stop route svc roa module=====");
    }

    @Override
    protected void init() {
        LOGGER.info("=====start route svc roa module=====");

        DbOwerInfo.init("routeSvc", "routedb");
    }
}
