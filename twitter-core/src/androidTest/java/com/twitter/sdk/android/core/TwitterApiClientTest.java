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

package com.twitter.sdk.android.core;

import io.fabric.sdk.android.FabricAndroidTestCase;
import io.fabric.sdk.android.FabricTestUtils;

import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import static org.mockito.Mockito.mock;

public class TwitterApiClientTest extends FabricAndroidTestCase {

    public void testGetService_sdkNotStarted() {
        try {
            FabricTestUtils.resetFabric();
            new TwitterApiClient(mock(Session.class));
            fail();
        } catch (IllegalStateException ise) {
            assertEquals("Must Initialize Fabric before using singleton()", ise.getMessage());
        }
    }

    public void testConstructor_noSession() throws Exception {
        try {
            final TwitterCore twitterCore = TwitterTestUtils.createTwitter(
                    new TwitterAuthConfig("", ""), null);
            FabricTestUtils.with(getContext(), twitterCore);
            new TwitterApiClient(null);
            fail();
        } catch (IllegalArgumentException ie) {
            assertEquals("Session must not be null.", ie.getMessage());
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testGetService_cachedService() throws Exception {
        final TwitterApiClient client = newTwitterApiClient();
        final StatusesService service = client.getService(StatusesService.class);
        assertSame(service, client.getService(StatusesService.class));
    }

    public void testGetService_differentServices() throws Exception {
        final TwitterApiClient client = newTwitterApiClient();
        final FavoriteService service = client.getService(FavoriteService.class);
        assertNotSame(service, client.getService(StatusesService.class));
    }

    private TwitterApiClient newTwitterApiClient() {
        return new TwitterApiClient(mock(TwitterAuthConfig.class), mock(Session.class),
                new TwitterApi(), mock(SSLSocketFactory.class), mock(ExecutorService.class));
    }
}
