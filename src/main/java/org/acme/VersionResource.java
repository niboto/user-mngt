package org.acme;


import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/version")
public class VersionResource {

    @ConfigProperty(name = "app.version")
    String version;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public VersionResponse getVersion() {
        return new VersionResponse(version);
    }

    public static class VersionResponse {
        public String version;
        

        public VersionResponse(String version) {
            this.version = version;
        }
    }



}