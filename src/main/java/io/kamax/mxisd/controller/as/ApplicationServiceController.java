/*
 * mxisd - Matrix Identity Server Daemon
 * Copyright (C) 2018 Kamax Sàrl
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.mxisd.controller.as;

import com.google.gson.JsonObject;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.mxisd.invitation.MxidRoomInvitationNotifier;
import io.kamax.mxisd.util.GsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@CrossOrigin
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApplicationServiceController {

    private final String notFoundBody;
    private final GsonParser parser;
    private final MxidRoomInvitationNotifier notifier;

    @Autowired
    public ApplicationServiceController(MxidRoomInvitationNotifier notifier) {
        this.notFoundBody = GsonUtil.get().toJson(GsonUtil.makeObj("errcode", "IO.KAMAX.MXISD.AS_NOT_FOUND"));
        this.parser = new GsonParser();
        this.notifier = notifier;
    }

    @RequestMapping(value = "/rooms/**", method = GET)
    public String getRoom(HttpServletResponse res) {
        res.setStatus(404);
        return notFoundBody;
    }

    @RequestMapping(value = "/users/**", method = GET)
    public String getUser(HttpServletResponse res) {
        res.setStatus(404);
        return notFoundBody;
    }

    @RequestMapping(value = "/transactions/{txnId:.+}", method = PUT)
    public Object getTransaction(
            HttpServletRequest request,
            @RequestParam(name = "access_token", required = false) String accessToken,
            @PathVariable String txnId) {
        try {
            List<JsonObject> events = GsonUtil.asList(GsonUtil.getArray(parser.parse(request.getInputStream()), "events"), JsonObject.class);
            notifier.processTransaction(accessToken, events);
            return "{}";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
