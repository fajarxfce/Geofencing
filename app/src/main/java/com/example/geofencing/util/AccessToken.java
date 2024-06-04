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
                    "  \"project_id\": \"geofencing-dbb5e\",\n" +
                    "  \"private_key_id\": \"4124fc2a852360c8e3be635d740e11037abc531c\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDGtu8AIpCant2Q\\nqZv1WvxAl5GmYO6+tzxk9jo3HU7PMx/El2qhQ5KyP3q6YSa4VMBbndTIGYRNRc/e\\nGbyDoFxVZG2C48yuEqKkvRbRR0HAlWI0YTdJzT35RuCRbshN+cZccxZBeOC13mO2\\n9goMd4ic5wZrcMLnBXlsmaeXl+ONfG3u/CD2AwPORSeH0z6P1AaHwswOLZuRUfoB\\na8Ovt9E0+MZGzIOemsVrfqNcEMmY5wP/fmo0xjI3aPsyUcYeup8Sbfxl8BZsxBkj\\n43aX2CLInChKPQMaA7Ob9JNXCBL4PkhkWhPjxmveoOxunxeQurtvHaWj01b5zEWL\\nBRGev2IDAgMBAAECggEARZs5v93bslW4vd4zo6/R0ZxmKBYnOwX7cVAw1zJ8cybw\\n10/EyaVeYvLKfjPeuM3zLYRtTwRlLB4qsJ1qQ98tyLmrhKBgB4++ih80OW2Q8jFB\\nm6B8Ge17anCx5C+emXXHToX/w0Jg0EQMwL+RiIlsgK2KOaNfmkX4IRr3sinBy0Ku\\nSDemrHbgTAWrDk2EQ01Bw0Fb5Me+jKQpgNYM5X1F/nV4Fj1rcPyNo6wuDIc+5PcZ\\nq+hR7mfxSkhps6EldAadXxbWpiPFehXsBKO200rAw45uCJ/WydHX8wx+kyA8gO0+\\nrKu/Rrea55daej2obAkDJkw+x6hm78ObSxT4AGi3PQKBgQD8gbRod9iINmBfvcss\\nhfxGbEcOCKokTWF0RwNhcraUXju1cL5wnythlEtv7BTH+K/UiA1WoNfj0TH6FrZw\\nM/drMxhmgkSYyvjvIB92jCDme3gLmFq45g18rK2SOHC3mx8q04oKUSKCtfTNUdvB\\n+xZFTA015jx5cpNjrvVdx/kMNwKBgQDJdrcQddoYFuM8LZBL+P+IY3zHF3z+i7sP\\ncxOQCYQvQ8uYf2nCkYaW34EbzKlUbdxkzHSYuXwsfCc5n5CBreWPxoI476ANthiZ\\nYmI8MnBFrpBv35y411A85Ar2kwsMBNIspihUhzeEPxymm6B5C1NBBj0aYd8e39IR\\n9Dwj8OfqlQKBgFdsCKwKojs2qFM9gLRfhyJSpp8jPNTovY7mbzMz5iakIYQGDskr\\nMnfMQfn0AJXjcezhVcziTxye9vChIHTkaQsCW7ZR77H+xAT2WC5RAasKoFtTWkWd\\n0JmTp1xykgPffwDy0F4WDR64JJNZff5BEkrXs9MvbUyW8JSYpHbdn0otAoGAYbSZ\\ng/okE5rGCeFvIelYxKS7VzMlA65wwkVlL23uFZ4jl4nzmNFLHNC4pOrVFIVTZe9i\\nfM84UbrEq0Wfp8TWmKwFa+eLEBtIQttcGSW9HY5Trm4ca+jAmUdfqcYWjXWpGM5c\\nsOIDIrgZBRRk909uyRJlf+4Hb8nM77grkqK7VyECgYAXBzz8GYGIoYTngP/Yja4L\\nKrhzAlz2sFAWmhVkVFEFYKrUDi9LA+atI2LOAhcNC1AlN3tu6bnXq6dGBGpV49H1\\n4i0zWAP71/yMIox1Kc71RLU6LU6N3qZR1GfpVRGFYJxbVxCFo0ptASuHcBXiESEk\\n3GIEtrrkj2ZkVxkK7pySJw==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-79f0j@geofencing-dbb5e.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"105977352713397993545\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-79f0j%40geofencing-dbb5e.iam.gserviceaccount.com\",\n" +
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
