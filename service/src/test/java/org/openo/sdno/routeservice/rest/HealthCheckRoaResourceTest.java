/*
 * Copyright (c) 2017, Huawei Technologies Co., Ltd.
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

package org.openo.sdno.routeservice.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mockit.Mocked;

/**
 * HealthCheckRoaResourceTest test class.<br/>
 * 
 * @author
 * @version SDNO 0.5 Feb 14, 2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring/applicationContext.xml",
                "classpath*:META-INF/spring/service.xml", "classpath*:spring/service.xml"})
public class HealthCheckRoaResourceTest {

    @Mocked
    private HttpServletRequest request;

    @Mocked
    private HttpServletResponse response;

    @Autowired
    private HealthCheckRoaResource hcRoaResource;

    @Test
    public void test() throws ServiceException {

        hcRoaResource.healthCheck(request, response);
    }
}
