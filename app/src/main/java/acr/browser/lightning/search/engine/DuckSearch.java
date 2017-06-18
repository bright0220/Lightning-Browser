package acr.browser.lightning.search.engine;

import acr.browser.lightning.R;
import acr.browser.lightning.constant.Constants;

/**
 * The DuckDuckGo search engine.
 * <p>
 * See https://duckduckgo.com/assets/logo_homepage.normal.v101.png for the icon.
 */
public class DuckSearch extends BaseSearchEngine {

    public DuckSearch() {
        super("file:///android_asset/duckduckgo.png", Constants.DUCK_SEARCH, R.string.search_engine_duckduckgo);
    }

}
