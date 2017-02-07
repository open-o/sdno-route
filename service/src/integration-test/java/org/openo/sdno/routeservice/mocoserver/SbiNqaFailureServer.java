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

package org.openo.sdno.routeservice.mocoserver;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;
import org.openo.sdno.framework.container.util.JsonUtil;
import org.openo.sdno.overlayvpn.errorcode.ErrorCode;
import org.openo.sdno.overlayvpn.model.v2.route.SbiNqa;
import org.openo.sdno.overlayvpn.result.FailData;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.testframework.http.model.HttpRequest;
import org.openo.sdno.testframework.http.model.HttpResponse;
import org.openo.sdno.testframework.http.model.HttpRquestResponse;
import org.openo.sdno.testframework.moco.MocoHttpServer;
import org.openo.sdno.testframework.moco.responsehandler.MocoResponseHandler;

/**
 * SbiNqaFailureServer class for failure test cases. <br>
 * 
 * @author
 * @version SDNO 0.5 Jan 9, 2017
 */
public class SbiNqaFailureServer extends MocoHttpServer {

    private static final String CREATE_NQA_FAIL =
            "src/integration-test/resources/acsbiadapter/createnqafail.json";


    public SbiNqaFailureServer() {

    }

    @Override
    public void addRequestResponsePairs() {

        this.addRequestResponsePair(CREATE_NQA_FAIL, new FailureResponseHandler());
    }

    private class FailureResponseHandler extends MocoResponseHandler {

        @Override
        public void processRequestandResponse(HttpRquestResponse httpObject) {

            HttpRequest httpRequest = httpObject.getRequest();
            HttpResponse httpResponse = httpObject.getResponse();
            
            List<SbiNqa> inputList =
                    JsonUtil.fromJson(httpRequest.getData(), new TypeReference<List<SbiNqa>>() {});
            FailData<SbiNqa> failData = new FailData<SbiNqa>();
            failData.setErrcode(ErrorCode.OVERLAYVPN_FAILED);
            failData.setData(inputList.get(0));
            
            ResultRsp<SbiNqa> newResult =
                    new ResultRsp<SbiNqa>(ErrorCode.OVERLAYVPN_FAILED);

            newResult.getFail().add(failData);
            httpResponse.setData(JsonUtil.toJson(newResult));
        }
    }

}
