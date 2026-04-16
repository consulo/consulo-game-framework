/**
 * @author VISTALL
 * @since 2025-01-23
 */
module consulo.game.framework.api {
    requires consulo.language.api;
    requires consulo.project.ui.api;
    requires consulo.ui.ex.api;

    exports consulo.gameFramework.meta;
    exports consulo.gameFramework.localize;
    exports consulo.gameFramework.icon;
}