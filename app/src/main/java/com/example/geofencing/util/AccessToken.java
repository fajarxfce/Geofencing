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
                    "  \"project_id\": \"geofencing-d914f\",\n" +
                    "  \"private_key_id\": \"d9a328085e2bfbdcc3a16df8396f181b96478075\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtgP1A91umeNTq\\nTpHYUilAGmDm5cuvtJLVce8fqeu/7vm+9Lsa3e3bAAo8HdjhhebPugcQ8c5CMhDQ\\nvbJVOOwcHjfGszWR0w4uo9xus7H8YM21J2YuIjZG6ltXjGYBDD2vxNpc4/OzZAjx\\n6h9SUhQbg07LpuW1f4RAG+e4kyAEPHfDOghe56G1/c1Su2eyRv7RURvKI7ZztrZe\\nRwsaBGFGTnPbbbxHteT8dZ15w8SNi6Q4SEhiAZniFAz9QnukfcGMhr+gZbQvRfIv\\ny9HkR1t/o3c5T36ZV1dVQ6xlOUI0Z+hZ/XdYZhv8zhjOb1kK+NVGf5IX0exh4mXD\\n0i2LXBLtAgMBAAECggEAHqruGM2gJYy2FbRNky7ym6IN/sIffq8sRQ4edjpSsVpM\\nSrR3XghcQpl/NaEQ9Mv2QEFhfl0/V/QBhOQC4Y1uLooo0bZbgasMlrHgBLE+hJ2X\\nvq14Ii9p6J8EBjWK0iSXwrnI3yJrsQm9iwUXBDX9198dzmFxuOMRPUOxca6Pui+r\\nUymON2CBbEgUq7lmKSYx63p++4j+vLhbi8z2DirJIe9c4pM0SlP2gpOM9rrdJUqd\\nOy1KU58k/u+M00275tfwJGLmHTY81N9HNfE0bo8mxthb4I+E6LS7xDGRHSFgGdSk\\ntCAAGLhb2tt1watXRKGxvy3pWjOFRU6goCWjFoAIAQKBgQDgtrmaoUE0tDvFilpa\\nUijCg4QBBEeROpoWNox5ic/h/iRVUlam46HnQ3sO+G1r6TzxkgZdybesVYDZtxzn\\nN42f8SrSsLe8iDS83IGQQLkqoa18u4mXHaIylzxUT/qz37CX8c7WleVg6S+fvOYO\\nxxJyDWXujWNvzCaFPpBz1I01qQKBgQDFqQjiZ8F1NeEgGK/chghsAHGlmdadUVpS\\nRfMaKDsN2gKLC5A2rttl4y/1htJfAMbc89ClRVbSAu11r6MfSSP0ba26BES6luNT\\n+PA3CU98wbFSVqBNfUMwNqmGbqXdpCtTCrrpDaGDfeNo3eNCIhxYmJPK9rMYUtGr\\nJ8Uwmyi1pQKBgBos7r3WXtQW0oeWMCQpQdo2m5/KsdN28g/4VrCpnu+CPRU7PCDA\\nj1FzCNibk5NoXdL94YP3RU/rvdCehd1hGTwySXy1XRG3A/2j6eu1vlv67UyEQ+i4\\naPZfq4Ves6NUQSvR3PgeCdQaFZQT2vCJu9pROThqe9TAARSwTv9PGQvRAoGAZIIw\\nt1fFIsHUCQskmcE6FRkgsPGKFRLiF8Xj+SAN71w1QNkPaw79F1Ev+OymmpJ9jKsa\\nJIAexsmgT+CQ1P7PHqKK3XUAMdU6IWALXiEI30pqzZCpEN4qRSD2kRpAk3TAZwik\\nxI8e2wsvkQqLXfyUZ2SmemV56ltqrFqbxbyQNdUCgYAndm53lpQqOVqrLNmGYqZU\\n4V8NXn4Iv6NUdIAO9io4Jqll6fFSFzzLbBecOr4pSPLzzeihuSwJ0zZitpQ1vrt8\\nGvrXzxXtsVEr/s1BTHW7u1ogUeSA35R376wdhGLavJfFQGXwurB7fyd6zY6HIK2K\\nE3+4Ixr3Ck20puc07ijFAw==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-hown3@geofencing-d914f.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"101095024395971484785\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-hown3%40geofencing-d914f.iam.gserviceaccount.com\",\n" +
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
