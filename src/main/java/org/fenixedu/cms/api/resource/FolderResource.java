package org.fenixedu.cms.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.FolderAdapter;
import org.fenixedu.cms.domain.CMSFolder;

import com.google.gson.JsonElement;

@Path("/cms/folders")
public class FolderResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement listAllFolders() {
        return view(Bennu.getInstance().getCmsFolderSet(), FolderAdapter.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement listFolder(@PathParam("oid") CMSFolder folder) {
        return view(folder, FolderAdapter.class);
    }
}