package com.medvault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Talks to an IPFS (Kubo) daemon's HTTP RPC API to store a record's content
 * and get back a content identifier (CID).
 *
 * Local dev:  install IPFS Desktop or `ipfs` CLI, run `ipfs daemon`,
 *             it exposes the API at http://127.0.0.1:5001 by default.
 * Production: point ipfs.api-url at a hosted pinning gateway instead
 *             (e.g. Pinata's IPFS API, or your own Kubo node's public API),
 *             see README for details.
 *
 * If no IPFS node is reachable, addContent() fails soft and returns null so
 * the rest of the app keeps working without decentralized storage configured.
 */
@Service
public class IPFSService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ipfs.api-url:http://127.0.0.1:5001}")
    private String ipfsApiUrl;

    public String addContent(String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
                @Override
                public String getFilename() { return "record.json"; }
            });

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            Map<?, ?> response = restTemplate.postForObject(
                    ipfsApiUrl + "/api/v0/add", request, Map.class);

            return response != null ? (String) response.get("Hash") : null;
        } catch (Exception e) {
            // No IPFS node reachable — degrade gracefully, don't break record creation.
            return null;
        }
    }

    public String gatewayUrl(String cid) {
        return cid == null ? null : "https://ipfs.io/ipfs/" + cid;
    }
}
