package org.fenixedu.cms.ui;

import com.google.gson.JsonObject;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostContentRevision;
import org.fenixedu.cms.domain.Site;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.FenixFramework;

import static java.util.Optional.ofNullable;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/versions")
public class AdminVersions {
  private static final String JSON = "application/json;charset=utf-8";

  @RequestMapping(value = "{slugSite}/{slugPost}", method = RequestMethod.GET)
  public String versions(Model model,
                         @PathVariable String slugSite,
                         @PathVariable String slugPost) {

    Site site = Site.fromSlug(slugSite);
    AdminSites.canEdit(site);
    Post post = site.postForSlug(slugPost);

    if(post.isStaticPost()) {
      model.addAttribute("page", post.getStaticPage().get());
    }

    model.addAttribute("post", post);
    model.addAttribute("site", site);

    return "fenixedu-cms/versions";
  }

  @RequestMapping(value = "{slugSite}/{slugPost}/data", method = RequestMethod.POST, produces = JSON)
  @ResponseBody public String versionData(@PathVariable String slugSite,
                            @PathVariable String slugPost,
                            @RequestParam(required = false) PostContentRevision revision) {
    Site site = Site.fromSlug(slugSite);
    AdminSites.canEdit(site);
    Post post = site.postForSlug(slugPost);

    if (revision == null) {
      revision = post.getLatestRevision();
    }

    if (revision.getPost() != post) {
      throw new RuntimeException("Invalid Revision");
    }

    JsonObject json = new JsonObject();

    json.add("content", revision.getBody().json());
    json.addProperty("modifiedAt", revision.getRevisionDate().toString());
    json.addProperty("user", revision.getCreatedBy().getUsername());
    json.addProperty("userName", revision.getCreatedBy().getProfile().getDisplayName());
    json.addProperty("id", revision.getExternalId());
    json.addProperty("next", ofNullable(revision.getNext()).map(x -> x.getExternalId()).orElse(null));
    json.addProperty("previous", ofNullable(revision.getPrevious()).map(x -> x.getExternalId()).orElse(null));

    if (revision.getPrevious() != null) {
      json.add("previousContent", revision.getPrevious().getBody().json());
    }

    return json.toString();
  }

  @RequestMapping(value = "{slugSite}/{slugPost}/revertTo", method = RequestMethod.POST)
  public RedirectView revertTo(@PathVariable String slugSite,
                               @PathVariable String slugPost,
                               @RequestParam PostContentRevision revision) {
    Site site = Site.fromSlug(slugSite);
    AdminSites.canEdit(site);
    Post post = site.postForSlug(slugPost);

    if (revision.getPost() != post) {
      throw new RuntimeException("Invalid Revision");
    }

    FenixFramework.atomic(() -> post.setBody(revision.getBody()));

    if(post.isStaticPost() && post.getStaticPage().isPresent()) {
      String pageSlug = post.getStaticPage().get().getSlug();
      return new RedirectView("/cms/pages/" + site.getSlug() + "/" + pageSlug + "/edit", true);
    } else {
      return new RedirectView("/cms/posts/" + site.getSlug() + "/" + post.getSlug() + "/edit",
                              true);
    }
  }

}
