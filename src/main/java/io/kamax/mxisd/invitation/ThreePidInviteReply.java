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

package io.kamax.mxisd.invitation;

public class ThreePidInviteReply implements IThreePidInviteReply {

    private boolean isForMxid;
    private String id;
    private IThreePidInvite invite;
    private String token;
    private String displayName;

    public ThreePidInviteReply(IThreePidInvite invite) {
        this.invite = invite;
        this.isForMxid = true;
    }

    public ThreePidInviteReply(String id, IThreePidInvite invite, String token, String displayName) {
        this.id = id;
        this.invite = invite;
        this.token = token;
        this.displayName = displayName;
        this.isForMxid = false;
    }

    @Override
    public boolean isForMxid() {
        return isForMxid;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public IThreePidInvite getInvite() {
        return invite;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

}
