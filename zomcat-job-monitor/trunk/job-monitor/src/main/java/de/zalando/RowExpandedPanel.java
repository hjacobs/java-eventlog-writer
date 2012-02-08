package de.zalando;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class RowExpandedPanel extends Panel {

    private static final long serialVersionUID = -8259859785105151450L;

    public RowExpandedPanel(final String id, final Space space) {
        super(id);

        setOutputMarkupId(true);

        final List<String> states = space.getStates();
        add(new ListView<String>("rows", states) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(final ListItem<String> item) {
                    if (item.getIndex() == 0) {
                        final Label name = new Label("name", space.getName());
                        name.add(new AttributeModifier("rowspan", states.size() + 1 + ""));
                        item.add(name);

                        final AjaxLink<RowCollapsedPanel> link = new AjaxLink<RowCollapsedPanel>("link") {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void onClick(final AjaxRequestTarget target) {
                                final RowCollapsedPanel rowCollapsedPanel = new RowCollapsedPanel(id, space);
                                RowExpandedPanel.this.replaceWith(rowCollapsedPanel);
                                target.add(rowCollapsedPanel);
                            }
                        };
                        link.add(new AttributeModifier("rowspan", states.size() + ""));
                        item.add(link);
                    } else {
                        item.add(new WebMarkupContainer("name").setVisible(false));
                        item.add(new WebMarkupContainer("link").setVisible(false));
                    }

                    final String state = item.getModelObject();
                    item.add(new Label("state", state));
                    item.add(new Label("count", space.getCountForState(state) + ""));
                }
            });
        add(new Label("totalCount", space.getTotalCount() + ""));
    }

}
