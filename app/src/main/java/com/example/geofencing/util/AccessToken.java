package com.example.geofencing.util;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class AccessToken {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public static String getAccessToken() {

        try{
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"geofencing-new\",\n" +
                    "  \"private_key_id\": \"e7202be552c4ccc70ab18700d39ac3a92675f6b5\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCNDgTO6D68ro0F\\nqB1dcmZUHf3dWxsUX/pJORyzoeyLDxk0r8IcfAtBGFzosSPN7WLZ39ifb8zcepgx\\nmbRmDYrbPs9An5JkDLF+QmuZ669n7aLaNiLlhGZq8t5X52FpM84/Dr247u6nqE5k\\nPAtUnQeGuaQZcqHY8OnJygjxV/ONZcfHD1OGLMNTavTc8OAGEwfMs7jPzj0CaS4S\\nDJUlWGzisTSxQouvhejqqKF9hEGEZcsr1bzuu3wNbkwQA4jBcaJc86Q6ULltWSIY\\nmU+Qt1rhiL+FRTyMAdYMmF5mJCSLlOVAtJmYE0CP92xUDW79zOLMsObloObHtFnq\\nm8WwS+pVAgMBAAECggEABIhVCg4HSA+xgI3ypZ2nC/XBF4Nq3G3UxdS+kfIhKaXE\\n+P8F1TL84e0FvUTF7QMqCEIWZ1HTytOV/5AGw+RprW+4VVqwp6OLHOQcVOBF173Y\\nhruzGt4f3ri/rCJHVyFUSQ1GS5ftQM4aeMOU10r1d97lbEY/96U5f05imQNrFC8g\\ndMZxaKrC8EzWURU7F0JOMPsNNAmi8fKamcVk1PieVAb6er1Sb8AwKz3wiK5UcNyi\\nETae/FGMrfikRfzxEvGygR/8VBCY4HeT/kmoVlTsQnr64OPPF7sNAJYB+8sQuYRJ\\nGZ5zjS+bDkn469nzjzriE8QDtLuEaGVUGZRjAntcAQKBgQC+11dRIRRl86YZ3Byw\\nAWNWuVUbnFBR0mk0ZhVfThAYZ8jqW8dIgazif8AmkR4feKPaiDWWr89sjWm5lySm\\nPeI20oQlLtuQPaPquvcfIxpX/L8jLhalu5Uqt3Imp3CcghALYlxV1Qf6Ig+5jQeX\\nAo+kftniFBO74WsXj2neUA9eVQKBgQC9Nwwt0dzRGAixeqtCwBEeqN8sl98CLtQ3\\nlRelEQpwTNapu9nWMyakaAnI+22veG91X5lG9qjBfCZL1CXcCMo2OhgecoA4UrvB\\nS2GAu0KO0EFBWMUYb2utt4Mflp7Nh8pe1sBCwJ/DsRheXR324G59SR5ElA3nvKHr\\nTGV3Ov5cAQKBgQCIiHmw0lDMq+cu4xonFacjeY6ZJVkVYMrhsRMjTy8WttG4UZce\\nmZ+9oBYxwWriqyXXB9IHD5r/l6CHJcoToXyBKlVeYMq2xFLMcZyxSyTixDFfEu8i\\n/TQ4Dmx9mRdo8WhXLLTQt4twegP0BSDj+fIYhqSNKT4BEcHTZkggw/hqzQKBgEDy\\nRyTnFvNSW5GHuIcl4/pxHoFw86QNLNyTOVV4PuwDA9+o0gG69vWRnGI3IxQKs79G\\n/BamjZA0K/T1MbWP8tCgKi0xQh+TDk0vsZz9KcBZbTyf2q8jd4NeLQzHp2SGyoi5\\nD68Z7Py/qcyiC6n0qRgp49DIPOmhDrb0NxOw1SQBAoGBAKzW2vsuzbc9xmGCu9QW\\n0pW1UxbOALqI9PcCfzhUnPFdhCbEh/PVZCtENlZ5ZV60erFKwnRjF4Hgh6NvUqem\\neRJfXMRG+AU4idLOM/LRAJbmOHiB0WaMbEgJNbRlnjDaSXKsWCyTeHONVKBdfUwG\\nCXaIPsI8e79aMdfkocn6m81O\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-yvyxb@geofencing-new.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"110772926218150141443\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-yvyxb%40geofencing-new.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Collections.singleton(firebaseMessagingScope));

            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
