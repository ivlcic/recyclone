package com.dropchop.recyclone.service.api.mapping;

import com.dropchop.recyclone.model.api.Dto;
import com.dropchop.recyclone.model.api.invoke.Params;
import com.dropchop.recyclone.model.api.localization.TitleTranslation;
import com.dropchop.recyclone.model.api.localization.Translation;
import com.dropchop.recyclone.model.api.marker.HasTitle;
import com.dropchop.recyclone.model.api.marker.HasTranslation;
import com.dropchop.recyclone.model.api.rest.Constants.ContentDetail;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import java.util.*;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 30. 04. 22.
 */
@Slf4j
public class FilteringDtoContext<P extends Params> extends MappingContext<P> {

  Deque<FieldFilter.PathSegment> path = new LinkedList<>();
  String lastProp = null;

  Integer contentTreeLevel = 1;
  String contentDetailLevel = ContentDetail.NESTED_OBJS_IDCODE;
  String translationLang = null;
  List<FieldFilter> includes = new ArrayList<>();
  List<FieldFilter> excludes = new ArrayList<>();


  @Override
  public void setParams(@NonNull P params) {
    super.setParams(params);
    Integer contentTreeLevel = params.getContentTreeLevel();
    if (contentTreeLevel != null) {
      this.contentTreeLevel = contentTreeLevel;
      this.contentDetailLevel = null;
    }
    String contentDetailLevel = params.getContentDetailLevel();
    if (contentDetailLevel != null && !contentDetailLevel.isBlank()) {
      this.contentDetailLevel = contentDetailLevel;
    }

    String translationLang = params.getTranslationLang();
    if (translationLang != null && !translationLang.isBlank()) {
      this.translationLang = translationLang;
    }

    List<String> includes = params.getContentIncludes();
    if (includes != null) {
      for (String includeStr : includes) {
        this.includes.add(new FieldFilter().parseFilterSegments(includeStr));
      }
    }
    List<String> excludes = params.getContentExcludes();
    if (excludes != null) {
      for (String excludeStr : excludes) {
        this.excludes.add(new FieldFilter().parseFilterSegments(excludeStr));
      }
    }
  }

  @Override
  public FilteringDtoContext<P> params(P params) {
    this.setParams(params);
    return this;
  }

  private boolean filterByFields(FieldFilter.PathSegment segment) {
    boolean result = false;
    if (!this.includes.isEmpty()) {
      result = true; //exclusive includes
      for (FieldFilter filter : this.includes) {
        if (filter.matches(this.path, segment.name)) {
          result = false;
          break;
        }
      }
    }
    if (!this.excludes.isEmpty()) {
      for (FieldFilter filter : this.excludes) {
        if (filter.matches(this.path, segment.name)) {
          result = true;
          break;
        }
      }
    }
    return result;
  }

  private boolean filterByLevel(FieldFilter.PathSegment segment, boolean willNest) {
    boolean isForAll = FilteringConditions.isDetailForAll(this.contentDetailLevel);
    boolean isForNested = FilteringConditions.isDetailForNested(this.contentDetailLevel);

    if (segment.level >= this.contentTreeLevel && willNest && !(isForAll || isForNested)) {
      return true;
    }

    return segment.level > this.contentTreeLevel && !(isForAll || isForNested);
  }

  private boolean filterByContentDetail(FieldFilter.PathSegment segment, boolean willNest) {
    boolean isForAll = FilteringConditions.isDetailForAll(this.contentDetailLevel);
    boolean isForNested = FilteringConditions.isDetailForNested(this.contentDetailLevel);
    if (!(isForAll || isForNested)) {
      return false;
    }

    boolean isPropId = FilteringConditions.isPropertyIdCode(segment);
    boolean isPropTitle = FilteringConditions.isPropertyTitle(segment);
    boolean isPropLang = FilteringConditions.isPropertyLang(segment);
    boolean isSpecialCollection = FilteringConditions.isSpecialCollection(segment, false);
    boolean isSpecialInstance = FilteringConditions.isSpecialClass(segment, false);
    boolean isTranslationCollection = FilteringConditions.isSpecialCollection(segment, true);
    boolean isTranslationInstance = FilteringConditions.isSpecialClass(segment, true);
    boolean isTranslatableInstance = FilteringConditions.isTranslatableInstance(segment);

    //print this level and decide if progress
    if (isForNested) {
      if ((segment.level == this.contentTreeLevel && isSpecialCollection) || isSpecialInstance) {
        return false;
      }
      if (segment.level == this.contentTreeLevel + 1) {
        if (ContentDetail.NESTED_OBJS_IDCODE_TITLE.equals(this.contentDetailLevel) && this.translationLang != null) {
          return !(isPropId || isPropTitle || isTranslationCollection ||
            isTranslationInstance || (isPropLang && isTranslatableInstance)); //don't filter
        }
        if (ContentDetail.NESTED_OBJS_IDCODE.equals(this.contentDetailLevel)) {
          return !isPropId; //don't filter
        }
        if (ContentDetail.NESTED_OBJS_IDCODE_TITLE.equals(this.contentDetailLevel)) {
          return !(isPropId || isPropTitle || (isPropLang && isTranslatableInstance)); //don't filter
        }
        if (ContentDetail.NESTED_OBJS_IDCODE_TITLE_TRANS.equals(this.contentDetailLevel)) {
          return !(isPropId || isPropTitle || isTranslationCollection ||
            isTranslationInstance || (isPropLang && isTranslatableInstance)); //don't filter
        }

      }

      //we progress only if level is smaller than limit + 1 for nested
      return !(segment.level <= this.contentTreeLevel + 1);
    }

    //isForAll is always true here and level filter already filtered us out if we're too deep,
    //so we only need property checking
    if (segment.level < this.contentTreeLevel && willNest) {
      return false;
    }
    if (ContentDetail.ALL_OBJS_IDCODE_TITLE.equals(this.contentDetailLevel) && this.translationLang != null) {
      return !(isPropId || isPropTitle || isTranslationCollection ||
        isTranslationInstance || (isPropLang && isTranslatableInstance)); //don't filter
    }
    if (ContentDetail.ALL_OBJS_IDCODE.equals(this.contentDetailLevel)) {
      return !isPropId; //don't filter
    }
    if (ContentDetail.ALL_OBJS_IDCODE_TITLE.equals(this.contentDetailLevel)) {
      return !(isPropId || isPropTitle || (isPropLang && isTranslatableInstance)); //don't filter
    }
    if (ContentDetail.ALL_OBJS_IDCODE_TITLE_TRANS.equals(this.contentDetailLevel)) {
      return !(isPropId || isPropTitle || isTranslationCollection ||
        isTranslationInstance || (isPropLang && isTranslatableInstance)); //don't filter
    }
    return true;
  }

