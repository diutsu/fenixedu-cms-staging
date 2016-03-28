package org.fenixedu.cms.api.resource;

import com.google.gson.JsonElement;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.cms.api.json.MenuItemAdapter;
import org.fenixedu.cms.domain.MenuItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.fenixedu.cms.domain.PermissionEvaluation.ensureCanDoThis;
import static org.fenixedu.cms.domain.PermissionsArray.Permission.*;

@Path("/cms/menuItems")
public class MenuItemResource extends BennuRestResource {

    //TODO: check permissions in all methods

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement getMenuItem(@PathParam("oid") MenuItem menuItem) {
        return view(menuItem, MenuItemAdapter.class);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public Response deleteMenuItem(@PathParam("oid") MenuItem menuItem) {
        ensureCanDoThis(menuItem.getMenu().getSite(), LIST_MENUS, EDIT_MENU, EDIT_MENU_ITEM, DELETE_MENU_ITEM);
        menuItem.delete();
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{oid}")
    public JsonElement updateMenuItem(@PathParam("oid") MenuItem menuItem, JsonElement json) {
        return updateMenuItemFromJson(menuItem, json);
    }

    private JsonElement updateMenuItemFromJson(MenuItem menuItem, JsonElement json) {
        return view(update(json, menuItem, MenuItemAdapter.class));
    }
}