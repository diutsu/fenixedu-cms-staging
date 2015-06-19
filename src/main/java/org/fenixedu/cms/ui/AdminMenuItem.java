/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import java.util.Optional;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/menus")
public class AdminMenuItem {

    @RequestMapping(value = "{slugSite}/{oidMenu}/change", method = RequestMethod.GET)
    public String change(Model model, @PathVariable(value = "slugSite") String slugSite,
                         @PathVariable(value = "oidMenu") String oidMenu) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        model.addAttribute("site", s);
        model.addAttribute("menu", s.menuForOid(oidMenu));
        return "fenixedu-cms/changeMenu";
    }

    private JsonObject serialize(MenuItem item) {
        JsonObject root = new JsonObject();

        root.add("title", new JsonPrimitive(item.getName().isEmpty() ? "---" : item.getName().getContent()));
        root.add("name", item.getName().json());

        root.add("key", new JsonPrimitive(item.getExternalId()));
        root.add("url", item.getUrl() == null ? null : new JsonPrimitive(item.getUrl()));
        root.add("page", item.getPage() == null ? null : new JsonPrimitive(item.getPage().getSlug()));
        root.add("position", new JsonPrimitive(item.getPosition()));
        root.add("isFolder", new JsonPrimitive(Optional.ofNullable(item.getFolder()).orElse(false)));
        root.add("pageUrl", item.getPage() == null ? null : new JsonPrimitive(item.getPage().getAddress()));
        root.add("pageEditUrl", item.getPage() == null ? null : new JsonPrimitive(item.getPage().getEditUrl()));

        if (item.getChildrenSet().size() > 0) {
            root.add("folder", new JsonPrimitive(true));
            JsonArray child = new JsonArray();

            for (MenuItem subitem : item.getChildrenSorted()) {
                child.add(serialize(subitem));
            }
            root.add("children", child);
        }
        return root;
    }

    @RequestMapping(value = "{slugSite}/{oidMenu}/data", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public
    @ResponseBody
    String data(Model model, @PathVariable(value = "slugSite") String slugSite, @PathVariable(
            value = "oidMenu") String oidMenu) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Menu m = s.menuForOid(oidMenu);

        JsonObject root = new JsonObject();

        root.add("title", new JsonPrimitive(m.getName().isEmpty() ? "---" : m.getName().getContent()));
        root.add("name", m.getName().json());
        root.add("key", new JsonPrimitive("null"));
        root.add("root", new JsonPrimitive(true));
        root.add("folder", new JsonPrimitive(true));

        JsonArray child = new JsonArray();

        m.getToplevelItemsSorted().map(this::serialize).forEach(json -> child.add(json));

        root.add("children", child);

        JsonArray top = new JsonArray();

        top.add(root);
        return top.toString();
    }

    @RequestMapping(value = "{slugSite}/{oidMenu}/createItem", method = RequestMethod.POST)
    public RedirectView create(Model model, @PathVariable(value = "slugSite") String slugSite,
                               @PathVariable(value = "oidMenu") String oidMenu, @RequestParam String menuItemOid,
                               @RequestParam LocalizedString name, @RequestParam String use,
                               @RequestParam(required = false) String url,
                               @RequestParam(required = false) String slugPage) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Menu m = s.menuForOid(oidMenu);

        createMenuItem(menuItemOid, name, use, url, slugPage, s, m);
        return new RedirectView("/cms/menus/" + slugSite + "/" + oidMenu + "/change", true);
    }

    @Atomic
    private void createMenuItem(String menuItemOid, LocalizedString name, String use, String url, String slugPage, Site s, Menu m) {
        MenuItem mi = new MenuItem(m);
        mi.setName(name);

        if (!menuItemOid.equals("null")) {
            MenuItem parent = FenixFramework.getDomainObject(menuItemOid);
            if (parent.getMenu() != m) {
                throw new RuntimeException("Wrong Parents");
            }
            parent.add(mi);
        } else {
            m.add(mi);
        }

        if (use.equals("url")) {
            mi.setUrl(url);
        } else if (use.equals("folder")) {
            mi.setFolder(true);
        } else {
            Page p = s.pageForSlug(slugPage);
            if (p == null) {
                throw new RuntimeException("Page not found");
            }
            mi.setPage(p);
        }
    }

    @RequestMapping(value = "{slugSite}/{oidMenu}/changeItem", method = RequestMethod.POST)
    public RedirectView change(Model model, @PathVariable(value = "slugSite") String slugSite,
                               @PathVariable(value = "oidMenu") String oidMenu, @RequestParam String menuItemOid,
                               @RequestParam String menuItemOidParent, @RequestParam LocalizedString name, @RequestParam String use,
                               @RequestParam String url, @RequestParam String slugPage, @RequestParam Integer position) {

        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Menu m = s.menuForOid(oidMenu);

        if (menuItemOid.equals("null") && menuItemOidParent.equals("null")) {
            changeMenu(m, name);
        } else {

            MenuItem mi = FenixFramework.getDomainObject(menuItemOid);

            if (mi.getMenu() != m) {
                throw new RuntimeException("Wrong Parents");
            }

            changeMenuItem(mi, menuItemOidParent, name, use, url, slugPage, s, m, position);
        }
        return new RedirectView("/cms/menus/" + slugSite + "/" + oidMenu + "/change", true);
    }

    @Atomic
    private void changeMenu(Menu m, LocalizedString name) {
        m.setName(name);
    }

    @Atomic
    private void changeMenuItem(MenuItem mi, String menuItemOid, LocalizedString name, String use, String url, String slugPage,
                                Site s, Menu m, Integer position) {

        mi.setName(name);

        if (!menuItemOid.equals("null")) {
            if ((mi.getParent() == null && !menuItemOid.equals("null"))
                    || (mi.getParent() != null && !mi.getParent().getOid().equals(menuItemOid))) {
                MenuItem mip = FenixFramework.getDomainObject(menuItemOid);

                if (mip.getMenu() != m) {
                    throw new RuntimeException("Wrong Parents");
                }
                mip.putAt(mi, position);
                mi.setTop(null);
            }
        } else {
            m.putAt(mi, position);
            mi.setParent(null);
        }

        if (use.equals("folder")) {
            mi.setFolder(true);
            mi.setUrl(null);
            mi.setPage(null);
        } else if (use.equals("url")) {
            mi.setFolder(false);
            mi.setUrl(url);
            mi.setPage(null);
        } else if (use.equals("page")) {
            mi.setFolder(false);
            mi.setUrl(null);
            Page p = s.pageForSlug(slugPage);

            if (p == null) {
                throw new RuntimeException("Page not found");
            }

            mi.setPage(p);
        }
    }

    @RequestMapping(value = "{slugSite}/{oidMenu}/delete/{oidMenuItem}", method = RequestMethod.POST)
    public RedirectView delete(Model model, @PathVariable(value = "slugSite") String slugSite,
                               @PathVariable(value = "oidMenu") String oidMenu, @PathVariable(value = "oidMenuItem") String oidMenuItem) {
        Site s = Site.fromSlug(slugSite);

        AdminSites.canEdit(s);

        Menu m = s.menuForOid(oidMenu);
        MenuItem item = FenixFramework.getDomainObject(oidMenuItem);
        if (item.getMenu() != m && item.getTop() != m) {
            throw new RuntimeException("Wrong Parents");
        }
        item.delete();
        return new RedirectView("/cms/menus/" + slugSite + "/" + oidMenu + "/change", true);
    }
}
