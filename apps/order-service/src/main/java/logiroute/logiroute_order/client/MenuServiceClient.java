package logiroute.logiroute_order.client;

import logiroute.logiroute_order.dto.MenuItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MenuServiceClient {

    private final RestClient restClient;

    public MenuServiceClient(
            @Value("${logiroute.menu-service.url:http://localhost:8081}") String menuServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(menuServiceUrl)
                .build();
    }

    public List<MenuItemResponse> getMenuItems(List<UUID> itemIds) {
        String idsParam = itemIds.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(","));

        String uri = UriComponentsBuilder.fromPath("/api/items")
                .queryParam("ids", idsParam)
                .build()
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
