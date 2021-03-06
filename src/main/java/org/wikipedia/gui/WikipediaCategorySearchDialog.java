// License: GPL. For details, see LICENSE file.
package org.wikipedia.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.wikipedia.WikipediaApp;
import org.wikipedia.tools.WikiProperties;

final class WikipediaCategorySearchDialog extends ExtendedDialog {

    private final Selector selector;
    private static final WikipediaCategorySearchDialog INSTANCE = new WikipediaCategorySearchDialog();

    private WikipediaCategorySearchDialog() {
        super(MainApplication.getMainFrame(), tr("Search Wikipedia category"), tr("Load category"), tr("Cancel"));
        this.selector = new Selector();
        this.selector.setDblClickListener(e -> buttonAction(0, null));

        setContent(selector, false);
        setPreferredSize(new Dimension(600, 300));
    }

    /**
     * Returns the unique instance of {@code MenuItemSearchDialog}.
     *
     * @return the unique instance of {@code MenuItemSearchDialog}.
     */
    public static synchronized WikipediaCategorySearchDialog getInstance() {
        return INSTANCE;
    }

    @Override
    public ExtendedDialog showDialog() {
        selector.init();
        super.showDialog();
        selector.clearSelection();
        selector.requestFocus();
        return this;
    }

    String getCategory() {
        return selector.getSelectedItem();
    }

    private static class Selector extends WikiSearchTextResultListPanel<String> {

        @Override
        protected void filterItems() {
            final String query = edSearchText.getText();
            debouncer.debounce(getClass(), () -> {
                final List<String> entries = query == null || query.isEmpty()
                        ? Collections.emptyList()
                        : WikipediaApp.forLanguage(WikiProperties.WIKIPEDIA_LANGUAGE.get()).getCategoriesForPrefix(query);
                GuiHelper.runInEDT(() -> lsResultModel.setItems(entries));
            }, 200, TimeUnit.MILLISECONDS);
        }
    }
}
