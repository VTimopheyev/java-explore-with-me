package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsRecordDto;

import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class StatisticsClient {
    private RestTemplate rest;

    public Integer get(String path, Map<String, Object> params) {

        ResponseEntity<StatsDto[]> result = makeAndSendGetRequest(HttpMethod.GET, path, params, null);

        StatsDto[] statistics = result.getBody();

        if (!Objects.isNull(statistics) && statistics.length != 0) {
            List<StatsDto> list = Arrays.asList(statistics);
            return list.get(0).getHits();
        }
        return 0;
    }


    public void post(StatsRecordDto body) {
        String path = "http://stats-server:9090/hit";
        makeAndSendPostRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<StatsDto[]> makeAndSendGetRequest(HttpMethod method, String path,
                                                                 @Nullable Map<String, Object> parameters,
                                                                 @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<StatsDto[]> shareitServerResponse = null;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, StatsDto[].class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, StatsDto[].class);
            }
        } catch (HttpStatusCodeException e) {
            log.info(String.valueOf(e.getStatusCode()));
        }
        return prepareGatewayResponseForGet(shareitServerResponse);
    }

    private <T> ResponseEntity<Object> makeAndSendPostRequest(HttpMethod method, String path,
                                                              @Nullable Map<String, Object> parameters,
                                                              @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> shareitServerResponse = null;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            log.info(String.valueOf(e.getStatusCode()));
        }
        return prepareGatewayResponseForPost(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<StatsDto[]> prepareGatewayResponseForGet(ResponseEntity<StatsDto[]> response) {
        if (!Objects.isNull(response.getStatusCode()) && response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private static ResponseEntity<Object> prepareGatewayResponseForPost(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {

            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}

