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
import org.openo.sdno.overlayvpn.model.v2.route.NbiNeStaticRoute;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNeStaticRoute;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.routeservice.service.inf.StaticRouteService;
import org.openo.sdno.routeservice.util.check.CheckStaticRoute;
import org.openo.sdno.routeservice.util.db.NbiNeStaticRouteDbOper;
import org.openo.sdno.routeservice.util.db.SbiNeStaticRouteDbOper;
import org.openo.sdno.routeservice.util.operation.StaticRouteNbiToSbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * The rest interface of static route. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
@Service
@Path("/sdnoroute/v1/static-routes")
public class StaticRouteRoaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticRouteRoaResource.class);

    private static final int QUERY_MAX_LENGTH = 1000;

    @Resource
    private StaticRouteService staticRouteSvc;

    public StaticRouteService getStaticRouteSvc() {
        return staticRouteSvc;
    }

    public void setStaticRouteSvc(StaticRouteService staticRouteSvc) {
        this.staticRouteSvc = staticRouteSvc;
    }

    /**
     * Batch query static routes.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param uuidList Collection of static routes UUID
     * @return Collection of NbiNeStaticRoute object
     * @throws ServiceException when query static routes failed
     * @since SDNO 0.5
     */
    @POST
    @Path("/batch-query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<NbiNeStaticRoute> batchQuery(@Context HttpServletRequest req, @Context HttpServletResponse resp,
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

        ResultRsp<List<NbiNeStaticRoute>> resultRsp = staticRouteSvc.batchQuery(req, resp, uuidList);

        LOGGER.info("Exit batch query static routes method. cost time(ms): "
                + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getData();
    }

    /**
     * Query policy route by UUID.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param routeId UUID of static route
     * @return NbiNeStaticRoute object
     * @throws ServiceException when query static route failed
     * @since SDNO 0.5
     */
    @GET
    @Path("/{routeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NbiNeStaticRoute query(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            @PathParam("routeId") String routeId) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        LOGGER.info("Query begin, nbiRouteId is :" + routeId);

        UuidUtils.checkUuid(routeId);

        ResultRsp<NbiNeStaticRoute> resultRsp = staticRouteSvc.query(req, resp, routeId);

        LOGGER.info(
                "Exit query static routes method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getData();
    }

    /**
     * Batch create static routes.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes Collection of NbiNeStaticRoute object
     * @return Collection of NbiNeStaticRoute object
     * @throws ServiceException when create static routes failed
     * @since SDNO 0.5
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<NbiNeStaticRoute> create(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            List<NbiNeStaticRoute> nbiRoutes) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.warn("Input body is empty.");
            return new ArrayList<>();
        }

        LOGGER.info("Create body is :" + JsonUtil.toJson(nbiRoutes));

        Map<String, NetworkElementMO> neIdToNeMoMap = new HashMap<>();
        CheckStaticRoute.check(nbiRoutes, neIdToNeMoMap);

        NbiNeStaticRouteDbOper.insert(nbiRoutes);

        List<SbiNeStaticRoute> sbiRoutes = new ArrayList<>();
        StaticRouteNbiToSbi.convertNbiToSbiModel(nbiRoutes, sbiRoutes, neIdToNeMoMap);

        SbiNeStaticRouteDbOper.insert(sbiRoutes);

        ResultRsp<NbiNeStaticRoute> resultRsp = staticRouteSvc.create(req, resp, nbiRoutes, sbiRoutes);

        LOGGER.info(
                "Exit create static routes method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getSuccessed();
    }

    /**
     * Deploy/Undeploy static routes.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiActionModel NbiActionModel Object
     * @return Collection of successful UUID
     * @throws ServiceException when deploy or undeploy static routes failed
     * @since SDNO 0.5
     */
    @POST
    @Path("/action")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> action(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            NbiActionModel nbiActionModel) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        ResultRsp<String> rsp = null;
        if(!CollectionUtils.isEmpty(nbiActionModel.getDeploy())) {

            rsp = staticRouteSvc.deploy(req, resp, nbiActionModel.getDeploy());
        } else if(!CollectionUtils.isEmpty(nbiActionModel.getUndeploy())) {

            rsp = staticRouteSvc.undeploy(req, resp, nbiActionModel.getUndeploy());
        } else {

            LOGGER.error("deploy and undeploy list is empty.");
            throw new ParameterServiceException("Action body is empty.");
        }

        LOGGER.info("Exit deploy/undeploy static routes method. cost time(ms): "
                + (System.currentTimeMillis() - infterEnterTime));

        return rsp.getSuccessed();
    }

    /**
     * Update static routes.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param nbiRoutes Collection of NbiNeStaticRoute object
     * @return Collection of NbiNeStaticRoute object
     * @throws ServiceException when update static routes failed
     * @since SDNO 0.5
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<NbiNeStaticRoute> update(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            List<NbiNeStaticRoute> nbiRoutes) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        if(CollectionUtils.isEmpty(nbiRoutes)) {
            LOGGER.warn("Input body is empty.");
            return new ArrayList<>();
        }

        LOGGER.info("Update body is :" + JsonUtil.toJson(nbiRoutes));

        Map<String, NetworkElementMO> neIdToNeMoMap = new HashMap<>();
        CheckStaticRoute.check(nbiRoutes, neIdToNeMoMap);

        ResultRsp<NbiNeStaticRoute> resultRsp = staticRouteSvc.update(req, resp, nbiRoutes);

        LOGGER.info(
                "Exit update static routes method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp.getSuccessed();
    }

    /**
     * Delete static route.<br/>
     * 
     * @param req HttpServletRequest Object
     * @param resp HttpServletResponse Object
     * @param routeId UUID of static route
     * @return UUID of static route
     * @throws ServiceException when delete static route failed
     * @since SDNO 0.5
     */
    @DELETE
    @Path("/{routeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@Context HttpServletRequest req, @Context HttpServletResponse resp,
            @PathParam("routeId") String routeId) throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        LOGGER.info("Delete begin, nbiRouteId is :" + routeId);

        UuidUtils.checkUuid(routeId);

        staticRouteSvc.delete(req, resp, routeId);

        LOGGER.info(
                "Exit delete static routes method. cost time(ms): " + (System.currentTimeMillis() - infterEnterTime));

        return routeId;
    }

}
