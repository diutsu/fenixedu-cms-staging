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

import static pt.ist.fenixframework.FenixFramework.atomic;

import javax.script.ScriptException;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.NashornStrategy;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.CMSFolder;
import org.fenixedu.cms.domain.CMSFolder.FolderResolver;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/folders")
public class AdminFolders {

    @RequestMapping(method = RequestMethod.GET)
    public String listFolders(Model model) {
        model.addAttribute("folders", Bennu.getInstance().getCmsFolderSet());
        return "fenixedu-cms/folders";
    }

    @RequestMapping(method = RequestMethod.POST)
    public RedirectView createFolder(@RequestParam String path, @RequestParam LocalizedString description) {
        atomic(() -> {
            new CMSFolder(PortalConfiguration.getInstance().getMenu(), path, description);
        });
        return new RedirectView("/cms/folders", true);
    }

    @RequestMapping(value = "/resolver/{folder}", method = RequestMethod.GET)
    public String folderResolver(Model model, @PathVariable CMSFolder folder) {
        model.addAttribute("folder", folder);
        return "fenixedu-cms/folderResolver";
    }

    @RequestMapping(value = "/resolver/{folder}", method = RequestMethod.PUT)
    public ResponseEntity<?> saveFolderResolver(@PathVariable CMSFolder folder, @RequestBody String code) {
        try {
            atomic(() -> {
                if (Strings.isNullOrEmpty(code)) {
                    folder.setResolver(null);
                } else {
                    folder.setResolver(new NashornStrategy<>(FolderResolver.class, code));
                }
                return null;
            });
        } catch (Exception e) {
            Throwable original = unwrap(e);
            return new ResponseEntity<>(original.getClass().getName() + ": " + original.getMessage(),
                    HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Throwable unwrap(Throwable e) {
        while (e.getCause() != null) {
            if (e instanceof ScriptException) {
                return e;
            }
            e = e.getCause();
        }
        return e;
    }

    @RequestMapping("/delete/{folder}")
    public RedirectView deleteFolder(@PathVariable CMSFolder folder) {
        atomic(() -> folder.delete());
        return new RedirectView("/cms/folders", true);
    }
}
