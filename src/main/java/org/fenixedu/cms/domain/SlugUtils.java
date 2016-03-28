package org.fenixedu.cms.domain;

import com.google.common.base.Joiner;
import org.fenixedu.commons.StringNormalizer;

import java.util.UUID;

public class SlugUtils {
    static String makeSlug(Sluggable element, String slug){
        if (slug == null) {
            slug = "";
        }

        slug = StringNormalizer.slugify(slug);

        while (!element.isValidSlug(slug)) {
            String randomSlug = UUID.randomUUID().toString().substring(0, 3);
            slug = Joiner.on("-").join(slug, randomSlug);
        }
        
        return slug;
    }
}
