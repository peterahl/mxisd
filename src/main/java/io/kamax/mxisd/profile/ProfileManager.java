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

package io.kamax.mxisd.profile;

import io.kamax.matrix._MatrixID;
import io.kamax.mxisd.ThreePid;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfileManager {

    private List<ProfileProvider> readers;
    private List<ProfileWriter> writers;

    public ProfileManager(List<ProfileProvider> providers, List<ProfileWriter> writers) {
        this.readers = providers.stream().filter(ProfileProvider::isEnabled).collect(Collectors.toList());
        this.writers = writers.stream().filter(ProfileWriter::isEnabled).collect(Collectors.toList());
    }

    public List<ThreePid> getThreepids(_MatrixID mxid) {
        List<ThreePid> threepids = new ArrayList<>();
        readers.forEach(p -> threepids.addAll(p.getThreepids(mxid)));
        return threepids;
    }

    public void addThreepid(_MatrixID mxid, ThreePid tpid) {
        writers.forEach(w -> w.addThreepid(mxid, tpid));
    }

}