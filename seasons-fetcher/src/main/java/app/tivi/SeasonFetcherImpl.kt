/*
 * Copyright 2018 Google, Inc.
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

package app.tivi

import app.tivi.data.daos.LastRequestDao
import app.tivi.data.entities.Request
import app.tivi.trakt.TraktSeasonFetcher
import org.threeten.bp.Period
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeasonFetcherImpl @Inject constructor(
    private val lastRequestDao: LastRequestDao,
    private val traktSeasonFetcher: TraktSeasonFetcher
) : SeasonFetcher {
    override suspend fun updateIfNeeded(showId: Long) {
        if (lastRequestDao.isRequestBefore(Request.SHOW_SEASONS, showId, Period.ofDays(1))) {
            update(showId)
        }
    }

    override suspend fun update(showId: Long) {
        traktSeasonFetcher.updateSeasonData(showId)
        // Update the timestamp
        lastRequestDao.updateLastRequest(Request.SHOW_SEASONS, showId)
    }
}