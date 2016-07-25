package com.github.api.gateway.filters.route.ribbon;

/**
 * Created by yifanzhang.
 */
public interface RibbonCommandFactory<T extends RibbonCommand> {
    T create(RibbonCommandContext context);
}
