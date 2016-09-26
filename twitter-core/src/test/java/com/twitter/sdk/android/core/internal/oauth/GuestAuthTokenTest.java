/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.core.internal.oauth;

import com.twitter.sdk.android.core.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GuestAuthTokenTest  {

    private static final int HEADERS_COUNT = 2;
    private static final String TOKEN_TYPE = "tokenType";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String GUEST_TOKEN = "guestToken";
    private static final long ONE_HOUR_AGE = System.currentTimeMillis() - (3600 * 1000);
    private static final long THREE_HOURS_AGO = System.currentTimeMillis() - (3600 * 3 * 1000);

    @Test
    public void testGetAuthHeaders() {
        final GuestAuthToken token = new GuestAuthToken(TOKEN_TYPE, ACCESS_TOKEN, GUEST_TOKEN);
        final Map<String, String> headers = token.getAuthHeaders(null, null, null, null);
        assertEquals(HEADERS_COUNT, headers.size());
        assertEquals(OAuth2Service.getAuthorizationHeader(token),
                headers.get(GuestAuthToken.HEADER_AUTHORIZATION));
        assertEquals(GUEST_TOKEN, headers.get(GuestAuthToken.HEADER_GUEST_TOKEN));
    }

    @Test
    public void testIsExpired_newToken() {
        final GuestAuthToken token = new GuestAuthToken(TOKEN_TYPE, ACCESS_TOKEN, GUEST_TOKEN);
        assertFalse(token.isExpired());
    }

    @Test
    public void testIsExpired_oneHourOld() {
        final GuestAuthToken token = new GuestAuthToken(TOKEN_TYPE, ACCESS_TOKEN, GUEST_TOKEN,
                ONE_HOUR_AGE);
        assertFalse(token.isExpired());
    }

    @Test
    public void testIsExpired_threeHoursOld() {
        final GuestAuthToken token = new GuestAuthToken(TOKEN_TYPE, ACCESS_TOKEN, GUEST_TOKEN,
                THREE_HOURS_AGO);
        assertTrue(token.isExpired());
    }

    @Test
    public void testIsExpired_createdAtZero() {
        final GuestAuthToken token = new GuestAuthToken(TOKEN_TYPE, ACCESS_TOKEN, GUEST_TOKEN, 0);
        assertTrue(token.isExpired());
    }
}
