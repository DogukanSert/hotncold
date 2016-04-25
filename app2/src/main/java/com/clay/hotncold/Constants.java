/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.clay.hotncold;

public class Constants {

    public static final String IDENTITY_POOL_ID = "us-east-1:8dd2ddef-8e80-4251-b287-9b60b3ead353";

    private Constants() {}

    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    public static final int REQUEST_CODE_ENABLE_BLE = 1001;
    public static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    public static final int REQUEST_CODE_PLACE_PICKER = 1003;

    public static final String AUTH_SCOPE = "oauth2:https://www.googleapis.com/auth/userlocation.beacon.registry";
    public static final String PREFS_NAME = "com.google.sample.beaconservice.Prefs";
}
