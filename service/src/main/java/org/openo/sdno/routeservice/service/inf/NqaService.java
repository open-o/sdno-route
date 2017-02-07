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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.ResultRsp;

/**
 * NQA service interface. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public interface NqaService extends IService {

    /**
     * Batch query NQA. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param uuidList The collection of NQA UUID
     * @return The object of ResultRsp
     * @throws ServiceException When query NQA failed
     * @since SDNO 0.5
     */
    ResultRsp<List<NbiNqa>> batchQuery(HttpServletRequest req, HttpServletResponse resp, List<String> uuidList)
            throws ServiceException;

    /**
     * Query NQA operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nqaId The UUID of NQA
     * @return The object of ResultRsp
     * @throws ServiceException When query NQA failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNqa> query(HttpServletRequest req, HttpServletResponse resp, String nqaId) throws ServiceException;

    /**
     * Create NQA operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiNqas The collection of NBI NQA data
     * @param sbiNqas The collection of SBI NQA data
     * @return The object of ResultRsp
     * @throws ServiceException When create NQA failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNqa> create(HttpServletRequest req, HttpServletResponse resp, List<NbiNqa> nbiNqas,
            List<SbiNqa> sbiNqas) throws ServiceException;

    /**
     * Deploy NQA operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiNqaIds The collection of NQA UUID
     * @return The object of ResultRsp
     * @throws ServiceException When deploy NQA failed
     * @since SDNO 0.5
     */
    ResultRsp<String> deploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiNqaIds)
            throws ServiceException;

    /**
     * Undeploy NQA operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiNqaIds The collection of NQA UUID
     * @return The object of ResultRsp
     * @throws ServiceException When undeploy NQA failed
     * @since SDNO 0.5
     */
    ResultRsp<String> undeploy(HttpServletRequest req, HttpServletResponse resp, List<String> nbiNqaIds)
            throws ServiceException;

    /**
     * Update NQA operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiNqas The collection of NQA data
     * @return The object of ResultRsp
     * @throws ServiceException When update NQA failed
     * @since SDNO 0.5
     */
    ResultRsp<NbiNqa> update(HttpServletRequest req, HttpServletResponse resp, List<NbiNqa> nbiNqas)
            throws ServiceException;

    /**
     * Delete NQA operation. <br>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nqaId The UUID of NQA
     * @return The object of ResultRsp
     * @throws ServiceException When delete NQA failed
     * @since SDNO 0.5
     */
    ResultRsp<String> delete(HttpServletRequest req, HttpServletResponse resp, String nqaId) throws ServiceException;

}
