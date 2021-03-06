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

package org.openo.sdno.routeservice.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.exception.ParameterServiceException;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.framework.container.util.UuidUtils;
import org.openo.sdno.overlayvpn.brs.model.NetworkElementMO;
import org.openo.sdno.overlayvpn.model.v2.route.NbiActionModel;
import org.openo.sdno.overlayvpn.model.v2.route.NbiNqa;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.service.inf.NqaService;
import org.openo.sdno.routeservice.util.check.CheckNqa;
import org.openo.sdno.routeservice.util.db.NbiNqaDbOper;
import org.openo.sdno.routeservice.util.db.SbiNqaDbOper;
import org.openo.sdno.routeservice.util.operation.NqaNbiToSbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * The rest interface of NQA. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
@Service
@Path("/sdnoroute/v1/nqas")
public class NqaRoaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(NqaRoaResource.class);

    private static final int QUERY_MAX_LENGTH = 1000;

    @Resource
    private NqaService nqaSvc;

    public NqaService getNqaSvc() {
        return nqaSvc;
    }

    public void setNqaSvc(NqaService nqaSvc) {
        this.nqaSvc = nqaSvc;
    }

    /**
     * Batch query NQA.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param uuidList Collection of NQA UUID
     * @return Collection of NbiNqa object
     * @throws ServiceException when query NQA failed
     * @since SDNO 0.5
     */
    @POST
    @Path("/batch-query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<NbiNqa> batchQuery(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            List<String> uuidList) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        if(CollectionUtils.isEmpty(uuidList)) {
            LOGGER.warn("Input uuid list is empty.");
            return new ArrayList<>();
        }

        if(uuidList.size() > QUERY_MAX_LENGTH) {
            LOGGER.error("uuidList invalid. uuidList size: " + uuidList.size() + " max size: " + QUERY_MAX_LENGTH);
            throw new ParameterServiceException("uuidList invalid. uuidList size too long. size:" + uuidList.size());
        }

        LOGGER.info("Batch query body is :" + JsonUtil.toJson(uuidList));

        UuidUtils.checkUuidList(uuidList);

        ResultRsp<List<NbiNqa>> resultRsp = nqaSvc.batchQuery(req, resp, uuidList);

        LOGGER.info("Exit batch query NQA method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getData();
    }

    /**
     * Query NQA by UUID.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nqaId UUID of NQA
     * @return NbiNqa object
     * @throws ServiceException when query NQA failed
     * @since SDNO 0.5
     */
    @GET
    @Path("/{nqaId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NbiNqa query(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            @PathParam("nqaId") String nqaId) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        LOGGER.info("Query begin, nqaId is :" + nqaId);

        UuidUtils.checkUuid(nqaId);

        ResultRsp<NbiNqa> resultRsp = nqaSvc.query(req, resp, nqaId);

        LOGGER.info("Exit query NQA method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getData();
    }

    /**
     * Batch create NQA.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes Collection of NbiNqa object
     * @return Collection of NbiNqa object
     * @throws ServiceException when create NQA failed
     * @since SDNO 0.5
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<NbiNqa> create(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            List<NbiNqa> nbiRoutes) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.warn("Input body is empty.");
            return new ArrayList<>();
        }

        LOGGER.info("Create body is :" + JsonUtil.toJson(nbiRoutes));

        Map<String, NetworkElementMO> neIdToNeMoMap = new HashMap<>();
        CheckNqa.check(nbiRoutes, neIdToNeMoMap);

        NbiNqaDbOper.insert(nbiRoutes);

        List<SbiNqa> sbiRoutes = new ArrayList<>();
        NqaNbiToSbi.convertNbiToSbiModel(nbiRoutes, sbiRoutes, neIdToNeMoMap);

        SbiNqaDbOper.insert(sbiRoutes);

        ResultRsp<NbiNqa> resultRsp = nqaSvc.create(req, resp, nbiRoutes, sbiRoutes);

        LOGGER.info("Exit create NQA method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getSuccessed();
    }

    /**
     * Deploy/Undeploy NQA.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiActionModel NbiActionModel Object
     * @return Collection of successful UUID
     * @throws ServiceException when deploy or undeploy NQA failed
     * @since SDNO 0.5
     */
    @POST
    @Path("/action")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> action(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            NbiActionModel nbiActionModel) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        ResultRsp<String> rsp;
        if(!CollectionUtils.isEmpty(nbiActionModel.getDeploy())) {

            rsp = nqaSvc.deploy(req, resp, nbiActionModel.getDeploy());
        } else if(!CollectionUtils.isEmpty(nbiActionModel.getUndeploy())) {

            rsp = nqaSvc.undeploy(req, resp, nbiActionModel.getUndeploy());
        } else {

            LOGGER.error("deploy and undeploy list is empty.");
            throw new ParameterServiceException("Action body is empty.");
        }

        LOGGER.info(
                "Exit deploy/undeploy NQA method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return rsp.getSuccessed();
    }

    /**
     * Update NQA.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes Collection of NbiNqa object
     * @return Collection of NbiNqa object
     * @throws ServiceException when update NQA failed
     * @since SDNO 0.5
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<NbiNqa> update(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            List<NbiNqa> nbiRoutes) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.warn("Input body is empty.");
            return new ArrayList<>();
        }

        LOGGER.info("Update body is :" + JsonUtil.toJson(nbiRoutes));

        Map<String, NetworkElementMO> neIdToNeMoMap = new HashMap<>();
        CheckNqa.check(nbiRoutes, neIdToNeMoMap);

        ResultRsp<NbiNqa> resultRsp = nqaSvc.update(req, resp, nbiRoutes);

        LOGGER.info("Exit update NQA method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getSuccessed();
    }

    /**
     * Delete NQA.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nqaId UUID of NQA
     * @return UUID of NQA
     * @throws ServiceException when delete NQA failed
     * @since SDNO 0.5
     */
    @DELETE
    @Path("/{nqaId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            @PathParam("nqaId") String nqaId) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        LOGGER.info("Delete begin, nqaId is :" + nqaId);

        UuidUtils.checkUuid(nqaId);

        nqaSvc.delete(req, resp, nqaId);

        LOGGER.info("Exit delete NQA method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return nqaId;
    }

}
