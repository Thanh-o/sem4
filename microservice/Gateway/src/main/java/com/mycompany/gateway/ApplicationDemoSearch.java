package com.mycompany.gateway;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApplicationDemoSearch {
    public static void main(String[] args) throws IOException {
        // 1. Kết nối tới Elasticsearch trên localhost:9200
        RestClient restClient = RestClient.builder(HttpHost.create("http://localhost:9200")).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        String index = "books";

        // 2. Index một document JSON đơn giản
        Map<String, Object> book = Map.of(
            "title",  "Elasticsearch cơ bản",
            "author", "Nguyễn Tuan",
            "year",   2025
        );
        IndexResponse ir = client.index(i -> i
            .index(index)
            .id("1")
            .document(book)
        );
        System.out.println("Indexed version: " + ir.version());

        // 3. Refresh để có thể tìm ngay
        client.indices().refresh(r -> r.index(index));

        // 4. Thực hiện một match query
        SearchResponse<Map> sr = client.search(s -> s
            .index(index)
            .query(q -> q
                .match(m -> m
                    .field("title")
                    .query("cơ bản")
                )
            ), Map.class);

        List<Hit<Map>> hits = sr.hits().hits();
        hits.forEach(hit ->
            System.out.println("Found: " + hit.source())
        );

        // 5. Đóng kết nối
        restClient.close();
    }
}
