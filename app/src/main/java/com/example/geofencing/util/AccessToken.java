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
                    "  \"private_key_id\": \"2814658c11c80aea7b0c40673d0226f952fc74f5\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCJTmMHsVIyf8/j\\nAAvcm+7kiqclELAuRHk9fgKwRdBmcJmPh6aZH24CoAfA01sn1AytBodG+pMslWhJ\\n5uKy2PusFHUMISNAZa9p2nzJ2aqkPM9poIdju7sWp/EiDkY9x+JUk1mba36SQIrR\\nXr4XshUanAtVrFfzk8OI7XFzv3MQrTOVJmMvZGv12vtKEEoC7X3cdf9ybtIqy23q\\n1gzbqthfRAw9d0lFw+FjQmzP3hevu39EHSV8EXAi/JcTk1ePutGaQ+28LAnmat2j\\n6M73vDvBzHuf7OVAGxid0ti8XKQ7HzSo7vIp+MEEEJdzSgyeDDjc0oFweOyxrAnc\\nRUSYiTV7AgMBAAECggEABhD8CBl5/7gs56MXYxJSni9QDi+sEQc+cgyQstgKypPb\\n/P7pyecdFambXhYYd2yDeNuKe4TTnsslFerJBkzW5bXrq/ME2zIC64LXM5XV17bG\\nFS0d/jaLoAcgfLgzu0QLCGCQO3Em4PBQEcsFTAe2rfvJ9mmMYzsajwCqnAoP2+My\\niwqPHjVYxXPJmI0NHwZkYPm6ChF7far1EWHyJot1kP+0QoXzQDrA3P2KXkFPmtea\\nYNVbSI/zSgMIEpKfA9vP8Rw7RQvphuVo4fTQsblImmqakkqprv2zTUe/WD9nimnM\\nsU/qiaMktl/LANN4mXamcK8UbTOlaJHGF1u45lj9aQKBgQDAr1Nl/2F33EjxHJvj\\ngKmO9baDS3RKQr0pcjL0nRLRFtJ14M0c/4EBeHyLnqYR+Izt/USwtAFFeUljoF0X\\nTorZvsVAUZgDCPbZEEMrxk0zpx60nSaBuM58xu5SSbt/gLB3doEjQu/wZjeIBpMO\\nXxJIwvUmRZB7i9EhT/0AVUgl8wKBgQC2bJnQ3nQfv97HLLv/8WY++Jkcxc+OkYEB\\nu8kcAL09koKKL2DxVM4HU+eT2VIHNH0xACN23crGidfL6L1tQjgp/pRZZjrbEsWZ\\nNgAGyXZAq78QLpAa+NT1wdiUEFYZeMF7+4PKyVsYt0QjqSW8sYmeHNMieE6jQk7Z\\nEGNMWKjsWQKBgFQIotCrDjEeC1nMZlhT9p74nn6oW7E/Zgw7V+nCz0ANOgBa6Fmr\\nPPINQBQMPtU7lwr8GAcQmjVYbh9bAv51LK1GO/SPIULBmBaWtxTSST0rt4KBvxd4\\nJ+XXRHzy1ykGUO/o6O0d9lLb4YAbjesIn4rIK+5jDF41XiGvJuIMT4jtAoGASLSv\\n+Vcv4RePqsJhi3iiz6Y1IAqW1rOXPyGg3dBNnCSwcnK/qG+sTa9bNMoAr250Fvt/\\nJvjuwIJfx51TIiCqUdJtPdRI0NeCebXT/OB+iPfqhoCIXred6PUBZMG+DxjZxJhR\\nxpjJ/efM+ImJyNLZjnLek3ZvoRHkaw7lBKCxcsECgYB5vl2Y6Oj1hbic30BnxMlg\\nzHhGqsy+Ew3L7T0zkBBUuKuHRgzRvlzku721OTNpUUO/lmLSwT6CQEY4YaK8j2L0\\n43czLMB5KyReCE4deMcMiwJane68u7kUT27AWhu0wF1s4ARNmMycNZgWJGyJREeQ\\nfAK6mvmzqjv3KIVUlNl1sw==\\n-----END PRIVATE KEY-----\\n\",\n" +
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
