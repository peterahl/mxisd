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

package io.kamax.mxisd.invitation;

import com.google.gson.JsonObject;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixClientRequestException;
import io.kamax.matrix.client.PresenceStatus;
import io.kamax.matrix.client.as.MatrixApplicationServiceClient;
import io.kamax.matrix.client.as._MatrixApplicationServiceClient;
import io.kamax.matrix.event.EventKey;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.mxisd.config.ListenerConfig;
import io.kamax.mxisd.config.MatrixConfig;
import io.kamax.mxisd.exception.MatrixException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MxidRoomInvitationNotifier {

    private final Logger log = LoggerFactory.getLogger(MxidRoomInvitationNotifier.class);

    private ListenerConfig cfg;
    private MatrixConfig mxCfg;
    private _MatrixApplicationServiceClient mxClient;

    @Autowired
    public MxidRoomInvitationNotifier(ListenerConfig cfg, MatrixConfig mxCfg) {
        this.cfg = cfg;
        this.mxCfg = mxCfg;

        init();
    }

    private void init() {
        if (Objects.isNull(cfg.getUrl())) {
            return;
        }

        MatrixClientContext context = new MatrixClientContext()
                .setDomain(mxCfg.getDomain())
                .setHsBaseUrl(cfg.getUrl())
                .setToken(cfg.getToken().getAs())
                .setUserWithLocalpart(cfg.getLocalpart());
        mxClient = new MatrixApplicationServiceClient(context);
        mxClient.getUser(MatrixID.from(cfg.getLocalpart(), mxCfg.getDomain()).acceptable()).getName();
    }

    public void processTransaction(String token, List<JsonObject> eventsJson) {
        if (StringUtils.isBlank(token)) {
            throw new MatrixException(401, "M_UNAUTHORIZED", "No HS token");
        }

        if (!StringUtils.equals(cfg.getToken().getHs(), token)) {
            throw new MatrixException(403, "M_FORBIDDEN", "Invalid HS token");
        }

        eventsJson.forEach(ev -> {
            if (!StringUtils.equals("m.room.member", GsonUtil.getStringOrNull(ev, "type"))) {
                return;
            }

            if (!StringUtils.equals("invite", GsonUtil.getStringOrNull(ev, "membership"))) {
                return;
            }

            EventKey.StateKey.findString(ev).ifPresent(id -> {
                _MatrixID mxid = MatrixID.from(id).acceptable();
                log.info("Got invite for {}", mxid.getId());
                try {
                    mxClient.getUser(mxid).getPresence().ifPresent(p -> {
                        if (!PresenceStatus.Online.is(p.getStatus())) {
                            log.info("Found offline user, sending notification");
                        }
                    });
                } catch (MatrixClientRequestException e) {
                    e.getError().ifPresent(err -> {
                        log.info("{} - {}", err.getErrcode(), err.getError());
                    });
                }
            });
        });
    }

}
