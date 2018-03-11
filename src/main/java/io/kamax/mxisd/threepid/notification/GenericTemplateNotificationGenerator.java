/*
 * mxisd - Matrix Identity Server Daemon
 * Copyright (C) 2017 Maxime Dor
 *
 * https://max.kamax.io/
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

package io.kamax.mxisd.threepid.notification;

import io.kamax.mxisd.config.MatrixConfig;
import io.kamax.mxisd.config.ServerConfig;
import io.kamax.mxisd.config.threepid.medium.GenericTemplateConfig;
import io.kamax.mxisd.exception.InternalServerError;
import io.kamax.mxisd.invitation.IThreePidInviteReply;
import io.kamax.mxisd.threepid.session.IThreePidSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public abstract class GenericTemplateNotificationGenerator extends PlaceholderNotificationGenerator implements INotificationGenerator {

    private Logger log = LoggerFactory.getLogger(GenericTemplateNotificationGenerator.class);

    private GenericTemplateConfig cfg;

    @Autowired
    private ApplicationContext app;

    public GenericTemplateNotificationGenerator(MatrixConfig mxCfg, ServerConfig srvCfg, GenericTemplateConfig cfg) {
        super(mxCfg, srvCfg);
        this.cfg = cfg;
    }

    private String getTemplateContent(String location) {
        try {
            InputStream is = StringUtils.startsWith(location, "classpath:") ?
                    app.getResource(location).getInputStream() : new FileInputStream(location);
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InternalServerError("Unable to read template content at " + location + ": " + e.getMessage());
        }
    }

    @Override
    public String getForInvite(IThreePidInviteReply invite) {
        log.info("Generating notification content for 3PID invite");
        return populateForInvite(invite, getTemplateContent(invite.isForMxid() ? cfg.getInviteByMxid() : cfg.getInvite()));
    }

    @Override
    public String getForValidation(IThreePidSession session) {
        log.info("Generating notification content for 3PID Session validation");
        return populateForValidation(session, getTemplateContent(cfg.getSession().getValidation().getLocal()));
    }

    @Override
    public String getForRemoteValidation(IThreePidSession session) {
        log.info("Generating notification content for remote-only 3PID session");
        return populateForRemoteValidation(session, getTemplateContent(cfg.getSession().getValidation().getRemote()));
    }

}
