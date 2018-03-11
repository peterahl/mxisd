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

package io.kamax.mxisd.config.threepid.connector;

import io.kamax.mxisd.util.GsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties("notification.handlers.sendgrid")
public class EmailSendGridConfig {

    public static class EmailTemplate {

        public static class EmailBody {

            private String text;
            private String html;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getHtml() {
                return html;
            }

            public void setHtml(String html) {
                this.html = html;
            }

        }

        private String subject;
        private EmailBody body = new EmailBody();

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public EmailBody getBody() {
            return body;
        }

        public void setBody(EmailBody body) {
            this.body = body;
        }

    }

    public static class Api {

        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

    public static class Identity {

        private String from;
        private String name;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class Templates {

        public static class TemplateSession {

            private EmailTemplate local = new EmailTemplate();
            private EmailTemplate remote = new EmailTemplate();

            public EmailTemplate getLocal() {
                return local;
            }

            public void setLocal(EmailTemplate local) {
                this.local = local;
            }

            public EmailTemplate getRemote() {
                return remote;
            }

            public void setRemote(EmailTemplate remote) {
                this.remote = remote;
            }
        }

        private EmailTemplate invite = new EmailTemplate();
        private EmailTemplate inviteByMxid = new EmailTemplate();
        private TemplateSession session = new TemplateSession();

        public EmailTemplate getInvite() {
            return invite;
        }

        public void setInvite(EmailTemplate invite) {
            this.invite = invite;
        }

        public EmailTemplate getInviteByMxid() {
            return inviteByMxid;
        }

        public void setInviteByMxid(EmailTemplate inviteByMxid) {
            this.inviteByMxid = inviteByMxid;
        }

        public TemplateSession getSession() {
            return session;
        }

        public void setSession(TemplateSession session) {
            this.session = session;
        }

    }

    private Logger log = LoggerFactory.getLogger(EmailSendGridConfig.class);

    private Api api = new Api();
    private Identity identity = new Identity();
    private Templates templates = new Templates();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Templates getTemplates() {
        return templates;
    }

    public void setTemplates(Templates templates) {
        this.templates = templates;
    }

    @PostConstruct
    public void build() {
        log.info("--- Email SendGrid connector config ---");
        log.info("API key configured?: {}", StringUtils.isNotBlank(api.getKey()));
        log.info("Identity: {}", GsonUtil.build().toJson(identity));
        log.info("Templates: {}", GsonUtil.build().toJson(templates));
    }

}
