package dev.fullstackcode.currencyexchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/posts2")
@Slf4j
public class PostController2 {
    @Autowired
    private RestOperations restTemplate;

    @Value("${json.mock.api}")
    private String jsonApi;

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> entity = new HttpEntity<Post>(post ,headers);
        ResponseEntity<Post> response = restTemplate.exchange(jsonApi, HttpMethod.POST,entity,Post.class);
        System.out.println(response.getBody());
        return response.getBody();
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable Integer id) {
        try {
            ResponseEntity<Post> response = restTemplate.getForEntity(jsonApi + "/" + id, Post.class);

            if ( response.getBody() == null) {
                throw new RestClientException("no information found");
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }


//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Post> entity = new HttpEntity<Post>(post ,headers);
//        ResponseEntity<Post> response = restTemplate.exchange(jsonApi, HttpMethod.GET,entity,Post.class);
//        System.out.println(response.getBody());
//        return response.getBody();
    }

    @PutMapping("/{id}")
    public Post updatePost(@RequestBody Post post,@PathVariable Integer id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> entity = new HttpEntity<Post>(post ,headers);
        ResponseEntity<Post> response = null;
        try {
         response = restTemplate.exchange(jsonApi+"/"+id, HttpMethod.PUT,entity,Post.class);
    } catch (HttpClientErrorException e) {
        throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
    }

        return response.getBody();
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Integer id) {
        try {
             restTemplate.delete(jsonApi+"/"+id);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }

    }

    @GetMapping
    public List<Post> filterPostByUserId(@RequestParam Integer userId) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
////        HttpHeaders headers = new HttpHeaders();
////        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//        map.add("email", "first.last@example.com");
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
//        Post post = null;
//        try {
//            post = restTemplate.exchange(jsonApi+"?userId="+id,HttpMethod.GET,Post[].class);
//        } catch (HttpClientErrorException e) {
//            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
//        }
//
//        if ( post == null) {
//            throw new RestClientException("no information found");
//        }
//        return post;


        // Post[] postArray = null;
        ResponseEntity<Post[]> response;
        try {
            response = restTemplate.getForEntity(jsonApi+"?userId="+userId, Post[].class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }

        if ( Objects.requireNonNull(response.getBody()).length == 0) {
            throw new RestClientException("no information found");
        }
        return Arrays.stream(response.getBody()).toList();

    }
}