  public boolean filter(@TargetProperty String propName) {
    this.lastProp = propName;

    FieldFilter.PathSegment segment = FieldFilter.computePath(path, propName);
    log.trace("{} {} {}", segment.level, segment.path, segment.referer.getClass().getSimpleName());
    if (filterByFields(segment)) {
      return false;
    }
    boolean willNest = FilteringConditions.willPropertyNest(segment);
    if (filterByLevel(segment, willNest)) {
      return false;
    }
    return !filterByContentDetail(segment, willNest);
  }

  public void before(Object source, Object target) {
    if (source == null) {
      return;
    }
    if (this.lastProp == null) {
      this.lastProp = FieldFilter.ROOT_OBJECT;
    }

    if (source instanceof Collection) {
      FieldFilter.PathSegment segment = this.path.peekLast();
      if (segment instanceof FieldFilter.CollectionPathSegment) {
        this.path.offerLast(new FieldFilter.CollectionPathSegment(segment.name, segment.index, source));
        ((FieldFilter.CollectionPathSegment) segment).currIndex++;
      } else {
        this.path.offerLast(new FieldFilter.CollectionPathSegment(this.lastProp, source));
      }
    } else {
      FieldFilter.PathSegment segment = this.path.peekLast();
      if (segment instanceof FieldFilter.CollectionPathSegment) {
        this.path.offerLast(new FieldFilter.PathSegment(segment.name, ((FieldFilter.CollectionPathSegment) segment).currIndex, source));
        ((FieldFilter.CollectionPathSegment) segment).currIndex++;
      } else {
        this.path.offerLast(new FieldFilter.PathSegment(this.lastProp, source));
      }
    }
  }

  private void patchContentDetailLevel(Object target, FieldFilter.PathSegment segment) {
    if (segment == null) {
      return;
    }
    boolean isForAll = FilteringConditions.isDetailForAll(this.contentDetailLevel);
    boolean isForNested = FilteringConditions.isDetailForNested(this.contentDetailLevel);
    if (!(isForNested || isForAll)) {
      return;
    }
    if (!(target instanceof Dto)) {
      return;
    }
    boolean cleanUpLevel = (isForAll && segment.level <= this.contentTreeLevel - 1) ||
      (isForNested && segment.level == this.contentTreeLevel);

    if (cleanUpLevel) {
      boolean isLevelIdCode = ContentDetail.ALL_OBJS_IDCODE.equals(this.contentDetailLevel) ||
        ContentDetail.NESTED_OBJS_IDCODE.equals(this.contentDetailLevel);
      boolean isLevelTitle = ContentDetail.ALL_OBJS_IDCODE_TITLE.equals(this.contentDetailLevel) ||
        ContentDetail.NESTED_OBJS_IDCODE_TITLE.equals(this.contentDetailLevel);
      if ((isLevelIdCode || isLevelTitle) && FilteringConditions.isObjectTranslatable(target)) {
        ((HasTranslation<?>) target).setTranslations(null);
      }
    }
  }

  private void swapTranslations(Object target) {
    if (!(target instanceof HasTranslation<?>)) {
      return;
    }
    if (this.translationLang == null) {
      return;
    }
    Translation swap = ((HasTranslation<?>) target).getTranslation(this.translationLang);
    if (swap == null) {
      return;
    }
    String defaultLang = ((HasTranslation<?>) target).getLang();
    Translation defaultTrans = ((HasTranslation<?>) target).getTranslation(defaultLang);
    if (swap instanceof TitleTranslation) {
      if (defaultTrans == null) {
        com.dropchop.recyclone.model.dto.localization.TitleTranslation trans = new com.dropchop.recyclone.model.dto.localization.TitleTranslation();
        if (target instanceof HasTitle) {
          trans.setTitle(((HasTitle) target).getTitle());
          ((HasTitle)target).setTitle(((TitleTranslation) swap).getTitle());
        }
        trans.setLang(defaultLang);
        trans.setBase(true);
        //noinspection unchecked
        ((HasTranslation<TitleTranslation>) target).addTranslation(trans);
      }
      if (target instanceof HasTitle) {
        ((HasTitle)target).setTitle(((TitleTranslation) swap).getTitle());
      }
    }
    ((HasTranslation<?>) target).setLang(swap.getLang());
  }

  public void after(Object source, Object target) {
    FieldFilter.PathSegment segment = this.path.pollLast();
    swapTranslations(target);
    patchContentDetailLevel(target, segment);
    segment = this.path.peekLast();
    if (segment != null) {
      this.lastProp = segment.name;
    } else {
      this.lastProp = FieldFilter.ROOT_OBJECT;
    }
  }
}
