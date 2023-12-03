package dev.fullstackcode.wiremockexample;

import dev.fullstackcode.wiremockexample.controller.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ExtendWith(WiremockTestContainersExtension.class)
class WiremockExample22IT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int serverPort;

    private static final String ROOT_URL = "http://localhost:";

    @Test
    void contextLoads() {
    }

    @Test
    public void testCreatePost() throws Exception {
        Post p = new Post();
        p.setBody("body");
        p.setTitle("Title");

        ResponseEntity<Post> response = testRestTemplate.postForEntity(ROOT_URL + serverPort + "/posts", p, Post.class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull( response.getBody().getId());
        assertEquals(p.getBody(), response.getBody().getBody());
        assertEquals(p.getTitle(), response.getBody().getTitle());

    }

    @Test
    public void testGetPost() throws Exception {

        ResponseEntity<Post> response = testRestTemplate.getForEntity(ROOT_URL + serverPort + "/posts/1", Post.class);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getId());
        assertEquals("testbody", response.getBody().getBody());
        assertEquals("testtitle", response.getBody().getTitle());
        assertEquals(1, response.getBody().getUserId());
    }

    @Test
    public void testFilterPost() throws Exception {

        ResponseEntity<List<Post>> response = testRestTemplate.exchange(
                ROOT_URL + serverPort + "/posts?userId=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Post>>(){});
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(response.getBody()).stream().map(Post::getUserId).collect(Collectors.toSet()).size());
        assertTrue( Objects.requireNonNull(response.getBody()).stream().map(Post::getUserId).collect(Collectors.toSet()).stream().allMatch(p -> p.equals(1)));

    }


    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        System.out.println( WiremockTestContainersExtension.WIRE_MOCK_CONTAINER.getHttpUrl());
        registry.add("json.mock.api",
                () -> WiremockTestContainersExtension.WIRE_MOCK_CONTAINER.getHttpUrl() + "/posts");
    }

}




