package com.vera.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/14/17.
 */
@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    @GET
    @Path("/world")
    public Response getWorld() {
        Map<String, String> values = new HashMap<>();
        values.put("message", "World");
        return Response.status(Response.Status.OK)
                .entity(values)
                .build();
    }
}