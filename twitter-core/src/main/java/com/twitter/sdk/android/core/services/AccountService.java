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

package com.twitter.sdk.android.core.services;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.User;

import retrofit.http.GET;
import retrofit.http.Query;

public interface AccountService {

    /**
     * Returns an HTTP 200 OK response code and a representation of the requesting user if
     * authentication was successful; returns a 401 status code and an error message if not. Use
     * this method to test if supplied user credentials are valid.
     *
     * @param includeEntities (optional) The entities node will not be included when set to false.
     * @param skipStatus (optional) When set to either true, t or 1 statuses will not be included in
     *                   the returned user objects.
     * @param cb The callback to invoke when the request completes.
     */
    @GET("/1.1/account/verify_credentials.json")
    void verifyCredentials(@Query("include_entities") Boolean includeEntities,
            @Query("skip_status") Boolean skipStatus,@Query("include_email") Boolean includeEmail,
            Callback<User> cb);

    /**
     * Synchronous version of the verify credentials API.
     *
     * Returns an HTTP 200 OK response code and a representation of the requesting user if
     * authentication was successful; returns a 401 status code and an error message if not. Use
     * this method to test if supplied user credentials are valid.
     *
     * @param includeEntities (optional) The entities node will not be included when set to false.
     * @param skipStatus (optional) When set to either true, t or 1 statuses will not be included in
     *                   the returned user objects.
     * @return the User Object
     */
    @GET("/1.1/account/verify_credentials.json")
    User verifyCredentials(@Query("include_entities") Boolean includeEntities,
            @Query("skip_status") Boolean skipStatus,@Query("include_email") Boolean includeEmail);

}
